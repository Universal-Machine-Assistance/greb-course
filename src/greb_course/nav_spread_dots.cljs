(ns greb-course.nav-spread-dots
  "MacOS Dock-style magnification for reader spread dots.")

;; ── Tuning knobs ───────────────────────────────────────────────
(def ^:private base-px    7)   ;; resting dot diameter
(def ^:private max-scale  3.0) ;; peak scale under cursor
(def ^:private min-scale  1.0) ;; scale far from cursor (at rest)
(def ^:private radius-px  90)  ;; influence radius in pixels

;; ── Gaussian-ish falloff (like macOS Dock) ─────────────────────
(defn- gaussian [dist sigma]
  (js/Math.exp (/ (- (* dist dist)) (* 2 sigma sigma))))

;; ── Apply a scale to every dot ─────────────────────────────────
(defn- apply-scales! [dots scales transition active-idx]
  (doseq [[i dot] (map-indexed vector dots)]
    (let [s   (nth scales i 1)
          active? (= i active-idx)
          ;; Fade color from active: neighbors blend toward translucent white
          dist-from-active (js/Math.abs (- i active-idx))
          color-blend (gaussian dist-from-active 2.5)
          bg  (when-not active?
                (str "rgba(255,255,255," (.toFixed (+ 0.15 (* 0.15 color-blend)) 2) ")"))]
      (set! (.. dot -style -width)      (str base-px "px"))
      (set! (.. dot -style -height)     (str base-px "px"))
      (set! (.. dot -style -transition) transition)
      (set! (.. dot -style -transform)  (str "scale(" (.toFixed s 2) ")"))
      (set! (.. dot -style -background) (or bg "")))))

;; ── Idle: gentle bump on active dot, uniform elsewhere ─────────
(defn idle-scales! [dots active-idx]
  (when (seq dots)
    (let [n      (count dots)
          sigma  3.0
          scales (mapv (fn [i]
                         (let [d (js/Math.abs (- i active-idx))
                               g (gaussian d sigma)]
                           (+ min-scale (* 0.6 g))))
                       (range n))]
      (apply-scales! dots scales "transform .32s cubic-bezier(.34,1.56,.64,1), background .3s" active-idx))))

;; ── Hover: Dock magnification from mouse X ─────────────────────
(defn magnify-move! [dots active-idx mx]
  (when (seq dots)
    (let [sigma  (* radius-px 0.45)
          scales (mapv (fn [i]
                         (let [dot    (nth dots i)
                               rect   (.getBoundingClientRect dot)
                               cx     (+ (.-left rect) (/ (.-width rect) 2))
                               dist   (js/Math.abs (- mx cx))
                               g      (gaussian dist sigma)
                               ;; Blend: magnification from cursor + subtle active bump
                               active-bump (if (= i active-idx) 0.15 0)
                               s      (+ min-scale (* (- max-scale min-scale) g) active-bump)]
                           (min max-scale s)))
                       (range (count dots)))]
      (apply-scales! dots scales "transform .08s ease-out, background .1s" active-idx))))

;; ── Attach listeners ───────────────────────────────────────────
(defn attach-dock! [bar dots state]
  (when (and bar (seq dots) (not (.-grebSpreadDock (.-dataset bar))))
    (set! (.-grebSpreadDock (.-dataset bar)) "1")
    (idle-scales! dots @state)
    (.addEventListener bar "mousemove"
      (fn [e] (magnify-move! dots @state (.-clientX e))))
    (.addEventListener bar "mouseleave"
      (fn [_] (idle-scales! dots @state)))))
