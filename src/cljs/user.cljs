(ns cljs.user
  "Default REPL namespace — helpers available at cljs.user=> without extra requires."
  (:require [greb-course.editor :as editor]
            [greb-course.repl :as repl]))

(defn ask!
  "Send prompt to Claude via /api/editor-config; prints the reply (or error) when done.
   Usage: (ask! \"hello world\") or (ask \"hello world\")"
  [prompt]
  (-> (editor/anthropic-quick-ask! prompt)
      (.then (fn [r] (println r)))
      (.catch (fn [e] (println "Error:" (.-message e)))))
  nil)

(def ask ask!)

;; Pass-through REPL helpers (same as greb-course.repl)
(def go! repl/go!)
(def edit! repl/edit!)
(def list-courses repl/list-courses)
(def list-pages repl/list-pages)
