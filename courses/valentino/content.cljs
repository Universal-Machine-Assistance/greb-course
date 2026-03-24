(ns valentino.content
  "All content data for the Valentino food hygiene course.")

;; ── Index ───────────────────────────────────────────────────────
(def index-title "Índice")

(def index-entries
  [;; 1–5 Presentación
   {:id "portada"                  :label "Portada"                         :page 1}
   {:id "contenido"                :label "Contenido"                       :page 2}
   {:id "indice"                   :label "Índice"                          :page 3}
   {:id "indice-2"                 :label "Índice (cont.)"                  :page 4}
   {:id "introduccion"             :label "Introducción"                    :page 5}
   ;; 6–10 Higiene del Personal
   {:id "lavado-y-guantes"         :label "Lavado de Manos — Protocolo"     :page 6}
   {:id "lavado-tecnica"           :label "Lavado de Manos — Técnica"       :page 7}
   {:id "higiene-personal"         :label "Higiene Personal — Checklist"    :page 8}
   {:id "higiene-disciplina"       :label "Higiene Personal — Disciplina"   :page 9}
   {:id "higiene-instalaciones"    :label "Instalaciones y Salud"           :page 10}
   ;; 11–14 Recepción y Almacenamiento
   {:id "recepcion-almacenamiento"        :label "Recepción — Introducción"       :page 11}
   {:id "recepcion-inspeccion"             :label "Recepción — Inspección"         :page 12}
   {:id "recepcion-almacenamiento-detalle" :label "Almacenamiento — Reglas"        :page 13}
   {:id "recepcion-controles"              :label "Recepción — Controles"          :page 14}
   ;; 15–28 Limpieza y Desinfección
   {:id "limpieza-desinfeccion"    :label "Limpieza — Protocolo"            :page 15}
   {:id "limpieza-spread-intro"    :label "Limpieza — Portada sección"      :page 16}
   {:id "limpieza-vitrina"         :label "Limpieza — Vitrina de Helados"   :page 17}
   {:id "limpieza-camara-fria"     :label "Limpieza — Cámara Fría"          :page 18}
   {:id "limpieza-profunda"        :label "Limpieza — Profunda del Local"   :page 19}
   {:id "limpieza-areas"           :label "Limpieza — Pisos, Mesas, Armarios" :page 20}
   {:id "limpieza-equipos"         :label "Limpieza — Equipos y Scooper"    :page 21}
   {:id "limpieza-utensilios"      :label "Limpieza — Utensilios"           :page 22}
   {:id "limpieza-manejo-utensilios" :label "Limpieza — Manejo Utensilios"  :page 23}
   {:id "limpieza-operativa"       :label "Limpieza — Operativa"            :page 24}
   {:id "limpieza-calendario"      :label "Limpieza — Calendario Operativo" :page 25}
   {:id "limpieza-registro"        :label "Limpieza — Ejemplo de Registro"  :page 26}
   {:id "limpieza-basurero"        :label "Basurero e Inventario"           :page 27}
   {:id "limpieza-desinfectantes"  :label "Desinfectantes"                  :page 28}
   ;; 29–30 Mantenimiento
   {:id "mantenimiento"            :label "Mantenimiento — Equipos"         :page 29}
   {:id "mantenimiento-plan"       :label "Mantenimiento — Plan Preventivo" :page 30}
   ;; 31–35 Servicio al Cliente
   {:id "servicio-cliente"         :label "Servicio al Cliente"             :page 31}
   {:id "servicio-preparacion"     :label "Servicio — Helados y Bebidas"    :page 32}
   {:id "servicio-postres"         :label "Servicio — Postres"              :page 33}
   {:id "servicio-postres-2"       :label "Servicio — Banana Split y Brownie" :page 34}
   {:id "quejas"                   :label "Manejo de Quejas"                :page 35}
   ;; 36+ Riesgos
   {:id "riesgos-divider"           :label "Familias de Riesgo"              :page 36}
   {:id "riesgos-familias-intro"    :label "Riesgos — Las cuatro familias"   :page 37}
   {:id "riesgo-microbiologico"     :label "Riesgo microbiológico"           :page 38}
   {:id "riesgo-fisico"             :label "Riesgo físico"                   :page 39}
   {:id "riesgo-alergenos"          :label "Riesgo de alérgenos"             :page 40}
   {:id "riesgo-quimico"            :label "Riesgo químico"                  :page 41}
   {:id "glosario"                  :label "Glosario de Términos"            :page 42}
   {:id "creditos"                  :label "Créditos"                        :page 50}])

;; ── TOC sections ────────────────────────────────────────────────
(def contenido-title "Guía de Higiene de los Alimentos para las Tiendas de Valentino")
(def contenido-subtitle "Contenido")

(def contenido-sections
  [{:id "higiene-personal" :img "toc-higiene-personal.png" :title "Higiene del Personal"
    :items [{:label "Uniforme"           :ok true}
            {:label "Lavado de Manos"    :ok true}
            {:label "Guantes"            :ok true}
            {:label "Salud del personal" :ok true}]}
   {:id "recepcion-almacenamiento" :img "toc-recepcion.png" :title "Recepción y Almacenamiento"
    :items [{:label "Recepción del producto"    :ok true}
            {:label "Mantenimiento"             :ok true}
            {:label "Vencimiento"               :ok true}
            {:label "Controles de Temperatura"  :ok true}]}
   {:id "limpieza-desinfeccion" :img "toc-limpieza.png" :title "Limpieza y Desinfección"
    :items [{:label "Protocolo de superficies"  :ok true}
            {:label "Vitrina y cámara fría"     :ok true}
            {:label "Áreas y equipos"           :ok true}
            {:label "Utensilios y paños"        :ok true}
            {:label "Basurero y desinfectantes" :ok true}]}
   {:id "mantenimiento" :img "ref-mantenimiento.png" :title "Mantenimiento"
    :items [{:label "Equipos críticos"     :ok true}
            {:label "Plan preventivo"      :ok true}]}
   {:id "servicio-cliente" :img "ref-servicio-cliente.png" :title "Servicio al Cliente"
    :items [{:label "Normas de servicio"   :ok true}
            {:label "Preparación helados"  :ok true}
            {:label "Postres y bebidas"    :ok true}
            {:label "Manejo de quejas"     :ok true}]}
   {:id "riesgo-microbiologico" :img "toc-riesgos.png" :title "Riesgos Alimentarios"
    :items [{:label "Microbiológico"  :ok true}
            {:label "Físico"          :ok true}
            {:label "Alérgenos"       :ok true}
            {:label "Químico"         :ok true}]}])

;; ── Hygiene ─────────────────────────────────────────────────────
(def higiene-personal-title "Higiene del Personal")
(def hygiene-subtitle
  "Una estación impecable empieza por una rutina visible, ordenada y constante.")

(def hygiene-checklist
  [{:label "Gorro de chef" :tone "orange"}
   {:label "Uniforme limpio de estación" :tone "green"}
   {:label "Guantes desechables" :tone "yellow"}
   {:label "Zapatos limpios y cerrados" :tone "orange"}
   {:label "Uñas cortas, sin esmalte ni postizas" :tone "green"}
   {:label "Sin joyas visibles ni reloj" :tone "yellow"}])

(def hygiene-health-rules
  [{:title "Síntomas de enfermedad"
    :text "El personal con resfriado, gripe o infecciones nasales debe informar al responsable y ser retirado del área de servicio. Usar mascarilla si hay infección nasal u oral."}
   {:title "Heridas y cortes"
    :text "Desinfectar, proteger con curita y colocarse guantes encima. Los guantes son obligatorios para todas las tareas si hay herida abierta. Cambiarlos al interrumpir el trabajo o manipular contaminantes."}
   {:title "Sin medicamentos en zona de alimentos"
    :text "Está prohibido consumir medicamentos en la zona de preparación de alimentos."}])

(def hygiene-avatar-rules
  [{:title "Cabello controlado"
    :text "El pelo largo debe ir totalmente recogido dentro del gorro marrón tipo chef."}
   {:title "Uñas seguras"
    :text "Se prohíben uñas postizas, esmalte y uñas largas: pueden desprenderse y convertirse en fuente de contaminación física."}
   {:title "Joyería prohibida"
    :text "Se prohíbe el uso de joyas visibles por motivos de salud y seguridad, así como por el riesgo de objetos extraños. Esto incluye relojes y piercings. Solo se permiten anillos de boda sin engarzar. Se aceptan joyas ocultas."}
   {:title "Maquillaje discreto"
    :text "El personal femenino puede usar maquillaje ligero."}])

(def hygiene-discipline-intro
  "El personal de las tiendas Valentino debe proyectar una imagen profesional e higiénica en todo momento. El cumplimiento de estas normas de vestimenta y conducta es obligatorio para garantizar la seguridad alimentaria y la confianza del cliente.")

(def hygiene-uniform-rules
  [{:title "Antes de entrar"
    :text "El uniforme no debe usarse en la calle. Llevarlo en un bulto y ponérselo dentro de la tienda para evitar importar contaminación."}
   {:title "Estado del uniforme"
    :text "La ropa de trabajo debe estar impecable. Sustituir inmediatamente piezas con manchas o roturas."}
   {:title "Calzado"
    :text "Zapatos cerrados y limpios en todo momento. No se permite calzado abierto ni sucio."}
   {:title "Responsabilidad diaria"
    :text "Cada colaborador es responsable de mantener sus uniformes limpios para cada jornada."}])

(def hygiene-uniform-zones
  ["Área trasera" "Área de venta" "Cuarto frío"])

(def hygiene-uniform-checklist
  [{:item "Gorro higiénico desechable"       :icon "hard-hat"     :qty "—"  :zones [true  true  true]}
   {:item "Redecilla para el cabello"         :icon "scan-face"    :qty "—"  :zones [true  true  true]}
   {:item "Gorra Valentino (marrón con logo)" :icon "crown"        :qty "2"  :zones [false true  false]}
   {:item "Poloshirt blanco con logo"         :icon "shirt"        :qty "2"  :zones [true  true  true]}
   {:item "Pantalón caqui"                    :icon "columns-2"    :qty "1"  :zones [true  true  true]}
   {:item "Delantal marrón con logo"          :icon "chef-hat"     :qty "2"  :zones [true  true  false]}
   {:item "Guantes desechables"               :icon "hand"         :qty "—"  :zones [true true true]
    :note "Pinzas de acero inoxidable para servicio al cliente"}
   {:item "Zapatos cerrados y limpios"        :icon "footprints"   :qty "1"  :zones [true  true  true]}
   {:item "Uñas cortas, sin postizas ni esmalte" :icon "scissors"  :qty "—"  :zones [true true true]}
   {:item "Joyas y reloj"                     :icon "gem"          :qty "—"  :zones [:prohibido :prohibido :prohibido]}])

(def hygiene-locker-rules
  [{:title "Llegada y delantal"
    :text "El personal llega con ropa de calle y se coloca el delantal."}
   {:title "Locker individual"
    :text "Las pertenencias personales se guardan en un locker individual que el personal mantiene limpio."}
   {:title "Separación de ropa"
    :text "La ropa limpia no debe entrar en contacto con la ropa ni los zapatos sucios. Estos no deben dejarse en el suelo (guardaropa exclusiva)."}
   {:title "Orden del vestuario"
    :text "Todo el personal es responsable de mantener limpios sus vestuarios (sin objetos en el suelo)."}])

(def hygiene-visitor-rules
  [{:zone "Zona de preparación trasera"
    :items ["Blusa desechable" "Gorro higiénico" "Cubrezapatos"
            "Los efectos personales deben permanecer fuera de la zona de preparación"]}
   {:zone "Zona de ventas"
    :items ["Blusa desechable" "Gorro higiénico"]}])

(def hygiene-toilet-rules
  [{:title "Limpieza de baños"
    :text "Baños del personal limpios (incorporados al programa de limpieza)."}
   {:title "Instalaciones de lavado"
    :text "Presencia de instalaciones para el lavado de manos."}])

(def hygiene-break-room-rules
  [{:title "Limpieza de sala de descanso"
    :text "Sala de descanso limpia, incluyendo equipos y utensilios como refrigerador, cafetera, horno (área incorporada al programa de limpieza)."}
   {:title "Almacenamiento seguro"
    :text "No almacenar productos que causen contaminación (alimentos vencidos, químicos, etc.)."}])

(def handwash-station
  ["Accesible"
   "Sistema de manos libres"
   "Sin obstrucciones"
   "Jabón antibacterial"
   "Toallas desechables o secador de manos"
   "Basurero"])

(def handwash-frequency
  ["Al comenzar a trabajar"
   "Antes de manipular productos sensibles"
   "Entre cada servicio"
   "Después de ir al baño"
   "Después de tomar un descanso o fumar"
   "Después de estornudar o sonarse la nariz"
   "Después de manipular agentes contaminantes, entregas, cajas o basura"
   "Después de limpiar"
   "Y al menos cada 30 minutos"])

(def handwash-steps
  [{:step "01" :title "Mojar" :text "Mojar manos y muñecas con agua corriente." :img "handwash-01-mojar.png"}
   {:step "02" :title "Enjabonar" :text "Aplicar jabón antibacterial suficiente." :img "handwash-02-enjabonar.png"}
   {:step "03" :title "Frotar" :text "Frotar palmas, dorsos, entre dedos, pulgares, uñas y muñecas por al menos 20 segundos." :img "handwash-03-frotar.png"}
   {:step "04" :title "Enjuagar" :text "Retirar completamente el jabón con agua." :img "handwash-04-enjuagar.png"}
   {:step "05" :title "Secar" :text "Secar con toalla desechable o secador de manos." :img "handwash-05-secar.png"}
   {:step "06" :title "Cerrar sin contaminar" :text "Usar la toalla para cerrar el grifo si no es manos libres y desecharla." :img "handwash-06-cerrar.png"}])

(def handwash-fun-facts
  [{:icon "percent" :text "El 80 % de las enfermedades transmitidas por alimentos se previenen con un correcto lavado de manos (*OMS — Organización Mundial de la Salud*)."}
   {:icon "timer" :text "Lavarse al menos 20 segundos reduce las bacterias en un 99 %. Regla: canta «Cumpleaños feliz» dos veces."}
   {:icon "hand" :text "Debajo de las uñas se acumula el 95 % de las bacterias. Clave: frotar las uñas con la palma."}
   {:icon "thermometer" :text "La temperatura del agua no elimina más gérmenes; lo que importa es la *fricción con jabón*."}
   {:icon "droplets" :text "Las manos mojadas transmiten 1 000× más bacterias que las secas. *Secar bien es tan importante como lavar*."}
   {:icon "clock" :text "En la industria alimentaria: lavarse las manos *al menos cada 30 minutos* de manipulación continua."}])

(def glove-rules
  [{:title "Tipo correcto"
    :text "Los guantes para alimentos deben ser de un solo uso y aptos para contacto alimentario; preferiblemente nitrilo. El látex puede limitarse por alergias."}
   {:title "Uso obligatorio"
    :text "Son obligatorios si hay heridas en las manos, cuando no se pueda acceder fácilmente a la estación de lavado o en preparaciones frente al cliente."}
   {:title "Nunca reemplazan el lavado"
    :text "Los guantes no sustituyen el lavado de manos y deben cambiarse cuando se ensucien, se rompan o se cambie de tarea."}])

;; ── Intro ───────────────────────────────────────────────────────
(def intro-title "Introducción")

(def intro-blocks
  ["Esta guía es el manual de consulta del equipo en tienda: condensa la normativa aplicable, el estándar de la franquicia Valentino y las buenas prácticas para manipular helados e ingredientes con higiene alimentaria. Sirve para formar al personal, resolver dudas durante el servicio y preparar las visitas de auditoría. El operador de las tiendas de Heladerías Valentino debe garantizar el cumplimiento de una serie de normas de salud, seguridad e higiene. Debe garantizar que el consumidor final disfrute del máximo nivel de seguridad en cuanto a la calidad del producto y la ausencia de cualquier riesgo para la salud. Debe cumplir con las normativas del país y con las normas de la empresa. Las tiendas estarán sujetas a inspecciones regulares de la franquicia."
   "Para limitar los riesgos para la salud, se debe aplicar el sentido común y métodos específicos de análisis de riesgos y control de peligros."
   "Con esto en mente, la franquicia entrega la «Guía de Higiene Alimentaria para Tiendas de helados Valentino». Los métodos de trabajo descritos en estas páginas cumplen con la legislación y su único objetivo es proteger al consumidor."])

(def intro-dropcap "E")

(def risk-families-title "Cuatro familias de Riesgos")

(def riesgos-familias-intro-paras
  ["En seguridad alimentaria los peligros se agrupan en cuatro familias. No es solo teoría: en la tienda cada familia se gestiona con medidas concretas. El riesgo microbiológico se combate con cadena de frío, caducidades, higiene de manos y limpieza de superficies y utensilios. El físico se previene inspeccionando insumos, cuidando equipos y evitando joyas, pelo suelto o prácticas que introduzcan cuerpos extraños."
   "Los alérgenos exigen saber qué lleva cada sabor, etiquetar con claridad y evitar cruces entre cucuruchos, cucharones y superficies. El riesgo químico aparece cuando desinfectantes, detergentes o productos de limpieza quedan mal guardados, sin etiqueta o cerca del helado o los ingredientes. Mezclar estos controles en la cabeza del equipo reduce errores y quejas."
   "Reconocer las cuatro familias es el primer paso para proteger al cliente y a la marca Valentino. Abajo tienes un resumen visual de cada una; las páginas siguientes profundizan en señales, causas y prevención adaptada a heladería."])

(def riesgos-familias-bolas
  [{:img "riesgo-bola-microbiologico.png" :tone "micro"
    :title "Microbiológico"
    :lead "Gérmenes y toxinas"
    :text "Bacterias y microorganismos si fallan frío, higiene o caducidad. Provoca gastroenteritis e intoxicaciones alimentarias."}
   {:img "riesgo-bola-fisico.png" :tone "fisico"
    :title "Físico"
    :lead "Cuerpos extraños"
    :text "Vidrio, metal, plástico, pelo… que entran por equipos, empaques o prácticas deficientes. Un solo caso es grave para el cliente y la marca."}
   {:img "riesgo-bola-alergenos.png" :tone "alergeno"
    :title "Alérgenos"
    :lead "Leche, frutos secos, gluten…"
    :text "Reacciones leves a anafilaxia. Exige conocer fichas, etiquetar bien y evitar contaminación cruzada entre sabores y utensilios."}
   {:img "riesgo-bola-quimico.png" :tone "quimico"
    :title "Químico"
    :lead "Limpieza y químicos"
    :text "Desinfectantes mal guardados, sin etiqueta o cerca del producto pueden contaminar. Residuo químico en helado: riesgo inaceptable."}])

;; ── Risk families ───────────────────────────────────────────────
(def risk-families
  [{:id "riesgo-microbiologico"
    :icon  "bug"
    :color "orange"
    :title "Riesgo microbiológico"
    :sub   "Puede provocar enfermedades o intoxicación alimentaria por bacterias o toxinas de bacterias como resultado de:"
    :enhanced? true
    :hero-bola "riesgo-bola-microbiologico.png"
    :items [{:label "Malas condiciones de almacenamiento o transporte"          :icon "package-x"}
            {:label "Exceder la fecha de caducidad"                             :icon "calendar-x"}
            {:label "Romper la cadena de frío"                                  :icon "snowflake"}
            {:label "Falta de higiene en el servicio"                           :icon "hand"}
            {:label "Falta de limpieza (instalaciones, equipos y utensilios)"   :icon "sparkles"}
            {:label "Malas prácticas de higiene del personal"                   :icon "user-x"}]
    :danger-zone {:title "Zona de peligro"
                  :ranges [{:label "Ebullición" :temp "100°C" :zone "safe" :icon "flame"}
                           {:label "Zona segura caliente" :temp "> 60°C" :zone "safe" :icon "check-circle"}
                           {:label "ZONA DE PELIGRO" :temp "5°C – 60°C" :zone "danger" :icon "alert-triangle"}
                           {:label "Refrigeración" :temp "0°C – 5°C" :zone "safe" :icon "thermometer-snowflake"}
                           {:label "Congelación" :temp "< -18°C" :zone "safe" :icon "snowflake"}]
                  :note "Las bacterias se multiplican cada 20 minutos entre 5°C y 60°C"}
    :bacteria [{:name "Salmonella" :icon "bug" :source "Huevos, carnes, lácteos" :effect "Gastroenteritis severa"}
               {:name "Listeria" :icon "bug" :source "Lácteos no pasteurizados" :effect "Meningitis, abortos"}
               {:name "E. coli" :icon "bug" :source "Agua contaminada, carnes" :effect "Diarrea hemorrágica"}
               {:name "S. aureus" :icon "bug" :source "Piel, nariz, heridas" :effect "Vómitos, diarrea"}]
    :stats [{:value "~600 M" :label "personas con enfermedad transmitida por alimentos al año (orden OMS)"}
            {:value "~420 K" :label "muertes anuales vinculadas a alimentación insegura en el mundo"}
            {:value "×2" :label "duplicación aprox. de bacterias cada 20 min entre 5 °C y 60 °C"}]
    :prevention [{:icon "snowflake" :title "Cadena de frío" :text "Mantener helados ≤ -18°C"}
                 {:icon "hand" :title "Lavado de manos" :text "Cada 30 min y entre tareas"}
                 {:icon "spray-can" :title "Desinfección" :text "Superficies y utensilios"}
                 {:icon "calendar-check" :title "Rotación PEPS" :text "Primero en entrar, primero en salir"}]}
   {:id "riesgo-fisico"
    :icon  "shield-off"
    :color "green"
    :title "Riesgo físico"
    :sub   "Derivado de la presencia de cuerpos extraños en los helados debido a:"
    :enhanced? true
    :hero-bola "riesgo-bola-fisico.png"
    :items [{:label "Uso de joyas, uñas postizas, peinado inadecuado"           :icon "gem"}
            {:label "Incumplimiento de las buenas prácticas de almacenamiento"  :icon "archive-x"}
            {:label "Problemas en instalaciones — luces descubiertas"           :icon "lightbulb-off"}]
    :contaminants [{:type "Metal" :icon "wrench" :examples "Tornillos, clips, grapas, cuchillas" :severity "high"}
                   {:type "Vidrio" :icon "glass-water" :examples "Ampolletas, vasos rotos, luces" :severity "high"}
                   {:type "Plástico" :icon "package" :examples "Fragmentos de envase, guantes rotos" :severity "medium"}
                   {:type "Orgánico" :icon "leaf" :examples "Cabello, uñas, insectos, astillas" :severity "medium"}
                   {:type "Piedra" :icon "mountain" :examples "Piedras, arena en materias primas" :severity "low"}
                   {:type "Hueso" :icon "bone" :examples "Fragmentos en insumos con proteínas" :severity "low"}]
    :stats [{:value "⚠" :label "Principal causa de quejas del consumidor"}
            {:value "35%" :label "de retiros de producto son por contaminación física"}
            {:value "0" :label "tolerancia — un solo incidente es grave"}]
    :sources [{:area "Personal" :icon "user" :detail "Joyas, cabello suelto, uñas postizas, curitas sin guante"}
              {:area "Equipos" :icon "settings" :detail "Tornillos flojos, cuchillas desgastadas, empaques rotos"}
              {:area "Instalaciones" :icon "building" :detail "Luces sin protección, pintura descascarada, techos con filtraciones"}
              {:area "Insumos" :icon "package" :detail "Envolturas plásticas, grapas de cajas, fragmentos de empaque"}]
    :prevention [{:icon "scan-eye" :title "Inspección visual" :text "Revisar producto e insumos al recibir"}
                 {:icon "hard-hat" :title "Uniforme completo" :text "Gorro, sin joyas, uñas cortas"}
                 {:icon "wrench" :title "Mantenimiento" :text "Revisión periódica de equipos e instalaciones"}
                 {:icon "shield-check" :title "Protocolo" :text "Proteger luces, reportar roturas de inmediato"}]}
   {:id "riesgo-alergenos"
    :icon  "triangle-alert"
    :color "yellow"
    :title "Riesgo de Alérgenos"
    :sub   "Reacciones inmunitarias que pueden provocar síntomas leves o graves (shock anafiláctico) resultantes de:"
    :enhanced? true
    :hero-bola "riesgo-bola-alergenos.png"
    :items [{:label "Falta o desinformación sobre composición y alérgenos del producto" :icon "info"}
            {:label "Contaminación cruzada de alérgenos"                               :icon "shuffle"}
            {:label "No cambiar guantes ni lavar manos entre preparaciones"             :icon "hand"}
            {:label "Uso compartido de utensilios sin limpieza"                        :icon "utensils"}]
    :allergens [{:name "Leche" :icon "milk" :source "Helados, cremas, toppings lácteos" :reaction "Urticaria, vómitos, anafilaxia" :severity "high"}
                {:name "Frutos secos" :icon "nut" :source "Toppings, pralinés, salsas" :reaction "Edema, dificultad respiratoria" :severity "high"}
                {:name "Maní" :icon "bean" :source "Mantequilla de maní, toppings" :reaction "Anafilaxia severa" :severity "high"}
                {:name "Gluten" :icon "wheat" :source "Conos, galletas, brownies" :reaction "Inflamación intestinal, celiaquía" :severity "medium"}
                {:name "Huevo" :icon "egg" :source "Bases de helado, merengue" :reaction "Urticaria, vómitos" :severity "medium"}
                {:name "Soja" :icon "leaf" :source "Lecitina en chocolate, bases" :reaction "Dermatitis, edema leve" :severity "low"}]
    :cross-contam-steps [{:icon "ice-cream-cone" :label "Servir sabor A"}
                         {:icon "hand" :label "Misma cuchara / guante"}
                         {:icon "ice-cream-cone" :label "Servir sabor B"}
                         {:icon "alert-triangle" :label "Cliente alérgico expuesto"}]
    :stats [{:value "14" :label "alérgenos de declaración obligatoria (UE)"}
            {:value "2%" :label "adultos con alergia alimentaria"}
            {:value "8%" :label "niños afectados por algún alérgeno"}]
    :prevention [{:icon "clipboard-list" :title "Conocer el producto" :text "Consultar fichas técnicas de cada sabor"}
                 {:icon "hand" :title "Cambiar guantes" :text "Entre cada cliente con solicitud especial"}
                 {:icon "spray-can" :title "Limpiar utensilios" :text "Separar cucharas por tipo de producto"}
                 {:icon "message-circle" :title "Comunicar" :text "Informar con exactitud, nunca improvisar"}]}
   {:id "riesgo-quimico"
    :icon  "flask-conical"
    :color "dark"
    :title "Riesgo químico"
    :sub   "Contaminación por productos tóxicos resultante de:"
    :hero-bola "riesgo-bola-quimico.png"
   :enhanced? true
   :items [{:label "Almacenamiento conjunto de productos alimentarios y de mantenimiento" :icon "layers"}
           {:label "Incumplimiento de los protocolos de limpieza"                        :icon "clipboard-x"}
           {:label "Químicos sin etiqueta o diluciones incorrectas"                      :icon "tag"}
           {:label "Uso de recipientes no aptos para contacto alimentario"               :icon "beaker"}]
   :stats [{:value "0" :label "tolerancia a residuos químicos en producto"}
           {:value "1" :label "error de dosificación puede contaminar lote"}
           {:value "100%" :label "químicos deben estar identificados y segregados"}]
   :chemical-sources [{:title "Almacenamiento" :icon "archive-x" :detail "Limpiadores junto a envases, cucharas o materias primas." :level "high"}
                      {:title "Preparación" :icon "beaker" :detail "Diluciones sin medir o mezcla de químicos incompatibles." :level "high"}
                      {:title "Servicio" :icon "spray-can" :detail "Aplicar químicos cerca del producto expuesto al cliente." :level "medium"}
                      {:title "Utensilios" :icon "utensils-crossed" :detail "Recipientes de limpieza reutilizados para alimentos." :level "high"}]
   :chemical-examples [{:icon "spray-can" :title "Desinfectante en vitrina" :text "Rociar cerca de helados listos para consumo puede dejar residuos."}
                       {:icon "package-x" :title "Envase sin etiqueta" :text "No identificar el producto químico impide uso seguro y trazable."}
                       {:icon "beaker" :title "Dilución incorrecta" :text "Una concentración mayor a la indicada puede contaminar superficies."}
                       {:icon "utensils-crossed" :title "Recipiente compartido" :text "Usar el mismo envase para limpiar y manipular alimento es crítico."}]
   :prevention [{:icon "archive" :title "Separación estricta" :text "Químicos en área exclusiva, cerrada y señalizada"}
                {:icon "tag" :title "Etiquetado completo" :text "Nombre, dilución, fecha y responsable en cada envase"}
                {:icon "clipboard-check" :title "Protocolo de limpieza" :text "Seguir tiempos, dosis y enjuague según ficha técnica"}
                {:icon "shield-check" :title "Verificación final" :text "Confirmar ausencia de residuos antes de operar"}]}])

;; ── Operations ──────────────────────────────────────────────────
(def recepcion-title "Recepción y Almacenamiento")

(def recepcion-intro
  ["Recepción y Almacenamiento son etapas críticas en la cadena de frío que garantizan la calidad y seguridad del producto desde que llega a la tienda hasta que se sirve al consumidor. Un fallo en esta etapa puede comprometer toda la operación."
   "El pedido de helados llega los **lunes y jueves** en camiones refrigerados. Al recibir cada entrega, se realiza un **inventario diario** del producto para asegurar que la cantidad y condición coinciden con la orden colocada."
   "El producto se recibe en **bandejas** que llegan a una temperatura de **-18°C**. La persona que entrega el producto lo coloca sobre las neveras, y el encargado en tienda las va colocando en el **cuarto frío de almacenamiento**."
   "El cuarto frío tiene **cuatro tramos**: el producto nuevo se coloca en los tramos de la **izquierda** y en los tramos de la **derecha** se coloca el producto más viejo — siguiendo el principio **PEPS** (Primero en Entrar, Primero en Salir)."
   "Cuando se recibe el producto, debe **contarse** para asegurar que se recibió la cantidad correcta contra la orden colocada. Cualquier discrepancia debe reportarse inmediatamente."])

(def recepcion-bandeja
  "El producto se recibe generalmente lunes y jueves en camiones refrigerados a -18°C. El conductor coloca el producto sobre las neveras; el encargado lo traslada al cuarto frío de almacenamiento. Al recibir, se cuenta el producto para verificar que coincide con la orden.")

(def almacenamiento-intro
  ["Los productos deben ser almacenados adecuadamente según su tipo: el producto **refrigerado y congelado** se coloca en cuartos fríos y neveras, mientras que el producto **seco o a temperatura ambiente** se guarda en armarios y tramos."
   "**Nunca** debe colocarse producto para consumo directamente en el suelo. El producto debe colocarse en **recipientes plásticos** o sobre los **tramos** designados."
   "Los productos y materiales secos (conos, envases, servilletas) se organizan en los tramos del **almacén seco** o en los tramos del **área de servicio**."
   "Los productos refrigerados (toppings, salsas, frutas) se colocan en las **neveras del área de servicio** a la temperatura indicada por el fabricante."
   "Las **bandejas de helado** se colocan en los cuartos fríos sobre los tramos, etiquetadas con **fecha de recepción** y **número de lote**."])

(def recepcion-criteria
  [{:que "Temperatura del producto"
    :icon "thermometer"
    :como "Termómetro infrarrojo"
    :criterio "T = -18°C. Rechazar si la temperatura interna no es conforme."}
   {:que "Limpieza del vehículo"
    :icon "truck"
    :como "Inspección visual"
    :criterio "Rechazar si el vehículo está sucio."}
   {:que "Condición del producto"
    :icon "package-check"
    :como "Inspección visual"
    :criterio "Rechazar si la condición no es apta."}
   {:que "Número de lote"
    :icon "hash"
    :como "Inspección visual"
    :criterio "Rechazar si no está el número de lote."}
   {:que "Registro de recepción"
    :icon "clipboard-check"
    :como "Formulario"
    :criterio "Completar: fecha, temperatura, condición, vehículo, lote. Marcar aceptado o rechazado."}])

(def recepcion-mantenimiento-vencimiento-controles
  [{:title "Mantenimiento de cámaras frías"
    :icon  "wrench"
    :text "Regularmente debe darse mantenimiento a las cámaras frías con una frecuencia programada (definir frecuencia según proveedor). Registrar cada intervención de mantenimiento."}
   {:title "Vencimiento del producto"
    :icon  "calendar-x"
    :text "El producto tiene una vida media definida por el fabricante. Los productos se reciben con una etiqueta que indica la fecha de vencimiento. Si algún producto llega a la fecha de vencimiento debe ser descartado de inmediato."}
   {:title "Control matutino de temperatura"
    :icon  "sunrise"
    :text "Diariamente, cuando el personal llega a la tienda, antes de abrir el cuarto frío, debe registrar la temperatura en el formulario de control."}
   {:title "Control vespertino de temperatura"
    :icon  "sunset"
    :text "En la tarde, en momentos de poca actividad en el almacén, se cierra el cuarto frío durante 15 a 20 minutos hasta que se estabilice la temperatura y se anota la lectura en el formulario de control."}])

(def almacenamiento-protocolo-limpieza
  [{:img "ref-protocolo-limpieza.png"
    :title "Protocolo de limpieza de superficies"
    :text "Para una eficiencia óptima = **EL PRODUCTO ADECUADO**, en el lugar adecuado, en la **dosis adecuada**, con la **técnica adecuada**, con la **frecuencia adecuada**."
    :bullets ["Temperatura > Acción > Concentración (dosis) > Tiempo."
              "Cumpla con los procedimientos operativos recomendados."
              "NO olvide enjuagar cuando sea necesario."
              "Se recomienda el uso de una aspiradora de agua."]}
   {:img "ref-normas-almacenamiento-utensilios.png"
    :title "Normas de almacenamiento de equipos y utensilios pequeños"
    :text ""
    :bullets ["Después de limpiarlos, guarde los utensilios en un área de almacenamiento limpia colgados."
              "Coloque los utensilios limpios boca abajo para que el agua residual se escurra y evitar la contaminación por polvo."]}
   {:img "ref-seguridad-productos-limpieza.png"
    :title "Instrucciones de seguridad para productos de limpieza"
    :text ""
    :bullets ["Guarde los equipos y productos de mantenimiento en un armario o área de almacenamiento independiente, y no en un lugar donde se almacenen productos."
              "Los productos de limpieza diluidos se dosifican en pulverizadores adecuados y debidamente identificados mediante etiquetas."
              "Tenga cuidado de no almacenar los artículos en el suelo."]}])

(def almacenamiento-rules
  [{:title "Nunca en el suelo"
    :icon  "ban"
    :text "El producto para consumo nunca se coloca directamente en el piso. Usar recipientes plásticos o tramos."}
   {:title "Cuarto frío — rotación PEPS"
    :icon  "arrow-right-left"
    :text "Producto nuevo a la izquierda, más viejo a la derecha. Despachar siempre el producto más antiguo."}
   {:title "Productos secos"
    :icon  "package"
    :text "Conos y envases se organizan en tramos del almacén seco o área de servicio."}
   {:title "Producto refrigerado"
    :icon  "refrigerator"
    :text "Colocar en las neveras del área de servicio."}
   {:title "Bandejas de helado"
    :icon  "ice-cream-cone"
    :text "Colocar en cuartos fríos sobre los tramos. Etiquetar debidamente con fecha y lote."}])

(def temp-control-rules
  [{:title "Registro matutino"
    :icon  "sunrise"
    :text "Antes de abrir, registrar la temperatura del cuarto frío en el formulario de control."}
   {:title "Registro vespertino"
    :icon  "sunset"
    :text "Con poca actividad, cerrar el cuarto frío 15–20 min hasta estabilizar la temperatura. Anotar el valor."}
   {:title "Mantenimiento preventivo"
    :icon  "wrench"
    :text "Las cámaras frías reciben mantenimiento regular con frecuencia programada. Registrar cada intervención."}
   {:title "Vencimiento"
    :icon  "calendar-check"
    :text "Verificar fechas al recibir y al usar. Todo producto que llegue a su fecha de vencimiento debe descartarse."}])

(def recepcion-alertas-clave
  ["Controlar y registrar temperaturas al recibir, al almacenar y al cerrar."
   "Aplicar PEPS todos los días: producto más antiguo sale primero."
   "Separar productos secos, refrigerados y congelados para evitar cruces."
   "Si un criterio falla, documentar, aislar y escalar al responsable."])

(def recepcion-controles-gallery
  [{:img "cadena-nevera.png"
    :kicker "Bodega"
    :title "Orden y segregación por zona térmica"}
   {:img "cadena-termometro.png"
    :kicker "Control"
    :title "Toma de temperatura en puntos críticos"}
   {:img "cadena-registro-papel.png"
    :kicker "Trazabilidad"
    :title "Registro diario de controles y acciones"}])

(def limpieza-title "Limpieza y Desinfección")

(def limpieza-def-limpiar
  "Resultado «estético»: sin residuo orgánico visible. Elimina suciedad, grasa y partículas antes de desinfectar.")

(def limpieza-def-desinfectar
  "Resultado «microscópico»: reducción de gérmenes a nivel seguro. Usar solo productos profesionales autorizados para entornos alimentarios.")

(def limpieza-productos
  [{:nombre "Detergente desinfectante alimentario"
    :icon   "spray-can"
    :uso    "Para toda superficie en contacto con alimentos o zona de almacenamiento."
    :tipo   "superficie"}
   {:nombre "Toallitas desinfectantes"
    :icon   "hand"
    :uso    "Desinfección rápida entre preparaciones, sin enjuague — encimeras, utensilios, etc."
    :tipo   "superficie"}
   {:nombre "Detergente alcalino fuerte"
    :icon   "flask-conical"
    :uso    "Eliminar grasa cocida — hornos, placas y freidoras."
    :tipo   "maquinas"}
   {:nombre "Agente desincrustante"
    :icon   "beaker"
    :uso    "Retirar depósitos minerales y sarro de equipos."
    :tipo   "maquinas"}
   {:nombre "Detergente lavavajillas"
    :icon   "settings"
    :uso    "Lavar utensilios y recipientes en máquina lavavajillas."
    :tipo   "maquinas"}
   {:nombre "Líquido lavavajillas a mano"
    :icon   "hand"
    :uso    "Lavar utensilios y recipientes a mano."
    :tipo   "superficie"}
   {:nombre "Jabón antibacterial líquido"
    :icon   "hand"
    :uso    "Lavado de manos CON AGUA en zonas de preparación."
    :tipo   "manos"}
   {:nombre "Gel desinfectante de manos"
    :icon   "droplets"
    :uso    "Desinfección SIN AGUA en puntos de venta. No permitido en zona de preparación."
    :tipo   "manos"}
   {:nombre "Limpiador de pisos y superficies"
    :icon   "sparkles"
    :uso    "Limpieza de pisos, paredes y superficies no en contacto directo con alimentos."
    :tipo   "superficie"}])

(def limpieza-prohibido
  "PROHIBIDO: esponjas, mopas de hilo y escobas comunes. Usar cepillos de cerdas para uso diario y mopas planas. Aspiradora solo en zona de ventas — prohibida en zona de preparación.")

(def limpieza-vitrina-steps
  [{:step "01" :icon "beaker" :title "Preparar solución"
    :text "Preparar recipiente con jabón y cloro en dilución indicada; usar paños limpios diferenciados."}
   {:step "02" :icon "spray-can" :title "Limpiar interior"
    :text "Paño con detergente: paredes, superficie, bordes de soportes y separadores frontales; retirar residuos visibles."}
   {:step "03" :icon "droplets" :title "Enjuagar"
    :text "Pasar paño con agua clara hasta eliminar por completo residuos de productos químicos."}
   {:step "04" :icon "scan-eye" :title "Cortina y vidrio"
    :text "Limpiar la cortina por dentro y fuera, y verificar transparencia del vidrio interior antes de cargar producto."}
   {:step "05" :icon "power" :title "Encender y estabilizar"
    :text "Encender nevera y luces; esperar estabilidad térmica antes de colocar bandejas."}
   {:step "06" :icon "ice-cream-cone" :title "Colocar bandejas"
    :text "Retirar bandejas del cuarto frío, validar etiqueta (fecha/lote) y limpiar vidrio exterior con limpiacristales."}])

(def limpieza-schedule
  [{:freq "Diario" :icon "refrigerator" :days-label "Lun-Dom" :month-count "31" :area "Vitrina de helados"
    :text "Inicio de turno: limpiar interior, enjuagar y dejar seca; limpiar vidrio exterior. Registrar ejecución en checklist."}
   {:freq "Diario" :icon "house" :days-label "Lun-Dom" :month-count "31" :area "Piso general"
    :text "Inicio/final: barrer bajo mesas, sillas y neveras; trapear con desinfectante. Mediodía: solo áreas de tránsito."}
   {:freq "Diario" :icon "snowflake" :days-label "Lun-Dom" :month-count "31" :area "Piso interior y cuarto frío"
    :text "Barrer moviendo lo necesario y trapear almacén/cuarto frío con solución aprobada para entorno alimentario."}
   {:freq "Diario" :icon "table" :days-label "Lun-Dom" :month-count "31" :area "Mesas y sillas"
    :text "Inicio/final: paño húmedo con cloro. Durante servicio: limpiar después de cada cliente."}
   {:freq "Diario" :icon "cup-soda" :days-label "Lun-Dom" :month-count "31" :area "Recipiente scooper"
    :text "Inicio y cierre: vaciar, lavar, enjuagar hasta agua clara y reponer agua limpia."}
   {:freq "Semanal" :icon "archive" :days-label "Sabado" :month-count "4" :area "Armarios — básico"
    :text "Revisar tramos, limpiar superficies visibles y reorganizar insumos por tipo y rotación."}
   {:freq "Mensual" :icon "clipboard-check" :days-label "Semana 1" :month-count "1" :area "Armarios — profundo"
    :text "Retirar todo el material, limpiar/desinfectar a fondo, secar y reordenar con control visual."}])

;; ── Vitrina de helados — limpieza detallada ────────────────────
(def limpieza-vitrina-interior-steps
  [{:step "01" :icon "beaker" :title "Preparar solución"
    :text "Se prepara un recipiente con jabón y cloro en dilución indicada. La nevera ha quedado vacía del turno de la noche."}
   {:step "02" :icon "spray-can" :title "Limpiar paredes y superficies"
    :text "Limpiar con un paño con detergente y desinfectante todas las paredes y superficie de la nevera eliminando toda suciedad."}
   {:step "03" :icon "grip" :title "Soportes y separadores"
    :text "Se limpian los bordes de los soportes de las bandejas por debajo y los separadores del área frontal."}
   {:step "04" :icon "droplets" :title "Enjuagar residuos"
    :text "Después de pasar el paño con detergente y desinfectante, pasar un paño con agua clara para eliminar los residuos de químicos."}
   {:step "05" :icon "blinds" :title "Cortina de protección"
    :text "Se limpia la cortina de protección de la nevera de helados por dentro y por fuera."}
   {:step "06" :icon "scan-eye" :title "Vidrio interior"
    :text "Se limpia el vidrio de la nevera por dentro, verificando que quede completamente transparente."}
   {:step "07" :icon "power" :title "Encender nevera"
    :text "Se enciende la nevera y las luces. Esperar a que alcance temperatura estable."}])

(def limpieza-vitrina-exterior-steps
  [{:step "08" :icon "sparkles" :title "Vidrio exterior"
    :text "Con un paño y limpiacristales se limpia el vidrio de la nevera por fuera hasta dejarlo impecable."}
   {:step "09" :icon "ice-cream-cone" :title "Colocar bandejas"
    :text "Una vez limpia la nevera, se colocan las bandejas de helados que se van retirando del cuarto frío cuidando de colocar las etiquetas debidamente."}])

;; ── Cámara fría — limpieza ────────────────────────────────────
(def limpieza-camara-fria-steps
  [{:step "01" :icon "clipboard-list" :title "Preparación"
    :text "Planificar la limpieza en horas de baja venta. Trasladar los helados a otro congelador que esté a ≤ −18 °C para evitar ruptura de cadena de frío."}
   {:step "02" :icon "hard-hat" :title "Equipos de protección"
    :text "Usar EPP: guantes térmicos, guantes de limpieza y botas antideslizantes. Desconectar ventiladores o evaporadores si el diseño lo permite."}
   {:step "03" :icon "trash-2" :title "Retiro de residuos"
    :text "Retirar residuos de producto. Barrer o aspirar fragmentos de hielo. Nunca usar agua caliente directamente sobre el piso congelado porque genera hielo inmediato."}
   {:step "04" :icon "spray-can" :title "Lavado de superficies"
    :text "Preparar detergente neutro o alcalino suave apto para alimentos. Limpiar con paño o cepillo plástico: estanterías, paredes, piso, puertas y empaques. En cámaras de congelación se recomienda mínima agua (paños húmedos)."}
   {:step "05" :icon "droplets" :title "Enjuague"
    :text "Retirar el detergente con paños húmedos o mop ligeramente húmedo. Evitar exceso de agua para prevenir formación de hielo."}
   {:step "06" :icon "shield-check" :title "Desinfección"
    :text "Aplicar un desinfectante aprobado para contacto con superficies de alimentos."}
   {:step "07" :icon "wind" :title "Secado"
    :text "Secar con paños o permitir evaporación natural. Verificar que no quede agua que pueda congelarse."}
   {:step "08" :icon "power" :title "Reorganización y encendido"
    :text "Encender el equipo. Esperar que la cámara alcance ≤ −18 °C. Colocar nuevamente los helados respetando FIFO/FEFO, separación del piso y circulación de aire."}])

(def limpieza-camara-fria-frecuencia
  [{:freq "Inmediata" :icon "alert-triangle" :days-label "Cuando ocurra" :month-count "—" :area "Derrames"
    :text "Limpieza ligera inmediata por cualquier derrame de producto dentro de la cámara fría."}
   {:freq "Semanal" :icon "calendar-check" :days-label "Sábado" :month-count "4" :area "Estantería"
    :text "Limpieza de estanterías, organización y verificación de etiquetas y rotación FIFO/FEFO."}
   {:freq "Mensual" :icon "calendar" :days-label "Semana 1" :month-count "1" :area "Limpieza completa"
    :text "Limpieza completa de la cámara: paredes, piso, puertas, empaques y estanterías. Registro obligatorio."}
   {:freq "Según\nacumulación" :icon "snowflake" :days-label "Variable" :month-count "—" :area "Deshielo de evaporadores"
    :text "Deshielo de evaporadores según acumulación de hielo. Verificar funcionamiento después del deshielo."}])

;; ── Limpieza profunda del local ───────────────────────────────
(def limpieza-profunda-intro
  "La limpieza profunda mensual garantiza que paredes, pisos, mobiliario y vidrios queden libres de residuos acumulados durante el servicio diario. Planifícala en franja de baja afluencia, con el local despejado y los productos sensibles resguardados en frío.")

(def limpieza-profunda-steps
  [{:step "01" :icon "calendar" :title "Programar mensualmente"
    :text "Mensualmente se realiza una limpieza profunda del local. Planificar en horario de baja actividad."}
   {:step "02" :icon "package-x" :title "Despejar el área"
    :text "Se quitan todos los insumos de la meseta y los helados se guardan en la cámara fría."}
   {:step "03" :icon "spray-can" :title "Limpieza de superficies"
    :text "Se realiza una limpieza profunda de paredes, piso, sillas y mesas, zafacones, gabinetes y nevera."}
   {:step "04" :icon "droplets" :title "Pisos con agua"
    :text "El piso se limpia cuando es posible con agua, detergente y desinfectante. Mesas y sillas se lavan individualmente."}
   {:step "05" :icon "panel-top" :title "Vidrios del local"
    :text "Los vidrios del local se limpian lavándolos con agua y detergente, y pasando un escurridor de vidrio para un acabado impecable."}
   {:step "06" :icon "check-circle" :title "Inspección final"
    :text "Verificar que todas las áreas queden limpias y secas antes de reanudar operaciones. Registrar en el formulario de limpieza profunda."}])

;; ── Pisos, Mesas y Sillas ─────────────────────────────────────
(def limpieza-pisos-rules
  [{:title "Piso interior y almacén"
    :icon  "footprints"
    :text "Barrer bien el área moviendo lo que sea necesario y por debajo de aparatos cuando sea posible. Preparar cubeta con agua y desinfectante y limpiar el piso del área interior, incluyendo área de almacén y cuarto frío."}])

(def limpieza-mesas-sillas-rules
  [{:title "Diario — inicio/final del turno"
    :icon  "sunrise"
    :text "Limpiar las mesas con un paño húmedo con cloro. Limpiar las sillas con un paño húmedo."}
   {:title "Constantemente — después de cada cliente"
    :icon  "users"
    :text "Cada vez que un cliente usa una mesa, limpiar después del uso."}
   {:title "Mensual — limpieza profunda"
    :icon  "calendar"
    :text "Con la limpieza profunda, limpiar sillas y mesas con agua y detergente."}])

;; ── Armarios ──────────────────────────────────────────────────
(def limpieza-armarios-rules
  [{:title "Profunda — Mensual"
    :icon  "calendar"
    :text "Retirar todo el material de los armarios. Limpiar con un paño húmedo con desinfectante. Pasar un paño seco y volver a organizar los tramos."}
   {:title "Básica — Semanal"
    :icon  "calendar-check"
    :text "Revisar los tramos y pasar paño húmedo si es necesario. Organizar los tramos."}])

;; ── Neveras de tartas y producto ──────────────────────────────
(def limpieza-nevera-tartas-rules
  [{:title "Diario — exterior"
    :icon  "sparkles"
    :text "Se limpia diariamente la nevera en el exterior con un paño húmedo."}
   {:title "Semanal — limpieza profunda"
    :icon  "calendar-check"
    :text "Semanalmente se sacan los helados y tartas y se colocan en la cámara fría. Se limpia profundamente con agua con cloro. Se reponen los helados."}])

;; ── Recipiente de lavado de Scooper ───────────────────────────
(def limpieza-scooper-steps
  [{:step "01" :icon "droplets" :title "Vaciar el agua"
    :text "Sacar toda el agua ayudado con una esponja."}
   {:step "02" :icon "spray-can" :title "Lavar el recipiente"
    :text "Lavar el recipiente con un estropajo limpio."}
   {:step "03" :icon "waves" :title "Enjuagar"
    :text "Abrir la llave y dejar correr el agua para enjuagar por dentro y por fuera."}
   {:step "04" :icon "check-circle" :title "Agua limpia"
    :text "Dejar correr el agua hasta asegurar que solo haya agua limpia."}])

;; ── Utensilios diversos ───────────────────────────────────────
(def limpieza-utensilios-rules
  [{:title "Frascos de toppings"
    :icon  "flask-round"
    :text "Lavar bien una vez que se vacían, secarlos con una servilleta de papel y rellenar."}
   {:title "Dispensador de servilletas"
    :icon  "scroll"
    :text "Limpiar bien por fuera con un paño húmedo limpio y desinfectante. Rellenar cuantas veces sea necesario."}
   {:title "Display y recipientes de conos"
    :icon  "cone"
    :text "Cuando se vacía se limpia con un paño húmedo y se repone."}
   {:title "Recipientes de tapas"
    :icon  "box"
    :text "Cuando se vacía se limpia con un paño húmedo y se repone."}
   {:title "Bandejas de frascos y tapas"
    :icon  "layout-grid"
    :text "Cuando se vacía se limpia con un paño húmedo y se repone."}
   {:title "Pantalla y mesa de computadora"
    :icon  "monitor"
    :text "Se pasa un paño húmedo semanalmente."}
   {:title "Lámparas de adornos"
    :icon  "lamp"
    :text "Quincenalmente se pasa un paño húmedo."}])

;; ── Microondas ────────────────────────────────────────────────
(def limpieza-microondas-rules
  [{:title "Diario — cada uso"
    :icon  "zap"
    :text "Cada vez que se usa debe limpiarse con un paño húmedo."}
   {:title "Semanal — profunda"
    :icon  "calendar-check"
    :text "Se saca la bandeja interior y se limpia a profundidad paredes. Se lava la bandeja con detergente y desinfectante."}])

;; ── Mesetas, fregadero y bandejas ─────────────────────────────
(def limpieza-mesetas-rules
  [{:title "Mesetas y fregadero"
    :icon  "layout-dashboard"
    :text "Diariamente se limpian con agua y desinfectante."}
   {:title "Bandejas de ingredientes"
    :icon  "package"
    :text "Según se van vaciando se elimina el exceso de helado con agua, se lavan con agua y detergente y se envían a las tiendas donde se realiza una limpieza profunda y se desinfecta."}
   {:title "Batidora"
    :icon  "blend"
    :text "Cada vez que se usa debe lavarse con agua y detergente."}])

;; ── Manejo de utensilios de limpieza ──────────────────────────
(def limpieza-manejo-utensilios-intro
  ["Los utensilios de limpieza son una pieza clave de la seguridad alimentaria: si no están en buen estado o se mezclan entre áreas, el riesgo de contaminación cruzada aumenta de inmediato."
   "Cada turno debe comenzar con paños limpios, identificados por color según el mapa de la tienda, y con la certeza de que estropajos y mopas no se usan indistintamente entre cocina, piso de venta y baños. Ante la duda, cambiar el paño y avisar al encargado."])

(def limpieza-utensilios-manejo-rules
  [{:title "Estropajos"
    :icon  "eraser"
    :text "Los estropajos deben mantenerse en buenas condiciones y deben ser descartados cuando estén sucios, maltratados o rotos. No dejarlos húmedos en cubetas cerradas: escurrir y ventilar para evitar olores y proliferación microbiana."}
   {:title "Paños — lavado diario"
    :icon  "shirt"
    :text "Diariamente los paños deben ser lavados al final del turno y dejados en una solución desinfectante hasta el otro día. Al comenzar el turno, usar paños limpios. Deben ser descartados cuando se maltratan, recomendable una vez al día."}
   {:title "Paños — diferenciación por área"
    :icon  "palette"
    :text "Los paños de preparación y área de servicio deben ser diferentes a los usados en la limpieza general. Usar código de colores: **Azul** = Área de preparación y servicio, **Amarillo** = Espacio comercial, **Rosa** = Baños."}
   {:title "Secado y almacenamiento"
    :icon  "wind"
    :text "Tras el lavado, los paños deben secarse por completo antes del siguiente uso o guardarse en el lugar señalado, separados por color. No apilar paños húmedos ni mezclar colores en la misma cesta."}
   {:title "Revisión al abrir"
    :icon  "eye"
    :text "Al iniciar servicio, revisar olor, manchas persistentes y deshilachado. Si un paño no pasa la revisión, retirarlo del piso y reemplazarlo antes de manipular superficies en contacto con alimentos."}])

(def limpieza-panos-colores
  [{:area "Área de preparación y servicio" :color "Azul"    :css-color "#89CFF0" :icon "utensils"}
   {:area "Espacio comercial"              :color "Amarillo" :css-color "#FFE44D" :icon "store"}
   {:area "Baños"                          :color "Rosa"     :css-color "#E8A0BF" :icon "bath"}])

;; ── Basurero e inventario ──────────────────────────────────────
(def limpieza-basurero-recoleccion
  [{:title "Recolección (área de preparación)"
    :icon  "trash-2"
    :text "**RECOJA LOS RESIDUOS** a medida que avanza en contenedores de basura de manos libres y en bolsas plásticas."}
   {:title "Equipo de protección"
    :icon  "hard-hat"
    :text "**USE EPP ADECUADO** para el tratamiento de residuos: guantes desechables y delantal."}
   {:title "Vaciar la basura"
    :icon  "package-x"
    :text "**VACÍE LA BASURA** al menos una vez después de cada ciclo de preparación. Si durante el día el basurero se rebosa, retire la basura del área."}
   {:title "Limpiar y desinfectar"
    :icon  "spray-can"
    :text "**LIMPIE Y DESINFECTE** los contenedores de basura a diario. Lávese las manos después de manipular residuos."}])

(def limpieza-basurero-retiro
  [{:title "Almacenar lejos de clientes"
    :icon  "map-pin-off"
    :text "**ALMACENE LOS RESIDUOS** en el lugar designado, lejos de los clientes y de las estaciones de trabajo."}
   {:title "Bolsas de basura"
    :icon  "package"
    :text "**ALMACENE LAS BOLSAS** en contenedores ubicados lejos del área de preparación de alimentos. Limpie y desinfecte el área y los contenedores al menos una vez por semana."}
   {:title "Retiro de residuos"
    :icon  "truck"
    :text "**RETIRE LOS RESIDUOS** fuera de los períodos de entrega. Saque diariamente la basura al finalizar el día."}])

(def limpieza-basurero-extra
  [{:title "Fundas en basureros"
    :icon  "shopping-bag"
    :text "Coloque fundas en los basureros siempre. Semanalmente lave el basurero con agua y detergente y desinfectar con agua de cloro."}
   {:title "Plagas"
    :icon  "bug"
    :text "Preste especial atención al tratamiento del espacio contra insectos y roedores."}])

(def inventario-diario-rules
  [{:title "Inventario diario"
    :icon  "clipboard-list"
    :text "Hacer inventario diario y reponer suministros de los tramos."}
   {:title "Colocar pedidos"
    :icon  "shopping-cart"
    :text "Colocar el pedido los días que corresponda según el calendario establecido."}])

;; ── Desinfectantes ────────────────────────────────────────────
(def desinfectantes-intro
  "Siga las instrucciones del fabricante. Se recomienda usar productos que sean aprobados para usarse en alimentos. **No comprar desinfectantes comerciales del supermercado.**")

(def desinfectantes-tabla
  [{:que "Amonio Cuaternario"  :icon "flask-conical" :como "200 – 400 ppm"  :criterio "No deja olor fuerte"}
   {:que "Hipoclorito (Cloro)" :icon "droplets"      :como "50 – 100 ppm"   :criterio "Puede corroer metales"}
   {:que "Alcohol 70%"         :icon "beaker"        :como "Superficies pequeñas" :criterio "Observación rápida"}])

;; ── Mantenimiento ─────────────────────────────────────────────
(def mantenimiento-intro
  "Un programa de mantenimiento para una tienda de helados debe asegurar principalmente: **control de temperatura**, **higiene de equipos**, **seguridad eléctrica y mecánica**, y **continuidad de la cadena de frío**.")

(def mantenimiento-equipos-criticos
  [{:title "Cámaras o congeladores"    :icon "snowflake"     :text "Almacenamiento de producto a ≤ −18 °C."}
   {:title "Vitrinas exhibidoras"      :icon "refrigerator"  :text "Exhibición y servicio de helados al público."}
   {:title "Aire acondicionado"        :icon "wind"          :text "Confort y control de ambiente del local."}
   {:title "Sistema eléctrico"         :icon "zap"           :text "Alimentación segura de todos los equipos."}
   {:title "Termómetros y sensores"    :icon "thermometer"   :text "Monitoreo continuo de cadena de frío."}
   {:title "Puertas y empaques"        :icon "door-open"     :text "Sellado hermético de cámaras."}
   {:title "Extractores y ventilación" :icon "fan"           :text "Circulación de aire y eliminación de olores."}])

(def mantenimiento-plan
  [{:que "Vitrina de helados"       :icon "refrigerator"  :como "Verificar temperatura (≤ −18 °C)"           :criterio "Diaria"     :freq-icon "sun"}
   {:que "Cámaras o congeladores"   :icon "snowflake"     :como "Verificar temperatura"                      :criterio "Diaria"     :freq-icon "sun"}
   {:que "Puertas de cámara"        :icon "door-open"     :como "Revisar cierre y sellos"                    :criterio "Diaria"     :freq-icon "sun"}
   {:que "Evaporadores visibles"    :icon "eye"           :como "Revisar acumulación de hielo"               :criterio "Diaria"     :freq-icon "sun"}
   {:que "Vitrina de helados"       :icon "sparkles"      :como "Limpieza profunda interior"                 :criterio "Semanal"    :freq-icon "calendar-check"}
   {:que "Cámaras o congeladores"   :icon "spray-can"     :como "Limpieza de estantes"                      :criterio "Semanal"    :freq-icon "calendar-check"}
   {:que "Empaques de puertas"      :icon "scan-eye"      :como "Limpieza y revisión"                       :criterio "Semanal"    :freq-icon "calendar-check"}
   {:que "Condensadores visibles"   :icon "wind"          :como "Eliminación de polvo"                      :criterio "Semanal"    :freq-icon "calendar-check"}
   {:que "Termómetros"              :icon "thermometer"   :como "Verificación contra termómetro patrón"     :criterio "Semanal"    :freq-icon "calendar-check"}
   {:que "Condensadores"            :icon "wrench"        :como "Limpieza técnica"                          :criterio "Mensual"    :freq-icon "calendar"}
   {:que "Ventiladores"             :icon "fan"           :como "Revisión de funcionamiento"                :criterio "Mensual"    :freq-icon "calendar"}
   {:que "Drenajes de cámaras"      :icon "droplets"      :como "Limpieza para evitar hielo"                :criterio "Mensual"    :freq-icon "calendar"}
   {:que "Sistema eléctrico"        :icon "zap"           :como "Verificación visual de cables"             :criterio "Mensual"    :freq-icon "calendar"}
   {:que "Puertas de cámara"        :icon "door-open"     :como "Ajuste de bisagras"                       :criterio "Mensual"    :freq-icon "calendar"}
   {:que "Sist. de refrigeración"   :icon "thermometer"   :como "Revisión de presión de refrigerante"      :criterio "Trimestral" :freq-icon "calendar-range"}
   {:que "Compresores"              :icon "cog"           :como "Inspección mecánica"                      :criterio "Trimestral" :freq-icon "calendar-range"}
   {:que "Sensores de temperatura"  :icon "gauge"         :como "Calibración o verificación"               :criterio "Trimestral" :freq-icon "calendar-range"}
   {:que "Aire acondicionado"       :icon "wind"          :como "Limpieza de filtros"                      :criterio "Trimestral" :freq-icon "calendar-range"}
   {:que "Sist. de refrigeración"   :icon "wrench"        :como "Mantenimiento completo"                   :criterio "Anual"      :freq-icon "calendar-clock"}
   {:que "Sistema eléctrico"        :icon "zap"           :como "Inspección técnica"                       :criterio "Anual"      :freq-icon "calendar-clock"}
   {:que "Termómetros"              :icon "thermometer"   :como "Calibración certificada"                  :criterio "Anual"      :freq-icon "calendar-clock"}])

;; ── Servicio al Cliente ───────────────────────────────────────
(def servicio-cliente-intro
  "HELADERÍA VALENTINO es una empresa comprometida con sus clientes, brindándoles productos de excelente calidad y servicio, por considerarlo el activo mayor de la Franquicia. El Franquiciatario debe velar por la entrega al cliente del producto de acuerdo a los lineamientos establecidos por la marca y en una presentación uniforme, atractiva e higiénica.")

(def servicio-cliente-rules
  [{:title "Recibimiento"
    :icon  "smile"
    :text "El recibimiento por parte del personal a los clientes es **«Bienvenido a Valentino»** amablemente."}
   {:title "Elección libre de sabores"
    :icon  "ice-cream-cone"
    :text "Los clientes pueden elegir los sabores de los helados, siropes y toppings libremente. En caso necesario, el dependiente puede recomendar."}
   {:title "Probar sabores"
    :icon  "utensils"
    :text "Al cliente se le debe permitir probar los sabores de los helados libremente."}
   {:title "Forma de pago"
    :icon  "credit-card"
    :text "La norma es que el cliente pague antes de que se le sirva el helado, pero si solicita que sea al revés, se debe hacer a preferencia del cliente."}
   {:title "Sabores nuevos"
    :icon  "star"
    :text "Si hay un sabor nuevo, el dependiente debe indicarlo y ofrecerlo al cliente para probar."}])

(def limpieza-gantt
  {:title "Ciclo calendario de limpieza"
   :days ["L" "M" "X" "J" "V" "S" "D"]
   :rows [{:label "Diario" :icon "sun" :active [0 1 2 3 4 5 6] :note "Todos los dias"}
          {:label "Semanal" :icon "calendar-check" :active [5] :note "Cada sabado"}
          {:label "Mensual" :icon "calendar" :active [0] :note "Primera semana"}]
   :note "Referencia visual para planificar el turno y distribuir tareas de limpieza."})

(def limpieza-calendar
  {:title "Calendario operativo de limpieza"
   :default-mode "unificado"
   :default-day "l"
   :month 3
   :year 2026
   :month-label "Marzo 2026"
   :modes [{:id "unificado" :label "Unificado" :icon "layers" :tone "unificado"}
           {:id "diario" :label "Diario" :icon "sun" :tone "diario"}
           {:id "semanal" :label "Semanal" :icon "calendar-check" :tone "semanal"}
           {:id "mensual" :label "Mensual" :icon "calendar" :tone "mensual"}]
   :days [{:id "l" :label "Lun"}
          {:id "m" :label "Mar"}
          {:id "x" :label "Mie"}
          {:id "j" :label "Jue"}
          {:id "v" :label "Vie"}
          {:id "s" :label "Sab"}
          {:id "d" :label "Dom"}]
   :date-cells [nil nil nil nil nil nil {:date 1 :day "d"}
                {:date 2 :day "l"} {:date 3 :day "m"} {:date 4 :day "x"} {:date 5 :day "j"} {:date 6 :day "v"} {:date 7 :day "s"} {:date 8 :day "d"}
                {:date 9 :day "l"} {:date 10 :day "m"} {:date 11 :day "x"} {:date 12 :day "j"} {:date 13 :day "v"} {:date 14 :day "s"} {:date 15 :day "d"}
                {:date 16 :day "l"} {:date 17 :day "m"} {:date 18 :day "x"} {:date 19 :day "j"} {:date 20 :day "v"} {:date 21 :day "s"} {:date 22 :day "d"}
                {:date 23 :day "l"} {:date 24 :day "m"} {:date 25 :day "x"} {:date 26 :day "j"} {:date 27 :day "v"} {:date 28 :day "s"} {:date 29 :day "d"}
                {:date 30 :day "l"} {:date 31 :day "m"} nil nil nil]
   :activities [{:mode "diario" :days ["l" "m" "x" "j" "v" "s" "d"] :time "Apertura" :icon "refrigerator"
                 :task "Vitrina: limpiar interior, enjuagar y dejar seca; registrar checklist."}
                {:mode "diario" :days ["l" "m" "x" "j" "v" "s" "d"] :time "Mediodia" :icon "house"
                 :task "Piso general: mantenimiento de areas de transito y puntos de contacto."}
                {:mode "diario" :days ["l" "m" "x" "j" "v" "s" "d"] :time "Cierre" :icon "cup-soda"
                 :task "Recipiente scooper: vaciar, lavar, enjuagar hasta agua clara y reponer."}
                {:mode "semanal" :days ["s"] :time "10:00" :icon "archive"
                 :task "Armarios basico: revisar tramos, limpiar superficies y reorganizar por rotacion."}
                {:mode "semanal" :days ["s"] :time "18:30" :icon "clipboard-check"
                 :task "Checklist semanal: validacion de tareas y firma de encargado."}
                {:mode "mensual" :days ["l"] :time "Semana 1" :icon "sparkles"
                 :task "Armarios profundo: retiro total, desinfeccion, secado y reordenamiento completo."}
                {:mode "mensual" :days ["l"] :time "Semana 1" :icon "folder-check"
                 :task "Consolidar y archivar registros diarios/semanales para auditoria."}]
   :note "Haz click en modo y dia para ver exactamente que tareas corresponden."})

(def limpieza-registro-modos
  [{:id "diario" :label "Diario" :icon "sun"}
   {:id "semanal" :label "Semanal" :icon "calendar-check"}
   {:id "mensual" :label "Mensual" :icon "calendar"}])

(def limpieza-spread-intro
  {:img "ValenAgostoA40of49-scaled.jpg"
   :alt "Helados Valentino — higiene y operación en tienda"
   :kicker "Limpieza y Desinfección"
   :title "Una tienda limpia es una tienda segura"
   :subtitle "El programa de limpieza de Valentino cubre cada superficie, equipo y utensilio — desde la vitrina de helados hasta la cámara fría, con frecuencias diarias, semanales y mensuales para garantizar la inocuidad del producto."
   :caption "Helados Valentino"})

(def limpieza-registro-meta-hint
  "Días anteriores: en papel, una hoja por día con la fecha indicada. El calendario arriba en esta página muestra qué tareas correspondían a cada día.")

(def limpieza-registro-meta
  [{:label "Sucursal" :value "_________________________"}
   {:label "Encargado" :value "_________________________"}
   {:label "Fecha" :value "____ / ____ / ______"}])

(def limpieza-registro-sucursales
  [{:id "suc-cataluna"   :name "Plaza Cataluña"       :icon "store"      :address "Av. Gustavo Mejia Ricart, Piantini"               :manager "Ana Gómez"    :tel "(809) 540-0998"}
   {:id "suc-galeria"    :name "Galería 360"           :icon "store"      :address "Av. JFK Esq. Bienvenido Garcia Gautier"            :manager "Carlos López" :tel "(809) 788-2122"}
   {:id "suc-agora"      :name "Ágora Mall"            :icon "store"      :address "Av. JFK Esq. Abraham Lincoln"                      :manager "María Torres" :tel "(809) 227-5379"}
   {:id "suc-sambil"     :name "Sambil"                :icon "store"      :address "Av. JFK, Sambil (Primer nivel feria)"              :manager "Luis Pérez"   :tel "(809) 547-4449"}
   {:id "suc-bluemall"   :name "BlueMall"              :icon "store"      :address "Av. Winston Churchill, BlueMall (4to nivel)"       :manager "Rosa Díaz"    :tel "(809) 955-3293"}
   {:id "suc-colonial"   :name "Zona Colonial"         :icon "store"      :address "Calle Isabel La Católica No. 157"                  :manager "Pedro Reyes"  :tel "(809) 692-7512"}
   {:id "suc-santiago"   :name "Plaza Paseo, Santiago"  :icon "store"      :address "Av. Juan Pablo Duarte, Santiago"                   :manager "Juana Marte"  :tel "(809) 233-1385"}
   {:id "suc-puntacana"  :name "Blue Mall Punta Cana"  :icon "store"      :address "Blvd. Turístico del Este, Punta Cana"              :manager "Frank Núñez"  :tel "(809) 784-7044"}])

(def limpieza-registro-timeframes
  [{:id "apertura" :label "Apertura 06:30-10:00" :icon "sunrise"}
   {:id "operacion" :label "Operacion 10:00-18:00" :icon "sun"}
   {:id "cierre" :label "Cierre 18:00-22:00" :icon "sunset"}])

(def limpieza-registro-stats
  [{:icon "check-check" :value "12" :label "controles diarios"}
   {:icon "calendar-check" :value "4" :label "controles semanales"}
   {:icon "calendar-range" :value "1" :label "control mensual"}
   {:icon "clipboard-list" :value "100%" :label "registro requerido"}])

(def limpieza-registro-ejemplo
  [{:freq "diario"
    :hora "07:30"
    :control "Temperatura vitrina y estado general"
    :colaborador "Ana Gómez"
    :accion "T: -18°C. Vitrina limpia, seca y operativa."}
   {:freq "diario"
    :hora "08:10"
    :control "Limpieza de vidrio y cortina"
    :colaborador "Carlos López"
    :accion "Interior/exterior sin residuos. Producto visible."}
   {:freq "diario"
    :hora "14:20"
    :control "Piso general y zona de tránsito"
    :colaborador "María Torres"
    :accion "Barrido y trapeado parcial de mantenimiento. Sin obstrucciones."}
   {:freq "semanal"
    :hora "SAB 10:00"
    :control "Armarios — limpieza básica"
    :colaborador "Luis Pérez"
    :accion "Revisión de tramos, limpieza visible y reorganización por rotación."}
   {:freq "semanal"
    :hora "SAB 18:30"
    :control "Verificación de checklist semanal"
    :colaborador "Ana Gómez"
    :accion "Checklist semanal firmada y archivada por encargado de turno."}
   {:freq "mensual"
    :hora "SEM 1"
    :control "Armarios — limpieza profunda"
    :colaborador "Carlos López"
    :accion "Retiro total de material, desinfección profunda, secado y reordenamiento."}
   {:freq "mensual"
    :hora "SEM 1"
    :control "Consolidado y resguardo de registros"
    :colaborador "Ana Gómez"
    :accion "Compilar registros diario/semanal y archivar para auditoría."}])

(def limpieza-registro-tips
  [{:icon "clock-3" :text "Escribir hora exacta y responsable en cada control."}
   {:icon "shield-alert" :text "Registrar no conformidades y acción correctiva aplicada."}
   {:icon "ban" :text "Evitar celdas vacías: usar N/A cuando corresponda."}
   {:icon "archive" :text "Conservar el registro para auditoría y trazabilidad."}])

;; ── Glossary ────────────────────────────────────────────────────
(def glosario-title "Glosario de Términos")

(def glosario-terms
  [{:id          "glosario-alergenos"
    :term        "ALÉRGENOS"
    :icon        "triangle-alert"
    :img         "glosario-personaje.png"
    :callouts    ["Guantes limpios" "Superficie separada"]
    :img-left    "glosario-lavado.png"
    :risk        "Una pequeña cantidad de alérgeno puede provocar reacciones graves o shock anafiláctico."
    :tip         "Si un cliente pregunta por alérgenos y no sabes la respuesta, consulta la ficha técnica del producto. Nunca improvises."
    :tip-img     "temp-bombillo.png"
    :def         "Ingrediente presente en algunos productos al que algunas personas son alérgicas. Las personas alérgicas desarrollan una respuesta inmunitaria que puede provocar reacciones leves: cutáneas, gastrointestinales; o graves: dificultades respiratorias e incluso la muerte (shock anafiláctico)."
    :lead        "En tienda, el riesgo no es solo el ingrediente original: también importa cualquier contacto accidental con utensilios, toppings, superficies o manos."
    :details     ["Identificar ingredientes declarables como leche, nueces, maní, gluten."
                  "Responder con precisión cuando un cliente pregunte; si no estás seguro, consulta la ficha técnica."
                  "Evitar contaminación cruzada separando utensilios y superficies."]
    :actions     ["Informar ingredientes con exactitud."
                  "Separar utensilios y superficies cuando sea posible."
                  "Cambiar guantes y lavar manos antes de atender una solicitud sensible."
                  "Lavarse las manos frecuentemente."]
    :action-imgs ["glosario-ficha2.png" "glosario-utensilios2.png" "glosario-lavado2.png" nil]}
   {:id          "glosario-cadena-de-frio"
    :term        "CADENA DE FRÍO"
    :icon        "snowflake"
    :theme       "cold"
    :img         "cadena-operador.png"
    :img-badge   "cadena-temperatura.png"
    :callouts    ["−18°C MAX" "−3°C vitrina"]
    :risk        "Helados por encima de 0°C pierden calidad, desarrollan bacterias y pueden resultar en enfermedades alimentarias."
    :tip         "Recuerda: nunca apagues un congelador por un corte eléctrico. Mantenlo cerrado hasta que vuelva la luz."
    :tip-img     "cadena-bombillo2.png"
    :def         "Conjunto de eslabones desde la producción hasta el consumo que garantiza que los helados se mantengan a temperaturas por debajo de 0°C. Previene el desarrollo de bacterias de deterioro y enfermedades alimentarias."
    :lead        "La cadena de frío significa que el producto nunca debe pasar por ciclos de descongelación y recongelación durante recepción, almacenamiento, vitrina y servicio."
    :details     ["Vigilar que la temperatura de las vitrinas, congeladores y productos recibidos esté siempre bajo 0°C."
                  "Registrar temperaturas en un log de frío para tener control y evidencia de que se mantiene la cadena de frío."
                  "Reportar inmediatamente cualquier desviación de temperatura o equipo con señales de avería."]
    :detail-imgs ["cadena-hielo.png" "cadena-lista.png" "cadena-registro.png"]
    :feat-img    "cadena-preparacion.png"
    :actions     ["Verificar temperaturas con frecuencia."
                  "Mover el producto rápidamente entre equipos."
                  "Reportar cualquier equipo que falle enseguida."]
    :action-imgs ["cadena-termometro.png" nil "cadena-herramienta.png"]}
   {:id          "glosario-temperatura-central"
    :term        "TEMPERATURA CENTRAL"
    :icon        "thermometer"
    :img-left    "temp-helado.png"
    :risk        "La lectura del equipo no siempre refleja la temperatura real interna del alimento."
    :tip         "La sonda debe limpiarse y desinfectarse antes y después de cada uso para evitar contaminación cruzada."
    :tip-img     "temp-bombillo.png"
    :def         "La temperatura en el interior del producto alimenticio. Se obtiene comprobándola con un termómetro con sonda de boquilla."
    :lead        "La lectura externa del equipo no siempre confirma la temperatura real del alimento; por eso la temperatura central es la referencia más útil."
    :details     ["Dos productos en el mismo congelador pueden tener temperaturas distintas según su ubicación."
                  "La sonda debe desinfectarse antes y después de cada uso."
                  "Medir en el punto más representativo del producto, evitando lecturas superficiales engañosas."]
    :actions     ["Usar termómetro calibrado."
                  "Desinfectar la sonda antes y después de medir."
                  "Registrar desviaciones y aplicar acción correctiva."]
    :action-imgs ["temp-termometro.png" "temp-spray.png" "temp-lista.png"]}
   {:id          "glosario-contaminacion-cruzada"
    :term        "CONTAMINACIÓN CRUZADA"
    :icon        "shuffle"
    :hero-img    "contam-diagrama.png"
    :risk        "Basta una cuchara, pinza o superficie compartida para contaminar un producto en buen estado."
    :tip         "La contaminación cruzada también aplica a alérgenos: trazas mínimas pueden afectar a clientes sensibles."
    :tip-img     "contam-bombillo.png"
    :def         "Transferencia de bacterias (patógenos) del producto A al producto B, ya sea directamente o de forma indirecta a través de manos, utensilios, fregadero, refrigeradores, etc. También aplica a alérgenos."
    :lead        "No hace falta mezclar productos para contaminar: basta compartir cuchara, pinza, mesa, recipiente, guante o una mano mal lavada."
    :details     ["Puede darse con superficies que tocaron cajas, basura, dinero o insumos sucios."
                  "Ocurre al manipular varios sabores sin limpiar utensilios o cambiar guantes."
                  "En alérgenos, trazas muy pequeñas pueden afectar a clientes sensibles."]
    :detail-imgs ["contam-cuchara.png" "contam-guante.png" nil]
    :actions     ["Separar tareas limpias y sucias."
                  "Lavar manos entre actividades."
                  "Limpiar y desinfectar utensilios y superficies con frecuencia."]
    :action-imgs ["contam-utensilios.png" nil "contam-alarma.png"]}
   {:id          "glosario-peps"
    :term        "PEPS"
    :icon        "arrow-right-left"
    :img         "cadena-nevera.png"
    :callouts    ["Antiguo al frente" "Nuevo al fondo"]
    :feat-img    "cadena-preparacion.png"
    :risk        "Un producto más antiguo que permanece atrás seguirá envejeciendo y puede caducar antes de usarse."
    :tip         "Si el etiquetado interno es confuso, el sistema PEPS deja de funcionar aunque el congelador parezca ordenado."
    :tip-img     "cadena-bombillo.png"
    :def         "Sistema de almacén que garantiza que los primeros productos que ingresan sean los primeros en despacharse, manteniendo la trazabilidad y evitando productos vencidos."
    :lead        "PEPS — Primeras Entradas, Primeras Salidas — no es solo orden visual; es una regla operativa para reducir pérdidas y vencimientos."
    :details     ["El producto más antiguo va al frente o en posición de uso inmediato, el nuevo detrás."
                  "Etiquetar con fecha de ingreso para facilitar la rotación correcta."
                  "Revisar el orden cada vez que entra producto nuevo al almacén."]
    :actions     ["Etiquetar fechas con claridad."
                  "Reordenar cada vez que entra producto nuevo."
                  "No usar primero el producto recién recibido."]
    :action-imgs ["temp-registro.png" "cadena-lista.png" "temp-peligro.png"]}
   {:id          "glosario-trazabilidad"
    :term        "TRAZABILIDAD"
    :icon        "scan-search"
    :img         "cadena-registro-papel.png"
    :callouts    ["Etiqueta visible" "Registro actualizado"]
    :risk        "Sin trazabilidad, un retiro de producto puede paralizar toda la operación innecesariamente."
    :tip         "Las etiquetas, facturas y registros de recepción son el sistema de trazabilidad. Consérvelos."
    :tip-img     "cadena-bombillo.png"
    :def         "Identificación del producto que permite rastrear todos los insumos y condiciones de producción, con el fin de poder retirarlo del mercado si fuera necesario."
    :lead        "La trazabilidad permite responder rápido a una queja, incidente o retiro de producto sin paralizar toda la operación."
    :details     ["Debe poder saberse qué lote se recibió, cuándo entró, dónde se almacenó y en qué periodo se utilizó."
                  "Sin trazabilidad, una incidencia pequeña puede convertirse en una pérdida mayor por inmovilizar más producto del necesario."
                  "Las etiquetas, facturas, registros de recepción y rotación son parte del sistema de trazabilidad."]
    :actions     ["Conservar etiquetas y documentos clave."
                  "Registrar entradas y movimientos."
                  "Retirar y aislar producto cuando no pueda identificarse correctamente."]
    :action-imgs ["glosario-ficha2.png" "cadena-lista.png" "temp-peligro.png"]}
   {:id          "glosario-fecha-de-vencimiento"
    :term        "FECHA DE VENCIMIENTO"
    :icon        "calendar-clock"
    :img-left    "temp-registro.png"
    :risk        "Un producto vencido puede parecer apto visualmente pero comprometer la seguridad del consumidor."
    :tip         "Productos sin fecha visible o ilegible deben tratarse como no conformes hasta aclarar su estado."
    :tip-img     "temp-bombillo.png"
    :def         "Fecha límite en la cual se puede consumir el producto sin que pierda sus características de calidad y seguridad."
    :lead        "La fecha de vencimiento no se interpreta ni se extiende; se respeta y se controla con disciplina diaria."
    :details     ["Revisar fechas al recibir, al almacenar y antes del servicio."
                  "Un producto vencido debe separarse y descartarse según el procedimiento."
                  "No reetiquetar para extender vida útil sin autorización de la gerencia."]
    :actions     ["Verificar fechas al recibir y al usar."
                  "Separar y descartar producto vencido según procedimiento."
                  "No reetiquetar para extender vida útil sin autorización."]
    :action-imgs ["cadena-lista.png" "temp-peligro.png" "cadena-registro-papel.png"]}])

;; ── Credits ─────────────────────────────────────────────────────
(def credits-title "Créditos")
(def credits-by "Manual elaborado por Carolina Mueses y Guillermo Molina.")
(def credits-orgs "Agrobiotek Internacional / GREB")
(def credits-legal
  "Este manual es de uso exclusivo de Helados Valentino (comisionado) y es confidencial. No puede ser divulgado, reproducido ni utilizado sin autorización previa y por escrito de Helados Valentino y de los titulares del documento. Queda prohibida su distribución a terceros.")

;; ── Images ──────────────────────────────────────────────────────
(def images-for-sections
  {:portada         ["ValenAgostoA40of49-scaled.jpg"]
   :contenido       ["6Cajitaportada-r8e2ojegj31fojq9dlnllec34fy60qwm3ffquq93b0.png"
                     "3-CajitaNuestrosSaboresMenu-r8e46cntsmpecvka5d1oubh3fkpnoxjehrzgy0gp30.png"]
   :introduccion    ["BarquillaMenu-r8e36ec2drhk9tsd3bkeyw3dfzx89pt0jtnv3zrt30.png"
                     "Avatarconcajita-r8e3rxsx02z4c4hyb4rmlyenivr1o0bkifvg19u0ho.png"]
   :microbiologico  ["Avatarconcajita2-r8e3sa0tgxfuj207bs1s0dbn8w2tg2o2w4cr9vbw8s.png"]
   :fisico          ["6Cajitaportada-r8e2ojegj31fojq9dlnllec34fy60qwm3ffquq93b0.png"]
   :alergenos       ["3-CajitaNuestrosSaboresMenu-r8e46cntsmpecvka5d1oubh3fkpnoxjehrzgy0gp30.png"]
   :quimico         ["riesgo-quimico-hero.png"
                     "riesgo-quimico-elementos_0000_Layer-2.png"
                     "riesgo-quimico-elementos_0001_Layer-3.png"
                     "riesgo-quimico-elementos_0002_Layer-4.png"
                     "riesgo-quimico-elementos_0003_Layer-5.png"]})

;; ── Sucursales / Map ──────────────────────────────────────────
(def valentino-sucursales
  [{:name "Plaza Cataluña"       :addr "Av. Gustavo Mejia Ricart Esq. Freddy Prestol Castillo, Piantini" :tel "(809) 540-0998" :hours "L-S 10AM-11PM | D 10AM-9PM"           :lat 18.4715 :lng -69.9370}
   {:name "Jumbo Luperón"        :addr "Av. Luperón Esq. Gustavo Mejia Ricart"                           :tel "(809) 378-0414" :hours "L-S 10AM-11PM | D 10AM-9PM"           :lat 18.4870 :lng -69.9700}
   {:name "Megacentro"           :addr "Av. San Vicente de Paul, Jumbo Megacentro"                       :tel "(809) 788-2478" :hours "L-S 11AM-10PM | D 11AM-8PM"           :lat 18.5050 :lng -69.8770}
   {:name "Occidental Mall"      :addr "Occidental Mall, Prolongación 27 de Febrero"                     :tel "(809) 692-3758" :hours "L-V 10AM-9PM | S-D 11AM-10PM"          :lat 18.4580 :lng -69.9780}
   {:name "Plaza Paseo, Santiago" :addr "Av. Juan Pablo Duarte, Supermercado Nacional, Santiago"          :tel "(809) 233-1385" :hours "L-S 11AM-10PM | D 11AM-8PM"           :lat 19.4500 :lng -70.6900}
   {:name "Casa España"          :addr "Av. Independencia, Club Casa España, Santo Domingo"              :tel "(809) 537-1802" :hours "L-S 11AM-10PM | D 11AM-8PM"           :lat 18.4690 :lng -69.9090}
   {:name "Plaza Central"        :addr "Av. 27 de Febrero Esq. Winston Churchill, Piantini"              :tel "(809) 735-9322" :hours "L-V 11AM-8PM | S-D 10AM-8PM"          :lat 18.4650 :lng -69.9450}
   {:name "Plaza Duarte"         :addr "Av. John F. Kennedy, Plaza Duarte (Carrefour)"                   :tel "(829) 893-3529" :hours "L-V 10AM-10PM | S-D 9AM-10PM"         :lat 18.4820 :lng -69.9520}
   {:name "Nacional Arroyo Hondo":addr "Calle Camino Chiquito, Supermercado Nacional"                    :tel "(809) 563-9491" :hours "L-V 10AM-9PM | S-D 1PM-9PM"           :lat 18.4950 :lng -69.9400}
   {:name "Galería 360"          :addr "Av. John F. Kennedy Esq. Bienvenido Garcia Gautier"              :tel "(809) 788-2122" :hours "L-V 11AM-10PM | S-D 11AM-11PM"         :lat 18.4790 :lng -69.9300}
   {:name "Nacional Tiradentes"  :addr "Av. Tiradentes Esq. Octavio del Pozo"                            :tel "(809) 732-7679" :hours "L-V 11AM-10PM | S-D 10AM-10PM"         :lat 18.4730 :lng -69.9250}
   {:name "Almacenes Unidos"     :addr "Av. Sarasota Esq. Pedro Ant. Bobea"                              :tel "(809) 286-0811" :hours "L-S 10AM-8PM | D 9AM-3PM"             :lat 18.4570 :lng -69.9430}
   {:name "Blue Mall Punta Cana" :addr "Blvd. Turístico del Este, Punta Cana"                            :tel "(809) 784-7044" :hours "L-S 10AM-9PM | D 10AM-8PM"            :lat 18.5800 :lng -68.3700}
   {:name "Plaza Estrella, Santiago" :addr "Av. Estrella Sadhalá, Supermercado Nacional, Santiago"        :tel "(809) 233-2618" :hours "L-S 11AM-10PM | D 11AM-8PM"           :lat 19.4550 :lng -70.6800}
   {:name "Florida Suites"       :addr "Av. Simón Bolívar Esq. Armando Rodríguez, La Esperilla"          :tel "(829) 824-0639" :hours "D-J 11AM-11PM | V-S 11AM-12AM"        :lat 18.4660 :lng -69.9320}
   {:name "Bella Vista Mall"     :addr "Av. Sarasota No. 62, Bella Vista"                                :tel "(829) 240-3442" :hours "L-D 10AM-10PM"                        :lat 18.4600 :lng -69.9390}
   {:name "Baní"                 :addr "Calle Presidente Billini No. 35, Mangos Food Park, Baní"         :tel "(809) 810-3087" :hours "L-M 12PM-10PM | J-D 12PM-11PM"        :lat 18.2850 :lng -70.3310}
   {:name "San Cristóbal"        :addr "Avenida Constitución No. 111, San Cristóbal"                     :tel "(809) 384-2474" :hours "L-J 10AM-9PM | V-D 10AM-10PM"         :lat 18.4170 :lng -70.1070}
   {:name "Sambil"               :addr "Av. John F. Kennedy, Sambil (Primer nivel feria)"                :tel "(809) 547-4449" :hours "L-V 11AM-10PM | S-D 11AM-11PM"         :lat 18.4800 :lng -69.9380}
   {:name "Zona Colonial"        :addr "Calle Isabel La Católica No. 157, Zona Colonial"                 :tel "(809) 692-7512" :hours "L-V 11AM-10PM | S-D 10AM-10PM"         :lat 18.4730 :lng -69.8840}
   {:name "Ágora Mall"           :addr "Av. John F. Kennedy Esq. Abraham Lincoln"                        :tel "(809) 227-5379" :hours "L-V 10AM-9PM | S-D 9AM-9PM"           :lat 18.4770 :lng -69.9420}
   {:name "Api Beach Punta Cana" :addr "La Marina, Cap Cana, Punta Cana"                                 :tel "(809) 469-7070" :hours "L-V 10AM-8PM | S-D 10AM-5PM"          :lat 18.5100 :lng -68.3700}
   {:name "Jumbo Bávaro"         :addr "Av. Barceló, DownTown Mall, Punta Cana"                          :tel "(809) 362-7505" :hours "L-V 10AM-9PM | S-D 11AM-10PM"          :lat 18.6800 :lng -68.4000}
   {:name "Evaristo Morales"     :addr "Calle Paseo de los Locutores No. 49, Local 103"                  :tel "(829) 546-1423" :hours "L-M 11:30AM-8PM | J-S 11:30AM-9PM | D 12PM-8PM" :lat 18.4730 :lng -69.9350}
   {:name "Acrópolis Center"     :addr "Acrópolis Center, Av. Winston Churchill"                         :tel "(829) 245-0909" :hours "L-S 11AM-10PM | D 11AM-8PM"           :lat 18.4660 :lng -69.9480}
   {:name "BlueMall"             :addr "Av. Winston Churchill, BlueMall (4to nivel)"                     :tel "(809) 955-3293" :hours "L-J 11AM-9PM | V-D 10AM-10PM"          :lat 18.4670 :lng -69.9520}
   {:name "Downtown Center"      :addr "Av. Rómulo Betancourt Esq. Núñez de Cáceres"                    :tel "(809) 487-6478" :hours "L-D 10AM-10PM"                         :lat 18.4580 :lng -69.9510}])

;; ── Preparación de productos ──────────────────────────────────
(def servicio-conos-vasos
  [{:step "01" :icon "hand" :title "Colocar guante"
    :text "El dependiente se coloca un guante plástico en la mano izquierda y con esta mano sostiene el cono o el vaso."}
   {:step "02" :icon "ice-cream-cone" :title "Servir el helado"
    :text "Con la mano derecha toma el scoop correspondiente al tamaño solicitado (75g, 100g o 130g) y sirve el helado."}])

(def servicio-empaques-llevar
  [{:step "01" :icon "hand" :title "Colocar guante"
    :text "El dependiente se coloca un guante plástico en la mano izquierda y con esta mano sostiene el tarro."}
   {:step "02" :icon "package" :title "Servir y empacar"
    :text "Con la mano derecha toma la pala y sirve el helado hasta llenar el tarro. Tapa el tarro y lo coloca en la funda correspondiente: pinta en funda blanca pequeña, dos pintas en funda blanca grande."}])

(def servicio-granizado-steps
  [{:step "01" :icon "hand" :title "Vaso rojo 12 oz"
    :text "Colocar guante en mano izquierda y sostener un vaso rojo plástico de 12 onzas."}
   {:step "02" :icon "cup-soda" :title "Servir granizado"
    :text "Con la mano derecha servir el granizado. Colocar el sorbete dejando un pedazo de papel como cubierta."}])

(def servicio-malteada-steps
  [{:step "01" :icon "hand" :title "Preparar vaso"
    :text "Colocar guante en mano izquierda y sostener el vaso del drinkmixer."}
   {:step "02" :icon "ice-cream-cone" :title "Agregar helado y leche"
    :text "Servir dos scoops de 130g. Cubrir las dos bolas casi completas de leche entera Parmalat UHT."}
   {:step "03" :icon "blend" :title "Batir"
    :text "Colocar en drinkmixer, mover con cuchara para verificar que no quede helado sin batir, volver a batir."}
   {:step "04" :icon "cup-soda" :title "Servir"
    :text "Servir en vaso transparente de 12 oz con tapa redonda alta y sorbete, dejando papel como cubierta."}])

(def servicio-fondant-steps
  [{:step "01" :icon "package-x" :title "Retirar plástico"
    :text "Retirar el plástico del fondant."}
   {:step "02" :icon "microwave" :title "Calentar"
    :text "Colocar en el microondas durante un minuto."}
   {:step "03" :icon "ice-cream-cone" :title "Agregar helado"
    :text "Retirar del microondas y colocar encima una bola de 130g de helado."}
   {:step "04" :icon "sparkles" :title "Decorar"
    :text "Colocar nata alrededor y un topping encima del helado. Puede prepararse para llevar en caja plástica transparente."}])

(def servicio-crepe-steps
  [{:step "01" :icon "power" :title "Encender crepera"
    :text "Encender la crepera a 350 °F. Esperar hasta que encienda el botón «ready». Poner aceite de oliva con brocha."}
   {:step "02" :icon "utensils" :title "Preparar crepe"
    :text "Mover la mezcla con cuchara. Tomar una medida y colocarla lentamente en la crepera. Expandir con espátula. Voltear."}
   {:step "03" :icon "sparkles" :title "Rellenar y decorar"
    :text "Añadir Nutella o dulce de leche en zigzag. Doblar en cuatro. Transferir a plato. Decorar encima en zigzag."}
   {:step "04" :icon "ice-cream-cone" :title "Completar"
    :text "Colocar una bola de 130g de helado encima. Crema chantilly en dos extremos. Añadir topping."}])

(def servicio-banana-split-steps
  [{:step "01" :icon "banana" :title "Base"
    :text "Colocar una banana pelada en plato tipo bote."}
   {:step "02" :icon "ice-cream-cone" :title "Helado y sirope"
    :text "Colocar tres bolas de 130g de helado encima. Sirope de chocolate, fresa o caramelo en zigzag."}
   {:step "03" :icon "sparkles" :title "Decorar"
    :text "Colocar topping encima y crema chantilly en ambos extremos. Puede prepararse para llevar en caja plástica."}])

(def servicio-brownie-steps
  [{:step "01" :icon "microwave" :title "Calentar brownie"
    :text "Colocar sin plástico en plato cuadrado. Microondas por 45 segundos. Hacer corte en diagonal."}
   {:step "02" :icon "ice-cream-cone" :title "Agregar helado"
    :text "En el espacio del corte colocar dos bolas de helado. Sirope en zigzag."}
   {:step "03" :icon "sparkles" :title "Decorar"
    :text "Topping encima del helado. Crema chantilly en dos extremos del plato."}])

;; ── Quejas ────────────────────────────────────────────────────
(def quejas-intro
  "En HELADERÍA VALENTINO **«el cliente siempre tiene la razón»**. Los Franquiciatarios y sus empleados deben esforzarse por crear relaciones en las que el cliente nunca se sienta que está equivocado. La capacidad de manejar las quejas es el signo de un gran servicio al cliente.")

(def quejas-estrategia
  [{:title "Escuchar"
    :icon  "ear"
    :text "Dar la ***máxima atención*** al cliente, manteniendo el ***contacto visual*** sin interrupción, hasta que toda su frustración sea ventilada."}
   {:title "Disculparse"
    :icon  "heart-handshake"
    :text "Repetir lo que el cliente ha dicho de manera ***positiva, agradable, segura y tranquila***. Ofrecer una disculpa que transmita ***sinceridad***."}
   {:title "Resolver"
    :icon  "check-circle"
    :text "Preguntar al cliente cómo le gustaría que el problema sea resuelto y tomar ***medidas correctivas inmediatas***."}
   {:title "Dar gracias"
    :icon  "thumbs-up"
    :text "***Dar gracias*** al cliente por traer la queja y utilizar la información para ***mejorar el servicio***. Informar al Franquiciador de todas las quejas."}])
