package histograma;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ProcesadorHistograma {

    public static BufferedImage ecualizar(BufferedImage original, int porcentaje) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        int totalPixeles = ancho * alto;

        int[] freqR = new int[256];
        int[] freqG = new int[256];
        int[] freqB = new int[256];

        // Contar frecuencia de cada nivel de brillo por canal
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                // Extraer canales mediante operaciones de bits
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                freqR[r]++;
                freqG[g]++;
                freqB[b]++;
            }
        }

        // Calcular distribución acumulada (CDF)
        int[] acumR = new int[256];
        int[] acumG = new int[256];
        int[] acumB = new int[256];
        acumR[0] = freqR[0];
        acumG[0] = freqG[0];
        acumB[0] = freqB[0];
        for (int i = 1; i < 256; i++) {
            // Sumar la frecuencia actual con la acumulada anterior (fórmula CDF acumulada)
            acumR[i] = acumR[i - 1] + freqR[i];
            acumG[i] = acumG[i - 1] + freqG[i];
            acumB[i] = acumB[i - 1] + freqB[i];
        }

        int acumMinR = encontrarMinimo(acumR);
        int acumMinG = encontrarMinimo(acumG);
        int acumMinB = encontrarMinimo(acumB);

        // Crear tabla de mapeo (traducción de colores)
        int[] mapaR = new int[256];
        int[] mapaG = new int[256];
        int[] mapaB = new int[256];
        for (int i = 0; i < 256; i++) {
            mapaR[i] = ecualizarValor(acumR[i], acumMinR, totalPixeles);
            mapaG[i] = ecualizarValor(acumG[i], acumMinG, totalPixeles);
            mapaB[i] = ecualizarValor(acumB[i], acumMinB, totalPixeles);
        }

        double factor = porcentaje / 100.0;
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = original.getRGB(x, y);
                int rOrig = (pixel >> 16) & 0xFF;
                int gOrig = (pixel >> 8) & 0xFF;
                int bOrig = pixel & 0xFF;

                int rEq = mapaR[rOrig];
                int gEq = mapaG[gOrig];
                int bEq = mapaB[bOrig];

                // Interpolar linealmente según el slider
                int rFinal = (int) (rOrig * (1 - factor) + rEq * factor);
                int gFinal = (int) (gOrig * (1 - factor) + gEq * factor);
                int bFinal = (int) (bOrig * (1 - factor) + bEq * factor);

                // Evitar desbordamiento de límites (clamping)
                rFinal = Math.min(255, Math.max(0, rFinal));
                gFinal = Math.min(255, Math.max(0, gFinal));
                bFinal = Math.min(255, Math.max(0, bFinal));

                int pixelNuevo = (rFinal << 16) | (gFinal << 8) | bFinal;
                resultado.setRGB(x, y, pixelNuevo);
            }
        }

        return resultado;
    }

    private static int ecualizarValor(int acumValor, int acumMin, int totalPixeles) {
        if (totalPixeles <= acumMin) return 0;
        // Fórmula matemática estándar de ecualización
        int resultado = (int) Math.round(((double) (acumValor - acumMin)) / (totalPixeles - acumMin) * 255.0);
        return Math.min(255, Math.max(0, resultado));
    }

    private static int encontrarMinimo(int[] acum) {
        for (int i = 0; i < 256; i++) {
            if (acum[i] > 0) return acum[i];
        }
        return 0;
    }

    public static BufferedImage mezclar(BufferedImage original, BufferedImage ecualizada, int porcentaje) {
        int ancho = original.getWidth();
        int alto = original.getHeight();

        if (porcentaje <= 0) return copiarImagen(original);
        if (porcentaje >= 100) return copiarImagen(ecualizada);

        double factor = porcentaje / 100.0;
        double invFactor = 1.0 - factor;

        int total = ancho * alto;
        // Obtener todos los píxeles en un solo arreglo (optimización de velocidad)
        int[] pOrig = original.getRGB(0, 0, ancho, alto, null, 0, ancho);
        int[] pEq = ecualizada.getRGB(0, 0, ancho, alto, null, 0, ancho);
        int[] pRes = new int[total];

        for (int i = 0; i < total; i++) {
            int o = pOrig[i];
            int e = pEq[i];
            // Mezclar canales en lote para evitar llamadas costosas por píxel
            int r = (int) (((o >> 16) & 0xFF) * invFactor + ((e >> 16) & 0xFF) * factor);
            int g = (int) (((o >> 8) & 0xFF) * invFactor + ((e >> 8) & 0xFF) * factor);
            int b = (int) ((o & 0xFF) * invFactor + (e & 0xFF) * factor);
            pRes[i] = (r << 16) | (g << 8) | b;
        }

        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        resultado.setRGB(0, 0, ancho, alto, pRes, 0, ancho);
        return resultado;
    }

    public static int[][] calcularHistograma(BufferedImage imagen) {
        int[] histoR = new int[256];
        int[] histoG = new int[256];
        int[] histoB = new int[256];

        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                int p = imagen.getRGB(x, y);
                histoR[(p >> 16) & 0xFF]++;
                histoG[(p >> 8) & 0xFF]++;
                histoB[p & 0xFF]++;
            }
        }

        return new int[][] { histoR, histoG, histoB };
    }

    public static BufferedImage marcarZonas(BufferedImage imagen, boolean marcarQuemadas, boolean marcarOscuras) {
        BufferedImage copia = copiarImagen(imagen);
        int ancho = copia.getWidth();
        int alto = copia.getHeight();

        // Identificar y colorear píxeles sobreexpuestos (rojo) y subexpuestos (azul)
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = copia.getRGB(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                // Colorear de rojo si está quemado o de azul si está oscuro
                if (marcarQuemadas && r >= 240 && g >= 240 && b >= 240) {
                    copia.setRGB(x, y, (255 << 16) | (0 << 8) | 0);
                }

                if (marcarOscuras && r <= 15 && g <= 15 && b <= 15) {
                    copia.setRGB(x, y, (0 << 16) | (0 << 8) | 255);
                }
            }
        }

        return copia;
    }

    public static class Diagnostico {
        public final double pctQuemados;
        public final double pctOscuros;
        public final String texto;

        public Diagnostico(double pctQuemados, double pctOscuros, String texto) {
            this.pctQuemados = pctQuemados;
            this.pctOscuros = pctOscuros;
            this.texto = texto;
        }
    }

    public static Diagnostico diagnosticar(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        int totalPixeles = ancho * alto;

        int quemados = 0;
        int oscuros = 0;
        // Analizar la exposición general contando píxeles en los extremos de brillo
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int p = imagen.getRGB(x, y);
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                // Contar píxeles quemados (brillo extremo alto) u oscuros (brillo extremo bajo)
                if (r >= 240 && g >= 240 && b >= 240) quemados++;
                if (r <= 15 && g <= 15 && b <= 15) oscuros++;
            }
        }

        // Calcular los porcentajes relativos al total de píxeles
        double pctQuemados = (quemados * 100.0) / totalPixeles;
        double pctOscuros = (oscuros * 100.0) / totalPixeles;

        String diagnostico = "";
        if (pctQuemados > 5) diagnostico += "Imagen quemada (" + String.format("%.1f", pctQuemados) + "% sobreexpuesto)";
        if (pctOscuros > 5) diagnostico += "Imagen oscura (" + String.format("%.1f", pctOscuros) + "% subexpuesto)";
        if (diagnostico.isEmpty()) diagnostico = "Exposición adecuada";

        return new Diagnostico(pctQuemados, pctOscuros, diagnostico);
    }

    public static int obtenerMaximo(int[] array) {
        int max = 0;
        for (int valor : array) {
            if (valor > max) max = valor;
        }
        return max;
    }

    public static BufferedImage copiarImagen(BufferedImage fuente) {
        // Crear una imagen nueva con el mismo tamaño para evitar modificar la original
        BufferedImage copia = new BufferedImage(fuente.getWidth(), fuente.getHeight(), fuente.getType());
        Graphics g = copia.getGraphics();
        g.drawImage(fuente, 0, 0, null);
        g.dispose();
        return copia;
    }
}
