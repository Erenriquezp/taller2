package practice;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Plantilla fiable para histograma RGB con vista clara de los 3 canales.
public class PlantillaHistograma {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/salida_hist_simple.png");

        try {
            BufferedImage img = ImageIO.read(entrada);

            int[] histR = new int[256];
            int[] histG = new int[256];
            int[] histB = new int[256];
            int ancho = img.getWidth();
            int alto = img.getHeight();

            // Contar píxeles por canal
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int p = img.getRGB(x, y);
                    histR[(p >> 16) & 0xFF]++;
                    histG[(p >> 8) & 0xFF]++;
                    histB[p & 0xFF]++;
                }
            }

            // Crear imagen de salida
            int anchoH = 600;
            int altoH = 400;
            BufferedImage out = new BufferedImage(anchoH, altoH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = out.createGraphics();
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, anchoH, altoH);

            // Hallar máximo global
            int maxGlobal = Math.max(obtenerMax(histR), Math.max(obtenerMax(histG), obtenerMax(histB)));

            double escalaX = anchoH / 256.0;
            double escalaY = altoH / (double) maxGlobal;

            // Dibujar histogramas
            dibujarHistograma(g2, histR, Color.RED, escalaX, escalaY, altoH);
            dibujarHistograma(g2, histG, Color.GREEN, escalaX, escalaY, altoH);
            dibujarHistograma(g2, histB, Color.BLUE, escalaX, escalaY, altoH);

            g2.dispose();
            ImageIO.write(out, "png", salida);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void dibujarHistograma(Graphics2D g2, int[] hist, Color color,
                                          double escalaX, double escalaY, int outH) {
        g2.setColor(color);
        for (int i = 1; i < 256; i++) {
            int x1 = (int) (escalaX * (i - 1));
            int x2 = (int) (escalaX * i);
            int y1 = outH - (int) (hist[i - 1] * escalaY);
            int y2 = outH - (int) (hist[i] * escalaY);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private static int obtenerMax(int[] array) {
        int max = 0;
        for (int v : array) if (v > max) max = v;
        return max;
    }
}

