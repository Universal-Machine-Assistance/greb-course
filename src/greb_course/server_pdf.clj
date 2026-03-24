(ns greb-course.server-pdf
  "Server-side PDF export using Playwright."
  (:require [clojure.java.shell :as sh]
            [clojure.string :as str]
            [ring.util.response :as response])
  (:import [java.io File]))

(defn- safe-token [s fallback]
  (let [v (or s fallback)]
    (-> v
        (str/replace #"[^A-Za-z0-9._-]+" "-")
        (str/replace #"-{2,}" "-")
        (str/replace #"^-|-$" "")
        (#(if (seq %) % fallback)))))

(defn- build-base-url [req]
  (let [scheme (name (or (:scheme req) :http))
        host   (or (get-in req [:headers "host"]) "localhost:8021")]
    (str scheme "://" host)))

(defn export-pdf-handler
  "Generate a PDF for /:org/:slug and return as attachment."
  [req]
  (let [org      (or (get-in req [:params "org"]) (get-in req [:params :org]))
        slug     (or (get-in req [:params "slug"]) (get-in req [:params :slug]))
        org-safe (safe-token org "course")
        slug-safe (safe-token slug "document")]
    (if (or (str/blank? org) (str/blank? slug))
      (-> (response/response (pr-str {:ok false :error "Missing org or slug"}))
          (response/status 400)
          (response/content-type "application/edn"))
      (let [pages    (or (get-in req [:params "pages"]) (get-in req [:params :pages]))
            target-url (str (build-base-url req) "/" org "/" slug "/")
            out-file   (File/createTempFile (str "greb-export-" org-safe "-" slug-safe "-") ".pdf")
            filename   (str org-safe "-" slug-safe (when pages (str "-p" pages)) ".pdf")
            args       (cond-> ["node" "scripts/export_pdf.js" target-url (.getAbsolutePath out-file)]
                         pages (conj pages))
            result     (apply sh/sh args)]
        (if (zero? (:exit result))
          (-> (response/file-response (.getAbsolutePath out-file))
              (response/header "Content-Type" "application/pdf")
              (response/header "Content-Disposition" (str "attachment; filename=\"" filename "\""))
              (response/header "Cache-Control" "no-store"))
          (-> (response/response
                (pr-str {:ok false
                         :error "PDF export failed"
                         :details (str/trim (or (:err result) (:out result) ""))}))
              (response/status 500)
              (response/content-type "application/edn")))))))
