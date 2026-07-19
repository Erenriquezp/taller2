package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EjercicioPropuestoBufferAcumulacion {
    public static void main(String[] args) {
        File entrada = new File("taza.jpg");

        try {
            BufferedImage imagen = ImageIO.read(entrada);
            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            float[] factores = {1.0f, 0.75f, 0.50f, 0.25f};

            for (int f = 0; f < factores.length; f++) {
                float factor = factores[f];
                float[] bufferR = new float[ancho * alto];
                float[] bufferG = new float[ancho * alto];
                float[] bufferB = new float[ancho * alto];
                // GL_LOAD
                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {
                        int index = y * ancho + x;
                        int pixel = imagen.getRGB(x, y);

                        int r = (pixel >> 16) & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int b = pixel & 0xFF;

                        bufferR[index] = r;
                        bufferG[index] = g;
                        bufferB[index] = b;
                    }
                }
                // GL_MULT
                for (int i = 0; i < bufferR.length; i++) {
                    bufferR[i] *= factor;
                    bufferG[i] *= factor;
                    bufferB[i] *= factor;
                }
                BufferedImage resultado =
                        new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

                // GL_RETURN
                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {
                        int index = y * ancho + x;

                        int r = Math.clamp((int) bufferR[index], 0, 255);
                        int g = Math.clamp((int) bufferG[index], 0, 255);
                        int b = Math.clamp((int) bufferB[index], 0, 255);

                        int pixelNuevo = (r << 16) | (g << 8) | b;

                        resultado.setRGB(x, y, pixelNuevo);
                    }
                }
                String nombreSalida = "taza_factor_" + factor + ".png";
                ImageIO.write(resultado, "png", new File(nombreSalida));
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }
}