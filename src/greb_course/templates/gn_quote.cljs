(ns greb-course.templates.gn-quote
  "Greb-nue full-image quote page — photo background with quote overlay."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]))

(defn render
  "Render a Greb-nue full-quote page.
   Data keys:
     :id     — page id
     :img    — background image filename
     :quote  — quote text (supports *italic* for yellow emphasis)
     :author — attribution text"
  [{:keys [id img quote author]} page-num _theme]
  (d/el :article {:class "page gn-page gn-quote-page full-image-page" :id id}
        (d/el :div {:class "gn-quote-bg"}
              (d/src-img img (or author "") "full-image-photo"))
        (d/el :div {:class "gn-quote-overlay"}
              (apply d/el :div {:class "gn-quote-text animate d1"}
                     (rich/inline-children (str "\u201C" quote "\u201D")))
              (when author
                (d/el :div {:class "gn-quote-author animate d2"} author)))
        (d/page-footer page-num)))
