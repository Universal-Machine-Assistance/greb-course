(ns greb-course.omnirepl
  "Floating omnibar: search pages, run commands like (go 1), (open doc)."
  (:require [greb-course.dom    :as d]
            [greb-course.state  :as state]
            [greb-course.ui     :as ui]
            [greb-course.editor :as editor]
            [greb-course.sounds :as sfx]
            [clojure.string     :as str]))

(declare dismiss!)

(defonce ^:private omni-state (atom nil))

;; ── Searchable entries ──

(defn- build-entries
  "Collect all navigable pages from toc-groups + id->spread."
  []
  (when-let [{:keys [toc-groups id->spread]} @state/current-nav]
    (vec
      (for [{:keys [label entries]} toc-groups
            {:keys [id label page icon] :as entry} entries
            :when (get id->spread id)]
        {:id id :label label :page page :icon icon
         :type :page :section label :spread-idx (get id->spread id)}))))

(defn- build-course-entries
  "Collect all available courses for (open ...) autocomplete."
  []
  (vec
    (for [c (or @state/current-courses [])
          :let [m (:meta c)]]
      {:id    (:id m)
       :label (:title m)
       :org   (:org m)
       :slug  (:slug m)
       :desc  (:description m)
       :type  :course})))

(defn- match-entries [query entries]
  (if (empty? query)
    entries
    (let [q (str/lower-case query)]
      (filter (fn [{:keys [label section id desc]}]
                (or (str/includes? (str/lower-case (or label "")) q)
                    (str/includes? (str/lower-case (or section "")) q)
                    (str/includes? (str/lower-case (or id "")) q)
                    (str/includes? (str/lower-case (or desc "")) q)))
              entries))))

;; ── Navigation helpers ──

(defn- navigate-to-spread! [idx]
  (when-let [{:keys [go!]} @state/current-nav]
    (go! idx nil)))

(defn- navigate-to-page-id! [page-id]
  (when-let [{:keys [go! id->spread]} @state/current-nav]
    (when-let [idx (get id->spread page-id)]
      (go! idx nil))))

(defn- open-course! [{:keys [org slug]}]
  (set! (.-location js/window) (str "/" org "/" slug "/")))

(defn- go-home! []
  (set! (.-location js/window) "/"))

;; ── Command evaluation ──

(defn- eval-command
  "Evaluate simple REPL commands. Returns true if handled."
  [input]
  (let [s (str/trim input)]
    (cond
      ;; (home) — go to catalog
      (re-matches #"\(home\)" s)
      (do (go-home!) true)

      ;; (open id) — open course by id
      (re-matches #"\(open\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)
      (let [[_ id] (re-matches #"\(open\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)
            courses (build-course-entries)
            match (first (filter #(= (:id %) id) courses))]
        (if match
          (do (open-course! match) true)
          (do (ui/show-toast! (str "Unknown document: " id) 2000) true)))

      ;; (go N) — go to spread/page N (1-based)
      (re-matches #"\(go\s+(\d+)\)" s)
      (let [[_ n] (re-matches #"\(go\s+(\d+)\)" s)
            idx (dec (js/parseInt n 10))]
        (navigate-to-spread! idx)
        (ui/show-toast! (str "Page " n))
        true)

      ;; (go "id") or (go id) — go to page by id
      (re-matches #"\(go\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)
      (let [[_ id] (re-matches #"\(go\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)]
        (navigate-to-page-id! id)
        (ui/show-toast! (str "\u2192 " id))
        true)

      ;; (reset) — reset zoom/pan
      (re-matches #"\(reset\)" s)
      (do (reset! state/doc-view {:zoom 1.0 :text-scale 1.0 :pan-x 0 :pan-y 0})
          (ui/show-toast! "Reset")
          true)

      ;; (zoom N) — set zoom
      (re-matches #"\(zoom\s+([\d.]+)\)" s)
      (let [[_ n] (re-matches #"\(zoom\s+([\d.]+)\)" s)]
        (swap! state/doc-view assoc :zoom (js/parseFloat n))
        (ui/show-toast! (str "Zoom " n))
        true)

      ;; (pages) — show page count
      (re-matches #"\(pages\)" s)
      (let [n (count (or (:spread-ids @state/current-nav) []))]
        (ui/show-toast! (str n " pages") 2000)
        true)

      ;; (edit) — open page editor
      (re-matches #"\(edit\)" s)
      (do (editor/edit!) true)

      ;; (ask "...") — one-shot Claude reply in a toast
      (re-matches #"\(ask\s+\"([^\"]*)\"\)" s)
      (let [[_ q] (re-matches #"\(ask\s+\"([^\"]*)\"\)" s)]
        (-> (editor/anthropic-quick-ask! q)
            (.then (fn [reply] (ui/show-toast! reply 28000)))
            (.catch (fn [e] (ui/show-toast! (str "Ask failed: " (.-message e)) 14000))))
        true)

      ;; ── Bare commands (without parens) ──

      ;; edit
      (re-matches #"(?i)edit" s)
      (do (editor/edit!) true)

      ;; ask hello world … (Claude quick reply)
      (re-matches #"(?i)ask\s+(.+)" s)
      (let [[_ q] (re-matches #"(?i)ask\s+(.+)" s)]
        (-> (editor/anthropic-quick-ask! (str/trim q))
            (.then (fn [reply] (ui/show-toast! reply 28000)))
            (.catch (fn [e] (ui/show-toast! (str "Ask failed: " (.-message e)) 14000))))
        true)

      ;; home
      (re-matches #"(?i)home" s)
      (do (go-home!) true)

      ;; open id
      (re-matches #"(?i)open\s+\"?([a-zA-Z0-9_-]+)\"?" s)
      (let [[_ id] (re-matches #"(?i)open\s+\"?([a-zA-Z0-9_-]+)\"?" s)
            courses (build-course-entries)
            match (first (filter #(= (:id %) id) courses))]
        (if match
          (do (open-course! match) true)
          (do (ui/show-toast! (str "Unknown document: " id) 2000) true)))

      ;; go N or go id
      (re-matches #"(?i)go\s+(\S+)" s)
      (let [[_ arg] (re-matches #"(?i)go\s+(\S+)" s)]
        (if (re-matches #"\d+" arg)
          (let [idx (dec (js/parseInt arg 10))]
            (navigate-to-spread! idx)
            (ui/show-toast! (str "Page " arg))
            true)
          (do (navigate-to-page-id! arg)
              (ui/show-toast! (str "\u2192 " arg))
              true)))

      ;; reset
      (re-matches #"(?i)reset" s)
      (do (reset! state/doc-view {:zoom 1.0 :text-scale 1.0 :pan-x 0 :pan-y 0})
          (ui/show-toast! "Reset")
          true)

      ;; pages
      (re-matches #"(?i)pages" s)
      (let [n (count (or (:spread-ids @state/current-nav) []))]
        (ui/show-toast! (str n " pages") 2000)
        true)

      ;; zoom N
      (re-matches #"(?i)zoom\s+([\d.]+)" s)
      (let [[_ n] (re-matches #"(?i)zoom\s+([\d.]+)" s)]
        (swap! state/doc-view assoc :zoom (js/parseFloat n))
        (ui/show-toast! (str "Zoom " n))
        true)

      ;; Not a command
      :else false)))

;; ── Detect (open ...) prefix for live autocomplete ──

(defn- open-prefix?
  "Returns the partial arg after '(open ' or 'open ' or nil."
  [text]
  (or
    (when-let [[_ arg] (re-matches #"\(open\s+(.*)" text)]
      (str/replace arg #"[\")\s]+$" ""))
    (when-let [[_ arg] (re-matches #"(?i)open\s+(.*)" text)]
      (str/replace arg #"[\")\s]+$" ""))))

;; ── Available commands for bare-word autocomplete ──

(def ^:private command-entries
  [{:id "open"   :label "open <doc>"  :desc "Open a document"       :type :command}
   {:id "go"     :label "go <page>"   :desc "Go to page number/id"  :type :command}
   {:id "home"   :label "home"        :desc "Go to catalog"         :type :command}
   {:id "reset"  :label "reset"       :desc "Reset zoom and pan"    :type :command}
   {:id "zoom"   :label "zoom <n>"    :desc "Set zoom level"        :type :command}
   {:id "pages"  :label "pages"       :desc "Show page count"       :type :command}
   {:id "edit"   :label "edit"        :desc "Edit current page (Monaco + Claude)" :type :command}
   {:id "ask"    :label "ask <msg>"   :desc "Quick question to Claude (toast)"     :type :command}])

(defn- match-commands [query]
  (if (empty? query)
    []
    (let [q (str/lower-case query)]
      (filter (fn [{:keys [id label desc]}]
                (or (str/includes? id q)
                    (str/includes? (str/lower-case label) q)
                    (str/includes? (str/lower-case desc) q)))
              command-entries))))

;; ── UI ──

(defn- render-page-results! [results-el entries selected on-click]
  (set! (.-innerHTML results-el) "")
  (doseq [[i {:keys [id label page section]}] (map-indexed vector (take 12 entries))]
    (let [row (d/el :div {:class (str "omni-row" (when (= i selected) " omni-row--selected"))}
                    (d/el :span {:class "omni-row-section"} (or section ""))
                    (d/el :span {:class "omni-row-label"} (or label id))
                    (when page (d/el :span {:class "omni-row-page"} (str page))))]
      (.addEventListener row "click" (fn [] (on-click i)))
      (.addEventListener row "mouseenter" sfx/row-enter-handler)
      (.appendChild results-el row))))

(defn- render-course-results! [results-el entries selected on-click]
  (set! (.-innerHTML results-el) "")
  ;; Home row always first
  (let [home-row (d/el :div {:class (str "omni-row omni-row--home" (when (= selected 0) " omni-row--selected"))}
                       (d/el :span {:class "omni-row-section"} "")
                       (d/el :span {:class "omni-row-label"} "Home (catalog)"))]
    (.addEventListener home-row "click" (fn [] (sfx/play! :pop) (go-home!) (dismiss!)))
    (.addEventListener home-row "mouseenter" sfx/row-enter-handler)
    (.appendChild results-el home-row))
  (doseq [[i {:keys [id label org desc]}] (map-indexed vector (take 11 entries))]
    (let [row (d/el :div {:class (str "omni-row" (when (= (inc i) selected) " omni-row--selected"))}
                    (d/el :span {:class "omni-row-section"} (or org ""))
                    (d/el :span {:class "omni-row-label"} (or label id)))]
      (.addEventListener row "click" (fn [] (on-click i)))
      (.addEventListener row "mouseenter" sfx/row-enter-handler)
      (.appendChild results-el row))))

(defn- render-command-results! [results-el entries selected on-click]
  (set! (.-innerHTML results-el) "")
  (doseq [[i {:keys [label desc]}] (map-indexed vector (take 12 entries))]
    (let [row (d/el :div {:class (str "omni-row" (when (= i selected) " omni-row--selected"))}
                    (d/el :span {:class "omni-row-section"} (or desc ""))
                    (d/el :span {:class "omni-row-label"} (or label "")))]
      (.addEventListener row "click" (fn [] (on-click i)))
      (.addEventListener row "mouseenter" sfx/row-enter-handler)
      (.appendChild results-el row))))

(defn dismiss! []
  (when-let [{:keys [scrim on-key]} @omni-state]
    (.removeEventListener js/document "keydown" on-key true)
    (when (.-parentNode scrim) (.remove scrim))
    (reset! omni-state nil)))

(defn show! []
  (when @omni-state (dismiss!))
  (let [page-entries   (or (build-entries) [])
        course-entries (build-course-entries)
        selected       (atom 0)
        filtered       (atom page-entries)
        mode           (atom :pages)   ;; :pages or :courses
        scrim          (d/el :div {:class "omni-scrim"})
        bar            (d/el :div {:class "omni-bar"})
        input-el       (d/el :input {:class "omni-input" :type "text"
                                      :placeholder "Search pages, open doc, go 1, home..."})
        results-el     (d/el :div {:class "omni-results"})
        update!*       (atom nil)
        commit-course! (fn [i]
                         (let [entry (nth @filtered i nil)]
                           (when entry
                             (sfx/play! :pop)
                             (open-course! entry)
                             (dismiss!))))
        commit-page!   (fn [i]
                         (let [entry (nth @filtered i nil)]
                           (when entry
                             (sfx/play! :pop)
                             (navigate-to-spread! (:spread-idx entry))
                             (dismiss!))))
        commit-cmd!    (fn [i]
                         (let [entry (nth @filtered i nil)]
                           (when entry
                             (set! (.-value input-el) (str (:id entry) " "))
                             (.focus input-el)
                             (@update!*))))
        commit!        (fn []
                         (let [val (.-value input-el)]
                           (cond
                             ;; In courses mode, commit selected course
                             (and (= @mode :courses) (pos? (count @filtered)))
                             (if (= @selected 0)
                               (do (sfx/play! :pop) (go-home!) (dismiss!))
                               (commit-course! (dec @selected)))
                             ;; In commands mode, fill selected command
                             (= @mode :commands)
                             (let [entry (nth @filtered @selected nil)]
                               (when entry
                                 (set! (.-value input-el) (str (:id entry) " "))
                                 (.focus input-el)
                                 (@update!*)))
                             ;; Try evaluating as command (with or without parens)
                             (and (> (count val) 0) (eval-command val))
                             (do (sfx/play! :pop) (dismiss!))
                             ;; Regular page search
                             :else
                             (commit-page! @selected))))
        render!        (fn []
                         (case @mode
                           :courses  (render-course-results! results-el @filtered @selected commit-course!)
                           :commands (render-command-results! results-el @filtered @selected commit-cmd!)
                           (render-page-results! results-el @filtered @selected commit-page!)))
        update!        (fn []
                         (let [q (.-value input-el)
                               op (open-prefix? q)]
                           (cond
                             ;; "open foo" or "(open foo" — course autocomplete
                             (some? op)
                             (do (reset! mode :courses)
                                 (reset! filtered (match-entries op course-entries))
                                 (reset! selected 0))
                             ;; Paren command being typed — no results
                             (and (> (count q) 0) (= (first q) \())
                             (do (reset! mode :pages)
                                 (reset! filtered []))
                             ;; Bare word matches a command name — show commands
                             (and (> (count q) 0) (seq (match-commands q)))
                             (do (reset! mode :commands)
                                 (reset! filtered (match-commands q))
                                 (reset! selected 0))
                             ;; Regular page search
                             :else
                             (do (reset! mode :pages)
                                 (reset! filtered (match-entries q page-entries))
                                 (reset! selected 0)))
                           (render!)))
        _              (reset! update!* update!)
        max-sel        (fn []
                         (if (= @mode :courses)
                           (count @filtered)  ;; +1 for home row, but 0-indexed offset
                           (dec (count @filtered))))
        on-key         (fn [e]
                         ;; Stop ALL keys from reaching doc/pres handlers while omnibar is open
                         (.stopImmediatePropagation e)
                         (let [k (.-key e)]
                           (case k
                             "Escape"
                             (do (.preventDefault e) (dismiss!))
                             "Enter"
                             (do (.preventDefault e) (commit!))
                             "ArrowDown"
                             (do (.preventDefault e)
                                 (swap! selected #(min (max-sel) (inc %)))
                                 (render!))
                             "ArrowUp"
                             (do (.preventDefault e)
                                 (swap! selected #(max 0 (dec %)))
                                 (render!))
                             "Tab"
                             (do (.preventDefault e)
                                 (cond
                                   ;; Tab-complete: fill selected course id into input
                                   (= @mode :courses)
                                   (when-let [entry (nth @filtered (if (= @selected 0) 0 (dec @selected)) nil)]
                                     (set! (.-value input-el) (str "open " (:id entry)))
                                     (@update!*))
                                   ;; Tab-complete: fill selected command
                                   (= @mode :commands)
                                   (when-let [entry (nth @filtered @selected nil)]
                                     (set! (.-value input-el) (str (:id entry) " "))
                                     (@update!*))))
                             nil)))]
    (.addEventListener input-el "input" update!)
    (.addEventListener scrim "click"
      (fn [e] (when (= (.-target e) scrim) (dismiss!))))
    (.appendChild bar input-el)
    (.appendChild bar results-el)
    (.appendChild scrim bar)
    (.appendChild (.-body js/document) scrim)
    ;; On home (no pages), show courses by default
    (if (empty? page-entries)
      (do (reset! mode :courses)
          (reset! filtered course-entries)
          (render-course-results! results-el course-entries 0 commit-course!))
      (render-page-results! results-el page-entries 0 commit-page!))
    (.addEventListener js/document "keydown" on-key true)
    (reset! omni-state {:scrim scrim :on-key on-key})
    (sfx/play! :drop)
    (.focus input-el)))

(defn toggle! []
  (if @omni-state (dismiss!) (show!)))

(defn try-eval-command!
  "Evaluate REPL / bare command string (same rules as omnibar Enter). Returns true if handled."
  [s]
  (boolean (eval-command (str s))))

;; ── Embedded demo bar (manual page) — in-page search + commands, no modal ──

(defn- mount-embedded-in! [host]
  (when (and host (not (.getAttribute host "data-omni-embedded")))
    (.setAttribute host "data-omni-embedded" "1")
    (let [page-entries   (or (build-entries) [])
          course-entries (build-course-entries)
          selected       (atom 0)
          filtered       (atom page-entries)
          mode           (atom :pages)
          bar            (d/el :div {:class "omni-embed-bar"})
          input-el       (d/el :input {:class "omni-input omni-input--embedded" :type "text"
                                      :placeholder "Buscar página, go 1, pages, reset…"
                                      :aria-label "OmniREPL embebido"})
          results-el     (d/el :div {:class "omni-results omni-results--embedded"})
          update!*       (atom nil)
          commit-course! (fn [i]
                           (let [entry (nth @filtered i nil)]
                             (when entry
                               (sfx/play! :pop)
                               (open-course! entry))))
          commit-page!   (fn [i]
                           (let [entry (nth @filtered i nil)]
                             (when entry
                               (sfx/play! :pop)
                               (navigate-to-spread! (:spread-idx entry))
                               (ui/show-toast!
                                 (str "→ " (or (:label entry) (:id entry))) 1400))))
          commit-cmd!    (fn [i]
                           (let [entry (nth @filtered i nil)]
                             (when entry
                               (set! (.-value input-el) (str (:id entry) " "))
                               (.focus input-el)
                               (@update!*))))
          commit!        (fn []
                           (let [val (str/trim (.-value input-el))]
                             (cond
                               (and (= @mode :courses) (pos? (count @filtered)))
                               (if (= @selected 0)
                                 (do (sfx/play! :pop)
                                     (go-home!)
                                     (ui/show-toast! "Catálogo" 1200))
                                 (commit-course! (dec @selected)))
                               (= @mode :commands)
                               (let [entry (nth @filtered @selected nil)]
                                 (when entry
                                   (set! (.-value input-el) (str (:id entry) " "))
                                   (@update!*)))
                               (and (pos? (count val)) (try-eval-command! val))
                               (do (sfx/play! :pop)
                                   (set! (.-value input-el) "")
                                   (@update!*))
                               :else
                               (commit-page! @selected))))
          render!        (fn []
                           (case @mode
                             :courses  (render-course-results! results-el @filtered @selected commit-course!)
                             :commands (render-command-results! results-el @filtered @selected commit-cmd!)
                             (render-page-results! results-el @filtered @selected commit-page!)))
          update!        (fn []
                           (let [q (.-value input-el)
                                 op (open-prefix? q)]
                             (cond
                               (some? op)
                               (do (reset! mode :courses)
                                   (reset! filtered (match-entries op course-entries))
                                   (reset! selected 0))
                               (and (pos? (count q)) (= (first q) \())
                               (do (reset! mode :pages)
                                   (reset! filtered []))
                               (and (pos? (count q)) (seq (match-commands q)))
                               (do (reset! mode :commands)
                                   (reset! filtered (match-commands q))
                                   (reset! selected 0))
                               :else
                               (do (reset! mode :pages)
                                   (reset! filtered (match-entries q page-entries))
                                   (reset! selected 0)))
                             (render!)))
          _              (reset! update!* update!)
          max-sel        (fn []
                           (if (= @mode :courses)
                             (count @filtered)
                             (dec (max 1 (count @filtered)))))
          on-input-key   (fn [e]
                           (.stopPropagation e)
                           (let [k (.-key e)]
                             (case k
                               "Enter"
                               (do (.preventDefault e) (commit!))
                               "ArrowDown"
                               (do (.preventDefault e)
                                   (swap! selected #(min (max-sel) (inc %)))
                                   (render!))
                               "ArrowUp"
                               (do (.preventDefault e)
                                   (swap! selected #(max 0 (dec %)))
                                   (render!))
                               "Tab"
                               (when (#{:courses :commands} @mode)
                                 (.preventDefault e)
                                 (cond
                                   (= @mode :courses)
                                   (when-let [entry (nth @filtered (if (= @selected 0) 0 (dec @selected)) nil)]
                                     (set! (.-value input-el) (str "open " (:id entry)))
                                     (@update!*))
                                   (= @mode :commands)
                                   (when-let [entry (nth @filtered @selected nil)]
                                     (set! (.-value input-el) (str (:id entry) " "))
                                     (@update!*))))
                               nil)))]
      (.addEventListener input-el "input" update!)
      (.addEventListener input-el "keydown" on-input-key)
      (.appendChild bar input-el)
      (.appendChild bar results-el)
      (.appendChild host bar)
      (if (empty? page-entries)
        (do (reset! mode :courses)
            (reset! filtered course-entries)
            (render-course-results! results-el course-entries 0 commit-course!))
        (render-page-results! results-el page-entries 0 commit-page!)))))

(defn mount-embedded-hosts!
  "Attach interactive OmniREPL bars to every .omni-embed-host (e.g. manual p. 9)."
  []
  (doseq [host (.querySelectorAll js/document ".omni-embed-host")]
    (mount-embedded-in! host)))
