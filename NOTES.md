# NOTES DE ESTUDIO - PROYECTO TALLER 2

Objetivo: memorizar y entender el codigo del proyecto de forma simple.

## 0) Mapa rapido del proyecto (que hace cada bloque)

- `src/tarea1/FiltrosImagen.java`: filtros base (gris, negativo, brillo, HSV, alpha).
- `src/tarea1/Filtos.java`: pruebas manuales de filtros (mucho codigo comentado).
- `src/tarea1/GeneradorDegradados.java`: crea degradados lineales y radial.
- `src/tarea1/TareaFiltrosAvanzados.java`: 6 filtros avanzados (version antigua con `package taller`).
- `src/tarea1/NOTES_FILTROS.md`: notas teoricas de filtros avanzados.

- `src/tarea2/TareaFiltrosAvanzados.java`: misma logica de filtros avanzados, version final en `package tarea2`.
- `src/tarea2/Filtos.java`: practica de combinacion (recorte de bits + HSV + alpha).
- `src/tarea2/Conclusiones.txt`: conclusiones visuales de los filtros.

- `src/taller/Color.java`: recoloracion por luminosidad.
- `src/taller/Convolucion.java`: convolucion manual 3x3.
- `src/taller/Convolucion9.java`: comparacion blur 9x9 vs blur 3x3 repetido 9 veces.
- `src/taller/ConvulucionOp.java`: convolucion con API de Java (`ConvolveOp`).
- `src/taller/images/Kernels.java`: kernels predefinidos (normal, enfoque, blur, bordes, etc).

- `src/practice/Plantilla.java`: plantilla base minima para memorizar el flujo completo.
- `src/practice/Histograma.java`: histograma RGB con conteo por canal y dibujo con `Graphics2D`.
- `src/practice/MatrizColores.java`: matriz de color tipo sepia/grises.
- `src/practice/Blending.java`: mezcla de 2 imagenes con peso `alpha`.
- `src/practice/TripleBlending.java`: mezcla de 3 imagenes con pesos fijos.

- `src/tarea3/reduccion/RBitsBinario.java`: reducir a 4 bits y estirar en binario.
- `src/tarea3/reduccion/RBitsDecimal.java`: reducir a 4 bits y estirar en decimal.
- `src/tarea3/reduccion/RBitsHexadecimal.java`: reducir a 4 bits y estirar en hexadecimal.
- `src/tarea3/reduccion/ReduccionYEstiramiento.java`: version unificada de los 3 metodos.

---

## 1) Base comun que se repite en casi todo el proyecto

### 1.1 Flujo general de procesamiento
1. Leer imagen con `ImageIO.read(...)`.
2. Crear imagen de salida (`BufferedImage`) con tipo RGB o ARGB.
3. Recorrer pixeles con doble `for (y) for (x)`.
4. Extraer canales con bits.
5. Aplicar formula del filtro.
6. Reconstruir pixel y guardarlo con `setRGB`.
7. Escribir archivo con `ImageIO.write(...)`.

### 1.2 Extraccion y empaquetado ARGB/RGB (esto SI o SI)

Para extraer:
```java
int a = (pixel >> 24) & 0xFF;
int r = (pixel >> 16) & 0xFF;
int g = (pixel >> 8) & 0xFF;
int b = pixel & 0xFF;
```

Para reconstruir:
```java
int pixelNuevoARGB = (a << 24) | (r << 16) | (g << 8) | b;
int pixelNuevoRGB  = (r << 16) | (g << 8) | b;
```

Regla de memoria:
- `>>` desplaza para traer el canal.
- `& 0xFF` limpia y deja solo 8 bits.
- `|` une canales en un entero de 32 bits.

### 1.3 Clamping (evitar salir de rango)

Siempre mantener canales entre 0 y 255:
```java
// v = Math.min(255, Math.max(0, v));
```

Sin clamping hay colores rotos o overflow.

---

## 2) Nivel basico: filtros directos por pixel

Archivos clave: `src/tarea1/FiltrosImagen.java`, `src/tarea1/Filtos.java`, `src/tarea2/Filtos.java`.

### 2.1 Escala de grises
Formula usada:
```text
gris = (r + g + b) / 3
```
Luego:
```text
(r,g,b) = (gris, gris, gris)
```

### 2.2 Negativo
Formula:
```text
r' = 255 - r, g' = 255 - g, b' = 255 - b
```

### 2.3 Brillo
Formula:
```text
r' = clamp(r + brillo)
g' = clamp(g + brillo)
b' = clamp(b + brillo)
```

### 2.4 HSV (saturacion y valor)
Se convierte RGB -> HSV, se multiplican factores, y se regresa a RGB.

Idea clave de examen:
- `factorS > 1`: colores mas intensos.
- `factorV < 1`: imagen mas oscura.

### 2.5 Transparencia (alpha)
Formula:
```text
a' = min(255, a * factorAlfa)
```
Necesita `TYPE_INT_ARGB` para no perder alpha.

---

## 3) Nivel intermedio: filtros avanzados de tarea

Archivos clave:
- `src/tarea2/TareaFiltrosAvanzados.java` (version principal)
- `src/tarea1/TareaFiltrosAvanzados.java` (misma logica, otra ruta/paquete)

## 3.1 Funcion central: cuantizacion

Metodo: `cuantizarColor(int valor, int n)`

Logica:
1. `step = 255.0 / (n - 1)`
2. `nivel = round(valor / step)`
3. `resultado = round(nivel * step)`

Interpretacion:
- `n=2` -> casi solo negro/blanco por canal.
- `n` grande -> mas parecido al original.

Ejemplo rapido con `n=4`:
- `step = 85`
- `valor=100` -> nivel 1 -> resultado 85
- `valor=200` -> nivel 2 -> resultado 170

### 3.2 Los 6 efectos que debes saber explicar

1) Vidrio esmerilado (en este codigo: alpha segun brillo)
- Calcula `brillo=(r+g+b)/3`
- Mapea alpha: de 50 a 255 segun brillo
- Resultado: zonas oscuras mas transparentes

2) Desvanecimiento circular
- Distancia al centro: `sqrt((x-cx)^2 + (y-cy)^2)`
- Factor: `1 - dist/distMax`
- Alpha: `255 * factor`

3) Retro 1 (cuantizacion total RGB)
- Cuantiza R, G y B con `n` niveles
- Efecto posterizado (estilo retro)

4) Retro 2 (cuantizacion parcial por canal)
- Permite decidir que canales cuantizar
- En este codigo, `b` se inicia en `0`; si `afectarB=false`, azul queda en 0
- Resultado: dominante amarilla/verdosa (falta azul)

5) Blanco y negro por umbral
- `gris=(r+g+b)/3`
- Si `gris > 127` -> 255; si no -> 0

6) Grises cuantizado
- Primero pasa a gris
- Luego cuantiza ese gris a `n` niveles

---

## 4) Nivel intermedio-alto: degradados y recoloracion

### 4.1 Degradados (`src/tarea1/GeneradorDegradados.java`)

Tipos generados:
- Izquierda -> derecha
- Derecha -> izquierda
- Arriba -> abajo
- Abajo -> arriba
- Radial (centro -> esquinas)

Patron comun:
- Crear `factorBlanco` entre 0 y 1 segun posicion.
- Usar `r=255*factor`, `g=255*factor`, `b=255`.

Formula radial:
```text
factor = distanciaActual / distanciaMax
```

### 4.2 Recoloracion (`src/taller/Color.java`)

Idea:
1. Calcular luminosidad (BT.709):
```text
lum = 0.2126*r + 0.7152*g + 0.0722*b
```
2. Escalar esa luminosidad al color objetivo:
```text
nuevoR = lum * tonoR / 255
nuevoG = lum * tonoG / 255
nuevoB = lum * tonoB / 255
```

Traduccion simple:
- Mantiene luces/sombras originales.
- Cambia el "tinte" global a azul, rojo, verde o lila.

### 4.3 Nuevos ejemplos agregados en `practice`

#### Histograma RGB (`src/practice/Histograma.java`)
- Cuenta frecuencias con tres arreglos de 256 posiciones.
- Dibuja líneas con `Graphics2D`.
- Normaliza usando el mayor valor de los tres canales.

#### Matriz de colores (`src/practice/MatrizColores.java`)
- Cada canal de salida es una suma ponderada de `r`, `g` y `b`.
- Ejemplo sepia:
```text
nr = 0.393r + 0.769g + 0.189b
ng = 0.349r + 0.686g + 0.168b
nb = 0.272r + 0.534g + 0.131b
```

#### Blending (`src/practice/Blending.java`)
- Mezcla 2 imágenes con un peso `alpha` para el fondo.
```text
resultado = (1 - alpha) * original + alpha * fondo
```

#### Triple blending (`src/practice/TripleBlending.java`)
- Mezcla 3 imágenes usando pesos fijos.
```text
resultado = img1 * 0.5 + img2 * 0.3 + img3 * 0.2
```

---

## 5) Nivel alto: convolucion

Archivos:
- `src/taller/Convolucion.java`
- `src/taller/Convolucion9.java`
- `src/taller/ConvulucionOp.java`
- `src/taller/images/Kernels.java`

### 5.1 Que es convolucion (en una linea)
Es recalcular cada pixel usando una suma ponderada de sus vecinos con una mascara (kernel).

### 5.2 Ecuacion mental
```text
nuevoPixel = suma(vecino(i,j) * kernel(i,j))
```
(se aplica por separado a R, G y B)

### 5.3 Kernels que debes memorizar

- Normal: centro=1, no cambia imagen.
- Blur 3x3: todos `1/9`.
- Blur 9x9: todos `1/81`.
- Enfoque (sharpen): centro alto, vecinos negativos.
- Bordes: centro positivo, vecinos negativos.

### 5.4 Diferencias entre implementaciones

- `Convolucion.java`: manual, 3x3, usa blur promedio.
- `Convolucion9.java`:
  - compara blur directo 9x9
  - contra blur 3x3 repetido 9 veces.
- `ConvulucionOp.java`: usa libreria Java (`Kernel`, `ConvolveOp`) con `EDGE_NO_OP`.

### 5.5 Detalles de examen importantes

- Bordes: casi siempre se evitan (for inicia en 1, termina en alto-1, etc).
- Si no se rellena borde, puede quedar negro o sin cambio en extremos.
- Repetir blur pequeno muchas veces aproxima un blur mas grande.

---

## 6) Nivel alto: reduccion y estiramiento de bits

Archivos:
- `src/tarea3/reduccion/RBitsBinario.java`
- `src/tarea3/reduccion/RBitsDecimal.java`
- `src/tarea3/reduccion/RBitsHexadecimal.java`
- `src/tarea3/reduccion/ReduccionYEstiramiento.java`

## 6.1 Flujo comun
1. Extraer ARGB.
2. Reducir a 4 bits: `canal >>= 4`.
3. Estirar de vuelta a 8 bits con un metodo.
4. Guardar imagen.

### 6.2 3 metodos de estiramiento

1) Binario
```text
v = (v << 4) | v
```

2) Decimal
```text
v = (v * 255) / 15
```

3) Hexadecimal
```text
v = (v << 4) | (v & 0x0F)
```

Punto clave:
- Binario y hexadecimal aqui terminan en la misma idea (replicar nibble).
- Decimal usa regla de 3.
- Visualmente, las diferencias suelen ser minimas.

### 6.3 Version recomendada para explicar en examen

Usar `ReduccionYEstiramiento.java` porque:
- unifica todo en `procesarFiltro(...)`
- evita repetir codigo
- deja clara la comparacion entre metodos

---

## 7) Preguntas tipicas de examen (respuesta corta)

### 7.1 Por que usar `TYPE_INT_ARGB`?
Porque se necesita conservar transparencia (canal alpha).

### 7.2 Por que usar `& 0xFF`?
Para aislar solo los ultimos 8 bits del canal.

### 7.3 Que pasa si no hago clamping?
Puede haber valores fuera de 0..255 y colores incorrectos.

### 7.4 Que hace un kernel blur 3x3 de `1/9`?
Promedia el pixel con sus 8 vecinos, suavizando detalles.

### 7.5 Que es cuantizacion?
Reducir niveles posibles de color para perder precision y simplificar paleta.

### 7.6 Diferencia entre gris y blanco/negro?
- Gris: muchos niveles intermedios.
- Blanco/negro: solo 2 niveles por umbral.

---
## 8) Checklist final de memorizacion (repaso rapido)

- [ ] Se extraer/armar ARGB con bits.
- [ ] Se explicar clamping y por que evita errores.
- [ ] Se explicar gris, negativo, brillo, HSV y alpha.
- [ ] Se explicar cuantizacion (`step`, `nivel`, `round`).
- [ ] Se explicar los 6 filtros avanzados de tarea2.
- [ ] Se explicar degradados lineales y radial.
- [ ] Se explicar histograma RGB, matrices de color y blending.
- [ ] Se definir convolucion + kernels comunes.
- [ ] Se comparar blur grande vs blur pequeno repetido.
- [ ] Se explicar reduccion a 4 bits y estiramiento (binario/decimal/hex).
- [ ] Se mencionar 3-5 detalles reales del codigo (rutas, N64, canal azul, etc).

Si dominas este checklist, ya tienes una base fuerte para el examen.

