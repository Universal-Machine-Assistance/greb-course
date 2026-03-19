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
(defonce ^:private doc-view (atom {:zoom 1.0 :text-scale 1.0}))

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

(defn- doc-apply-view! []
  (when-let [reader (.querySelector js/document ".reader")]
    (let [{:keys [zoom text-scale pan-x pan-y]} @doc-view]
      (.setProperty (.-style reader) "--doc-zoom" (str (or zoom 1.0)))
      (.setProperty (.-style reader) "--doc-text-scale" (str (or text-scale 1.0)))
      (.setProperty (.-style reader) "--doc-pan-x" (str (or pan-x 0) "px"))
      (.setProperty (.-style reader) "--doc-pan-y" (str (or pan-y 0) "px")))))

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
    ;; ── Swipe gestures (touch) — reader only, not during presentation ──
    (let [touch-x (atom nil)
          touch-y (atom nil)]
      (.addEventListener js/document "touchstart"
        (fn [e]
          (when-not (.contains (.-classList (.-documentElement js/document)) "presenting")
            (let [t (aget (.-touches e) 0)]
              (reset! touch-x (.-clientX t))
              (reset! touch-y (.-clientY t)))))
        #js {:passive true})
      (.addEventListener js/document "touchend"
        (fn [e]
          (when-not (.contains (.-classList (.-documentElement js/document)) "presenting")
            (when-let [x0 @touch-x]
              (let [t  (aget (.-changedTouches e) 0)
                    dx (- (.-clientX t) x0)
                    dy (- (.-clientY t) @touch-y)]
                (when (and (> (js/Math.abs dx) 48)
                           (< (js/Math.abs dy) (js/Math.abs dx)))
                  (if (< dx 0)
                    (go! (inc @state) nil)
                    (go! (dec @state) "going-back"))))
              (reset! touch-x nil)
              (reset! touch-y nil))))
        #js {:passive true}))
    {:go! go! :nav-state state}))

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

(defn- pres-toggle-section-mode! []
  "Toggle section highlight/navigate mode — keeps section bar visible, emphasizes navigation."
  (when-let [{:keys [overlay]} @pres-state]
    (if (.contains (.-classList overlay) "pres-overlay--section-mode")
      (do (.remove (.-classList overlay) "pres-overlay--section-mode")
          (swap! pres-state assoc :section-mode? false))
      (do (.add (.-classList overlay) "pres-overlay--section-mode")
          (swap! pres-state assoc :section-mode? true)))))

(defn- pres-toggle-maximize! []
  "Toggle current slide to fill entire window (like portada)."
  (when-let [{:keys [overlay max-btn]} @pres-state]
    (let [maximized? (.contains (.-classList overlay) "pres-overlay--slide-maximized")]
      (if maximized?
        (do (.remove (.-classList overlay) "pres-overlay--slide-maximized")
            (swap! pres-state assoc :slide-maximized? false)
            (when max-btn
              (doto max-btn
                (.setAttribute "title" (i18n/t :maximize-slide))
                (.setAttribute "aria-label" (i18n/t :maximize-slide)))
              (when-let [ic (.querySelector max-btn ".icon")]
                (.setAttribute ic "data-lucide" "square"))
              (when (and js/lucide (.-createIcons js/lucide))
                (.createIcons js/lucide #js {:root max-btn}))))
        (do (.add (.-classList overlay) "pres-overlay--slide-maximized")
            (swap! pres-state assoc :slide-maximized? true)
            (when max-btn
              (doto max-btn
                (.setAttribute "title" (i18n/t :restore-slide))
                (.setAttribute "aria-label" (i18n/t :restore-slide)))
              (when-let [ic (.querySelector max-btn ".icon")]
                (.setAttribute ic "data-lucide" "minimize-2"))
              (when (and js/lucide (.-createIcons js/lucide))
                (.createIcons js/lucide #js {:root max-btn}))))))))

(defn- pres-apply-view! []
  (when-let [overlay (:overlay @pres-state)]
    (let [zoom (or (:zoom @pres-state) 1.0)
          tscale (or (:text-scale @pres-state) 1.0)
          pan-x (or (:pan-x @pres-state) 0)
          pan-y (or (:pan-y @pres-state) 0)]
      (.setProperty (.-style overlay) "--pres-zoom" (str zoom))
      (.setProperty (.-style overlay) "--pres-text-scale" (str tscale))
      (.setProperty (.-style overlay) "--pres-pan-x" (str pan-x "px"))
      (.setProperty (.-style overlay) "--pres-pan-y" (str pan-y "px")))))

(defn- pres-save-session! [idx]
  "Persist presentation slide index and zoom to sessionStorage."
  (.setItem js/sessionStorage "greb-pres-idx" (str idx))
  (when-let [z (:zoom @pres-state)]
    (.setItem js/sessionStorage "greb-pres-zoom" (str z)))
  (when-let [ts (:text-scale @pres-state)]
    (.setItem js/sessionStorage "greb-pres-tscale" (str ts))))

(defn- pres-clear-session! []
  (.removeItem js/sessionStorage "greb-pres-idx")
  (.removeItem js/sessionStorage "greb-pres-zoom")
  (.removeItem js/sessionStorage "greb-pres-tscale"))

(defn- pres-restore-session []
  "Returns saved slide index or nil."
  (when-let [v (.getItem js/sessionStorage "greb-pres-idx")]
    (js/parseInt v 10)))

(defn- pres-restore-zoom []
  "Returns saved zoom level or nil."
  (when-let [v (.getItem js/sessionStorage "greb-pres-zoom")]
    (js/parseFloat v)))

(defn- pres-restore-text-scale []
  (when-let [v (.getItem js/sessionStorage "greb-pres-tscale")]
    (js/parseFloat v)))

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
        ;; Reset pan + velocity on slide change
        (swap! pres-state assoc :pan-x 0 :pan-y 0)
        (when-let [^js p (:phy @pres-state)]
          (set! (.-vx p) 0) (set! (.-vy p) 0)
          (set! (.-zv p) 0) (set! (.-wx p) 0) (set! (.-wy p) 0)
          (set! (.-spx p) 0) (set! (.-spy p) 0)
          (set! (.-svx p) 0) (set! (.-svy p) 0))
        (pres-apply-view!)
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

(def ^:private block-container-selectors
  ".page-body, .toc-grid, .pres-grouped, .mission-grid, .wash-grid, .mini-info-grid, .stat-grid, .product-grid, .rq-causes, .gd-cols, .duo-grid, .intro-layout")

(defn- add-block-animations! [root]
  "Add .animate and staggered d1-d4 to block-level children for offset transition."
  (doseq [container (array-seq (.querySelectorAll root block-container-selectors))]
    (doseq [[i child] (map-indexed vector (array-seq (.-children container)))]
      (when (.-classList child)
        (when-not (.contains (.-classList child) "animate")
          (.add (.-classList child) "animate"))
        (when-not (some #(.contains (.-classList child) %) ["d1" "d2" "d3" "d4"])
          (.add (.-classList child) (str "d" (inc (mod i 4)))))))))

(defn- sparse-content? [el]
  "Returns true if the element has very little text content (single sentence)."
  (let [text (.-textContent el)
        len  (count (.trim text))]
    (and (pos? len) (< len 200))))

(defn- make-slide [content-el full? theme-classes page-bg bg-img-src]
  "Wrap a cloned element in the pres-slide > pres-slide-inner structure."
  (when (not full?) (add-block-animations! content-el))
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

;; ── Shortcuts help overlay ──────────────────────────────────
(def ^:private pres-shortcuts
  [["n / Space / →" "Next slide"]
   ["p / ←"         "Previous slide"]
   ["h / j / k / l" "Pan left / down / up / right"]
   ["a / s"         "Slower / faster pan"]
   ["u / + / ="     "Zoom in"]
   ["m / -"         "Zoom out"]
   ["]"             "Text bigger"]
   ["["             "Text smaller"]
   ["r"             "Recenter view"]
   ["0"             "Reset all"]
   ["z"             "Toggle spotlight"]
   ["i"             "Toggle index"]
   ["f"             "Toggle fullscreen"]
   ["q / Esc"       "Exit presentation"]
   ["?"             "Show / hide shortcuts"]])

(def ^:private doc-shortcuts
  [["n / → / Space"  "Next page"]
   ["p / ←"          "Previous page"]
   ["h / j / k / l"  "Pan left / down / up / right"]
   ["a / s"          "Slower / faster pan"]
   ["u / + / ="      "Zoom in"]
   ["m / -"          "Zoom out"]
   ["]"              "Text bigger"]
   ["["              "Text smaller"]
   ["r"              "Recenter view"]
   ["0"              "Reset all"]
   ["i"              "Toggle index"]
   ["o"              "Enter presentation"]
   ["?"              "Show / hide shortcuts"]])

(defn- build-shortcuts-panel [shortcuts]
  (let [rows (map (fn [[key desc]]
                    (d/el :div {:class "shortcuts-row"}
                          (d/el :kbd {:class "shortcuts-key"} key)
                          (d/el :span {:class "shortcuts-desc"} desc)))
                  shortcuts)]
    (apply d/el :div {:class "shortcuts-panel"}
           (d/el :div {:class "shortcuts-title"} "Keyboard Shortcuts")
           rows)))

(defonce ^:private shortcuts-overlay (atom nil))

(defn- toggle-shortcuts! [shortcuts]
  (if-let [el @shortcuts-overlay]
    (do (when (.-parentNode el) (.remove el))
        (reset! shortcuts-overlay nil))
    (let [scrim (d/el :div {:class "shortcuts-scrim"})
          panel (build-shortcuts-panel shortcuts)]
      (.addEventListener scrim "click" #(toggle-shortcuts! shortcuts))
      (.appendChild scrim panel)
      (.appendChild (.-body js/document) scrim)
      (reset! shortcuts-overlay scrim))))

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
          max-btn     (doto (d/el :button {:class "pres-toolbar-btn"
                                          :title (i18n/t :maximize-slide)
                                          :aria-label (i18n/t :maximize-slide)}
                                  (d/ic "square" ""))
                        (.addEventListener "click" #(pres-toggle-maximize!)))
          sec-btn     (doto (d/el :button {:class "pres-toolbar-btn"
                                          :title (i18n/t :section-mode)
                                          :aria-label (i18n/t :section-mode)}
                                  (d/ic "layers" ""))
                        (.addEventListener "click" #(pres-toggle-section-mode!)))
          exit-btn    (doto (d/el :button {:class "pres-toolbar-btn" :aria-label (i18n/t :close)}
                                  (d/ic "x" ""))
                            (.addEventListener "click" #(exit-presentation!)))
          ;; ── Zoom controls ──
          zoom-label  (d/el :span {:class "pres-zoom-label"} "100%")
          zoom-slider (doto (d/el :input {:type "range" :class "pres-zoom-slider"
                                          :min "25" :max "500" :step "5" :value "100"})
                        (.addEventListener "input"
                          (fn [e]
                            (let [v (/ (js/parseFloat (.. e -target -value)) 100)]
                              (swap! pres-state assoc :zoom v)
                              (set! (.-textContent zoom-label) (str (js/Math.round (* v 100)) "%"))
                              (pres-apply-view!)))))
          set-zoom!   (fn [z]
                        (let [z (max 0.25 (min 5.0 z))]
                          (swap! pres-state assoc :zoom z)
                          (set! (.-value zoom-slider) (str (js/Math.round (* z 100))))
                          (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))
                          (pres-apply-view!)))
          zoom-out-btn (doto (d/el :button {:class "pres-toolbar-btn pres-zoom-btn"
                                            :title (i18n/t :zoom-out)}
                                   (d/ic "minus" ""))
                         (.addEventListener "click"
                           (fn [] (set-zoom! (- (or (:zoom @pres-state) 1.0) 0.1)))))
          zoom-in-btn  (doto (d/el :button {:class "pres-toolbar-btn pres-zoom-btn"
                                            :title (i18n/t :zoom-in)}
                                   (d/ic "plus" ""))
                         (.addEventListener "click"
                           (fn [] (set-zoom! (+ (or (:zoom @pres-state) 1.0) 0.1)))))
          zoom-controls (d/el :div {:class "pres-zoom-controls"}
                              zoom-out-btn zoom-slider zoom-label zoom-in-btn)
          ;; ── Text scale controls ──
          ts-label    (d/el :span {:class "pres-zoom-label"} "A 100%")
          ts-slider   (doto (d/el :input {:type "range" :class "pres-zoom-slider"
                                          :min "50" :max "500" :step "5" :value "100"})
                        (.addEventListener "input"
                          (fn [e]
                            (let [v (/ (js/parseFloat (.. e -target -value)) 100)]
                              (swap! pres-state assoc :text-scale v)
                              (set! (.-textContent ts-label) (str "A " (js/Math.round (* v 100)) "%"))
                              (pres-apply-view!)))))
          set-text-scale! (fn [s]
                            (let [s (max 0.5 (min 5.0 s))]
                              (swap! pres-state assoc :text-scale s)
                              (set! (.-value ts-slider) (str (js/Math.round (* s 100))))
                              (set! (.-textContent ts-label) (str "A " (js/Math.round (* s 100)) "%"))
                              (pres-apply-view!)))
          ts-down-btn (doto (d/el :button {:class "pres-toolbar-btn pres-zoom-btn"
                                           :title "Smaller text"}
                                  (d/el :span {:class "pres-ts-icon pres-ts-icon--sm"} "A"))
                        (.addEventListener "click"
                          (fn [] (set-text-scale! (- (or (:text-scale @pres-state) 1.0) 0.1)))))
          ts-up-btn   (doto (d/el :button {:class "pres-toolbar-btn pres-zoom-btn"
                                           :title "Bigger text"}
                                  (d/el :span {:class "pres-ts-icon pres-ts-icon--lg"} "A"))
                        (.addEventListener "click"
                          (fn [] (set-text-scale! (+ (or (:text-scale @pres-state) 1.0) 0.1)))))
          ts-controls (d/el :div {:class "pres-zoom-controls"} ts-down-btn ts-slider ts-label ts-up-btn)
          toolbar-el  (d/el :div {:class "pres-toolbar"} idx-btn zoom-controls ts-controls fs-btn max-btn sec-btn exit-btn)
          ;; ── Highlight cursor ──
          hl-cursor   (d/el :div {:class "pres-highlight-cursor"})
          on-mouse    (fn [e]
                        (.setProperty (.-style hl-cursor) "--hl-x" (str (.-clientX e) "px"))
                        (.setProperty (.-style hl-cursor) "--hl-y" (str (.-clientY e) "px")))
          ;; ── Touch pan for presentation mode (mobile) ─────────────
          touch-pan   (atom nil)   ;; {:x :y :t} on start
          on-touch-start (fn [e]
                           (when (= (.-touches.length e) 1)
                             (let [t (aget (.-touches e) 0)]
                               (reset! touch-pan {:x (.-clientX t) :y (.-clientY t) :moved false}))))
          on-touch-move  (fn [e]
                           (.preventDefault e)
                           (when-let [p @touch-pan]
                             (when (= (.-touches.length e) 1)
                               (let [t   (aget (.-touches e) 0)
                                     dx  (- (.-clientX t) (:x p))
                                     dy  (- (.-clientY t) (:y p))
                                     spd 1.8]
                                 ;; Feed into wheel-style impulse for physics engine
                                 (set! (.-wx phy) (+ (.-wx phy) (* dx spd)))
                                 (set! (.-wy phy) (+ (.-wy phy) (* dy spd)))
                                 (ensure-raf!)
                                 (reset! touch-pan {:x (.-clientX t) :y (.-clientY t) :moved true})))))
          on-touch-end   (fn [e]
                           (reset! touch-pan nil))
          ;; ── Game-engine style smooth pan + zoom (delta-time based) ──
          held-keys      (atom #{})
          ;; Mutable physics state in a single JS object for zero GC pressure
          ^js phy        #js {:vx 0 :vy 0 :zv 0 :wx 0 :wy 0 :last 0
                              :kx 0 :ky 0         ;; key-active flags (1/0) for release detection
                              :spx 0 :spy 0       ;; spring offset position
                              :svx 0 :svy 0}      ;; spring offset velocity
          pan-speed      (atom 700)    ;; px/sec
          zoom-speed     2.0           ;; zoom units/sec
          ;; Release half-life (glide feel when you let go)
          release        0.16          ;; 160ms — smooth glide
          zoom-release   0.18
          ;; Spring overshoot params (underdamped)
          sp-stiff       400           ;; spring stiffness — higher = faster settle
          sp-damp        18            ;; damping — lower = more bouncy
          sp-impulse     0.045         ;; impulse factor on release (subtle)
          wheel-half     0.30          ;; 300ms wheel momentum
          snap-v         0.5
          snap-z         0.001
          snap-w         0.5
          pan-raf        (atom nil)
          pres-wheel-vel (atom nil)    ;; only for teardown reset
          pan-tick       (fn pan-tick [now]
                           (let [prev  (.-last phy)
                                 raw   (if (pos? prev) (- now prev) 16.67)
                                 dt    (/ (min raw 50) 1000.0)  ;; seconds, capped at 50ms
                                 _     (set! (.-last phy) now)
                                 spd   @pan-speed
                                 keys  @held-keys
                                 ;; Target velocity from held keys (px/sec)
                                 tx    (* (+ (if (contains? keys "h") 1 0)
                                             (if (contains? keys "l") -1 0)) spd)
                                 ty    (* (+ (if (contains? keys "k") 1 0)
                                             (if (contains? keys "j") -1 0)) spd)
                                 tz    (* (+ (if (contains? keys "u") 1 0)
                                             (if (contains? keys "m") -1 0)) zoom-speed)
                                 ;; Instant attack, smooth release: key held = full speed NOW
                                 decay  (js/Math.exp (/ (- dt) release))
                                 zdecay (js/Math.exp (/ (- dt) zoom-release))
                                 old-vx (.-vx phy)
                                 old-vy (.-vy phy)
                                 vx     (if (not= tx 0) tx (* old-vx decay))
                                 vy     (if (not= ty 0) ty (* old-vy decay))
                                 zv     (if (not= tz 0) tz (* (.-zv phy) zdecay))
                                 ;; Snap to zero
                                 vx     (if (< (js/Math.abs vx) snap-v) 0 vx)
                                 vy     (if (< (js/Math.abs vy) snap-v) 0 vy)
                                 zv     (if (< (js/Math.abs zv) snap-z) 0 zv)
                                 ;; Detect key release transition → fire spring impulse
                                 _      (when (and (== tx 0) (== 1 (.-kx phy)))
                                          (set! (.-svx phy) (+ (.-svx phy) (* old-vx sp-impulse))))
                                 _      (when (and (== ty 0) (== 1 (.-ky phy)))
                                          (set! (.-svy phy) (+ (.-svy phy) (* old-vy sp-impulse))))
                                 _      (set! (.-kx phy) (if (not= tx 0) 1 0))
                                 _      (set! (.-ky phy) (if (not= ty 0) 1 0))
                                 ;; Spring overshoot physics (underdamped harmonic oscillator)
                                 sp-ax  (- (- (* sp-stiff (.-spx phy))) (* sp-damp (.-svx phy)))
                                 sp-ay  (- (- (* sp-stiff (.-spy phy))) (* sp-damp (.-svy phy)))
                                 n-svx  (+ (.-svx phy) (* sp-ax dt))
                                 n-svy  (+ (.-svy phy) (* sp-ay dt))
                                 n-spx  (+ (.-spx phy) (* n-svx dt))
                                 n-spy  (+ (.-spy phy) (* n-svy dt))
                                 ;; Snap spring to zero when settled
                                 n-spx  (if (and (< (js/Math.abs n-spx) 0.1) (< (js/Math.abs n-svx) 0.5)) 0 n-spx)
                                 n-spy  (if (and (< (js/Math.abs n-spy) 0.1) (< (js/Math.abs n-svy) 0.5)) 0 n-spy)
                                 n-svx  (if (== n-spx 0) 0 n-svx)
                                 n-svy  (if (== n-spy 0) 0 n-svy)
                                 ;; Reset spring when actively moving
                                 n-spx  (if (not= tx 0) 0 n-spx)
                                 n-spy  (if (not= ty 0) 0 n-spy)
                                 n-svx  (if (not= tx 0) 0 n-svx)
                                 n-svy  (if (not= ty 0) 0 n-svy)
                                 ;; Wheel momentum — exponential decay
                                 wfac   (js/Math.exp (/ (- dt) wheel-half))
                                 wx     (* (.-wx phy) wfac)
                                 wy     (* (.-wy phy) wfac)
                                 wx     (if (< (js/Math.abs wx) snap-w) 0 wx)
                                 wy     (if (< (js/Math.abs wy) snap-w) 0 wy)
                                 ;; Displacement = base velocity + spring offset + wheel
                                 dx     (+ (* vx dt) (- n-spx (.-spx phy)) (* wx dt))
                                 dy     (+ (* vy dt) (- n-spy (.-spy phy)) (* wy dt))
                                 dz     (* zv dt)
                                 ;; Anything alive?
                                 alive  (or (not= vx 0) (not= vy 0) (not= zv 0)
                                            (not= wx 0) (not= wy 0)
                                            (not= n-spx 0) (not= n-spy 0))]
                             ;; Write back to mutable state
                             (set! (.-vx phy) vx)
                             (set! (.-vy phy) vy)
                             (set! (.-zv phy) zv)
                             (set! (.-wx phy) wx)
                             (set! (.-wy phy) wy)
                             (set! (.-spx phy) n-spx)
                             (set! (.-spy phy) n-spy)
                             (set! (.-svx phy) n-svx)
                             (set! (.-svy phy) n-svy)
                             ;; Apply position + zoom in one batch swap
                             (let [moved (or (not= dx 0) (not= dy 0) (not= dz 0))]
                               (when moved
                                 (swap! pres-state
                                   (fn [s]
                                     (let [px (+ (or (:pan-x s) 0) dx)
                                           py (+ (or (:pan-y s) 0) dy)
                                           z  (max 0.25 (min 5.0 (+ (or (:zoom s) 1.0) dz)))]
                                       (assoc s :pan-x px :pan-y py :zoom z))))
                                 (when (not= dz 0)
                                   (let [z (or (:zoom @pres-state) 1.0)]
                                     (set! (.-value zoom-slider) (str (js/Math.round (* z 100))))
                                     (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))))
                                 (pres-apply-view!))
                               ;; Keep looping while anything is moving
                               (if (or (seq keys) alive)
                                 (reset! pan-raf (js/requestAnimationFrame pan-tick))
                                 (do (set! (.-last phy) 0)
                                     (reset! pan-raf nil))))))
          ensure-raf!    (fn [] (when-not @pan-raf
                                  (set! (.-last phy) 0)
                                  (reset! pan-raf (js/requestAnimationFrame pan-tick))))
          on-key      (fn [e]
                        (let [k (.-key e)]
                          (if (#{"h" "j" "k" "l" "u" "m"} k)
                            ;; Continuous keys — track held state for RAF loop
                            (do (.preventDefault e) (.stopPropagation e)
                                (when-not (contains? @held-keys k)
                                  (swap! held-keys conj k)
                                  (ensure-raf!)))
                            (case k
                              ("ArrowRight" "ArrowDown" " " "n")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (pres-show-page! (inc (:current @pres-state)) nil))
                              ("ArrowLeft" "ArrowUp" "p")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (pres-show-page! (dec (:current @pres-state)) :back))
                              ("Escape" "q")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (exit-presentation!))
                              ("+" "=")
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-zoom! (+ (or (:zoom @pres-state) 1.0) 0.1)))
                              "-"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-zoom! (- (or (:zoom @pres-state) 1.0) 0.1)))
                              "a"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (swap! pan-speed #(max 150 (- % 150))))
                              "s"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (swap! pan-speed #(min 2000 (+ % 150))))
                              "0"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-zoom! 1.0)
                                  (set-text-scale! 1.0)
                                  (reset! pan-speed 700)
                                  (swap! pres-state assoc :pan-x 0 :pan-y 0)
                                  (set! (.-vx phy) 0) (set! (.-vy phy) 0)
                                  (set! (.-zv phy) 0) (set! (.-wx phy) 0) (set! (.-wy phy) 0)
                                  (set! (.-spx phy) 0) (set! (.-spy phy) 0)
                                  (set! (.-svx phy) 0) (set! (.-svy phy) 0)
                                  (pres-apply-view!))
                              "]"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-text-scale! (+ (or (:text-scale @pres-state) 1.0) 0.1)))
                              "["
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-text-scale! (- (or (:text-scale @pres-state) 1.0) 0.1)))
                              "r"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (set-zoom! 1.0)
                                  (set-text-scale! 1.0)
                                  (swap! pres-state assoc :pan-x 0 :pan-y 0)
                                  (set! (.-vx phy) 0) (set! (.-vy phy) 0)
                                  (set! (.-zv phy) 0) (set! (.-wx phy) 0) (set! (.-wy phy) 0)
                                  (set! (.-spx phy) 0) (set! (.-spy phy) 0)
                                  (set! (.-svx phy) 0) (set! (.-svy phy) 0)
                                  (pres-apply-view!))
                              "z"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (.toggle (.-classList hl-cursor) "pres-highlight-cursor--active"))
                              "?"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (toggle-shortcuts! pres-shortcuts))
                              "i"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (toggle!))
                              "f"
                              (do (.preventDefault e) (.stopPropagation e)
                                  (pres-toggle-fullscreen! overlay))
                              nil))))
          on-keyup    (fn [e]
                        (let [k (.-key e)]
                          (when (#{"h" "j" "k" "l" "u" "m"} k)
                            (swap! held-keys disj k))))
          on-wheel    (fn [e]
                        (.preventDefault e)
                        (let [dx (.-deltaX e)
                              dy (.-deltaY e)
                              ;; Convert wheel deltas to px/sec impulse
                              ;; Trackpad gives small frequent deltas; mouse gives large infrequent ones
                              impulse 12]
                          (set! (.-wx phy) (- (.-wx phy) (* dx impulse)))
                          (set! (.-wy phy) (- (.-wy phy) (* dy impulse)))
                          (ensure-raf!)))
          on-fschange (fn []
                        (when (and (nil? (.-fullscreenElement js/document))
                                   (nil? (.-webkitFullscreenElement js/document)))
                          (pres-apply-view!)))
          on-resize   (fn [] (pres-apply-view!))]
      (.appendChild overlay panel)
      (.appendChild overlay scrim)
      (.appendChild overlay toolbar-el)
      (.appendChild overlay section-bar)
      (.appendChild overlay hl-cursor)
      ;; Store state
      (reset! pres-state {:overlay overlay :viewport viewport :indicator indicator
                          :slides slide-els :page-ids page-ids :n n :current -1
                          :slide->section slide->section :entry-els entry-els
                          :section-dots section-dots :current-section nil
                          :zoom (or (pres-restore-zoom) 1.0)
                          :text-scale (or (pres-restore-text-scale) 1.0)
                          :fs-btn fs-btn :max-btn max-btn
                          :on-key on-key :on-keyup on-keyup :on-mouse on-mouse :on-wheel on-wheel
                          :on-touch-start on-touch-start :on-touch-move on-touch-move :on-touch-end on-touch-end
                          :held-keys held-keys :phy phy :pan-raf pan-raf
                          :on-fschange on-fschange :on-resize on-resize})
      ;; Sync zoom & text-scale UI with restored values
      (let [z (or (:zoom @pres-state) 1.0)
            ts (or (:text-scale @pres-state) 1.0)]
        (set! (.-value zoom-slider) (str (js/Math.round (* z 100))))
        (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))
        (set! (.-value ts-slider) (str (js/Math.round (* ts 100))))
        (set! (.-textContent ts-label) (str "A " (js/Math.round (* ts 100)) "%")))
      ;; Compute scale
      (pres-apply-view!)
      ;; Add presenting class (hides document)
      (.add (.-classList (.-documentElement js/document)) "presenting")
      ;; Register listeners
      (.addEventListener js/document "keydown" on-key true)
      (.addEventListener js/document "keyup" on-keyup true)
      (.addEventListener js/document "fullscreenchange" on-fschange)
      (.addEventListener js/document "webkitfullscreenchange" on-fschange)
      (.addEventListener js/window "resize" on-resize)
      (.addEventListener js/window "mousemove" on-mouse)
      (.addEventListener js/document "wheel" on-wheel #js {:passive false})
      (.addEventListener overlay "touchstart" on-touch-start #js {:passive true})
      (.addEventListener overlay "touchmove"  on-touch-move  #js {:passive false})
      (.addEventListener overlay "touchend"   on-touch-end   #js {:passive true})
      ;; Show starting slide — use same path as navigation so it looks like the rest
      (pres-show-page! start nil)
      ;; Tag slide landmarks for View Transition matching
      (tag-vt! (nth slide-els start))
      ;; Re-render lucide icons
      (when (and js/lucide (.-createIcons js/lucide))
        (.createIcons js/lucide #js {:root overlay}))
      ;; Return overlay for fullscreen
      overlay)))

(defn- teardown-presentation!
  "Core teardown: remove listeners, clear state. Returns [overlay target-page-id]."
  []
  (when-let [{:keys [overlay on-key on-keyup on-mouse on-wheel on-fschange on-resize
                     on-touch-start on-touch-move on-touch-end
                     held-keys ^js phy pan-raf page-ids current slides]} @pres-state]
    (let [target-page-id (nth page-ids (or current 0) nil)
          active-slide   (nth slides (or current 0) nil)]
      ;; Cancel pan animation
      (when-let [raf @pan-raf] (js/cancelAnimationFrame raf))
      (when held-keys (reset! held-keys #{}))
      ;; Remove listeners
      (.removeEventListener js/document "keydown" on-key true)
      (.removeEventListener js/document "keyup" on-keyup true)
      (.removeEventListener js/document "fullscreenchange" on-fschange)
      (.removeEventListener js/document "webkitfullscreenchange" on-fschange)
      (.removeEventListener js/window "resize" on-resize)
      (.removeEventListener js/window "mousemove" on-mouse)
      (when on-wheel (.removeEventListener js/document "wheel" on-wheel))
      (when on-touch-start (.removeEventListener overlay "touchstart" on-touch-start))
      (when on-touch-move  (.removeEventListener overlay "touchmove"  on-touch-move))
      (when on-touch-end   (.removeEventListener overlay "touchend"   on-touch-end))
      (when phy
        (set! (.-vx phy) 0) (set! (.-vy phy) 0)
        (set! (.-zv phy) 0) (set! (.-wx phy) 0) (set! (.-wy phy) 0))
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
        mobile?   (mobile-layout?)
        print-btn (doto (d/el :button {:class (str "toolbar-btn" (when mobile? " toolbar-desktop-only"))}
                              (d/ic "printer" "") (i18n/t :print))
                        (.addEventListener "click" #(.print js/window)))
        idx-btn   (doto (d/el :button {:class "toolbar-ghost-btn"}
                              (d/ic "list" "") (i18n/t :index))
                        (.addEventListener "click" toggle-toc!))
        pres-btn  (doto (d/el :button {:class (str "toolbar-ghost-btn" (when mobile? " toolbar-desktop-only"))}
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
        {:keys [go! nav-state]} (build-navigator spreads spread-ids dots indicator prev-btn next-btn init-idx)
        {:keys [overlay panel toggle!]} (build-toc-panel go! id->spread toc-groups)
        _          (reset! current-nav {:go! go! :nav-state nav-state :id->spread id->spread :spread-ids spread-ids
                                        :toggle-toc! toggle!
                                        :spread->pages (into {} (map-indexed
                                                        (fn [si group]
                                                          [si (mapv #(.-id %) (filter some? group))])
                                                        groups))})]
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
      ;; Document-mode: game-engine delta-time physics (matches pres mode)
      (let [doc-held      (atom #{})
            ^js doc-phy   #js {:vx 0 :vy 0 :zv 0 :wx 0 :wy 0 :last 0
                               :kx 0 :ky 0 :spx 0 :spy 0 :svx 0 :svy 0}
            doc-pan-raf   (atom nil)
            doc-pan-speed (atom 700)
            doc-zoom-spd  2.0
            doc-release   0.16
            doc-zr        0.18
            doc-wh        0.30
            doc-snap-v    0.5
            doc-snap-z    0.001
            doc-snap-w    0.5
            dsp-stiff     400
            dsp-damp      18
            dsp-impulse   0.045
            doc-tick     (fn doc-tick [now]
                           (let [prev   (.-last doc-phy)
                                 raw    (if (pos? prev) (- now prev) 16.67)
                                 dt     (/ (min raw 50) 1000.0)
                                 _      (set! (.-last doc-phy) now)
                                 spd    @doc-pan-speed
                                 keys   @doc-held
                                 tx     (* (+ (if (contains? keys "h") 1 0)
                                              (if (contains? keys "l") -1 0)) spd)
                                 ty     (* (+ (if (contains? keys "k") 1 0)
                                              (if (contains? keys "j") -1 0)) spd)
                                 tz     (* (+ (if (contains? keys "u") 1 0)
                                              (if (contains? keys "m") -1 0)) doc-zoom-spd)
                                 decay  (js/Math.exp (/ (- dt) doc-release))
                                 zdecay (js/Math.exp (/ (- dt) doc-zr))
                                 old-vx (.-vx doc-phy)
                                 old-vy (.-vy doc-phy)
                                 vx     (if (not= tx 0) tx (* old-vx decay))
                                 vy     (if (not= ty 0) ty (* old-vy decay))
                                 zv     (if (not= tz 0) tz (* (.-zv doc-phy) zdecay))
                                 vx     (if (< (js/Math.abs vx) doc-snap-v) 0 vx)
                                 vy     (if (< (js/Math.abs vy) doc-snap-v) 0 vy)
                                 zv     (if (< (js/Math.abs zv) doc-snap-z) 0 zv)
                                 ;; Release detection → spring impulse
                                 _      (when (and (== tx 0) (== 1 (.-kx doc-phy)))
                                          (set! (.-svx doc-phy) (+ (.-svx doc-phy) (* old-vx dsp-impulse))))
                                 _      (when (and (== ty 0) (== 1 (.-ky doc-phy)))
                                          (set! (.-svy doc-phy) (+ (.-svy doc-phy) (* old-vy dsp-impulse))))
                                 _      (set! (.-kx doc-phy) (if (not= tx 0) 1 0))
                                 _      (set! (.-ky doc-phy) (if (not= ty 0) 1 0))
                                 ;; Spring overshoot
                                 sp-ax  (- (- (* dsp-stiff (.-spx doc-phy))) (* dsp-damp (.-svx doc-phy)))
                                 sp-ay  (- (- (* dsp-stiff (.-spy doc-phy))) (* dsp-damp (.-svy doc-phy)))
                                 n-svx  (+ (.-svx doc-phy) (* sp-ax dt))
                                 n-svy  (+ (.-svy doc-phy) (* sp-ay dt))
                                 n-spx  (+ (.-spx doc-phy) (* n-svx dt))
                                 n-spy  (+ (.-spy doc-phy) (* n-svy dt))
                                 n-spx  (if (and (< (js/Math.abs n-spx) 0.1) (< (js/Math.abs n-svx) 0.5)) 0 n-spx)
                                 n-spy  (if (and (< (js/Math.abs n-spy) 0.1) (< (js/Math.abs n-svy) 0.5)) 0 n-spy)
                                 n-svx  (if (== n-spx 0) 0 n-svx)
                                 n-svy  (if (== n-spy 0) 0 n-svy)
                                 n-spx  (if (not= tx 0) 0 n-spx)
                                 n-spy  (if (not= ty 0) 0 n-spy)
                                 n-svx  (if (not= tx 0) 0 n-svx)
                                 n-svy  (if (not= ty 0) 0 n-svy)
                                 ;; Wheel
                                 wfac   (js/Math.exp (/ (- dt) doc-wh))
                                 wx     (* (.-wx doc-phy) wfac)
                                 wy     (* (.-wy doc-phy) wfac)
                                 wx     (if (< (js/Math.abs wx) doc-snap-w) 0 wx)
                                 wy     (if (< (js/Math.abs wy) doc-snap-w) 0 wy)
                                 ;; Displacement
                                 dx     (+ (* vx dt) (- n-spx (.-spx doc-phy)) (* wx dt))
                                 dy     (+ (* vy dt) (- n-spy (.-spy doc-phy)) (* wy dt))
                                 dz     (* zv dt)
                                 alive  (or (not= vx 0) (not= vy 0) (not= zv 0)
                                            (not= wx 0) (not= wy 0)
                                            (not= n-spx 0) (not= n-spy 0))]
                             (set! (.-vx doc-phy) vx) (set! (.-vy doc-phy) vy)
                             (set! (.-zv doc-phy) zv) (set! (.-wx doc-phy) wx) (set! (.-wy doc-phy) wy)
                             (set! (.-spx doc-phy) n-spx) (set! (.-spy doc-phy) n-spy)
                             (set! (.-svx doc-phy) n-svx) (set! (.-svy doc-phy) n-svy)
                             (let [moved (or (not= dx 0) (not= dy 0) (not= dz 0))]
                               (when moved
                                 (swap! doc-view
                                   (fn [dv]
                                     (let [px (+ (or (:pan-x dv) 0) dx)
                                           py (+ (or (:pan-y dv) 0) dy)
                                           z  (max 0.25 (min 5.0 (+ (or (:zoom dv) 1.0) dz)))]
                                       (assoc dv :pan-x px :pan-y py :zoom z))))
                                 (doc-apply-view!))
                               (if (or (seq keys) alive)
                                 (reset! doc-pan-raf (js/requestAnimationFrame doc-tick))
                                 (do (set! (.-last doc-phy) 0)
                                     (reset! doc-pan-raf nil))))))
            ensure-doc-raf! (fn [] (when-not @doc-pan-raf
                                     (set! (.-last doc-phy) 0)
                                     (reset! doc-pan-raf (js/requestAnimationFrame doc-tick))))]
        (.addEventListener js/document "keydown"
          (fn [e]
            (when-not @pres-state
              (when (not (.closest (.-target e) "input, textarea, select"))
                (let [k (.-key e)]
                  (if (#{"h" "j" "k" "l" "u" "m"} k)
                    (do (.preventDefault e)
                        (when-not (contains? @doc-held k)
                          (swap! doc-held conj k)
                          (ensure-doc-raf!)))
                    (let [dv @doc-view
                          z  (or (:zoom dv) 1.0)
                          ts (or (:text-scale dv) 1.0)]
                      (case k
                        ("+" "=")
                        (do (.preventDefault e)
                            (swap! doc-view assoc :zoom (min 5.0 (+ z 0.1)))
                            (doc-apply-view!))
                        "-"
                        (do (.preventDefault e)
                            (swap! doc-view assoc :zoom (max 0.25 (- z 0.1)))
                            (doc-apply-view!))
                        "]"
                        (do (.preventDefault e)
                            (swap! doc-view assoc :text-scale (min 5.0 (+ ts 0.1)))
                            (doc-apply-view!))
                        "["
                        (do (.preventDefault e)
                            (swap! doc-view assoc :text-scale (max 0.5 (- ts 0.1)))
                            (doc-apply-view!))
                        "a"
                        (do (.preventDefault e)
                            (swap! doc-pan-speed #(max 150 (- % 150))))
                        "s"
                        (do (.preventDefault e)
                            (swap! doc-pan-speed #(min 2000 (+ % 150))))
                        "0"
                        (do (.preventDefault e)
                            (reset! doc-view {:zoom 1.0 :text-scale 1.0 :pan-x 0 :pan-y 0})
                            (reset! doc-pan-speed 700)
                            (set! (.-vx doc-phy) 0) (set! (.-vy doc-phy) 0)
                            (set! (.-zv doc-phy) 0) (set! (.-wx doc-phy) 0) (set! (.-wy doc-phy) 0)
                            (set! (.-spx doc-phy) 0) (set! (.-spy doc-phy) 0)
                            (set! (.-svx doc-phy) 0) (set! (.-svy doc-phy) 0)
                            (doc-apply-view!))
                        "n"
                        (do (.preventDefault e)
                            (when-let [{:keys [go! nav-state]} @current-nav]
                              (go! (inc @nav-state) nil)))
                        "p"
                        (do (.preventDefault e)
                            (when-let [{:keys [go! nav-state]} @current-nav]
                              (go! (dec @nav-state) "going-back")))
                        "r"
                        (do (.preventDefault e)
                            (reset! doc-view {:zoom 1.0 :text-scale 1.0 :pan-x 0 :pan-y 0})
                            (set! (.-vx doc-phy) 0) (set! (.-vy doc-phy) 0)
                            (set! (.-zv doc-phy) 0) (set! (.-wx doc-phy) 0) (set! (.-wy doc-phy) 0)
                            (set! (.-spx doc-phy) 0) (set! (.-spy doc-phy) 0)
                            (set! (.-svx doc-phy) 0) (set! (.-svy doc-phy) 0)
                            (reset! doc-pan-speed 700)
                            (doc-apply-view!))
                        "i"
                        (do (.preventDefault e)
                            (when-let [tog (:toggle-toc! @current-nav)] (tog)))
                        "o"
                        (do (.preventDefault e) (enter-presentation!))
                        "?"
                        (do (.preventDefault e) (toggle-shortcuts! doc-shortcuts))
                        nil))))))))
        (.addEventListener js/document "keyup"
          (fn [e]
            (when-not @pres-state
              (let [k (.-key e)]
                (when (#{"h" "j" "k" "l" "u" "m"} k)
                  (swap! doc-held disj k))))))
        ;; Mouse wheel with momentum (iPhone-like scroll feel)
        (.addEventListener js/document "wheel"
          (fn [e]
            (when-not @pres-state
              (.preventDefault e)
              (let [dx (.-deltaX e)
                    dy (.-deltaY e)
                    impulse 12]
                (set! (.-wx doc-phy) (- (.-wx doc-phy) (* dx impulse)))
                (set! (.-wy doc-phy) (- (.-wy doc-phy) (* dy impulse)))
                (ensure-doc-raf!))))
          #js {:passive false}))
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
