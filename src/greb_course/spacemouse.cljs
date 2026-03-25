(ns greb-course.spacemouse
  "3Dconnexion SpaceMouse support via WebHID API.
   On macOS, 3DxWare must be quit first — it exclusively locks the USB device."
  (:require [greb-course.dom   :as d]
            [greb-course.state :as state]
            [greb-course.ui    :as ui]))

(def ^:private sm-vendor-id 0x256F)
(def ^:private sm-dead-zone 8)
(def ^:private virtual-mouse-pid 0xC671)
(def ^:private poll-interval-ms 16)
(def ^:private sm-smooth 0.35)  ;; EMA factor: 0 = ignore new, 1 = no smoothing

(defn- sm-dead [v]
  (if (< (js/Math.abs v) sm-dead-zone) 0 v))

(defn- sm-parse-int16-le [^js dv offset]
  (.getInt16 dv offset true))

(defonce ^:private debug-counter (atom 0))
(defonce ^:private poll-timer (atom nil))

;; ── Input handling ──

(defn- sm-lerp
  "Exponential moving average: blend old toward new."
  [old new-val]
  (+ (* sm-smooth new-val) (* (- 1 sm-smooth) old)))

(defn- handle-translate [^js data]
  (let [x (sm-dead (sm-parse-int16-le data 0))
        y (sm-dead (sm-parse-int16-le data 2))
        z (sm-dead (sm-parse-int16-le data 4))
        prev @state/sm-translate
        sx (if (zero? x) 0 (sm-lerp (or (:x prev) 0) x))
        sy (if (zero? y) 0 (sm-lerp (or (:y prev) 0) y))
        sz (if (zero? z) 0 (sm-lerp (or (:z prev) 0) z))]
    (reset! state/sm-translate {:x sx :y sy :z sz})
    (when (or (not= sx 0) (not= sy 0) (not= sz 0))
      (when-let [raf! @state/sm-ensure-raf!] (raf!)))))

(defn- handle-rotate [^js data]
  (let [pitch (sm-dead (sm-parse-int16-le data 0))
        roll  (sm-dead (sm-parse-int16-le data 2))
        yaw   (sm-dead (sm-parse-int16-le data 4))]
    (reset! state/sm-rotate {:pitch pitch :roll roll :yaw yaw})))

(defonce ^:private btn-debounce (atom 0))

(defn- handle-buttons [^js data]
  (let [mask (aget (js/Uint8Array. (.-buffer data)) 0)
        now  (.now js/Date)]
    (when (and (pos? mask) (> (- now @btn-debounce) 250))
      (reset! btn-debounce now)
      (cond
        (== mask 3) (when-let [f @state/sm-on-reset] (f))
        (== mask 1) (when-let [f @state/sm-on-prev] (f))
        (== mask 2) (when-let [f @state/sm-on-next] (f))))))

;; ── inputreport path ──

(defn- on-sm-input [e]
  (let [^js data (.-data e)
        rid      (.-reportId e)
        len      (.-byteLength data)
        cnt      (swap! debug-counter inc)]
    (when (<= cnt 10)
      (let [bytes (js/Array.from (js/Uint8Array. (.-buffer data)))
            vals  (when (>= len 6)
                    (str "x=" (sm-parse-int16-le data 0)
                         " y=" (sm-parse-int16-le data 2)
                         " z=" (sm-parse-int16-le data 4)
                         (when (>= len 12)
                           (str " rx=" (sm-parse-int16-le data 6)
                                " ry=" (sm-parse-int16-le data 8)
                                " rz=" (sm-parse-int16-le data 10)))))]
        (js/console.log "SM" (str "rid=" rid " len=" len) vals bytes)))
    (case rid
      1 (if (>= len 12)
          ;; Combined translate + rotate in one 12-byte report
          (let [tx (sm-dead (sm-parse-int16-le data 0))
                ty (sm-dead (sm-parse-int16-le data 2))
                tz (sm-dead (sm-parse-int16-le data 4))
                rx (sm-dead (sm-parse-int16-le data 6))
                ry (sm-dead (sm-parse-int16-le data 8))
                rz (sm-dead (sm-parse-int16-le data 10))
                prev @state/sm-translate
                sx (if (zero? tx) 0 (sm-lerp (or (:x prev) 0) tx))
                sy (if (zero? ty) 0 (sm-lerp (or (:y prev) 0) ty))
                sz (if (zero? tz) 0 (sm-lerp (or (:z prev) 0) tz))]
            (reset! state/sm-translate {:x sx :y sy :z sz})
            (reset! state/sm-rotate {:pitch rx :roll ry :yaw rz})
            (when (or (not= sx 0) (not= sy 0) (not= sz 0))
              (when-let [raf! @state/sm-ensure-raf!] (raf!))))
          ;; Standard 6-byte translate-only report
          (handle-translate data))
      2 (handle-rotate data)
      3 (handle-buttons data)
      nil)))

;; ── Feature-report polling (fallback when only feature reports available) ──

(defn- has-input-reports? [^js dev]
  (some (fn [c] (pos? (.-length (.-inputReports ^js c))))
        (array-seq (.-collections dev))))

(defn- feature-report-ids [^js dev]
  (set (mapcat (fn [c]
                 (map #(.-reportId ^js %)
                      (array-seq (.-featureReports ^js c))))
               (array-seq (.-collections dev)))))

(defn- poll-feature-reports! [^js dev translate-rid rotate-rid]
  (when (and (.-opened dev) (identical? dev @state/sm-device))
    (-> (js/Promise.all
          #js [(.receiveFeatureReport dev translate-rid)
               (.receiveFeatureReport dev rotate-rid)])
        (.then (fn [results]
          (let [cnt (swap! debug-counter inc)]
            (when (<= cnt 5)
              (js/console.log "SM poll" cnt
                               (js/Array.from (js/Uint8Array. (.-buffer (aget results 0))))
                               (js/Array.from (js/Uint8Array. (.-buffer (aget results 1)))))))
          (handle-translate (aget results 0))
          (handle-rotate (aget results 1))
          (reset! poll-timer
            (js/setTimeout #(poll-feature-reports! dev translate-rid rotate-rid)
                           poll-interval-ms))))
        (.catch (fn [err]
          (let [msg (str (.-message err))]
            (if (or (re-find #"close" msg) (re-find #"not open" msg)
                    (re-find #"abort" msg) (re-find #"detach" msg))
              (js/console.warn "SM poll stopped:" msg)
              (reset! poll-timer
                (js/setTimeout #(poll-feature-reports! dev translate-rid rotate-rid)
                               poll-interval-ms)))))))))

(defn- stop-polling! []
  (when-let [t @poll-timer] (js/clearTimeout t))
  (reset! poll-timer nil))

;; ── Device attach / connect ──

(defn update-indicator! []
  (doseq [el (array-seq (.querySelectorAll js/document ".sm-indicator"))]
    (if @state/sm-device
      (.add (.-classList el) "sm-connected")
      (.remove (.-classList el) "sm-connected"))))

(defn- virtual-mouse? [^js dev]
  (== (.-productId dev) virtual-mouse-pid))

(defn- log-device! [^js dev]
  (js/console.log "SM:" (.-productName dev)
                   "pid=" (.-productId dev) "opened?" (.-opened dev))
  (doseq [c (array-seq (.-collections dev))]
    (js/console.log "  collection:"
                     (str "0x" (.toString (.-usagePage c) 16))
                     (str "/0x" (.toString (.-usage c) 16))
                     (str "in=" (.-length (.-inputReports c)))
                     (str "feat=" (.-length (.-featureReports c))))))

(defn- start-listening! [^js dev]
  (reset! debug-counter 0)
  (if (has-input-reports? dev)
    (do (js/console.log "SM: using inputreport events")
        (.addEventListener dev "inputreport" on-sm-input)
        (ui/show-toast! "SpaceMouse connected"))
    ;; Fallback: try polling feature reports 1 & 2
    (let [ids (feature-report-ids dev)]
      (if (and (ids 1) (ids 2))
        (do (js/console.log "SM: polling feature reports 1 & 2")
            (ui/show-toast! "SpaceMouse connected")
            (poll-feature-reports! dev 1 2))
        (do (js/console.warn "SM: device has no input reports and no usable feature reports")
            (ui/show-toast! "SpaceMouse: no usable data channels" 4000))))))

(defn- dedupe-devices
  "Remove duplicate entries for the same physical device (same productId).
   Prefer entries that advertise input reports in their collections.
   Excludes the 3DxWare virtual mouse which would fight the OS cursor."
  [devices]
  (let [physical (remove virtual-mouse? devices)
        grouped  (group-by #(.-productId ^js %) physical)]
    (map (fn [[_ devs]]
           (or (first (filter has-input-reports? devs))
               (first devs)))
         grouped)))

(defn- attach-device!
  "Open and set up a single HID device. Returns a promise that resolves to
   :ok or :fail."
  [^js dev]
  ;; Skip if we already have a device connected
  (if @state/sm-device
    (js/Promise.resolve :ok)
    (do (log-device! dev)
        (-> (if (.-opened dev) (js/Promise.resolve) (.open dev))
            (.then (fn []
              (js/console.log "SM: opened successfully")
              (reset! state/sm-device dev)
              (update-indicator!)
              (start-listening! dev)
              :ok))
            (.catch (fn [err]
              (js/console.warn "SM: could not open —" (.-message err))
              :fail))))))

(defn- show-3dxware-help!
  "Show a modal explaining why the SpaceMouse can't connect and how to fix it."
  []
  (let [cmd     "killall 3DconnexionHelper"
        overlay (d/el :div {:class "sm-help-scrim"})
        copy-btn (d/el :button {:class "sm-help-copy-btn"} "Copy command")
        retry-btn (d/el :button {:class "sm-help-retry-btn"} "Retry connection")
        close-btn (d/el :button {:class "sm-help-close-btn"} "\u00D7")
        panel
        (d/el :div {:class "sm-help-panel"}
          close-btn
          (d/el :h3 {} "SpaceMouse — Cannot open device")
          (d/el :p {}
            "The 3Dconnexion driver (3DxWare) holds an exclusive lock on the USB device. "
            "The browser's WebHID API cannot open the device while 3DxWare is running.")
          (d/el :h4 {} "How to fix")
          (d/el :ol {}
            (d/el :li {}
              "Open " (d/el :strong {} "Terminal") " and run:")
            (d/el :li {:class "sm-help-cmd-wrap"}
              (d/el :code {:class "sm-help-cmd"} cmd)
              copy-btn)
            (d/el :li {}
              "Click " (d/el :strong {} "Retry connection") " below.")
            (d/el :li {}
              "The browser will ask you to select the SpaceMouse — confirm it."))
          (d/el :p {:class "sm-help-note"}
            "Alternatively, quit 3DxWare from the macOS menu bar icon before connecting.")
          retry-btn)]
    (.appendChild overlay panel)
    (.appendChild (.-body js/document) overlay)
    (let [close! (fn []
                   (when (.-parentNode overlay) (.remove overlay)))]
      (.addEventListener close-btn "click" close!)
      (.addEventListener overlay "click"
        (fn [e] (when (= (.-target e) overlay) (close!))))
      (.addEventListener copy-btn "click"
        (fn [e]
          (.stopPropagation e)
          (-> (.writeText js/navigator.clipboard cmd)
              (.then  #(do (set! (.-textContent copy-btn) "Copied!")
                           (js/setTimeout (fn [] (set! (.-textContent copy-btn) "Copy command")) 2000)))
              (.catch #(ui/show-toast! "Copy failed" 2000)))))
      (.addEventListener retry-btn "click"
        (fn [e]
          (.stopPropagation e)
          (close!)
          (connect!))))))

(defn- try-open-any!
  "Try opening devices in order. First success wins. If all fail, show a
   friendly message (unless silent? is true)."
  ([devices] (try-open-any! devices false))
  ([devices silent?]
   (if (empty? devices)
     (when-not silent? (show-3dxware-help!))
     (-> (attach-device! (first devices))
         (.then (fn [result]
           (when (= result :fail)
             (try-open-any! (rest devices) silent?))))))))

(defn available? []
  (and js/navigator (.-hid js/navigator)))

(defn connect! []
  (if @state/sm-device
    ;; Disconnect
    (do (stop-polling!)
        (.removeEventListener @state/sm-device "inputreport" on-sm-input)
        (when (.-opened @state/sm-device) (.close @state/sm-device))
        (reset! state/sm-device nil)
        (reset! state/sm-translate {:x 0 :y 0 :z 0})
        (reset! state/sm-rotate {:pitch 0 :roll 0 :yaw 0})
        (update-indicator!)
        (ui/show-toast! "SpaceMouse disconnected"))
    ;; Connect
    (when (available?)
      (-> (.requestDevice (.-hid js/navigator)
            #js {:filters #js [#js {:vendorId sm-vendor-id
                                     :usagePage 0x01 :usage 0x08}
                                #js {:vendorId sm-vendor-id}]})
          (.then (fn [devices]
            (let [devs (remove virtual-mouse? (array-seq devices))]
              (when (seq devs)
                (let [unique (dedupe-devices devs)]
                  (try-open-any! unique))))))
          (.catch (fn [err]
            (js/console.warn "SpaceMouse:" (.-message err))))))))

(defn disconnect! []
  (when @state/sm-device
    (stop-polling!)
    (.removeEventListener @state/sm-device "inputreport" on-sm-input)
    (when (.-opened @state/sm-device)
      (try (.close @state/sm-device) (catch :default _)))
    (reset! state/sm-device nil)
    (reset! state/sm-translate {:x 0 :y 0 :z 0})
    (reset! state/sm-rotate {:pitch 0 :roll 0 :yaw 0})
    (update-indicator!)))

(defn restart!
  "Disconnect and immediately reconnect the SpaceMouse."
  []
  (disconnect!)
  (js/setTimeout connect! 300))

;; Note: we intentionally do NOT close the device on beforeunload.
;; The browser releases the HID handle automatically, and keeping the
;; authorization allows try-reconnect! to re-open it after refresh.

(defn try-reconnect!
  "Auto-reconnect to a previously-authorized SpaceMouse after page refresh.
   Uses navigator.hid.getDevices() which returns devices the user already
   granted permission to — no picker required."
  []
  (when (and (available?) (not @state/sm-device))
    (-> (.getDevices (.-hid js/navigator))
        (.then (fn [devices]
          (let [devs (->> (array-seq devices)
                          (filter #(== (.-vendorId ^js %) sm-vendor-id))
                          (remove virtual-mouse?))]
            (when (seq devs)
              (let [unique (dedupe-devices devs)]
                (js/console.log "SM: auto-reconnecting to" (count unique) "previously authorized device(s)")
                (try-open-any! unique true))))))
        (.catch (fn [err]
          (js/console.warn "SM auto-reconnect:" (.-message err)))))))
