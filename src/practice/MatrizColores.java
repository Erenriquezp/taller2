package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MatrizColores {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/asa.png");
        File salida = new File("src/practice/images/salida_matriz_colores.png");

        float[][] sepiaMatrix = {
                {0.393f, 0.769f, 0.189f},
                {0.349f, 0.686f, 0.168f},
                {0.272f, 0.534f, 0.131f}
        };

        try {
            BufferedImage original = ImageIO.read(entrada);

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

                    int nr = clamp((int) (sepiaMatrix[0][0] * r + sepiaMatrix[0][1] * g + sepiaMatrix[0][2] * b));
                    int ng = clamp((int) (sepiaMatrix[1][0] * r + sepiaMatrix[1][1] * g + sepiaMatrix[1][2] * b));
                    int nb = clamp((int) (sepiaMatrix[2][0] * r + sepiaMatrix[2][1] * g + sepiaMatrix[2][2] * b));

                    int pixelNuevo = (a << 24) | (nr << 16) | (ng << 8) | nb;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(resultado, "png", salida);
            System.out.println("Matriz de colores guardada en: " + salida.getPath());

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }

    private static int clamp(int valor) {
        return Math.clamp(valor, 0, 255);
    }
}

