package BufferAcumulacion.taller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProfundidadDeCampo {

    public static void main(String[] args) {

        // Imagen de entrada (puede ser cualquier imagen)
        File archivoOriginal = new File("src/BufferAcumulacion/imagenes/ahy.jpg");

        // Imagen donde se guardará el resultado
        File archivoNuevo = new File("src/BufferAcumulacion/imagenes/tazaProfundidadDeCampo.png");

        // Número de renders que se van a acumular
        int muestras = 24;

        // Radio máximo del desplazamiento de la cámara (apertura):
        // a más apertura, más borroso queda lo que está fuera de foco
        float apertura = 12f;

        // Radio de la zona enfocada, como fracción del lado menor de la imagen
        float factorFocal = 0.35f;

        // Ancho (en píxeles) de la transición entre zona nítida y zona borrosa
        float transicion = 100f;

        try {

            // Cargar imagen original
            BufferedImage imagen = ImageIO.read(archivoOriginal);

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            // Centro y radio del plano focal (por defecto, el centro de la imagen)
            float centroX = ancho / 2f;
            float centroY = alto / 2f;
            float radioFocal = factorFocal * Math.min(ancho, alto);

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
             * Se acumulan N "renders" de la imagen, cada uno con la
             * cámara desplazada un poco (jitter).
             *
             * Los píxeles del plano focal no se desplazan: caen
             * siempre en el mismo lugar y quedan nítidos. Cuanto más
             * lejos está un píxel del plano focal, más se desplaza,
             * y el promedio de los N renders lo desenfoca.
             *
             * El primer render (i = 0) equivale a GL_LOAD con factor 1/N;
             * los siguientes equivalen a GL_ACCUM con factor 1/N.
             */

            for (int i = 0; i < muestras; i++) {

                /*
                 * Desplazamiento de la cámara para este render.
                 * Se reparte en espiral dentro de un disco (apertura).
                 */
                float radio = apertura * (float) Math.sqrt((i + 0.5f) / muestras);
                float angulo = i * 2.399963f;

                float dx = radio * (float) Math.cos(angulo);
                float dy = radio * (float) Math.sin(angulo);

                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {

                        // Convertir coordenadas (x,y) en posición lineal
                        int index = y * ancho + x;

                        /*
                         * Distancia de este píxel al centro del plano
                         * focal. Dentro del radio focal el factor es 0
                         * (sin desplazamiento); fuera crece hasta 1.
                         */
                        float distX = x - centroX;
                        float distY = y - centroY;
                        float dist = (float) Math.sqrt(distX * distX + distY * distY);

                        float factor = Math.clamp((dist - radioFocal) / transicion, 0f, 1f);

                        /*
                         * Píxel de origen desplazado según la cámara.
                         * Se limita al borde para no salir de la imagen.
                         */
                        int origenX = Math.clamp(Math.round(x + dx * factor), 0, ancho - 1);
                        int origenY = Math.clamp(Math.round(y + dy * factor), 0, alto - 1);

                        // Leer píxel desplazado
                        int pixel = imagen.getRGB(origenX, origenY);

                        int r = (pixel >> 16) & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int b = pixel & 0xFF;

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

            System.out.println("Profundidad de campo generada correctamente.");
            System.out.println("Centro nitido (radio " + (int) radioFocal + " px), fondo promediado con " + muestras + " renders.");

        } catch (IOException e) {
            e.getMessage();
        }
    }
}
