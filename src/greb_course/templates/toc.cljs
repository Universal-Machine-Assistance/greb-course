(ns greb-course.templates.toc
  "Table of contents card grid template."
  (:require [greb-course.dom :as d]))

(defn- toc-card [{:keys [id title items img], icon-name :icon}]
  (let [items-ul (apply d/el :ul {:class "toc-items"}
                        (mapv (fn [{:keys [label ok]}]
                                (d/el :li {:class "toc-item"}
                                      (when ok (d/ic "check-circle" "item-ok"))
                                      label))
                              items))
        title-h3 (d/el :h3 {:class "toc-card-title"} title)]
    (apply d/el :div {:class (str "toc-card animate" (when img " toc-card--img")) :id id}
           (if img
             [(d/el :div {:class "toc-card-media"}
                    (d/el :div {:class "toc-card-img-wrap"}
                          (d/src-img img title "toc-card-img"))
                    (d/el :div {:class "toc-card-copy"}
                          title-h3
                          items-ul))]
             [(d/ic icon-name "toc-icon")
              title-h3
              items-ul]))))

(defn render [{:keys [title subtitle sections article-class]} page-num _theme]
  (d/el :article {:class (str "page" (when article-class (str " " article-class))) :id "contenido"}
        (d/el :div {:class "page-body"}
              (d/el :div {:class "contenido-title-area"}
                    (d/el :p {:class "contenido-eyebrow"} subtitle)
                    (d/el :h1 {:class "contenido-title"} title))
              (apply d/el :div {:class "toc-grid"}
                     (mapv toc-card sections)))
        (d/page-footer page-num)))
