package reduccion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RBitsBinario {
    public static void main(String[] args) {

        File file = new File("src/tarea3/imagenes/dogs.png");
        File fileNuevo = new File("src/tarea3/imagenes/dogsBinario.png");

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

                    // Estirar (binario)
                    r = (r << 4) | r;
                    g = (g << 4) | g;
                    b = (b << 4) | b;

                    pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;

                    bufer2.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(bufer2, "png", fileNuevo);
            System.out.println("Imagen BINARIO creada");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
