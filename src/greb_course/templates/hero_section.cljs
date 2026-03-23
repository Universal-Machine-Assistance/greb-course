(ns greb-course.templates.hero-section
  "Hero + sections template — pages with a hero area and content blocks below."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]
            [greb-course.templates.blocks :as blocks]
            [greb-course.templates.components :as comp]))

(defn render [{:keys [id hero blocks]} page-num _theme]
  (let [{:keys [kicker title subtitle intro meter-value meter-caption hero-img]} hero
        intro-el (when intro
                   (if (vector? intro)
                     (when-let [paras (seq (filter #(and (string? %) (seq %)) intro))]
                       (apply d/el :div {:class "hero-intro-stack rich-surface"}
                              (mapv #(rich/rich-p % "hero-intro-p") paras)))
                     (when (and (string? intro) (seq intro))
                       (rich/rich-p intro "hero-intro-p"))))
        hero-media (if hero-img
                     (d/el :div {:class "hero-product-img"}
                           (d/src-img hero-img title "hero-product-photo"))
                     (when meter-value
                       (d/el :div {:class "hero-meter"}
                             (d/el :div {:class "hero-ring"}
                                   (d/el :span {:class "hero-ring-value"} meter-value))
                             (d/el :p {:class "hero-meter-caption"} meter-caption))))]
    (d/el :article {:class "page hygiene-page" :id id}
          (d/el :div {:class "page-body hygiene-body"}
                (d/el :section {:class "hygiene-hero animate"}
                      (d/el :div {:class "hero-copy"}
                            (d/el :p {:class "hero-kicker"} kicker)
                            (d/el :h1 {:class "hygiene-main-title"} title)
                            (d/el :p {:class "hero-sub"} subtitle)
                            intro-el)
                      hero-media)
                ;; Delegate all block rendering to the blocks module
                (apply d/el :div {} (mapv blocks/render-block blocks)))
          (d/page-footer page-num))))
