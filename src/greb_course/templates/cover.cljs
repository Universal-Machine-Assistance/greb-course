(ns greb-course.templates.cover
  "Cover page template."
  (:require [greb-course.dom :as d]
            [clojure.string :as s]))

(defn- subtitle-bullets [subtitle]
  "Split subtitle by middle dot into bullet items."
  (when (and subtitle (string? subtitle))
    (let [parts (s/split (str subtitle) #"\s*[·]\s*")]
      (when (> (count parts) 1) (mapv s/trim parts)))))

(defn render [{:keys [hero-img logo logo-secondary title subtitle bullets]} page-num _theme]
  (let [bullet-items (or bullets (subtitle-bullets subtitle))]
    (d/el :article {:class "page portada-page" :id "portada"}
          (d/el :div {:class "portada-hero"}
                (when hero-img
                  (d/src-img hero-img title "portada-hero-img portada-hero-img--float"))
                (d/el :div {:class "portada-overlay"}))
          (d/el :div {:class "portada-body"}
                (d/el :div {:class "portada-logos animate d1"}
                      (when logo (d/src-img logo title "portada-logo"))
                      (when logo-secondary (d/src-img logo-secondary title "portada-logo-secondary")))
                (d/el :h1 {:class "portada-title animate d2"} title)
                (if (seq bullet-items)
                  (apply d/el :ul {:class "portada-bullets"}
                         (map-indexed (fn [i item]
                                        (d/el :li {:class (str "portada-bullet animate d" (min 4 (inc i)))}
                                              item))
                                      bullet-items))
                  (d/el :p {:class "portada-sub animate d2"} subtitle)))
          (d/page-footer page-num))))
