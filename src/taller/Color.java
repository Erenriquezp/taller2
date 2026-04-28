package taller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Color {

    public static void main(String[] args) {
        File archivoEntrada = new File("src/taller/images/original.png");

        try {
            BufferedImage original = ImageIO.read(archivoEntrada);

            // Definición de colores estilo Word
            // Azul, color énfasis 1 (aprox 47, 84, 150)
            ImageIO.write(volverAColorear(original, 47, 84, 150), "png", new File("src/taller/images/recolor_azul.png"));

            // Rojo (enfático)
            ImageIO.write(volverAColorear(original, 192, 0, 0), "png", new File("src/taller/images/recolor_rojo.png"));

            // Verde (enfático)
            ImageIO.write(volverAColorear(original, 0, 176, 80), "png", new File("src/taller/images/recolor_verde.png"));

            // Lila / Púrpura
            ImageIO.write(volverAColorear(original, 112, 48, 160), "png", new File("src/taller/images/recolor_lila.png"));

            System.out.println("¡Efectos de recoloración generados!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static BufferedImage volverAColorear(BufferedImage original, int redTono, int greenTono, int blueTono) {
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

                // 1. Calcular luminosidad (Escala de grises estándar ITU-R BT.709)
                int luminosidad = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);

                // 2. Mapear la luminosidad al color objetivo
                // Dividimos por 255 para normalizar y luego multiplicamos por el tono deseado
                int nuevoR = (luminosidad * redTono) / 255;
                int nuevoG = (luminosidad * greenTono) / 255;
                int nuevoB = (luminosidad * blueTono) / 255;

                int pixelNuevo = (a << 24) | (nuevoR << 16) | (nuevoG << 8) | nuevoB;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }

}