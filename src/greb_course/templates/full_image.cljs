(ns greb-course.templates.full-image
  "Full-page background image template with optional overlay text."
  (:require [greb-course.dom :as d]))

(defn render [{:keys [id img alt kicker title subtitle bullets caption screenshot?]} page-num _theme]
  (d/el :article {:class (str "page full-image-page"
                               (when screenshot? " full-image-page--screenshot"))
                  :id id}
        (d/el :div {:class "full-image-bg"}
              (d/src-img img (or alt title "") "full-image-photo"))
        (when (or kicker title subtitle bullets)
          (d/el :div {:class "full-image-overlay"}
                (when kicker (d/el :p {:class "full-image-kicker"} kicker))
                (when title (d/el :h1 {:class "full-image-title"} title))
                (when subtitle (d/el :p {:class "full-image-subtitle"} subtitle))
                (when (seq bullets)
                  (apply d/el :ul {:class "full-image-bullets"}
                         (map (fn [b] (d/el :li {} b)) bullets)))))
        (when caption
          (d/el :p {:class "full-image-caption"} caption))
        (d/page-footer page-num)))
