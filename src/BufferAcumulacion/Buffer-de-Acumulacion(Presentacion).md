# Buffer de Acumulación

> Fundamentos, operaciones y aplicaciones en gráficos por computadora, basado en
> *Advanced Graphics Programming* de Tom McReynolds y David Blythe.

**INTEGRANTES:** EDISON ENRIQUEZ, ANGELO SULVA, STALIN ACURIO

*preencoded.png*

---

## ¿Qué es el Buffer de Acumulación?

### Definición

Es un framebuffer off-screen especial del pipeline de rasterización que
combina múltiples imágenes renderizadas con alta precisión para
producir efectos visuales de calidad superior: antialiasing, motion blur,
profundidad de campo y suavizado de sombras.

### ¿Cómo funciona?

Actúa como un «sumador» de imágenes: acumula (promedia
ponderadamente) varios renders y al final copia el resultado al color
buffer para mostrarlo en pantalla. A diferencia del color buffer normal, no
recibe geometría directamente; solo acepta el contenido del color buffer
del framebuffer.

*preencoded.png*

---

## Buffer de Acumulación vs. Color Buffer Normal

La mayor precisión numérica y el rango de valores extendido son las características más importantes del buffer de acumulación.

| Aspecto | Color Buffer Normal | Buffer de Acumulación |
| --- | --- | --- |
| Rango de valores | [0, 1] | [-1, 1] |
| Precisión por componente | 8 bits (típico) | 16 bits o más |
| Soporte de valores negativos | No | Sí (nativo) |
| Aceleración hardware | Alta | Limitada en hardware de bajo costo |
| Operaciones soportadas | Blend, lógicas, etc. | LOAD, ACCUM, ADD, MULT, RETURN |

El soporte nativo de valores negativos en el rango [-1, 1] es exclusivo del buffer de acumulación dentro del framebuffer de OpenGL.

*preencoded.png*

---

## Flujo de Trabajo Interno

El buffer de acumulación no recibe geometría directamente. El pipeline de renderizado no puede dibujar sobre él; solo acepta el contenido del color buffer. Esta
limitación implica que el depth buffer y el stencil no participan en las operaciones de acumulación.

**Renderizar → Transferir → Repetir → Copiar**

El buffer se concibe mejor como un espacio de alta precisión para escalar, combinar y clamping imágenes de color, no como un destino de renderizado directo.

*preencoded.png*

---

## Las Cinco Operaciones del Buffer

1. **GL_LOAD**
   Carga una nueva imagen reemplazando el contenido. Primer paso de cualquier secuencia.
2. **GL_ACCUM**
   Escala la imagen del color buffer y la suma al buffer. Operación central del ciclo de acumulación.
3. **GL_ADD**
   Agrega un valor de sesgo (bias) constante a todos los píxeles. No lee el color buffer.
4. **GL_MULT**
   Multiplica todos los píxeles por un factor constante. Útil para desvanecimiento progresivo.
5. **GL_RETURN**
   Copia el contenido al color buffer, clamping los valores a [0, 1]. Último paso de la secuencia.

*preencoded.png*

---

## GL_LOAD y GL_ACCUM: Las Operaciones Principales

### GL_LOAD — Inicializar

Reemplaza el contenido del buffer con la imagen del color buffer
multiplicada por un factor escalar. Siempre es el primer paso de una
secuencia.

```text
acum[x][y] = color_buffer[x][y] × factor
```

Se usa con factor = 1.0 para cargar sin escalar, o factor < 1.0 si la
primera imagen tendrá menos peso.

### GL_ACCUM — Acumular

Toma la imagen actual, la escala por un factor y la suma al contenido
existente. Se usa en el ciclo principal llamándola N veces con factor =
1/N para obtener un promedio.

```text
acum[x][y] += color_buffer[x][y] × factor
```

Si se acumulan N=8 renders, el factor sería 1.0/8 = 0.125. La suma de los
8 pesos dará 1.0, produciendo un promedio correcto.

*preencoded.png*

---

## GL_ADD, GL_MULT y GL_RETURN

### GL_ADD — Sesgo

Agrega un valor constante (bias) a todos los
píxeles. No lee el color buffer. Útil para
desplazar valores del rango [-1, 1] a [0, 1]
agregando un bias de 0.5.

```text
acum[x][y] += bias
```

### GL_MULT — Escalar

Multiplica cada píxel del buffer por un factor
constante. No lee el color buffer. Útil para
efectos de estela (trail): primero
GL_MULT(0.8) para debilitar lo anterior,
luego GL_ACCUM para agregar el frame
actual.

```text
acum[x][y] *= factor
```

### GL_RETURN — Mostrar

Operación final: escala el contenido,
clampea los valores a [0, 1] y los copia al
color buffer visible.

```text
color_buffer[x][y] = clamp(acum[x][y] × factor, 0.0, 1.0)
```

*preencoded.png*

---

## Aplicaciones: Antialiasing y Motion Blur

### Antialiasing por Supersampling

Se renderiza la escena N veces con la cámara desplazada en subpíxeles (jitter).
Cada render se acumula con peso 1/N y el promedio suaviza los bordes dentados.
El desplazamiento se aplica modificando la matriz de proyección con glOrtho.

### Motion Blur

Simula una cámara con velocidad de obturación baja: se renderizan posiciones
del objeto en múltiples instantes de tiempo dentro de un mismo frame y se
promedian. Optimización clave: renderizar el fondo estático una sola vez con
GL_LOAD(1.0) y acumular solo el objeto móvil N veces con GL_ACCUM(1.0/N),
evitando re-renderizar objetos estáticos.

*preencoded.png*

---

## Aplicaciones: Profundidad de Campo, Sombras y Composición

### Profundidad de Campo

Se renderiza la escena desde múltiples
posiciones de cámara desplazadas lateralmente,
todas apuntando al mismo punto focal. Los
objetos en el plano focal aparecen nítidos; los
fuera de él, borrosos.

### Sombras Suaves (Penumbra)

Se renderiza múltiples veces con la fuente de luz
desplazada dentro del área de la fuente. El
promedio de muchas sombras duras produce
una sombra suave, igual que el antialiasing pero
aplicado a la posición de la luz.

### Composición de Alta Precisión

El rango [-1, 1] y los 16 bits por componente son
valiosos para blending donde 8 bits no es
suficiente. Limitación: solo soporta
multiplicación por escalar constante, no por
peso alpha por píxel.

*preencoded.png*

---

## Referencias

- McReynolds, T. y Blythe, D. (2005). *Advanced Graphics Programming Using OpenGL*. Morgan Kaufmann.
- Shreiner, D. et al. (2013). *OpenGL Programming Guide* (8.ª ed.). Addison-Wesley.
- Porter, T. y Duff, T. (1984). *Compositing Digital Images*. SIGGRAPH '84.
- Oracle. *Class BufferedImage* — Java SE API.
