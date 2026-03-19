(ns grebdocs.course
  "Course definition for the Greb Docs user manual."
  (:require [grebdocs.content :as c]))

(def course
  {:meta  {:id          "grebdocs"
           :org         "greb"
           :slug        "manual_greb_docs"
           :title       "Manual de Greb Docs"
           :description "Manual de usuario de Greb Docs — la aplicación web para crear y presentar documentos interactivos."
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "Greb Docs"
           :logo        nil
           :images-base nil
           :colors {:primary   "#2563eb"
                    :secondary "#0ea5e9"
                    :accent    "#8b5cf6"
                    :ink       "#1e293b"
                    :paper     "#e2e8f0"
                    :page      "#fffffe"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Presentación"
          :entries (subvec c/index-entries 0 3)}
         {:label "Modos de Visualización"
          :entries (subvec c/index-entries 3 7)}
         {:label "Controles"
          :entries (subvec c/index-entries 7 10)}
         {:label "Desarrollo"
          :entries (subvec c/index-entries 10 12)}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:title    "Manual de Greb Docs"
            :subtitle "GREBDev · Documentos interactivos · Presentaciones · Automatización con IA"}}

    ;; 2. Table of Contents
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Intro — ¿Qué es Greb Docs?
    {:template :hero-section
     :data {:id "que-es"
            :hero {:kicker "GREBDev"
                   :title  c/intro-title
                   :subtitle c/intro-lead}
            :blocks [{:type :info-grid :icon "layers" :title "El Sistema"
                      :items c/intro-sistema}
                     {:type :highlight :icon "sparkles" :title "Datos como código"
                      :items c/intro-blocks}]}}

    ;; 4. Modo Documento
    {:template :hero-section
     :data {:id "modo-documento"
            :hero {:kicker "Visualización"
                   :title  c/modo-doc-title
                   :subtitle c/modo-doc-subtitle}
            :blocks [{:type :info-grid :icon "book-open" :title "Características"
                      :items c/modo-doc-items}]}}

    ;; 5. Modo Presentación
    {:template :hero-section
     :data {:id "modo-presentacion"
            :hero {:kicker "Visualización"
                   :title  c/modo-pres-title
                   :subtitle c/modo-pres-subtitle
                   :intro c/modo-pres-intro}
            :blocks [{:type :info-grid :icon "cpu" :title "Lógica detrás del modo"
                      :items c/modo-pres-logic-items}
                     {:type :info-grid :icon "presentation" :title "En la práctica"
                      :items c/modo-pres-items}]}}

    ;; 6. Zoom y Navegación
    {:template :hero-section
     :data {:id "zoom-navegacion"
            :hero {:kicker "Controles"
                   :title  c/zoom-title
                   :subtitle c/zoom-subtitle
                   :intro c/zoom-intro}
            :blocks [{:type :highlight :icon "sparkles" :title "Atajos esenciales"
                      :items c/zoom-highlight-items}
                     {:type :info-grid :icon "zoom-in" :title "Tipos de zoom y pan"
                      :items c/zoom-items}
                     {:type :info-grid :icon "compass" :title "Navegación en el documento"
                      :items c/zoom-nav-items}]}}

    ;; 7. Texto y Escala
    {:template :hero-section
     :data {:id "texto-escala"
            :hero {:kicker "Accesibilidad"
                   :title  c/texto-title
                   :subtitle c/texto-subtitle
                   :intro c/texto-intro}
            :blocks [{:type :highlight :icon "sparkles" :title "Resumen rápido"
                      :items c/texto-highlight-items}
                     {:type :info-grid :icon "type" :title c/texto-subtitle
                      :items c/texto-items}
                     {:type :info-grid :icon "layers" :title c/texto-combos-title
                      :items c/texto-combos}]}}

    ;; 8. Atajos de Teclado
    {:template :blocks
     :data {:id "atajos"
            :header {:icon "keyboard"
                     :kicker "Referencia"
                     :title  c/atajos-title}
            :blocks [{:type :info-grid :icon "monitor" :title c/atajos-doc-title
                      :items c/atajos-doc}
                     {:type :info-grid :icon "presentation" :title c/atajos-pres-title
                      :items c/atajos-pres}]}}

    ;; 9. Omnibar / OmniREPL
    {:template :hero-section
     :data {:id "omnibar"
            :hero {:kicker "Controles · Taller"
                   :title  c/omnibar-title
                   :subtitle c/omnibar-subtitle
                   :intro c/omnibar-intro}
            :blocks [{:type :highlight :icon "list-checks" :title c/omnibar-exercise-title
                      :items c/omnibar-exercise-items}
                     {:type :omni-embed :icon "terminal" :title "Barra de práctica (OmniREPL)"
                      :caption c/omnibar-embed-caption}
                     {:type :info-grid :icon "braces" :title c/omnibar-command-ref-title
                      :items c/omnibar-command-ref}
                     {:type :info-grid :icon "lightbulb" :title c/omnibar-items-title
                      :items c/omnibar-items}]}}

    ;; 10. SpaceMouse
    {:template :hero-section
     :data {:id "spacemouse"
            :hero {:kicker "Hardware"
                   :title  c/spacemouse-title
                   :subtitle c/spacemouse-subtitle}
            :blocks [{:type :info-grid :icon "gamepad-2" :title "Soporte"
                      :items c/spacemouse-items}]}}

    ;; 11. Creación de Documentos
    {:template :blocks
     :data {:id "creacion"
            :header {:icon "folder-code"
                     :kicker "Desarrollo"
                     :title  c/creacion-title}
            :blocks [{:type :info-grid :icon "folder-tree" :title "Estructura de un curso"
                      :items c/creacion-estructura}
                     {:type :info-grid :icon "layout-template" :title "Templates disponibles"
                      :items c/creacion-templates}
                     {:type :info-grid :icon "workflow" :title "Flujo de trabajo con Claude Code"
                      :items c/creacion-flujo}]}}

    ;; 12. Créditos
    {:template :credits
     :data {:title c/credits-title
            :by    c/credits-by
            :logos [{:src "logo-greb.png" :alt "GREB" :dark? true}]
            :orgs  c/credits-orgs
            :legal c/credits-legal}}]})
