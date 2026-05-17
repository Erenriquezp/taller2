package matrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Histograma {
    public static void main(String[] args) {
        File entrada = new File("src/matrix/milky-way.jpg");
        File salida = new File("src/matrix/salida_histograma-milky.png");

        try {
            BufferedImage original = ImageIO.read(entrada);
            if (original == null) {
                System.out.println("No se pudo leer la imagen de entrada.");
                return;
            }

            // Dimensiones para la imagen del HISTOGRAMA
            int anchoH = 800;
            int altoH = 600;

            int[] histoR = new int[256];
            int[] histoG = new int[256];
            int[] histoB = new int[256];

            // 1. Contar frecuencias
            for (int y = 0; y < original.getHeight(); y++) {
                for (int x = 0; x < original.getWidth(); x++) {
                    int p = original.getRGB(x, y);
                    histoR[(p >> 16) & 0xFF]++;
                    histoG[(p >> 8) & 0xFF]++;
                    histoB[p & 0xFF]++;
                }
            }

            // 2. Preparar lienzo para el dibujo
            BufferedImage imgHisto = new BufferedImage(anchoH, altoH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = imgHisto.createGraphics();

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, anchoH, altoH);

            // Obtener el máximo para normalizar (usamos el mayor de los 3 para mantener proporción)
            int maxR = obtenerMaxEImprimir(histoR, "Rojo");
            int maxG = obtenerMaxEImprimir(histoG, "Verde");
            int maxB = obtenerMaxEImprimir(histoB, "Azul");
            int maxGlobal = Math.max(maxR, Math.max(maxG, maxB));

            // 3. Dibujar las líneas
            float escalaX = (float) anchoH / 256f;
            float escalaY = (float) altoH / maxGlobal;

            for (int i = 1; i < 256; i++) {
                // Las coordenadas X son comunes para los tres canales
                int x1 = (int) (escalaX * (i - 1));
                int x2 = (int) (escalaX * i);

                // --- Canal Rojo ---
                int rY1 = altoH - (int) (histoR[i - 1] * escalaY);
                int rY2 = altoH - (int) (histoR[i] * escalaY);

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x1, rY1, x2, rY2);

                // --- Canal Verde ---
                int gY1 = altoH - (int) (histoG[i - 1] * escalaY);
                int gY2 = altoH - (int) (histoG[i] * escalaY);
                g2d.setColor(Color.GREEN);
                g2d.drawLine(x1, gY1, x2, gY2);

                // --- Canal Azul ---
                int bY1 = altoH - (int) (histoB[i - 1] * escalaY);
                int bY2 = altoH - (int) (histoB[i] * escalaY);
                g2d.setColor(Color.BLUE);
                g2d.drawLine(x1, bY1, x2, bY2);
            }

            g2d.dispose();
            ImageIO.write(imgHisto, "png", salida);
            System.out.println("Histograma guardado en: " + salida.getPath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Método para obtener valor máximo e índice
    private static int obtenerMaxEImprimir(int[] array, String nombreCanal) {
        int maxVal = 0;
        int indiceMax = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxVal) {
                maxVal = array[i];
                indiceMax = i;
            }
        }
        System.out.println("Canal " + nombreCanal + ": Max = " + maxVal + ", Tono predominante (Índice) = " + indiceMax);
        return maxVal;
    }
}