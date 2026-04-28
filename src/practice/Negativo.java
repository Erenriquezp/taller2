package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Negativo {
    public static void main(String[] args) {

        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/negativo.png");

        try {
            BufferedImage original = ImageIO.read(entrada);

            if (original == null) {
                System.out.println("Error al leer imagen");
                return;
            }

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

                    r = 255 - r;
                    g = 255 - g;
                    b = 255 - b;

                    int nuevoPixel = (a << 24) | (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, nuevoPixel);
                }

            }

            ImageIO.write(resultado, "png", salida);
            System.out.println("Success");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
