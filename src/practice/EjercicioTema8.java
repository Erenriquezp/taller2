package practice;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EjercicioTema8 {
    public static void main(String[] args) {
        try {
            File entradaFondo = new File("paisaje.png");
            File entradaFiltro = new File("universo.png");
            File archivoSalida = new File("ExamenTema8.png");

            BufferedImage imgPaisaje = ImageIO.read(entradaFondo);
            BufferedImage imgUniversoOriginal = ImageIO.read(entradaFiltro);

            int ancho = imgPaisaje.getWidth();
            int alto = imgPaisaje.getHeight();

            // Escalamos imagen del universo
            Image imgTemporal = imgUniversoOriginal.getScaledInstance(ancho, alto, Image.SCALE_FAST);
            BufferedImage imgUniverso = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D grTmp = imgUniverso.createGraphics();
            grTmp.drawImage(imgTemporal, 0, 0, null);
            grTmp.dispose();

            BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

            float[][] zBuffer = new float[ancho][alto];
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    zBuffer[x][y] = 9999.0f;
                }
            }
            // Variables para el Stencil Test
            int centroX = ancho / 2;
            int centroY = alto / 2;
            int radio = Math.min(ancho, alto) / 3;

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    // capa 1: paisaje
                    float zPaisaje = 10.0f;
                    int colorPaisaje = imgPaisaje.getRGB(x, y);
                    // Depth Test para el paisaje
                    if (zPaisaje < zBuffer[x][y]) {
                        zBuffer[x][y] = zPaisaje;
                        salida.setRGB(x, y, colorPaisaje);
                    }
                    // capa 2: universo
                    float zUniverso = 5.0f;
                    int colorUniverso = imgUniverso.getRGB(x, y);
                    // Stencil Test (Lente circular)
                    int dx = x - centroX;
                    int dy = y - centroY;

                    if (dx * dx + dy * dy <= radio * radio) {
                        // Alpha Test (Brillo promedio)
                        Color c = new Color(colorUniverso, true);
                        int promedio = (c.getRed() + c.getGreen() + c.getBlue()) / 3;

                        if (promedio > 128) {
                            //Depth Test (Comparación de profundidad)
                            if (zUniverso < zBuffer[x][y]) {
                                zBuffer[x][y] = zUniverso;
                                salida.setRGB(x, y, colorUniverso);
                            }
                        }
                    }
                }
            }

            ImageIO.write(salida, "png", archivoSalida);
            System.out.println("Imagen generada correctamente.");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}