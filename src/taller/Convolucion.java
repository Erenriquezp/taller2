package taller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Convolucion {
    public static void main(String[] args) {
        // Operacion matematica que permite aplicar efectos como blur (desenfoque), deteccion de bordes, realce de nitidez
        // Convolucion = recorrer una imagen y recalcular cada piexel usando sus vecinos

        // Se utiliza una matriz pequeña llamada kernel o mascara, que se desliza sobre cada pixel, el valor del nuevo
        // pixel es la suma ponderada de sus vecinos segun los valoes del kernel

        //aplicar una matriz 9*9 y comparar con hacerlo 9 veces
        File archivoEntrada = new File("src/taller/images/jennie.jpg");

        float[][] matriz = {
                {1f/9, 1f/9, 1f/9},
                {1f/9, 1f/9, 1f/9},
                {1f/9, 1f/9, 1f/9}
        };

        float[] matriz2 = {
                0, 1, 0,
                1, 4, 1,
                0, 1, 0
        };

        float[] gaussian = {
                1f/16, 2f/16, 1f/16,
                2f/16, 4f/16, 2f/16,
                1f/16, 2f/16, 1f/16
        };

        float[] edge = {
                -1, -1, -1,
                -1,  8, -1,
                -1, -1, -1
        };

        float[] sharpen = {
                0, -1,  0,
                -1,  5, -1,
                0, -1,  0
        };

        int r, g, b ;
        int indice;
        float sumaR;
        float sumaG;
        float sumaB;

        try {
            BufferedImage imagenOriginal = ImageIO.read(archivoEntrada);
            int ancho = imagenOriginal.getWidth();
            int alto = imagenOriginal.getHeight();

            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            for (int y = 1; y < alto - 1; y++) {
                for (int x = 1; x < ancho - 1; x++) {
                    // Reinicio cada vez que cambia de pixel
                    sumaR = sumaG = sumaB = 0;
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            int pixel = imagenOriginal.getRGB(x+i, y+j);

                            r = (pixel >> 16) & 0xFF;
                            g = (pixel >> 8) & 0xFF;
                            b = pixel & 0xFF;

                            sumaR = sumaR + r*matriz[i+1][j+1];
                            sumaG = sumaG + g*matriz[i+1][j+1];
                            sumaB = sumaB + b*matriz[i+1][j+1];
                        }
                    }

                    r = (int) sumaR;
                    g = (int) sumaG;
                    b = (int) sumaB;

                    int pixelNuevo = (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }
            ImageIO.write(resultado, "jpg", new File("src/taller/images/jennieConv6.jpg"));

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }
}
