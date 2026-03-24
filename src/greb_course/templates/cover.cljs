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

(defn- greb-logo?
  "True when logo data looks like GREB branding."
  [{:keys [src alt]}]
  (let [text (s/lower-case (str src " " alt))]
    (boolean (re-find #"greb" text))))

(defn render [{:keys [hero-img title subtitle bullets logos]} _page-num _theme]
  (let [bullet-items (or bullets (subtitle-bullets subtitle))
        greb-logo (some #(when (greb-logo? %) %) logos)
        cover-logos (cond
                      greb-logo [greb-logo]
                      (seq logos) [(first logos)]
                      :else nil)]
    (d/el :article {:class "page portada-page" :id "portada"}
          (d/el :div {:class "portada-hero"}
                (when hero-img
                  (d/src-img hero-img title "portada-hero-img"))
                (d/el :div {:class "portada-overlay"}))
          (d/el :div {:class "portada-body"}
                (when (seq cover-logos)
                  (apply d/el :div {:class "portada-logos animate d1"}
                         (mapv (fn [{:keys [src alt]}]
                                 (d/src-img src (or alt "") "portada-logo"))
                               cover-logos)))
                (d/el :div {:class "portada-copy animate d2"}
                      (d/el :h1 {:class "portada-title"} title)
                      (if (seq bullet-items)
                        (apply d/el :ul {:class "portada-bullets"}
                               (map-indexed (fn [i item]
                                              (d/el :li {:class (str "portada-bullet animate d" (min 4 (inc i)))}
                                                    item))
                                            bullet-items))
                        (d/el :p {:class "portada-sub"} subtitle)))))))
