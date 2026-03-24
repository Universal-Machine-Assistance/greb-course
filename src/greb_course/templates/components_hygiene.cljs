(ns greb-course.templates.components-hygiene
  "Hygiene-course components: risk families, highlight bar, checklist, schedule."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]
            [clojure.string :as str]))

;; ── Risk families ─────────────────────────────────────────────

(defn- risk-fam-bola-visual [img title lead]
  (d/el :div {:class "risk-fam-bola-visual"}
        (d/el :div {:class "risk-fam-bola-img-wrap"}
              (d/src-img img (str title " — " lead) "risk-fam-bola-img"))))

(defn- risk-fam-bola-copy [title lead text]
  (d/el :div {:class "risk-fam-bola-copy"}
        (d/el :h3 {:class "risk-fam-bola-title"} title)
        (d/el :p {:class "risk-fam-bola-lead"} lead)
        (d/el :p {:class "risk-fam-bola-text"} text)))

(defn risk-familias-bolas-grid [items & [{:keys [hide-visuals?]}]]
  (apply d/el :div {:class (str "risk-fam-bolas-grid"
                                (when hide-visuals? " risk-fam-bolas-grid--text-only"))}
         (map-indexed
          (fn [idx {:keys [img title lead text tone]}]
            (let [slot (case idx 0 "a" 1 "b" 2 "c" "d")
                  cls  (str "risk-fam-bola-card risk-fam-bola-card--" slot
                            (when tone (str " risk-fam-bola--" tone))
                            (when (and (not hide-visuals?) (#{0 3} idx))
                              " risk-fam-bola-card--wide")
                            (when hide-visuals? " risk-fam-bola-card--no-visual"))]
              (d/el :article {:class cls}
                    (when-not hide-visuals? (risk-fam-bola-visual img title lead))
                    (risk-fam-bola-copy title lead text))))
          items)))

;; ── Highlight bar ─────────────────────────────────────────────

(defn highlight-bar [{:keys [icon title items]}]
  (d/el :div {:class "highlight-bar animate"}
        (d/el :div {:class "highlight-bar-icon-wrap"}
              (d/ic icon "highlight-bar-icon"))
        (d/el :div {:class "highlight-bar-body"}
              (apply d/el :p {:class "highlight-bar-title"}
                     (rich/inline-children (str title)))
              (apply d/el :ul {:class "highlight-bar-list"}
                     (mapv (fn [item]
                             (if (map? item)
                               (d/el :li {:class "highlight-step-item"}
                                     (when-let [item-icon (:icon item)]
                                       (d/ic item-icon "highlight-step-icon"))
                                     (apply d/el :span {:class "highlight-step-text"}
                                            (rich/inline-children (str (:text item)))))
                               (apply d/el :li {}
                                      (rich/inline-children (str item)))))
                           items)))))

;; ── Uniform checklist ─────────────────────────────────────────

(defn- ck-cell [v row-id i]
  (cond
    (= v :prohibido)
    (d/el :span {:class "uniform-ck-cell uniform-ck-cell--prohibido"} "PROHIBIDO")
    v
    (let [cb-id (str row-id "-" i)
          cb    (d/el :input {:type "checkbox" :class "uniform-ck-cb" :id cb-id})
          lbl   (d/el :label {:for cb-id :class "uniform-ck-check"} "")]
      (d/el :span {:class "uniform-ck-cell"} cb lbl))
    :else
    (d/el :span {:class "uniform-ck-cell uniform-ck-cell--na"} "—")))

(defn- ck-row [has-qty? tpl zones {:keys [item note] :as row}]
  (let [row-id (str "uck-" (hash item))
        cells  (into [(d/el :span {:class "uniform-ck-item"}
                            (when-let [ic (:icon row)] (d/ic ic "uniform-ck-item-icon"))
                            item)]
                     (concat
                      (when has-qty?
                        [(d/el :span {:class "uniform-ck-cell uniform-ck-cell--qty"}
                               (or (:qty row) "—"))])
                      (map-indexed (partial ck-cell row-id) zones)))
        row-el (apply d/el :div {:class "uniform-ck-row"
                                 :style (str "grid-template-columns:" tpl)}
                      cells)]
    (if note
      (d/el :div {:class "uniform-ck-row-wrap"}
            row-el (d/el :p {:class "uniform-ck-note"} note))
      row-el)))

(defn uniform-checklist [{:keys [zones rows]}]
  (let [has-qty? (some :qty rows)
        tpl      (str "2.8fr " (when has-qty? ".6fr ")
                      (apply str (repeat (count zones) "1fr ")))]
    (d/el :div {:class "uniform-ck"}
          (apply d/el :div {:class "uniform-ck-hdr"
                            :style (str "grid-template-columns:" tpl)}
                 (into [(d/el :span {:class "uniform-ck-hdr-cell"} "")]
                       (concat
                        (when has-qty? [(d/el :span {:class "uniform-ck-hdr-cell"} "Cant.")])
                        (mapv #(d/el :span {:class "uniform-ck-hdr-cell"} %) zones))))
          (apply d/el :div {:class "uniform-ck-body"}
                 (mapv (partial ck-row has-qty? tpl zones) rows)))))

;; ── Section bar / Omnibar ─────────────────────────────────────

(defn section-bar [icon-name title]
  (d/el :div {:class "section-bar mission-bar"}
        (d/ic icon-name "bar-icon")
        (d/el :h2 {} title)))

(defn omnibar-embed-block [{:keys [icon title caption]}]
  (d/el :section {:class "hygiene-block omni-embed-section"}
        (when (or icon title)
          (section-bar (or icon "terminal") (or title "OmniREPL — prueba interactiva")))
        (when caption
          (apply d/el :p {:class "omni-embed-caption"}
                 (rich/inline-children (str caption))))
        (d/el :div {:class "omni-embed-host"})))

;; ── Criteria table ────────────────────────────────────────────

(defn- criteria-row [{:keys [que como criterio icon freq-icon]}]
  (d/el :tr {:class "criteria-row"}
        (d/el :td {:class "criteria-que"}
              (d/el :span {:class "criteria-que-wrap"}
                    (when icon (d/ic icon "criteria-que-icon"))
                    (d/el :span {:class "criteria-que-txt"} que)))
        (d/el :td {:class "criteria-como"} como)
        (d/el :td {:class "criteria-criterio"}
              (d/el :span {:class "criteria-freq-wrap"}
                    (when freq-icon (d/ic freq-icon "criteria-freq-icon"))
                    (d/el :span {} criterio)))))

(defn criteria-table [headers rows]
  (d/el :div {:class "criteria-table-wrap"}
        (d/el :table {:class "criteria-table"}
              (apply d/el :thead {}
                     [(apply d/el :tr {}
                             (mapv #(d/el :th {} %) headers))])
              (apply d/el :tbody {}
                     (mapv criteria-row rows)))))

;; ── Schedule row ──────────────────────────────────────────────

(defn- freq-norm [freq]
  (-> (or freq "")
      (str/replace #"\n+" " ")
      (str/replace #"[ \t]+" " ")
      str/trim))

(defn sched-row [{:keys [freq area text icon days-label month-count]}]
  (let [fn*      (freq-norm freq)
        fi       (case fn* "Diario" "sun" "Semanal" "calendar-check"
                       "Mensual" "calendar" "Inmediata" "zap"
                       "Según acumulación" "snowflake" nil)
        fc       (case fn* "Diario" "sched-freq-diario" "Semanal" "sched-freq-semanal"
                       "Mensual" "sched-freq-mensual" "Inmediata" "sched-freq-inmediata"
                       "Según acumulación" "sched-freq-acumulacion" "sched-freq-default")]
    (d/el :div {:class "sched-row animate"}
          (d/el :span {:class (str "sched-freq " fc)}
                (d/el :span {:class "sched-freq-wrap"}
                      (when fi (d/ic fi "sched-freq-icon"))
                      (d/el :span {:class "sched-freq-label"} (or freq ""))))
          (when icon (d/ic icon "sched-icon"))
          (d/el :div {:class "sched-content"}
                (d/el :strong {:class "sched-area"} area)
                (d/el :p {:class "sched-text"} text)
                (when (or days-label month-count)
                  (d/el :div {:class "sched-meta"}
                        (when days-label
                          (d/el :span {:class "sched-days"} days-label))
                        (when month-count
                          (d/el :span {:class "sched-count"} (str month-count "/mes")))))))))
