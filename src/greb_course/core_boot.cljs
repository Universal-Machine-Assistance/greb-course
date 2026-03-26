(ns greb-course.core-boot
  "Course boot: viewer setup, physics, keyboard handling."
  (:require [greb-course.dom                :as d]
            [greb-course.i18n               :as i18n]
            [greb-course.state              :as state]
            [greb-course.ui                 :as ui]
            [greb-course.animation          :as anim]
            [greb-course.hints              :as hints]
            [greb-course.spacemouse         :as sm]
            [greb-course.presentation       :as pres]
            [greb-course.catalog            :as catalog]
            [greb-course.theme              :as theme]
            [greb-course.viewer             :as viewer]
            [greb-course.omnirepl           :as omni]
            [greb-course.editor             :as editor]
            [cljs.reader                    :as reader]))

;; ── Layout ───────────────────────────────────────────────────

(defn- mobile-layout? []
  (.-matches (js/matchMedia "(max-width: 840px)")))

(defn- hide-boot-loader! []
  (when-let [el (.getElementById js/document "app-boot-loader")]
    (.add (.-classList el) "app-boot-loader--out")
    (js/setTimeout #(when (and el (.-parentNode el)) (.remove el)) 480)))

(defn- fit-reader-scale! []
  (when-let [reader (.querySelector js/document ".reader")]
    (let [mobile?  (mobile-layout?)
          rem-px   (js/parseFloat (.-fontSize (js/getComputedStyle (.-documentElement js/document))))
          page-w   816  page-h 1056
          gap      (if mobile? 0 3)
          side-pad (* (if mobile? 1.0 5.5) rem-px)
          v-pad    (* (if mobile? 0.55 1.25) rem-px)
          pages    (if mobile? 1 2)
          spread-w (+ (* pages page-w) gap (* 2 side-pad))
          spread-h (+ page-h (* 2 v-pad))
          rw       (.-clientWidth reader)
          rh       (.-clientHeight reader)
          scale    (min 1 (/ rw spread-w) (/ rh spread-h))]
      (.setProperty (.-style reader) "--reader-scale" (str (max (if mobile? 0.35 0.5) scale))))))

;; ── Boot course (called by core/boot) ────────────────────────
;; Callbacks are passed in `opts` to avoid circular deps with core.cljs:
;;   :course-path-fn, :doc-apply-view!, :save-doc-text-scale!,
;;   :restore-doc-text-scale, :commit-canvas-zoom!, :preflight-debounced!, :reload!

(declare boot-course*)

(defn- apply-patches [course patches]
  (if (empty? patches)
    course
    (let [pages (vec (:pages course))]
      (assoc course :pages
        (reduce-kv (fn [ps idx page-def]
                     (if (< idx (count ps))
                       (assoc ps idx page-def)
                       ps))
                   pages patches)))))

(defn- load-local-patches [org]
  (try
    (let [ls-key (str "greb-patches:" org)
          raw (.getItem js/localStorage ls-key)]
      (if raw (reader/read-string raw) {}))
    (catch :default _ {})))

(defn- load-patches! [org cb]
  (let [local (load-local-patches org)]
    (-> (js/fetch (str "/api/patches?org=" org))
        (.then (fn [r] (if (.-ok r) (.text r) (js/Promise.resolve "{}"))))
        (.then (fn [text]
                 (let [server (try (reader/read-string text) (catch :default _ {}))]
                   ;; Merge: local patches override server patches
                   (cb (merge server local)))))
        (.catch (fn [_] (cb local))))))

(defn boot-course [course opts]
  (let [{:keys [course-path-fn doc-apply-view! save-doc-text-scale!
                restore-doc-text-scale commit-canvas-zoom! preflight-debounced! reload!]} opts
        org       (get-in course [:meta :org])]
    ;; Load patches then continue boot
    (load-patches! org
      (fn [patches]
        (boot-course* (apply-patches course patches) opts)))))

(defn- boot-course* [course opts]
  (let [{:keys [course-path-fn doc-apply-view! save-doc-text-scale!
                restore-doc-text-scale commit-canvas-zoom! preflight-debounced! reload!]} opts
        course    (catalog/apply-overrides course)
        meta-data (:meta course)
        theme     (:theme course)
        lang      (or (:lang meta-data) :en)
        overrides (:i18n-overrides meta-data)
        img-base  (or (:images-base theme)
                      (str (course-path-fn course) "images/"))]
    (reset! state/current-course course)
    ;; Load saved illustration style for this course
    (let [course-id (or (:id meta-data) "")
          ls-key (str "greb-style:" course-id)
          saved (.getItem js/localStorage ls-key)]
      (reset! state/illustration-style
        (or saved (get-in meta-data [:style :illustration]))))
    (i18n/init! lang overrides)
    (d/set-brand-name! (or (:brand-name theme) ""))
    (d/set-images-base! img-base)
    (theme/apply-theme! theme)
    (when-let [title (:title meta-data)] (set! (.-title js/document) title))
    (.setAttribute (.-documentElement js/document) "lang" (name lang))
    (reset! state/on-enter-presentation #(pres/enter-presentation!))
    (let [app (.getElementById js/document "app")]
      (.appendChild app (viewer/build-viewer course))
      (fit-reader-scale!)
      (when-not (state/scale-resize-bound?)
        (.addEventListener js/window "resize" fit-reader-scale!)
        (state/set-scale-resize-bound!))
      (when (and js/lucide (.-createIcons js/lucide)) (.createIcons js/lucide))
      (js/requestAnimationFrame
        (fn [] (js/requestAnimationFrame
                 (fn [] (hide-boot-loader!) (omni/mount-embedded-hosts!) (sm/try-reconnect!)))))
      ;; Restore saved text-scale
      (when-let [saved-ts (restore-doc-text-scale)]
        (swap! state/doc-view assoc :text-scale saved-ts)
        (doc-apply-view!))
      ;; Preflight on nav change
      (when-let [nav-state (:nav-state @state/current-nav)]
        (add-watch nav-state ::preflight-on-nav
          (fn [_ _ _ _] (hints/dismiss-hints!) (preflight-debounced!))))
      ;; ── Document-mode physics engine ──
      (let [doc-held      (atom #{})
            ^js phy       #js {:vx 0 :vy 0 :zv 0 :wx 0 :wy 0 :last 0
                               :kx 0 :ky 0 :kz 0 :spx 0 :spy 0 :svx 0 :svy 0 :spz 0 :szv 0}
            doc-raf       (atom nil)
            pan-speed     (atom 350)
            zoom-spd      0.4
            rel 0.28  zr 0.24  wh 0.45
            sv 0.5  sz 0.001  sw 0.5
            ss 350  sd 14  si 0.055
            zss 500  zsd 20  zsi 0.04
            doc-tick
            (fn doc-tick [now]
              (let [prev (.-last phy) raw (if (pos? prev) (- now prev) 16.67)
                    dt (/ (min raw 50) 1000.0) _ (set! (.-last phy) now)
                    reader (.querySelector js/document ".reader")
                    rs (if reader (let [v (js/parseFloat (.getPropertyValue (.-style reader) "--reader-scale"))]
                                    (if (js/isNaN v) 1 (max 0.3 v))) 1)
                    spd (* @pan-speed rs)
                    keys @doc-held
                    tx (* (+ (if (contains? keys "h") 1 0) (if (contains? keys "l") -1 0)) spd)
                    ty (* (+ (if (contains? keys "k") 1 0) (if (contains? keys "j") -1 0)) spd)
                    tz (* (+ (if (contains? keys "f") 1 0) (if (contains? keys "d") -1 0)) zoom-spd)
                    decay (js/Math.exp (/ (- dt) rel))  zdecay (js/Math.exp (/ (- dt) zr))
                    old-vx (.-vx phy) old-vy (.-vy phy)
                    vx (if (not= tx 0) tx (* old-vx decay))
                    vy (if (not= ty 0) ty (* old-vy decay))
                    zv (if (not= tz 0) tz (* (.-zv phy) zdecay))
                    vx (if (< (js/Math.abs vx) sv) 0 vx)
                    vy (if (< (js/Math.abs vy) sv) 0 vy)
                    zv (if (< (js/Math.abs zv) sz) 0 zv)
                    old-zv (.-zv phy)
                    _ (when (and (== tx 0) (== 1 (.-kx phy))) (set! (.-svx phy) (+ (.-svx phy) (* old-vx si))))
                    _ (when (and (== ty 0) (== 1 (.-ky phy))) (set! (.-svy phy) (+ (.-svy phy) (* old-vy si))))
                    _ (when (and (== tz 0) (== 1 (.-kz phy))) (set! (.-szv phy) (+ (.-szv phy) (* old-zv zsi))))
                    _ (set! (.-kx phy) (if (not= tx 0) 1 0))
                    _ (set! (.-ky phy) (if (not= ty 0) 1 0))
                    _ (set! (.-kz phy) (if (not= tz 0) 1 0))
                    ;; Pan spring
                    sp-ax (- (- (* ss (.-spx phy))) (* sd (.-svx phy)))
                    sp-ay (- (- (* ss (.-spy phy))) (* sd (.-svy phy)))
                    n-svx (+ (.-svx phy) (* sp-ax dt)) n-svy (+ (.-svy phy) (* sp-ay dt))
                    n-spx (+ (.-spx phy) (* n-svx dt)) n-spy (+ (.-spy phy) (* n-svy dt))
                    n-spx (if (and (< (js/Math.abs n-spx) 0.1) (< (js/Math.abs n-svx) 0.5)) 0 n-spx)
                    n-spy (if (and (< (js/Math.abs n-spy) 0.1) (< (js/Math.abs n-svy) 0.5)) 0 n-spy)
                    n-svx (if (== n-spx 0) 0 n-svx) n-svy (if (== n-spy 0) 0 n-svy)
                    n-spx (if (not= tx 0) 0 n-spx) n-spy (if (not= ty 0) 0 n-spy)
                    n-svx (if (not= tx 0) 0 n-svx) n-svy (if (not= ty 0) 0 n-svy)
                    ;; Zoom spring
                    zs-a (- (- (* zss (.-spz phy))) (* zsd (.-szv phy)))
                    n-szv (+ (.-szv phy) (* zs-a dt)) n-spz (+ (.-spz phy) (* n-szv dt))
                    n-spz (if (and (< (js/Math.abs n-spz) 0.0005) (< (js/Math.abs n-szv) 0.005)) 0 n-spz)
                    n-szv (if (== n-spz 0) 0 n-szv)
                    n-spz (if (not= tz 0) 0 n-spz) n-szv (if (not= tz 0) 0 n-szv)
                    ;; Wheel
                    wfac (js/Math.exp (/ (- dt) wh))
                    wx (* (.-wx phy) wfac) wy (* (.-wy phy) wfac)
                    wx (if (< (js/Math.abs wx) sw) 0 wx) wy (if (< (js/Math.abs wy) sw) 0 wy)
                    ;; SpaceMouse
                    sm @state/sm-translate smx (or (:x sm) 0) smy (or (:y sm) 0) smz (or (:z sm) 0)
                    sm-sc (/ spd 350.0)
                    dx (+ (* vx dt) (- n-spx (.-spx phy)) (* wx dt) (* smx sm-sc dt))
                    dy (+ (* vy dt) (- n-spy (.-spy phy)) (* wy dt) (* smy sm-sc dt))
                    dz (+ (* zv dt) (- n-spz (.-spz phy)) (* (/ smz -500.0) zoom-spd dt))
                    alive (or (not= vx 0) (not= vy 0) (not= zv 0) (not= wx 0) (not= wy 0)
                              (not= n-spx 0) (not= n-spy 0) (not= n-spz 0)
                              (not= smx 0) (not= smy 0) (not= smz 0))]
                (set! (.-vx phy) vx) (set! (.-vy phy) vy) (set! (.-zv phy) zv)
                (set! (.-wx phy) wx) (set! (.-wy phy) wy)
                (set! (.-spx phy) n-spx) (set! (.-spy phy) n-spy)
                (set! (.-svx phy) n-svx) (set! (.-svy phy) n-svy)
                (set! (.-spz phy) n-spz) (set! (.-szv phy) n-szv)
                (let [moved (or (not= dx 0) (not= dy 0) (not= dz 0))]
                  (when moved
                    (if (and @state/canvas-zoom-active? (not= dz 0))
                      (do (swap! state/canvas-zoom #(max 0.25 (min 10.0 (+ % dz))))
                          (when (or (not= dx 0) (not= dy 0))
                            (swap! state/doc-view (fn [dv] (assoc dv :pan-x (+ (or (:pan-x dv) 0) dx)
                                                                     :pan-y (+ (or (:pan-y dv) 0) dy))))))
                      (swap! state/doc-view
                        (fn [dv] (let [old-z (or (:zoom dv) 1.0) z (max 0.25 (min 20.0 (+ old-z dz)))
                                       zr (if (and (not= dz 0) (pos? old-z)) (/ z old-z) 1)]
                                   (assoc dv :pan-x (* (+ (or (:pan-x dv) 0) dx) zr)
                                             :pan-y (* (+ (or (:pan-y dv) 0) dy) zr) :zoom z)))))
                    (doc-apply-view!))
                  (if (or (seq keys) alive)
                    (reset! doc-raf (js/requestAnimationFrame doc-tick))
                    (do (set! (.-last phy) 0) (reset! doc-raf nil))))))
            ensure-raf! (fn [] (when-not @doc-raf
                                 (set! (.-last phy) 0)
                                 (reset! doc-raf (js/requestAnimationFrame doc-tick))))]
        ;; SpaceMouse callbacks
        (reset! state/sm-doc-ensure-raf! ensure-raf!)
        (when-not @state/pres-state (reset! state/sm-ensure-raf! ensure-raf!))
        (reset! state/sm-on-prev
          #(when-let [{:keys [go! nav-state]} @state/current-nav] (go! (dec @nav-state) "going-back")))
        (reset! state/sm-on-next
          #(when-let [{:keys [go! nav-state]} @state/current-nav] (go! (inc @nav-state) nil)))
        (reset! state/sm-on-reset
          #(when-let [f @state/on-enter-presentation] (f)))
        ;; Keyboard
        (.addEventListener js/document "keydown"
          (fn [e]
            (when-not @state/pres-state
              (if (and (= (.-key e) "g") (or (.-ctrlKey e) (.-metaKey e)))
                (do (.preventDefault e) (omni/toggle!))
              (if (and (= (.-key e) "e") (or (.-ctrlKey e) (.-metaKey e)))
                (do (.preventDefault e) (editor/edit!))
              (when (not (.closest (.-target e) "input, textarea, select"))
                (let [k (.-key e)
                      toc-open? (when-let [tw (.querySelector js/document ".toc-wrapper")]
                                  (.contains (.-classList tw) "open"))]
                  ;; Shift+J/K scrolls left TOC column, Ctrl+J/K scrolls right TOC column
                  (if (and toc-open?
                           (or (.-shiftKey e) (.-ctrlKey e))
                           (#{"j" "k" "J" "K" "ArrowDown" "ArrowUp"} k))
                    (let [col (if (.-ctrlKey e)
                                (.querySelector js/document ".toc-col-left")
                                (.querySelector js/document ".toc-entries-scroll"))
                          dir (if (#{"j" "J" "ArrowDown"} k) 80 -80)]
                      (.preventDefault e)
                      (when col (.scrollBy col #js {:top dir :behavior "smooth"})))
                  (if (and @state/hint-state (re-matches #"[a-zA-Z]" k))
                    (do (.preventDefault e) (when-not (hints/hint-type-char! k) (hints/dismiss-hints!)))
                  (if (#{"h" "j" "k" "l" "d" "f"} k)
                    (do (.preventDefault e)
                        (when-not (contains? @doc-held k) (swap! doc-held conj k) (ensure-raf!)))
                    (let [dv @state/doc-view z (or (:zoom dv) 1.0) ts (or (:text-scale dv) 1.0)]
                      (case k
                        ("+" "=") (do (.preventDefault e)
                                    (if @state/canvas-zoom-active?
                                      (swap! state/canvas-zoom #(min 10.0 (+ % 0.1)))
                                      (let [nz (min 20.0 (+ z 0.01)) zr (/ nz z)]
                                        (swap! state/doc-view #(assoc % :zoom nz :pan-x (* (or (:pan-x %) 0) zr) :pan-y (* (or (:pan-y %) 0) zr)))))
                                    (doc-apply-view!))
                        "-"       (do (.preventDefault e)
                                    (if @state/canvas-zoom-active?
                                      (swap! state/canvas-zoom #(max 0.25 (- % 0.1)))
                                      (let [nz (max 0.25 (- z 0.01)) zr (/ nz z)]
                                        (swap! state/doc-view #(assoc % :zoom nz :pan-x (* (or (:pan-x %) 0) zr) :pan-y (* (or (:pan-y %) 0) zr)))))
                                    (doc-apply-view!))
                        ("]" "t") (do (.preventDefault e)
                                    (let [nts (min 5.0 (+ ts 0.02))]
                                      (swap! state/doc-view assoc :text-scale nts) (save-doc-text-scale! nts)
                                      (doc-apply-view!) (preflight-debounced! 300)))
                        ("[" "T") (do (.preventDefault e)
                                    (let [nts (max 0.5 (- ts 0.02))]
                                      (swap! state/doc-view assoc :text-scale nts) (save-doc-text-scale! nts)
                                      (doc-apply-view!) (preflight-debounced! 300)))
                        "a" (do (.preventDefault e) (swap! pan-speed #(max 0 (- % 50))) (ui/show-toast! (str "Speed " @pan-speed)))
                        "s" (do (.preventDefault e) (swap! pan-speed #(min 2000 (+ % 50))) (ui/show-toast! (str "Speed " @pan-speed)))
                        "0" (do (.preventDefault e) (reset! state/canvas-zoom 1.0) (reset! state/canvas-zoom-active? false)
                                (reset! pan-speed 150)
                                (anim/animate-view-to! state/doc-view
                                  {:zoom 1.0 :text-scale (or (:text-scale @state/doc-view) 1.0) :pan-x 0 :pan-y 0}
                                  doc-apply-view! :phy phy :duration 400))
                        "\\" (do (.preventDefault e) (swap! state/doc-view assoc :text-scale 1.0)
                                 (save-doc-text-scale! 1.0) (doc-apply-view!))
                        ";" (do (.preventDefault e)
                                (when-let [{:keys [go! nav-state]} @state/current-nav] (go! (inc @nav-state) nil)))
                        "n" (do (.preventDefault e)
                                (when-let [{:keys [go! nav-state]} @state/current-nav] (go! (dec @nav-state) "going-back")))
                        "r" (do (.preventDefault e) (.reload (.-location js/window)))
                        "i" (do (.preventDefault e) (when-let [tog (:toggle-toc! @state/current-nav)] (tog)))
                        "z" (do (.preventDefault e) (when-let [hl (.querySelector js/document ".doc-highlight-cursor")]
                                                      (.toggle (.-classList hl) "pres-highlight-cursor--active")))
                        "e" (do (.preventDefault e)
                              (if @state/hint-state
                                (do (hints/dismiss-hints!)
                                    (anim/animate-view-to! state/doc-view
                                      {:zoom 1.0 :text-scale (or (:text-scale @state/doc-view) 1.0) :pan-x 0 :pan-y 0}
                                      doc-apply-view! :phy phy))
                                (let [near? (and (<= (js/Math.abs (- z 1.0)) 0.05)
                                                 (<= (js/Math.abs (or (:pan-x dv) 0)) 5)
                                                 (<= (js/Math.abs (or (:pan-y dv) 0)) 5))
                                      show-fn (fn []
                                                (when-let [spread (.querySelector js/document ".spread.active")]
                                                  (hints/show-hints! spread
                                                    (fn [el]
                                                      (let [er (hints/text-focused-rect el)
                                                            ex (+ (.-left er) (/ (.-width er) 2))
                                                            ey (+ (.-top er) (/ (.-height er) 2))
                                                            vw (.-innerWidth js/window) vh (.-innerHeight js/window)
                                                            nz (min 3.0 (max 1.2 (* 0.5 (min (/ vw (max 1 (.-width er))) (/ vh (max 1 (.-height er)))))))]
                                                        (anim/animate-view-to! state/doc-view
                                                          {:zoom nz :text-scale ts :pan-x (- (* (- ex (/ vw 2)) nz)) :pan-y (- (* (- ey (/ vh 2)) nz))}
                                                          doc-apply-view! :phy phy))))))]
                                  (if near? (show-fn)
                                    (anim/animate-view-to! state/doc-view
                                      {:zoom 1.0 :text-scale ts :pan-x 0 :pan-y 0}
                                      doc-apply-view! :phy phy :on-done show-fn)))))
                        ("1" "2" "3" "4" "5" "6" "7" "8" "9")
                        (do (.preventDefault e)
                          (let [num (js/parseInt k 10)]
                            (when-let [groups (:toc-groups @state/current-nav)]
                              (when (<= num (count groups))
                                (let [pid (:id (first (:entries (nth groups (dec num)))))]
                                  (when-let [{:keys [go! id->spread]} @state/current-nav]
                                    (when-let [idx (get id->spread pid)] (go! idx nil))))))))
                        "y" (do (.preventDefault e)
                              (if @state/canvas-zoom-active?
                                (do (commit-canvas-zoom!) (reset! state/canvas-zoom-active? false) (ui/show-toast! "Canvas zoom OFF"))
                                (do (reset! state/canvas-zoom-active? true) (ui/show-toast! "Canvas zoom ON — f/d to zoom, y to exit" 2000)))
                              (doc-apply-view!))
                        "p" (do (.preventDefault e) (.toggle (.-classList (.-documentElement js/document)) "ui-hidden"))
                        "u" (do (.preventDefault e) (pres/toggle-view!))
                        "o" (do (.preventDefault e) (pres/toggle-fullscreen!))
                        "?" (do (.preventDefault e) (ui/toggle-shortcuts! ui/doc-shortcuts))
                        "Escape" (do (.preventDefault e)
                                   ;; Close TOC if open, otherwise reset view
                                   (let [tw (.querySelector js/document ".toc-wrapper")
                                         toc? (and tw (.contains (.-classList tw) "open"))]
                                     (if toc?
                                       (when-let [tog (:toggle-toc! @state/current-nav)] (tog))
                                       (do (reset! state/canvas-zoom 1.0) (reset! state/canvas-zoom-active? false)
                                           (reset! pan-speed 150)
                                           (anim/animate-view-to! state/doc-view
                                             {:zoom 1.0 :text-scale (or (:text-scale @state/doc-view) 1.0) :pan-x 0 :pan-y 0}
                                             doc-apply-view! :phy phy :duration 400)))))
                        nil))))))))))))
        (.addEventListener js/document "keyup"
          (fn [e] (when-not @state/pres-state
                    (when (#{"h" "j" "k" "l" "d" "f"} (.-key e)) (swap! doc-held disj (.-key e))))))
        (.addEventListener js/document "wheel"
          (fn [e]
            (when-not @state/pres-state
              (.preventDefault e)
              (let [reader (.querySelector js/document ".reader")
                    rs (if reader (let [v (js/parseFloat (.getPropertyValue (.-style reader) "--reader-scale"))]
                                    (if (js/isNaN v) 1 (max 0.3 v))) 1)
                    impulse (* 4 rs (/ @pan-speed 350))]
                (set! (.-wx phy) (- (.-wx phy) (* (.-deltaX e) impulse)))
                (set! (.-wy phy) (- (.-wy phy) (* (.-deltaY e) impulse)))
                (ensure-raf!))))
          #js {:passive false}))
      (preflight-debounced!)
      (sm/try-reconnect!)
      (when-let [saved-idx (pres/pres-restore-session)]
        (js/setTimeout #(pres/enter-presentation! saved-idx) 300)))))

(defn boot-catalog [courses reload!]
  (set! (.-title js/document) "greb-course")
  ;; Disable spacemouse panning on catalog page
  (reset! state/sm-ensure-raf! nil)
  (reset! state/sm-doc-ensure-raf! nil)
  (reset! state/sm-on-prev nil)
  (reset! state/sm-on-next nil)
  (reset! state/sm-on-reset nil)
  (reset! state/sm-translate {:x 0 :y 0 :z 0})
  (let [app (.getElementById js/document "app")]
    (.appendChild app (catalog/build-catalog courses #(reload! @state/current-courses)))
    (when (and js/lucide (.-createIcons js/lucide)) (.createIcons js/lucide))
    (.addEventListener js/document "keydown"
      (fn [e] (when (and (= (.-key e) "g") (or (.-ctrlKey e) (.-metaKey e)))
                (.preventDefault e) (omni/toggle!))))
    (js/requestAnimationFrame
      (fn [] (js/requestAnimationFrame (fn [] (hide-boot-loader!)))))))

(defn boot [courses match-course-fn opts]
  (reset! state/current-courses courses)
  (if-let [course (match-course-fn courses)]
    (boot-course course opts)
    (boot-catalog courses (:reload! opts))))
