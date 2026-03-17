(ns scratch
  (:require [greb-course.repl :as r]))

(comment
  ;; Evaluate any form below with Ctrl+Enter (cursor on the form)

  (r/list-courses)
  (r/list-pages)

  ;; Navigate
  (r/go! "portada")
  (r/go! "riesgo-quimico")
  (r/go! 7)

  ;; Rename
  (r/rename-page! 4 "Nuevo Título")

  ;; Add page
  (r/add-blocks-page! "nueva-pagina"
    [{:type :info-grid :icon "star" :title "Mi Sección"
      :items [{:title "Punto 1" :text "Descripción"}]}])

  ;; Remove / move
  (r/remove-page! 12)
  (r/move-page! 3 10)

  ,)
