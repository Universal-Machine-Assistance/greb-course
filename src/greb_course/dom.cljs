(ns greb-course.dom
  "Shared DOM utilities: element builder and common components.")

(defonce ^:private current-images-base (atom "/images/"))
(defonce ^:private current-brand-name (atom ""))

(defn set-images-base! [base]
  (reset! current-images-base base))

(defn set-brand-name! [name]
  (reset! current-brand-name name))

(defn el [tag attrs & children]
  (let [node (.createElement js/document (name tag))]
    (doseq [[k v] attrs]
      (when v
        (case k
          :class       (set! (.-className node) v)
          :id          (set! (.-id node) v)
          :href        (.setAttribute node "href" v)
          :src         (.setAttribute node "src" v)
          :alt         (.setAttribute node "alt" v)
          :data-lucide (.setAttribute node "data-lucide" v)
          :disabled    (set! (.-disabled node) v)
          (when (string? v) (.setAttribute node (name k) v)))))
    (doseq [ch children]
      (when ch
        (if (string? ch)
          (.appendChild node (.createTextNode js/document ch))
          (.appendChild node ch))))
    node))

(defn ic
  "Lucide icon element. icon-name must match a Lucide icon slug."
  [icon-name cls]
  (el :i {:data-lucide icon-name :class (str "icon " (or cls ""))}))

(defn src-img [path alt cls]
  (el :img {:src (str @current-images-base path) :alt (or alt "") :class (or cls "manual-img")}))

(defn page-footer [n]
  (el :footer {:class "page-footer"}
      (el :span {:class "page-footer-branding"}
          (src-img "logo-greb.png" "GREB" "page-footer-logo"))
      (el :span {:class "page-num"} (str n))))
