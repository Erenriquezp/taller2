package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Convolucion9 {
    public static void main(String[] args) {

        File archivoEntrada = new File("src/convolucion/jennie.jpg");

        // Kernel 3x3 (tu blur)
        float[] matriz = {
                1f/9, 1f/9, 1f/9,
                1f/9, 1f/9, 1f/9,
                1f/9, 1f/9, 1f/9
        };

        // Kernel 9x9 (promedio)
        float[] matriz9 = new float[81];
        for (int i = 0; i < 81; i++) {
            matriz9[i] = 1f / 81;
        }

        try {
            BufferedImage imagenOriginal = ImageIO.read(archivoEntrada);
            int ancho = imagenOriginal.getWidth();
            int alto = imagenOriginal.getHeight();

            // =========================
            // 1. BLUR 9x9
            // =========================
            BufferedImage resultado9 = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            for (int y = 4; y < alto - 4; y++) {
                for (int x = 4; x < ancho - 4; x++) {

                    float sumaR = 0, sumaG = 0, sumaB = 0;
                    int indice = 0;

                    for (int i = -4; i <= 4; i++) {
                        for (int j = -4; j <= 4; j++) {

                            int pixel = imagenOriginal.getRGB(x + j, y + i);

                            int r = (pixel >> 16) & 0xFF;
                            int g = (pixel >> 8) & 0xFF;
                            int b = pixel & 0xFF;

                            sumaR += r * matriz9[indice];
                            sumaG += g * matriz9[indice];
                            sumaB += b * matriz9[indice];
                            indice++;
                        }
                    }

                    int r = Math.min(255, Math.max(0, (int) sumaR));
                    int g = Math.min(255, Math.max(0, (int) sumaG));
                    int b = Math.min(255, Math.max(0, (int) sumaB));

                    int pixelNuevo = (r << 16) | (g << 8) | b;
                    resultado9.setRGB(x, y, pixelNuevo);
                }
            }

            // =========================
            // 2. BLUR 3x3 REPETIDO 9 VECES
            // =========================
            BufferedImage repetido = imagenOriginal;

            for (int iter = 0; iter < 9; iter++) {

                BufferedImage temp = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

                for (int y = 1; y < alto - 1; y++) {
                    for (int x = 1; x < ancho - 1; x++) {

                        float sumaR = 0, sumaG = 0, sumaB = 0;
                        int indice = 0;

                        for (int i = -1; i < 2; i++) {
                            for (int j = -1; j < 2; j++) {

                                int pixel = repetido.getRGB(x + j, y + i);

                                int r = (pixel >> 16) & 0xFF;
                                int g = (pixel >> 8) & 0xFF;
                                int b = pixel & 0xFF;

                                sumaR += r * matriz[indice];
                                sumaG += g * matriz[indice];
                                sumaB += b * matriz[indice];
                                indice++;
                            }
                        }

                        int r = Math.min(255, Math.max(0, (int) sumaR));
                        int g = Math.min(255, Math.max(0, (int) sumaG));
                        int b = Math.min(255, Math.max(0, (int) sumaB));

                        int pixelNuevo = (r << 16) | (g << 8) | b;
                        temp.setRGB(x, y, pixelNuevo);
                    }
                }

                repetido = temp; // importante: encadenar resultado
            }

            // =========================
            // GUARDAR RESULTADOS
            // =========================
            ImageIO.write(resultado9, "jpg", new File("src/convolucion/blur9x9.jpg"));
            ImageIO.write(repetido, "jpg", new File("src/convolucion/blur_repetido.jpg"));

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }
}