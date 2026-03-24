(ns greb-course.templates.legal
  "Legal information page template."
  (:require [greb-course.dom :as d]))

(defn render
  [{:keys [id title subtitle owner rights edition-date location version update-note legal-lines]}
   page-num
   _theme]
  (d/el :article {:class "page legal-page" :id (or id "legal")}
        (d/el :div {:class "page-body legal-body"}
              (d/el :h1 {:class "legal-title"} (or title "Información Legal"))
              (when subtitle
                (d/el :p {:class "legal-subtitle"} subtitle))
              (d/el :div {:class "legal-meta"}
                    (when owner
                      (d/el :p {:class "legal-meta-line"}
                            (d/el :strong {} "Titular: ")
                            owner))
                    (when rights
                      (d/el :p {:class "legal-meta-line"}
                            (d/el :strong {} "Derechos: ")
                            rights))
                    (when edition-date
                      (d/el :p {:class "legal-meta-line"}
                            (d/el :strong {} "Edición: ")
                            edition-date))
                    (when location
                      (d/el :p {:class "legal-meta-line"}
                            (d/el :strong {} "Lugar: ")
                            location))
                    (when version
                      (d/el :p {:class "legal-meta-line legal-version"}
                            (d/el :strong {} "Versión de la guía: ")
                            version))
                    (when update-note
                      (d/el :p {:class "legal-note"} update-note)))
              (when (seq legal-lines)
                (apply d/el :ul {:class "legal-list"}
                       (mapv (fn [txt] (d/el :li {} txt)) legal-lines))))
        (d/page-footer page-num)))
