package transparence;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TripleBlending {

    public static void main(String[] args) {
        File imgFile1 = new File("src/transparence/original.png");
        File imgFile2 = new File("src/transparence/fondo.jpg");
        File imgFile3 = new File("src/transparence/fondo3.jpg");
        File salida = new File("src/transparence/salida_triple2.jpg");

        try {
            BufferedImage img1 = ImageIO.read(imgFile1);
            BufferedImage img2 = ImageIO.read(imgFile2);
            BufferedImage img3 = ImageIO.read(imgFile3);

            if (img1 == null || img2 == null || img3 == null) {
                System.err.println("Error: One or more images missing.");
                return;
            }

            int w = img1.getWidth();
            int h = img1.getHeight();

            BufferedImage scaled2 = scaleImage(img2, w, h);
            BufferedImage scaled3 = scaleImage(img3, w, h);

            BufferedImage resultado = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            float alpha1 = 0.5f;
            float alpha2 = 0.3f;
            float alpha3 = 0.2f;

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int p1 = img1.getRGB(x, y);
                    int p2 = scaled2.getRGB(x, y);
                    int p3 = scaled3.getRGB(x, y);

                    int r1 = (p1 >> 16) & 0xFF;
                    int g1 = (p1 >> 8)  & 0xFF;
                    int b1 =  p1        & 0xFF;

                    int r2 = (p2 >> 16) & 0xFF;
                    int g2 = (p2 >> 8)  & 0xFF;
                    int b2 =  p2        & 0xFF;

                    int r3 = (p3 >> 16) & 0xFF;
                    int g3 = (p3 >> 8)  & 0xFF;
                    int b3 =  p3        & 0xFF;

                    float resR = (r1 * alpha1) + (r2 * alpha2) + (r3 * alpha3);
                    float resG = (g1 * alpha1) + (g2 * alpha2) + (g3 * alpha3);
                    float resB = (b1 * alpha1) + (b2 * alpha2) + (b3 * alpha3);

                    int r = Math.min(255, (int) resR);
                    int g = Math.min(255, (int) resG);
                    int b = Math.min(255, (int) resB);

                    int pixelNuevo = (r << 16) | (g << 8) | b;
                    resultado.setRGB(x, y, pixelNuevo);
                }
            }

            ImageIO.write(resultado, "jpg", salida);
            System.out.println("Triple blend complete: " + salida.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Helper method to keep the main code clean
    private static BufferedImage scaleImage(BufferedImage source, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }
}