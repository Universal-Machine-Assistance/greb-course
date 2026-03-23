(ns rivia.course
  "Course definition for the Rivia LIMS technical documentation."
  (:require [rivia.content :as c]))

(def course
  {:meta  {:id          "rivia"
           :org         "upgrade"
           :slug        "rivia_lims"
           :title       "Rivia LIMS — Documentación Técnica"
           :description "Documentación técnica del sistema LIMS multi-tenant para gestión de informes de análisis de laboratorio."
           :category    "Manuales de Software"
           :tags        ["LIMS" "laboratorio" "GraphQL" "Next.js" "PostgreSQL"]
           :style       {:illustration "clean technical illustration, laboratory equipment mixed with code symbols, blue and teal color palette, isometric perspective, modern flat design, test tubes connected to database icons, no text"}
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "Rivia LIMS"
           :logo        nil
           :images-base nil
           :colors {:primary   "#0c4a6e"
                    :secondary "#0891b2"
                    :accent    "#06b6d4"
                    :ink       "#0f172a"
                    :paper     "#ecfeff"
                    :page      "#ffffff"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Visión General"
          :entries (subvec c/index-entries 0 6)}
         {:label "Setup"
          :entries (subvec c/index-entries 6 10)}
         {:label "Base de Datos"
          :entries (subvec c/index-entries 10 14)}
         {:label "Funcionalidades"
          :entries (subvec c/index-entries 14 16)}
         {:label "API"
          :entries (subvec c/index-entries 16 20)}
         {:label "Seguridad"
          :entries (subvec c/index-entries 20 22)}
         {:label "Reportes y Despliegue"
          :entries (subvec c/index-entries 22 26)}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:title    "Rivia LIMS"
            :subtitle "Sistema de Gestión de Laboratorio · Documentación Técnica"}}

    ;; 2. Contenido (cards)
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Índice (detallado)
    {:template :index
     :data {:title   "Índice"
            :entries c/index-entries
            :groups  [{:label "Visión General"
                       :items (subvec c/index-entries 3 6)}
                      {:label "Setup y Configuración"
                       :items (subvec c/index-entries 6 10)}
                      {:label "Base de Datos"
                       :items (subvec c/index-entries 10 14)}
                      {:label "Sistema y API"
                       :items (subvec c/index-entries 14 20)}
                      {:label "Seguridad, Reportes y Deploy"
                       :items (subvec c/index-entries 20 25)}]}}

    ;; 4. Introducción — drop cap
    {:template :blocks
     :data {:id "introduccion"
            :header {:icon "book-open" :kicker "LIMS" :title "Introducción"}
            :intro c/intro-dropcap
            :blocks [{:type :callout :icon "building" :style :accent
                      :title "Multi-tenant Nativo"
                      :text "Múltiples organizaciones utilizan la misma instancia de forma aislada — cada una con sus propios datos, usuarios, roles y configuraciones. El aislamiento se implementa a nivel de base de datos."}]}}

    ;; 5. Screenshot: Login
    {:template :full-image
     :data {:id "img-login"
            :img "login.png"
            :screenshot? true
            :kicker "Autenticación"
            :title "Pantalla de Login"
            :subtitle "Acceso con usuario y contraseña. Branding personalizado por tenant."}}

    ;; 6. Stack Tecnológico
    {:template :hero-section
     :data {:id "stack"
            :hero {:kicker "Tecnología"
                   :title  c/stack-title
                   :subtitle c/stack-subtitle}
            :blocks [{:type :info-grid :icon "monitor" :title "Frontend"
                      :items c/stack-frontend}
                     {:type :info-grid :icon "server" :title "Backend + Base de Datos"
                      :items c/stack-backend}]}}

    ;; 7. Arquitectura
    {:template :hero-section
     :data {:id "arquitectura"
            :hero {:kicker "Diseño"
                   :title  c/arq-title
                   :subtitle c/arq-subtitle}
            :blocks [{:type :text-block :content c/arq-text}
                     {:type :stat-grid :items c/arq-stats}
                     {:type :steps :items c/arq-flow-items}]}}

    ;; 8. Screenshot: Dashboard
    {:template :full-image
     :data {:id "img-dashboard"
            :img "dashboard.png"
            :screenshot? true
            :kicker "Dashboard"
            :title "Panel de Control"
            :subtitle "Métricas de conformidad, tendencias mensuales y distribución de estados por período."}}

    ;; 9. Instalación
    {:template :blocks
     :data {:id "instalacion"
            :header {:icon "download" :kicker "Setup" :title c/install-title}
            :blocks [{:type :steps :items c/install-steps}
                     {:type :callout :icon "box" :style :accent
                      :title "Docker"
                      :text c/install-docker-text}]}}

    ;; 10. Scripts
    {:template :blocks
     :data {:id "scripts"
            :header {:icon "terminal" :kicker "Setup" :title "Scripts Principales"}
            :blocks [{:type :info-grid :icon "terminal" :title "Comandos Disponibles"
                      :items c/install-scripts}]}}

    ;; 11. Configuración BD
    {:template :blocks
     :data {:id "configuracion"
            :header {:icon "settings" :kicker "Setup" :title "Configuración — Base de Datos"}
            :blocks [{:type :info-grid :icon "database" :title "Variables de Conexión"
                      :items c/config-db}
                     {:type :highlight :icon "file-text" :title ".env mínimo para desarrollo"
                      :items c/config-env-example}]}}

    ;; 12. Configuración App
    {:template :blocks
     :data {:id "config-app"
            :header {:icon "sliders-horizontal" :kicker "Setup" :title "Configuración — Aplicación"}
            :blocks [{:type :info-grid :icon "settings" :title "Aplicación y Servicios Externos"
                      :items c/config-app}]}}

    ;; 13. Base de Datos
    {:template :hero-section
     :data {:id "base-de-datos"
            :hero {:kicker "Datos"
                   :title  c/db-title
                   :subtitle c/db-subtitle}
            :blocks [{:type :text-block :content c/db-text}
                     {:type :info-grid :icon "wrench" :title "Herramientas"
                      :items c/db-tools}]}}

    ;; 14. Esquema de Tablas
    {:template :blocks
     :data {:id "esquema"
            :header {:icon "table" :kicker "Base de Datos" :title c/esquema-title}
            :blocks [{:type :info-grid :icon "users" :title "Usuarios y Permisos"
                      :items c/esquema-usuarios}
                     {:type :info-grid :icon "clipboard-list" :title "Dominio Analítico"
                      :items c/esquema-dominio}]}}

    ;; 15. Migraciones
    {:template :blocks
     :data {:id "migraciones"
            :header {:icon "refresh-cw" :kicker "Base de Datos" :title "Migraciones"}
            :blocks [{:type :text-block :content c/migrations-text}
                     {:type :info-grid :icon "arrow-up" :title "Comandos de Migración"
                      :items c/migrations-items}]}}

    ;; 16. Seeds
    {:template :blocks
     :data {:id "seeds"
            :header {:icon "sprout" :kicker "Base de Datos" :title "Seeds (Datos Iniciales)"}
            :blocks [{:type :info-grid :icon "sprout" :title "Archivos de Seed"
                      :items c/seeds-items}]}}

    ;; 17. Funcionalidades
    {:template :hero-section
     :data {:id "funcionalidades"
            :hero {:kicker "Sistema"
                   :title  c/func-title
                   :subtitle c/func-subtitle}
            :blocks [{:type :text-block :content c/func-text}
                     {:type :info-grid :icon "box" :title "Módulos Principales"
                      :items c/func-core}]}}

    ;; 18. Screenshot: Informes de Análisis
    {:template :full-image
     :data {:id "img-reports"
            :img "report-analysis.png"
            :screenshot? true
            :kicker "Módulo Central"
            :title "Informes de Análisis"
            :subtitle "Listado paginado con búsqueda por ID, filtros por cliente y estado del flujo de trabajo."}}

    ;; 19. Módulos del Sistema
    {:template :blocks
     :data {:id "modulos"
            :header {:icon "box" :kicker "Funcionalidades" :title c/modulos-title}
            :blocks [{:type :info-grid :icon "layout-grid" :title "Catálogo Completo"
                      :items c/modulos-items}]}}

    ;; 20. Screenshot: Clientes
    {:template :full-image
     :data {:id "img-clients"
            :img "clients.png"
            :screenshot? true
            :kicker "Gestión"
            :title "Clientes"
            :subtitle "Administración de clientes con datos de contacto, normas asociadas y tipo (externo/interno)."}}

    ;; 21. API GraphQL
    {:template :hero-section
     :data {:id "api-graphql"
            :hero {:kicker "API"
                   :title  c/api-title
                   :subtitle c/api-subtitle}
            :blocks [{:type :text-block :content c/api-text}
                     {:type :info-grid :icon "share-2" :title "Patrones de Diseño"
                      :items c/api-patterns}]}}

    ;; 22. Queries
    {:template :blocks
     :data {:id "api-queries"
            :header {:icon "search" :kicker "API" :title "Queries Principales"}
            :blocks [{:type :info-grid :icon "search" :title "Consultas GraphQL"
                      :items c/queries-main}]}}

    ;; 23. Mutations
    {:template :blocks
     :data {:id "api-mutations"
            :header {:icon "edit" :kicker "API" :title "Mutations por Dominio"}
            :blocks [{:type :info-grid :icon "edit" :title "Operaciones de Escritura"
                      :items c/mutations-main}]}}

    ;; 24. Endpoints REST
    {:template :blocks
     :data {:id "api-rest"
            :header {:icon "globe" :kicker "API" :title c/rest-title}
            :blocks [{:type :info-grid :icon "globe" :title c/rest-subtitle
                      :items c/rest-items}]}}

    ;; 25. Autenticación
    {:template :hero-section
     :data {:id "autenticacion"
            :hero {:kicker "Seguridad"
                   :title  c/auth-title
                   :subtitle c/auth-subtitle}
            :blocks [{:type :text-block :content c/auth-text}
                     {:type :steps :items c/auth-flow}]}}

    ;; 26. Screenshot: Usuarios
    {:template :full-image
     :data {:id "img-users"
            :img "users.png"
            :screenshot? true
            :kicker "RBAC"
            :title "Gestión de Usuarios"
            :subtitle "Usuarios con roles, estado de activación y control de acceso por tenant."}}

    ;; 27. Roles y Permisos
    {:template :blocks
     :data {:id "roles"
            :header {:icon "shield" :kicker "Seguridad" :title c/roles-title}
            :blocks [{:type :info-grid :icon "shield" :title "Roles del Sistema"
                      :items c/roles-items}
                     {:type :text-block :content c/roles-text}
                     {:type :callout :icon "building" :style :accent
                      :title "Multi-tenancy"
                      :text c/tenant-text}]}}

    ;; 28. Screenshot: Normas
    {:template :full-image
     :data {:id "img-norms"
            :img "norms.png"
            :screenshot? true
            :kicker "Configuración"
            :title "Normas y Estándares"
            :subtitle "Gestión de normas de calidad con límites por parámetro, vinculadas a subtipos de muestra."}}

    ;; 29. Reportes PDF
    {:template :hero-section
     :data {:id "reportes"
            :hero {:kicker "Reportes"
                   :title  c/reportes-title
                   :subtitle c/reportes-subtitle}
            :blocks [{:type :text-block :content c/reportes-text}
                     {:type :info-grid :icon "file-text" :title "Formatos Disponibles"
                      :items c/reportes-formatos}]}}

    ;; 30. Screenshot: Tipos de Análisis
    {:template :full-image
     :data {:id "img-analysis-types"
            :img "analysis-types.png"
            :screenshot? true
            :kicker "Configuración"
            :title "Tipos de Análisis"
            :subtitle "Catálogo de ensayos y determinaciones con nomenclatura, clasificación y ordenamiento."}}

    ;; 31. Despliegue Docker
    {:template :blocks
     :data {:id "despliegue"
            :header {:icon "cloud" :kicker "DevOps" :title "Despliegue — Docker"}
            :blocks [{:type :text-block :content c/deploy-text}
                     {:type :steps :items c/deploy-stages}]}}

    ;; 32. Cloud Run
    {:template :blocks
     :data {:id "deploy-cloud"
            :header {:icon "rocket" :kicker "DevOps" :title "Google Cloud Run"}
            :blocks [{:type :info-grid :icon "terminal" :title "Comandos de Deploy"
                      :items c/deploy-commands}
                     {:type :info-grid :icon "globe" :title "Ambientes"
                      :items c/deploy-envs}]}}

    ;; 33. Créditos
    {:template :credits
     :data {:title c/credits-title
            :by    c/credits-by
            :orgs  c/credits-orgs
            :legal c/credits-legal}}]})
