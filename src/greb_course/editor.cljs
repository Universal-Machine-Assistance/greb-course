(ns greb-course.editor
  "Page editor: Monaco + LLM (OpenRouter/Anthropic) with Vim mode and diff view."
  (:require [greb-course.dom                :as d]
            [greb-course.state              :as state]
            [greb-course.ui                 :as ui]
            [greb-course.templates.registry :as reg]
            [cljs.pprint                    :as pprint]
            [cljs.reader                    :as reader]
            [clojure.string                 :as str]))

(declare dismiss! send-to-claude! set-status! edit!)

;; ── State ────────────────────────────────────────────────────
(defonce vim-mode-enabled? (atom true))
(defonce ^:private vim-inst (atom nil))
(defonce ^:private remote-cfg (atom nil))
(defonce ^:private editor-state
  (atom {:active? false :instance nil :panel nil :page-idx nil
         :target nil :edit-page nil :listener nil :diff-editor nil
         :loading? false}))
(defonce ^:private sync-timer (atom nil))
(defonce ^:private edit-side (atom :left))
(defonce ^:private monaco-loading? (atom false))
(defonce ^:private pre-llm-code (atom nil))
(defonce ^:private loading-interval (atom nil))

(def ^:private cdn "https://cdn.jsdelivr.net/npm/monaco-editor@0.52.2/min")

;; ── Helpers ──────────────────────────────────────────────────
(defn- load-script! [src cb]
  (let [s (.createElement js/document "script")]
    (set! (.-src s) src) (set! (.-onload s) cb)
    (set! (.-onerror s) #(js/console.error "script load failed:" src %))
    (.appendChild (.-head js/document) s)))

(defn- set-status! [text]
  (when-let [el (some-> (:panel @editor-state) (.querySelector ".editor-status"))]
    (set! (.-textContent el) text)))

;; ── Loading animation ────────────────────────────────────────
(defn- current-model-name []
  (let [provider (:provider @remote-cfg)]
    (if (= provider "openrouter") "claude-sonnet-4 (OpenRouter)" "claude-sonnet-4")))

(defn- start-loading! []
  (swap! editor-state assoc :loading? true)
  (when-let [t @loading-interval] (js/clearInterval t))
  (let [frames ["⠋" "⠙" "⠹" "⠸" "⠼" "⠴" "⠦" "⠧" "⠇" "⠏"]
        model  (current-model-name)
        i (atom 0)]
    (reset! loading-interval
      (js/setInterval
        (fn []
          (set-status! (str (nth frames (mod @i (count frames))) " " model " thinking…"))
          (swap! i inc))
        80))))

(defn- stop-loading! []
  (swap! editor-state assoc :loading? false)
  (when-let [t @loading-interval] (js/clearInterval t) (reset! loading-interval nil)))

;; ── Editor config ────────────────────────────────────────────
(defn- fetch-editor-config! []
  (if @remote-cfg
    (js/Promise.resolve @remote-cfg)
    (-> (js/fetch "/api/editor-config")
        (.then #(if (.-ok %) (.json %) (js/Promise.reject nil)))
        (.then (fn [j]
                 (let [r (js->clj j)
                       c {:messages-url (get r "messagesUrl")
                          :uses-server-key (boolean (get r "usesServerKey"))
                          :provider (or (get r "provider") "anthropic")}]
                   (reset! remote-cfg c) c)))
        (.catch (fn [_]
                  (let [c {:messages-url "https://api.anthropic.com/v1/messages"
                           :uses-server-key false :provider "anthropic"}]
                    (reset! remote-cfg c) (js/Promise.resolve c)))))))

;; ── Source & sync ────────────────────────────────────────────
(defn- page-source [i]
  (when-let [{:keys [template data]} (nth (:pages @state/current-course) i nil)]
    (str ";; Page " (inc i) " — " (name template)
         "\n;; Ctrl+Enter → LLM, Ctrl+X → close\n\n"
         "{:template " (pr-str template) "\n :data\n "
         (with-out-str (pprint/pprint data)) "}")))

(defn- try-parse [text]
  (try
    (let [cleaned (->> (str/split-lines text)
                       (remove #(re-matches #"\s*;;.*" %))
                       (str/join "\n"))
          m (reader/read-string cleaned)]
      (when (and (map? m) (:template m) (:data m)) m))
    (catch :default _ nil)))

(defn- render-into! [target-el new-el]
  "Replace target element's children with new-el's children, trigger icons + animations."
  (set! (.-innerHTML target-el) "")
  (doseq [ch (array-seq (.-childNodes new-el))]
    (.appendChild target-el (.cloneNode ch true)))
  (set! (.-id target-el) (.-id new-el))
  (when (and js/lucide (.-createIcons js/lucide))
    (.createIcons js/lucide #js {:attrs #js {:class "icon"}}))
  ;; Trigger .animate → .visible so cards/blocks appear
  (js/requestAnimationFrame
    (fn []
      (doseq [node (array-seq (.querySelectorAll target-el ".animate"))]
        (.add (.-classList node) "visible")))))

(defn- sync-page! []
  (when-let [{:keys [instance edit-page page-idx active?]} @editor-state]
    (when (and active? instance edit-page)
      (if-let [{:keys [template data]} (try-parse (.getValue ^js instance))]
        (when-let [el (reg/render-page template data (inc page-idx) (:theme @state/current-course))]
          (render-into! edit-page el)
          (set-status! (str "Synced — page " (inc page-idx))))
        (set-status! "Editing...")))))

(defn- schedule-sync! []
  (when-let [t @sync-timer] (js/clearTimeout t))
  (reset! sync-timer (js/setTimeout #(do (reset! sync-timer nil) (sync-page!)) 400)))

;; ── Monaco loading ───────────────────────────────────────────
(defn- load-monaco! [cb]
  (if (and (exists? js/monaco) (.. js/monaco -editor))
    (cb)
    (if @monaco-loading?
      (let [p (atom nil)]
        (reset! p (js/setInterval
                    #(when (and (exists? js/monaco) (.. js/monaco -editor))
                       (js/clearInterval @p) (cb)) 100)))
      (do (reset! monaco-loading? true)
          (let [link (.createElement js/document "link")]
            (set! (.-rel link) "stylesheet")
            (set! (.-href link) (str cdn "/vs/editor/editor.main.css"))
            (.appendChild (.-head js/document) link))
          (let [saved-define (aget js/window "define")
                saved-require (aget js/window "require")]
            (js-delete js/window "define")
            (js-delete js/window "require")
            (load-script! (str cdn "/vs/loader.js")
              (fn []
                (let [amd-require (aget js/window "require")]
                  (when saved-define (aset js/window "define" saved-define))
                  (when saved-require (aset js/window "require" saved-require))
                  (when (and amd-require (fn? (aget amd-require "config")))
                    (.call (aget amd-require "config") amd-require
                      #js {:paths #js {:vs (str cdn "/vs")}}))
                  (amd-require #js ["vs/editor/editor.main"]
                    (fn [] (js/console.log "Monaco loaded") (cb)))))))))))

;; ── Vim mode ─────────────────────────────────────────────────
(defn- attach-vim! [ed el]
  (when @vim-mode-enabled?
    (if (exists? js/MonacoVim)
      (reset! vim-inst (.initVimMode js/MonacoVim ed el))
      (load-script! "https://cdn.jsdelivr.net/npm/monaco-vim@0.4.1/dist/monaco-vim.js"
        #(when (exists? js/MonacoVim)
           (reset! vim-inst (.initVimMode js/MonacoVim ed el)))))))

(defn- detach-vim! []
  (when-let [v @vim-inst] (.dispose v) (reset! vim-inst nil)))

(defn toggle-vim!
  ([] (toggle-vim! (not @vim-mode-enabled?)))
  ([on?]
   (reset! vim-mode-enabled? on?)
   (if on?
     (when-let [{:keys [instance panel active?]} @editor-state]
       (when (and active? instance)
         (when-let [el (.querySelector panel ".editor-vim-status")]
           (attach-vim! instance el))))
     (detach-vim!))
   on?))

;; ── Diff view ────────────────────────────────────────────────
(defn- show-diff! [old-code new-code]
  (when-let [{:keys [instance panel]} @editor-state]
    (let [ctr (.querySelector panel ".editor-container")]
      (when instance (.dispose ^js instance))
      (set! (.-innerHTML ctr) "")
      (let [orig (.createModel (.-editor js/monaco) old-code)
            mod  (.createModel (.-editor js/monaco) new-code)
            diff (.createDiffEditor (.-editor js/monaco) ctr
                   #js {:theme "vs-dark" :fontSize 13 :readOnly false
                        :renderSideBySide false :wordWrap "on"
                        :automaticLayout true :minimap #js {:enabled false}})]
        (.setModel diff #js {:original orig :modified mod})
        (swap! editor-state assoc :diff-editor diff :instance nil)
        ;; Preview the new code on the live page immediately
        (when-let [{:keys [edit-page page-idx]} @editor-state]
          (when edit-page
            (when-let [{:keys [template data]} (try-parse new-code)]
              (when-let [el (reg/render-page template data (inc page-idx) (:theme @state/current-course))]
                (render-into! edit-page el)))))
        (set-status! "Diff — Accept or Reject")
        (let [bar (d/el :div {:class "editor-diff-bar"})
              accept (doto (d/el :button {:class "editor-send-btn"} "Accept")
                       (.addEventListener "click"
                         (fn []
                           (.dispose diff) (.dispose orig) (.dispose mod)
                           (set! (.-innerHTML ctr) "")
                           (let [ed (.create (.-editor js/monaco) ctr
                                     #js {:value new-code :language "plaintext" :theme "vs-dark"
                                          :fontSize 13 :lineNumbers "on" :minimap #js {:enabled false}
                                          :wordWrap "on" :scrollBeyondLastLine false
                                          :automaticLayout true :tabSize 2})
                                 li (.onDidChangeModelContent ed (fn [_] (schedule-sync!)))]
                             (swap! editor-state assoc :instance ed :listener li :diff-editor nil)
                             (when (.-parentNode bar) (.remove bar))
                             (set-status! "Accepted — editing")
                             (schedule-sync!)))))
              reject (doto (d/el :button {:class "editor-close-btn"} "Reject")
                       (.addEventListener "click"
                         (fn []
                           (.dispose diff) (.dispose orig) (.dispose mod)
                           (set! (.-innerHTML ctr) "")
                           (let [ed (.create (.-editor js/monaco) ctr
                                     #js {:value old-code :language "plaintext" :theme "vs-dark"
                                          :fontSize 13 :lineNumbers "on" :minimap #js {:enabled false}
                                          :wordWrap "on" :scrollBeyondLastLine false
                                          :automaticLayout true :tabSize 2})
                                 li (.onDidChangeModelContent ed (fn [_] (schedule-sync!)))]
                             (swap! editor-state assoc :instance ed :listener li :diff-editor nil)
                             (when (.-parentNode bar) (.remove bar))
                             ;; Restore original page preview
                             (when-let [{:keys [edit-page page-idx]} @editor-state]
                               (when edit-page
                                 (when-let [{:keys [template data]} (try-parse old-code)]
                                   (when-let [el (reg/render-page template data (inc page-idx) (:theme @state/current-course))]
                                     (render-into! edit-page el)))))
                             (set-status! "Rejected — reverted")))))]
          (.appendChild bar accept) (.appendChild bar reject)
          (.insertBefore (.-parentNode ctr) bar ctr))))))

;; ── LLM integration ─────────────────────────────────────────
(defn- get-api-key []
  (or (.getItem js/localStorage "greb-claude-api-key")
      (let [k (js/prompt "API key (stored in localStorage):")]
        (when (not (str/blank? k)) (.setItem js/localStorage "greb-claude-api-key" k) k))))

(defn- parse-response [json provider]
  (when-not json (throw (js/Error. "No response body")))
  (if (= provider "openrouter")
    (let [c (some-> (.-choices json) (aget 0) .-message .-content)]
      (if (not (str/blank? c)) (str/trim c) (throw (js/Error. "Empty response"))))
    (do (when (= "error" (.-type json))
          (throw (js/Error. (or (some-> (.-error json) .-message) "API error"))))
        (let [content (.-content json)]
          (or (some #(let [t (.-text %)] (when (not (str/blank? t)) (str/trim t)))
                    (array-seq content))
              (throw (js/Error. "Empty response")))))))

(defn- llm-body [provider sys msgs max-tok]
  (clj->js
    (if (= provider "openrouter")
      {:model "anthropic/claude-sonnet-4" :max_tokens max-tok
       :messages (into [{:role "system" :content sys}] msgs)}
      {:model "claude-sonnet-4-20250514" :max_tokens max-tok
       :system sys :messages msgs})))

(defn- llm-headers [provider srv? key]
  (if srv? #js {"Content-Type" "application/json"}
    (if (= provider "openrouter")
      #js {"Content-Type" "application/json" "Authorization" (str "Bearer " key)}
      #js {"Content-Type" "application/json" "x-api-key" key
           "anthropic-version" "2023-06-01"
           "anthropic-dangerous-direct-browser-access" "true"})))

(defn- llm-call! [sys msgs max-tok]
  (-> (fetch-editor-config!)
      (.then (fn [{:keys [uses-server-key provider messages-url]}]
               (let [key (when-not uses-server-key (get-api-key))]
                 (if (and (not uses-server-key) (not key))
                   (js/Promise.reject (js/Error. "No API key"))
                   (js/fetch messages-url
                     #js {:method "POST"
                          :headers (llm-headers provider uses-server-key key)
                          :body (.stringify js/JSON (llm-body provider sys msgs max-tok))})))))
      (.then (fn [r] (if (.-ok r) (.json r)
                       (.then (.text r) #(js/Promise.reject (js/Error. (str "API " (.-status r) ": " %)))))))
      (.then #(parse-response % (:provider @remote-cfg)))))

(defn anthropic-quick-ask! [prompt]
  (if (str/blank? prompt)
    (js/Promise.reject (js/Error. "Empty prompt"))
    (llm-call! "Reply concisely in plain text. No markdown fences unless asked."
               [{:role "user" :content prompt}] 1024)))

(defn- execute-claude-request! [instance code user-prompt]
  (start-loading!)
  (reset! pre-llm-code code)
  (-> (llm-call! (str "You edit Greb Docs page definitions in ClojureScript EDN.\n"
                      "Return ONLY the modified {:template ... :data ...} map. No markdown fences.\n"
                      "IMPORTANT RULES:\n"
                      "- Keep ALL existing blocks from the original. Only add/modify what was requested.\n"
                      "- Do NOT remove or restructure existing content unless explicitly asked.\n"
                      "- The page MUST render — preserve the exact block structure.\n\n"
                      "Available block types in :blocks vector:\n"
                      "1. {:type :info-grid :icon \"star\" :title \"Section\" :items [{:title \"Card\" :icon \"file\" :text \"Content **bold**\"}]}\n"
                      "2. {:type :code-block :caption \"Title\" :lines [\"line 1\" {:text \"highlighted\" :hl true}]}\n"
                      "3. {:type :text-block :content \"Paragraph with **bold** and `code`.\"}\n"
                      "4. {:type :highlight :icon \"zap\" :title \"Points\" :items [\"Point 1\" \"Point 2\"]}\n"
                      "5. {:type :callout :icon \"info\" :title \"Title\" :text \"Text\" :style :accent} — banner (styles: nil/accent/warning)\n"
                      "6. {:type :feature-list :items [\"Feature 1\" \"Feature 2\"]} — 2-column checklist with ✓\n"
                      "7. {:type :steps :items [{:num \"1\" :title \"Step\" :icon \"star\" :text \"Details\"}]} — numbered process\n"
                      "8. {:type :pricing-table :title \"T\" :rows [{:label \"Item\" :amount \"$X\" :highlight? true}] :total {:label \"Total\" :amount \"$Y\"} :footer \"Note\"}\n"
                      "9. {:type :timeline :items [{:year \"S1\" :title \"Phase\" :text \"Details\"}]}\n"
                      "10. {:type :stat-grid :items [{:icon \"star\" :label \"Label\" :value \"42\"}]} — stat cards\n"
                      "11. {:type :two-col :left \"Left column text\" :right \"Right column text\"}\n"
                      "12. {:type :image-block :src \"filename.png\" :caption \"Description\" :float :left} — float: :left/:right/:full or nil for centered\n\n"
                      "Keep ALL existing blocks. Add :text-block BETWEEN blocks for explanations.\n"
                      "Use **bold**, `code` in :content/:text fields.")
                 [{:role "user" :content (str "Code:\n```\n" code "\n```\nRequest: " user-prompt)}]
                 4096)
      (.then (fn [text]
               (stop-loading!)
               (let [clean (-> text (str/replace #"^```\w*\n?" "") (str/replace #"\n?```$" "") str/trim)]
                 (if (= clean code)
                   (set-status! "No changes")
                   ;; Validate the LLM output parses correctly before showing diff
                   (if-let [parsed (try-parse clean)]
                     (show-diff! code clean)
                     ;; Parse failed — feed error back to LLM for a retry
                     (do (set-status! "LLM output has syntax errors — retrying…")
                         (start-loading!)
                         (-> (llm-call! (str "You edit Greb Docs page definitions in ClojureScript.\n"
                                            "Return ONLY the modified {:template ... :data ...} map. No markdown fences.\n"
                                            "IMPORTANT: Your previous output had a syntax error and could not be parsed.")
                                       [{:role "user" :content (str "Code:\n```\n" code "\n```\nRequest: " user-prompt)}
                                        {:role "assistant" :content clean}
                                        {:role "user" :content "That output has a ClojureScript syntax error. Fix it and return ONLY the corrected map."}]
                                       4096)
                             (.then (fn [text2]
                                      (stop-loading!)
                                      (let [clean2 (-> text2 (str/replace #"^```\w*\n?" "") (str/replace #"\n?```$" "") str/trim)]
                                        (if (try-parse clean2)
                                          (show-diff! code clean2)
                                          (do (set-status! "LLM output still broken — showing raw diff")
                                              (show-diff! code clean2))))))
                             (.catch (fn [e2]
                                       (stop-loading!)
                                       (set-status! (str "Retry failed: " (.-message e2))))))))))))
      (.catch (fn [e]
                (stop-loading!)
                (js/console.error "LLM:" e)
                (set-status! (str "Error: " (.-message e)))
                ;; Restore editor with original code
                (when-let [{:keys [panel]} @editor-state]
                  (when-let [ctr (.querySelector panel ".editor-container")]
                    (set! (.-innerHTML ctr) "")
                    (let [ed (.create (.-editor js/monaco) ctr
                               #js {:value code :language "plaintext" :theme "vs-dark"
                                    :fontSize 13 :lineNumbers "on" :minimap #js {:enabled false}
                                    :wordWrap "on" :scrollBeyondLastLine false
                                    :automaticLayout true :tabSize 2})
                          li (.onDidChangeModelContent ed (fn [_] (schedule-sync!)))]
                      (swap! editor-state assoc :instance ed :listener li))))))))

(defn send-to-claude! []
  (when-let [{:keys [instance active? loading?]} @editor-state]
    (when (and active? instance (not loading?))
      (let [p (.-value (.querySelector (:panel @editor-state) ".editor-prompt-input"))
            code (.getValue ^js instance)]
        (if (str/blank? p) (ui/show-toast! "Type a prompt first" 2000)
          (execute-claude-request! instance code p))))))

;; ── Headless LLM edit (from omnirepl :edit command) ──────────
(defn edit-with-prompt!
  "Open editor and immediately send an LLM request with the given prompt.
   Called from omnirepl `:edit some modification` command."
  [prompt]
  (when-not (:active? @editor-state) (edit!))
  ;; Wait for editor to be ready, then fire the request
  (let [poll (atom nil)
        tries (atom 0)]
    (reset! poll
      (js/setInterval
        (fn []
          (swap! tries inc)
          (when (or (:instance @editor-state) (> @tries 50))
            (js/clearInterval @poll)
            (when-let [{:keys [instance active?]} @editor-state]
              (when (and active? instance)
                ;; Set the prompt input text
                (when-let [input (.querySelector (:panel @editor-state) ".editor-prompt-input")]
                  (set! (.-value input) prompt))
                (execute-claude-request! instance (.getValue ^js instance) prompt)))))
        100))))

;; ── Save (persist to localStorage + server) ──────────────────
(defn- save-to-local! [org page-idx parsed]
  (let [ls-key (str "greb-patches:" org)
        existing (try (reader/read-string (or (.getItem js/localStorage ls-key) "{}"))
                      (catch :default _ {}))
        updated (assoc existing page-idx parsed)]
    (.setItem js/localStorage ls-key (pr-str updated))
    ;; Also update the in-memory course so changes survive navigation
    (when-let [course @state/current-course]
      (let [pages (vec (:pages course))]
        (when (< page-idx (count pages))
          (reset! state/current-course (assoc course :pages (assoc pages page-idx parsed))))))))

(defn save!
  "Save current editor content. Persists to localStorage immediately and tries server."
  []
  (when-let [{:keys [instance active? page-idx]} @editor-state]
    (when (and active? instance)
      (let [code (str/trim (.getValue ^js instance))
            parsed (try-parse code)]
        (if-not parsed
          (do (set-status! "Cannot save — parse error") (ui/show-toast! "Fix syntax before saving" 3000))
          (let [org (get-in @state/current-course [:meta :org])]
            (if-not org
              (set-status! "Cannot save — no course org")
              (do
                ;; Always save to localStorage + memory immediately
                (save-to-local! org page-idx parsed)
                (set-status! "Saved locally")
                ;; Try server too (may fail if server hasn't been restarted)
                (-> (js/fetch "/api/save-page"
                      #js {:method "POST"
                           :headers #js {"Content-Type" "application/edn"}
                           :body (pr-str {:org org :page-idx page-idx :content parsed})})
                    (.then (fn [r]
                             (if (.-ok r)
                               (do (set-status! "Saved") (ui/show-toast! (str "Page " (inc page-idx) " saved") 2000))
                               (do (set-status! "Saved locally (server unavailable)")
                                   (ui/show-toast! (str "Page " (inc page-idx) " saved locally") 2000)))))
                    (.catch (fn [_]
                              (set-status! "Saved locally")
                              (ui/show-toast! (str "Page " (inc page-idx) " saved locally") 2000))))))))))))

;; ── Panel & editor creation ──────────────────────────────────
(defn- make-panel []
  (let [panel (d/el :div {:class "editor-panel"})
        hdr   (d/el :div {:class "editor-header"})
        st    (d/el :span {:class "editor-status"} "Ready")
        pr-in (d/el :input {:class "editor-prompt-input" :type "text" :placeholder "Describe what to change..."})
        ctr   (d/el :div {:class "editor-container"})
        vim-s (d/el :div {:class "editor-vim-status"})
        btn   (fn [cls icon lbl cb]
                (doto (d/el :button {:class cls} (when icon (d/ic icon "")) (or lbl ""))
                  (.addEventListener "click" cb)))]
    (doseq [el [(d/el :span {:class "editor-title"} "Page Editor") st
                (btn "editor-nav-btn" "chevron-left" nil
                  #(when-let [idx (:page-idx @editor-state)] (goto-page! (dec idx))))
                (btn "editor-nav-btn" "chevron-right" nil
                  #(when-let [idx (:page-idx @editor-state)] (goto-page! (inc idx))))
                (btn "editor-flip-btn" "arrow-left-right" nil
                  #(edit! (if (= @edit-side :left) :right :left)))
                (btn "editor-send-btn" "bot" "Ask LLM" #(send-to-claude!))
                (btn "editor-save-btn" "save" "Save" #(save!))
                (btn "editor-close-btn" nil "\u2715" #(dismiss!))]]
      (.appendChild hdr el))
    (let [pr-row (d/el :div {:class "editor-prompt-row"})]
      (.appendChild pr-row pr-in)
      (doseq [el [hdr pr-row ctr vim-s]] (.appendChild panel el)))
    {:panel panel :container ctr :status st :prompt pr-in :vim-status vim-s}))

(defn- make-editor [ctr src vim-el]
  (let [ed (.create (.-editor js/monaco) ctr
             #js {:value src :language "plaintext" :theme "vs-dark" :fontSize 13
                  :lineNumbers "on" :minimap #js {:enabled false} :wordWrap "on"
                  :scrollBeyondLastLine false :automaticLayout true :tabSize 2})]
    ;; Ctrl+Enter → send to LLM
    (.addCommand ed (bit-or (.. js/monaco -KeyMod -CtrlCmd) (.. js/monaco -KeyCode -Enter))
      #(send-to-claude!))
    ;; Ctrl+S → save
    (.addCommand ed (bit-or (.. js/monaco -KeyMod -CtrlCmd) (.. js/monaco -KeyCode -KeyS))
      #(save!))
    ;; Ctrl+X → close editor (Escape stays free for Vim)
    (.addCommand ed (bit-or (.. js/monaco -KeyMod -CtrlCmd) (.. js/monaco -KeyCode -KeyX))
      #(dismiss!))
    ;; Ctrl+[ → prev page, Ctrl+] → next page
    (.addCommand ed (bit-or (.. js/monaco -KeyMod -CtrlCmd) (.. js/monaco -KeyCode -BracketLeft))
      #(when-let [idx (:page-idx @editor-state)] (goto-page! (dec idx))))
    (.addCommand ed (bit-or (.. js/monaco -KeyMod -CtrlCmd) (.. js/monaco -KeyCode -BracketRight))
      #(when-let [idx (:page-idx @editor-state)] (goto-page! (inc idx))))
    (attach-vim! ed vim-el)
    ed))

;; ── Public API ───────────────────────────────────────────────
(defn- find-target []
  (when-let [spread (.querySelector js/document ".spread.active")]
    (let [pages (array-seq (.querySelectorAll spread ".page"))]
      (case (count pages)
        2 (if (= @edit-side :right)
            {:edit-page (second pages) :target (first pages)}
            {:edit-page (first pages) :target (second pages)})
        1 {:edit-page (first pages) :target (first pages)}
        nil))))

(defn edit!
  ([] (edit! nil))
  ([side]
   (when (:active? @editor-state) (dismiss!))
   ;; Use explicitly passed side, or side selected from bottom dots, or current
   (let [effective-side (or side @state/selected-edit-side)]
     (when effective-side (reset! edit-side effective-side))
     (reset! state/selected-edit-side nil))
   (when-let [{:keys [edit-page target]} (find-target)]
     (let [eid  (.-id edit-page)
           {:keys [spread->pages spread-ids]} @state/current-nav
           ids  (vec (mapcat #(get spread->pages %) (range (count spread-ids))))
           pidx (.indexOf ids eid)]
       (when-let [src (page-source pidx)]
         (let [{:keys [panel container status prompt vim-status]} (make-panel)]
           (.appendChild target panel)
           (set! (.-textContent status) "Loading editor…")
           (load-monaco!
             (fn []
               (let [ed (make-editor container src vim-status)
                     li (.onDidChangeModelContent ed (fn [_] (schedule-sync!)))]
                 (reset! editor-state {:active? true :instance ed :panel panel :page-idx pidx
                                       :target target :edit-page edit-page :listener li
                                       :diff-editor nil :loading? false})
                 ;; Eagerly fetch config so model name is available
                 (fetch-editor-config!)
                 (set! (.-textContent status) (str "Editing page " (inc pidx)
                                                    (when @vim-mode-enabled? " [vim]")))
                 (when (and js/lucide (.-createIcons js/lucide))
                   (.createIcons js/lucide #js {:attrs #js {:class "icon"}}))
                 (.focus prompt))))))))))

(defn goto-page!
  "Switch the editor to a different page by index (0-based) or page id string.
   Navigates the viewer and reloads the editor content."
  [target]
  (let [{:keys [id->spread spread->pages spread-ids]} @state/current-nav
        nav-go! (:go! @state/current-nav)
        all-ids (vec (mapcat #(get spread->pages %) (range (count spread-ids))))
        n       (count all-ids)
        idx (cond
              (number? target) (max 0 (min (dec n) target))
              (string? target) (let [i (.indexOf all-ids target)] (if (neg? i) -1 i))
              (keyword? target) (let [i (.indexOf all-ids (name target))] (if (neg? i) -1 i))
              :else -1)]
    (when (and (>= idx 0) (< idx n))
      ;; Navigate viewer to that page's spread
      (when-let [page-id (nth all-ids idx nil)]
        (when-let [si (get id->spread page-id)]
          (when nav-go! (nav-go! si nil))))
      ;; Close editor and re-open on the new page after spread transition
      (dismiss!)
      (js/setTimeout #(edit!) 400))))

(defn dismiss! []
  (when (:active? @editor-state)
    (stop-loading!)
    (detach-vim!)
    (when-let [t @sync-timer] (js/clearTimeout t) (reset! sync-timer nil))
    (when-let [d (:diff-editor @editor-state)] (.dispose ^js d))
    (when-let [l (:listener @editor-state)] (.dispose ^js l))
    (when-let [i (:instance @editor-state)] (.dispose ^js i))
    (when-let [p (:panel @editor-state)] (when (.-parentNode p) (.remove p)))
    (when-let [bar (some-> (:panel @editor-state) .-parentNode (.querySelector ".editor-diff-bar"))]
      (.remove bar))
    (reset! editor-state {:active? false :instance nil :panel nil :page-idx nil
                          :target nil :edit-page nil :listener nil
                          :diff-editor nil :loading? false})))

(defn active? [] (:active? @editor-state))
