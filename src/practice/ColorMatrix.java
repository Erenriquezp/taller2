package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorMatrix {
    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original2.png");
        File salida = new File("src/practice/images/salida-conv.png");

        try {
            BufferedImage original = ImageIO.read(entrada);
            float[][] sepia = {
                    {0.393f, 0.769f, 0.189f},
                    {0.349f, 0.686f, 0.168f},
                    {0.272f, 0.534f, 0.131f},
            };

            int ancho = original.getWidth();
            int alto = original.getHeight();
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x,y);
                    int a = (pixel >> 24) & 0xFF;
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    r = Math.clamp((int) (sepia[0][0] * r + sepia[0][1] * g + sepia[0][2] * b),0,255);
                    g = Math.clamp((int) (sepia[1][0] * r + sepia[1][1] * g + sepia[1][2] * b),0,255);
                    b = Math.clamp((int) (sepia[2][0] * r + sepia[2][1] * g + sepia[2][2] * b),0,255);

                    int pixelNuevo = a << 24 | r << 16 | g << 8 | b;
                    resultado.setRGB(x,y,pixelNuevo);
                }
            }

            ImageIO.write(resultado, "png", salida);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
