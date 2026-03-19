(ns greb-course.hints
  "Vimium-style letter hints for quick element navigation."
  (:require [greb-course.dom   :as d]
            [greb-course.state :as state]
            [clojure.string    :as str]))

(declare dismiss-hints!)

;; Home-row-first alphabet for short, easy-to-type codes
(def ^:private hint-chars "asdfghjklqwertyuiopzxcvbnm")

(defn- generate-codes
  "Generate n unique letter codes using the hint alphabet.
   Produces 1-letter codes first, then 2-letter, etc."
  [n]
  (let [alpha (vec hint-chars)
        base  (count alpha)]
    (loop [i 0, codes []]
      (if (>= (count codes) n)
        (vec (take n codes))
        (let [code (loop [num i, chars []]
                     (let [chars (cons (nth alpha (mod num base)) chars)]
                       (if (< num base)
                         (apply str chars)
                         (recur (dec (quot num base)) chars))))]
          (recur (inc i) (conj codes code)))))))

(defn text-focused-rect
  "Get a bounding rect focused on the text content of an element.
   Uses Range on the first non-empty text node, falling back to full element rect."
  [el]
  (or (some (fn [child]
              (when (and (= (.-nodeType child) 3)
                         (pos? (count (.trim (.-textContent child)))))
                (let [r (.createRange js/document)]
                  (.selectNodeContents r child)
                  (.getBoundingClientRect r))))
            (array-seq (.-childNodes el)))
      (.getBoundingClientRect el)))

(defn- sort-by-position
  "Sort elements top-to-bottom, left-to-right."
  [els]
  (sort-by (fn [el]
             (let [r (.getBoundingClientRect el)]
               [(.-top r) (.-left r)]))
           els))

(defn show-hints!
  "Scan container-el for headers and important elements, overlay Vimium-style letter labels.
   on-select is called with the target element when a hint is chosen."
  [container-el on-select]
  (dismiss-hints!)
  (let [els (array-seq (.querySelectorAll container-el
              (str "h1, h2, h3, h4, h5, h6, p, li, blockquote, figure, figcaption, "
                   ".hero-kicker, .hero-subtitle, .section-title, "
                   ".full-image-page img, .portada-page, .table-block, .step-card, "
                   ".risk-card, .info-box, .callout, .gd-card")))
        ;; Filter to visible elements
        els (vec (sort-by-position
                   (filter #(and (pos? (.-offsetHeight %))
                                 (> (.-offsetWidth %) 40)) els)))
        codes (generate-codes (count els))
        ;; Fixed-position overlay container for labels
        hc  (d/el :div {:class "hint-container"
                        :style "position:fixed;top:0;left:0;width:100vw;height:100vh;pointer-events:none;z-index:99999"})
        labels (atom [])
        code-map (atom {})]  ;; code -> element
    (.appendChild (.-body js/document) hc)
    (doseq [[i el] (map-indexed vector els)]
      (let [code (nth codes i)
            er   (.getBoundingClientRect el)
            lbl  (d/el :div {:class "hint-label"
                             :style (str "position:absolute;top:" (- (.-top er) 2) "px;left:" (- (.-left er) 42) "px;pointer-events:auto")
                             :data-code code}
                       (str/upper-case code))]
        (.appendChild hc lbl)
        (.addEventListener lbl "click"
          (fn [e] (.stopPropagation e) (on-select el) (dismiss-hints!)))
        (swap! labels conj lbl)
        (swap! code-map assoc code el)))
    (reset! state/hint-state {:container hc :labels @labels :elements els
                              :on-select on-select :code-map @code-map
                              :typed "" :codes codes})))

(defn dismiss-hints! []
  (when-let [{:keys [container labels]} @state/hint-state]
    ;; Animate labels out, then remove
    (doseq [lbl labels]
      (.add (.-classList lbl) "hint-removing"))
    (js/setTimeout
      (fn []
        (when (.-parentNode container) (.remove container)))
      200)
    (reset! state/hint-state nil)))

(defn hint-type-char!
  "Process a typed character in hint mode. Returns :consumed if handled, nil otherwise."
  [ch]
  (when-let [{:keys [code-map on-select typed labels] :as hs} @state/hint-state]
    (let [new-typed (str typed (str/lower-case ch))
          ;; Find exact match
          exact (get code-map new-typed)
          ;; Find prefix matches
          prefix-matches (filter #(str/starts-with? % new-typed) (keys code-map))]
      (cond
        ;; Exact match and no longer codes start with this prefix — activate
        (and exact (= 1 (count prefix-matches)))
        (do (on-select exact) (dismiss-hints!) :consumed)

        ;; Has prefix matches — narrow down, highlight matching labels
        (seq prefix-matches)
        (do (swap! state/hint-state assoc :typed new-typed)
            ;; Update label visibility: hide non-matching, highlight matching
            (doseq [lbl labels]
              (let [code (.getAttribute lbl "data-code")]
                (if (str/starts-with? code new-typed)
                  (do (.remove (.-classList lbl) "hint-dimmed")
                      ;; Bold the typed portion
                      (let [upper-typed (str/upper-case new-typed)
                            upper-code  (str/upper-case code)
                            rest-code   (subs upper-code (count new-typed))]
                        (set! (.-innerHTML lbl)
                          (str "<b>" upper-typed "</b>" rest-code))))
                  (.add (.-classList lbl) "hint-dimmed"))))
            :consumed)

        ;; No matches — dismiss hints
        :else
        (do (dismiss-hints!) nil)))))
