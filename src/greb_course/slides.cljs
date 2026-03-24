(ns greb-course.slides
  "Slide extraction from pages and View Transition helpers."
  (:require [greb-course.dom :as d]))

(def ^:private block-container-selectors
  ".page-body, .toc-grid, .pres-grouped, .mission-grid, .wash-grid, .mini-info-grid, .stat-grid, .product-grid, .rq-causes, .gd-cols, .duo-grid, .intro-layout, .gn-pills, .gn-steps-list, .gn-toc")

(defn- full-page? [page]
  (let [cl (.-classList page)]
    (or (.contains cl "portada-page")
        (.contains cl "full-image-page")
        (.contains cl "gn-cover-page")
        (.contains cl "gn-backcover-page")
        (.contains cl "gn-divider-page")
        (.contains cl "gn-quote-page"))))

(defn- page-theme-classes [page]
  (let [all-classes (array-seq (.-classList page))]
    (filterv #(or (.endsWith % "-page") (.endsWith % "-red")
                  (.endsWith % "-blue") (.endsWith % "-green")
                  (.endsWith % "-yellow") (.endsWith % "-orange")
                  (.endsWith % "-enhanced") (.endsWith % "-dark")
                  (= % "rq-page"))
             all-classes)))

(defn- visible-el? [el]
  (let [style (js/getComputedStyle el)]
    (not (or (= "none" (.-display style))
             (= "hidden" (.-visibility style))
             (and (zero? (.-offsetWidth el))
                  (zero? (.-offsetHeight el)))))))

(defn- reset-animations! [root]
  (doseq [node (array-seq (.querySelectorAll root ".animate"))]
    (.remove (.-classList node) "visible"))
  (.remove (.-classList root) "visible"))

(defn- add-block-animations! [root]
  (doseq [container (array-seq (.querySelectorAll root block-container-selectors))]
    (doseq [[i child] (map-indexed vector (array-seq (.-children container)))]
      (when (.-classList child)
        (when-not (.contains (.-classList child) "animate")
          (.add (.-classList child) "animate"))
        (when-not (some #(.contains (.-classList child) %) ["d1" "d2" "d3" "d4"])
          (.add (.-classList child) (str "d" (inc (mod i 4)))))))))

(defn- sparse-content? [el]
  (let [text (.-textContent el)
        len  (count (.trim text))]
    (and (pos? len) (< len 200))))

(defn- make-slide [content-el full? theme-classes page-bg bg-img-src]
  (when (not full?) (add-block-animations! content-el))
  (let [is-kv? (key-visual? content-el)
        inner (d/el :div {:class "pres-slide-inner"})
        slide (d/el :div {:class (str "pres-slide"
                                      (when full? " pres-slide--full")
                                      (when is-kv? " pres-slide--key-visual"))})]
    (when bg-img-src
      (.add (.-classList slide) "pres-slide--has-bg")
      (.setProperty (.-style slide) "--slide-bg-img" (str "url(" bg-img-src ")")))
    (.appendChild inner content-el)
    (.appendChild slide inner)
    (when (and (not full?) (sparse-content? content-el))
      (.add (.-classList slide) "pres-slide--sparse"))
    (doseq [cls theme-classes]
      (.add (.-classList slide) cls))
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
  (let [cl (.-classList el)]
    (or (.contains cl "risk-page-body")
        (.contains cl "rq-body")
        (.contains cl "risk-micro-body")
        (.contains cl "risk-micro-cols")
        (.contains cl "gd-cols")
        (.contains cl "duo-grid")
        (and (= "DIV" (.-tagName el))
             (= 0 (.-length cl))
             (> (.-childElementCount el) 1)))))

(defn- skip-el? [el]
  (let [cl (.-classList el)]
    (or (.contains cl "page-footer")
        (.contains cl "gd-nav")
        (= "FOOTER" (.-tagName el)))))

(defn- find-body [page]
  (or (.querySelector page ".page-body")
      (.querySelector page ".risk-page-body")
      (.querySelector page ".portada-body")))

(defn- key-visual? [el]
  (and (.-classList el)
       (.contains (.-classList el) "key-visual")))

(defn- small-el? [el]
  (and (not (key-visual? el))
       (< (.-offsetHeight el) 350)))

(defn- group-small-children [children]
  (loop [remaining children
         result   []
         pending  []]
    (if (empty? remaining)
      (if (seq pending) (conj result pending) result)
      (let [child (first remaining)
            rest-children (rest remaining)]
        (if (small-el? child)
          (let [new-pending (conj pending child)
                total-h    (reduce + 0 (map #(.-offsetHeight %) new-pending))]
            (if (> total-h 600)
              (recur rest-children (conj result pending) [child])
              (recur rest-children result new-pending)))
          (let [result (if (seq pending) (conj result pending) result)]
            (recur rest-children (conj result [child]) [])))))))

(defn- extract-children-as-slides [parent classes bg bg-img-src page-id]
  (let [children (when parent (array-seq (.-children parent)))
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
        groups   (group-small-children flat)]
    (mapv (fn [group]
            {:page-id page-id
             :slide
             (if (= 1 (count group))
               (let [clone (.cloneNode (first group) true)]
                 (reset-animations! clone)
                 (make-slide clone false classes bg bg-img-src))
               (let [wrapper (d/el :div {:class "pres-grouped"})]
                 (doseq [el group]
                   (let [clone (.cloneNode el true)]
                     (reset-animations! clone)
                     (.appendChild wrapper clone)))
                 (make-slide wrapper false classes bg bg-img-src)))})
          groups)))

(defn- page-bg-img-src [page]
  (when-let [bg-el (.querySelector page ".page-bg-img img")]
    (.-src bg-el)))

(defn extract-slides []
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
            (full-page? page)
            (let [clone (.cloneNode page true)]
              (reset-animations! clone)
              (conj slides {:page-id pid :slide (make-slide clone true classes bg nil)}))

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

            :else
            (let [body (find-body page)]
              (if body
                (into slides (extract-children-as-slides body classes bg bg-img-src pid))
                (let [clone (.cloneNode page true)]
                  (reset-animations! clone)
                  (conj slides {:page-id pid :slide (make-slide clone true classes nil nil)})))))))
      [] source-pages)))

(defn slide-idx-for-page [page-ids page-id]
  (or (first (keep-indexed (fn [i pid] (when (= pid page-id) i)) page-ids))
      0))

;; ── View Transition helpers ──────────────────────────────────
(def ^:private vt-selectors
  [["h1, .hygiene-main-title, .risk-hero-title, .portada-title, .gd-title, .contenido-title, .gn-divider-title, .gn-title-bar"
    "vt-title"]
   [".hero-kicker, .gd-kicker, .rq-hero-sub, .portada-sub, .contenido-eyebrow, .gn-quote-author, .gn-divider-number"
    "vt-kicker"]
   ["img.hero-product-photo, img.rq-hero-img, img.portada-hero-img, img.gd-hero-diagram-img, .intro-img-stack img, .risk-hero-bola-wrap img, .full-image-bg img, .gn-divider-bg img, .gn-quote-bg img"
    "vt-hero-img"]])

(defn tag-vt! [root]
  (doseq [[sel vt-name] vt-selectors]
    (when-let [el (.querySelector root sel)]
      (.setProperty (.-style el) "view-transition-name" vt-name))))

(defn clear-vt! [root]
  (doseq [[sel _] vt-selectors]
    (when-let [el (.querySelector root sel)]
      (.removeProperty (.-style el) "view-transition-name"))))

(defn has-view-transitions? []
  (and (exists? js/document.startViewTransition)
       (fn? (.-startViewTransition js/document))))
