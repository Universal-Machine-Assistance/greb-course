(ns greb-course.templates.cover
  "Cover page template."
  (:require [greb-course.dom :as d]))

(defn render [{:keys [hero-img logo logo-secondary title subtitle]} page-num _theme]
  (d/el :article {:class "page portada-page" :id "portada"}
        (d/el :div {:class "portada-hero"}
              (when hero-img (d/src-img hero-img title "portada-hero-img"))
              (d/el :div {:class "portada-overlay"}))
        (d/el :div {:class "portada-body"}
              (d/el :div {:class "portada-logos"}
                    (when logo (d/src-img logo title "portada-logo"))
                    (when logo-secondary (d/src-img logo-secondary title "portada-logo-secondary")))
              (d/el :h1 {:class "portada-title"} title)
              (d/el :p {:class "portada-sub"} subtitle))
        (d/page-footer page-num)))
