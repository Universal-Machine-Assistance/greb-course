(ns grebdocs.content
  "All content data for the Greb Docs user manual.")

;; ── Index ───────────────────────────────────────────────────────
(def index-title "Índice")

(def index-entries
  [{:id "portada"           :label "Portada"                      :page 1}
   {:id "contenido"         :label "Contenido"                    :page 2}
   {:id "indice"            :label "Índice"                       :page 3}
   {:id "introduccion"      :label "Introducción"                 :page 4  :icon "book-open"}
   {:id "que-es"            :label "¿Qué es Greb Docs?"          :page 5  :icon "layers"}
   {:id "modo-documento"    :label "Modo Documento"               :page 6  :icon "book-open"}
   {:id "modo-presentacion" :label "Modo Presentación"            :page 7  :icon "presentation"}
   {:id "img-doc-vs-pres"   :label "Vista: Doc vs Presentación"   :page 8  :icon "columns"}
   {:id "overlay-mode"      :label "Overlay Mode"                 :page 9  :icon "layers"}
   {:id "img-overlay"       :label "Vista: Overlay"               :page 10 :icon "eye"}
   {:id "zoom-navegacion"   :label "Zoom y Navegación"            :page 11 :icon "zoom-in"}
   {:id "texto-escala"      :label "Texto y Escala"               :page 12 :icon "type"}
   {:id "atajos"            :label "Atajos de Teclado"            :page 13 :icon "keyboard"}
   {:id "omnibar"           :label "Omnibar / OmniREPL"           :page 14 :icon "terminal"}
   {:id "spacemouse"        :label "SpaceMouse"                   :page 15 :icon "gamepad-2"}
   {:id "creacion"          :label "Creación de Documentos"       :page 16 :icon "folder-code"}
   {:id "creditos"          :label "Créditos"                     :page 17 :icon "heart"}])

;; ── TOC sections ────────────────────────────────────────────────
(def contenido-title "Manual de Greb Docs")
(def contenido-subtitle "Contenido")

(def contenido-sections
  [{:id "que-es" :title "Introducción" :img "que-es-lego.png"
    :items [{:label "¿Qué es Greb Docs?"    :ok true}
            {:label "El sistema GREB"        :ok true}
            {:label "Creación automatizada"  :ok true}]}
   {:id "modo-documento" :title "Modos de Visualización" :img "modo-documento-lego.png"
    :items [{:label "Modo Documento"     :ok true}
            {:label "Modo Presentación"  :ok true}
            {:label "Zoom y Navegación"  :ok true}
            {:label "Texto y Escala"     :ok true}]}
   {:id "atajos" :title "Controles" :img "keyboard-lego.png"
    :items [{:label "Atajos de Teclado"   :ok true}
            {:label "Omnibar / OmniREPL"  :ok true}
            {:label "SpaceMouse"          :ok true}]}
   {:id "creacion" :title "Desarrollo" :img "code-lego.png"
    :items [{:label "Estructura de cursos"   :ok true}
            {:label "Templates y bloques"    :ok true}
            {:label "Flujo con Claude Code"  :ok true}]}])

;; ── Intro: ¿Qué es Greb Docs? ─────────────────────────────────
(def intro-title "¿Qué es Greb Docs?")

(def intro-lead
  "Greb Docs es una aplicación web para crear y presentar documentos interactivos — manuales, guías y cursos — con navegación paginada, presentaciones a pantalla completa y controles avanzados de zoom.")

(def intro-blocks
  ["Forma parte del sistema GREB (GREBDev), un ecosistema de herramientas para la creación automatizada de contenido técnico y educativo."
   "Los documentos se definen como datos puros en ClojureScript: un mapa con metadatos, tema visual, tabla de contenido y páginas. Cada página referencia un template y sus datos de contenido."
   "El flujo de trabajo aprovecha Claude Code (el CLI de Anthropic) como chatbot de desarrollo: se describe lo que se necesita en lenguaje natural y el asistente genera el código ClojureScript, los estilos y la estructura del curso."])

(def intro-sistema
  [{:title "ClojureScript" :icon "code-2"
    :text "Los cursos se definen como estructuras de datos inmutables. Sin HTML manual — todo se genera desde mapas y vectores."}
   {:title "Templates"     :icon "layout-template"
    :text "Sistema de plantillas componibles: portada, grillas, bloques de contenido, héroes, créditos y más."}
   {:title "Claude Code"   :icon "bot"
    :text "Flujo de desarrollo asistido por IA: describes el contenido y la estructura, Claude Code genera el código."}
   {:title "Reactivo"      :icon "zap"
    :text "Interfaz reactiva con Reagent (React). Cambios en datos = actualización inmediata de la vista."}])

(def intro-dropcap
  ["Los documentos técnicos tradicionales — PDFs estáticos, Google Docs, Notion — fueron diseñados para leer, no para presentar. Cada vez que un equipo necesita convertir su manual en una presentación, el contenido se duplica, se pierde la sincronización y se desperdician horas en formateo. Greb Docs nace de una pregunta simple: **¿y si el mismo documento pudiera ser ambas cosas?**"
   "A diferencia de PowerPoint o Keynote, Greb Docs no separa el contenido de la estructura. Todo se define como **datos puros en ClojureScript** — mapas, vectores y strings — que el sistema renderiza automáticamente como páginas de libro o diapositivas fullscreen. Cambiás un texto en un solo lugar y se actualiza en ambos modos al instante."
   "Frente a herramientas como Notion o Confluence, Greb Docs ofrece **control total sobre el diseño** sin sacrificar velocidad. Cada documento tiene su propia paleta de colores, tipografía y tema visual. Y gracias a la integración con **Claude Code**, crear un documento completo desde cero es tan rápido como describir lo que necesitás en lenguaje natural."])

(def intro-text
  "Greb Docs transforma la forma en que se crean documentos técnicos. En lugar de escribir HTML o usar editores WYSIWYG, **defines tu contenido como datos** en ClojureScript y el sistema genera automáticamente un documento paginado con diseño editorial profesional. Cada documento incluye dos modos de visualización (lectura y presentación), navegación con atajos de teclado estilo Vim, zoom con física inercial, y una paleta de comandos tipo VS Code.")

;; ── Modo Documento ──────────────────────────────────────────────
(def modo-doc-title "Modo Documento")
(def modo-doc-subtitle "Lectura con scroll paginado y doble página")

(def modo-doc-items
  [{:title "Scroll Paginado"     :icon "book-open"
    :text "Las páginas se organizan en pares (spreads) como un libro abierto. El scroll se ajusta automáticamente a la página más cercana."}
   {:title "Responsive"          :icon "monitor-smartphone"
    :text "En pantallas pequeñas se muestra una sola página. En pantallas anchas, dos páginas lado a lado."}
   {:title "Navegación"          :icon "arrow-left-right"
    :text "Flechas izquierda/derecha o H/L para avanzar y retroceder por spreads. Home/End para ir al inicio o final."}
   {:title "Barra de Secciones"  :icon "list"
    :text "Barra lateral con las secciones del documento. Clic en una sección para saltar directamente a esa página."}])

(def modo-doc-text
  "El modo documento es la vista principal. Al abrir un curso, verás las páginas en formato de **doble página** (como un libro abierto) en escritorio, o **página individual** en móvil. Navegá con las **flechas del teclado**, haciendo clic en los números de página, o con gestos de swipe en táctil. Cada página tiene un ancho fijo de 816px y alto de 1056px — optimizado para impresión A4.")

;; ── Modo Presentación ───────────────────────────────────────────
(def modo-pres-title "Modo Presentación")
(def modo-pres-subtitle "Diapositivas a pantalla completa — mismo contenido, otra lectura")

(def modo-pres-intro
  ["El modo presentación **reutiliza el mismo HTML** del curso: no hay una segunda versión. El visor *analiza cada página*, parte el cuerpo en fragmentos visibles y arma una cola de diapositivas para pantalla grande o aula."
   "Entrá con `P` o escribí `:pres` en la Omnibar (`Ctrl+G`). Salí con `Escape` o `Q`. Usá flechas, `Espacio` o toques en mitades de pantalla; el índice lateral agrupa por página del documento."])

(def modo-pres-logic-items
  [   {:title "De dónde sale cada diapositiva" :icon "layout"
    :text "Para páginas tipo guía se toma el **bloque principal** (.page-body u homólogo); se recorren los hijos *visibles* del DOM y cada candidato puede ser una diapositiva. Se omiten pie de página y decoraciones."}
   {:title "Contenedores anidados" :icon "layers"
    :text "Si un hijo es un «contenedor expansible», se abre y entran sus hijos a la lista plana: así secciones internas pasan a ser diapositivas propias en lugar de quedar ocultas dentro de un solo marco."}
   {:title "Agrupar fragmentos pequeños" :icon "columns-2"
    :text "Varios elementos muy bajos en altura se **agrupan** en una sola diapositiva hasta ~*600px* de altura: evitá láminas con una sola línea y mantené ritmo de exposición."}
   {:title "Páginas con reglas propias" :icon "book-marked"
    :text "Las portadas «full bleed» suelen ir como una diapositiva única. Las páginas de riesgo separan hero y cuerpo; el glosario puede trocear columnas en grupos. El índice de presentación recuerda la página de origen de cada grupo."}])

(def modo-pres-items
  [{:title "Pantalla completa" :icon "maximize"
    :text "La vista ocupa el navegador (**sin barras del lector**); ideal para *proyectar* o formar. Temas y fondos se conservan al clonar cada fragmento."}
   {:title "Progreso e índice" :icon "bar-chart-2"
    :text "Indicador `actual / total` y barra con marcas por sección. Abrí el índice tipo papel, saltá a una página o avanzá de a un corte lógico."}
   {:title "Gestos y teclado" :icon "keyboard"
    :text "`→`, `Espacio` o `L` y tap derecho: **siguiente**. `←` o `H` e *izquierda*: anterior. `Home` / `End`: extremos. También táctil para sala."}
   {:title "Transiciones y continuidad" :icon "sparkles"
    :text "Si el navegador lo permite, las entradas usan *View Transitions* para un pasaje suave desde la página al **modo presentación**."}])

;; ── Zoom y Navegación ───────────────────────────────────────────
(def zoom-title "Zoom y Navegación")
(def zoom-subtitle "Dos tipos de zoom independientes")

(def zoom-intro
  ["Greb Docs separa el **zoom de maquetación** del **zoom de lienzo**: el primero *reescribe* tamaños y flujo del texto; el segundo actúa como una lupa sobre lo ya dibujado, sin recalcular el layout."
   "Los atajos se combinan con la **navegación por spreads** (`←` / `→`, `H` / `L`, *Home* / *End*). Usá `0` para volver el lienzo a su centro; el zoom de maquetación no se pierde."])

(def zoom-highlight-items
  ["`F` acerca y `D` aleja el **zoom layout** (persistente en localStorage)."
   "`Y` y `Shift+Y` controlan el **zoom canvas** (transform CSS, sin reflow)."
   "`H` `J` `K` `L` desplazan el lienzo; mantené `Shift` para pasos más grandes."
   "`0` reinicia canvas y pan; no altera el nivel F/D ni la escala de texto (`T`)."])

(def zoom-items
  [{:title "Zoom Layout (`F` / `D`)"   :icon "layout"
    :text "Modifica el tamaño base del documento recalculando el CSS. **`F`** acerca, **`D`** aleja. Afecta el flujo del texto y la disposición de los elementos. Se guarda en *localStorage*."}
   {:title "Zoom Canvas (`Y` / `Shift+Y`)" :icon "scan"
    :text "Escala visual pura con *CSS transform*: no recalcula el layout, solo amplía o reduce lo visible. Ideal para **inspeccionar** detalle sin romper la rejilla."}
   {:title "Pan (`H` `J` `K` `L`)"         :icon "move"
    :text "Desplazá el lienzo en cualquier dirección: **`H`** izquierda, **`J`** abajo, **`K`** arriba, **`L`** derecha. Con **`Shift`** el movimiento es más rápido."}
   {:title "Reset (`0`)"             :icon "rotate-ccw"
    :text "La tecla **`0`** reinicia *zoom canvas* y *pan* a valores por defecto. El **zoom layout** (`F`/`D`) y la **escala de texto** (`T`) no cambian."}])

(def zoom-nav-items
  [{:title "Spreads y flechas" :icon "arrow-left-right"
    :text "Avanzá o retrocedé con **`→`** / **`←`**, **`L`** / **`H`**, o rueda del mouse alineada al spread más cercano."}
   {:title "Inicio y fin" :icon "skip-forward"
    :text "**`Home`** salta al primer spread; **`End`** al último. Misma lógica en modo documento y al volver desde presentación."}
   {:title "Barra de secciones" :icon "list"
    :text "El *dock* de secciones permite saltar a una página concreta; útil cuando el zoom canvas dejó el contenido fuera de vista."}
   {:title "Zoom + texto" :icon "type"
    :text "**`]`**/**`t`** y **`[`**/**`T`** ajustan la escala tipográfica *aparte* del zoom. Combiná con `F`/`D` o `Y` según necesites leer a distancia."}])

;; ── Texto y Escala ──────────────────────────────────────────────
(def texto-title "Texto y Escala")
(def texto-subtitle "Ajuste independiente del tamaño de texto")

(def texto-intro
  ["La **escala tipográfica** es un canal aparte del zoom: cambiá el tamaño de letra *sin* recalcular toda la maquetación como `F`/`D`, ni la lupa del lienzo (`Y`). Sirve sobre todo para **accesibilidad** y lectura a distancia."
   "Cada curso recuerda tu nivel en *localStorage*: al volver al manual, recuperás la misma escala. Podés combinar texto grande con **zoom layout** bajo o **zoom canvas** alto según el contexto."])

(def texto-highlight-items
  ["**`]`** o **`t`**: un paso más grande; **`[`** o **`T`** (*Shift+t*): un paso más chico — mismos cambios que los corchetes (pasos de **0.02**, p. ej. 100% → 102%)."
   "La insignia flotante muestra **`T 115%`** cuando la escala de texto no es 100% (y el zoom de página si tampoco es 100%)."
   "No modifica imágenes ni rejillas como **`F`**: solo el **cuerpo tipográfico** del `.reader`."
   "El rango va de **0.5×** a **5.0×**. Con **`0`** reseteás *canvas* y *pan*; la escala de texto **sigue**; **`\\`** vuelve el texto a 100%."])

(def texto-items
  [{:title "Teclas (`]` `[` · `t` `T`)" :icon "type"
    :text "**`]`** o **`t`** agrandan el texto; **`[`** o **`T`** (*Shift+t*) lo reducen — mismos pasos que los **corchetes**. Es *independiente* del zoom layout (`F`/`D`) y del zoom canvas (`Y`). La insignia inferior muestra el porcentaje como **`T 120%`** cuando no es 100%."}
   {:title "Persistencia por curso" :icon "save"
    :text "El valor se guarda en *localStorage* con clave por **`meta :id`** del curso. Recargar o abrir más tarde **restaura** tu preferencia en ese manual."}
   {:title "Rango y pasos" :icon "sliders-horizontal"
    :text "De **0.5×** a **5.0×** en pasos de **0.02** por pulsación (`]`/`[`/`t`/`T`)."}
   {:title "Overflow en la página" :icon "maximize-2"
    :text "Al subir mucho la escala, el *page-body* puede necesitar scroll: es esperado. Si tapa contenido, probá **`Y`** para alejar el lienzo **sin** bajar el texto."}])

(def texto-combos-title "Combinaciones útiles")
(def texto-combos
  [{:title "Proyector o monitor lejano" :icon "monitor"
    :text "Subí **`T`** hasta cómodo y, si hace falta, bajá un poco el **zoom layout** (`D`) para que entren dos páginas en el spread."}
   {:title "Vista general del spread" :icon "scan"
    :text "Usá **`Y`** para alejar el *canvas* y ver el conjunto; mantené un **`T`** alto si las etiquetas siguen siendo pequeñas."}
   {:title "Modo presentación" :icon "presentation"
    :text "Al entrar con **`P`** o **`:pres`**, el clon del fragmento **hereda** la escala activa: ensayá el tamaño antes de presentar."}
   {:title "Volver al defaults visual" :icon "rotate-ccw"
    :text "**`0`** limpia pan y zoom *canvas*. Para el texto solo: **`\\`** → 100%, o **`[`** / **`T`** hasta el nivel deseado."}])

;; ── Atajos de Teclado ───────────────────────────────────────────
(def atajos-title "Atajos de Teclado")

(def atajos-doc-title "Modo Documento")
(def atajos-doc
  [{:title "← / H"         :icon "arrow-left"       :text "Spread anterior"}
   {:title "→ / L"          :icon "arrow-right"      :text "Spread siguiente"}
   {:title "Home"           :icon "skip-back"        :text "Primera página"}
   {:title "End"            :icon "skip-forward"     :text "Última página"}
   {:title "F"              :icon "zoom-in"          :text "Zoom layout: acercar"}
   {:title "D"              :icon "zoom-out"         :text "Zoom layout: alejar"}
   {:title "Y"              :icon "scan"             :text "Zoom canvas: acercar"}
   {:title "Shift+Y"        :icon "minimize-2"       :text "Zoom canvas: alejar"}
   {:title "H J K L (pan)"  :icon "move"             :text "Desplazar canvas"}
   {:title "0"              :icon "rotate-ccw"       :text "Reset zoom canvas y pan"}
   {:title "] / [ · t / T"  :icon "type"             :text "Texto +/− (insignia T n%)"}
   {:title "P"              :icon "presentation"     :text "Entrar en modo presentación"}
   {:title "Ctrl+G"         :icon "terminal"         :text "Abrir Omnibar"}])

(def atajos-pres-title "Modo Presentación")
(def atajos-pres
  [{:title "→ / Espacio / L"   :icon "arrow-right"   :text "Siguiente diapositiva"}
   {:title "← / H"             :icon "arrow-left"    :text "Diapositiva anterior"}
   {:title "Escape / Q"        :icon "x"             :text "Salir de presentación"}
   {:title "Home"              :icon "skip-back"      :text "Primera diapositiva"}
   {:title "End"               :icon "skip-forward"   :text "Última diapositiva"}
   {:title "Tap derecha"       :icon "pointer"        :text "Siguiente (táctil)"}
   {:title "Tap izquierda"     :icon "pointer"        :text "Anterior (táctil)"}])

;; ── Omnibar / OmniREPL ─────────────────────────────────────────
(def omnibar-title "Omnibar / OmniREPL")
(def omnibar-subtitle "Ejercicio práctico + referencia de comandos")

(def omnibar-intro
  ["Esta página es un **taller corto**: la barra de abajo se comporta como la omnibar de **`Ctrl+G`**, pero queda **incrustada** para que puedas leer los pasos y probar al mismo tiempo."
   "Objetivos: (1) **filtrar** páginas escribiendo, (2) moverte con **flechas** y confirmar con **Enter**, (3) reconocer **comandos** de texto que el REPL entiende — con o sin paréntesis estilo Clojure."])

(def omnibar-exercise-title "Ejercicio — seguí estos pasos")
(def omnibar-exercise-items
  ["**1.** Enfocá el **campo gris** debajo. Es la barra de práctica (equivalente a abrir `Ctrl+G`)."
   "**2.** Escribí **present** o **texto**: la lista debe **acortarse**. Usá **↓** / **↑** para cambiar la fila resaltada."
   "**3.** Con una **página** seleccionada, pulsá **Enter**: el curso **salta** y aparece un toast **→ …**."
   "**4.** Dejá solo **`pages`** y **Enter**: un toast indica **cuántos spreads** tiene el documento abierto."
   "**5.** Probá **`go 3`** (número de página/spread) o **`go que-es`** (id de página) y **Enter** — navegás **sin** elegir con el mouse."
   "**6.** Escribí **`open `** (con espacio): aparecen **otros cursos** del catálogo; elegí uno o usá **Tab** para autocompletar **alto** cuando el modo lo permita."])

(def omnibar-embed-caption
  "**Campo de práctica** — hacé acá los pasos del ejercicio. También podés abrir **`Ctrl+G`** en paralelo para comparar la misma lista y los mismos atajos.")

(def omnibar-command-ref-title "Comandos que entiende el REPL")
(def omnibar-command-ref
  [{:title "go N · go id"     :icon "arrow-right"
    :text "**`go 1`** salta al spread 1; **`go omnibar`** o **`go que-es`** saltan por **id** de página. También **`(go 2)`**, **`(go \"portada\")`**."}
   {:title "pages"           :icon "hash"
    :text "**`pages`** — toast con la **cantidad de spreads** del curso actual."}
   {:title "reset"           :icon "rotate-ccw"
    :text "**`reset`** o **`(reset)`** — zoom y pan por defecto (como en el visor)."}
   {:title "zoom N"          :icon "zoom-in"
    :text "**`zoom 1.2`** / **`(zoom 1.2)`** — fija el **zoom de maquetación**."}
   {:title "home"            :icon "house"
    :text "**`home`** / **`(home)`** — vuelve al **catálogo** de cursos."}
   {:title "open id"         :icon "folder-open"
    :text "**`open grebdocs`** u otro **id** — abre ese curso (misma ventana). **`(open id)`** también."}
   {:title "edit"            :icon "pencil"
    :text "**`edit`** / **`(edit)`** — editor de página si está **configurado**."}
   {:title "ask …"           :icon "message-circle"
    :text "**`ask …`** o **`(ask \"…\")`** — pregunta rápida a **Claude** (toast; requiere API)."}])

(def omnibar-items-title "Ideas del diseño")
(def omnibar-items
  [{:title "Ctrl+G"          :icon "terminal"
    :text "Atajo global: misma paleta en **modo documento** y en **presentación** (donde aplique)."}
   {:title "Dos modos"       :icon "columns-2"
    :text "**Lista de páginas** cuando buscás texto; modo **cursos** tras **`open `**; sugerencias de **comando** cuando escribís `go`, `reset`, etc."}
   {:title "Enter vs clic"   :icon "mouse-pointer-2"
    :text "**Enter** confirma la fila **seleccionada**; si el texto es un **comando** completo, se **evalúa** y la lista no hace falta."}
   {:title "Origen REPL"     :icon "code-2"
    :text "Formas **`(comando …)`** pensadas para quien ya usa **Clojure**; las variantes **sin paréntesis** agilizan el uso diario."}])

;; ── SpaceMouse ──────────────────────────────────────────────────
(def spacemouse-title "SpaceMouse")
(def spacemouse-subtitle "Soporte para dispositivos 3Dconnexion")

(def spacemouse-items
  [{:title "WebHID"            :icon "usb"
    :text "Greb Docs se conecta directamente al SpaceMouse via la API WebHID del navegador — sin drivers adicionales."}
   {:title "Navegación 3D"     :icon "move-3d"
    :text "Inclina el knob para hacer pan, gira para hacer zoom. Control fluido y natural para explorar documentos grandes."}
   {:title "3DxWare"           :icon "alert-triangle"
    :text "Importante: el driver 3DxWare de 3Dconnexion bloquea el acceso WebHID. Debe estar desactivado o desinstalado para que funcione."}
   {:title "Compatibilidad"   :icon "check-circle"
    :text "Probado con SpaceMouse Compact y SpaceMouse Enterprise. Requiere Chrome o Edge (navegadores con soporte WebHID)."}])

;; ── Creación de Documentos ──────────────────────────────────────
(def creacion-title "Creación de Documentos")
(def creacion-subtitle "Cómo se construyen los cursos")

(def creacion-estructura
  [{:title "courses/mi-curso/"       :icon "folder"
    :text "Cada curso vive en su propia carpeta bajo courses/. Contiene course.cljs, content.cljs y una carpeta images/."}
   {:title "content.cljs"            :icon "file-text"
    :text "Define todo el texto del curso: títulos, párrafos, listas, tablas. Datos puros — sin lógica de presentación."}
   {:title "course.cljs"             :icon "file-code"
    :text "El mapa principal del curso: :meta (id, org, slug), :theme (colores, fuentes), :toc (secciones) y :pages (array de templates + datos)."}
   {:title "app.cljs"                :icon "plug"
    :text "Punto de entrada. Se agrega el nuevo curso al vector de cursos y se registra con require. El catálogo lo detecta automáticamente."}])

(def creacion-templates
  [{:title ":cover"            :icon "image"
    :text "Portada con logo, título y subtítulo sobre imagen de fondo."}
   {:title ":toc-card-grid"    :icon "layout-grid"
    :text "Grilla de tarjetas para la tabla de contenido, con iconos y checklists."}
   {:title ":intro"            :icon "file-text"
    :text "Página introductoria con estadística destacada, imágenes y texto."}
   {:title ":hero-section"     :icon "image"
    :text "Héroe con título/métrica + bloques de contenido debajo."}
   {:title ":blocks"           :icon "layout-list"
    :text "Template flexible: header opcional + bloques de contenido (info-grid, stat-grid, timeline, etc)."}
   {:title ":credits"          :icon "award"
    :text "Página de créditos con logos, organizaciones y aviso legal."}])

(def creacion-flujo
  [{:title "1. Describir"   :icon "message-square"
    :text "Describes en lenguaje natural qué contenido necesitas: secciones, texto, estructura visual."}
   {:title "2. Generar"     :icon "bot"
    :text "Claude Code genera content.cljs y course.cljs siguiendo los patrones existentes del proyecto."}
   {:title "3. Compilar"    :icon "play"
    :text "shadow-cljs compila el proyecto. Hot-reload muestra los cambios al instante en el navegador."}
   {:title "4. Iterar"      :icon "repeat"
    :text "Revisas el resultado, pides ajustes, y Claude Code modifica el código. Ciclo rápido de retroalimentación."}])

;; ── Créditos ────────────────────────────────────────────────────
(def credits-title "Créditos")
(def credits-by "Creado con Greb Docs")
(def credits-orgs "GREBDev — Herramientas de creación automatizada de contenido")
(def credits-legal "Este manual fue generado como documento de referencia del sistema Greb Docs. Puede ser redistribuido libremente.")
