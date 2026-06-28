package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PracticeBlending {
    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/salida-blendings.png");

        float[][] matrix = {
                {-2, -1,  0},
                {-1,  1,  1},
                { 0,  1,  2}
        };

        try {
            BufferedImage original = ImageIO.read(entrada);
            int ancho = original.getWidth();
            int alto = original.getHeight();

            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            for (int y = 1; y < alto-1; y++) {
                for (int x = 1; x < ancho-1; x++) {
                    float sumaR = 0, sumaG = 0, sumaB = 0;
                    for (int i = -1; i <= 1 ; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int pixel = original.getRGB(x+j, y+i);
                            int r = (pixel >> 16) & 0xFF;
                            int g = (pixel >> 8) & 0xFF;
                            int b = pixel & 0xFF;

                            sumaR += matrix[i+1][j+1]*r;
                            sumaG += matrix[i+1][j+1]*g;
                            sumaB += matrix[i+1][j+1]*b;
                        }
                    }
                    int nr = (int) Math.clamp(sumaR, 0 , 255);
                    int ng = (int) Math.clamp(sumaG, 0 , 255);
                    int nb = (int) Math.clamp(sumaB, 0 , 255);

                    int pixelNuevo = nr << 16 | ng << 8 | nb;
                    resultado.setRGB(x,y,pixelNuevo);
                }
            }
            ImageIO.write(resultado,"png",salida);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
