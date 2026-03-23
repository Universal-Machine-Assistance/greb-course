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

(defn- build-card [course reload-fn!]
  (let [meta-data  (:meta course)
        course-id  (:id meta-data)
        theme      (:theme course)
        colors     (:colors theme)
        primary    (get-override course-id :primary (:primary colors))
        icon-name  (get-override course-id :icon "book-open")
        path       (course-path course)
        img-base   (or (:images-base theme) (str path "images/"))
        cover-img  (get-in (first (:pages course)) [:data :hero-img])
        tags       (or (:tags meta-data) [])
        card-wrap  (d/el :div {:class "catalog-card-wrap"})
        card-link  (d/el :a {:href path :class "catalog-card"})
        edit-host  (d/el :div {:class "edit-host"})
        edit-open? (atom false)]
    (.setProperty (.-style card-link) "--card-primary" primary)
    (when cover-img
      (.appendChild card-link
        (d/el :div {:class "catalog-card-img"}
              (d/el :img {:src (str img-base cover-img) :alt (or (:title meta-data) "")}))))
    (.appendChild card-link
      (d/el :div {:class "catalog-card-hdr"}
            (d/ic icon-name "catalog-card-icon")
            (d/el :span {:class "catalog-card-org"} (or (:org meta-data) ""))))
    (.appendChild card-link
      (d/el :h2 {:class "catalog-card-title"} (or (:title meta-data) (:id meta-data))))
    (when-let [desc (:description meta-data)]
      (.appendChild card-link (d/el :p {:class "catalog-card-desc"} desc)))
    ;; Tags
    (when (seq tags)
      (.appendChild card-link
        (apply d/el :div {:class "catalog-card-tags"}
               (mapv (fn [t] (d/el :span {:class "catalog-tag"} t)) tags))))
    (.appendChild card-link
      (d/el :span {:class "catalog-card-pages"} (str (count (:pages course)) " pages")))
    (let [edit-btn (doto (d/el :button {:class "catalog-edit-btn" :aria-label "Edit"}
                                (d/ic "settings" ""))
                         (.addEventListener "click"
                           (fn [e] (.stopPropagation e) (.preventDefault e)
                             (if @edit-open?
                               (do (set! (.-innerHTML edit-host) "") (reset! edit-open? false))
                               (do (.appendChild edit-host (build-edit-panel course reload-fn!))
                                   (reset! edit-open? true))))))]
      (.appendChild card-link edit-btn))
    (.appendChild card-wrap card-link)
    (.appendChild card-wrap edit-host)
    card-wrap))

(defn build-catalog [courses reload-fn!]
  (let [grouped   (group-by #(or (get-in % [:meta :category]) "Sin Categoría") courses)
        all-cats  ["Manuales de Software" "Seguridad Alimentaria" "Propuestas"]
        cats      (vec (distinct (concat (filter #(get grouped %) all-cats)
                                         (sort (remove (set all-cats) (keys grouped)))
                                         (remove #(get grouped %) all-cats))))
        cat-icons {"Manuales de Software" "code"
                   "Seguridad Alimentaria" "shield-check"
                   "Propuestas" "lightbulb"
                   "Sin Categoría" "folder"}
        all-tags  (->> courses (mapcat #(get-in % [:meta :tags])) (remove nil?) distinct sort vec)
        total-pgs (->> courses (map #(count (:pages %))) (reduce +))
        ;; Filter state
        active-cat (atom nil)
        active-tag (atom nil)
        ;; DOM refs
        main-el   (d/el :div {:class "catalog-main"})
        render!   (atom nil)]
    ;; ── Render function ──
    (reset! render!
      (fn []
        (set! (.-innerHTML main-el) "")
        (let [filt-courses (cond->> courses
                             @active-cat (filter #(= (get-in % [:meta :category]) @active-cat))
                             @active-tag (filter #(some #{@active-tag} (get-in % [:meta :tags]))))]
          (if (seq filt-courses)
            (let [fg (group-by #(or (get-in % [:meta :category]) "Sin Categoría") filt-courses)
                  vis-cats (if @active-cat [@active-cat] (filter #(get fg %) cats))]
              (doseq [cat vis-cats]
                (let [section (d/el :div {:class "catalog-section"})
                      hdr (d/el :div {:class "catalog-section-hdr"}
                                (d/ic (get cat-icons cat "folder") "catalog-section-icon")
                                (d/el :h2 {:class "catalog-section-title"} cat)
                                (d/el :span {:class "catalog-section-count"}
                                      (str (count (get fg cat)) " docs")))]
                  (.appendChild section hdr)
                  (.appendChild section
                    (apply d/el :div {:class "catalog-grid"}
                           (mapv #(build-card % reload-fn!) (get fg cat))))
                  (.appendChild main-el section))))
            (.appendChild main-el
              (d/el :div {:class "catalog-empty"}
                    (d/ic "search" "catalog-empty-icon")
                    (d/el :p {:class "catalog-empty-text"} "No hay documentos con este filtro."))))
          ;; Always show Propuestas placeholder if not filtered away
          (when (and (not @active-tag) (or (nil? @active-cat) (= @active-cat "Propuestas")))
            (when-not (get (group-by #(get-in % [:meta :category]) filt-courses) "Propuestas")
              (let [section (d/el :div {:class "catalog-section"})
                    hdr (d/el :div {:class "catalog-section-hdr"}
                              (d/ic "lightbulb" "catalog-section-icon")
                              (d/el :h2 {:class "catalog-section-title"} "Propuestas")
                              (d/el :span {:class "catalog-section-count"} "0 docs"))]
                (.appendChild section hdr)
                (.appendChild section
                  (d/el :div {:class "catalog-empty"}
                        (d/ic "plus-circle" "catalog-empty-icon")
                        (d/el :p {:class "catalog-empty-text"} "Próximamente — documentos en fase de propuesta y borrador.")))
                (.appendChild main-el section)))))))
    ;; ── Sidebar ──
    (let [sidebar (d/el :aside {:class "catalog-sidebar"})
          ;; Stats
          stats (d/el :div {:class "cat-sidebar-stats"}
                      (d/el :div {:class "cat-stat"}
                            (d/el :span {:class "cat-stat-num"} (str (count courses)))
                            (d/el :span {:class "cat-stat-label"} "documentos"))
                      (d/el :div {:class "cat-stat"}
                            (d/el :span {:class "cat-stat-num"} (str total-pgs))
                            (d/el :span {:class "cat-stat-label"} "páginas"))
                      (d/el :div {:class "cat-stat"}
                            (d/el :span {:class "cat-stat-num"} (str (count cats)))
                            (d/el :span {:class "cat-stat-label"} "categorías")))
          ;; Category nav
          cat-nav (d/el :nav {:class "cat-sidebar-nav"})
          _ (.appendChild cat-nav (d/el :h3 {:class "cat-sidebar-heading"} "Categorías"))
          all-btn (doto (d/el :button {:class "cat-nav-item cat-nav-item--active"}
                              (d/ic "layout-grid" "cat-nav-icon") "Todos")
                    (.addEventListener "click"
                      (fn [] (reset! active-cat nil)
                        (doseq [b (array-seq (.querySelectorAll cat-nav ".cat-nav-item"))]
                          (.remove (.-classList b) "cat-nav-item--active"))
                        (.add (.-classList all-btn) "cat-nav-item--active")
                        (@render!))))
          _ (.appendChild cat-nav all-btn)
          _ (doseq [cat cats]
              (let [cnt (count (get grouped cat []))
                    btn (doto (d/el :button {:class "cat-nav-item"}
                                    (d/ic (get cat-icons cat "folder") "cat-nav-icon")
                                    (str cat " (" cnt ")"))
                          (.addEventListener "click"
                            (fn []
                              (reset! active-cat cat)
                              (doseq [b (array-seq (.querySelectorAll cat-nav ".cat-nav-item"))]
                                (.remove (.-classList b) "cat-nav-item--active"))
                              (.add (.-classList btn) "cat-nav-item--active")
                              (@render!))))]
                (.appendChild cat-nav btn)))
          ;; Tags
          tag-section (d/el :div {:class "cat-sidebar-tags"})
          _ (.appendChild tag-section (d/el :h3 {:class "cat-sidebar-heading"} "Tags"))
          _ (doseq [tag all-tags]
              (let [btn (doto (d/el :button {:class "cat-tag-btn"} tag)
                          (.addEventListener "click"
                            (fn []
                              (if (= @active-tag tag)
                                (do (reset! active-tag nil)
                                    (.remove (.-classList btn) "cat-tag-btn--active"))
                                (do (reset! active-tag tag)
                                    (doseq [b (array-seq (.querySelectorAll tag-section ".cat-tag-btn"))]
                                      (.remove (.-classList b) "cat-tag-btn--active"))
                                    (.add (.-classList btn) "cat-tag-btn--active")))
                              (@render!))))]
                (.appendChild tag-section btn)))]
      (.appendChild sidebar stats)
      (.appendChild sidebar cat-nav)
      (.appendChild sidebar tag-section)
      ;; Initial render
      (@render!)
      (d/el :div {:class "catalog"}
            (d/el :nav {:class "toolbar"}
                  (d/el :div {:class "toolbar-logo"}
                        (d/el :span {:class "catalog-brand"} "greb-course"))
                  (d/el :span {:class "spread-indicator"} (str (count courses) " documents")))
            (d/el :div {:class "catalog-layout"}
                  sidebar main-el)))))

(defn apply-overrides [course]
  (let [course-id (get-in course [:meta :id])
        color-keys [:primary :secondary :accent :paper :page]]
    (reduce (fn [c k]
              (if-let [v (ls-get course-id k)]
                (assoc-in c [:theme :colors k] v)
                c))
            course color-keys)))
