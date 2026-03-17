(ns greb-course.templates.intro
  "Intro page template."
  (:require [greb-course.dom :as d]))

(defn render [{:keys [eyebrow title stat-num stat-label
                      images lead para2
                      risk-families-title risk-families
                      closing]} page-num _theme]
  (d/el :article {:class "page intro-page" :id "introduccion"}
        (d/el :div {:class "intro-header animate"}
              (d/el :div {}
                    (d/el :p {:class "intro-eyebrow"} eyebrow)
                    (d/el :h1 {:class "intro-header-title"} title))
              (d/el :div {:class "intro-header-stat"}
                    (d/el :span {:class "stat-num"} stat-num)
                    (d/el :span {:class "stat-label"} stat-label)))
        (d/el :div {:class "page-body"}
              (d/el :div {:class "intro-layout"}
                    (apply d/el :div {:class "intro-img-stack animate d1"}
                           (mapv #(d/src-img % "" nil) images))
                    (d/el :div {:class "intro-text-col animate d2"}
                          (d/el :p {:class "intro-lead"} lead)
                          (d/el :p {:class "intro-para2"} para2)))
              (d/el :div {:class "risk-families-callout animate d3"}
                    (d/ic "shield-alert" "callout-icon")
                    (d/el :p {:class "callout-text"} risk-families-title)
                    (apply d/el :div {:class "risk-family-tags"}
                           (mapv (fn [{:keys [icon title color]}]
                                   (d/el :span {:class (str "risk-tag risk-tag-" color)}
                                         (d/ic icon "risk-tag-icon") title))
                                 risk-families)))
              (d/el :div {:class "intro-closing animate d4"}
                    (d/ic "quote" "intro-closing-icon")
                    (d/el :p {:class "intro-closing-text"} closing)))
        (d/page-footer page-num)))
