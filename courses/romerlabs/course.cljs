(ns romerlabs.course
  "Course definition for Romer Labs food safety diagnostics."
  (:require [romerlabs.content :as c]))

(def course
  {:meta  {:id          "romerlabs"
           :org         "romerlabs"
           :slug        "diagnostico_seguridad_alimentaria"
           :title       "Diagnóstico Analítico para Seguridad Alimentaria — Romer Labs"
           :description "Tecnologías de diagnóstico para detección de contaminantes en la industria alimentaria."
           :category    "Seguridad Alimentaria"
           :tags        ["diagnóstico" "contaminantes" "laboratorio"]
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "Romer Labs"
           :logo        "romer-labs-logo.png"
           :images-base nil
           :colors {:primary   "#00529b"
                    :secondary "#7ab648"
                    :accent    "#f39c12"
                    :ink       "#1c1c1e"
                    :paper     "#e8ecf0"
                    :page      "#fffffe"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Introducción"
          :entries (subvec c/index-entries 0 4)}
         {:label "Tecnologías"
          :entries (subvec c/index-entries 4 10)}
         {:label "AgraVision™ & ELISA"
          :entries (subvec c/index-entries 10 14)}
         {:label "ABT Internacional"
          :entries (subvec c/index-entries 14 17)}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:hero-img "agrastrip-cartridges-lab.png"
            :logo     "romer-labs-logo.png"
            :logo-secondary "abt-internacional-logo.png"
            :title    "Diagnóstico Analítico para Seguridad Alimentaria"
            :subtitle "Romer Labs — Tecnologías de detección de contaminantes · Distribuido por ABT Internacional"}}

    ;; 2. TOC + Index
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Índice con números de página
    {:template :index
     :data {:title "Índice"
            :entries c/index-entries
            :groups [{:label "Introducción"
                      :icon-default "book-open"
                      :items (subvec c/index-entries 0 4)}
                     {:label "Tecnologías de Detección"
                      :icon-default "zap"
                      :items (subvec c/index-entries 4 10)}
                     {:label "AgraVision™ & ELISA"
                      :icon-default "monitor"
                      :items (subvec c/index-entries 10 14)}
                     {:label "ABT Internacional"
                      :icon-default "star"
                      :items (subvec c/index-entries 14 17)}]}}

    ;; 4. Intro + Problema (merged — dense page)
    {:template :hero-section
     :data {:id "introduccion"
            :hero {:kicker "Romer Labs · dsm-firmenich"
                   :title c/intro-title
                   :subtitle c/romerlabs-about
                   :hero-img "agrastrip-cartridges.png"}
            :blocks [{:type :info-grid :icon "layers" :title "Ecosistema de soluciones"
                      :items c/intro-soluciones}
                     {:type :stat-grid :icon "trending-up" :title "Impacto global"
                      :items c/problema-impacto}
                     {:type :highlight
                      :icon "link" :title "AgraStrip + AgraVision = Resultado digital"
                      :items ["AgraStrip® realiza el test rápido · AgraVision™ lee y cuantifica digitalmente"
                              "Juntos eliminan la interpretación subjetiva y generan datos trazables"
                              "Cumplimiento con FDA, UE, Codex Alimentarius y GFSI"]}]}}

    ;; ── SECTION DIVIDER: Tecnologías ──────────────────────────────
    ;; 4. Romer Labs historia + tecnologías (con bg image)
    {:template :blocks
     :data {:id "tecnologias"
            :bg-img "lab-equipment.png"
            :header {:icon "building-2"
                     :kicker "Romer Labs · dsm-firmenich"
                     :title "Historia y tecnologías"}
            :blocks [{:type :timeline :icon "clock" :title "Making the World's Food Safer® desde 1982"
                      :items c/romerlabs-historia-timeline}
                     {:type :info-grid :icon "shield-check" :title "Certificaciones y calidad"
                      :items c/romerlabs-calidad}]}}

    ;; 5. AgraStrip — intro + historia + producto (all-in-one dense)
    {:template :hero-section
     :data {:id "agrastrip"
            :hero {:kicker "Test rápido de flujo lateral"
                   :title "AgraStrip®"
                   :subtitle "Resultados en 4–10 minutos. Análisis en planta o campo sin laboratorio."
                   :hero-img "agrastrip-single-test.png"}
            :blocks [{:type :product-showcase
                      :features [{:icon "zap" :label "4–10 min"}
                                 {:icon "user-check" :label "Sin especialista"}
                                 {:icon "map-pin" :label "Campo/planta"}
                                 {:icon "scan-line" :label "Flujo lateral"}
                                 {:icon "monitor" :label "AgraVision™"}
                                 {:icon "award" :label "AOAC"}]}
                     {:type :info-grid :icon "zap" :title "Catálogo de analitos"
                      :items c/agrastrip-catalogo}
                     {:type :chip-row :icon "map-pin" :title "Aplicaciones"
                      :items c/agrastrip-applications}]}}

    ;; 6. AgraStrip — Historia con imagen de fondo
    {:template :blocks
     :data {:id "agrastrip-historia"
            :header {:icon "history"
                     :kicker "AgraStrip®"
                     :title "Historia y significado"}
            :blocks [{:type :product-timeline
                      :img "agrastrip-cartridges-lab.png"
                      :alt "AgraStrip cartuchos en laboratorio"
                      :features [{:icon "zap" :label "Rápido"}
                                 {:icon "scan-line" :label "Lateral flow"}
                                 {:icon "award" :label "AOAC"}]
                      :timeline-items c/agrastrip-historia-timeline}
                     {:type :info-grid :icon "book-open" :title "Significado del nombre"
                      :items c/agrastrip-nombre-desglose}
                     {:type :highlight
                      :icon "lightbulb"
                      :title "¿Por qué AgraStrip?"
                      :items ["Combina simplicidad de uso con resultados confiables validados por AOAC y GIPSA. Los kits incluyen todo lo necesario para ejecutar el análisis en pocos minutos, con interpretación visual directa o lectura digital opcional."
                              "Ideal para screening en planta sin necesidad de laboratorio ni personal especializado. El operador sigue las instrucciones paso a paso; no se requieren pipetas de precisión ni equipos complejos. Resultados en 4–10 minutos según analito."
                              "Integración con AgraVision™ permite pasar de cualitativo a cuantitativo cuando se requiere trazabilidad. La tira se puede leer a ojo para un sí/no, o escanear con AgraVision Pro para obtener valores numéricos exportables a LIMS y cumplimiento regulatorio."]}]}}

    ;; 7. AgraQuant — intro + historia + producto (all-in-one dense)
    {:template :hero-section
     :data {:id "agraquant"
            :hero {:kicker "Análisis ELISA cuantitativo"
                   :title "AgraQuant®"
                   :subtitle "Cuantificación precisa en microplaca de 96 pocillos. Mide ppm/ppb exactos."
                   :hero-img "agraquant-kit.jpeg"}
            :blocks [{:type :product-showcase
                      :features [{:icon "bar-chart-3" :label "Cuantitativo"}
                                 {:icon "layout-grid" :label "96 pozos"}
                                 {:icon "target" :label "Alta sensibilidad"}
                                 {:icon "award" :label "AOAC / GIPSA"}
                                 {:icon "monitor" :label "450 nm"}
                                 {:icon "calculator" :label "Curva calibración"}]}
                     {:type :info-grid :icon "bar-chart-3" :title "Características"
                      :items c/agraquant-features}
                     {:type :chip-row :icon "building-2" :title "Sectores de uso"
                      :items c/agraquant-uses}]}}

    ;; 8. AgraQuant — Historia + procedimiento
    {:template :blocks
     :data {:id "agraquant-historia"
            :header {:icon "history"
                     :kicker "AgraQuant®"
                     :title "Historia y procedimiento"}
            :blocks [{:type :product-timeline
                      :img "agraquant-kit.jpeg"
                      :alt "AgraQuant kit"
                      :features [{:icon "bar-chart-3" :label "ELISA"}
                                 {:icon "layout-grid" :label "96 pozos"}
                                 {:icon "award" :label "AOAC"}]
                      :timeline-items c/agraquant-historia-timeline}
                     {:type :wash-grid :icon "list-ordered" :title "Procedimiento de análisis"
                      :items c/agraquant-procedure}]}}

    ;; ── SECTION DIVIDER: AgraVision ──────────────────────────────
    ;; 9. Full-image divider
    {:template :full-image
     :data {:id "agravision"
            :img "agravision-in-lab.jpeg"
            :alt "AgraVision Pro en laboratorio"
            :kicker "AgraVision™ Pro"
            :title "Lectura Digital de Resultados"
            :subtitle "Sistema óptico que digitaliza tiras AgraStrip Pro. Elimina interpretación visual subjetiva. Incubador integrado y trazabilidad LIMS."}}

    ;; 10. AgraVision — producto + historia + specs (dense)
    {:template :blocks
     :data {:id "agravision-producto"
            :header {:icon "monitor"
                     :kicker "AgraVision™ Pro"
                     :title "Producto, historia y especificaciones"}
            :blocks [{:type :product-timeline
                      :img "agravision-pro-cartridges.png"
                      :alt "AgraVision Pro Reader"
                      :features [{:icon "monitor" :label "Pantalla 7\""}
                                 {:icon "thermometer" :label "Incubador"}
                                 {:icon "scan-line" :label "Sensor óptico"}
                                 {:icon "layers" :label "4 simultáneos"}
                                 {:icon "usb" :label "USB / LIMS"}
                                 {:icon "shield-check" :label "CE"}]
                      :timeline-items c/agravision-historia-timeline}
                     {:type :info-grid :icon "settings" :title "Especificaciones"
                      :items c/agravision-specs}
                     {:type :wash-grid :icon "list-ordered" :title "Procedimiento"
                      :items c/agravision-como-funciona}]}}

    ;; 11. ELISA — ¿Qué es? + Significado (merged)
    {:template :hero-section
     :data {:id "elisa"
            :hero {:kicker "Fundamento técnico"
                   :title c/elisa-title
                   :subtitle c/elisa-subtitle
                   :hero-img "drops-on-elisa.webp"}
            :blocks [{:type :info-grid :icon "spell-check" :title "Significado del acrónimo"
                      :items c/elisa-siglas-desglose}
                     {:type :info-grid :icon "microscope" :title "Capacidades de detección"
                      :items c/elisa-intro}
                     {:type :highlight
                      :icon (:icon c/elisa-idea-clave)
                      :title (:title c/elisa-idea-clave)
                      :items (:items c/elisa-idea-clave)}]}}

    ;; 12. ELISA — Historia + Lector + Aplicaciones (dense)
    {:template :blocks
     :data {:id "elisa-historia"
            :header {:icon "history"
                     :kicker "ELISA"
                     :title "Historia, lector y aplicaciones"}
            :blocks [{:type :product-timeline
                      :img "biotek-800ts-reader.png"
                      :alt "BioTek 800 TS"
                      :features [{:icon "monitor" :label "Táctil"}
                                 {:icon "scan-line" :label "450 nm"}
                                 {:icon "layout-grid" :label "96 pozos"}
                                 {:icon "usb" :label "USB"}
                                 {:icon "calculator" :label "Curva auto"}
                                 {:icon "shield-check" :label "CE"}]
                      :timeline-items c/elisa-historia-timeline
                      :disclaimer "BioTek 800 TS: producto de Agilent Technologies, no de Romer Labs. Distribuido por ABT Internacional — www.agrobiotek.com"}
                     {:type :info-grid :icon "search" :title "Analitos y aplicaciones"
                      :items c/elisa-aplicaciones}
                     {:type :stat-grid :icon "rocket" :title "Tecnologías derivadas"
                      :items c/elisa-tecnologias-derivadas}]}}

    ;; 13. Flujo de análisis + Analitos (merged)
    {:template :blocks
     :data {:id "flujo-analisis"
            :header {:icon "git-branch"
                     :kicker "Implementación"
                     :title "Flujo de análisis y analitos detectados"}
            :blocks [{:type :wash-grid :icon "workflow" :title "Pasos del proceso"
                      :items c/flujo-steps}
                     {:type :info-grid :icon "bug" :title "Micotoxinas"
                      :items c/micotoxinas}
                     {:type :info-grid :icon "triangle-alert" :title "Alérgenos"
                      :items c/alergenos}]}}

    ;; ── SECTION DIVIDER: ABT ──────────────────────────────────────
    ;; 14. Full-image divider
    {:template :full-image
     :data {:id "por-que-abt"
            :img "abt-equipo-planta.png"
            :alt "Equipo ABT Internacional en planta"
            :kicker "ABT Internacional"
            :title "Tu socio en diagnóstico alimentario"
            :subtitle "Representante oficial de Romer Labs en Centroamérica y el Caribe. Más de 30 años de experiencia."}}

    ;; 15. ABT — Servicios + ¿Por qué?
    {:template :blocks
     :data {:id "abt-servicios"
            :header {:icon "star"
                     :kicker "ABT Internacional"
                     :title "Servicios y ventajas"}
            :blocks [{:type :image-grid :icon "camera" :title "Nuestros servicios"
                      :items c/abt-servicios-grid}
                     {:type :info-grid :icon "star" :title "¿Por qué trabajar con nosotros?"
                      :items c/abt-porque-trabajar}
                     {:type :stat-grid :icon "bar-chart-3" :title "Presencia regional"
                      :items c/abt-stats}]}}

    ;; 16. Contacto + Cierre
    {:template :blocks
     :data {:id "contacto"
            :header {:icon "map-pin"
                     :kicker "Cobertura regional"
                     :title "Contáctanos en 5 países"}
            :blocks [{:type :product-showcase
                      :img "abt-mapa-regional.png"
                      :alt "Presencia de ABT Internacional en Centroamérica y el Caribe"
                      :features [{:icon "globe" :label "www.agrobiotek.com"}
                                 {:icon "phone" :label "+809 972-4364"}
                                 {:icon "mail" :label "info@agrobiotek.com"}
                                 {:icon "message-circle" :label "WhatsApp"}]}
                     {:type :info-grid :icon "building-2" :title "Oficinas regionales"
                      :items c/abt-contacto-regional}]}}]})
