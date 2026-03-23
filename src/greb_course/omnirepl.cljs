(ns greb-course.omnirepl
  "Floating omnibar UI: search pages, run commands, open docs."
  (:require [greb-course.dom              :as d]
            [greb-course.state            :as state]
            [greb-course.ui               :as ui]
            [greb-course.sounds           :as sfx]
            [greb-course.omnirepl-commands :as cmds]
            [clojure.string               :as str]))

(declare dismiss!)
(defonce ^:private omni-state (atom nil))

;; ── Result renderers ─────────────────────────────────────────

(defn- render-rows! [results-el entries selected on-click row-fn]
  (set! (.-innerHTML results-el) "")
  (doseq [[i entry] (map-indexed vector (take 12 entries))]
    (let [row (row-fn i entry selected)]
      (.addEventListener row "click" (fn [] (on-click i)))
      (.addEventListener row "mouseenter" sfx/row-enter-handler)
      (.appendChild results-el row))))

(defn- page-row [i {:keys [id label page section]} selected]
  (d/el :div {:class (str "omni-row" (when (= i selected) " omni-row--selected"))}
    (d/el :span {:class "omni-row-section"} (or section ""))
    (d/el :span {:class "omni-row-label"} (or label id))
    (when page (d/el :span {:class "omni-row-page"} (str page)))))

(defn- course-row [i {:keys [id label org]} selected]
  (d/el :div {:class (str "omni-row" (when (= i selected) " omni-row--selected"))}
    (d/el :span {:class "omni-row-section"} (or org ""))
    (d/el :span {:class "omni-row-label"} (or label id))))

(defn- command-row [i {:keys [label desc]} selected]
  (d/el :div {:class (str "omni-row" (when (= i selected) " omni-row--selected"))}
    (d/el :span {:class "omni-row-section"} (or desc ""))
    (d/el :span {:class "omni-row-label"} (or label ""))))

(defn- render-course-results! [results-el entries selected on-click]
  (set! (.-innerHTML results-el) "")
  ;; Home row always first
  (let [home (d/el :div {:class (str "omni-row omni-row--home" (when (= selected 0) " omni-row--selected"))}
               (d/el :span {:class "omni-row-section"} "")
               (d/el :span {:class "omni-row-label"} "Home (catalog)"))]
    (.addEventListener home "click" (fn [] (sfx/play! :pop) (cmds/go-home!) (dismiss!)))
    (.addEventListener home "mouseenter" sfx/row-enter-handler)
    (.appendChild results-el home))
  (doseq [[i entry] (map-indexed vector (take 11 entries))]
    (let [row (course-row i entry (dec selected))]
      (.addEventListener row "click" (fn [] (on-click i)))
      (.addEventListener row "mouseenter" sfx/row-enter-handler)
      (.appendChild results-el row))))

;; ── Core UI ──────────────────────────────────────────────────

(defn dismiss! []
  (when-let [{:keys [scrim on-key]} @omni-state]
    (.removeEventListener js/document "keydown" on-key true)
    (when (.-parentNode scrim) (.remove scrim))
    (reset! omni-state nil)))

(defn show! []
  (when @omni-state (dismiss!))
  (let [page-entries   (or (cmds/build-entries) [])
        course-entries (cmds/build-course-entries)
        selected       (atom 0)
        filtered       (atom page-entries)
        mode           (atom :pages)
        scrim          (d/el :div {:class "omni-scrim"})
        bar            (d/el :div {:class "omni-bar"})
        input-el       (d/el :input {:class "omni-input" :type "text"
                                      :placeholder "Search pages, open doc, go 1, home..."})
        results-el     (d/el :div {:class "omni-results"})
        update!*       (atom nil)
        commit-course! (fn [i]
                         (when-let [entry (nth @filtered i nil)]
                           (sfx/play! :pop) (cmds/open-course! entry) (dismiss!)))
        commit-page!   (fn [i]
                         (when-let [entry (nth @filtered i nil)]
                           (sfx/play! :pop) (cmds/navigate-to-spread! (:spread-idx entry)) (dismiss!)))
        commit-cmd!    (fn [i]
                         (when-let [entry (nth @filtered i nil)]
                           (set! (.-value input-el) (str (:id entry) " "))
                           (.focus input-el) (@update!*)))
        render!        (fn []
                         (case @mode
                           :courses  (render-course-results! results-el @filtered @selected commit-course!)
                           :commands (render-rows! results-el @filtered @selected commit-cmd! command-row)
                           (render-rows! results-el @filtered @selected commit-page! page-row)))
        commit!        (fn []
                         (let [val (.-value input-el)]
                           (cond
                             (and (= @mode :courses) (pos? (count @filtered)))
                             (if (= @selected 0)
                               (do (sfx/play! :pop) (cmds/go-home!) (dismiss!))
                               (commit-course! (dec @selected)))
                             (= @mode :commands)
                             (when-let [entry (nth @filtered @selected nil)]
                               (set! (.-value input-el) (str (:id entry) " "))
                               (.focus input-el) (@update!*))
                             (and (> (count val) 0) (cmds/eval-command val))
                             (do (sfx/play! :pop) (dismiss!))
                             :else (commit-page! @selected))))
        update!        (fn []
                         (let [q (.-value input-el)
                               op (cmds/open-prefix? q)]
                           (cond
                             (some? op)
                             (do (reset! mode :courses)
                                 (reset! filtered (cmds/match-entries op course-entries))
                                 (reset! selected 0))
                             (and (> (count q) 0) (= (first q) \())
                             (do (reset! mode :pages) (reset! filtered []))
                             (and (> (count q) 0) (seq (cmds/match-commands q)))
                             (do (reset! mode :commands)
                                 (reset! filtered (cmds/match-commands q))
                                 (reset! selected 0))
                             :else
                             (do (reset! mode :pages)
                                 (reset! filtered (cmds/match-entries q page-entries))
                                 (reset! selected 0)))
                           (render!)))
        _              (reset! update!* update!)
        max-sel        (fn [] (if (= @mode :courses) (count @filtered) (dec (count @filtered))))
        on-key         (fn [e]
                         (.stopImmediatePropagation e)
                         (case (.-key e)
                           "Escape"    (do (.preventDefault e) (dismiss!))
                           "Enter"     (do (.preventDefault e) (commit!))
                           "ArrowDown" (do (.preventDefault e) (swap! selected #(min (max-sel) (inc %))) (render!))
                           "ArrowUp"   (do (.preventDefault e) (swap! selected #(max 0 (dec %))) (render!))
                           "Tab"       (do (.preventDefault e)
                                         (cond
                                           (= @mode :courses)
                                           (when-let [entry (nth @filtered (if (= @selected 0) 0 (dec @selected)) nil)]
                                             (set! (.-value input-el) (str "open " (:id entry))) (@update!*))
                                           (= @mode :commands)
                                           (when-let [entry (nth @filtered @selected nil)]
                                             (set! (.-value input-el) (str (:id entry) " ")) (@update!*))))
                           nil))]
    (.addEventListener input-el "input" update!)
    (.addEventListener scrim "click" (fn [e] (when (= (.-target e) scrim) (dismiss!))))
    (.appendChild bar input-el) (.appendChild bar results-el) (.appendChild scrim bar)
    (.appendChild (.-body js/document) scrim)
    (if (empty? page-entries)
      (do (reset! mode :courses) (reset! filtered course-entries)
          (render-course-results! results-el course-entries 0 commit-course!))
      (render-rows! results-el page-entries 0 commit-page! page-row))
    (.addEventListener js/document "keydown" on-key true)
    (reset! omni-state {:scrim scrim :on-key on-key})
    (sfx/play! :drop) (.focus input-el)))

(defn toggle! [] (if @omni-state (dismiss!) (show!)))

(defn try-eval-command! [s] (boolean (cmds/eval-command (str s))))

;; ── Embedded demo bar ────────────────────────────────────────

(defn- mount-embedded-in! [host]
  (when (and host (not (.getAttribute host "data-omni-embedded")))
    (.setAttribute host "data-omni-embedded" "1")
    (let [page-entries   (or (cmds/build-entries) [])
          course-entries (cmds/build-course-entries)
          selected       (atom 0)
          filtered       (atom page-entries)
          mode           (atom :pages)
          bar            (d/el :div {:class "omni-embed-bar"})
          input-el       (d/el :input {:class "omni-input omni-input--embedded" :type "text"
                                       :placeholder "Buscar página, go 1, pages, reset…"})
          results-el     (d/el :div {:class "omni-results omni-results--embedded"})
          update!*       (atom nil)
          commit-course! (fn [i]
                           (when-let [entry (nth @filtered i nil)]
                             (sfx/play! :pop) (cmds/open-course! entry)))
          commit-page!   (fn [i]
                           (when-let [entry (nth @filtered i nil)]
                             (sfx/play! :pop) (cmds/navigate-to-spread! (:spread-idx entry))
                             (ui/show-toast! (str "→ " (or (:label entry) (:id entry))) 1400)))
          commit-cmd!    (fn [i]
                           (when-let [entry (nth @filtered i nil)]
                             (set! (.-value input-el) (str (:id entry) " "))
                             (.focus input-el) (@update!*)))
          render!        (fn []
                           (case @mode
                             :courses  (render-course-results! results-el @filtered @selected commit-course!)
                             :commands (render-rows! results-el @filtered @selected commit-cmd! command-row)
                             (render-rows! results-el @filtered @selected commit-page! page-row)))
          commit!        (fn []
                           (let [val (str/trim (.-value input-el))]
                             (cond
                               (and (= @mode :courses) (pos? (count @filtered)))
                               (if (= @selected 0)
                                 (do (sfx/play! :pop) (cmds/go-home!) (ui/show-toast! "Catálogo" 1200))
                                 (commit-course! (dec @selected)))
                               (= @mode :commands)
                               (when-let [entry (nth @filtered @selected nil)]
                                 (set! (.-value input-el) (str (:id entry) " ")) (@update!*))
                               (and (pos? (count val)) (try-eval-command! val))
                               (do (sfx/play! :pop) (set! (.-value input-el) "") (@update!*))
                               :else (commit-page! @selected))))
          update!        (fn []
                           (let [q (.-value input-el)
                                 op (cmds/open-prefix? q)]
                             (cond
                               (some? op)
                               (do (reset! mode :courses)
                                   (reset! filtered (cmds/match-entries op course-entries))
                                   (reset! selected 0))
                               (and (pos? (count q)) (= (first q) \())
                               (do (reset! mode :pages) (reset! filtered []))
                               (and (pos? (count q)) (seq (cmds/match-commands q)))
                               (do (reset! mode :commands)
                                   (reset! filtered (cmds/match-commands q))
                                   (reset! selected 0))
                               :else
                               (do (reset! mode :pages)
                                   (reset! filtered (cmds/match-entries q page-entries))
                                   (reset! selected 0)))
                             (render!)))
          _              (reset! update!* update!)
          max-sel        (fn [] (if (= @mode :courses) (count @filtered) (dec (max 1 (count @filtered)))))
          on-key         (fn [e]
                           (.stopPropagation e)
                           (case (.-key e)
                             "Enter"     (do (.preventDefault e) (commit!))
                             "ArrowDown" (do (.preventDefault e) (swap! selected #(min (max-sel) (inc %))) (render!))
                             "ArrowUp"   (do (.preventDefault e) (swap! selected #(max 0 (dec %))) (render!))
                             "Tab"       (when (#{:courses :commands} @mode)
                                           (.preventDefault e)
                                           (cond
                                             (= @mode :courses)
                                             (when-let [entry (nth @filtered (if (= @selected 0) 0 (dec @selected)) nil)]
                                               (set! (.-value input-el) (str "open " (:id entry))) (@update!*))
                                             (= @mode :commands)
                                             (when-let [entry (nth @filtered @selected nil)]
                                               (set! (.-value input-el) (str (:id entry) " ")) (@update!*))))
                             nil))]
      (.addEventListener input-el "input" update!)
      (.addEventListener input-el "keydown" on-key)
      (.appendChild bar input-el) (.appendChild bar results-el) (.appendChild host bar)
      (if (empty? page-entries)
        (do (reset! mode :courses) (reset! filtered course-entries)
            (render-course-results! results-el course-entries 0 commit-course!))
        (render-rows! results-el page-entries 0 commit-page! page-row)))))

(defn mount-embedded-hosts! []
  (doseq [host (.querySelectorAll js/document ".omni-embed-host")]
    (mount-embedded-in! host)))
