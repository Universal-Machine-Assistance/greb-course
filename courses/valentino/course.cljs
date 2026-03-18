(ns valentino.course
  "Full course definition map for Valentino food hygiene manual."
  (:require [valentino.content :as c]))

(def course
  {:meta  {:id          "valentino"
           :org         "valentino"
           :slug        "guia_de_higiene_alimentaria"
           :title       "Guía de Higiene Alimentaria — Helados Valentino"
           :description "Manual de higiene y seguridad alimentaria para las tiendas de Helados Valentino."
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
          :entries (subvec c/index-entries 0 4)}
         {:label "Higiene del Personal"
          :entries (subvec c/index-entries 4 8)}
         {:label "Recepción y Almacenamiento"
          :entries (subvec c/index-entries 8 10)}
         {:label "Limpieza y Desinfección"
          :entries (subvec c/index-entries 10 12)}
         {:label "Riesgos"
          :entries (subvec c/index-entries 12 17)}
         {:label "Glosario"
          :entries (into [(nth c/index-entries 17)]
                         (map-indexed (fn [i t] {:id (:id t) :label (:term t) :page (+ 19 i)})
                                      c/glosario-terms))}
         {:label "Créditos"
          :entries [(nth c/index-entries 18)]}]

   :pages
   [;; 1. Cover
    {:template :cover
     :data {:hero-img "ValenAgostoA40of49-scaled.jpg"
            :logo     "LOGO-VALENTINOTIPOBLANCOSMANUEVO.png"
            :title    "Guía de Higiene Alimentaria"
            :subtitle "para Tiendas de Helados Valentino"}}

    ;; 2. Table of Contents
    {:template :toc-card-grid
     :data {:title    c/contenido-title
            :subtitle c/contenido-subtitle
            :sections c/contenido-sections}}

    ;; 3. Index
    {:template :index
     :data {:title   c/index-title
            :entries c/index-entries
            :sections c/contenido-sections
            :groups [{:label "Presentación"
                      :items (take 4 c/index-entries)
                      :icon-default "file-text"}
                     {:label "Contenido"
                      :type :sections
                      :items c/contenido-sections}
                     {:label "Riesgos"
                      :items (mapv (fn [{:keys [id icon title]}] {:id id :label title :icon icon :page (get (into {} (map (juxt :id :page) c/index-entries)) id)})
                                   c/risk-families)
                      :icon-default "file-text"}
                     {:label "Referencia"
                      :items [{:id "glosario" :label "Glosario de Términos" :icon "book-open"
                               :page (get (into {} (map (juxt :id :page) c/index-entries)) "glosario")}]}
                     {:label "Créditos"
                      :items [{:id "creditos" :label "Créditos" :icon "copyright"
                               :page (get (into {} (map (juxt :id :page) c/index-entries)) "creditos")}]}]}}

    ;; 4. Introduction
    {:template :intro
     :data {:eyebrow "Manual · Higiene Alimentaria"
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

    ;; 5. Handwash page (handwash-head + duo-grid)
    {:template :blocks
     :data {:id "lavado-y-guantes"
            :head-section {:kicker "Protocolo crítico" :title "Lavado de manos"}
            :duo-grid [{:type :chip-row :icon "hand-metal" :title "Estación de lavado"
                        :items c/handwash-station}
                       {:type :frequency-list :icon "timer-reset" :title "Frecuencia"
                        :items c/handwash-frequency}]
            :blocks []}}

    ;; 6. Handwash steps page
    {:template :blocks
     :data {:id "lavado-tecnica"
            :blocks [{:type :wash-grid :icon "waves" :title "Técnica de lavado — 6 pasos"
                      :items c/handwash-steps}
                     {:type :info-grid :icon "shield-plus" :title "Uso de guantes"
                      :items c/glove-rules}]}}

    ;; 7. Personal hygiene (hero-section)
    {:template :hero-section
     :data {:id "higiene-personal"
            :hero {:kicker "Atención al cliente"
                   :title c/higiene-personal-title
                   :subtitle c/hygiene-subtitle
                   :meter-value "100%"
                   :meter-caption "Listo para abrir"}
            :blocks [{:type :mission-grid :icon "clipboard-list" :title "Checklist de apertura"
                      :items c/hygiene-checklist}
                     {:type :info-grid :icon "scan-face" :title "Avatar higiénico"
                      :items c/hygiene-avatar-rules}]}}

    ;; 8. Hygiene discipline (blocks, no hero)
    {:template :blocks
     :data {:id "higiene-disciplina"
            :blocks [{:type :info-grid :icon "briefcase-business" :title "Disciplina del uniforme"
                      :items c/hygiene-uniform-rules}
                     {:type :info-grid :icon "lock-keyhole" :title "Lockers y cambio de ropa"
                      :items c/hygiene-locker-rules}
                     {:type :visitor-grid :icon "users-round" :title "Visitantes"
                      :items c/hygiene-visitor-rules}
                     {:type :info-grid :icon "heart-pulse" :title "Salud del personal"
                      :items c/hygiene-health-rules}]}}

    ;; 9. Reception (hero-section)
    {:template :hero-section
     :data {:id "recepcion-almacenamiento"
            :hero {:kicker "Operaciones"
                   :title c/recepcion-title
                   :subtitle c/recepcion-bandeja
                   :meter-value "-18°"
                   :meter-caption "Temperatura de recepción"}
            :blocks [{:type :criteria-table :icon "clipboard-check"
                      :title "Criterios de inspección al recibir"
                      :headers ["¿Qué?" "¿Cómo?" "Criterio"]
                      :items c/recepcion-criteria}]}}

    ;; 10. Storage & controls (blocks)
    {:template :blocks
     :data {:id "recepcion-controles"
            :blocks [{:type :info-grid :icon "archive" :title "Reglas de almacenamiento"
                      :items c/almacenamiento-rules}
                     {:type :info-grid :icon "thermometer" :title "Controles de temperatura y vencimiento"
                      :items c/temp-control-rules}]}}

    ;; 11. Cleaning protocol (blocks with header)
    {:template :blocks
     :data {:id "limpieza-desinfeccion"
            :header {:icon "spray-can"
                     :kicker "Higiene del entorno"
                     :title c/limpieza-title
                     :pills [{:icon "droplets" :label "Limpiar" :text c/limpieza-def-limpiar :css-class "def-limpiar"}
                             {:icon "shield-check" :label "Desinfectar" :text c/limpieza-def-desinfectar :css-class "def-desinfectar"}]}
            :blocks [{:type :product-grid :icon "flask-conical" :title "Selección de productos"
                      :legend true
                      :items c/limpieza-productos}]
            :callout c/limpieza-prohibido}}

    ;; 12. Cleaning operations (blocks)
    {:template :blocks
     :data {:id "limpieza-operativa"
            :blocks [{:type :wash-grid :icon "refrigerator" :title "Vitrina de helados — apertura"
                      :items c/limpieza-vitrina-steps}
                     {:type :sched-grid :icon "calendar-check" :title "Frecuencia de limpieza"
                      :items c/limpieza-schedule}]}}

    ;; 13. Section divider — Riesgos
    {:template :full-image
     :data {:id "riesgos-divider"
            :img "ValenAgostoA40of49-scaled.jpg"
            :alt "Helados Valentino"
            :kicker "Seguridad Alimentaria"
            :title "Cuatro Familias de Riesgo"
            :subtitle "Microbiológico · Físico · Alérgenos · Químico — Los riesgos que todo operador debe conocer y prevenir."}}

    ;; 14-17. Risk pages
    {:template :risk
     :data (assoc (nth c/risk-families 0) :images-for-sections c/images-for-sections)}
    {:template :risk
     :data (assoc (nth c/risk-families 1) :images-for-sections c/images-for-sections)}
    {:template :risk
     :data (assoc (nth c/risk-families 2) :images-for-sections c/images-for-sections)}
    {:template :risk
     :data (assoc (nth c/risk-families 3) :images-for-sections c/images-for-sections)}

    ;; 18. Glossary index
    {:template :glossary-index
     :data {:title c/glosario-title
            :terms c/glosario-terms}}

    ;; 18-24. Glossary detail pages
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 0) :all-terms c/glosario-terms)}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 1) :all-terms c/glosario-terms)}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 2) :all-terms c/glosario-terms)}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 3) :all-terms c/glosario-terms)}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 4) :all-terms c/glosario-terms)}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 5) :all-terms c/glosario-terms)}
    {:template :glossary-detail
     :data (assoc (nth c/glosario-terms 6) :all-terms c/glosario-terms)}

    ;; 25. Credits
    {:template :credits
     :data {:title c/credits-title
            :by    c/credits-by
            :logos [{:src "logo-abt.png" :alt "Agrobiotek Internacional"}
                    {:src "logo-greb.png" :alt "GREB" :dark? true}]
            :orgs  c/credits-orgs
            :legal c/credits-legal}}]})
