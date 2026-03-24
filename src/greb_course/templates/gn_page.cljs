(ns greb-course.templates.gn-page
  "Greb-nue content page template — flexible block-based layout
   with optional blob/grid backgrounds and section sidebar."
  (:require [greb-course.dom :as d]
            [greb-course.rich-text :as rich]))

;; ── Sub-components ──────────────────────────────────────────

(defn- gn-tag [{:keys [label color]}]
  (d/el :span {:class (str "gn-tag gn-tag--" (or (name color) "blue"))}
        (d/el :span {:class "gn-tag-diamond"} label)))

(defn- gn-title-bar [{:keys [title icon img color]}]
  (d/el :div {:class (str "gn-title-bar gn-title-bar--" (or (name color) "yellow"))}
        (when img (d/src-img img title nil))
        (when icon (d/ic icon nil))
        title))

(defn- gn-card [{:keys [color title text children no-border info-icon] :as data}]
  (let [cls (str "gn-card"
                 (when color (str " gn-card--" (name color)))
                 (when no-border " gn-card--no-border")
                 (when info-icon " gn-card--info"))]
    (apply d/el :div {:class cls}
           (concat
             (when info-icon
               [(d/src-img (if (string? info-icon) info-icon "icons8-info.svg") "info" "gn-info-icon")])
             (when title [(d/el :h3 {} title)])
             (when text
               (if (vector? text)
                 (mapv (fn [p] (apply d/el :p {} (rich/inline-children (str p)))) text)
                 [(apply d/el :p {} (rich/inline-children (str text)))]))
             (or children [])))))

(defn- gn-two-col-text [{:keys [paragraphs drop-cap?]}]
  (apply d/el :div {:class "gn-two-col"}
         (map-indexed
           (fn [i p]
             (apply d/el :p {:class (when (and drop-cap? (zero? i)) "gn-drop-cap")}
                    (rich/inline-children (str p))))
           paragraphs)))

(defn- gn-tip [{:keys [label title text]}]
  (d/el :div {:class "gn-tip"}
        (d/src-img "greb-g.png" "Greb" "gn-tip-logo")
        (d/el :div {:class "gn-tip-content"}
              (when label (d/el :div {:class "gn-tip-label"} label))
              (when title (d/el :div {:class "gn-tip-title"} title))
              (when text (d/el :div {:class "gn-tip-text"} text)))))

(defn- gn-bio-card [{:keys [name photo bio info-text aside-img aside-text]}]
  (d/el :div {:class "gn-bio-card"}
        (d/el :div {:class "gn-bio-card-header"} name)
        (when photo (d/src-img photo name "gn-bio-card-photo"))
        (d/el :div {:class "gn-bio-card-body"}
              (d/el :div {:class "gn-card--info"}
                    (d/src-img "icons8-info.svg" "info" "gn-info-icon")
                    (apply d/el :div {}
                           (if (vector? info-text)
                             (mapv (fn [p] (apply d/el :p {} (rich/inline-children (str p)))) info-text)
                             [(apply d/el :p {} (rich/inline-children (str info-text)))]))))
        (when aside-img
          (d/el :div {:class "gn-bio-card-aside"}
                (d/src-img aside-img name "gn-content-img")
                (when aside-text (apply d/el :p {} (rich/inline-children (str aside-text))))))))

(defn- gn-feature-pills [{:keys [items]}]
  (apply d/el :div {:class "gn-pills"}
         (mapv (fn [{:keys [icon img title text color]}]
                 (d/el :div {:class (str "gn-pill gn-pill--" (or (name color) "blue"))}
                       (when img (d/src-img img title nil))
                       (when icon (d/ic icon nil))
                       (d/el :div {}
                             (d/el :div {:class "gn-pill-title"} title)
                             (when text (d/el :div {:class "gn-pill-text"} text)))))
               items)))

(defn- gn-quote-box [{:keys [text author color]}]
  (d/el :div {:class (str "gn-quote-box gn-card--" (or (name color) "pink"))}
        (apply d/el :p {} (rich/inline-children (str "\"" text "\"")))
        (when author (d/el :p {:class "gn-quote-box-author"} (str "— " author)))))

(defn- gn-table [{:keys [headers rows]}]
  (d/el :table {:class "gn-table"}
        (when (seq headers)
          (apply d/el :thead {}
                 [(apply d/el :tr {}
                         (mapv (fn [h] (d/el :th {} h)) headers))]))
        (apply d/el :tbody {}
               (mapv (fn [row]
                       (apply d/el :tr {}
                              (mapv (fn [cell]
                                      (if (map? cell)
                                        (d/el :td {}
                                              (when (:icon cell) (d/ic (:icon cell) "gn-table-icon"))
                                              (when (:img cell) (d/src-img (:img cell) "" "gn-table-icon"))
                                              (when (:text cell) (str (:text cell))))
                                        (d/el :td {} (str cell))))
                                    row)))
                     rows))))

(defn- gn-steps-list [{:keys [items]}]
  (apply d/el :div {:class "gn-steps-list"}
         (mapv (fn [{:keys [icon img label time]}]
                 (d/el :div {:class "gn-step"}
                       (when icon (d/ic icon nil))
                       (when img (d/src-img img label nil))
                       (d/el :span {} label)
                       (when time (d/el :span {:class "gn-step-time"} (str "(" time ")")))))
               items)))

(defn- gn-pipeline [{:keys [nodes]}]
  (apply d/el :div {:class "gn-pipeline"}
         (interleave
           (mapv (fn [{:keys [label color]}]
                   (d/el :div {:class (str "gn-pipeline-node gn-card--" (or (name color) "blue"))}
                         label))
                 nodes)
           (repeat (dec (count nodes))
                   (d/el :div {:class "gn-pipeline-arrow"} "\u2193")))))

(defn- gn-image-with-cards [{:keys [img cards]}]
  (d/el :div {:class "gn-image-text"}
        (d/src-img img "" "gn-content-img")
        (apply d/el :div {}
               (mapv (fn [c] (gn-card c)) cards))))

(defn- gn-icon-list [{:keys [items]}]
  (apply d/el :ul {:class "gn-icon-list"}
         (mapv (fn [{:keys [icon img text]}]
                 (d/el :li {}
                       (when icon (d/ic icon nil))
                       (when img (d/src-img img "" nil))
                       (apply d/el :span {} (rich/inline-children (str text)))))
               items)))

(defn- gn-image-block [{:keys [src alt caption float class]}]
  (d/el :div {:class (str "gn-image-block" (when float (str " gn-float-img" (when (= float :right) " gn-float-img--right"))))}
        (d/src-img src (or alt "") (or class "gn-content-img"))
        (when caption (d/el :p {:class "gn-caption"} caption))))

;; ── Block dispatcher ────────────────────────────────────────

(defn- render-block [{:keys [type] :as block}]
  (case type
    :gn-title-bar    (gn-title-bar block)
    :gn-card         (gn-card block)
    :gn-two-col-text (gn-two-col-text block)
    :gn-tip          (gn-tip block)
    :gn-bio-card     (gn-bio-card block)
    :gn-feature-pills (gn-feature-pills block)
    :gn-quote-box    (gn-quote-box block)
    :gn-table        (gn-table block)
    :gn-steps-list   (gn-steps-list block)
    :gn-pipeline     (gn-pipeline block)
    :gn-image-with-cards (gn-image-with-cards block)
    :gn-icon-list    (gn-icon-list block)
    :gn-image        (gn-image-block block)
    :gn-text         (let [{:keys [text drop-cap?]} block]
                       (if (vector? text)
                         (apply d/el :div {:class "gn-text-block"}
                                (map-indexed
                                  (fn [i p]
                                    (apply d/el :p {:class (when (and drop-cap? (zero? i)) "gn-drop-cap")}
                                           (rich/inline-children (str p))))
                                  text))
                         (apply d/el :div {:class "gn-text-block"}
                                [(apply d/el :p {:class (when drop-cap? "gn-drop-cap")}
                                        (rich/inline-children (str text)))])))
    ;; fallback
    (d/el :div {:class "gn-card gn-card--cream"}
          (d/el :p {} (str "[Unknown block type: " type "]")))))

;; ── Main render ─────────────────────────────────────────────

(defn render
  "Render a Greb-nue styled page.
   Data keys:
     :id             — page id
     :section        — section number (1, 2, 3)
     :sidebar-label  — text for vertical sidebar
     :sidebar-color  — :blue, :pink, :green
     :tag            — {:label \"/intro\" :color :blue}
     :bg-type        — :blobs, :grid, or nil
     :blocks         — vector of block maps"
  [{:keys [id section sidebar-label sidebar-color tag bg-type blocks]} page-num _theme]
  (let [has-sidebar? (boolean sidebar-label)
        bg-class (case bg-type
                   :blobs " gn-blobs-bg"
                   :grid  " gn-grid-bg"
                   "")]
    (d/el :article {:class (str "page gn-page" bg-class
                                (when has-sidebar? " gn-page--has-sidebar"))
                    :id id}
          ;; Background decorations rendered by CSS pseudo-elements
          ;; Sidebar
          (when has-sidebar?
            (d/el :div {:class (str "gn-sidebar gn-sidebar--" (or (name sidebar-color) "blue"))}
                  (d/el :span {:class "gn-sidebar-label"} sidebar-label)))
          ;; Page body
          (apply d/el :div {:class "page-body"}
                 (concat
                   ;; Header row: tag + title-bar (if first blocks are those types)
                   (when tag
                     [(d/el :div {:class "gn-content-header animate d1"}
                            (gn-tag tag))])
                   ;; Content blocks
                   (map-indexed
                     (fn [i block]
                       (d/el :div {:class (str "animate d" (inc (mod i 4)))}
                             (render-block block)))
                     blocks)))
          ;; Footer
          (d/page-footer page-num))))
