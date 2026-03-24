(ns greb-course.nav
  "URL hash helpers, spread navigator, and TOC panel."
  (:require [greb-course.dom              :as d]
            [greb-course.i18n             :as i18n]
            [greb-course.state            :as state]
            [greb-course.animation        :as anim]
            [greb-course.nav-spread-dots  :as sdots]
            [clojure.string               :as cstr]))

(defn current-hash []
  (let [h (.-hash js/location)]
    (when (> (count h) 1) (subs h 1))))

(defn set-hash! [hash]
  (.replaceState js/history nil "" (str "#" hash)))

(defn build-navigator [spreads spread-ids dots indicator prev-btn next-btn initial-idx
                       & {:keys [total-pages mobile?]}]
  (let [n     (count spreads)
        tp    (or total-pages n)
        mob?  (boolean mobile?)
        pg-label (fn [spread-idx]
                   (let [first-pg (inc (if mob? spread-idx (* spread-idx 2)))]
                     (str first-pg " / " tp)))
        state (atom (max 0 (min (dec n) (or initial-idx 0))))
        go!   (fn [i dir]
                (let [ni (max 0 (min (dec n) i))
                      cur @state]
                  (when (not= ni cur)
                    (let [old (nth spreads cur)]
                      (.add (.-classList old) "spread-exiting")
                      (when dir (.add (.-classList old) dir))
                      (js/setTimeout #(.remove (.-classList old) "active" "going-back" "spread-exiting") 300))
                    (reset! state ni)
                    (let [nw (nth spreads ni)]
                      (when dir (.remove (.-classList nw) "going-back"))
                      (.add (.-classList nw) "active")
                      (anim/animate-spread! nw))
                    (doseq [[j dt] (map-indexed vector dots)]
                      (if (= j ni)
                        (.add (.-classList dt) "active")
                        (.remove (.-classList dt) "active")))
                    (set! (.-textContent indicator) (pg-label ni))
                    (set! (.-disabled prev-btn) (= ni 0))
                    (set! (.-disabled next-btn) (= ni (dec n)))
                    (set-hash! (nth spread-ids ni ""))
                    ;; Bounce the dots bar + refresh Dock scales (genie at rest)
                    (when-let [bar (some-> (first dots) (.-parentElement))]
                      (sdots/idle-scales! dots ni)
                      (.remove (.-classList bar) "dots-bounce")
                      ;; Force reflow to restart animation
                      (.-offsetWidth bar)
                      (.add (.-classList bar) "dots-bounce")))
                  ni))]
    (.addEventListener prev-btn "click" #(go! (dec @state) "going-back"))
    (.addEventListener next-btn "click" #(go! (inc @state) nil))
    (.addEventListener js/document "keydown"
      (fn [e]
        (when (not (.closest (.-target e) "a, input, textarea, .toc-wrapper"))
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
    (when (seq dots)
      (js/requestAnimationFrame
        (fn []
          (when-let [bar (some-> (first dots) .-parentElement)]
            (when (.contains (.-classList bar) "spread-dots")
              (sdots/attach-dock! bar dots state))))))
    {:go! go! :nav-state state}))

(defn- build-hjkl-widget []
  (let [grid    (d/el :div {:class "hjkl-grid"})
        dot     (d/el :div {:class "hjkl-dot"})
        card    (d/el :div {:class "toc-hjkl-card"})
        dial-nd (d/el :div {:class "toc-hjkl-dial-needle"})
        dirs    {"h" {:pos [0 1] :arrow "\u2190" :label "h"}
                 "k" {:pos [1 0] :arrow "\u2191" :label "k"}
                 "l" {:pos [2 1] :arrow "\u2192" :label "l"}
                 "j" {:pos [1 2] :arrow "\u2193" :label "j"}}
        dir-cls (fn [k] (str "hjkl-cell hjkl-cell--dir hjkl-dir--" k))
        cells   (atom {})  ;; "h"/"j"/"k"/"l" -> cell element
        timer   (atom nil)
        zmin    0.82
        zmax    1.2
        z-step  0.06
        zoom    (atom 1)
        zoom->deg (fn [z]
                    (+ -52 (* 104 (/ (- z zmin) (- zmax zmin)))))
        apply-zoom!
        (fn [z]
          (let [z (max zmin (min zmax z))]
            (reset! zoom z)
            (.setProperty (.-style card) "--hjkl-zoom" (str z))
            (.setProperty (.-style dial-nd) "--hjkl-dial-deg" (str (zoom->deg z) "deg"))))]
    ;; Build 3x3 grid (row-major: top-left=0,0)
    (doseq [row (range 3)]
      (doseq [col (range 3)]
        (let [match (first (filter (fn [[_ {:keys [pos]}]] (= pos [col row])) dirs))
              cell  (if match
                      (let [[key {:keys [arrow label]}] match
                            c (d/el :div {:class (dir-cls key)}
                                    (d/el :span {:class "hjkl-arrow"} arrow)
                                    (d/el :span {:class "hjkl-key"} label))]
                        (swap! cells assoc key c)
                        c)
                      (if (and (= col 1) (= row 1))
                        (d/el :div {:class "hjkl-cell hjkl-cell--center"})
                        (d/el :div {:class "hjkl-cell"})))]
          (.appendChild grid cell))))
    (.appendChild grid dot)
    (let [hdr (d/el :div {:class "toc-hjkl-hdr"}
                   (d/ic "move" "toc-hjkl-icon")
                   (d/el :span {:class "toc-hjkl-title"} "Pan"))
          dial  (d/el :div {:class "toc-hjkl-dial"}
                    (d/el :div {:class "toc-hjkl-dial-face"}
                          (d/el :div {:class "toc-hjkl-dial-track"})
                          dial-nd)
                    (d/el :div {:class "toc-hjkl-dial-keys"}
                          (d/el :kbd {:class "toc-hjkl-dial-k"} "d")
                          (d/el :span {:class "toc-hjkl-dial-lbl"} "zoom")
                          (d/el :kbd {:class "toc-hjkl-dial-k"} "f")))]
      (.appendChild card hdr)
      (.appendChild card grid)
      (.appendChild card dial)
      (apply-zoom! 1))
    ;; hjkl pan preview + d/f zoom (TOC open, not typing in a field)
    (let [on-key
          (fn [e]
            (when-not (.closest (.-target e) "input, textarea, [contenteditable=true]")
              (let [k (.-key e)
                    toc-open?
                    (when-let [tw (.querySelector js/document ".toc-wrapper")]
                      (.contains (.-classList tw) "open"))]
                (cond
                  (contains? dirs k)
                  (do
                    (.setAttribute grid "data-dir" k)
                    (doseq [[dk cel] @cells]
                      (if (= dk k)
                        (.add (.-classList cel) "hjkl-cell--active")
                        (.remove (.-classList cel) "hjkl-cell--active")))
                    (when-let [t @timer] (js/clearTimeout t))
                    (reset! timer
                      (js/setTimeout
                        (fn []
                          (.removeAttribute grid "data-dir")
                          (doseq [[_ cel] @cells]
                            (.remove (.-classList cel) "hjkl-cell--active")))
                        600)))
                  (and toc-open? (#{"d" "f"} k))
                  (do (.preventDefault e)
                      (case k
                        "d" (apply-zoom! (- @zoom z-step))
                        "f" (apply-zoom! (+ @zoom z-step))))))))]
      (.addEventListener js/document "keydown" on-key))
    (d/el :div {:class "toc-hjkl-dock"}
          (d/el :div {:class "toc-hjkl-swing"}
                card))))

(defn build-toc-panel [navigate! toc-groups shortcuts]
  (let [open?   (atom false)
        overlay (d/el :div {:class "toc-overlay"})
        wrapper (d/el :div {:class "toc-wrapper"})
        left    (d/el :div {:class "toc-col toc-col-left"})
        right   (d/el :div {:class "toc-col toc-col-right"})
        close!  #(do (when-let [m @state/current-nav]
                       (when-let [f (:clear-dot-preview! m)] (f)))
                     (reset! open? false)
                     (.remove (.-classList wrapper) "open")
                     (.remove (.-classList overlay) "open"))
        open!   #(do (reset! open? true)
                     (.add    (.-classList wrapper) "open")
                     (.add    (.-classList overlay) "open"))
        toggle! #(if @open? (close!) (open!))
        pres?   #(.contains (.-classList (.-documentElement js/document)) "presenting")]
    ;; ── LEFT COLUMN: Brand card + Shortcuts card (HJKL dock is appended to wrapper) ──
    (.appendChild left
      (d/el :div {:class "toc-brand-card"}
            (doto (d/el :button {:class "toc-close-btn" :aria-label (i18n/t :close)}
                        (d/ic "x" ""))
                  (.addEventListener "click" close!))
            (d/el :div {:class "toc-brand-logo"}
                  (d/ic "book-open" "toc-brand-icon")
                  (d/el :span {:class "toc-brand-name"} "GREB Docs"))
            (d/el :p {:class "toc-brand-tagline"} (i18n/t :toc-tagline))
            (d/el :div {:class "toc-mode-badge"}
                  (d/ic "monitor" "toc-mode-icon")
                  (d/el :span {:class "toc-mode-label"}
                        (if (pres?) (i18n/t :toc-mode-pres) (i18n/t :toc-mode-doc))))))
    (when (seq shortcuts)
      (let [sc-card (d/el :div {:class "toc-shortcuts-card"})
            sc-hdr  (d/el :div {:class "toc-shortcuts-hdr"}
                          (d/ic "keyboard" "toc-shortcuts-icon")
                          (d/el :span {} (i18n/t :toc-shortcuts-title)))]
        (.appendChild sc-card sc-hdr)
        (doseq [[key desc] shortcuts]
          (.appendChild sc-card
            (d/el :div {:class "toc-shortcut-row"}
                  (d/el :kbd {:class "toc-shortcut-key"} key)
                  (d/el :span {:class "toc-shortcut-desc"} desc))))
        (.appendChild left sc-card)))
    ;; Document Config card
    (when-let [course @state/current-course]
      (let [md (or (:meta course) {})
            th (or (:theme course) {})
            config-card
            (d/el :div {:class "toc-api-card"}
              (d/el :div {:class "toc-api-hdr"}
                    (d/ic "file-text" "toc-api-icon")
                    (d/el :span {} (or (:title md) "Document"))
                    (d/el :span {:class "toc-api-status"}
                          (str (count (:pages course)) " pages")))
              (doto (d/el :button {:class "toc-api-btn"}
                          (d/ic "info" "") "Details")
                (.addEventListener "click"
                  (fn []
                    ;; Show floating modal in center
                    (let [scrim (d/el :div {:class "doc-detail-scrim"})
                          modal (d/el :div {:class "doc-detail-modal"})
                          close-btn (doto (d/el :button {:class "doc-detail-close"} "\u2715")
                                      (.addEventListener "click" #(when (.-parentNode scrim) (.remove scrim))))
                          hdr (d/el :div {:class "doc-detail-hdr"}
                                    (d/ic "file-text" "doc-detail-icon")
                                    (d/el :h2 {:class "doc-detail-title"} (or (:title md) "Document"))
                                    close-btn)
                          body (d/el :div {:class "doc-detail-body"})
                          make-row (fn [label value]
                                    (let [row (d/el :div {:class "doc-detail-row"})
                                          lbl (d/el :span {:class "doc-detail-label"} label)
                                          val-el (d/el :span {:class "doc-detail-value"} (str (or value "")))]
                                      (.addEventListener val-el "click"
                                        (fn []
                                          (let [inp (d/el :input {:class "doc-detail-input" :type "text"
                                                                  :value (str (or value ""))})]
                                            (set! (.-innerHTML row) "")
                                            (.appendChild row lbl)
                                            (.appendChild row inp)
                                            (.focus inp)
                                            (.addEventListener inp "blur"
                                              (fn [] (set! (.-innerHTML row) "")
                                                (.appendChild row lbl)
                                                (let [new-val (.-value inp)]
                                                  (.appendChild row (d/el :span {:class "doc-detail-value"} new-val))))))))
                                      (.appendChild row lbl)
                                      (.appendChild row val-el)
                                      row))]
                      ;; Metadata section
                      (.appendChild body (d/el :h3 {:class "doc-detail-section"} "Metadata"))
                      (doseq [[l v] [["ID" (:id md)] ["Org" (:org md)] ["Slug" (:slug md)]
                                     ["Category" (:category md)]
                                     ["Tags" (cstr/join ", " (or (:tags md) []))]
                                     ["Lang" (name (or (:lang md) :en))]]]
                        (.appendChild body (make-row l v)))
                      ;; Theme section
                      (.appendChild body (d/el :h3 {:class "doc-detail-section"} "Theme"))
                      (doseq [[l v] [["Brand" (:brand-name th)]
                                     ["Display Font" (get-in th [:fonts :display])]
                                     ["Body Font" (get-in th [:fonts :body])]]]
                        (.appendChild body (make-row l v)))
                      ;; Color swatches
                      (let [swatch-row (d/el :div {:class "doc-detail-swatches"})]
                        (doseq [k [:primary :secondary :accent :ink :paper :page]]
                          (when-let [c (get-in th [:colors k])]
                            (let [sw (d/el :div {:class "doc-detail-swatch"})
                                  lbl (d/el :span {:class "doc-detail-swatch-label"} (name k))]
                              (.setProperty (.-style sw) "--swatch-color" c)
                              (.appendChild sw lbl)
                              (.appendChild swatch-row sw))))
                        (.appendChild body swatch-row))
                      ;; Image generation prompts
                      (.appendChild body (d/el :h3 {:class "doc-detail-section"} "Image Prompts"))
                      (let [course-id (or (:id md) "")
                            ls-key (str "greb-style:" course-id)
                            style-val (or @state/illustration-style
                                          (.getItem js/localStorage ls-key)
                                          (get-in md [:style :illustration])
                                          "")
                            style-input (d/el :textarea {:class "doc-detail-textarea"
                                                         :placeholder "Illustration style (prepended to all image prompts)"
                                                         :rows "3"})
                            _ (set! (.-value style-input) style-val)
                            _ (.addEventListener style-input "blur"
                                (fn []
                                  (let [v (.-value style-input)]
                                    (reset! state/illustration-style v)
                                    (.setItem js/localStorage ls-key v))))
                            gen-input (d/el :input {:class "doc-detail-input" :type "text"
                                                    :placeholder "Describe what to generate..."})
                            gen-btn (doto (d/el :button {:class "doc-detail-gen-btn"}
                                                (d/ic "image" "") "Generate")
                                      (.addEventListener "click"
                                        (fn []
                                          (let [style (.-value style-input)
                                                subject (.-value gen-input)
                                                prompt (if (pos? (count style))
                                                         (str style ", " subject)
                                                         subject)]
                                            (when (pos? (count subject))
                                              (set! (.-textContent gen-btn) "Generating...")
                                              (-> (js/fetch "/api/kie/generate"
                                                    (clj->js {:method "POST"
                                                              :headers {"Content-Type" "application/json"}
                                                              :body (.stringify js/JSON (clj->js {:prompt prompt :size "3:2"}))}))
                                                  (.then #(.json %))
                                                  (.then (fn [r]
                                                           (when-let [tid (some-> (aget r "data") (aget "taskId"))]
                                                             (set! (.-textContent gen-btn) "Polling...")
                                                             (let [poll (atom nil)]
                                                               (reset! poll
                                                                 (js/setInterval
                                                                   (fn []
                                                                     (-> (js/fetch (str "/api/kie/task?taskId=" tid))
                                                                         (.then #(.json %))
                                                                         (.then (fn [s]
                                                                                  (when (= "SUCCESS" (some-> (aget s "data") (aget "status")))
                                                                                    (js/clearInterval @poll)
                                                                                    (let [url (some-> (aget s "data") (aget "response") (aget "resultUrls") (aget 0))
                                                                                          preview (d/el :img {:class "doc-detail-preview" :src url})]
                                                                                      (set! (.-textContent gen-btn) "Generate")
                                                                                      (.appendChild body preview)))))))
                                                                   3000))))))))))))]
                        (.appendChild body (make-row "Style" nil))
                        (.appendChild body style-input)
                        (.appendChild body (d/el :div {:class "doc-detail-gen-row"} gen-input gen-btn)))
                      (.appendChild modal hdr)
                      (.appendChild modal body)
                      (.appendChild scrim modal)
                      (.addEventListener scrim "click"
                        (fn [e] (when (= (.-target e) scrim) (.remove scrim))))
                      (.appendChild (.-body js/document) scrim))))))]
        (.appendChild left config-card)))
    ;; ── RIGHT COLUMN: Index entries card ──
    (let [body   (d/el :div {:class "toc-entries-card"})
          hdr    (d/el :div {:class "toc-entries-hdr"}
                       (d/ic "list" "toc-entries-icon")
                       (d/el :span {} (i18n/t :toc-title)))
          scroll (d/el :div {:class "toc-entries-scroll"})
          sec-n  (atom 0)]
      (.appendChild body hdr)
      (doseq [{:keys [label entries]} toc-groups]
        (let [num (swap! sec-n inc)]
          (.appendChild scroll
            (d/el :p {:class "toc-group-hdr"}
                  (d/el :kbd {:class "toc-shortcut-key toc-section-num"} (str num))
                  (str " " label))))
        (doseq [{:keys [id label page icon]} entries]
          (let [entry-el (d/el :a {:href (str "#" id) :class "index-entry"}
                               (d/ic (or icon "file-text") "entry-icon")
                               (d/el :span {:class "entry-label"} label)
                               (d/el :span {:class "entry-dots"})
                               (when page (d/el :span {:class "entry-page"} (str page))))]
            (.addEventListener entry-el "click"
              (fn [e]
                (.preventDefault e)
                (navigate! id)
                (close!)))
            (.appendChild scroll entry-el))))
      (.appendChild body scroll)
      (.appendChild right body))
    ;; ── Assemble ──
    (.appendChild wrapper left)
    (.appendChild wrapper right)
    (.appendChild wrapper (build-hjkl-widget))
    (.addEventListener overlay "click" close!)
    {:overlay overlay :panel wrapper :toggle! toggle! :open? open?}))
