(ns greb-course.editor
  "Page editor: opens Monaco in the adjacent page slot showing the source
   of the current page. Connected to Claude API for AI-assisted editing.
   Live-syncs editor changes back to the rendered page."
  (:require [greb-course.dom                :as d]
            [greb-course.state              :as state]
            [greb-course.ui                 :as ui]
            [greb-course.templates.registry :as reg]
            [cljs.pprint                    :as pprint]
            [cljs.reader                    :as reader]
            [clojure.string                 :as str]))

(declare dismiss!)
(declare send-to-claude!)
(declare set-status!)

;; Resolved via GET /api/editor-config when using greb-course.server; else default anthropic URL.
(defonce ^:private editor-remote-config (atom nil))

(defn- fetch-editor-config! []
  (if @editor-remote-config
    (js/Promise.resolve @editor-remote-config)
    (-> (js/fetch "/api/editor-config")
        (.then (fn [r] (if (.-ok r) (.json r) (js/Promise.reject nil))))
        (.then (fn [j]
                 (let [raw (js->clj j)
                       c {:messages-url (get raw "messagesUrl")
                          :uses-server-key (boolean (get raw "usesServerKey"))}]
                   (reset! editor-remote-config c)
                   c)))
        (.catch (fn [_]
                  (let [c {:messages-url "https://api.anthropic.com/v1/messages"
                           :uses-server-key false}]
                    (reset! editor-remote-config c)
                    (js/Promise.resolve c)))))))

(defonce ^:private editor-state
  (atom {:active?   false
         :instance  nil      ;; Monaco editor instance
         :panel     nil      ;; DOM panel element injected into the page slot
         :page-idx  nil      ;; 0-based index of the page being edited
         :target    nil      ;; The .page element that hosts the editor panel
         :edit-page nil      ;; The .page element being live-previewed
         :listener  nil}))   ;; Monaco onDidChangeModelContent disposable

(defonce ^:private sync-timer (atom nil))

;; ── Source extraction ──────────────────────────────────────────

(defn- page-source
  "Return a string representation of the page definition at index i."
  [page-idx]
  (when-let [course @state/current-course]
    (let [pages (:pages course)
          page  (nth pages page-idx nil)]
      (when page
        (let [{:keys [template data]} page]
          (str ";; Page " (inc page-idx) " — template: " (name template) "\n"
               ";; Edit the :data map below to change this page's content.\n"
               ";; Press Ctrl+Enter to send to Claude, Escape to close.\n\n"
               "{:template " (pr-str template) "\n"
               " :data\n"
               " " (with-out-str (pprint/pprint data)) "}"))))))

;; ── Live sync: editor → rendered page ──────────────────────────

(defn- strip-comments
  "Remove ;; comment lines from editor text before parsing."
  [s]
  (->> (str/split-lines s)
       (remove #(re-matches #"\s*;;.*" %))
       (str/join "\n")))

(defn- try-parse-page
  "Try to parse editor content as a {:template :foo :data {...}} map.
   Returns the map on success, nil on parse error."
  [text]
  (try
    (let [cleaned (strip-comments text)
          parsed  (reader/read-string cleaned)]
      (when (and (map? parsed)
                 (contains? parsed :template)
                 (contains? parsed :data))
        parsed))
    (catch :default _ nil)))

(defn- sync-page!
  "Re-render the edit-page element from the current editor content."
  []
  (when-let [{:keys [instance edit-page page-idx active?]} @editor-state]
    (when (and active? instance edit-page)
      (let [text   (.getValue ^js instance)
            parsed (try-parse-page text)]
        (if parsed
          (let [{:keys [template data]} parsed
                theme  (:theme @state/current-course)
                new-el (reg/render-page template data (inc page-idx) theme)]
            (when new-el
              ;; Replace all children of edit-page with children of new-el
              ;; but keep the edit-page element itself (it's in the spread DOM)
              (set! (.-innerHTML edit-page) "")
              (let [children (array-seq (.-childNodes new-el))]
                (doseq [ch children]
                  (.appendChild edit-page (.cloneNode ch true))))
              ;; Preserve the page id
              (set! (.-id edit-page) (.-id new-el))
              ;; Re-render lucide icons inside the updated page
              (when (and js/lucide (.-createIcons js/lucide))
                (.createIcons js/lucide #js {:attrs #js {:class "icon"}}))
              (set-status! (str "Synced — page " (inc page-idx)))))
          ;; Parse failed — don't update, just show hint
          (set-status! "Editing..."))))))

(defn- schedule-sync! []
  (when-let [t @sync-timer] (js/clearTimeout t))
  (reset! sync-timer
    (js/setTimeout (fn [] (reset! sync-timer nil) (sync-page!)) 400)))

;; ── Find the adjacent page slot ────────────────────────────────

(defn- find-editor-target
  "Given the current spread, find the page element where the editor should go.
   Returns {:edit-page <page being edited> :target <page hosting editor>} or nil.
   The editor goes on the OTHER page in the spread (adjacent to the one being edited)."
  []
  (when-let [spread (.querySelector js/document ".spread.active")]
    (let [pages   (array-seq (.querySelectorAll spread ".page"))
          n       (count pages)
          hash-id (some-> js/location .-hash (subs 1))]
      (cond
        ;; Desktop: 2 pages in spread
        (= n 2)
        (let [left       (first pages)
              right      (second pages)
              right-id   (.-id right)
              on-right?  (= hash-id right-id)
              edit-page  (if on-right? right left)
              target     (if on-right? left right)]
          {:edit-page edit-page :target target})

        ;; Mobile or single page: overlay on the same page
        (= n 1)
        {:edit-page (first pages) :target (first pages)}

        :else nil))))

;; ── Panel creation ─────────────────────────────────────────────

(defn- create-editor-panel! []
  (let [panel   (d/el :div {:class "editor-panel"})
        header  (d/el :div {:class "editor-header"})
        title   (d/el :span {:class "editor-title"} "Page Editor")
        status  (d/el :span {:class "editor-status"} "Ready")
        close   (doto (d/el :button {:class "editor-close-btn"} "\u2715")
                      (.addEventListener "click" #(dismiss!)))
        send    (doto (d/el :button {:class "editor-send-btn"}
                            (d/ic "bot" "") "Ask Claude")
                      (.addEventListener "click" #(send-to-claude!)))
        api-btn (doto (d/el :button {:class "editor-api-btn"
                                     :title "Anthropic API key (localStorage); optional if server uses GREB_ANTHROPIC_API_KEY proxy"}
                            (d/ic "key" ""))
                      (.addEventListener "click"
                        (fn []
                          (let [current (or (.getItem js/localStorage "greb-claude-api-key") "")
                                key     (js/prompt
                                         (str "Anthropic API key (browser). Leave unset if /api/anthropic/messages proxy is configured via GREB_ANTHROPIC_API_KEY on the server.\n\n"
                                              "Override URL: env GREB_ANTHROPIC_MESSAGES_URL on server sets editor endpoint.\n\n"
                                              "Key:")
                                         current)]
                            (when (and key (not (str/blank? key)))
                              (.setItem js/localStorage "greb-claude-api-key" key)
                              (set-status! "API key saved"))))))
        prompt-row (d/el :div {:class "editor-prompt-row"})
        prompt-input (d/el :input {:class "editor-prompt-input" :type "text"
                                    :placeholder "Describe what to change..."})
        container (d/el :div {:class "editor-container"})]
    (.appendChild header title)
    (.appendChild header status)
    (.appendChild header api-btn)
    (.appendChild header send)
    (.appendChild header close)
    (.appendChild prompt-row prompt-input)
    (.appendChild panel header)
    (.appendChild panel prompt-row)
    (.appendChild panel container)
    {:panel panel :container container :status status :prompt prompt-input}))

(defn- init-monaco! [container source]
  (if (and js/monaco (.-editor js/monaco))
    (let [ed (.create (.-editor js/monaco) container
               #js {:value          source
                    :language       "clojure"
                    :theme          "vs-dark"
                    :fontSize       13
                    :lineNumbers    "on"
                    :minimap        #js {:enabled false}
                    :wordWrap       "on"
                    :scrollBeyondLastLine false
                    :automaticLayout true
                    :tabSize        2})]
      (.addCommand ed
        (bit-or (.. js/monaco -KeyMod -CtrlCmd)
                (.. js/monaco -KeyCode -Enter))
        (fn [] (send-to-claude!)))
      (.addCommand ed
        (.. js/monaco -KeyCode -Escape)
        (fn [] (dismiss!)))
      ed)
    (do (js/console.error "Monaco editor not loaded")
        nil)))

;; ── Claude API integration ─────────────────────────────────────

(defn- get-api-key []
  (or (.getItem js/localStorage "greb-claude-api-key")
      (let [key (js/prompt "Enter your Anthropic API key (stored in localStorage):")]
        (when (and key (not (str/blank? key)))
          (.setItem js/localStorage "greb-claude-api-key" key)
          key))))

(defn- set-status! [text]
  (when-let [{:keys [panel]} @editor-state]
    (when-let [el (.querySelector panel ".editor-status")]
      (set! (.-textContent el) text))))

(defn- anthropic-messages-json->text
  "First non-empty assistant text from Messages API JSON.
  Walks every content block — models with extended thinking put non-text blocks first."
  [json]
  (if-not json
    (throw (js/Error. "No response body"))
    (if (and (map? json) (:messages-url json) (nil? (:content json)))
      (throw (js/Error.
              "Internal bug: received editor config instead of Anthropic JSON (broken promise chain)"))
      (let [typ (.-type json)]
        (when (= typ "error")
          (let [err (.-error json)]
            (throw (js/Error.
                    (if (string? err)
                      err
                      (or (some-> err .-message str) (str err)))))))
        (let [content (.-content json)]
          (if-not content
            (let [hint (try (let [s (.stringify js/JSON json)]
                              (subs s 0 (min 380 (count s))))
                            (catch :default _ nil))]
              (throw (js/Error. (str "No content in API response"
                                     (when hint (str ": " hint))))))
            (let [len (.-length content)]
              (or (loop [i 0]
                    (when (< i len)
                      (let [blk (aget content i)
                            t (when blk (.-text blk))]
                        (if (and (string? t) (not (str/blank? (str/trim t))))
                          (str/trim t)
                          (recur (inc i))))))
                  (throw (js/Error. "Empty response — no text blocks"))))))))))

(defn anthropic-quick-ask!
  "One-shot Messages API call; returns a JS Promise resolving to assistant text (trimmed).
  Uses /api/editor-config (proxy or direct) like the page editor.
  Must use the same `->` / `.then` shape as `execute-claude-request!`: a 3-arg `.then` on
  `fetch-editor-config!` would treat the response handler as onRejected and pass the
  raw `Response` into the JSON step (empty `content`)."
  [prompt]
  (if (str/blank? prompt)
    (js/Promise.reject (js/Error. "Empty prompt"))
    (-> (fetch-editor-config!)
        (.then
         (fn [cfg]
           (let [uses-server? (:uses-server-key cfg)
                 api-key (when-not uses-server? (get-api-key))]
             (if (and (not uses-server?) (not api-key))
               (js/Promise.reject (js/Error. "No API key"))
               (let [body (clj->js {:model "claude-sonnet-4-20250514"
                                    :max_tokens 1024
                                    :system (str "Reply concisely in plain text unless the user asks for code or formatting.\n"
                                                 "No markdown code fences unless they ask for code.")
                                    :messages [{:role "user" :content prompt}]})
                     headers (if uses-server?
                               #js {"Content-Type" "application/json"}
                               #js {"Content-Type" "application/json"
                                    "x-api-key" api-key
                                    "anthropic-version" "2023-06-01"
                                    "anthropic-dangerous-direct-browser-access" "true"})]
                 (js/fetch (:messages-url cfg)
                           #js {:method "POST" :headers headers
                                :body (.stringify js/JSON body)}))))))
        (.then (fn [resp]
                 (if (.-ok resp)
                   (.json resp)
                   (.then (.text resp)
                          (fn [t]
                            (js/console.error "Anthropic:" t)
                            (js/Promise.reject
                              (js/Error. (str "API error " (.-status resp) ": " t))))))))
        (.then (fn [json]
                 (anthropic-messages-json->text json))))))

(defn- execute-claude-request! [instance code user-prompt]
  (-> (fetch-editor-config!)
      (.then
       (fn [cfg]
         (let [uses-server? (:uses-server-key cfg)
               api-key (when-not uses-server? (get-api-key))]
           (if (and (not uses-server?) (not api-key))
             (do (set-status! "No API key")
                 (js/Promise.reject (js/Error. "No API key")))
             (let [system-msg (str "You are editing a Greb Docs page definition in ClojureScript.\n"
                                   "The page is a map with :template and :data keys.\n"
                                   "Return ONLY the modified ClojureScript map — no explanation, no markdown fences.\n"
                                   "Keep the same :template unless the user asks to change it.")
                   body (clj->js {:model "claude-sonnet-4-20250514"
                                  :max_tokens 4096
                                  :system system-msg
                                  :messages [{:role "user"
                                              :content (str "Current page code:\n```\n" code "\n```\n\n"
                                                            "Request: " user-prompt)}]})
                   headers (if uses-server?
                             #js {"Content-Type" "application/json"}
                             #js {"Content-Type" "application/json"
                                  "x-api-key" api-key
                                  "anthropic-version" "2023-06-01"
                                  "anthropic-dangerous-direct-browser-access" "true"})]
               (js/fetch (:messages-url cfg)
                         #js {:method "POST" :headers headers
                              :body (.stringify js/JSON body)})))))
      (.then (fn [resp]
               (if (.-ok resp)
                 (.json resp)
                 (.then (.text resp)
                        (fn [t]
                          (js/console.error "API body:" t)
                          (js/Promise.reject
                            (js/Error. (str "API error " (.-status resp) ": " t))))))))
      (.then (fn [json]
               (let [text (anthropic-messages-json->text json)
                     clean (-> text
                               (str/replace #"^```\w*\n?" "")
                               (str/replace #"\n?```$" "")
                               str/trim)]
                 (.setValue ^js instance clean)
                 ;; setValue triggers onDidChangeModelContent → schedule-sync! → sync-page!
                 (set-status! "Updated — review changes"))))
      (.catch (fn [err]
                (when err
                  (js/console.error "Claude API error:" err)
                  (set-status! (str "Error: " (.-message err)))))))))

(defn send-to-claude! []
  (when-let [{:keys [instance active?]} @editor-state]
    (when (and active? instance)
      (let [prompt-el (.querySelector (:panel @editor-state) ".editor-prompt-input")
            user-prompt (.-value prompt-el)
            code (.getValue ^js instance)]
        (if (str/blank? user-prompt)
          (ui/show-toast! "Type a prompt first" 2000)
          (do (set-status! "Sending to Claude...")
              (execute-claude-request! instance code user-prompt)))))))

;; ── Public API ─────────────────────────────────────────────────

(defn edit!
  "Open editor for the current page. Shows Monaco in the adjacent page slot."
  []
  (when (:active? @editor-state)
    (dismiss!))
  (when-let [{:keys [edit-page target]} (find-editor-target)]
    (let [edit-id (.-id edit-page)
          {:keys [spread->pages spread-ids]} @state/current-nav
          all-ids      (vec (mapcat (fn [si] (get spread->pages si)) (range (count spread-ids))))
          page-idx     (.indexOf all-ids edit-id)]
      (when-let [source (page-source page-idx)]
        (let [{:keys [panel container status prompt]} (create-editor-panel!)]
          ;; Inject editor panel into the target page element
          (.appendChild target panel)
          ;; Force layout before creating Monaco
          (.-offsetHeight container)
          (let [ed       (init-monaco! container source)
                ;; Listen to editor changes and live-sync to the page
                listener (when ed
                           ;; API lives on the editor, not getModel() (model uses onDidChangeContent)
                           (.onDidChangeModelContent ed (fn [_] (schedule-sync!))))]
            (reset! editor-state {:active?   true
                                  :instance  ed
                                  :panel     panel
                                  :page-idx  page-idx
                                  :target    target
                                  :edit-page edit-page
                                  :listener  listener})
            (set! (.-textContent status) (str "Editing page " (inc page-idx)))
            ;; Refresh lucide icons for the buttons
            (when (and js/lucide (.-createIcons js/lucide))
              (.createIcons js/lucide #js {:attrs #js {:class "icon"}}))
            (.focus prompt)))))))

(defn dismiss! []
  (when-let [{:keys [instance panel active? listener]} @editor-state]
    (when active?
      (when-let [t @sync-timer] (js/clearTimeout t) (reset! sync-timer nil))
      (when listener (.dispose ^js listener))
      (when instance (.dispose ^js instance))
      (when (and panel (.-parentNode panel)) (.remove panel))
      (reset! editor-state {:active? false :instance nil :panel nil :page-idx nil
                            :target nil :edit-page nil :listener nil}))))

(defn active? [] (:active? @editor-state))
