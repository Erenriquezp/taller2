## 1. Explicación de los Filtros (Resultado Esperado)

| Filtro | ¿Qué hace en palabras sencillas? | Resultado Visual Esperado |
| :--- | :--- | :--- |
| **1. Vidrio Esmerilado** | Analiza qué tan "claro" es un punto. [cite_start]Si es brillante, lo deja opaco; si es oscuro, lo vuelve casi transparente[cite: 87, 88]. | Verás la imagen original, pero las sombras parecerán "agujeros" transparentes que dejan ver lo que hay detrás. |
| **2. Desvanecimiento** | Mide qué tan lejos está cada píxel del centro. [cite_start]A más distancia, más transparente se vuelve[cite: 174, 179]. | Un efecto de "viñeta" donde el centro es sólido y las esquinas desaparecen suavemente. |
| **3. Retro 1 (N colores)** | [cite_start]Toma los millones de colores originales y los "obliga" a convertirse en solo unos pocos niveles por canal[cite: 43, 83]. | La imagen se verá "pixelada" en sus colores (posterizada), como un juego de consola de los años 80 o 90. |
| **4. Retro 2 (Canales)** | Igual que el anterior, pero solo "daña" los colores de ciertos canales (ej. solo el Rojo y Verde), dejando el Azul normal. | Un efecto artístico extraño donde algunos colores se ven en bloques y otros mantienen sus gradientes suaves. |
| **5. Blanco y Negro** | Es un filtro binario. [cite_start]Si el promedio de color supera la mitad (127), se vuelve blanco; si no, negro puro[cite: 176]. | Una imagen de alto contraste sin grises, similar a una fotocopia vieja o un stencil. |
| **6. Grises Cuantizado** | Primero quita el color y luego reduce los niveles de gris a solo $N$ opciones. | Verás una imagen en blanco y negro, pero en lugar de transiciones suaves, verás "bandas" o escalones de gris. |

---

## 2. Partes Complicadas del Código (Análisis Técnico)

### A. La Matemática de la Cuantización (`cuantizarColor`)
Esta es la parte más abstracta. [cite_start]El objetivo es reducir 256 niveles (0-255) a solo $N$ niveles[cite: 174, 176].

**La fórmula lógica:**
1.  **El Salto (`step`):** Dividimos el rango total (255) entre los espacios disponibles ($N-1$).
    * *Ejemplo ($N=2$):* $255 / (2-1) = 255$. Los saltos son de 255 en 255.
2.  **El Nivel:** Dividimos el color original para ver en qué "escalón" cae y redondeamos.
3.  **El Resultado:** Multiplicamos el escalón por el salto para volver al rango 0-255.

**Ejemplo con $N=4$ colores (Saltos de 85):**
* Si el Rojo es **100**:
    1.  $100 / 85 = 1.17$ $\rightarrow$ se redondea al nivel **1**.
    2.  $1 \times 85 = 85$. El nuevo Rojo es **85**.
* Si el Rojo es **200**:
    1.  $200 / 85 = 2.35$ $\rightarrow$ se redondea al nivel **2**.
    2.  $2 \times 85 = 170$. El nuevo Rojo es **170**.



### B. El Mapeo Lineal del Alpha (Vidrio Esmerilado)
[cite_start]Aquí usamos una interpolación lineal simple para que la transparencia ($A$) dependa del brillo[cite: 176].

$$A = A_{min} + \left( \frac{\text{brillo}}{255} \right) \times (A_{max} - A_{min})$$

* **¿Por qué?** Si solo usáramos el brillo como Alpha, los puntos negros serían totalmente invisibles ($A=0$). [cite_start]El código usa un **piso de 50** para que incluso lo más oscuro tenga un toque de visibilidad[cite: 179].

### C. Geometría de Coordenadas (Desvanecimiento Circular)
[cite_start]Para este filtro, tratamos la imagen como un plano cartesiano donde el centro es $(C_x, C_y)$[cite: 179].

1.  **Distancia Euclidiana:** Usamos Pitágoras para saber qué tan lejos está el píxel $(x, y)$ del centro:
    [cite_start]$$d = \sqrt{(x - C_x)^2 + (y - C_y)^2}$$ [cite: 179]
2.  **Factor Inverso:** Como queremos que el centro sea opaco ($A=255$) y las esquinas transparentes ($A=0$), restamos la distancia del total:
    $$\text{factor} = 1.0 - \left( \frac{\text{distancia\_actual}}{\text{distancia\_maxima}} \right)$$



### D. Empaquetado ARGB (32 bits)
[cite_start]Recuerda que para estos filtros usamos `BufferedImage.TYPE_INT_ARGB`[cite: 49]. Al final de cada filtro, "armamos" el píxel usando desplazamientos de bits:
* `a << 24`: Mueve la transparencia al frente.
* `r << 16`: Coloca el rojo.
* `g << 8`: Coloca el verde.
* `b`: Coloca el azul al final.
* [cite_start]`|`: El operador OR une todos estos pedazos en un solo número entero que la tarjeta de video entiende[cite: 43, 109].
