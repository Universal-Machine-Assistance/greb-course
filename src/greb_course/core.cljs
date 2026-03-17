(ns greb-course.core
  "Reader shell, navigation, scaling — generic course viewer with routing."
  (:require [greb-course.dom                  :as d]
            [greb-course.i18n                 :as i18n]
            [greb-course.templates.registry   :as reg]))

(declare reload!)

(defonce ^:private scale-resize-bound? (atom false))
(defonce ^:private built-mobile? (atom nil))
(defonce ^:private current-courses (atom nil))
(defonce ^:private current-nav (atom nil))

;; ── Routing helpers ─────────────────────────────────────────────
(defn- base-path []
  "Returns the base path without trailing hash. E.g. '/valentino/guia_de_higiene_alimentaria/'"
  (let [path (.-pathname js/location)]
    (if (.endsWith path "/") path (str path "/"))))

(defn- course-path [course]
  (let [org  (get-in course [:meta :org])
        slug (get-in course [:meta :slug])]
    (str "/" org "/" slug "/")))

(defn- match-course [courses]
  "Find the course matching the current URL path."
  (let [path (base-path)]
    (first (filter #(= path (course-path %)) courses))))

;; ── Layout helpers ──────────────────────────────────────────────
(defn- mobile-layout? []
  (.-matches (js/matchMedia "(max-width: 840px)")))

(defn- fit-reader-scale! []
  (let [now-mobile? (mobile-layout?)
        rebuilt?    (when (and (some? @built-mobile?) (not= now-mobile? @built-mobile?))
                     (reset! built-mobile? now-mobile?)
                     (reload! @current-courses)
                     true)]
    (when-not rebuilt?
      (when-let [reader (.querySelector js/document ".reader")]
        (let [mobile?    now-mobile?
              rem-px     (js/parseFloat (.-fontSize (js/getComputedStyle (.-documentElement js/document))))
              page-w     816
              page-h     1056
              gap        (if mobile? 0 3)
              side-pad   (* (if mobile? 1.0 5.5) rem-px)
              v-pad      (* (if mobile? 0.55 1.25) rem-px)
              pages      (if mobile? 1 2)
              spread-w   (+ (* pages page-w) gap (* 2 side-pad))
              spread-h   (+ page-h (* 2 v-pad))
              rw         (.-clientWidth reader)
              rh         (.-clientHeight reader)
              scale      (min 1 (/ rw spread-w) (/ rh spread-h))]
          (.setProperty (.-style reader) "--reader-scale" (str (max (if mobile? 0.35 0.5) scale))))))))

;; ── Spread entrance animation ────────────────────────────────────
(defn- animate-spread! [spread-el]
  (js/setTimeout
    (fn []
      (doseq [node (array-seq (.querySelectorAll spread-el ".animate"))]
        (.add (.-classList node) "visible")))
    60))

;; ── URL hash helpers ─────────────────────────────────────────────
(defn- current-hash []
  (let [h (.-hash js/location)]
    (when (> (count h) 1) (subs h 1))))

(defn- set-hash! [hash]
  (.replaceState js/history nil "" (str "#" hash)))

;; ── Navigator ────────────────────────────────────────────────────
(defn- build-navigator [spreads spread-ids dots indicator prev-btn next-btn]
  (let [n     (count spreads)
        state (atom 0)
        go!   (fn [i dir]
                (let [ni (max 0 (min (dec n) i))
                      cur @state]
                  (when (not= ni cur)
                    (let [old (nth spreads cur)]
                      (when dir (.add (.-classList old) dir))
                      (js/setTimeout #(.remove (.-classList old) "active" "going-back") 400))
                    (reset! state ni)
                    (let [nw (nth spreads ni)]
                      (when dir (.remove (.-classList nw) "going-back"))
                      (.add (.-classList nw) "active")
                      (animate-spread! nw))
                    (doseq [[j dt] (map-indexed vector dots)]
                      (if (= j ni)
                        (.add (.-classList dt) "active")
                        (.remove (.-classList dt) "active")))
                    (set! (.-textContent indicator) (str (inc ni) " / " n))
                    (set! (.-disabled prev-btn) (= ni 0))
                    (set! (.-disabled next-btn) (= ni (dec n)))
                    (set-hash! (nth spread-ids ni "")))
                  ni))]
    (.addEventListener prev-btn "click" #(go! (dec @state) "going-back"))
    (.addEventListener next-btn "click" #(go! (inc @state) nil))
    (.addEventListener js/document "keydown"
      (fn [e]
        (when (not (.closest (.-target e) "a, input, textarea, .toc-panel"))
          (case (.-key e)
            "ArrowRight" (go! (inc @state) nil)
            "ArrowLeft"  (go! (dec @state) "going-back")
            nil))))
    (doseq [[i dot] (map-indexed vector dots)]
      (.addEventListener dot "click"
        #(go! i (if (< i @state) "going-back" nil))))
    go!))

;; ── Floating TOC panel ───────────────────────────────────────────
(defn- build-toc-panel [go! id->spread toc-groups]
  (let [open?   (atom false)
        overlay (d/el :div {:class "toc-overlay"})
        panel   (d/el :div {:class "toc-panel"})
        close!  #(do (reset! open? false)
                     (.remove (.-classList panel)   "open")
                     (.remove (.-classList overlay) "open"))
        open!   #(do (reset! open? true)
                     (.add    (.-classList panel)   "open")
                     (.add    (.-classList overlay) "open"))
        toggle! #(if @open? (close!) (open!))]
    ;; Header
    (.appendChild panel
      (d/el :div {:class "toc-panel-hdr"}
            (d/el :div {:class "toc-panel-title"}
                  (d/ic "book-open" "toc-hdr-icon") (d/el :span {} (i18n/t :toc-title)))
            (doto (d/el :button {:class "toc-close-btn" :aria-label (i18n/t :close)}
                        (d/ic "x" ""))
                  (.addEventListener "click" close!))))
    ;; Scrollable body
    (let [body (d/el :div {:class "toc-panel-body"})]
      (doseq [{:keys [label entries]} toc-groups]
        (.appendChild body (d/el :p {:class "toc-group-label"} label))
        (doseq [{:keys [id label page]} entries]
          (.appendChild body
            (doto (d/el :button {:class "toc-entry"}
                        (d/el :span {:class "toc-entry-label"} label)
                        (d/el :span {:class "toc-entry-pg"} (str page)))
                  (.addEventListener "click"
                    (fn []
                      (when-let [idx (get id->spread id)]
                        (go! idx nil))
                      (close!)))))))
      (.appendChild panel body))
    (.addEventListener overlay "click" close!)
    {:overlay overlay :panel panel :toggle! toggle!}))

;; ── Toolbar ──────────────────────────────────────────────────────
(defn- toolbar [indicator toggle-toc! theme]
  (let [logo      (get theme :logo)
        brand     (get theme :brand-name "")
        print-btn (doto (d/el :button {:class "toolbar-btn"}
                              (d/ic "printer" "") (i18n/t :print))
                        (.addEventListener "click" #(.print js/window)))
        idx-btn   (doto (d/el :button {:class "toolbar-ghost-btn"}
                              (d/ic "list" "") (i18n/t :index))
                        (.addEventListener "click" toggle-toc!))]
    (d/el :nav {:class "toolbar"}
          (d/el :a {:href "#portada" :class "toolbar-logo"}
                (when logo (d/src-img logo brand nil)))
          indicator idx-btn print-btn)))

;; ── Apply theme CSS custom properties ────────────────────────────
(defn- hex->rgba [hex alpha]
  (let [h (if (= (first hex) "#") (subs hex 1) hex)
        r (js/parseInt (subs h 0 2) 16)
        g (js/parseInt (subs h 2 4) 16)
        b (js/parseInt (subs h 4 6) 16)]
    (str "rgba(" r "," g "," b "," alpha ")")))

(defn- apply-theme! [theme]
  (let [root-style (.-style (.-documentElement js/document))
        colors     (:colors theme)]
    (when-let [primary (:primary colors)]
      (.setProperty root-style "--brand-primary" primary)
      (.setProperty root-style "--brand-primary-bg" (hex->rgba primary 0.12)))
    (when-let [secondary (:secondary colors)]
      (.setProperty root-style "--brand-secondary" secondary)
      (.setProperty root-style "--brand-secondary-bg" (hex->rgba secondary 0.14)))
    (when-let [accent (:accent colors)]
      (.setProperty root-style "--brand-accent" accent)
      (.setProperty root-style "--brand-accent-bg" (hex->rgba accent 0.14)))
    (when-let [ink (:ink colors)]
      (.setProperty root-style "--ink" ink))
    (when-let [paper (:paper colors)]
      (.setProperty root-style "--paper" paper))
    (when-let [page (:page colors)]
      (.setProperty root-style "--page" page))))

;; ── Build course viewer ──────────────────────────────────────────
(defn- build-viewer [course]
  (let [theme      (:theme course)
        toc-groups (:toc course)
        pages-def  (:pages course)
        all-pages  (vec (map-indexed
                          (fn [i {:keys [template data]}]
                            (reg/render-page template data (inc i) theme))
                          pages-def))
        mobile?    (mobile-layout?)
        _          (reset! built-mobile? mobile?)
        groups     (if mobile?
                     (mapv vector all-pages)
                     (partition 2 2 nil all-pages))
        spread-ids (mapv #(.-id (first %)) groups)
        id->spread (into {} (mapcat (fn [[i group]]
                                       (keep (fn [pg] (when pg [(.-id pg) i])) group))
                                     (map-indexed vector groups)))
        spreads    (mapv (fn [group] (apply d/el :div {:class "spread"} group)) groups)
        n          (count spreads)
        dots       (mapv (fn [_] (d/el :button {:class "spread-dot"})) (range n))
        indicator  (d/el :span {:class "spread-indicator"} (str "1 / " n))
        prev-btn   (d/el :button {:class "nav-btn nav-prev"} (d/ic "chevron-left" ""))
        next-btn   (d/el :button {:class "nav-btn nav-next"} (d/ic "chevron-right" ""))
        go!        (build-navigator spreads spread-ids dots indicator prev-btn next-btn)
        _          (reset! current-nav {:go! go! :id->spread id->spread :spread-ids spread-ids
                                        :spread->pages (into {} (map-indexed
                                                        (fn [si group]
                                                          [si (mapv #(.-id %) (filter some? group))])
                                                        groups))})
        {:keys [overlay panel toggle!]} (build-toc-panel go! id->spread toc-groups)
        init-idx   (get id->spread (current-hash) 0)]
    (.add (.-classList (first spreads)) "active")
    (.add (.-classList (first dots))    "active")
    (set! (.-disabled prev-btn) true)
    (animate-spread! (first spreads))
    (when-not (current-hash) (set-hash! (first spread-ids)))
    (when (> init-idx 0) (js/setTimeout #(go! init-idx nil) 50))
    (.addEventListener js/window "hashchange"
      (fn []
        (when-let [idx (get id->spread (current-hash))]
          (go! idx nil))))
    (d/el :div {}
          overlay panel
          (toolbar indicator toggle! theme)
          (apply d/el :div {:class (str "reader" (when mobile? " reader--mobile"))}
                 (concat spreads [prev-btn next-btn
                                  (apply d/el :div {:class "spread-dots"} dots)])))))

;; ── Build catalog (landing page) ─────────────────────────────────
(defn- build-catalog [courses]
  (d/el :div {:class "catalog"}
        (d/el :nav {:class "toolbar"}
              (d/el :div {:class "toolbar-logo"}
                    (d/el :span {:class "catalog-brand"} "greb-course"))
              (d/el :span {:class "spread-indicator"} (str (count courses) " documents")))
        (d/el :div {:class "catalog-body"}
              (d/el :h1 {:class "catalog-title"} "Documents")
              (apply d/el :div {:class "catalog-grid"}
                     (mapv (fn [course]
                             (let [meta-data (:meta course)
                                   theme     (:theme course)
                                   path      (course-path course)]
                               (d/el :a {:href path :class "catalog-card"}
                                     (d/el :div {:class "catalog-card-hdr"}
                                           (d/ic "book-open" "catalog-card-icon")
                                           (d/el :span {:class "catalog-card-org"}
                                                 (or (:org meta-data) "")))
                                     (d/el :h2 {:class "catalog-card-title"}
                                           (or (:title meta-data) (:id meta-data)))
                                     (when-let [desc (:description meta-data)]
                                       (d/el :p {:class "catalog-card-desc"} desc))
                                     (d/el :span {:class "catalog-card-pages"}
                                           (str (count (:pages course)) " pages")))))
                           courses)))))

;; ── Boot ─────────────────────────────────────────────────────────
(defn- boot-course [course]
  (let [meta-data (:meta course)
        theme     (:theme course)
        lang      (or (:lang meta-data) :en)
        overrides (:i18n-overrides meta-data)
        img-base  (or (:images-base theme)
                      (str (course-path course) "images/"))]
    (i18n/init! lang overrides)
    (d/set-brand-name! (or (:brand-name theme) ""))
    (d/set-images-base! img-base)
    (apply-theme! theme)
    (when-let [title (:title meta-data)]
      (set! (.-title js/document) title))
    (.setAttribute (.-documentElement js/document) "lang" (name lang))
    (let [app (.getElementById js/document "app")]
      (.appendChild app (build-viewer course))
      (fit-reader-scale!)
      (when-not @scale-resize-bound?
        (.addEventListener js/window "resize" fit-reader-scale!)
        (reset! scale-resize-bound? true))
      (when (and js/lucide (.-createIcons js/lucide))
        (.createIcons js/lucide)))))

(defn- boot-catalog [courses]
  (set! (.-title js/document) "greb-course")
  (let [app (.getElementById js/document "app")]
    (.appendChild app (build-catalog courses))
    (when (and js/lucide (.-createIcons js/lucide))
      (.createIcons js/lucide))))

(defn- boot [courses]
  (reset! current-courses courses)
  (if-let [course (match-course courses)]
    (boot-course course)
    (boot-catalog courses)))

(defn init! [courses] (boot courses))

(defn reload! [courses]
  (when-let [app (.getElementById js/document "app")]
    (set! (.-innerHTML app) "")
    (boot courses)))

;; ── Public API for REPL ─────────────────────────────────────────
(defn get-courses [] @current-courses)

(defn update-courses! [courses]
  (reset! current-courses courses)
  (reload! courses))

(defn navigate!
  "Navigate to a spread by page id (string) or spread index (number)."
  [target]
  (when-let [{:keys [go! id->spread]} @current-nav]
    (let [idx (if (number? target)
                target
                (get id->spread (name target)))]
      (when idx (go! idx nil)))))

(defn current-page-id
  "Return the id of the currently visible page (from URL hash)."
  []
  (current-hash))
