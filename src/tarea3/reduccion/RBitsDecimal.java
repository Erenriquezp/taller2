package tarea3.reduccion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class RBitsDecimal {

    public static void main(String[] args) {

        File file = new File("src/tarea3/imagenes/dogs.png");
        File fileNuevo = new File("src/tarea3/imagenes/dogsDecimal.png");

        int ancho, alto, pixel, pixelNuevo;
        int a, r, g, b;

        int mascara = 0xFF;
        int mascaraRecorteBit = 0b1111;

        try {

            BufferedImage bufer = ImageIO.read(file);
            ancho = bufer.getWidth();
            alto = bufer.getHeight();

            BufferedImage bufer2 = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    pixel = bufer.getRGB(x, y);

                    a = (pixel >> 24) & mascara;
                    r = (pixel >> 16) & mascara;
                    g = (pixel >> 8) & mascara;
                    b = pixel & mascara;

                    // Reducir a 4 bits
                    r = (r >> 4) & mascaraRecorteBit;
                    g = (g >> 4) & mascaraRecorteBit;
                    b = (b >> 4) & mascaraRecorteBit;

                    // Estirar (decimal - regla de 3)
                    r = (r * 255) / 15;
                    g = (g * 255) / 15;
                    b = (b * 255) / 15;

                    pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;

                    bufer2.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(bufer2, "jpg", fileNuevo);
            System.out.println("Imagen DECIMAL creada");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
