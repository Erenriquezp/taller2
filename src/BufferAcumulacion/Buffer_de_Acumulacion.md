# Buffer de Acumulación

**Fundamentos, Operaciones y Aplicaciones en Gráficos por Computadora**

> Basado en: *Advanced Graphics Programming* — Tom McReynolds & David Blythe

- **Integrantes:** Edison Enriquez, Angelo Silva, Stalin Acurio
- **Semetre:** Noveno
- **Curso:** 001
- **Materia:** Taller II
- **Computación — 2026**

---

## 1. ¿Qué es el Buffer de Acumulación?

El buffer de acumulación es un framebuffer fuera de pantalla (off-screen) especial que forma parte
del pipeline de rasterización. Su propósito principal es combinar múltiples imágenes renderizadas
con alta precisión para producir efectos visuales de calidad superior como antialiasing, motion
blur, profundidad de campo y suavizado de sombras.

A diferencia del color buffer normal que almacena el fotograma actual visible, el buffer de
acumulación actúa como un "sumador" de imágenes: permite ir acumulando (promediando
ponderadamente) varios renders, y al final copiar el resultado al color buffer para mostrarlo en
pantalla.

### 1.1 Diferencia con el Color Buffer Ordinario

Una de las características más importantes del buffer de acumulación es su mayor precisión
numérica y su rango de valores extendido. Esto se resume en la siguiente comparación:

| Aspecto | Color Buffer Normal | Buffer de Acumulación |
| --- | --- | --- |
| Rango de valores | [0, 1] | [-1, 1] |
| Precisión por componente | 8 bits (típico) | 16 bits o más |
| Soporte de valores negativos | No | Sí |
| Aceleración hardware | Alta | Limitada en hardware de bajo costo |
| Operaciones soportadas | Blend, lógicas, etc. | LOAD, ACCUM, ADD, MULT, RETURN |

---

## 2. ¿Cómo Funciona Internamente?

El buffer de acumulación NO recibe geometría directamente. El pipeline de renderizado no puede
dibujar sobre él. Solo acepta como entrada el contenido del color buffer del framebuffer. El flujo de
trabajo es siempre el mismo:

- Se renderiza la escena normalmente al color buffer.
- Se transfiere el color buffer al buffer de acumulación usando una operación (LOAD o
  ACCUM).
- Se repite este proceso para cada variación de la escena (distintos ángulos, tiempos, jitters
  de cámara, etc.).
- Al finalizar todas las acumulaciones, se copia el buffer de acumulación de vuelta al color
  buffer con GL_RETURN para visualizar el resultado.

Esta limitación de acceso, que puede parecer restrictiva, tiene una consecuencia importante: no
es posible enmascarar o reemplazar parcialmente una imagen usando el buffer de profundidad
(depth) o el stencil, ya que estos no participan en las operaciones de acumulación. El buffer se
piensa mejor como un espacio de alta precisión para escalar, combinar y fijar (clamp) imágenes de
color.

---

## 3. Las Cinco Operaciones del Buffer de Acumulación

Todas las operaciones sobre el buffer de acumulación se controlan mediante una única función
que recibe como parámetros la constante de operación y un valor escalar. A continuación, se
detallan las cinco operaciones disponibles y cómo replicar su comportamiento recorriendo los
píxeles manualmente (enfoque que usa tu profesor):

| Operación (Constante) | Descripción | Efecto en el Buffer | Uso Común |
| --- | --- | --- | --- |
| GL_LOAD | Carga una nueva imagen en el buffer de acumulación reemplazando su contenido actual. | Reemplaza el contenido existente con la imagen escalada. | Primer paso de cualquier secuencia. Inicializa la acumulación. |
| GL_ACCUM | Escala la imagen del color buffer y la suma al contenido actual del buffer de acumulación. | Acumula la imagen ponderada encima de lo ya almacenado. | Antialiasing, motion blur, profundidad de campo — el loop principal. |
| GL_ADD | Agrega un valor de sesgo (bias) constante a todos los píxeles del buffer de acumulación. | Desplaza el rango de valores del buffer sumando una constante. | Ajuste de brillo global, corrección de rango de valores. |
| GL_MULT | Multiplica todos los píxeles del buffer de acumulación por un factor constante. | Escala el contenido actual del buffer sin modificar el color buffer. | Reducción de intensidad acumulada, efectos de desvanecimiento. |
| GL_RETURN | Copia el contenido del buffer de acumulación de regreso al color buffer, escalando y fijando los valores al rango [0,1]. | Transfiere el resultado final al framebuffer visible. | Último paso de cualquier secuencia: mostrar el resultado final. |

### 3.1 GL_LOAD — Cargar una imagen

Esta operación reemplaza completamente el contenido del buffer de acumulación con la imagen
del color buffer multiplicada por un factor escalar. Es la operación de inicio: establece el estado
base del buffer antes de comenzar a acumular.

Comportamiento matemático por píxel:

```text
acum[x][y] = color_buffer[x][y] * factor
```

Implementación pixel a pixel (sin API de acumulación):

```java
for (int y = 0; y < alto; y++) {
    for (int x = 0; x < ancho; x++) {
        acum[y][x].r = imagen_actual[y][x].r * factor;
        acum[y][x].g = imagen_actual[y][x].g * factor;
        acum[y][x].b = imagen_actual[y][x].b * factor;
    }
}
```

Uso típico: Es siempre el primer paso de una secuencia de acumulación. Se usa con factor = 1.0
para cargar la imagen sin escalar, o con factor < 1.0 si la primera imagen tendrá menos peso que
las siguientes.

### 3.2 GL_ACCUM — Acumular (sumar ponderada)

Esta es la operación central del buffer. Toma la imagen actual del color buffer, la escala por un
factor y la SUMA al contenido ya existente en el buffer de acumulación. Se usa en el ciclo de
acumulación, llamándola N veces con factor = 1/N para obtener un promedio simple de N
imágenes.

Comportamiento matemático por píxel:

```text
acum[x][y] += color_buffer[x][y] * factor
```

Implementación pixel a pixel:

```java
for (int y = 0; y < alto; y++) {
    for (int x = 0; x < ancho; x++) {
        acum[y][x].r += imagen_actual[y][x].r * factor;
        acum[y][x].g += imagen_actual[y][x].g * factor;
        acum[y][x].b += imagen_actual[y][x].b * factor;
    }
}
```

Si se van a acumular N = 8 renders, el factor de cada llamada sería 1.0/8 = 0.125. La suma final
de los 8 pesos dará 1.0, produciendo un promedio correcto.

### 3.3 GL_ADD — Agregar sesgo (bias)

A diferencia de GL_ACCUM, esta operación NO lee el color buffer. Simplemente agrega un valor
constante (bias) a todos los píxeles del buffer de acumulación. Su propósito es desplazar el rango
de valores del buffer, por ejemplo para trasladar valores negativos al rango visible.

Comportamiento matemático por píxel:

```text
acum[x][y] += valor_constante
```

Implementación pixel a pixel:

```java
for (int y = 0; y < alto; y++) {
    for (int x = 0; x < ancho; x++) {
        acum[y][x].r += bias;
        acum[y][x].g += bias;
        acum[y][x].b += bias;
    }
}
```

Uso típico: Cuando se trabaja con valores en el rango [-1, 1] del buffer de acumulación y se quiere
llevarlos al rango [0, 1] antes de retornarlos al color buffer, se puede agregar un bias de 0.5 para
centrar los valores.

### 3.4 GL_MULT — Multiplicar (escalar) el buffer

Esta operación multiplica cada píxel ya existente en el buffer de acumulación por un factor
constante. Tampoco lee el color buffer; opera únicamente sobre lo que ya está almacenado en el
buffer. Es útil para reducir el peso de las imágenes anteriores o para implementar efectos de
desvanecimiento progresivo.

Comportamiento matemático por píxel:

```text
acum[x][y] *= factor
```

Implementación pixel a pixel:

```java
for (int y = 0; y < alto; y++) {
    for (int x = 0; x < ancho; x++) {
        acum[y][x].r *= factor;
        acum[y][x].g *= factor;
        acum[y][x].b *= factor;
    }
}
```

Uso típico: En efectos de trail (estela) o suavizado temporal donde las imágenes más antiguas
deben tener menor peso que las más recientes. Ejemplo: primero GL_MULT(0.8) para «debilitar»
lo anterior, luego GL_ACCUM para agregar el frame actual.

### 3.5 GL_RETURN — Devolver al color buffer

Esta es la operación final en cualquier secuencia de acumulación. Toma el contenido del buffer de
acumulación, lo escala por el factor dado, fija (clampea) los valores resultantes al rango [0, 1], y
los copia de vuelta al color buffer del framebuffer para que puedan visualizarse en pantalla.

Comportamiento matemático por píxel:

```text
color_buffer[x][y] = clamp(acum[x][y] * factor, 0.0, 1.0)
```

Implementación pixel a pixel:

```java
for (int y = 0; y < alto; y++) {
    for (int x = 0; x < ancho; x++) {
        color[y][x].r = clamp(acum[y][x].r * factor, 0.0f, 1.0f);
        color[y][x].g = clamp(acum[y][x].g * factor, 0.0f, 1.0f);
        color[y][x].b = clamp(acum[y][x].b * factor, 0.0f, 1.0f);
    }
}

// Función clamp:
float clamp(float val, float min, float max) {
    if (val < min) return min;
    if (val > max) return max;
    return val;
}
```

---

## 4. Aplicaciones Principales del Buffer de Acumulación

### 4.1 Antialiasing por Supersampling (Suavizado de Bordes)

El antialiasing es quizás la aplicación más clásica del buffer de acumulación. La idea es renderizar
la escena varias veces con la cámara (o la proyección) ligeramente desplazada en subpíxeles
(jitter), acumular todas las versiones con peso 1/N, y el promedio resultante suaviza los bordes
dentados.

El proceso paso a paso es:

- Definir N posiciones de subpíxel (jitter values). Por ejemplo, para N=4: {(0.375, 0.25),
  (0.125, 0.75), (0.875, 0.25), (0.625, 0.75)}.
- Renderizar la escena completa con la proyección desplazada por el primer valor de jitter.
  Cargar con GL_LOAD(1/N).
- Para cada jitter restante: desplazar la proyección, renderizar y acumular con
  GL_ACCUM(1/N).
- Al finalizar, ejecutar GL_RETURN(1.0) para mostrar el resultado promediado.

El desplazamiento de subpíxel se aplica modificando la matriz de proyección. Para una proyección
ortográfica:

```text
// Desplazamiento en coordenadas de ventana a coordenadas ojo: //
dx_ojo = dx_pixel * (right - left) / ancho_viewport // dy_ojo = dy_pixel * (top -
bottom) / alto_viewport
// // La proyección queda: //
glOrtho(left - dx, right - dx, top - dy, bottom - dy, near, far);
```

### 4.2 Motion Blur (Desenfoque de Movimiento)

El motion blur simula el efecto de una cámara fotográfica con velocidad de obturación baja: los
objetos en movimiento aparecen borrosos en la dirección de su desplazamiento. El buffer de
acumulación permite implementarlo renderizando la escena en varios instantes de tiempo dentro
de un mismo frame y promediando los resultados.

Si la animación corre a 30 fps y se quieren 10 muestras temporales por frame, se renderizan las
posiciones del objeto en los instantes t-4x, t-3x, ... t ... t+5x (donde x = 1/300 seg) y se acumulan
con peso 1/10 cada una.

Optimización para escenas con un objeto móvil sobre fondo estático:

- Renderizar la escena SIN el objeto móvil una sola vez. Cargarlo con GL_LOAD(1.0).: Paso
  1
- En un loop de N iteraciones: posicionar el objeto en su posición para ese instante de
  tiempo, renderizarlo (solo él, sobre fondo negro), y acumularlo con GL_ACCUM(1.0/N).:
  Paso 2
- Copiar el resultado final al color buffer con GL_RETURN(1.0).: Paso 3

Esta optimización evita re-renderizar los objetos estáticos N veces, reduciendo significativamente
el costo computacional.

### 4.3 Profundidad de Campo (Depth of Field)

El efecto de profundidad de campo simula el comportamiento de una lente de cámara real: objetos
a cierta distancia (el plano focal) aparecen nítidos, mientras que los que están más cerca o más
lejos aparecen desenfocados. Se implementa renderizando la escena desde múltiples posiciones
de cámara ligeramente diferentes, todas apuntando al mismo punto focal.

El proceso consiste en desplazar el punto de vista lateralmente en pequeños incrementos
alrededor de la posición original, manteniendo el punto de convergencia (plano focal) fijo. Objetos
en ese plano se proyectarán al mismo lugar en todas las vistas y aparecerán nítidos. Objetos fuera
del plano focal se proyectarán a posiciones ligeramente distintas en cada render, y el promedio los
hará ver borrosos.

La cantidad de desenfoque de cada objeto depende de su distancia al plano focal y del tamaño del
desplazamiento lateral utilizado.

### 4.4 Suavizado de Sombras y Penumbra

Las sombras duras (hard shadows) se producen cuando se usa una luz puntual. Para simular
sombras suaves (soft shadows) con penumbra —como las que produce una fuente de luz de
área— se puede usar el buffer de acumulación renderizando múltiples veces la escena con la
fuente de luz desplazada en pequeños incrementos dentro del área de la fuente. El promedio de
las sombras produce el efecto de penumbra.

Es el mismo principio del antialiasing pero aplicado a la posición de la luz: distintas posiciones de
luz producen distintas sombras duras, y el promedio de muchas sombras duras da como resultado
una sombra suave.

### 4.5 Composición de Imágenes de Alta Precisión

El rango extendido [-1, 1] y la mayor profundidad de bits del buffer de acumulación lo hacen
valioso para operaciones de composición (blending) donde la precisión del color buffer normal de
8 bits por componente no es suficiente.

Sin embargo, el libro aclara que la capacidad del buffer de acumulación para reducir errores de
composición es limitada: aunque acumula con mayor precisión, solo soporta multiplicación por un
escalar constante, no por un peso por píxel (como el canal alpha). Por lo tanto, las operaciones
que requieren mezcla ponderada por alpha pixel a pixel deben hacerse igualmente en el color
buffer con blending normal; solo el resultado final puede acumularse.

El buffer de acumulación es más útil para operaciones tipo multiplicar-sumar por constante, donde
el color buffer normal no tiene suficiente rango dinámico para representar simultáneamente el
resultado final y un solo término de entrada pequeño.

---

## 5. Precisión, Errores y Consideraciones Importantes

### 5.1 Errores de Cuantización

Cuando se compone un número grande de imágenes utilizando el color buffer normal de 8 bits, los
errores de cuantización se acumulan rápidamente. Cada operación de multiplicación en punto fijo
de 8 bits introduce aproximadamente ½ bit de error. Con componentes de 8 bits, eso se traduce
en ~0.4% de error por operación de composición.

Después de 10 operaciones: ~4% de error. Después de 100 operaciones: ~40% de error
acumulado. El buffer de acumulación con 16 bits por componente reduce este error a ~0.025% por
operación, haciéndolo viable para secuencias largas de acumulación.

### 5.2 Errores de Gamma

Un error frecuente en composición es mezclar imágenes que han sido gamma-corregidas sin
antes convertirlas al espacio lineal. Las operaciones de blending son lineales y asumen que los
operandos tienen una relación lineal. Si las imágenes están gamma-corregidas (como la mayoría
de las imágenes JPG/PNG guardadas para pantalla), mezclarlas directamente produce resultados
incorrectos.

La secuencia correcta es: convertir de gamma a lineal → componer/acumular → aplicar gamma de
nuevo. El error en el peor caso (mezclar blanco con negro) puede superar el 25% si se omiten las
conversiones.

### 5.3 Limitaciones de Hardware

El buffer de acumulación frecuentemente no está acelerado en hardware de gama baja, porque
sus operaciones requieren multiplicadores y sumadores más grandes para implementar la
aritmética de mayor precisión. Esto puede hacerlo muy lento en GPUs de costo reducido.

Estrategias para manejar esto:

- Usar el buffer de acumulación para generación de imágenes 'offline' de alta calidad
  (texturas, mapas de iluminación) que luego se usen sin costo en tiempo de ejecución.
- En tiempo real, preferir técnicas como multisampling o implementaciones basadas en
  texturas flotantes que sí cuentan con aceleración hardware moderna.
- Minimizar el número de acumulaciones al mínimo necesario para la calidad requerida.

---

## 6. Resumen y Conceptos Clave

El buffer de acumulación es una herramienta fundamental para producir imágenes de alta calidad
en gráficos por computadora. Los puntos más importantes a recordar son:

- Es un framebuffer off-screen de alta precisión con rango [-1, 1] por componente y
  típicamente 16 bits o más de profundidad de color.
- Solo recibe datos del color buffer. No hay renderizado directo sobre él.
- Sus cinco operaciones son: GL_LOAD (inicializar), GL_ACCUM (acumular), GL_ADD
  (sesgo), GL_MULT (escalar), GL_RETURN (mostrar resultado).
- Sus aplicaciones principales son: antialiasing por supersampling, motion blur, profundidad
  de campo y sombras suaves.
- Puede ser lento en hardware de gama baja por la mayor complejidad de sus operaciones
  aritméticas.
- Es ideal para renderizado offline de alta calidad, aunque técnicas modernas como
  multisampling son preferibles en tiempo real.
- El clamping al rango [0, 1] ocurre automáticamente en GL_RETURN; al implementarlo
  manualmente, es responsabilidad del programador aplicarlo.

— Fin del documento —
