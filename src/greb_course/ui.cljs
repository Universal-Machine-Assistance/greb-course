(ns greb-course.ui
  "Toast notifications, status badge, and shortcuts overlay."
  (:require [greb-course.dom    :as d]
            [greb-course.state  :as state]
            [greb-course.sounds :as sfx]))

(defn- ensure-status-badge! []
  (when-not @state/ui-status-el
    (let [el (d/el :div {:class "ui-status-badge"})]
      (.appendChild (.-body js/document) el)
      (reset! state/ui-status-el el)))
  @state/ui-status-el)

(defn update-status-badge! [zoom text-scale]
  (let [el (ensure-status-badge!)
        z  (js/Math.round (* (or zoom 1.0) 100))
        ts (js/Math.round (* (or text-scale 1.0) 100))
        parts (cond-> []
                (not= z 100)  (conj (str z "%"))
                (not= ts 100) (conj (str "T " ts "%")))]
    (if (seq parts)
      (do (set! (.-textContent el) (apply str (interpose "  " parts)))
          (.remove (.-classList el) "empty"))
      (do (set! (.-textContent el) "")
          (.add (.-classList el) "empty")))))

(defn show-toast!
  ([text] (show-toast! text 800))
  ([text duration-ms]
   (when-not @state/toast-el
     (let [el (d/el :div {:class "speed-toast"})]
       (.appendChild (.-body js/document) el)
       (reset! state/toast-el el)))
   (let [el @state/toast-el]
     (set! (.-textContent el) text)
     (.add (.-classList el) "visible")
     (when-let [t @state/toast-timer] (js/clearTimeout t))
     (reset! state/toast-timer
       (js/setTimeout #(.remove (.-classList el) "visible") duration-ms)))))

(defn show-toast-rich!
  "Show a toast with arbitrary DOM children instead of plain text."
  [children duration-ms]
  (when-not @state/toast-el
    (let [el (d/el :div {:class "speed-toast"})]
      (.appendChild (.-body js/document) el)
      (reset! state/toast-el el)))
  (let [el @state/toast-el]
    (set! (.-innerHTML el) "")
    (doseq [ch (if (sequential? children) children [children])]
      (if (string? ch)
        (.appendChild el (.createTextNode js/document ch))
        (.appendChild el ch)))
    (.add (.-classList el) "visible")
    (when-let [t @state/toast-timer] (js/clearTimeout t))
    (reset! state/toast-timer
      (js/setTimeout #(.remove (.-classList el) "visible") duration-ms))))

;; ── Shortcuts help overlay ──────────────────────────────────

(def pres-shortcuts
  [["; / Space / →" "Next slide"]
   ["n / ←"         "Previous slide"]
   ["h / j / k / l" "Pan left / down / up / right"]
   ["a / s"         "Slower / faster pan"]
   ["f / + / ="     "Zoom in"]
   ["d / -"         "Zoom out"]
   ["] / t"         "Text bigger"]
   ["[ / T"         "Text smaller (Shift+t)"]
   ["r"             "Refresh page"]
   ["0"             "Reset position & zoom"]
   ["\\"            "Reset text size"]
   ["y"             "Canvas zoom (no layout change)"]
   ["z"             "Toggle spotlight"]
   ["e"             "Element hints (type letters to jump)"]
   ["i"             "Toggle index"]
   ["p"             "Toggle UI"]
   ["u"             "Switch to page mode"]
   ["o"             "Toggle full screen"]
   ["Ctrl/Cmd+G"    "Omnibar"]
   ["q / Esc"       "Exit presentation"]
   ["?"             "Show / hide shortcuts"]])

(def doc-shortcuts
  [["; / → / Space"  "Next page"]
   ["n / ←"          "Previous page"]
   ["h / j / k / l"  "Pan left / down / up / right"]
   ["a / s"          "Slower / faster pan"]
   ["f / + / ="      "Zoom in"]
   ["d / -"          "Zoom out"]
   ["] / t"          "Text bigger"]
   ["[ / T"          "Text smaller (Shift+t)"]
   ["r"              "Refresh page"]
   ["0"              "Reset position & zoom"]
   ["\\"             "Reset text size"]
   ["y"              "Canvas zoom (no layout change)"]
   ["i"              "Toggle index"]
   ["p"              "Toggle UI"]
   ["z"              "Toggle spotlight"]
   ["e"              "Element hints (type letters to jump)"]
   ["Ctrl/Cmd+G"     "Omnibar"]
   ["u"              "Switch to slide mode"]
   ["o"              "Toggle full screen"]
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

(defn toggle-shortcuts! [shortcuts]
  (if-let [el @state/shortcuts-overlay]
    (do (when (.-parentNode el) (.remove el))
        (reset! state/shortcuts-overlay nil))
    (let [scrim (d/el :div {:class "shortcuts-scrim"})
          panel (build-shortcuts-panel shortcuts)]
      (.addEventListener scrim "click" #(toggle-shortcuts! shortcuts))
      (.appendChild scrim panel)
      (.appendChild (.-body js/document) scrim)
      (sfx/play! :drop)
      (reset! state/shortcuts-overlay scrim))))
