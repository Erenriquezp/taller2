package practice;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PracticeHist {
    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/histAsa.png");

        try {
            BufferedImage original = ImageIO.read(entrada);
            int ancho = original.getWidth();
            int alto = original.getHeight();

            int[] histR = new int[256];
            int[] histG = new int[256];
            int[] histB = new int[256];

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x,y);
                    histR[(pixel >> 16) & 0xFF]++;
                    histG[(pixel >> 8) & 0xFF]++;
                    histB[(pixel) & 0xFF]++;
                }
            }

            int anchoH = 800;
            int altoH = 600;

            BufferedImage hist = new BufferedImage(anchoH, altoH, BufferedImage.TYPE_INT_ARGB);
            int maxGlobal = max(histR);

            double escalaX = anchoH/256.0;
            double escalaY = altoH/(double) maxGlobal;

            Graphics2D g2 = hist.createGraphics();
            g2.setColor(Color.BLACK);
            g2.fillRect(0,0,anchoH,altoH);

            dibujar(g2,histR,Color.RED,escalaX,escalaY,altoH);
            dibujar(g2,histG,Color.BLUE,escalaX,escalaY,altoH);
            dibujar(g2,histB,Color.GREEN,escalaX,escalaY,altoH);
            g2.dispose();

            ImageIO.write(hist, "png", salida);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dibujar(Graphics2D g2, int[] hist, Color color, double escalaX, double escalaY, int altoH) {
        g2.setColor(color);
        for (int i = 1; i < 256; i++) {
            int x1 = (int) (escalaX * (i - 1));
            int x2 = (int) (escalaX * i);
            int y1 = (int) (altoH - hist[i-1]*escalaY);
            int y2 = (int) (altoH - hist[i]*escalaY);
            g2.drawLine(x1,y1,x2,y2);
        }
    }

    private static int max(int[] hist) {
        int max = 0;
        for (int valor: hist) if (valor > max) max = valor;
        return max;
    }
}
