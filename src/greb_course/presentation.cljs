(ns greb-course.presentation
  "Presentation mode: setup, teardown, enter/exit, physics, controls."
  (:require [greb-course.dom        :as d]
            [greb-course.i18n       :as i18n]
            [greb-course.state      :as state]
            [greb-course.ui         :as ui]
            [greb-course.animation  :as anim]
            [greb-course.hints      :as hints]
            [greb-course.nav        :as nav]
            [greb-course.slides     :as slides]
            [greb-course.spacemouse :as sm]
            [greb-course.omnirepl   :as omni]
            [greb-course.sounds     :as sfx]))

;; ── Presentation Mode ──────────────────────────────────────────
(declare toggle-view! toggle-fullscreen!)

(defn- pres-toggle-fullscreen! [overlay]
  (if (or (.-fullscreenElement js/document) (.-webkitFullscreenElement js/document))
    (do (when (.-exitFullscreen js/document) (.exitFullscreen js/document))
        (when (.-webkitExitFullscreen js/document) (.webkitExitFullscreen js/document)))
    (do (when (.-requestFullscreen overlay) (.requestFullscreen overlay))
        (when (.-webkitRequestFullscreen overlay) (.webkitRequestFullscreen overlay)))))

(defn- pres-toggle-section-mode! []
  "Toggle section highlight/navigate mode — keeps section bar visible, emphasizes navigation."
  (when-let [{:keys [overlay]} @state/pres-state]
    (if (.contains (.-classList overlay) "pres-overlay--section-mode")
      (do (.remove (.-classList overlay) "pres-overlay--section-mode")
          (swap! state/pres-state assoc :section-mode? false))
      (do (.add (.-classList overlay) "pres-overlay--section-mode")
          (swap! state/pres-state assoc :section-mode? true)))))

(defn- pres-toggle-maximize! []
  "Toggle current slide to fill entire window (like portada)."
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

;; pres-schedule-canvas-commit! removed — canvas zoom no longer auto-commits.
;; Commit only happens when the user exits canvas zoom mode.

(defn- pres-save-session! [idx]
  "Persist presentation slide index and zoom to sessionStorage."
  (.setItem js/sessionStorage "greb-pres-idx" (str idx))
  (when-let [z (:zoom @state/pres-state)]
    (.setItem js/sessionStorage "greb-pres-zoom" (str z)))
  (when-let [ts (:text-scale @state/pres-state)]
    (.setItem js/sessionStorage "greb-pres-tscale" (str ts))))

(defn- pres-clear-session! []
  (.removeItem js/sessionStorage "greb-pres-idx")
  (.removeItem js/sessionStorage "greb-pres-zoom")
  (.removeItem js/sessionStorage "greb-pres-tscale"))

(defn pres-restore-session []
  "Returns saved slide index or nil."
  (when-let [v (.getItem js/sessionStorage "greb-pres-idx")]
    (js/parseInt v 10)))

(defn- pres-restore-zoom []
  "Returns saved zoom level or nil."
  (when-let [v (.getItem js/sessionStorage "greb-pres-zoom")]
    (js/parseFloat v)))

(defn- pres-restore-text-scale []
  (when-let [v (.getItem js/sessionStorage "greb-pres-tscale")]
    (js/parseFloat v)))

(defn- dock-magnify!
  "Attach macOS Dock-style magnification to the section bar.
   Dots scale up based on proximity to cursor; neighbours also grow."
  [bar dots]
  (let [max-scale  2.4
        base-scale 1.0
        radius     80  ;; px influence radius
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
                  ;; smooth gaussian-ish falloff
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
        ;; Update index panel entries
        (doseq [el entry-els]
          (.remove (.-classList el) "pres-index-entry--active"))
        (when (and sec (< sec (count entry-els)))
          (let [el (nth entry-els sec)]
            (.add (.-classList el) "pres-index-entry--active")
            (.scrollIntoView el #js {:block "nearest" :behavior "smooth"})))
        ;; Update section dots
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
        ;; Animate old slide out (crossfade)
        (when-let [old-slide (nth slides old-idx nil)]
          (.remove (.-classList old-slide) "pres-active")
          (.add (.-classList old-slide) "pres-exiting")
          (when (= direction :back)
            (.add (.-classList old-slide) "pres-going-back"))
          ;; Clean up old slide after exit transition
          (js/setTimeout
            (fn []
              (.remove (.-classList old-slide) "pres-exiting" "pres-going-back"))
            450))
        ;; Animate new slide in
        (let [new-slide (nth slides idx)]
          (when (= direction :back)
            (.add (.-classList new-slide) "pres-going-back"))
          ;; Force reflow then add active
          (.-offsetHeight new-slide)
          (.add (.-classList new-slide) "pres-active")
          (when (= direction :back)
            ;; Remove going-back after transition starts
            (js/requestAnimationFrame
              #(.remove (.-classList new-slide) "pres-going-back")))
          ;; Reset and re-trigger .animate elements with delay for slide to settle
          (doseq [node (array-seq (.querySelectorAll new-slide ".animate"))]
            (.remove (.-classList node) "visible"))
          (js/setTimeout
            (fn []
              (doseq [node (array-seq (.querySelectorAll new-slide ".animate"))]
                (.add (.-classList node) "visible")))
            180))
        ;; Animate zoom/pan back to default if zoomed, otherwise instant reset
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
        ;; Update indicator & section highlight
        (set! (.-textContent indicator) (str (inc idx) " / " n))
        (swap! state/pres-state assoc :current idx)
        (pres-update-section! idx)
        ;; Persist to session
        (pres-save-session! idx)))))

(declare exit-presentation!)

(defn- setup-presentation!
  "Core setup: build overlay, register listeners, show starting slide.
   Called inside or outside a View Transition."
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
    ;; Build index panel (unified paper-styled TOC)
    (let [;; Group slide indices by page-id for section dots & highlighting
          slide-groups (reduce
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
          ;; Build navigate callback: page-id → first slide index
          page-id->slide (into {} (map (fn [{:keys [pid indices]}] [pid (first indices)]) slide-groups))
          pres-navigate! (fn [page-id]
                           (when-let [idx (get page-id->slide page-id)]
                             (pres-show-page! idx nil)))
          ;; Use unified TOC panel with course toc-groups
          toc-groups     (or (:toc-groups @state/current-nav) [])
          toc-result     (nav/build-toc-panel pres-navigate! toc-groups ui/pres-shortcuts)
          toc-overlay    (:overlay toc-result)
          toc-panel      (:panel toc-result)
          toggle!        (:toggle! toc-result)
          entry-els      []  ;; Not used for paper-styled panel highlighting
          ;; Section dots bar — clickable dots at the bottom
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
          idx-btn     (doto (d/el :button {:class "pres-toolbar-btn" :title (i18n/t :select-section)}
                                  (d/ic "list" ""))
                            (.addEventListener "mouseenter" sfx/row-enter-handler)
                            (.addEventListener "click" toggle!))
          fs-btn      (doto (d/el :button {:class "pres-toolbar-btn" :title (i18n/t :fullscreen)}
                                  (d/ic "maximize-2" ""))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" #(pres-toggle-fullscreen! overlay)))
          max-btn     (doto (d/el :button {:class "pres-toolbar-btn"
                                          :title (i18n/t :maximize-slide)
                                          :aria-label (i18n/t :maximize-slide)}
                                  (d/ic "square" ""))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" #(pres-toggle-maximize!)))
          sec-btn     (doto (d/el :button {:class "pres-toolbar-btn"
                                          :title (i18n/t :section-mode)
                                          :aria-label (i18n/t :section-mode)}
                                  (d/ic "layers" ""))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" #(pres-toggle-section-mode!)))
          exit-btn    (doto (d/el :button {:class "pres-toolbar-btn" :aria-label (i18n/t :close)}
                                  (d/ic "x" ""))
                            (.addEventListener "mouseenter" sfx/row-enter-handler)
                            (.addEventListener "click" #(exit-presentation!)))
          ;; ── Zoom controls (buttons + value label, no sliders) ──
          zoom-label  (d/el :span {:class "pres-val-label"} "100%")
          set-zoom!   (fn [z]
                        (let [z (max 0.25 (min 20.0 z))
                              old-z (or (:zoom @state/pres-state) 1.0)
                              zr (if (pos? old-z) (/ z old-z) 1)]
                          (swap! state/pres-state
                            (fn [s]
                              (assoc s :zoom z
                                       :pan-x (* (or (:pan-x s) 0) zr)
                                       :pan-y (* (or (:pan-y s) 0) zr))))
                          (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))
                          (pres-apply-view!)))
          zoom-out-btn (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                            :title (i18n/t :zoom-out)}
                                   (d/ic "minus" ""))
                         (.addEventListener "mouseenter" sfx/row-enter-handler)
                         (.addEventListener "click"
                           (fn [] (set-zoom! (- (or (:zoom @state/pres-state) 1.0) 0.1)))))
          zoom-in-btn  (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                            :title (i18n/t :zoom-in)}
                                   (d/ic "plus" ""))
                         (.addEventListener "mouseenter" sfx/row-enter-handler)
                         (.addEventListener "click"
                           (fn [] (set-zoom! (+ (or (:zoom @state/pres-state) 1.0) 0.1)))))
          zoom-controls (d/el :div {:class "pres-ctrl-group"}
                              zoom-out-btn zoom-label zoom-in-btn)
          ;; ── Text scale controls (buttons + value label, no sliders) ──
          ts-label    (d/el :span {:class "pres-val-label"} "A 100%")
          set-text-scale! (fn [s]
                            (let [s (max 0.5 (min 5.0 s))]
                              (swap! state/pres-state assoc :text-scale s)
                              (set! (.-textContent ts-label) (str "A " (js/Math.round (* s 100)) "%"))
                              (pres-apply-view!)))
          ts-down-btn (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                           :title "Smaller text"}
                                  (d/el :span {:class "pres-ts-icon pres-ts-icon--sm"} "A"))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click"
                          (fn [] (set-text-scale! (- (or (:text-scale @state/pres-state) 1.0) 0.02)))))
          ts-up-btn   (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                           :title "Bigger text"}
                                  (d/el :span {:class "pres-ts-icon pres-ts-icon--lg"} "A"))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click"
                          (fn [] (set-text-scale! (+ (or (:text-scale @state/pres-state) 1.0) 0.02)))))
          ts-controls (d/el :div {:class "pres-ctrl-group"} ts-down-btn ts-label ts-up-btn)
          sm-btn      (when (sm/available?)
                        (doto (d/el :button {:class "pres-toolbar-btn sm-indicator"
                                             :title "SpaceMouse"
                                             :aria-label "SpaceMouse"}
                                    (d/ic "move-3d" "sm-icon"))
                          (.addEventListener "mouseenter" sfx/row-enter-handler)
                          (.addEventListener "click" #(sm/connect!))))
          toolbar-el  (apply d/el :div {:class "pres-toolbar"}
                        (remove nil? [idx-btn zoom-controls ts-controls fs-btn max-btn sec-btn sm-btn exit-btn]))
          ;; ── Highlight cursor ──
          hl-cursor   (d/el :div {:class "pres-highlight-cursor"})
          on-mouse    (fn [e]
                        (.setProperty (.-style hl-cursor) "--hl-x" (str (.-clientX e) "px"))
                        (.setProperty (.-style hl-cursor) "--hl-y" (str (.-clientY e) "px")))
          ;; ── Game-engine style smooth pan + zoom (delta-time based) ──
          held-keys      (atom #{})
          ;; Mutable physics state in a single JS object for zero GC pressure
          ^js phy        #js {:vx 0 :vy 0 :zv 0 :wx 0 :wy 0 :last 0
                              :kx 0 :ky 0 :kz 0   ;; key-active flags (1/0) for release detection
                              :spx 0 :spy 0        ;; spring offset position (pan)
                              :svx 0 :svy 0        ;; spring offset velocity (pan)
                              :spz 0 :szv 0}       ;; spring offset for zoom
          pan-speed      (atom 500)    ;; px/sec
          zoom-speed     1.2           ;; zoom units/sec
          ;; Release half-life (glide feel when you let go)
          release        0.28          ;; 280ms — more inertia
          zoom-release   0.24
          ;; Spring overshoot params (underdamped)
          sp-stiff       350           ;; spring stiffness — higher = faster settle
          sp-damp        14            ;; damping — lower = more bouncy
          sp-impulse     0.055         ;; impulse factor on release
          ;; Zoom spring (subtler)
          zsp-stiff      500
          zsp-damp       20
          zsp-impulse    0.04
          wheel-half     0.45          ;; 450ms wheel momentum
          snap-v         0.5
          snap-z         0.001
          snap-w         0.5
          pan-raf        (atom nil)
          pres-wheel-vel (atom nil)    ;; only for teardown reset
          pan-tick       (fn pan-tick [now]
                           (let [prev  (.-last phy)
                                 raw   (if (pos? prev) (- now prev) 16.67)
                                 dt    (/ (min raw 50) 1000.0)  ;; seconds, capped at 50ms
                                 _     (set! (.-last phy) now)
                                 spd   @pan-speed
                                 keys  @held-keys
                                 ;; Target velocity from held keys (px/sec)
                                 tx    (* (+ (if (contains? keys "h") 1 0)
                                             (if (contains? keys "l") -1 0)) spd)
                                 ty    (* (+ (if (contains? keys "k") 1 0)
                                             (if (contains? keys "j") -1 0)) spd)
                                 tz    (* (+ (if (contains? keys "f") 1 0)
                                             (if (contains? keys "d") -1 0)) zoom-speed)
                                 ;; Instant attack, smooth release: key held = full speed NOW
                                 decay  (js/Math.exp (/ (- dt) release))
                                 zdecay (js/Math.exp (/ (- dt) zoom-release))
                                 old-vx (.-vx phy)
                                 old-vy (.-vy phy)
                                 vx     (if (not= tx 0) tx (* old-vx decay))
                                 vy     (if (not= ty 0) ty (* old-vy decay))
                                 zv     (if (not= tz 0) tz (* (.-zv phy) zdecay))
                                 ;; Snap to zero
                                 vx     (if (< (js/Math.abs vx) snap-v) 0 vx)
                                 vy     (if (< (js/Math.abs vy) snap-v) 0 vy)
                                 zv     (if (< (js/Math.abs zv) snap-z) 0 zv)
                                 ;; Detect key release transition → fire spring impulse
                                 old-zv (.-zv phy)
                                 _      (when (and (== tx 0) (== 1 (.-kx phy)))
                                          (set! (.-svx phy) (+ (.-svx phy) (* old-vx sp-impulse))))
                                 _      (when (and (== ty 0) (== 1 (.-ky phy)))
                                          (set! (.-svy phy) (+ (.-svy phy) (* old-vy sp-impulse))))
                                 _      (when (and (== tz 0) (== 1 (.-kz phy)))
                                          (set! (.-szv phy) (+ (.-szv phy) (* old-zv zsp-impulse))))
                                 _      (set! (.-kx phy) (if (not= tx 0) 1 0))
                                 _      (set! (.-ky phy) (if (not= ty 0) 1 0))
                                 _      (set! (.-kz phy) (if (not= tz 0) 1 0))
                                 ;; Pan spring overshoot (underdamped harmonic oscillator)
                                 sp-ax  (- (- (* sp-stiff (.-spx phy))) (* sp-damp (.-svx phy)))
                                 sp-ay  (- (- (* sp-stiff (.-spy phy))) (* sp-damp (.-svy phy)))
                                 n-svx  (+ (.-svx phy) (* sp-ax dt))
                                 n-svy  (+ (.-svy phy) (* sp-ay dt))
                                 n-spx  (+ (.-spx phy) (* n-svx dt))
                                 n-spy  (+ (.-spy phy) (* n-svy dt))
                                 ;; Snap pan spring to zero when settled
                                 n-spx  (if (and (< (js/Math.abs n-spx) 0.1) (< (js/Math.abs n-svx) 0.5)) 0 n-spx)
                                 n-spy  (if (and (< (js/Math.abs n-spy) 0.1) (< (js/Math.abs n-svy) 0.5)) 0 n-spy)
                                 n-svx  (if (== n-spx 0) 0 n-svx)
                                 n-svy  (if (== n-spy 0) 0 n-svy)
                                 ;; Reset pan spring when actively moving
                                 n-spx  (if (not= tx 0) 0 n-spx)
                                 n-spy  (if (not= ty 0) 0 n-spy)
                                 n-svx  (if (not= tx 0) 0 n-svx)
                                 n-svy  (if (not= ty 0) 0 n-svy)
                                 ;; Zoom spring overshoot
                                 zs-a   (- (- (* zsp-stiff (.-spz phy))) (* zsp-damp (.-szv phy)))
                                 n-szv  (+ (.-szv phy) (* zs-a dt))
                                 n-spz  (+ (.-spz phy) (* n-szv dt))
                                 n-spz  (if (and (< (js/Math.abs n-spz) 0.0005) (< (js/Math.abs n-szv) 0.005)) 0 n-spz)
                                 n-szv  (if (== n-spz 0) 0 n-szv)
                                 n-spz  (if (not= tz 0) 0 n-spz)
                                 n-szv  (if (not= tz 0) 0 n-szv)
                                 ;; Wheel momentum — exponential decay
                                 wfac   (js/Math.exp (/ (- dt) wheel-half))
                                 wx     (* (.-wx phy) wfac)
                                 wy     (* (.-wy phy) wfac)
                                 wx     (if (< (js/Math.abs wx) snap-w) 0 wx)
                                 wy     (if (< (js/Math.abs wy) snap-w) 0 wy)
                                 ;; SpaceMouse continuous input
                                 sm     @state/sm-translate
                                 smx    (or (:x sm) 0)
                                 smy    (or (:y sm) 0)
                                 smz    (or (:z sm) 0)
                                 sm-sc  (/ spd 350.0)
                                 sm-dx  (* smx sm-sc dt)
                                 sm-dy  (* smy sm-sc dt)
                                 sm-dz  (* (/ smz -500.0) zoom-speed dt)
                                 ;; Displacement = base velocity + spring offset + wheel + spacemouse
                                 dx     (+ (* vx dt) (- n-spx (.-spx phy)) (* wx dt) sm-dx)
                                 dy     (+ (* vy dt) (- n-spy (.-spy phy)) (* wy dt) sm-dy)
                                 dz     (+ (* zv dt) (- n-spz (.-spz phy)) sm-dz)
                                 ;; Anything alive?
                                 alive  (or (not= vx 0) (not= vy 0) (not= zv 0)
                                            (not= wx 0) (not= wy 0)
                                            (not= n-spx 0) (not= n-spy 0)
                                            (not= n-spz 0)
                                            (not= smx 0) (not= smy 0) (not= smz 0))]
                             ;; Write back to mutable state
                             (set! (.-vx phy) vx)
                             (set! (.-vy phy) vy)
                             (set! (.-zv phy) zv)
                             (set! (.-wx phy) wx)
                             (set! (.-wy phy) wy)
                             (set! (.-spx phy) n-spx)
                             (set! (.-spy phy) n-spy)
                             (set! (.-svx phy) n-svx)
                             (set! (.-svy phy) n-svy)
                             (set! (.-spz phy) n-spz)
                             (set! (.-szv phy) n-szv)
                             ;; Apply position + zoom in one batch swap
                             (let [moved (or (not= dx 0) (not= dy 0) (not= dz 0))]
                               (when moved
                                 (if (and @state/canvas-zoom-active? (not= dz 0))
                                   ;; Canvas zoom: redirect zoom delta to transform scale
                                   (do (swap! state/canvas-zoom #(max 0.25 (min 10.0 (+ % dz))))
                                       (when (or (not= dx 0) (not= dy 0))
                                         (swap! state/pres-state
                                           (fn [s]
                                             (assoc s :pan-x (+ (or (:pan-x s) 0) dx)
                                                      :pan-y (+ (or (:pan-y s) 0) dy))))))
                                   ;; Normal zoom
                                   (do (swap! state/pres-state
                                         (fn [s]
                                           (let [old-z (or (:zoom s) 1.0)
                                                 z  (max 0.25 (min 20.0 (+ old-z dz)))
                                                 zr (if (and (not= dz 0) (pos? old-z)) (/ z old-z) 1)
                                                 px (* (+ (or (:pan-x s) 0) dx) zr)
                                                 py (* (+ (or (:pan-y s) 0) dy) zr)]
                                             (assoc s :pan-x px :pan-y py :zoom z))))
                                       (when (not= dz 0)
                                         (let [z (or (:zoom @state/pres-state) 1.0)]
                                           (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))))))
                                 (pres-apply-view!))
                               ;; Keep looping while anything is moving
                               (if (or (seq keys) alive)
                                 (reset! pan-raf (js/requestAnimationFrame pan-tick))
                                 (do (set! (.-last phy) 0)
                                     (reset! pan-raf nil))))))
          ensure-raf!    (fn [] (when-not @pan-raf
                                  (set! (.-last phy) 0)
                                  (reset! pan-raf (js/requestAnimationFrame pan-tick))))
          ;; ── Touch pan for presentation mode (mobile) ─────────────
          touch-pan      (atom nil)
          on-touch-start (fn [e]
                           (when (= (.-length (.-touches e)) 1)
                             (let [t (aget (.-touches e) 0)]
                               (reset! touch-pan {:x0 (.-clientX t) :y0 (.-clientY t)
                                                  :x  (.-clientX t) :y  (.-clientY t)
                                                  :moved false}))))
          on-touch-move  (fn [e]
                           (.preventDefault e)
                           (when-let [p @touch-pan]
                             (when (= (.-length (.-touches e)) 1)
                               (let [t   (aget (.-touches e) 0)
                                     dx  (- (.-clientX t) (:x p))
                                     dy  (- (.-clientY t) (:y p))
                                     tot-dx (js/Math.abs (- (.-clientX t) (:x0 p)))
                                     tot-dy (js/Math.abs (- (.-clientY t) (:y0 p)))
                                     moved? (or (:moved p) (> (+ tot-dx tot-dy) 10))]
                                 (when moved?
                                   (set! (.-wx phy) (+ (.-wx phy) (* dx 1.8)))
                                   (set! (.-wy phy) (+ (.-wy phy) (* dy 1.8)))
                                   (ensure-raf!))
                                 (reset! touch-pan {:x0 (:x0 p) :y0 (:y0 p)
                                                    :x  (.-clientX t) :y (.-clientY t)
                                                    :moved moved?})))))
          on-touch-end   (fn [e]
                           (when-let [p @touch-pan]
                             ;; Tap (no significant move) → navigate by screen half
                             ;; but NOT if the tap landed on a toolbar/UI element
                             (when (not (:moved p))
                               (let [raw-target (.-target e)
                                     target (if (= (.-nodeType raw-target) 1)
                                              raw-target
                                              (.-parentElement raw-target))
                                     on-ui? (when target
                                              (.closest target ".pres-toolbar, .pres-toolbar-btn, .pres-index, .pres-index-scrim, .toc-wrapper, .toc-overlay, .pres-section-bar, .pres-indicator, button, a"))]
                                 (when-not on-ui?
                                   (let [mid (/ (.-innerWidth js/window) 2)
                                         x   (:x0 p)]
                                     (if (< x mid)
                                       (pres-show-page! (dec (or (:current @state/pres-state) 0)) :back)
                                       (pres-show-page! (inc (or (:current @state/pres-state) 0)) nil)))))))
                           (reset! touch-pan nil))
          on-key      (fn [e]
                        (let [k (.-key e)]
                          (if (and (= k "g") (or (.-ctrlKey e) (.-metaKey e)))
                            (do (.preventDefault e) (.stopPropagation e) (omni/toggle!))
                          (when-not (.closest (.-target e) "input, textarea, select")
                          ;; When hints are active, route letters to hint system
                          (if (and @state/hint-state (re-matches #"[a-zA-Z]" k))
                            (do (.preventDefault e) (.stopPropagation e)
                                (when-not (hints/hint-type-char! k)
                                  (hints/dismiss-hints!)))
                          (if (#{"h" "j" "k" "l" "d" "f"} k)
                            ;; Continuous keys — track held state for RAF loop
                            (do (.preventDefault e) (.stopPropagation e)
                                (when-not (contains? @held-keys k)
                                  (swap! held-keys conj k)
                                  (ensure-raf!)))
                            (case k
                              ("ArrowRight" "ArrowDown" " " ";")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (pres-show-page! (inc (:current @state/pres-state)) nil))
                              ("ArrowLeft" "ArrowUp" "n")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (pres-show-page! (dec (:current @state/pres-state)) :back))
                              ("Escape" "q")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (exit-presentation!))
                              ("+" "=")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (if @state/canvas-zoom-active?
                                    (do (swap! state/canvas-zoom #(min 10.0 (+ % 0.1)))
                                         (pres-apply-view!))
                                    (set-zoom! (+ (or (:zoom @state/pres-state) 1.0) 0.1))))
                              "-"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (if @state/canvas-zoom-active?
                                    (do (swap! state/canvas-zoom #(max 0.25 (- % 0.1)))
                                         (pres-apply-view!))
                                    (set-zoom! (- (or (:zoom @state/pres-state) 1.0) 0.1))))
                              "a"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (swap! pan-speed #(max 0 (- % 50)))
                                  (ui/show-toast! (str "Speed " @pan-speed)))
                              "s"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (swap! pan-speed #(min 2000 (+ % 50)))
                                  (ui/show-toast! (str "Speed " @pan-speed)))
                              "0"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (reset! pan-speed 500)
                                  (reset! state/canvas-zoom 1.0)
                                  (reset! state/canvas-zoom-active? false)
                                  (anim/animate-view-to! state/pres-state
                                    {:zoom 1.0 :text-scale (or (:text-scale @state/pres-state) 1.0) :pan-x 0 :pan-y 0}
                                    pres-apply-view! :phy phy :duration 500
                                    :on-done (fn []
                                               (set! (.-textContent zoom-label) "100%"))))
                              "\\"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (swap! state/pres-state assoc :text-scale 1.0)
                                  (pres-apply-view!)
                                  (set! (.-textContent ts-label) "A 100%"))
                              "]"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-text-scale! (+ (or (:text-scale @state/pres-state) 1.0) 0.02)))
                              "["
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-text-scale! (- (or (:text-scale @state/pres-state) 1.0) 0.02)))
                              "t"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-text-scale! (+ (or (:text-scale @state/pres-state) 1.0) 0.02)))
                              "T"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-text-scale! (- (or (:text-scale @state/pres-state) 1.0) 0.02)))
                              "r"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (.reload (.-location js/window)))
                              "z"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (.toggle (.-classList hl-cursor) "pres-highlight-cursor--active"))
                              "e"
                              (let [reset-zoom! (fn [& [cb]]
                                                  (anim/animate-view-to! state/pres-state
                                                    {:zoom 1.0 :text-scale (or (:text-scale @state/pres-state) 1.0) :pan-x 0 :pan-y 0}
                                                    pres-apply-view! :phy phy
                                                    :on-done (fn []
                                                               (set! (.-textContent zoom-label) "100%")
                                                               (when cb (cb)))))]
                                (do (.preventDefault e) (.stopPropagation e)
                                  (if @state/hint-state
                                    (do (hints/dismiss-hints!) (reset-zoom!))
                                    (let [near-default? (and (<= (js/Math.abs (- (or (:zoom @state/pres-state) 1.0) 1.0)) 0.05)
                                                            (<= (js/Math.abs (or (:pan-x @state/pres-state) 0)) 5)
                                                            (<= (js/Math.abs (or (:pan-y @state/pres-state) 0)) 5))
                                          show-fn (fn []
                                                    (when-let [slide (nth slide-els (or (:current @state/pres-state) 0) nil)]
                                                      (hints/show-hints! slide
                                                        (fn [el]
                                                          (let [er (hints/text-focused-rect el)
                                                                ex (+ (.-left er) (/ (.-width er) 2))
                                                                ey (+ (.-top er) (/ (.-height er) 2))
                                                                vw (.-innerWidth js/window)
                                                                vh (.-innerHeight js/window)
                                                                sx (- ex (/ vw 2))
                                                                sy (- ey (/ vh 2))
                                                                ew (max 1 (.-width er))
                                                                eh (max 1 (.-height er))
                                                                z  (min 4.0 (max 1.2 (* 0.65 (min (/ vw ew) (/ vh eh)))))]
                                                            (anim/animate-view-to! state/pres-state
                                                              {:zoom z :text-scale (or (:text-scale @state/pres-state) 1.0)
                                                               :pan-x (- (* sx z)) :pan-y (- (* sy z))}
                                                              pres-apply-view! :phy phy
                                                              :on-done (fn []
                                                                         (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%")))))))))]
                                      (if near-default?
                                        (show-fn)
                                        (reset-zoom! show-fn))))))
                              ("1" "2" "3" "4" "5" "6" "7" "8" "9")
                              (do (.preventDefault e) (.stopPropagation e)
                                (let [num (js/parseInt k 10)]
                                  ;; Jump to section N
                                  (when (and (<= 1 num) (<= num (count slide-groups)))
                                    (pres-show-page! (first (:indices (nth slide-groups (dec num)))) nil))))
                              "?"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (ui/toggle-shortcuts! ui/pres-shortcuts))
                              "i"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (toggle!))
                              "y"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (if @state/canvas-zoom-active?
                                    (do (pres-commit-canvas-zoom!)
                                        (reset! state/canvas-zoom-active? false)
                                        (ui/show-toast! "Canvas zoom OFF"))
                                    (do (reset! state/canvas-zoom-active? true)
                                        (ui/show-toast! "Canvas zoom ON — f/d to zoom, y to exit" 2000)))
                                  (pres-apply-view!))
                              "p"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (.toggle (.-classList (.-documentElement js/document)) "ui-hidden"))
                              "u"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (toggle-view!))
                              "o"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (toggle-fullscreen!))
                              nil)))))))
          on-keyup    (fn [e]
                        (let [k (.-key e)]
                          (when (#{"h" "j" "k" "l" "d" "f"} k)
                            (swap! held-keys disj k))))
          on-wheel    (fn [e]
                        (.preventDefault e)
                        (let [dx (.-deltaX e)
                              dy (.-deltaY e)
                              ;; Scale wheel impulse with speed setting (base 12 at speed 500)
                              impulse (* 12 (/ @pan-speed 500))]
                          (set! (.-wx phy) (- (.-wx phy) (* dx impulse)))
                          (set! (.-wy phy) (- (.-wy phy) (* dy impulse)))
                          (ensure-raf!)))
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
                          :zoom (or (pres-restore-zoom) 1.0)
                          :text-scale (or (pres-restore-text-scale) 1.0)
                          :fs-btn fs-btn :max-btn max-btn
                          :on-key on-key :on-keyup on-keyup :on-mouse on-mouse :on-wheel on-wheel
                          :on-touch-start on-touch-start :on-touch-move on-touch-move :on-touch-end on-touch-end
                          :held-keys held-keys :phy phy :pan-raf pan-raf
                          :on-fschange on-fschange :on-resize on-resize})
      ;; Register SpaceMouse callbacks for pres mode
      (reset! state/sm-on-prev #(pres-show-page! (dec (or (:current @state/pres-state) 0)) :back))
      (reset! state/sm-on-next #(pres-show-page! (inc (or (:current @state/pres-state) 0)) nil))
      (reset! state/sm-on-reset #(do (set-zoom! 1.0)
                                     (set-text-scale! 1.0)
                                     (swap! state/pres-state assoc :pan-x 0 :pan-y 0)
                                     (pres-apply-view!)))
      ;; Register SpaceMouse RAF callback for pres mode
      (reset! state/sm-ensure-raf! ensure-raf!)
      (when sm-btn (sm/update-indicator!))
      ;; Sync zoom & text-scale UI with restored values
      (let [z (or (:zoom @state/pres-state) 1.0)
            ts (or (:text-scale @state/pres-state) 1.0)]
        (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))
        (set! (.-textContent ts-label) (str "A " (js/Math.round (* ts 100)) "%")))
      ;; Compute scale
      (pres-apply-view!)
      ;; Add presenting class (fades out document), then settle after transition
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
      ;; Show starting slide — use same path as navigation so it looks like the rest
      (pres-show-page! start nil)
      ;; Tag slide landmarks for View Transition matching
      (slides/tag-vt! (nth slide-els start))
      ;; Re-render lucide icons
      (when (and js/lucide (.-createIcons js/lucide))
        (.createIcons js/lucide #js {:root overlay}))
      ;; Return overlay for fullscreen
      overlay)))

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
      ;; Remove ui-hidden if it was toggled
      (.remove (.-classList (.-documentElement js/document)) "ui-hidden")
      ;; Clear session & state
      (pres-clear-session!)
      (reset! state/pres-state nil)
      ;; Reset SpaceMouse callbacks
      (reset! state/sm-on-prev nil)
      (reset! state/sm-on-next nil)
      (reset! state/sm-on-reset nil)
      ;; Restore SpaceMouse RAF to doc mode
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
         ;; ── View Transitions morph ──
         (do
           ;; Tag source page landmarks (old state)
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
         ;; ── Fallback ──
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
            ;; ── Fallback ──
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
  "Toggle between document (page) mode and presentation (slide) mode.
   Preserves fullscreen state across the switch."
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
      ;; In doc mode, fullscreen the whole page
      (let [el (.-documentElement js/document)]
        (when (.-requestFullscreen el) (.requestFullscreen el))
        (when (.-webkitRequestFullscreen el) (.webkitRequestFullscreen el))))))
