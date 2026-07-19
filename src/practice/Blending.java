package practice;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Blending {

    public static void main(String[] args) {
        File entrada = new File("src/practice/images/original2.png");
        File fondo = new File("src/transparence/fondo.jpg");
        File salida = new File("src/practice/images/salida_blending.jpg");

        try {
            BufferedImage original = ImageIO.read(entrada);
            BufferedImage fondoImg = ImageIO.read(fondo);

            int ancho = original.getWidth();
            int alto = original.getHeight();

            BufferedImage fondoRedimensionado = escalarImagen(fondoImg, ancho, alto);
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            float alpha = 0.3f;
            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pOrig = original.getRGB(x, y);
                    int pFondo = fondoRedimensionado.getRGB(x, y);

                    int rO = (pOrig >> 16) & 0xFF;
                    int gO = (pOrig >> 8) & 0xFF;
                    int bO = pOrig & 0xFF;

                    int rF = (pFondo >> 16) & 0xFF;
                    int gF = (pFondo >> 8) & 0xFF;
                    int bF = pFondo & 0xFF;

                    int r = Math.clamp((int) ((1 - alpha) * rO + alpha * rF),0,255);
                    int g = Math.clamp((int) ((1 - alpha) * gO + alpha * gF),0,255);
                    int b = Math.clamp((int) ((1 - alpha) * bO + alpha * bF), 0,255);

                    int pixelNuevo = (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }
            ImageIO.write(resultado, "jpg", salida);
            System.out.println("Blending guardado en: " + salida.getPath());
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }

    private static BufferedImage escalarImagen(BufferedImage fuente, int ancho, int alto) {
        BufferedImage redimensionada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = redimensionada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(fuente, 0, 0, ancho, alto, null);
        g2d.dispose();
        return redimensionada;
    }
}

