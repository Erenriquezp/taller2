package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Degradados {
    public static void main(String[] args) throws Exception {
        int ancho = 600;
        int alto = 300;

        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        // Formulas base para otros degradados simples (t siempre queda en rango 0..1):
        // Derecha -> izquierda: t = 1f - ((float) x / (ancho - 1));
        // Arriba -> abajo:      t = (float) y / (alto - 1);
        // Abajo -> arriba:      t = 1f - ((float) y / (alto - 1));

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                // 0 en el borde izquierdo, 1 en el derecho
                // float t = (float) x / (ancho - 1);
                float t = 1f - ((float) x / (ancho - 1));
                //int gris = Math.round(255 * t);
                int r = (int) (t * 255);
                int g = (int) (t * 255);
                int b = 255;
                int rgb = (r << 16) | (g << 8) | b;
                salida.setRGB(x, y, rgb);
            }
        }

        File archivoSalida = new File("src/practice/images/degradado_right.png");
        ImageIO.write(salida, "png", archivoSalida);

        System.out.println("Degradado generado en: " + archivoSalida.getPath());
    }
}
