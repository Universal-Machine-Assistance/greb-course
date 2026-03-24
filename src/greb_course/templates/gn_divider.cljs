(ns greb-course.templates.gn-divider
  "Greb-nue section divider — full-image with big section number + title."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]))

(defn render
  "Render a Greb-nue section divider page.
   Data keys:
     :id     — page id
     :img    — background image filename
     :number — section number (string or int)
     :title  — section title (supports *italic* for yellow emphasis)"
  [{:keys [id img number title]} page-num _theme]
  (d/el :article {:class "page gn-page gn-divider-page full-image-page" :id id}
        (d/el :div {:class "gn-divider-bg"}
              (d/src-img img (or title "") "full-image-photo"))
        (d/el :div {:class "gn-divider-content"}
              (d/el :div {:class "gn-divider-number animate d1"} (str number))
              (apply d/el :h1 {:class "gn-divider-title animate d2"}
                     (rich/inline-children (str title))))
        (d/page-footer page-num)))
