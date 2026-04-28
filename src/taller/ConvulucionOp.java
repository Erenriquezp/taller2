package taller;

import taller.images.Kernels;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class ConvulucionOp {
    static void main() {
        File archivoEntrada = new File("src/taller/images/jennie.jpg");
        float[] matriz = Kernels.kBordes;
        Kernel kernel = new Kernel((int) Math.sqrt(matriz.length), (int) Math.sqrt(matriz.length), matriz);

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        try {
            BufferedImage imagenOriginal = ImageIO.read(archivoEntrada);
            BufferedImage resultado = null;
            for (int i = 0; i < 9; i++) {
              resultado = op.filter(imagenOriginal, null);
            }
            ImageIO.write(resultado, "jpg", new File("src/taller/images/jennieConvOp.jpg"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
