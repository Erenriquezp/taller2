package transparence;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Blending {

    public static void main(String[] args) {
        File entrada = new File("src/transparence/original.png");
        File fondo = new File("src/transparence/fondo.jpg");
        File salida = new File("src/transparence/salida_blending3.jpg");

        try {
            BufferedImage original = ImageIO.read(entrada);
            BufferedImage fondoImg = ImageIO.read(fondo);

            if (original == null || fondoImg == null) {
                System.err.println("Error: One or both input images could not be found.");
                return;
            }

            // We use the dimensions of the 'original' image as our target
            int ancho = original.getWidth();
            int alto = original.getHeight();

            // 1. Resize the background to match the original
            BufferedImage fondoResized = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = fondoResized.createGraphics();
            // Using Hint for better scaling quality
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(fondoImg, 0, 0, ancho, alto, null);
            g2d.dispose();

            // 2. Prepare the result buffer
            BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            // alpha = opacity of the BACKGROUND (0.3 = 30% background, 70% original)
            float alpha = 0.3f;

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pOrig = original.getRGB(x, y);
                    int pFondo = fondoResized.getRGB(x, y);

                    // Extract channels for Original (The Main Image)
                    int rO = (pOrig >> 16) & 0xFF;
                    int gO = (pOrig >> 8) & 0xFF;
                    int bO = pOrig & 0xFF;

                    // Extract channels for Fondo (The Background)
                    int rF = (pFondo >> 16) & 0xFF;
                    int gF = (pFondo >> 8) & 0xFF;
                    int bF = pFondo & 0xFF;

                    // BLENDING: (70% Original) + (30% Fondo)
                    int r = (int) ((1 - alpha) * rO + alpha * rF);
                    int g = (int) ((1 - alpha) * gO + alpha * gF);
                    int b = (int) ((1 - alpha) * bO + alpha * bF);

                    // Reconstruct and set
                    int pixelNuevo = (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(resultado, "jpg", salida);
            System.out.println("Success! Blended image saved to: " + salida.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }
}