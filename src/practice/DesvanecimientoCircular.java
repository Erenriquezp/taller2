package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DesvanecimientoCircular {
    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida  = new File("src/practice/images/desvanecimiento.png");

        try {
            BufferedImage original = ImageIO.read(entrada);
            if (original == null) {
                System.out.println("No se pudo leer la imagen.");
                return;
            }

            int ancho = original.getWidth();
            int alto  = original.getHeight();

            // Centro de la imagen
            double cx = ancho / 2.0;
            double cy = alto  / 2.0;

            // Distancia maxima: del centro a la esquina (Pitagoras)
            double distMax = Math.sqrt(cx * cx + cy * cy);

            // ARGB obligatorio para guardar transparencia
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x, y);

                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8)  & 0xFF;
                    int b =  pixel        & 0xFF;

                    // Distancia del pixel actual al centro
                    double dist = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));

                    // Factor: 1.0 en el centro, 0.0 en los bordes
                    double factor = 1.0 - (dist / distMax);

                    // Alpha proporcional al factor
                    int a = (int) (255 * factor);

                    int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(resultado, "png", salida);
            System.out.println("Desvanecimiento circular aplicado. Guardado en: " + salida.getPath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
