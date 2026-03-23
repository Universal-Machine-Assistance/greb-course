(ns greb-course.templates.components
  "Shared sub-components used across templates."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]
            [clojure.string :as str]))

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

(defn store-map [{:keys [locations]}]
  (let [map-el  (d/el :div {:id "valentino-map" :class "store-map-container"})
        list-el (d/el :div {:class "store-map-list"})
        wrapper (d/el :div {:class "store-map"} map-el list-el)]
    ;; Build the sidebar list
    (doseq [{:keys [name addr tel hours lat lng]} locations]
      (let [card (d/el :div {:class "store-map-card" :data-lat (str lat) :data-lng (str lng) :data-name name}
                       (d/el :div {:class "store-map-card-hdr"}
                             (d/ic "ice-cream-cone" "store-map-card-logo")
                             (d/el :strong {:class "store-map-card-name"} name))
                       (d/el :p {:class "store-map-card-addr"} addr)
                       (d/el :p {:class "store-map-card-detail"}
                             (d/ic "phone" "store-map-card-icon") tel)
                       (d/el :p {:class "store-map-card-detail"}
                             (d/ic "clock" "store-map-card-icon") hours))]
        (.appendChild list-el card)))
    ;; Init Leaflet after mount
    (js/setTimeout
      (fn []
        (when (and (.-L js/window) (.getElementById js/document "valentino-map"))
          (let [L    (.-L js/window)
                m    (.setView (.map L "valentino-map" #js {:attributionControl false})
                               #js [18.47 -69.93] 9)
                icon (.icon L #js {:iconUrl "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'%3E%3Ccircle cx='12' cy='12' r='8' fill='%238B4513' stroke='white' stroke-width='2'/%3E%3Ctext x='12' y='16' text-anchor='middle' fill='white' font-size='10' font-weight='bold'%3EV%3C/text%3E%3C/svg%3E"
                                   :iconSize #js [24 24] :iconAnchor #js [12 12] :popupAnchor #js [0 -14]})
                mkrs (atom [])]
            (.addTo (.tileLayer L "https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
                                #js {:maxZoom 18}) m)
            ;; Add markers
            (doseq [{:keys [name addr tel hours lat lng]} locations]
              (let [html   (str "<div style='font-size:12px;line-height:1.4'>"
                                "<strong style='color:#8B4513'>" name "</strong><br>"
                                addr "<br>📞 " tel "<br>🕐 " hours "</div>")
                    marker (.addTo (.marker L #js [lat lng] #js {:icon icon}) m)]
                (.bindPopup marker html #js {:maxWidth 220})
                (swap! mkrs conj marker)))
            ;; Wire clicks
            (let [cards  (.querySelectorAll list-el ".store-map-card")
                  select (fn [idx sname]
                           (.setView m #js [(js/parseFloat (.getAttribute (.item cards idx) "data-lat"))
                                            (js/parseFloat (.getAttribute (.item cards idx) "data-lng"))] 15)
                           (.openPopup (nth @mkrs idx))
                           (doseq [j (range (.-length cards))]
                             (.remove (.-classList (.item cards j)) "is-active"))
                           (.add (.-classList (.item cards idx)) "is-active")
                           (.dispatchEvent js/document
                             (js/CustomEvent. "valentino-branch-select"
                               #js {:detail #js {:name sname}})))]
              (doseq [i (range (.-length cards))]
                (let [card  (.item cards i)
                      sname (.getAttribute card "data-name")]
                  (.addEventListener card "click" (fn [_] (select i sname)))
                  (.on (nth @mkrs i) "click" (fn [_] (select i sname)))))))))
      300)
    wrapper))

(defn uniform-checklist [{:keys [zones rows]}]
  (let [has-qty? (some :qty rows)
        tpl   (str "2.8fr " (when has-qty? ".6fr ") (apply str (repeat (count zones) "1fr ")))]
    (d/el :div {:class "uniform-ck"}
          ;; header row
          (apply d/el :div {:class "uniform-ck-hdr" :style (str "grid-template-columns:" tpl)}
                 (into [(d/el :span {:class "uniform-ck-hdr-cell"} "")]
                       (concat
                        (when has-qty? [(d/el :span {:class "uniform-ck-hdr-cell"} "Cant.")])
                        (mapv (fn [z] (d/el :span {:class "uniform-ck-hdr-cell"} z)) zones))))
          ;; data rows
          (apply d/el :div {:class "uniform-ck-body"}
                 (mapv (fn [{:keys [item zones note] :as row}]
                         (let [row-id (str "uck-" (hash item))
                               cells (into [(d/el :span {:class "uniform-ck-item"}
                                                       (when-let [ic (:icon row)]
                                                         (d/ic ic "uniform-ck-item-icon"))
                                                       item)]
                                                (concat
                                                 (when has-qty?
                                                   [(d/el :span {:class "uniform-ck-cell uniform-ck-cell--qty"}
                                                          (or (:qty row) "—"))])
                                                 (map-indexed
                                                  (fn [i v]
                                                    (cond
                                                      (= v :prohibido)
                                                      (d/el :span {:class "uniform-ck-cell uniform-ck-cell--prohibido"} "PROHIBIDO")
                                                      v
                                                      (let [cb-id (str row-id "-" i)
                                                            cb  (d/el :input {:type "checkbox" :class "uniform-ck-cb" :id cb-id})
                                                            lbl (d/el :label {:for cb-id :class "uniform-ck-check"} "")]
                                                        (d/el :span {:class "uniform-ck-cell"} cb lbl))
                                                      :else
                                                      (d/el :span {:class "uniform-ck-cell uniform-ck-cell--na"} "—")))
                                                  zones)))
                               row-el (apply d/el :div {:class "uniform-ck-row"
                                                        :style (str "grid-template-columns:" tpl)}
                                             cells)]
                           (if note
                             (d/el :div {:class "uniform-ck-row-wrap"}
                                   row-el
                                   (d/el :p {:class "uniform-ck-note"} note))
                             row-el)))
                       rows)))))

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
                             (apply d/el :span {:class "product-showcase-feat-label"}
                                    (rich/inline-children (str label)))))
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
                                     (apply d/el :span {:class "product-showcase-feat-label"}
                                            (rich/inline-children (str label)))))
                             features))))
        (d/el :div {:class "product-timeline-right"}
              (apply d/el :div {:class "timeline timeline--compact"}
                     (mapv timeline-entry timeline-items)))
        (when disclaimer
          (apply d/el :p {:class "product-timeline-disclaimer"}
                 (rich/inline-children (str disclaimer))))))

(defn mission-chip [label]
  (apply d/el :span {:class "mission-chip animate"}
         (rich/inline-children (str label))))

(defn wash-step [{:keys [step title text icon img]}]
  (let [mp4 (when img (str/replace img #"\.png$" ".mp4"))]
    (d/el :article {:class "wash-step animate"}
          (cond
            img
            (d/el :div {:class "wash-step-img-wrap"}
                  (d/el :video {:src (str @d/current-images-base mp4)
                                :poster (str @d/current-images-base img)
                                :autoplay true :loop true :muted true :playsinline true
                                :class "wash-step-img"})
                  ;; Hidden img fallback for print
                  (d/el :img {:src (str @d/current-images-base img)
                              :alt (or title "")
                              :class "wash-step-img wash-step-img--print"}))
            :else
            (d/el :div {:class "wash-step-num"}
                  (if icon (d/ic icon "wash-step-icon") step)))
          (d/el :div {:class "wash-step-body"}
                (apply d/el :h3 {:class "wash-step-title"}
                       (rich/inline-children (str title)))
                (apply d/el :p {:class "wash-step-text"}
                       (rich/inline-children (str text)))))))

(defn wash-carousel
  "Carousel showing one handwash step at a time with video + description alternating sides."
  [items]
  (let [n         (count items)
        container (d/el :div {:class "wash-carousel"})
        slides    (atom [])
        current   (atom 0)
        show!     (fn [idx]
                    (let [idx (mod idx n)]
                      (reset! current idx)
                      (doseq [[i sl] (map-indexed vector @slides)]
                        (if (= i idx)
                          (do (.remove (.-classList sl) "wc-slide--hidden")
                              (.add (.-classList sl) "wc-slide--active")
                              ;; Play the video
                              (when-let [vid (.querySelector sl "video")]
                                (.play vid)))
                          (do (.remove (.-classList sl) "wc-slide--active")
                              (.add (.-classList sl) "wc-slide--hidden")
                              ;; Pause other videos
                              (when-let [vid (.querySelector sl "video")]
                                (.pause vid)
                                (set! (.-currentTime vid) 0)))))))]
    ;; Build slides
    (doseq [[i {:keys [step title text icon img]}] (map-indexed vector items)]
      (let [mp4   (when img (str/replace img #"\.png$" ".mp4"))
            even? (even? i)
            slide (d/el :div {:class (str "wc-slide" (if even? " wc-slide--video-left" " wc-slide--video-right")
                                         (when (pos? i) " wc-slide--hidden"))})]
        ;; Video panel
        (let [video-panel (d/el :div {:class "wc-video-panel"})
              vid-el      (if img
                            (d/el :video {:src (str @d/current-images-base mp4)
                                          :poster (str @d/current-images-base img)
                                          :loop true :muted true :playsinline true
                                          :class "wc-video"})
                            (d/el :div {:class "wc-video-placeholder"}
                                  (d/el :div {:class "wash-step-num"}
                                        (if icon (d/ic icon "wash-step-icon") (str step)))))
              ;; Print fallback
              print-img   (when img
                            (d/el :img {:src (str @d/current-images-base img)
                                        :alt (or title "") :class "wc-video wc-video--print"}))]
          (.appendChild video-panel vid-el)
          (when print-img (.appendChild video-panel print-img))
          ;; Text panel
          (let [text-panel (d/el :div {:class "wc-text-panel"})
                step-num   (d/el :span {:class "wc-step-num"} (str "Paso " (inc i)))
                title-el   (apply d/el :h3 {:class "wc-title"} (rich/inline-children (str title)))
                desc-el    (apply d/el :p {:class "wc-desc"} (rich/inline-children (str text)))]
            (.appendChild text-panel step-num)
            (.appendChild text-panel title-el)
            (.appendChild text-panel desc-el)
            ;; Assemble slide — order depends on even/odd
            (if even?
              (do (.appendChild slide video-panel) (.appendChild slide text-panel))
              (do (.appendChild slide text-panel) (.appendChild slide video-panel)))))
        (.appendChild container slide)
        (swap! slides conj slide)))
    ;; Navigation dots
    (let [nav-bar (d/el :div {:class "wc-nav"})]
      (doseq [i (range n)]
        (let [dot (d/el :button {:class (str "wc-dot" (when (zero? i) " wc-dot--active"))
                                 :title (str "Paso " (inc i))}
                        (str (inc i)))]
          (.addEventListener dot "click" (fn [_] (show! i)))
          (.appendChild nav-bar dot)))
      ;; Prev/Next arrows
      (let [prev-btn (d/el :button {:class "wc-arrow wc-arrow--prev" :title "Anterior"} "‹")
            next-btn (d/el :button {:class "wc-arrow wc-arrow--next" :title "Siguiente"} "›")]
        (.addEventListener prev-btn "click" (fn [_] (show! (dec @current))))
        (.addEventListener next-btn "click" (fn [_] (show! (inc @current))))
        (.appendChild container prev-btn)
        (.appendChild container next-btn))
      (.appendChild container nav-bar)
      ;; Update dots on slide change
      (add-watch current ::dots
        (fn [_ _ _ new-idx]
          (let [dots (.querySelectorAll nav-bar ".wc-dot")]
            (doseq [j (range (.-length dots))]
              (let [d (.item dots j)]
                (if (= j new-idx)
                  (.add (.-classList d) "wc-dot--active")
                  (.remove (.-classList d) "wc-dot--active")))))))
      ;; Auto-play first video
      (js/setTimeout #(show! 0) 100))
    container))

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
    ;; Listen for map branch selection
    (when (seq branches)
      (.addEventListener js/document "valentino-branch-select"
        (fn [e]
          (let [sel-name (some-> (.-detail e) (.-name))
                match (first (filter #(= (:name %) sel-name) branches))]
            (when match
              (reset! branch* (:id match))
              (update!)
              ;; Scroll registro into view
              (js/setTimeout #(.scrollIntoView root #js {:behavior "smooth" :block "nearest"}) 100))))))
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

;; ── Proposal-friendly components ─────────────────────────────

(defn pricing-row [{:keys [label amount note highlight?]}]
  (d/el :div {:class (str "pricing-row" (when highlight? " pricing-row--hl"))}
        (apply d/el :span {:class "pricing-label"} (rich/inline-children (str label)))
        (d/el :span {:class "pricing-amount"} (or amount ""))
        (when note (apply d/el :span {:class "pricing-note"} (rich/inline-children (str note))))))

(defn pricing-table [{:keys [title rows total footer]}]
  (d/el :div {:class "pricing-table animate"}
        (when title (d/el :h3 {:class "pricing-title"} title))
        (apply d/el :div {:class "pricing-body"} (mapv pricing-row rows))
        (when total
          (d/el :div {:class "pricing-total"}
                (apply d/el :span {:class "pricing-label"} (rich/inline-children (str (:label total))))
                (d/el :span {:class "pricing-amount"} (:amount total))))
        (when footer (apply d/el :p {:class "pricing-footer"} (rich/inline-children (str footer))))))

(defn step-item [{:keys [num title text icon]}]
  (d/el :div {:class "step-item animate"}
        (d/el :div {:class "step-num-wrap"}
              (if icon (d/ic icon "step-icon")
                (d/el :span {:class "step-num"} (str (or num "•")))))
        (d/el :div {:class "step-body"}
              (apply d/el :h3 {:class "step-title"} (rich/inline-children (str title)))
              (apply d/el :p {:class "step-text"} (rich/inline-children (str text))))))

(defn steps-block [items]
  (apply d/el :div {:class "steps-list"} (mapv step-item items)))

(defn two-col-block [{:keys [left right]}]
  (d/el :div {:class "two-col animate"}
        (apply d/el :div {:class "two-col-left"} (rich/inline-children (str left)))
        (apply d/el :div {:class "two-col-right"} (rich/inline-children (str right)))))

(defn callout-block [{:keys [icon title text style]}]
  (d/el :div {:class (str "callout-block animate" (when style (str " callout--" (name style))))}
        (when icon (d/ic icon "callout-icon"))
        (d/el :div {:class "callout-body"}
              (when title (apply d/el :h3 {:class "callout-title"} (rich/inline-children (str title))))
              (apply d/el :p {:class "callout-text"} (rich/inline-children (str text))))))

(defn feature-list [{:keys [items]}]
  (apply d/el :ul {:class "feature-list animate"}
         (mapv (fn [item]
                 (if (map? item)
                   (d/el :li {:class "feature-item"}
                         (when (:icon item) (d/ic (:icon item) "feature-icon"))
                         (apply d/el :span {} (rich/inline-children (str (:text item)))))
                   (d/el :li {:class "feature-item"}
                         (d/ic "check" "feature-icon")
                         (apply d/el :span {} (rich/inline-children (str item))))))
               items)))

(defn quote-table
  "Professional quotation table with line items, subtotal, taxes, total."
  [{:keys [title number date client client-rnc client-email logos
           items subtotal discount tax total notes terms]}]
  (let [hdr-row (fn [cols]
                  (apply d/el :div {:class "qt-hdr-row"}
                         (mapv (fn [{:keys [label flex]}]
                                 (d/el :span {:class "qt-hdr-cell" :style (str "flex:" (or flex 1))} label))
                               cols)))
        data-row (fn [{:keys [desc qty unit-price amount highlight?]}]
                   (d/el :div {:class (str "qt-row" (when highlight? " qt-row--hl"))}
                         (d/el :span {:class "qt-cell qt-cell--desc" :style "flex:4"}
                               (if (map? desc)
                                 (d/el :span {}
                                       (apply d/el :strong {} (rich/inline-children (str (:title desc))))
                                       (when (:detail desc)
                                         (apply d/el :span {:class "qt-detail"} (rich/inline-children (str (:detail desc))))))
                                 (apply d/el :span {} (rich/inline-children (str desc)))))
                         (d/el :span {:class "qt-cell qt-cell--qty" :style "flex:1"} (str (or qty "")))
                         (d/el :span {:class "qt-cell qt-cell--price" :style "flex:1.5"} (str (or unit-price "")))
                         (d/el :span {:class "qt-cell qt-cell--amount" :style "flex:1.5"} (str (or amount "")))))
        summary-row (fn [label value cls]
                      (d/el :div {:class (str "qt-summary-row" (when cls (str " " cls)))}
                            (d/el :span {:class "qt-summary-label"} label)
                            (d/el :span {:class "qt-summary-value"} value)))]
    (d/el :div {:class "quote-table animate"}
          ;; Header with logos + quote info
          (d/el :div {:class "qt-header"}
                (d/el :div {:class "qt-header-left"}
                      (when (seq logos)
                        (apply d/el :div {:class "qt-logos"}
                               (mapv (fn [{:keys [src alt]}]
                                       (d/src-img src (or alt "") "qt-logo"))
                                     logos)))
                      (when title (d/el :h3 {:class "qt-title"} title))
                      (when number (d/el :span {:class "qt-number"} (str "N° " number))))
                (d/el :div {:class "qt-header-right"}
                      (when date (d/el :span {:class "qt-date"} (str "Fecha: " date)))
                      (when client (d/el :span {:class "qt-client"} (str "Cliente: " client)))
                      (when client-rnc (d/el :span {:class "qt-client-detail"} client-rnc))
                      (when client-email (d/el :span {:class "qt-client-detail"} client-email))))
          ;; Column headers
          (hdr-row [{:label "Descripción" :flex 4}
                    {:label "Cant." :flex 1}
                    {:label "P. Unitario" :flex 1.5}
                    {:label "Total" :flex 1.5}])
          ;; Line items
          (apply d/el :div {:class "qt-body"} (mapv data-row items))
          ;; Summary
          (d/el :div {:class "qt-summary"}
                (when subtotal (summary-row "Subtotal" subtotal nil))
                (when discount (summary-row (:label discount) (:amount discount) "qt-discount"))
                (when tax (summary-row (:label tax) (:amount tax) nil))
                (when total (summary-row "TOTAL" total "qt-total")))
          ;; Notes / terms
          (when notes
            (d/el :div {:class "qt-notes"}
                  (d/el :strong {} "Notas: ")
                  (apply d/el :span {} (rich/inline-children (str notes)))))
          (when terms
            (d/el :div {:class "qt-terms"}
                  (d/el :strong {} "Condiciones: ")
                  (apply d/el :span {} (rich/inline-children (str terms))))))))

(defn bank-card
  "Compact bank account card with logo and labeled fields."
  [{:keys [logo bank ref fields]}]
  (d/el :div {:class "bank-card animate"}
        (d/el :div {:class "bank-card-header"}
              (when logo (d/src-img (:src logo) (or (:alt logo) "") "bank-card-logo"))
              (d/el :div {:class "bank-card-header-text"}
                    (d/el :strong {:class "bank-card-bank"} (or bank ""))
                    (when ref (d/el :span {:class "bank-card-ref"} ref))))
        (apply d/el :div {:class "bank-card-fields"}
               (mapv (fn [{:keys [icon label value]}]
                       (d/el :div {:class "bank-card-field"}
                             (d/ic (or icon "info") "bank-card-icon")
                             (d/el :span {:class "bank-card-label"} (str label ":"))
                             (d/el :span {:class "bank-card-value"} value)))
                     fields))))

(defn text-block-el
  "Render a text-block with inline markdown (**bold**, etc.)."
  [content]
  (d/el :div {:class "text-block"}
    (apply d/el :p {:class "text-block-content"}
           (rich/inline-children (or content "")))))

(defn visitor-zone [{:keys [zone items]}]
  (d/el :article {:class "visitor-zone animate"}
        (apply d/el :h3 {:class "visitor-zone-title"}
               (rich/inline-children (str zone)))
        (apply d/el :div {:class "chip-row"}
               (mapv mission-chip items))))
