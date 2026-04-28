package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class Plantilla3 {
    public static void main(String[] args) {

        File entrada = new File("src/practice/images/original.png");
        File salida = new File("src/practice/images/salida_convolucionop.png");

//        float[][] kernel = {
//                {1/9f, 1/9f, 1/9f},
//                {1/9f, 1/9f, 1/9f},
//                {1/9f, 1/9f, 1/9f},
//        };

        float[] matriz = {
                1/9f, 1/9f, 1/9f,
                1/9f, 1/9f, 1/9f,
                1/9f, 1/9f, 1/9f,
        };
        Kernel kernel = new Kernel(3, 3, matriz);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        try {
            BufferedImage original = ImageIO.read(entrada);
            BufferedImage resultado = op.filter(original, null);
            ImageIO.write(resultado, "png", salida);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        try {
//            BufferedImage img = ImageIO.read(entrada);
//            int alto = img.getHeight();
//            int ancho = img.getWidth();
//
//            BufferedImage img2 = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
//
//            for (int y = 1; y < alto - 1; y++) {
//                for (int x = 1; x < ancho - 1; x++) {
//                    float sr = 0;
//                    float sg = 0;
//                    float sb = 0;
//
//                    for (int i = -1; i <= 1 ; i++) {
//                        for (int j = -1; j <= 1 ; j++) {
//                            int pixel =  img.getRGB(x + j, y + i);
//
//                            int r = (pixel >> 16) &  0xff;
//                            int g =  (pixel >> 8) &  0xff;
//                            int b =  (pixel) &  0xff;
//
//                            sr += r*kernel[i+1][j+1];
//                            sg += g*kernel[i+1][j+1];
//                            sb += b*kernel[i+1][j+1];
//
//                        }
//                    }
//                    int nr = (int) Math.clamp(sr, 0, 255);
//                    int nc = (int) Math.clamp(sg, 0, 255);
//                    int nb = (int) Math.clamp(sb, 0, 255);
//
//                    int pixelNuevo = (nr << 16 ) | (nc << 8) | nb;
//                    img2.setRGB(x, y, pixelNuevo);
//                }
//            }
//            ImageIO.write(img2, "png", salida);
//            System.out.println("Success");
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
