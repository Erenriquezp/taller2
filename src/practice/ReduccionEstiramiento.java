package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ReduccionEstiramiento {
    public static void main(String[] args) throws Exception {
        File entrada = new File("src/practice/images/original2.png");
        File salida = new File("src/practice/images/salida_reduccion_estiramiento.png");

        BufferedImage imagenOriginal = ImageIO.read(entrada);
        if (imagenOriginal == null) throw new IllegalArgumentException("No se pudo leer: " + entrada.getPath());

        int ancho = imagenOriginal.getWidth();
        int alto = imagenOriginal.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagenOriginal.getRGB(x, y);
                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // PASO 1: Reducir a 4 bits (dividir entre 16)
                r = r >> 4;
                g = g >> 4;
                b = b >> 4;

                // PASO 2: Estirar a 8 bits
                // Elige UNO de estos 3 metodos:

                // Metodo DECIMAL (actual): regla de 3, mapea 0..15 a 0..255
                r = (r * 255) / 15;
                g = (g * 255) / 15;
                b = (b * 255) / 15;

                // Metodo BINARIO (alternativa): replica el nibble 4 bits
                // r = (r << 4) | r;
                // g = (g << 4) | g;
                // b = (b << 4) | b;

                // Metodo HEXADECIMAL (alternativa): replicar con máscara
                // r = (r << 4) | (r & 0x0F);
                // g = (g << 4) | (g & 0x0F);
                // b = (b << 4) | (b & 0x0F);

                int nuevoPixel = (a << 24) | (r << 16) | (g << 8) | b;
                resultado.setRGB(x, y, nuevoPixel);
            }
        }

        ImageIO.write(resultado, "png", salida);
        System.out.println("Reduccion y estiramiento generado en: " + salida.getPath());
    }
}
