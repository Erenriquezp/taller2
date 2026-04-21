package tarea2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TareaFiltrosAvanzados {

    public static void main(String[] args) {
        File archivoEntrada = new File("src/tarea2/images/original.png");

        try {
            BufferedImage original = ImageIO.read(archivoEntrada);
            if (original == null) {
                System.out.println("No se pudo leer la imagen. Verifica la ruta.");
                return;
            }

            System.out.println("Generando filtros de la tarea...");

            // 1. Vidrio Esmerilado
            ImageIO.write(efectoVidrioEsmerilado(original), "png", new File("src/tarea2/images/1_vidrio_esmerilado.png"));

            // 2. Desvanecimiento Circular
            ImageIO.write(efectoDesvanecimientoCircular(original), "png", new File("src/tarea2/images/2_desvanecimiento.png"));

            // 3. Efecto Retro 1
            ImageIO.write(efectoRetro1(original, 2), "png", new File("src/tarea2/images/3_retro1_N2.png"));
            ImageIO.write(efectoRetro1(original, 4), "png", new File("src/tarea2/images/3_retro1_N4.png"));
            ImageIO.write(efectoRetro1(original, 8), "png", new File("src/tarea2/images/3_retro1_N8.png"));
            ImageIO.write(efectoRetro1(original, 64), "png", new File("src/tarea2/images/3_retro1_N64.png"));
            ImageIO.write(efectoRetro1(original, 128), "png", new File("src/tarea2/images/3_retro1_N128.png"));
            ImageIO.write(efectoRetro1(original, 255), "png", new File("src/tarea2/images/3_retro1_N255.png"));

            // 4. Efecto Retro 2 (RG con N = 4, canal B se mantiene original)
            ImageIO.write(efectoRetro2(original, 2, true, true, false), "png", new File("src/tarea2/images/4_retro2_N2_RG.png"));
            ImageIO.write(efectoRetro2(original, 4, true, true, false), "png", new File("src/tarea2/images/4_retro2_N4_RG.png"));
            ImageIO.write(efectoRetro2(original, 8, true, true, false), "png", new File("src/tarea2/images/4_retro2_N8_RG.png"));
            ImageIO.write(efectoRetro2(original, 64, true, true, false), "png", new File("src/tarea2/images/4_retro2_N64_RG.png"));
            ImageIO.write(efectoRetro2(original, 128, true, true, false), "png", new File("src/tarea2/images/4_retro2_N128_RG.png"));
            ImageIO.write(efectoRetro2(original, 255, true, true, false), "png", new File("src/tarea2/images/4_retro2_N255_RG.png"));

            // 5. Blanco y Negro Puro
            ImageIO.write(efectoBlancoYNegro(original), "png", new File("src/tarea2/images/5_blanco_negro.png"));

            // 6. Escala de Grises Cuantizada
            ImageIO.write(efectoGrisesCuantizado(original, 2), "png", new File("src/tarea2/images/6_grises_N2.png"));
            ImageIO.write(efectoGrisesCuantizado(original, 4), "png", new File("src/tarea2/images/6_grises_N4.png"));
            ImageIO.write(efectoGrisesCuantizado(original, 8), "png", new File("src/tarea2/images/6_grises_N8.png"));
            ImageIO.write(efectoGrisesCuantizado(original, 16), "png", new File("src/tarea2/images/6_grises_N64.png"));
            ImageIO.write(efectoGrisesCuantizado(original, 128), "png", new File("src/tarea2/images/6_grises_N128.png"));
            ImageIO.write(efectoGrisesCuantizado(original, 255), "png", new File("src/tarea2/images/6_grises_N255.png"));

            System.out.println("¡Todas las imágenes generadas con éxito!");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // ==========================================================
    // MÉTODO AUXILIAR MATEMÁTICO
    // ==========================================================

    /**
     * Reduce un valor de color (0-255) a uno de los N niveles posibles.
     */
    private static int cuantizarColor(int valor, int n) {
        if (n <= 1) return 0;
        if (n >= 256) return valor;

        // Calculamos el tamaño del "salto" entre colores
        double step = 255.0 / (n - 1);

        // Encontramos el nivel más cercano y lo multiplicamos por el salto
        int nivel = (int) Math.round(valor / step);
        return (int) Math.round(nivel * step);
    }

    // ==========================================================
    // RESOLUCIÓN DE LA TAREA
    // ==========================================================

    // 1. Vidrio esmerilado: Transparencia dependiente del brillo
    public static BufferedImage efectoVidrioEsmerilado(BufferedImage original) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // Calculamos el brillo (promedio)
                int brillo = (r + g + b) / 3;

                // Mapeo lineal: Si brillo es 0 -> a=50. Si brillo es 255 -> a=255.
                int a = 50 + (int) ((brillo / 255.0) * (255 - 50));

                int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    // 2. Desvanecimiento circular
    public static BufferedImage efectoDesvanecimientoCircular(BufferedImage original) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        double cx = ancho / 2.0;
        double cy = alto / 2.0;
        double distanciaMax = Math.sqrt((cx * cx) + (cy * cy));

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // Calculamos distancia al centro
                double dist = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));

                // Factor: 1.0 en el centro, 0.0 en los bordes
                double factor = 1.0 - (dist / distanciaMax);
                factor = Math.max(0.0, factor);

                int a = (int) (255 * factor);

                int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    // 3. Efecto Retro 1 (Cuantización RGB)
    public static BufferedImage efectoRetro1(BufferedImage original, int n) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                int a = (pixel >> 24) & 0xFF; // Mantenemos Alpha intacto
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                r = cuantizarColor(r, n);
                g = cuantizarColor(g, n);
                b = cuantizarColor(b, n);

                int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    // 4. Efecto Retro 2 (Cuantización Parcial de Canales)
    public static BufferedImage efectoRetro2(BufferedImage original, int n, boolean afectarR, boolean afectarG, boolean afectarB) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                //int b = pixel & 0xFF;
                int b = 0;

                if (afectarR) r = cuantizarColor(r, n);
                if (afectarG) g = cuantizarColor(g, n);
                if (afectarB) b = cuantizarColor(b, n);

                int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    // 5. Efecto Blanco y Negro (Thresholding/Umbralización)
    public static BufferedImage efectoBlancoYNegro(BufferedImage original) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int gris = (r + g + b) / 3;

                // Si el gris promedio es mayor a 127, se vuelve blanco puro (255), caso contrario negro puro (0)
                int colorFinal = (gris > 127) ? 255 : 0;

                int pixelNuevo = (a << 24) | (colorFinal << 16) | (colorFinal << 8) | colorFinal;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    // 6. Efecto Escala de Grises Cuantizada
    public static BufferedImage efectoGrisesCuantizado(BufferedImage original, int n) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // Promedio a gris
                int gris = (r + g + b) / 3;

                // Cuantizamos el valor del gris
                gris = cuantizarColor(gris, n);

                int pixelNuevo = (a << 24) | (gris << 16) | (gris << 8) | gris;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }
}