(ns greb-course.server-proxy
  "LLM proxy logic (Anthropic & OpenRouter) and .env loading."
  (:require [ring.util.response :as response]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.net URI]
           [java.net.http HttpClient HttpRequest HttpRequest$BodyPublishers HttpResponse$BodyHandlers]
           [java.time Duration]))

;; ---------------------------------------------------------------------------
;; .env helpers
;; ---------------------------------------------------------------------------

(defn env-lookup
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

(defn load-dotenv!
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

;; ---------------------------------------------------------------------------
;; API key lookups
;; ---------------------------------------------------------------------------

(defn- openrouter-api-key-env []
  (or (env-lookup "OPENROUTER_API")
      (env-lookup "OPENROUTER_API_KEY")))

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

;; ---------------------------------------------------------------------------
;; Editor config & proxy handlers
;; ---------------------------------------------------------------------------

(defn editor-config-json []
  "JSON for the browser editor: where to POST and whether the server adds the API key.
  Prefers OpenRouter when OPENROUTER_API is set; falls back to Anthropic."
  (let [or-key (openrouter-api-key-env)]
    (if (not-empty or-key)
      ;; OpenRouter mode — server proxies, browser never sees key
      (str "{\"messagesUrl\":\"/api/openrouter/chat\""
           ",\"usesServerKey\":true"
           ",\"provider\":\"openrouter\"}")
      ;; Anthropic mode (existing behaviour)
      (let [custom (env-lookup "GREB_ANTHROPIC_MESSAGES_URL")
            up (anthropic-upstream-messages-url)
            use-proxy? (anthropic-proxy-can-run?)
            [url uses-srv?] (cond
                              (some? (not-empty (or custom ""))) [(str/trim custom) false]
                              use-proxy? ["/api/anthropic/messages" true]
                              :else [up false])]
        (str "{\"messagesUrl\":" (pr-str url)
             ",\"usesServerKey\":" (if uses-srv? "true" "false")
             ",\"provider\":\"anthropic\"}")))))

(defn anthropic-proxy-handler [req]
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

(defn openrouter-proxy-handler [req]
  (let [api-key (openrouter-api-key-env)]
    (if (str/blank? api-key)
      (-> (response/response
            "{\"error\":\"Set OPENROUTER_API in .env for OpenRouter proxy.\"}")
          (response/status 503)
          (response/content-type "application/json"))
      (try
        (let [body (if-let [b (:body req)] (slurp b) "")
              client (-> (HttpClient/newBuilder)
                         (.connectTimeout (Duration/ofSeconds 45))
                         .build)
              hreq (.build
                     (-> (HttpRequest/newBuilder)
                         (.uri (URI/create "https://openrouter.ai/api/v1/chat/completions"))
                         (.timeout (Duration/ofMinutes 4))
                         (.header "Content-Type" "application/json")
                         (.header "Authorization" (str "Bearer " api-key))
                         (.header "HTTP-Referer" "https://greb-course.local")
                         (.header "X-OpenRouter-Title" "GREB Course Editor")
                         (.POST (HttpRequest$BodyPublishers/ofString body))))
              resp (.send client hreq (HttpResponse$BodyHandlers/ofString))]
          (-> (response/response (.body resp))
              (response/status (.statusCode resp))
              (response/content-type "application/json")))
        (catch Exception e
          (-> (response/response (str "{\"error\":" (pr-str (.getMessage e)) "}"))
              (response/status 502)
              (response/content-type "application/json")))))))

;; ---------------------------------------------------------------------------
;; Boot-time diagnostics (called from ns init in server.clj)
;; ---------------------------------------------------------------------------

;; ---------------------------------------------------------------------------
;; Kie AI image generation proxy
;; ---------------------------------------------------------------------------

(defn- kie-api-key [] (env-lookup "KIE_API"))

(defn kie-generate-handler [req]
  (let [api-key (kie-api-key)]
    (if (str/blank? api-key)
      (-> (response/response "{\"error\":\"Set KIE_API in .env\"}")
          (response/status 503) (response/content-type "application/json"))
      (try
        (let [body (if-let [b (:body req)] (slurp b) "{}")
              client (-> (HttpClient/newBuilder) (.connectTimeout (Duration/ofSeconds 30)) .build)
              hreq (.build
                     (-> (HttpRequest/newBuilder)
                         (.uri (URI/create "https://kieai.erweima.ai/api/v1/gpt4o-image/generate"))
                         (.timeout (Duration/ofMinutes 2))
                         (.header "Content-Type" "application/json")
                         (.header "Authorization" (str "Bearer " api-key))
                         (.POST (HttpRequest$BodyPublishers/ofString body))))
              resp (.send client hreq (HttpResponse$BodyHandlers/ofString))]
          (-> (response/response (.body resp))
              (response/status (.statusCode resp))
              (response/content-type "application/json")))
        (catch Exception e
          (-> (response/response (str "{\"error\":" (pr-str (.getMessage e)) "}"))
              (response/status 502) (response/content-type "application/json")))))))

(defn kie-task-status-handler [task-id]
  (let [api-key (kie-api-key)]
    (if (str/blank? api-key)
      (-> (response/response "{\"error\":\"Set KIE_API in .env\"}")
          (response/status 503) (response/content-type "application/json"))
      (try
        (let [client (-> (HttpClient/newBuilder) (.connectTimeout (Duration/ofSeconds 15)) .build)
              url (str "https://kieai.erweima.ai/api/v1/gpt4o-image/record-info?taskId=" task-id)
              hreq (.build
                     (-> (HttpRequest/newBuilder)
                         (.uri (URI/create url))
                         (.timeout (Duration/ofSeconds 30))
                         (.header "Authorization" (str "Bearer " api-key))
                         (.GET)))
              resp (.send client hreq (HttpResponse$BodyHandlers/ofString))]
          (-> (response/response (.body resp))
              (response/status (.statusCode resp))
              (response/content-type "application/json")))
        (catch Exception e
          (-> (response/response (str "{\"error\":" (pr-str (.getMessage e)) "}"))
              (response/status 502) (response/content-type "application/json")))))))

(defn kie-save-image-handler [req]
  (try
    (let [body   (slurp (:body req))
          params (read-string body)
          url    (:url params)
          org    (:org params)
          fname  (or (:filename params) (str "gen-" (System/currentTimeMillis) ".png"))
          dir    (java.io.File. (str "courses/" org "/images"))
          file   (java.io.File. dir fname)]
      (when-not (.exists dir) (.mkdirs dir))
      (with-open [in (.openStream (java.net.URL. url))
                  out (io/output-stream file)]
        (io/copy in out))
      (-> (response/response (pr-str {:ok true :path (.getPath file) :filename fname}))
          (response/content-type "application/edn")))
    (catch Exception e
      (-> (response/response (pr-str {:ok false :error (.getMessage e)}))
          (response/status 500) (response/content-type "application/edn")))))

(defn print-boot-diagnostics! []
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
      (println "[anthropic] proxy upstream →" up))))
