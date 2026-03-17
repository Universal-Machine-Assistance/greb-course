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

(defn render [{:keys [id icon title sub items color images-for-sections]} page-num _theme]
  (let [img-key (keyword (str/replace id "riesgo-" ""))
        imgs    (get images-for-sections img-key)]
    (if (= id "riesgo-quimico")
      (quimico-page {:id id :icon icon :title title :sub sub :items items} imgs page-num)
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
