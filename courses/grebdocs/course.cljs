(ns grebdocs.course
  "Course definition for the Greb Docs user manual."
  (:require [grebdocs.content :as c]))

(def course
  {:meta  {:id          "grebdocs"
           :org         "greb"
           :slug        "manual_greb_docs"
           :title       "Manual de Greb Docs"
           :description "Manual de usuario de Greb Docs — la aplicación web para crear y presentar documentos interactivos."
           :category    "Manuales de Software"
           :tags        ["documentación" "ClojureScript" "presentaciones"]
           :style       {:illustration "playful neo-brutalist UI, toy-like LEGO-ish 3D blocks, chunky geometric shapes, bold primary colors red blue yellow, thick black outlines, isometric perspective, fun and modern, no text"}
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "Greb Docs"
           :logo        nil
           :images-base nil
           :colors {:primary   "#0f172a"
                    :secondary "#2563eb"
                    :accent    "#8b5cf6"
                    :ink       "#0f172a"
                    :paper     "#f1f5f9"
                    :page      "#ffffff"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Presentación"
          :entries (subvec c/index-entries 0 5)}
         {:label "Modos de Visualización"
          :entries (subvec c/index-entries 5 12)}
         {:label "Controles"
          :entries (subvec c/index-entries 12 15)}
         {:label "Desarrollo"
          :entries (subvec c/index-entries 15 17)}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:hero-img "cover-lego.png"
            :title    "Greb Docs"
            :subtitle "Documentos Interactivos · Presentaciones · IA"}}

    ;; 2. Contenido (cards)
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Índice (detallado)
    {:template :index
     :data {:title   "Índice"
            :entries c/index-entries
            :groups  [{:label "Presentación"
                       :items (subvec c/index-entries 3 5)}
                      {:label "Modos de Visualización"
                       :items (subvec c/index-entries 5 12)}
                      {:label "Controles y Hardware"
                       :items (subvec c/index-entries 12 15)}
                      {:label "Desarrollo"
                       :items (subvec c/index-entries 15 17)}]}}

    ;; 4. Introducción — drop cap
    {:template :blocks
     :data {:id "introduccion"
            :header {:icon "book-open" :kicker "Por qué Greb Docs" :title "Introducción"}
            :intro c/intro-dropcap
            :blocks [{:type :callout :icon "zap" :style :accent
                      :title "Dato clave"
                      :text "Un documento Greb Docs es un archivo `.cljs` de ~200 líneas. No hay base de datos, no hay CMS, no hay servidor de edición. El documento **es** el código y el código **es** el documento."}]}}

    ;; 5. ¿Qué es Greb Docs?
    {:template :hero-section
     :data {:id "que-es"
            :hero {:kicker "GREBDev"
                   :title  c/intro-title
                   :subtitle c/intro-lead
                   :hero-img "que-es-lego.png"}
            :blocks [{:type :text-block :content c/intro-text}
                     {:type :info-grid :icon "layers" :title "El Sistema"
                      :items c/intro-sistema}]}}

    ;; 4. Modo Documento
    {:template :hero-section
     :data {:id "modo-documento"
            :hero {:kicker "Visualización"
                   :title  c/modo-doc-title
                   :subtitle c/modo-doc-subtitle
                   :hero-img "modo-documento-lego.png"}
            :blocks [{:type :text-block :content c/modo-doc-text}
                     {:type :info-grid :icon "book-open" :title "Características"
                      :items c/modo-doc-items}]}}

    ;; 5. Modo Presentación — overview
    {:template :hero-section
     :data {:id "modo-presentacion"
            :hero {:kicker "Visualización"
                   :title  c/modo-pres-title
                   :subtitle c/modo-pres-subtitle
                   :hero-img "modo-presentacion-lego.png"}
            :blocks [{:type :callout :icon "monitor"
                      :title "Mismo contenido, otra lectura"
                      :text "El modo presentación **reutiliza el mismo HTML** del documento. No hay una segunda versión — el sistema analiza cada página, parte el cuerpo en fragmentos y arma diapositivas automáticamente."}
                     {:type :steps
                      :items [{:num "1" :title "Entrar" :icon "play"
                               :text "**P** o botón ▶. También `:pres` en Omnibar."}
                              {:num "2" :title "Navegar" :icon "arrow-right"
                               :text "Flechas, Espacio o tap. **1-9** para secciones."}
                              {:num "3" :title "Controlar" :icon "sliders"
                               :text "**HJKL** pan, **D/F** zoom, **]** texto."}
                              {:num "4" :title "Salir" :icon "x"
                               :text "**Escape** o **Q**. Guarda la diapositiva."}]}]}}

    ;; 5b. Doc vs Presentación — comparación visual
    {:template :full-image
     :data {:id "img-doc-vs-pres"
            :img "doc-vs-pres.png"
            :alt "Comparación: modo documento vs modo presentación"
            :screenshot? true
            :kicker "Visualización"
            :title "Documento → Presentación"
            :subtitle "El mismo contenido se transforma automáticamente de lectura paginada a diapositivas fullscreen"}}

    ;; 5c. Overlay Mode
    {:template :blocks
     :data {:id "overlay-mode"
            :header {:icon "layers" :kicker "Presentación" :title "Overlay Mode"}
            :blocks [{:type :text-block
                      :content "Durante la presentación, presioná **I** para abrir el **panel de índice** como overlay transparente. El panel se desliza desde la izquierda mostrando todas las secciones del documento. Hacé clic en cualquier sección para saltar directamente a ella."}
                     {:type :callout :icon "eye" :style :accent
                      :title "UI adaptativa"
                      :text "La interfaz se oculta automáticamente para no distraer. Mové el mouse a la zona inferior para ver la **barra de secciones** (dots), o a la zona superior para la **toolbar** con controles de zoom, texto y fullscreen."}
                     {:type :feature-list
                      :items ["**I** — abrir/cerrar panel de índice"
                              "**P** — ocultar toda la UI (modo limpio)"
                              "**O** — toggle fullscreen"
                              "**U** — alternar doc ↔ presentación"
                              "**Z** — activar cursor spotlight"
                              "**E** — zoom inteligente a elemento"
                              "**?** — mostrar todos los atajos"
                              "**1-9** — saltar a sección por número"]}]}}

    ;; 5d. Overlay screenshot
    {:template :full-image
     :data {:id "img-overlay"
            :img "overlay-mode.png"
            :alt "Overlay mode con panel de índice"
            :screenshot? true
            :kicker "Presentación"
            :title "Overlay Mode"
            :subtitle "Panel de índice transparente sobre la presentación, con secciones clickeables y barra de navegación"}}

    ;; 6. Zoom y Navegación
    {:template :hero-section
     :data {:id "zoom-navegacion"
            :hero {:kicker "Controles"
                   :title  c/zoom-title
                   :subtitle c/zoom-subtitle
                   :hero-img "zoom-lego.png"}
            :blocks [{:type :highlight :icon "sparkles" :title "Atajos esenciales"
                      :items c/zoom-highlight-items}
                     {:type :info-grid :icon "zoom-in" :title "Tipos de zoom y pan"
                      :items c/zoom-items}]}}

    ;; 7. Texto y Escala
    {:template :blocks
     :data {:id "texto-escala"
            :header {:icon "type" :kicker "Accesibilidad" :title c/texto-title}
            :blocks [{:type :highlight :icon "sparkles" :title "Resumen rápido"
                      :items c/texto-highlight-items}
                     {:type :info-grid :icon "type" :title c/texto-subtitle
                      :items c/texto-items}]}}

    ;; 8. Atajos de Teclado
    {:template :blocks
     :data {:id "atajos"
            :header {:icon "keyboard" :kicker "Referencia" :title c/atajos-title}
            :blocks [{:type :info-grid :icon "monitor" :title c/atajos-doc-title
                      :items c/atajos-doc}
                     {:type :info-grid :icon "presentation" :title c/atajos-pres-title
                      :items c/atajos-pres}]}}

    ;; 9. Omnibar
    {:template :hero-section
     :data {:id "omnibar"
            :hero {:kicker "Controles"
                   :title  c/omnibar-title
                   :subtitle c/omnibar-subtitle
                   :hero-img "omnibar-lego.png"}
            :blocks [{:type :omni-embed :icon "terminal" :title "Probá aquí"
                      :caption c/omnibar-embed-caption}
                     {:type :info-grid :icon "braces" :title c/omnibar-command-ref-title
                      :items c/omnibar-command-ref}]}}

    ;; 10. SpaceMouse
    {:template :hero-section
     :data {:id "spacemouse"
            :hero {:kicker "Hardware"
                   :title  c/spacemouse-title
                   :subtitle c/spacemouse-subtitle}
            :blocks [{:type :callout :icon "gamepad-2" :style :accent
                      :title "Dispositivo 3D"
                      :text "SpaceMouse permite **pan y zoom simultáneo** con 6 grados de libertad. Detección automática vía WebHID."}
                     {:type :info-grid :icon "gamepad-2" :title "Soporte"
                      :items c/spacemouse-items}]}}

    ;; 11. Creación de Documentos
    {:template :blocks
     :data {:id "creacion"
            :header {:icon "folder-code" :kicker "Desarrollo" :title c/creacion-title}
            :blocks [{:type :steps
                      :items [{:num "1" :title "Directorio" :icon "folder-plus"
                               :text "`courses/mi-doc/` con `course.cljs` y `content.cljs`."}
                              {:num "2" :title "Contenido" :icon "file-text"
                               :text "Textos, items y datos de cada sección en `content.cljs`."}
                              {:num "3" :title "Páginas" :icon "layout"
                               :text "Vector de `{:template :X :data {...}}` en `course.cljs`."}
                              {:num "4" :title "Registrar" :icon "plug"
                               :text "Require en `app.cljs` y añadir al vector de courses."}]}
                     {:type :info-grid :icon "layout-template" :title "Templates"
                      :items c/creacion-templates}]}}

    ;; 12. Créditos
    {:template :credits
     :data {:title c/credits-title
            :by    c/credits-by
            :logos [{:src "logo-greb.png" :alt "GREB" :dark? true}]
            :orgs  c/credits-orgs
            :legal c/credits-legal}}]})
