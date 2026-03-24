(ns grebnue.course
  "Course definition for GREB: Sistemas para personas inteligentes (Greb-nue style)."
  (:require [grebnue.content :as c]))

(def course
  {:meta  {:id          "grebnue"
           :org         "grebnue"
           :slug        "greb_libro"
           :title       "GREB: Sistemas para personas inteligentes"
           :description "El libro Greb recreado con el estilo editorial Greb-nue."
           :category    "Metodología"
           :tags        ["greb" "sistemas" "flow" "productividad"]
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "Greb"
           :logo        "greb-logo.png"
           :images-base nil
           :colors {:primary   "#333333"
                    :secondary "#d4e8f0"
                    :accent    "#ffe88d"
                    :ink       "#1a1a1a"
                    :paper     "#f5f5f5"
                    :page      "#ffffff"}
           :fonts  {:display "JetBrains Mono"
                    :head    "JetBrains Mono"
                    :body    "Source Serif 4"}}

   :toc [{:label "¿Qué es Greb?"
          :entries (subvec c/index-entries 0 6)}
         {:label "Herramientas de Greb"
          :entries (subvec c/index-entries 6 14)}
         {:label "Ejemplos de Greb"
          :entries (subvec c/index-entries 14)}]

   :pages
   [;; ── 1. Cover (Portada) ─────────────────────────────────
    {:template :gn-cover
     :data c/cover-data}

    ;; ── 2. Contraportada ───────────────────────────────────
    {:template :gn-backcover
     :data {:id "backcover"
            :bg-img "greb_main_site.png"
            :logo "greb-logo.png"
            :badge "La solución\na todos los problemas"
            :card-color :blue
            :card-text (:text c/backcover-data)}}

    ;; ── 3. Estructura (3 partes) ───────────────────────────
    {:template :gn-page
     :data {:id "estructura"
            :bg-type :blobs
            :blocks
            [{:type :gn-title-bar :title (:title c/structure-data) :color :yellow}
             {:type :gn-card :color :cream
              :text [(:desc c/structure-data)
                     "**¿Qué es Greb?:** Introducción al flujo y el pensamiento intuitivo"
                     "**Herramientas de Greb:** Los términos y herramientas para aplicar **Greb**"
                     "**Ejemplos de Greb:** Historias, productos, procedimientos e ideas **Greb**"]}]}}

    ;; ── 4. Tabla de contenidos ─────────────────────────────
    {:template :gn-toc
     :data {:sections c/toc-sections
            :footer-text "Cada uno de las etiquetas a lo largo del libro y en esta tabla de contenidos corresponde a una sección de nuestra página web."}}

    ;; ── 5. Quote: Navaja de Ockham ─────────────────────────
    {:template :gn-quote
     :data {:id "quote-ockham"
            :img "contexto.png"
            :quote "La respuesta más simple es usualmente la respuesta *correcta*"
            :author "Navaja de Ockham"}}

    ;; ── 6. Section Divider: 1 ¿Qué es Greb? ───────────────
    {:template :gn-divider
     :data {:id "seccion-1"
            :img "contexto.png"
            :number "1"
            :title "¿Qué es *Greb*?"}}

    ;; ── 7. Blank grid page (page 8 in book) ────────────────
    ;; Skip blank page

    ;; ── 8. ¿Qué es Greb? (page 9) ─────────────────────────
    {:template :gn-page
     :data c/intro-page-9}

    ;; ── 9. "Todos somos un poco Greb" (page 10) ────────────
    {:template :gn-page
     :data c/intro-page-10}

    ;; ── 10. Un ejemplo de Greb (page 11) ───────────────────
    {:template :gn-page
     :data c/intro-page-11}

    ;; ── 11. Ocupado ≠ Productivo (page 12) ─────────────────
    {:template :gn-page
     :data c/intro-page-12}

    ;; ── 12. Tareas y niveles (page 13) ─────────────────────
    {:template :gn-page
     :data c/intro-page-13}

    ;; ── 13. Puertas Norman (page 14) ───────────────────────
    {:template :gn-page
     :data c/norman-page-14}

    ;; ── 14. Estudios de Movimiento (page 16) ───────────────
    {:template :gn-page
     :data c/move-page-16}

    ;; ── 15. Frank Gilbreth (page 17) ───────────────────────
    {:template :gn-page
     :data c/move-page-17}

    ;; ── 16. Therbligs (page 18) ────────────────────────────
    {:template :gn-page
     :data c/therbligs-page-18}

    ;; ── 17. Estudios de Movimiento ejemplo (page 19) ───────
    {:template :gn-page
     :data c/move-page-19}

    ;; ── 18. Feature pills: Flow (pages 20-21) ──────────────
    {:template :gn-page
     :data c/flow-page-20}

    ;; ── 19. Quote: Herramienta correcta ────────────────────
    {:template :gn-quote
     :data {:id "quote-herramienta"
            :img "fabrica.png"
            :quote "Hay una herramienta *correcta* para cada problema"
            :author ""}}

    ;; ── 20. Section Divider: 2 Herramientas ────────────────
    {:template :gn-divider
     :data {:id "seccion-2"
            :img "fabrica.png"
            :number "2"
            :title "Herramientas de *Greb*"}}]})
