(ns greb-course.core
  "Reader shell, navigation, scaling — generic course viewer with routing."
  (:require [greb-course.dom                  :as d]
            [greb-course.i18n                 :as i18n]
            [greb-course.state                :as state]
            [greb-course.ui                   :as ui]
            [greb-course.animation            :as anim]
            [greb-course.hints                :as hints]
            [greb-course.spacemouse           :as sm]
            [greb-course.nav                  :as nav]
            [greb-course.slides               :as slides]
            [greb-course.presentation         :as pres]
            [greb-course.catalog              :as catalog]
            [greb-course.theme                :as theme]
            [greb-course.viewer               :as viewer]
            [greb-course.omnirepl            :as omni]
            [greb-course.editor             :as editor]
            [greb-course.debuglog            :as dbg]
            [greb-course.templates.registry   :as reg]))

(declare reload!)
(declare preflight!)
(declare preflight-debounced!)
(defonce ^:private preflight-timer (atom nil))

(defn- doc-ts-key []
  (str "greb-ts:" (.-pathname js/location)))

(defn- save-doc-text-scale! [ts]
  (try (.setItem js/localStorage (doc-ts-key) (str ts)) (catch :default _)))

(defn- restore-doc-text-scale []
  (try
    (when-let [v (.getItem js/localStorage (doc-ts-key))]
      (let [n (js/parseFloat v)]
        (when-not (js/isNaN n) n)))
    (catch :default _ nil)))

;; ── Routing helpers ─────────────────────────────────────────────
(defn- base-path []
  (let [path (.-pathname js/location)]
    (if (.endsWith path "/") path (str path "/"))))

(defn- course-path [course]
  (let [org  (get-in course [:meta :org])
        slug (get-in course [:meta :slug])]
    (str "/" org "/" slug "/")))

(defn- match-course [courses]
  (let [path (base-path)]
    (first (filter #(= path (course-path %)) courses))))

;; ── Layout helpers ──────────────────────────────────────────────
(defn- mobile-layout? []
  (.-matches (js/matchMedia "(max-width: 840px)")))

(defn- fit-reader-scale! []
  (let [now-mobile? (mobile-layout?)
        rebuilt?    (when (and (some? @state/built-mobile?) (not= now-mobile? @state/built-mobile?))
                     (reset! state/built-mobile? now-mobile?)
                     (reload! @state/current-courses)
                     true)]
    (when-not rebuilt?
      (when-let [reader (.querySelector js/document ".reader")]
        (let [mobile?    now-mobile?
              rem-px     (js/parseFloat (.-fontSize (js/getComputedStyle (.-documentElement js/document))))
              page-w     816
              page-h     1056
              gap        (if mobile? 0 3)
              side-pad   (* (if mobile? 1.0 5.5) rem-px)
              v-pad      (* (if mobile? 0.55 1.25) rem-px)
              pages      (if mobile? 1 2)
              spread-w   (+ (* pages page-w) gap (* 2 side-pad))
              spread-h   (+ page-h (* 2 v-pad))
              rw         (.-clientWidth reader)
              rh         (.-clientHeight reader)
              scale      (min 1 (/ rw spread-w) (/ rh spread-h))]
          (.setProperty (.-style reader) "--reader-scale" (str (max (if mobile? 0.35 0.5) scale))))))))

(defn- commit-canvas-zoom!
  "Transfer canvas zoom (transform:scale, pixelated) into CSS zoom (crisp re-render),
   then reset canvas scale to 1. Scales pan to keep the view centred and suppresses
   transitions so the swap is visually instantaneous."
  []
  (when (and @state/canvas-zoom-active?
             (not= @state/canvas-zoom 1.0))
    (let [cz @state/canvas-zoom]
      ;; Scale pan proportionally so view centre stays fixed
      (swap! state/doc-view
        (fn [dv]
          (let [old-z (or (:zoom dv) 1.0)
                new-z (* old-z cz)]
            (assoc dv
              :zoom  new-z
              :pan-x (* (or (:pan-x dv) 0) cz)
              :pan-y (* (or (:pan-y dv) 0) cz)))))
      (reset! state/canvas-zoom 1.0)
      (when-let [reader (.querySelector js/document ".reader")]
        ;; Suppress transitions so zoom + scale swap is instantaneous
        (when-let [active (.querySelector reader ".spread.active")]
          (.setProperty (.-style active) "transition" "none"))
        (.setProperty (.-style reader) "--canvas-scale" "1")
        (let [{:keys [zoom text-scale pan-x pan-y]} @state/doc-view]
          (.setProperty (.-style reader) "--doc-zoom" (str (or zoom 1.0)))
          (.setProperty (.-style reader) "--doc-pan-x" (str (or pan-x 0) "px"))
          (.setProperty (.-style reader) "--doc-pan-y" (str (or pan-y 0) "px"))
          (ui/update-status-badge! zoom text-scale))
        ;; Force layout, then restore transitions next frame
        (.offsetHeight reader)
        (js/requestAnimationFrame
          (fn []
            (when-let [active (.querySelector reader ".spread.active")]
              (.removeProperty (.-style active) "transition"))))))))

;; schedule-canvas-commit! removed — canvas zoom no longer auto-commits.
;; Commit only happens when the user exits canvas zoom mode (y key / 0 key).

(defn- doc-apply-view! []
  (when-let [reader (.querySelector js/document ".reader")]
    (let [{:keys [zoom text-scale pan-x pan-y]} @state/doc-view]
      (.setProperty (.-style reader) "--doc-zoom" (str (or zoom 1.0)))
      (.setProperty (.-style reader) "--doc-text-scale" (str (or text-scale 1.0)))
      (.setProperty (.-style reader) "--doc-pan-x" (str (or pan-x 0) "px"))
      (.setProperty (.-style reader) "--doc-pan-y" (str (or pan-y 0) "px"))
      (.setProperty (.-style reader) "--canvas-scale" (str @state/canvas-zoom))
      (if (and text-scale (not= text-scale 1.0))
        (.add (.-classList reader) "text-scaled")
        (.remove (.-classList reader) "text-scaled"))
      (ui/update-status-badge! zoom text-scale))))

;; ── Boot ─────────────────────────────────────────────────────────
(defn- hide-boot-loader! []
  (when-let [el (.getElementById js/document "app-boot-loader")]
    (.add (.-classList el) "app-boot-loader--out")
    (js/setTimeout
      (fn []
        (when (and el (.-parentNode el))
          (.remove el)))
      480)))

(defn- boot-course [course]
  (let [course    (catalog/apply-overrides course)
        meta-data (:meta course)
        theme     (:theme course)
        lang      (or (:lang meta-data) :en)
        overrides (:i18n-overrides meta-data)
        img-base  (or (:images-base theme)
                      (str (course-path course) "images/"))]
    (reset! state/current-course course)
    (i18n/init! lang overrides)
    (d/set-brand-name! (or (:brand-name theme) ""))
    (d/set-images-base! img-base)
    (theme/apply-theme! theme)
    (when-let [title (:title meta-data)]
      (set! (.-title js/document) title))
    (.setAttribute (.-documentElement js/document) "lang" (name lang))
    ;; Register enter-presentation! callback for viewer toolbar
    (reset! state/on-enter-presentation #(pres/enter-presentation!))
    (let [app (.getElementById js/document "app")]
      (.appendChild app (viewer/build-viewer course))
      (fit-reader-scale!)
      (when-not (state/scale-resize-bound?)
        (.addEventListener js/window "resize" fit-reader-scale!)
        (state/set-scale-resize-bound!))
      (when (and js/lucide (.-createIcons js/lucide))
        (.createIcons js/lucide))
      (js/requestAnimationFrame
        (fn []
          (js/requestAnimationFrame
            (fn []
              (hide-boot-loader!)
              (omni/mount-embedded-hosts!)
              ;; Auto-reconnect SpaceMouse if previously authorized
              (sm/try-reconnect!)))))
      ;; Restore saved text-scale for this document
      (when-let [saved-ts (restore-doc-text-scale)]
        (swap! state/doc-view assoc :text-scale saved-ts)
        (doc-apply-view!))
      ;; Re-run preflight on every page change & dismiss hints
      (when-let [nav-state (:nav-state @state/current-nav)]
        (add-watch nav-state ::preflight-on-nav
          (fn [_ _ _ _]
            (hints/dismiss-hints!)
            (preflight-debounced!))))
      ;; Document-mode: game-engine delta-time physics (matches pres mode)
      (let [doc-held      (atom #{})
            ^js doc-phy   #js {:vx 0 :vy 0 :zv 0 :wx 0 :wy 0 :last 0
                               :kx 0 :ky 0 :kz 0 :spx 0 :spy 0 :svx 0 :svy 0
                               :spz 0 :szv 0}
            doc-pan-raf   (atom nil)
            doc-pan-speed (atom 500)
            doc-zoom-spd  1.2
            doc-release   0.28
            doc-zr        0.24
            doc-wh        0.45
            doc-snap-v    0.5
            doc-snap-z    0.001
            doc-snap-w    0.5
            dsp-stiff     350
            dsp-damp      14
            dsp-impulse   0.055
            dzsp-stiff    500
            dzsp-damp     20
            dzsp-impulse  0.04
            doc-tick     (fn doc-tick [now]
                           (let [prev   (.-last doc-phy)
                                 raw    (if (pos? prev) (- now prev) 16.67)
                                 dt     (/ (min raw 50) 1000.0)
                                 _      (set! (.-last doc-phy) now)
                                 raw-spd @doc-pan-speed
                                 reader  (.querySelector js/document ".reader")
                                 rs      (if reader
                                           (let [v (js/parseFloat (.getPropertyValue (.-style reader) "--reader-scale"))]
                                             (if (js/isNaN v) 1 (max 0.3 v)))
                                           1)
                                 spd     (* raw-spd rs)
                                 keys   @doc-held
                                 tx     (* (+ (if (contains? keys "h") 1 0)
                                              (if (contains? keys "l") -1 0)) spd)
                                 ty     (* (+ (if (contains? keys "k") 1 0)
                                              (if (contains? keys "j") -1 0)) spd)
                                 tz     (* (+ (if (contains? keys "f") 1 0)
                                              (if (contains? keys "d") -1 0)) doc-zoom-spd)
                                 decay  (js/Math.exp (/ (- dt) doc-release))
                                 zdecay (js/Math.exp (/ (- dt) doc-zr))
                                 old-vx (.-vx doc-phy)
                                 old-vy (.-vy doc-phy)
                                 vx     (if (not= tx 0) tx (* old-vx decay))
                                 vy     (if (not= ty 0) ty (* old-vy decay))
                                 zv     (if (not= tz 0) tz (* (.-zv doc-phy) zdecay))
                                 vx     (if (< (js/Math.abs vx) doc-snap-v) 0 vx)
                                 vy     (if (< (js/Math.abs vy) doc-snap-v) 0 vy)
                                 zv     (if (< (js/Math.abs zv) doc-snap-z) 0 zv)
                                 old-zv (.-zv doc-phy)
                                 _      (when (and (== tx 0) (== 1 (.-kx doc-phy)))
                                          (set! (.-svx doc-phy) (+ (.-svx doc-phy) (* old-vx dsp-impulse))))
                                 _      (when (and (== ty 0) (== 1 (.-ky doc-phy)))
                                          (set! (.-svy doc-phy) (+ (.-svy doc-phy) (* old-vy dsp-impulse))))
                                 _      (when (and (== tz 0) (== 1 (.-kz doc-phy)))
                                          (set! (.-szv doc-phy) (+ (.-szv doc-phy) (* old-zv dzsp-impulse))))
                                 _      (set! (.-kx doc-phy) (if (not= tx 0) 1 0))
                                 _      (set! (.-ky doc-phy) (if (not= ty 0) 1 0))
                                 _      (set! (.-kz doc-phy) (if (not= tz 0) 1 0))
                                 ;; Pan spring overshoot
                                 sp-ax  (- (- (* dsp-stiff (.-spx doc-phy))) (* dsp-damp (.-svx doc-phy)))
                                 sp-ay  (- (- (* dsp-stiff (.-spy doc-phy))) (* dsp-damp (.-svy doc-phy)))
                                 n-svx  (+ (.-svx doc-phy) (* sp-ax dt))
                                 n-svy  (+ (.-svy doc-phy) (* sp-ay dt))
                                 n-spx  (+ (.-spx doc-phy) (* n-svx dt))
                                 n-spy  (+ (.-spy doc-phy) (* n-svy dt))
                                 n-spx  (if (and (< (js/Math.abs n-spx) 0.1) (< (js/Math.abs n-svx) 0.5)) 0 n-spx)
                                 n-spy  (if (and (< (js/Math.abs n-spy) 0.1) (< (js/Math.abs n-svy) 0.5)) 0 n-spy)
                                 n-svx  (if (== n-spx 0) 0 n-svx)
                                 n-svy  (if (== n-spy 0) 0 n-svy)
                                 n-spx  (if (not= tx 0) 0 n-spx)
                                 n-spy  (if (not= ty 0) 0 n-spy)
                                 n-svx  (if (not= tx 0) 0 n-svx)
                                 n-svy  (if (not= ty 0) 0 n-svy)
                                 ;; Zoom spring overshoot
                                 zs-a   (- (- (* dzsp-stiff (.-spz doc-phy))) (* dzsp-damp (.-szv doc-phy)))
                                 n-szv  (+ (.-szv doc-phy) (* zs-a dt))
                                 n-spz  (+ (.-spz doc-phy) (* n-szv dt))
                                 n-spz  (if (and (< (js/Math.abs n-spz) 0.0005) (< (js/Math.abs n-szv) 0.005)) 0 n-spz)
                                 n-szv  (if (== n-spz 0) 0 n-szv)
                                 n-spz  (if (not= tz 0) 0 n-spz)
                                 n-szv  (if (not= tz 0) 0 n-szv)
                                 ;; Wheel
                                 wfac   (js/Math.exp (/ (- dt) doc-wh))
                                 wx     (* (.-wx doc-phy) wfac)
                                 wy     (* (.-wy doc-phy) wfac)
                                 wx     (if (< (js/Math.abs wx) doc-snap-w) 0 wx)
                                 wy     (if (< (js/Math.abs wy) doc-snap-w) 0 wy)
                                 ;; SpaceMouse continuous input
                                 sm     @state/sm-translate
                                 smx    (or (:x sm) 0)
                                 smy    (or (:y sm) 0)
                                 smz    (or (:z sm) 0)
                                 sm-sc  (/ spd 350.0)
                                 sm-dx  (* smx sm-sc dt)
                                 sm-dy  (* smy sm-sc dt)
                                 sm-dz  (* (/ smz -500.0) doc-zoom-spd dt)
                                 ;; Displacement
                                 dx     (+ (* vx dt) (- n-spx (.-spx doc-phy)) (* wx dt) sm-dx)
                                 dy     (+ (* vy dt) (- n-spy (.-spy doc-phy)) (* wy dt) sm-dy)
                                 dz     (+ (* zv dt) (- n-spz (.-spz doc-phy)) sm-dz)
                                 alive  (or (not= vx 0) (not= vy 0) (not= zv 0)
                                            (not= wx 0) (not= wy 0)
                                            (not= n-spx 0) (not= n-spy 0)
                                            (not= n-spz 0)
                                            (not= smx 0) (not= smy 0) (not= smz 0))]
                             (set! (.-vx doc-phy) vx) (set! (.-vy doc-phy) vy)
                             (set! (.-zv doc-phy) zv) (set! (.-wx doc-phy) wx) (set! (.-wy doc-phy) wy)
                             (set! (.-spx doc-phy) n-spx) (set! (.-spy doc-phy) n-spy)
                             (set! (.-svx doc-phy) n-svx) (set! (.-svy doc-phy) n-svy)
                             (set! (.-spz doc-phy) n-spz) (set! (.-szv doc-phy) n-szv)
                             (let [moved (or (not= dx 0) (not= dy 0) (not= dz 0))]
                               (when moved
                                 (if (and @state/canvas-zoom-active? (not= dz 0))
                                   ;; Canvas zoom: redirect zoom delta to transform scale
                                   (do (swap! state/canvas-zoom #(max 0.25 (min 10.0 (+ % dz))))
                                       (when (or (not= dx 0) (not= dy 0))
                                         (swap! state/doc-view
                                           (fn [dv]
                                             (assoc dv :pan-x (+ (or (:pan-x dv) 0) dx)
                                                       :pan-y (+ (or (:pan-y dv) 0) dy))))))
                                   ;; Normal zoom
                                   (swap! state/doc-view
                                     (fn [dv]
                                       (let [old-z (or (:zoom dv) 1.0)
                                             z  (max 0.25 (min 20.0 (+ old-z dz)))
                                             zr (if (and (not= dz 0) (pos? old-z)) (/ z old-z) 1)
                                             px (* (+ (or (:pan-x dv) 0) dx) zr)
                                             py (* (+ (or (:pan-y dv) 0) dy) zr)]
                                         (assoc dv :pan-x px :pan-y py :zoom z)))))
                                 (doc-apply-view!))
                               (if (or (seq keys) alive)
                                 (reset! doc-pan-raf (js/requestAnimationFrame doc-tick))
                                 (do (set! (.-last doc-phy) 0)
                                     (reset! doc-pan-raf nil))))))
            ensure-doc-raf! (fn [] (when-not @doc-pan-raf
                                     (set! (.-last doc-phy) 0)
                                     (reset! doc-pan-raf (js/requestAnimationFrame doc-tick))))]
        ;; Register SpaceMouse RAF callback for doc mode (pres mode overrides when active)
        (reset! state/sm-doc-ensure-raf! ensure-doc-raf!)
        (when-not @state/pres-state
          (reset! state/sm-ensure-raf! ensure-doc-raf!))
        ;; Set SpaceMouse navigation callbacks for doc mode
        (reset! state/sm-on-prev
          #(when-let [{:keys [go! nav-state]} @state/current-nav]
             (go! (dec @nav-state) "going-back")))
        (reset! state/sm-on-next
          #(when-let [{:keys [go! nav-state]} @state/current-nav]
             (go! (inc @nav-state) nil)))
        (reset! state/sm-on-reset
          #(do (reset! state/doc-view {:zoom 1.0 :text-scale 1.0 :pan-x 0 :pan-y 0})
               (reset! doc-pan-speed 500)
               (set! (.-vx doc-phy) 0) (set! (.-vy doc-phy) 0)
               (set! (.-zv doc-phy) 0) (set! (.-wx doc-phy) 0) (set! (.-wy doc-phy) 0)
               (set! (.-spx doc-phy) 0) (set! (.-spy doc-phy) 0) (set! (.-spz doc-phy) 0)
               (set! (.-svx doc-phy) 0) (set! (.-svy doc-phy) 0) (set! (.-szv doc-phy) 0)
               (doc-apply-view!)))
        (.addEventListener js/document "keydown"
          (fn [e]
            (when-not @state/pres-state
              (if (and (= (.-key e) "g") (or (.-ctrlKey e) (.-metaKey e)))
                (do (.preventDefault e) (omni/toggle!))
              (if (and (= (.-key e) "e") (or (.-ctrlKey e) (.-metaKey e)))
                (do (.preventDefault e) (editor/edit!))
              (when (not (.closest (.-target e) "input, textarea, select"))
                (let [k (.-key e)]
                  ;; When hints are active, route letters to hint system
                  (if (and @state/hint-state (re-matches #"[a-zA-Z]" k))
                    (do (.preventDefault e)
                        (when-not (hints/hint-type-char! k)
                          (hints/dismiss-hints!)))
                  (if (#{"h" "j" "k" "l" "d" "f"} k)
                    (do (.preventDefault e)
                        (when-not (contains? @doc-held k)
                          (swap! doc-held conj k)
                          (ensure-doc-raf!)))
                    (let [dv @state/doc-view
                          z  (or (:zoom dv) 1.0)
                          ts (or (:text-scale dv) 1.0)]
                      (case k
                        ("+" "=")
                        (do (.preventDefault e)
                            (if @state/canvas-zoom-active?
                              (swap! state/canvas-zoom #(min 10.0 (+ % 0.1)))
                              (let [nz (min 20.0 (+ z 0.01)) zr (/ nz z)]
                                (swap! state/doc-view #(assoc % :zoom nz
                                                          :pan-x (* (or (:pan-x %) 0) zr)
                                                          :pan-y (* (or (:pan-y %) 0) zr)))))
                            (doc-apply-view!))
                        "-"
                        (do (.preventDefault e)
                            (if @state/canvas-zoom-active?
                              (swap! state/canvas-zoom #(max 0.25 (- % 0.1)))
                              (let [nz (max 0.25 (- z 0.01)) zr (/ nz z)]
                                (swap! state/doc-view #(assoc % :zoom nz
                                                          :pan-x (* (or (:pan-x %) 0) zr)
                                                          :pan-y (* (or (:pan-y %) 0) zr)))))
                            (doc-apply-view!))
                        "]"
                        (do (.preventDefault e)
                            (let [nts (min 5.0 (+ ts 0.02))]
                              (swap! state/doc-view assoc :text-scale nts)
                              (save-doc-text-scale! nts)
                              (doc-apply-view!)
                              (preflight-debounced! 300)))
                        "["
                        (do (.preventDefault e)
                            (let [nts (max 0.5 (- ts 0.02))]
                              (swap! state/doc-view assoc :text-scale nts)
                              (save-doc-text-scale! nts)
                              (doc-apply-view!)
                              (preflight-debounced! 300)))
                        "t"
                        (do (.preventDefault e)
                            (let [nts (min 5.0 (+ ts 0.02))]
                              (swap! state/doc-view assoc :text-scale nts)
                              (save-doc-text-scale! nts)
                              (doc-apply-view!)
                              (preflight-debounced! 300)))
                        "T"
                        (do (.preventDefault e)
                            (let [nts (max 0.5 (- ts 0.02))]
                              (swap! state/doc-view assoc :text-scale nts)
                              (save-doc-text-scale! nts)
                              (doc-apply-view!)
                              (preflight-debounced! 300)))
                        "a"
                        (do (.preventDefault e)
                            (swap! doc-pan-speed #(max 0 (- % 50)))
                            (ui/show-toast! (str "Speed " @doc-pan-speed)))
                        "s"
                        (do (.preventDefault e)
                            (swap! doc-pan-speed #(min 2000 (+ % 50)))
                            (ui/show-toast! (str "Speed " @doc-pan-speed)))
                        "0"
                        (do (.preventDefault e)
                            (reset! state/canvas-zoom 1.0)
                            (reset! state/canvas-zoom-active? false)
                            (reset! doc-pan-speed 500)
                            (anim/animate-view-to! state/doc-view
                              {:zoom 1.0 :text-scale (or (:text-scale @state/doc-view) 1.0) :pan-x 0 :pan-y 0}
                              doc-apply-view! :phy doc-phy :duration 400))
                        "\\"
                        (do (.preventDefault e)
                            (swap! state/doc-view assoc :text-scale 1.0)
                            (save-doc-text-scale! 1.0)
                            (doc-apply-view!))
                        ";"
                        (do (.preventDefault e)
                            (when-let [{:keys [go! nav-state]} @state/current-nav]
                              (go! (inc @nav-state) nil)))
                        "n"
                        (do (.preventDefault e)
                            (when-let [{:keys [go! nav-state]} @state/current-nav]
                              (go! (dec @nav-state) "going-back")))
                        "r"
                        (do (.preventDefault e)
                            (.reload (.-location js/window)))
                        "i"
                        (do (.preventDefault e)
                            (when-let [tog (:toggle-toc! @state/current-nav)] (tog)))
                        "z"
                        (do (.preventDefault e)
                            (when-let [hl (.querySelector js/document ".doc-highlight-cursor")]
                              (.toggle (.-classList hl) "pres-highlight-cursor--active")))
                        "e"
                        (do (.preventDefault e)
                            (if @state/hint-state
                              (do (hints/dismiss-hints!)
                                  (anim/animate-view-to! state/doc-view
                                    {:zoom 1.0 :text-scale (or (:text-scale @state/doc-view) 1.0) :pan-x 0 :pan-y 0}
                                    doc-apply-view! :phy doc-phy))
                              (let [already-at-default? (and (<= (js/Math.abs (- (or (:zoom @state/doc-view) 1.0) 1.0)) 0.05)
                                                            (<= (js/Math.abs (or (:pan-x @state/doc-view) 0)) 5)
                                                            (<= (js/Math.abs (or (:pan-y @state/doc-view) 0)) 5))
                                    show-fn (fn []
                                              (when-let [spread (.querySelector js/document ".spread.active")]
                                                (hints/show-hints! spread
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
                                                          nz (min 4.0 (max 1.2 (* 0.65 (min (/ vw ew) (/ vh eh)))))]
                                                      (anim/animate-view-to! state/doc-view
                                                        {:zoom nz :text-scale (or (:text-scale @state/doc-view) 1.0)
                                                         :pan-x (- (* sx nz)) :pan-y (- (* sy nz))}
                                                        doc-apply-view! :phy doc-phy))))))]
                                (if already-at-default?
                                  (show-fn)
                                  (anim/animate-view-to! state/doc-view
                                    {:zoom 1.0 :text-scale (or (:text-scale @state/doc-view) 1.0) :pan-x 0 :pan-y 0}
                                    doc-apply-view! :phy doc-phy
                                    :on-done show-fn)))))
                        ("1" "2" "3" "4" "5" "6" "7" "8" "9")
                        (do (.preventDefault e)
                          (let [num (js/parseInt k 10)]
                            (when-let [groups (:toc-groups @state/current-nav)]
                              (when (and (<= 1 num) (<= num (count groups)))
                                (let [section (nth groups (dec num))
                                      page-id (:id (first (:entries section)))]
                                  (when-let [{:keys [go! id->spread]} @state/current-nav]
                                    (when-let [idx (get id->spread page-id)]
                                      (go! idx nil))))))))
                        "y"
                        (do (.preventDefault e)
                            (if @state/canvas-zoom-active?
                              (do (commit-canvas-zoom!)
                                  (reset! state/canvas-zoom-active? false)
                                  (ui/show-toast! "Canvas zoom OFF"))
                              (do (reset! state/canvas-zoom-active? true)
                                  (ui/show-toast! "Canvas zoom ON — f/d to zoom, y to exit" 2000)))
                            (doc-apply-view!))
                        "p"
                        (do (.preventDefault e)
                            (.toggle (.-classList (.-documentElement js/document)) "ui-hidden"))
                        "u"
                        (do (.preventDefault e) (pres/toggle-view!))
                        "o"
                        (do (.preventDefault e) (pres/toggle-fullscreen!))
                        "?"
                        (do (.preventDefault e) (ui/toggle-shortcuts! ui/doc-shortcuts))
                        nil)))))))))))
        (.addEventListener js/document "keyup"
          (fn [e]
            (when-not @state/pres-state
              (let [k (.-key e)]
                (when (#{"h" "j" "k" "l" "d" "f"} k)
                  (swap! doc-held disj k))))))
        ;; Mouse wheel with momentum
        (.addEventListener js/document "wheel"
          (fn [e]
            (when-not @state/pres-state
              (.preventDefault e)
              (let [dx (.-deltaX e)
                    dy (.-deltaY e)
                    reader (.querySelector js/document ".reader")
                    rs (if reader
                         (let [v (js/parseFloat (.getPropertyValue (.-style reader) "--reader-scale"))]
                           (if (js/isNaN v) 1 (max 0.3 v)))
                         1)
                    impulse (* 12 rs (/ @doc-pan-speed 500))]
                (set! (.-wx doc-phy) (- (.-wx doc-phy) (* dx impulse)))
                (set! (.-wy doc-phy) (- (.-wy doc-phy) (* dy impulse)))
                (ensure-doc-raf!))))
          #js {:passive false}))
      (preflight-debounced!)
      ;; Auto-reconnect SpaceMouse if previously granted
      (sm/try-reconnect!)
      ;; Restore presentation mode if it was active before refresh
      (when-let [saved-idx (pres/pres-restore-session)]
        (js/setTimeout #(pres/enter-presentation! saved-idx) 300)))))

(defn- boot-catalog [courses]
  (set! (.-title js/document) "greb-course")
  (let [app (.getElementById js/document "app")]
    (.appendChild app (catalog/build-catalog courses #(reload! @state/current-courses)))
    (when (and js/lucide (.-createIcons js/lucide))
      (.createIcons js/lucide))
    (.addEventListener js/document "keydown"
      (fn [e]
        (when (and (= (.-key e) "g") (or (.-ctrlKey e) (.-metaKey e)))
          (.preventDefault e)
          (omni/toggle!))))
    (js/requestAnimationFrame
      (fn []
        (js/requestAnimationFrame
          (fn []
            (hide-boot-loader!)))))))

(defn- boot [courses]
  (reset! state/current-courses courses)
  (if-let [course (match-course courses)]
    (boot-course course)
    (boot-catalog courses)))

(defn init! [courses]
  (dbg/install!)
  (boot courses))

(defn reload! [courses]
  (when-let [app (.getElementById js/document "app")]
    (set! (.-innerHTML app) "")
    (boot courses)))

;; ── Public API for REPL ─────────────────────────────────────────
(defn get-courses [] @state/current-courses)

(defn update-courses! [courses]
  (reset! state/current-courses courses)
  (reload! courses))

(defn navigate!
  "Navigate to a spread by page id (string) or spread index (number)."
  [target]
  (when-let [{:keys [go! id->spread]} @state/current-nav]
    (let [idx (if (number? target)
                target
                (get id->spread (name target)))]
      (when idx (go! idx nil)))))

(defn current-page-id
  "Return the id of the currently visible page (from URL hash)."
  []
  (nav/current-hash))

;; ── Preflight overflow check ──────────────────────────────────
(defn preflight!
  "Scan all pages for content overflow."
  []
  (let [pages   (array-seq (.querySelectorAll js/document ".page"))
        results (atom [])]
    (doseq [page pages]
      (let [id       (or (.-id page) "?")
            body     (.querySelector page ".page-body")
            overflow? (when body
                        (> (.-scrollHeight body) (+ (.-clientHeight body) 2)))]
        (if overflow?
          (let [delta (- (.-scrollHeight body) (.-clientHeight body))]
            (.add (.-classList page) "preflight-overflow")
            (swap! results conj {:id id :overflow-px delta
                                 :scroll-h (.-scrollHeight body)
                                 :client-h (.-clientHeight body)})
            (js/console.warn
              (str "⚠ OVERFLOW page #" id
                   " — content exceeds by " delta "px"
                   " (scrollH=" (.-scrollHeight body)
                   " clientH=" (.-clientHeight body) ")")))
          (.remove (.-classList page) "preflight-overflow"))))
    (if (seq @results)
      (do (js/console.warn (str "⚠ PREFLIGHT: " (count @results) " page(s) overflowing"))
          (js/console.table (clj->js @results)))
      (js/console.log "✓ PREFLIGHT: All pages fit — no overflow detected"))
    @results))

(defn- preflight-debounced!
  "Debounced preflight — cancels pending, runs after delay."
  ([] (preflight-debounced! 1500))
  ([delay-ms]
   (when-let [t @preflight-timer] (js/clearTimeout t))
   (reset! preflight-timer
     (js/setTimeout (fn [] (reset! preflight-timer nil) (preflight!)) delay-ms))))
