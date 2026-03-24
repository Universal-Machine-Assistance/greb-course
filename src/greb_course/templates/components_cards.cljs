(ns greb-course.templates.components-cards
  "Card and list primitives: mission, info, stat, timeline, image, visitor."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]))

(defn mission-card [{:keys [label tone]}]
  (d/el :article {:class (str "mission-card animate tone-" tone)}
        (d/el :div {:class "mission-card-top"}
              (d/ic "badge-check" "mission-card-icon")
              (d/el :span {:class "mission-tag"} "Checklist"))
        (apply d/el :p {:class "mission-copy"}
               (rich/inline-children (str label)))))

(defn info-card [{:keys [title text icon]}]
  (d/el :article {:class "info-card animate"}
        (d/el :div {:class "info-card-header"}
              (when icon
                (d/el :div {:class "info-card-icon-wrap"}
                      (d/ic icon "info-card-icon")))
              (apply d/el :h3 {:class "info-card-title"}
                     (rich/inline-children title)))
        (apply d/el :p {:class "info-card-text"}
               (rich/inline-children text))))

(defn stat-card [{:keys [icon label value]}]
  (d/el :article {:class "stat-card animate"}
        (d/el :div {:class "stat-card-icon-wrap"}
              (d/ic icon "stat-card-icon"))
        (apply d/el :p {:class "stat-card-label"}
               (rich/inline-children (str label)))
        (when value (d/el :p {:class "stat-card-value"} value))))

(defn timeline-entry [{:keys [year title text]}]
  (d/el :div {:class "timeline-entry"}
        (d/el :div {:class "timeline-marker"}
              (d/el :span {:class "timeline-year"} year))
        (d/el :div {:class "timeline-content"}
              (apply d/el :h3 {:class "timeline-title"}
                     (rich/inline-children (str title)))
              (apply d/el :p {:class "timeline-text"}
                     (rich/inline-children (str text))))))

(defn timeline [items]
  (apply d/el :div {:class "timeline"}
         (mapv timeline-entry items)))

(defn mission-chip [label]
  (apply d/el :span {:class "mission-chip animate"}
         (rich/inline-children (str label))))

(defn image-card [{:keys [img kicker title]}]
  (d/el :div {:class "image-card animate"}
        (d/src-img img (or title "") "image-card-photo")
        (d/el :div {:class "image-card-overlay"}
              (when kicker (d/el :p {:class "image-card-kicker"} kicker))
              (d/el :p {:class "image-card-title"} title))))

(defn image-grid [items & [{:keys [featured?]}]]
  (apply d/el :div {:class (str "image-grid" (when featured? " image-grid--featured"))}
         (mapv image-card items)))

(defn visitor-zone [{:keys [zone items]}]
  (d/el :article {:class "visitor-zone animate"}
        (apply d/el :h3 {:class "visitor-zone-title"}
               (rich/inline-children (str zone)))
        (apply d/el :div {:class "chip-row"}
               (mapv mission-chip items))))
