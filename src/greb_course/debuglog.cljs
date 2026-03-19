(ns greb-course.debuglog
  "Debug recorder: toggle with Ctrl+Shift+D.
   Records keydown, click, state snapshots. Copy log with Ctrl+Shift+C."
  (:require [greb-course.state :as state]
            [greb-course.dom   :as d]))

(defonce ^:private recording? (atom false))
(defonce ^:private log-buf (atom []))
(defonce ^:private badge-el (atom nil))
(defonce ^:private t0 (atom 0))
(defonce ^:private installed? (atom false))

(defn- ts []
  (let [ms (- (.now js/Date) @t0)
        s  (/ ms 1000.0)]
    (.toFixed s 3)))

(defn- snap-state []
  (let [dv  @state/doc-view
        ps  @state/pres-state
        cz  @state/canvas-zoom
        cza @state/canvas-zoom-active?
        sm  @state/sm-translate
        nav (when-let [n @state/current-nav]
              {:page (when-let [ns (:nav-state n)] @ns)
               :total (count (:spread-ids n))})]
    (cond-> {:doc-view dv :canvas-zoom cz :canvas-active? cza}
      ps  (assoc :pres (select-keys ps [:zoom :text-scale :pan-x :pan-y :current-idx]))
      nav (assoc :nav nav)
      (not= sm {:x 0 :y 0 :z 0}) (assoc :spacemouse sm))))

(defn- push! [entry]
  (when @recording?
    (swap! log-buf conj (assoc entry :t (ts)))))

(defn- update-badge! []
  (when-let [el @badge-el]
    (let [n (count @log-buf)]
      (if @recording?
        (do (set! (.-textContent el) (str "REC " n))
            (.add (.-classList el) "dbg-active"))
        (do (set! (.-textContent el) "DBG")
            (.remove (.-classList el) "dbg-active"))))))

(defn- ensure-badge! []
  (when-not @badge-el
    (let [el (d/el :div {:class "dbg-badge"} "DBG")]
      (.appendChild (.-body js/document) el)
      (reset! badge-el el)
      ;; Inject styles
      (let [style (.createElement js/document "style")]
        (set! (.-textContent style)
          ".dbg-badge {
             position:fixed; bottom:8px; right:8px; z-index:99999;
             padding:3px 8px; border-radius:4px;
             font:600 11px/1 var(--font-head,system-ui,sans-serif);
             color:rgba(255,255,255,.5); background:rgba(0,0,0,.35);
             pointer-events:none; transition:all .2s;
             opacity:0;
           }
           .dbg-badge.dbg-visible { opacity:1; }
           .dbg-badge.dbg-active {
             color:#fff; background:rgba(220,38,38,.85);
             animation:dbg-pulse 1.2s ease-in-out infinite;
           }
           @keyframes dbg-pulse {
             0%,100% { box-shadow:0 0 0 0 rgba(220,38,38,.4); }
             50%     { box-shadow:0 0 0 6px rgba(220,38,38,0); }
           }")
        (.appendChild (.-head js/document) style)))))

(defn- format-log []
  (let [lines (map (fn [{:keys [t type] :as e}]
                     (let [rest (dissoc e :t :type)]
                       (str "[" t "s] " (name type) " " (pr-str rest))))
                   @log-buf)]
    (str "=== GREB Debug Log (" (count @log-buf) " events) ===\n"
         "--- State at start ---\n"
         (pr-str (first (keep :state @log-buf)))
         "\n--- Events ---\n"
         (apply str (interpose "\n" lines))
         "\n--- State at end ---\n"
         (pr-str (snap-state))
         "\n=== END ===")))

(defn- copy-log! []
  (let [text (format-log)]
    (-> (.writeText (.-clipboard js/navigator) text)
        (.then #(do (when-let [el @badge-el]
                      (set! (.-textContent el) "COPIED!")
                      (js/setTimeout update-badge! 1500))))
        (.catch #(js/console.warn "Debug log copy failed" %)))))

(defn start! []
  (reset! recording? true)
  (reset! log-buf [])
  (reset! t0 (.now js/Date))
  (push! {:type :start :state (snap-state)})
  (ensure-badge!)
  (when-let [el @badge-el]
    (.add (.-classList el) "dbg-visible"))
  (update-badge!))

(defn stop! []
  (push! {:type :stop :state (snap-state)})
  (reset! recording? false)
  (update-badge!))

(defn toggle! []
  (if @recording? (stop!) (start!)))

;; ── Global event listeners (installed once) ──

(defn- on-keydown [e]
  (let [k (.-key e)]
    (cond
      ;; Ctrl+Shift+D  → toggle recording
      (and (= k "D") (.-ctrlKey e) (.-shiftKey e))
      (do (.preventDefault e) (toggle!))
      ;; Ctrl+Shift+C  → copy log
      (and (= k "C") (.-ctrlKey e) (.-shiftKey e))
      (do (.preventDefault e) (copy-log!))
      ;; Record key if recording
      @recording?
      (push! {:type :key
              :key k
              :ctrl (.-ctrlKey e)
              :shift (.-shiftKey e)
              :meta (.-metaKey e)
              :target (let [t (.-target e)]
                        (str (.-tagName t)
                             (when-let [c (.-className t)]
                               (when (pos? (count c))
                                 (str "." (first (.split c " ")))))))}))))

(defn- on-click [e]
  (when @recording?
    (let [t (.-target e)
          tag (.-tagName t)
          cls (.-className t)
          txt (let [s (.-textContent t)]
                (when (and s (pos? (count s)) (<= (count s) 60)) s))
          closest-btn (.closest t "button, a, .toolbar-ghost-btn, .toolbar-btn")]
      (push! {:type :click
              :tag tag
              :class (when (and cls (string? cls) (pos? (count cls)))
                       (first (.split cls " ")))
              :text txt
              :button (when closest-btn
                        (let [s (.-textContent closest-btn)]
                          (when (and s (<= (count s) 40)) (.trim s))))
              :x (.-clientX e)
              :y (.-clientY e)}))))

(defn- on-state-change [key ref old-val new-val]
  (when (and @recording? (not= old-val new-val))
    (push! {:type :state-change
            :atom (name key)
            :from (if (map? old-val)
                    (into {} (filter (fn [[k v1]]
                                       (not= v1 (get new-val k))) old-val))
                    old-val)
            :to   (if (map? new-val)
                    (into {} (filter (fn [[k v1]]
                                       (not= v1 (get old-val k))) new-val))
                    new-val)})))

(defn install!
  "Install global listeners. Safe to call multiple times."
  []
  (when-not @installed?
    (reset! installed? true)
    ;; Key + click listeners (capture phase to see everything)
    (.addEventListener js/document "keydown" on-keydown true)
    (.addEventListener js/document "click" on-click true)
    ;; Watch key state atoms
    (add-watch state/canvas-zoom-active? ::dbg
      (fn [_ _ o n] (on-state-change :canvas-zoom-active? nil o n)))
    (add-watch state/pres-state ::dbg
      (fn [_ _ o n]
        (on-state-change :pres-mode nil (some? o) (some? n))))))
