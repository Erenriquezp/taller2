package BufferAcumulacion.taller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BufferAcumulacion {

    public static void main(String[] args) {

        // Imagen de entrada
        File archivoOriginal = new File("src/BufferAcumulacion/imagenes/taza.jpg");

        // Imagen donde se guardará el resultado
        File archivoNuevo = new File("src/BufferAcumulacion/imagenes/tazaBufferAcumulacion.jpg");

        // Número de copias que se van a acumular
        int muestras = 25;

        // Cantidad de píxeles que se desplaza cada copia
        int desplazamiento = 8;

        try {

            // Cargar imagen original
            BufferedImage imagen = ImageIO.read(archivoOriginal);

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            /*
             * Buffer de acumulación.
             * Aquí se almacenarán los valores RGB acumulados
             * antes de generar la imagen final.
             */
            float[] bufferR = new float[ancho * alto];
            float[] bufferG = new float[ancho * alto];
            float[] bufferB = new float[ancho * alto];

            // Imagen donde se escribirá el resultado final
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            // ====================================================
            // GL_LOAD
            // ====================================================
            /*
             * Carga la imagen original dentro del buffer.
             * Es el equivalente a GL_LOAD del buffer de acumulación.
             */

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    // Convertir coordenadas (x,y) en posición lineal
                    int index = y * ancho + x;

                    // Obtener el píxel original
                    int pixel = imagen.getRGB(x, y);

                    // Extraer componentes RGB
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    // Guardar los valores en el buffer
                    bufferR[index] = r;
                    bufferG[index] = g;
                    bufferB[index] = b;
                }
            }

            // ====================================================
            // GL_ACCUM
            // ====================================================
            /*
             * Acumula varias copias desplazadas de la imagen.
             *
             * Cada copia tiene un peso menor que la anterior.
             * Esto genera la estela o efecto de movimiento.
             */

            for (int i = 1; i < muestras; i++) {

                // Desplazamiento de esta copia
                int offset = i * desplazamiento;

                // Peso decreciente
                float peso = (float) Math.pow(0.85, i);

                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {

                        /*
                         * Se toma un píxel más a la izquierda
                         * para crear una estela hacia la derecha.
                         */
                        int origenX = x - offset;

                        if (origenX >= 0 && origenX < ancho) {

                            int index = y * ancho + x;

                            // Leer píxel desplazado
                            int pixel = imagen.getRGB(origenX, y);

                            int r = (pixel >> 16) & 0xFF;
                            int g = (pixel >> 8) & 0xFF;
                            int b = pixel & 0xFF;

                            /*
                             * Acumular en el buffer.
                             * Mientras más lejos esté la copia,
                             * menor será su contribución.
                             */
                            bufferR[index] += r * peso;
                            bufferG[index] += g * peso;
                            bufferB[index] += b * peso;
                        }
                    }
                }
            }

            // ====================================================
            // GL_RETURN
            // ====================================================
            /*
             * Convierte el contenido del buffer acumulado
             * nuevamente en una imagen.
             */

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    int index = y * ancho + x;

                    /*
                     * Limitar los valores RGB al rango válido
                     * [0,255].
                     */
                    int nuevoR = Math.clamp((int) bufferR[index], 0, 255);
                    int nuevoG = Math.clamp((int) bufferG[index], 0, 255);
                    int nuevoB = Math.clamp((int) bufferB[index], 0, 255);

                    // Reconstruir el píxel RGB
                    int pixelNuevo =(nuevoR << 16) |(nuevoG << 8) |nuevoB;

                    // Guardar el píxel en la imagen resultado
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            // Guardar imagen final
            ImageIO.write(resultado, "jpg", archivoNuevo);

            System.out.println("Buffer de acumulacion generado correctamente.");

        } catch (IOException e) {
            e.getMessage();
        }
    }
}