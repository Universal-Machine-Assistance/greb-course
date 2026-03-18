(ns greb-course.server
  "Ring server for serving courses with path-based routing."
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [compojure.core :refer [GET POST DELETE defroutes]]
            [compojure.route :as route]
            [clojure.java.io :as io])
  (:import [java.io File]))

(defn- index-response []
  (let [f (File. "public/index.html")]
    (if (.exists f)
      (-> (response/file-response (.getPath f))
          (response/content-type "text/html; charset=utf-8"))
      (response/not-found "index.html not found"))))

(defn- serve-image [org slug path]
  (let [;; courses/org/images, then volume courses/org, then public/org/slug/images, then root images/
        course-f (File. (str "courses/" org "/images") path)
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
      (response/not-found (str "image " path)))))

(def ^:private images-root "images")

(defn- resolve-upload-dir
  "Resolve target directory within the images volume.
   subdir can be nil (root) or e.g. \"courses/romerlabs\"."
  [subdir]
  (let [dir (if (and subdir (not= subdir ""))
              (File. images-root subdir)
              (File. images-root))]
    (.mkdirs dir)
    dir))

(defn- upload-image!
  "Save an uploaded file (multipart map) to the images volume under subdir."
  [{:keys [tempfile filename]} subdir]
  (let [dir  (resolve-upload-dir subdir)
        dest (File. dir filename)]
    (io/copy tempfile dest)
    (.getPath dest)))

(defn- list-images
  "List image files in a subdirectory of the images volume."
  [subdir]
  (let [dir (resolve-upload-dir subdir)]
    (->> (.listFiles dir)
         (filter #(.isFile %))
         (map #(.getName %))
         sort
         vec)))

(defn- delete-image!
  "Delete an image from the images volume."
  [subdir filename]
  (let [f (File. (resolve-upload-dir subdir) filename)]
    (when (.exists f)
      (.delete f)
      true)))

(defn- open-in-editor!
  "Open a file in the default editor. Supports optional line number."
  [file-path & [line]]
  (let [f (File. file-path)]
    (when (.exists f)
      (let [abs (.getAbsolutePath f)]
        ;; Try VS Code first, fall back to macOS open
        (try
          (let [arg (if line (str abs ":" line) abs)]
            (.exec (Runtime/getRuntime) (into-array ["code" "--goto" arg])))
          (catch Exception _
            (.exec (Runtime/getRuntime) (into-array ["open" "-t" abs]))))
        abs))))

(defroutes app
  ;; Catalog (landing)
  (GET "/" [] (index-response))

  ;; Dev: open file in editor
  (GET "/dev/open" [file line]
       (if-let [abs (open-in-editor! file (when line (Integer/parseInt line)))]
         (-> (response/response (str "opened " abs))
             (response/content-type "text/plain"))
         (response/not-found (str "file not found: " file))))

  ;; --- Image management API (before course routes to avoid /:org/:slug match) ---

  ;; List images: GET /api/images?subdir=courses/romerlabs
  (GET "/api/images" [subdir]
       (-> (response/response (pr-str (list-images subdir)))
           (response/content-type "application/edn")))

  ;; Upload image: POST /api/images  (multipart, field "file", optional "subdir")
  (POST "/api/images" {params :params}
        (let [file   (get params "file")
              subdir (get params "subdir")]
          (if file
            (let [path (upload-image! file subdir)]
              (-> (response/response (str "{:ok true :path \"" path "\"}"))
                  (response/content-type "application/edn")))
            (-> (response/response "{:ok false :error \"no file\"}")
                (response/status 400)
                (response/content-type "application/edn")))))

  ;; Delete image: DELETE /api/images?subdir=courses/romerlabs&name=foo.png
  (DELETE "/api/images" [subdir name]
          (if (delete-image! subdir name)
            (-> (response/response (str "{:ok true :deleted \"" name "\"}"))
                (response/content-type "application/edn"))
            (response/not-found (str "{:ok false :error \"not found: " name "\"}"))))

  ;; Course viewer — serve same index.html, JS handles routing
  (GET "/:org/:slug/" [org slug] (index-response))
  (GET "/:org/:slug" [org slug]
       (response/redirect (str "/" org "/" slug "/")))

  ;; Course images
  (GET "/:org/:slug/images/*" [org slug :as req]
       (let [uri  (:uri req)
             pfx  (str "/" org "/" slug "/images/")
             path (when (.startsWith uri pfx) (subs uri (count pfx)))]
         (if (and path (not= path ""))
           (serve-image org slug path)
           (response/not-found "Not found"))))

  ;; Static assets (css, js) — served by wrap-file below
  (route/not-found "Not found"))

(defn wrap-file-safe
  "Serve static files from dir, but only for paths that don't match course routes."
  [handler dir]
  (let [file-handler (wrap-file handler dir)]
    (fn [req]
      (let [uri (:uri req)]
        ;; Skip static file lookup for course paths (two segments) to avoid
        ;; empty public subdirectories shadowing the index-response route.
        (if (re-matches #"/[^/]+/[^/]+/?" uri)
          (handler req)
          (file-handler req))))))

(def handler (-> app
                  wrap-multipart-params
                  (wrap-file-safe "public")))

(defn -main [& _]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8020"))]
    (println (str "Starting server on port " port))
    (jetty/run-jetty #'handler {:port port :join? true})))
