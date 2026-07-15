package BufferAcumulacion.taller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ComposicionPrecisa {

    public static void main(String[] args) {

        // Imagen de entrada (puede ser cualquier imagen)
        File archivoOriginal = new File("src/BufferAcumulacion/imagenes/zuha.jpg");

        // Imagen donde se guardará el resultado
        File archivoNuevo = new File("src/BufferAcumulacion/imagenes/tazaComposicionPrecisa.png");

        // Número de pasadas en que se acumula la imagen
        int muestras = 32;

        // Niveles que puede representar el buffer de baja precisión
        int niveles = 16;

        try {

            // Cargar imagen original
            BufferedImage imagen = ImageIO.read(archivoOriginal);

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            // Mitad superior: baja precisión (banding). Mitad inferior: buffer float.
            int mitad = alto / 2;

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

            // Peso de cada pasada: 1/N para que la suma total sea la imagen completa
            float peso = 1f / muestras;

            // ====================================================
            // GL_LOAD + GL_ACCUM
            // ====================================================
            /*
             * La imagen se reconstruye acumulando N pasadas tenues
             * (cada una aporta 1/N del color final), igual que una
             * composición real de varios renders.
             *
             * Gracias a que el buffer es float, las fracciones
             * pequeñas no se pierden por redondeo en el camino.
             *
             * La primera pasada (i = 0) equivale a GL_LOAD con factor 1/N;
             * las siguientes equivalen a GL_ACCUM con factor 1/N.
             */

            for (int i = 0; i < muestras; i++) {

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
             *
             * La mitad superior se guarda como si el buffer solo
             * tuviera unos pocos bits por canal: los valores se
             * fuerzan al nivel representable más cercano y aparece
             * el banding. La mitad inferior conserva la precisión
             * del buffer float y los degradados quedan continuos.
             */

            float paso = 255f / (niveles - 1);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    int index = y * ancho + x;

                    float valorR = bufferR[index];
                    float valorG = bufferG[index];
                    float valorB = bufferB[index];

                    if (y < mitad) {

                        /*
                         * Cuantizar al nivel más cercano:
                         * esto es lo que pasa cuando la precisión
                         * del buffer no alcanza.
                         */
                        valorR = Math.round(valorR / paso) * paso;
                        valorG = Math.round(valorG / paso) * paso;
                        valorB = Math.round(valorB / paso) * paso;
                    }

                    /*
                     * Limitar los valores RGB al rango válido
                     * [0,255].
                     */
                    int nuevoR = Math.clamp((int) valorR, 0, 255);
                    int nuevoG = Math.clamp((int) valorG, 0, 255);
                    int nuevoB = Math.clamp((int) valorB, 0, 255);

                    // Línea divisoria entre las dos mitades
                    if (y == mitad || y == mitad - 1) {
                        nuevoR = 51; nuevoG = 51; nuevoB = 51;
                    }

                    // Reconstruir el píxel RGB
                    int pixelNuevo = (nuevoR << 16) | (nuevoG << 8) | nuevoB;

                    // Guardar el píxel en la imagen resultado
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            // Guardar imagen final
            ImageIO.write(resultado, "png", archivoNuevo);

            System.out.println("Composicion precisa generada correctamente.");
            System.out.println("Arriba: " + niveles + " niveles (banding) | Abajo: buffer float (continuo).");

        } catch (IOException e) {
            e.getMessage();
        }
    }
}
