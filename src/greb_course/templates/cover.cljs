(ns greb-course.templates.cover
  "Cover page template."
  (:require [greb-course.dom :as d]
            [clojure.string :as s]))

(defn- subtitle-bullets
  "Split subtitle by middle dot into bullet items."
  [subtitle]
  (when (and subtitle (string? subtitle))
    (let [parts (s/split (str subtitle) #"\s*[·]\s*")]
      (when (> (count parts) 1) (mapv s/trim parts)))))

(defn render [{:keys [hero-img title subtitle bullets]} page-num _theme]
  (let [bullet-items (or bullets (subtitle-bullets subtitle))]
    (d/el :article {:class "page portada-page" :id "portada"}
          (d/el :div {:class "portada-hero"}
                (when hero-img
                  (d/src-img hero-img title "portada-hero-img"))
                (d/el :div {:class "portada-overlay"}))
          (d/el :div {:class "portada-body"}
                (d/el :div {:class "portada-copy animate d2"}
                      (d/el :h1 {:class "portada-title"} title)
                      (if (seq bullet-items)
                        (apply d/el :ul {:class "portada-bullets"}
                               (map-indexed (fn [i item]
                                              (d/el :li {:class (str "portada-bullet animate d" (min 4 (inc i)))}
                                                    item))
                                            bullet-items))
                        (d/el :p {:class "portada-sub"} subtitle))))
          (d/page-footer page-num))))
