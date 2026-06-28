package BufferAcumulacion.taller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EjercicioPropuestoBufferAcumulacion {

    /**
     *EJERCICIO PROPUESTO
     * "Implementar un filtro de transición Día → Noche utilizando la operación GL_MULT del Buffer de Acumulación.
     * El programa debe generar 4 imágenes con factores de intensidad 1.0, 0.75, 0.50 y 0.25."
     */

    public static void main(String[] args) {


        File archivoOriginal = new File("src/BufferAcumulacion/imagenes/taza.jpg");

        try {

            BufferedImage imagen = ImageIO.read(archivoOriginal);

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            // Factores de iluminación
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

                        bufferR[index] = (pixel >> 16) & 0xFF;
                        bufferG[index] = (pixel >> 8) & 0xFF;
                        bufferB[index] = pixel & 0xFF;
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

                String nombreSalida =
                        "src/BufferAcumulacion/imagenes/taza_factor_" + factor + ".png";

                ImageIO.write(resultado, "png", new File(nombreSalida));

                System.out.println("Imagen creada: " + nombreSalida);
            }

        } catch (IOException e) {
            e.getMessage();
        }
    }
}