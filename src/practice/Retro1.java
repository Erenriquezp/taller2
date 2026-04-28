package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Retro1 {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida  = new File("src/practice/images/salida_retro1.png");

        int n = 4; // niveles posibles por canal: 0, 85, 170, 255

        try {
            BufferedImage original = ImageIO.read(entrada);
            int ancho = original.getWidth();
            int alto  = original.getHeight();
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x, y);

                    int a = (pixel >> 24) & 0xFF;
                    int r = cuantizar((pixel >> 16) & 0xFF, n);
                    int g = cuantizar((pixel >> 8)  & 0xFF, n);
                    int b = cuantizar( pixel        & 0xFF, n);

                    resultado.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }

            ImageIO.write(resultado, "png", salida);
            System.out.println("Retro1 aplicado. Guardado en: " + salida.getPath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Reduce 'valor' (0-255) a uno de los N niveles posibles
    private static int cuantizar(int valor, int n) {
        double step = 255.0 / (n - 1);          // salto entre niveles
        int nivel = (int) Math.round(valor / step); // escalón más cercano
        return (int) Math.round(nivel * step);   // volver a rango 0-255
    }
}
