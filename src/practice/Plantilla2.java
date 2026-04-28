package practice;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Plantilla2 {
    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/originalretro.png");

        float factorS = 1.3f;
        float factorV = 0.9f;

        int n = 4;

        int tonoR = 190;
        int tonoG = 130;
        int tonoB = 90;

        try {
            BufferedImage original = ImageIO.read(entrada);
            if (original == null) {
                System.out.println("Image not found");
                return;
            }

            int ancho = original.getWidth();
            int alto = original.getHeight();

            double cx = ancho / 2;
            double cy = alto / 2;
            double distMax = Math.sqrt(cx * cx + cy * cy);

            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x, y);

                    int a = (pixel >> 24) & 0xFF;
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    // Brillo
//                    r = Math.clamp(r + 50, 0, 255);
//                    g = Math.clamp(g + 50, 0, 255);
//                    b = Math.clamp(b + 50, 0, 255);

                    // hsv
//                    float[] hsv = Color.RGBtoHSB(r, g, b, null);
//                    float h = hsv[0];
//                    float s = Math.clamp(hsv[1] * factorS, 0f, 1f);
//                    float v = Math.clamp(hsv[2] * factorV, 0f, 1f);

//                    int brillo = (r + g + b) / 3;
//                    a = (int) (50 + ((brillo / 255.0) * (255 - 50)));
                    // Centralizado
//                    double dist = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));
//                    double factor = 1.0 - (dist / distMax);
//
//                    a = (int) (255 * factor);

                    // Retro
//                    r = cuantizar((pixel >> 16) & 0xFF, n);
//                    g = cuantizar((pixel >> 8) & 0xFF, n);
//                    b = cuantizar(pixel & 0xFF, n);

                    // Recolorizacion
                    double lum = 0.2126*r + 0.7152*g + 0.0722*b;
                    int nr = Math.clamp(Math.round((lum * tonoR) / 255.0), 0, 255);
                    int ng = Math.clamp(Math.round((lum * tonoG) / 255.0), 0, 255);
                    int nb = Math.clamp(Math.round((lum * tonoB) / 255.0), 0, 255);

                    int pixelNuevo = (a << 24) | (nr << 16) | (ng << 8) | nb;
                    //int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(resultado, "png", salida);
            System.out.println("Plantilla 2 sucess");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int cuantizar(int valor, int n) {
        double step = 255.0 / (n - 1);
        int nivel = (int) Math.round(valor / step);
        return (int) Math.round(nivel * step);
    }
}
