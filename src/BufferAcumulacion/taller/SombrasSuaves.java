package BufferAcumulacion.taller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SombrasSuaves {

    public static void main(String[] args) {

        // Imagen de entrada (puede ser cualquier imagen)
        File archivoOriginal = new File("src/BufferAcumulacion/imagenes/jen.jpg");

        // Imagen donde se guardará el resultado
        File archivoNuevo = new File("src/BufferAcumulacion/imagenes/tazaSombrasSuaves.png");

        // Número de posiciones de luz que se van a acumular
        int muestras = 24;

        // Radio de la fuente de luz extendida (jitter de la sombra).
        // Con radioLuz = 0 la sombra sale dura (una sola luz puntual).
        float radioLuz = 18f;

        // Cuánto oscurece la sombra al fondo (0 = negro, 1 = sin sombra)
        float oscuridad = 0.55f;

        // Margen del lienzo alrededor de la imagen
        int margen = 100;

        // Desplazamiento base de la sombra respecto a la imagen
        int desplazX = 35;
        int desplazY = 45;

        try {

            // Cargar imagen original
            BufferedImage imagen = ImageIO.read(archivoOriginal);

            int imgAncho = imagen.getWidth();
            int imgAlto = imagen.getHeight();

            // El lienzo es más grande que la imagen para que quepa la sombra
            int ancho = imgAncho + 2 * margen;
            int alto = imgAlto + 2 * margen;

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

            // Peso de cada render: 1/N para que el promedio sea correcto
            float peso = 1f / muestras;

            // ====================================================
            // GL_LOAD + GL_ACCUM
            // ====================================================
            /*
             * Se acumulan N renders de la misma escena: la imagen
             * "flotando" sobre un fondo claro y proyectando sombra.
             *
             * En cada render la luz está en una posición ligeramente
             * distinta, así que la sombra (dura en cada render
             * individual) cae en un lugar distinto. Al promediar,
             * los bordes donde las sombras no coinciden quedan a
             * medio oscurecer: eso es la penumbra.
             *
             * La imagen no depende de la luz: cae siempre en el
             * mismo lugar y queda idéntica.
             *
             * El primer render (i = 0) equivale a GL_LOAD con factor 1/N;
             * los siguientes equivalen a GL_ACCUM con factor 1/N.
             */

            for (int i = 0; i < muestras; i++) {

                /*
                 * Posición de la luz para este render.
                 * Se reparte en espiral dentro de un disco (área de la luz).
                 */
                float radio = radioLuz * (float) Math.sqrt((i + 0.5f) / muestras);
                float angulo = i * 2.399963f;

                int jx = Math.round(radio * (float) Math.cos(angulo));
                int jy = Math.round(radio * (float) Math.sin(angulo));

                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {

                        // Convertir coordenadas (x,y) en posición lineal
                        int index = y * ancho + x;

                        // Fondo claro del lienzo
                        int r = 244;
                        int g = 244;
                        int b = 240;

                        /*
                         * Sombra proyectada: la silueta rectangular de
                         * la imagen, desplazada por la posición de la
                         * luz de este render.
                         */
                        int sombraX = x - (margen + desplazX + jx);
                        int sombraY = y - (margen + desplazY + jy);

                        if (sombraX >= 0 && sombraX < imgAncho
                                && sombraY >= 0 && sombraY < imgAlto) {
                            r = (int) (r * oscuridad);
                            g = (int) (g * oscuridad);
                            b = (int) (b * oscuridad);
                        }

                        /*
                         * La imagen se dibuja encima, siempre en el
                         * mismo lugar (no depende de la luz).
                         */
                        int imgX = x - margen;
                        int imgY = y - margen;

                        if (imgX >= 0 && imgX < imgAncho
                                && imgY >= 0 && imgY < imgAlto) {

                            int pixel = imagen.getRGB(imgX, imgY);

                            r = (pixel >> 16) & 0xFF;
                            g = (pixel >> 8) & 0xFF;
                            b = pixel & 0xFF;
                        }

                        // Acumular en el buffer con peso 1/N
                        bufferR[index] += r * peso;
                        bufferG[index] += g * peso;
                        bufferB[index] += b * peso;
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
                    int pixelNuevo = (nuevoR << 16) | (nuevoG << 8) | nuevoB;

                    // Guardar el píxel en la imagen resultado
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            // Guardar imagen final
            ImageIO.write(resultado, "png", archivoNuevo);

            System.out.println("Sombras suaves generadas correctamente.");
            System.out.println("Sombra promediada con " + muestras + " posiciones de luz (radio " + (int) radioLuz + " px).");

        } catch (IOException e) {
            e.getMessage();
        }
    }
}
