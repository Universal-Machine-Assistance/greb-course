(ns greb-course.app
  "Wiring file — registers all courses, boots the right one based on URL."
  (:require [greb-course.core :as core]
            [valentino.course :as valentino]
            [romerlabs.course :as romerlabs]
            [grebdocs.course :as grebdocs]
            [greb.course :as greb]
            [harmonia.course :as harmonia]
            [propuesta-web.course :as propuesta-web]
            [rivia.course :as rivia]
            [grebnue.course :as grebnue]))

;; All available courses — add new ones here
(def courses
  [valentino/course
   romerlabs/course
   grebdocs/course
   greb/course
   harmonia/course
   propuesta-web/course
   rivia/course
   grebnue/course])

(defn init [] (core/init! courses))

(defn after-load [] (core/reload! courses))
