(ns valentino.course
  "Full course definition map for Valentino food hygiene manual."
  (:require [valentino.content :as c]))

(def course
  {:meta  {:id          "valentino"
           :org         "valentino"
           :slug        "guia_de_higiene_alimentaria"
           :title       "Guía de Higiene Alimentaria — Helados Valentino"
           :description "Manual de higiene y seguridad alimentaria para las tiendas de Helados Valentino."
           :category    "Seguridad Alimentaria"
           :tags        ["higiene" "helados" "HACCP"]
           :lang        :es
           :i18n-overrides {}}

   :theme {:brand-name  "Helados Valentino"
           :logo        "LOGO-VALENTINOTIPOBLANCOSMANUEVO.png"
           :images-base nil  ;; resolved at runtime from course path
           :colors {:primary   "#e4602c"
                    :secondary "#b4bc45"
                    :accent    "#ed9e45"
                    :ink       "#1c1c1e"
                    :paper     "#e0dbd0"
                    :page      "#fffffe"}
           :fonts  {:display "Playfair Display"
                    :head    "Outfit"
                    :body    "DM Sans"}}

   :toc [{:label "Presentación"
          :entries (subvec c/index-entries 0 5)}
         {:label "Higiene del Personal"
          :entries (subvec c/index-entries 5 10)}
         {:label "Recepción y Almacenamiento"
          :entries (subvec c/index-entries 10 14)}
         {:label "Limpieza y Desinfección"
          :entries (subvec c/index-entries 14 28)}
         {:label "Mantenimiento"
          :entries (subvec c/index-entries 28 30)}
         {:label "Servicio al Cliente"
          :entries (subvec c/index-entries 30 35)}
         {:label "Riesgos"
          :entries (subvec c/index-entries 35 41)}
         {:label "Glosario"
          :entries (into [(nth c/index-entries 41)]
                         (map-indexed (fn [i t] {:id (:id t) :label (:term t) :page (+ 42 i)})
                                      c/glosario-terms))}
         {:label "Créditos"
          :entries [(nth c/index-entries 42)]}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:hero-img "cover-higiene-abstract.png"
            :logo     "LOGO-VALENTINOTIPOBLANCOSMANUEVO.png"
            :title    "Guía de Higiene Alimentaria"
            :subtitle "para Tiendas de Helados Valentino"}}

    ;; 2. Table of Contents
    {:template :toc-card-grid
     :data {:title         c/contenido-title
            :subtitle      c/contenido-subtitle
            :sections      c/contenido-sections
            :article-class "valentino-contenido"}}

    ;; 3. Index — page 1 (sin «Riesgos Alimentarios» — va a p.4 para equilibrar)
    {:template :index
     :data {:title   c/index-title
            :entries c/index-entries
            :sections c/contenido-sections
            :groups [{:label "Presentación"
                      :items (take 5 c/index-entries)
                      :icon-default "file-text"}
                     {:label "Contenido"
                      :type :sections
                      :items (take 5 c/contenido-sections)}]}}

    ;; 4. Index — page 2
    {:template :index
     :data {:id           "indice-2"
            :hide-title?  true
            :entries      c/index-entries
            :sections     c/contenido-sections
            :groups       [{:label "Contenido"
                            :type :sections
                            :items [(nth c/contenido-sections 5)]}
                           {:label "Riesgos"
                            :icon-default "file-text"
                            :items (let [pg #(get (into {} (map (juxt :id :page) c/index-entries)) %)]
                                     (concat [{:id "riesgos-divider" :label "Familias de riesgo (portada)" :icon "image" :page (pg "riesgos-divider")}
                                              {:id "riesgos-familias-intro" :label "Las cuatro familias explicadas" :icon "layout-grid" :page (pg "riesgos-familias-intro")}]
                                             (mapv (fn [{:keys [id icon title]}]
                                                     {:id id :label title :icon icon :page (pg id)})
                                                   c/risk-families)))}
                           {:label "Referencia"
                            :items [{:id "glosario" :label "Glosario de Términos" :icon "book-open"
                                     :page (get (into {} (map (juxt :id :page) c/index-entries)) "glosario")}]}
                           {:label "Créditos"
                            :items [{:id "creditos" :label "Créditos" :icon "copyright"
                                     :page (get (into {} (map (juxt :id :page) c/index-entries)) "creditos")}]}]}}

    ;; 4. Introduction
    {:template :intro
     :data {:section-tag {:label "Presentación" :color "presentacion"}
            :eyebrow "Manual · Higiene Alimentaria"
            :title c/intro-title
            :stat-num "4"
            :stat-label "familias de riesgo"
            :images ["BarquillaMenu-r8e36ec2drhk9tsd3bkeyw3dfzx89pt0jtnv3zrt30.png"
                     "Avatarconcajita-r8e3rxsx02z4c4hyb4rmlyenivr1o0bkifvg19u0ho.png"]
            :lead (first c/intro-blocks)
            :para2 (nth c/intro-blocks 1)
            :risk-families-title c/risk-families-title
            :risk-families c/risk-families
            :closing (nth c/intro-blocks 2)}}

    ;; 5. Handwash page (handwash-head + duo-grid + gloves)
    {:template :blocks
     :data {:section-tag {:label "Higiene" :color "higiene"}
            :id "lavado-y-guantes"
            :head-section {:kicker "Protocolo crítico" :title "Lavado de manos"}
            :duo-grid [{:type :chip-row :icon "hand-metal" :title "Estación de lavado"
                        :items c/handwash-station}
                       {:type :frequency-list :icon "timer-reset" :title "Frecuencia"
                        :items c/handwash-frequency}]
            :blocks [{:type :info-grid :icon "shield-plus" :title "Uso de guantes"
                      :items c/glove-rules}]}}

    ;; 6. Handwash steps (grid in doc mode, carousel slides in pres mode)
    {:template :blocks
     :data {:section-tag {:label "Higiene" :color "higiene"}
            :id "lavado-tecnica"
            :blocks [{:type :wash-grid :icon "waves" :title "Técnica de lavado — 6 pasos"
                      :items c/handwash-steps}
                     {:type :highlight
                      :icon "lightbulb"
                      :title "¿Sabías que…?"
                      :items c/handwash-fun-facts}]}}

    ;; 7. Personal hygiene (hero-section)
    {:template :hero-section
     :data {:section-tag {:label "Higiene" :color "higiene"}
            :id "higiene-personal"
            :hero {:kicker "Atención al cliente"
                   :title c/higiene-personal-title
                   :subtitle c/hygiene-subtitle
                   :meter-value "100%"
                   :meter-caption "Listo para abrir"}
            :blocks [{:type :mission-grid :icon "clipboard-list" :title "Checklist de apertura"
                      :items c/hygiene-checklist}
                     {:type :info-grid :icon "scan-face" :title "Avatar higiénico"
                      :items c/hygiene-avatar-rules}]}}

    ;; 8. Hygiene discipline — uniforme y apariencia
    {:template :blocks
     :data {:section-tag {:label "Higiene" :color "higiene"}
            :id "higiene-disciplina"
            :intro c/hygiene-discipline-intro
            :blocks [{:type :uniform-checklist :icon "clipboard-check" :title "Uniforme Reglamentario"
                      :zones c/hygiene-uniform-zones
                      :items c/hygiene-uniform-checklist}
                     {:type :info-grid :icon "briefcase-business" :title "Disciplina del uniforme"
                      :items c/hygiene-uniform-rules}
                     {:type :info-grid :icon "scan-face" :title "Apariencia personal"
                      :items c/hygiene-avatar-rules}]}}

    ;; 9. Hygiene discipline — visitantes, baños, descanso, salud, lockers
    {:template :blocks
     :data {:section-tag {:label "Higiene" :color "higiene"}
            :id "higiene-instalaciones"
            :header {:icon "building"
                     :kicker "Higiene del entorno"
                     :title "Instalaciones y Salud"}
            :blocks [{:type :info-grid :icon "lock-keyhole" :title "Lockers y cambio de ropa"
                      :items c/hygiene-locker-rules}
                     {:type :visitor-grid :icon "users-round" :title "Visitantes"
                      :items c/hygiene-visitor-rules}
                     {:type :info-grid :icon "bath" :title "Baños del personal"
                      :items c/hygiene-toilet-rules}
                     {:type :info-grid :icon "coffee" :title "Sala de descanso"
                      :items c/hygiene-break-room-rules}
                     {:type :info-grid :icon "heart-pulse" :title "Salud del personal"
                      :items c/hygiene-health-rules}]}}

    ;; 9. Reception intro (with drop cap)
    {:template :hero-section
     :data {:section-tag {:label "Recepción" :color "recepcion"}
            :id "recepcion-almacenamiento"
            :hero {:kicker "Operaciones"
                   :title c/recepcion-title
                   :subtitle c/recepcion-bandeja
                   :meter-value "-18°"
                   :meter-caption "Temperatura de recepción"}
            :blocks [{:type :text-block :content (nth c/recepcion-intro 0)}
                     {:type :text-block :content (nth c/recepcion-intro 1)}
                     {:type :text-block :content (nth c/recepcion-intro 2)}]}}

    ;; 10. Reception — inspection criteria + storage intro + maintenance/expiry/temp controls
    {:template :blocks
     :data {:section-tag {:label "Recepción" :color "recepcion"}
            :id "recepcion-inspeccion"
            :intro [(nth c/recepcion-intro 3) (nth c/recepcion-intro 4)]
            :blocks [{:type :criteria-table :icon "clipboard-check"
                      :title "Criterios de inspección al recibir"
                      :headers ["¿Qué?" "¿Cómo?" "Criterio"]
                      :items c/recepcion-criteria}
                     {:type :info-grid :icon "thermometer-snowflake"
                      :title "Mantenimiento, Vencimiento y Controles de Temperatura"
                      :items c/recepcion-mantenimiento-vencimiento-controles}]}}

    ;; 11. Storage — reference table (protocolo limpieza, normas utensilios, seguridad productos)
    {:template :blocks
     :data {:section-tag {:label "Recepción" :color "recepcion"}
            :id "recepcion-almacenamiento-detalle"
            :header {:icon "archive"
                     :kicker "Almacenamiento"
                     :title "Reglas de Almacenamiento"}
            :intro c/almacenamiento-intro
            :blocks [{:type :ref-table :icon "table" :title "Guía de referencia"
                      :items c/almacenamiento-protocolo-limpieza}]}}

    ;; 12. Storage rules + Temperature controls & alerts
    {:template :blocks
     :data {:section-tag {:label "Recepción" :color "recepcion"}
            :id "recepcion-controles"
            :blocks [{:type :info-grid :icon "archive" :title "Resumen de reglas"
                      :items c/almacenamiento-rules}
                     {:type :info-grid :icon "thermometer" :title "Controles de temperatura y vencimiento"
                      :items c/temp-control-rules}
                     {:type :image-grid
                      :featured? false
                      :items c/recepcion-controles-gallery}
                     {:type :highlight
                      :icon "sparkles"
                      :title "Puntos críticos del turno"
                      :items c/recepcion-alertas-clave}]}}

    ;; 11. Cleaning protocol (blocks with header)
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-desinfeccion"
            :header {:icon "spray-can"
                     :kicker "Higiene del entorno"
                     :title c/limpieza-title
                     :pills [{:icon "droplets" :label "Limpiar" :verb "Verbo clave: LAVAR"
                              :text c/limpieza-def-limpiar :css-class "def-limpiar"}
                             {:icon "shield-check" :label "Desinfectar" :verb "Verbo clave: DESINFECTAR"
                              :text c/limpieza-def-desinfectar :css-class "def-desinfectar"}]}
            :blocks [{:type :product-grid :icon "flask-conical" :title "Selección de productos"
                      :legend true
                      :items c/limpieza-productos}]
            :callout c/limpieza-prohibido}}

    ;; 12. Spread intro — pairs with protocolo; next spread = operativa + registro
    {:template :full-image
     :data (assoc c/limpieza-spread-intro :id "limpieza-spread-intro"
                  :section-tag {:label "Limpieza" :color "limpieza"})}

    ;; Vitrina de helados — limpieza interior detallada
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-vitrina"
            :header {:icon "refrigerator"
                     :kicker "Limpieza diaria"
                     :title "Vitrina de Helados"}
            :blocks [{:type :image-block :src "ref-vitrina-interior.png" :alt "Limpieza interior de la vitrina"}
                     {:type :wash-grid :icon "sparkles" :title "Limpiar la vitrina por dentro"
                      :items c/limpieza-vitrina-interior-steps}
                     {:type :wash-grid :icon "eye" :title "Exterior y colocación"
                      :items c/limpieza-vitrina-exterior-steps}
                     {:type :image-block :src "ref-vitrina-exterior.png" :alt "Vidrio exterior y colocación de bandejas"
                      :caption "Vidrio exterior limpio y bandejas correctamente colocadas."}]}}

    ;; Cámara fría — imagen + procedimiento
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-camara-fria"
            :header {:icon "snowflake"
                     :kicker "Limpieza programada"
                     :title "Limpieza de la Cámara Fría"}
            :blocks [{:type :image-block :src "ref-camara-fria-limpieza.png" :alt "Preparación y lavado de cámara fría"}
                     {:type :wash-grid :icon "list-checks" :title "Procedimiento paso a paso"
                      :items c/limpieza-camara-fria-steps}]}}

    ;; Cámara fría — reorganización y frecuencia
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-camara-fria-2"
            :header {:icon "snowflake"
                     :kicker "Cámara fría"
                     :title "Reorganización y Frecuencia"}
            :blocks [{:type :image-block :src "ref-camara-fria-reorganizar.png" :alt "Reorganización y encendido"
                      :caption "Reorganizar productos y encender el equipo."}
                     {:type :sched-grid :icon "calendar-check" :title "Frecuencia recomendada"
                      :items c/limpieza-camara-fria-frecuencia}]}}

    ;; Limpieza profunda del local
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-profunda"
            :header {:icon "sparkles"
                     :kicker "Mensual"
                     :title "Limpieza Profunda del Local"}
            :intro c/limpieza-profunda-intro
            :blocks [{:type :image-block :src "ref-limpieza-profunda.png" :alt "Limpieza profunda"
                      :caption "Limpieza profunda de paredes, pisos y mobiliario — frecuencia mensual."}
                     {:type :wash-grid :icon "house" :title "Procedimiento de limpieza profunda"
                      :items c/limpieza-profunda-steps}]}}

    ;; Pisos, mesas, sillas y armarios
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-areas"
            :header {:icon "footprints"
                     :kicker "Limpieza de áreas"
                     :title "Pisos, Mesas, Sillas y Armarios"}
            :blocks [{:type :image-block :src "ref-limpieza-areas.png" :alt "Limpieza de áreas" :float :right
                      :caption "Áreas de trabajo: barrido, desinfección de pisos y limpieza de mesas, sillas y armarios."}
                     {:type :info-grid :icon "footprints" :title "Pisos — preparación y almacén"
                      :items c/limpieza-pisos-rules}
                     {:type :info-grid :icon "armchair" :title "Mesas y sillas"
                      :items c/limpieza-mesas-sillas-rules}
                     {:type :info-grid :icon "archive" :title "Armarios"
                      :items c/limpieza-armarios-rules}]}}

    ;; Neveras, scooper y equipos
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-equipos"
            :header {:icon "refrigerator"
                     :kicker "Limpieza de equipos"
                     :title "Neveras, Scooper y Equipos"}
            :blocks [{:type :info-grid :icon "refrigerator" :title "Neveras de tartas y producto"
                      :items c/limpieza-nevera-tartas-rules}
                     {:type :wash-grid :icon "cup-soda" :title "Recipiente de lavado de Scooper"
                      :items c/limpieza-scooper-steps}
                     {:type :image-block :src "ref-limpieza-equipos.png" :alt "Limpieza de equipos"}
                     {:type :info-grid :icon "layout-dashboard" :title "Mesetas, fregadero y bandejas"
                      :items c/limpieza-mesetas-rules}]}}

    ;; Utensilios diversos + Microondas
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-utensilios"
            :header {:icon "utensils"
                     :kicker "Limpieza de utensilios"
                     :title "Utensilios y Accesorios"}
            :blocks [{:type :info-grid :icon "utensils" :title "Utensilios del área de servicio"
                      :items c/limpieza-utensilios-rules}
                     {:type :info-grid :icon "microwave" :title "Microondas"
                      :items c/limpieza-microondas-rules}]}}

    ;; Manejo de utensilios de limpieza
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-manejo-utensilios"
            :header {:icon "shirt"
                     :kicker "Buenas prácticas"
                     :title "Manejo de Utensilios de Limpieza"}
            :intro c/limpieza-manejo-utensilios-intro
            :blocks [{:type :info-grid :icon "eraser" :title "Cuidado de utensilios de limpieza"
                      :items c/limpieza-utensilios-manejo-rules}]}}

    ;; Código de colores de paños — página completa
    {:template :full-image
     :data {:id "limpieza-panos-colores"
            :img "ref-panos-colores.png"
            :alt "Código de colores de paños"
            :caption "Cada área tiene su paño de color asignado para evitar contaminación cruzada."}}

    ;; Cleaning operations overview + schedule
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-operativa"
            :blocks [{:type :wash-grid :icon "refrigerator" :title "Vitrina de helados — resumen apertura"
                      :items c/limpieza-vitrina-steps}
                     {:type :sched-grid :icon "calendar-check" :title "Frecuencia de limpieza"
                      :items c/limpieza-schedule}]}}

    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-calendario"
            :header {:icon "calendar-days"
                     :kicker "Planificación"
                     :title "Calendario de Limpieza"}
            :blocks [{:type :sched-grid :icon "calendar-days" :title "Calendario operativo de limpieza"
                      :items []
                      :calendar c/limpieza-calendar}]}}

    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-registro"
            :header {:icon "clipboard-pen-line"
                     :kicker "Control operativo"
                     :title "Registro de Limpieza"}
            :blocks [{:type :highlight
                      :icon "badge-check"
                      :title "Cómo completar el registro"
                      :items c/limpieza-registro-tips}
                     {:type :registro-sheet :icon "clipboard-pen-line" :title "Ejemplo de registro diario"
                      :modes c/limpieza-registro-modos
                      :default-mode "diario"
                      :meta c/limpieza-registro-meta
                      :branches c/limpieza-registro-sucursales
                      :default-branch "suc-centro"
                      :items c/limpieza-registro-ejemplo}
                     {:type :store-map :icon "map-pin" :title "Nuestras sucursales"
                      :items c/valentino-sucursales}]}}

    ;; Basurero, inventario y desinfectantes
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-basurero"
            :header {:icon "trash-2"
                     :kicker "Manejo de residuos"
                     :title "Basurero y Residuos"}
            :intro "El manejo correcto de residuos es esencial para evitar plagas, malos olores y contaminación cruzada. Cada turno debe asegurar que los basureros estén limpios, con bolsa, tapa y en buen estado."
            :blocks [{:type :info-grid :icon "trash-2" :title "Recolección de residuos (área de preparación)"
                      :items c/limpieza-basurero-recoleccion}
                     {:type :info-grid :icon "truck" :title "Retiro y almacenamiento de residuos"
                      :items c/limpieza-basurero-retiro}
                     {:type :info-grid :icon "shopping-bag" :title "Mantenimiento de basureros"
                      :items c/limpieza-basurero-extra}]}}

    ;; Desinfectantes
    {:template :blocks
     :data {:section-tag {:label "Limpieza" :color "limpieza"}
            :id "limpieza-desinfectantes"
            :header {:icon "flask-conical"
                     :kicker "Productos aprobados"
                     :title "Desinfectantes"}
            :intro c/desinfectantes-intro
            :blocks [{:type :info-grid :icon "clipboard-list" :title "Inventario y pedidos"
                      :items c/inventario-diario-rules}
                     {:type :image-block
                      :src "ref-basurero.png"
                      :alt "Protocolo de manejo de residuos"
                      :caption "Recolección, retiro y almacenamiento temporal de residuos."}
                     {:type :criteria-table :icon "flask-conical"
                      :title "Desinfectantes posibles"
                      :headers ["Desinfectante" "Concentración típica" "Observación"]
                      :items c/desinfectantes-tabla}]}}

    ;; Mantenimiento — introducción y equipos críticos
    {:template :blocks
     :data {:section-tag {:label "Mantenimiento" :color "mantenimiento"}
            :id "mantenimiento"
            :header {:icon "wrench"
                     :kicker "Programa preventivo"
                     :title "Mantenimiento"}
            :intro c/mantenimiento-intro
            :blocks [{:type :info-grid :icon "cog" :title "Equipos críticos"
                      :items c/mantenimiento-equipos-criticos}]}}

    ;; Mantenimiento — imagen completa
    {:template :full-image
     :data {:id "mantenimiento-visual"
            :section-tag {:label "Mantenimiento" :color "mantenimiento"}
            :img "ref-mantenimiento.png"
            :alt "Equipos y sistemas de la tienda"
            :title "Equipos y Sistemas"
            :subtitle "Mantenimiento preventivo de los equipos críticos de la tienda."}}

    ;; Plan de mantenimiento preventivo
    {:template :blocks
     :data {:section-tag {:label "Mantenimiento" :color "mantenimiento"}
            :id "mantenimiento-plan"
            :header {:icon "calendar-check"
                     :kicker "Plan preventivo"
                     :title "Plan de Mantenimiento"}
            :blocks [{:type :criteria-table :icon "wrench"
                      :title "Plan de Mantenimiento Preventivo"
                      :headers ["Equipo" "Actividad" "Frecuencia"]
                      :items c/mantenimiento-plan}]}}

    ;; Servicio al cliente
    {:template :blocks
     :data {:section-tag {:label "Servicio" :color "servicio"}
            :id "servicio-cliente"
            :header {:icon "smile"
                     :kicker "Atención al público"
                     :title "Servicio al Cliente"}
            :intro c/servicio-cliente-intro
            :blocks [{:type :info-grid :icon "heart" :title "Normas de servicio"
                      :items c/servicio-cliente-rules}
                     {:type :image-grid :featured? false
                      :items [{:img "ref-servicio-cliente.png" :kicker "Valentino" :title "Servicio con calidad y calidez"}]}]}}

    ;; Preparación — conos, vasos, empaques
    {:template :blocks
     :data {:section-tag {:label "Servicio" :color "servicio"}
            :id "servicio-preparacion"
            :header {:icon "ice-cream-cone"
                     :kicker "Preparación"
                     :title "Forma de Servir los Helados"}
            :blocks [{:type :wash-grid :icon "ice-cream-cone" :title "Conos y vasos (75g / 100g / 130g)"
                      :items c/servicio-conos-vasos}
                     {:type :wash-grid :icon "package" :title "Empaques para llevar"
                      :items c/servicio-empaques-llevar}
                     {:type :image-grid :featured? false
                      :items [{:img "ref-servicio-helados.png" :kicker "Servicio" :title "Conos, vasos y empaques"}]}
                     {:type :wash-grid :icon "cup-soda" :title "Granizado"
                      :items c/servicio-granizado-steps}
                     {:type :wash-grid :icon "blend" :title "Malteada"
                      :items c/servicio-malteada-steps}]}}

    ;; Preparación — fondant, crepe
    {:template :blocks
     :data {:section-tag {:label "Servicio" :color "servicio"}
            :id "servicio-postres"
            :header {:icon "cake"
                     :kicker "Preparación"
                     :title "Postres y Especialidades"}
            :blocks [{:type :wash-grid :icon "cake" :title "Fondant"
                      :items c/servicio-fondant-steps}
                     {:type :wash-grid :icon "utensils" :title "Crepes"
                      :items c/servicio-crepe-steps}
                     {:type :image-grid :featured? false
                      :items [{:img "ref-servicio-malteada.png" :kicker "Bebidas" :title "Granizados y malteadas"}]}]}}

    ;; Preparación — banana split, brownie
    {:template :blocks
     :data {:section-tag {:label "Servicio" :color "servicio"}
            :id "servicio-postres-2"
            :header {:icon "banana"
                     :kicker "Preparación"
                     :title "Banana Split y Brownie"}
            :blocks [{:type :image-block :src "ref-servicio-postres.png" :alt "Postres y especialidades"
                      :caption "Fondant, crepes, banana split y brownie — presentación Valentino."}
                     {:type :wash-grid :icon "banana" :title "Banana Split"
                      :items c/servicio-banana-split-steps}
                     {:type :wash-grid :icon "square" :title "Brownie a la Mode"
                      :items c/servicio-brownie-steps}]}}

    ;; Quejas
    {:template :blocks
     :data {:section-tag {:label "Servicio" :color "servicio"}
            :id "quejas"
            :header {:icon "message-circle"
                     :kicker "Servicio al cliente"
                     :title "Manejo de Quejas"}
            :blocks [{:type :image-block
                      :src "ref-quejas.png"
                      :alt "El cliente siempre tiene la razón"
                      :caption "Una queja bien manejada puede convertir un cliente insatisfecho en un cliente leal."}
                     {:type :text-block :content c/quejas-intro}
                     {:type :info-grid :icon "heart-handshake" :title "Estrategia: Escuchar — Disculparse — Resolver — Dar gracias"
                      :items c/quejas-estrategia}]}}

    ;; Section divider — Riesgos
    {:template :full-image
     :data {:section-tag {:label "Riesgos" :color "riesgos"}
            :id "riesgos-divider"
            :img "cuatro-familias-riesgo.jpg"
            :alt "Cuatro familias de riesgo: microbiológico, físico, alérgenos y químico"
            :kicker "Seguridad Alimentaria"
            :title "Cuatro Familias de Riesgo"
            :subtitle "Todo operador debe identificar y prevenir los peligros que pueden comprometer la seguridad del producto y la salud del consumidor."
            :bullets ["Microbiológico — bacterias y toxinas cuando falla el frío, la higiene o se supera la caducidad."
                      "Físico — cuerpos extraños (vidrio, metal, plástico, pelo) por equipos defectuosos o malas prácticas."
                      "Alérgenos — leche, frutos secos, gluten: contaminación cruzada entre sabores, cucharones y superficies."
                      "Químico — desinfectantes o detergentes mal guardados, sin etiqueta o almacenados junto al producto."]}}

    ;; 16. Guía visual — cada bola = una familia
    {:template :blocks
     :data {:section-tag {:label "Riesgos" :color "riesgos"}
            :id "riesgos-familias-intro"
            :intro c/riesgos-familias-intro-paras
            :blocks [{:type :risk-familias-bolas :icon "circle-dot" :title "Las cuatro familias de riesgo"
                      :items c/riesgos-familias-bolas}]
            :callout "Las páginas siguientes profundizan en cada familia: señales, causas y prevención en tienda."}}

    ;; 17–20. Risk pages
    {:template :risk
     :data (assoc (nth c/risk-families 0) :images-for-sections c/images-for-sections
                  :section-tag {:label "Riesgos" :color "riesgos"})}
    {:template :risk
     :data (assoc (nth c/risk-families 1) :images-for-sections c/images-for-sections
                  :section-tag {:label "Riesgos" :color "riesgos"})}
    {:template :risk
     :data (assoc (nth c/risk-families 2) :images-for-sections c/images-for-sections
                  :section-tag {:label "Riesgos" :color "riesgos"})}
    {:template :risk
     :data (assoc (nth c/risk-families 3) :images-for-sections c/images-for-sections
                  :section-tag {:label "Riesgos" :color "riesgos"})}

    ;; 19. Glossary index
    {:template :glossary-index
     :data {:section-tag {:label "Glosario" :color "glosario"}
            :title c/glosario-title
            :terms c/glosario-terms}}

    ;; 20-26. Glossary detail pages
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 0) :all-terms c/glosario-terms
                  :section-tag {:label "Glosario" :color "glosario"})}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 1) :all-terms c/glosario-terms
                  :section-tag {:label "Glosario" :color "glosario"})}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 2) :all-terms c/glosario-terms
                  :section-tag {:label "Glosario" :color "glosario"})}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 3) :all-terms c/glosario-terms
                  :section-tag {:label "Glosario" :color "glosario"})}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 4) :all-terms c/glosario-terms
                  :section-tag {:label "Glosario" :color "glosario"})}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 5) :all-terms c/glosario-terms
                  :section-tag {:label "Glosario" :color "glosario"})}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 6) :all-terms c/glosario-terms
                  :section-tag {:label "Glosario" :color "glosario"})}

    ;; 27. Credits
    {:template :credits
     :data {:section-tag {:label "Créditos" :color "creditos"}
            :title c/credits-title
            :by    c/credits-by
            :logos [{:src "logo-abt.png" :alt "Agrobiotek Internacional"}
                    {:src "logo-greb.png" :alt "GREB"}]
            :orgs  c/credits-orgs
            :legal c/credits-legal}}]})
