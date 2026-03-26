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
            [greb-course.server-proxy :as proxy]
            [greb-course.server-pdf :as pdf]
            [greb-course.server-images :as images])
  (:import [java.io File]))

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------

(defn- index-response []
  (let [f (File. "public/index.html")]
    (if (.exists f)
      (-> (response/file-response (.getPath f))
          (response/content-type "text/html; charset=utf-8"))
      (response/not-found "index.html not found"))))

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

  ;; Editor: runtime config (URLs from env; key never sent to browser)
  (GET "/api/editor-config" []
       (-> (response/response (proxy/editor-config-json))
           (response/content-type "application/json; charset=utf-8")))

  ;; Same-origin Anthropic proxy (API key only from server env)
  (POST "/api/anthropic/messages" req (proxy/anthropic-proxy-handler req))

  ;; OpenRouter proxy (API key only from server env)
  (POST "/api/openrouter/chat" req (proxy/openrouter-proxy-handler req))

  ;; Export course as PDF (server-side, direct download)
  (GET "/api/export-pdf" req (pdf/export-pdf-handler req))
  (GET "/api/export-pdf/" req (pdf/export-pdf-handler req))

  ;; Save page patch (persist editor changes to disk)
  (POST "/api/save-page" req
    (try
      (let [body   (slurp (:body req))
            params (read-string body)
            org    (:org params)
            idx    (:page-idx params)
            content (:content params)]
        (if (and org (number? idx) content)
          (let [dir  (File. (str "courses/" org))
                file (File. dir "patches.edn")]
            (when-not (.exists dir) (.mkdirs dir))
            (let [patches (if (.exists file)
                            (read-string (slurp file))
                            {})]
              (spit file (pr-str (assoc patches idx content)))
              (edn-response {:ok true :page-idx idx})))
          (-> (edn-response {:ok false :error "Missing org, page-idx, or content"})
              (response/status 400))))
      (catch Exception e
        (-> (edn-response {:ok false :error (.getMessage e)})
            (response/status 500)))))

  ;; Kie AI image generation
  (POST "/api/kie/generate" req (proxy/kie-generate-handler req))
  (GET  "/api/kie/task" [taskId] (proxy/kie-task-status-handler taskId))
  (POST "/api/kie/save" req (proxy/kie-save-image-handler req))

  ;; Load saved page patches
  (GET "/api/patches" [org]
    (let [file (File. (str "courses/" org "/patches.edn"))]
      (if (.exists file)
        (-> (response/response (slurp file))
            (response/content-type "application/edn"))
        (edn-response {}))))

  ;; --- Image management API ---

  ;; List raw files
  (GET "/api/images" [subdir]
       (edn-response (images/list-images subdir)))

  ;; Upload (raw, no optimization)
  (POST "/api/images" {params :params}
        (let [file   (get params "file")
              subdir (get params "subdir")]
          (if file
            (edn-response {:ok true :path (images/upload-image! file subdir)})
            (-> (edn-response {:ok false :error "no file"})
                (response/status 400)))))

  ;; Delete raw file
  (DELETE "/api/images" [subdir name]
          (if (images/delete-image! subdir name)
            (edn-response {:ok true :deleted name})
            (response/not-found (pr-str {:ok false :error (str "not found: " name)}))))

  ;; Manifest: view
  (GET "/api/images/manifest" []
       (edn-response @images/manifest-atom))

  ;; Manifest: init/rebuild (scan volume, generate clean names)
  (POST "/api/images/manifest/init" []
        (edn-response (images/init-manifest!)))

  ;; Optimize: process all pending entries
  (POST "/api/images/optimize" []
        (edn-response (images/start-optimization!)))

  ;; Status: summary stats
  (GET "/api/images/status" []
       (edn-response (images/image-status)))

  ;; Redirects map: old-name -> new-name (for updating course files)
  (GET "/api/images/redirects" []
       (edn-response (images/image-redirects)))

  ;; Ingest: upload + auto-optimize (single file)
  (POST "/api/images/ingest" {params :params}
        (let [file   (get params "file")
              subdir (get params "subdir")]
          (if file
            (edn-response (images/ingest-image! file subdir))
            (-> (edn-response {:ok false :error "no file"})
                (response/status 400)))))

  ;; Preview clean name without uploading
  (GET "/api/images/preview-name" [filename]
       (if filename
         (edn-response (images/preview-name filename))
         (-> (edn-response {:error "provide ?filename=..."})
             (response/status 400))))

  ;; Embed viewer — frameable, no X-Frame-Options
  (GET "/embed/:org/:slug/" [org slug]
       (-> (index-response)
           (response/header "X-Frame-Options" "ALLOWALL")
           (response/header "Content-Security-Policy" "frame-ancestors *")))
  (GET "/embed/:org/:slug" [org slug]
       (response/redirect (str "/embed/" org "/" slug "/")))

  ;; Embed images (reuse same image serving as normal course)
  (GET "/embed/:org/:slug/images/*" [org slug :as req]
       (let [uri  (:uri req)
             pfx  (str "/embed/" org "/" slug "/images/")
             path (when (.startsWith uri pfx) (subs uri (count pfx)))]
         (if (and path (not= path ""))
           (images/serve-image org slug path)
           (response/not-found "Not found"))))

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
           (images/serve-image org slug path)
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
                     (not (re-matches #"^/(js|css|fonts|favicon)(/.*)?$" uri)))
                (re-matches #"/embed/[^/]+/[^/]+/?" uri))
          (handler req)
          (file-handler req))))))

(def handler (-> app
                 wrap-params
                 wrap-multipart-params
                 (wrap-file-safe "public")
                 wrap-content-type))

;; shadow-cljs dev-http uses `handler` without calling -main; load .env at ns init.
(proxy/load-dotenv!)
(proxy/print-boot-diagnostics!)

(defn -main [& _]
  (let [port (Integer/parseInt (or (proxy/env-lookup "PORT") "8020"))]
    ;; Load manifest if it exists
    (try
      (images/reload-manifest!)
      (println "Manifest loaded:" (count (:entries @images/manifest-atom)) "entries")
      (catch Exception e
        (println "No manifest yet (run POST /api/images/manifest/init):" (.getMessage e))))
    (println (str "Starting server on port " port))
    (jetty/run-jetty #'handler {:port port :join? true})))
