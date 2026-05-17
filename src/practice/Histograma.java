package practice;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Histograma {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/salida_histograma.png");

        try {
            BufferedImage original = ImageIO.read(entrada);
            if (original == null) {
                System.out.println("No se pudo leer la imagen de entrada.");
                return;
            }

            int[] histoR = new int[256];
            int[] histoG = new int[256];
            int[] histoB = new int[256];

            for (int y = 0; y < original.getHeight(); y++) {
                for (int x = 0; x < original.getWidth(); x++) {
                    int p = original.getRGB(x, y);
                    histoR[(p >> 16) & 0xFF]++;
                    histoG[(p >> 8) & 0xFF]++;
                    histoB[p & 0xFF]++;
                }
            }

            int anchoH = 800;
            int altoH = 600;
            BufferedImage imgHisto = new BufferedImage(anchoH, altoH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = imgHisto.createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, anchoH, altoH);

            int maxGlobal = Math.max(obtenerMaximo(histoR), Math.max(obtenerMaximo(histoG), obtenerMaximo(histoB)));
            if (maxGlobal == 0) {
                maxGlobal = 1;
            }

            float escalaX = (float) anchoH / 256f;
            float escalaY = (float) altoH / maxGlobal;
            g2d.setStroke(new BasicStroke(2));

            for (int i = 1; i < 256; i++) {
                int x1 = (int) (escalaX * (i - 1));
                int x2 = (int) (escalaX * i);

                int rY1 = altoH - (int) (histoR[i - 1] * escalaY);
                int rY2 = altoH - (int) (histoR[i] * escalaY);
                g2d.setColor(Color.RED);
                g2d.drawLine(x1, rY1, x2, rY2);

                int gY1 = altoH - (int) (histoG[i - 1] * escalaY);
                int gY2 = altoH - (int) (histoG[i] * escalaY);
                g2d.setColor(Color.GREEN);
                g2d.drawLine(x1, gY1, x2, gY2);

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

    private static int obtenerMaximo(int[] array) {
        int max = 0;
        for (int valor : array) {
            if (valor > max) {
                max = valor;
            }
        }
        return max;
    }
}

