# Ecualización de Histograma - Documentación

## Descripción general

Aplicación de escritorio en Java que permite cargar una imagen, visualizar su histograma RGB y aplicar ecualización de histograma de forma dinámica mediante un slider. Además detecta y señala zonas sobreexpuestas (quemadas) y subexpuestas (oscuras) para ayudar a evaluar la calidad de la exposición.

---

## Funcionalidades de la aplicación

### 1. Cargar imagen

El botón **"Cargar Imagen"** abre un `JFileChooser` que permite seleccionar archivos PNG, JPG, JPEG, BMP o GIF. La imagen seleccionada se muestra en el panel central, escalada proporcionalmente para caber en la ventana.

### 2. Histograma RGB

En el panel derecho se dibuja el histograma de la imagen procesada en tiempo real. Se muestran tres curvas superpuestas con transparencia:

- **Rojo** — canal R
- **Verde** — canal G
- **Azul** — canal B

El histograma se normaliza usando el valor máximo global de los tres canales para que las curvas sean comparables.

### 3. Slider de ecualización

El slider inferior controla la **intensidad de la ecualización** de 0% a 100%:

- **0%** → imagen original sin modificar
- **100%** → imagen completamente ecualizada
- **Valores intermedios** → mezcla lineal entre la original y la ecualizada

Al mover el slider, la imagen y el histograma se actualizan dinámicamente.

### 4. Detección de zonas quemadas y oscuras

Dos checkboxes permiten activar la detección visual:

- **"Marcar zonas quemadas"** → pinta en **rojo** los pixeles donde R, G y B son todos ≥ 240 (sobreexposición)
- **"Marcar zonas oscuras"** → pinta en **azul** los pixeles donde R, G y B son todos ≤ 15 (subexposición)

### 5. Barra de estado

En la parte inferior se muestra información en tiempo real:

- Porcentaje de ecualización actual
- Resolución de la imagen (ancho × alto)
- Porcentaje de pixeles quemados y oscuros
- Diagnóstico automático: si alguno supera el 5%, se muestra una advertencia ⚠; si no, se muestra ✓ Exposición adecuada

### 6. Guardar resultado

El botón **"Guardar Resultado"** permite exportar la imagen procesada como archivo PNG.

---

## Lógica de procesamiento (ProcesadorHistograma.java)

Esta clase contiene la lógica pura del procesamiento de la imagen. A continuación se explican sus métodos clave:

### 1. `ecualizar(original, porcentaje)`
Aplica el algoritmo estándar de ecualización para mejorar el contraste de forma independiente por canal (R, G, B):
- **Contar frecuencias:** Recorre la imagen pixel por pixel y cuenta cuántas veces aparece cada nivel de color (0 a 255) en cada canal.
- **Calcular distribución acumulada (CDF):** Suma las frecuencias de manera acumulada para saber la distribución del brillo.
- **Mapear intensidades:** Aplica la fórmula `(cdf - cdfMin) / (total - cdfMin) * 255` para estirar los contrastes de forma uniforme.
- **Mezclar:** Combina linealmente la imagen original y la versión completamente ecualizada usando el porcentaje seleccionado.

### 2. `mezclar(original, ecualizada, porcentaje)` (Optimizado)
Mezcla la imagen original y la ya ecualizada de forma extremadamente rápida:
- **Lectura en bloque:** Usa `getRGB` y `setRGB` con arreglos de enteros (`int[]`), evitando leer pixel por pixel de forma individual.
- **Interpolar:** Realiza un promedio ponderado de los colores de ambas imágenes según el porcentaje del slider. Al pre-calcular la ecualización al cargar la imagen, este método es el único que se ejecuta mientras se arrastra el slider, logrando que la interfaz sea fluida.

### 3. `calcularHistograma(imagen)`
Cuenta las frecuencias de brillo para el dibujo de la gráfica:
- Devuelve una matriz `int[3][256]` que representa la cantidad de pixeles para cada uno de los 256 niveles de Rojo, Verde y Azul.

### 4. `marcarZonas(imagen, quemadas, oscuras)`
Detecta problemas de iluminación pintando los pixeles afectados:
- **Quemadas:** Si los canales R, G y B del pixel son $\ge 240$, se pinta de rojo.
- **Oscuras:** Si los canales R, G y B del pixel son $\le 15$, se pinta de azul.

### 5. `diagnosticar(imagen)`
Calcula estadísticas de exposición de la imagen:
- Obtiene el porcentaje de pixeles quemados y oscuros del total.
- Retorna un objeto `Diagnostico` con un mensaje indicando si la exposición es adecuada o si la imagen está sobreexpuesta/subexpuesta (si alguna zona crítica supera el 5% del total).

