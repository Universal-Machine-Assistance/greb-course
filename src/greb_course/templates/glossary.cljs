(ns greb-course.templates.glossary
  "Glossary index + detail page templates."
  (:require [greb-course.dom :as d]
            [greb-course.i18n :as i18n]))

;; ── Glossary Index ──────────────────────────────────────────────
(defn render-index [{:keys [title terms]} page-num _theme]
  (d/el :article {:class "page" :id "glosario"}
        (d/el :div {:class "page-body"}
              (d/el :h1 {} title)
              (apply d/el :div {:class "glossary-cols"}
                     (mapv (fn [{:keys [id term icon theme def]}]
                             (d/el :a {:href (str "#" id) :class "glossary-term-link"}
                                   (d/el :article {:class (str "glossary-term" (when theme (str " gt-" theme)))}
                                         (d/el :div {:class "glossary-term-hdr"}
                                               (d/ic (or icon "book-open") "gt-icon")
                                               (d/el :h4 {:class "glossary-term-title"} term))
                                         (d/el :p {:class "glossary-term-def"} def)
                                         (d/el :span {:class "glossary-term-cta"}
                                               "Ver detalle"
                                               (d/ic "arrow-right" "gt-cta-arrow")))))
                           terms)))
        (d/page-footer page-num)))

;; ── Glossary Detail ─────────────────────────────────────────────
(defn- detail-item [text img-file]
  (if img-file
    (d/el :li {:class "gd-detail-img-li"}
          (d/src-img img-file "" "gd-item-img")
          (d/el :span {} text))
    (d/el :li {} (d/ic "check-circle" "gd-check") text)))

(defn- action-item [text img-file]
  (if img-file
    (d/el :li {:class "gd-action-img-li"}
          (d/ic "check-circle" "gd-check")
          (d/el :span {:class "gd-action-text"} text)
          (d/src-img img-file "" "gd-action-img"))
    (d/el :li {} (d/ic "check-circle" "gd-check") text)))

(defn render-detail [{:keys [id term icon theme def lead details actions
                              risk tip tip-img img img-badge callouts
                              detail-imgs action-imgs img-left feat-img
                              hero-img all-terms]} page-num _theme]
  (let [terms   all-terms
        idx     (first (keep-indexed #(when (= (:id %2) id) %1) terms))
        prev-t  (when (pos? (or idx 0)) (nth terms (dec idx)))
        next-t  (when (and idx (< idx (dec (count terms)))) (nth terms (inc idx)))
        hero-cls (str "gd-hero animate" (when theme (str " theme-" theme)))]
    (d/el :article {:class "page glossary-detail-page" :id id}
          (d/el :div {:class hero-cls}
                (d/el :div {:class "gd-hero-copy"}
                      (d/el :div {:class "gd-hero-kicker-row"}
                            (d/ic (or icon "book-open") "gd-hero-icon")
                            (d/el :p {:class "gd-kicker"} "Glosario ampliado"))
                      (d/el :h1 {:class "gd-title"} term)
                      (d/el :p {:class "gd-lead"} lead)))
          (d/el :div {:class "gd-cols"}
                (when hero-img
                  (d/el :div {:class (str "gd-hero-diagram animate"
                                         (when (= hero-img "contam-diagrama.png")
                                           " gd-hero-diagram--light"))}
                        (d/src-img hero-img "" "gd-hero-diagram-img")))
                (d/el :div {:class "gd-left animate d1"}
                      (when img-left
                        (d/el :div {:class "gd-left-illus gd-left-illus--hero animate"}
                              (d/src-img img-left "" "gd-left-illus-img")))
                      (when risk
                        (d/el :div {:class "gd-riesgo"}
                              (d/el :span {:class "gd-riesgo-hdr"}
                                    (d/ic "triangle-alert" "gd-riesgo-icon") "RIESGO")
                              (d/el :p {:class "gd-riesgo-text"} risk)))
                      (when-not img-left
                        (d/el :div {:class "gd-card"}
                              (d/el :div {:class "gd-sec-row"}
                                    (d/ic "book-text" "gd-sec-icon")
                                    (d/el :h2 {} "Definición"))
                              (d/el :p {:class "gd-def-text"} def)))
                      (when (seq details)
                        (d/el :div {:class "gd-card"}
                              (d/el :p {:class "gd-sub-hdr"} "En tienda debes:")
                              (apply d/el :ul {:class "gd-list"}
                                     (map-indexed (fn [i txt]
                                                    (detail-item txt (nth detail-imgs i nil)))
                                                  details)))))
                (d/el :div {:class "gd-right animate d2"}
                      (when img-left
                        (d/el :div {:class "gd-card"}
                              (d/el :div {:class "gd-sec-row"}
                                    (d/ic "book-text" "gd-sec-icon")
                                    (d/el :h2 {} "Definición"))
                              (d/el :p {:class "gd-def-text"} def)))
                      (when img
                        (d/el :div {:class "gd-illus-wrap"}
                              (d/src-img img term "gd-illus")
                              (when img-badge (d/src-img img-badge "" "gd-illus-badge"))
                              (when (seq callouts)
                                (apply d/el :div {:class "gd-callouts"}
                                       (mapv #(d/el :span {:class "gd-callout"}
                                                    (d/ic "check-circle" "gd-callout-chk") %)
                                             callouts)))))
                      (when feat-img
                        (d/el :div {:class "gd-feat-img-wrap animate d2"}
                              (d/src-img feat-img "" "gd-feat-img")))
                      (when (seq actions)
                        (d/el :div {:class "gd-card"}
                              (d/el :div {:class "gd-sec-row"}
                                    (d/ic "badge-check" "gd-sec-icon")
                                    (d/el :h2 {} "Acciones clave"))
                              (apply d/el :ul {:class "gd-list"}
                                     (map-indexed (fn [i txt]
                                                    (action-item txt (nth action-imgs i nil)))
                                                  actions))))
                      (when tip
                        (d/el :div {:class "gd-tip animate d3"}
                              (d/el :span {:class "gd-tip-hdr"}
                                    (d/ic "lightbulb" "gd-tip-icon") "TIP")
                              (d/el :div {:class "gd-tip-body"}
                                    (d/el :p {:class "gd-tip-text"} tip)
                                    (when tip-img
                                      (d/src-img tip-img "" "gd-tip-img")))))))
          (d/el :div {:class "gd-nav"}
                (if prev-t
                  (d/el :a {:href (str "#" (:id prev-t)) :class "gd-nav-btn"}
                        (d/ic "arrow-left" "") (i18n/t :prev))
                  (d/el :span {}))
                (d/el :a {:href "#glosario" :class "gd-nav-center"}
                      (i18n/t :glossary) (d/ic "layout-grid" ""))
                (if next-t
                  (d/el :a {:href (str "#" (:id next-t)) :class "gd-nav-btn gd-nav-next"}
                        (i18n/t :next) (d/ic "arrow-right" ""))
                  (d/el :span {})))
          (d/page-footer page-num))))
