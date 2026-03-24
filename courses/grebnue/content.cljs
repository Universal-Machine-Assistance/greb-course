(ns grebnue.content
  "Content data for GREB: Sistemas para personas inteligentes.")

;; ── Index entries (TOC references) ──────────────────────────

(def index-entries
  [{:id "intro"     :label "¿Qué es Greb?"                          :page 9}
   {:id "norman"    :label "Puertas Norman"                          :page 14}
   {:id "move"      :label "Estudios de Movimiento"                  :page 16}
   {:id "flow"      :label "¿Qué es Flow?"                          :page 20}
   {:id "flowing"   :label "¿Cómo entrar en Flow?"                  :page 26}
   {:id "pain"      :label "Reducir los Pain Points"                 :page 34}
   {:id "diagram"   :label "Diagramar Procesos y Cuellos de Botella" :page 38}
   {:id "belt"      :label "Cinturones"                              :page 43}
   {:id "blue"      :label "Ideas Azul"                              :page 50}
   {:id "pick"      :label "Cómo elegir la opción más Greb"         :page 56}
   {:id "think"     :label "¿Cómo elegir ideas Greb?"               :page 60}
   {:id "okr"       :label "OKR (Objective Key Results)"             :page 68}
   {:id "5s"        :label "Método 5s"                               :page 72}
   {:id "versions"  :label "Versiones"                               :page 76}
   {:id "story"     :label "El Mundo está lleno de gente Greb"      :page 80}
   {:id "people"    :label "Manejo de Personas"                      :page 87}
   {:id "decide"    :label "Tipos de Decisiones"                     :page 90}
   {:id "lead"      :label "Hablando del Futuro con Directivos"     :page 96}
   {:id "outro"     :label "Final"                                   :page 104}])

;; ── TOC sections ────────────────────────────────────────────

(def toc-sections
  [{:number 1
    :title "¿Qué es Greb?"
    :entries [{:tag "/intro"   :label "¿Qué es Greb?"                          :page 9   :color :blue}
              {:tag "/norman"  :label "Puertas Norman"                          :page 14  :color :blue}
              {:tag "/move"    :label "Estudios de Movimiento"                  :page 16  :color :blue}
              {:tag "/flow"    :label "¿Qué es Flow?"                          :page 20  :color :blue}
              {:tag "/flowing" :label "¿Cómo entrar en Flow?"                  :page 26  :color :blue}
              {:tag "/pain"    :label "Reducir los Pain Points"                 :page 34  :color :blue}]}
   {:number 2
    :title "Herramientas de Greb"
    :entries [{:tag "/diagram"   :label "Diagramar Procesos y Cuellos de Botella" :page 38  :color :pink}
              {:tag "/belt"      :label "Cinturones"                              :page 43  :color :pink}
              {:tag "/blue"      :label "Ideas Azul"                              :page 50  :color :pink}
              {:tag "/pick"      :label "Cómo elegir la opción más Greb"         :page 56  :color :pink}
              {:tag "/think"     :label "¿Cómo elegir ideas Greb?"               :page 60  :color :pink}
              {:tag "/okr"       :label "OKR (Objective Key Results)"             :page 68  :color :pink}
              {:tag "/5s"        :label "Método 5s"                               :page 72  :color :green}
              {:tag "/versions"  :label "Versiones"                               :page 76  :color :green}]}
   {:number 3
    :title "Ejemplos de Greb"
    :entries [{:tag "/story"   :label "El Mundo está lleno de gente Greb"      :page 80  :color :green}
              {:tag "/people"  :label "Manejo de Personas"                      :page 87  :color :green}
              {:tag "/decide"  :label "Tipos de Decisiones"                     :page 90  :color :green}
              {:tag "/lead"    :label "Hablando del Futuro con Directivos"     :page 96  :color :green}
              {:tag "/outro"   :label "Final"                                   :page 104 :color :green}]}])

;; ── Cover data ──────────────────────────────────────────────

(def cover-data
  {:logo "greb-logo.png"
   :isbn "978-9945-80-029-6 001.000"
   :copyright "Greb: Sistemas para personas inteligentes\n© Guillermo Molina Mueses 2025"
   :credits ["Este libro está licenciado bajo una Creative Commons Atribución-CompartirIgual 4.0 Internacional (CC BY-SA 4.0)."
             "y de manera completamente gratuita en www.greb.app"
             "Dirección y Diseño: Guillermo Molina Mueses"
             "Jefe de Diagramación: Giuseppe Di Vanna – OneArt Book Design"
             "Jefe de Edición: Ricardo Domínguez"
             "Diagramador: Bryan Mogena"
             "Equipo de corrección: Carolina Mueses, Luis Emilio Molina y Georgina Corporán"]})

;; ── Contraportada data ──────────────────────────────────────

(def backcover-data
  {:badge "La solución a todos los problemas"
   :text ["**Greb** es un método para sistematizar todas las cosas, este es un libro practico que explica como ver las cosas de manera **Greb** y explica la metodología para:"
          "· Organizar procesos\n· Ahorrar dinero\n· Disminuir la frustración\n· Tomar decisiones\n· Manejar equipos\n· Muchas cosas más"
          "Este libro es corto, pero te prometo que cambiará la manera en la cual verás los procesos."
          "**Guillermo Molina Mueses**\n(creador de **Greb**)"]})

;; ── Structure page data ─────────────────────────────────────

(def structure-data
  {:title "El libro está dividido en 3 partes"
   :desc "Para mantener las cosas sencillas el libro está dividido en **3 Secciones**"
   :sections [{:label "¿Qué es Greb?" :desc "Introducción al flujo y el pensamiento intuitivo" :color :blue :img "greb-g.png"}
              {:label "Herramientas de Greb" :desc "Los términos y herramientas para aplicar Greb" :color :pink :img "tool-symbols-pulsar.svg"}
              {:label "Ejemplos de Greb" :desc "Historias, productos, procedimientos e ideas Greb" :color :green :img "family-symbols-pulsar.svg"}]})

;; ══════════════════════════════════════════════════════════════
;; SECCIÓN 1: ¿Qué es Greb?
;; ══════════════════════════════════════════════════════════════

;; ── Page 9: ¿Qué es Greb? ──────────────────────────────────

(def intro-page-9
  {:id "intro"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :tag {:label "/intro" :color :blue}
   :bg-type :grid
   :blocks
   [{:type :gn-title-bar :title "¿QUÉ ES GREB?" :img "greb-g.png" :color :yellow}
    {:type :gn-card :color :blue :info-icon true
     :text ["**Greb** hace que las cosas fluyan."
            "Las organizaciones que son **Greb** son mucho más productivas que aquellas que están atadas a modelos estáticos del pasado."]}
    {:type :gn-card :color :yellow
     :text ["**Greb** es una palabra inventada. Es un *concepto*, un *adjetivo* y un *nombre*."
            "**Greb** nos ayuda a describir las cosas que son intuitivas, los procesos que fluyen, y los productos y objetos que funcionan particularmente bien."
            "A diferencia de muchos otros métodos, **Greb** busca **eliminar todo lo que sea absurdo** dentro de una organización y enfocarse en **tareas** que son **productivas** y que nos ayudan a cumplir nuestras metas."
            "En **Greb** no hay espacio para cosas que no funcionan."]}
    {:type :gn-two-col-text :drop-cap? false
     :paragraphs
     ["**Greb** nos lleva a adoptar ideas que crean mejores ambientes de trabajo, tanto en el sector público como en el privado, en organizaciones privadas e instituciones gubernamentales. **Greb** busca que tomemos decisiones que hagan que los usuarios de nuestros productos digan: \"Eso sí funciona\"."
      "Este libro recoge una serie de técnicas y estrategias para\n· Manejar equipos\n· Fomentar el desarrollo creativo\n· Organizar tu día a día\n· Mejorar la toma de decisiones."
      "**Greb** es la palabra que le deja saber a todo el mundo esta es la dirección correcta, una mentalidad de **mejora continua**. El espirítú de ser proactivo y anticiparse a los problemas siempre mejorar los procesos. Todos somos un poco **Greb** sin saberlo."
      "Con **Greb**, aprenderás a implementar métodos que optimizan la colaboración y aumentan la eficiencia en cualquier entorno laboral."
      "Al aplicar los principios de **Greb**, transformas tu entorno en un espacio donde todos pueden prosperar y alcanzar su máximo potencial."]}]})

;; ── Page 10: "Todos somos un poco Greb sin saberlo" ─────────

(def intro-page-10
  {:id "intro-10"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-title-bar :title "\"Todos somos un poco Greb sin saberlo\"" :color :yellow}
    {:type :gn-two-col-text :drop-cap? true
     :paragraphs
     ["Hola, mi nombre es Guillermo Molina y soy un amante de que las cosas fluyan… es un placer conocerle, gracias por tomarse el tiempo de leer mi libro. Durante los últimos quince años, he estado completamente y continuamente adicto a optimizar flujos de trabajo y hacer que las cosas sean amigables para el subconsciente."
      "Voy a intentar en estas páginas explicar lo que hago y qué es **Greb**. Esto es más fácil decirlo que hacerlo, ya que como veremos **Greb es algo subjetivo, diferente para cada persona**."
      "**Greb** es una **filosofía** que promueve hacer las cosas de una manera \"**amigable al subconsciente**\". Promueve ideas y comportamiento que te guían a operar en un estado de **flow**/flujo."
      "Es un **concepto**, un **adjetivo** y un **sustantivo**. Es una palabra inventada que no significaba nada hasta hace unos minutos, cuando agarraste este libro, y ahora te toca a ti definir **qué es Greb para ti**."
      "Queremos que los procesos y las cosas sean **Greb** porque son **divertidos de usar**, **intuitivos**, te hacen **más eficiente** y te permiten nunca tener que preocuparte por los componentes. Cuando algo es **Greb** y sale mal, no es una emergencia, es una molestia."
      "**Greb** es una forma de sistematizar prácticamente cualquier cosa, definir tus propios procesos para realizar tareas en estado de flow y diseñar esquemas de trabajo que sean amigables para el subconsciente. Desde productos hasta procesos y software, cualquier cosa puede ser un poco más o un poco menos **Greb**."]}]})

;; ── Page 11: Un ejemplo de Greb ─────────────────────────────

(def intro-page-11
  {:id "intro-11"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-title-bar :title "Un ejemplo de Greb" :color :yellow}
    {:type :gn-two-col-text :drop-cap? true
     :paragraphs
     ["A través de un **experimento mental**, Imagínate en una sala de espera de un hospital. Una persona se acerca al recepcionista y pregunta: **\"¿Dónde está el salón de radiografías?\"** La recepcionista responde: \"Al final del pasillo, a la izquierda\". Pasan cinco minutos y **otro paciente** viene con la misma pregunta, recibiendo las mismas indicaciones. Esto se repite varias veces más en el poco tiempo que tienes sentado. Si fueras el recepcionista y te enfrentaras a la misma pregunta repetitiva todos los días, ¿qué harías?"
      "Bueno, si piensas en **Greb**, ya estarías gritando: \"**¡PON UN LETRERO!\"**, o algo similar que resolvería esta molestia. Ya vas a buen camino."
      "**¡Eso es Greb!** Esa parte de nosotros que dice… vamos, tiene que haber una mejor manera. Analizando el ejemplo, el problema no es que responder esta pregunta esté fuera de la descripción del puesto de la recepcionista, ni que nadie esté haciendo algo indebido. Sin embargo, seguramente hay mejores cosas que esta persona podría estar haciendo, y cada vez que alguien la interrumpe se rompe el estado de flow."
      "Este es un proceso que claramente podría ser fácilmente sistematizado para que un gran porcentaje de las personas, siempre y cuando lean, puedan resolver este problema y seguir su camino. **Greb** contempla que hay cosas que necesitan ser perfectas, cosas que necesitan ser buenas y cosas que necesitan ser decentes para no perturbar las cosas buenas y perfectas."
      "**¿Qué parte de nosotros grita cuando las cosas no son intuitivas?** ¿Qué podemos hacer para diseñar cosas que eviten este grito interno? **Greb** es una palabra que explica cuando las cosas **no son amigables al subconsciente**, cuando causan ruido porque \"se sienten mal\"."]}
    {:type :gn-image :src "salon-de-espera.tif" :alt "Sala de espera de hospital" :caption ""}]})

;; ── Page 12 ─────────────────────────────────────────────────

(def intro-page-12
  {:id "intro-12"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-card :color :yellow
     :text ["Cuando decimos \"**esto me da la impresión**\" o \"**no puedo explicar**\", la parte que grita es el subconsciente, que no está siendo escuchado por la parte racional. El concepto de introducir decisiones amigables al subconsciente en ambientes de trabajo no debería ser una conversación tabú; al contrario, es un concepto bien estudiado e implementado en las culturas orientales."
            "Aplicando **Greb**,\n· Identificarías este problema\n· Crearías un sistema para anticipar la pregunta\n· Maximizarías el tiempo productivo"]}
    {:type :gn-card :color :pink
     :text ["Hay una diferencia entre pasarse el día trabajando y hacer algo **productivo**. Es fácil pasarse el día en reuniones, mandando correos y coordinando cosas, pero esto no necesariamente nos hace productivos. Lo más Greb es preguntarnos: ¿qué estoy tratando de implementar?, ¿cuál es mi entregable? Nunca hay que sentarse a trabajar sin tener esto claro."]}
    {:type :gn-tip :label "Recuerda:" :title "Ocupado ≠ Productivo" :text "Trata de enfocarte en una tarea precisa"}]})

;; ── Page 13 ─────────────────────────────────────────────────

(def intro-page-13
  {:id "intro-13"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-card :color :yellow
     :text ["Cuando he trabajado en consultorías, muchas veces lo primero que pasa cuando se habla de sistematización es que alguien pregunta: \"**Y cuando pongamos el letrero, ¿qué va a hacer la recepcionista?**\" Esto nos pasa con muchas otras cosas que no queremos sistematizar por miedo a que la persona se quede sin funciones."
            "¿Qué tal si en vez pensamos en que la recepcionista tendrá más tiempo para encargarse de los pacientes o programar citas recurrentes? Cuando no tenemos a nuestros colaboradores haciendo cosas que ni nosotros mismos haríamos, hacemos que los trabajos sean más humanos y productivos."
            "Entonces, **¿Greb se trata solo de optimizar?** No siempre. Hay muchos atributos que hacen que algo sea Greb, y lo que es Greb para mí no necesariamente es lo mismo para ti. Para eso, hay que definir buenos criterios, pero no entremos en eso por ahora. Tenemos dos cosas de las que necesitamos hablar primero: **las tareas y los niveles**."]}
    {:type :gn-card :color :cream
     :text ["La unidad básica de un sistema **Greb** es una **tarea**. Cada vez que transformas algo, tienes una tarea. Por ejemplo, si estás escribiendo, es una tarea convertir lo que piensas en texto; si estás cocinando un huevo, las tareas podrían ser romper el huevo en un tazón, mezclar con sal, verter en el sartén. Lo que define estos pasos como una tarea es que tenemos un \"**input**\" o una \"**entrada**\" y un \"**output**\" o una \"salida\" que convierte el elemento de no cocinado a cocinado, sin sal a con sal, etc. Si podemos contar cuáles son los procesos y definir qué pasa en nuestras organizaciones, podemos determinar qué está automatizado y qué tarea tiene muchos \"**inputs**\" o entradas. Esto puede ser **un indicador de un problema**."]}]})

;; ── Page 14: Puertas Norman ─────────────────────────────────

(def norman-page-14
  {:id "norman"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-title-bar :title "Cuando le damos prioridad al diseño sobre función." :img "icono_circle-exclamation.png" :color :pink}
    {:type :gn-two-col-text :drop-cap? true
     :paragraphs
     ["Empecemos con un término: la **Puerta Norman**, una puerta que se supone debe abrirse empujando pero tiene un manubrio de jalar, eso hace que cada vez que vas a abrir la puerta, la jalas y luego te das cuenta que hay que empujar. Cualquier puerta ambigua puede calificar."
      "**Las Puertas Norman son objetos que han preferido el diseño por encima de la funcionalidad.** Esta indiscreción genera puntos de fricción que hacen nuestro día a dia menos amigable al subconsciente."
      "Las **Puertas Norman** no solo son objetos, pueden ser procedimientos, distribuciones de espacios. Siempre que encontremos un elemento que no funciona bien pero se ve bonito tenemos un buen indicador de que puede haber un problema. Requieren esfuerzo cognitivo por parte del usuario, contradiciendo la esencia misma del buen diseño, que según Norman, debería ser invisible e intuitivo."
      "El principio subyacente aquí se extiende mucho más allá de los objetos físicos como puertas e interruptores. Es un principio que se aplica a todos los aspectos de la vida, desde organizar tu día de trabajo hasta diseñar una interfaz digital o estructurar un proceso empresarial."]}
    {:type :gn-bio-card
     :name "Donald Norman"
     :photo "donald_norman.jpg"
     :info-text "**Donald Arthur Norman** (25 de diciembre de 1935) es un investigador, profesor y autor estadounidense. Norman es el director del Laboratorio de Diseño en la Universidad de California, San Diego. Es mejor conocido por sus libros sobre diseño, especialmente \"**La psicología de los objetos cotidianos**\"."}]})

;; ── Page 16: Estudios de Movimiento ─────────────────────────

(def move-page-16
  {:id "move"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-title-bar :title "¿Qué son los estudios de movimiento?" :img "family-symbols-pulsar.svg" :color :blue}
    {:type :gn-two-col-text :drop-cap? true
     :paragraphs
     ["Un estudio de movimiento es una técnica utilizada para analizar el desplazamiento y la interacción de objetos y seres vivos en el espacio y el tiempo. Esta práctica tiene sus raíces en la ciencia y el arte, donde se busca comprender y reproducir con precisión los movimientos naturales para diversas aplicaciones, desde la animación y el cine hasta la biomecánica y la robótica."
      "Muybridge utilizó una serie de cámaras dispuestas en fila para capturar secuencias de imágenes de caballos galopando. Este trabajo, realizado en la década de 1870, no solo demostró que los caballos levantan las cuatro patas del suelo al galopar, sino que también sentó las bases para el desarrollo del cine y la animación. Los estudios de movimiento consisten en grabar el proceso y analizar cuánto tiempo toma cada uno de los pasos en una tabla."]}
    {:type :gn-bio-card
     :name "Eadweard Muybridge"
     :photo "muybridge_profile.jpg"
     :info-text "**Eadweard Muybridge** (9 de abril de 1830 - 8 de mayo de 1904) fue un fotógrafo inglés conocido por sus trabajos pioneros en estudios fotográficos del movimiento y en la proyección de películas. Es famoso por sus experimentos fotográficos sobre el movimiento de animales y humanos, especialmente su serie de 1878 \"**El caballo en movimiento**\", que usó múltiples cámaras para capturar el movimiento en fotografías stop-motion."
     :aside-img "muybridge.jpg"
     :aside-text "**Los estudios de movimiento** ahorran tiempo, una imagen vale mas que mil palabras y un video más que mil imágenes."}]})

;; ── Page 17: Frank Gilbreth ─────────────────────────────────

(def move-page-17
  {:id "move-17"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-title-bar :title "Si vas a hacer algo trata de ser el mejor en esa tarea" :img "icono_check.png" :color :blue}
    {:type :gn-two-col-text :drop-cap? true
     :paragraphs
     ["Hay un libro escrito por Frank B. Gilbreth Jr. y Ernestine Gilbreth Carey, \"**Más barato por docena**\", que ofrece un ejemplo de una familia **Greb**. El libro habla sobre el padre y la madre de los autores, Frank y Lilian eran expertos en eficiencia y tenían doce hijos."
      "La esposa de Gilbreth, **Lillian Moller Gilbreth**, fue una psicóloga industrial e ingeniera que complementaba el trabajo de su esposo con estudios sobre el comportamiento humano y la psicología. Juntos, aplicaron su experiencia a su hogar, transformándolo en un modelo de eficiencia, de ahí el título \"**Más barato por docena**\"."
      "**Frank B. Gilbreth Sr.** fue un pionero del **estudio del movimiento**, reconocido por su capacidad para convertir tareas mundanas en procesos eficientes. Frank empezó su carrera como enladrillador y diseñó una forma más eficiente de colocar ladrillos; su principal innovación fue un andamio para no tener que bajar al piso a buscar cemento."]}
    {:type :gn-bio-card
     :name "Frank Gilbreth"
     :photo "frankgilbreth.png"
     :info-text "**Frank Bunker Gilbreth** (7 de julio de 1868 – 14 de junio de 1924) fue un ingeniero, consultor y pionero estadounidense del estudio científico del trabajo. Junto con su esposa Lillian Moller Gilbreth, desarrolló técnicas de estudio de tiempos y movimientos para la eficiencia industrial. Sus innovaciones incluyen el proceso de \"**therbligs**\" para analizar movimientos y el uso de fotografías y películas para estudiar el trabajo."}]})

;; ── Page 18: Therbligs ──────────────────────────────────────

(def therbligs-page-18
  {:id "move-18"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-card :color :cream
     :title "Tareas/Therbligs"
     :text ["El proceso **Therbligs** o **tareas** es una herramienta de análisis de movimientos desarrollada por Frank y Lillian Gilbreth. El término **Therblig** es un anagrama del apellido \"Gilbreth\" *(casi al revés)* y representa los **movimientos básicos** de las manos en una tarea manual. En **Greb** expandiendo los **Therbligs** y utilizamos el término \"**tarea**\" para referirnos a cualquier proceso incluyendo los 18 Therblings originales."]}
    {:type :gn-table
     :headers ["Therblig" "Símbolo" "Descripción"]
     :rows [["01. Buscar"       {:icon "search"} "Ojos/manos buscan objeto"]
            ["02. Seleccionar"  {:icon "list"} "Elegir un objeto entre varios"]
            ["03. Alcanzar"     {:icon "move"} "Mover la mano hacia un objeto"]
            ["04. Agarrar"      {:icon "hand"} "Cerrar la mano sobre un objeto"]
            ["05. Mover"        {:icon "move-right"} "Transportar el objeto"]
            ["06. Colocar"      {:icon "grid-3x3"} "Ajustar a una posición específica"]
            ["07. Soltar"       {:icon "square"} "Dejar ir un objeto"]
            ["08. Usar"         {:icon "wrench"} "Aplicar un objeto a su uso"]
            ["09. Inspeccionar" {:icon "eye"} "Observar cuidadosamente"]
            ["10. Ensamblar"    {:icon "puzzle"} "Juntar piezas"]
            ["11. Desensamblar" {:icon "unplug"} "Separar piezas"]
            ["12. Esperar(I)"   {:icon "x"} "Innecesario se puede eliminar"]
            ["13. Esperar(N)"   {:icon "clock"} "Necesario no se puede eliminar"]
            ["14. Planear"      {:icon "brain"} "Pensar los pasos siguientes"]
            ["15. Retener"      {:icon "lock"} "Mantener un objeto sin usarlo"]
            ["16. Pre-Colocar"  {:icon "target"} "Ubicar un objeto para usar luego"]
            ["17. Buscar(O)"    {:icon "scan"} "Solo ojos buscan objeto"]
            ["18. Fallo"        {:icon "alert-circle"} "Fallo de agarre o en la tarea"]]}]})

;; ── Page 19: Estudios de Movimiento (ejemplo galletas) ──────

(def move-page-19
  {:id "move-19"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-card :color :blue
     :title "Estudios de Movimiento"
     :text ["Analicemos un proceso por ejemplo: **hornear una tanda de galletas**.\nLos pasos pueden ser:"]}
    {:type :gn-steps-list
     :items [{:icon "search"      :label "Buscar los ingredientes en la despensa" :time "5 min"}
             {:icon "scale"       :label "Medir y pesar los ingredientes" :time "6 min"}
             {:icon "chef-hat"    :label "Mezclar los ingredientes" :time "4 min"}
             {:icon "flame"       :label "Precalentar el horno" :time "10 min"}
             {:icon "grid-3x3"    :label "Organizar las galletas en la bandeja" :time "3 min"}
             {:icon "timer"       :label "Hornear las galletas" :time "15 min"}
             {:icon "package"     :label "Esperar que enfríen y empaquetar" :time "10 min"}]}
    {:type :gn-card :color :cream
     :text ["Este es un proceso que puede repetirse varias veces al día en una planta o cocina industrial. **¿Cuál es la tarea principal y cuáles son las tareas de soporte?**"
            "La tarea principal es **hornear las galletas**, ya que es **la acción** que **transforma la materia prima en producto terminado**. Las demás son tareas de soporte necesarias para que esa acción se ejecute correctamente."
            "**¿Qué pasaría si tuviéramos que producir mil galletas en lugar de veinte?** Repetir manualmente estos 7 pasos una y otra vez sería ineficiente. Este es el tipo de fricción que queremos eliminar."
            "En **Greb** hay que hacerse experto en estudiar que son tareas principales y que son tareas de soporte y calcular nuestros tiempos."]}]})

;; ── Pages 20-21: ¿Qué es Flow? / Feature pills ─────────────

(def flow-page-20
  {:id "flow"
   :section 1
   :sidebar-label "Que es Greb"
   :sidebar-color :blue
   :bg-type :grid
   :blocks
   [{:type :gn-title-bar :title "¿Cómo identificar a un adicto al flujo?" :img "greb-g.png" :color :blue}
    {:type :gn-feature-pills
     :items [{:img "door-symbols-pulsar.svg"   :title "Empiezas a ver Puertas Norman en todas partes" :text "Te irritas cuando ves objetos que ponen estética sobre funcionalidad." :color :pink}
             {:img "pain-symbols-pulsar.png"    :title "La obsesión por la optimización" :text "Siempre buscas formas de agilizar los procesos y no desperdiciar ni tiempo ni recursos." :color :yellow}
             {:img "matrix-symbols-pulsar.svg"  :title "El espíritu de la sistematización" :text "Diseñas sistemas para todo, desde organizar tus archivos hasta planificar tu día." :color :green}
             {:img "time-symbols-pulsar.svg"    :title "El rastreador del tiempo" :text "Sabes cuánto tiempo te toman las tareas en promedio." :color :cream}
             {:img "wave-lines-pulsar.svg"      :title "Tienes tu Flow organizado" :text "Tienes planificado qué vas a hacer en tu próxima sesión de flow." :color :blue}
             {:img "open-end-wrench.svg"        :title "Te encanta encontrar herramientas" :text "Siempre estás buscando nuevas herramientas o software que puedan ayudar a optimizar tu productividad." :color :yellow}
             {:img "flash-on-icon.svg"          :title "Sabes manejar tu energía" :text "No sobre trabajas, pero sabes aprovechar los momentos de flow." :color :green}
             {:img "wordbook-icon.svg"          :title "El amante del aprendizaje" :text "Siempre estás abierto a nuevas ideas y conceptos que puedan ayudar a mejorar tu productividad y eficiencia." :color :cream}]}
    {:type :gn-card :color :yellow :no-border true
     :text ["Si te encuentras asintiendo a estos puntos, ¡felicidades!\n**Puede que seas miembro de pleno derecho del club de los adictos al flujo.**"]}]})
