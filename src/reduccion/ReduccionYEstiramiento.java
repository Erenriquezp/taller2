package reduccion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReduccionYEstiramiento {

    public static void main(String[] args) {
        File archivoEntrada = new File("src/tarea3/images/original2.png");

        try {
            BufferedImage imagenOriginal = ImageIO.read(archivoEntrada);
            if (imagenOriginal == null) throw new IOException("No se encontró la imagen original.");

            System.out.println("Procesando imágenes...");

            // Ejecutamos los 3 métodos reutilizando la misma lógica base
            ImageIO.write(procesarFiltro(imagenOriginal, "BINARIO"), "png", new File("src/tarea3/images/Binario.png"));
            ImageIO.write(procesarFiltro(imagenOriginal, "DECIMAL"), "png", new File("src/tarea3/images/Decimal.png"));
            ImageIO.write(procesarFiltro(imagenOriginal, "HEXADECIMAL"), "png", new File("src/tarea3/images/Hexadecimal.png"));

            System.out.println("¡Las 3 imágenes (.png) fueron creadas con éxito!");

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }

    /**
     * Procesa la imagen reduciendo a 4 bits y estirando según el método indicado.
     */
    private static BufferedImage procesarFiltro(BufferedImage original, String metodoEstiramiento) {
        int ancho = original.getWidth();
        int alto = original.getHeight();

        // Usamos ARGB para asegurar que la transparencia no se pierda al guardar
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);

                // 1. Extracción (0xFF aísla los últimos 8 bits)
                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // 2. Reducción a 4 bits (desplazamos para perder los bits menos significativos)
                r >>= 4;
                g >>= 4;
                b >>= 4;

                // 3. Estiramiento dinámico
                switch (metodoEstiramiento) {
                    case "BINARIO":
                        // Replicación desplazando y uniendo
                        r = (r << 4) | r;
                        g = (g << 4) | g;
                        b = (b << 4) | b;
                        break;

                    case "DECIMAL":
                        // Regla de tres simple (valor * máximo_nuevo / máximo_actual)
                        r = (r * 255) / 15;
                        g = (g * 255) / 15;
                        b = (b * 255) / 15;
                        break;

                    case "HEXADECIMAL":
                        // Replicación usando máscaras hexadecimales (0x0F = 15)
                        r = (r << 4) | (r & 0x0F);
                        g = (g << 4) | (g & 0x0F);
                        b = (b << 4) | (b & 0x0F);
                        break;
                }

                // 4. Reconstrucción
                int pixelNuevo = (a << 24) | (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }
        return resultado;
    }
}