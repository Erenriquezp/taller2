package tarea1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Filtos {
    public static void main(String[] args) {
        File file = new File("src/taller/images/original.png");
        File file2 = new File("src/taller/images/j4.png");
        int ancho, alto, pixel, pixelNuevo;
        int r, g, b, a;

        Random rd = new Random();
        int gris;
        int brillo = 20;

        float[] hsv = new float[3];
        float h, s, v;
        float factorS = 1.3f;
        float factorB = 0.9f;
        float factorT = 1.2f;

        try {

            BufferedImage buffer = ImageIO.read(file);
            ancho = buffer.getWidth();
            alto = buffer.getHeight();

            BufferedImage buffer2 = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            BufferedImage buffer3 = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);


            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    pixel = buffer.getRGB(x, y);

                    r = (pixel >> 16) & 0xFF;
                    g = (pixel >> 8) & 0xFF;
                    b = pixel & 0xFF;

                    // Escala de grises
//                    gris = (r + g + b) / 3;
//                    pixelNuevo = (gris << 16) | (gris << 8) | (gris);
//                    buffer2.setRGB(x, y, pixelNuevo);

                    // Filtro negativo
//                    r = (255-r);
//                    g = (255-g);
//                    b = (255-b);
//
//                    pixelNuevo = (r << 16) | (g << 8) | (b);
//                    buffer2.setRGB(x, y, pixelNuevo);

                    // Filtro negativo
//                    r = Math.min(255, r+brillo);
//                    g = Math.min(255, g+brillo);
//                    b = Math.min(255, b+brillo);
//
//                    pixelNuevo = (r << 16) | (g << 8) | (b);
//                    buffer2.setRGB(x, y, pixelNuevo);

                    // HSV Tono, saturacion, brillo
//                    hsv = Color.RGBtoHSB(r, g, b, null);
//                    h = hsv[0];
//                    s = hsv[1];
//                    v = hsv[2];
//
//                    s = Math.min(1, s*factorS);
//                    v = Math.min(1, v*factorB);
//
//                    pixelNuevo = Color.HSBtoRGB(h, s, v);
//                    buffer2.setRGB(x, y, pixelNuevo);
//
                    // Alfa
                    a = (pixel >> 24) & 0xFF;
                    a = (int) Math.min(255, a*factorT);

                    pixelNuevo = (a << 24 ) | (r << 16) | (g << 8) | b;
                    buffer3.setRGB(x, y, pixelNuevo);
                }
            }
            ImageIO.write(buffer3, "png", file2);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
