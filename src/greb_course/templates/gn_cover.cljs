(ns greb-course.templates.gn-cover
  "Greb-nue cover/portada page — logo + blobs + credits card."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]))

(defn render
  "Render a Greb-nue cover page.
   Data keys:
     :logo       — logo image filename
     :credits    — vector of credit lines (strings)
     :isbn       — ISBN string
     :copyright  — copyright line"
  [{:keys [logo credits isbn copyright]} page-num _theme]
  (d/el :article {:class "page gn-page gn-cover-page gn-blobs-bg portada-page" :id "portada"}
        ;; Logo centered
        (d/el :div {:class "gn-cover-logo animate d1"}
              (when logo (d/src-img logo "Greb" nil)))
        ;; Credits card at bottom
        (when (or credits isbn copyright)
          (apply d/el :div {:class "gn-cover-credits animate d2"}
                 (concat
                   (when isbn [(d/el :div {:class "gn-cover-credits-isbn"} (str "ISBN: " isbn))])
                   (when copyright [(d/el :div {:class "gn-cover-credits-copyright"} copyright)])
                   (mapv (fn [line] (d/el :div {:class "gn-cover-credits-line"} line)) (or credits [])))))))

(defn render-backcover
  "Render a Greb-nue contraportada/back cover page.
   Data keys:
     :id         — page id
     :bg-img     — background illustration filename
     :logo       — logo image filename (top left)
     :badge      — badge text (top right)
     :card-color — card background color keyword (:blue, :pink, etc.)
     :card-text  — vector of paragraphs for the floating card"
  [{:keys [id bg-img logo badge card-color card-text]} page-num _theme]
  (d/el :article {:class "page gn-page gn-backcover-page gn-grid-bg" :id (or id "backcover")}
        ;; Background illustration
        (when bg-img
          (d/el :div {:class "gn-backcover-illustration"}
                (d/src-img bg-img "" "gn-backcover-img")))
        ;; Logo top-left
        (when logo
          (d/el :div {:class "gn-backcover-logo animate d1"}
                (d/src-img logo "Greb" nil)))
        ;; Badge top-right
        (when badge
          (d/el :div {:class "gn-backcover-badge animate d1"} badge))
        ;; Floating card
        (when (seq card-text)
          (apply d/el :div {:class (str "gn-backcover-card gn-card--" (or (name card-color) "blue") " animate d2")}
                 (mapv (fn [p] (apply d/el :p {} (rich/inline-children (str p)))) card-text)))
        (d/page-footer page-num)))
