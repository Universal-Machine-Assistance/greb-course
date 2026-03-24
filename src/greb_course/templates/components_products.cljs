(ns greb-course.templates.components-products
  "Product-related components: showcase, timeline, expandable card."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]
            [greb-course.templates.components-cards :as cards]))

(defn- feat-item [{:keys [icon label]}]
  (d/el :div {:class "product-showcase-feat"}
        (d/el :div {:class "product-showcase-feat-icon-wrap"}
              (d/ic icon "product-showcase-feat-icon"))
        (apply d/el :span {:class "product-showcase-feat-label"}
               (rich/inline-children (str label)))))

(defn product-showcase [{:keys [img alt features]}]
  (d/el :div {:class "product-showcase"}
        (when img
          (d/el :div {:class "product-showcase-img-wrap"}
                (d/src-img img (or alt "") "product-showcase-img")))
        (apply d/el :div {:class "product-showcase-features"}
               (mapv feat-item features))))

(defn product-timeline [{:keys [img alt features timeline-items disclaimer]}]
  (d/el :div {:class "product-timeline animate"}
        (d/el :div {:class "product-timeline-left"}
              (d/el :div {:class "product-timeline-img-wrap"}
                    (d/src-img img (or alt "") "product-timeline-img"))
              (when features
                (apply d/el :div {:class "product-timeline-feats"}
                       (mapv feat-item features))))
        (d/el :div {:class "product-timeline-right"}
              (apply d/el :div {:class "timeline timeline--compact"}
                     (mapv cards/timeline-entry timeline-items)))
        (when disclaimer
          (apply d/el :p {:class "product-timeline-disclaimer"}
                 (rich/inline-children (str disclaimer))))))

(defn product-card [{:keys [nombre uso tipo icon]}]
  (let [expand (d/el :span {:class "product-expand-hint"} "↕")
        card   (d/el :div {:class (str "product-card animate product-tipo-" tipo)}
                     (d/el :div {:class "product-card-top"}
                           (d/el :span {:class "product-tipo-badge"} tipo)
                           (d/el :div {:class "product-name-row"}
                                 (when icon (d/ic icon "product-card-icon"))
                                 (d/el :span {:class "product-nombre"} nombre))
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
