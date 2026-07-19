package practice;

import java.awt.*;
import javax.swing.*;

public class EjercicioCuadradosZBuffer extends JPanel {
    static final int ANCHO = 800, ALTO = 600;
    Color[][] canvas = new Color[ANCHO][ALTO];
    float[][] zBuffer = new float[ANCHO][ALTO];

    // Profundidad inicial
    float zA = 0.4f, zB = 0.6f;

    public EjercicioCuadradosZBuffer() {
        renderizar();
    }

    private void renderizar() {
        // 1. Limpiar canvas y resetear zBuffer a infinito (1.0f)
        for (int x = 0; x < ANCHO; x++) {
            for (int y = 0; y < ALTO; y++) {
                canvas[x][y] = Color.GRAY;
                zBuffer[x][y] = 1.0f;
            }
        }

        // 2. Dibujar dos cuadrados: Posición (x, y), Tamaño, Profundidad, Color
        dibujarCuadrado(100, 100, 200, zA, Color.CYAN);
        dibujarCuadrado(250, 200, 200, zB, Color.MAGENTA);
        repaint();
    }

    // Función de borde
    private int funcionBorde(int x, int y, int v0x, int v0y, int v1x, int v1y) {
        return (x - v0x) * (v1y - v0y) - (y - v0y) * (v1x - v0x);
    }

    private void dibujarCuadrado(int x0, int y0, int tam, float z, Color c) {
        // Definir los 4 vértices del cuadrado
        int v0x = x0,           v0y = y0;           
        int v1x = x0,           v1y = y0 + tam;
        int v2x = x0 + tam,     v2y = y0 + tam;
        int v3x = x0 + tam,     v3y = y0;

        for (int x = x0; x < x0 + tam; x++) {
            for (int y = y0; y < y0 + tam; y++) {
                
                // Evaluar la función de borde
                int e0 = funcionBorde(x, y, v0x, v0y, v1x, v1y);
                int e1 = funcionBorde(x, y, v1x, v1y, v2x, v2y);
                int e2 = funcionBorde(x, y, v2x, v2y, v3x, v3y);
                int e3 = funcionBorde(x, y, v3x, v3y, v0x, v0y);

                // Validación del píxel
                if (e0 >= 0 && e1 >= 0 && e2 >= 0 && e3 >= 0) {
                    // Validación de límites de pantalla y Z-Test
                    if (x >= 0 && x < ANCHO && y >= 0 && y < ALTO && z < zBuffer[x][y]) {
                        zBuffer[x][y] = z;
                        canvas[x][y] = c;
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        for (int x = 0; x < ANCHO; x++) {
            for (int y = 0; y < ALTO; y++) {
                g.setColor(canvas[x][y]);
                g.fillRect(x, y, 1, 1);
            }
        }
    }

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Rasterización + Z-Buffer");
        EjercicioCuadradosZBuffer simulador = new EjercicioCuadradosZBuffer();

        // Panel de controles a la derecha
        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setPreferredSize(new Dimension(200, ALTO));

        JSlider sA = new JSlider(10, 90, 40);
        JSlider sB = new JSlider(10, 90, 60);

        sA.addChangeListener(e -> { simulador.zA = sA.getValue()/100f; simulador.renderizar(); });
        sB.addChangeListener(e -> { simulador.zB = sB.getValue()/100f; simulador.renderizar(); });

        controles.add(new JLabel("Profundidad Cyan:")); controles.add(sA);
        controles.add(new JLabel("Profundidad Magenta:")); controles.add(sB);

        ventana.setLayout(new BorderLayout());
        ventana.add(simulador, BorderLayout.CENTER);
        ventana.add(controles, BorderLayout.EAST);
        ventana.setSize(ANCHO + 200, ALTO);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
}