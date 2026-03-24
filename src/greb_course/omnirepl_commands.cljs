(ns greb-course.omnirepl-commands
  "OmniREPL: data builders, navigation helpers, command evaluation."
  (:require [greb-course.state  :as state]
            [greb-course.ui     :as ui]
            [greb-course.editor :as editor]
            [clojure.string     :as str]))

;; ── Searchable entries ──────────────────────────────────────

(defn build-entries []
  (when-let [{:keys [toc-groups id->spread]} @state/current-nav]
    (vec
      (for [{:keys [label entries]} toc-groups
            {:keys [id label page icon]} entries
            :when (get id->spread id)]
        {:id id :label label :page page :icon icon
         :type :page :section label :spread-idx (get id->spread id)}))))

(defn build-course-entries []
  (vec
    (for [c (or @state/current-courses [])
          :let [m (:meta c)]]
      {:id (:id m) :label (:title m) :org (:org m) :slug (:slug m)
       :desc (:description m) :type :course})))

(defn match-entries [query entries]
  (if (empty? query)
    entries
    (let [q (str/lower-case query)]
      (filter (fn [{:keys [label section id desc]}]
                (or (str/includes? (str/lower-case (or label "")) q)
                    (str/includes? (str/lower-case (or section "")) q)
                    (str/includes? (str/lower-case (or id "")) q)
                    (str/includes? (str/lower-case (or desc "")) q)))
              entries))))

;; ── Navigation helpers ──────────────────────────────────────

(defn navigate-to-spread! [idx]
  (when-let [{:keys [go!]} @state/current-nav]
    (go! idx nil)))

(defn navigate-to-page-id! [page-id]
  (when-let [{:keys [go! id->spread]} @state/current-nav]
    (when-let [idx (get id->spread page-id)]
      (go! idx nil))))

(defn navigate-to-page-number! [n]
  (when-let [entries (build-entries)]
    (when-let [entry (first (filter #(= (:page %) n) entries))]
      (navigate-to-spread! (:spread-idx entry)))))

(defn open-course! [{:keys [org slug]}]
  (set! (.-location js/window) (str "/" org "/" slug "/")))

(defn go-home! []
  (set! (.-location js/window) "/"))

(defn download-pdf! []
  (if-let [{:keys [org slug]} (:meta @state/current-course)]
    (-> (js/fetch (str "/api/export-pdf?org=" (js/encodeURIComponent org)
                       "&slug=" (js/encodeURIComponent slug)))
        (.then (fn [resp]
                 (if-not (.-ok resp)
                   (.reject js/Promise (js/Error. (str "HTTP " (.-status resp))))
                   (let [ctype (or (.get (.-headers resp) "content-type") "")]
                     (-> (.blob resp)
                         (.then (fn [blob]
                                  (if (and (.includes (.toLowerCase ctype) "application/pdf")
                                           (> (.-size blob) 10000))
                                    blob
                                    (.reject js/Promise
                                      (js/Error.
                                        (str "Invalid PDF response (" ctype ", " (.-size blob) " bytes)")))))))))))
        (.then (fn [blob]
                 (let [dl-url (.createObjectURL js/URL blob)
                       a      (.createElement js/document "a")
                       name   (str org "-" slug ".pdf")]
                   (set! (.-href a) dl-url)
                   (set! (.-download a) name)
                   (.appendChild (.-body js/document) a)
                   (.click a)
                   (.remove a)
                   (js/setTimeout #(.revokeObjectURL js/URL dl-url) 1200)
                   (ui/show-toast! (str "Downloaded: " name) 2200))))
        (.catch #(ui/show-toast! "PDF export failed. Restart server and try again." 5000)))
    (ui/show-toast! "No active course" 2200)))

;; ── Command entries ─────────────────────────────────────────

(def command-entries
  [{:id "open"   :label "open <doc>"  :desc "Open a document"       :type :command}
   {:id "go"     :label "go <page>"   :desc "Go to page number/id"  :type :command}
   {:id "home"   :label "home"        :desc "Go to catalog"         :type :command}
   {:id "reset"  :label "reset"       :desc "Reset zoom and pan"    :type :command}
   {:id "zoom"   :label "zoom <n>"    :desc "Set zoom level"        :type :command}
   {:id "pages"  :label "pages"       :desc "Show page count"       :type :command}
   {:id "print-dialog" :label "print-dialog" :desc "Open browser print dialog" :type :command}
   {:id "pdf-download" :label "pdf-download" :desc "Open print dialog for Save as PDF" :type :command}
   {:id "edit"   :label "edit <prompt>" :desc "Edit page (open editor, or with LLM prompt)" :type :command}
   {:id "vim"    :label "vim"         :desc "Enable Vim keybindings in editor"  :type :command}
   {:id "novim"  :label "novim"       :desc "Disable Vim keybindings in editor" :type :command}
   {:id "page"   :label "page <n|id>"   :desc "Switch editor to page N or page id" :type :command}
   {:id "imagine" :label "imagine <prompt>" :desc "Generate image with Kie AI"       :type :command}
   {:id "save"   :label "save"         :desc "Save current editor changes to disk" :type :command}
   {:id "ask"    :label "ask <msg>"   :desc "Quick question to LLM (toast)"     :type :command}])

(defn match-commands [query]
  (if (empty? query) []
    (let [q (str/lower-case query)]
      (filter (fn [{:keys [id label desc]}]
                (or (str/includes? id q)
                    (str/includes? (str/lower-case label) q)
                    (str/includes? (str/lower-case desc) q)))
              command-entries))))

;; ── Detect (open ...) prefix for live autocomplete ──────────

(defn open-prefix? [text]
  (or (when-let [[_ arg] (re-matches #"\(open\s+(.*)" text)]
        (str/replace arg #"[\")\s]+$" ""))
      (when-let [[_ arg] (re-matches #"(?i)open\s+(.*)" text)]
        (str/replace arg #"[\")\s]+$" ""))))

;; ── Command evaluation ─────────────────────────────────────

(defn eval-command [input]
  (let [s (str/trim input)]
    (cond
      (re-matches #"\(home\)" s)
      (do (go-home!) true)

      (re-matches #"\(open\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)
      (let [[_ id] (re-matches #"\(open\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)
            match (first (filter #(= (:id %) id) (build-course-entries)))]
        (if match (do (open-course! match) true)
          (do (ui/show-toast! (str "Unknown document: " id) 2000) true)))

      (re-matches #"\(go\s+(\d+)\)" s)
      (let [[_ n] (re-matches #"\(go\s+(\d+)\)" s)
            page-num (js/parseInt n 10)]
        (navigate-to-page-number! page-num)
        (ui/show-toast! (str "Page " n)) true)

      (re-matches #"\(go\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)
      (let [[_ id] (re-matches #"\(go\s+\"?([a-zA-Z0-9_-]+)\"?\)" s)]
        (navigate-to-page-id! id) (ui/show-toast! (str "\u2192 " id)) true)

      (re-matches #"\(reset\)" s)
      (do (reset! state/doc-view {:zoom 1.0 :text-scale 1.0 :pan-x 0 :pan-y 0})
          (ui/show-toast! "Reset") true)

      (re-matches #"\(zoom\s+([\d.]+)\)" s)
      (let [[_ n] (re-matches #"\(zoom\s+([\d.]+)\)" s)]
        (swap! state/doc-view assoc :zoom (js/parseFloat n))
        (ui/show-toast! (str "Zoom " n)) true)

      (re-matches #"\(pages\)" s)
      (let [n (count (or (:spread-ids @state/current-nav) []))]
        (ui/show-toast! (str n " pages") 2000) true)

      (re-matches #"(?i)\(print-dialog\)" s)
      (do (js/requestAnimationFrame (fn [] (.print js/window))) true)

      (re-matches #"(?i)\((pdf-download|pdf-downlaod)\)" s)
      (do (ui/show-toast! "Exporting PDF..." 1800)
          (download-pdf!)
          true)

      (re-matches #"\(edit\)" s)   (do (editor/edit!) true)

      ;; :edit <prompt> or edit <prompt> — open editor + send LLM request
      (re-matches #"(?i):?edit\s+(.+)" s)
      (let [[_ prompt] (re-matches #"(?i):?edit\s+(.+)" s)]
        (editor/edit-with-prompt! (str/trim prompt)) true)

      ;; bare "edit" — just open editor
      (re-matches #"(?i):?edit\s*$" s) (do (editor/edit!) true)

      ;; page N or page id — switch editor to different page
      (re-matches #"(?i):?page\s+(\S+)" s)
      (let [[_ arg] (re-matches #"(?i):?page\s+(\S+)" s)]
        (if (re-matches #"\d+" arg)
          (editor/goto-page! (dec (js/parseInt arg 10)))
          (editor/goto-page! arg))
        true)

      ;; imagine <prompt> — generate image and auto-save
      (re-matches #"(?i):?imagine\s+(.+)" s)
      (let [[_ prompt] (re-matches #"(?i):?imagine\s+(.+)" s)
            org (get-in @state/current-course [:meta :org] "default")]
        (ui/show-toast! (str "Generating: " prompt) 3000)
        (-> (js/fetch "/api/kie/generate"
              #js {:method "POST" :headers #js {"Content-Type" "application/json"}
                   :body (.stringify js/JSON (clj->js {:prompt prompt :size "1:1"}))})
            (.then #(.json %))
            (.then (fn [r]
                     (let [task-id (some-> (aget r "data") (aget "taskId"))]
                       (if-not task-id
                         (ui/show-toast! "Error: no taskId returned" 5000)
                         (let [poll (atom nil)]
                           (ui/show-toast! (str "Task " task-id " — polling…") 3000)
                           (reset! poll
                             (js/setInterval
                               (fn []
                                 (-> (js/fetch (str "/api/kie/task?taskId=" task-id))
                                     (.then #(.json %))
                                     (.then (fn [resp]
                                              (let [status (some-> (aget resp "data") (aget "status"))]
                                                (when (= "SUCCESS" status)
                                                  (js/clearInterval @poll)
                                                  (let [url (some-> (aget resp "data") (aget "response") (aget "resultUrls") (aget 0))
                                                        fname (str "gen-" (.now js/Date) ".png")]
                                                    (ui/show-toast! (str "Saving " fname "…") 3000)
                                                    (-> (js/fetch "/api/kie/save"
                                                          #js {:method "POST"
                                                               :headers #js {"Content-Type" "application/edn"}
                                                               :body (pr-str {:url url :org org :filename fname})})
                                                        (.then #(.text %))
                                                        (.then (fn [body]
                                                                 (ui/show-toast! (str "Saved: " fname) 8000)))
                                                        (.catch #(ui/show-toast! (str "Save error: " (.-message %)) 5000)))))
                                                (when (= "FAILED" status)
                                                  (js/clearInterval @poll)
                                                  (ui/show-toast! "Generation failed" 5000)))))
                                     (.catch (fn [e]
                                               (js/clearInterval @poll)
                                               (ui/show-toast! (str "Poll error: " (.-message e)) 5000)))))
                               3000)))))))
            (.catch #(ui/show-toast! (str "Error: " (.-message %)) 5000)))
        true)

      ;; save — persist editor changes
      (re-matches #"(?i):?save" s) (do (editor/save!) true)

      (re-matches #"\(ask\s+\"([^\"]*)\"\)" s)
      (let [[_ q] (re-matches #"\(ask\s+\"([^\"]*)\"\)" s)]
        (-> (editor/anthropic-quick-ask! q)
            (.then #(ui/show-toast! % 28000))
            (.catch #(ui/show-toast! (str "Ask failed: " (.-message %)) 14000)))
        true)

      (re-matches #"(?i)vim" s)
      (do (editor/toggle-vim! true) (ui/show-toast! "Vim mode ON" 2000) true)

      (re-matches #"(?i)novim" s)
      (do (editor/toggle-vim! false) (ui/show-toast! "Vim mode OFF" 2000) true)

      (re-matches #"(?i)ask\s+(.+)" s)
      (let [[_ q] (re-matches #"(?i)ask\s+(.+)" s)]
        (-> (editor/anthropic-quick-ask! (str/trim q))
            (.then #(ui/show-toast! % 28000))
            (.catch #(ui/show-toast! (str "Ask failed: " (.-message %)) 14000)))
        true)

      (re-matches #"(?i)home" s) (do (go-home!) true)

      (re-matches #"(?i)open\s+\"?([a-zA-Z0-9_-]+)\"?" s)
      (let [[_ id] (re-matches #"(?i)open\s+\"?([a-zA-Z0-9_-]+)\"?" s)
            match (first (filter #(= (:id %) id) (build-course-entries)))]
        (if match (do (open-course! match) true)
          (do (ui/show-toast! (str "Unknown document: " id) 2000) true)))

      (re-matches #"(?i)go\s+(\S+)" s)
      (let [[_ arg] (re-matches #"(?i)go\s+(\S+)" s)]
        (if (re-matches #"\d+" arg)
          (do (navigate-to-page-number! (js/parseInt arg 10))
              (ui/show-toast! (str "Page " arg)) true)
          (do (navigate-to-page-id! arg)
              (ui/show-toast! (str "\u2192 " arg)) true)))

      (re-matches #"(?i)reset" s)
      (do (reset! state/doc-view {:zoom 1.0 :text-scale 1.0 :pan-x 0 :pan-y 0})
          (ui/show-toast! "Reset") true)

      (re-matches #"(?i)pages" s)
      (let [n (count (or (:spread-ids @state/current-nav) []))]
        (ui/show-toast! (str n " pages") 2000) true)

      (re-matches #"(?i)print-dialog" s)
      (do (js/requestAnimationFrame (fn [] (.print js/window))) true)

      (re-matches #"(?i)(pdf-download|pdf-downlaod)" s)
      (do (ui/show-toast! "Exporting PDF..." 1800)
          (download-pdf!)
          true)

      (re-matches #"(?i)zoom\s+([\d.]+)" s)
      (let [[_ n] (re-matches #"(?i)zoom\s+([\d.]+)" s)]
        (swap! state/doc-view assoc :zoom (js/parseFloat n))
        (ui/show-toast! (str "Zoom " n)) true)

      :else false)))
