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
            [clojure.string :as str]
            [greb-course.images :as img])
  (:import [java.io File]
           [java.net URI]
           [java.net.http HttpClient HttpRequest HttpRequest$BodyPublishers HttpResponse$BodyHandlers]
           [java.time Duration]))

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

(defn- env-lookup
  "Prefer process env, then Java system properties (set from .env by load-dotenv!)."
  [^String name]
  (or (not-empty (System/getenv name))
      (not-empty (System/getProperty name))))

(defn- project-root-dir
  "Parent of src/ — stable even when shadow-cljs sets user.dir elsewhere."
  []
  (let [here (io/file *file*)]
    (if (and (.exists here) (.isFile here))
      (-> here .getParentFile .getParentFile .getParentFile .getCanonicalFile)
      (.getCanonicalFile (io/file (System/getProperty "user.dir" "."))))))

(defn- load-dotenv-file!
  [^java.io.File f]
  (when (.exists f)
    (println "[dotenv] loading" (.getPath f))
    (with-open [r (io/reader f)]
      (doseq [line (line-seq r)]
        (let [line (str/trim line)]
          (when (and (seq line)
                     (not (str/starts-with? line "#")))
            (when-let [[_ k raw] (re-matches
                                   #"^(?:export\s+)?([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$"
                                   line)]
              (let [raw (str/trim raw)
                    v (cond
                        (and (str/starts-with? raw "\"") (str/ends-with? raw "\""))
                        (subs raw 1 (dec (count raw)))
                        (and (str/starts-with? raw "'") (str/ends-with? raw "'"))
                        (subs raw 1 (dec (count raw)))
                        :else raw)]
                (when (seq v)
                  (System/setProperty k v))))))))))

(defn- load-dotenv!
  "Load repo-root secrets only — not under src/, so keys stay out of source trees.
  Order: .env then .env.local (local overrides). For old layouts, see dotenv-migration-hint!."
  []
  (let [root (project-root-dir)]
    (doseq [name [".env" ".env.local"]]
      (load-dotenv-file! (io/file root name)))))

(defn- dotenv-migration-hint! []
  (let [root   (project-root-dir)
        env    (io/file root ".env")
        legacy (io/file root "src/greb_course/.env")]
    (when (and (not (.exists env)) (.exists legacy))
      (println "[dotenv] Move Anthropic key to repo root:" (.getPath env)
               "(currently only" (.getPath legacy) "exists — that path is no longer loaded)."))))

(defn- anthropic-api-key-env []
  (or (env-lookup "GREB_ANTHROPIC_API_KEY")
      (env-lookup "ANTHROPIC_API_KEY")))

(defn- normalize-api-base [s]
  (str/replace (str/trim s) #"/$" ""))

(defn- anthropic-upstream-messages-url ^String []
  (or (some-> (env-lookup "GREB_ANTHROPIC_MESSAGES_UPSTREAM") str/trim not-empty)
      (let [base (or (some-> (env-lookup "GREB_ANTHROPIC_API_BASE") str/trim not-empty)
                     "https://api.anthropic.com")]
        (str (normalize-api-base base) "/v1/messages"))))

(defn- localhost-upstream? [^String uri-str]
  (try
    (when-let [h (some-> (URI/create uri-str) .getHost)]
      (let [host (str/lower-case h)]
        (or (#{"localhost" "127.0.0.1" "::1" "0:0:0:0:0:0:0:1"} host)
            (str/starts-with? host "127."))))
    (catch Exception _ false)))

(defn- anthropic-keyless-proxy-ok? [^String uri-str]
  (or (= "true" (env-lookup "GREB_ANTHROPIC_KEYLESS_PROXY"))
      (localhost-upstream? uri-str)))

(defn- anthropic-proxy-can-run? []
  (let [up (anthropic-upstream-messages-url)]
    (or (some? (some-> (anthropic-api-key-env) not-empty))
        (anthropic-keyless-proxy-ok? up))))

(defn- editor-config-json []
  "JSON for the browser editor: where to POST /v1/messages and whether the server adds the API key."
  (let [custom (env-lookup "GREB_ANTHROPIC_MESSAGES_URL")
        up (anthropic-upstream-messages-url)
        use-proxy? (anthropic-proxy-can-run?)
        [url uses-srv?] (cond
                          (some? (not-empty (or custom ""))) [(str/trim custom) false]
                          use-proxy? ["/api/anthropic/messages" true]
                          :else [up false])]
    (str "{\"messagesUrl\":" (pr-str url)
         ",\"usesServerKey\":" (if uses-srv? "true" "false") "}")))

(defn- anthropic-proxy-handler [req]
  (let [uri (anthropic-upstream-messages-url)
        api-key (anthropic-api-key-env)
        keyless? (anthropic-keyless-proxy-ok? uri)]
    (if (and (str/blank? api-key) (not keyless?))
      (-> (response/response
            (str "{\"error\":\"Set GREB_ANTHROPIC_API_KEY for Anthropic cloud, or GREB_ANTHROPIC_API_BASE=http://127.0.0.1:PORT targeting a local Anthropic-compatible /v1/messages (localhost allows keyless proxy). See .env.example.\"}"))
          (response/status 503)
          (response/content-type "application/json"))
      (try
        (let [body (if-let [b (:body req)] (slurp b) "")
              client (-> (HttpClient/newBuilder)
                         (.connectTimeout (Duration/ofSeconds 45))
                         .build)
              rb (-> (HttpRequest/newBuilder)
                     (.uri (URI/create uri))
                     (.timeout (Duration/ofMinutes 4))
                     (.header "Content-Type" "application/json"))
              rb (if (str/blank? api-key)
                   rb
                   (.header rb "x-api-key" api-key))
              rb (.header rb "anthropic-version" "2023-06-01")
              hreq (.build (.POST rb (HttpRequest$BodyPublishers/ofString body)))
              resp (.send client hreq (HttpResponse$BodyHandlers/ofString))]
          (-> (response/response (.body resp))
              (response/status (.statusCode resp))
              (response/content-type "application/json")))
        (catch Exception e
          (-> (response/response (str "{\"error\":" (pr-str (.getMessage e)) "}"))
              (response/status 502)
              (response/content-type "application/json")))))))

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

  ;; Editor: runtime config (URLs from env; key never sent to browser)
  (GET "/api/editor-config" []
       (-> (response/response (editor-config-json))
           (response/content-type "application/json; charset=utf-8")))

  ;; Same-origin Anthropic proxy (API key only from server env)
  (POST "/api/anthropic/messages" req (anthropic-proxy-handler req))

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
   Static paths like /js/*, /css/* are always served from the file system.
   Excludes / so the route handler serves index.html with proper content-type."
  [handler dir]
  (let [file-handler (wrap-file handler dir)]
    (fn [req]
      (let [uri (:uri req)]
        (if (or (= uri "/")
                (and (re-matches #"/[^/]+/[^/]+/?" uri)
                     (not (re-matches #"^/(js|css|fonts|favicon)(/.*)?$" uri))))
          (handler req)
          (file-handler req))))))

(def handler (-> app
                 wrap-params
                 wrap-multipart-params
                 (wrap-file-safe "public")
                 wrap-content-type))

;; shadow-cljs dev-http uses `handler` without calling -main; load .env at ns init.
(load-dotenv!)
(dotenv-migration-hint!)
(let [root (project-root-dir)
      f    (io/file root ".env")
      up   (anthropic-upstream-messages-url)]
  (when (and (.exists f)
             (str/blank? (anthropic-api-key-env))
             (not (anthropic-keyless-proxy-ok? up)))
    (println "[dotenv] .env found at" (.getPath f)
             "but no GREB_ANTHROPIC_API_KEY / ANTHROPIC_API_KEY — use GREB_ANTHROPIC_API_BASE for localhost or fix spelling"))
  (when-not (= "https://api.anthropic.com/v1/messages" up)
    (println "[anthropic] proxy upstream →" up)))

(defn -main [& _]
  (let [port (Integer/parseInt (or (env-lookup "PORT") "8020"))]
    ;; Load manifest if it exists
    (try
      (reload-manifest!)
      (println "Manifest loaded:" (count (:entries @manifest-atom)) "entries")
      (catch Exception e
        (println "No manifest yet (run POST /api/images/manifest/init):" (.getMessage e))))
    (println (str "Starting server on port " port))
    (jetty/run-jetty #'handler {:port port :join? true})))
