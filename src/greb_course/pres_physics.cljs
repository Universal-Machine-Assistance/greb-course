(ns greb-course.pres-physics
  "Presentation physics engine: spring/inertia pan, wheel momentum, touch handling."
  (:require [greb-course.state :as state]))

;; ── Constants ──────────────────────────────────────────────────
(def ^:private zoom-speed     0.4)
(def ^:private release        0.28)   ;; 280ms glide half-life
(def ^:private zoom-release   0.24)
(def ^:private sp-stiff       350)    ;; spring stiffness
(def ^:private sp-damp        14)     ;; damping
(def ^:private sp-impulse     0.055)  ;; impulse on release
(def ^:private zsp-stiff      500)
(def ^:private zsp-damp       20)
(def ^:private zsp-impulse    0.04)
(def ^:private wheel-half     0.45)
(def ^:private snap-v         0.5)
(def ^:private snap-z         0.001)
(def ^:private snap-w         0.5)

(defn make-physics
  "Create physics state and return a map of {:phy, :held-keys, :pan-speed, :pan-raf,
   :ensure-raf!, :on-touch-start, :on-touch-move, :on-touch-end, :on-wheel}.

   `apply-view!` — 0-arg fn to sync CSS vars after state change.
   `zoom-label`  — DOM element whose textContent shows zoom %.
   `show-page!`  — (fn [idx direction]) to navigate slides."
  [apply-view! zoom-label show-page!]
  (let [held-keys      (atom #{})
        ^js phy        #js {:vx 0 :vy 0 :zv 0 :wx 0 :wy 0 :last 0
                            :kx 0 :ky 0 :kz 0
                            :spx 0 :spy 0 :svx 0 :svy 0
                            :spz 0 :szv 0}
        pan-speed      (atom 350)
        pan-raf        (atom nil)
        pan-tick       (fn pan-tick [now]
                         (let [prev  (.-last phy)
                               raw   (if (pos? prev) (- now prev) 16.67)
                               dt    (/ (min raw 50) 1000.0)
                               _     (set! (.-last phy) now)
                               spd   @pan-speed
                               keys  @held-keys
                               tx    (* (+ (if (contains? keys "h") 1 0)
                                           (if (contains? keys "l") -1 0)) spd)
                               ty    (* (+ (if (contains? keys "k") 1 0)
                                           (if (contains? keys "j") -1 0)) spd)
                               tz    (* (+ (if (contains? keys "f") 1 0)
                                           (if (contains? keys "d") -1 0)) zoom-speed)
                               decay  (js/Math.exp (/ (- dt) release))
                               zdecay (js/Math.exp (/ (- dt) zoom-release))
                               old-vx (.-vx phy)
                               old-vy (.-vy phy)
                               vx     (if (not= tx 0) tx (* old-vx decay))
                               vy     (if (not= ty 0) ty (* old-vy decay))
                               zv     (if (not= tz 0) tz (* (.-zv phy) zdecay))
                               vx     (if (< (js/Math.abs vx) snap-v) 0 vx)
                               vy     (if (< (js/Math.abs vy) snap-v) 0 vy)
                               zv     (if (< (js/Math.abs zv) snap-z) 0 zv)
                               ;; Detect key release → fire spring impulse
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
                               n-spx  (if (and (< (js/Math.abs n-spx) 0.1) (< (js/Math.abs n-svx) 0.5)) 0 n-spx)
                               n-spy  (if (and (< (js/Math.abs n-spy) 0.1) (< (js/Math.abs n-svy) 0.5)) 0 n-spy)
                               n-svx  (if (== n-spx 0) 0 n-svx)
                               n-svy  (if (== n-spy 0) 0 n-svy)
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
                               ;; Wheel momentum
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
                               ;; Total displacement
                               dx     (+ (* vx dt) (- n-spx (.-spx phy)) (* wx dt) sm-dx)
                               dy     (+ (* vy dt) (- n-spy (.-spy phy)) (* wy dt) sm-dy)
                               dz     (+ (* zv dt) (- n-spz (.-spz phy)) sm-dz)
                               alive  (or (not= vx 0) (not= vy 0) (not= zv 0)
                                          (not= wx 0) (not= wy 0)
                                          (not= n-spx 0) (not= n-spy 0)
                                          (not= n-spz 0)
                                          (not= smx 0) (not= smy 0) (not= smz 0))]
                           ;; Write back mutable state
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
                           (let [moved (or (not= dx 0) (not= dy 0) (not= dz 0))]
                             (when moved
                               (if (and @state/canvas-zoom-active? (not= dz 0))
                                 ;; Canvas zoom mode
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
                               (apply-view!))
                             ;; Keep looping while anything is moving
                             (if (or (seq keys) alive)
                               (reset! pan-raf (js/requestAnimationFrame pan-tick))
                               (do (set! (.-last phy) 0)
                                   (reset! pan-raf nil))))))
        ensure-raf!    (fn [] (when-not @pan-raf
                                (set! (.-last phy) 0)
                                (reset! pan-raf (js/requestAnimationFrame pan-tick))))
        ;; ── Touch pan ──
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
                           (when (not (:moved p))
                             (let [raw-target (.-target e)
                                   target (if (= (.-nodeType raw-target) 1)
                                            raw-target
                                            (.-parentElement raw-target))
                                   on-ui? (when target
                                            (.closest target (str ".pres-toolbar, .pres-toolbar-btn, .pres-index, .pres-index-scrim,"
                                                                   ".toc-wrapper, .toc-overlay, .pres-section-bar, .pres-indicator,"
                                                                   "button, a,"
                                                                   ".store-map-card, .store-map-list, .store-map-container,"
                                                                   ".registro-check-btn, .registro-mode-btn, .registro-branch-btn, .registro-time-btn,"
                                                                   ".product-card, .clean-cal-date-btn, .clean-cal-mode-btn")))]
                               (when-not on-ui?
                                 (let [mid (/ (.-innerWidth js/window) 2)
                                       x   (:x0 p)]
                                   (if (< x mid)
                                     (show-page! (dec (or (:current @state/pres-state) 0)) :back)
                                     (show-page! (inc (or (:current @state/pres-state) 0)) nil)))))))
                         (reset! touch-pan nil))
        on-wheel    (fn [e]
                      (.preventDefault e)
                      (let [dx (.-deltaX e)
                            dy (.-deltaY e)
                            impulse (* 4 (/ @pan-speed 350))]
                        (set! (.-wx phy) (- (.-wx phy) (* dx impulse)))
                        (set! (.-wy phy) (- (.-wy phy) (* dy impulse)))
                        (ensure-raf!)))]
    {:phy            phy
     :held-keys      held-keys
     :pan-speed      pan-speed
     :pan-raf        pan-raf
     :ensure-raf!    ensure-raf!
     :on-touch-start on-touch-start
     :on-touch-move  on-touch-move
     :on-touch-end   on-touch-end
     :on-wheel       on-wheel}))
