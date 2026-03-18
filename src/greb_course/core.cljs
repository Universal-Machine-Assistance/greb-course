(ns greb-course.core
  "Reader shell, navigation, scaling — generic course viewer with routing."
  (:require [greb-course.dom                  :as d]
            [greb-course.i18n                 :as i18n]
            [greb-course.templates.registry   :as reg]))

(declare reload!)
(declare preflight!)

(defonce ^:private scale-resize-bound? (atom false))
(defonce ^:private built-mobile? (atom nil))
(defonce ^:private current-courses (atom nil))
(defonce ^:private current-nav (atom nil))
(defonce ^:private pres-state (atom nil))

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
(defn- build-navigator [spreads spread-ids dots indicator prev-btn next-btn initial-idx]
  (let [n     (count spreads)
        state (atom (max 0 (min (dec n) (or initial-idx 0))))
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

;; ── Presentation Mode ──────────────────────────────────────────
(defn- pres-toggle-fullscreen! [overlay]
  (if (or (.-fullscreenElement js/document) (.-webkitFullscreenElement js/document))
    (do (when (.-exitFullscreen js/document) (.exitFullscreen js/document))
        (when (.-webkitExitFullscreen js/document) (.webkitExitFullscreen js/document)))
    (do (when (.-requestFullscreen overlay) (.requestFullscreen overlay))
        (when (.-webkitRequestFullscreen overlay) (.webkitRequestFullscreen overlay)))))

(defn- fit-pres-scale! []
  (when-let [overlay (:overlay @pres-state)]
    (let [vw      (.-innerWidth js/window)
          vh      (.-innerHeight js/window)
          mobile? (< vw 840)
          pad     (if mobile? 20 80)
          scale   (min 1 (/ (- vw pad) 1280) (/ (- vh pad) 720))]
      (.setProperty (.-style overlay) "--pres-scale" (str scale)))))

(defn- pres-save-session! [idx]
  "Persist presentation slide index to sessionStorage."
  (.setItem js/sessionStorage "greb-pres-idx" (str idx)))

(defn- pres-clear-session! []
  (.removeItem js/sessionStorage "greb-pres-idx"))

(defn- pres-restore-session []
  "Returns saved slide index or nil."
  (when-let [v (.getItem js/sessionStorage "greb-pres-idx")]
    (js/parseInt v 10)))

(defn- pres-update-section! [idx]
  "Highlight the current section in index panel and section dots."
  (when-let [{:keys [slide->section entry-els section-dots]} @pres-state]
    (let [sec (get slide->section idx)]
      (when (not= sec (:current-section @pres-state))
        ;; Update index panel entries
        (doseq [el entry-els]
          (.remove (.-classList el) "pres-index-entry--active"))
        (when (and sec (< sec (count entry-els)))
          (let [el (nth entry-els sec)]
            (.add (.-classList el) "pres-index-entry--active")
            (.scrollIntoView el #js {:block "nearest" :behavior "smooth"})))
        ;; Update section dots
        (when section-dots
          (doseq [dot section-dots]
            (.remove (.-classList dot) "pres-section-dot--active"))
          (when (and sec (< sec (count section-dots)))
            (.add (.-classList (nth section-dots sec)) "pres-section-dot--active")))
        (swap! pres-state assoc :current-section sec)))))

(defn- pres-show-page! [idx direction]
  (when-let [{:keys [slides indicator n]} @pres-state]
    (let [idx (max 0 (min (dec n) idx))
          old-idx (:current @pres-state)]
      (when (not= idx old-idx)
        ;; Remove classes from old slide
        (when-let [old-slide (nth slides old-idx nil)]
          (.remove (.-classList old-slide) "pres-active" "pres-going-back"))
        ;; Add going-back class for backward direction
        (let [new-slide (nth slides idx)]
          (when (= direction :back)
            (.add (.-classList new-slide) "pres-going-back"))
          ;; Force reflow then add active
          (.-offsetHeight new-slide)
          (.add (.-classList new-slide) "pres-active")
          (when (= direction :back)
            ;; Remove going-back after transition starts
            (js/requestAnimationFrame
              #(.remove (.-classList new-slide) "pres-going-back")))
          ;; Reset and re-trigger .animate elements
          (doseq [node (array-seq (.querySelectorAll new-slide ".animate"))]
            (.remove (.-classList node) "visible"))
          (js/setTimeout
            (fn []
              (doseq [node (array-seq (.querySelectorAll new-slide ".animate"))]
                (.add (.-classList node) "visible")))
            80))
        ;; Update indicator & section highlight
        (set! (.-textContent indicator) (str (inc idx) " / " n))
        (swap! pres-state assoc :current idx)
        (pres-update-section! idx)
        ;; Persist to session
        (pres-save-session! idx)))))

(declare exit-presentation!)

(defn- full-page? [page]
  "Returns true for pages that should be cloned as a single full slide."
  (let [cl (.-classList page)]
    (or (.contains cl "portada-page")
        (.contains cl "full-image-page"))))

(defn- page-theme-classes [page]
  "Extract theme-related classes from a page element."
  (let [all-classes (array-seq (.-classList page))]
    (filterv #(or (.endsWith % "-page") (.endsWith % "-red")
                  (.endsWith % "-blue") (.endsWith % "-green")
                  (.endsWith % "-yellow") (.endsWith % "-orange")
                  (.endsWith % "-enhanced") (.endsWith % "-dark")
                  (= % "rq-page"))
             all-classes)))

(defn- visible-el? [el]
  "Returns true if element is visible and has dimensions."
  (let [style (js/getComputedStyle el)]
    (not (or (= "none" (.-display style))
             (= "hidden" (.-visibility style))
             (and (zero? (.-offsetWidth el))
                  (zero? (.-offsetHeight el)))))))

(defn- reset-animations! [root]
  "Remove .visible from all .animate elements (descendants + root).
   Keeps .animate class so they can be re-triggered."
  (doseq [node (array-seq (.querySelectorAll root ".animate"))]
    (.remove (.-classList node) "visible"))
  (.remove (.-classList root) "visible"))

(defn- sparse-content? [el]
  "Returns true if the element has very little text content (single sentence)."
  (let [text (.-textContent el)
        len  (count (.trim text))]
    (and (pos? len) (< len 200))))

(defn- make-slide [content-el full? theme-classes page-bg bg-img-src]
  "Wrap a cloned element in the pres-slide > pres-slide-inner structure."
  (let [inner (d/el :div {:class "pres-slide-inner"})
        slide (d/el :div {:class (str "pres-slide" (when full? " pres-slide--full"))})]
    ;; Background image (from .page-bg-img pages) — set as CSS background
    (when bg-img-src
      (.add (.-classList slide) "pres-slide--has-bg")
      (.setProperty (.-style slide) "--slide-bg-img" (str "url(" bg-img-src ")")))
    (.appendChild inner content-el)
    (.appendChild slide inner)
    ;; Mark sparse slides for larger text
    (when (and (not full?) (sparse-content? content-el))
      (.add (.-classList slide) "pres-slide--sparse"))
    ;; Propagate theme classes
    (doseq [cls theme-classes]
      (.add (.-classList slide) cls))
    ;; Propagate full background (color, gradient, etc.)
    (let [{:keys [bg-color bg-image]} page-bg
          has-gradient? (and bg-image
                            (not= bg-image "none")
                            (.includes bg-image "gradient"))]
      (when has-gradient?
        (.setProperty (.-style slide) "background-image" bg-image))
      (when (and bg-color
                 (not= bg-color "rgba(0, 0, 0, 0)")
                 (not= bg-color "transparent"))
        (.setProperty (.-style slide) "background-color" bg-color)))
    slide))

(defn- expandable-container? [el]
  "Returns true for containers whose children should each become a slide.
   Includes named containers and anonymous wrapper divs (no class, multiple children)."
  (let [cl (.-classList el)]
    (or (.contains cl "risk-page-body")
        (.contains cl "rq-body")
        (.contains cl "risk-micro-body")
        (.contains cl "risk-micro-cols")
        (.contains cl "gd-cols")
        (.contains cl "duo-grid")
        ;; Anonymous wrapper div — a <div> with no class and multiple children
        (and (= "DIV" (.-tagName el))
             (= 0 (.-length cl))
             (> (.-childElementCount el) 1)))))

(defn- skip-el? [el]
  "Returns true for elements that should never become slides."
  (let [cl (.-classList el)]
    (or (.contains cl "page-footer")
        (.contains cl "gd-nav")
        (= "FOOTER" (.-tagName el)))))

(defn- find-body [page]
  "Find the main body container of a page, trying multiple selectors."
  (or (.querySelector page ".page-body")
      (.querySelector page ".risk-page-body")
      (.querySelector page ".portada-body")))

(defn- small-el? [el]
  "Returns true if element is short enough to potentially group with neighbors.
   Threshold: roughly half the slide height (~350px)."
  (< (.-offsetHeight el) 350))

(defn- group-small-children
  "Given a seq of DOM elements, group adjacent small elements into combined
   wrapper divs so they share a slide. Large elements stay solo."
  [children]
  (loop [remaining children
         result   []
         pending  []]
    (if (empty? remaining)
      ;; Flush pending
      (if (seq pending) (conj result pending) result)
      (let [child (first remaining)
            rest-children (rest remaining)]
        (if (small-el? child)
          ;; Try to add to pending group
          (let [new-pending (conj pending child)
                total-h    (reduce + 0 (map #(.-offsetHeight %) new-pending))]
            (if (> total-h 600)
              ;; Pending group is full, flush it and start new group with current
              (recur rest-children (conj result pending) [child])
              ;; Fits, keep accumulating
              (recur rest-children result new-pending)))
          ;; Large element: flush pending, add element solo
          (let [result (if (seq pending) (conj result pending) result)]
            (recur rest-children (conj result [child]) [])))))))

(defn- extract-children-as-slides [parent classes bg bg-img-src page-id]
  "Extract visible children from a container as individual slides.
   Recursively expands known large containers. Groups adjacent small blocks.
   Returns vec of {:slide dom :page-id string}."
  (let [children (when parent (array-seq (.-children parent)))
        ;; First pass: flatten expandable containers
        flat     (reduce
                   (fn [acc child]
                     (cond
                       (skip-el? child)         acc
                       (not (visible-el? child)) acc
                       (expandable-container? child)
                       (into acc (filter #(and (not (skip-el? %)) (visible-el? %))
                                         (array-seq (.-children child))))
                       :else (conj acc child)))
                   [] (or children []))
        ;; Second pass: group adjacent small elements
        groups   (group-small-children flat)]
    (mapv (fn [group]
            {:page-id page-id
             :slide
             (if (= 1 (count group))
               (let [clone (.cloneNode (first group) true)]
                 (reset-animations! clone)
                 (make-slide clone false classes bg bg-img-src))
               ;; Multiple elements — wrap in a combined div
               (let [wrapper (d/el :div {:class "pres-grouped"})]
                 (doseq [el group]
                   (let [clone (.cloneNode el true)]
                     (reset-animations! clone)
                     (.appendChild wrapper clone)))
                 (make-slide wrapper false classes bg bg-img-src)))})
          groups)))

(defn- page-bg-img-src [page]
  "If the page has a .page-bg-img with an img, return its src. Otherwise nil."
  (when-let [bg-el (.querySelector page ".page-bg-img img")]
    (.-src bg-el)))

(defn- extract-slides []
  "Walk all .page elements, extract children into individual slides.
   Returns vec of {:slide dom-el :page-id string}."
  (let [source-pages (array-seq (.querySelectorAll js/document ".page"))]
    (reduce
      (fn [slides page]
        (let [classes    (page-theme-classes page)
              computed   (js/getComputedStyle page)
              bg         {:bg-color (.-backgroundColor computed)
                          :bg-image (.-backgroundImage computed)}
              bg-img-src (page-bg-img-src page)
              pid        (.-id page)]
          (cond
            ;; Full-page types: clone entire page as one slide
            (full-page? page)
            (let [clone (.cloneNode page true)]
              (reset-animations! clone)
              (conj slides {:page-id pid :slide (make-slide clone true classes bg nil)}))

            ;; Glossary detail: no .page-body, extract hero + cols children
            (.contains (.-classList page) "glossary-detail-page")
            (let [children (array-seq (.-children page))]
              (into slides
                (reduce
                  (fn [acc child]
                    (cond
                      (skip-el? child) acc
                      (not (visible-el? child)) acc
                      (.contains (.-classList child) "gd-cols")
                      (into acc (extract-children-as-slides child classes bg nil pid))
                      :else
                      (let [clone (.cloneNode child true)]
                        (reset-animations! clone)
                        (conj acc {:page-id pid :slide (make-slide clone false classes bg nil)}))))
                  [] children)))

            ;; Risk pages: extract hero + body children separately
            (.contains (.-classList page) "risk-page")
            (let [hero (.querySelector page ".risk-page-hero")
                  body (or (.querySelector page ".risk-page-body") nil)]
              (cond-> slides
                (and hero (visible-el? hero))
                (conj (let [clone (.cloneNode hero true)]
                        (reset-animations! clone)
                        {:page-id pid :slide (make-slide clone false classes bg bg-img-src)}))
                body
                (into (extract-children-as-slides body classes bg bg-img-src pid))))

            ;; Standard pages with .page-body
            :else
            (let [body (find-body page)]
              (if body
                (into slides (extract-children-as-slides body classes bg bg-img-src pid))
                ;; Fallback: clone whole page as full slide
                (let [clone (.cloneNode page true)]
                  (reset-animations! clone)
                  (conj slides {:page-id pid :slide (make-slide clone true classes nil nil)})))))))
      [] source-pages)))

(defn- build-pres-index [slide-data pres-go!]
  "Build a slide index panel for presentation mode.
   Groups slides by page-id, shows section headings.
   Returns {:panel :scrim :toggle! :slide->section :entry-els}."
  (let [panel   (d/el :div {:class "pres-index"})
        scrim   (d/el :div {:class "pres-index-scrim"})
        body    (d/el :div {:class "pres-index-body"})
        close!  #(do (.remove (.-classList panel) "pres-index--open")
                     (.remove (.-classList scrim) "pres-index-scrim--open"))
        open!   #(do (.add (.-classList panel) "pres-index--open")
                     (.add (.-classList scrim) "pres-index-scrim--open"))
        toggle! #(if (.contains (.-classList panel) "pres-index--open") (close!) (open!))
        ;; Group slide indices by page-id (preserve order)
        groups  (reduce
                  (fn [acc [i {:keys [page-id]}]]
                    (let [pid (or page-id "?")]
                      (if (and (seq acc) (= pid (:pid (peek acc))))
                        (update-in acc [(dec (count acc)) :indices] conj i)
                        (conj acc {:pid pid :indices [i]}))))
                  []
                  (map-indexed vector slide-data))
        ;; Build slide-index → section-index mapping
        slide->section (into {}
                         (mapcat (fn [si {:keys [indices]}]
                                   (map (fn [slide-i] [slide-i si]) indices))
                                 (range) groups))
        entry-els      (atom [])]
    (.appendChild panel (d/el :div {:class "pres-index-hdr"}
                              (d/el :span {} (i18n/t :toc-title))
                              (doto (d/el :button {:class "pres-index-close" :aria-label "Close"}
                                          (d/ic "x" ""))
                                    (.addEventListener "click" close!))))
    (doseq [{:keys [pid indices]} groups]
      (let [first-idx (first indices)
            page-el   (.getElementById js/document pid)
            title     (or (when page-el
                            (when-let [h (or (.querySelector page-el "h1")
                                             (.querySelector page-el ".hero-kicker")
                                             (.querySelector page-el ".risk-hero-title"))]
                              (let [t (.trim (.-textContent h))]
                                (when (pos? (count t)) t))))
                          pid)
            entry     (doto (d/el :button {:class "pres-index-entry"}
                                  (d/el :span {:class "pres-index-entry-title"} title)
                                  (d/el :span {:class "pres-index-entry-slides"}
                                        (str (inc (first indices)) "–" (inc (last indices)))))
                            (.addEventListener "click"
                              (fn [] (pres-go! first-idx nil) (close!))))]
        (swap! entry-els conj entry)
        (.appendChild body entry)))
    (.appendChild panel body)
    (.addEventListener scrim "click" close!)
    {:panel panel :scrim scrim :toggle! toggle!
     :slide->section slide->section :entry-els @entry-els
     :groups groups}))

(defn- slide-idx-for-page [page-ids page-id]
  "Return the first slide index whose page-id matches, or 0."
  (or (first (keep-indexed (fn [i pid] (when (= pid page-id) i)) page-ids))
      0))

;; ── View Transition helpers ──────────────────────────────────
(def ^:private vt-selectors
  "CSS selectors for landmark elements that morph between page ↔ slide.
   Each pair: [css-selector, view-transition-name]."
  [["h1, .hygiene-main-title, .risk-hero-title, .portada-title, .gd-title, .contenido-title"
    "vt-title"]
   [".hero-kicker, .gd-kicker, .rq-hero-sub, .portada-sub, .contenido-eyebrow"
    "vt-kicker"]
   ["img.hero-product-photo, img.rq-hero-img, img.portada-hero-img, img.gd-hero-diagram-img, .intro-img-stack img, .risk-hero-bola-wrap img, .full-image-bg img"
    "vt-hero-img"]])

(defn- tag-vt! [root]
  "Tag landmark elements within root with view-transition-name."
  (doseq [[sel vt-name] vt-selectors]
    (when-let [el (.querySelector root sel)]
      (.setProperty (.-style el) "view-transition-name" vt-name))))

(defn- clear-vt! [root]
  "Remove view-transition-name from landmark elements."
  (doseq [[sel _] vt-selectors]
    (when-let [el (.querySelector root sel)]
      (.removeProperty (.-style el) "view-transition-name"))))

(defn- has-view-transitions? []
  (and (exists? js/document.startViewTransition)
       (fn? (.-startViewTransition js/document))))

;; ── Enter / Exit ────────────────────────────────────────────
(defn- setup-presentation!
  "Core setup: build overlay, register listeners, show starting slide.
   Called inside or outside a View Transition."
  [slide-data start]
  (let [slide-els  (mapv :slide slide-data)
        page-ids   (mapv :page-id slide-data)
        n          (count slide-els)
        overlay    (d/el :div {:class "pres-overlay"})
        viewport   (d/el :div {:class "pres-viewport"})
        indicator  (d/el :span {:class "pres-indicator"} (str (inc start) " / " n))]
    ;; Append all slides to viewport
    (doseq [slide slide-els]
      (.appendChild viewport slide))
    (.appendChild overlay viewport)
    (.appendChild overlay indicator)
    (.appendChild (.-body js/document) overlay)
    ;; Build index panel
    (let [pres-go!    (fn [idx dir] (pres-show-page! idx dir))
          {:keys [panel scrim toggle! slide->section entry-els groups]}
          (build-pres-index slide-data pres-go!)
          ;; Section dots bar — clickable dots at the bottom
          section-dots (mapv
                         (fn [{:keys [pid indices]}]
                           (let [first-idx (first indices)
                                 page-el   (.getElementById js/document pid)
                                 tip       (or (when page-el
                                                 (when-let [h (or (.querySelector page-el "h1")
                                                                   (.querySelector page-el ".hero-kicker"))]
                                                   (let [t (.trim (.-textContent h))]
                                                     (when (pos? (count t)) t))))
                                               pid)]
                             (doto (d/el :button {:class "pres-section-dot" :title tip})
                                   (.addEventListener "click"
                                     (fn [] (pres-show-page! first-idx nil))))))
                         groups)
          section-bar (apply d/el :div {:class "pres-section-bar"} section-dots)
          idx-btn     (doto (d/el :button {:class "pres-toolbar-btn" :title (i18n/t :select-section)}
                                  (d/ic "list" ""))
                            (.addEventListener "click" toggle!))
          fs-btn      (doto (d/el :button {:class "pres-toolbar-btn" :title (i18n/t :fullscreen)}
                                  (d/ic "maximize-2" ""))
                        (.addEventListener "click" #(pres-toggle-fullscreen! overlay)))
          exit-btn    (doto (d/el :button {:class "pres-toolbar-btn" :aria-label (i18n/t :close)}
                                  (d/ic "x" ""))
                            (.addEventListener "click" #(exit-presentation!)))
          toolbar-el  (d/el :div {:class "pres-toolbar"} idx-btn fs-btn exit-btn)
          on-key      (fn [e]
                        (case (.-key e)
                          ("ArrowRight" "ArrowDown" " ")
                          (do (.preventDefault e) (.stopPropagation e)
                              (pres-show-page! (inc (:current @pres-state)) nil))
                          ("ArrowLeft" "ArrowUp")
                          (do (.preventDefault e) (.stopPropagation e)
                              (pres-show-page! (dec (:current @pres-state)) :back))
                          "Escape"
                          (do (.preventDefault e) (.stopPropagation e)
                              (exit-presentation!))
                          nil))
          on-fschange (fn []
                        (when (and (nil? (.-fullscreenElement js/document))
                                   (nil? (.-webkitFullscreenElement js/document)))
                          (fit-pres-scale!)))
          on-resize   (fn [] (fit-pres-scale!))]
      (.appendChild overlay panel)
      (.appendChild overlay scrim)
      (.appendChild overlay toolbar-el)
      (.appendChild overlay section-bar)
      ;; Store state
      (reset! pres-state {:overlay overlay :viewport viewport :indicator indicator
                          :slides slide-els :page-ids page-ids :n n :current -1
                          :slide->section slide->section :entry-els entry-els
                          :section-dots section-dots :current-section nil
                          :fs-btn fs-btn :on-key on-key :on-fschange on-fschange
                          :on-resize on-resize})
      ;; Compute scale
      (fit-pres-scale!)
      ;; Add presenting class (hides document)
      (.add (.-classList (.-documentElement js/document)) "presenting")
      ;; Register listeners
      (.addEventListener js/document "keydown" on-key true)
      (.addEventListener js/document "fullscreenchange" on-fschange)
      (.addEventListener js/document "webkitfullscreenchange" on-fschange)
      (.addEventListener js/window "resize" on-resize)
      ;; Show starting slide — use same path as navigation so it looks like the rest
      (pres-show-page! start nil)
      ;; Re-render lucide icons
      (when (and js/lucide (.-createIcons js/lucide))
        (.createIcons js/lucide #js {:root overlay}))
      ;; Tag slide landmarks for View Transition matching
      (tag-vt! (nth slide-els start))
      ;; Return overlay for fullscreen
      overlay)))

(defn- teardown-presentation!
  "Core teardown: remove listeners, clear state. Returns [overlay target-page-id]."
  []
  (when-let [{:keys [overlay on-key on-fschange on-resize page-ids current slides]} @pres-state]
    (let [target-page-id (nth page-ids (or current 0) nil)
          active-slide   (nth slides (or current 0) nil)]
      ;; Remove listeners
      (.removeEventListener js/document "keydown" on-key true)
      (.removeEventListener js/document "fullscreenchange" on-fschange)
      (.removeEventListener js/document "webkitfullscreenchange" on-fschange)
      (.removeEventListener js/window "resize" on-resize)
      ;; Exit fullscreen if active
      (when (or (.-fullscreenElement js/document) (.-webkitFullscreenElement js/document))
        (if (.-exitFullscreen js/document)
          (.exitFullscreen js/document)
          (when (.-webkitExitFullscreen js/document)
            (.webkitExitFullscreen js/document))))
      ;; Tag current slide landmarks for VT matching
      (when active-slide (tag-vt! active-slide))
      ;; Clear session & state
      (pres-clear-session!)
      (reset! pres-state nil)
      [overlay target-page-id])))

(defn- enter-presentation!
  ([] (enter-presentation! nil))
  ([start-idx]
   (when-not @pres-state
     (let [slide-data (extract-slides)
           page-ids   (mapv :page-id slide-data)
           n          (count (mapv :slide slide-data))
           auto-idx   (when (nil? start-idx)
                        (when-let [hash (current-hash)]
                          (slide-idx-for-page page-ids hash)))
           start      (max 0 (min (dec n) (or start-idx auto-idx 0)))
           src-page-id (nth page-ids start nil)
           src-el      (when src-page-id (.getElementById js/document src-page-id))]
       (if (and (has-view-transitions?) src-el)
         ;; ── View Transitions morph ──
         (do
           ;; Tag source page landmarks (old state)
           (tag-vt! src-el)
           (.setProperty (.-style src-el) "view-transition-name" "vt-page")
           (.startViewTransition js/document
             (fn []
               ;; This runs after old state snapshot — make DOM changes
               (clear-vt! src-el)
               (.removeProperty (.-style src-el) "view-transition-name")
               (let [overlay (setup-presentation! slide-data start)
                     slide   (nth (mapv :slide slide-data) start)]
                 ;; Tag the slide as the new "page" for the morph
                 (.setProperty (.-style slide) "view-transition-name" "vt-page")
                 ;; Clean up VT names after transition
                 (js/setTimeout
                   (fn []
                     (.removeProperty (.-style slide) "view-transition-name")
                     (clear-vt! slide))
                   700))
               nil)))
         ;; ── Fallback: simple fade ──
         (let [overlay (setup-presentation! slide-data start)]
           (.add (.-classList overlay) "pres-overlay--fade-in")
           (js/requestAnimationFrame
             #(.remove (.-classList overlay) "pres-overlay--fade-in"))))))))

(defn- exit-presentation! []
  (when @pres-state
    (let [[overlay target-page-id] (teardown-presentation!)]
      (when overlay
        (if (has-view-transitions?)
          ;; ── View Transitions morph ──
          (do
            ;; overlay is still in DOM with tagged landmarks (old state)
            (.startViewTransition js/document
              (fn []
                ;; Remove overlay, show document, navigate
                (when (.-parentNode overlay) (.remove overlay))
                (.remove (.-classList (.-documentElement js/document)) "presenting")
                ;; Navigate to matching page
                (when (and target-page-id @current-nav)
                  (let [{:keys [go! id->spread]} @current-nav]
                    (when-let [idx (get id->spread target-page-id)]
                      (go! idx nil))))
                ;; Tag target page as new "page" for morph
                (when-let [target-el (when target-page-id
                                       (.getElementById js/document target-page-id))]
                  (.setProperty (.-style target-el) "view-transition-name" "vt-page")
                  (tag-vt! target-el)
                  (js/setTimeout
                    (fn []
                      (.removeProperty (.-style target-el) "view-transition-name")
                      (clear-vt! target-el))
                    700))
                nil)))
          ;; ── Fallback: simple fade ──
          (do
            (.remove (.-classList (.-documentElement js/document)) "presenting")
            (when (and target-page-id @current-nav)
              (let [{:keys [go! id->spread]} @current-nav]
                (when-let [idx (get id->spread target-page-id)]
                  (go! idx nil))))
            (.add (.-classList overlay) "pres-overlay--fade-out")
            (js/setTimeout
              (fn [] (when (.-parentNode overlay) (.remove overlay)))
              400)))))))

;; ── Toolbar ──────────────────────────────────────────────────────
(defn- toolbar [indicator toggle-toc! theme]
  (let [logo      (get theme :logo)
        brand     (get theme :brand-name "")
        back-btn  (doto (d/el :a {:href "/" :class "toolbar-catalog-link" :title "Catalog" :aria-label "Catalog"}
                              (d/ic "house" ""))
                        (.addEventListener "click"
                          (fn [e]
                            (.preventDefault e)
                            (set! (.-location js/window) "/"))))
        print-btn (doto (d/el :button {:class "toolbar-btn"}
                              (d/ic "printer" "") (i18n/t :print))
                        (.addEventListener "click" #(.print js/window)))
        idx-btn   (doto (d/el :button {:class "toolbar-ghost-btn"}
                              (d/ic "list" "") (i18n/t :index))
                        (.addEventListener "click" toggle-toc!))
        pres-btn  (doto (d/el :button {:class "toolbar-ghost-btn"}
                              (d/ic "play" "") (i18n/t :present))
                        (.addEventListener "click" #(enter-presentation!)))]
    (d/el :nav {:class "toolbar"}
          (d/el :a {:href "#portada" :class "toolbar-logo"}
                (when logo (d/src-img logo brand nil)))
          indicator idx-btn pres-btn print-btn back-btn)))

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
        init-idx   (or (get id->spread (current-hash)) 0)
        go!        (build-navigator spreads spread-ids dots indicator prev-btn next-btn init-idx)
        _          (reset! current-nav {:go! go! :id->spread id->spread :spread-ids spread-ids
                                        :spread->pages (into {} (map-indexed
                                                        (fn [si group]
                                                          [si (mapv #(.-id %) (filter some? group))])
                                                        groups))})
        {:keys [overlay panel toggle!]} (build-toc-panel go! id->spread toc-groups)]
    (doseq [s spreads] (.remove (.-classList s) "active"))
    (doseq [d dots] (.remove (.-classList d) "active"))
    (.add (.-classList (nth spreads init-idx)) "active")
    (.add (.-classList (nth dots init-idx)) "active")
    (set! (.-textContent indicator) (str (inc init-idx) " / " n))
    (set! (.-disabled prev-btn) (= init-idx 0))
    (set! (.-disabled next-btn) (= init-idx (dec n)))
    (when-not (current-hash) (set-hash! (nth spread-ids init-idx "")))
    (animate-spread! (nth spreads init-idx))
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

;; ── LocalStorage helpers for catalog overrides ──────────────────
(defn- ls-key [course-id k]
  (str "greb-course:" course-id ":" (name k)))

(defn- ls-get [course-id k]
  (.getItem js/localStorage (ls-key course-id k)))

(defn- ls-set! [course-id k v]
  (.setItem js/localStorage (ls-key course-id k) v))

(defn- get-override [course-id k fallback]
  (or (ls-get course-id k) fallback))

;; ── Edit panel for a catalog card ───────────────────────────────
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
                               ;; Color stripe at top of card
                               (.setProperty (.-style card-link) "--card-primary" primary)
                               ;; Cover image
                               (when cover-img
                                 (.appendChild card-link
                                   (d/el :div {:class "catalog-card-img"}
                                         (d/el :img {:src (str img-base cover-img)
                                                     :alt (or (:title meta-data) "")}))))
                               ;; Card content
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
                               ;; Edit button
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
                                                                  #(reload! @current-courses)))
                                                              (reset! edit-open? true))))))]
                                 (.appendChild card-link edit-btn))
                               (.appendChild card-wrap card-link)
                               (.appendChild card-wrap edit-host)
                               card-wrap))
                           courses)))))

;; ── Apply localStorage overrides to course theme ────────────────
(defn- apply-overrides [course]
  (let [course-id (get-in course [:meta :id])
        color-keys [:primary :secondary :accent :paper :page]]
    (reduce (fn [c k]
              (if-let [v (ls-get course-id k)]
                (assoc-in c [:theme :colors k] v)
                c))
            course color-keys)))

;; ── Boot ─────────────────────────────────────────────────────────
(defn- hide-boot-loader! []
  (when-let [el (.getElementById js/document "app-boot-loader")]
    (.add (.-classList el) "app-boot-loader--out")
    (js/setTimeout
      (fn []
        (when (and el (.-parentNode el))
          (.remove el)))
      480)))

(defn- boot-course [course]
  (let [course    (apply-overrides course)
        meta-data (:meta course)
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
        (.createIcons js/lucide))
      (js/requestAnimationFrame
        (fn []
          (js/requestAnimationFrame
            (fn []
              (hide-boot-loader!)))))
      (js/setTimeout preflight! 1500)
      ;; Restore presentation mode if it was active before refresh
      (when-let [saved-idx (pres-restore-session)]
        (js/setTimeout #(enter-presentation! saved-idx) 300)))))

(defn- boot-catalog [courses]
  (set! (.-title js/document) "greb-course")
  (let [app (.getElementById js/document "app")]
    (.appendChild app (build-catalog courses))
    (when (and js/lucide (.-createIcons js/lucide))
      (.createIcons js/lucide))
    (js/requestAnimationFrame
      (fn []
        (js/requestAnimationFrame
          (fn []
            (hide-boot-loader!)))))))

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

;; ── Preflight overflow check ──────────────────────────────────
(defn preflight!
  "Scan all pages for content overflow. Highlights overflowing pages with a red
   border and logs details to the console. Call from REPL or browser console:
   greb_course.core.preflight_BANG_()"
  []
  (let [pages   (array-seq (.querySelectorAll js/document ".page"))
        results (atom [])]
    (doseq [page pages]
      (let [id       (or (.-id page) "?")
            body     (.querySelector page ".page-body")
            overflow? (when body
                        (> (.-scrollHeight body) (+ (.-clientHeight body) 2)))]
        (when overflow?
          (let [delta (- (.-scrollHeight body) (.-clientHeight body))]
            (.add (.-classList page) "preflight-overflow")
            (swap! results conj {:id id :overflow-px delta
                                 :scroll-h (.-scrollHeight body)
                                 :client-h (.-clientHeight body)})
            (js/console.warn
              (str "⚠ OVERFLOW page #" id
                   " — content exceeds by " delta "px"
                   " (scrollH=" (.-scrollHeight body)
                   " clientH=" (.-clientHeight body) ")"))))))
    (if (seq @results)
      (do (js/console.warn (str "⚠ PREFLIGHT: " (count @results) " page(s) overflowing"))
          (js/console.table (clj->js @results)))
      (js/console.log "✓ PREFLIGHT: All pages fit — no overflow detected"))
    @results))
