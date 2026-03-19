(ns greb-course.theme
  "Theme CSS custom property application.")

(defn hex->rgba [hex alpha]
  (let [h (if (= (first hex) "#") (subs hex 1) hex)
        r (js/parseInt (subs h 0 2) 16)
        g (js/parseInt (subs h 2 4) 16)
        b (js/parseInt (subs h 4 6) 16)]
    (str "rgba(" r "," g "," b "," alpha ")")))

(defn apply-theme! [theme]
  (let [root-style (.-style (.-documentElement js/document))
        colors     (:colors theme)]
    (when-let [primary (:primary colors)]
      (.setProperty root-style "--brand-primary" primary)
      (.setProperty root-style "--brand-primary-bg" (hex->rgba primary 0.12)))
    (when-let [secondary (:secondary colors)]
      (.setProperty root-style "--brand-secondary" secondary)
      (.setProperty root-style "--brand-secondary-bg" (hex->rgba secondary 0.14)))
    (when-let [accent (:accent colors)]
      (.setProperty root-style "--brand-accent" accent)
      (.setProperty root-style "--brand-accent-bg" (hex->rgba accent 0.14)))
    (when-let [ink (:ink colors)]
      (.setProperty root-style "--ink" ink))
    (when-let [paper (:paper colors)]
      (.setProperty root-style "--paper" paper))
    (when-let [page (:page colors)]
      (.setProperty root-style "--page" page))))
