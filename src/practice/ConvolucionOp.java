package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;

public class ConvolucionOp {
    public static void main(String[] args) throws Exception {
        float[] matrizKernel = {
                1f/9, 1f/9, 1f/9,
                1f/9, 1f/9, 1f/9,
                1f/9, 1f/9, 1f/9
        };

        int tamano = 3;
        Kernel kernel = new Kernel(tamano, tamano, matrizKernel);

        ConvolveOp operacion = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage imagenOriginal = ImageIO.read(new File("src/practice/images/original2.png"));
        BufferedImage resultado = operacion.filter(imagenOriginal, null);

        ImageIO.write(resultado, "png", new File("src/practice/images/salida_convolucion_op.png"));
    }
}
