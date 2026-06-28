package practice;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Practice {
    public static void main(String[] args) {

        File entrada = new File("src/practice/images/fondo.jpg");
        File fondo = new File("src/practice/images/jennie.jpg");
        File salida = new File("src/practice/images/salida.jpg");

        try {
            BufferedImage original = ImageIO.read(entrada);
            BufferedImage fondoB = ImageIO.read(fondo);
            int ancho = original.getWidth();
            int alto = original.getHeight();
            BufferedImage resultado = new BufferedImage(ancho,alto,BufferedImage.TYPE_INT_RGB);
            BufferedImage fondoE = escalarImagen(fondoB, ancho, alto);

            float alpha = 0.6f;
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixelO = original.getRGB(x,y);
                    int pixelF = fondoE.getRGB(x,y);

                    int rO = (pixelO >> 16) & 0xFF;
                    int gO = (pixelO >> 8) & 0xFF;
                    int bO = (pixelO) & 0xFF;
                    int rF = (pixelF >> 16) & 0xFF;
                    int gF = (pixelF >> 8) & 0xFF;
                    int bF = (pixelF) & 0xFF;

                    int r = Math.clamp((int) ((1 - alpha) * rO + alpha * rF),0,255);
                    int g = Math.clamp((int) ((1 - alpha) * gO + alpha * gF),0,255);
                    int b = Math.clamp((int) ((1 - alpha) * bO + alpha * bF), 0,255);

                    int pixelNuevo = r << 16 | g << 8 | b;
                    resultado.setRGB(x,y,pixelNuevo);
                }
            }
            ImageIO.write(resultado,"jpg",salida);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage escalarImagen(BufferedImage fuente, int ancho, int alto) {
        BufferedImage escalado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = escalado.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(fuente, ancho, alto, null);
        g2.dispose();
        return escalado;
    }
}
