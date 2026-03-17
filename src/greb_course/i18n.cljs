(ns greb-course.i18n
  "Lightweight i18n layer for UI labels.")

(def base-strings
  {:es {:print "Imprimir / PDF" :index "Índice" :close "Cerrar"
        :page-of "{current} / {total}" :toc-title "Índice"
        :prev "Anterior" :next "Siguiente" :glossary "Glosario"
        :ready "Listo para abrir"}
   :en {:print "Print / PDF" :index "Index" :close "Close"
        :page-of "{current} / {total}" :toc-title "Index"
        :prev "Previous" :next "Next" :glossary "Glossary"
        :ready "Ready to open"}})

(defonce ^:private current-strings (atom (:en base-strings)))

(defn init! [lang & [overrides]]
  (reset! current-strings
    (merge (get base-strings lang (:en base-strings))
           overrides)))

(defn t [k]
  (get @current-strings k (name k)))
