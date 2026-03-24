(ns greb-course.repl
  "REPL helpers for managing courses and pages live."
  (:require [greb-course.core :as core]
            [greb-course.state :as state]
            [greb-course.ui :as ui]
            [greb-course.editor :as editor]
            [greb-course.presentation :as pres]
            [greb-course.omnirepl :as omni]
            [clojure.string :as str]))

;; ── Edit in editor ──────────────────────────────────────────────
(defn- find-page-index
  "Find the page index for a given page id in the first course."
  [page-id]
  (let [pages (:pages (first (core/get-courses)))]
    (first (keep-indexed (fn [i pg] (when (= page-id (get-in pg [:data :id])) i)) pages))))

(defn- open-file! [file & [line]]
  (let [url (str "/dev/open?file=" (js/encodeURIComponent file)
                 (when line (str "&line=" line)))]
    (-> (js/fetch url)
        (.then #(.text %))
        (.then #(println (str "  " %))))))

(defn edit!
  "Open the source file for the current page (or given page-id) in your editor.
   Usage: (edit!)  or  (edit! \"riesgo-quimico\")  or  (edit! :course)"
  ([]
   (if-let [id (core/current-page-id)]
     (edit! id)
     (println "  No current page. Use (edit! \"page-id\") or (edit! :course)")))
  ([target]
   (let [course (first (core/get-courses))
         org    (get-in course [:meta :org])]
     (cond
       (= target :course)
       (open-file! (str "courses/" org "/course.cljs"))

       (= target :content)
       (open-file! (str "courses/" org "/content.cljs"))

       :else
       (let [page-id (name target)]
         ;; Open both course.cljs (page definition) and content.cljs (data)
         (open-file! (str "courses/" org "/course.cljs"))
         (println (str "  Page: \"" page-id "\" — opening course.cljs"))
         (println "  Tip: (edit! :content) to open content data file"))))))

;; ── Navigate ────────────────────────────────────────────────────
(defn go!
  "Navigate to a page by spread index or page id.
   Usage: (go! 5)  or  (go! \"riesgo-quimico\")"
  [target]
  (core/navigate! target))

(defn ask!
  "Claude quick question (same HTTP path as omnibar ask). Prints reply or error.
   Usage: (ask! \"hello world\")  or  (require '[greb-course.repl :as r]) (r/ask! \"hi\")"
  [prompt]
  (-> (editor/anthropic-quick-ask! prompt)
      (.then (fn [r] (println r)))
      (.catch (fn [e] (println "Error:" (.-message e)))))
  nil)

;; ── List ────────────────────────────────────────────────────────
(defn list-courses
  "Print all loaded courses."
  []
  (doseq [c (core/get-courses)]
    (println (str "  " (get-in c [:meta :org]) "/" (get-in c [:meta :slug])
                  "  — " (get-in c [:meta :title])
                  "  (" (count (:pages c)) " pages)"))))

(defn list-pages
  "Print all pages of the current (or first) course."
  ([] (list-pages 0))
  ([course-idx]
   (let [course (nth (core/get-courses) course-idx)]
     (println (str "\n  " (get-in course [:meta :title]) "\n"))
     (doseq [[i pg] (map-indexed vector (:pages course))]
       (println (str "  " i "  :" (name (:template pg))
                     "  id=" (get-in pg [:data :id] "-")
                     "  " (get-in pg [:data :title] "")))))))

;; ── Rename ──────────────────────────────────────────────────────
(defn rename-page!
  "Rename a page's title. course-idx defaults to 0."
  ([page-idx new-title] (rename-page! 0 page-idx new-title))
  ([course-idx page-idx new-title]
   (let [courses (vec (core/get-courses))
         course  (nth courses course-idx)
         pages   (vec (:pages course))
         page    (nth pages page-idx)
         page'   (assoc-in page [:data :title] new-title)
         pages'  (assoc pages page-idx page')
         course' (assoc course :pages pages')
         courses' (assoc courses course-idx course')]
     (core/update-courses! courses')
     (println (str "  Renamed page " page-idx " → \"" new-title "\"")))))

(defn set-page-id!
  "Change a page's :id. course-idx defaults to 0."
  ([page-idx new-id] (set-page-id! 0 page-idx new-id))
  ([course-idx page-idx new-id]
   (let [courses (vec (core/get-courses))
         course  (nth courses course-idx)
         pages   (vec (:pages course))
         page    (nth pages page-idx)
         page'   (assoc-in page [:data :id] new-id)
         pages'  (assoc pages page-idx page')
         course' (assoc course :pages pages')
         courses' (assoc courses course-idx course')]
     (core/update-courses! courses')
     (println (str "  Set page " page-idx " id → \"" new-id "\"")))))

;; ── Add ─────────────────────────────────────────────────────────
(defn add-page!
  "Add a page at position (or end). course-idx defaults to 0.
   Usage: (add-page! {:template :blocks :data {:id \"new\" :blocks [...]}})"
  ([page-def] (add-page! 0 page-def))
  ([course-idx page-def] (add-page! course-idx nil page-def))
  ([course-idx position page-def]
   (let [courses (vec (core/get-courses))
         course  (nth courses course-idx)
         pages   (vec (:pages course))
         pos     (or position (count pages))
         pages'  (vec (concat (subvec pages 0 pos)
                              [page-def]
                              (subvec pages pos)))
         course' (assoc course :pages pages')
         courses' (assoc courses course-idx course')]
     (core/update-courses! courses')
     (println (str "  Added page at position " pos
                   " (:" (name (:template page-def)) ")")))))

;; ── Remove ──────────────────────────────────────────────────────
(defn remove-page!
  "Remove page at index. course-idx defaults to 0."
  ([page-idx] (remove-page! 0 page-idx))
  ([course-idx page-idx]
   (let [courses (vec (core/get-courses))
         course  (nth courses course-idx)
         pages   (vec (:pages course))
         page    (nth pages page-idx)
         pages'  (vec (concat (subvec pages 0 page-idx)
                              (subvec pages (inc page-idx))))
         course' (assoc course :pages pages')
         courses' (assoc courses course-idx course')]
     (core/update-courses! courses')
     (println (str "  Removed page " page-idx
                   " (:" (name (:template page)) ")")))))

;; ── Move ────────────────────────────────────────────────────────
(defn move-page!
  "Move page from one position to another. course-idx defaults to 0."
  ([from-idx to-idx] (move-page! 0 from-idx to-idx))
  ([course-idx from-idx to-idx]
   (let [courses (vec (core/get-courses))
         course  (nth courses course-idx)
         pages   (vec (:pages course))
         page    (nth pages from-idx)
         pages'  (vec (concat (subvec pages 0 from-idx)
                              (subvec pages (inc from-idx))))
         pages'' (vec (concat (subvec pages' 0 to-idx)
                              [page]
                              (subvec pages' to-idx)))
         course' (assoc course :pages pages'')
         courses' (assoc courses course-idx course')]
     (core/update-courses! courses')
     (println (str "  Moved page " from-idx " → " to-idx)))))

;; ── Update page data ────────────────────────────────────────────
(defn update-page-data!
  "Merge keys into a page's :data map. course-idx defaults to 0.
   Usage: (update-page-data! 5 {:title \"New Title\" :id \"new-id\"})"
  ([page-idx data-map] (update-page-data! 0 page-idx data-map))
  ([course-idx page-idx data-map]
   (let [courses (vec (core/get-courses))
         course  (nth courses course-idx)
         pages   (vec (:pages course))
         page    (nth pages page-idx)
         page'   (update page :data merge data-map)
         pages'  (assoc pages page-idx page')
         course' (assoc course :pages pages')
         courses' (assoc courses course-idx course')]
     (core/update-courses! courses')
     (println (str "  Updated page " page-idx " data with " (keys data-map))))))

;; ── Quick page creators ─────────────────────────────────────────
(defn add-blocks-page!
  "Quick-add a blocks page. course-idx defaults to 0.
   Usage: (add-blocks-page! \"my-page\" [{:type :info-grid :icon \"star\" :title \"Hello\"
                                          :items [{:title \"A\" :text \"B\"}]}])"
  ([id blocks] (add-blocks-page! 0 id blocks))
  ([course-idx id blocks]
   (add-page! course-idx
              {:template :blocks
               :data {:id id :blocks blocks}})))

(defn add-hero-page!
  "Quick-add a hero-section page. course-idx defaults to 0.
   Usage: (add-hero-page! \"my-hero\" {:kicker \"K\" :title \"T\" :subtitle \"S\"
                                       :meter-value \"99%\" :meter-caption \"Done\"}
                          [{:type :info-grid :icon \"star\" :title \"Hello\"
                            :items [{:title \"A\" :text \"B\"}]}])"
  ([id hero blocks] (add-hero-page! 0 id hero blocks))
  ([course-idx id hero blocks]
   (add-page! course-idx
              {:template :hero-section
               :data {:id id :hero hero :blocks blocks}})))

;; ── Monaco editor (in-browser) ────────────────────────────────
(defn open-editor!
  "Open Monaco editor on current page. Pass :left or :right for spread side.
   Usage: (open-editor!)  or  (open-editor! :right)"
  ([] (editor/edit!))
  ([side] (editor/edit! side)))

(defn close-editor!
  "Close the Monaco editor."
  [] (editor/dismiss!))

(defn editor-page!
  "Switch editor to a different page by index (0-based) or page id.
   Usage: (editor-page! 5)  or  (editor-page! \"sedes\")"
  [target]
  (editor/goto-page! target))

(defn save!
  "Save current editor content to server (patches.edn).
   Usage: (save!)"
  [] (editor/save!))

(defn edit-llm!
  "Open editor and send LLM prompt immediately.
   Usage: (edit-llm! \"add a text block explaining the API\")"
  [prompt]
  (editor/edit-with-prompt! prompt))

(defn vim!
  "Toggle Vim mode in editor.
   Usage: (vim!)  or  (vim! true)  or  (vim! false)"
  ([] (let [on? (editor/toggle-vim!)]
        (println (str "  Vim mode " (if on? "ON" "OFF")))))
  ([on?] (editor/toggle-vim! on?)
         (println (str "  Vim mode " (if on? "ON" "OFF")))))

;; ── Presentation mode ─────────────────────────────────────────
(defn present!
  "Enter presentation mode. Optional start slide index.
   Usage: (present!)  or  (present! 5)"
  ([] (pres/enter-presentation!))
  ([idx] (pres/enter-presentation! idx)))

(defn doc-mode!
  "Exit presentation and return to document mode.
   Usage: (doc-mode!)"
  [] (pres/exit-presentation!))

(defn toggle-mode!
  "Toggle between document and presentation mode.
   Usage: (toggle-mode!)"
  [] (pres/toggle-view!))

(defn fullscreen!
  "Toggle fullscreen.
   Usage: (fullscreen!)"
  [] (pres/toggle-fullscreen!))

(defn- schedule-print! [msg]
  (omni/dismiss!)
  (when msg
    (println (str "  " msg))
    (ui/show-toast! msg 2400))
  (js/requestAnimationFrame
    (fn [] (.print js/window)))
  nil)

(defn print-dialog
  "Open browser print dialog.
   Usage: (print-dialog)"
  []
  (schedule-print! "Opening print dialog..."))

(defn- schedule-pdf-download! []
  (omni/dismiss!)
  (if-let [{:keys [org slug]} (:meta @state/current-course)]
    (do
      (println "  Exporting PDF...")
      (ui/show-toast! "Exporting PDF..." 2200)
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
                     (println (str "  Downloaded: " name)))))
          (.catch (fn [e]
                    (println (str "  PDF export failed: " (.-message e)))
                    (ui/show-toast! "PDF export failed. Restart server and try again." 4600)))))
    (println "  Error: no active course for PDF export"))
  nil)

(defn pdf-download
  "Download course PDF directly (no print dialog).
   Usage: (pdf-download)"
  []
  (schedule-pdf-download!))

;; Backward-compatible alias for common typo.
(def pdf-downlaod pdf-download)

;; ── OmniREPL ──────────────────────────────────────────────────
(defn omni!
  "Open the omnibar (Ctrl+G).
   Usage: (omni!)"
  [] (omni/show!))

(defn cmd!
  "Execute an omnibar command directly.
   Usage: (cmd! \"go 5\")  or  (cmd! \":edit add more text\")"
  [s]
  (if (omni/try-eval-command! s)
    (println (str "  ✓ " s))
    (println (str "  ✗ Unknown command: " s))))

;; ── Move block between pages ──────────────────
(defn move-block!
  "Move a block from one page to another. Creates new page if target doesn't exist.
   Usage: (move-block! 27 2 28)     ;; move block at index 2 from page 27 to page 28
          (move-block! 27 2 :new)   ;; move block at index 2 to a new page after 27"
  [from-page-idx block-idx to-target]
  (let [courses (vec (core/get-courses))
        course  (first courses)
        pages   (vec (:pages course))
        from-pg (nth pages from-page-idx nil)]
    (if-not from-pg
      (println (str "  Error: page " from-page-idx " not found"))
      (let [blocks (vec (get-in from-pg [:data :blocks]))
            block  (nth blocks block-idx nil)]
        (if-not block
          (println (str "  Error: block " block-idx " not found on page " from-page-idx))
          (let [;; Remove block from source page
                new-from-blocks (vec (concat (subvec blocks 0 block-idx)
                                             (subvec blocks (inc block-idx))))
                pages' (assoc-in pages [from-page-idx :data :blocks] new-from-blocks)
                ;; Add block to target
                pages'' (if (= to-target :new)
                          ;; Create new page after source with the moved block
                          (let [new-pg {:template :blocks
                                        :data {:id (str (get-in from-pg [:data :id]) "-cont")
                                               :header (get-in from-pg [:data :header])
                                               :blocks [block]}}]
                            (vec (concat (subvec pages' 0 (inc from-page-idx))
                                         [new-pg]
                                         (subvec pages' (inc from-page-idx)))))
                          ;; Move to existing page
                          (let [to-pg (nth pages' to-target nil)]
                            (if-not to-pg
                              (do (println (str "  Error: target page " to-target " not found"))
                                  nil)
                              (let [to-blocks (vec (or (get-in to-pg [:data :blocks]) []))]
                                (assoc-in pages' [to-target :data :blocks] (conj to-blocks block))))))
                course' (when pages'' (assoc course :pages pages''))
                courses' (when course' (assoc courses 0 course'))]
            (when courses'
              (core/update-courses! courses')
              (println (str "  Moved block " block-idx " from page " from-page-idx
                            " → " (if (= to-target :new) (str "new page " (inc from-page-idx)) (str "page " to-target)))))))))))

(defn split-page!
  "Split overflowing page: move blocks starting at block-idx to a new continuation page.
   Usage: (split-page! 27 3)  ;; move blocks 3+ from page 27 to new page"
  [page-idx from-block-idx]
  (let [courses (vec (core/get-courses))
        course  (first courses)
        pages   (vec (:pages course))
        pg      (nth pages page-idx nil)]
    (if-not pg
      (println (str "  Error: page " page-idx " not found"))
      (let [blocks (vec (get-in pg [:data :blocks]))
            keep-blocks (subvec blocks 0 from-block-idx)
            move-blocks (subvec blocks from-block-idx)
            pages' (assoc-in pages [page-idx :data :blocks] keep-blocks)
            new-pg {:template :blocks
                    :data {:id (str (get-in pg [:data :id]) "-cont")
                           :header {:icon (get-in pg [:data :header :icon])
                                    :kicker (get-in pg [:data :header :kicker])
                                    :title (str (get-in pg [:data :header :title]) " (cont.)")}
                           :blocks (vec move-blocks)}}
            pages'' (vec (concat (subvec pages' 0 (inc page-idx))
                                  [new-pg]
                                  (subvec pages' (inc page-idx))))
            course' (assoc course :pages pages'')
            courses' (assoc courses 0 course')]
        (core/update-courses! courses')
        (println (str "  Split page " page-idx ": kept " from-block-idx " blocks, moved " (count move-blocks) " to new page"))))))

;; ── Image generation (Kie AI) ─────────────────
(defn- show-image-dialog! [url]
  "Show custom modal with image preview and save controls."
  (let [org      (or (get-in @state/current-course [:meta :org]) "")
        scrim    (.createElement js/document "div")
        dialog   (.createElement js/document "div")
        img      (.createElement js/document "img")
        controls (.createElement js/document "div")
        input    (.createElement js/document "input")
        save-btn (.createElement js/document "button")
        close-btn (.createElement js/document "button")
        dismiss! (fn [] (when (.-parentNode scrim) (.remove scrim)))]
    ;; Scrim
    (set! (.-className scrim) "img-dialog-scrim")
    (.addEventListener scrim "click" (fn [e] (when (= (.-target e) scrim) (dismiss!))))
    ;; Dialog
    (set! (.-className dialog) "img-dialog")
    ;; Image preview
    (set! (.-className img) "img-dialog-preview")
    (set! (.-src img) url)
    (set! (.-alt img) "Generated image")
    ;; Input
    (set! (.-className input) "img-dialog-input")
    (set! (.-type input) "text")
    (set! (.-value input) "generated.png")
    (set! (.-placeholder input) "filename.png")
    (.addEventListener input "keydown" (fn [e] (when (= (.-key e) "Enter") (.click save-btn))))
    ;; Save button
    (set! (.-className save-btn) "img-dialog-save")
    (set! (.-textContent save-btn) (str "Save to " org "/images/"))
    (.addEventListener save-btn "click"
      (fn []
        (let [fname (.-value input)]
          (when (pos? (count fname))
            (save-image! url fname)
            (dismiss!)))))
    ;; Close button
    (set! (.-className close-btn) "img-dialog-close")
    (set! (.-textContent close-btn) "\u2715")
    (.addEventListener close-btn "click" dismiss!)
    ;; Path label
    (let [path-label (.createElement js/document "span")]
      (set! (.-className path-label) "img-dialog-path")
      (set! (.-textContent path-label) (str "courses/" org "/images/"))
      (set! (.-className controls) "img-dialog-controls")
      (.appendChild controls path-label)
      (.appendChild controls input)
      (.appendChild controls save-btn))
    (.appendChild dialog close-btn)
    (.appendChild dialog img)
    (.appendChild dialog controls)
    (.appendChild scrim dialog)
    (.appendChild (.-body js/document) scrim)
    (.focus input)
    (.select input)))

(defn- on-image-ready! [url]
  (println (str "  ✓ Image ready!"))
  (println (str "  URL: " url))
  (show-image-dialog! url))

(defn- poll-kie-task! [task-id]
  (let [poll (atom nil) tries (atom 0)]
    (reset! poll
      (js/setInterval
        (fn []
          (swap! tries inc)
          (when (> @tries 40) (js/clearInterval @poll) (println "  Timeout"))
          (-> (js/fetch (str "/api/kie/task?taskId=" task-id))
              (.then #(.json %))
              (.then (fn [s]
                       (when (= "SUCCESS" (some-> (aget s "data") (aget "status")))
                         (js/clearInterval @poll)
                         (let [url (some-> (aget s "data") (aget "response") (aget "resultUrls") (aget 0))]
                           (on-image-ready! url)))))))
        3000))))

(defn- get-illustration-style []
  (let [course-id (get-in @state/current-course [:meta :id] "")
        ls-key (str "greb-style:" course-id)]
    (or @state/illustration-style
        (.getItem js/localStorage ls-key)
        (get-in @state/current-course [:meta :style :illustration])
        "")))

(defn imagine!
  "Generate an image with Kie AI. Auto-prepends course illustration style.
   Usage: (imagine! \"a modern logo\")
          (imagine! \"wide banner\" {:size \"16:9\" :no-style true})"
  ([prompt] (imagine! prompt {}))
  ([prompt opts]
   (let [style (if (:no-style opts) "" (get-illustration-style))
         full-prompt (if (pos? (count style))
                       (str style ", " prompt)
                       prompt)]
     (println (str "  Style: " (if (pos? (count style)) style "(none)")))
     (println (str "  Generating: " full-prompt))
     (-> (js/fetch "/api/kie/generate"
           #js {:method "POST" :headers #js {"Content-Type" "application/json"}
                :body (.stringify js/JSON (clj->js {:prompt full-prompt :size (or (:size opts) "1:1")}))})
       (.then #(.json %))
       (.then (fn [r]
                (if-let [task-id (some-> (aget r "data") (aget "taskId"))]
                  (do (println (str "  Task: " task-id " — polling..."))
                      (poll-kie-task! task-id))
                  (println (str "  Error: " (aget r "msg"))))))
       (.catch #(println (str "  Error: " (.-message %))))))
   nil))

(defn save-image!
  "Download a generated image URL and save to current course images folder.
   Usage: (save-image! \"https://...\" \"cover.png\")
          (save-image! \"https://...\" \"cover.png\" \"harmonia\")  ;; specific org"
  ([url filename] (save-image! url filename nil))
  ([url filename org-override]
   (let [org (or org-override
                 (get-in @state/current-course [:meta :org])
                 (get-in (first (core/get-courses)) [:meta :org]))]
     (if-not org
       (println "  Error: no course loaded. Pass org: (save-image! url name \"harmonia\")")
       (do (println (str "  Saving to courses/" org "/images/" filename "..."))
           (-> (js/fetch "/api/kie/save"
                 #js {:method "POST" :headers #js {"Content-Type" "application/edn"}
                      :body (pr-str {:url url :org org :filename filename})})
               (.then #(.text %))
               (.then (fn [t] (println (str "  " t))))
               (.catch #(println (str "  Error: " (.-message %)))))))
     nil)))

(defn generate-cover!
  "Generate a cover image for the current course using its title/description.
   Usage: (generate-cover!)  or  (generate-cover! \"additional context\")"
  ([] (generate-cover! ""))
  ([extra]
   (let [course (or @state/current-course (first (core/get-courses)))
         title (get-in course [:meta :title])
         desc  (get-in course [:meta :description])
         prompt (str "Professional book cover design for: " title ". " desc
                     (when (seq extra) (str " " extra))
                     ". Modern, clean, editorial style. No text overlay.")]
     (println (str "  Generating cover for: " title))
     (imagine! prompt {:size "3:4"}))))

;; ── Info ──────────────────────────────────────────────────────
(defn current-page
  "Print current page id and index."
  []
  (let [id (core/current-page-id)]
    (println (str "  Page: " (or id "(none)")))))

(defn help
  "Print available REPL commands."
  []
  (println "
  ── Navigation ──
  (go! 5)              Go to spread 5
  (go! \"sedes\")        Go to page by id
  (current-page)       Show current page id
  (list-pages)         List all pages
  (list-courses)       List all courses

  ── Editor ──
  (open-editor!)       Open Monaco editor on current page
  (open-editor! :right) Edit right page in spread
  (close-editor!)      Close editor
  (save!)              Save editor changes to disk
  (edit-llm! \"...\")    Open editor + send LLM prompt
  (vim!)               Toggle Vim mode
  (ask! \"...\")         Quick LLM question

  ── Modes ──
  (present!)           Enter presentation mode
  (present! 5)         Start at slide 5
  (doc-mode!)          Exit to document mode
  (toggle-mode!)       Toggle doc ↔ presentation
  (fullscreen!)        Toggle fullscreen
  (print-dialog)       Open browser print dialog
  (pdf-download)       Open print dialog / save as PDF
  (omni!)              Open omnibar (Ctrl+G)
  (cmd! \"go 5\")        Run omnibar command

  ── Image Generation (Kie AI) ──
  (imagine! \"prompt\")    Generate image, print URL
  (imagine! \"p\" {:size \"16:9\"})  With aspect ratio
  (save-image! url \"name.png\")  Save to course images
  (generate-cover!)      Auto-generate course cover

  ── Page editing ──
  (rename-page! 5 \"New Title\")
  (add-page! {:template :blocks :data {...}})
  (remove-page! 5)
  (move-page! 3 7)
  (update-page-data! 5 {:title \"X\"})
  (move-block! 27 2 28)  Move block 2 from page 27 to 28
  (move-block! 27 2 :new) Move block to new page
  (split-page! 27 3)     Split page 27 at block 3
  (edit! \"page-id\")     Open source file in VS Code
  (edit! :course)        Open course.cljs
  (edit! :content)       Open content.cljs
  "))
