(ns greb-course.i18n
  "Lightweight i18n layer for UI labels.")

(def base-strings
  {:es {:print "Imprimir / PDF" :index "Índice" :close "Cerrar"
        :page-of "{current} / {total}" :toc-title "Índice"
        :prev "Anterior" :next "Siguiente" :glossary "Glosario"
        :ready "Listo para abrir" :present "Presentar"
        :fullscreen "Pantalla completa" :exit-fullscreen "Salir de pantalla completa"
        :select-section "Seleccionar sección"
        :maximize-slide "Maximizar slide" :restore-slide "Restaurar tamaño"
        :section-mode "Modo secciones" :exit-section-mode "Salir modo secciones"
        :zoom-in "Acercar" :zoom-out "Alejar" :zoom-reset "Restablecer zoom"}
   :en {:print "Print / PDF" :index "Index" :close "Close"
        :page-of "{current} / {total}" :toc-title "Index"
        :prev "Previous" :next "Next" :glossary "Glossary"
        :ready "Ready to open" :present "Present"
        :fullscreen "Full screen" :exit-fullscreen "Exit full screen"
        :select-section "Select section"
        :maximize-slide "Maximize slide" :restore-slide "Restore size"
        :section-mode "Section mode" :exit-section-mode "Exit section mode"
        :zoom-in "Zoom in" :zoom-out "Zoom out" :zoom-reset "Reset zoom"}})

(defonce ^:private current-strings (atom (:en base-strings)))

(defn init! [lang & [overrides]]
  (reset! current-strings
    (merge (get base-strings lang (:en base-strings))
           overrides)))

(defn t [k]
  (get @current-strings k (name k)))
