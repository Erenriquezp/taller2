package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Recoloracion {
    public static void main(String[] args) throws Exception {
        File entrada = new File("src/taller/images/original.png");
        File salida = new File("src/practice/images/salida_recoloracion.png");

        // Cambia solo estos 3 valores para elegir el color final.
        int tonoR = 170;
        int tonoG = 90;
        int tonoB = 255;

        BufferedImage imagen = ImageIO.read(entrada);
        if (imagen == null) throw new IllegalArgumentException("No se pudo leer: " + entrada.getPath());

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int p = imagen.getRGB(x, y);
                int a = (p >> 24) & 0xFF;
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;

                // Luminancia: mantiene luces y sombras, solo cambia el tinte.
                double lum = 0.2126 * r + 0.7152 * g + 0.0722 * b;

                int nr = clamp((int) Math.round((lum * tonoR) / 255.0));
                int ng = clamp((int) Math.round((lum * tonoG) / 255.0));
                int nb = clamp((int) Math.round((lum * tonoB) / 255.0));

                int nuevo = (a << 24) | (nr << 16) | (ng << 8) | nb;
                resultado.setRGB(x, y, nuevo);
            }
        }

        ImageIO.write(resultado, "png", salida);
        System.out.println("Recoloracion generada en: " + salida.getPath());
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
