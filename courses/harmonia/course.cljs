(ns harmonia.course
  "Course definition for the Nueva Acrópolis Harmonia user manual."
  (:require [harmonia.content :as c]))

(def course
  {:meta  {:id          "harmonia"
           :org         "harmonia"
           :slug        "manual_harmonia"
           :title       "Harmonia — Manual de Usuario"
           :description "Manual de usuario del sistema Harmonia — gestión académica para las escuelas de filosofía de Nueva Acrópolis."
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "Nueva Acrópolis"
           :logo        nil
           :images-base nil
           :colors {:primary   "#1a3a5c"
                    :secondary "#c9a84c"
                    :accent    "#7ab648"
                    :ink       "#1c1c1e"
                    :paper     "#eae6df"
                    :page      "#fffef8"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Introducción"
          :entries (subvec c/index-entries 0 7)}
         {:label "Gestión Académica"
          :entries (subvec c/index-entries 7 13)}
         {:label "Recursos"
          :entries (subvec c/index-entries 13 16)}
         {:label "Administración"
          :entries (subvec c/index-entries 16 25)}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:hero-img "home-selector.png"
            :title    "Harmonia"
            :subtitle "Nueva Acrópolis · Gestión Académica · Manual de Usuario"}}

    ;; 2. Table of Contents
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. ¿Qué es Harmonia?
    {:template :hero-section
     :data {:id "que-es"
            :hero {:kicker "Nueva Acrópolis"
                   :title  c/intro-title
                   :subtitle c/intro-lead
                   :hero-img "home-selector.png"}
            :blocks [{:type :info-grid :icon "layers" :title "El Sistema"
                      :items c/intro-sistema}
                     {:type :highlight :icon "sparkles" :title "Puntos clave"
                      :items c/intro-blocks}]}}

    ;; 4. Arquitectura del Sistema
    {:template :hero-section
     :data {:id "arquitectura"
            :hero {:kicker "Visión Técnica"
                   :title  c/arq-title
                   :subtitle c/arq-subtitle
                   :intro c/arq-intro}
            :blocks [{:type :info-grid :icon "cpu" :title "Stack Tecnológico"
                      :items c/arq-stack-items}
                     {:type :wash-grid :icon "workflow" :title "Flujo de Datos"
                      :items c/arq-flow-items}]}}

    ;; 5. Esquema de Base de Datos
    {:template :blocks
     :data {:id "esquema-bd"
            :header {:icon "database"
                     :kicker "Visión Técnica"
                     :title  c/esquema-title}
            :blocks [{:type :info-grid :icon "table" :title "Tablas y Patrones"
                      :items c/esquema-tablas}
                     {:type :info-grid :icon "search" :title "Consultas SQL Útiles"
                      :items c/esquema-consultas}]}}

    ;; 6. Acceso y Autenticación
    {:template :hero-section
     :data {:id "acceso"
            :hero {:kicker "Seguridad"
                   :title  c/acceso-title
                   :subtitle c/acceso-subtitle}
            :blocks [{:type :info-grid :icon "log-in" :title "Cómo acceder"
                      :items c/acceso-items}
                     {:type :info-grid :icon "shield" :title "Roles del Sistema"
                      :items c/acceso-roles}]}}

    ;; 7. API REST
    {:template :blocks
     :data {:id "api"
            :header {:icon "cloud"
                     :kicker "Visión Técnica"
                     :title  c/api-title}
            :blocks [{:type :info-grid :icon "server" :title "Entidades y Autenticación"
                      :items c/api-entidades}
                     {:type :info-grid :icon "shield" :title "Endpoints Admin"
                      :items c/api-admin}
                     {:type :info-grid :icon "library" :title "Biblioteca y Archivos"
                      :items c/api-biblioteca}]}}

    ;; 8. Sedes y Salas
    {:template :hero-section
     :data {:id "sedes"
            :hero {:kicker "Gestión Académica"
                   :title  c/sedes-title
                   :subtitle c/sedes-subtitle
                   :hero-img "sedes.png"}
            :blocks [{:type :info-grid :icon "file-text" :title "Campos y Relaciones"
                      :items c/sedes-campos}
                     {:type :info-grid :icon "compass" :title "Navegación en la App"
                      :items c/sedes-nav}]}}

    ;; 9. Cátedras
    {:template :hero-section
     :data {:id "catedras"
            :hero {:kicker "Gestión Académica"
                   :title  c/catedras-title
                   :subtitle c/catedras-subtitle
                   :hero-img "catedras.png"}
            :blocks [{:type :info-grid :icon "book-open" :title "Estructura y Datos"
                      :items c/catedras-items}]}}

    ;; 10. Cursos y Asignaciones
    {:template :hero-section
     :data {:id "cursos"
            :hero {:kicker "Gestión Académica"
                   :title  c/cursos-title
                   :subtitle c/cursos-subtitle
                   :hero-img "cursos.png"}
            :blocks [{:type :info-grid :icon "file-text" :title "Campos y Relaciones"
                      :items c/cursos-campos}
                     {:type :info-grid :icon "compass" :title "Navegación en la App"
                      :items c/cursos-nav}]}}

    ;; 11. Instructores
    {:template :hero-section
     :data {:id "instructores"
            :hero {:kicker "Gestión Académica"
                   :title  c/instructores-title
                   :subtitle c/instructores-subtitle
                   :hero-img "instructores.png"}
            :blocks [{:type :info-grid :icon "graduation-cap" :title "Datos y Relaciones"
                      :items c/instructores-items}]}}

    ;; 12. Miembros
    {:template :hero-section
     :data {:id "miembros"
            :hero {:kicker "Gestión Académica"
                   :title  c/miembros-title
                   :subtitle c/miembros-subtitle
                   :hero-img "miembros.png"}
            :blocks [{:type :info-grid :icon "id-card" :title "Campos y Relaciones"
                      :items c/miembros-campos}
                     {:type :info-grid :icon "compass" :title "Navegación en la App"
                      :items c/miembros-nav}]}}

    ;; 13. Eventos y Asistencia
    {:template :hero-section
     :data {:id "eventos"
            :hero {:kicker "Seguimiento"
                   :title  c/eventos-title
                   :subtitle c/eventos-subtitle
                   :hero-img "calendario.png"}
            :blocks [{:type :info-grid :icon "calendar" :title "Campos, Asistencia y Calendario"
                      :items c/eventos-campos}]}}

    ;; 14. Biblioteca
    {:template :hero-section
     :data {:id "biblioteca"
            :hero {:kicker "Recursos"
                   :title  c/biblioteca-title
                   :subtitle c/biblioteca-subtitle
                   :hero-img "biblioteca.png"}
            :blocks [{:type :info-grid :icon "book" :title "Campos del Libro"
                      :items c/biblioteca-campos}
                     {:type :info-grid :icon "globe" :title "Funcionalidades"
                      :items c/biblioteca-funciones}]}}

    ;; 15. Contribuciones
    {:template :blocks
     :data {:id "contribuciones"
            :header {:icon "credit-card"
                     :kicker "Recursos"
                     :title  c/contrib-title}
            :blocks [{:type :info-grid :icon "file-text" :title "Datos y Relaciones"
                      :items c/contrib-items}]}}

    ;; 16. Personajes
    {:template :blocks
     :data {:id "personajes"
            :header {:icon "star"
                     :kicker "Recursos"
                     :title  c/personajes-title}
            :blocks [{:type :info-grid :icon "book-open" :title "Figuras Filosóficas"
                      :items c/personajes-items}]}}

    ;; 17. Administración y Usuarios
    {:template :blocks
     :data {:id "administracion"
            :header {:icon "settings"
                     :kicker "Administración"
                     :title  c/admin-title}
            :blocks [{:type :info-grid :icon "users" :title "Usuarios del Sistema"
                      :items c/admin-items}
                     {:type :info-grid :icon "refresh-cw" :title "Sincronización y Herramientas"
                      :items c/admin-sync}]}}

    ;; 18. Operación Multi-País
    {:template :blocks
     :data {:id "multi-pais"
            :header {:icon "globe"
                     :kicker "Administración"
                     :title  c/multipais-title}
            :blocks [{:type :info-grid :icon "globe" :title "Funcionamiento"
                      :items c/multipais-items}]}}

    ;; 19. OmniREPL — Paleta de Comandos
    {:template :hero-section
     :data {:id "omnirepl"
            :hero {:kicker "Desarrollo"
                   :title  c/omnirepl-title
                   :subtitle c/omnirepl-subtitle
                   :intro c/omnirepl-intro}
            :blocks [{:type :info-grid :icon "keyboard" :title "Uso Básico"
                      :items c/omnirepl-items}
                     {:type :info-grid :icon "terminal" :title "Comandos Disponibles"
                      :items c/omnirepl-comandos}]}}

    ;; 20. OmniREPL — Ejemplos de Código
    {:template :blocks
     :data {:id "omnirepl-code"
            :header {:icon "braces"
                     :kicker "OmniREPL"
                     :title  "OmniREPL — Ejemplos de Código"}
            :blocks [{:type :code-block :icon "compass" :title "Navegación"
                      :caption "Ctrl+G → escribir comando → Enter"
                      :lines c/omnirepl-code-nav}
                     {:type :code-block :icon "zap" :title "Acciones y Creación"
                      :caption "Crear entidades desde la paleta de comandos"
                      :lines c/omnirepl-code-acciones}
                     {:type :code-block :icon "search" :title "Consultas"
                      :caption "Evaluar expresiones Clojure en tiempo real"
                      :lines c/omnirepl-code-consultas}]}}

    ;; 21. REPL — Código en Vivo (Frontend)
    {:template :hero-section
     :data {:id "repl"
            :hero {:kicker "Desarrollo"
                   :title  c/repl-title
                   :subtitle c/repl-subtitle
                   :intro c/repl-intro}
            :blocks [{:type :info-grid :icon "atom" :title "Estado Reactivo"
                      :items c/repl-estado-items}
                     {:type :info-grid :icon "terminal" :title "Ejemplos — Frontend REPL"
                      :items c/repl-ejemplos-items}]}}

    ;; 22. REPL — Ejemplos de Código
    {:template :blocks
     :data {:id "repl-code"
            :header {:icon "code-2"
                     :kicker "REPL Frontend"
                     :title  "REPL — Ejemplos de Código"}
            :blocks [{:type :code-block :icon "atom" :title "Estado"
                      :caption "Leer el estado de la aplicación desde el REPL del navegador"
                      :lines c/repl-code-estado}
                     {:type :code-block :icon "pencil" :title "Mutaciones"
                      :caption "Modificar datos — la UI se actualiza al instante"
                      :lines c/repl-code-mutaciones}]}}

    ;; 23. REPL Backend y CLI
    {:template :blocks
     :data {:id "repl-backend"
            :header {:icon "server"
                     :kicker "Desarrollo"
                     :title  c/repl-backend-title}
            :intro c/repl-backend-intro
            :blocks [{:type :info-grid :icon "database" :title "Helpers y Consultas"
                      :items c/repl-backend-helpers}
                     {:type :info-grid :icon "terminal" :title "Scripts CLI"
                      :items c/repl-cli-items}]}}

    ;; 24. Backend — Ejemplos de Código
    {:template :blocks
     :data {:id "repl-backend-code"
            :header {:icon "database"
                     :kicker "REPL Backend"
                     :title  "Backend — Ejemplos de Código"}
            :blocks [{:type :code-block :icon "search" :title "Helpers"
                      :caption "Funciones de consulta disponibles en el REPL backend"
                      :lines c/repl-backend-code-helpers}
                     {:type :code-block :icon "database" :title "SQL Directo"
                      :caption "Consultas SQL contra PostgreSQL"
                      :lines c/repl-backend-code-sql}
                     {:type :code-block :icon "upload" :title "Upsert y CLI"
                      :caption "Crear entidades y scripts de administración"
                      :lines c/repl-backend-code-upsert}]}}

    ;; 25. Créditos
    {:template :credits
     :data {:title c/credits-title
            :by    c/credits-by
            :orgs  c/credits-orgs
            :legal c/credits-legal}}]})
