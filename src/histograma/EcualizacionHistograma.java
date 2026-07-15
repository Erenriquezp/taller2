package histograma;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EcualizacionHistograma extends JFrame {

    private BufferedImage imagenOriginal;
    private BufferedImage imagenProcesada;
    private BufferedImage imagenEcualizada;

    private JLabel labelImagen;
    private JLabel labelEstado;
    private JSlider sliderEcualizacion;
    private JPanel panelHistograma;
    private JCheckBox checkMostrarQuemadas;
    private JCheckBox checkMostrarOscuras;

    private int[] histoR = new int[256];
    private int[] histoG = new int[256];
    private int[] histoB = new int[256];

    public EcualizacionHistograma() {
        super("Ecualización de Histograma");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        construirInterfaz();
        setVisible(true);
    }

    private void construirInterfaz() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnCargar = new JButton("Cargar Imagen");
        JButton btnGuardar = new JButton("Guardar Resultado");

        checkMostrarQuemadas = new JCheckBox("Marcar zonas quemadas");
        checkMostrarOscuras = new JCheckBox("Marcar zonas oscuras");

        panelBotones.add(btnCargar);
        panelBotones.add(btnGuardar);
        panelBotones.add(new JSeparator(SwingConstants.VERTICAL));
        panelBotones.add(checkMostrarQuemadas);
        panelBotones.add(checkMostrarOscuras);
        add(panelBotones, BorderLayout.NORTH);

        labelImagen = new JLabel("Cargue una imagen para comenzar", SwingConstants.CENTER);
        labelImagen.setPreferredSize(new Dimension(700, 500));
        JScrollPane scrollImagen = new JScrollPane(labelImagen);
        add(scrollImagen, BorderLayout.CENTER);

        panelHistograma = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarHistograma((Graphics2D) g);
            }
        };
        panelHistograma.setPreferredSize(new Dimension(280, 200));
        panelHistograma.setBackground(Color.BLACK);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(new JLabel(" Histograma RGB", SwingConstants.CENTER), BorderLayout.NORTH);
        panelDerecho.add(panelHistograma, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);

        JPanel panelInferior = new JPanel(new BorderLayout(8, 4));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        sliderEcualizacion = new JSlider(0, 100, 0);
        sliderEcualizacion.setMajorTickSpacing(25);
        sliderEcualizacion.setMinorTickSpacing(5);
        sliderEcualizacion.setPaintTicks(true);
        sliderEcualizacion.setPaintLabels(true);

        labelEstado = new JLabel("Ecualización: 0%  |  Sin imagen cargada");
        panelInferior.add(new JLabel("Intensidad de ecualización:"), BorderLayout.WEST);
        panelInferior.add(sliderEcualizacion, BorderLayout.CENTER);
        panelInferior.add(labelEstado, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarImagen());
        btnGuardar.addActionListener(e -> guardarImagen());

        sliderEcualizacion.addChangeListener(e -> {
            if (imagenOriginal != null) {
                aplicarYActualizar(sliderEcualizacion.getValue());
            }
        });

        checkMostrarQuemadas.addActionListener(e -> {
            if (imagenOriginal != null) aplicarYActualizar(sliderEcualizacion.getValue());
        });
        checkMostrarOscuras.addActionListener(e -> {
            if (imagenOriginal != null) aplicarYActualizar(sliderEcualizacion.getValue());
        });
    }

    private void cargarImagen() {
        JFileChooser chooser = new JFileChooser("src/histograma/images");
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "png", "jpg", "jpeg", "bmp", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                imagenOriginal = ImageIO.read(chooser.getSelectedFile());
                if (imagenOriginal == null) {
                    JOptionPane.showMessageDialog(this, "No se pudo leer la imagen.");
                    return;
                }
                imagenEcualizada = ProcesadorHistograma.ecualizar(imagenOriginal, 100);
                sliderEcualizacion.setValue(0);
                aplicarYActualizar(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer: " + ex.getMessage());
            }
        }
    }

    private void guardarImagen() {
        if (imagenProcesada == null) {
            JOptionPane.showMessageDialog(this, "No hay imagen procesada para guardar.");
            return;
        }
        JFileChooser chooser = new JFileChooser("src/histograma/images");
        chooser.setSelectedFile(new File("ecualizada.png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(imagenProcesada, "png", chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Imagen guardada con éxito.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void aplicarYActualizar(int porcentaje) {
        imagenProcesada = ProcesadorHistograma.mezclar(imagenOriginal, imagenEcualizada, porcentaje);

        BufferedImage imagenMostrar;
        boolean quemadas = checkMostrarQuemadas.isSelected();
        boolean oscuras = checkMostrarOscuras.isSelected();
        if (quemadas || oscuras) {
            imagenMostrar = ProcesadorHistograma.marcarZonas(imagenProcesada, quemadas, oscuras);
        } else {
            imagenMostrar = ProcesadorHistograma.copiarImagen(imagenProcesada);
        }

        int[][] histogramas = ProcesadorHistograma.calcularHistograma(imagenProcesada);
        histoR = histogramas[0];
        histoG = histogramas[1];
        histoB = histogramas[2];

        ProcesadorHistograma.Diagnostico diag = ProcesadorHistograma.diagnosticar(imagenProcesada);

        mostrarImagen(imagenMostrar);
        panelHistograma.repaint();
        actualizarEstado(porcentaje, diag);
    }

    private void dibujarHistograma(Graphics2D g2d) {
        int anchoH = panelHistograma.getWidth();
        int altoH = panelHistograma.getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, anchoH, altoH);

        int maxGlobal = Math.max(ProcesadorHistograma.obtenerMaximo(histoR),
                Math.max(ProcesadorHistograma.obtenerMaximo(histoG),
                        ProcesadorHistograma.obtenerMaximo(histoB)));
        if (maxGlobal == 0) return;

        float escalaX = (float) anchoH / 256f;
        float escalaY = (float) (altoH - 10) / maxGlobal;

        g2d.setStroke(new BasicStroke(1));

        for (int i = 1; i < 256; i++) {
            int x1 = (int) (escalaX * (i - 1));
            int x2 = (int) (escalaX * i);

            int rY1 = altoH - (int) (histoR[i - 1] * escalaY);
            int rY2 = altoH - (int) (histoR[i] * escalaY);
            g2d.setColor(new Color(255, 60, 60, 180));
            g2d.drawLine(x1, rY1, x2, rY2);

            int gY1 = altoH - (int) (histoG[i - 1] * escalaY);
            int gY2 = altoH - (int) (histoG[i] * escalaY);
            g2d.setColor(new Color(60, 255, 60, 180));
            g2d.drawLine(x1, gY1, x2, gY2);

            int bY1 = altoH - (int) (histoB[i - 1] * escalaY);
            int bY2 = altoH - (int) (histoB[i] * escalaY);
            g2d.setColor(new Color(60, 100, 255, 180));
            g2d.drawLine(x1, bY1, x2, bY2);
        }
    }

    private void mostrarImagen(BufferedImage imagen) {
        int maxAncho = 700;
        int maxAlto = 500;
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        double escala = Math.min((double) maxAncho / ancho, (double) maxAlto / alto);
        if (escala >= 1.0) escala = 1.0;

        int nuevoAncho = (int) (ancho * escala);
        int nuevoAlto = (int) (alto * escala);

        Image escalada = imagen.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_FAST);
        labelImagen.setIcon(new ImageIcon(escalada));
        labelImagen.setText(null);
    }

    private void actualizarEstado(int porcentaje, ProcesadorHistograma.Diagnostico diag) {
        int ancho = imagenOriginal.getWidth();
        int alto = imagenOriginal.getHeight();

        labelEstado.setText("Ecualización: " + porcentaje + "%  |  " +
                ancho + "x" + alto + "  |  Quemados: " + String.format("%.1f", diag.pctQuemados) +
                "%  Oscuros: " + String.format("%.1f", diag.pctOscuros) + "%" + diag.texto);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(EcualizacionHistograma::new);
    }
}
