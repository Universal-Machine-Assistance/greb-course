(ns propuesta-web.course
  "Propuesta comercial: sitio web Squarespace por GREB."
  (:require [propuesta-web.content :as c]))

(def course
  {:meta  {:id          "propuesta-web"
           :org         "propuesta_web"
           :slug        "propuesta_sitio_web"
           :title       "Propuesta — Sitio Web Profesional"
           :description "Propuesta comercial para diseño y desarrollo de sitio web en Squarespace. Incluye diseño, SEO, capacitación y soporte."
           :category    "Propuestas"
           :tags        ["Squarespace" "diseño web" "propuesta comercial"]
           :style       {:illustration "oil painting Norman Rockwell style, warm golden lighting, professional business setting, rich detailed brushwork, editorial quality"}
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "GREB"
           :logo        nil
           :images-base nil
           :colors {:primary   "#0f172a"
                    :secondary "#3b82f6"
                    :accent    "#10b981"
                    :ink       "#0f172a"
                    :paper     "#f8fafc"
                    :page      "#ffffff"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Propuesta"
          :entries (subvec c/index-entries 0 6)}
         {:label "Ejecución"
          :entries (subvec c/index-entries 6 9)}
         {:label "Inversión"
          :entries (subvec c/index-entries 9 12)}]

   :pages
   [;; 1. Portada
    {:template :cover
     :data {:hero-img "cover.png"
            :title    "Propuesta"
            :subtitle "Sitio Web Profesional · Squarespace · GREB"}}

    ;; 2. Contenido
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Resumen Ejecutivo
    {:template :hero-section
     :data {:id "resumen"
            :hero {:kicker "Propuesta Comercial"
                   :title  c/resumen-title
                   :subtitle c/resumen-subtitle}
            :blocks [{:type :text-block :content c/resumen-text}
                     {:type :info-grid :icon "sparkles" :title "Beneficios Clave"
                      :items c/resumen-items}]}}

    ;; 4. Alcance — feature list + callout
    {:template :blocks
     :data {:id "alcance"
            :header {:icon "target"
                     :kicker "Proyecto"
                     :title  c/alcance-title}
            :blocks [{:type :callout :icon "info" :title "Lo que incluye"
                      :text c/alcance-text}
                     {:type :feature-list
                      :items ["Hasta **7 páginas** personalizadas"
                              "Diseño responsive (móvil + desktop)"
                              "Formulario de contacto funcional"
                              "Integración con redes sociales"
                              "Configuración SEO completa"
                              "Google Analytics conectado"
                              "Certificado SSL gratuito"
                              "Mapa de Google Maps"
                              "Galería de imágenes"
                              "Conexión de dominio propio"]}
                     {:type :callout :icon "alert-triangle" :style :warning
                      :title "Fuera de alcance"
                      :text "No incluye: registro de dominio (~$12/año), suscripción Squarespace (pago directo del cliente), fotografía profesional, redacción de contenido, tienda e-commerce con +10 productos, sistemas de membresía o reservas."}]}}

    ;; 5. Squarespace — two-col + highlight
    {:template :hero-section
     :data {:id "squarespace"
            :hero {:kicker "Plataforma"
                   :title  c/squarespace-title
                   :subtitle c/squarespace-subtitle
                   :hero-img "squarespace.png"}
            :blocks [{:type :text-block :content c/squarespace-text}
                     {:type :highlight :icon "cloud" :title "Infraestructura incluida sin costo extra"
                      :items ["Hosting ilimitado con CDN global y 99.98% uptime"
                              "Certificado SSL gratuito — siempre https://"
                              "+100 plantillas premium responsive"
                              "Soporte técnico 24/7 por email y chat"
                              "Copias de seguridad automáticas"
                              "Actualizaciones de seguridad automáticas"]}]}}

    ;; 6. Plan Core — pricing table
    {:template :blocks
     :data {:id "plan-core"
            :header {:icon "credit-card"
                     :kicker "Squarespace"
                     :title  c/plan-core-title}
            :blocks [{:type :text-block :content c/plan-core-text}
                     {:type :pricing-table
                      :title "Squarespace Core — Comparación de Planes"
                      :rows [{:label "Plan mensual" :amount "$33/mes" :note "sin compromiso"}
                             {:label "Plan anual" :amount "$27/mes" :note "$324/año" :highlight? true}
                             {:label "Dominio gratis (primer año)" :amount "Incluido" :note "con plan anual" :highlight? true}
                             {:label "Páginas ilimitadas" :amount "✓"}
                             {:label "Analytics avanzados" :amount "✓"}
                             {:label "E-commerce (0% comisión)" :amount "✓"}
                             {:label "Integraciones (Mailchimp, Zapier...)" :amount "✓"}]
                      :footer "Recomendamos el plan anual por el ahorro del 18% y el dominio gratis incluido."}
                     {:type :callout :icon "lightbulb" :style :accent
                      :title "Recomendación"
                      :text "El plan **Core anual** ($27/mes) es la mejor opción: ahorra 18%, incluye dominio gratis el primer año, y tiene todas las funcionalidades necesarias. El cliente paga directamente a Squarespace con su tarjeta."}]}}

    ;; 7. Proceso — steps layout
    {:template :hero-section
     :data {:id "proceso"
            :hero {:kicker "Metodología"
                   :title  c/proceso-title
                   :subtitle c/proceso-subtitle
                   :hero-img "process.png"}
            :blocks [{:type :text-block :content c/proceso-text}
                     {:type :steps
                      :items [{:num "1" :title "Briefing y Estrategia" :icon "clipboard"
                               :text "**Semana 1 (Días 1-3)** — Reunión inicial, definición de objetivos y estructura. Usted envía: logo, textos, fotos. Entregable: **mapa del sitio aprobado**."}
                              {:num "2" :title "Diseño Visual" :icon "palette"
                               :text "**Semana 1-2 (Días 3-8)** — Personalización de plantilla, colores, tipografía. Entregable: **mockup visual aprobado** (2 rondas de revisión)."}
                              {:num "3" :title "Desarrollo" :icon "code"
                               :text "**Semana 2-3 (Días 8-16)** — Construcción de páginas, contenido, SEO, formularios, analytics. Entregable: **sitio en modo borrador**."}
                              {:num "4" :title "Lanzamiento" :icon "rocket"
                               :text "**Semana 3 (Días 16-21)** — Revisión final, ajustes, conexión de dominio, capacitación de 1 hora. Entregable: **sitio en producción + manual**."}]}]}}

    ;; 8. Entregables — feature list + stat grid
    {:template :blocks
     :data {:id "entregables"
            :header {:icon "package"
                     :kicker "Resultado"
                     :title  c/entregables-title}
            :blocks [{:type :stat-grid
                      :items [{:icon "globe" :label "Sitio web" :value "7 págs"}
                              {:icon "video" :label "Capacitación" :value "1 hora"}
                              {:icon "book-open" :label "Manual" :value "PDF"}
                              {:icon "folder" :label "Archivos" :value "Drive"}]}
                     {:type :info-grid :icon "check-square" :title "Detalle de Entregables"
                      :items c/entregables-items}]}}

    ;; 9. Cronograma — timeline
    {:template :blocks
     :data {:id "cronograma"
            :header {:icon "calendar"
                     :kicker "Tiempos"
                     :title  c/cronograma-title}
            :blocks [{:type :timeline
                      :items [{:year "S1" :title "Briefing + Diseño"
                               :text "Días 1-5: reunión, mapa del sitio, selección de plantilla, primer borrador visual."}
                              {:year "S2" :title "Desarrollo"
                               :text "Días 6-12: construcción de páginas, carga de contenido, configuración técnica."}
                              {:year "S3" :title "Lanzamiento"
                               :text "Días 13-21: revisión final, ajustes, dominio, capacitación, sitio en vivo."}]}
                     {:type :text-block :content c/cronograma-text}]}}

    ;; 10. Inversión — cotización formal
    {:template :blocks
     :data {:id "inversion"
            :header {:icon "receipt"
                     :kicker "Cotización"
                     :title  "Cotización de Servicios"}
            :blocks [{:type :quote-table
                      :title "Cotización — Sitio Web Profesional"
                      :number "2026-001"
                      :date "21 de marzo, 2026"
                      :client "[Nombre del Cliente]"
                      :items [{:desc {:title "Diseño y Desarrollo Web"
                                      :detail "Diseño personalizado sobre Squarespace. Hasta 7 páginas: Inicio, Nosotros, Servicios, Galería, Blog, Contacto + 1 adicional."}
                               :qty "1" :unit-price "$1,200.00" :amount "$1,200.00" :highlight? true}
                              {:desc {:title "Configuración SEO"
                                      :detail "Meta tags, URLs limpias, sitemap XML, Google Search Console, Open Graph para redes sociales."}
                               :qty "1" :unit-price "Incluido" :amount "—"}
                              {:desc {:title "Capacitación (videollamada grabada)"
                                      :detail "Sesión de 1 hora para aprender a gestionar el sitio: editar textos, subir fotos, crear páginas."}
                               :qty "1 hr" :unit-price "Incluido" :amount "—"}
                              {:desc {:title "Manual de Uso (PDF)"
                                      :detail "Instrucciones paso a paso con capturas de pantalla para las tareas más comunes."}
                               :qty "1" :unit-price "Incluido" :amount "—"}
                              {:desc {:title "Soporte Post-Lanzamiento"
                                      :detail "30 días de soporte gratuito para dudas y ajustes menores después del lanzamiento."}
                               :qty "30 días" :unit-price "Incluido" :amount "—"}]
                      :subtotal "$1,200.00"
                      :discount {:label "Descuento" :amount "$0.00"}
                      :tax {:label "ITBIS (0%)" :amount "$0.00"}
                      :total "$1,200.00"
                      :notes "Precios en USD. No incluye suscripción de Squarespace ($27/mes anual) ni registro de dominio. El cliente paga estos servicios directamente a Squarespace."
                      :terms "**50% anticipo** al aprobar ($600) + **50% al lanzar** ($600). Transferencia, Zelle o PayPal. Propuesta válida por 30 días."}]}}

    ;; 11. Condiciones — callouts + info grid
    {:template :blocks
     :data {:id "condiciones"
            :header {:icon "file-text"
                     :kicker "Legal"
                     :title  c/condiciones-title}
            :blocks [{:type :callout :icon "clock"
                      :title "Validez" :text "Esta propuesta es válida por **30 días** a partir de la fecha de emisión."}
                     {:type :callout :icon "key" :style :accent
                      :title "Propiedad" :text "La cuenta de Squarespace es **propiedad del cliente**. GREB no retiene acceso ni control. Todo el contenido y diseño pertenecen al cliente."}
                     {:type :info-grid :icon "scale" :title "Términos Adicionales"
                      :items c/condiciones-items}]}}

    ;; 12. Contacto / Créditos
    {:template :credits
     :data {:title c/credits-title
            :by    c/credits-by
            :orgs  c/credits-orgs
            :logos [{:src "logo-greb.png" :alt "GREB"}]
            :legal c/credits-legal}}]})
