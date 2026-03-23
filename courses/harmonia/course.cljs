(ns harmonia.course
  "Course definition for the Nueva Acrópolis Harmonia user manual."
  (:require [harmonia.content :as c]))

(def course
  {:meta  {:id          "harmonia"
           :org         "harmonia"
           :slug        "manual_harmonia"
           :title       "Harmonia — Manual de Usuario"
           :description "Manual de usuario del sistema Harmonia — gestión académica para las escuelas de filosofía de Nueva Acrópolis."
           :category    "Manuales de Software"
           :tags        ["gestión académica" "ClojureScript" "Nueva Acrópolis"]
           :style       {:illustration "oil painting illustration, warm golden light, rich brush strokes, editorial magazine quality, classical academy aesthetic, philosophy and education themes"}
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
          :entries (subvec c/index-entries 0 11)}
         {:label "Gestión Académica"
          :entries (subvec c/index-entries 11 25)}
         {:label "Recursos"
          :entries (subvec c/index-entries 25 29)}
         {:label "Administración"
          :entries (subvec c/index-entries 29 42)}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:hero-img "cover-filosofia.png"
            :title    "Harmonia"
            :subtitle "Nueva Acrópolis · Gestión Académica · Manual de Usuario"}}

    ;; 2. Table of Contents
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Índice detallado
    {:template :index
     :data {:title   "Índice"
            :entries c/index-entries
            :groups  [{:label "Introducción"
                       :items (subvec c/index-entries 3 11)}
                      {:label "Gestión Académica"
                       :items (subvec c/index-entries 11 25)}
                      {:label "Recursos y Administración"
                       :items (subvec c/index-entries 25 31)}
                      {:label "Desarrollo"
                       :items (subvec c/index-entries 31 42)}]}}

    ;; 4. Introducción — drop cap
    {:template :blocks
     :data {:id "introduccion"
            :header {:icon "book-open" :kicker "Nueva Acrópolis" :title "Introducción"}
            :intro c/intro-dropcap
            :blocks [{:type :callout :icon "sparkles" :style :accent
                      :title "Filosofía como sistema"
                      :text "Harmonia organiza el conocimiento como lo haría un filósofo: cada entidad (sede, curso, miembro, libro) es un **nodo** en una red de relaciones. Las salas llevan nombres de filósofos, las cátedras siguen 7 niveles de estudio, y los eventos conectan estudiantes con instructores en un ciclo continuo de aprendizaje."}]}}

    ;; 5. ¿Qué es Harmonia? — hero + feature list
    {:template :hero-section
     :data {:id "que-es"
            :hero {:kicker "Nueva Acrópolis"
                   :title  c/intro-title
                   :subtitle c/intro-lead
                   :hero-img "home-selector.png"}
            :blocks [{:type :text-block :content c/intro-text}
                     {:type :feature-list
                      :items ["Gestión de **sedes** y salas en múltiples ciudades"
                              "**12 tipos** de entidades: cursos, miembros, eventos..."
                              "Aislamiento por **país** con datos independientes"
                              "Interfaz **glass-card** moderna y responsive"
                              "**Omnibar** (Ctrl+G) para acceso rápido"
                              "Sincronización con API REST y PostgreSQL"]}]}}

    ;; 6. Arquitectura — steps + info grid
    {:template :hero-section
     :data {:id "arquitectura"
            :hero {:kicker "Visión Técnica"
                   :title  c/arq-title
                   :subtitle c/arq-subtitle}
            :blocks [{:type :steps
                      :items [{:num "1" :title "Frontend" :icon "monitor"
                               :text "**ClojureScript + Reagent** (React). Estado en un átomo. Shadow-CLJS. Puerto 8280."}
                              {:num "2" :title "Backend" :icon "server"
                               :text "**Clojure + Ring + Compojure**. API REST en puerto 3000. JWT + bcrypt."}
                              {:num "3" :title "Base de Datos" :icon "database"
                               :text "**PostgreSQL + JSONB**. Tabla entities con attrs flexible. Upsert masivo."}
                              {:num "4" :title "Despliegue" :icon "cloud"
                               :text "**Docker multi-stage** a Railway. PostgreSQL + S3 en la nube."}]}]}}

    ;; 7. Esquema BD — callout + info grids
    {:template :blocks
     :data {:id "esquema-bd"
            :header {:icon "database" :kicker "Visión Técnica" :title c/esquema-title}
            :blocks [{:type :callout :icon "table"
                      :title "Una tabla para todo"
                      :text "Todas las entidades (sedes, cursos, miembros, libros...) se almacenan en una **única tabla** `entities` con `type`, `country_code` y un campo **JSONB** flexible para atributos. Las relaciones se expresan como FKs dentro del JSONB o mediante la tabla `links`."}
                     {:type :info-grid :icon "search" :title "Consultas SQL"
                      :items c/esquema-consultas}]}}

    ;; 8. Acceso — steps + callout
    {:template :hero-section
     :data {:id "acceso"
            :hero {:kicker "Seguridad"
                   :title  c/acceso-title
                   :subtitle c/acceso-subtitle}
            :blocks [{:type :image-block :src "home-selector.png" :float :right
                      :caption "Pantalla de seleccion de pais"}
                     {:type :text-block :content c/acceso-text-registro}
                     {:type :steps
                      :items [{:num "1" :title "Seleccionar país" :icon "globe"
                               :text "Elegí tu país en la pantalla de inicio (banderas)."}
                              {:num "2" :title "Registrarse o login" :icon "log-in"
                               :text "Email + contraseña. Auto-registro con rol **member**."}
                              {:num "3" :title "Vinculación automática" :icon "link"
                               :text "Si tu email coincide con un miembro, se vinculan."}]}
                     {:type :callout :icon "shield" :style :accent
                      :title "Tres roles"
                      :text "**admin** (gestión completa) · **instructor** (cursos y miembros) · **member** (consulta personal). Contraseñas con bcrypt+sha512."}]}}

    ;; 9. Perfil — hero + info grid (split from original overflowing page)
    {:template :hero-section
     :data {:id "perfil"
            :hero {:kicker "Tu Cuenta"
                   :title  c/perfil-title
                   :subtitle c/perfil-subtitle}
            :blocks [{:type :text-block :content c/perfil-text}
                     {:type :info-grid :icon "link" :title "Vinculación Usuario → Miembro"
                      :items c/perfil-vinculacion}]}}

    ;; 8. Vista: Dashboard
    {:template :full-image
     :data {:id "img-dashboard"
            :img "home-selector.png"
            :alt "Dashboard de Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Dashboard"
            :subtitle "Panel principal con estadísticas, eventos y distribución académica"}}

    ;; API REST — callout + info grid
    {:template :blocks
     :data {:id "api"
            :header {:icon "cloud" :kicker "Visión Técnica" :title c/api-title}
            :blocks [{:type :callout :icon "server"
                      :title "API RESTful"
                      :text "Todos los datos se acceden vía **HTTP JSON**. `GET /api/entities?country=DO` descarga todo un país. `POST /api/entities` hace upsert masivo. Autenticación con JWT en header `Authorization: Bearer`."}
                     {:type :info-grid :icon "server" :title "Endpoints"
                      :items c/api-entidades}]}}

    ;; Sedes y Salas — hero + steps
    {:template :hero-section
     :data {:id "sedes"
            :hero {:kicker "Gestión Académica"
                   :title  c/sedes-title
                   :subtitle c/sedes-subtitle
                   :hero-img "sedes-art.png"}
            :blocks [{:type :image-block :src "sedes.png" :float :left
                      :caption "Vista de sedes en la app"}
                     {:type :text-block :content c/sedes-text}
                     {:type :highlight :icon "building" :title "Estructura"
                      :items ["Cada sede tiene **nombre**, **ciudad**, **dirección** y un **encargado**"
                              "Las **salas** pertenecen a una sede y llevan nombres filosóficos"
                              "Datos semilla: 3 sedes en Santo Domingo y Santiago"
                              "7 salas: Ágora, Hipatia, Platón, Giordano Bruno, Pitágoras..."]}]}}

    ;; 11. Vista: Sedes
    {:template :full-image
     :data {:id "img-sedes"
            :img "sedes.png"
            :alt "Vista de Sedes en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Sedes"
            :subtitle "Tarjetas glass-card con nombre, ciudad, dirección, encargado y conteo de cursos/salas"}}

    ;; Cátedras — callout + highlight
    {:template :blocks
     :data {:id "catedras"
            :header {:icon "book-open" :kicker "Gestión Académica" :title c/catedras-title}
            :blocks [{:type :text-block :content c/catedras-text}
                     {:type :callout :icon "layers" :style :accent
                      :title "7 Niveles de Estudio"
                      :text "**Primer año**: Diplomado, Ética, Sociopolítica. **Segundo**: Psicología, Filosofía Aplicada, Simbología. **Tercero a Séptimo**: Religiones Comparadas, Cosmogénesis, Astrología, Alquimia, Mayéutica. Total: **38 cátedras** en datos semilla."}
                     {:type :feature-list
                      :items ["Cada cátedra tiene **nombre**, **sigla** y **nivel**"
                              "Siglas como ETIC-101, PSIC-202, HFAN-207"
                              "Referenciadas por **asignaciones** y **eventos**"
                              "REPL: `(->> (entities) :catedras (group-by :nivel))`"]}]}}

    ;; Vista: Cátedras
    {:template :full-image
     :data {:id "img-catedras"
            :img "catedras.png"
            :alt "Vista de Cátedras en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Cátedras"
            :subtitle "Listado de cátedras con asignatura, nivel y ciclo académico"}}

    ;; Cursos — hero + steps
    {:template :hero-section
     :data {:id "cursos"
            :hero {:kicker "Gestión Académica"
                   :title  c/cursos-title
                   :subtitle c/cursos-subtitle
                   :hero-img "cursos-art.png"}
            :blocks [{:type :image-block :src "cursos.png" :float :right
                      :caption "Tarjetas de grupos de estudio"}
                     {:type :text-block :content c/cursos-text}
                     {:type :steps
                      :items [{:num "1" :title "Crear grupo" :icon "plus"
                               :text "Nombre, sede, día y hora. Ej: **Miembros Viernes** 19:00."}
                              {:num "2" :title "Asignar cátedras" :icon "book-open"
                               :text "Menú Asignaciones: curso + sala + cátedra + instructor."}
                              {:num "3" :title "Inscribir miembros" :icon "users"
                               :text "Los miembros se inscriben vía `cursoId` en su ficha."}]}]}}

    ;; Vista: Cursos
    {:template :full-image
     :data {:id "img-cursos"
            :img "cursos.png"
            :alt "Vista de Cursos en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Grupos de Estudio"
            :subtitle "Tarjetas con nombre, sede, día/hora y cátedras asignadas. Filtros por sede y día"}}

    ;; Vista: Asignaciones
    {:template :full-image
     :data {:id "img-asignaciones"
            :img "asignaciones.png"
            :alt "Vista de Asignaciones en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Asignaciones"
            :subtitle "Relación de cátedras asignadas a cada grupo de estudio con instructor y horario"}}

    ;; 15. Instructores
    {:template :hero-section
     :data {:id "instructores"
            :hero {:kicker "Gestión Académica"
                   :title  c/instructores-title
                   :subtitle c/instructores-subtitle
                   :hero-img "instructores.png"}
            :blocks [{:type :text-block :content c/instructores-text}
                     {:type :stat-grid
                      :items [{:icon "users" :label "Instructores" :value "14"}
                              {:icon "book-open" :label "Asignaciones" :value "20+"}
                              {:icon "link" :label "Vinculados" :value "Auto"}]}
                     {:type :highlight :icon "workflow" :title "Flujo de trabajo"
                      :items ["Crear instructor en **Menú → Instructores → + Nuevo**"
                              "Vincular con miembro existente por **coincidencia de email**"
                              "Asignar a cátedras desde **Menú → Asignaciones**"
                              "Consultar carga académica en la **ficha del instructor**"]}
                     {:type :info-grid :icon "graduation-cap" :title "Datos y Relaciones"
                      :items c/instructores-items}]}}

    ;; Vista: Instructores
    {:template :full-image
     :data {:id "img-instructores"
            :img "instructores.png"
            :alt "Vista de Instructores en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Instructores"
            :subtitle "Listado de instructores con nombre, especialidad y cursos asignados"}}

    ;; Miembros — hero + feature list + callout
    {:template :hero-section
     :data {:id "miembros"
            :hero {:kicker "Gestión Académica"
                   :title  c/miembros-title
                   :subtitle c/miembros-subtitle
                   :hero-img "miembros-art.png"}
            :blocks [{:type :image-block :src "miembros.png" :float :left
                      :caption "Lista de miembros"}
                     {:type :text-block :content c/miembros-text}
                     {:type :feature-list
                      :items ["**Nombre** y apellido"
                              "**Correo** y móvil"
                              "**Rol**: estudiante, coordinador, delegado, voluntario, instructor"
                              "**Estado**: member o probacionismo"
                              "**Curso** inscrito (FK)"
                              "**Foto** de perfil y galería"
                              "**Biografía** texto libre"
                              "**Fecha de ingreso**"]}]}}

    ;; 17. Vista: Miembros
    {:template :full-image
     :data {:id "img-miembros"
            :img "miembros.png"
            :alt "Vista de Miembros en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Miembros"
            :subtitle "Stats de totales, búsqueda por nombre, tabla con rol, estado y teléfono"}}

    ;; Eventos — hero + steps + callout
    {:template :hero-section
     :data {:id "eventos"
            :hero {:kicker "Seguimiento"
                   :title  c/eventos-title
                   :subtitle c/eventos-subtitle
                   :hero-img "eventos-art.png"}
            :blocks [{:type :image-block :src "calendario.png" :float :right
                      :caption "Calendario de eventos"}
                     {:type :text-block :content c/eventos-text}
                     {:type :steps
                      :items [{:num "1" :title "Crear evento" :icon "calendar"
                               :text "Título, fecha, hora, tipo (clase/taller/examen), curso asociado."}
                              {:num "2" :title "Registrar asistencia" :icon "check-square"
                               :text "Abrir evento, marcar **presente/ausente** por cada miembro."}
                              {:num "3" :title "Ver historial" :icon "clock"
                               :text "Timeline en el perfil del miembro con todas sus asistencias."}]}]}}

    ;; Vista: Calendario
    {:template :full-image
     :data {:id "img-calendario"
            :img "calendario.png"
            :alt "Vista de Calendario en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Calendario"
            :subtitle "Calendario mensual con eventos, clases y actividades académicas"}}

    ;; Vista: Encargados
    {:template :full-image
     :data {:id "img-encargados"
            :img "encargados.png"
            :alt "Vista de Encargados en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Encargados"
            :subtitle "Gestión de encargados por sede con roles y responsabilidades asignadas"}}

    ;; Biblioteca — hero + stat grid + highlight + campos
    {:template :hero-section
     :data {:id "biblioteca"
            :hero {:kicker "Recursos"
                   :title  c/biblioteca-title
                   :subtitle c/biblioteca-subtitle
                   :hero-img "biblioteca-art.png"}
            :blocks [{:type :image-block :src "biblioteca.png" :float :left
                      :caption "Catalogo de biblioteca"}
                     {:type :text-block :content c/biblioteca-text}
                     {:type :stat-grid
                      :items [{:icon "book" :label "Libros" :value "108"}
                              {:icon "globe" :label "Cross-country" :value "ISBN"}
                              {:icon "filter" :label "Categorias" :value "12+"}
                              {:icon "upload" :label "Import" :value "JSON"}]}
                     {:type :highlight :icon "search" :title "Busqueda"
                      :items ["Buscar por **titulo**, **autor**, **ISBN** o **codigo**"
                              "Filtros por **categoria** (Filosofia, Literatura, Ciencias...)"
                              "Busqueda **cross-country**: un ISBN en todos los paises"
                              "**Portadas** de Google Books automaticas"]}
                     {:type :info-grid :title "Campos del registro" :icon "database"
                      :items [{:title "Identificacion" :icon "hash"
                               :text c/biblioteca-campos-id}
                              {:title "Datos Bibliograficos" :icon "book"
                               :text c/biblioteca-campos-biblio}
                              {:title "Ubicacion Fisica" :icon "map-pin"
                               :text c/biblioteca-campos-ubicacion}
                              {:title "Archivos" :icon "paperclip"
                               :text c/biblioteca-campos-archivos}]}]}}

    ;; 20. Vista: Biblioteca
    {:template :full-image
     :data {:id "img-biblioteca"
            :img "biblioteca.png"
            :alt "Catálogo de Biblioteca en Harmonia"
            :screenshot? true
            :kicker "Captura de Pantalla"
            :title "Biblioteca"
            :subtitle "108 libros con portadas, búsqueda por título/autor/ISBN, filtros por tema"}}

    ;; 21. Contribuciones
    {:template :blocks
     :data {:id "contribuciones"
            :header {:icon "credit-card"
                     :kicker "Recursos"
                     :title  c/contrib-title}
            :blocks [{:type :text-block :content c/contrib-text}
                     {:type :info-grid :icon "file-text" :title "Datos y Relaciones"
                      :items c/contrib-items}]}}

    ;; 16. Personajes
    {:template :blocks
     :data {:id "personajes"
            :header {:icon "star"
                     :kicker "Recursos"
                     :title  c/personajes-title}
            :blocks [{:type :info-grid :icon "book-open" :title "Figuras Filosóficas"
                      :items c/personajes-items}]}}

    ;; Admin — hero + steps
    {:template :hero-section
     :data {:id "administracion"
            :hero {:kicker "Administracion"
                   :title  c/admin-title
                   :subtitle c/admin-subtitle
                   :hero-img "admin-art.png"}
            :blocks [{:type :steps
                      :items [{:num "1" :title "Crear usuario" :icon "user-plus"
                               :text "Admin: **Usuarios > + Agregar**. Email, password, pais, rol."}
                              {:num "2" :title "Asignar rol" :icon "shield"
                               :text "**admin** (todo), **instructor** (cursos) o **member** (consulta)."}
                              {:num "3" :title "Sincronizar" :icon "refresh-cw"
                               :text "Descargar datos del servidor o subir estado local."}]}]}}

    ;; Multi-Pais — callout + feature list
    {:template :blocks
     :data {:id "multi-pais"
            :header {:icon "globe" :kicker "Administracion" :title c/multipais-title}
            :blocks [{:type :callout :icon "globe"
                      :title "Aislamiento por pais"
                      :text "Cada pais opera con datos **independientes**. Al ingresar, seleccionas tu pais y trabajas solo con esa informacion. Patron de rutas: `/{countryCode}/{seccion}`."}
                     {:type :feature-list
                      :items ["**50+ paises** disponibles (Albania a Venezuela)"
                              "Filtro automatico `WHERE country_code = ?`"
                              "Rutas: `/do/sedes`, `/mx/cursos`, `/do/biblioteca`"
                              "Excepcion: busqueda de libros por ISBN es **global**"]}]}}

    ;; 19. Código — Entidades REPL
    {:template :blocks
     :data {:id "code-entidades"
            :header {:icon "terminal"
                     :kicker "Código"
                     :title  "Entidades — Comandos REPL"}
            :blocks [{:type :code-block
                      :caption "Sedes, Salas e Instructores — Backend REPL"
                      :lines c/code-entidades-sedes}
                     {:type :code-block
                      :caption "Cursos, Asignaciones y Cátedras — Backend REPL"
                      :lines c/code-entidades-cursos}
                     {:type :code-block
                      :caption "Miembros, Eventos y Asistencia — Backend REPL"
                      :lines c/code-entidades-miembros}]}}

    ;; 20. Código — Biblioteca y Libros
    {:template :blocks
     :data {:id "code-biblioteca"
            :header {:icon "library"
                     :kicker "Código"
                     :title  "Biblioteca — Comandos REPL"}
            :blocks [{:type :code-block
                      :caption "Consultas de libros — Backend REPL"
                      :lines c/code-biblioteca-consultas}
                     {:type :code-block
                      :caption "Importación y modificación de libros"
                      :lines c/code-biblioteca-importar}]}}

    ;; 21. Código — SQL y Esquema
    {:template :blocks
     :data {:id "code-sql"
            :header {:icon "database"
                     :kicker "Código"
                     :title  "SQL — Esquema y Consultas"}
            :blocks [{:type :code-block
                      :caption "Esquema PostgreSQL — tablas entities y links"
                      :lines c/code-sql-esquema}
                     {:type :code-block
                      :caption "Consultas SQL útiles para administración"
                      :lines c/code-sql-consultas}]}}

    ;; 22. Código — API REST (JSON)
    {:template :blocks
     :data {:id "code-api"
            :header {:icon "cloud"
                     :kicker "Código"
                     :title  "API REST — Peticiones y Respuestas"}
            :blocks [{:type :code-block
                      :caption "POST /api/auth/login — Autenticación"
                      :lines c/code-api-login}
                     {:type :code-block
                      :caption "GET /api/entities — Todas las entidades de un país"
                      :lines c/code-api-entities}
                     {:type :code-block
                      :caption "GET /api/libro/{isbn} — Búsqueda cross-country"
                      :lines c/code-api-libro}]}}

    ;; 23. OmniREPL — Paleta de Comandos
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
