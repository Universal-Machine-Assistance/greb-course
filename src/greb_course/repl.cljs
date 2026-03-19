(ns greb-course.repl
  "REPL helpers for managing courses and pages live."
  (:require [greb-course.core :as core]
            [greb-course.editor :as editor]
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
