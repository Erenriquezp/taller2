package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Esmerrillado {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida  = new File("src/practice/images/salida_esmerilado.png");

        try {
            BufferedImage original = ImageIO.read(entrada);
            if (original == null) {
                System.out.println("No se pudo leer la imagen.");
                return;
            }

            int ancho = original.getWidth();
            int alto = original.getHeight();

            // ARGB obligatorio para poder guardar transparencia
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x, y);

                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b =  pixel & 0xFF;

                    // Brillo promedio del pixel (0 = negro, 255 = blanco)
                    int brillo = (r + g + b) / 3;

                    // Mapeo lineal: brillo 0 -> alpha 50 (casi transparente)
                    //               brillo 255 -> alpha 255 (opaco)
                    int a = 50 + (int) ((brillo / 255.0) * (255 - 50));

                    int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(resultado, "png", salida);
            System.out.println("Efecto esmerilado aplicado. Imagen guardada en: " + salida.getPath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
