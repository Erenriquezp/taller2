package practice;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HSV {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original2.png");
        File salida = new File("src/practice/images/salida_hsv.png");

        // HSV Tono, saturacion, brillo
        float factorS = 1.3f; // >1 aumenta intensidad de color
        float factorV = 0.9f; // <1 baja brillo general

        try {
            BufferedImage original = ImageIO.read(entrada);
            if (original == null) {
                System.out.println("No se pudo leer la imagen de entrada.");
                return;
            }

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

                    float[] hsv = Color.RGBtoHSB(r, g, b, null);

                    float h = hsv[0];
                    float s = clamp01(hsv[1] * factorS);
                    float v = clamp01(hsv[2] * factorV);

                    int rgbNuevo = Color.HSBtoRGB(h, s, v);
                    int pixelNuevo = (a << 24) | (rgbNuevo & 0x00FFFFFF);
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(resultado, "png", salida);
            System.out.println("Filtro HSV aplicado. Imagen guardada en: " + salida.getPath());

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
        }
    }

    private static float clamp01(float valor) {
        // return Math.max(0f, Math.min(1f, valor));
        return Math.clamp(valor, 0f, 1f);
    }
}
