(ns greb-course.auth
  (:require [clojure.string :as str]
            [greb-course.server-proxy :as proxy])
  (:import [java.util UUID]))

;; ── Session store ─────────────────────────────────────────────────────────────

(defonce sessions (atom {}))

(def ^:private session-ttl-ms (* 7 24 60 60 1000)) ; 7 days

(defn new-session! []
  (let [token (str (UUID/randomUUID))]
    (swap! sessions assoc token {:created-at (System/currentTimeMillis)})
    token))

(defn valid-session? [token]
  (when token
    (when-let [{:keys [created-at]} (get @sessions token)]
      (< (- (System/currentTimeMillis) created-at) session-ttl-ms))))

(defn invalidate-session! [token]
  (swap! sessions dissoc token))

;; ── Credentials ───────────────────────────────────────────────────────────────

(defn auth-enabled? []
  (boolean (proxy/env-lookup "GREB_AUTH_PASS")))

(defn check-credentials [user pass]
  (let [expected-user (or (proxy/env-lookup "GREB_AUTH_USER") "admin")
        expected-pass (proxy/env-lookup "GREB_AUTH_PASS")]
    (and expected-pass
         (= user expected-user)
         (= pass expected-pass))))

;; ── Cookie helpers ─────────────────────────────────────────────────────────────

(defn get-session-token [req]
  (when-let [cookies (get-in req [:headers "cookie"])]
    (second (re-find #"gc_session=([^;]+)" cookies))))

(defn set-session-cookie [resp token]
  (assoc-in resp [:headers "Set-Cookie"]
            (str "gc_session=" token
                 "; Path=/; HttpOnly; SameSite=Lax; Max-Age=" (* 7 24 3600))))

(defn clear-session-cookie [resp]
  (assoc-in resp [:headers "Set-Cookie"]
            "gc_session=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0"))

;; ── Login page ────────────────────────────────────────────────────────────────

(def ^:private login-html-template
  "<!DOCTYPE html>
<html lang='es'>
<head>
  <meta charset='utf-8'>
  <meta name='viewport' content='width=device-width,initial-scale=1'>
  <title>Acceso</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: system-ui, -apple-system, sans-serif;
           background: #0f172a;
           display: flex; align-items: center; justify-content: center;
           min-height: 100vh; }
    .card { background: #1e293b; border-radius: 12px; padding: 2.5rem 2rem;
            width: 100%; max-width: 360px;
            box-shadow: 0 25px 50px rgba(0,0,0,.5); }
    h1 { color: #f1f5f9; font-size: 1.4rem; font-weight: 700;
         margin-bottom: 1.75rem; text-align: center; letter-spacing: -.02em; }
    label { display: block; color: #94a3b8; font-size: .8rem;
            font-weight: 500; text-transform: uppercase; letter-spacing: .06em;
            margin-bottom: .4rem; margin-top: 1.25rem; }
    input { width: 100%; padding: .65rem .9rem; border-radius: 8px;
            border: 1px solid #334155; background: #0f172a; color: #f1f5f9;
            font-size: 1rem; outline: none; transition: border-color .15s; }
    input:focus { border-color: #38bdf8; }
    button { margin-top: 1.75rem; width: 100%; padding: .75rem;
             background: #0891b2; color: #fff; border: none; border-radius: 8px;
             font-size: 1rem; font-weight: 600; cursor: pointer;
             transition: background .15s; }
    button:hover { background: #06b6d4; }
    .err { color: #f87171; font-size: .85rem; margin-top: 1rem; text-align: center; }
  </style>
</head>
<body>
  <div class='card'>
    <h1>greb-course</h1>
    <form method='POST' action='/login'>
      <label>Usuario</label>
      <input name='username' type='text' autocomplete='username' autofocus required>
      <label>Contraseña</label>
      <input name='password' type='password' autocomplete='current-password' required>
      <button type='submit'>Entrar</button>
      {{ERROR}}
    </form>
  </div>
</body>
</html>")

(defn login-page
  ([] (login-page nil))
  ([error]
   {:status 200
    :headers {"Content-Type" "text/html; charset=utf-8"}
    :body (str/replace login-html-template "{{ERROR}}"
                       (if error
                         (str "<p class='err'>" error "</p>")
                         ""))}))

;; ── Auth middleware ────────────────────────────────────────────────────────────

(defn- public-path? [uri method]
  (or
   ;; Auth endpoints themselves
   (= uri "/login")
   (= uri "/logout")
   ;; Static assets
   (str/starts-with? uri "/js/")
   (str/starts-with? uri "/css/")
   (str/starts-with? uri "/fonts/")
   (str/starts-with? uri "/favicon/")
   (str/starts-with? uri "/images/")
   (str/starts-with? uri "/sounds/")
   ;; Embed viewer
   (str/starts-with? uri "/embed/")
   ;; Course viewer: /:org/:slug/ and all sub-paths (2 path segments)
   ;; These are the shareable document links
   (boolean (re-matches #"/[^/]+/[^/]+/.*" uri))
   (boolean (re-matches #"/[^/]+/[^/]+/" uri))
   ;; Read-only patches API — needed by course viewer to load saved edits
   (and (= method :get) (= uri "/api/patches"))))

(defn wrap-auth [handler]
  (fn [req]
    (if-not (auth-enabled?)
      ;; Auth disabled (no GREB_AUTH_PASS set) — pass everything through
      (handler req)
      (if (public-path? (:uri req) (:request-method req))
        (handler req)
        (let [token (get-session-token req)]
          (if (valid-session? token)
            (handler req)
            ;; API routes → JSON 401; browser routes → redirect to /login
            (if (str/starts-with? (:uri req) "/api/")
              {:status 401
               :headers {"Content-Type" "application/json"}
               :body "{\"error\":\"unauthorized\"}"}
              {:status 302
               :headers {"Location" "/login"}
               :body ""})))))))
