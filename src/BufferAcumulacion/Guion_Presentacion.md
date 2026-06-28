# Guion de Presentación — Buffer de Acumulación

> Duración total: **~20 minutos** · Bloque 1 (teoría): 10 min · Bloque 2 (código): 10 min
> Integrantes: Edison Enriquez, Angelo Silva, Stalin Acurio · Taller II — Computación 2026
>
> **Cómo usar este guion:** el texto en *cursiva con viñeta* («**Decir:**») es lo que se narra
> en voz alta. Los recuadros **[SLIDE]**, **[IMAGEN]** y **[DEMO]** son indicaciones de apoyo
> (no se leen). Los tiempos son acumulativos y aproximados; el objetivo es no pasar de 10 min por bloque.

---

# BLOQUE 1 — TEORÍA (10 minutos)

## 0:00 – 0:45 · Apertura y gancho

**[SLIDE 1: portada de la presentación]**

- **Decir:** «Buenos días. Cuando vemos un coche de carreras borroso en una foto, o los bordes
  perfectamente suaves de una imagen renderizada, casi siempre hay la misma idea detrás:
  **promediar muchas imágenes en una sola**. La herramienta clásica para hacer eso con alta
  precisión es el **buffer de acumulación**. En los próximos diez minutos vamos a ver qué es,
  qué cinco operaciones lo definen y para qué sirve; y luego mi compañero mostrará cómo se
  programa píxel a píxel en Java.»

> **[IMAGEN sugerida — buscar]** Un collage de tres efectos reales lado a lado:
> *(a)* foto con *motion blur* (coche o ciclista), *(b)* comparación con/sin antialiasing
> (bordes dentados vs. suaves), *(c)* foto con *depth of field* (fondo desenfocado).
> Búsqueda: "motion blur", "antialiasing aliased vs antialiased", "depth of field bokeh".
> Objetivo: enganchar mostrando que es algo cotidiano.

## 0:45 – 2:15 · ¿Qué es el buffer de acumulación?

**[SLIDE 2: "¿Qué es el Buffer de Acumulación?"]**

- **Decir:** «El buffer de acumulación es un **framebuffer fuera de pantalla** —*off-screen*—,
  es decir, una zona de memoria de imagen que el usuario nunca ve directamente. Forma parte
  del *pipeline* de rasterización y su trabajo es uno solo: **combinar varias imágenes
  renderizadas con mucha precisión** para producir efectos de calidad superior: antialiasing,
  motion blur, profundidad de campo y sombras suaves.»
- **Decir:** «La analogía que conviene grabarse es la de un **"sumador" de imágenes**. El color
  buffer normal guarda únicamente el fotograma que se está viendo. El buffer de acumulación,
  en cambio, va **sumando —promediando ponderadamente— varios renders**, y solo al final
  copia el resultado al color buffer para mostrarlo.»

> **[IMAGEN sugerida — crear]** Diagrama tipo "cuenta bancaria": tres miniaturas de la misma
> escena (Render 1, 2, 3) entrando con una flecha "+" hacia una caja grande etiquetada
> "BUFFER DE ACUMULACIÓN", y de ahí una flecha "=" hacia la "PANTALLA". Refuerza la idea de sumador.

## 2:15 – 4:00 · Por qué es especial: precisión y rango

**[SLIDE 3: tabla "Buffer de Acumulación vs. Color Buffer Normal"]**

- **Decir:** «¿Por qué no usar el color buffer de siempre? Por **dos características**: más
  precisión numérica y un rango de valores extendido. Veámoslo en la tabla.»
- **Decir (recorriendo la tabla):** «El color buffer normal trabaja en el rango cero a uno, con
  típicamente **8 bits por componente**, no admite valores negativos y está muy acelerado por
  hardware. El buffer de acumulación trabaja en el rango **menos uno a uno**, con **16 bits o
  más** por componente, **sí admite valores negativos de forma nativa**, pero está poco
  acelerado en hardware barato.»
- **Decir:** «La clave de examen: **los 16 bits y el soporte de negativos** son lo que permite
  sumar cientos de imágenes sin que el error se descontrole. Con 8 bits, el redondeo de cada
  suma se acumula y arruina el resultado.»

> **[IMAGEN sugerida — crear]** Dos "reglas numéricas": una de 8 bits con pocas marcas (0–255)
> y otra de 16 bits con muchísimas marcas finas, más una etiqueta "[-1, 1] admite negativos".
> Comunica visualmente "más resolución".

## 4:00 – 5:30 · Cómo funciona internamente (el flujo)

**[SLIDE 4: "Flujo de Trabajo Interno" — Renderizar → Transferir → Repetir → Copiar]**

- **Decir:** «Un detalle fundamental: **al buffer de acumulación no se le dibuja geometría
  directamente**. El pipeline no puede rasterizar sobre él. Solo acepta **el contenido del
  color buffer**. Por eso el flujo es siempre el mismo cuatro pasos: **renderizar** la escena
  al color buffer, **transferir** ese color buffer al de acumulación, **repetir** para cada
  variación —otro ángulo, otro instante de tiempo, otro *jitter* de cámara— y al final
  **copiar** el resultado de vuelta para mostrarlo.»
- **Decir:** «Esto tiene una consecuencia: como el *depth buffer* y el *stencil* no participan,
  **no podemos enmascarar** parte de la imagen con profundidad. Conviene pensar el buffer no
  como un lienzo donde se dibuja, sino como un **espacio de alta precisión para escalar,
  combinar y recortar imágenes de color**.»

> **[IMAGEN sugerida — crear]** Diagrama de flujo circular con 4 nodos
> (Renderizar → Transferir → Repetir ↺ → Copiar) y, al margen, los íconos de *depth* y *stencil*
> tachados con una "X" para reforzar "no participan".

## 5:30 – 8:00 · Las cinco operaciones (el corazón de la charla)

**[SLIDE 5: "Las Cinco Operaciones del Buffer"]**

- **Decir:** «Todo el buffer se maneja con **cinco operaciones**. Si se entienden estas cinco,
  se entiende el tema completo. Las agrupo por lo que hacen.»

**[SLIDE 6: GL_LOAD y GL_ACCUM]**

- **Decir — GL_LOAD (cargar):** «**GL_LOAD** reemplaza el contenido del buffer con la imagen
  del color buffer multiplicada por un factor. Es **siempre el primer paso**: pone el estado
  base. Matemáticamente: `acum = color_buffer × factor`. Se usa con factor uno para cargar tal
  cual.»
- **Decir — GL_ACCUM (acumular):** «**GL_ACCUM** es la operación central. Toma la imagen actual,
  la escala y la **suma** a lo que ya hay: `acum += color_buffer × factor`. Si quiero promediar
  N imágenes, llamo N veces con factor **uno entre N**. Por ejemplo, con ocho imágenes, factor
  cero coma ciento veinticinco; los ocho pesos suman uno y obtengo un promedio correcto.»

**[SLIDE 7: GL_ADD, GL_MULT y GL_RETURN]**

- **Decir — GL_ADD (sesgo):** «**GL_ADD** suma una **constante** a todos los píxeles y **no lee
  el color buffer**. Sirve, por ejemplo, para mover valores del rango menos uno a uno hacia el
  rango cero a uno sumando un *bias* de cero coma cinco.»
- **Decir — GL_MULT (escalar):** «**GL_MULT** multiplica todo el buffer por una constante,
  tampoco lee el color buffer. Es ideal para **efectos de estela**: primero `GL_MULT(0.8)` para
  debilitar lo anterior y luego `GL_ACCUM` para añadir el cuadro nuevo.»
- **Decir — GL_RETURN (mostrar):** «Y **GL_RETURN** es el cierre: escala el contenido, **lo
  recorta (clamp) al rango cero a uno** y lo copia al color buffer visible.
  `color_buffer = clamp(acum × factor, 0, 1)`. El recorte es importante: sin él, la suma se
  saldría de rango.»
- **Decir (síntesis):** «Regla mnemónica: **LOAD inicia, ACCUM suma en bucle, ADD y MULT ajustan
  sin leer la imagen, y RETURN muestra recortando.**»

> **[IMAGEN sugerida — crear]** Infografía de una sola fila con 5 íconos y su fórmula debajo:
> LOAD (`=`), ACCUM (`+=`), ADD (`+ c`), MULT (`× c`), RETURN (`→ clamp`). Que quede como
> "chuleta" memorizable. Es la diapositiva que el público fotografiará.

## 8:00 – 9:30 · Aplicaciones

**[SLIDE 8: "Antialiasing y Motion Blur"]**

- **Decir — Antialiasing:** «La aplicación clásica. Se renderiza la escena **N veces con la
  cámara desplazada en subpíxeles** —el llamado *jitter*—, se acumula cada versión con peso uno
  entre N, y el promedio **suaviza los bordes dentados**. El desplazamiento se logra tocando la
  matriz de proyección con `glOrtho`.»
- **Decir — Motion Blur:** «Simula una cámara con obturador lento: se renderiza el objeto en
  **varios instantes de tiempo** dentro del mismo cuadro y se promedian. La optimización clave:
  el **fondo estático se carga una sola vez** con `GL_LOAD(1.0)` y solo el objeto móvil se
  acumula N veces; así no re-renderizamos lo que no se mueve.»

**[SLIDE 9: "Profundidad de Campo, Sombras y Composición"]**

- **Decir — Profundidad de campo:** «Se renderiza desde **varias posiciones de cámara**
  desplazadas lateralmente pero apuntando al **mismo punto focal**. Lo que está en el plano
  focal cae siempre en el mismo lugar y queda nítido; lo demás se promedia y sale borroso.»
- **Decir — Sombras suaves:** «Mismo truco del antialiasing pero aplicado a **la posición de la
  luz**: muchas sombras duras promediadas producen **penumbra**.»
- **Decir — Composición de alta precisión:** «Y el rango extendido y los 16 bits sirven para
  componer cuando 8 bits no alcanzan. **Limitación honesta:** solo multiplica por un escalar
  constante, **no por un alpha por píxel**.»

> **[IMAGEN sugerida — buscar/crear]** Cuatro paneles "antes/después": antialiasing (borde
> escalonado → suave), motion blur (objeto nítido → con estela), depth of field (todo nítido →
> fondo borroso), soft shadows (sombra dura → con penumbra). Una imagen por aplicación.

## 9:30 – 10:00 · Cierre del bloque teórico y puente al código

**[SLIDE 5 o slide de resumen]**

- **Decir:** «En resumen: el buffer de acumulación es un **lienzo de alta precisión** que **suma
  imágenes** mediante **cinco operaciones** —LOAD, ACCUM, ADD, MULT, RETURN— para lograr efectos
  que un solo render no puede. Lo interesante es que **esta idea no necesita OpenGL**: se puede
  reproducir a mano, píxel a píxel. Eso es justo lo que vamos a ver ahora en Java.»

---

# BLOQUE 2 — EXPLICACIÓN DEL CÓDIGO (10 minutos)

> Archivo: `src/BufferAcumulacion/taller/BufferAcumulacion.java`
> Idea: reproducir **LOAD → ACCUM → RETURN** sin OpenGL, usando arreglos `float` como buffer,
> para generar un **efecto de estela / motion blur** sobre una imagen fija (una taza).

**[DEMO recomendada]** Tener listas, antes de empezar, la imagen de entrada
`imagenes/taza.jpg` y la salida `imagenes/tazaBufferAcumulacion.jpg` ya generada, para mostrar
el resultado de inmediato si la ejecución en vivo falla.

## 10:00 – 11:00 · Visión general y parámetros

**[SLIDE/PANTALLA: líneas 10–23 del archivo]**

- **Decir:** «La meta de este programa es **demostrar el concepto sin librerías gráficas**:
  vamos a construir nuestro propio buffer de acumulación con tres arreglos de números y a
  aplicar las operaciones a mano. El efecto que buscamos es una **estela de movimiento** sobre
  una imagen estática.»
- **Decir (señalando parámetros):** «Definimos la **imagen de entrada** —`taza.jpg`— y la de
  **salida**. Y dos parámetros que controlan el efecto: `muestras = 25`, que es **cuántas copias
  acumulamos**, y `desplazamiento = 8`, que son **cuántos píxeles se corre cada copia**. A más
  muestras, estela más larga; a más desplazamiento, más separada.»

> **[IMAGEN sugerida — usar las del propio proyecto]** Mostrar `imagenes/taza.jpg` (original) y
> `imagenes/tazaBufferAcumulacion.jpg` (resultado) lado a lado. Es la prueba visual del efecto.

## 11:00 – 12:15 · La estructura del buffer (el concepto clave del código)

**[PANTALLA: líneas 27–42]**

- **Decir:** «Leemos la imagen y obtenemos ancho y alto. Y aquí está **la decisión de diseño más
  importante**: el buffer.»
- **Decir:** «En lugar de guardar colores en enteros de 0 a 255, creamos **tres arreglos de
  `float`** —`bufferR`, `bufferG`, `bufferB`— uno por canal. ¿Por qué `float` y no `int`? Porque
  vamos a **sumar muchas copias ponderadas**, y necesitamos **decimales y rango amplio** para no
  perder precisión en el camino. **Esto es exactamente lo que hacía el hardware con sus 16 bits**:
  aquí lo imitamos con punto flotante.»
- **Decir:** «Fíjense que el buffer es **unidimensional**, de tamaño `ancho × alto`. Por eso más
  adelante convertimos la coordenada bidimensional `(x, y)` en un índice lineal con
  `index = y × ancho + x`. Es solo una forma compacta de recorrer la imagen.»

> **[IMAGEN sugerida — crear]** Esquema de la rejilla 2D de píxeles `(x,y)` con una flecha que la
> "desenrolla" a un arreglo 1D, mostrando la fórmula `index = y*ancho + x`. Aclara el mapeo de
> coordenadas que suele confundir.

## 12:15 – 13:45 · GL_LOAD a mano

**[PANTALLA: líneas 44–71]**

- **Decir:** «Primer paso, **el equivalente a GL_LOAD**: cargar la imagen original dentro del
  buffer. Recorremos cada píxel con el doble bucle `for (y) for (x)`.»
- **Decir (explicando el bit-shifting):** «Por cada píxel calculamos su `index`, leemos el color
  con `getRGB`, y **extraemos los tres canales con operaciones de bits**: el rojo es desplazar 16
  bits y enmascarar con `& 0xFF`; el verde, 8 bits; el azul, sin desplazar. Esto separa un entero
  de 32 bits en sus tres componentes de 8.»
- **Decir:** «Y guardamos esos valores **tal cual** en el buffer: `bufferR[index] = r`, etc.
  Equivale a `GL_LOAD` con **factor uno**: cargamos la imagen base sin escalar.»

> **[IMAGEN sugerida — crear/reutilizar]** Diagrama del *bit-shifting* ARGB: un entero de 32 bits
> partido en cuatro bloques de 8 (A, R, G, B) con las flechas `>> 16 & 0xFF`, `>> 8 & 0xFF`,
> `& 0xFF`. (Puede reutilizarse de la chuleta `src/practice/NOTES.md`.)

## 13:45 – 16:00 · GL_ACCUM a mano (el núcleo del efecto)

**[PANTALLA: líneas 73–122]**

- **Decir:** «Segundo paso, **GL_ACCUM**, y es el corazón del programa. Aquí acumulamos **24
  copias adicionales** de la imagen, cada una **desplazada** y con **menos peso** que la anterior.»
- **Decir (el bucle externo, líneas 83–89):** «El bucle va de `i = 1` a `muestras`. Para cada
  copia calculamos dos cosas: el **desplazamiento** de esa copia, `offset = i × 8` —la copia
  número i va i veces más corrida—, y un **peso decreciente**, `peso = 0.85 elevado a i`. La
  primera copia pesa 0.85, la segunda 0.72, y así cae exponencialmente. **Ese peso decreciente
  es lo que hace que la estela se desvanezca** en vez de cortarse de golpe.»
- **Decir (el desplazamiento, líneas 98–100):** «Dentro, para cada píxel de destino `x`, leemos
  el píxel de **más a la izquierda**: `origenX = x − offset`. El `if` comprueba que no nos
  salgamos de la imagen. Tomar el píxel de la izquierda y colocarlo a la derecha es lo que
  **genera la estela hacia la derecha**.»
- **Decir (la acumulación, líneas 116–118):** «Y la línea esencial: `bufferR[index] += r × peso`,
  igual para G y B. **Sumamos, no reemplazamos.** Cada copia añade su contribución, escalada por
  su peso. Esto es literalmente la fórmula `acum += color × factor` que vimos en la teoría.»
- **Decir (matiz de experto — IMPORTANTE):** «Quiero ser honesto con un detalle: en un promedio
  formal de antialiasing los pesos suman uno. **Aquí no se divide por la suma de pesos**: partimos
  de la imagen a peso completo y le sumamos copias encima, así que **los valores crecen y tienden
  a saturar**. Por eso es imprescindible el recorte del siguiente paso. El resultado se parece más
  a una **estela luminosa / *ghosting*** que a un motion blur fotométricamente exacto, pero ilustra
  perfectamente el mecanismo LOAD + ACCUM + RETURN. Si quisiéramos un promedio real, dividiríamos
  el buffer entre la suma de los pesos antes de mostrar.»

> **[IMAGEN sugerida — crear]** Tira de copias semitransparentes de la taza, cada una más a la
> derecha y más tenue, superpuestas, mostrando cómo nace la estela. Etiquetar el peso
> `0.85^i` bajo cada copia.

> **[IMAGEN sugerida — crear, opcional]** Pequeña curva de `peso = 0.85^i` para i = 1..25,
> mostrando la caída exponencial. Refuerza por qué la estela se desvanece.

## 16:00 – 17:45 · GL_RETURN a mano

**[PANTALLA: líneas 124–151]**

- **Decir:** «Tercer y último paso, **GL_RETURN**: convertir el buffer acumulado de nuevo en una
  imagen visible.»
- **Decir (el clamp, líneas 141–143):** «Recorremos otra vez todos los píxeles y aplicamos
  `Math.clamp(valor, 0, 255)` a cada canal. **Este es el recorte del que hablábamos**: como la
  acumulación pudo superar 255, lo forzamos al rango válido. Es el equivalente exacto al *clamp*
  a cero–uno que `GL_RETURN` hace automáticamente; aquí, como trabajamos en 0–255, recortamos a
  255.»
- **Decir (reconstrucción, línea 146):** «Y volvemos a **empaquetar** los tres canales en un solo
  entero con la operación inversa al bit-shifting: rojo desplazado 16, verde 8, azul sin
  desplazar, unidos con OR. Lo escribimos en la imagen resultado con `setRGB`.»
- **Decir (líneas 154–156):** «Finalmente `ImageIO.write` guarda el JPG y confirmamos por
  consola. Ahí está el efecto completo.»

> **Nota técnica para responder si preguntan:** `Math.clamp` requiere **Java 21 o superior**;
> en versiones antiguas se sustituye por `Math.max(0, Math.min(255, valor))`. (Por esto el
> proyecto no compila entero con un JDK viejo.)

## 17:45 – 19:15 · Demo en vivo y lectura del resultado

**[DEMO: ejecutar el programa]**

- **Decir:** «Vamos a ejecutarlo.» Comando, desde la raíz del proyecto:
  ```bash
  javac -encoding UTF-8 -d out -sourcepath src src/BufferAcumulacion/taller/BufferAcumulacion.java
  java -cp out BufferAcumulacion.taller.BufferAcumulacion
  ```
- **Decir (mostrando la salida):** «Aquí está `tazaBufferAcumulacion.jpg`. Observen la **estela
  hacia la derecha** y cómo las zonas se **aclaran** por la suma. Si subimos `muestras`, la
  estela se alarga; si subimos `desplazamiento`, se separa; y si bajáramos el `0.85`, se
  desvanecería más rápido.»

> **[IMAGEN sugerida — preparar de respaldo]** Tener capturas con **distintos parámetros**
> (por ejemplo `muestras = 10` vs `25`, `desplazamiento = 4` vs `12`) para mostrar el efecto de
> cada variable sin recompilar en vivo.

## 19:15 – 20:00 · Cierre

- **Decir:** «Para cerrar: hemos visto que el buffer de acumulación es, en esencia, **sumar
  imágenes ponderadas en un espacio de alta precisión** y luego recortar al rango visible. Lo
  demostramos con cinco operaciones en la teoría y reprodujimos tres de ellas —**LOAD, ACCUM y
  RETURN**— en unas pocas decenas de líneas de Java, sin ninguna librería gráfica, usando
  arreglos `float` como buffer de precisión. El mismo principio que suaviza bordes, desenfoca
  fondos y crea estelas de movimiento en los motores gráficos modernos. Gracias.»

---

## Anexo · Guía rápida de imágenes a preparar

| # | Momento | Tipo | Qué muestra | Buscar / Crear |
| --- | --- | --- | --- | --- |
| 1 | Apertura | Foto | Motion blur + antialiasing + depth of field reales | Buscar |
| 2 | ¿Qué es? | Diagrama | Tres renders sumándose en una caja → pantalla | Crear |
| 3 | Precisión | Diagrama | Reglas de 8 vs 16 bits + rango [-1,1] | Crear |
| 4 | Flujo interno | Diagrama | Ciclo Renderizar→Transferir→Repetir→Copiar; depth/stencil tachados | Crear |
| 5 | 5 operaciones | Infografía | LOAD/ACCUM/ADD/MULT/RETURN con sus fórmulas | Crear |
| 6 | Aplicaciones | Antes/Después | 4 paneles: AA, motion blur, DoF, soft shadows | Buscar/Crear |
| 7 | Parámetros código | Foto proyecto | `taza.jpg` vs `tazaBufferAcumulacion.jpg` | Usar del proyecto |
| 8 | Buffer 1D | Diagrama | Rejilla 2D desenrollada a arreglo + `index=y*ancho+x` | Crear |
| 9 | GL_LOAD | Diagrama | Bit-shifting ARGB de 32 bits | Crear/Reutilizar |
| 10 | GL_ACCUM | Diagrama | Copias semitransparentes formando la estela + curva `0.85^i` | Crear |
| 11 | Demo | Capturas | Resultado con distintos `muestras`/`desplazamiento` | Crear (respaldo) |

## Anexo · Posibles preguntas del público

- **¿Por qué `float` y no `int` en el buffer?** Para conservar precisión al sumar muchas copias
  ponderadas; emula los 16 bits del hardware real.
- **¿Por qué la imagen se aclara?** Porque no se divide por la suma de pesos; se suma sobre la
  imagen a peso completo y el `clamp` corta en 255. Para un promedio real habría que normalizar.
- **¿Por qué la estela va hacia la derecha?** Porque cada copia lee el píxel de la izquierda
  (`origenX = x − offset`) y lo coloca en `x`.
- **¿Qué operaciones del buffer NO aparecen en el código?** `GL_ADD` (sesgo) y `GL_MULT`
  (escalar el buffer); el ejemplo solo necesita LOAD, ACCUM y RETURN.
- **¿Corre en cualquier Java?** No: `Math.clamp` exige Java 21+. Alternativa:
  `Math.max(0, Math.min(255, v))`.
