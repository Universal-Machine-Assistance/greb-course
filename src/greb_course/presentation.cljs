(ns greb-course.presentation
  "Presentation mode: coordinator — setup, teardown, enter/exit."
  (:require [greb-course.dom          :as d]
            [greb-course.i18n         :as i18n]
            [greb-course.state        :as state]
            [greb-course.ui           :as ui]
            [greb-course.animation    :as anim]
            [greb-course.hints        :as hints]
            [greb-course.nav          :as nav]
            [greb-course.slides       :as slides]
            [greb-course.spacemouse   :as sm]
            [greb-course.sounds       :as sfx]
            [greb-course.pres-session :as session]
            [greb-course.pres-physics :as physics]
            [greb-course.pres-controls :as controls]))

;; ── Presentation Mode ──────────────────────────────────────────
(declare toggle-view! toggle-fullscreen!)

(defn- pres-toggle-fullscreen! [overlay]
  (if (or (.-fullscreenElement js/document) (.-webkitFullscreenElement js/document))
    (do (when (.-exitFullscreen js/document) (.exitFullscreen js/document))
        (when (.-webkitExitFullscreen js/document) (.webkitExitFullscreen js/document)))
    (do (when (.-requestFullscreen overlay) (.requestFullscreen overlay))
        (when (.-webkitRequestFullscreen overlay) (.webkitRequestFullscreen overlay)))))

(defn- pres-toggle-section-mode! []
  "Toggle section highlight/navigate mode."
  (when-let [{:keys [overlay]} @state/pres-state]
    (if (.contains (.-classList overlay) "pres-overlay--section-mode")
      (do (.remove (.-classList overlay) "pres-overlay--section-mode")
          (swap! state/pres-state assoc :section-mode? false))
      (do (.add (.-classList overlay) "pres-overlay--section-mode")
          (swap! state/pres-state assoc :section-mode? true)))))

(defn- pres-toggle-maximize! []
  "Toggle current slide to fill entire window."
  (when-let [{:keys [overlay max-btn]} @state/pres-state]
    (let [maximized? (.contains (.-classList overlay) "pres-overlay--slide-maximized")]
      (if maximized?
        (do (.remove (.-classList overlay) "pres-overlay--slide-maximized")
            (swap! state/pres-state assoc :slide-maximized? false)
            (when max-btn
              (doto max-btn
                (.setAttribute "title" (i18n/t :maximize-slide))
                (.setAttribute "aria-label" (i18n/t :maximize-slide)))
              (when-let [ic (.querySelector max-btn ".icon")]
                (.setAttribute ic "data-lucide" "square"))
              (when (and js/lucide (.-createIcons js/lucide))
                (.createIcons js/lucide #js {:root max-btn}))))
        (do (.add (.-classList overlay) "pres-overlay--slide-maximized")
            (swap! state/pres-state assoc :slide-maximized? true)
            (when max-btn
              (doto max-btn
                (.setAttribute "title" (i18n/t :restore-slide))
                (.setAttribute "aria-label" (i18n/t :restore-slide)))
              (when-let [ic (.querySelector max-btn ".icon")]
                (.setAttribute ic "data-lucide" "minimize-2"))
              (when (and js/lucide (.-createIcons js/lucide))
                (.createIcons js/lucide #js {:root max-btn}))))))))

(defn pres-apply-view! []
  (when-let [overlay (:overlay @state/pres-state)]
    (let [zoom (or (:zoom @state/pres-state) 1.0)
          tscale (or (:text-scale @state/pres-state) 1.0)
          pan-x (or (:pan-x @state/pres-state) 0)
          pan-y (or (:pan-y @state/pres-state) 0)]
      (.setProperty (.-style overlay) "--pres-zoom" (str zoom))
      (.setProperty (.-style overlay) "--pres-text-scale" (str tscale))
      (.setProperty (.-style overlay) "--pres-pan-x" (str pan-x "px"))
      (.setProperty (.-style overlay) "--pres-pan-y" (str pan-y "px"))
      (.setProperty (.-style overlay) "--canvas-scale" (str @state/canvas-zoom))
      (ui/update-status-badge! zoom tscale))))

(defn- pres-commit-canvas-zoom!
  "Transfer canvas zoom into pres zoom for crisp re-render."
  []
  (when (and @state/canvas-zoom-active?
             (not= @state/canvas-zoom 1.0))
    (let [cz @state/canvas-zoom]
      (swap! state/pres-state update :zoom #(* (or % 1.0) cz))
      (reset! state/canvas-zoom 1.0)
      (pres-apply-view!))))

(defn- dock-magnify!
  "Attach macOS Dock-style magnification to the section bar."
  [bar dots]
  (let [max-scale  2.4
        base-scale 1.0
        radius     80
        reset!     (fn []
                     (doseq [dot dots]
                       (set! (.. dot -style -transform) (str "scale(" base-scale ")"))
                       (set! (.. dot -style -width) "10px")
                       (set! (.. dot -style -height) "10px")))]
    (.addEventListener bar "mousemove"
      (fn [e]
        (let [bar-rect (.getBoundingClientRect bar)
              mx       (.-clientX e)]
          (doseq [dot dots]
            (let [rect   (.getBoundingClientRect dot)
                  cx     (+ (.-left rect) (/ (.-width rect) 2))
                  dist   (js/Math.abs (- mx cx))
                  factor (max 0 (- 1 (/ dist radius)))
                  factor (* factor factor (- 3 (* 2 factor)))
                  s      (+ base-scale (* (- max-scale base-scale) factor))
                  sz     (js/Math.round (* 10 s))]
              (set! (.. dot -style -transform) (str "scale(" (.toFixed s 2) ")"))
              (set! (.. dot -style -width) (str sz "px"))
              (set! (.. dot -style -height) (str sz "px")))))))
    (.addEventListener bar "mouseleave"
      (fn [_] (reset!)))
    (reset!)))

(defn- pres-update-section! [idx]
  "Highlight the current section in index panel and section dots."
  (when-let [{:keys [slide->section entry-els section-dots]} @state/pres-state]
    (let [sec (get slide->section idx)]
      (when (not= sec (:current-section @state/pres-state))
        (doseq [el entry-els]
          (.remove (.-classList el) "pres-index-entry--active"))
        (when (and sec (< sec (count entry-els)))
          (let [el (nth entry-els sec)]
            (.add (.-classList el) "pres-index-entry--active")
            (.scrollIntoView el #js {:block "nearest" :behavior "smooth"})))
        (when section-dots
          (doseq [dot section-dots]
            (.remove (.-classList dot) "pres-section-dot--active"))
          (when (and sec (< sec (count section-dots)))
            (.add (.-classList (nth section-dots sec)) "pres-section-dot--active")))
        (swap! state/pres-state assoc :current-section sec)))))

(defn pres-show-page! [idx direction]
  (when-let [{:keys [slides indicator n]} @state/pres-state]
    (let [idx (max 0 (min (dec n) idx))
          old-idx (:current @state/pres-state)]
      (when (not= idx old-idx)
        (hints/dismiss-hints!)
        ;; Animate old slide out
        (when-let [old-slide (nth slides old-idx nil)]
          (.remove (.-classList old-slide) "pres-active")
          (.add (.-classList old-slide) "pres-exiting")
          (when (= direction :back)
            (.add (.-classList old-slide) "pres-going-back"))
          (js/setTimeout
            (fn []
              (.remove (.-classList old-slide) "pres-exiting" "pres-going-back"))
            450))
        ;; Animate new slide in
        (let [new-slide (nth slides idx)]
          (when (= direction :back)
            (.add (.-classList new-slide) "pres-going-back"))
          (.-offsetHeight new-slide)
          (.add (.-classList new-slide) "pres-active")
          (when (= direction :back)
            (js/requestAnimationFrame
              #(.remove (.-classList new-slide) "pres-going-back")))
          (doseq [node (array-seq (.querySelectorAll new-slide ".animate"))]
            (.remove (.-classList node) "visible"))
          (js/setTimeout
            (fn []
              (doseq [node (array-seq (.querySelectorAll new-slide ".animate"))]
                (.add (.-classList node) "visible")))
            180))
        ;; Reset zoom/pan on slide change
        (let [cur-z (or (:zoom @state/pres-state) 1.0)
              zoomed? (or (> (js/Math.abs (- cur-z 1.0)) 0.05)
                          (> (js/Math.abs (or (:pan-x @state/pres-state) 0)) 5)
                          (> (js/Math.abs (or (:pan-y @state/pres-state) 0)) 5))]
          (when-let [^js p (:phy @state/pres-state)]
            (set! (.-vx p) 0) (set! (.-vy p) 0)
            (set! (.-zv p) 0) (set! (.-wx p) 0) (set! (.-wy p) 0)
            (set! (.-spx p) 0) (set! (.-spy p) 0) (set! (.-spz p) 0)
            (set! (.-svx p) 0) (set! (.-svy p) 0) (set! (.-szv p) 0))
          (if zoomed?
            (anim/animate-view-to! state/pres-state
              {:zoom 1.0 :text-scale (or (:text-scale @state/pres-state) 1.0) :pan-x 0 :pan-y 0}
              pres-apply-view! :duration 250)
            (do (swap! state/pres-state assoc :pan-x 0 :pan-y 0 :zoom 1.0)
                (pres-apply-view!))))
        (set! (.-textContent indicator) (str (inc idx) " / " n))
        (swap! state/pres-state assoc :current idx)
        (pres-update-section! idx)
        (session/save-session! idx)))))

(declare exit-presentation!)

(defn- setup-presentation!
  "Core setup: build overlay, register listeners, show starting slide."
  [slide-data start]
  (let [slide-els  (mapv :slide slide-data)
        page-ids   (mapv :page-id slide-data)
        n          (count slide-els)
        overlay    (d/el :div {:class "pres-overlay"})
        viewport   (d/el :div {:class "pres-viewport"})
        indicator  (d/el :span {:class "pres-indicator"} (str (inc start) " / " n))]
    ;; Append all slides to viewport
    (doseq [slide slide-els]
      (.appendChild viewport slide))
    (.appendChild overlay viewport)
    (.appendChild overlay indicator)
    (.appendChild (.-body js/document) overlay)
    ;; Build index panel
    (let [slide-groups (reduce
                         (fn [acc [i {:keys [page-id]}]]
                           (let [pid (or page-id "?")]
                             (if (and (seq acc) (= pid (:pid (peek acc))))
                               (update-in acc [(dec (count acc)) :indices] conj i)
                               (conj acc {:pid pid :indices [i]}))))
                         []
                         (map-indexed vector slide-data))
          slide->section (into {}
                           (mapcat (fn [si {:keys [indices]}]
                                     (map (fn [slide-i] [slide-i si]) indices))
                                   (range) slide-groups))
          page-id->slide (into {} (map (fn [{:keys [pid indices]}] [pid (first indices)]) slide-groups))
          pres-navigate! (fn [page-id]
                           (when-let [idx (get page-id->slide page-id)]
                             (pres-show-page! idx nil)))
          toc-groups     (or (:toc-groups @state/current-nav) [])
          toc-result     (nav/build-toc-panel pres-navigate! toc-groups ui/pres-shortcuts)
          toc-overlay    (:overlay toc-result)
          toc-panel      (:panel toc-result)
          toggle!        (:toggle! toc-result)
          entry-els      []
          ;; Section dots bar
          section-dots (mapv
                         (fn [{:keys [pid indices]}]
                           (let [first-idx (first indices)
                                 page-el   (.getElementById js/document pid)
                                 tip       (or (when page-el
                                                 (when-let [h (or (.querySelector page-el "h1")
                                                                   (.querySelector page-el ".hero-kicker"))]
                                                   (let [t (.trim (.-textContent h))]
                                                     (when (pos? (count t)) t))))
                                               pid)]
                             (doto (d/el :button {:class "pres-section-dot" :title tip})
                                   (.addEventListener "click"
                                     (fn [] (pres-show-page! first-idx nil))))))
                         slide-groups)
          section-bar (apply d/el :div {:class "pres-section-bar"} section-dots)
          ;; Highlight cursor
          hl-cursor   (d/el :div {:class "pres-highlight-cursor"})
          on-mouse    (fn [e]
                        (.setProperty (.-style hl-cursor) "--hl-x" (str (.-clientX e) "px"))
                        (.setProperty (.-style hl-cursor) "--hl-y" (str (.-clientY e) "px")))
          ;; Build zoom/text-scale controls first (physics needs zoom-label)
          zoom-ctrl   (controls/make-zoom-controls pres-apply-view!)
          ts-ctrl     (controls/make-text-scale-controls pres-apply-view!)
          zoom-label  (:zoom-label zoom-ctrl)
          ts-label    (:ts-label ts-ctrl)
          set-zoom!   (:set-zoom! zoom-ctrl)
          set-text-scale! (:set-text-scale! ts-ctrl)]
      ;; Create physics engine with zoom-label wired in
      (let [phys        (physics/make-physics pres-apply-view! zoom-label pres-show-page!)
            {:keys [phy held-keys pan-speed pan-raf ensure-raf!
                    on-touch-start on-touch-move on-touch-end on-wheel]} phys
            ;; Build toolbar
            toolbar-res (controls/make-toolbar
                          toggle!
                          #(pres-toggle-fullscreen! overlay)
                          #(pres-toggle-maximize!)
                          #(pres-toggle-section-mode!)
                          #(exit-presentation!)
                          (:zoom-controls zoom-ctrl)
                          (:ts-controls ts-ctrl))
            {:keys [toolbar-el fs-btn max-btn sm-btn]} toolbar-res
            ;; Build keyboard handler
            kb          (controls/make-keyboard-handler
                          {:held-keys      held-keys
                           :ensure-raf!    ensure-raf!
                           :pan-speed      pan-speed
                           :show-page!     pres-show-page!
                           :exit!          exit-presentation!
                           :set-zoom!      set-zoom!
                           :set-text-scale! set-text-scale!
                           :apply-view!    pres-apply-view!
                           :zoom-label     zoom-label
                           :ts-label       ts-label
                           :hl-cursor      hl-cursor
                           :phy            phy
                           :slide-els      slide-els
                           :slide-groups   slide-groups
                           :toggle-idx!    toggle!
                           :toggle-view!   toggle-view!
                           :toggle-fs!     toggle-fullscreen!
                           :commit-canvas-zoom! pres-commit-canvas-zoom!})
            {:keys [on-key on-keyup]} kb
            on-fschange (fn []
                          (when (and (nil? (.-fullscreenElement js/document))
                                     (nil? (.-webkitFullscreenElement js/document)))
                            (pres-apply-view!)))
            on-resize   (fn [] (pres-apply-view!))]
        (.appendChild overlay toc-overlay)
        (.appendChild overlay toc-panel)
        (.appendChild overlay toolbar-el)
        (.appendChild overlay section-bar)
        (dock-magnify! section-bar section-dots)
        (.appendChild overlay hl-cursor)
        ;; Store state
        (reset! state/pres-state {:overlay overlay :viewport viewport :indicator indicator
                            :slides slide-els :page-ids page-ids :n n :current -1
                            :slide->section slide->section :entry-els entry-els
                            :section-dots section-dots :current-section nil
                            :zoom (or (session/restore-zoom) 1.0)
                            :text-scale (or (session/restore-text-scale) 1.0)
                            :fs-btn fs-btn :max-btn max-btn
                            :on-key on-key :on-keyup on-keyup :on-mouse on-mouse :on-wheel on-wheel
                            :on-touch-start on-touch-start :on-touch-move on-touch-move :on-touch-end on-touch-end
                            :held-keys held-keys :phy phy :pan-raf pan-raf
                            :on-fschange on-fschange :on-resize on-resize})
        ;; Register SpaceMouse callbacks
        (reset! state/sm-on-prev #(pres-show-page! (dec (or (:current @state/pres-state) 0)) :back))
        (reset! state/sm-on-next #(pres-show-page! (inc (or (:current @state/pres-state) 0)) nil))
        (reset! state/sm-on-reset #(do (set-zoom! 1.0)
                                       (set-text-scale! 1.0)
                                       (swap! state/pres-state assoc :pan-x 0 :pan-y 0)
                                       (pres-apply-view!)))
        (reset! state/sm-ensure-raf! ensure-raf!)
        (when sm-btn (sm/update-indicator!))
        ;; Sync zoom & text-scale UI with restored values
        (let [z (or (:zoom @state/pres-state) 1.0)
              ts (or (:text-scale @state/pres-state) 1.0)]
          (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))
          (set! (.-textContent ts-label) (str "A " (js/Math.round (* ts 100)) "%")))
        ;; Compute scale
        (pres-apply-view!)
        ;; Disable canvas zoom in presentation mode
        (reset! state/canvas-zoom-active? false)
        (reset! state/canvas-zoom 1.0)
        ;; Add presenting class
        (.add (.-classList (.-documentElement js/document)) "presenting")
        ;; Register listeners
        (.addEventListener js/document "keydown" on-key true)
        (.addEventListener js/document "keyup" on-keyup true)
        (.addEventListener js/document "fullscreenchange" on-fschange)
        (.addEventListener js/document "webkitfullscreenchange" on-fschange)
        (.addEventListener js/window "resize" on-resize)
        (.addEventListener js/window "mousemove" on-mouse)
        (.addEventListener js/document "wheel" on-wheel #js {:passive false})
        (.addEventListener overlay "touchstart" on-touch-start #js {:passive true})
        (.addEventListener overlay "touchmove"  on-touch-move  #js {:passive false})
        (.addEventListener overlay "touchend"   on-touch-end   #js {:passive true})
        ;; Show starting slide
        (pres-show-page! start nil)
        (slides/tag-vt! (nth slide-els start))
        ;; Re-render lucide icons
        (when (and js/lucide (.-createIcons js/lucide))
          (.createIcons js/lucide #js {:root overlay}))
        overlay))))

(defn- teardown-presentation!
  "Core teardown: remove listeners, clear state. Returns [overlay target-page-id]."
  []
  (when-let [{:keys [overlay on-key on-keyup on-mouse on-wheel on-fschange on-resize
                     on-touch-start on-touch-move on-touch-end
                     held-keys ^js phy pan-raf page-ids current slides]} @state/pres-state]
    (let [target-page-id (nth page-ids (or current 0) nil)
          active-slide   (nth slides (or current 0) nil)]
      ;; Cancel pan animation
      (when-let [raf @pan-raf] (js/cancelAnimationFrame raf))
      (when held-keys (reset! held-keys #{}))
      ;; Remove listeners
      (.removeEventListener js/document "keydown" on-key true)
      (.removeEventListener js/document "keyup" on-keyup true)
      (.removeEventListener js/document "fullscreenchange" on-fschange)
      (.removeEventListener js/document "webkitfullscreenchange" on-fschange)
      (.removeEventListener js/window "resize" on-resize)
      (.removeEventListener js/window "mousemove" on-mouse)
      (when on-wheel (.removeEventListener js/document "wheel" on-wheel))
      (when on-touch-start (.removeEventListener overlay "touchstart" on-touch-start))
      (when on-touch-move  (.removeEventListener overlay "touchmove"  on-touch-move))
      (when on-touch-end   (.removeEventListener overlay "touchend"   on-touch-end))
      (when phy
        (set! (.-vx phy) 0) (set! (.-vy phy) 0)
        (set! (.-zv phy) 0) (set! (.-wx phy) 0) (set! (.-wy phy) 0))
      ;; Exit fullscreen if active
      (when (or (.-fullscreenElement js/document) (.-webkitFullscreenElement js/document))
        (if (.-exitFullscreen js/document)
          (.exitFullscreen js/document)
          (when (.-webkitExitFullscreen js/document)
            (.webkitExitFullscreen js/document))))
      ;; Tag current slide landmarks for VT matching
      (when active-slide (slides/tag-vt! active-slide))
      ;; Remove ui-hidden if toggled
      (.remove (.-classList (.-documentElement js/document)) "ui-hidden")
      ;; Re-enable canvas zoom in document mode
      (reset! state/canvas-zoom-active? true)
      ;; Clear session & state
      (session/clear-session!)
      (reset! state/pres-state nil)
      ;; Reset SpaceMouse callbacks
      (reset! state/sm-on-prev nil)
      (reset! state/sm-on-next nil)
      (reset! state/sm-on-reset nil)
      (reset! state/sm-ensure-raf! @state/sm-doc-ensure-raf!)
      [overlay target-page-id])))

(defn enter-presentation!
  ([] (enter-presentation! nil))
  ([start-idx]
   (when-not @state/pres-state
     (let [slide-data (try (slides/extract-slides) (catch :default e (js/console.error "extract-slides error" e) []))
           page-ids   (mapv :page-id slide-data)
           n          (count slide-data)]
       (when (pos? n)
     (let [auto-idx   (when (nil? start-idx)
                        (when-let [hash (nav/current-hash)]
                          (slides/slide-idx-for-page page-ids hash)))
           start      (max 0 (min (dec n) (or start-idx auto-idx 0)))
           src-page-id (nth page-ids start nil)
           src-el      (when src-page-id (.getElementById js/document src-page-id))]
       (if (and (slides/has-view-transitions?) src-el)
         (do
           (slides/tag-vt! src-el)
           (.setProperty (.-style src-el) "view-transition-name" "vt-page")
           (.startViewTransition js/document
             (fn []
               (slides/clear-vt! src-el)
               (.removeProperty (.-style src-el) "view-transition-name")
               (let [_overlay (setup-presentation! slide-data start)
                     slide    (nth (mapv :slide slide-data) start)]
                 (.setProperty (.-style slide) "view-transition-name" "vt-page")
                 (js/setTimeout
                   (fn []
                     (.removeProperty (.-style slide) "view-transition-name")
                     (slides/clear-vt! slide))
                   400))
               nil)))
         (setup-presentation! slide-data start))))))))

(defn exit-presentation! []
  (when @state/pres-state
    (let [[overlay target-page-id] (teardown-presentation!)
          doc-el (.-documentElement js/document)]
      (when overlay
        (let [show-doc! (fn []
                          (.remove (.-classList doc-el) "presenting"))
              nav!      (fn []
                          (when (and target-page-id @state/current-nav)
                            (let [{:keys [go! id->spread]} @state/current-nav]
                              (when-let [idx (get id->spread target-page-id)]
                                (go! idx nil)))))]
          (if (slides/has-view-transitions?)
            (.startViewTransition js/document
              (fn []
                (when (.-parentNode overlay) (.remove overlay))
                (show-doc!)
                (nav!)
                (when-let [target-el (when target-page-id
                                       (.getElementById js/document target-page-id))]
                  (.setProperty (.-style target-el) "view-transition-name" "vt-page")
                  (slides/tag-vt! target-el)
                  (js/setTimeout
                    (fn []
                      (.removeProperty (.-style target-el) "view-transition-name")
                      (slides/clear-vt! target-el))
                    400))
                nil))
            (do
              (.add (.-classList overlay) "pres-overlay--fade-out")
              (js/setTimeout
                (fn []
                  (when (.-parentNode overlay) (.remove overlay))
                  (show-doc!)
                  (nav!))
                280))))))))

(defn- is-fullscreen? []
  (boolean (or (.-fullscreenElement js/document) (.-webkitFullscreenElement js/document))))

(defn toggle-view!
  "Toggle between document and presentation mode. Preserves fullscreen state."
  []
  (let [was-fs (is-fullscreen?)]
    (if @state/pres-state
      (do (exit-presentation!)
          (when was-fs
            (js/setTimeout
              (fn []
                (try
                  (let [el (.-documentElement js/document)]
                    (when (.-requestFullscreen el) (.requestFullscreen el))
                    (when (.-webkitRequestFullscreen el) (.webkitRequestFullscreen el)))
                  (catch :default _)))
              300)))
      (do (enter-presentation!)
          (when was-fs
            (js/setTimeout
              (fn []
                (try
                  (when-let [{:keys [overlay]} @state/pres-state]
                    (pres-toggle-fullscreen! overlay))
                  (catch :default _)))
              300))))))

(defn toggle-fullscreen!
  "Toggle browser fullscreen. In presentation mode, fullscreens the overlay."
  []
  (if (is-fullscreen?)
    (do (when (.-exitFullscreen js/document) (.exitFullscreen js/document))
        (when (.-webkitExitFullscreen js/document) (.webkitExitFullscreen js/document)))
    (if-let [{:keys [overlay]} @state/pres-state]
      (pres-toggle-fullscreen! overlay)
      (let [el (.-documentElement js/document)]
        (when (.-requestFullscreen el) (.requestFullscreen el))
        (when (.-webkitRequestFullscreen el) (.webkitRequestFullscreen el))))))

;; Re-export session restore for external callers
(def pres-restore-session session/restore-session)
