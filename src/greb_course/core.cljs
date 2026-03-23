(ns greb-course.core
  "Reader shell: routing, doc-view, preflight, public API."
  (:require [greb-course.state      :as state]
            [greb-course.ui          :as ui]
            [greb-course.nav         :as nav]
            [greb-course.debuglog    :as dbg]
            [greb-course.core-boot   :as boot]))

(declare reload!)
(declare preflight-debounced!)
(defonce ^:private preflight-timer (atom nil))

;; ── Doc text-scale persistence ───────────────────────────────

(defn- doc-ts-key [] (str "greb-ts:" (.-pathname js/location)))

(defn- save-doc-text-scale! [ts]
  (try (.setItem js/localStorage (doc-ts-key) (str ts)) (catch :default _)))

(defn- restore-doc-text-scale []
  (try
    (when-let [v (.getItem js/localStorage (doc-ts-key))]
      (let [n (js/parseFloat v)] (when-not (js/isNaN n) n)))
    (catch :default _ nil)))

;; ── Routing helpers ──────────────────────────────────────────

(defn- base-path []
  (let [path (.-pathname js/location)]
    (if (.endsWith path "/") path (str path "/"))))

(defn- course-path [course]
  (let [org  (get-in course [:meta :org])
        slug (get-in course [:meta :slug])]
    (str "/" org "/" slug "/")))

(defn- match-course [courses]
  (first (filter #(= (base-path) (course-path %)) courses)))

;; ── Doc-mode view ────────────────────────────────────────────

(defn- commit-canvas-zoom! []
  (when (and @state/canvas-zoom-active? (not= @state/canvas-zoom 1.0))
    (let [cz @state/canvas-zoom]
      (swap! state/doc-view
        (fn [dv] (let [old-z (or (:zoom dv) 1.0)]
                   (assoc dv :zoom (* old-z cz)
                             :pan-x (* (or (:pan-x dv) 0) cz)
                             :pan-y (* (or (:pan-y dv) 0) cz)))))
      (reset! state/canvas-zoom 1.0)
      (when-let [reader (.querySelector js/document ".reader")]
        (when-let [active (.querySelector reader ".spread.active")]
          (.setProperty (.-style active) "transition" "none"))
        (.setProperty (.-style reader) "--canvas-scale" "1")
        (let [{:keys [zoom text-scale pan-x pan-y]} @state/doc-view]
          (.setProperty (.-style reader) "--doc-zoom" (str (or zoom 1.0)))
          (.setProperty (.-style reader) "--doc-pan-x" (str (or pan-x 0) "px"))
          (.setProperty (.-style reader) "--doc-pan-y" (str (or pan-y 0) "px"))
          (ui/update-status-badge! zoom text-scale))
        (.offsetHeight reader)
        (js/requestAnimationFrame
          (fn [] (when-let [active (.querySelector reader ".spread.active")]
                   (.removeProperty (.-style active) "transition"))))))))

(defn- doc-apply-view! []
  (when-let [reader (.querySelector js/document ".reader")]
    (let [{:keys [zoom text-scale pan-x pan-y]} @state/doc-view]
      (.setProperty (.-style reader) "--doc-zoom" (str (or zoom 1.0)))
      (.setProperty (.-style reader) "--doc-text-scale" (str (or text-scale 1.0)))
      (.setProperty (.-style reader) "--doc-pan-x" (str (or pan-x 0) "px"))
      (.setProperty (.-style reader) "--doc-pan-y" (str (or pan-y 0) "px"))
      (.setProperty (.-style reader) "--canvas-scale" (str @state/canvas-zoom))
      (if (and text-scale (not= text-scale 1.0))
        (.add (.-classList reader) "text-scaled")
        (.remove (.-classList reader) "text-scaled"))
      (ui/update-status-badge! zoom text-scale))))

;; ── Preflight overflow check ─────────────────────────────────

(defn preflight! []
  (let [pages (array-seq (.querySelectorAll js/document ".page"))
        results (atom [])]
    (doseq [page pages]
      (let [id    (or (.-id page) "?")
            body  (.querySelector page ".page-body")
            over? (when body (> (.-scrollHeight body) (+ (.-clientHeight body) 2)))]
        (if over?
          (let [delta (- (.-scrollHeight body) (.-clientHeight body))]
            (.add (.-classList page) "preflight-overflow")
            (swap! results conj {:id id :overflow-px delta
                                 :scroll-h (.-scrollHeight body) :client-h (.-clientHeight body)})
            (js/console.warn (str "⚠ OVERFLOW page #" id " — content exceeds by " delta "px"
                                  " (scrollH=" (.-scrollHeight body) " clientH=" (.-clientHeight body) ")")))
          (.remove (.-classList page) "preflight-overflow"))))
    (if (seq @results)
      (do (js/console.warn (str "⚠ PREFLIGHT: " (count @results) " page(s) overflowing"))
          (js/console.table (clj->js @results)))
      (js/console.log "✓ PREFLIGHT: All pages fit"))
    @results))

(defn- preflight-debounced!
  ([] (preflight-debounced! 1500))
  ([delay-ms]
   (when-let [t @preflight-timer] (js/clearTimeout t))
   (reset! preflight-timer
     (js/setTimeout (fn [] (reset! preflight-timer nil) (preflight!)) delay-ms))))

;; ── Boot opts (passed to core-boot to avoid circular deps) ───

(defn- boot-opts []
  {:course-path-fn       course-path
   :doc-apply-view!      doc-apply-view!
   :save-doc-text-scale! save-doc-text-scale!
   :restore-doc-text-scale restore-doc-text-scale
   :commit-canvas-zoom!  commit-canvas-zoom!
   :preflight-debounced! preflight-debounced!
   :reload!              reload!})

;; ── Boot & reload ────────────────────────────────────────────

(defn- do-boot [courses]
  (boot/boot courses match-course (boot-opts)))

(defn init! [courses]
  (try
    (dbg/install!)
    (do-boot courses)
    (catch :default e
      (let [d (.createElement js/document "pre")]
        (set! (.. d -style -cssText)
          "position:fixed;top:0;left:0;right:0;z-index:999999;background:red;color:#fff;font:12px/1.4 monospace;padding:12px;max-height:60vh;overflow:auto;white-space:pre-wrap")
        (set! (.-textContent d) (str "BOOT ERROR: " (.-message e) "\n" (.-stack e)))
        (.appendChild (.-body js/document) d)))))

(defn reload! [courses]
  (when-let [app (.getElementById js/document "app")]
    (set! (.-innerHTML app) "")
    (do-boot courses)))

;; ── Public API for REPL ──────────────────────────────────────

(defn get-courses [] @state/current-courses)

(defn update-courses! [courses]
  (reset! state/current-courses courses)
  (reload! courses))

(defn navigate! [target]
  (when-let [{:keys [go! id->spread]} @state/current-nav]
    (let [idx (if (number? target) target (get id->spread (name target)))]
      (when idx (go! idx nil)))))

(defn current-page-id [] (nav/current-hash))
