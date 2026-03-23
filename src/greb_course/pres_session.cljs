(ns greb-course.pres-session
  "Presentation session persistence: save/restore slide index, zoom, text-scale."
  (:require [greb-course.state :as state]))

(defn save-session! [idx]
  "Persist presentation slide index and zoom to sessionStorage."
  (.setItem js/sessionStorage "greb-pres-idx" (str idx))
  (when-let [z (:zoom @state/pres-state)]
    (.setItem js/sessionStorage "greb-pres-zoom" (str z)))
  (when-let [ts (:text-scale @state/pres-state)]
    (.setItem js/sessionStorage "greb-pres-tscale" (str ts))))

(defn clear-session! []
  (.removeItem js/sessionStorage "greb-pres-idx")
  (.removeItem js/sessionStorage "greb-pres-zoom")
  (.removeItem js/sessionStorage "greb-pres-tscale"))

(defn restore-session []
  "Returns saved slide index or nil."
  (when-let [v (.getItem js/sessionStorage "greb-pres-idx")]
    (js/parseInt v 10)))

(defn restore-zoom []
  "Returns saved zoom level or nil."
  (when-let [v (.getItem js/sessionStorage "greb-pres-zoom")]
    (js/parseFloat v)))

(defn restore-text-scale []
  (when-let [v (.getItem js/sessionStorage "greb-pres-tscale")]
    (js/parseFloat v)))
