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

(defn- quimico-page [{:keys [id icon title sub items hero-bola]} imgs page-n]
  (let [[hero-img _layer2 arrow-bowl chemicals-bowl bottle-spoon] imgs
        hero-circle (or hero-bola hero-img)]
    (d/el :article {:class "page risk-page risk-dark rq-page" :id id}
          (d/el :div {:class "risk-page-hero rq-hero"}
                (d/el :div {:class "rq-hero-copy"}
                      (d/ic icon "risk-hero-icon animate")
                      (d/el :h1 {:class "risk-hero-title animate d1"} title)
                      (d/el :p {:class "rq-hero-sub animate d2"} sub))
                (when hero-circle
                  (d/el :div {:class "rq-hero-img-wrap rq-hero-bola-wrap animate d2"}
                        (d/src-img hero-circle title "rq-hero-img"))))
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

;; ── Shared enhanced components ───────────────────────────────
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
  (let [default-icons ["users" "alert-triangle" "clock-3"]]
    (apply d/el :div {:class "risk-stats"}
           (map-indexed
            (fn [idx {:keys [value label icon]}]
              (d/el :div {:class "risk-stat"}
                    (d/el :div {:class "risk-stat-top"}
                          (d/ic (or icon (nth default-icons (mod idx (count default-icons)))) "risk-stat-icon")
                          (d/el :span {:class "risk-stat-val"} value))
                    (d/el :span {:class "risk-stat-lbl"} label)))
            stats))))

(defn- prevention-row [items]
  (apply d/el :div {:class "prev-grid"}
         (mapv (fn [{:keys [icon title text]}]
                 (d/el :div {:class "prev-card"}
                       (d/ic icon "prev-icon")
                       (d/el :strong {:class "prev-title"} title)
                       (d/el :span {:class "prev-text"} text)))
               items)))

(defn- micro-control-flow []
  (let [steps [{:icon "package-check" :title "Recepción" :text "Verifica lote, temperatura y empaque"}
               {:icon "snowflake" :title "Almacenamiento" :text "Mantener productos por debajo de -18°C"}
               {:icon "thermometer" :title "Servicio" :text "Evitar exposición prolongada en vitrina"}
               {:icon "clipboard-check" :title "Registro" :text "Anotar controles y acciones correctivas"}]
        total (count steps)]
    (apply d/el :div {:class "micro-flow"}
           (map-indexed
            (fn [idx {:keys [icon title text]}]
              (d/el :div {:class "micro-flow-step"}
                    (d/el :div {:class "micro-flow-head"}
                          (d/ic icon "micro-flow-icon")
                          (d/el :strong {:class "micro-flow-title"} title))
                    (d/el :span {:class "micro-flow-text"} text)
                    (when (< idx (dec total))
                      (d/el :span {:class "micro-flow-arrow"} "→"))))
            steps))))

(defn- micro-alert-grid []
  (let [cards [{:icon "clock-3" :title "Tiempo crítico" :value "20 min"}
               {:icon "thermometer-sun" :title "Zona de riesgo" :value "5°C-60°C"}
               {:icon "users" :title "Capacitación" :value "Equipo entrenado"}
               {:icon "shield-alert" :title "Acción" :value "Corregir al detectar desvíos"}]]
    (apply d/el :div {:class "micro-alert-grid"}
           (mapv (fn [{:keys [icon title value]}]
                   (d/el :article {:class "micro-alert-card"}
                         (d/ic icon "micro-alert-icon")
                         (d/el :span {:class "micro-alert-title"} title)
                         (d/el :strong {:class "micro-alert-value"} value)))
                 cards))))

(defn- micro-reading-guide []
  (let [legend [{:icon "flame" :title "Caliente seguro" :temp "> 60°C" :tone "safe"}
                {:icon "alert-triangle" :title "Peligro térmico" :temp "5°C-60°C" :tone "danger"}
                {:icon "snowflake" :title "Frío seguro" :temp "0°C a -18°C" :tone "safe"}]
        actions [{:icon "thermometer" :title "1. Verificar" :text "Toma la temperatura en recepción y durante servicio."}
                 {:icon "shield-check" :title "2. Corregir" :text "Si está en zona de peligro, enfría o descarta según protocolo."}
                 {:icon "clipboard-check" :title "3. Registrar" :text "Anota incidencia, hora y acción para trazabilidad."}]]
    (d/el :section {:class "micro-guide"}
          (d/el :h3 {:class "micro-guide-h"}
                (d/ic "book-marked" "micro-guide-icon")
                "Guía rápida: cómo interpretar esta página")
          (apply d/el :div {:class "micro-legend-grid"}
                 (mapv (fn [{:keys [icon title temp tone]}]
                         (d/el :article {:class (str "micro-legend-card micro-legend-" tone)}
                               (d/ic icon "micro-legend-icon")
                               (d/el :strong {:class "micro-legend-title"} title)
                               (d/el :span {:class "micro-legend-temp"} temp)))
                       legend))
          (apply d/el :div {:class "micro-actions-grid"}
                 (mapv (fn [{:keys [icon title text]}]
                         (d/el :article {:class "micro-action-card"}
                               (d/ic icon "micro-action-icon")
                               (d/el :strong {:class "micro-action-title"} title)
                               (d/el :p {:class "micro-action-text"} text)))
                       actions)))))

;; ── Physical risk components ──────────────────────────────────
(defn- contaminant-table [contaminants]
  (d/el :div {:class "contam-grid"}
        (d/el :div {:class "contam-hdr"}
              (d/el :span {} "Tipo")
              (d/el :span {} "Ejemplos")
              (d/el :span {} "Riesgo"))
        (apply d/el :div {}
               (mapv (fn [{:keys [type icon examples severity]}]
                       (d/el :div {:class (str "contam-row contam-" severity)}
                             (d/el :span {:class "contam-type"}
                                   (d/ic icon "contam-icon") type)
                             (d/el :span {:class "contam-ex"} examples)
                             (d/el :span {:class (str "contam-sev contam-sev-" severity)}
                                   (case severity
                                     "high"   "Alto"
                                     "medium" "Medio"
                                     "low"    "Bajo"
                                     severity))))
                     contaminants))))

(defn- sources-list [sources]
  (apply d/el :div {:class "src-grid"}
         (mapv (fn [{:keys [area icon detail]}]
                 (d/el :div {:class "src-card"}
                       (d/ic icon "src-icon")
                       (d/el :div {:class "src-copy"}
                             (d/el :strong {:class "src-area"} area)
                             (d/el :span {:class "src-detail"} detail))))
               sources)))

(defn- chemical-source-grid [sources]
  (apply d/el :div {:class "chem-src-grid"}
         (mapv (fn [{:keys [title icon detail level]}]
                 (d/el :article {:class (str "chem-src-card chem-src-" (or level "medium"))}
                       (d/ic icon "chem-src-icon")
                       (d/el :div {:class "chem-src-copy"}
                             (d/el :strong {:class "chem-src-title"} title)
                             (d/el :span {:class "chem-src-detail"} detail))))
               sources)))

(defn- chemical-example-grid [examples]
  (apply d/el :div {:class "chem-ex-grid"}
         (mapv (fn [{:keys [icon title text]}]
                 (d/el :article {:class "chem-ex-card"}
                       (d/ic icon "chem-ex-icon")
                       (d/el :strong {:class "chem-ex-title"} title)
                       (d/el :p {:class "chem-ex-text"} text)))
               examples)))

(defn- chemical-reading-guide []
  (let [steps [{:icon "archive-x" :title "Separar" :text "Químicos lejos de ingredientes, utensilios y envases."}
               {:icon "tag" :title "Etiquetar" :text "Todo recipiente con nombre, dilución y fecha."}
               {:icon "shield-check" :title "Verificar" :text "Aplicar protocolo y registrar limpieza segura."}]]
    (d/el :section {:class "chemical-guide"}
          (d/el :h3 {:class "chemical-guide-h"}
                (d/ic "flask-conical" "chemical-guide-icon")
                "Guía rápida: control de riesgo químico")
          (apply d/el :div {:class "chemical-steps-grid"}
                 (mapv (fn [{:keys [icon title text]}]
                         (d/el :article {:class "chemical-step-card"}
                               (d/ic icon "chemical-step-icon")
                               (d/el :strong {:class "chemical-step-title"} title)
                               (d/el :p {:class "chemical-step-text"} text)))
                       steps)))))

(defn- physical-reading-guide []
  (let [checks [{:icon "search" :title "Inspección inicial" :text "Revisa envases, tapas, cucharas y vitrina antes de abrir."}
                {:icon "user-check" :title "Presentación personal" :text "Sin joyas, uñas cortas, cabello protegido y uniforme completo."}
                {:icon "wrench" :title "Equipo seguro" :text "Confirma tornillos, cuchillas y protectores en buen estado."}
                {:icon "shield-alert" :title "Respuesta inmediata" :text "Si aparece cuerpo extraño, retira producto y reporta."}]]
    (d/el :section {:class "physical-guide"}
          (d/el :h3 {:class "physical-guide-h"}
                (d/ic "shield-check" "physical-guide-icon")
                "Guía rápida: prevención de riesgo físico")
          (apply d/el :div {:class "physical-checks-grid"}
                 (mapv (fn [{:keys [icon title text]}]
                         (d/el :article {:class "physical-check-card"}
                               (d/ic icon "physical-check-icon")
                               (d/el :strong {:class "physical-check-title"} title)
                               (d/el :p {:class "physical-check-text"} text)))
                       checks)))))

;; ── Allergen risk components ──────────────────────────────────
(defn- allergen-table [allergens]
  (d/el :div {:class "allerg-grid"}
        (d/el :div {:class "allerg-hdr"}
              (d/el :span {} "Alérgeno")
              (d/el :span {} "Presente en")
              (d/el :span {} "Reacción"))
        (apply d/el :div {}
               (mapv (fn [{:keys [name icon source reaction severity]}]
                       (d/el :div {:class (str "allerg-row allerg-" (or severity "medium"))}
                             (d/el :span {:class "allerg-name"}
                                   (d/ic icon "allerg-icon") name)
                             (d/el :span {:class "allerg-src"} source)
                             (d/el :span {:class "allerg-react"} reaction)))
                     allergens))))

(defn- cross-contam-flow [steps]
  (let [total (count steps)]
    (apply d/el :div {:class "xcontam-flow"}
           (map-indexed
            (fn [idx {:keys [icon label]}]
              (d/el :div {:class "xcontam-step"}
                    (d/ic icon "xcontam-icon")
                    (d/el :span {:class "xcontam-label"} label)
                    (when (< idx (dec total))
                      (d/el :span {:class "xcontam-arrow"} "→"))))
            steps))))

;; ── Generic enhanced risk page ────────────────────────────────
(defn- enhanced-risk-page [{:keys [id icon title sub color hero-bola items
                                    danger-zone bacteria stats prevention
                                    contaminants sources
                                    allergens cross-contam-steps
                                    chemical-sources chemical-examples]} page-num]
  (d/el :article {:class (str "page risk-page risk-enhanced risk-" color) :id id}
        (if (= id "riesgo-microbiologico")
          (d/el :div {:class "risk-page-hero risk-micro-hero risk-micro-hero--triad"}
                (d/el :div {:class "risk-micro-hero-left"}
                      (d/ic icon "risk-hero-icon animate")
                      (d/el :h1 {:class "risk-hero-title animate d1"} title))
                (when hero-bola
                  (d/el :div {:class "risk-hero-bola-wrap risk-hero-bola-wrap--full animate d2"}
                        (d/src-img hero-bola title "risk-hero-bola-img")))
                (d/el :div {:class "risk-micro-hero-stats"}
                      (stat-badges stats)))
          (d/el :div {:class "risk-page-hero risk-micro-hero"}
                (d/ic icon "risk-hero-icon animate")
                (d/el :div {:class "risk-micro-hero-main"}
                      (d/el :h1 {:class "risk-hero-title animate d1"} title)
                      (stat-badges stats))
                (when hero-bola
                  (d/el :div {:class "risk-hero-bola-wrap risk-hero-bola-wrap--full risk-hero-bola-wrap--side animate d2"}
                        (d/src-img hero-bola title "risk-hero-bola-img")))))
        (d/el :div {:class "risk-page-body risk-micro-body"}
              (d/el :p {:class "risk-description animate d1"} sub)
              (d/el :div {:class "risk-micro-cols"}
                    ;; Left column
                    (d/el :div {:class "risk-micro-left"}
                          (cond
                            ;; Microbiological
                            danger-zone
                            (d/el :div {}
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "thermometer" "rsh-icon") "Zona de peligro térmico")
                                  (danger-zone-bar danger-zone)
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "microscope" "rsh-icon") "Bacterias comunes")
                                  (bacteria-table bacteria)
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "git-branch" "rsh-icon") "Flujo de control")
                                  (micro-control-flow))
                            ;; Allergens
                            allergens
                            (d/el :div {}
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "wheat" "rsh-icon") "Alérgenos principales")
                                  (allergen-table allergens))
                            ;; Physical
                            contaminants
                            (d/el :div {}
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "search" "rsh-icon") "Tipos de contaminantes")
                                  (contaminant-table contaminants)
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "map-pin" "rsh-icon") "Fuentes de origen")
                                  (sources-list sources))
                            ;; Chemical
                            chemical-sources
                            (d/el :div {}
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "flask-conical" "rsh-icon") "Fuentes de contaminación")
                                  (chemical-source-grid chemical-sources))))
                    ;; Right column
                    (d/el :div {:class "risk-micro-right"}
                          (d/el :h3 {:class "risk-section-h"} (d/ic "alert-triangle" "rsh-icon") "Causas")
                          (apply d/el :div {:class "risk-causes-compact"}
                                 (mapv (fn [c]
                                         (d/el :div {:class "cause-compact"}
                                               (d/ic (:icon c) "cause-icon")
                                               (d/el :span {:class "cause-label"} (:label c))))
                                       items))
                          (when danger-zone
                            (d/el :div {}
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "bar-chart-3" "rsh-icon") "Alertas clave")
                                  (micro-alert-grid)))
                          (when chemical-examples
                            (d/el :div {}
                                  (d/el :h3 {:class "risk-section-h"} (d/ic "triangle-alert" "rsh-icon") "Escenarios críticos")
                                  (chemical-example-grid chemical-examples)))
                          (d/el :h3 {:class "risk-section-h"} (d/ic "shield-check" "rsh-icon") "Prevención")
                          (prevention-row prevention)))
              (when (and allergens cross-contam-steps)
                (d/el :div {:class "xcontam-bar"}
                      (d/el :h3 {:class "xcontam-bar-h"}
                            (d/ic "shuffle" "xcontam-bar-h-icon")
                            "Flujo de contaminación cruzada")
                      (cross-contam-flow cross-contam-steps)))
              (when danger-zone
                (micro-reading-guide))
              (when contaminants
                (physical-reading-guide))
              (when chemical-sources
                (chemical-reading-guide)))
        (d/page-footer page-num)))

(defn render [{:keys [id icon title sub items color images-for-sections
                       enhanced?] :as data} page-num _theme]
  (let [img-key (keyword (str/replace id "riesgo-" ""))
        imgs    (get images-for-sections img-key)]
    (cond
      enhanced?
      (enhanced-risk-page data page-num)

      (= id "riesgo-quimico")
      (quimico-page data imgs page-num)

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
