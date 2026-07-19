package practice;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class EjercicioFragmentos {

    public static void main(String[] args) {

        try {

            // Cargar imágenes
            BufferedImage img1 = ImageIO.read(new File("src/Imagenes/Minion.jpg"));
            BufferedImage img2 = ImageIO.read(new File("src/Imagenes/perro.jpg"));

            // Crear máscara (Stencil)
            BufferedImage mascara = crearStencil(img1.getWidth(), img1.getHeight());

            // Aplicar Stencil
            BufferedImage stencil = aplicarStencil(img1, mascara);
            ImageIO.write(stencil, "jpg", new File("src/Imagenes/Stencil.jpg"));

            // Redimensionar la segunda imagen
            BufferedImage img2Red = new BufferedImage(
                    stencil.getWidth(),
                    stencil.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g = img2Red.createGraphics();
            g.drawImage(img2, 0, 0, stencil.getWidth(), stencil.getHeight(), null);
            g.dispose();

            // Aplicar Blending
            BufferedImage blending = aplicarBlending(stencil, img2Red, 0.6f);
            ImageIO.write(blending, "jpg", new File("src/Imagenes/Blending.jpg"));

            // Aplicar XOR
            BufferedImage resultado = aplicarXOR(blending, img2Red);
            ImageIO.write(resultado, "jpg", new File("src/Imagenes/ResultadoFinal.jpg"));

            System.out.println("Proceso terminado.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Crear una máscara rectangular
    public static BufferedImage crearStencil(int ancho, int alto) {

        BufferedImage mascara = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = mascara.createGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, ancho, alto);

        g.setColor(Color.WHITE);
        g.fillRect(ancho / 4, alto / 4, ancho / 2, alto / 2);

        g.dispose();

        return mascara;
    }

    // STENCIL TEST
    public static BufferedImage aplicarStencil(BufferedImage imagen, BufferedImage mascara) {

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {

            for (int x = 0; x < ancho; x++) {

                if (mascara.getRGB(x, y) == Color.WHITE.getRGB()) {

                    salida.setRGB(x, y, imagen.getRGB(x, y));

                } else {

                    salida.setRGB(x, y, Color.BLACK.getRGB());

                }
            }
        }

        return salida;
    }

    // BLENDING
    public static BufferedImage aplicarBlending(BufferedImage fondo,
                                                BufferedImage superior,
                                                float alpha) {

        int ancho = fondo.getWidth();
        int alto = fondo.getHeight();

        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {

            for (int x = 0; x < ancho; x++) {

                int p1 = fondo.getRGB(x, y);
                int p2 = superior.getRGB(x, y);

                int r1 = (p1 >> 16) & 0xFF;
                int g1 = (p1 >> 8) & 0xFF;
                int b1 = p1 & 0xFF;

                int r2 = (p2 >> 16) & 0xFF;
                int g2 = (p2 >> 8) & 0xFF;
                int b2 = p2 & 0xFF;

                int r = (int) (r2 * alpha + r1 * (1 - alpha));
                int g = (int) (g2 * alpha + g1 * (1 - alpha));
                int b = (int) (b2 * alpha + b1 * (1 - alpha));

                int pixel = (r << 16) | (g << 8) | b;

                salida.setRGB(x, y, pixel);
            }
        }

        return salida;
    }

    // LOGIC OP - XOR
    public static BufferedImage aplicarXOR(BufferedImage img1, BufferedImage img2) {

        int ancho = img1.getWidth();
        int alto = img1.getHeight();

        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {

            for (int x = 0; x < ancho; x++) {

                int p1 = img1.getRGB(x, y);
                int p2 = img2.getRGB(x, y);

                int r = ((p1 >> 16) & 0xFF) ^ ((p2 >> 16) & 0xFF);
                int g = ((p1 >> 8) & 0xFF) ^ ((p2 >> 8) & 0xFF);
                int b = (p1 & 0xFF) ^ (p2 & 0xFF);

                int pixel = (r << 16) | (g << 8) | b;

                salida.setRGB(x, y, pixel);
            }
        }

        return salida;
    }
}