(ns greb-course.animation
  "Smooth view animations and spread entrance effects.")

(defn animate-view-to!
  "Smoothly animate view-atom from its current value to target over duration-ms.
   Only interpolates :zoom :text-scale :pan-x :pan-y — preserves all other keys.
   apply-fn! is called each frame. phy is optional physics object to zero out."
  [view-atom target apply-fn! & {:keys [phy duration on-done]}]
  (let [dur    (or duration 400)
        start  (js/performance.now)
        from   @view-atom
        ease   (fn [t] ;; ease-out cubic
                (let [t1 (- 1 t)] (- 1 (* t1 t1 t1))))]
    (letfn [(tick [now]
              (let [t (min 1.0 (/ (- now start) dur))
                    et (ease t)
                    lerp (fn [a b] (+ a (* (- b a) et)))]
                (swap! view-atom assoc
                  :zoom       (lerp (or (:zoom from) 1.0) (or (:zoom target) 1.0))
                  :text-scale (lerp (or (:text-scale from) 1.0) (or (:text-scale target) 1.0))
                  :pan-x      (lerp (or (:pan-x from) 0) (or (:pan-x target) 0))
                  :pan-y      (lerp (or (:pan-y from) 0) (or (:pan-y target) 0)))
                (apply-fn!)
                (if (< t 1.0)
                  (js/requestAnimationFrame tick)
                  (do
                    (when phy
                      (set! (.-vx phy) 0) (set! (.-vy phy) 0)
                      (set! (.-zv phy) 0) (set! (.-wx phy) 0) (set! (.-wy phy) 0)
                      (set! (.-spx phy) 0) (set! (.-spy phy) 0) (set! (.-spz phy) 0)
                      (set! (.-svx phy) 0) (set! (.-svy phy) 0) (set! (.-szv phy) 0))
                    (when on-done (on-done))))))]
      (js/requestAnimationFrame tick))))

(defn animate-spread! [spread-el]
  (js/setTimeout
    (fn []
      (doseq [node (array-seq (.querySelectorAll spread-el ".animate"))]
        (.add (.-classList node) "visible")))
    60))
