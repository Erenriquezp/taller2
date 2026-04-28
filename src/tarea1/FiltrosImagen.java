package taller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FiltrosImagen {

    public static void main(String[] args) {
        // Rutas relativas configuradas para el entorno de IntelliJ
        File archivoEntrada = new File("src/taller/images/original.png");
        File archivoSalida = new File("src/taller/images/j_modificado.png");

        try {
            // 1. Cargar la imagen original en memoria
            BufferedImage bufferOriginal = ImageIO.read(archivoEntrada);
            if (bufferOriginal == null) {
                System.out.println("No se pudo leer la imagen. Verifica la ruta.");
                return;
            }

            System.out.println("Procesando imagen...");

            // 2. Aplicar el filtro deseado llamando a su método específico
            // Puedes cambiar este método por aplicarNegativo, aplicarBrillo, etc.
            BufferedImage bufferResultado = modificarTransparencia(bufferOriginal, 1.2f);

            // 3. Guardar el resultado
            ImageIO.write(bufferResultado, "png", archivoSalida);
            System.out.println("Imagen guardada con éxito en: " + archivoSalida.getPath());

        } catch (IOException e) {
            System.err.println("Error de I/O: " + e.getMessage());
        }
    }

    // ==========================================================
    // MÉTODOS DE PROCESAMIENTO DE IMÁGENES (FILTROS)
    // ==========================================================

    /**
     * Convierte la imagen a escala de grises promediando los canales RGB.
     */
    public static BufferedImage aplicarEscalaDeGrises(BufferedImage original) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);

                // Extracción de canales usando máscaras de bits (0xFF aísla los últimos 8 bits)
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int gris = (r + g + b) / 3;

                // Reconstrucción del píxel con el mismo valor en los tres canales
                int pixelNuevo = (gris << 16) | (gris << 8) | gris;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    /**
     * Aplica el filtro negativo invirtiendo los colores (255 - valor).
     */
    public static BufferedImage aplicarNegativo(BufferedImage original) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);

                int r = 255 - ((pixel >> 16) & 0xFF);
                int g = 255 - ((pixel >> 8) & 0xFF);
                int b = 255 - (pixel & 0xFF);

                int pixelNuevo = (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    /**
     * Aumenta o disminuye el brillo de la imagen sumando una constante.
     */
    public static BufferedImage aplicarBrillo(BufferedImage original, int nivelBrillo) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // Clamping: Asegura que el valor final se mantenga entre 0 y 255
                r = Math.min(255, Math.max(0, r + nivelBrillo));
                g = Math.min(255, Math.max(0, g + nivelBrillo));
                b = Math.min(255, Math.max(0, b + nivelBrillo));

                int pixelNuevo = (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    /**
     * Modifica la saturación y el valor (brillo) utilizando el espacio de color HSV.
     */
    public static BufferedImage modificarHSV(BufferedImage original, float factorS, float factorV) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // Convierte de RGB a HSV
                float[] hsv = Color.RGBtoHSB(r, g, b, null);
                float h = hsv[0];

                // Aplica los factores y usa Clamping (máximo 1.0 para HSV)
                float s = Math.min(1.0f, hsv[1] * factorS);
                float v = Math.min(1.0f, hsv[2] * factorV);

                // Convierte de vuelta a RGB (el método devuelve el entero de 32 bits empaquetado)
                int pixelNuevo = Color.HSBtoRGB(h, s, v);
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

    /**
     * Modifica el canal Alpha (transparencia). Requiere formato ARGB (soporte para PNG).
     */
    public static BufferedImage modificarTransparencia(BufferedImage original, float factorAlfa) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        // IMPORTANTE: Se usa TYPE_INT_ARGB para soportar el canal de transparencia
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);

                int a = (pixel >> 24) & 0xFF; // Extrae el canal Alpha
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // Clamping del canal Alpha
                a = (int) Math.min(255, a * factorAlfa);

                // Empaquetado final incluyendo el canal Alpha desplazado 24 bits
                int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }
}