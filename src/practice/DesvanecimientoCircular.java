package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DesvanecimientoCircular {
    public static void main(String[] args) {
        File entrada = new File("original2.png");
        File salida  = new File("desvanecimiento.png");

        try {
            BufferedImage original = ImageIO.read(entrada);

            int ancho = original.getWidth();
            int alto  = original.getHeight();

            double cx = ancho / 2.0;
            double cy = alto  / 2.0;

            double distMax = Math.sqrt(cx * cx + cy * cy);

            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x, y);

                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8)  & 0xFF;
                    int b =  pixel        & 0xFF;

                    double dist = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));

                    double factor = 1.0 - (dist / distMax);

                    int a = (int) (255 * factor);

                    int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }
            ImageIO.write(resultado, "png", salida);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
