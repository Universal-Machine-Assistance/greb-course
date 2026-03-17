(ns romerlabs.content
  "All content data for the Romer Labs food safety diagnostics course.")

;; ── Index ───────────────────────────────────────────────────────
(def index-title "Índice")

(def index-entries
  [;; Front matter
   {:id "portada"                  :label "Portada"                           :page 1}
   {:id "contenido"                :label "Contenido"                         :page 2}
   {:id "indice"                   :label "Índice"                            :page 3}
   {:id "introduccion"             :label "Introducción y contexto"           :page 4}
   ;; Technologies
   {:id "tecnologias"              :label "Tecnologías de detección"          :page 5}
   {:id "agrastrip"                :label "AgraStrip® — Test rápido"          :page 6}
   {:id "agrastrip-historia"       :label "AgraStrip® — Historia"             :page 7}
   {:id "agraquant"                :label "AgraQuant® — ELISA cuantitativo"   :page 8}
   {:id "agraquant-historia"       :label "AgraQuant® — Historia"             :page 9}
   {:id "agravision"               :label "AgraVision™ — Lectura digital"     :page 10}
   ;; AgraVision & ELISA
   {:id "agravision-producto"      :label "AgraVision™ — Producto y specs"    :page 11}
   {:id "elisa"                    :label "ELISA — Fundamentos"               :page 12}
   {:id "elisa-historia"           :label "ELISA — Historia y lector"         :page 13}
   {:id "flujo-analisis"           :label "Flujo de análisis y analitos"      :page 14}
   ;; ABT
   {:id "por-que-abt"              :label "ABT Internacional"                 :page 15}
   {:id "abt-servicios"            :label "¿Por qué ABT?"                    :page 16}
   {:id "contacto"                 :label "Contacto regional"                 :page 17}])

;; ── TOC sections ────────────────────────────────────────────────
(def contenido-title "Diagnóstico Analítico para Seguridad Alimentaria")
(def contenido-subtitle "Contenido")

(def contenido-sections
  [{:id "introduccion" :img "lab-equipment.png" :title "Introducción"
    :items [{:label "Romer Labs y soluciones"  :ok true}
            {:label "Impacto y contexto"       :ok true}]}
   {:id "agrastrip" :img "agrastrip-cartridges.png" :title "Tecnologías"
    :items [{:label "AgraStrip® — Test rápido"  :ok true}
            {:label "AgraQuant® — ELISA"        :ok true}
            {:label "AgraVision™ — Digital"     :ok true}]}
   {:id "elisa" :img "drops-on-elisa.webp" :title "ELISA y Analitos"
    :items [{:label "Fundamentos ELISA"         :ok true}
            {:label "Flujo de análisis"         :ok true}]}
   {:id "por-que-abt" :img "abt-equipo-planta.png" :title "ABT Internacional"
    :items [{:label "Servicios y ventajas"      :ok true}
            {:label "Contacto en 5 países"      :ok true}]}])

;; ── Introduction ────────────────────────────────────────────────
(def intro-title "Romer Labs — Diagnóstico Analítico")

(def intro-lead
  "Romer Labs, una división de ERBER Group, es líder mundial en soluciones de diagnóstico para la industria alimentaria y de piensos. Con más de 40 años de experiencia, desarrolla y fabrica kits de diagnóstico innovadores para la detección de micotoxinas, alérgenos alimentarios, patógenos, residuos y organismos GM. Presencia en más de 100 países.")

(def intro-capabilities
  [{:title "Micotoxinas" :icon "biohazard"
    :text "Detección de toxinas producidas por hongos en granos y alimentos procesados."}
   {:title "Patógenos" :icon "bug"
    :text "Detección de contaminación microbiológica en alimentos y superficies."}
   {:title "Proteínas específicas" :icon "flask-conical"
    :text "Cuantificación de proteínas de interés como caseína, proteína de huevo y leche."}
   {:title "Alérgenos" :icon "wheat"
    :text "Identificación de proteínas alergénicas como gluten, maní, soya, leche y huevo."}])

(def intro-stats
  [{:icon "globe" :label "Presencia en más de" :value "100 países"}
   {:icon "microscope" :label "Investigación y desarrollo de kits de diagnóstico" :value "de vanguardia"}
   {:icon "shield-check" :label "Cumplimiento con las normativas de seguridad" :value "y calidad"}])

;; ── About Romer Labs ─────────────────────────────────────────
(def romerlabs-about
  "Romer Labs es líder mundial en soluciones de diagnóstico innovadoras para la seguridad alimentaria y de piensos. Con enfoque en micotoxinas, alérgenos alimentarios, OGM y contaminantes microbiológicos, se esfuerza por satisfacer las demandas cambiantes de sus clientes. La innovación está en el centro de todo lo que hace: explora constantemente nuevas tecnologías para simplificar flujos de trabajo, mejorar la precisión y aumentar la fiabilidad. Parte de dsm-firmenich.")

(def romerlabs-historia-timeline
  [{:year "1982" :title "Fundación"
    :text "Tom y Marie Romer fundan Romer Labs Inc. en Washington, MO. Inicialmente un servicio de análisis de micotoxinas."}
   {:year "1995" :title "Primer kit de prueba"
    :text "Lanzamiento de FluoroQuant®, el primer kit de detección de micotoxinas de Romer Labs."}
   {:year "1999" :title "Adquisición por ERBER AG"
    :text "ERBER AG (holding austriaco) adquiere Romer Labs. Comienza la internacionalización."}
   {:year "2002" :title "Sede en Austria"
    :text "Apertura de oficina en Tulln, Austria, que se convierte en la sede global de Romer Labs."}
   {:year "2007" :title "Expansión a Brasil"
    :text "Oficina en Campinas, Brasil. Presencia en Asia-Pacífico desde 2004 (Singapur) y China desde 2009."}
   {:year "2012" :title "Adquisición SDIX"
    :text "Adquisición de SDIX Food Safety y GMO Business. Portafolio completo en microbiología y detección de OGM."}
   {:year "2018" :title "Nuevo centro I+D"
    :text "Inauguración del nuevo centro de I+D y producción en Tulln con laboratorio ISO 17025. Apertura de Romer Labs México."}
   {:year "2020" :title "dsm-firmenich"
    :text "dsm-firmenich adquiere Romer Labs junto con Biomin. Misión: Making the World's Food Safer®."}])

(def romerlabs-calidad
  [{:title "ISO 9001" :icon "shield-check" :text "Sistema de gestión de calidad certificado para procesos de producción y servicios."}
   {:title "ISO 17025" :icon "microscope" :text "Requisitos técnicos para laboratorios de análisis. Implementado en todos los laboratorios de servicios analíticos."}
   {:title "ISO 17034" :icon "flask-conical" :text "Competencia como productor de materiales de referencia certificados."}
   {:title "Sostenibilidad" :icon "leaf" :text "Compromiso con la neutralidad de carbono. Compensación de emisiones de CO₂ mediante proyectos de protección forestal y energías renovables."}])

(def intro-soluciones
  [{:title "AgraStrip® — Screening rápido" :icon "zap"
    :text "Test de flujo lateral para análisis en 4–10 minutos. Ideal en planta o campo. Resultados cualitativos (presencia/ausencia) sin laboratorio."}
   {:title "AgraQuant® — Cuantificación ELISA" :icon "bar-chart-3"
    :text "Análisis cuantitativo en microplaca de 96 pozos. Mide concentración exacta en ppm/ppb. Para laboratorios de control de calidad."}
   {:title "AgraVision™ — Lectura digital" :icon "monitor"
    :text "Lector óptico que digitaliza las tiras AgraStrip Pro. Elimina interpretación visual subjetiva. Incubador integrado + trazabilidad LIMS."}
   {:title "Sistema integrado" :icon "link"
    :text "Las tres tecnologías trabajan juntas: screening con AgraStrip → confirmación con AgraQuant → lectura digital con AgraVision. Cobertura completa."}])

(def intro-highlight
  {:icon "globe"
   :title "Presencia en más de 100 países"
   :items ["Investigación y desarrollo de kits de diagnóstico de vanguardia"
           "Cumplimiento con las normativas de seguridad y calidad"]})

(def intro-users
  ["Laboratorios de control de calidad"
   "Plantas de alimentos"
   "Exportadores agrícolas"
   "Laboratorios regulatorios"
   "Centros de investigación"])

;; ── Problem ─────────────────────────────────────────────────────
(def problema-title "¿Por qué es necesario?")

(def problema-lead
  "En la producción de alimentos existe un desafío fundamental: muchos contaminantes peligrosos no pueden detectarse a simple vista.")

(def problema-examples
  [{:title "Micotoxinas en granos" :icon "wheat"
    :text "Producidas por hongos como Aspergillus y Fusarium, pueden estar presentes en concentraciones de partes por billón (ppb). Causan daños hepáticos, renales e inmunológicos. Límites regulatorios: 4–20 ppb para aflatoxinas según FDA/UE."}
   {:title "Alérgenos ocultos" :icon "alert-triangle"
    :text "Proteínas alergénicas en alimentos procesados pueden causar reacciones graves, incluso shock anafiláctico, en cantidades de miligramos. Regulaciones como FALCPA y EU-FIC exigen declaración obligatoria de 14 alérgenos principales."}
   {:title "Contaminación cruzada" :icon "shuffle"
    :text "En líneas de producción compartidas, trazas de un producto pueden transferirse a otro sin ser visibles. Un cambio de lote sin limpieza adecuada puede contaminar miles de unidades con alérgenos no declarados."}
   {:title "Patógenos microbiológicos" :icon "bug"
    :text "Bacterias como Salmonella, Listeria monocytogenes o E. coli O157:H7 pueden estar presentes sin alterar el aspecto, olor o sabor del alimento. Causan brotes que afectan a miles de personas."}])

(def problema-impacto
  [{:icon "heart-pulse" :label "Salud pública" :value "millones afectados/año"}
   {:icon "ban" :label "Retiros de producto" :value "pérdidas millonarias"}
   {:icon "scale" :label "Regulaciones" :value "FDA · UE · Codex"}])

(def problema-closing
  "Las tecnologías de Romer Labs permiten identificar estos contaminantes antes de que los alimentos lleguen al consumidor, ayudando a garantizar la inocuidad alimentaria.")

;; ── Technologies overview ───────────────────────────────────────
(def tecnologias-title "Tecnologías principales")
(def tecnologias-subtitle "Romer Labs ofrece varias tecnologías complementarias que permiten analizar contaminantes en diferentes etapas del proceso productivo.")

(def tecnologias-overview
  [{:title "AgraStrip®" :icon "zap"
    :text "Test rápido de flujo lateral — resultados en 4–10 minutos. Ideal para screening en planta, campo o punto de recepción de materias primas. Formato portátil sin laboratorio."}
   {:title "AgraQuant®" :icon "bar-chart-3"
    :text "Análisis ELISA cuantitativo en microplaca de 96 pozos — mide concentración exacta de contaminantes en ppm o ppb. Para laboratorio de control de calidad."}
   {:title "AgraVision™" :icon "monitor"
    :text "Lector óptico digital para tiras AgraStrip Pro — elimina interpretación subjetiva, incubador integrado, pantalla táctil y exportación de datos para trazabilidad LIMS."}
   {:title "Integración completa" :icon "link"
    :text "Las tres tecnologías funcionan como sistema integrado: screening rápido con AgraStrip, confirmación cuantitativa con AgraQuant, lectura digital con AgraVision. Cobertura completa del proceso."}])

;; ── AgraStrip ───────────────────────────────────────────────────
(def agrastrip-nombre-desglose
  [{:title "Agra" :icon "wheat"
    :text "Agricultura / agroindustria — diseñado para el sector agrícola, alimentario y de piensos."}
   {:title "Strip" :icon "scan-line"
    :text "Tira reactiva — formato de tira inmunocromatográfica donde la muestra fluye y genera líneas de color visibles."}])

(def agrastrip-historia-timeline
  [{:year "1980" :title "Primeros inmunoensayos rápidos"
    :text "Aparecen los primeros tests rápidos basados en anticuerpos para uso fuera de laboratorio. Formato similar al test de embarazo — resultado visual de presencia/ausencia."}
   {:year "1990" :title "Tiras de flujo lateral (LFD)"
    :text "Se desarrollan las tiras de flujo lateral inmunocromatográficas. La muestra migra por capilaridad y reacciona con anticuerpos inmovilizados. Adopción en diagnóstico médico rápido."}
   {:year "2000" :title "Romer Labs lanza AgraStrip"
    :text "Romer Labs adapta la tecnología de flujo lateral al sector alimentario. Kits específicos para micotoxinas (aflatoxinas, DON, fumonisinas) con resultados en minutos."}
   {:year "2008" :title "AgraStrip Pro"
    :text "Versión mejorada con tiras cuantificables mediante lector AgraVision. Mayor rango de analitos: alérgenos (gluten, maní, soya, sésamo), GMO y más micotoxinas."}
   {:year "HOY" :title "Plataforma completa"
    :text "AgraStrip cubre más de 20 analitos. Compatible con AgraVision Pro para lectura digital. Usado en más de 80 países en recepción de materias primas, plantas y laboratorios."}])

(def agrastrip-catalogo
  [{:title "Micotoxinas" :icon "wheat"
    :text "Aflatoxinas (total y B1), DON, Fumonisinas, Zearalenona, Ocratoxina A, T-2/HT-2. Rangos de detección desde 2 ppb."}
   {:title "Alérgenos" :icon "alert-triangle"
    :text "Gluten/Gliadina, Maní, Soya, Sésamo, Leche (caseína), Huevo (ovoalbúmina). Para limpieza y producto terminado."}
   {:title "GMO" :icon "dna"
    :text "Detección de proteínas de organismos genéticamente modificados: Cry1Ab, Cry9C, CP4 EPSPS, PAT/pat, entre otros."}
   {:title "Otros" :icon "flask-conical"
    :text "Tests de higiene y limpieza. Verificación de superficies. Control de alérgenos residuales en líneas de producción compartidas."}])

(def agrastrip-specs
  [{:title "Formato" :text "Tira inmunocromatográfica de flujo lateral (LFD) en cartucho plástico"}
   {:title "Tiempo de análisis" :text "4–10 minutos según analito (incluye extracción rápida)"}
   {:title "Tipo de resultado" :text "Cualitativo (visual) o cuantitativo (con AgraVision Pro)"}
   {:title "Almacenamiento" :text "2–8°C. Llevar a temperatura ambiente antes de usar"}
   {:title "Muestra" :text "Granos, harinas, alimentos procesados, superficies, agua de enjuague"}
   {:title "Certificaciones" :text "Validaciones AOAC-RI, GIPSA. Aceptado por FDA y organismos europeos"}])

(def agrastrip-features
  [{:title "Resultados en 4–10 minutos" :icon "zap"
    :text "Diagnóstico rápido que permite tomar decisiones inmediatas sobre materias primas y producto en proceso. Ideal para screening de lotes en recepción."}
   {:title "Fácil de usar" :icon "user-check"
    :text "No requiere personal altamente especializado ni formación técnica avanzada. Instrucciones claras paso a paso incluidas en cada kit."}
   {:title "Análisis en planta o campo" :icon "map-pin"
    :text "Portátil y funcional sin necesidad de laboratorio completo. Se puede usar en silos, bodegas, plantas de procesamiento y puntos de acopio."}
   {:title "Mínimo equipamiento" :icon "package"
    :text "Solo requiere el kit de test y opcionalmente un lector AgraVision™ para obtener resultados cuantitativos digitales con trazabilidad."}
   {:title "Formato inmunocromatográfico" :icon "scan-line"
    :text "Basado en flujo lateral (lateral flow): la muestra migra por la tira y reacciona con anticuerpos específicos formando líneas visibles de color."}
   {:title "Amplio rango de analitos" :icon "list-checks"
    :text "Kits disponibles para aflatoxinas, DON, fumonisinas, zearalenona, ocratoxina A, T-2/HT-2, gluten, maní, soya, sésamo y más."}])

(def agrastrip-applications
  ["Recepción de materias primas"
   "Monitoreo de proceso"
   "Verificación de limpieza"
   "Control de alérgenos"
   "Screening rápido de micotoxinas"])

;; ── AgraQuant ───────────────────────────────────────────────────
(def agraquant-nombre-desglose
  [{:title "Agra" :icon "wheat"
    :text "Agricultura / agroindustria — kits diseñados para matrices alimentarias y agrícolas."}
   {:title "Quant" :icon "bar-chart-3"
    :text "Cuantificación — a diferencia de AgraStrip (cualitativo), AgraQuant mide la concentración exacta del analito en ppm o ppb."}])

(def agraquant-historia-timeline
  [{:year "1971" :title "Invención del ELISA"
    :text "Eva Engvall y Peter Perlmann desarrollan la técnica ELISA. Base científica sobre la cual se construirán los kits AgraQuant décadas después."}
   {:year "1990" :title "ELISA en alimentos"
    :text "La técnica ELISA se adapta para análisis de micotoxinas y alérgenos en la industria alimentaria. Primeros kits comerciales aparecen en el mercado."}
   {:year "2000" :title "Romer Labs lanza AgraQuant"
    :text "Romer Labs desarrolla la línea AgraQuant: kits ELISA cuantitativos optimizados para matrices alimentarias. Validaciones AOAC y GIPSA desde el inicio."}
   {:year "2010" :title "Expansión de catálogo"
    :text "Se amplía la gama a más de 30 kits: micotoxinas (aflatoxinas, DON, ZEA, OTA, fumonisinas, T-2), alérgenos (gluten, maní, soya, leche, huevo, sésamo) y proteínas GM."}
   {:year "HOY" :title "Estándar de la industria"
    :text "AgraQuant es referencia para análisis cuantitativos en laboratorios de control de calidad. Compatible con lectores ELISA estándar. Usado por exportadores, plantas y laboratorios regulatorios."}])

(def agraquant-specs
  [{:title "Formato" :text "Microplaca ELISA de 96 pozos recubierta con anticuerpos específicos"}
   {:title "Método" :text "ELISA competitivo o sándwich según analito. Detección colorimétrica a 450 nm"}
   {:title "Sensibilidad" :text "Desde 1 ppb (aflatoxinas) hasta 4 ppm (gluten) según kit"}
   {:title "Tiempo" :text "45–90 minutos incluyendo extracción, incubación y lectura"}
   {:title "Lector requerido" :text "Lector de microplacas ELISA con filtro de 450 nm (ej. BioTek 800 TS)"}
   {:title "Certificaciones" :text "Validaciones AOAC-RI, GIPSA. Programas de competencia interlaboratorial FAPAS"}])

(def agraquant-features
  [{:title "Análisis cuantitativo" :icon "bar-chart-3"
    :text "Mide la concentración exacta del contaminante en la muestra, no solo su presencia o ausencia. Valores numéricos precisos para toma de decisiones."}
   {:title "Alta sensibilidad" :icon "target"
    :text "Capaz de detectar analitos en rangos de partes por billón (ppb). Sensibilidad comparable a métodos instrumentales como HPLC o LC-MS/MS."}
   {:title "Múltiples muestras" :icon "layout-grid"
    :text "Permite analizar hasta 40 muestras simultáneamente en una microplaca ELISA de 96 pozos, incluyendo estándares y controles."}
   {:title "Resultados en ppm o ppb" :icon "calculator"
    :text "Valores cuantitativos precisos mediante curva de calibración. Cumplimiento directo con límites regulatorios de FDA, UE y Codex Alimentarius."}
   {:title "Validación internacional" :icon "award"
    :text "Kits validados según métodos AOAC, GIPSA y programas de competencia interlaboratorial. Resultados aceptados por organismos regulatorios."}
   {:title "Amplio catálogo" :icon "list-checks"
    :text "Kits disponibles para micotoxinas (aflatoxinas, DON, fumonisinas, ZEA, OTA), alérgenos (gluten, maní, soya, leche, huevo) y proteínas GM."}])

(def agraquant-procedure
  [{:step "01" :title "Extracción" :icon "flask-conical" :text "Pesar y moler la muestra representativa. Mezclar con solución de extracción proporcionada en el kit durante el tiempo indicado (5–20 min según analito)."}
   {:step "02" :title "Dilución" :icon "droplets" :text "Filtrar o centrifugar el extracto y diluir según las instrucciones del kit. Preparar los estándares de calibración proporcionados."}
   {:step "03" :title "Incubación" :icon "thermometer" :text "Pipetear estándares, controles y muestras en la microplaca ELISA recubierta con anticuerpos. Incubar durante el tiempo indicado (10–30 min)."}
   {:step "04" :title "Lavado" :icon "droplet" :text "Aspirar el contenido de los pozos y lavar 3–5 veces con solución de lavado para eliminar las sustancias no unidas."}
   {:step "05" :title "Conjugado enzimático" :icon "link" :text "Añadir el conjugado (anticuerpo marcado con enzima HRP o AP). Incubar para permitir la unión al complejo anticuerpo-antígeno."}
   {:step "06" :title "Sustrato y lectura" :icon "scan-line" :text "Añadir sustrato cromogénico (TMB). Incubar hasta desarrollo de color. Detener la reacción y medir absorbancia a 450 nm en lector ELISA."}
   {:step "07" :title "Cálculo" :icon "calculator" :text "Software calcula la concentración de cada muestra interpolando en la curva de calibración generada por los estándares. Resultado en ppm o ppb."}])

(def agraquant-uses
  ["Laboratorios de control de calidad"
   "Exportación agrícola"
   "Análisis confirmatorios"
   "Cumplimiento regulatorio"])

;; ── ELISA ───────────────────────────────────────────────────────
(def elisa-title "¿Qué es ELISA?")
(def elisa-subtitle "Enzyme-Linked Immunosorbent Assay — técnica analítica para detectar y cuantificar sustancias en alimentos y muestras biológicas.")

(def elisa-intro
  [{:title "Técnica universal" :icon "microscope"
    :text "ELISA detecta y mide concentraciones de proteínas, toxinas, alérgenos, hormonas, virus o bacterias en una muestra."}
   {:title "Seguridad alimentaria" :icon "shield-check"
    :text "Se emplea para identificar contaminantes como micotoxinas o alérgenos en alimentos, granos y materias primas."}
   {:title "Múltiples muestras" :icon "layout-grid"
    :text "Permite analizar múltiples muestras simultáneamente, ofreciendo resultados cuantitativos para control de calidad."}
   {:title "Alta sensibilidad" :icon "target"
    :text "Detecta sustancias en rangos de ppm o ppb — niveles imposibles de identificar visualmente."}])

(def elisa-problema
  [{:title "Micotoxinas en granos" :icon "wheat"
    :text "Toxinas producidas por hongos presentes en concentraciones de partes por billón que causan daños hepáticos e inmunológicos."}
   {:title "Alérgenos ocultos" :icon "alert-triangle"
    :text "Proteínas alergénicas en alimentos procesados que causan reacciones graves incluso en cantidades mínimas."}
   {:title "Residuos biológicos" :icon "bug"
    :text "Contaminantes proteicos y residuos biológicos invisibles que requieren métodos analíticos especializados para su detección."}])

(def elisa-principio
  [{:title "Anticuerpo-Antígeno"
    :text "Un anticuerpo reconoce y se une selectivamente a una molécula particular (antígeno), como una llave y cerradura."}
   {:title "Superficie sólida"
    :text "Los anticuerpos se fijan en una microplaca de laboratorio con múltiples pozos (wells) donde se introduce la muestra."}
   {:title "Reacción enzimática"
    :text "Un sistema enzimático genera una reacción química que produce un cambio de color medible proporcional al analito."}
   {:title "Medición óptica"
    :text "La intensidad del color se mide con un lector a 450 nm y se compara con una curva de calibración."}])

(def elisa-procedure
  [{:step "01" :title "Preparación" :text "La muestra se mezcla con solvente para extraer las moléculas objetivo, produciendo un extracto líquido."}
   {:step "02" :title "Incubación" :text "El extracto se añade a pozos con anticuerpos específicos. La molécula objetivo se une a los anticuerpos fijados."}
   {:step "03" :title "Reacción enzimática" :text "Se añade un reactivo con enzima unida a otro anticuerpo que participará en la reacción de color."}
   {:step "04" :title "Desarrollo de color" :text "Al agregar sustrato químico, la enzima cataliza una reacción que produce color dentro del pozo."}
   {:step "05" :title "Medición óptica" :text "El lector ELISA ilumina cada pozo a 450 nm y mide la absorbancia — proporcional al analito presente."}
   {:step "06" :title "Cuantificación" :text "La absorbancia se compara con estándares de concentración conocida mediante curva de calibración."}])

(def elisa-ventajas
  [{:icon "target" :label "Alta sensibilidad" :value "ppb / ppm"}
   {:icon "lock" :label "Alta especificidad" :value "anticuerpos"}
   {:icon "layout-grid" :label "Análisis simultáneo" :value "múltiples muestras"}])

(def elisa-aplicaciones
  [{:title "Micotoxinas"
    :text "Aflatoxinas, DON, fumonisinas y zearalenona en granos y alimentos procesados."}
   {:title "Alérgenos alimentarios"
    :text "Gluten, maní, sésamo, soya, leche y huevo en productos industriales."}
   {:title "Control de autenticidad"
    :text "Verificación de proteínas específicas para garantizar la integridad del producto."}
   {:title "Análisis regulatorio"
    :text "Laboratorios de control de calidad, exportadores, plantas de alimentos y organismos regulatorios."}])

;; ── ELISA — Significado y origen ───────────────────────────────
(def elisa-siglas-title "¿Qué significan las siglas?")
(def elisa-siglas-subtitle "Enzyme-Linked ImmunoSorbent Assay — Ensayo inmunoenzimático ligado a enzimas.")

(def elisa-siglas-desglose
  [{:title "Enzyme-Linked" :icon "flask-conical"
    :text "Usa una enzima para producir una reacción detectable — el cambio de color que permite la medición."}
   {:title "Immuno" :icon "shield-check"
    :text "Se basa en anticuerpos que reconocen moléculas específicas con alta selectividad (interacción antígeno-anticuerpo)."}
   {:title "Sorbent" :icon "square-dot"
    :text "Las moléculas se adhieren a una superficie sólida — la microplaca de 96 pozos donde ocurre la reacción."}
   {:title "Assay" :icon "clipboard-check"
    :text "Significa prueba analítica o ensayo de laboratorio — un procedimiento estandarizado para medir una sustancia."}])

(def elisa-idea-clave
  {:icon "lightbulb"
   :title "anticuerpo + enzima + color = medición química precisa"
   :items ["Detectar moléculas invisibles de forma segura, cuantificable y escalable"
           "Reemplazó los marcadores radiactivos por enzimas — más seguro y económico"
           "Base de tecnologías derivadas: CLIA, tests de flujo lateral (AgraStrip, COVID)"]})

(def elisa-historia-title "Historia del ELISA")
(def elisa-historia-subtitle "De los isótopos radiactivos a la técnica analítica más utilizada en el mundo.")

(def elisa-historia
  [{:step "1960" :title "Radioinmunoensayo (RIA)"
    :text "La técnica dominante usaba isótopos radiactivos para detectar hormonas y proteínas. Desarrollada por Rosalyn Yalow y Solomon Berson. Costosa, compleja y con riesgos de seguridad."}
   {:step "1971" :title "Nacimiento del ELISA"
    :text "Peter Perlmann y Eva Engvall reemplazan el marcador radiactivo por una enzima. La enzima produce un cambio de color medible con luz. Más seguro, más barato y automatizable. Publicado en Immunochemistry."}
   {:step "1980" :title "Expansión en laboratorios"
    :text "Aparecen los primeros lectores ELISA automáticos (microplate readers). Permiten analizar 96 muestras simultáneamente. Aplicaciones clave: VIH, hepatitis, control de alimentos."}
   {:step "HOY" :title "ELISA moderno"
    :text "Uno de los métodos más usados en medicina, industria alimentaria e investigación. Detecta micotoxinas, alérgenos, hormonas, anticuerpos, biomarcadores y residuos veterinarios."}])

(def elisa-historia-timeline
  [{:year "1959" :title "Radioinmunoensayo (RIA)"
    :text "Rosalyn Yalow y Solomon Berson desarrollan el RIA usando isótopos radiactivos. Revoluciona la detección de hormonas, pero requiere material radiactivo costoso y peligroso."}
   {:year "1966" :title "Primeros anticuerpos marcados"
    :text "Wide y Porath exploran la unión de antígenos a superficies sólidas, sentando las bases para los inmunoensayos en fase sólida sin radiactividad."}
   {:year "1971" :title "Invención del ELISA"
    :text "Eva Engvall y Peter Perlmann publican el primer ELISA en Immunochemistry. La innovación clave: reemplazar el marcador radiactivo por una enzima (fosfatasa alcalina) que produce color medible."}
   {:year "1975" :title "Voller adapta ELISA a microplacas"
    :text "Alister Voller adapta el ELISA al formato de microplaca de 96 pozos, permitiendo el análisis masivo de muestras. Se aplica por primera vez al diagnóstico de malaria y parasitosis."}
   {:year "1985" :title "ELISA para VIH"
    :text "La FDA aprueba el primer test ELISA para detección de anticuerpos anti-VIH en bancos de sangre. ELISA se convierte en herramienta crítica de salud pública mundial."}
   {:year "1990" :title "Automatización y microplate readers"
    :text "Aparecen los primeros lectores ELISA automáticos. Laboratorios analizan cientos de muestras diarias. Se expande a seguridad alimentaria: micotoxinas, alérgenos, residuos veterinarios."}
   {:year "2000" :title "ELISA en seguridad alimentaria"
    :text "Romer Labs y otros fabricantes desarrollan kits ELISA específicos para la industria alimentaria. Detección de aflatoxinas, DON, fumonisinas, gluten y alérgenos se estandariza."}
   {:year "HOY" :title "Estándar global"
    :text "ELISA es el método de referencia en más de 150 países. Base de tecnologías derivadas: CLIA (quimioluminiscencia), tests de flujo lateral (AgraStrip, COVID), y arrays multiplex."}])

(def elisa-tecnologias-derivadas
  [{:icon "test-tubes" :label "CLIA" :value "Quimioluminiscencia"}
   {:icon "scan-line" :label "Flujo lateral" :value "AgraStrip / COVID"}
   {:icon "microscope" :label "Multiplex" :value "Arrays de anticuerpos"}])

(def elisa-reader-specs
  [{:title "Equipo" :text "BioTek 800 TS Absorbance Reader — lector de microplacas de 96 pozos"}
   {:title "Medición" :text "Absorbancia óptica a 450 nm (longitud de onda estándar ELISA)"}
   {:title "Capacidad" :text "Microplaca de 96 pozos — hasta 40 muestras + estándares y controles por placa"}
   {:title "Software" :text "Compatible con software de cálculo de curva de calibración y exportación de datos LIMS"}
   {:title "Alimentación" :text "100–240 V AC, 50/60 Hz. Interfaz USB para conexión a PC"}
   {:title "Certificaciones" :text "CE, cGMP. Validado para uso con kits AgraQuant® de Romer Labs"}])

;; backward compat alias
(def elisa-explanation elisa-principio)

;; ── Analytes ────────────────────────────────────────────────────
(def micotoxinas
  [{:title "Aflatoxinas"    :text "Producidas por Aspergillus. Altamente tóxicas y carcinogénicas. Comunes en maní, maíz y frutos secos."}
   {:title "DON"            :text "Deoxinivalenol — micotoxina de Fusarium. Frecuente en trigo, cebada y maíz."}
   {:title "Fumonisinas"    :text "Producidas por Fusarium verticillioides. Asociadas al maíz y productos derivados."}
   {:title "Zearalenona"    :text "Micotoxina estrogénica de Fusarium. Afecta granos almacenados en condiciones húmedas."}
   {:title "Ocratoxina A"   :text "Producida por Aspergillus y Penicillium. Presente en café, vino, cereales y especias."}
   {:title "T-2 / HT-2"    :text "Tricotecenos de Fusarium. Altamente tóxicos incluso en bajas concentraciones."}])

(def alergenos
  [{:title "Gluten"   :text "Proteína presente en trigo, cebada y centeno. Peligroso para celíacos."}
   {:title "Maní"     :text "Uno de los alérgenos más potentes. Puede causar anafilaxia en trazas mínimas."}
   {:title "Soya"     :text "Proteína de soya presente en gran variedad de alimentos procesados."}
   {:title "Sésamo"   :text "Alérgeno emergente, cada vez más regulado a nivel mundial."}
   {:title "Leche"    :text "Caseína y proteínas del suero. Presente en muchos productos inesperados."}
   {:title "Huevo"    :text "Ovoalbúmina y otras proteínas. Común en productos de panadería y salsas."}])

;; ── AgraVision ──────────────────────────────────────────────────
(def agravision-nombre-desglose
  [{:title "Agra" :icon "wheat"
    :text "Agricultura / agroindustria — diseñado específicamente para el sector agrícola y alimentario."}
   {:title "Vision" :icon "eye"
    :text "Sistema óptico que \"ve\" y mide el resultado — sensor digital que reemplaza la interpretación visual subjetiva."}])

(def agravision-diferencia
  [{:title "ELISA"
    :text "Absorbancia de color en microplaca de 96 pozos. Laboratorio central. Resultados en horas."}
   {:title "AgraVision"
    :text "Intensidad de línea en tira de flujo lateral. Prueba rápida en campo. Resultados en minutos."}])

(def agravision-historia
  [{:step "1990" :title "Tiras rápidas visuales"
    :text "Aparecen las tiras inmunocromatográficas (similar a test de embarazo). Lectura visual subjetiva — dependía del ojo del operador, generando variabilidad en resultados."}
   {:step "2005" :title "Nace AgraVision"
    :text "Romer Labs desarrolla el sistema AgraVision: visión digital + software para medir la intensidad de las líneas. Convierte pruebas rápidas visuales en cuantitativas."}
   {:step "2015" :title "AgraVision Pro"
    :text "Evolución con pantalla táctil, incubador integrado, 4 puertos simultáneos y conectividad USB para trazabilidad digital con Romer Labs Data Manager."}])

(def agravision-historia-timeline
  [{:year "1990" :title "Nacen los tests rápidos"
    :text "Se desarrollan los Lateral Flow Immunoassays (LFIA). Funcionan como tests de embarazo: aparecen líneas de color. Aplicación inicial en diagnóstico médico, luego en análisis de granos y micotoxinas. Problema: lectura visual subjetiva."}
   {:year "2000" :title "Industrialización agrícola"
    :text "Empresas como Romer Labs, Neogen y Charm Sciences producen tiras rápidas para aflatoxinas, fumonisinas y DON. Resultados en 5–10 min, portátiles y baratas. Aún dependían del ojo humano."}
   {:year "2005" :title "Nacen los lectores digitales"
    :text "Para eliminar la subjetividad aparecen los strip readers: escanean la tira, miden intensidad de línea y calculan concentración. Surgen AgraVision, Reveal AccuScan y Charm EZ-M Reader."}
   {:year "2010" :title "Desarrollo del AgraVision"
    :text "Romer Labs desarrolla AgraVision: lectura óptica automática de tiras AgraStrip con curvas de calibración y almacenamiento digital. Detecta aflatoxina, fumonisina, DON, zearalenona y ocratoxina."}
   {:year "2015" :title "AgraVision Pro"
    :text "Se introduce AgraVision Pro con mayor precisión, pantalla táctil, incubador integrado e integración con AgraStrip Pro. Uso en plantas, puertos, silos y laboratorios de control de calidad."}
   {:year "HOY" :title "Diagnóstico en campo"
    :text "Los lectores como AgraVision forman parte del diagnóstico descentralizado: análisis directamente donde está el grano. Resultados en 5–10 minutos, menos logística y decisiones rápidas en compra."}])

(def agravision-como-funciona
  [{:step "01" :title "Preparar muestra" :text "Extraer y diluir la muestra según el protocolo del analito específico."}
   {:step "02" :title "Colocar en tira" :text "Aplicar la muestra en la tira AgraStrip — el reactivo fluye y genera líneas coloreadas."}
   {:step "03" :title "Insertar en lector" :text "Colocar el cartucho con la tira en uno de los 4 puertos del AgraVision Pro."}
   {:step "04" :title "Lectura óptica" :text "El sensor óptico mide la intensidad de la línea del test usando análisis de imagen y curva de calibración."}
   {:step "05" :title "Resultado" :text "Concentración del analito en ppm o ppb — resultado cuantitativo, trazable y exportable."}])

(def agravision-micotoxinas-detectadas
  [{:icon "wheat" :label "Aflatoxinas" :value "maní, maíz"}
   {:icon "wheat" :label "Fumonisinas" :value "maíz, derivados"}
   {:icon "wheat" :label "DON" :value "trigo, cebada"}])

(def agravision-features
  [{:title "Lectura automática de tiras AgraStrip Pro"
    :text "Elimina la interpretación visual subjetiva, proporcionando resultados cuantitativos precisos en pocos minutos."}
   {:title "Pantalla táctil de 7 pulgadas"
    :text "Interfaz intuitiva que guía al usuario a través de cada paso del análisis."}
   {:title "Incubador integrado"
    :text "Control de temperatura automático durante el ensayo para resultados consistentes."}
   {:title "Hasta 4 análisis simultáneos"
    :text "Cada muestra se analiza de forma independiente, aumentando la productividad del laboratorio."}
   {:title "Exportación de datos"
    :text "Conexión USB tipo A y B. Compatible con Romer Labs Data Manager para trazabilidad y reportes LIMS."}])

(def agravision-specs
  [{:title "Tipo de equipo"     :text "Lector de tiras de flujo lateral (LFD reader)"}
   {:title "Modelo"             :text "AgraVision Pro Reader — Código: RLAB-0024"}
   {:title "Compatibilidad"     :text "Tiras AgraStrip Pro para micotoxinas, GMO y alérgenos"}
   {:title "Alimentación"       :text "100–240 V AC, 50/60 Hz"}
   {:title "Temperatura"        :text "Almacenamiento: 15°C – 25°C"}
   {:title "Certificaciones"    :text "CE — Gestión de datos compatible con Romer Labs Data Manager"}])

(def agravision-applications
  ["Detección de micotoxinas en granos y alimentos"
   "Identificación de organismos genéticamente modificados (GMO)"
   "Análisis de alérgenos alimentarios"
   "Control de calidad en industria alimentaria y agrícola"
   "Análisis rápido en campo o laboratorio"])

(def agravision-standards
  ["HACCP"
   "ISO 22000"
   "FSSC 22000"
   "Auditorías GFSI"])

(def agravision-procedure
  [{:step "01" :title "Preparar muestra" :text "Extraer y diluir la muestra según el protocolo del analito."}
   {:step "02" :title "Colocar tira" :text "Insertar una tira AgraStrip Pro en el cartucho del lector."}
   {:step "03" :title "Introducir cartucho" :text "Colocar el cartucho en uno de los 4 puertos del AgraVision Pro."}
   {:step "04" :title "Incubación automática" :text "El equipo controla la temperatura y el tiempo de incubación."}
   {:step "05" :title "Lectura y resultado" :text "El sistema mide la intensidad de la línea del test y calcula la concentración del analito."}])

;; ── Analysis flow ───────────────────────────────────────────────
(def flujo-steps
  [{:step "01" :title "Muestreo" :text "Tomar una muestra representativa del lote de producto o materia prima."}
   {:step "02" :title "Preparación" :text "Moler, homogeneizar o disolver la muestra según el protocolo del analito."}
   {:step "03" :title "Extracción" :text "Aplicar solución de extracción para liberar el analito de la matriz alimentaria."}
   {:step "04" :title "Screening rápido" :text "Usar AgraStrip® para un resultado rápido de presencia/ausencia."}
   {:step "05" :title "Cuantificación" :text "Si el screening es positivo, confirmar con AgraQuant® ELISA para valor exacto."}
   {:step "06" :title "Lectura digital" :text "Registrar resultado con AgraVision® para trazabilidad y documentación."}])

(def flujo-decision
  [{:title "Resultado negativo"
    :text "El lote cumple con los límites establecidos. Liberar para uso o comercialización."}
   {:title "Resultado positivo"
    :text "El lote excede los límites permitidos. Retener, confirmar con análisis cuantitativo y aplicar acción correctiva."}
   {:title "Resultado dudoso"
    :text "Repetir el análisis con nueva muestra. Si persiste, escalar a análisis confirmatorio de laboratorio."}])

;; ── Closing / ABT ─────────────────────────────────────────────
(def cierre-contacto
  [{:title "Sitio web" :icon "globe" :text "www.agrobiotek.com"}
   {:title "Soporte técnico" :icon "headphones" :text "Asesoría especializada en diagnóstico alimentario, capacitación y servicio post-venta."}
   {:title "Distribución" :icon "truck" :text "Cobertura regional con inventario local. Entrega rápida de kits, reactivos y equipos Romer Labs."}
   {:title "Capacitación" :icon "graduation-cap" :text "Programas de formación en técnicas ELISA, uso de AgraStrip y operación de AgraVision Pro."}])

;; ── ¿Por qué ABT Internacional? ──────────────────────────────
(def abt-porque-trabajar
  [{:title "Representante oficial" :icon "shield-check"
    :text "Distribuidor autorizado de Romer Labs en Centroamérica y el Caribe. Productos originales con garantía de fábrica."}
   {:title "Soporte técnico local" :icon "headphones"
    :text "Equipo de especialistas en diagnóstico alimentario. Asistencia en español, en tu zona horaria, con tiempos de respuesta rápidos."}
   {:title "Inventario regional" :icon "package"
    :text "Inventario de kits, reactivos y equipos en la región. Entrega rápida sin depender de importaciones individuales."}
   {:title "Capacitación práctica" :icon "graduation-cap"
    :text "Programas de formación presencial y virtual en técnicas ELISA, uso de AgraStrip® y operación de AgraVision™ Pro."}
   {:title "Servicio post-venta" :icon "wrench"
    :text "Calibración, mantenimiento preventivo y soporte continuo para tus equipos de laboratorio."}
   {:title "Experiencia comprobada" :icon "award"
    :text "Más de 30 años de experiencia en la industria alimentaria. 500+ marcas confían en nosotros."}])

(def abt-stats
  [{:icon "globe" :label "Países" :value "5 oficinas"}
   {:icon "calendar" :label "Años" :value "30+"}
   {:icon "tag" :label "Marcas" :value "500+"}
   {:icon "users" :label "Clientes" :value "2K+"}])

(def abt-servicios-grid
  [{:img "abt-equipo-planta.png" :kicker "Equipo en planta" :title "Auditorías de sanidad e inocuidad"}
   {:img "abt-capacitacion.png" :kicker "Capacitación in situ" :title "Formación técnica en planta"}
   {:img "abt-calibracion.png" :kicker "Laboratorio" :title "Calibración y mantenimiento"}
   {:img "abt-auditora.png" :kicker "Líneas de producción" :title "Control de calidad en planta"}])

(def abt-contacto-regional
  [{:title "Honduras — Tegucigalpa" :icon "building-2"
    :text "Edif. Santa Bárbara 726, 3 Ave, 7-8 Calles · PBX: +(504) 2238-0872 · honduras@agrobiotek.com"}
   {:title "Honduras — San Pedro Sula" :icon "building-2"
    :text "Edif. San Remo, 9 Ave S.O. 4-5 Calle, Barrio El Benque · PBX: +(504) 2552-7116 · sanpedrosula@agrobiotek.com"}
   {:title "República Dominicana" :icon "phone"
    :text "Calle Santiago No. 608 Altos, Gazcue, Santo Domingo · Tel: +809-221-5751 · WhatsApp: +1 (809) 972-4364 · dominicana@agrobiotek.com"}
   {:title "El Salvador" :icon "mail"
    :text "Col. Miramonte Poniente, Av. A No. 245, San Salvador · Tel: +(503) 2260-7669 / 7670 · elsalvador@agrobiotek.com"}
   {:title "Guatemala" :icon "map-pin"
    :text "1 Calle 2-29, Zona 10, Ciudad de Guatemala · Tel: +(502) 2334-6946 · guatemala@agrobiotek.com"}
   {:title "Nicaragua" :icon "map-pin"
    :text "Reparto El Carmen, Managua · Tel: +(505) 266-6186 / 268-7747 · nicaragua@agrobiotek.com"}])
