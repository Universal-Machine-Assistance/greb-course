(ns greb-course.templates.components-cleaning
  "Gantt + interactive cleaning calendar widgets."
  (:require [greb-course.dom :as d]))

(defn cleaning-gantt [{:keys [title days rows note week-labels]}]
  (let [n-cols (count days)
        col-css (str "repeat(" n-cols ", 1fr)")
        table (d/el :div {:class "clean-gantt-table"})]
    ;; Set dynamic column count via inline style on grids
    (let [day-grid (apply d/el :div {:class "clean-gantt-day-grid"}
                          (map-indexed
                           (fn [idx d-label]
                             (d/el :span {:class (str "clean-gantt-day"
                                                      (when (and (pos? idx) (zero? (mod idx 5)))
                                                        " clean-gantt-day--week-sep"))}
                                   d-label))
                           days))
          _ (.setProperty (.-style day-grid) "grid-template-columns" col-css)
          days-row (d/el :div {:class "clean-gantt-days"}
                         (d/el :span {:class "clean-gantt-spacer"} "")
                         day-grid)]
      ;; Week headers above days
      (when (seq week-labels)
        (let [wk-grid (apply d/el :div {:class "clean-gantt-week-grid"}
                             (map-indexed
                              (fn [idx {:keys [label span]}]
                                (d/el :span {:class (str "clean-gantt-week-hdr clean-gantt-week-hdr--" (inc idx))
                                             :style (str "grid-column: span " (or span 5))}
                                      label))
                              week-labels))
              wk-row (d/el :div {:class "clean-gantt-days"}
                           (d/el :span {:class "clean-gantt-spacer"} "")
                           wk-grid)]
          (.setProperty (.-style wk-grid) "grid-template-columns" col-css)
          (.appendChild table wk-row)))
      (.appendChild table days-row)
      ;; Task rows
      (let [rows-el (apply d/el :div {:class "clean-gantt-rows"}
                           (mapv (fn [{:keys [label icon active note]}]
                                   (let [active-set (set active)
                                         bar (apply d/el :div {:class "clean-gantt-bar-grid"}
                                                    (map-indexed
                                                     (fn [idx _]
                                                       (d/el :span {:class (str "clean-gantt-cell"
                                                                                (when (contains? active-set idx) " active")
                                                                                (when (and (pos? idx) (zero? (mod idx 5)))
                                                                                  " clean-gantt-cell--week-sep"))}))
                                                     days))]
                                     (.setProperty (.-style bar) "grid-template-columns" col-css)
                                     (d/el :div {:class "clean-gantt-row"}
                                           (d/el :div {:class "clean-gantt-label"}
                                                 (when icon (d/ic icon "clean-gantt-label-icon"))
                                                 (d/el :span {:class "clean-gantt-label-text"} label))
                                           bar
                                           (when note (d/el :span {:class "clean-gantt-row-note"} note)))))
                                 rows))]
        (.appendChild table rows-el)))
    (d/el :div {:class "clean-gantt animate"}
          (when title
            (d/el :div {:class "clean-gantt-title-row"}
                  (d/ic "calendar-clock" "clean-gantt-title-icon")
                  (d/el :strong {:class "clean-gantt-title"} title)))
          table
          (when note
            (d/el :p {:class "clean-gantt-note"} note)))))

(defn cleaning-calendar [{:keys [title modes days activities note default-mode default-day month-label date-cells month year]}]
  (let [mode*      (atom (or default-mode (some-> modes first :id) "diario"))
        day*       (atom (or default-day (some-> days first :id) "l"))
        sel-date*  (atom nil)
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
                           (let [node (d/el :div {:class (str "clean-cal-row clean-cal-row--" mode)}
                                            (d/el :div {:class "clean-cal-label"}
                                                  (when icon (d/ic icon "clean-cal-task-icon"))
                                                  (d/el :span {:class "clean-cal-time"} time))
                                            (d/el :span {:class "clean-cal-task"} task))]
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
                         (if (= date @sel-date*)
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
    (doseq [{:keys [day date node]} date-btns]
      (when day
        (.addEventListener node "click"
          (fn []
            (pulse! node)
            (reset! day* day)
            (reset! sel-date* date)
            (update!)))))
    (.appendChild root hdr)
    (.appendChild root body)
    (when note
      (.appendChild root (d/el :p {:class "clean-cal-note"} note)))
    (update!)
    root))
