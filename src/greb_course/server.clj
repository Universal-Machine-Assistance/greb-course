(ns greb-course.server
  "Ring server for serving courses with path-based routing."
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.file :refer [wrap-file]]
            [compojure.core :refer [GET POST defroutes]]
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
  (let [;; courses/org/images, then public/org/slug/images (static tree), then repo images/
        course-f (File. (str "courses/" org "/images") path)
        public-f (File. (str "public/" org "/" slug "/images") path)
        root-f   (File. "images" path)
        f        (cond (.exists course-f) course-f
                       (.exists public-f) public-f
                       (.exists root-f)   root-f
                       :else              nil)]
    (if f
      (response/file-response (.getPath f))
      (response/not-found (str "image " path)))))

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

(def handler (wrap-file-safe app "public"))

(defn -main [& _]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8020"))]
    (println (str "Starting server on port " port))
    (jetty/run-jetty #'handler {:port port :join? true})))
