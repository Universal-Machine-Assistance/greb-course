(ns greb-course.templates.components-registro
  "Interactive cleaning log sheet (modes, branches, rows)."
  (:require [greb-course.dom :as d]))

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
                     (d/el :span {:class "registro-col registro-col-action"} "Registro / acción")
                     (d/el :span {:class "registro-col registro-col-colab"} "Colaborador"))
        row-nodes  (mapv (fn [{:keys [freq hora control accion colaborador]}]
                           (let [check-btn (d/el :button {:class "registro-check-btn" :type "button"} "☐")
                                 node      (d/el :div {:class "registro-row" :data-freq freq}
                                                  check-btn
                                                  (d/el :span {:class "registro-col registro-col-time"} hora)
                                                  (d/el :span {:class "registro-col registro-col-control"} control)
                                                  (d/el :span {:class "registro-col registro-col-action"} accion)
                                                  (d/el :span {:class "registro-col registro-col-colab"} (or colaborador "")))]
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
    (when (seq branches)
      (.addEventListener js/document "valentino-branch-select"
        (fn [e]
          (let [sel-name (some-> (.-detail e) (.-name))
                match (first (filter #(= (:name %) sel-name) branches))]
            (when match
              (reset! branch* (:id match))
              (update!)
              (js/setTimeout #(.scrollIntoView root #js {:behavior "smooth" :block "nearest"}) 100))))))
    root))
