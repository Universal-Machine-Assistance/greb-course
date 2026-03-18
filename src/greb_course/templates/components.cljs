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

(defn highlight-bar [{:keys [icon title items]}]
  (d/el :div {:class "highlight-bar animate"}
        (d/el :div {:class "highlight-bar-icon-wrap"}
              (d/ic icon "highlight-bar-icon"))
        (d/el :div {:class "highlight-bar-body"}
              (d/el :p {:class "highlight-bar-title"} title)
              (apply d/el :ul {:class "highlight-bar-list"}
                     (mapv (fn [item]
                             (if (map? item)
                               (d/el :li {:class "highlight-step-item"}
                                     (when-let [item-icon (:icon item)]
                                       (d/ic item-icon "highlight-step-icon"))
                                     (d/el :span {:class "highlight-step-text"} (:text item)))
                               (d/el :li {} item)))
                           items)))))

(defn product-showcase [{:keys [img alt features]}]
  (d/el :div {:class "product-showcase"}
        (when img
          (d/el :div {:class "product-showcase-img-wrap"}
                (d/src-img img (or alt "") "product-showcase-img")))
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

(defn criteria-row [{:keys [que como criterio icon]}]
  (d/el :tr {:class "criteria-row"}
        (d/el :td {:class "criteria-que"}
              (d/el :span {:class "criteria-que-wrap"}
                    (when icon (d/ic icon "criteria-que-icon"))
                    (d/el :span {:class "criteria-que-txt"} que)))
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

(defn- registro-today-str []
  (let [d (js/Date.)
        pad (fn [n] (if (< n 10) (str "0" n) (str n)))]
    (str (pad (.getDate d)) "/" (pad (inc (.getMonth d))) "/" (.getFullYear d))))

(defn registro-sheet [{:keys [modes default-mode meta meta-hint rows
                              branches default-branch
                              timeframes default-timeframe
                              stats]}]
  (let [mode*      (atom (or default-mode (some-> modes first :id) "diario"))
        branch*    (atom (or default-branch (some-> branches first :id)))
        timeframe* (atom (or default-timeframe (some-> timeframes first :id)))
        root       (d/el :div {:class "registro-sheet"})
        val-suc    (when (seq branches) (d/el :span {:class "registro-meta-value registro-meta-value--live"}))
        val-enc    (when (seq branches) (d/el :span {:class "registro-meta-value registro-meta-value--live"}))
        val-fec    (when (seq branches) (d/el :span {:class "registro-meta-value registro-meta-value--live"}))
        meta-row   (if (seq branches)
                     (d/el :div {:class "registro-meta-wrap"}
                           (d/el :div {:class "registro-meta-row"}
                                 (d/el :div {:class "registro-meta-item"}
                                       (d/el :span {:class "registro-meta-label"} "Sucursal")
                                       val-suc)
                                 (d/el :div {:class "registro-meta-item"}
                                       (d/el :span {:class "registro-meta-label"} "Encargado")
                                       val-enc)
                                 (d/el :div {:class "registro-meta-item"}
                                       (d/el :span {:class "registro-meta-label"} "Fecha")
                                       val-fec))
                           (when meta-hint
                             (d/el :p {:class "registro-meta-hint"} meta-hint)))
                     (apply d/el :div {:class "registro-meta-row"}
                            (mapv (fn [{:keys [label value]}]
                                    (d/el :div {:class "registro-meta-item"}
                                          (d/el :span {:class "registro-meta-label"} label)
                                          (d/el :span {:class "registro-meta-value"} value)))
                                  meta)))
        branch-btns (mapv (fn [{:keys [id name icon]}]
                            (let [btn (d/el :button {:class "registro-branch-btn" :type "button"}
                                            (when icon (d/ic icon "registro-branch-icon"))
                                            (d/el :span {} name))]
                              {:id id :node btn}))
                          branches)
        branch-wrap (when (seq branch-btns)
                     (apply d/el :div {:class "registro-branch-wrap"} (mapv :node branch-btns)))
        branch-address (d/el :p {:class "registro-branch-address"} "")
        timeframe-btns (mapv (fn [{:keys [id label icon]}]
                               (let [btn (d/el :button {:class "registro-time-btn" :type "button"}
                                               (when icon (d/ic icon "registro-time-icon"))
                                               (d/el :span {} label))]
                                 {:id id :node btn}))
                             timeframes)
        timeframe-wrap (when (seq timeframe-btns)
                         (apply d/el :div {:class "registro-time-wrap"} (mapv :node timeframe-btns)))
        stats-row   (when (seq stats)
                      (apply d/el :div {:class "registro-stats-row"}
                             (mapv (fn [{:keys [icon value label]}]
                                     (d/el :div {:class "registro-stat"}
                                           (when icon (d/ic icon "registro-stat-icon"))
                                           (d/el :span {:class "registro-stat-value"} value)
                                           (d/el :span {:class "registro-stat-label"} label)))
                                   stats)))
        mode-btns  (mapv (fn [{:keys [id label icon]}]
                           (let [btn (d/el :button {:class "registro-mode-btn" :type "button"}
                                           (when icon (d/ic icon "registro-mode-icon"))
                                           (d/el :span {} label))]
                             {:id id :node btn}))
                         modes)
        mode-wrap  (apply d/el :div {:class "registro-mode-wrap"} (mapv :node mode-btns))
        hdr        (d/el :div {:class "registro-row registro-row--hdr"}
                     (d/el :span {:class "registro-col registro-col-check"} "Check")
                     (d/el :span {:class "registro-col registro-col-time"} "Hora")
                     (d/el :span {:class "registro-col registro-col-control"} "Control realizado")
                     (d/el :span {:class "registro-col registro-col-action"} "Registro / acción"))
        row-nodes  (mapv (fn [{:keys [freq hora control accion]}]
                           (let [check-btn (d/el :button {:class "registro-check-btn" :type "button"} "☐")
                                 node      (d/el :div {:class "registro-row" :data-freq freq}
                                                  check-btn
                                                  (d/el :span {:class "registro-col registro-col-time"} hora)
                                                  (d/el :span {:class "registro-col registro-col-control"} control)
                                                  (d/el :span {:class "registro-col registro-col-action"} accion))]
                             (.addEventListener check-btn "click"
                               (fn []
                                 (let [checked? (= (.-textContent check-btn) "☑")]
                                   (set! (.-textContent check-btn) (if checked? "☐" "☑"))
                                   (if checked?
                                     (.remove (.-classList check-btn) "is-checked")
                                     (.add (.-classList check-btn) "is-checked")))))
                             {:freq freq :node node}))
                         rows)
        rows-wrap  (apply d/el :div {:class "registro-rows"} (cons hdr (mapv :node row-nodes)))
        update!    (fn []
                     (doseq [{:keys [id node]} mode-btns]
                       (if (= id @mode*)
                         (.add (.-classList node) "is-active")
                         (.remove (.-classList node) "is-active")))
                     (doseq [{:keys [id node]} branch-btns]
                       (if (= id @branch*)
                         (.add (.-classList node) "is-active")
                         (.remove (.-classList node) "is-active")))
                     (doseq [{:keys [id node]} timeframe-btns]
                       (if (= id @timeframe*)
                         (.add (.-classList node) "is-active")
                         (.remove (.-classList node) "is-active")))
                     (when (seq branches)
                       (if-let [b (first (filter #(= (:id %) @branch*) branches))]
                         (do (set! (.-textContent branch-address)
                               (str (:address b) "  |  Encargado sugerido: " (:manager b)))
                             (when (and val-suc val-enc val-fec)
                               (set! (.-textContent val-suc) (:name b))
                               (set! (.-textContent val-enc) (:manager b))
                               (set! (.-textContent val-fec) (registro-today-str))))
                         (do (set! (.-textContent branch-address) "")
                             (when (and val-suc val-enc val-fec)
                               (doseq [n [val-suc val-enc val-fec]]
                                 (set! (.-textContent n) "—"))))))
                     (doseq [{:keys [freq node]} row-nodes]
                       (set! (.-display (.-style node))
                             (if (= freq @mode*) "grid" "none"))))]
    (doseq [{:keys [id node]} mode-btns]
      (.addEventListener node "click"
        (fn []
          (reset! mode* id)
          (update!))))
    (doseq [{:keys [id node]} branch-btns]
      (.addEventListener node "click"
        (fn []
          (reset! branch* id)
          (update!))))
    (doseq [{:keys [id node]} timeframe-btns]
      (.addEventListener node "click"
        (fn []
          (reset! timeframe* id)
          (update!))))
    (.appendChild root meta-row)
    (when branch-wrap
      (.appendChild root branch-wrap)
      (.appendChild root branch-address))
    (when timeframe-wrap
      (.appendChild root timeframe-wrap))
    (when stats-row
      (.appendChild root stats-row))
    (.appendChild root mode-wrap)
    (.appendChild root rows-wrap)
    (update!)
    root))

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

(defn sched-row [{:keys [freq area text icon days-label month-count]}]
  (let [freq-icon (case freq
                    "Diario"  "sun"
                    "Semanal" "calendar-check"
                    "Mensual" "calendar"
                    nil)
        freq-cls  (case freq
                    "Diario"  "sched-freq-diario"
                    "Semanal" "sched-freq-semanal"
                    "Mensual" "sched-freq-mensual"
                    "sched-freq-default")]
  (d/el :div {:class "sched-row animate"}
        (d/el :span {:class (str "sched-freq " freq-cls)}
              (d/el :span {:class "sched-freq-wrap"}
                    (when freq-icon (d/ic freq-icon "sched-freq-icon"))
                    (d/el :span {:class "sched-freq-label"} freq)))
        (when icon
          (d/ic icon "sched-icon"))
        (d/el :div {:class "sched-content"}
              (d/el :strong {:class "sched-area"} area)
              (d/el :p {:class "sched-text"} text)
              (when (or days-label month-count)
                (d/el :div {:class "sched-meta"}
                      (when days-label
                        (d/el :span {:class "sched-days"} days-label))
                      (when month-count
                        (d/el :span {:class "sched-count"} (str month-count "/mes")))))))))

(defn cleaning-gantt [{:keys [title days rows note]}]
  (d/el :div {:class "clean-gantt animate"}
        (when title
          (d/el :div {:class "clean-gantt-title-row"}
                (d/ic "calendar-clock" "clean-gantt-title-icon")
                (d/el :strong {:class "clean-gantt-title"} title)))
        (d/el :div {:class "clean-gantt-table"}
              (d/el :div {:class "clean-gantt-days"}
                    (d/el :span {:class "clean-gantt-spacer"} "")
                    (apply d/el :div {:class "clean-gantt-day-grid"}
                           (mapv (fn [d] (d/el :span {:class "clean-gantt-day"} d)) days)))
              (apply d/el :div {:class "clean-gantt-rows"}
                     (mapv (fn [{:keys [label icon active note]}]
                             (d/el :div {:class "clean-gantt-row"}
                                   (d/el :div {:class "clean-gantt-label"}
                                         (when icon (d/ic icon "clean-gantt-label-icon"))
                                         (d/el :span {:class "clean-gantt-label-text"} label))
                                   (apply d/el :div {:class "clean-gantt-bar-grid"}
                                          (map-indexed
                                           (fn [idx _]
                                             (d/el :span {:class (str "clean-gantt-cell "
                                                                      (if (contains? (set active) idx)
                                                                        "active" "idle"))}))
                                           days))
                                   (d/el :span {:class "clean-gantt-row-note"} note)))
                           rows)))
        (when note
          (d/el :p {:class "clean-gantt-note"} note))))

(defn cleaning-calendar [{:keys [title modes days activities note default-mode default-day month-label date-cells month year]}]
  (let [mode*      (atom (or default-mode (some-> modes first :id) "diario"))
        day*       (atom (or default-day (some-> days first :id) "l"))
        now        (js/Date.)
        cur-month  (inc (.getMonth now))
        cur-year   (.getFullYear now)
        cur-date   (.getDate now)
        today-date (when (and (= month cur-month) (= year cur-year)) cur-date)
        root       (d/el :div {:class "clean-cal animate"})
        hdr        (d/el :div {:class "clean-cal-hdr"}
                     (d/ic "calendar-clock" "clean-cal-hdr-icon")
                     (d/el :strong {:class "clean-cal-hdr-title"} title))
        mode-btns  (mapv (fn [{:keys [id label icon tone]}]
                           (let [btn (d/el :button {:class (str "clean-cal-mode-btn clean-cal-mode-" (or tone id))
                                                    :type "button"}
                                           (when icon (d/ic icon "clean-cal-mode-icon"))
                                           (d/el :span {} label))]
                             {:id id :node btn}))
                         modes)
        mode-wrap  (apply d/el :div {:class "clean-cal-mode-wrap"} (mapv :node mode-btns))
        weekday-hdr (apply d/el :div {:class "clean-cal-weekday-grid"}
                           (mapv (fn [{:keys [id label]}]
                                   (d/el :span {:class (str "clean-cal-weekday"
                                                            (when (#{ "s" "d"} id) " is-weekend"))}
                                         label))
                                 days))
        date-btns   (mapv (fn [cell]
                            (if (nil? cell)
                              {:id nil :day nil :node (d/el :span {:class "clean-cal-date-empty"} "")}
                              (let [{:keys [date day]} cell
                                    dots (d/el :span {:class "clean-cal-date-dots"}
                                               (d/el :span {:class "clean-cal-dot clean-cal-dot-diario"})
                                               (d/el :span {:class "clean-cal-dot clean-cal-dot-semanal"})
                                               (d/el :span {:class "clean-cal-dot clean-cal-dot-mensual"}))
                                    btn (d/el :button {:class "clean-cal-date-btn" :type "button"}
                                              (d/el :span {:class "clean-cal-date-num"} (str date))
                                              dots)]
                                {:id (str "d-" date)
                                 :day day
                                 :date date
                                 :dots {:diario (.querySelector dots ".clean-cal-dot-diario")
                                        :semanal (.querySelector dots ".clean-cal-dot-semanal")
                                        :mensual (.querySelector dots ".clean-cal-dot-mensual")}
                                 :node btn})))
                          date-cells)
        date-grid   (apply d/el :div {:class "clean-cal-date-grid"} (mapv :node date-btns))
        cal-wrap    (d/el :div {:class "clean-cal-month-wrap"}
                      (d/el :div {:class "clean-cal-month-hdr"}
                            (d/ic "calendar-days" "clean-cal-month-icon")
                            (d/el :span {:class "clean-cal-month-label"} month-label))
                      weekday-hdr
                      date-grid)
        rows-wrap  (d/el :div {:class "clean-cal-rows"})
        row-nodes  (mapv (fn [{:keys [mode days time task icon]}]
                           (let [node (d/el :div {:class "clean-cal-row"}
                                            (d/el :span {:class "clean-cal-time"} time)
                                            (d/el :div {:class "clean-cal-task-wrap"}
                                                  (when icon (d/ic icon "clean-cal-task-icon"))
                                                  (d/el :span {:class "clean-cal-task"} task)))]
                             {:mode mode :day-set (set days) :node node}))
                         activities)
        empty-node (d/el :div {:class "clean-cal-empty"} "Sin tareas para este dia en el modo seleccionado.")
        body       (d/el :div {:class "clean-cal-body"}
                     (d/el :div {:class "clean-cal-left"}
                           mode-wrap
                           cal-wrap)
                     (d/el :div {:class "clean-cal-right"} rows-wrap))
        pulse!     (fn [el]
                     (.add (.-classList el) "is-pop")
                     (js/setTimeout #(.remove (.-classList el) "is-pop") 220))
        update!    (fn []
                     (let [day->modes (reduce (fn [acc {:keys [mode day-set]}]
                                                (reduce (fn [m d]
                                                          (update m d (fnil conj #{}) mode))
                                                        acc
                                                        day-set))
                                              {}
                                              row-nodes)
                           matches-mode? (fn [row-mode]
                                           (or (= @mode* "unificado")
                                               (= row-mode @mode*)))]
                     (.remove (.-classList root) "mode-diario" "mode-semanal" "mode-mensual" "mode-unificado")
                     (.add (.-classList root) (str "mode-" @mode*))
                     (doseq [{:keys [id node]} mode-btns]
                       (if (= id @mode*)
                         (.add (.-classList node) "is-active")
                         (.remove (.-classList node) "is-active")))
                     (doseq [{:keys [day date node dots]} date-btns]
                       (when day
                         (if (= day @day*)
                           (.add (.-classList node) "is-active")
                           (.remove (.-classList node) "is-active"))
                         (if (#{ "s" "d"} day)
                           (.add (.-classList node) "is-weekend")
                           (.remove (.-classList node) "is-weekend"))
                         (if (= date today-date)
                           (.add (.-classList node) "is-today")
                           (.remove (.-classList node) "is-today"))
                         (let [modes-on-day (get day->modes day #{})]
                           (doseq [[k dot] dots]
                             (if (contains? modes-on-day (name k))
                               (.add (.-classList dot) "is-on")
                               (.remove (.-classList dot) "is-on"))))))
                     (set! (.-innerHTML rows-wrap) "")
                     (let [visible (filter (fn [{:keys [mode day-set]}]
                                             (and (matches-mode? mode) (contains? day-set @day*)))
                                           row-nodes)]
                       (if (seq visible)
                         (doseq [{:keys [node]} visible]
                           (.appendChild rows-wrap node))
                         (.appendChild rows-wrap empty-node)))))]
    (doseq [{:keys [id node]} mode-btns]
      (.addEventListener node "click"
        (fn []
          (pulse! node)
          (reset! mode* id)
          (update!))))
    (doseq [{:keys [day node]} date-btns]
      (when day
        (.addEventListener node "click"
          (fn []
            (pulse! node)
            (reset! day* day)
            (update!)))))
    (.appendChild root hdr)
    (.appendChild root body)
    (when note
      (.appendChild root (d/el :p {:class "clean-cal-note"} note)))
    (update!)
    root))

(defn visitor-zone [{:keys [zone items]}]
  (d/el :article {:class "visitor-zone animate"}
        (d/el :h3 {:class "visitor-zone-title"} zone)
        (apply d/el :div {:class "chip-row"}
               (mapv mission-chip items))))
