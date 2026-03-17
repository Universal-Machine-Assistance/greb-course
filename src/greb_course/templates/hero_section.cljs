(ns greb-course.templates.hero-section
  "Hero + sections template — pages with a hero area and content blocks below."
  (:require [greb-course.dom :as d]
            [greb-course.templates.components :as comp]))

(defn- render-block [{:keys [type icon title items] :as block}]
  (if (#{:highlight :product-showcase :product-timeline :image-grid} type)
    (case type
      :highlight (comp/highlight-bar block)
      :product-showcase (comp/product-showcase block)
      :product-timeline (comp/product-timeline block)
      :image-grid (comp/image-grid items block))
    (d/el :section {:class "hygiene-block"}
        (comp/section-bar icon title)
        (case type
          :mission-grid
          (apply d/el :div {:class "mission-grid"}
                 (mapv comp/mission-card items))

          :info-grid
          (apply d/el :div {:class "mini-info-grid"}
                 (mapv comp/info-card items))

          :visitor-grid
          (apply d/el :div {:class "visitor-grid"}
                 (mapv comp/visitor-zone items))

          :chip-row
          (apply d/el :div {:class "chip-row"}
                 (mapv comp/mission-chip items))

          :frequency-list
          (apply d/el :ul {:class "frequency-list"}
                 (mapv (fn [item] (d/el :li {} item)) items))

          :wash-grid
          (apply d/el :div {:class "wash-grid"}
                 (mapv comp/wash-step items))

          :criteria-table
          (comp/criteria-table (:headers block) items)

          :product-grid
          (d/el :div {}
                (when (:legend block)
                  (d/el :span {:class "product-legend"}
                        (d/el :span {:class "leg-dot dot-superficie"}) "Superficies "
                        (d/el :span {:class "leg-dot dot-maquinas"}) "Equipos "
                        (d/el :span {:class "leg-dot dot-manos"}) "Manos"))
                (apply d/el :div {:class "product-grid"}
                       (mapv comp/product-card items)))

          :sched-grid
          (apply d/el :div {:class "sched-grid"}
                 (mapv comp/sched-row items))

          :stat-grid
          (apply d/el :div {:class "stat-grid"}
                 (mapv comp/stat-card items))

          :timeline
          (comp/timeline items)

          :product-showcase
          (comp/product-showcase block)

          :highlight
          (comp/highlight-bar block)

          ;; default: info-grid
          (apply d/el :div {:class "mini-info-grid"}
                 (mapv comp/info-card items))))))

(defn render [{:keys [id hero blocks]} page-num _theme]
  (let [{:keys [kicker title subtitle meter-value meter-caption hero-img]} hero]
    (d/el :article {:class "page hygiene-page" :id id}
          (d/el :div {:class "page-body hygiene-body"}
                (d/el :section {:class "hygiene-hero animate"}
                      (d/el :div {:class "hero-copy"}
                            (d/el :p {:class "hero-kicker"} kicker)
                            (d/el :h1 {:class "hygiene-main-title"} title)
                            (d/el :p {:class "hero-sub"} subtitle))
                      (if hero-img
                        (d/el :div {:class "hero-product-img"}
                              (d/src-img hero-img title "hero-product-photo"))
                        (when meter-value
                          (d/el :div {:class "hero-meter"}
                                (d/el :div {:class "hero-ring"}
                                      (d/el :span {:class "hero-ring-value"} meter-value))
                                (d/el :p {:class "hero-meter-caption"} meter-caption)))))
                (apply d/el :div {} (mapv render-block blocks)))
          (d/page-footer page-num))))
