(ns greb-course.templates.components-proposal
  "Business/proposal components: ref-table, pricing, steps, quote, bank-card."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]))

;; ── Reference table ───────────────────────────────────────────

(defn- ref-table-row [{:keys [img title text bullets]}]
  (d/el :div {:class "ref-table-row"}
        (d/el :div {:class "ref-table-img-cell"}
              (when img (d/src-img img (or title "") "ref-table-img")))
        (d/el :div {:class "ref-table-desc-cell"}
              (d/el :h3 {:class "ref-table-title"} title)
              (when (and text (pos? (count text)))
                (apply d/el :p {:class "ref-table-text"} (rich/inline-children (str text))))
              (when (seq bullets)
                (apply d/el :ul {:class "ref-table-bullets"}
                       (mapv #(apply d/el :li {} (rich/inline-children (str %))) bullets))))))

(defn ref-table [rows]
  (d/el :div {:class "ref-table"}
        (d/el :div {:class "ref-table-header"}
              (d/el :span {:class "ref-table-hdr-label"} "Imagen de referencia")
              (d/el :span {:class "ref-table-hdr-label"} "Descripciones Generales"))
        (apply d/el :div {:class "ref-table-body"}
               (mapv ref-table-row rows))))

;; ── Pricing table ─────────────────────────────────────────────

(defn- pricing-row [{:keys [label amount note highlight?]}]
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

;; ── Steps ─────────────────────────────────────────────────────

(defn- step-item [{:keys [num title text icon]}]
  (d/el :div {:class "step-item animate"}
        (d/el :div {:class "step-num-wrap"}
              (if icon (d/ic icon "step-icon")
                (d/el :span {:class "step-num"} (str (or num "•")))))
        (d/el :div {:class "step-body"}
              (apply d/el :h3 {:class "step-title"} (rich/inline-children (str title)))
              (apply d/el :p {:class "step-text"} (rich/inline-children (str text))))))

(defn steps-block [items]
  (apply d/el :div {:class "steps-list"} (mapv step-item items)))

;; ── Layout blocks ─────────────────────────────────────────────

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

(defn text-block-el [content]
  (d/el :div {:class "text-block"}
        (apply d/el :p {:class "text-block-content"}
               (rich/inline-children (or content "")))))

;; ── Quote table ───────────────────────────────────────────────

(defn- qt-hdr-row [cols]
  (apply d/el :div {:class "qt-hdr-row"}
         (mapv (fn [{:keys [label flex]}]
                 (d/el :span {:class "qt-hdr-cell" :style (str "flex:" (or flex 1))} label))
               cols)))

(defn- qt-data-row [{:keys [desc qty unit-price amount highlight?]}]
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

(defn- qt-summary-row [label value cls]
  (d/el :div {:class (str "qt-summary-row" (when cls (str " " cls)))}
        (d/el :span {:class "qt-summary-label"} label)
        (d/el :span {:class "qt-summary-value"} value)))

(defn quote-table [{:keys [title number date client client-rnc client-email logos
                            items subtotal discount tax total notes terms]}]
  (d/el :div {:class "quote-table animate"}
        (d/el :div {:class "qt-header"}
              (d/el :div {:class "qt-header-left"}
                    (when (seq logos)
                      (apply d/el :div {:class "qt-logos"}
                             (mapv #(d/src-img (:src %) (or (:alt %) "") "qt-logo") logos)))
                    (when title (d/el :h3 {:class "qt-title"} title))
                    (when number (d/el :span {:class "qt-number"} (str "N° " number))))
              (d/el :div {:class "qt-header-right"}
                    (when date (d/el :span {:class "qt-date"} (str "Fecha: " date)))
                    (when client (d/el :span {:class "qt-client"} (str "Cliente: " client)))
                    (when client-rnc (d/el :span {:class "qt-client-detail"} client-rnc))
                    (when client-email (d/el :span {:class "qt-client-detail"} client-email))))
        (qt-hdr-row [{:label "Descripción" :flex 4} {:label "Cant." :flex 1}
                     {:label "P. Unitario" :flex 1.5} {:label "Total" :flex 1.5}])
        (apply d/el :div {:class "qt-body"} (mapv qt-data-row items))
        (d/el :div {:class "qt-summary"}
              (when subtotal (qt-summary-row "Subtotal" subtotal nil))
              (when discount (qt-summary-row (:label discount) (:amount discount) "qt-discount"))
              (when tax (qt-summary-row (:label tax) (:amount tax) nil))
              (when total (qt-summary-row "TOTAL" total "qt-total")))
        (when notes
          (d/el :div {:class "qt-notes"}
                (d/el :strong {} "Notas: ")
                (apply d/el :span {} (rich/inline-children (str notes)))))
        (when terms
          (d/el :div {:class "qt-terms"}
                (d/el :strong {} "Condiciones: ")
                (apply d/el :span {} (rich/inline-children (str terms)))))))

;; ── Bank card ─────────────────────────────────────────────────

(defn bank-card [{:keys [logo bank ref fields]}]
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
