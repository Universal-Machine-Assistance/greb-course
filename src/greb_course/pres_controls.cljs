(ns greb-course.pres-controls
  "Presentation controls: keyboard handler, zoom/text-scale UI, toolbar construction."
  (:require [greb-course.dom        :as d]
            [greb-course.i18n       :as i18n]
            [greb-course.state      :as state]
            [greb-course.ui         :as ui]
            [greb-course.animation  :as anim]
            [greb-course.hints      :as hints]
            [greb-course.omnirepl   :as omni]
            [greb-course.sounds     :as sfx]
            [greb-course.spacemouse :as sm]))

(defn make-zoom-controls
  "Build zoom buttons + label. Returns {:zoom-controls, :zoom-label, :set-zoom!}."
  [apply-view!]
  (let [zoom-label  (d/el :span {:class "pres-val-label"} "100%")
        set-zoom!   (fn [z]
                      (let [z (max 0.25 (min 20.0 z))
                            old-z (or (:zoom @state/pres-state) 1.0)
                            zr (if (pos? old-z) (/ z old-z) 1)]
                        (swap! state/pres-state
                          (fn [s]
                            (assoc s :zoom z
                                     :pan-x (* (or (:pan-x s) 0) zr)
                                     :pan-y (* (or (:pan-y s) 0) zr))))
                        (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%"))
                        (apply-view!)))
        zoom-out-btn (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                           :title (i18n/t :zoom-out)}
                                  (d/ic "minus" ""))
                       (.addEventListener "mouseenter" sfx/row-enter-handler)
                       (.addEventListener "click"
                         (fn [] (set-zoom! (- (or (:zoom @state/pres-state) 1.0) 0.1)))))
        zoom-in-btn  (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                           :title (i18n/t :zoom-in)}
                                  (d/ic "plus" ""))
                       (.addEventListener "mouseenter" sfx/row-enter-handler)
                       (.addEventListener "click"
                         (fn [] (set-zoom! (+ (or (:zoom @state/pres-state) 1.0) 0.1)))))
        zoom-controls (d/el :div {:class "pres-ctrl-group"}
                            zoom-out-btn zoom-label zoom-in-btn)]
    {:zoom-controls zoom-controls
     :zoom-label    zoom-label
     :set-zoom!     set-zoom!}))

(defn make-text-scale-controls
  "Build text-scale buttons + label. Returns {:ts-controls, :ts-label, :set-text-scale!}."
  [apply-view!]
  (let [ts-label    (d/el :span {:class "pres-val-label"} "A 100%")
        set-text-scale! (fn [s]
                          (let [s (max 0.5 (min 5.0 s))]
                            (swap! state/pres-state assoc :text-scale s)
                            (set! (.-textContent ts-label) (str "A " (js/Math.round (* s 100)) "%"))
                            (apply-view!)))
        ts-down-btn (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                          :title "Smaller text"}
                                 (d/el :span {:class "pres-ts-icon pres-ts-icon--sm"} "A"))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click"
                        (fn [] (set-text-scale! (- (or (:text-scale @state/pres-state) 1.0) 0.02)))))
        ts-up-btn   (doto (d/el :button {:class "pres-toolbar-btn pres-ctrl-btn"
                                          :title "Bigger text"}
                                 (d/el :span {:class "pres-ts-icon pres-ts-icon--lg"} "A"))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click"
                        (fn [] (set-text-scale! (+ (or (:text-scale @state/pres-state) 1.0) 0.02)))))
        ts-controls (d/el :div {:class "pres-ctrl-group"} ts-down-btn ts-label ts-up-btn)]
    {:ts-controls     ts-controls
     :ts-label        ts-label
     :set-text-scale! set-text-scale!}))

(defn make-toolbar
  "Build the toolbar element with all buttons. Returns the toolbar DOM element.
   `toggle-idx!`       — fn to toggle TOC panel
   `toggle-fs!`        — fn to toggle fullscreen
   `toggle-maximize!`  — fn to toggle slide maximize
   `toggle-section!`   — fn to toggle section mode
   `exit!`             — fn to exit presentation
   `zoom-controls`     — zoom control group element
   `ts-controls`       — text-scale control group element"
  [toggle-idx! toggle-fs! toggle-maximize! toggle-section! exit! zoom-controls ts-controls]
  (let [idx-btn     (doto (d/el :button {:class "pres-toolbar-btn" :title (i18n/t :select-section)}
                                (d/ic "list" ""))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click" toggle-idx!))
        fs-btn      (doto (d/el :button {:class "pres-toolbar-btn" :title (i18n/t :fullscreen)}
                              (d/ic "maximize-2" ""))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click" toggle-fs!))
        max-btn     (doto (d/el :button {:class "pres-toolbar-btn"
                                         :title (i18n/t :maximize-slide)
                                         :aria-label (i18n/t :maximize-slide)}
                                (d/ic "square" ""))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click" toggle-maximize!))
        sec-btn     (doto (d/el :button {:class "pres-toolbar-btn"
                                         :title (i18n/t :section-mode)
                                         :aria-label (i18n/t :section-mode)}
                                (d/ic "layers" ""))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click" toggle-section!))
        exit-btn    (doto (d/el :button {:class "pres-toolbar-btn" :aria-label (i18n/t :close)}
                                (d/ic "x" ""))
                      (.addEventListener "mouseenter" sfx/row-enter-handler)
                      (.addEventListener "click" exit!))
        sm-btn      (when (sm/available?)
                      (doto (d/el :button {:class "pres-toolbar-btn sm-indicator"
                                           :title "SpaceMouse"
                                           :aria-label "SpaceMouse"}
                                  (d/ic "move-3d" "sm-icon"))
                        (.addEventListener "mouseenter" sfx/row-enter-handler)
                        (.addEventListener "click" #(sm/connect!))))
        toolbar-el  (apply d/el :div {:class "pres-toolbar"}
                      (remove nil? [idx-btn zoom-controls ts-controls fs-btn max-btn sec-btn sm-btn exit-btn]))]
    {:toolbar-el toolbar-el
     :fs-btn     fs-btn
     :max-btn    max-btn
     :sm-btn     sm-btn}))

(defn make-keyboard-handler
  "Build the keydown/keyup handlers. Returns {:on-key, :on-keyup}.

   Parameters (map):
   :held-keys     — atom of held keys set
   :ensure-raf!   — fn to start physics rAF loop
   :pan-speed     — atom of current pan speed (px/sec)
   :show-page!    — (fn [idx direction])
   :exit!         — fn to exit presentation
   :set-zoom!     — (fn [z])
   :set-text-scale! — (fn [s])
   :apply-view!   — fn to sync CSS vars
   :zoom-label    — DOM element for zoom %
   :ts-label      — DOM element for text-scale %
   :hl-cursor     — DOM element for highlight cursor
   :phy           — JS physics object
   :slide-els     — vec of slide DOM elements
   :slide-groups  — vec of {:pid, :indices}
   :toggle-idx!   — fn to toggle TOC panel
   :toggle-view!  — fn to toggle view mode
   :toggle-fs!    — fn to toggle fullscreen
   :commit-canvas-zoom! — fn to commit canvas zoom"
  [{:keys [held-keys ensure-raf! pan-speed show-page! exit!
           set-zoom! set-text-scale! apply-view! zoom-label ts-label
           hl-cursor phy slide-els slide-groups toggle-idx! toggle-view! toggle-fs!
           commit-canvas-zoom!]}]
  (let [on-key  (fn [e]
                  (let [k (.-key e)]
                    (if (and (= k "g") (or (.-ctrlKey e) (.-metaKey e)))
                      (do (.preventDefault e) (.stopPropagation e) (omni/toggle!))
                    (when-not (.closest (.-target e) "input, textarea, select")
                    (if (and @state/hint-state (re-matches #"[a-zA-Z]" k))
                      (do (.preventDefault e) (.stopPropagation e)
                          (when-not (hints/hint-type-char! k)
                            (hints/dismiss-hints!)))
                    (if (#{"h" "j" "k" "l" "d" "f"} k)
                      (do (.preventDefault e) (.stopPropagation e)
                          (when-not (contains? @held-keys k)
                            (swap! held-keys conj k)
                            (ensure-raf!)))
                      (case k
                        ("ArrowRight" "ArrowDown" " " ";")
                        (do (.preventDefault e) (.stopPropagation e)
                            (show-page! (inc (:current @state/pres-state)) nil))
                        ("ArrowLeft" "ArrowUp" "n")
                        (do (.preventDefault e) (.stopPropagation e)
                            (show-page! (dec (:current @state/pres-state)) :back))
                        ("Escape" "q")
                        (do (.preventDefault e) (.stopPropagation e)
                            (exit!))
                        ("+" "=")
                        (do (.preventDefault e) (.stopPropagation e)
                            (if @state/canvas-zoom-active?
                              (do (swap! state/canvas-zoom #(min 10.0 (+ % 0.1)))
                                   (apply-view!))
                              (set-zoom! (+ (or (:zoom @state/pres-state) 1.0) 0.1))))
                        "-"
                        (do (.preventDefault e) (.stopPropagation e)
                            (if @state/canvas-zoom-active?
                              (do (swap! state/canvas-zoom #(max 0.25 (- % 0.1)))
                                   (apply-view!))
                              (set-zoom! (- (or (:zoom @state/pres-state) 1.0) 0.1))))
                        "a"
                        (do (.preventDefault e) (.stopPropagation e)
                            (swap! pan-speed #(max 0 (- % 50)))
                            (ui/show-toast! (str "Speed " @pan-speed)))
                        "s"
                        (do (.preventDefault e) (.stopPropagation e)
                            (swap! pan-speed #(min 2000 (+ % 50)))
                            (ui/show-toast! (str "Speed " @pan-speed)))
                        "0"
                        (do (.preventDefault e) (.stopPropagation e)
                            (reset! pan-speed 500)
                            (reset! state/canvas-zoom 1.0)
                            (reset! state/canvas-zoom-active? false)
                            (anim/animate-view-to! state/pres-state
                              {:zoom 1.0 :text-scale (or (:text-scale @state/pres-state) 1.0) :pan-x 0 :pan-y 0}
                              apply-view! :phy phy :duration 500
                              :on-done (fn []
                                         (set! (.-textContent zoom-label) "100%"))))
                        "\\"
                        (do (.preventDefault e) (.stopPropagation e)
                            (swap! state/pres-state assoc :text-scale 1.0)
                            (apply-view!)
                            (set! (.-textContent ts-label) "A 100%"))
                        "]"
                        (do (.preventDefault e) (.stopPropagation e)
                            (set-text-scale! (+ (or (:text-scale @state/pres-state) 1.0) 0.02)))
                        "["
                        (do (.preventDefault e) (.stopPropagation e)
                            (set-text-scale! (- (or (:text-scale @state/pres-state) 1.0) 0.02)))
                        "t"
                        (do (.preventDefault e) (.stopPropagation e)
                            (set-text-scale! (+ (or (:text-scale @state/pres-state) 1.0) 0.02)))
                        "T"
                        (do (.preventDefault e) (.stopPropagation e)
                            (set-text-scale! (- (or (:text-scale @state/pres-state) 1.0) 0.02)))
                        "r"
                        (do (.preventDefault e) (.stopPropagation e)
                            (.reload (.-location js/window)))
                        "z"
                        (do (.preventDefault e) (.stopPropagation e)
                            (.toggle (.-classList hl-cursor) "pres-highlight-cursor--active"))
                        "e"
                        (let [reset-zoom! (fn [& [cb]]
                                            (anim/animate-view-to! state/pres-state
                                              {:zoom 1.0 :text-scale (or (:text-scale @state/pres-state) 1.0) :pan-x 0 :pan-y 0}
                                              apply-view! :phy phy
                                              :on-done (fn []
                                                         (set! (.-textContent zoom-label) "100%")
                                                         (when cb (cb)))))]
                          (do (.preventDefault e) (.stopPropagation e)
                            (if @state/hint-state
                              (do (hints/dismiss-hints!) (reset-zoom!))
                              (let [near-default? (and (<= (js/Math.abs (- (or (:zoom @state/pres-state) 1.0) 1.0)) 0.05)
                                                      (<= (js/Math.abs (or (:pan-x @state/pres-state) 0)) 5)
                                                      (<= (js/Math.abs (or (:pan-y @state/pres-state) 0)) 5))
                                    show-fn (fn []
                                              (when-let [slide (nth slide-els (or (:current @state/pres-state) 0) nil)]
                                                (hints/show-hints! slide
                                                  (fn [el]
                                                    (let [er (hints/text-focused-rect el)
                                                          ex (+ (.-left er) (/ (.-width er) 2))
                                                          ey (+ (.-top er) (/ (.-height er) 2))
                                                          vw (.-innerWidth js/window)
                                                          vh (.-innerHeight js/window)
                                                          sx (- ex (/ vw 2))
                                                          sy (- ey (/ vh 2))
                                                          ew (max 1 (.-width er))
                                                          eh (max 1 (.-height er))
                                                          z  (min 4.0 (max 1.2 (* 0.65 (min (/ vw ew) (/ vh eh)))))]
                                                      (anim/animate-view-to! state/pres-state
                                                        {:zoom z :text-scale (or (:text-scale @state/pres-state) 1.0)
                                                         :pan-x (- (* sx z)) :pan-y (- (* sy z))}
                                                        apply-view! :phy phy
                                                        :on-done (fn []
                                                                   (set! (.-textContent zoom-label) (str (js/Math.round (* z 100)) "%")))))))))]
                                (if near-default?
                                  (show-fn)
                                  (reset-zoom! show-fn))))))
                        ("1" "2" "3" "4" "5" "6" "7" "8" "9")
                        (do (.preventDefault e) (.stopPropagation e)
                          (let [num (js/parseInt k 10)]
                            (when (and (<= 1 num) (<= num (count slide-groups)))
                              (show-page! (first (:indices (nth slide-groups (dec num)))) nil))))
                        "?"
                        (do (.preventDefault e) (.stopPropagation e)
                            (ui/toggle-shortcuts! ui/pres-shortcuts))
                        "i"
                        (do (.preventDefault e) (.stopPropagation e)
                            (toggle-idx!))
                        "y"
                        (do (.preventDefault e) (.stopPropagation e)
                            (if @state/canvas-zoom-active?
                              (do (commit-canvas-zoom!)
                                  (reset! state/canvas-zoom-active? false)
                                  (ui/show-toast! "Canvas zoom OFF"))
                              (do (reset! state/canvas-zoom-active? true)
                                  (ui/show-toast! "Canvas zoom ON — f/d to zoom, y to exit" 2000)))
                            (apply-view!))
                        "p"
                        (do (.preventDefault e) (.stopPropagation e)
                            (.toggle (.-classList (.-documentElement js/document)) "ui-hidden"))
                        "u"
                        (do (.preventDefault e) (.stopPropagation e)
                            (toggle-view!))
                        "o"
                        (do (.preventDefault e) (.stopPropagation e)
                            (toggle-fs!))
                        nil)))))))
        on-keyup (fn [e]
                   (let [k (.-key e)]
                     (when (#{"h" "j" "k" "l" "d" "f"} k)
                       (swap! held-keys disj k))))]
    {:on-key   on-key
     :on-keyup on-keyup}))
