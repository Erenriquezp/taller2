package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Plantilla {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/salida_plantilla.png");

        try {
            // 1) Leer imagen origen
            BufferedImage original = ImageIO.read(entrada);
            if (original == null) {
                System.out.println("No se pudo leer la imagen de entrada.");
                return;
            }

            int ancho = original.getWidth();
            int alto = original.getHeight();

            // 2) Crear imagen destino (ARGB conserva transparencia)
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            // 3) Recorrer pixeles (fila por fila)
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = original.getRGB(x, y);

                    // 4) Extraer canales con bits
                    int a = (pixel >> 24) & 0xFF;
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    // 5) Aplicar fórmula del filtro (ejemplo: escala de grises)
                    int gris = (r + g + b) / 3;
                    r = gris;
                    g = gris;
                    b = gris;

                    // 6) Reconstruir y guardar pixel
                    int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            // 7) Escribir imagen de salida
            ImageIO.write(resultado, "png", salida);
            System.out.println("Plantilla ejecutada. Imagen guardada en: " + salida.getPath());

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }
}
