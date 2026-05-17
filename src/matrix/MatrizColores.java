package matrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MatrizColores {
    static void main() {
        File entrada = new File("src/taller/images/original.png");
        File salida = new File("src/taller/images/salida_colores2.png");

        float[][] colores_gris = {
                {0.299f, 0.587f, 0.114f, 0.0f},
                {0.299f, 0.587f, 0.114f, 0.0f},
                {0.299f, 0.587f, 0.114f, 0.0f},
                {0.0f, 0.0f, 0.0f, 1.0f},
        };

        float[][] sepiaMatrix = {
                {0.393f, 0.769f, 0.189f, 0.0f},
                {0.349f, 0.686f, 0.168f, 0.0f},
                {0.272f, 0.534f, 0.131f, 0.0f},
                {0.0f, 0.0f, 0.0f, 1.0f},
        };

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
                    int r1 = (int) (sepiaMatrix[0][0]*r + sepiaMatrix[0][1]*g + sepiaMatrix[0][2]*b);
                    int g1 = (int) (sepiaMatrix[1][0]*r + sepiaMatrix[1][1]*g + sepiaMatrix[1][2]*b);
                    int b1 = (int) (sepiaMatrix[2][0]*r + sepiaMatrix[2][1]*g + sepiaMatrix[2][2]*b);

                    r1 = Math.min(255, Math.max(0, r1));
                    g1 = Math.min(255, Math.max(0, g1));
                    b1 = Math.min(255, Math.max(0, b1));
                    
                    // 6) Reconstruir y guardar pixel
                    int pixelNuevo = (a << 24) | (r1 << 16) | (g1 << 8) | b1;
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
