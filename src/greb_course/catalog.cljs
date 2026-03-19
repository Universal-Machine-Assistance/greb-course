(ns greb-course.catalog
  "Catalog landing page, localStorage overrides, and edit panel."
  (:require [greb-course.dom :as d]))

(defn- ls-key [course-id k]
  (str "greb-course:" course-id ":" (name k)))

(defn- ls-get [course-id k]
  (.getItem js/localStorage (ls-key course-id k)))

(defn- ls-set! [course-id k v]
  (.setItem js/localStorage (ls-key course-id k) v))

(defn- get-override [course-id k fallback]
  (or (ls-get course-id k) fallback))

(defn- course-path [course]
  (let [org  (get-in course [:meta :org])
        slug (get-in course [:meta :slug])]
    (str "/" org "/" slug "/")))

(defn- build-edit-panel [course on-save!]
  (let [course-id (get-in course [:meta :id])
        colors    (get-in course [:theme :colors])
        fields    [{:key :primary   :label "Primary"   :default (:primary colors)}
                   {:key :secondary :label "Secondary" :default (:secondary colors)}
                   {:key :accent    :label "Accent"    :default (:accent colors)}
                   {:key :paper     :label "Background":default (:paper colors)}
                   {:key :page      :label "Page"      :default (:page colors)}]
        icon-val  (get-override course-id :icon "book-open")
        panel     (d/el :div {:class "edit-panel"})
        inputs    (atom {})]
    ;; Icon field
    (let [row (d/el :div {:class "edit-row"})
          lbl (d/el :label {:class "edit-label"} "Icon")
          inp (d/el :input {:class "edit-input" :type "text" :value icon-val})]
      (.setAttribute inp "placeholder" "lucide icon name")
      (.appendChild row lbl)
      (.appendChild row inp)
      (.appendChild panel row)
      (swap! inputs assoc :icon inp))
    ;; Color fields
    (doseq [{:keys [key label default]} fields]
      (let [cur (get-override course-id key default)
            row (d/el :div {:class "edit-row"})
            lbl (d/el :label {:class "edit-label"} label)
            inp (d/el :input {:class "edit-color" :type "color" :value cur})]
        (.appendChild row lbl)
        (.appendChild row inp)
        (.appendChild panel row)
        (swap! inputs assoc key inp)))
    ;; Save button
    (let [save-btn (doto (d/el :button {:class "edit-save-btn"}
                               (d/ic "check" "") "Save")
                         (.addEventListener "click"
                           (fn [e]
                             (.stopPropagation e)
                             (.preventDefault e)
                             (doseq [[k inp] @inputs]
                               (ls-set! course-id k (.-value inp)))
                             (on-save!))))]
      (.appendChild panel save-btn))
    panel))

(defn build-catalog [courses reload-fn!]
  (d/el :div {:class "catalog"}
        (d/el :nav {:class "toolbar"}
              (d/el :div {:class "toolbar-logo"}
                    (d/el :span {:class "catalog-brand"} "greb-course"))
              (d/el :span {:class "spread-indicator"} (str (count courses) " documents")))
        (d/el :div {:class "catalog-body"}
              (d/el :h1 {:class "catalog-title"} "Documents")
              (apply d/el :div {:class "catalog-grid"}
                     (mapv (fn [course]
                             (let [meta-data  (:meta course)
                                   course-id  (:id meta-data)
                                   theme      (:theme course)
                                   colors     (:colors theme)
                                   primary    (get-override course-id :primary (:primary colors))
                                   icon-name  (get-override course-id :icon "book-open")
                                   path       (course-path course)
                                   img-base   (or (:images-base theme)
                                                  (str (course-path course) "images/"))
                                   cover-img  (get-in (first (:pages course)) [:data :hero-img])
                                   card-wrap  (d/el :div {:class "catalog-card-wrap"})
                                   card-link  (d/el :a {:href path :class "catalog-card"})
                                   edit-host  (d/el :div {:class "edit-host"})
                                   edit-open? (atom false)]
                               (.setProperty (.-style card-link) "--card-primary" primary)
                               (when cover-img
                                 (.appendChild card-link
                                   (d/el :div {:class "catalog-card-img"}
                                         (d/el :img {:src (str img-base cover-img)
                                                     :alt (or (:title meta-data) "")}))))
                               (.appendChild card-link
                                 (d/el :div {:class "catalog-card-hdr"}
                                       (d/ic icon-name "catalog-card-icon")
                                       (d/el :span {:class "catalog-card-org"}
                                             (or (:org meta-data) ""))))
                               (.appendChild card-link
                                 (d/el :h2 {:class "catalog-card-title"}
                                       (or (:title meta-data) (:id meta-data))))
                               (when-let [desc (:description meta-data)]
                                 (.appendChild card-link
                                   (d/el :p {:class "catalog-card-desc"} desc)))
                               (.appendChild card-link
                                 (d/el :span {:class "catalog-card-pages"}
                                       (str (count (:pages course)) " pages")))
                               (let [edit-btn (doto (d/el :button {:class "catalog-edit-btn" :aria-label "Edit"}
                                                          (d/ic "settings" ""))
                                                    (.addEventListener "click"
                                                      (fn [e]
                                                        (.stopPropagation e)
                                                        (.preventDefault e)
                                                        (if @edit-open?
                                                          (do (set! (.-innerHTML edit-host) "")
                                                              (reset! edit-open? false))
                                                          (do (.appendChild edit-host
                                                                (build-edit-panel course
                                                                  reload-fn!))
                                                              (reset! edit-open? true))))))]
                                 (.appendChild card-link edit-btn))
                               (.appendChild card-wrap card-link)
                               (.appendChild card-wrap edit-host)
                               card-wrap))
                           courses)))))

(defn apply-overrides [course]
  (let [course-id (get-in course [:meta :id])
        color-keys [:primary :secondary :accent :paper :page]]
    (reduce (fn [c k]
              (if-let [v (ls-get course-id k)]
                (assoc-in c [:theme :colors k] v)
                c))
            course color-keys)))
