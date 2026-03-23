(ns propuesta-web.content
  "Contenido de la propuesta comercial para sitio web Squarespace.")

;; ── Index ───────────────────────────────────────────────────────
(def index-entries
  [{:id "portada"        :label "Portada"                     :page 1}
   {:id "contenido"      :label "Contenido"                   :page 2}
   {:id "resumen"        :label "Resumen Ejecutivo"           :page 3}
   {:id "alcance"        :label "Alcance del Proyecto"        :page 4}
   {:id "squarespace"    :label "Plataforma Squarespace"      :page 5}
   {:id "plan-core"      :label "Plan Squarespace Core"       :page 6}
   {:id "proceso"        :label "Proceso de Trabajo"          :page 7}
   {:id "entregables"    :label "Entregables"                 :page 8}
   {:id "cronograma"     :label "Cronograma"                  :page 9}
   {:id "inversion"      :label "Cotización"                   :page 10}
   {:id "condiciones"    :label "Condiciones"                 :page 11}
   {:id "transferencia"  :label "Información de Transferencia" :page 12}
   {:id "creditos"       :label "Contacto"                    :page 13}])

;; ── TOC ─────────────────────────────────────────────────────────
(def contenido-title "Propuesta — Sitio Web via Plantilla")
(def contenido-subtitle "Índice")

(def contenido-sections
  [{:id "resumen" :title "Propuesta"
    :items [{:label "Resumen Ejecutivo"      :ok true}
            {:label "Alcance del Proyecto"   :ok true}
            {:label "Plataforma"             :ok true}
            {:label "Plan Squarespace"       :ok true}]}
   {:id "proceso" :title "Ejecución"
    :items [{:label "Proceso de Trabajo"     :ok true}
            {:label "Entregables"            :ok true}
            {:label "Cronograma"             :ok true}]}
   {:id "inversion" :title "Inversión"
    :items [{:label "Desglose de costos"     :ok true}
            {:label "Condiciones comerciales":ok true}
            {:label "Información de transferencia" :ok true}]}])

;; ── Resumen Ejecutivo ───────────────────────────────────────────
(def resumen-title "Resumen Ejecutivo")
(def resumen-subtitle "Su presencia digital profesional, lista en 3 semanas")

(def resumen-text
  "GREB le propone el diseño y desarrollo de un **sitio web profesional** utilizando **Squarespace** como plataforma. Squarespace es líder mundial en creación de sitios web, con infraestructura robusta, certificado SSL incluido, hosting ilimitado y plantillas de diseño premiadas. Su nuevo sitio web estará optimizado para dispositivos móviles, preparado para SEO y listo para crecer con su negocio.")

(def resumen-items
  [{:title "Diseño Profesional"     :icon "palette"
    :text "Diseño personalizado sobre plantillas Squarespace premium. Identidad visual coherente con su marca: colores, tipografía, logotipo e imágenes optimizadas para web."}
   {:title "100% Administrable"     :icon "settings"
    :text "Usted podrá actualizar contenido, agregar páginas, publicar blog posts y gestionar productos sin necesidad de un desarrollador. Squarespace incluye un editor visual intuitivo."}
   {:title "Optimizado para Móviles" :icon "smartphone"
    :text "Diseño **responsive** que se adapta automáticamente a celulares, tablets y computadoras. Google prioriza sitios mobile-friendly en sus resultados de búsqueda."}
   {:title "SEO y Analytics"        :icon "bar-chart"
    :text "Herramientas SEO integradas: meta tags, URLs limpias, sitemap XML automático. Google Analytics y métricas de Squarespace incluidas para medir el rendimiento de su sitio."}])

;; ── Alcance ─────────────────────────────────────────────────────
(def alcance-title "Alcance del Proyecto")
(def alcance-subtitle "Qué incluye esta propuesta")

(def alcance-incluido
  [{:title "Páginas Incluidas"         :icon "file-text"
    :text "Hasta **7 páginas** personalizadas: **Inicio**, **Nosotros**, **Servicios** (o Productos), **Galería**, **Blog**, **Contacto** y una página adicional a definir."}
   {:title "Diseño Visual"             :icon "image"
    :text "Selección y personalización de plantilla premium. Paleta de colores acorde a su marca. Tipografía profesional. Optimización de imágenes proporcionadas por el cliente."}
   {:title "Funcionalidades"           :icon "zap"
    :text "Formulario de contacto, integración con redes sociales (Instagram, Facebook, WhatsApp), mapa de Google Maps, galería de imágenes y botones de llamada a la acción (CTA)."}
   {:title "Configuración Técnica"     :icon "wrench"
    :text "Conexión de dominio personalizado (el cliente provee el dominio). Certificado SSL gratuito. Configuración de correo electrónico. Favicon y metadatos. Google Search Console."}])

(def alcance-text
  "Esta propuesta cubre el diseño, desarrollo y puesta en línea de un sitio web completo. El cliente proporciona: **logotipo** en alta resolución, **textos** de cada sección, **fotografías** del negocio (o se utilizan imágenes de stock), y acceso al **dominio** si ya lo tiene registrado. GREB se encarga de todo lo demás.")

(def alcance-no-incluido
  [{:title "No Incluido"          :icon "x-circle"
    :text "Registro de dominio (costo aparte ~$12/año), suscripción mensual de Squarespace (la paga el cliente directamente), fotografía profesional, redacción de contenido, tienda online con más de 10 productos, funcionalidades de membresía o cursos online."}
   {:title "Servicios Adicionales" :icon "plus-circle"
    :text "Disponibles con cotización separada: tienda e-commerce completa, blog con calendario editorial, integración con sistemas de reservas, landing pages para campañas, mantenimiento mensual."}])

;; ── Squarespace ─────────────────────────────────────────────────
(def squarespace-title "¿Por qué Squarespace?")
(def squarespace-subtitle "La plataforma elegida por millones de negocios")

(def squarespace-text
  "Squarespace es una plataforma **todo-en-uno** que combina hosting, diseño, dominio y herramientas de marketing. A diferencia de WordPress, **no requiere mantenimiento técnico**: no hay plugins que actualizar, no hay riesgos de seguridad por software desactualizado, y las copias de seguridad son automáticas. Su sitio web estará siempre en línea, seguro y actualizado.")

(def squarespace-items
  [{:title "Hosting Ilimitado"        :icon "server"
    :text "Ancho de banda y almacenamiento ilimitados. CDN global para carga rápida desde cualquier país. **99.98% uptime** garantizado. Sin límites de tráfico."}
   {:title "SSL Gratuito"             :icon "lock"
    :text "Certificado SSL incluido sin costo adicional. Su sitio siempre se muestra como **https://** — seguro para visitantes y mejor ranking en Google."}
   {:title "Plantillas Premium"       :icon "layout"
    :text "Más de 100 plantillas diseñadas por expertos. Todas son responsive. Personalizables sin código. Actualizaciones de diseño automáticas sin romper su contenido."}
   {:title "Soporte 24/7"             :icon "headphones"
    :text "Soporte técnico de Squarespace por email y chat en vivo, 24 horas, 7 días. Documentación extensa y video tutoriales para aprender a gestionar su sitio."}])

;; ── Plan Core ───────────────────────────────────────────────────
(def plan-core-title "Plan Squarespace Core")
(def plan-core-subtitle "El plan recomendado para su proyecto")

(def plan-core-text
  "Recomendamos el plan **Squarespace Core** (antes llamado 'Business'). Este plan incluye todo lo necesario para un sitio web profesional con presencia comercial. El costo es pagado **directamente por el cliente** a Squarespace — GREB no cobra comisión sobre la suscripción.")

(def plan-core-precio
  [{:title "Precio Mensual"          :icon "credit-card"
    :text "**$33 USD/mes** facturado mensualmente, o **$27 USD/mes** facturado anualmente ($324/año). El plan anual representa un ahorro del 18%."}
   {:title "Dominio Gratis (Anual)"  :icon "globe"
    :text "El plan anual incluye un **dominio personalizado gratis** durante el primer año (ej: www.sunegocio.com). Después se renueva a ~$20/año."}])

(def plan-core-incluye
  [{:title "Páginas Ilimitadas"   :icon "files"
    :text "Sin límite en la cantidad de páginas. Cree tantas secciones como necesite: servicios, productos, galería, equipo, testimonios, etc."}
   {:title "Analíticas Avanzadas" :icon "bar-chart-2"
    :text "Panel de estadísticas con visitas, páginas más vistas, fuentes de tráfico, palabras clave de búsqueda y datos geográficos de sus visitantes."}
   {:title "E-commerce Básico"    :icon "shopping-bag"
    :text "Venda productos o servicios directamente. Acepta tarjetas de crédito vía **Stripe** y **PayPal**. Squarespace cobra **0% comisión** de transacción en el plan Core."}
   {:title "Integraciones"        :icon "plug"
    :text "Google Workspace, Mailchimp, Zapier, redes sociales, Google Analytics 4, Pinterest, y cientos de extensiones disponibles en el marketplace de Squarespace."}])

;; ── Proceso ─────────────────────────────────────────────────────
(def proceso-title "Proceso de Trabajo")
(def proceso-subtitle "4 fases, 3 semanas, comunicación constante")

(def proceso-text
  "Trabajamos con un proceso estructurado para garantizar que su sitio web refleje exactamente la visión de su negocio. Cada fase tiene entregables claros y puntos de revisión donde usted aprueba antes de avanzar.")

(def proceso-items
  [{:title "1. Briefing y Estrategia"   :icon "clipboard"
    :text "**Semana 1 (Días 1-3)** — Reunión inicial para definir objetivos, público meta, estructura del sitio y preferencias de diseño. Usted nos envía: logo, textos, fotos y referencias de sitios que le gustan. Entregable: **mapa del sitio aprobado**."}
   {:title "2. Diseño Visual"           :icon "figma"
    :text "**Semana 1-2 (Días 3-8)** — Selección de plantilla, personalización de colores/tipografía, diseño de la página de inicio y una página interior como muestra. Entregable: **mockup visual aprobado** (2 rondas de revisión incluidas)."}
   {:title "3. Desarrollo"              :icon "code"
    :text "**Semana 2-3 (Días 8-16)** — Construcción de todas las páginas en Squarespace. Carga de contenido, optimización de imágenes, configuración de formularios, SEO, redes sociales y analytics. Entregable: **sitio completo en modo borrador**."}
   {:title "4. Lanzamiento"             :icon "rocket"
    :text "**Semana 3 (Días 16-21)** — Revisión final con el cliente. Ajustes de última hora (1 ronda). Conexión del dominio. Capacitación de 1 hora para que aprenda a gestionar su sitio. Entregable: **sitio en producción + manual de uso**."}])

;; ── Entregables ─────────────────────────────────────────────────
(def entregables-title "Entregables")
(def entregables-subtitle "Qué recibe al finalizar el proyecto")

(def entregables-items
  [{:title "Sitio Web Completo"      :icon "globe"
    :text "Hasta 7 páginas diseñadas y publicadas en su dominio. Diseño responsive, SEO configurado, formulario de contacto funcional, redes sociales conectadas."}
   {:title "Capacitación"            :icon "video"
    :text "Sesión de capacitación de **1 hora** (videollamada grabada) donde le enseñamos a: editar textos, agregar imágenes, crear páginas nuevas, publicar blog posts y revisar estadísticas."}
   {:title "Manual de Uso"           :icon "book-open"
    :text "Documento PDF con instrucciones paso a paso para las tareas más comunes: cambiar fotos, editar menú, agregar productos, ver estadísticas. Con capturas de pantalla."}
   {:title "Archivos de Diseño"      :icon "folder"
    :text "Entrega de todos los archivos utilizados: imágenes optimizadas, paleta de colores, tipografías seleccionadas. Todo organizado en una carpeta de Google Drive compartida."}])

;; ── Cronograma ──────────────────────────────────────────────────
(def cronograma-title "Cronograma")
(def cronograma-subtitle "21 días hábiles desde la aprobación")

(def cronograma-items
  [{:title "Semana 1"   :icon "calendar"
    :text "**Días 1-5** — Briefing, estrategia, mapa del sitio. Selección de plantilla y primer borrador de diseño visual. Primera revisión con el cliente."}
   {:title "Semana 2"   :icon "calendar"
    :text "**Días 6-12** — Ajustes de diseño aprobado. Desarrollo de todas las páginas. Carga de contenido y configuración técnica (SEO, formularios, analytics)."}
   {:title "Semana 3"   :icon "calendar"
    :text "**Días 13-21** — Revisión final del cliente. Ajustes de última hora. Conexión del dominio. Capacitación. **Lanzamiento**."}])

(def cronograma-text
  "El cronograma asume que el cliente entrega el material (textos, fotos, logo) en los primeros 3 días. Los tiempos de revisión del cliente (aprobaciones) no se cuentan como días de trabajo. Si el material se entrega tarde, el cronograma se extiende proporcionalmente.")

;; ── Inversión ───────────────────────────────────────────────────
(def inversion-title "Inversión")
(def inversion-subtitle "Desglose transparente de costos")

(def inversion-items
  [{:title "Diseño y Desarrollo Web"     :icon "code"
    :text "**USD $1,200** — Pago único. Incluye: diseño personalizado, desarrollo de hasta 7 páginas, configuración técnica, SEO, capacitación y manual de uso. Este es el único pago a GREB."}
   {:title "Suscripción Squarespace Core" :icon "credit-card"
    :text "**USD $27/mes** (plan anual) o **$33/mes** (mensual). Pagado por el cliente directamente a Squarespace. Incluye hosting, SSL, dominio gratis el primer año, soporte 24/7 y actualizaciones."}
   {:title "Dominio (si no lo tiene)"     :icon "globe"
    :text "**~USD $12-20/año** — Registro de dominio .com, .net o .org. Puede registrarse directamente en Squarespace (incluido gratis el primer año con plan anual) o en Namecheap/GoDaddy."}])

(def inversion-text
  "**Inversión total primer año:** USD $1,200 (diseño GREB) + $324 (Squarespace anual) + $0 (dominio incluido) = **USD $1,524**. A partir del segundo año solo paga la suscripción de Squarespace ($324/año) y renovación de dominio (~$20/año).")

(def inversion-formas-pago
  [{:title "Forma de Pago"    :icon "wallet"
    :text "**50% al aprobar** la propuesta (USD $600) + **50% al lanzar** el sitio (USD $600). Transferencia bancaria, Zelle o PayPal. La suscripción de Squarespace se paga con tarjeta de crédito del cliente."}
   {:title "Garantía"         :icon "shield-check"
    :text "**30 días de soporte gratuito** después del lanzamiento para resolver dudas y hacer ajustes menores. Después del período de garantía, soporte disponible a $50/hora."}])

;; ── Condiciones ─────────────────────────────────────────────────
(def condiciones-title "Condiciones Comerciales")
(def condiciones-subtitle "Términos y validez de la propuesta")

(def condiciones-items
  [{:title "Validez"         :icon "clock"
    :text "Esta propuesta es válida por **30 días** a partir de la fecha de emisión. Los precios están sujetos a cambio después de este período."}
   {:title "Revisiones"      :icon "repeat"
    :text "Se incluyen **2 rondas de revisión** en la fase de diseño y **1 ronda** antes del lanzamiento. Revisiones adicionales se cotizan a $50/hora."}
   {:title "Propiedad"       :icon "key"
    :text "La cuenta de Squarespace es **propiedad del cliente**. GREB no retiene acceso ni control después del lanzamiento. Todo el contenido, diseño y configuración pertenecen al cliente."}
   {:title "Cancelación"     :icon "x-circle"
    :text "Si el proyecto se cancela antes de iniciar la fase de desarrollo, se reembolsa el 100% del anticipo. Durante desarrollo, se retiene el 50% proporcional al trabajo realizado."}])

;; ── Créditos ────────────────────────────────────────────────────
(def credits-title nil)
(def credits-by "Diseño y Desarrollo Web")
(def credits-orgs
  [{:name "www.greb.app" :role "Sistemas para personas inteligentes"}])
(def credits-legal
  "© 2026 GREB · www.greb.app · Sistemas para personas inteligentes. Esta propuesta es confidencial y está dirigida exclusivamente al destinatario. Precios en USD. Squarespace® es marca registrada de Squarespace, Inc.")

;; ── Transferencia Bancaria ────────────────────────────────────
(def transferencia-title "Información de Transferencia")
(def transferencia-subtitle "Datos bancarios para el pago por transferencia")

(def transferencia-banco
  [{:title "Banco"              :icon "landmark"
    :text "**Banco Múltiple BHD, S. A.** — Entidad de intermediación financiera, RNC: 101136792."}
   {:title "Titular"            :icon "building"
    :text "**AGROBIOTEK LABORATORIOS, SRL** — RNC: 101867541"}
   {:title "No. Producto"       :icon "hash"
    :text "**11875990019**"}
   {:title "Cuenta Regional"    :icon "credit-card"
    :text "**DO21BCBH0000000011875990019**"}
   {:title "Tipo de Producto"   :icon "wallet"
    :text "Cuentas Corrientes"}
   {:title "Moneda"             :icon "coins"
    :text "**DOP** (Pesos Dominicanos)"}])

(def transferencia-instrucciones
  [{:title "Monto"          :icon "receipt"
    :text "50% anticipo (**USD $600**) al aprobar la propuesta. 50% restante (**USD $600**) al entregar el sitio en producción. Use la tasa de cambio del día de la transferencia."}
   {:title "Referencia"     :icon "file-text"
    :text "Incluir en el concepto: **\"GREB — Sitio Web — LETS DRUNCH RD\"** y el número de cotización **2026-001**."}
   {:title "Confirmación"   :icon "check-circle"
    :text "Enviar comprobante de transferencia a **info@greb.dev** para confirmar la recepción y activar el inicio del proyecto."}
   {:title "Alternativas"   :icon "arrow-right-left"
    :text "También aceptamos **Zelle** y **PayPal**. Para pagos internacionales en USD consulte los datos de la cuenta corresponsal."}])
