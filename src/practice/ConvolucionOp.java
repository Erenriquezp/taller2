package practice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;

public class ConvolucionOp {
    public static void main(String[] args) throws Exception {
        // Convolución usando la librería de Java (ConvolveOp)
        // Un kernel 3x3 de blur con pesos iguales.
        float[] matrizKernel = {
                1f/9, 1f/9, 1f/9,
                1f/9, 1f/9, 1f/9,
                1f/9, 1f/9, 1f/9
        };

        // Crear objeto Kernel: el tamano es 3 (3x3).
        int tamano = 3;
        Kernel kernel = new Kernel(tamano, tamano, matrizKernel);

        // Crear la operación de convolución (EDGE_NO_OP = no procesa los bordes).
        ConvolveOp operacion = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage imagenOriginal = ImageIO.read(new File("src/practice/images/original.png"));
        BufferedImage resultado = operacion.filter(imagenOriginal, null);

        ImageIO.write(resultado, "png", new File("src/practice/images/salida_convolucion_op.png"));
        System.out.println("ConvolveOp generada en: src/practice/images/salida_convolucion_op.png");
    }
}
