# CHULETA: Procesamiento de Imágenes en Java

## 📋 PLANTILLA BASE (SE REPITE EN TODO)

```java
File entrada = new File("src/practice/images/original.png");
File salida = new File("src/practice/images/salida_XX.png");

BufferedImage original = ImageIO.read(entrada);
int ancho = original.getWidth();
int alto = original.getHeight();
BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

for (int y = 0; y < alto; y++) {
    for (int x = 0; x < ancho; x++) {
        int pixel = original.getRGB(x, y);
        
        int a = (pixel >> 24) & 0xFF;  // Alpha
        int r = (pixel >> 16) & 0xFF;  // Rojo
        int g = (pixel >> 8) & 0xFF;   // Verde
        int b = pixel & 0xFF;           // Azul
        
        // [AQUI VА TU FORMULA]
        
        int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
        resultado.setRGB(x, y, pixelNuevo);
    }
}

ImageIO.write(resultado, "png", salida);
```

**Clamp obligatorio: `Math.clamp(valor, 0, 255)`** (confina a rango válido)

---

## 🆕 EJEMPLOS NUEVOS EN `practice`

- `Histograma.java`: cuenta frecuencias RGB y dibuja el histograma con `Graphics2D`.
- `MatrizColores.java`: aplica una matriz de color tipo sepia/grises por canal.
- `Blending.java`: mezcla 2 imágenes con un factor `alpha`.
- `TripleBlending.java`: mezcla 3 imágenes con pesos fijos.

---

## 🎨 PLANTILLA DEGRADADOS (SIN IMAGEN)

```java
int ancho = 600, alto = 300;
BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

for (int y = 0; y < alto; y++) {
    for (int x = 0; x < ancho; x++) {
        float t = (float) x / (ancho - 1);  // 0.0 izq -> 1.0 dcha (variar para direcciones)
        
        int r = (int) (t * 255);
        int g = (int) (t * 255);
        int b = 255;
        
        int rgb = (r << 16) | (g << 8) | b;
        salida.setRGB(x, y, rgb);
    }
}
```

**Variables t comunes:**
- Izq→Dcha: `t = (float) x / (ancho - 1)`
- Dcha→Izq: `t = 1f - ((float) x / (ancho - 1))`
- Arriba→Abajo: `t = (float) y / (alto - 1)`
- Abajo→Arriba: `t = 1f - ((float) y / (alto - 1))`
- Radial: `t = 1.0 - (dist / distMax)` donde `dist = sqrt((x-cx)² + (y-cy)²)`

---

## ⚙️ PLANTILLA CONVOLUCIÓN (MATRIZ 3×3)

```java
float[][] matriz = {
    {1f/9f, 1f/9f, 1f/9f},
    {1f/9f, 1f/9f, 1f/9f},
    {1f/9f, 1f/9f, 1f/9f}
};

for (int y = 1; y < alto - 1; y++) {
    for (int x = 1; x < ancho - 1; x++) {
        float sumaR = 0, sumaG = 0, sumaB = 0;
        
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int p = original.getRGB(x + j, y + i);
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                
                sumaR += r * matriz[i + 1][j + 1];
                sumaG += g * matriz[i + 1][j + 1];
                sumaB += b * matriz[i + 1][j + 1];
            }
        }
        
        int nr = clamp((int) sumaR);
        int ng = clamp((int) sumaG);
        int nb = clamp((int) sumaB);
        
        resultado.setRGB(x, y, (nr << 16) | (ng << 8) | nb);
    }
}
```

**Alternativa CONVOLVEOP (usando API de Java):**
```java
float[] matrizKernel = {1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9, 1f/9};
Kernel kernel = new Kernel(3, 3, matrizKernel);
ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
BufferedImage resultado = op.filter(original, null);
```

---

## 📝 EJERCICIOS (FORMULA + SINTAXIS UNICA)

### 1. **NEGATIVO** - Invierte colores
**Fórmula:** `nuevo = 255 - valor`
```java
r = 255 - r;
g = 255 - g;
b = 255 - b;
```

### 2. **BRILLO** - Suma/resta a todos los canales
**Fórmula:** `nuevo = clamp(valor + factor)`
```java
int brillo = 40;  // Positivo aclara, negativo oscurece
r = clamp(r + brillo);
g = clamp(g + brillo);
b = clamp(b + brillo);
```

### 3. **HSV** - Modifica color usando Hue-Saturation-Value
**Fórmula:** RGB → HSV → modificar → HSV → RGB
```java
float[] hsv = Color.RGBtoHSB(r, g, b, null);
float h = hsv[0];   // Matiz (no tocar para cambiar saturación)
float s = clamp01(hsv[1] * 1.3f);  // Saturation (>1 más intenso)
float v = clamp01(hsv[2] * 0.9f);  // Value/Brillo (<1 más oscuro)

int rgbNuevo = Color.HSBtoRGB(h, s, v);
int pixelNuevo = (a << 24) | (rgbNuevo & 0x00FFFFFF);

// Helper: clamp a rango [0..1]
private static float clamp01(float v) { return Math.clamp(v, 0f, 1f); }
```

### 4. **ESMERRILLADO** - Transparencia por brillo
**Fórmula:** `alpha = 50 + (brillo/255) * 205`
```java
int brillo = (r + g + b) / 3;
int a = 50 + (int) ((brillo / 255.0) * (255 - 50));
// Resultado: bordes transparentes, centro opaco
```
**Nota:** Usar `TYPE_INT_ARGB` para máscara de transparencia

### 5. **DESVANECIMIENTO CIRCULAR** - Alpha según distancia al centro
**Fórmula:** `alpha = 255 * (1 - dist/distMax)`
```java
double cx = ancho / 2.0;
double cy = alto / 2.0;
double distMax = Math.sqrt(cx*cx + cy*cy);

double dist = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));
double factor = 1.0 - (dist / distMax);
int a = (int) (255 * factor);
// Centro opaco (255), bordes transparentes (0)
```

### 6. **RETRO1 / CUANTIZACIÓN** - Reduce niveles de color
**Fórmula:** `nivel_cuantizado = round(round(valor/step) * step)` con `step = 255/(n-1)`
```java
int n = 4;  // Cuantos niveles: 0, 85, 170, 255
double step = 255.0 / (n - 1);
int nivel = (int) Math.round(valor / step);
int nuevoValor = (int) Math.round(nivel * step);
```

### 7. **RECOLORIZACIÓN (LUMINANCIA)** - Tinta respetando luces y sombras
**Fórmula:** `nuevo = tono * luminancia/255` con luminancia ITU-R BT.709
```java
int tonoR = 170, tonoG = 90, tonoB = 255;
double lum = 0.2126*r + 0.7152*g + 0.0722*b;  // Pesos ITU-R BT.709
int nr = clamp((int) Math.round((lum * tonoR) / 255.0));
int ng = clamp((int) Math.round((lum * tonoG) / 255.0));
int nb = clamp((int) Math.round((lum * tonoB) / 255.0));
// Ajusta tonoR, tonoG, tonoB para cambiar el color final
```

### 8. **REDUCCIÓN Y ESTIRAMIENTO (4 BITS)** - Compresión en 4 bits + 3 expansiones
**Paso 1 - Reducir a 4 bits:**
```java
r = r >> 4;  // Divide entre 16, rango [0..15]
g = g >> 4;
b = b >> 4;
```

**Paso 2 - Estirar a 8 bits (ELIGE UNO):**

**DECIMAL (Regla de 3):**
```java
r = (r * 255) / 15;  // Mapea [0..15] → [0..255]
```

**BINARIO (Replica nibble):**
```java
r = (r << 4) | r;  // Si r=0101, resultado=01010101
```

**HEXADECIMAL (Replica con máscara):**
```java
r = (r << 4) | (r & 0x0F);  // Mismo resultado que BINARIO
```

### 9. **DEGRADADO RADIAL** - Gradiente desde centro hacia afuera
**Fórmula:** Similar a desvanecimiento pero en escala de grises
```java
double cx = ancho / 2.0;
double cy = alto / 2.0;
double distMax = Math.sqrt(cx*cx + cy*cy);
double dist = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy));
double t = 1.0 - (dist / distMax);
t = Math.clamp(t, 0.0, 1.0);
int gris = (int) Math.round(255 * t);
int rgb = (gris << 16) | (gris << 8) | gris;
```

### 10. **CONVOLUCIÓN - BLUR (3×3 promedio)**
```java
float[][] matriz = {
    {1f/9, 1f/9, 1f/9},
    {1f/9, 1f/9, 1f/9},
    {1f/9, 1f/9, 1f/9}
};
```
Resultado: Suaviza bordes

### 11. **CONVOLUCIÓN - DETECCIÓN DE BORDES (Sobel X)**
```java
float[][] matriz = {
    {-1, 0, 1},
    {-2, 0, 2},
    {-1, 0, 1}
};
```
Resultado: Resalta cambios horizontales

### 12. **CONVOLUCIÓN - DETECCIÓN DE BORDES (Sobel Y)**
```java
float[][] matriz = {
    {-1, -2, -1},
    { 0,  0,  0},
    { 1,  2,  1}
};
```
Resultado: Resalta cambios verticales

### 13. **CONVOLUCIÓN - SHARPEN (Enfoque)**
```java
float[][] matriz = {
    { 0, -1,  0},
    {-1,  5, -1},
    { 0, -1,  0}
};
```
Resultado: Aumenta contraste de bordes

### 14. **CONVOLUCIÓN - EMBOSS (Relieve)**
```java
float[][] matriz = {
    {-2, -1,  0},
    {-1,  1,  1},
    { 0,  1,  2}
};
```
Resultado: Efecto 3D

### 15. **HISTOGRAMA RGB** - Frecuencia por canal
```java
int[] histoR = new int[256];
int[] histoG = new int[256];
int[] histoB = new int[256];

int p = original.getRGB(x, y);
histoR[(p >> 16) & 0xFF]++;
histoG[(p >> 8) & 0xFF]++;
histoB[p & 0xFF]++;
```
La altura de cada línea se normaliza con el máximo global para no deformar la gráfica.

### 16. **MATRIZ DE COLORES** - Sepia / grises por multiplicación
```java
float[][] sepiaMatrix = {
    {0.393f, 0.769f, 0.189f},
    {0.349f, 0.686f, 0.168f},
    {0.272f, 0.534f, 0.131f}
};

int nr = clamp((int) (sepiaMatrix[0][0] * r + sepiaMatrix[0][1] * g + sepiaMatrix[0][2] * b));
int ng = clamp((int) (sepiaMatrix[1][0] * r + sepiaMatrix[1][1] * g + sepiaMatrix[1][2] * b));
int nb = clamp((int) (sepiaMatrix[2][0] * r + sepiaMatrix[2][1] * g + sepiaMatrix[2][2] * b));
```
Regla mental: cada canal de salida es una suma ponderada de `r`, `g` y `b`.

### 17. **BLENDING** - Mezcla de 2 imágenes
```java
float alpha = 0.3f; // peso del fondo

int r = clamp((int) ((1 - alpha) * rO + alpha * rF));
int g = clamp((int) ((1 - alpha) * gO + alpha * gF));
int b = clamp((int) ((1 - alpha) * bO + alpha * bF));
```
Interpretación: `alpha = 0` deja solo la imagen original; `alpha = 1` deja solo el fondo.

### 18. **TRIPLE BLENDING** - Mezcla de 3 imágenes
```java
float alpha1 = 0.5f;
float alpha2 = 0.3f;
float alpha3 = 0.2f;

float resR = (r1 * alpha1) + (r2 * alpha2) + (r3 * alpha3);
float resG = (g1 * alpha1) + (g2 * alpha2) + (g3 * alpha3);
float resB = (b1 * alpha1) + (b2 * alpha2) + (b3 * alpha3);
```
Las ponderaciones deben sumar 1 para mantener el brillo general.

---

## 🎓 INFORMACIÓN DE REFERENCIA

### CANALES RGB Y BIT SHIFTING
| Operación | Fórmula | Resultado |
|-----------|---------|-----------|
| Extraer Rojo | `(pixel >> 16) & 0xFF` | 0-255 |
| Extraer Verde | `(pixel >> 8) & 0xFF` | 0-255 |
| Extraer Azul | `pixel & 0xFF` | 0-255 |
| Extraer Alpha | `(pixel >> 24) & 0xFF` | 0-255 |
| Reconstruir | `(a << 24) \| (r << 16) \| (g << 8) \| b` | ARGB |

### TIPOS DE IMAGEN
| Tipo | Uso | Transparencia |
|------|-----|---------------|
| `TYPE_INT_RGB` | Normal | ❌ No |
| `TYPE_INT_ARGB` | Conserva alpha | ✅ Sí |

**Regla:** Usa `ARGB` si manipulas transparencia/alpha, `RGB` cuando no importa

### LUMINANCIA (ESTÁNDAR ITU-R BT.709)
```
L = 0.2126 * R + 0.7152 * G + 0.0722 * B
```
**Pesos:** Verde es más luminoso que rojo, azul es menos

### CLAMP - CONFINA A RANGO [0..255]
```java
Math.clamp(valor, 0, 255);  // Java 21+
// O alternativa antigua:
Math.max(0, Math.min(255, valor));
```

### CLAMP01 - CONFINA A RANGO [0.0..1.0]
```java
Math.clamp(valor, 0f, 1f);  // Para HSV, factores, etc
```

### COLOR PICKING (COLORES ÚTILES)
```
Rojo puro:        255,   0,   0
Verde puro:         0, 255,   0
Azul puro:          0,   0, 255
Blanco:           255, 255, 255
Negro:              0,   0,   0
Gris neutral:     128, 128, 128

Naranja:          255, 165,   0
Magenta:          255,   0, 255
Cian:               0, 255, 255
Amarillo:         255, 255,   0
```

### DISTANCIA ENTRE PUNTOS (PITÁGORAS)
```java
double dist = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));
// O forma moderna:
double dx = x - cx, dy = y - cy;
double dist = Math.sqrt(dx*dx + dy*dy);
```

### LOOPS BORDES (CONVOLUCIÓN)
- **Inicio:** `y = 1` (no `y = 0`)
- **Fin:** `y < alto - 1` (no `alto`)
- **Razón:** Los píxeles de los bordes no tienen vecinos completos

### FACTORES HSV (MODIFICAN PROPIEDADES)
| Factor | Efecto | Rango | Ejemplo |
|--------|--------|-------|---------|
| `factorS` | Saturación | [0..2] | 1.3 = más vivido |
| `factorV` | Brillo | [0..2] | 0.9 = más oscuro |
| `factorH` | Matiz | (NO USAR) | No tocar |

### INTERPOLACIÓN LINEAL
```java
float t = (float) x / (ancho - 1);  // Rango [0.0 .. 1.0]
int valor = (int) (t * 255);         // Mapea a [0 .. 255]
```
**Fórmula general:** `resultado = inicio + t * (fin - inicio)`

---

## ⚡ TRUCOS DE VELOCIDAD

1. **Evita `Math.pow(a, 2)`:** Usa `a*a` en su lugar
2. **Evita operaciones dentro de loops:** Calcula fuera primero
3. **Prefiere bit shifting:** `r >> 4` es más rápido que `r / 16`
4. **Caché valores repetitivos:** `int ancho = img.getWidth()` una sola vez

---

## 🐛 ERRORES COMUNES

❌ **Olvidar clamp:** Los valores > 255 causan colores raros  
❌ **Usar RGB en lugar de ARGB:** Pierdes transparencia  
❌ **Loops desde y=0:** Los bordes no tienen vecinos  
❌ **Confundir índices matriz:** `matriz[i+1][j+1]` es correcto, no `[i][j]`  
❌ **No convertir a float en convolución:** Necesitas `float sumaR` no `int`  
❌ **Olvidar & 0xFF en bit shifting:** Necesita máscara para extraer byte

---

## 📊 RESUMEN RÁPIDO POR TIPO

| Tipo | Complejidad | Loop | Alpha |
|------|-------------|------|-------|
| Negativo | Trivial | Simple | No |
| Brillo | Simple | Simple | No |
| HSV | Media | Simple | No |
| Esmerrillado | Media | Simple | **Sí** |
| Desvanecimiento | Media | Simple | **Sí** |
| Retro | Simple | Simple | No |
| Recolorización | Media | Simple | No |
| Reducción | Simple | Simple | No |
| Convolución | Media/Alta | 3 anidados | No |
| Degradados | Variable | Simple | No |
| Histograma | Media | Simple | No |
| Matriz de colores | Media | Simple | No |
| Blending | Media | Simple | No |
| Triple blending | Media | Simple | No |


