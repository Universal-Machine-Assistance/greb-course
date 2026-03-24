(ns cljs.user
  "Default REPL namespace — helpers available at cljs.user=> without extra requires."
  (:require [greb-course.editor :as editor]
            [greb-course.repl :as repl]))

;; ── Quick LLM ──
(defn ask! [prompt]
  (-> (editor/anthropic-quick-ask! prompt)
      (.then #(println %))
      (.catch #(println "Error:" (.-message %))))
  nil)
(def ask ask!)

;; ── Re-exports from repl ──
(def go! repl/go!)
(def edit! repl/edit!)
(def list-courses repl/list-courses)
(def list-pages repl/list-pages)
(def open-editor! repl/open-editor!)
(def close-editor! repl/close-editor!)
(def save! repl/save!)
(def edit-llm! repl/edit-llm!)
(def vim! repl/vim!)
(def present! repl/present!)
(def doc-mode! repl/doc-mode!)
(def toggle-mode! repl/toggle-mode!)
(def fullscreen! repl/fullscreen!)
(def print-dialog repl/print-dialog)
(def pdf-download repl/pdf-download)
(def pdf-downlaod repl/pdf-downlaod)
(def omni! repl/omni!)
(def cmd! repl/cmd!)
(def current-page repl/current-page)
(def editor-page! repl/editor-page!)
(def move-block! repl/move-block!)
(def split-page! repl/split-page!)
(def imagine! repl/imagine!)
(def save-image! repl/save-image!)
(def generate-cover! repl/generate-cover!)
(def help repl/help)
