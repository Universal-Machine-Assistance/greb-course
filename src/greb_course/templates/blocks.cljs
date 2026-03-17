(ns greb-course.templates.blocks
  "Blocks template — pages with content blocks but no hero."
  (:require [greb-course.dom :as d]
            [greb-course.templates.components :as comp]))

(defn- render-block-content [{:keys [type icon title items] :as block}]
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

    :product-timeline
    (comp/product-timeline block)

    :image-grid
    (comp/image-grid items block)

    :highlight
    (comp/highlight-bar block)

    ;; default: info-grid
    (apply d/el :div {:class "mini-info-grid"}
           (mapv comp/info-card items))))

(defn- render-block [block]
  (if (#{:highlight :product-showcase :product-timeline :image-grid} (:type block))
    (render-block-content block)
    (d/el :section {:class "hygiene-block"}
          (comp/section-bar (:icon block) (:title block))
          (render-block-content block))))

(defn render [{:keys [id blocks header head-section duo-grid callout bg-img]} page-num _theme]
  (d/el :article {:class (str "page hygiene-page" (when bg-img " page--bg-img")) :id id}
        (when bg-img
          (d/el :div {:class "page-bg-img"}
                (d/src-img bg-img "" "page-bg-photo")))
        (d/el :div {:class "page-body hygiene-body"}
              ;; Limpieza-style header with optional pills
              (when header
                (let [{:keys [kicker title icon]} header]
                  (d/el :section {:class (or (:class header) "limpieza-header animate")}
                        (d/el :div {:class "limpieza-title-row"}
                              (when icon (d/ic icon "limpieza-hdr-icon"))
                              (when kicker (d/el :span {:class "hero-kicker"} kicker))
                              (d/el :h1 {:class "hygiene-main-title"} title))
                        (when-let [pills (:pills header)]
                          (apply d/el :div {:class "def-pills"}
                                 (mapv (fn [{:keys [icon label text css-class]}]
                                         (d/el :div {:class (str "def-pill " (or css-class ""))}
                                               (d/ic icon "def-icon")
                                               (d/el :div {}
                                                     (d/el :strong {} label)
                                                     (d/el :p {:class "def-text"} text))))
                                       pills))))))
              ;; Handwash-style head section (kicker + title, no hero meter)
              (when head-section
                (d/el :section {:class "handwash-head animate"}
                      (d/el :p {:class "hero-kicker"} (:kicker head-section))
                      (d/el :h1 {:class "hygiene-main-title"} (:title head-section))))
              ;; Duo-grid layout (two panels side by side)
              (when duo-grid
                (apply d/el :section {:class "duo-grid"}
                       (mapv (fn [panel]
                               (d/el :div {:class "duo-panel"}
                                     (comp/section-bar (:icon panel) (:title panel))
                                     (render-block-content panel)))
                             duo-grid)))
              ;; Regular blocks
              (apply d/el :div {}
                     (mapv render-block blocks))
              ;; Callout at bottom
              (when callout
                (d/el :div {:class "limpieza-prohibido animate"}
                      (d/ic "ban" "prohibido-icon")
                      (d/el :p {:class "prohibido-text"} callout))))
        (d/page-footer page-num)))
