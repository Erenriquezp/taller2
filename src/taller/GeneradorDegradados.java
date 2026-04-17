package taller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GeneradorDegradados {

    public static void main(String[] args) {
        int ancho = 800;
        int alto = 800;

        // Aseguramos que la carpeta exista
        new File("src/taller/images").mkdirs();

        try {
            System.out.println("Generando imágenes de Azul a Blanco...");
            generarIzquierdaDerecha(ancho, alto);
            generarDerechaIzquierda(ancho, alto);
            generarArribaAbajo(ancho, alto);
            generarAbajoArriba(ancho, alto);
            generarRadialCentro(ancho, alto);
            System.out.println("¡Todas las imágenes han sido generadas con éxito!");
        } catch (IOException e) {
            System.err.println("Error al guardar las imágenes: " + e.getMessage());
        }
    }

    // 1. Degradado Lineal: Izquierda (Azul) a Derecha (Blanco)
    private static void generarIzquierdaDerecha(int ancho, int alto) throws IOException {
        BufferedImage bufer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                // El factor aumenta de 0.0 a 1.0 hacia la derecha
                double factorBlanco = (double) x / ancho;

                int r = (int) (255 * factorBlanco);
                int g = (int) (255 * factorBlanco);
                int b = 255; // El azul siempre está encendido al máximo

                int pixel = (r << 16) | (g << 8) | b;
                bufer.setRGB(x, y, pixel);
            }
        }
        ImageIO.write(bufer, "jpg", new File("src/taller/images/1_izq_der_blanco.jpg"));
    }

    // 2. Degradado Lineal: Derecha (Azul) a Izquierda (Blanco)
    private static void generarDerechaIzquierda(int ancho, int alto) throws IOException {
        BufferedImage bufer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                // El factor aumenta de 0.0 a 1.0 hacia la izquierda
                double factorBlanco = (double) (ancho - x) / ancho;

                int r = (int) (255 * factorBlanco);
                int g = (int) (255 * factorBlanco);
                int b = 255;

                int pixel = (r << 16) | (g << 8) | b;
                bufer.setRGB(x, y, pixel);
            }
        }
        ImageIO.write(bufer, "jpg", new File("src/taller/images/2_der_izq_blanco.jpg"));
    }

    // 3. Degradado Lineal: Arriba (Azul) a Abajo (Blanco)
    private static void generarArribaAbajo(int ancho, int alto) throws IOException {
        BufferedImage bufer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                // El factor aumenta de 0.0 a 1.0 hacia abajo
                double factorBlanco = (double) y / alto;

                int r = (int) (255 * factorBlanco);
                int g = (int) (255 * factorBlanco);
                int b = 255;

                int pixel = (r << 16) | (g << 8) | b;
                bufer.setRGB(x, y, pixel);
            }
        }
        ImageIO.write(bufer, "jpg", new File("src/taller/images/3_arr_aba_blanco.jpg"));
    }

    // 4. Degradado Lineal: Abajo (Azul) a Arriba (Blanco)
    private static void generarAbajoArriba(int ancho, int alto) throws IOException {
        BufferedImage bufer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                // El factor aumenta de 0.0 a 1.0 hacia arriba
                double factorBlanco = (double) (alto - y) / alto;

                int r = (int) (255 * factorBlanco);
                int g = (int) (255 * factorBlanco);
                int b = 255;

                int pixel = (r << 16) | (g << 8) | b;
                bufer.setRGB(x, y, pixel);
            }
        }
        ImageIO.write(bufer, "jpg", new File("src/taller/images/4_aba_arr_blanco.jpg"));
    }

    // 5. Degradado Radial: Centro (Azul) a Esquinas (Blanco)
    private static void generarRadialCentro(int ancho, int alto) throws IOException {
        BufferedImage bufer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        double cx = ancho / 2.0;
        double cy = alto / 2.0;
        double distanciaMax = Math.sqrt((cx * cx) + (cy * cy));

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                double distanciaActual = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));

                // A medida que nos alejamos del centro, el factor se acerca a 1.0 (Blanco)
                double factorBlanco = distanciaActual / distanciaMax;

                // Aseguramos que no sobrepase 1.0 por errores de precisión
                factorBlanco = Math.min(1.0, Math.max(0.0, factorBlanco));

                int r = (int) (255 * factorBlanco);
                int g = (int) (255 * factorBlanco);
                int b = 255;

                int pixel = (r << 16) | (g << 8) | b;
                bufer.setRGB(x, y, pixel);
            }
        }
        ImageIO.write(bufer, "jpg", new File("src/taller/images/5_radial_centro_blanco.jpg"));
    }
}