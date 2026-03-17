(ns greb-course.templates.page-index
  "Index page template."
  (:require [greb-course.dom :as d]))

(defn- idx-row [id label pg icon]
  (d/el :li {}
        (d/el :a {:href (str "#" id) :class "index-entry"}
              (d/ic icon "entry-icon")
              (d/el :span {:class "entry-label"} label)
              (d/el :span {:class "entry-dots"})
              (when pg (d/el :span {:class "entry-page"} (str pg))))))

(defn- toc-subrow [{:keys [label ok]}]
  (d/el :li {:class "toc-subitem"}
        (if ok (d/ic "check-circle" "toc-sub-ok")
            (d/el :span {:class "toc-sub-dash"} "—"))
        (d/el :span {:class "toc-sub-label"} label)))

(defn- toc-section [{:keys [id icon title items]} pages-map]
  (let [pg (get pages-map id)]
    (d/el :li {:class "toc-section-group"}
          (d/el :a {:href (str "#" (or (when pg id) "contenido")) :class "toc-section-link"}
                (d/ic icon "toc-sec-icon")
                (d/el :span {:class "toc-sec-label"} title)
                (d/el :span {:class "entry-dots"})
                (when pg (d/el :span {:class "entry-page"} (str pg))))
          (apply d/el :ul {:class "toc-subitems"}
                 (mapv toc-subrow items)))))

(defn render [{:keys [title entries sections groups]} page-num _theme]
  (let [pm (into {} (map (juxt :id :page) entries))]
    (d/el :article {:class "page" :id "indice"}
          (d/el :div {:class "page-body"}
                (d/el :h1 {} title)
                (apply d/el :div {}
                       (mapv (fn [{:keys [label items type icon-default]}]
                               (d/el :div {}
                                     (d/el :p {:class "toc-group-hdr"} label)
                                     (if (= type :sections)
                                       (apply d/el :ul {:class "index-toc toc-sections"}
                                              (mapv #(toc-section % pm) items))
                                       (apply d/el :ul {:class "index-toc"}
                                              (mapv #(idx-row (:id %) (:label %) (:page %) (or (:icon %) icon-default "file-text"))
                                                    items)))))
                             groups)))
          (d/page-footer page-num))))
