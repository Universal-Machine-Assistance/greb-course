(ns greb-course.templates.blocks
  "Blocks template — pages with content blocks but no hero."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]
            [greb-course.templates.components :as comp]))

(defn- render-block-content [{:keys [type items] :as block}]
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

    :wash-carousel
    (comp/wash-carousel items)

    :criteria-table
    (comp/criteria-table (:headers block) items)

    :ref-table
    (comp/ref-table items)

    :registro-sheet
    (comp/registro-sheet {:modes (:modes block)
                          :default-mode (:default-mode block)
                          :meta (:meta block)
                          :meta-hint (:meta-hint block)
                          :branches (:branches block)
                          :default-branch (:default-branch block)
                          :timeframes (:timeframes block)
                          :default-timeframe (:default-timeframe block)
                          :stats (:stats block)
                          :rows items})

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
    (d/el :div {}
          (when (seq items)
            (apply d/el :div {:class "sched-grid"}
                   (mapv comp/sched-row items)))
          (when-let [cal (:calendar block)]
            (comp/cleaning-calendar cal))
          (when-let [gantt (:gantt block)]
            (comp/cleaning-gantt gantt)))

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

    :uniform-checklist
    (comp/uniform-checklist {:zones (:zones block) :rows items})

    :store-map
    (comp/store-map {:locations items})

    :omni-embed
    (comp/omnibar-embed-block block)

    :code-block
    (let [lines (or (:lines block) items)]
      (d/el :div {:class "code-block-wrapper"}
            (when (:caption block)
              (d/el :p {:class "code-block-caption"} (:caption block)))
            (apply d/el :pre {:class "code-block"}
                   (mapv (fn [line]
                           (if (map? line)
                             (d/el :code {:class (str "code-line" (when (:hl line) " code-hl"))}
                                   (str (:text line) "\n"))
                             (d/el :code {:class "code-line"} (str line "\n"))))
                         lines))))

    :text
    (comp/text-block-el (:content block))

    :text-block
    (comp/text-block-el (:content block))

    :pricing-table
    (comp/pricing-table block)

    :steps
    (comp/steps-block items)

    :two-col
    (comp/two-col-block block)

    :callout
    (comp/callout-block block)

    :feature-list
    (comp/feature-list block)

    :quote-table
    (comp/quote-table block)

    :bank-card
    (comp/bank-card block)

    :image-block
    (let [{:keys [src alt caption float]} block]
      (d/el :div {:class (str "image-block" (when float (str " image-block--" (name float))))}
            (d/src-img src (or alt "") "image-block-img")
            (when caption
              (d/el :p {:class "image-block-caption"} caption))))

    :qr-link
    (let [{:keys [url image-src title hint]} block
          qr-src (or image-src
                     (str "https://api.qrserver.com/v1/create-qr-code/?size=220x220&data="
                          (js/encodeURIComponent (or url ""))))]
      (d/el :section {:class "qr-upload-cta animate"}
            (d/el :div {:class "qr-upload-copy"}
                  (d/el :p {:class "qr-upload-title"} (or title "Sube tus archivos aquí"))
                  (d/el :p {:class "qr-upload-hint"} (or hint "Escanea el código QR o abre el enlace para cargar el material.")))
            (d/el :a {:href url :class "qr-upload-link" :target "_blank" :rel "noopener noreferrer"}
                  (d/el :img {:src qr-src :alt "QR para subir archivos" :class "qr-upload-img"})
                  (d/el :span {:class "qr-upload-url"} url))))

    :risk-familias-bolas
    (comp/risk-familias-bolas-grid items {:hide-visuals? (:hide-visuals? block)})

    ;; default: info-grid
    (apply d/el :div {:class "mini-info-grid"}
           (mapv comp/info-card items))))

(defn render-block [block]
  (if (#{:highlight :product-showcase :product-timeline :image-grid :omni-embed :code-block :text :text-block :pricing-table :steps :two-col :callout :feature-list :quote-table :bank-card :wash-carousel :image-block :ref-table :qr-link} (:type block))
    (render-block-content block)
    (d/el :section {:class "hygiene-block"}
          (comp/section-bar (:icon block) (:title block))
          (render-block-content block))))

(defn render [{:keys [id blocks header head-section duo-grid callout bg-img intro]} page-num _theme]
  (d/el :article {:class (str "page hygiene-page" (when bg-img " page--bg-img")) :id id}
        (when bg-img
          (d/el :div {:class "page-bg-img"}
                (d/src-img bg-img "" "page-bg-photo")))
        (d/el :div {:class "page-body hygiene-body"}
              ;; Header first
              (when header
                (let [{:keys [kicker title icon]} header]
                  (d/el :section {:class (or (:class header) "limpieza-header animate")}
                        (d/el :div {:class "limpieza-title-row"}
                              (when icon (d/ic icon "limpieza-hdr-icon"))
                              (when kicker (d/el :span {:class "hero-kicker"} kicker))
                              (d/el :h1 {:class "hygiene-main-title"} title))
                        (when-let [pills (:pills header)]
                          (apply d/el :div {:class "def-pills"}
                                 (mapv (fn [{:keys [icon label text css-class verb]}]
                                         (d/el :div {:class (str "def-pill " (or css-class ""))}
                                               (d/ic icon "def-icon")
                                               (d/el :div {}
                                                     (d/el :strong {} label)
                                                     (when verb
                                                       (d/el :p {:class "def-verb"} verb))
                                                     (d/el :p {:class "def-text"} text))))
                                       pills))))))
              ;; Intro paragraphs with drop cap (after header)
              (when intro
                (if (vector? intro)
                  (let [paras (vec (filter seq intro))]
                    (when (seq paras)
                      (apply d/el :div {:class "intro-prose"}
                             (map-indexed
                              (fn [i s]
                                (apply d/el :p {:class (str "intro-prose-p"
                                                           (when (zero? i) " intro-prose-p--dropcap"))}
                                       (rich/inline-children (str s))))
                              paras))))
                  (when (seq intro)
                    (d/el :div {:class "intro-prose"}
                          (apply d/el :p {:class "intro-prose-p intro-prose-p--dropcap"}
                                 (rich/inline-children (str intro)))))))
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
