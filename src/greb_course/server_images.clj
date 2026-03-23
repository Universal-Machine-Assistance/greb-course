(ns greb-course.server-images
  "Image management: serving, uploading, manifest operations."
  (:require [ring.util.response :as response]
            [clojure.java.io :as io]
            [greb-course.images :as img])
  (:import [java.io File]))

;; ---------------------------------------------------------------------------
;; State
;; ---------------------------------------------------------------------------

(def ^:private images-root "images")
(def manifest-atom (atom {:version 1 :entries []}))
(def index-atom (atom {:by-original {} :by-optimized {}}))

(defn reload-manifest! []
  (let [m (img/load-manifest images-root)]
    (reset! manifest-atom m)
    (reset! index-atom (img/build-index m))
    m))

;; ---------------------------------------------------------------------------
;; Image serving (manifest-aware)
;; ---------------------------------------------------------------------------

(defn- find-optimized-file
  "Given a manifest entry, return the optimized File if it exists."
  [entry]
  (when-let [opt-name (:optimized entry)]
    (let [subdir (:subdir entry)
          f (File. images-root (str "optimized/" subdir "/" opt-name))]
      (when (.exists f) f))))

(defn- find-original-file
  "Given a manifest entry, return the original backup File."
  [entry]
  (let [subdir (:subdir entry)
        f (File. images-root (str "originals/" subdir "/" (:original entry)))]
    (when (.exists f) f)))

(defn serve-image [org slug path]
  (let [idx     @index-atom
        subdirs (if org [(str "courses/" org) "root"] ["root"])
        ;; Look up by original name, clean name, or optimized name across subdirs
        entry   (some (fn [sd]
                        (or (get-in idx [:by-original  [sd path]])
                            (get-in idx [:by-clean     [sd path]])
                            (get-in idx [:by-optimized [sd path]])))
                      subdirs)]
    (if entry
      ;; Manifest hit: serve optimized, fallback to original
      (if-let [f (or (find-optimized-file entry) (find-original-file entry))]
        (let [fname (.getName f)
              ctype (cond
                      (.endsWith fname ".webp") "image/webp"
                      (.endsWith fname ".png")  "image/png"
                      (.endsWith fname ".jpg")  "image/jpeg"
                      (.endsWith fname ".jpeg") "image/jpeg"
                      (.endsWith fname ".gif")  "image/gif"
                      :else                     "application/octet-stream")]
          (-> (response/file-response (.getPath f))
              (response/content-type ctype)
              (response/header "Cache-Control" "public, max-age=31536000, immutable")))
        (response/not-found (str "image " path)))
      ;; Legacy fallback: direct file lookup (check multiple locations)
      (let [candidates [(File. (str "courses/" org "/images") path)
                        (File. (str "images/courses/" org) path)
                        (File. (str "public/" org "/" slug "/images") path)
                        (File. "images" path)]
            ;; Also scan all courses/*/images/ directories for the file
            courses-dir (File. "courses")
            scan-hits   (when (.isDirectory courses-dir)
                          (->> (.listFiles courses-dir)
                               (filter #(.isDirectory %))
                               (map #(File. (File. % "images") path))
                               (filter #(.exists %))
                               first))
            f (or (first (filter #(.exists %) candidates)) scan-hits)]
        (if f
          (response/file-response (.getPath f))
          (response/not-found (str "image " path)))))))

;; ---------------------------------------------------------------------------
;; Upload / list / delete (raw files)
;; ---------------------------------------------------------------------------

(defn- resolve-upload-dir [subdir]
  (let [dir (if (and subdir (not= subdir ""))
              (File. images-root subdir)
              (File. images-root))]
    (.mkdirs dir)
    dir))

(defn upload-image! [{:keys [tempfile filename]} subdir]
  (let [dir  (resolve-upload-dir subdir)
        dest (File. dir filename)]
    (io/copy tempfile dest)
    (.getPath dest)))

(defn list-images [subdir]
  (let [dir (resolve-upload-dir subdir)]
    (->> (.listFiles dir)
         (filter #(.isFile %))
         (map #(.getName %))
         sort
         vec)))

(defn delete-image! [subdir filename]
  (let [f (File. (resolve-upload-dir subdir) filename)]
    (when (.exists f)
      (.delete f)
      true)))

;; ---------------------------------------------------------------------------
;; Manifest / optimize / ingest helpers (used by routes in server.clj)
;; ---------------------------------------------------------------------------

(defn init-manifest! []
  (let [m (img/init-manifest! images-root)]
    (reload-manifest!)
    {:ok true :entries (count (:entries m))}))

(defn start-optimization! []
  (future
    (let [m (img/process-all! images-root @manifest-atom)]
      (img/save-manifest! images-root m)
      (reload-manifest!)
      (println "Optimization complete.")))
  {:ok true :status "optimization started in background"})

(defn image-status []
  (let [entries (:entries @manifest-atom)
        by-status (group-by :status entries)
        total-orig (reduce + 0 (keep :original-size entries))
        total-opt  (reduce + 0 (keep :optimized-size entries))]
    {:total      (count entries)
     :done       (count (get by-status :done []))
     :pending    (count (get by-status :pending []))
     :error      (count (get by-status :error []))
     :original-bytes total-orig
     :optimized-bytes total-opt
     :savings-pct (if (pos? total-orig)
                    (Math/round (* 100.0 (- 1 (/ (double total-opt) total-orig))))
                    0)}))

(defn image-redirects []
  (let [entries (:entries @manifest-atom)]
    (into {} (map (fn [{:keys [original optimized subdir]}]
                    [original {:optimized optimized :subdir subdir}])
                  entries))))

(defn ingest-image! [file subdir]
  (let [subdir (or subdir "root")
        ;; Save raw file first
        raw-path (upload-image! file (if (= subdir "root") nil subdir))
        ;; Generate clean name
        filename    (get file :filename)
        [stem ext]  (img/clean-filename filename)
        out-ext     "webp"
        clean-name  (str stem "." out-ext)
        ;; Build entry
        entry {:original  filename
               :clean     (str stem "." ext)
               :subdir    subdir
               :optimized nil
               :status    :pending}
        ;; Optimize
        result (img/process-entry! images-root entry)]
    (when result
      (let [m (swap! manifest-atom
                     update :entries conj result)]
        (img/save-manifest! images-root m)
        (reset! index-atom (img/build-index m))))
    {:ok true
     :original filename
     :optimized (:optimized result)
     :savings (when (and (:original-size result) (:optimized-size result))
                (str (Math/round (* 100.0 (- 1 (/ (double (:optimized-size result))
                                                  (:original-size result))))) "%"))}))

(defn preview-name [filename]
  (let [[stem ext] (img/clean-filename filename)]
    {:original filename
     :clean (str stem "." ext)
     :optimized (str stem ".webp")}))
