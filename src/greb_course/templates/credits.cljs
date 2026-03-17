(ns greb-course.templates.credits
  "Credits page template."
  (:require [greb-course.dom :as d]))

(defn render [{:keys [title by logos orgs legal]} page-num _theme]
  (d/el :article {:class "page credits-page" :id "creditos"}
        (d/el :div {:class "page-body credits-body"}
              (d/el :h1 {:class "credits-title"} title)
              (d/el :p {:class "credits-by"} by)
              (when (seq logos)
                (apply d/el :div {:class "credits-logos"}
                       (mapv (fn [{:keys [src alt dark?]}]
                               (d/el :span {:class (str "credits-logo-wrap"
                                                        (when dark? " credits-logo-wrap--dark"))}
                                     (d/src-img src alt "credits-logo")))
                             logos)))
              (d/el :p {:class "credits-orgs"} orgs)
              (d/el :div {:class "credits-legal-wrap"}
                    (d/el :p {:class "credits-legal"} legal)))
        (d/page-footer page-num)))
