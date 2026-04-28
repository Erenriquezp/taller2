package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Convolucion {
    public static void main(String[] args) throws Exception {
        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/salida_convolucion.png");

        // Cambia esta matriz para otro efecto.
        // Este ejemplo esta pensado para una matriz 3x3.
        float[][] matriz = {
                {1f / 9f, 1f / 9f, 1f / 9f},
                {1f / 9f, 1f / 9f, 1f / 9f},
                {1f / 9f, 1f / 9f, 1f / 9f}
        };

        BufferedImage imagenOriginal = ImageIO.read(entrada);
        if (imagenOriginal == null) throw new IllegalArgumentException("No se pudo leer: " + entrada.getPath());

        int ancho = imagenOriginal.getWidth();
        int alto = imagenOriginal.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 1; y < alto - 1; y++) {
            for (int x = 1; x < ancho - 1; x++) {
                float sumaR = 0;
                float sumaG = 0;
                float sumaB = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = imagenOriginal.getRGB(x + j, y + i);

                        int r = (pixel >> 16) & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int b = pixel & 0xFF;

                        sumaR += r * matriz[i + 1][j + 1];
                        sumaG += g * matriz[i + 1][j + 1];
                        sumaB += b * matriz[i + 1][j + 1];
                    }
                }

                int rojoNuevo = clamp((int) sumaR);
                int verdeNuevo = clamp((int) sumaG);
                int azulNuevo = clamp((int) sumaB);

                int pixelNuevo = (rojoNuevo << 16) | (verdeNuevo << 8) | azulNuevo;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }

        ImageIO.write(resultado, "png", salida);
        System.out.println("Convolucion generada en: " + salida.getPath());
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
