package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DegradadoRadial {
    public static void main(String[] args) throws Exception {
        int ancho = 600;
        int alto = 600;

        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        double cx = ancho / 2.0;
        double cy = alto / 2.0;
        double distMax = Math.sqrt(cx * cx + cy * cy);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                double dx = x - cx;
                double dy = y - cy;
                double dist = Math.sqrt(dx * dx + dy * dy);

                // Centro claro (1), bordes oscuros (0).
                double t = 1.0 - (dist / distMax);
                // t = Math.max(0.0, Math.min(1.0, t));
                t = Math.clamp(t, 0.0, 1.0);

                int gris = (int) Math.round(255 * t);
                int rgb = (gris << 16) | (gris << 8) | gris;
                salida.setRGB(x, y, rgb);
            }
        }

        File archivoSalida = new File("src/practice/images/degradado_radial.png");
        ImageIO.write(salida, "png", archivoSalida);

        System.out.println("Degradado radial generado en: " + archivoSalida.getPath());
    }
}
