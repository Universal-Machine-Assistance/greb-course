(ns rivia.content
  "All content data for the Rivia LIMS technical documentation.")

;; ── Index ───────────────────────────────────────────────────────
(def index-title "Índice")

(def index-entries
  [{:id "portada"        :label "Portada"                    :page 1}
   {:id "contenido"      :label "Contenido"                  :page 2}
   {:id "indice"         :label "Índice"                     :page 3}
   {:id "introduccion"   :label "Introducción"               :page 4  :icon "book-open"}
   {:id "stack"          :label "Stack Tecnológico"          :page 6  :icon "layers"}
   {:id "arquitectura"   :label "Arquitectura"               :page 7  :icon "git-branch"}
   {:id "instalacion"    :label "Instalación"                :page 9  :icon "download"}
   {:id "scripts"        :label "Scripts"                    :page 10 :icon "terminal"}
   {:id "configuracion"  :label "Configuración BD"           :page 11 :icon "settings"}
   {:id "config-app"     :label "Configuración App"          :page 12 :icon "sliders-horizontal"}
   {:id "base-de-datos"  :label "Base de Datos"              :page 13 :icon "database"}
   {:id "esquema"        :label "Esquema de Tablas"          :page 14 :icon "table"}
   {:id "migraciones"    :label "Migraciones"                :page 15 :icon "refresh-cw"}
   {:id "seeds"          :label "Seeds"                      :page 16 :icon "sprout"}
   {:id "funcionalidades" :label "Funcionalidades"           :page 17 :icon "layout-grid"}
   {:id "modulos"        :label "Módulos del Sistema"        :page 19 :icon "box"}
   {:id "api-graphql"    :label "API GraphQL"                :page 21 :icon "share-2"}
   {:id "api-queries"    :label "Queries"                    :page 22 :icon "search"}
   {:id "api-mutations"  :label "Mutations"                  :page 23 :icon "edit"}
   {:id "api-rest"       :label "Endpoints REST"             :page 24 :icon "globe"}
   {:id "autenticacion"  :label "Autenticación"              :page 25 :icon "lock"}
   {:id "roles"          :label "Roles y Permisos"           :page 27 :icon "shield"}
   {:id "reportes"       :label "Reportes PDF"               :page 29 :icon "file-text"}
   {:id "despliegue"     :label "Despliegue Docker"          :page 31 :icon "cloud"}
   {:id "deploy-cloud"   :label "Cloud Run"                  :page 32 :icon "rocket"}
   {:id "creditos"       :label "Créditos"                   :page 33 :icon "heart"}])

;; ── TOC sections ────────────────────────────────────────────────
(def contenido-title "Rivia LIMS")
(def contenido-subtitle "Documentación Técnica")

(def contenido-sections
  [{:id "introduccion" :title "Visión General" :icon "book-open"
    :items [{:label "Introducción"          :ok true}
            {:label "Stack Tecnológico"     :ok true}
            {:label "Arquitectura"          :ok true}]}
   {:id "instalacion" :title "Setup" :icon "download"
    :items [{:label "Instalación y Scripts" :ok true}
            {:label "Configuración BD"      :ok true}
            {:label "Configuración App"     :ok true}]}
   {:id "base-de-datos" :title "Base de Datos" :icon "database"
    :items [{:label "Esquema de Tablas"     :ok true}
            {:label "Migraciones"           :ok true}
            {:label "Seeds"                 :ok true}]}
   {:id "funcionalidades" :title "Funcionalidades" :icon "layout-grid"
    :items [{:label "Módulos Principales"   :ok true}
            {:label "Catálogo Completo"     :ok true}]}
   {:id "api-graphql" :title "API" :icon "share-2"
    :items [{:label "GraphQL"               :ok true}
            {:label "Queries y Mutations"   :ok true}
            {:label "Endpoints REST"        :ok true}]}
   {:id "autenticacion" :title "Seguridad" :icon "lock"
    :items [{:label "JWT + Cookies"         :ok true}
            {:label "Roles y Permisos"      :ok true}]}
   {:id "reportes" :title "Reportes" :icon "file-text"
    :items [{:label "PDF con React"         :ok true}
            {:label "Puppeteer"             :ok true}]}
   {:id "despliegue" :title "Despliegue" :icon "cloud"
    :items [{:label "Docker"                :ok true}
            {:label "Cloud Run"             :ok true}]}])

;; ── Introducción ────────────────────────────────────────────────
(def intro-title "¿Qué es Rivia?")

(def intro-lead
  "Rivia es un sistema LIMS (Laboratory Information Management System) multi-tenant para la gestión integral de informes de análisis de laboratorio.")

(def intro-dropcap
  ["Los laboratorios de análisis enfrentan un desafío constante: gestionar el flujo completo de muestras, resultados e informes de forma eficiente, trazable y conforme a normas de calidad. Rivia nace como respuesta a esta necesidad — una plataforma completa que cubre desde la **recepción de muestras** hasta la **generación y entrega de informes finales** en formato PDF."
   "A diferencia de soluciones genéricas, Rivia está diseñado específicamente para laboratorios. Su modelo de datos refleja la realidad del trabajo analítico: **tipos de análisis**, **normas con límites configurables**, **métodos asociados** y un **flujo de estados** (borrador → revisión → aprobación) que garantiza trazabilidad."
   "El sistema soporta **multi-tenancy** nativo: múltiples organizaciones pueden utilizar la misma instancia de forma completamente aislada, cada una con sus propios datos, usuarios, roles y configuraciones. Todo construido con un stack moderno: React 18, Next.js 13, Relay, GraphQL y PostgreSQL."])

(def intro-text
  "Rivia proporciona una plataforma completa para administrar el ciclo de vida de los análisis de laboratorio. Desde la recepción de muestras hasta la generación y entrega de informes finales en PDF, el sistema cubre todos los aspectos de la operación de un laboratorio analítico moderno.")

(def intro-features
  [{:title "Multi-tenant"        :icon "building"
    :text "Aislamiento completo por organización. Cada tenant tiene sus propios datos, usuarios y configuraciones."}
   {:title "Informes de Análisis" :icon "clipboard-list"
    :text "Flujo completo: creación, edición, revisión, aprobación y generación de PDF. Soporte para informes consolidados."}
   {:title "Normas y Límites"     :icon "ruler"
    :text "Gestión de estándares de calidad con parámetros configurables: rango, entre valores y booleano."}
   {:title "Reportes PDF"         :icon "file-text"
    :text "Generación automática de informes en 4 formatos usando @react-pdf/renderer y Puppeteer."}])

;; ── Stack Tecnológico ───────────────────────────────────────────
(def stack-title "Stack Tecnológico")
(def stack-subtitle "Tecnologías utilizadas en el proyecto")

(def stack-frontend
  [{:title "React 18"          :icon "atom"
    :text "Biblioteca de UI basada en componentes. Renderizado eficiente con virtual DOM y hooks."}
   {:title "Next.js 13"        :icon "triangle"
    :text "Framework de React con SSR, enrutamiento basado en archivos y API routes integradas."}
   {:title "Relay 15"          :icon "share-2"
    :text "Cliente GraphQL de Meta con data fetching declarativo por fragmentos y compilación en build."}
   {:title "Ant Design 4"      :icon "layout"
    :text "Biblioteca de componentes enterprise: tablas, formularios, modales, notificaciones."}])

(def stack-backend
  [{:title "Node.js 18"        :icon "hexagon"
    :text "Runtime del servidor. Express como framework HTTP subyacente."}
   {:title "Apollo Server"     :icon "share-2"
    :text "Servidor GraphQL montado sobre Express. Esquema con tipos, resolvers y mutations."}
   {:title "PostgreSQL 14"     :icon "database"
    :text "Base de datos relacional con UUID como claves primarias y soft deletes."}
   {:title "Knex.js + Objection" :icon "layers"
    :text "Query builder + ORM con relaciones, validación, migraciones y Dataloader para N+1."}])

;; ── Arquitectura ────────────────────────────────────────────────
(def arq-title "Arquitectura General")
(def arq-subtitle "Flujo de datos del sistema")

(def arq-text
  "El frontend se comunica exclusivamente a través de GraphQL. Relay gestiona las consultas y mutaciones de forma declarativa mediante fragmentos asociados a cada componente. En el servidor, Apollo Server recibe las operaciones GraphQL, las resuelve a través de **21 servicios** que encapsulan la lógica de negocio, y estos interactúan con la base de datos mediante Objection.js y Knex.js.")

(def arq-flow-items
  [{:num "1" :title "Frontend" :icon "monitor"
    :text "React 18 + Relay + Ant Design. Cada componente declara sus datos via fragmentos GraphQL."}
   {:num "2" :title "API GraphQL" :icon "share-2"
    :text "Apollo Server Express en `/api/graphql`. 40+ queries con paginación Relay y 49 mutations."}
   {:num "3" :title "Capa de Servicios" :icon "box"
    :text "21 servicios con Dataloader para batching. Cada servicio recibe `tenantId` para aislamiento."}
   {:num "4" :title "PostgreSQL" :icon "database"
    :text "107 migraciones, UUID PKs, soft deletes con `deleted_at`, triggers de `updated_at`."}])

(def arq-stats
  [{:icon "layers"    :label "Servicios"    :value "21"}
   {:icon "code"      :label "Mutations"    :value "49"}
   {:icon "database"  :label "Migraciones"  :value "107"}
   {:icon "share-2"   :label "Queries"      :value "40+"}])

;; ── Instalación ─────────────────────────────────────────────────
(def install-title "Instalación")
(def install-subtitle "Configuración del entorno de desarrollo")

(def install-steps
  [{:num "1" :title "Clonar" :icon "git-branch"
    :text "`git clone <url>` y `cd rivia`. Requiere **Node.js 18+**, **PostgreSQL 14+** y **Yarn**."}
   {:num "2" :title "Dependencias" :icon "package"
    :text "`yarn install` — instala todas las dependencias del proyecto."}
   {:num "3" :title "Configurar .env" :icon "file-text"
    :text "Crear `.env` con `DB_HOST`, `DB_PORT`, `DB_DATABASE`, `DB_USER`, `DB_PASS` y `JWT_SECRET`."}
   {:num "4" :title "Base de datos" :icon "database"
    :text "`yarn onboarding` (setup rápido) o `yarn migrate:latest` + `yarn seed` (manual)."}
   {:num "5" :title "GraphQL" :icon "share-2"
    :text "`yarn build-graphql` — genera el schema y compila los artefactos de Relay."}
   {:num "6" :title "Iniciar" :icon "play"
    :text "`yarn dev` — servidor de desarrollo en `http://localhost:3000`. Login: **admin / 1234**."}])

(def install-docker-text
  "Alternativamente, `docker-compose up` levanta PostgreSQL (puerto 5431) y la aplicación (puerto 3000) con hot-reload.")

(def install-scripts
  [{:title "yarn dev"             :icon "play"        :text "Servidor de desarrollo (puerto 3000)."}
   {:title "yarn build"           :icon "package"     :text "Build de producción (Next.js)."}
   {:title "yarn build-graphql"   :icon "share-2"     :text "Compilar schema GraphQL + artefactos Relay."}
   {:title "yarn migrate:latest"  :icon "database"    :text "Ejecutar migraciones pendientes."}
   {:title "yarn migrate:rollback" :icon "rotate-ccw" :text "Revertir última migración."}
   {:title "yarn seed"            :icon "sprout"      :text "Cargar datos iniciales."}])

;; ── Configuración ───────────────────────────────────────────────
(def config-title "Configuración")
(def config-subtitle "Variables de entorno")

(def config-db
  [{:title "DB_HOST / DB_PORT"   :icon "server"
    :text "Host y puerto de PostgreSQL. Por defecto `localhost:5432`."}
   {:title "DB_DATABASE"         :icon "database"
    :text "Nombre de la base de datos. Sugerido: `rivia`."}
   {:title "DB_USER / DB_PASS"   :icon "key"
    :text "Credenciales de conexión a PostgreSQL."}
   {:title "DB_POOL_MIN/MAX"     :icon "sliders-horizontal"
    :text "Pool de conexiones: mínimo 2, máximo 10 por defecto. `DB_ACQUIRE_TIMEOUT` = 5000ms."}])

(def config-app
  [{:title "JWT_SECRET"          :icon "lock"
    :text "Clave secreta para firmar tokens JWT. **Obligatoria** en producción."}
   {:title "BASE_URL"            :icon "globe"
    :text "URL base de la aplicación. Usado por Puppeteer para generar PDFs."}
   {:title "SMTP_USER/PASSWORD"  :icon "mail"
    :text "Credenciales SMTP para envío de correos con informes PDF."}
   {:title "AUTHENTICATE_API_URL" :icon "shield"
    :text "URL de API externa de autenticación (opcional). Permite integrar SSO/LDAP."}])

(def config-env-example
  ["`DB_HOST=localhost`"
   "`DB_PORT=5432`"
   "`DB_DATABASE=rivia`"
   "`DB_USER=postgres`"
   "`DB_PASS=postgres`"
   "`JWT_SECRET=dev-secret-key`"])

;; ── Base de Datos ───────────────────────────────────────────────
(def db-title "Base de Datos")
(def db-subtitle "PostgreSQL 14 + Knex.js + Objection.js")

(def db-text
  "La base de datos utiliza **PostgreSQL 14** como motor, **Knex.js** como query builder y gestor de migraciones, y **Objection.js** como ORM con soporte para relaciones y validación. **Dataloader** resuelve el problema N+1 agrupando consultas en los resolvers GraphQL.")

(def db-tools
  [{:title "Knex.js"       :icon "wrench"
    :text "Query builder para Node.js. Gestiona migraciones y construcción de consultas SQL."}
   {:title "Objection.js"  :icon "layers"
    :text "ORM construido sobre Knex con modelos, relaciones, validación y hooks de ciclo de vida."}
   {:title "Dataloader"    :icon "zap"
    :text "Batching y caching de consultas. Previene N+1 en resolvers GraphQL."}
   {:title "knex-stringcase" :icon "case-sensitive"
    :text "Conversión automática camelCase ↔ snake_case entre código y base de datos."}])

;; ── Esquema de Tablas ───────────────────────────────────────────
(def esquema-title "Esquema de Tablas")
(def esquema-subtitle "Estructura principal de la base de datos")

(def esquema-usuarios
  [{:title "tenants"         :icon "building"
    :text "Organizaciones del sistema. Cada tenant agrupa sus propios datos (multi-tenancy). Campos: `id`, `name`, `display_name`, `logo`."}
   {:title "users"           :icon "user"
    :text "Usuarios con `first_name`, `last_name`, `email`, `username`, `password` (SHA-256 + Base64), `tenant_id`."}
   {:title "roles"           :icon "shield"
    :text "Roles predefinidos: **admin**, **tech** (Gestor Técnico), **analyser**, **service**, **digitate** (Digitador)."}
   {:title "permissions"     :icon "key"
    :text "Permisos granulares con formato `módulo::acción` (ej: `analysis_report::create`, `access::clients`)."}])

(def esquema-dominio
  [{:title "analysis_report"     :icon "clipboard-list"
    :text "Informe principal. Referencia tenant, cliente y usuario. Flujo: borrador → enviado → revisado → aprobado."}
   {:title "analysis_report_sample" :icon "test-tubes"
    :text "Muestras dentro de un informe con identificador, condiciones y vínculos a tipos de análisis."}
   {:title "sample_types / sub_sample_types" :icon "layers"
    :text "Clasificación jerárquica de muestras. Subtipos vinculados a normas y tipos de análisis."}
   {:title "norms / norm_limits" :icon "ruler"
    :text "Estándares de calidad con límites por parámetro: rango, entre valores o booleano."}])

;; ── Migraciones y Seeds ─────────────────────────────────────────
(def migrations-title "Migraciones y Seeds")
(def migrations-subtitle "107 migraciones y 4 archivos de seed")

(def migrations-text
  "Las migraciones se escriben en TypeScript (`db/migrations-ts/`) y se compilan a JavaScript. Cada archivo exporta funciones `up` y `down`. Las tablas usan **UUID** como clave primaria y un trigger automático `on_update_timestamp()` para el campo `updated_at`.")

(def migrations-items
  [{:title "migrate:make"     :icon "plus"
    :text "`yarn migrate:make nombre-descriptivo` — Crea nueva migración con timestamp."}
   {:title "migrate:latest"   :icon "arrow-up"
    :text "`yarn migrate:latest` — Ejecuta todas las migraciones pendientes."}
   {:title "migrate:rollback" :icon "rotate-ccw"
    :text "`yarn migrate:rollback` — Revierte la última tanda de migraciones."}])

(def seeds-items
  [{:title "1_users.js"       :icon "user"
    :text "Crea el tenant por defecto (`abt`) y un usuario admin con email `admin@abt.com` y contraseña `1234`."}
   {:title "2_roles.js"       :icon "shield"
    :text "Crea los 5 roles del sistema y asigna admin al usuario inicial."}
   {:title "3_permissions.js" :icon "key"
    :text "Genera permisos CRUD por módulo. Permisos especiales: `complete`, `print`, `review`, `approve`."}
   {:title "4_modules.js"     :icon "layout-grid"
    :text "Registra módulos de navegación con URL, icono, grupo (`principal`, `configuración`, `avanzado`) y permiso."}])

;; ── Funcionalidades ─────────────────────────────────────────────
(def func-title "Funcionalidades")
(def func-subtitle "Módulos del sistema Rivia")

(def func-text
  "Rivia organiza su funcionalidad en **12 módulos** que cubren todo el ciclo de vida de un laboratorio analítico: desde la gestión de clientes y muestras hasta la generación de reportes PDF y el dashboard de métricas.")

(def func-core
  [{:title "Informes de Análisis" :icon "clipboard-list"
    :text "Módulo central. Creación, edición, flujo de estados (borrador → revisión → aprobación), consolidación e impresión en 4 formatos."}
   {:title "Muestras"            :icon "test-tubes"
    :text "Registro, seguimiento, duplicación de muestras. Resultados por tipo de análisis con evaluación de cumplimiento."}
   {:title "Normas y Estándares" :icon "ruler"
    :text "Gestión de normas de calidad con límites configurables (rango, entre valores, booleano). Duplicación de normas."}
   {:title "Dashboard"           :icon "bar-chart-2"
    :text "Métricas de no conformidad, distribución de estados, tendencia mensual, conteo por subtipo, informes vencidos."}])

;; ── Módulos del Sistema ─────────────────────────────────────────
(def modulos-title "Módulos del Sistema")

(def modulos-items
  [{:title "Clientes"                :icon "users"
    :text "Gestión de clientes con RNC, contactos y normas predeterminadas. Ruta: `/clients`."}
   {:title "Tipos de Análisis"       :icon "flask-conical"
    :text "Catálogo de ensayos y determinaciones disponibles en el laboratorio. Ruta: `/analysis_types`."}
   {:title "Tipos de Muestra"        :icon "layers"
    :text "Clasificación jerárquica: tipos y subtipos de muestra. Rutas: `/sample_types`, `/sub_sample_type`."}
   {:title "Métodos"                 :icon "beaker"
    :text "Metodologías de análisis vinculadas a normas y tipos de muestra. Ruta: `/methods`."}
   {:title "Productos"               :icon "package"
    :text "Catálogo de productos que son objeto de análisis. Ruta: `/products`."}
   {:title "Materiales de Referencia" :icon "bookmark"
    :text "Estándares de calibración y control de calidad. Ruta: `/reference_material`."}
   {:title "Usuarios y Roles"        :icon "shield"
    :text "Gestión RBAC con 5 roles. Soft delete, multi-tenant. Ruta: `/users`."}
   {:title "Reportes PDF"            :icon "file-text"
    :text "4 formatos: estándar, personalizado, nuevo y consolidado. Email automático."}])

;; ── API GraphQL ─────────────────────────────────────────────────
(def api-title "API GraphQL")
(def api-subtitle "Endpoint principal del sistema")

(def api-text
  "Rivia expone una API GraphQL como punto de entrada principal. El endpoint `POST /api/graphql` recibe queries y mutations; `GET /api/graphql` sirve el GraphQL Playground para exploración interactiva del esquema.")

(def api-patterns
  [{:title "Relay Pagination"    :icon "list"
    :text "Paginación basada en cursores con `first`, `last`, `before`, `after` y `pageInfo`."}
   {:title "Dataloader Batching" :icon "zap"
    :text "Cada servicio usa Dataloader para agrupar consultas y evitar el problema N+1."}
   {:title "Context Auth"        :icon "lock"
    :text "Usuario y tenant extraídos del JWT se pasan como contexto GraphQL a todos los resolvers."}
   {:title "Service Layer"       :icon "box"
    :text "Todos los resolvers delegan a la capa de servicios. Factory `getServices(tenantId)`."}])

;; ── Queries y Mutations ─────────────────────────────────────────
(def queries-title "Queries y Mutations")
(def queries-subtitle "Operaciones GraphQL disponibles")

(def queries-main
  [{:title "analysisReports"     :icon "clipboard-list"
    :text "Listar informes con filtros por fecha, cliente, código. Paginación cursor-based + offset."}
   {:title "clients / users"     :icon "users"
    :text "Listar clientes y usuarios con paginación Relay estándar (`first`, `after`)."}
   {:title "norms / methods"     :icon "ruler"
    :text "Consultar normas con límites y métodos con tipos de muestra asociados."}
   {:title "dashboard"           :icon "bar-chart-2"
    :text "Métricas filtradas por rango de fechas: no conformidades, estados, tendencias."}
   {:title "viewer / tenant"     :icon "user"
    :text "Usuario autenticado actual y datos del tenant. Sin parámetros."}
   {:title "modules"             :icon "layout-grid"
    :text "Módulos de navegación con permisos asociados. Define el menú visible por rol."}])

(def mutations-main
  [{:title "Informes"    :icon "clipboard-list"
    :text "`AddAnalysisReport`, `UpdateAnalysisReport`, `RemoveAnalysisReport`, `ReviewAnalysisReport`, `ApproveAnalysisReport`, `AddConsolidateAnalysisReport`."}
   {:title "Muestras"    :icon "test-tubes"
    :text "`AddAnalysisReportSample`, `UpdateAnalysisReportSample`, `DuplicateAnalysisReportSample`, `AddResultToSample`, `AddCommentToSample`."}
   {:title "Maestros"    :icon "database"
    :text "Add/Update/Remove para Client, User, Method, Norm, Product, ReferenceMaterial, SampleType, SubSampleType, AnalysisType."}
   {:title "Auth"        :icon "lock"
    :text "`Authenticate` (login con username/password), `ChangeUserPassword`."}])

;; ── Endpoints REST ──────────────────────────────────────────────
(def rest-title "Endpoints REST")
(def rest-subtitle "Operaciones complementarias")

(def rest-items
  [{:title "GET /api/health"     :icon "heart-pulse"
    :text "Verificación de salud. Sin autenticación. Retorna status, timestamp, uptime, environment, version."}
   {:title "GET /api/report"     :icon "file-text"
    :text "Genera PDF con `@react-pdf/renderer`. Params: `id`, `samples`, `condition`, `limits`, `labors`."}
   {:title "POST /api/sendReport" :icon "send"
    :text "Genera PDFs y los envía por email al cliente. Params: `id`, `samples`, `reportType` (individual/all)."}
   {:title "GET /api/sendPdf"    :icon "printer"
    :text "PDF via Puppeteer + envío por email o descarga directa. Params: `reportId`, `reportType`, `orientation`."}
   {:title "POST /api/upload"    :icon "upload"
    :text "Sube archivo de firma a GCS. Multipart con `file` y `user_id`. Actualiza campo `signature`."}
   {:title "GET /api/getUploadUrl" :icon "link"
    :text "URL presignada de GCS (10 min vigencia). Params: `user_id`, `object_type`."}])

;; ── Autenticación ───────────────────────────────────────────────
(def auth-title "Autenticación")
(def auth-subtitle "JWT con cookies HTTP-only")

(def auth-text
  "El sistema utiliza **JWT** (JSON Web Tokens) almacenados en cookies HTTP-only. La contraseña se hashea con **SHA-256 + Base64**. El token se lee de la cookie `token` o del header `token` como fallback.")

(def auth-flow
  [{:num "1" :title "Credenciales" :icon "log-in"
    :text "El usuario envía username/password via la mutation `Authenticate`."}
   {:num "2" :title "Verificación" :icon "shield-check"
    :text "SHA-256 → Base64 del password. Se compara con el hash almacenado en la BD."}
   {:num "3" :title "Token JWT" :icon "key"
    :text "Si es válido, se genera un JWT firmado con `JWT_SECRET`. Payload: datos del usuario sin password."}
   {:num "4" :title "Cookie" :icon "cookie"
    :text "Token en cookie HTTP-only con `maxAge: 86400s` (1 día). Protección contra XSS."}
   {:num "5" :title "Validación" :icon "check-circle"
    :text "Middleware `validateAuthentication` verifica token en cada POST a `/api/graphql`."}])

(def auth-whitelist
  ["`mutation useAuthenticateMutation` — login"
   "`NavigationQuery` — navegación pública"
   "`loginQuery` — página de login"
   "`GET /api/health` — health check"])

;; ── Roles y Permisos ────────────────────────────────────────────
(def roles-title "Roles y Permisos")
(def roles-subtitle "Control de acceso basado en roles (RBAC)")

(def roles-items
  [{:title "Admin"       :icon "crown"
    :text "Acceso total al sistema. Gestión de usuarios, roles, configuración y todos los módulos."}
   {:title "Tech"        :icon "wrench"
    :text "Gestor Técnico. Acceso a informes, muestras, normas y métodos. Sin gestión de usuarios."}
   {:title "Analyser"    :icon "microscope"
    :text "Analista de laboratorio. Acceso a informes y muestras. Puede registrar resultados."}
   {:title "Service"     :icon "headphones"
    :text "Gestor de Servicio. Acceso a clientes e informes. Orientado a atención al cliente."}
   {:title "Digitate"    :icon "keyboard"
    :text "Digitador. Acceso limitado a ingreso de datos en informes y muestras."}])

(def roles-text
  "Los permisos siguen el formato `módulo::acción` (ej: `analysis_report::create`). Acciones estándar: `create`, `delete`, `update`, `view`, `access`. Permisos especiales para informes: `complete`, `print`, `review`, `approve`, `comments`.")

(def tenant-text
  "Cada usuario tiene un `tenantId` incluido en el JWT. Todos los servicios se instancian con `getServices(user.tenantId)`, asegurando que las consultas solo retornan datos del tenant correspondiente. La tabla `tenants` almacena configuración por organización.")

;; ── Reportes PDF ────────────────────────────────────────────────
(def reportes-title "Reportes PDF")
(def reportes-subtitle "Generación automática de informes")

(def reportes-text
  "El sistema utiliza dos mecanismos de generación de PDF: **@react-pdf/renderer** (componentes React → buffer PDF) como mecanismo principal, y **Puppeteer** (headless Chrome) para capturar páginas HTML como PDF. Ambos soportan envío automático por email.")

(def reportes-formatos
  [{:title "Estándar"      :icon "file-text"
    :text "Formato clásico con datos del cliente, muestras y resultados por tipo de análisis."}
   {:title "Personalizado" :icon "sliders-horizontal"
    :text "Tabla personalizada con campos configurables. Componentes `PrintCustom` / `PrintCustomTable`."}
   {:title "Nuevo Formato" :icon "sparkles"
    :text "Variante con diseño actualizado para impresión."}
   {:title "Consolidado"   :icon "layers"
    :text "Múltiples muestras impresas en un único documento PDF."}])

(def reportes-opciones
  ["`showLimits` — incluir límites de norma en el reporte"
   "`showLabors` — incluir información de ensayos realizados"
   "`showConditions` — incluir condiciones de la muestra"
   "`samplesIds` — filtrar muestras específicas para incluir"])

(def reportes-email-text
  "Al enviar un informe por email: (1) se genera el PDF, (2) se obtienen los correos del cliente, (3) se envía con los PDFs como adjuntos, (4) se actualiza `lastSentAt` del informe. Usa **nodemailer** (SMTP) como transporte principal.")

;; ── Despliegue ──────────────────────────────────────────────────
(def deploy-title "Despliegue")
(def deploy-subtitle "Docker + Google Cloud Run")

(def deploy-text
  "El proyecto se despliega en **Google Cloud Run** (proyecto `upgrade-rivia`, región `us-central1`). El Dockerfile usa un build multi-etapa con `node:18-slim` como imagen final, incluyendo Google Chrome para Puppeteer.")

(def deploy-stages
  [{:num "1" :title "deps" :icon "package"
    :text "`node:18-alpine` — `yarn install --frozen-lockfile` con todas las dependencias."}
   {:num "2" :title "builder" :icon "hammer"
    :text "`node:18-alpine` — `yarn build` (Next.js) + `yarn build-migration` (TypeScript → JS)."}
   {:num "3" :title "runner" :icon "play"
    :text "`node:18-slim` — imagen mínima con Chrome, fuentes, artefactos de build. Puerto 3000."}])

(def deploy-commands
  [{:title "yarn build-deployment"    :icon "cloud-upload"
    :text "Build de imagen Docker y push a Google Container Registry."}
   {:title "yarn deploy-production"   :icon "rocket"
    :text "Deploy al servicio `rivia-prod` en Cloud Run."}
   {:title "yarn deploy-integration"  :icon "flask-conical"
    :text "Deploy al servicio `int` en Cloud Run (ambiente de pruebas)."}])

(def deploy-envs
  [{:title "Desarrollo"   :icon "code"
    :text "Ejecución local con `yarn dev`. BD local o via docker-compose (PostgreSQL en puerto 5431)."}
   {:title "Integración"  :icon "flask-conical"
    :text "Servicio `int` en Cloud Run. Misma imagen Docker, variables de entorno de prueba."}
   {:title "Producción"   :icon "globe"
    :text "Servicio `rivia-prod` en Cloud Run. Variables de entorno productivas con SSL y pool extendido."}])

;; ── Créditos ────────────────────────────────────────────────────
(def credits-title "Créditos")
(def credits-by "Documentación generada con Greb Docs")
(def credits-orgs "Upgrade.do — Rivia LIMS")
(def credits-legal "Documentación técnica del sistema Rivia. Uso interno del equipo de desarrollo.")
