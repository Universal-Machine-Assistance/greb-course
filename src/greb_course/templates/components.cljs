(ns greb-course.templates.components
  "Shared sub-components used across templates."
  (:require [greb-course.dom :as d]))

(defn mission-card [{:keys [label tone]}]
  (d/el :article {:class (str "mission-card animate tone-" tone)}
        (d/el :div {:class "mission-card-top"}
              (d/ic "badge-check" "mission-card-icon")
              (d/el :span {:class "mission-tag"} "Checklist"))
        (d/el :p {:class "mission-copy"} label)))

(defn info-card [{:keys [title text icon]}]
  (d/el :article {:class "info-card animate"}
        (d/el :div {:class "info-card-header"}
              (when icon
                (d/el :div {:class "info-card-icon-wrap"}
                      (d/ic icon "info-card-icon")))
              (d/el :h3 {:class "info-card-title"} title))
        (d/el :p {:class "info-card-text"} text)))

(defn stat-card [{:keys [icon label value]}]
  (d/el :article {:class "stat-card animate"}
        (d/el :div {:class "stat-card-icon-wrap"}
              (d/ic icon "stat-card-icon"))
        (d/el :p {:class "stat-card-label"} label)
        (when value (d/el :p {:class "stat-card-value"} value))))

(defn timeline-entry [{:keys [year title text]}]
  (d/el :div {:class "timeline-entry"}
        (d/el :div {:class "timeline-marker"}
              (d/el :span {:class "timeline-year"} year))
        (d/el :div {:class "timeline-content"}
              (d/el :h3 {:class "timeline-title"} title)
              (d/el :p {:class "timeline-text"} text))))

(defn timeline [items]
  (apply d/el :div {:class "timeline"}
         (mapv timeline-entry items)))

(defn highlight-bar [{:keys [icon title items]}]
  (d/el :div {:class "highlight-bar animate"}
        (d/el :div {:class "highlight-bar-icon-wrap"}
              (d/ic icon "highlight-bar-icon"))
        (d/el :div {:class "highlight-bar-body"}
              (d/el :p {:class "highlight-bar-title"} title)
              (apply d/el :ul {:class "highlight-bar-list"}
                     (mapv (fn [item] (d/el :li {} item)) items)))))

(defn product-showcase [{:keys [img alt features]}]
  (d/el :div {:class "product-showcase"}
        (d/el :div {:class "product-showcase-img-wrap"}
              (d/src-img img (or alt "") "product-showcase-img"))
        (apply d/el :div {:class "product-showcase-features"}
               (mapv (fn [{:keys [icon label]}]
                       (d/el :div {:class "product-showcase-feat"}
                             (d/el :div {:class "product-showcase-feat-icon-wrap"}
                                   (d/ic icon "product-showcase-feat-icon"))
                             (d/el :span {:class "product-showcase-feat-label"} label)))
                     features))))

(defn image-card [{:keys [img kicker title]}]
  (d/el :div {:class "image-card animate"}
        (d/src-img img (or title "") "image-card-photo")
        (d/el :div {:class "image-card-overlay"}
              (when kicker (d/el :p {:class "image-card-kicker"} kicker))
              (d/el :p {:class "image-card-title"} title))))

(defn image-grid [items & [{:keys [featured?]}]]
  (apply d/el :div {:class (str "image-grid" (when featured? " image-grid--featured"))}
         (mapv image-card items)))

(defn product-timeline [{:keys [img alt features timeline-items disclaimer]}]
  (d/el :div {:class "product-timeline animate"}
        (d/el :div {:class "product-timeline-left"}
              (d/el :div {:class "product-timeline-img-wrap"}
                    (d/src-img img (or alt "") "product-timeline-img"))
              (when features
                (apply d/el :div {:class "product-timeline-feats"}
                       (mapv (fn [{:keys [icon label]}]
                               (d/el :div {:class "product-showcase-feat"}
                                     (d/el :div {:class "product-showcase-feat-icon-wrap"}
                                           (d/ic icon "product-showcase-feat-icon"))
                                     (d/el :span {:class "product-showcase-feat-label"} label)))
                             features))))
        (d/el :div {:class "product-timeline-right"}
              (apply d/el :div {:class "timeline timeline--compact"}
                     (mapv timeline-entry timeline-items)))
        (when disclaimer
          (d/el :p {:class "product-timeline-disclaimer"} disclaimer))))

(defn mission-chip [label]
  (d/el :span {:class "mission-chip animate"} label))

(defn wash-step [{:keys [step title text icon]}]
  (d/el :article {:class "wash-step animate"}
        (d/el :div {:class "wash-step-num"}
              (if icon (d/ic icon "wash-step-icon") step))
        (d/el :div {:class "wash-step-body"}
              (d/el :h3 {:class "wash-step-title"} title)
              (d/el :p {:class "wash-step-text"} text))))

(defn section-bar [icon-name title]
  (d/el :div {:class "section-bar mission-bar"}
        (d/ic icon-name "bar-icon")
        (d/el :h2 {} title)))

(defn criteria-row [{:keys [que como criterio]}]
  (d/el :tr {:class "criteria-row"}
        (d/el :td {:class "criteria-que"} que)
        (d/el :td {:class "criteria-como"} como)
        (d/el :td {:class "criteria-criterio"} criterio)))

(defn criteria-table [headers rows]
  (d/el :div {:class "criteria-table-wrap"}
        (d/el :table {:class "criteria-table"}
              (apply d/el :thead {}
                     [(apply d/el :tr {}
                             (mapv #(d/el :th {} %) headers))])
              (apply d/el :tbody {}
                     (mapv criteria-row rows)))))

(defn product-card [{:keys [nombre uso tipo]}]
  (let [expand (d/el :span {:class "product-expand-hint"} "↕")
        card   (d/el :div {:class (str "product-card animate product-tipo-" tipo)}
                     (d/el :div {:class "product-card-top"}
                           (d/el :span {:class "product-tipo-badge"} tipo)
                           (d/el :span {:class "product-nombre"} nombre)
                           expand)
                     (d/el :p {:class "product-uso"} uso))]
    (.addEventListener card "click"
      (fn []
        (let [currently-active (.classList.contains card "active")]
          (when-let [grid (.closest card ".product-grid")]
            (doseq [c (array-seq (.querySelectorAll grid ".product-card"))]
              (.remove (.-classList c) "active")))
          (if currently-active
            (set! (.-textContent expand) "↕")
            (do (.add (.-classList card) "active")
                (set! (.-textContent expand) "✕"))))))
    card))

(defn sched-row [{:keys [freq area text]}]
  (d/el :div {:class "sched-row animate"}
        (d/el :span {:class "sched-freq"} freq)
        (d/el :div {:class "sched-content"}
              (d/el :strong {:class "sched-area"} area)
              (d/el :p {:class "sched-text"} text))))

(defn visitor-zone [{:keys [zone items]}]
  (d/el :article {:class "visitor-zone animate"}
        (d/el :h3 {:class "visitor-zone-title"} zone)
        (apply d/el :div {:class "chip-row"}
               (mapv mission-chip items))))
