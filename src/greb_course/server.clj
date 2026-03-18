(ns greb-course.server
  "Ring server for serving courses with path-based routing."
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core :refer [GET POST DELETE defroutes]]
            [compojure.route :as route]
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

(defn- serve-image [org slug path]
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
      ;; Legacy fallback: direct file lookup
      (let [course-f (File. (str "courses/" org "/images") path)
            vol-f    (File. (str "images/courses/" org) path)
            public-f (File. (str "public/" org "/" slug "/images") path)
            root-f   (File. "images" path)
            f        (cond (.exists course-f) course-f
                           (.exists vol-f)    vol-f
                           (.exists public-f) public-f
                           (.exists root-f)   root-f
                           :else              nil)]
        (if f
          (response/file-response (.getPath f))
          (response/not-found (str "image " path)))))))

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------

(defn- index-response []
  (let [f (File. "public/index.html")]
    (if (.exists f)
      (-> (response/file-response (.getPath f))
          (response/content-type "text/html; charset=utf-8"))
      (response/not-found "index.html not found"))))

(defn- resolve-upload-dir [subdir]
  (let [dir (if (and subdir (not= subdir ""))
              (File. images-root subdir)
              (File. images-root))]
    (.mkdirs dir)
    dir))

(defn- upload-image! [{:keys [tempfile filename]} subdir]
  (let [dir  (resolve-upload-dir subdir)
        dest (File. dir filename)]
    (io/copy tempfile dest)
    (.getPath dest)))

(defn- list-images [subdir]
  (let [dir (resolve-upload-dir subdir)]
    (->> (.listFiles dir)
         (filter #(.isFile %))
         (map #(.getName %))
         sort
         vec)))

(defn- delete-image! [subdir filename]
  (let [f (File. (resolve-upload-dir subdir) filename)]
    (when (.exists f)
      (.delete f)
      true)))

(defn- edn-response [data]
  (-> (response/response (pr-str data))
      (response/content-type "application/edn")))

(defn- open-in-editor! [file-path & [line]]
  (let [f (File. file-path)]
    (when (.exists f)
      (let [abs (.getAbsolutePath f)]
        (try
          (let [arg (if line (str abs ":" line) abs)]
            (.exec (Runtime/getRuntime) (into-array ["code" "--goto" arg])))
          (catch Exception _
            (.exec (Runtime/getRuntime) (into-array ["open" "-t" abs]))))
        abs))))

;; ---------------------------------------------------------------------------
;; Routes
;; ---------------------------------------------------------------------------

(defroutes app
  ;; Catalog (landing)
  (GET "/" [] (index-response))

  ;; Dev: open file in editor
  (GET "/dev/open" [file line]
       (if-let [abs (open-in-editor! file (when line (Integer/parseInt line)))]
         (-> (response/response (str "opened " abs))
             (response/content-type "text/plain"))
         (response/not-found (str "file not found: " file))))

  ;; --- Image management API ---

  ;; List raw files
  (GET "/api/images" [subdir]
       (edn-response (list-images subdir)))

  ;; Upload (raw, no optimization)
  (POST "/api/images" {params :params}
        (let [file   (get params "file")
              subdir (get params "subdir")]
          (if file
            (edn-response {:ok true :path (upload-image! file subdir)})
            (-> (edn-response {:ok false :error "no file"})
                (response/status 400)))))

  ;; Delete raw file
  (DELETE "/api/images" [subdir name]
          (if (delete-image! subdir name)
            (edn-response {:ok true :deleted name})
            (response/not-found (pr-str {:ok false :error (str "not found: " name)}))))

  ;; Manifest: view
  (GET "/api/images/manifest" []
       (edn-response @manifest-atom))

  ;; Manifest: init/rebuild (scan volume, generate clean names)
  (POST "/api/images/manifest/init" []
        (let [m (img/init-manifest! images-root)]
          (reload-manifest!)
          (edn-response {:ok true :entries (count (:entries m))})))

  ;; Optimize: process all pending entries
  (POST "/api/images/optimize" []
        (future
          (let [m (img/process-all! images-root @manifest-atom)]
            (img/save-manifest! images-root m)
            (reload-manifest!)
            (println "Optimization complete.")))
        (edn-response {:ok true :status "optimization started in background"}))

  ;; Status: summary stats
  (GET "/api/images/status" []
       (let [entries (:entries @manifest-atom)
             by-status (group-by :status entries)
             total-orig (reduce + 0 (keep :original-size entries))
             total-opt  (reduce + 0 (keep :optimized-size entries))]
         (edn-response {:total      (count entries)
                        :done       (count (get by-status :done []))
                        :pending    (count (get by-status :pending []))
                        :error      (count (get by-status :error []))
                        :original-bytes total-orig
                        :optimized-bytes total-opt
                        :savings-pct (if (pos? total-orig)
                                       (Math/round (* 100.0 (- 1 (/ (double total-opt) total-orig))))
                                       0)})))

  ;; Redirects map: old-name -> new-name (for updating course files)
  (GET "/api/images/redirects" []
       (let [entries (:entries @manifest-atom)]
         (edn-response
          (into {} (map (fn [{:keys [original optimized subdir]}]
                          [original {:optimized optimized :subdir subdir}])
                        entries)))))

  ;; Ingest: upload + auto-optimize (single file)
  (POST "/api/images/ingest" {params :params}
        (let [file   (get params "file")
              subdir (or (get params "subdir") "root")]
          (if file
            (let [;; Save raw file first
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
              (edn-response {:ok true
                             :original filename
                             :optimized (:optimized result)
                             :savings (when (and (:original-size result) (:optimized-size result))
                                        (str (Math/round (* 100.0 (- 1 (/ (double (:optimized-size result))
                                                                          (:original-size result))))) "%"))}))
            (-> (edn-response {:ok false :error "no file"})
                (response/status 400)))))

  ;; Preview clean name without uploading
  (GET "/api/images/preview-name" [filename]
       (if filename
         (let [[stem ext] (img/clean-filename filename)]
           (edn-response {:original filename
                          :clean (str stem "." ext)
                          :optimized (str stem ".webp")}))
         (-> (edn-response {:error "provide ?filename=..."})
             (response/status 400))))

  ;; Course viewer — serve same index.html, JS handles routing
  (GET "/:org/:slug/" [org slug] (index-response))
  (GET "/:org/:slug" [org slug]
       (response/redirect (str "/" org "/" slug "/")))

  ;; Course images (manifest-aware)
  (GET "/:org/:slug/images/*" [org slug :as req]
       (let [uri  (:uri req)
             pfx  (str "/" org "/" slug "/images/")
             path (when (.startsWith uri pfx) (subs uri (count pfx)))]
         (if (and path (not= path ""))
           (serve-image org slug path)
           (response/not-found "Not found"))))

  ;; Static assets (css, js) — served by wrap-file below
  (route/not-found "Not found"))

;; ---------------------------------------------------------------------------
;; Middleware & startup
;; ---------------------------------------------------------------------------

(defn wrap-file-safe
  "Serve static files from dir, but only for paths that don't match course routes.
   Static paths like /js/*, /css/* are always served from the file system."
  [handler dir]
  (let [file-handler (wrap-file handler dir)]
    (fn [req]
      (let [uri (:uri req)]
        (if (and (re-matches #"/[^/]+/[^/]+/?" uri)
                 (not (re-matches #"^/(js|css|fonts|favicon)(/.*)?$" uri)))
          (handler req)
          (file-handler req))))))

(def handler (-> app
                 wrap-params
                 wrap-multipart-params
                 (wrap-file-safe "public")
                 wrap-content-type))

(defn -main [& _]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8020"))]
    ;; Load manifest if it exists
    (try
      (reload-manifest!)
      (println "Manifest loaded:" (count (:entries @manifest-atom)) "entries")
      (catch Exception e
        (println "No manifest yet (run POST /api/images/manifest/init):" (.getMessage e))))
    (println (str "Starting server on port " port))
    (jetty/run-jetty #'handler {:port port :join? true})))
