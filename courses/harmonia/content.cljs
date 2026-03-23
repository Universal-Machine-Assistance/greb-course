(ns harmonia.content
  "All content data for the Nueva Acrópolis Harmonia user manual.
   Incorporates full documentation from NA_SYS docs/wiki.")

;; ── Index ───────────────────────────────────────────────────────
(def index-entries
  [{:id "portada"          :label "Portada"                       :page 1}
   {:id "contenido"        :label "Contenido"                     :page 2}
   {:id "indice"           :label "Indice"                        :page 3  :icon "list"}
   {:id "introduccion"     :label "Introduccion"                  :page 4  :icon "book-open"}
   {:id "que-es"           :label "Que es Harmonia?"              :page 5  :icon "help-circle"}
   {:id "arquitectura"     :label "Arquitectura del Sistema"      :page 6  :icon "cpu"}
   {:id "esquema-bd"       :label "Esquema de Base de Datos"      :page 7  :icon "database"}
   {:id "acceso"           :label "Acceso y Autenticacion"        :page 8  :icon "log-in"}
   {:id "perfil"           :label "Perfil de Usuario"             :page 9  :icon "user"}
   {:id "img-dashboard"    :label "Vista: Dashboard"              :page 10 :icon "monitor"}
   {:id "api"              :label "API REST"                      :page 11 :icon "cloud"}
   {:id "sedes"            :label "Sedes y Salas"                 :page 12 :icon "building"}
   {:id "img-sedes"        :label "Vista: Sedes"                  :page 13 :icon "image"}
   {:id "catedras"         :label "Catedras"                      :page 14 :icon "book-open"}
   {:id "img-catedras"     :label "Vista: Catedras"               :page 15 :icon "image"}
   {:id "cursos"           :label "Cursos y Asignaciones"         :page 16 :icon "calendar"}
   {:id "img-cursos"       :label "Vista: Cursos"                 :page 17 :icon "image"}
   {:id "img-asignaciones" :label "Vista: Asignaciones"           :page 18 :icon "image"}
   {:id "instructores"     :label "Instructores"                  :page 19 :icon "graduation-cap"}
   {:id "img-instructores" :label "Vista: Instructores"           :page 20 :icon "image"}
   {:id "miembros"         :label "Miembros"                      :page 21 :icon "users"}
   {:id "img-miembros"     :label "Vista: Miembros"               :page 22 :icon "image"}
   {:id "eventos"          :label "Eventos y Asistencia"          :page 23 :icon "calendar"}
   {:id "img-calendario"   :label "Vista: Calendario"             :page 24 :icon "image"}
   {:id "img-encargados"   :label "Vista: Encargados"             :page 25 :icon "image"}
   {:id "biblioteca"       :label "Biblioteca"                    :page 26 :icon "library"}
   {:id "img-biblioteca"   :label "Vista: Biblioteca"             :page 27 :icon "image"}
   {:id "contribuciones"   :label "Contribuciones"                :page 28 :icon "credit-card"}
   {:id "personajes"       :label "Personajes"                    :page 29 :icon "star"}
   {:id "administracion"   :label "Administracion y Usuarios"     :page 30 :icon "settings"}
   {:id "multi-pais"       :label "Operacion Multi-Pais"          :page 31 :icon "globe"}
   {:id "code-entidades"   :label "Codigo Entidades REPL"         :page 32 :icon "terminal"}
   {:id "code-biblioteca"  :label "Codigo Biblioteca y Libros"    :page 33 :icon "terminal"}
   {:id "code-sql"         :label "Codigo SQL y Esquema"          :page 34 :icon "database"}
   {:id "code-api"         :label "Codigo API REST"               :page 35 :icon "cloud"}
   {:id "omnirepl"         :label "OmniREPL Paleta de Comandos"   :page 36 :icon "terminal"}
   {:id "omnirepl-code"    :label "OmniREPL Ejemplos de Codigo"   :page 37 :icon "code"}
   {:id "repl"             :label "REPL Codigo en Vivo"           :page 38 :icon "terminal"}
   {:id "repl-code"        :label "REPL Ejemplos de Codigo"       :page 39 :icon "code"}
   {:id "repl-backend"     :label "REPL Backend y CLI"            :page 40 :icon "server"}
   {:id "repl-backend-code" :label "Backend Ejemplos de Codigo"   :page 41 :icon "code"}
   {:id "creditos"         :label "Creditos"                      :page 42 :icon "heart"}])

;; ── TOC sections ────────────────────────────────────────────────
(def contenido-title "Harmonia — Manual de Usuario")
(def contenido-subtitle "Contenido")

(def contenido-sections
  [{:id "que-es" :title "Introducción"
    :items [{:label "¿Qué es Harmonia?"           :ok true}
            {:label "Arquitectura del sistema"    :ok true}
            {:label "Esquema de base de datos"    :ok true}
            {:label "Acceso y autenticación"      :ok true}
            {:label "Perfil de Usuario"           :ok true}
            {:label "API REST"                    :ok true}]}
   {:id "sedes" :title "Gestión Académica"
    :items [{:label "Sedes y Salas"               :ok true}
            {:label "Cátedras"                    :ok true}
            {:label "Cursos y Asignaciones"       :ok true}
            {:label "Instructores"                :ok true}
            {:label "Miembros"                    :ok true}
            {:label "Eventos y Asistencia"        :ok true}]}
   {:id "biblioteca" :title "Recursos"
    :items [{:label "Biblioteca"                  :ok true}
            {:label "Contribuciones"              :ok true}
            {:label "Personajes"                  :ok true}]}
   {:id "administracion" :title "Administración"
    :items [{:label "Usuarios y Roles"            :ok true}
            {:label "Operación Multi-País"        :ok true}
            {:label "OmniREPL"                    :ok true}
            {:label "REPL Frontend"               :ok true}
            {:label "REPL Backend y CLI"          :ok true}]}])

(def intro-dropcap
  ["En las escuelas de filosofía de **Nueva Acrópolis**, la gestión académica solía dispersarse entre hojas de cálculo, chats grupales y cuadernos de asistencia. Cada sede manejaba sus datos de forma independiente, sin visibilidad entre países ni herramientas unificadas. Harmonia nace para resolver esto: un sistema donde **toda la vida académica** — desde la inscripción de un estudiante hasta el préstamo de un libro — vive en un solo lugar."
   "El nombre no es casual. **Harmonia** evoca el principio pitagórico de la armonía universal: la idea de que el orden y la proporción subyacen a toda la realidad. Así como los pitagóricos buscaban la armonía en los números y la música, este sistema busca la armonía en la **organización del conocimiento** — conectando personas, espacios, materias y eventos en una estructura coherente."
   "A diferencia de soluciones genéricas como Google Classroom o Moodle, Harmonia fue diseñado específicamente para la estructura de Nueva Acrópolis: **7 niveles de cátedras** filosóficas, **salas nombradas como filósofos**, un sistema de **contribuciones** voluntarias y una **biblioteca** que puede compartir libros entre países. Todo opera con aislamiento por país pero con visibilidad global cuando se necesita."])

;; ── Intro: ¿Qué es Harmonia? ─────────────────────────────────
(def intro-title "¿Qué es Harmonia?")

(def intro-lead
  "Harmonia es el sistema de gestión académica de Nueva Acrópolis — una plataforma web para administrar sedes, cursos, miembros, eventos, biblioteca y contribuciones de las escuelas de filosofía en múltiples países.")

(def intro-blocks
  ["Diseñado para coordinadores, instructores y administradores que necesitan organizar la actividad académica diaria: inscripción de estudiantes, registro de asistencia, gestión de biblioteca y seguimiento de contribuciones."
   "Opera con aislamiento por país: cada sede nacional trabaja con sus propios datos. El mismo sistema sirve a República Dominicana, México y cualquier país que se incorpore."
   "Interfaz moderna con diseño **glass-card**, navegación por secciones y herramientas avanzadas como la **Omnibar** (`Ctrl+G`) para acceso rápido a cualquier función."])

(def intro-sistema
  [{:title "Multi-País"         :icon "globe"
    :text "Cada país opera con datos independientes. Seleccioná el país al ingresar y trabajá solo con esa información. Patrón de rutas: `/{countryCode}/{seccion}` — por ejemplo `/do/sedes` o `/mx/cursos`."}
   {:title "12 Entidades"       :icon "database"
    :text "Todos los objetos se almacenan en una tabla única `entities` con `type`, `country_code` y `attrs` JSONB. Tipos: `sede`, `catedra`, `instructor`, `sala`, `curso`, `asignacion`, `evento`, `miembro`, `asistencia`, `contribucion`, `personaje`, `libro`."}
   {:title "Tiempo Real"        :icon "zap"
    :text "Toda la UI deriva de un átomo Reagent. Un `swap!` actualiza la vista al instante. El estado se persiste en `localStorage` bajo la clave `nuevaAcropolisData` y se sincroniza con el servidor vía API REST."}
   {:title "Roles y Permisos"   :icon "shield-check"
    :text "Tres roles: **admin** (gestión completa, CRUD, sync), **instructor** (vista de cursos y miembros), **member** (consulta personal). JWT + bcrypt para autenticación."}])

(def intro-text
  "Harmonia centraliza toda la información académica en una sola plataforma. Al ingresar, el usuario selecciona su **país** y accede a un dashboard con estadísticas de sedes, cursos activos, miembros inscritos y próximos eventos. Desde el **menú lateral** se navega entre las secciones: Sedes, Grupos, Miembros, Instructores, Cátedras, Asignaciones, Calendario, Biblioteca y Contribuciones.")

;; ── Arquitectura ──────────────────────────────────────────────
(def arq-title "Arquitectura del Sistema")
(def arq-subtitle "Stack tecnológico y flujo de datos")

(def arq-intro
  ["Harmonia usa un modelo **entidad/enlace** compatible con PostgreSQL y Datomic. Todos los objetos se almacenan en la tabla `entities` con atributos flexibles JSONB. Las relaciones se expresan como campos FK dentro de `attrs` o mediante la tabla `links`."
   "El frontend es **ClojureScript + Reagent** (React), compilado por Shadow-CLJS. El backend es **Clojure + Ring + Compojure**. Base de datos **PostgreSQL**. Despliegue via **Docker** a **Railway**."])

(def arq-stack-items
  [{:title "Frontend"       :icon "monitor"
    :text "**ClojureScript + Reagent + Shadow-CLJS**. Estado en un único átomo. Tailwind CSS con diseño glass-card. Hot-reload en desarrollo. Puerto `8280` con `npm run cljs:dev`."}
   {:title "Backend"        :icon "server"
    :text "**Clojure + Ring + Compojure**. API REST en puerto `3000` con `clj -M:run`. Autenticación JWT con `buddy-sign`, hashing con `buddy-hashers` (bcrypt+sha512). S3-compatible para fotos."}
   {:title "Base de Datos"  :icon "database"
    :text "**PostgreSQL con JSONB**. Tabla `entities` (id UUID, type, country_code, attrs JSONB, timestamps). Tabla `links` (from_id, to_id, link_type, attrs). Upsert con `ON CONFLICT`."}
   {:title "Despliegue"     :icon "cloud"
    :text "**Docker multi-stage → Railway**. Frontend compilado en release + backend empaquetado en un solo contenedor. PostgreSQL y almacenamiento S3 provisto por Railway."}])

(def arq-flow-items
  [{:title "1. Carga Inicial"        :icon "download"
    :text "El navegador lee `localStorage[\"nuevaAcropolisData\"]` → hidrata el átomo Reagent. Versión actual: 2 (almacenada en `nuevaAcropolisDataVer`). Imágenes base64 se eliminan antes de guardar para respetar el límite de 5MB."}
   {:title "2. Sincronización"       :icon "refresh-cw"
    :text "`GET /api/entities?country=XX` descarga todas las entidades del país. Se mezclan en el estado local con `sync-with-api!`. No se ejecuta automáticamente al cargar — debe llamarse explícitamente."}
   {:title "3. Edición Local"        :icon "pencil"
    :text "Los cambios se aplican con `swap!` sobre el átomo y se persisten automáticamente en localStorage. Cada tipo tiene getters/setters: `(db/get-sedes)`, `(db/set-sedes! [...])`."}
   {:title "4. Guardado al Servidor" :icon "upload"
    :text "`POST /api/entities` con `Authorization: Bearer <JWT>`. Envía el estado completo. El servidor hace upsert masivo: `INSERT ... ON CONFLICT (id) DO UPDATE SET attrs = entities.attrs || EXCLUDED.attrs`."}])

;; ── Esquema de Base de Datos ──────────────────────────────────
(def esquema-title "Esquema de Base de Datos")
(def esquema-subtitle "Tablas PostgreSQL, JSONB e índices")

(def esquema-tablas
  [{:title "Tabla entities"    :icon "table"
    :text "Columnas: `id` UUID PRIMARY KEY (default `gen_random_uuid()`), `type` VARCHAR NOT NULL, `country_code` VARCHAR(2) NOT NULL, `attrs` JSONB DEFAULT '{}', `created_at` TIMESTAMP, `updated_at` TIMESTAMP. Índice en `(type, country_code)`."}
   {:title "Tabla links"       :icon "link"
    :text "Columnas: `id` UUID PRIMARY KEY, `from_id` UUID NOT NULL (FK → entities), `to_id` UUID NOT NULL (FK → entities), `link_type` VARCHAR NOT NULL, `attrs` JSONB DEFAULT '{}'. Índice en `(from_id, to_id, link_type)`."}
   {:title "Patrón Upsert"    :icon "merge"
    :text "`INSERT INTO entities (id, type, country_code, attrs) VALUES (?, ?, ?, ?::jsonb) ON CONFLICT (id) DO UPDATE SET attrs = entities.attrs || EXCLUDED.attrs, updated_at = now()`. Mezcla JSONB: claves nuevas se agregan, existentes se sobreescriben, ausentes se preservan."}
   {:title "IDs"               :icon "hash"
    :text "Datos semilla usan strings simples (`\"1\"`, `\"2\"`, `\"p1\"`). Creados en runtime usan timestamp (`\"lib-1716000000\"`). Backend genera UUID con `gen_random_uuid()` si no se provee."}])

(def esquema-consultas
  [{:title "Contar por tipo"         :icon "hash"
    :text "`SELECT type, count(*) FROM entities WHERE country_code='DO' GROUP BY type ORDER BY count DESC` — resumen de cuántas entidades hay de cada tipo en un país."}
   {:title "Ver campos de un tipo"   :icon "search"
    :text "`SELECT DISTINCT jsonb_object_keys(attrs) AS k FROM entities WHERE type='miembro' ORDER BY k` — lista todos los campos JSONB que existen para un tipo de entidad."}
   {:title "Buscar por campo"        :icon "filter"
    :text "`SELECT id, attrs->>'nombre' FROM entities WHERE type='miembro' AND attrs->>'correo' LIKE '%@gmail%'` — buscar entidades filtrando dentro del JSONB."}
   {:title "Últimas modificaciones"  :icon "clock"
    :text "`SELECT type, id, attrs->>'nombre', updated_at FROM entities WHERE country_code='DO' ORDER BY updated_at DESC LIMIT 10` — las 10 entidades modificadas más recientemente."}])

;; ── Acceso y Autenticación ────────────────────────────────────
(def acceso-title "Acceso y Autenticación")
(def acceso-subtitle "Inicio de sesión, registro y gestión de cuenta")

(def acceso-items
  [{:title "Selector de País"    :icon "globe"
    :text "Al abrir Harmonia, la ruta `/` muestra los países disponibles con banderas. Seleccioná tu país para cargar los datos de esa sede nacional."}
   {:title "Inicio de Sesión"    :icon "log-in"
    :text "`POST /api/auth/login` con `{\"email\": \"tu@correo.com\", \"password\": \"clave\"}`. Devuelve `{\"user\": {...}, \"token\": \"eyJhbGc...\"}`. El token se guarda en localStorage."}
   {:title "Auto-Registro"       :icon "user-plus"
    :text "`POST /api/auth/register` con `{\"email\": \"...\", \"password\": \"...\", \"name\": \"...\", \"country\": \"DO\"}`. Crea cuenta con rol `member`. Si el email coincide con un miembro existente, se vinculan automáticamente."}
   {:title "Sesión Persistente"  :icon "key"
    :text "El JWT se guarda en el átomo de estado (`auth.token`) y persiste en localStorage. No necesitás iniciar sesión cada vez. Contraseñas hasheadas con **bcrypt+sha512** vía `buddy-hashers`."}])

(def acceso-roles
  [{:title "admin"      :icon "shield"
    :text "Acceso total: crear/editar/eliminar cualquier entidad, gestionar usuarios con `POST /api/users/add`, cambiar roles con `POST /api/users/set-role`, sincronizar datos. Usuarios admin por defecto: `eva@acropolis.org`, `gabriel@acropolis.com`, `admin@acropolis.com` (password: `filosofia`)."}
   {:title "instructor"  :icon "graduation-cap"
    :text "Vista de cursos asignados, lista de miembros de sus grupos, registro de asistencia y eventos. Puede estar vinculado a un registro de Instructor vía `miembroId`."}
   {:title "member"      :icon "user"
    :text "Rol por defecto al registrarse. Consulta su perfil personal, curso inscrito, historial de asistencia y contribuciones. Lectura de datos del país pero sin escritura al servidor."}])

(def acceso-text-registro
  "Para crear un nuevo usuario, ingresá a la pantalla de **Registro** desde el enlace en la página de login. Completá los campos: **email** (será tu identificador único), **contraseña** (mínimo 6 caracteres), **nombre completo** y **país** (seleccioná del combobox). Al registrarte, el sistema te asigna el rol **member** automáticamente. Si tu email coincide con un miembro ya existente en el sistema, se vinculan automáticamente y heredás los datos de ese perfil.")

(def acceso-text-admin
  "Un administrador puede crear usuarios directamente desde **Menú → Administración → Usuarios**. Hacé clic en **+ Agregar Usuario** y completá: email, contraseña temporal, nombre, país y rol (admin, instructor o member). También puede cambiar roles de usuarios existentes o vincular manualmente un usuario a un registro de miembro o instructor si la vinculación automática no funcionó.")

;; ── Perfil de Usuario ─────────────────────────────────────────
(def perfil-title "Perfil de Usuario")
(def perfil-subtitle "Cada usuario tiene un perfil personal vinculado a su ficha de miembro")

(def perfil-intro
  ["Cuando un usuario inicia sesión, Harmonia **vincula automáticamente** su cuenta con un registro de **Miembro**. Esto crea un perfil personal accesible desde `/{pais}/perfil` o haciendo clic en el avatar en la esquina superior derecha."
   "Si no existe un miembro con el mismo correo, el sistema **crea uno automáticamente** con datos básicos. Así, todo usuario autenticado tiene siempre un perfil de miembro asociado."])

(def perfil-vinculacion
  [{:title "Vinculación Automática"   :icon "link"
    :text "Al hacer login, la función `ensure-perfil-miembro!` busca un miembro cuyo `correo` coincida con el `email` del usuario. Si lo encuentra, vincula ambos registros mediante `miembroId`. Si no existe ningún miembro con ese correo, crea uno nuevo con rol **estudiante** y estado **member**."}
   {:title "Acceso al Perfil"         :icon "user"
    :text "Ruta `/{pais}/perfil` (ej: `/do/perfil`). También accesible haciendo clic en el avatar o el email en la barra superior. El slug especial `perfil` resuelve al miembro vinculado al usuario actual."}
   {:title "Avatar"                   :icon "image"
    :text "El avatar se resuelve en orden: **1)** foto del miembro (campo `imagen`), **2)** Gravatar del correo del miembro, **3)** Gravatar del email del usuario, **4)** fallback con inicial del email sobre un gradiente de color determinístico."}
   {:title "Doble Identidad"          :icon "layers"
    :text "Un **Usuario** es la cuenta de autenticación (email, password, role, JWT). Un **Miembro** es la ficha académica (nombre, apellido, curso, asistencia, contribuciones). Están vinculados por `miembroId` o coincidencia de email, pero son entidades separadas."}])

(def perfil-contenido
  [{:title "Datos Personales"     :icon "id-card"
    :text "Nombre, apellido, correo, móvil, fecha de ingreso. Editables desde el formulario del perfil. Los cambios se guardan automáticamente al salir del campo (on-blur)."}
   {:title "Estado y Rol"         :icon "badge"
    :text "Rol del miembro (**estudiante**, **coordinador**, **delegado**, **voluntario**, **instructor**) y estado (**member**, **probacionismo**). Si es instructor, también muestra **título de instructor** y sus clases asignadas."}
   {:title "Curso Inscrito"       :icon "book-open"
    :text "El curso al que pertenece el miembro. Se puede cambiar desde el perfil (admin). Muestra nombre del curso, día, hora y sede."}
   {:title "Biografía y Fotos"    :icon "camera"
    :text "Campo de texto libre para biografía. Galería de imágenes (`imagenes` array). Foto de perfil principal (`imagen`). Las fotos se muestran en carrusel en la ficha completa."}
   {:title "Historial Académico"  :icon "clock"
    :text "Timeline de **asistencia** a eventos (fechas, tipo de evento, presente/ausente). **Contribuciones** (pagos, membresías, donaciones con montos y fechas). **Hechos** registrados por instructores."}
   {:title "Compañeros"           :icon "users"
    :text "Lista de otros miembros del mismo curso. Acceso rápido a sus perfiles. Visualización de la comunidad del grupo de estudio."}])

(def perfil-code-vinculacion
  [";; ── Flujo de vinculación Usuario → Miembro ──"
   ""
   ";; 1. Login exitoso"
   {:text "(db/set-user! {:email \"eva@acropolis.org\"" :hl true}
   {:text "               :role \"admin\"" :hl true}
   {:text "               :token \"eyJhbGc...\"})" :hl true}
   ""
   ";; 2. ensure-perfil-miembro! se ejecuta automáticamente"
   ";; Busca miembro con correo = email del usuario"
   "(some #(when (= (lower (:correo %)) \"eva@acropolis.org\") %)"
   "      (db/get-miembros))"
   ""
   ";; 3a. Si encuentra → vincula"
   {:text "(swap! app-state assoc-in [:auth :miembroId] (:id miembro))" :hl true}
   ""
   ";; 3b. Si NO encuentra → crea miembro nuevo"
   "(db/set-miembros!"
   "  (conj (db/get-miembros)"
   "    {:id (str (js/Date.now))"
   "     :nombre \"\""
   "     :apellido \"\""
   "     :correo \"eva@acropolis.org\""
   "     :role \"estudiante\""
   {:text "     :status \"member\"" :hl true}
   "     :countryCode \"DO\"}))"
   ""
   ";; 4. Acceder al perfil"
   {:text ";; Navegar a /do/perfil" :hl true}
   "(db/get-miembro-for-user)  ;; → {:nombre \"Eva\" ...}"])

(def perfil-text
  "Al hacer login por primera vez, el sistema crea automáticamente tu **perfil de miembro** vinculado a tu cuenta. Desde **Menú → Perfil** (o haciendo clic en tu avatar en la barra superior) podés ver y editar: nombre, apellido, móvil, correo, biografía y foto de perfil. Los cambios se guardan automáticamente al salir de cada campo. Si sos instructor, también verás tus clases asignadas y tu título docente.")

;; ── API REST ──────────────────────────────────────────────────
(def api-title "API REST")
(def api-subtitle "Endpoints, autenticación y formatos")

(def api-entidades
  [{:title "GET /api/entities"          :icon "download"
    :text "`GET /api/entities?country=DO` — devuelve **todas** las entidades de un país agrupadas por tipo: `{\"sedes\": [...], \"catedras\": [...], \"cursos\": [...], ...}`. Sin autenticación. Alternativa: `GET /api/countries/{code}/entities`."}
   {:title "POST /api/entities"         :icon "upload"
    :text "Cuerpo: mismo formato que GET + `\"country\": \"DO\"`. Requiere `Authorization: Bearer <JWT>`. Respuesta: `{\"ok\": true, \"country\": \"DO\"}`. Hace upsert masivo — mezcla JSONB sin borrar campos existentes."}
   {:title "POST /api/auth/login"       :icon "log-in"
    :text "Cuerpo: `{\"email\": \"user@example.com\", \"password\": \"filosofia\"}`. Respuesta: `{\"user\": {\"id\": \"uuid\", \"email\": \"...\", \"role\": \"admin\", \"countryCode\": \"DO\"}, \"token\": \"eyJ...\"}`."}
   {:title "POST /api/auth/register"    :icon "user-plus"
    :text "Cuerpo: `{\"email\": \"...\", \"password\": \"...\", \"name\": \"...\", \"country\": \"DO\"}`. Crea usuario con rol `member`. Misma respuesta que login."}])

(def api-admin
  [{:title "GET /api/users"              :icon "users"
    :text "Lista todos los usuarios. Requiere JWT de admin. Devuelve array con id, email, name, role, countryCode de cada usuario."}
   {:title "POST /api/users/add"         :icon "user-plus"
    :text "Crear usuario como admin. Cuerpo: `{\"email\": \"...\", \"password\": \"...\", \"name\": \"...\", \"country\": \"DO\", \"role\": \"member\"}`. Requiere JWT admin."}
   {:title "POST /api/users/set-role"    :icon "shield"
    :text "Cambiar rol de un usuario: `{\"userId\": \"uuid\", \"role\": \"admin\"}`. Requiere JWT admin. Roles válidos: `admin`, `member`, `instructor`."}
   {:title "POST /api/users/set-miembro" :icon "link"
    :text "Vincular usuario a miembro/instructor: `{\"userId\": \"uuid\", \"miembroId\": \"uuid\"}`. Útil cuando la vinculación automática por email no funcionó."}])

(def api-biblioteca
  [{:title "GET /api/libro/{isbn}"   :icon "search"
    :text "Busca un libro por ISBN en **todos los países**. Respuesta: `{\"book\": {\"titulo\": \"...\", \"isbn\": \"...\"}, \"locations\": [{\"countryCode\": \"DO\", \"biblioteca\": \"NAC\", \"disponible\": true}, ...]}`."}
   {:title "PUT /api/libro"          :icon "pencil"
    :text "Actualizar datos de un libro. Requiere JWT admin. Cuerpo: objeto libro con campos actualizados."}
   {:title "POST /api/upload-file"   :icon "upload"
    :text "Subir archivo (PDF, imagen) asociado a una entidad. Cuerpo: `{\"base64\": \"data:...\", \"entityType\": \"libro\", \"entityId\": \"uuid\", \"filename\": \"manual.pdf\"}`. Respuesta: `{\"url\": \"/uploads/libro/uuid/manual.pdf\"}`."}
   {:title "GET /api/serve-image"    :icon "image"
    :text "Sirve imágenes desde almacenamiento S3-compatible. Requiere configurar `S3_BUCKET`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, `S3_ENDPOINT` en variables de entorno."}])

;; ── Sedes y Salas ─────────────────────────────────────────────
(def sedes-title "Sedes y Salas")
(def sedes-subtitle "Gestión de locales y espacios físicos")

(def sedes-campos
  [{:title "Campos de Sede"     :icon "file-text"
    :text "**id** UUID · **countryCode** ISO 3166-1 · **tipo** `main` (nacional) o `sub` (local) · **nombre** · **ciudad** · **direccion** · **encargadoId** FK a instructor · **imagen** base64 · **imagenes** galería."}
   {:title "Campos de Sala"     :icon "file-text"
    :text "**id** UUID · **countryCode** · **sedeId** FK a sede · **nombre** · **ciudad** · **descripcion** · **imagen**. Las 7 salas semilla llevan nombres filosóficos: *Ágora, Hipatia, Platón, Giordano Bruno, Pitágoras, FFVV*."}
   {:title "Relaciones"         :icon "git-branch"
    :text "Sede **contiene** Salas (vía `sala.sedeId`), **contiene** Cursos (vía `curso.sedeId`), tiene **Encargado** (vía `encargadoId` → Instructor). Datos semilla: 3 sedes (Los Prados, Naco, Centro León) en Santo Domingo y Santiago."}
   {:title "REPL Sedes"         :icon "terminal"
    :text "Backend: `(sedes)` — todas las sedes de DO. `(sedes \"MX\")` — sedes de México. `(first (sedes))` — estructura de una sede. Frontend: `(db/get-sedes)` devuelve el vector, `(db/set-sedes! [...])` lo reemplaza y actualiza la UI."}])

(def sedes-nav
  [{:title "Listado de Sedes"  :icon "list"
    :text "Menú lateral → **Sedes**. Grilla de tarjetas glass-card con nombre, ciudad, dirección, encargado y conteo de cursos/salas. Botón **+ Nueva Sede** para agregar."}
   {:title "Detalle de Sede"   :icon "eye"
    :text "Clic en una sede para ver información completa. Botones ✏️ para editar y × para eliminar (solo admin). Vista de salas disponibles dentro de esa sede."}
   {:title "Encargados"        :icon "user-check"
    :text "Vista **Sedes y Encargados**: tabla con sede, ciudad, encargado y contacto (teléfono, email). Directorio de referencia para coordinación."}])

(def sedes-text
  "Para crear una nueva sede, andá a **Menú → Sedes** y hacé clic en **+ Nueva Sede**. Completá: **nombre** de la sede, **ciudad**, **dirección** física, y opcionalmente seleccioná un **encargado** (instructor responsable) del combobox. Podés agregar una **foto** de la sede. Dentro de cada sede podés crear **salas** — los espacios donde se imparten las clases. Cada sala necesita un **nombre** y opcionalmente una descripción e imagen.")

;; ── Cátedras ──────────────────────────────────────────────────
(def catedras-title "Cátedras")
(def catedras-subtitle "Materias académicas organizadas por nivel")

(def catedras-items
  [{:title "Campos"         :icon "file-text"
    :text "**id** UUID · **countryCode** · **nivel** (`Primer` a `Séptimo`) · **nombre** (ej: *Ética*, *Psicología*, *Historia de la Filosofía Antigua*) · **sigla** código (ej: `ETIC-101`, `PSIC-202`, `HFAN-207`)."}
   {:title "7 Niveles"      :icon "layers"
    :text "**Primer** (4): Diplomado, Ética, Sociopolítica, Filosofía de la Historia. **Segundo** (11): Psicología, Filosofía Aplicada, Simbología Teológica, etc. **Tercero** (7) a **Séptimo** (3): Religiones Comparadas, Cosmogénesis, Astrología, Alquimia, Mayéutica."}
   {:title "Relaciones"     :icon "git-branch"
    :text "Referenciada por **Asignaciones** (vía `asignacion.catedraId`), por **Eventos** (vía `evento.catedraId` opcional), y listada en **Cursos** como array `catedras`. 38 cátedras en datos semilla."}
   {:title "REPL"           :icon "terminal"
    :text "Backend: `(->> (entities) :catedras (map :sigla) sort)` — todas las siglas. `(->> (entities) :catedras (group-by :nivel) (map (fn [[n cs]] [n (count cs)])))` — conteo por nivel. Frontend: `(db/get-catedras)`."}])

(def catedras-text
  "Las cátedras se gestionan desde **Menú → Cátedras**. Para crear una nueva, hacé clic en **+ Nueva Cátedra** y completá: **nombre** de la materia (ej: Ética), **sigla** (ej: ETIC-101) y **nivel** (Primer a Séptimo año). Las cátedras se asignan a los cursos a través de las **Asignaciones**, donde se define qué instructor enseña qué materia en qué sala.")

;; ── Cursos y Asignaciones ─────────────────────────────────────
(def cursos-title "Cursos y Asignaciones")
(def cursos-subtitle "Grupos de estudio, horarios y materias")

(def cursos-campos
  [{:title "Campos de Curso"     :icon "file-text"
    :text "**id** · **countryCode** · **nombre** (ej: `Miembros Viernes`, `santiago_01`) · **sedeId** FK a sede · **dia** (Lunes–Viernes) · **hora** (`19:00`) · **catedras** array de IDs de cátedras asignadas."}
   {:title "Tipos de Curso"      :icon "layers"
    :text "**Miembros X**: grupos regulares con día y hora fijos. **Diplomado X**: programas especiales sin horario semanal fijo. Datos semilla: 16 cursos (9 grupos + 3 diplomados en sede principal + cursos Santiago)."}
   {:title "Asignación"          :icon "calendar"
    :text "Entidad de **unión de 4 vías**: `cursoId` → Curso, `salaId` → Sala, `catedraId` → Cátedra, `instructorId` → Instructor. Responde: *quién enseña qué materia, en qué sala, para qué grupo*. Campos: **id**, **countryCode** + los 4 FKs."}
   {:title "REPL Cursos"         :icon "terminal"
    :text "Backend: `(->> (entities) :cursos (map #(select-keys % [:nombre :dia :hora])))` — resumen. `(->> (entities) :cursos (filter #(= (:dia %) \"Martes\")))` — cursos del martes. Frontend: `(db/get-cursos)`, `(db/get-asignaciones)`."}])

(def cursos-nav
  [{:title "Listado de Cursos"    :icon "list"
    :text "Menú → **Grupos**. Grilla con tarjetas por curso: nombre, sede, día/hora, cátedras asignadas. Filtros por sede (combobox) y por día de la semana (botones Lun–Vie). Botón **+ Nuevo Curso**."}
   {:title "Miembros del Curso"   :icon "users"
    :text "Dentro del detalle de un curso, lista completa de miembros inscriptos (vía `miembro.cursoId`). Nombre, rol, estado de membresía."}
   {:title "Vista Asignaciones"   :icon "clock"
    :text "Menú → **Asignaciones**. Estadísticas: total asignaciones, instructores activos, salas en uso, cátedras asignadas. Filtros por curso e instructor. Botón **+ Nueva Asignación** para crear enlace de 4 vías."}])

(def cursos-text
  "Para crear un nuevo grupo de estudio, andá a **Menú → Grupos** y hacé clic en **+ Nuevo Curso**. Completá: **nombre** del grupo (ej: Miembros Viernes), **sede** (seleccioná del combobox), **día** de la semana y **hora**. Las **asignaciones** se crean desde **Menú → Asignaciones → + Nueva Asignación** donde seleccionás: el **curso**, la **sala**, la **cátedra** y el **instructor**. Esto define quién enseña qué materia, dónde y para qué grupo.")

;; ── Instructores ──────────────────────────────────────────────
(def instructores-title "Instructores")
(def instructores-subtitle "Equipo docente de la escuela")

(def instructores-items
  [{:title "Campos"           :icon "file-text"
    :text "**id** UUID · **countryCode** · **nombre** · **apellido** · **movil** · **correo** · **miembroId** FK (vinculación a Miembro) · **tituloInstructor** · **imagen** base64 · **imagenes** galería."}
   {:title "Relaciones"       :icon "git-branch"
    :text "Referenciado por **Asignaciones** (vía `asignacion.instructorId`), **Eventos** (vía `evento.encargadoId`), **Sedes** (vía `sede.encargadoId`). Auto-vinculado a **Miembro** por `miembroId` o coincidencia de email."}
   {:title "Vista"            :icon "layout-grid"
    :text "Menú → **Instructores**. Stats: total, con asignaciones, total asignaciones. Barra de búsqueda. Grilla de tarjetas con avatar (iniciales o foto), nombre, contacto y badge de asignaciones. Botón **+ Nuevo Instructor**."}
   {:title "REPL"             :icon "terminal"
    :text "Backend: `(->> (instructores) (map #(select-keys % [:nombre :apellido])))`. `(->> (instructores) (filter #(:miembroId %)))` — instructores vinculados a miembro. Frontend: `(db/get-instructores)`. Semilla: 14 instructores."}])

(def instructores-text
  "Para agregar un instructor, andá a **Menú → Instructores** y hacé clic en **+ Nuevo Instructor**. Completá: **nombre**, **apellido**, **móvil**, **correo** y opcionalmente un **título de instructor**. Si el instructor también es miembro, el sistema los vincula automáticamente por coincidencia de email. Desde la ficha del instructor podés ver sus **asignaciones** (qué cátedras enseña en qué cursos y salas).")

;; ── Miembros ──────────────────────────────────────────────────
(def miembros-title "Miembros")
(def miembros-subtitle "Estudiantes, coordinadores e instructores")

(def miembros-campos
  [{:title "Campos Principales"   :icon "file-text"
    :text "**id** UUID · **countryCode** · **nombre** · **apellido** · **role** (`estudiante`, `coordinador`, `delegado`, `voluntario`, `instructor`) · **status** (`member`, `probacionismo`) · **cursoId** FK a curso."}
   {:title "Datos de Contacto"    :icon "phone"
    :text "**movil** teléfono · **correo** email · **imagen** foto de perfil base64 · **imagenes** galería de fotos · **biografia** texto libre · **fechaIngreso** fecha ISO."}
   {:title "Relaciones"           :icon "git-branch"
    :text "Inscrito en **Curso** (vía `cursoId`). Vinculado a **Usuario** (auto por email). Vinculado a **Instructor** (vía `instructor.miembroId`). Tiene **Asistencias** (vía `asistencia.miembroId`), **Contribuciones** (vía `contribucion.miembroId`), **Hechos** (vía `miembroHechos`)."}
   {:title "REPL"                 :icon "terminal"
    :text "`(->> (miembros) count)` → total. `(->> (miembros) (map :role) frequencies)` → conteo por rol. `(->> (miembros) (filter #(= (:cursoId %) \"1\")))` → miembros del curso 1. `(->> (miembros) (filter #(= (:status %) \"probacionismo\")))` → en prueba."}])

(def miembros-nav
  [{:title "Listado"           :icon "list"
    :text "Menú → **Miembros**. Stats: Total, Miembros activos, Probacionismo, Coordinadores. Barra de búsqueda por nombre. Tabla con avatar, nombre, curso, rol, estado y teléfono. Botones ✏️/× por fila."}
   {:title "Detalle"           :icon "eye"
    :text "Clic en un miembro para ver ficha completa: datos personales, galería de fotos, historial de asistencia, contribuciones y hechos registrados. Editable por admin."}
   {:title "Resolver Duplicados" :icon "copy"
    :text "Botón **Resolver Duplicados** en la vista de miembros. Detecta registros duplicados por nombre/correo y permite fusionarlos en uno solo. Herramienta admin."}])

(def miembros-text
  "Para inscribir un nuevo miembro, andá a **Menú → Miembros** y hacé clic en **+ Nuevo Miembro**. Completá: **nombre**, **apellido**, **correo**, **móvil**, **rol** (estudiante, coordinador, delegado, voluntario o instructor), **estado** (member o probacionismo) y seleccioná el **curso** al que se inscribe. Opcionalmente podés agregar **fecha de ingreso**, **biografía** y **foto de perfil**. El miembro aparecerá automáticamente en la lista de su curso y podrá registrarse para crear su propia cuenta vinculada.")

;; ── Eventos y Asistencia ──────────────────────────────────────
(def eventos-title "Eventos y Asistencia")
(def eventos-subtitle "Sesiones de clase y registro de participación")

(def eventos-campos
  [{:title "Campos de Evento"       :icon "file-text"
    :text "**id** UUID · **countryCode** · **cursoId** FK · **titulo** · **descripcion** · **fecha** ISO · **hora** · **tipo** (`clase`, `taller`, `examen`, `conferencia`, `ceremonia`, `actividad`) · **catedraId** FK opcional · **encargadoId** FK opcional · **completado** bool · **notas**."}
   {:title "Asistencia"             :icon "check-square"
    :text "Entidad **Asistencia**: **id** UUID · **countryCode** · **eventoId** FK · **miembroId** FK · **presente** boolean · **fecha** ISO. Un registro por cada miembro por cada evento. Se crea desde la vista de detalle del evento."}
   {:title "Calendario"             :icon "calendar-days"
    :text "Menú → **Calendario**. Mini-calendario mensual con días con actividad resaltados. Lista de próximos eventos y celebraciones (equinoccios, solsticios, aniversarios). Eventos recientes con curso y fecha."}
   {:title "REPL"                   :icon "terminal"
    :text "Backend: `(->> (entities) :eventos (filter #(= (:cursoId %) \"1\")))` — eventos del curso 1. `(->> (entities) :asistencias (filter #(= (:eventoId %) \"ev-1\")) (filter :presente) count)` — presentes en un evento. Frontend: `(db/get-eventos)`, `(db/get-asistencias)`."}])

(def eventos-text
  "Para crear un evento, andá a **Menú → Calendario** y hacé clic en **+ Nuevo Evento**. Completá: **título**, **descripción**, **fecha**, **hora**, **tipo** (clase, taller, examen, conferencia, ceremonia o actividad), seleccioná el **curso** asociado y opcionalmente la **cátedra** y el **encargado**. Para registrar asistencia, abrí el evento y marcá **presente/ausente** para cada miembro del curso. El historial de asistencia se guarda automáticamente.")

;; ── Biblioteca ────────────────────────────────────────────────
(def biblioteca-title "Biblioteca")
(def biblioteca-subtitle "Catálogo de libros con búsqueda por ISBN")

(def biblioteca-campos
  [{:title "Identificación"        :icon "hash"
    :text "**id** UUID (o `lib-timestamp`) · **countryCode** · **codigo** interno (ej: `NAC-100-BAC-001-01`) · **isbn** · **slug** auto-generado del título."}
   {:title "Datos Bibliográficos"  :icon "book"
    :text "**titulo** · **subtitulo** · **autor** · **anio** · **editorial** · **categoria** · **categoriaCDD** Clasificación Dewey · **areaTema** · **formato** · **tipoRecurso** · **etiquetas** · **palabrasClave**."}
   {:title "Ubicación Física"      :icon "map-pin"
    :text "**signatura** topográfica (ej: `NAC-100-BAC-001-01`) · **biblioteca** nombre (ej: `NAC`) · **sala** (ej: `Giordano Bruno`) · **ubicacionFisica** posición en estantería · **disponible** bool · **archivado** bool."}
   {:title "Archivos"              :icon "paperclip"
    :text "**imagenPortada** URL de Google Books · **imagen** alternativa · **archivos** array de adjuntos: `[{:name \"x.pdf\" :url \"...\" :type \"application/pdf\"}]`. Subida vía `POST /api/upload-file`."}])

(def biblioteca-funciones
  [{:title "Búsqueda Cross-Country"  :icon "globe"
    :text "`GET /api/libro/{isbn}` busca en **todos los países**: `{\"book\": {...}, \"locations\": [{\"countryCode\": \"DO\", \"biblioteca\": \"NAC\", \"disponible\": true}, {\"countryCode\": \"MX\", ...}]}`. Fallback: busca por isbn, codigo, slug o id."}
   {:title "Catálogo visual"         :icon "image"
    :text "Menú → **Biblioteca**. 108 libros con portadas. Buscador por título, autor, código o ISBN. Filtros por tema (Filosofía, Literatura, Psicología, etc.). Botón **Importar JSON** para carga masiva."}
   {:title "Importación"             :icon "file-json"
    :text "Admin: botón **Importar JSON** acepta array de objetos libro. REPL: `(import-json! :libro \"DO\" \"data/catalogo_libros.json\")`. Útil para migración inicial o carga masiva de catálogos."}
   {:title "REPL"                    :icon "terminal"
    :text "`(->> (libros) count)` → total. `(->> (libros) (map :categoria) distinct sort)` → categorías. `(->> (libros) (map :biblioteca) frequencies)` → conteo por biblioteca. `(->> (libros) (filter #(= (:isbn %) \"9879191102\")) first)` → buscar por ISBN."}])

(def biblioteca-text
  "Para agregar un libro, andá a **Menú → Biblioteca** y hacé clic en **+ Nuevo Libro**. Completá: **título**, **autor**, **ISBN**, **editorial**, **categoría**, **año** y opcionalmente **signatura topográfica**, **sala** y **ubicación física** en la estantería. Podés agregar una **portada** (imagen) y **archivos adjuntos** (PDFs). Para importación masiva, usá el botón **Importar JSON** con un archivo que contenga un array de objetos libro.")

(def biblioteca-campos-id
  "**id** UUID · **countryCode** · **codigo** interno (ej: `NAC-100-BAC-001-01`) · **isbn** · **slug** auto-generado del título.")

(def biblioteca-campos-biblio
  "**titulo** · **autor** · **anio** · **editorial** · **categoria** · **categoriaCDD** Dewey · **areaTema** · **formato** · **etiquetas** · **palabrasClave**.")

(def biblioteca-campos-ubicacion
  "**signatura** topográfica · **biblioteca** (ej: `NAC`) · **sala** (ej: `Giordano Bruno`) · **ubicacionFisica** posición en estantería · **disponible** · **archivado**.")

(def biblioteca-campos-archivos
  "**imagenPortada** URL de Google Books · **imagen** alternativa · **archivos** adjuntos (PDFs). Subida vía `POST /api/upload-file`.")

;; ── Contribuciones ────────────────────────────────────────────
(def contrib-title "Contribuciones")
(def contrib-subtitle "Pagos, membresías y donaciones")

(def contrib-items
  [{:title "Campos"              :icon "file-text"
    :text "**id** (timestamp-based) · **countryCode** · **miembroId** FK · **tipo** (`membresia`, `diplomado`, `donacion`) · **fecha** YYYY-MM · **monto** opcional · **referenciaId** FK opcional a curso (para diplomado)."}
   {:title "Relaciones"          :icon "git-branch"
    :text "Vinculada a **Miembro** (vía `miembroId`). Opcionalmente a **Curso** (vía `referenciaId` para pagos de diplomado). Colección inicialmente vacía — se crean desde la vista de contribuciones."}
   {:title "Historial por Miembro" :icon "history"
    :text "Desde la ficha del miembro, historial completo de contribuciones con totales acumulados y desglose por período. Útil para seguimiento de membresías y pagos."}
   {:title "REPL"                :icon "terminal"
    :text "Frontend: `(db/get-contribuciones)` → todas. `(->> (db/get-contribuciones) (filter #(= (:miembroId %) \"uuid\")) (map :monto) (reduce +))` → total pagado por un miembro. `(->> (db/get-contribuciones) (map :tipo) frequencies)` → conteo por tipo."}])

;; ── Personajes ────────────────────────────────────────────────
(def personajes-title "Personajes")
(def personajes-subtitle "Figuras históricas y filosóficas de referencia")

(def personajes-items
  [{:title "Campos"         :icon "file-text"
    :text "**id** UUID · **countryCode** · **nombre** · **slug** auto-generado · **descripcion** opcional · **aliases** array de nombres alternativos · **imagen** base64 · **imagenes** galería."}
   {:title "Semilla"        :icon "star"
    :text "5 personajes iniciales: **Pitágoras** (matemático y filósofo), **Hipatia** (filósofa y matemática de Alejandría), **Platón** (fundador de la Academia), **Ágora** (espacio de diálogo), **Giordano Bruno** (filósofo del Renacimiento)."}
   {:title "Uso"            :icon "link"
    :text "Las **Salas** llevan nombres de estos personajes (Sala Hipatia, Sala Platón, etc.), conectando el espacio físico con la tradición filosófica. Referencia cultural para estudiantes y visitantes."}
   {:title "REPL"           :icon "terminal"
    :text "Backend: `(->> (entities) :personajes (map :nombre))` → lista de nombres. Frontend: `(db/get-personajes)`. `(db/set-personajes! (conj (db/get-personajes) {:id \"p6\" :nombre \"Sócrates\" :countryCode \"DO\"}))` → agregar personaje."}])

(def contrib-text
  "Las contribuciones se registran desde la **ficha del miembro** o desde **Menú → Contribuciones**. Hacé clic en **+ Nueva Contribución** y completá: seleccioná el **miembro**, el **tipo** (membresía, diplomado o donación), el **período** (mes/año) y opcionalmente el **monto**. Para pagos de diplomado, también seleccioná el **curso** asociado. El historial de contribuciones se muestra como timeline en el perfil del miembro.")

;; ── Administración ────────────────────────────────────────────
(def admin-title "Administración y Usuarios")
(def admin-subtitle "Gestión de cuentas, roles y sincronización")

(def admin-items
  [{:title "Entidad Usuario"      :icon "file-text"
    :text "Campos: **id** UUID · **countryCode** · **email** (único) · **password** (bcrypt+sha512) · **name** · **role** (`admin` o `member`) · **source** · **miembroId** FK opcional. No se almacena en `entities` — tabla propia."}
   {:title "Usuarios por Defecto" :icon "users"
    :text "Creados con `clj -M:setup-admins`: `eva@acropolis.org`, `gabriel@acropolis.com`, `admin@acropolis.com`, `gmolina@gmail.com`. Todos con password `filosofia` y rol `admin`. País `DO`."}
   {:title "Gestión (Admin)"      :icon "settings"
    :text "Endpoints admin: `GET /api/users` listar · `POST /api/users/add` crear · `POST /api/users/delete` eliminar · `POST /api/users/set-role` cambiar rol · `POST /api/users/set-miembro` vincular a miembro/instructor."}
   {:title "Promover a Admin"     :icon "arrow-up"
    :text "`POST /api/auth/promote-admin` con password secreto (variable de entorno `ADMIN_PROMOTE_SECRET`). Permite auto-promoción sin depender de otro admin."}])

(def admin-sync
  [{:title "Sincronizar (↓)"    :icon "download"
    :text "Botón de sincronización: ejecuta `db/sync-with-api!`. Descarga `GET /api/entities?country=XX` y mezcla los datos del servidor en el átomo local. No se ejecuta automáticamente al cargar."}
   {:title "Guardar (↑)"        :icon "upload"
    :text "Ejecuta `db/save-to-api!`. Envía `POST /api/entities` con todo el estado. Upsert masivo: datos nuevos se agregan, existentes se actualizan, ausentes se preservan en el servidor."}
   {:title "Omnibar"            :icon "terminal"
    :text "`Ctrl+G` abre la paleta de comandos. Escribí el nombre de una sección para navegar. Comandos disponibles: `go N` (ir a sección), `pages` (listar), `reset`, `zoom N`, `home`. Acepta formas Clojure: `(go 2)`."}])

;; ── Multi-País ────────────────────────────────────────────────
(def multipais-title "Operación Multi-País")
(def multipais-subtitle "Aislamiento de datos y soporte internacional")

(def multipais-items
  [{:title "Selector de País"       :icon "globe"
    :text "Ruta `/` muestra banderas de 50+ países (Albania a Venezuela). Cada país es una base de datos aislada: sedes, cursos, miembros y biblioteca independientes. Combobox para seleccionar."}
   {:title "Aislamiento por country_code" :icon "hash"
    :text "Cada entidad lleva `countryCode` (ISO 3166-1 alfa-2: `DO`, `MX`, etc.). Todas las consultas SQL filtran: `WHERE country_code = ?`. Frontend muestra un país a la vez con `db/current-country`."}
   {:title "Rutas con País"         :icon "route"
    :text "Patrón: `/{countryCode}/{seccion}`. Ejemplos: `/do/sedes`, `/do/cursos`, `/do/miembros`, `/do/biblioteca`, `/do/calendario`, `/do/catedras`, `/do/asignaciones`. Gestionado con `window.history.pushState`."}
   {:title "ISBN Cross-Country"     :icon "search"
    :text "Única excepción al aislamiento: `GET /api/libro/{isbn}` consulta todos los países. La ruta `/libro/{isbn}` es global (sin prefijo de país). El mismo ISBN puede existir en DO y MX con diferentes ubicaciones."}])

;; ── Código — Entidades REPL ────────────────────────────
(def code-entidades-sedes
  [";; ── Sedes, Salas, Instructores ──────────────"
   ""
   {:text "(sedes)                        ;; todas las sedes de DO" :hl true}
   "(sedes \"MX\")                  ;; sedes de México"
   "(first (sedes))                ;; ver estructura de una sede"
   ""
   ";; Salas"
   "(->> (entities) :salas"
   {:text "     (map #(select-keys % [:nombre :ciudad])))" :hl true}
   ""
   ";; Instructores"
   "(->> (instructores) count)     ;; → 14"
   "(->> (instructores)"
   "     (filter #(not-empty (:miembroId %))))"
   "     ;; → instructores vinculados a miembro"])

(def code-entidades-cursos
  [";; ── Cursos, Asignaciones, Cátedras ──────────"
   ""
   ";; Resumen de cursos"
   "(->> (entities) :cursos"
   {:text "     (map #(select-keys % [:nombre :dia :hora])))" :hl true}
   ""
   ";; Cursos del martes"
   "(->> (entities) :cursos"
   "     (filter #(= (:dia %) \"Martes\")))"
   ""
   ";; Cátedras por nivel"
   "(->> (entities) :catedras"
   {:text "     (group-by :nivel)" :hl true}
   "     (map (fn [[n cs]] [n (count cs)])))"
   ";; → ([\"Primer\" 4] [\"Segundo\" 11] ...)"
   ""
   ";; Siglas de cátedras"
   "(->> (entities) :catedras (map :sigla) sort)"
   ";; → (\"ALQU-702\" \"ANTG-503\" \"ASTL-701\" ...)"
   ""
   ";; Asignaciones de un curso"
   "(->> (entities) :asignaciones"
   "     (filter #(= (:cursoId %) \"1\")))"
   ""
   ";; Clases en una sala"
   "(filterv #(= (:salaId %) \"1\")"
   "  (db/get-asignaciones))"])

(def code-entidades-miembros
  [";; ── Miembros ────────────────────────────────"
   ""
   {:text "(->> (miembros) count)              ;; → 33" :hl true}
   ""
   ";; Conteo por rol"
   {:text "(->> (miembros) (map :role) frequencies)" :hl true}
   ";; → {\"estudiante\" 30, \"instructor\" 2, \"coordinador\" 1}"
   ""
   ";; Miembros de un curso"
   "(->> (miembros)"
   "     (filter #(= (:cursoId %) \"1\")))"
   ""
   ";; En probacionismo"
   "(->> (miembros)"
   "     (filter #(= (:status %) \"probacionismo\")))"
   ""
   ";; Miembro vinculado al usuario actual (frontend)"
   "(db/get-miembro-for-user)"
   ""
   ";; Eventos y asistencia"
   "(->> (entities) :eventos"
   "     (filter #(= (:tipo %) \"clase\")) count)"
   "(->> (entities) :eventos (sort-by :fecha) last)"
   ""
   ";; Asistencias de un evento"
   "(->> (entities) :asistencias"
   {:text "     (filter #(= (:eventoId %) \"e1\"))" :hl true}
   "     (filter :presente) count)"])

;; ── Código — Biblioteca ───────────────────────────────
(def code-biblioteca-consultas
  [";; ── Biblioteca — Consultas REPL ─────────────"
   ""
   {:text "(libros)                          ;; todos los libros de DO" :hl true}
   "(->> (libros) count)              ;; → 108"
   ""
   ";; Ver estructura de un libro"
   "(keys (first (libros)))"
   ";; → (:id :titulo :autor :isbn :categoria ...)"
   ""
   ";; TODOS los campos que existen"
   {:text "(->> (libros) (mapcat keys) distinct sort)" :hl true}
   ""
   ";; Categorías existentes"
   "(->> (libros) (map :categoria) distinct sort)"
   ";; → (\"Autoayuda\" \"Ciencias Sociales\" \"Filosofía\" ...)"
   ""
   ";; Conteo por biblioteca"
   "(->> (libros) (map :biblioteca) frequencies)"
   ";; → {\"NAC\" 108}"
   ""
   ";; Buscar por ISBN"
   "(->> (libros)"
   {:text "     (filter #(= (:isbn %) \"9879191102\"))" :hl true}
   "     first)"
   ""
   ";; Buscar por autor"
   "(->> (libros)"
   "     (filter #(clojure.string/includes?"
   "               (or (:autor %) \"\") \"Coelho\")))"])

(def code-biblioteca-importar
  [";; ── Importación y Modificación ──────────────"
   ""
   ";; Importar catálogo desde archivo JSON"
   {:text "(import-json! :libro \"DO\" \"data/catalogo_libros.json\")" :hl true}
   ";; => \"Imported 42 libro(s) for DO\""
   ""
   ";; Upsert un libro"
   "(upsert! :libro \"DO\""
   "  {:titulo \"La República\""
   "   :autor \"Platón\""
   "   :isbn \"978-0-14-044914-3\""
   "   :categoria \"Filosofía\""
   {:text "   :editorial \"Gredos\"})" :hl true}
   ""
   ";; SQL directo — campos JSONB de libros"
   "(query"
   "  \"SELECT DISTINCT jsonb_object_keys(attrs) AS k"
   "   FROM entities WHERE type='libro' ORDER BY k\")"
   ""
   ";; SQL — buscar libro por ISBN"
   "(query"
   "  \"SELECT id, attrs->>'titulo' as titulo"
   "   FROM entities"
   "   WHERE type='libro'"
   {:text "   AND attrs->>'isbn' = ?\" \"9879191102\")" :hl true}])

;; ── Código — SQL y Esquema ────────────────────────────
(def code-sql-esquema
  [";; ── Esquema PostgreSQL ──────────────────────"
   ""
   {:text "CREATE TABLE IF NOT EXISTS entities (" :hl true}
   "  id            UUID PRIMARY KEY"
   "                DEFAULT gen_random_uuid(),"
   "  type          VARCHAR(64) NOT NULL,"
   "  country_code  VARCHAR(8)  NOT NULL,"
   "  attrs         JSONB NOT NULL DEFAULT '{}',"
   "  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),"
   {:text "  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now());" :hl true}
   ""
   "CREATE INDEX idx_entities_type_country"
   "  ON entities (type, country_code);"
   "CREATE INDEX idx_entities_attrs"
   "  ON entities USING gin (attrs);"
   ""
   {:text "CREATE TABLE IF NOT EXISTS links (" :hl true}
   "  id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),"
   "  from_id   UUID NOT NULL REFERENCES entities(id)"
   "            ON DELETE CASCADE,"
   "  to_id     UUID NOT NULL REFERENCES entities(id)"
   "            ON DELETE CASCADE,"
   "  link_type VARCHAR(64) NOT NULL,"
   "  attrs     JSONB NOT NULL DEFAULT '{}',"
   {:text "  UNIQUE (from_id, to_id, link_type));" :hl true}])

(def code-sql-consultas
  [";; ── Consultas SQL útiles ────────────────────"
   ""
   ";; Contar entidades por tipo"
   {:text "SELECT type, count(*) FROM entities" :hl true}
   {:text "  WHERE country_code='DO'" :hl true}
   {:text "  GROUP BY type ORDER BY count DESC;" :hl true}
   ""
   ";; Contar por tipo y país"
   "SELECT type, country_code, count(*)"
   "  FROM entities"
   "  GROUP BY type, country_code ORDER BY type;"
   ""
   ";; Ver todos los campos JSONB de un tipo"
   {:text "SELECT DISTINCT jsonb_object_keys(attrs) AS campo" :hl true}
   {:text "  FROM entities WHERE type = 'miembro' ORDER BY campo;" :hl true}
   ""
   ";; Buscar miembro por correo"
   "SELECT id, attrs->>'nombre', attrs->>'correo'"
   "  FROM entities"
   "  WHERE type='miembro'"
   "  AND attrs->>'correo' LIKE '%@gmail%';"
   ""
   ";; Entidades creadas hoy"
   "SELECT type, id, attrs->>'nombre' as nombre"
   "  FROM entities"
   "  WHERE created_at::date = CURRENT_DATE;"
   ""
   ";; Últimas 10 modificaciones"
   "SELECT type, attrs->>'nombre', updated_at"
   "  FROM entities"
   "  WHERE country_code='DO'"
   "  ORDER BY updated_at DESC LIMIT 10;"
   ""
   ";; Patrón UPSERT"
   {:text "INSERT INTO entities (id, type, country_code, attrs)" :hl true}
   {:text "VALUES (?, ?, ?, ?::jsonb)" :hl true}
   {:text "ON CONFLICT (id) DO UPDATE SET" :hl true}
   {:text "  attrs = entities.attrs || EXCLUDED.attrs," :hl true}
   {:text "  updated_at = now();" :hl true}])

;; ── Código — API REST (JSON) ──────────────────────────
(def code-api-login
  [";; ── POST /api/auth/login ────────────────────"
   ""
   ";; Request:"
   {:text "{ \"email\": \"eva@acropolis.org\"," :hl true}
   {:text "  \"password\": \"filosofia\" }" :hl true}
   ""
   ";; Response:"
   "{ \"user\": {"
   "    \"id\": \"uuid-1234\","
   "    \"email\": \"eva@acropolis.org\","
   "    \"name\": \"Eva Rodriguez\","
   {:text "    \"role\": \"admin\"," :hl true}
   "    \"countryCode\": \"DO\""
   "  },"
   {:text "  \"token\": \"eyJhbGciOiJIUzI1NiJ9...\" }" :hl true}])

(def code-api-entities
  [";; ── GET /api/entities?country=DO ────────────"
   ""
   ";; Response — todas las entidades agrupadas:"
   "{"
   {:text "  \"sedes\":        [{\"id\":\"1\", \"nombre\":\"Los Prados\", ...}]," :hl true}
   "  \"catedras\":     [{\"id\":\"ETIC-101\", \"nombre\":\"Ética\", ...}],"
   "  \"instructores\": [{\"id\":\"1\", \"nombre\":\"Sally\", ...}],"
   "  \"salas\":        [{\"id\":\"1\", \"nombre\":\"Ágora\", ...}],"
   {:text "  \"cursos\":       [{\"id\":\"1\", \"nombre\":\"Miembros Viernes\", ...}]," :hl true}
   "  \"asignaciones\": [{\"cursoId\":\"1\", \"salaId\":\"2\", ...}],"
   "  \"eventos\":      [{\"titulo\":\"Clase 04\", \"fecha\":\"...\"}],"
   {:text "  \"miembros\":     [{\"nombre\":\"Alvin\", \"role\":\"estudiante\", ...}]," :hl true}
   "  \"asistencias\":  [...],"
   "  \"contribuciones\": [],"
   "  \"personajes\":   [{\"nombre\":\"Pitágoras\", ...}],"
   "  \"libros\":       [{\"titulo\":\"El Caballero...\", ...}]"
   "}"
   ""
   ";; ── POST /api/entities ─────────────────────"
   ";; Requiere: Authorization: Bearer <JWT>"
   ";; Body: mismo formato + \"country\": \"DO\""
   ";; Response:"
   {:text "{ \"ok\": true, \"country\": \"DO\" }" :hl true}])

(def code-api-libro
  [";; ── GET /api/libro/{isbn} ───────────────────"
   ";; Búsqueda cross-country (todos los países)"
   ""
   ";; Response:"
   "{"
   "  \"book\": {"
   "    \"titulo\": \"Ningún lugar está lejos\","
   "    \"autor\": \"Richard Bach\","
   {:text "    \"isbn\": \"9879191102\"" :hl true}
   "  },"
   "  \"locations\": ["
   {:text "    { \"countryCode\": \"DO\"," :hl true}
   {:text "      \"biblioteca\": \"NAC\"," :hl true}
   {:text "      \"disponible\": true," :hl true}
   {:text "      \"ubicacionFisica\": \"0.0\" }," :hl true}
   "    { \"countryCode\": \"MX\","
   "      \"biblioteca\": \"NAC-MX\","
   "      \"disponible\": false }"
   "  ]"
   "}"
   ""
   ";; ── POST /api/upload-file ──────────────────"
   ";; Request:"
   "{ \"base64\": \"data:application/pdf;base64,...\","
   "  \"entityType\": \"libro\","
   "  \"entityId\": \"uuid\","
   "  \"filename\": \"manual.pdf\" }"
   ";; Response:"
   {:text "{ \"url\": \"/uploads/libro/uuid/manual.pdf\" }" :hl true}])

;; ── OmniREPL ──────────────────────────────────────────
(def omnirepl-title "OmniREPL — Paleta de Comandos")
(def omnirepl-subtitle "Navegación, acciones y código Clojure desde Ctrl+G")

(def omnirepl-intro
  ["La **OmniREPL** es una paleta de comandos integrada en Harmonia. Abrila con `Ctrl+G` (o `Cmd+G` en macOS). Permite navegar secciones, ejecutar acciones administrativas y evaluar expresiones **Clojure** en tiempo real usando **SCI** (Small Clojure Interpreter)."
   "Todo lo que escribís con paréntesis se evalúa como código Clojure con acceso completo al estado de la aplicación. Sin paréntesis, busca secciones para navegar."])

(def omnirepl-items
  [{:title "Abrir y Cerrar"        :icon "keyboard"
    :text "`Ctrl+G` abre la paleta. `Escape` o `Ctrl+G` de nuevo la cierra. Mientras está abierta, escribí para filtrar secciones o ejecutar comandos."}
   {:title "Navegación"            :icon "compass"
    :text "Escribí el nombre de una sección y pulsá `Enter` para navegar: **sedes**, **cursos**, **miembros**, **biblioteca**, **catedras**, **asignaciones**, **calendario**, **instructores**, **usuarios**. Las flechas `↑`/`↓` seleccionan entre resultados."}
   {:title "Expresiones Lisp"      :icon "braces"
    :text "Cualquier texto con paréntesis se evalúa como Clojure: `(navigate :biblioteca)`, `(count (miembros))`, `(filter #(= (:role %) \"coordinador\") (miembros))`. El resultado aparece en la consola del navegador."}
   {:title "Autocompletado"        :icon "text-cursor-input"
    :text "`Tab` autocompleta comandos y cicla entre argumentos. Por ejemplo, escribí `nav` + `Tab` → `(navigate ...)`, luego `Tab` para rotar entre `:sedes`, `:cursos`, `:miembros`, etc."}])

(def omnirepl-comandos
  [{:title "(navigate :seccion)"       :icon "compass"
    :text "Navega a una sección. Valores: `:dashboard`, `:sedes`, `:encargados`, `:cursos`, `:catedras`, `:instructores`, `:miembros`, `:salas`, `:personajes`, `:biblioteca`, `:asignaciones`, `:usuarios`, `:countries`."}
   {:title "(action :accion)"          :icon "zap"
    :text "Ejecuta una acción contextual. Valores: `:agregar-miembro-al-grupo`, `:nuevo-miembro`, `:agregar-evento-al-grupo`, `:nueva-sede`, `:nuevo-instructor`, `:nueva-catedra`."}
   {:title "(crear-evento ...)"        :icon "calendar-plus"
    :text "`(crear-evento \"cursoId\" \"2025-03-20\" \"Clase de Ética\" \"clase\")` — crea un evento directamente. Tipos: `clase`, `taller`, `examen`, `conferencia`, `ceremonia`, `actividad`."}
   {:title "(crear-libro ...)"         :icon "book-plus"
    :text "`(crear-libro \"La República\" {:autor \"Platón\" :categoria \"Filosofía\"})` — agrega un libro al catálogo con los metadatos proporcionados."}
   {:title "(crear-miembro ...)"       :icon "user-plus"
    :text "`(crear-miembro {:nombre \"Ana\" :apellido \"López\" :role \"estudiante\" :cursoId \"1\"})` — crea un miembro y lo inscribe al curso indicado."}
   {:title "Clojure libre"            :icon "terminal"
    :text "Cualquier expresión válida: `(+ 1 2)`, `(->> (miembros) (map :nombre) sort)`, `(merge {:a 1} {:b 2})`. Funciones disponibles: `map`, `filter`, `reduce`, `count`, `merge`, `assoc`, `get`, etc."}])

;; Code examples for OmniREPL page
(def omnirepl-code-nav
  [";; ── Navegación desde la OmniREPL ──────────────"
   ""
   {:text "(navigate :dashboard)        ;; Ir al panel principal" :hl true}
   "(navigate :sedes)            ;; Vista de sedes"
   "(navigate :cursos)           ;; Vista de grupos"
   "(navigate :miembros)         ;; Lista de miembros"
   "(navigate :biblioteca)       ;; Catálogo de libros"
   "(navigate :catedras)         ;; Materias por nivel"
   "(navigate :asignaciones)     ;; Horarios"
   "(navigate :instructores)     ;; Equipo docente"
   "(navigate :calendario)       ;; Eventos y calendario"
   "(navigate :countries)        ;; Selector de país"])

(def omnirepl-code-acciones
  [";; ── Acciones contextuales ────────────────────"
   ""
   {:text "(action :nuevo-miembro)                 ;; Abrir formulario" :hl true}
   "(action :agregar-miembro-al-grupo)      ;; Agregar al curso actual"
   "(action :agregar-evento-al-grupo)       ;; Nuevo evento en curso"
   ""
   ";; ── Crear entidades directamente ─────────────"
   ""
   {:text "(crear-evento \"1\" \"2025-03-20\" \"Clase de Ética\" \"clase\")" :hl true}
   ""
   "(crear-libro \"La República\""
   "  {:autor \"Platón\""
   "   :categoria \"Filosofía\""
   "   :editorial \"Gredos\"})"
   ""
   "(crear-miembro"
   "  {:nombre \"Ana\""
   "   :apellido \"López\""
   "   :role \"estudiante\""
   "   :cursoId \"1\""
   {:text "   :correo \"ana@ejemplo.com\"})" :hl true}])

(def omnirepl-code-consultas
  [";; ── Consultas en la OmniREPL ─────────────────"
   ""
   {:text "(count (miembros))                ;; → 33" :hl true}
   "(count (cursos))                  ;; → 2"
   "(count (libros))                  ;; → 108"
   ""
   ";; Filtrar miembros por rol"
   {:text "(->> (miembros) (map :role) frequencies)" :hl true}
   ";; → {\"estudiante\" 30, \"instructor\" 2, ...}"
   ""
   ";; Buscar un miembro por nombre"
   "(->> (miembros)"
   "     (filter #(= (:nombre %) \"Gabriel\"))"
   "     first)"
   ""
   ";; Listar cursos con día y hora"
   "(->> (cursos)"
   {:text "     (map #(select-keys % [:nombre :dia :hora])))" :hl true}
   ""
   ";; Categorías de libros más populares"
   "(->> (libros)"
   "     (map :categoria)"
   "     frequencies"
   "     (sort-by val >))"])

;; ── REPL Frontend ─────────────────────────────────────────────
(def repl-title "REPL — Código en Vivo")
(def repl-subtitle "ClojureScript: cómo el código modifica la UI en tiempo real")

(def repl-intro
  ["Harmonia está construido con **ClojureScript + Reagent**: toda la UI deriva de un único **átomo de estado**. Cambiar ese átomo desde el REPL del navegador actualiza la interfaz **al instante**, sin recargar la página."
   "Conectá el REPL del navegador con `npm run cljs:repl` o abrí la consola del navegador. Cada expresión se evalúa y los componentes Reagent que dependen de los datos modificados se re-renderizan automáticamente."])

(def repl-estado-items
  [{:title "El átomo app-state"     :icon "atom"
    :text "`(defonce app-state (r/atom initial-state))` en `db.cljs`. Un solo `swap!` es suficiente para que Reagent re-renderice. Ejemplo: `(swap! db/app-state assoc :current-country \"MX\")` — cambia el país y toda la vista se actualiza."}
   {:title "Getters y Setters"      :icon "code-2"
    :text "Patrón `get-{tipo}` / `set-{tipo}!` para cada entidad: `(db/get-sedes)` `(db/set-sedes! [...])` · `(db/get-cursos)` `(db/set-cursos! [...])` · `(db/get-miembros)` · `(db/get-libros)` · etc. Auth: `(db/get-user)` `(db/set-user! {...})` `(db/logout!)`."}
   {:title "Persistencia automática" :icon "save"
    :text "Cada mutación del átomo se guarda en `localStorage[\"nuevaAcropolisData\"]`. Imágenes base64 se eliminan antes de guardar (se recargan al sincronizar con API). Versión 2. Al recargar, el estado se restaura exactamente."}])

(def repl-ejemplos-items
  [{:title "Consultar datos"         :icon "search"
    :text "`(count (db/get-miembros))` → 33. `(->> (db/get-miembros) (map :nombre))` → lista de nombres. `(->> (db/get-cursos) (map #(select-keys % [:nombre :dia :hora])))` → resumen de cursos."}
   {:title "Filtrar y agregar"       :icon "filter"
    :text "`(->> (db/get-miembros) (map :role) frequencies)` → `{\"estudiante\" 30, \"instructor\" 2, ...}`. `(->> (db/get-miembros) (filter #(= (:status %) \"probacionismo\")) count)` → miembros en prueba."}
   {:title "Modificar miembro"       :icon "pencil"
    :text "`(db/set-miembros! (mapv #(if (= (:nombre %) \"María\") (assoc % :role \"coordinador\") %) (db/get-miembros)))` — promueve a María a coordinadora. La tabla de miembros se actualiza **al instante**."}
   {:title "Agregar entidad"         :icon "plus"
    :text "`(db/set-sedes! (conj (db/get-sedes) {:id \"nueva\" :nombre \"Sede Norte\" :ciudad \"Puerto Plata\" :tipo \"sub\" :countryCode \"DO\"}))` — aparece una nueva tarjeta en Sedes **inmediatamente**."}
   {:title "Navegar por código"      :icon "compass"
    :text "`(set! js/window.location.hash \"/do/biblioteca\")` → navega a biblioteca. `(db/current-country)` → país actual. Sincronizar: `(db/sync-with-api!)` descarga del servidor. `(db/save-to-api!)` sube al servidor."}
   {:title "Estadísticas al vuelo"   :icon "bar-chart"
    :text "`(->> (db/get-cursos) (group-by :dia) (map (fn [[d cs]] [d (count cs)])))` → cursos por día. `(->> (db/get-libros) (map :categoria) frequencies (sort-by val >))` → categorías más populares de la biblioteca."}])

;; Code examples for REPL Frontend
(def repl-code-estado
  [";; ── El átomo de estado ───────────────────────"
   ";; Todo el estado vive en un único átomo Reagent."
   ";; Modificarlo actualiza la UI al instante."
   ""
   {:text "(defonce app-state (r/atom initial-state))" :hl true}
   ""
   ";; Getters — leer datos"
   "(db/get-sedes)              ;; → [{:id \"1\" :nombre \"Los Prados\" ...} ...]"
   "(db/get-cursos)             ;; → [{:id \"1\" :nombre \"Miembros Viernes\" ...}]"
   "(db/get-miembros)           ;; → [33 miembros]"
   "(db/get-libros)             ;; → [108 libros]"
   "(db/get-catedras)           ;; → [38 cátedras]"
   "(db/get-instructores)       ;; → [14 instructores]"
   "(db/get-asignaciones)       ;; → [7 asignaciones]"
   "(db/get-eventos)            ;; → eventos del país"
   "(db/get-contribuciones)     ;; → contribuciones"
   "(db/current-country)        ;; → \"DO\""
   "(db/get-user)               ;; → {:email \"...\" :role \"admin\" :token \"...\"}"])

(def repl-code-mutaciones
  [";; ── Mutaciones — cambiar datos y ver la UI actualizarse ──"
   ""
   ";; Cambiar el país activo (toda la vista se actualiza)"
   {:text "(swap! db/app-state assoc :current-country \"MX\")" :hl true}
   ""
   ";; Promover un miembro a coordinador"
   "(db/set-miembros!"
   "  (mapv #(if (= (:nombre %) \"María\")"
   "           (assoc % :role \"coordinador\")"
   "           %)"
   {:text "    (db/get-miembros)))" :hl true}
   ""
   ";; Agregar una sede nueva (aparece al instante en la UI)"
   "(db/set-sedes!"
   "  (conj (db/get-sedes)"
   "    {:id \"nueva-1\""
   "     :nombre \"Sede Norte\""
   "     :ciudad \"Puerto Plata\""
   "     :tipo \"sub\""
   {:text "     :countryCode \"DO\"}))" :hl true}
   ""
   ";; Sincronizar con el servidor"
   "(db/sync-with-api!)         ;; ↓ descargar del servidor"
   "(db/save-to-api!)           ;; ↑ subir al servidor"])

;; ── REPL Backend y CLI ────────────────────────────────────────
(def repl-backend-title "REPL Backend y CLI")
(def repl-backend-subtitle "Comandos del servidor y scripts de administración")

(def repl-backend-intro
  ["El backend Clojure ofrece un REPL conectado a PostgreSQL para consultas directas, modificación de datos y administración del sistema. Conectá con `clj -M:repl` desde el directorio `backend/`."
   "Además, hay scripts CLI para tareas comunes: migración de esquema, carga de datos semilla, creación de admins y listado de usuarios."])

(def repl-backend-helpers
  [{:title "Helpers de consulta"    :icon "search"
    :text "`(entities)` → mapa de todas las entidades de DO agrupadas por tipo. `(sedes)` `(cursos)` `(miembros)` `(libros)` `(instructores)` → accesos directos por tipo. `(sedes \"MX\")` → especificar país. `(count-entities)` → conteo total."}
   {:title "Consulta SQL directa"   :icon "database"
    :text "`(query \"SELECT type, count(*) FROM entities GROUP BY type\")` → conteo por tipo. `(query \"SELECT DISTINCT jsonb_object_keys(attrs) FROM entities WHERE type='miembro'\")` → todos los campos de miembros en la BD."}
   {:title "Modificar datos"        :icon "pencil"
    :text "`(upsert! {:id \"nuevo\" :type \"sede\" :country_code \"DO\" :attrs {:nombre \"Sede Norte\" :ciudad \"Puerto Plata\" :tipo \"sub\"}})` → inserta o actualiza directamente en PostgreSQL. El frontend lo verá en la próxima sincronización."}
   {:title "Gestión de enlaces"     :icon "link"
    :text "`(e/query-entities ds :sala \"DO\")` → todas las salas. `(e/insert-entity! ds {:id \"x\" :type \"asignacion\" :country_code \"DO\" :attrs {:cursoId \"1\" :salaId \"2\" :catedraId \"3\" :instructorId \"4\"}})` → crear asignación."}])

(def repl-cli-items
  [{:title "clj -M:run"            :icon "play"
    :text "Inicia el servidor API en puerto 3000. Requiere `DATABASE_URL` configurado. Ejemplo: `DATABASE_URL=\"jdbc:postgresql://localhost:5432/na_sys\" clj -M:run`."}
   {:title "clj -M:migrate"        :icon "database"
    :text "Ejecuta la migración del esquema PostgreSQL: crea tablas `entities` y `links` con índices. Idempotente — seguro de ejecutar múltiples veces."}
   {:title "clj -M:seed"           :icon "sprout"
    :text "Carga datos semilla: 3 sedes, 7 salas, 14 instructores, 38 cátedras, 16 cursos, 75+ miembros, 5 personajes, asignaciones. Para el país DO."}
   {:title "clj -M:setup-admins"   :icon "shield"
    :text "Crea usuarios admin por defecto: `eva@acropolis.org`, `gabriel@acropolis.com`, `admin@acropolis.com`, `gmolina@gmail.com`. Password: `filosofia`. País: DO."}
   {:title "clj -M:list-users"     :icon "list"
    :text "Lista todos los usuarios registrados en la base de datos con id, email, name, role y countryCode. Útil para auditoría."}
   {:title "npm run cljs:dev"      :icon "monitor"
    :text "Inicia Shadow-CLJS en modo desarrollo → puerto 8280. Hot-reload: los cambios en `.cljs` se reflejan sin recargar. `npm run cljs:release` para build de producción. `npm run cljs:repl` para REPL del navegador."}])

;; Code examples for Backend REPL
(def repl-backend-code-helpers
  [";; ── Backend REPL — Helpers de consulta ───────"
   ";; Conectar: clj -M:repl (desde backend/)"
   ""
   {:text "(entities)                  ;; → mapa de TODAS las entidades de DO" :hl true}
   ";; {:sedes [...] :cursos [...] :miembros [...] ...}"
   ""
   "(sedes)                     ;; → [{:id \"1\" :nombre \"Los Prados\" ...}]"
   "(sedes \"MX\")                ;; → sedes de México"
   "(cursos)                    ;; → todos los cursos"
   "(miembros)                  ;; → todos los miembros"
   "(libros)                    ;; → todos los libros"
   "(instructores)              ;; → todos los instructores"
   {:text "(count-entities)             ;; → conteo total" :hl true}])

(def repl-backend-code-sql
  [";; ── Consultas SQL directas ───────────────────"
   ""
   ";; Conteo de entidades por tipo"
   {:text "(query \"SELECT type, count(*) FROM entities" :hl true}
   {:text "        WHERE country_code='DO' GROUP BY type\")" :hl true}
   ""
   ";; Ver todos los campos JSONB de un tipo"
   "(query \"SELECT DISTINCT jsonb_object_keys(attrs)"
   "        FROM entities WHERE type='miembro'\")"
   ""
   ";; Buscar miembro por correo"
   "(query \"SELECT id, attrs->>'nombre', attrs->>'correo'"
   "        FROM entities"
   "        WHERE type='miembro'"
   {:text "        AND attrs->>'correo' LIKE '%@gmail%'\")" :hl true}
   ""
   ";; Últimas 10 entidades modificadas"
   "(query \"SELECT type, attrs->>'nombre', updated_at"
   "        FROM entities"
   "        WHERE country_code='DO'"
   "        ORDER BY updated_at DESC LIMIT 10\")"])

(def repl-backend-code-upsert
  [";; ── Upsert — crear/actualizar entidades ──────"
   ""
   ";; Crear una sede nueva en PostgreSQL"
   {:text "(upsert! {:id \"sede-norte\"" :hl true}
   {:text "          :type \"sede\"" :hl true}
   {:text "          :country_code \"DO\"" :hl true}
   {:text "          :attrs {:nombre \"Sede Norte\"" :hl true}
   {:text "                  :ciudad \"Puerto Plata\"" :hl true}
   {:text "                  :tipo \"sub\"}})" :hl true}
   ""
   ";; Crear una asignación (enlace de 4 vías)"
   "(e/insert-entity! ds"
   "  {:id \"asig-nueva\""
   "   :type \"asignacion\""
   "   :country_code \"DO\""
   "   :attrs {:cursoId \"1\""
   "           :salaId \"2\""
   "           :catedraId \"ETIC-101\""
   "           :instructorId \"3\"}})"
   ""
   ";; ── Scripts CLI ─────────────────────────────"
   ""
   {:text "clj -M:run              # Servidor API (puerto 3000)" :hl true}
   {:text "clj -M:migrate          # Crear tablas entities + links" :hl true}
   {:text "clj -M:seed             # Datos semilla (3 sedes, 75+ miembros...)" :hl true}
   {:text "clj -M:setup-admins     # Crear usuarios admin" :hl true}
   "clj -M:list-users        # Listar usuarios"
   {:text "npm run cljs:dev        # Frontend dev (puerto 8280)" :hl true}
   "npm run cljs:release      # Build producción"
   "npm run cljs:repl         # REPL del navegador"])

;; ── Créditos ──────────────────────────────────────────────────
(def credits-title "Créditos")
(def credits-by "Creado con Greb Docs")
(def credits-orgs "Nueva Acrópolis — Escuelas de Filosofía a la manera clásica")
(def credits-legal "Este manual fue generado como documento de referencia del sistema Harmonia para uso interno de Nueva Acrópolis. Puede ser redistribuido libremente dentro de la organización.")
