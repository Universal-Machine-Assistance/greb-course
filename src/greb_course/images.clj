(ns greb-course.images
  "Image management: naming, optimization (ImageMagick), manifest tracking."
  (:require [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.edn :as edn]
            [clojure.string :as str])
  (:import [java.io File]
           [java.time Instant]))

;; ---------------------------------------------------------------------------
;; Filename cleaning
;; ---------------------------------------------------------------------------

(def ^:private accent-map
  {\á \a \é \e \í \i \ó \o \ú \u \ñ \n
   \Á \A \É \E \Í \I \Ó \O \Ú \U \Ñ \N
   \ü \u \Ü \U})

(defn- transliterate [s]
  (apply str (map #(get accent-map % %) s)))

(defn- strip-wp-hash
  "Remove WordPress/Squarespace CDN hashes like -r8e46cntsmpecvka5d1o..."
  [s]
  (str/replace s #"-[a-z0-9]{25,}$" ""))

(defn- strip-numeric-prefix
  "Remove leading numeric prefixes: '3-Cajita...' -> 'Cajita...', '0000_hielo' -> 'hielo'"
  [s]
  (-> s
      (str/replace #"^\d+-" "")
      (str/replace #"^\d{3,4}_" "")))

(defn- strip-screenshot-noise
  "Simplify 'Captura-de-Pantalla-...' names."
  [s]
  (if (str/starts-with? (str/lower-case s) "captura-de-pantalla")
    (-> s
        (str/replace #"(?i)captura-de-pantalla-?" "captura-")
        (str/replace #"-a\.-m\.?" "-am")
        (str/replace #"-p\.-m\.?" "-pm")
        (str/replace #"-a-las-" "-"))
    s))

(defn- normalize-separators [s]
  (-> s
      (str/replace #"[_ ]+" "-")
      (str/replace #"-{2,}" "-")
      (str/replace #"^-|-$" "")))

(defn clean-filename
  "Transform a messy filename into a clean kebab-case name.
   Returns [clean-stem extension]."
  [filename]
  (let [dot-idx  (str/last-index-of filename ".")
        [stem ext] (if (and dot-idx (pos? dot-idx))
                     [(subs filename 0 dot-idx) (str/lower-case (subs filename (inc dot-idx)))]
                     [filename nil])
        clean (-> stem
                  strip-screenshot-noise
                  strip-wp-hash
                  strip-numeric-prefix
                  transliterate
                  str/lower-case
                  normalize-separators)]
    [clean ext]))

(defn deduplicate-name
  "Given a desired name and a set of taken names, append -2, -3 etc. if needed."
  [name taken-set]
  (if-not (contains? taken-set name)
    name
    (loop [n 2]
      (let [candidate (str name "-" n)]
        (if-not (contains? taken-set candidate)
          candidate
          (recur (inc n)))))))

;; ---------------------------------------------------------------------------
;; Manifest I/O
;; ---------------------------------------------------------------------------

(defn manifest-path [images-root]
  (File. ^String images-root "manifest.edn"))

(defn load-manifest [images-root]
  (let [f (manifest-path images-root)]
    (if (.exists f)
      (edn/read-string (slurp f))
      {:version 1 :entries []})))

(defn save-manifest! [images-root manifest]
  (let [f (manifest-path images-root)]
    (spit f (pr-str (assoc manifest :updated (str (Instant/now)))))))

;; ---------------------------------------------------------------------------
;; Lookup helpers (build index maps from manifest)
;; ---------------------------------------------------------------------------

(defn build-index
  "Build lookup maps keyed by [subdir filename] for both original and optimized names."
  [manifest]
  (let [entries (:entries manifest)]
    {:by-original  (into {} (map (fn [e] [[(:subdir e) (:original e)] e]) entries))
     :by-optimized (into {} (keep (fn [e]
                                    (when (:optimized e)
                                      [[(:subdir e) (:optimized e)] e]))
                                  entries))
     :by-clean     (into {} (map (fn [e] [[(:subdir e) (:clean e)] e]) entries))}))

;; ---------------------------------------------------------------------------
;; Scanning & manifest building
;; ---------------------------------------------------------------------------

(defn- list-files-recursive [^File dir]
  (when (.isDirectory dir)
    (->> (.listFiles dir)
         (filter #(.isFile ^File %))
         (map #(.getName ^File %)))))

(defn build-manifest-entries
  "Scan a flat directory of images and produce manifest entries with clean names."
  [^File dir subdir]
  (let [files (sort (list-files-recursive dir))]
    (loop [remaining files
           taken     #{}
           entries   []]
      (if-let [original (first remaining)]
        (let [[clean-stem ext] (clean-filename original)
              clean-full (if ext (str clean-stem "." ext) clean-stem)
              deduped    (deduplicate-name clean-full taken)
              ;; optimized will be WebP (set during optimization)
              entry {:original  original
                     :clean     deduped
                     :subdir    subdir
                     :optimized nil
                     :status    :pending}]
          (recur (rest remaining)
                 (conj taken deduped)
                 (conj entries entry)))
        entries))))

;; ---------------------------------------------------------------------------
;; ImageMagick optimization
;; ---------------------------------------------------------------------------

(def ^:private max-dimension 1600)
(def ^:private webp-quality 82)

(defn- magick-identify
  "Get image dimensions via ImageMagick identify."
  [^String path]
  (let [{:keys [out exit]} (sh/sh "identify" "-format" "%wx%h" path)]
    (when (zero? exit)
      (let [[w h] (str/split (str/trim out) #"x")]
        {:width (Integer/parseInt w) :height (Integer/parseInt h)}))))

(defn- target-ext
  "Determine output extension. Everything becomes WebP."
  [original-ext]
  "webp")

(defn optimize-image!
  "Optimize a single image using ImageMagick. Returns {:optimized-path ... :size ...} or nil."
  [^String src-path ^String dest-path]
  ;; Use [0] suffix to take first frame of multi-layer TIF/PSD files
  (let [src-arg  (str src-path "[0]")
        {:keys [exit err]} (sh/sh "convert" src-arg
                                  "-resize" (str max-dimension "x" max-dimension ">")
                                  "-strip"
                                  "-quality" (str webp-quality)
                                  dest-path)]
    (if (zero? exit)
      (let [f (File. dest-path)]
        {:optimized-path dest-path
         :size           (.length f)})
      (do (println "ImageMagick error for" src-path ":" err) nil))))

(defn process-entry!
  "Process one manifest entry: copy original to originals/, optimize to optimized/."
  [^String images-root entry]
  (let [subdir     (:subdir entry)
        orig-name  (:original entry)
        [clean-stem _ext] (clean-filename orig-name)
        out-ext    (target-ext _ext)
        opt-name   (str clean-stem "." out-ext)
        ;; Source: currently at images-root/subdir-flat (for root, just images-root)
        src-file   (if (= subdir "root")
                     (File. images-root orig-name)
                     (File. (str images-root "/" subdir) orig-name))
        ;; Originals backup
        orig-dir   (File. images-root (str "originals/" subdir))
        orig-dest  (File. orig-dir orig-name)
        ;; Optimized output
        opt-dir    (File. images-root (str "optimized/" subdir))
        opt-dest   (File. opt-dir opt-name)]
    (when (.exists src-file)
      ;; Ensure dirs
      (.mkdirs orig-dir)
      (.mkdirs opt-dir)
      ;; Copy original (preserve)
      (when-not (.exists orig-dest)
        (io/copy src-file orig-dest))
      ;; Optimize
      (let [result (optimize-image! (.getPath src-file) (.getPath opt-dest))]
        (if result
          (let [orig-size (.length src-file)]
            (merge entry
                   {:optimized      opt-name
                    :format         out-ext
                    :original-size  orig-size
                    :optimized-size (:size result)
                    :status         :done}))
          (assoc entry :status :error))))))

(defn process-all!
  "Process all pending entries in the manifest. Returns updated manifest."
  [^String images-root manifest]
  (let [entries (:entries manifest)
        updated (mapv (fn [entry]
                        (if (= :pending (:status entry))
                          (do (println "Optimizing:" (:original entry) "(" (:subdir entry) ")")
                              (or (process-entry! images-root entry)
                                  entry))
                          entry))
                      entries)]
    (assoc manifest :entries updated)))

;; ---------------------------------------------------------------------------
;; Migration: build manifest from existing flat volume structure
;; ---------------------------------------------------------------------------

(defn init-manifest!
  "Scan the volume, build manifest, return it. Does NOT optimize yet."
  [^String images-root]
  (let [root-dir     (File. images-root)
        ;; Root images (skip subdirs like originals/, optimized/, courses/)
        root-files   (when (.isDirectory root-dir)
                       (->> (.listFiles root-dir)
                            (filter #(and (.isFile ^File %)
                                         (not= (.getName ^File %) "manifest.edn")))
                            (map #(.getName ^File %))
                            sort))
        ;; Course subdirs
        courses-dir  (File. images-root "courses")
        course-subdirs (when (.isDirectory courses-dir)
                         (->> (.listFiles courses-dir)
                              (filter #(.isDirectory ^File %))
                              (map #(.getName ^File %))))
        root-entries (build-manifest-entries root-dir "root")
        course-entries (mapcat (fn [org]
                                 (build-manifest-entries
                                  (File. courses-dir org)
                                  (str "courses/" org)))
                               (or course-subdirs []))
        manifest {:version 1
                  :entries (vec (concat root-entries course-entries))}]
    (save-manifest! images-root manifest)
    (println "Manifest created:" (count (:entries manifest)) "entries")
    manifest))
