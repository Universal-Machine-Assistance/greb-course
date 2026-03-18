(ns greb-course.templates.risk
  "Risk page template."
  (:require [greb-course.dom :as d]
            [clojure.string :as str]))

(defn- quimico-cause-item [img text]
  (d/el :li {:class "rq-cause-item"}
        (when img (d/src-img img "" "rq-cause-img"))
        (d/el :span {:class "rq-cause-txt"} text)))

(defn- quimico-example [text img]
  (d/el :article {:class "rq-example-card"}
        (d/el :span {:class "rq-example-x"} "✕")
        (d/el :p {:class "rq-example-text"} text)
        (when img (d/src-img img "" "rq-example-img"))))

(defn- quimico-page [{:keys [id icon title sub items]} imgs page-n]
  (let [[hero-img _layer2 arrow-bowl chemicals-bowl bottle-spoon] imgs]
    (d/el :article {:class "page risk-page risk-dark rq-page" :id id}
          (d/el :div {:class "risk-page-hero rq-hero"}
                (d/el :div {:class "rq-hero-copy"}
                      (d/ic icon "risk-hero-icon animate")
                      (d/el :h1 {:class "risk-hero-title animate d1"} title)
                      (d/el :p {:class "rq-hero-sub animate d2"} sub))
                (when hero-img
                  (d/el :div {:class "rq-hero-img-wrap animate d2"}
                        (d/src-img hero-img title "rq-hero-img"))))
          (d/el :div {:class "risk-page-body rq-body"}
                (d/el :div {:class "rq-top-grid"}
                      (d/el :section {:class "rq-block"}
                            (d/el :h3 {:class "rq-block-title"} "CAUSAS")
                            (apply d/el :ul {:class "rq-cause-list"}
                                   (mapv (fn [item]
                                           (d/el :li {:class "rq-cause-item"}
                                                 (when (:icon item) (d/ic (:icon item) "rq-cause-icon"))
                                                 (d/el :span {:class "rq-cause-txt"} (:label item))))
                                         items)))
                      (d/el :section {:class "rq-block"}
                            (d/el :h3 {:class "rq-block-title"} "CONSECUENCIA")
                            (d/el :div {:class "rq-cons-row"}
                                  (when arrow-bowl (d/src-img arrow-bowl "" "rq-cons-img"))
                                  (d/el :div {:class "rq-cons-copy"}
                                        (d/el :p {:class "rq-cons-h"} "Contaminación de alimentos")
                                        (d/el :p {:class "rq-cons-p"} "Productos tóxicos pueden contaminar helados y superficies de contacto con alimentos")))))
                (d/el :h3 {:class "rq-examples-h"} "Ejemplos de riesgo químico")
                (d/el :div {:class "rq-examples-grid"}
                      (quimico-example "Helados junto a desinfectantes" chemicals-bowl)
                      (quimico-example "Envases de químicos sin etiqueta" bottle-spoon)
                      (quimico-example "Usar el mismo recipiente para limpieza" arrow-bowl)))
          (d/page-footer page-n))))

;; ── Enhanced microbiological risk page ─────────────────────────
(defn- danger-zone-bar [{:keys [ranges note]}]
  (d/el :div {:class "dz-wrap"}
        (apply d/el :div {:class "dz-bars"}
               (mapv (fn [{:keys [label temp zone icon]}]
                       (d/el :div {:class (str "dz-row dz-" (name zone))}
                             (d/ic icon "dz-icon")
                             (d/el :span {:class "dz-temp"} temp)
                             (d/el :span {:class "dz-label"} label)))
                     ranges))
        (d/el :p {:class "dz-note"} note)))

(defn- bacteria-table [bacteria]
  (d/el :div {:class "bact-grid"}
        (d/el :div {:class "bact-hdr"}
              (d/el :span {} "Bacteria")
              (d/el :span {} "Fuente")
              (d/el :span {} "Efecto"))
        (apply d/el :div {}
               (mapv (fn [{:keys [name source effect]}]
                       (d/el :div {:class "bact-row"}
                             (d/el :span {:class "bact-name"} name)
                             (d/el :span {:class "bact-src"} source)
                             (d/el :span {:class "bact-eff"} effect)))
                     bacteria))))

(defn- stat-badges [stats]
  (apply d/el :div {:class "risk-stats"}
         (mapv (fn [{:keys [value label]}]
                 (d/el :div {:class "risk-stat"}
                       (d/el :span {:class "risk-stat-val"} value)
                       (d/el :span {:class "risk-stat-lbl"} label)))
               stats)))

(defn- prevention-row [items]
  (apply d/el :div {:class "prev-grid"}
         (mapv (fn [{:keys [icon title text]}]
                 (d/el :div {:class "prev-card"}
                       (d/ic icon "prev-icon")
                       (d/el :strong {:class "prev-title"} title)
                       (d/el :span {:class "prev-text"} text)))
               items)))

(defn- enhanced-micro-page [{:keys [id icon title sub color items
                                     danger-zone bacteria stats prevention]} page-num]
  (d/el :article {:class (str "page risk-page risk-enhanced risk-" color) :id id}
        (d/el :div {:class "risk-page-hero risk-micro-hero"}
              (d/ic icon "risk-hero-icon animate")
              (d/el :h1 {:class "risk-hero-title animate d1"} title)
              (stat-badges stats))
        (d/el :div {:class "risk-page-body risk-micro-body"}
              (d/el :p {:class "risk-description animate d1"} sub)
              (d/el :div {:class "risk-micro-cols"}
                    ;; Left column: danger zone + bacteria
                    (d/el :div {:class "risk-micro-left"}
                          (d/el :h3 {:class "risk-section-h"} (d/ic "thermometer" "rsh-icon") "Zona de peligro térmico")
                          (danger-zone-bar danger-zone)
                          (d/el :h3 {:class "risk-section-h"} (d/ic "microscope" "rsh-icon") "Bacterias comunes")
                          (bacteria-table bacteria))
                    ;; Right column: causes + prevention
                    (d/el :div {:class "risk-micro-right"}
                          (d/el :h3 {:class "risk-section-h"} (d/ic "alert-triangle" "rsh-icon") "Causas")
                          (apply d/el :div {:class "risk-causes-compact"}
                                 (mapv (fn [c]
                                         (d/el :div {:class "cause-compact"}
                                               (d/ic (:icon c) "cause-icon")
                                               (d/el :span {:class "cause-label"} (:label c))))
                                       items))
                          (d/el :h3 {:class "risk-section-h"} (d/ic "shield-check" "rsh-icon") "Prevención")
                          (prevention-row prevention))))
        (d/page-footer page-num)))

(defn render [{:keys [id icon title sub items color images-for-sections
                       enhanced? danger-zone bacteria stats prevention] :as data} page-num _theme]
  (let [img-key (keyword (str/replace id "riesgo-" ""))
        imgs    (get images-for-sections img-key)]
    (cond
      enhanced?
      (enhanced-micro-page data page-num)

      (= id "riesgo-quimico")
      (quimico-page {:id id :icon icon :title title :sub sub :items items} imgs page-num)

      :else
      (d/el :article {:class (str "page risk-page risk-" color) :id id}
            (d/el :div {:class "risk-page-hero"}
                  (d/ic icon "risk-hero-icon animate")
                  (d/el :h1 {:class "risk-hero-title animate d1"} title)
                  (when (seq imgs)
                    (d/el :div {:class "risk-hero-img-wrap"}
                          (d/src-img (first imgs) title "risk-hero-img"))))
            (d/el :div {:class "risk-page-body"}
                  (d/el :p {:class "risk-description animate d1"} sub)
                  (apply d/el :div {:class "risk-causes-grid"}
                         (map-indexed (fn [i cause]
                                        (d/el :div {:class (str "risk-cause animate d" (inc (mod i 4)))}
                                              (when (:icon cause) (d/ic (:icon cause) "cause-icon"))
                                              (d/el :span {:class "cause-label"} (:label cause))))
                                      items)))
            (d/page-footer page-num)))))
