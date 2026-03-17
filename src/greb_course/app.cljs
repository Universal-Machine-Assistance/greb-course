(ns greb-course.app
  "Wiring file — registers all courses, boots the right one based on URL."
  (:require [greb-course.core :as core]
            [valentino.course :as valentino]
            [romerlabs.course :as romerlabs]))

;; All available courses — add new ones here
(def courses
  [valentino/course
   romerlabs/course])

(defn init [] (core/init! courses))

(defn after-load [] (core/reload! courses))
