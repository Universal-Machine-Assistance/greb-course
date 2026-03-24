(ns greb-course.templates.gn-toc
  "Greb-nue table of contents — monospace entries with colored tags."
  (:require [greb-course.dom :as d]))

(defn render
  "Render a Greb-nue TOC page.
   Data keys:
     :sections — [{:number 1 :title \"...\" :entries [{:tag \"/intro\" :label \"...\" :page 9 :color :blue}]}]
     :footer-text — optional footer text"
  [{:keys [sections footer-text]} page-num _theme]
  (d/el :article {:class "page gn-page gn-grid-bg" :id "contenido"}
        (d/el :div {:class "page-body"}
              (apply d/el :div {:class "gn-toc animate d1"}
                     (concat
                       [(d/el :div {:class "gn-toc-title"} "CONTENIDO")]
                       (mapv
                         (fn [{:keys [number title entries]}]
                           (apply d/el :div {:class "gn-toc-section"}
                                  (concat
                                    [(d/el :div {:class "gn-toc-section-title"}
                                           (str number ": " title))]
                                    (mapv
                                      (fn [{:keys [tag label page color]}]
                                        (d/el :div {:class "gn-toc-entry"}
                                              (d/el :span {:class (str "gn-toc-entry-tag gn-tag--" (name (or color :blue)))} tag)
                                              (d/el :span {:class "gn-toc-entry-label"} (str "– " label))
                                              (d/el :span {:class "gn-toc-entry-dots"})
                                              (d/el :span {:class "gn-toc-entry-page"} (str page))))
                                      entries))))
                         sections)
                       (when footer-text
                         [(d/el :div {:class "gn-toc-footer"}
                                (d/src-img "greb-g.png" "Greb" nil)
                                (d/el :span {} footer-text))]))))
        (d/page-footer page-num)))
