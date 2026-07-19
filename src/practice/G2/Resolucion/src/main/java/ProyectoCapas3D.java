package practice.G2.Resolucion.src.main.java;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Arrays;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class ProyectoCapas3D extends JFrame {
    private double[] zValues = {1.5, 2.5, 3.5};
    private int[] alphas = {255, 255, 255};
    private Color[] colors = {Color.WHITE, Color.WHITE, Color.WHITE};
    private double[] xOffsets = {-90, 0, 90};
    private double[] yOffsets = {-30, 0, 30};
    private BufferedImage[] textures = new BufferedImage[3];
    private Texture[] joglTextures = new Texture[3];

    private JComboBox<String> layerCombo;
    private JSlider zSlider;
    private JSlider alphaSlider;
    private JSlider rSlider, gSlider, bSlider;

    private JCheckBox depthTestCheck;
    private JCheckBox depthMapCheck;
    private JCheckBox texturesCheck;

    private RenderPanel renderPanel;
    private boolean isUpdating = false;

    private boolean depthTestEnabled = true;
    private boolean depthMapEnabled = false;
    private boolean texturesEnabled = true;

    public ProyectoCapas3D() {
        setTitle("OpenGL 1.0 - Capas 3D");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initTextures();
        initUI();
    }

    private void initTextures() {
        try {
            textures[0] = ImageIO.read(new File("Imagenes/ejemplo.jpg"));
            textures[1] = ImageIO.read(new File("Imagenes/perfil.jpg"));
            textures[2] = ImageIO.read(new File("Imagenes/anime-night-sky-illustration.jpg"));
        } catch (Exception e) {
            for (int i = 0; i < 3; i++) {
                textures[i] = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = textures[i].createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (i == 0) {
                    g.setColor(Color.RED);
                    g.fillOval(10, 10, 108, 108);
                } else if (i == 1) {
                    g.setColor(Color.GREEN);
                    g.fillRect(10, 10, 108, 108);
                } else {
                    g.setColor(Color.BLUE);
                    int[] x = {64, 10, 118};
                    int[] y = {10, 118, 118};
                    g.fillPolygon(x, y, 3);
                }
                g.dispose();
            }
        }
    }

    private void initUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(680);

        renderPanel = new RenderPanel();
        renderPanel.setPreferredSize(new Dimension(680, 560));

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        sidebar.add(new JLabel("Seleccionar Capa:"));
        layerCombo = new JComboBox<>(new String[]{"Capa 1 - Roja", "Capa 2 - Verde", "Capa 3 - Azul"});
        layerCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        layerCombo.addActionListener(e -> updateSelectedLayerUI());
        sidebar.add(layerCombo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        sidebar.add(new JLabel("Profundidad Z:"));
        zSlider = new JSlider(50, 500, 150);
        zSlider.addChangeListener(e -> {
            if (!isUpdating) {
                zValues[layerCombo.getSelectedIndex()] = zSlider.getValue() / 100.0;
                renderPanel.repaint();
            }
        });
        sidebar.add(zSlider);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(new JLabel("Transparencia (Alpha):"));
        alphaSlider = new JSlider(0, 255, 255);
        alphaSlider.addChangeListener(e -> {
            if (!isUpdating) {
                alphas[layerCombo.getSelectedIndex()] = alphaSlider.getValue();
                renderPanel.repaint();
            }
        });
        sidebar.add(alphaSlider);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(new JLabel("Tinte Rojo (R):"));
        rSlider = new JSlider(0, 255, 255);
        rSlider.addChangeListener(e -> updateTintColor());
        sidebar.add(rSlider);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));

        sidebar.add(new JLabel("Tinte Verde (G):"));
        gSlider = new JSlider(0, 255, 255);
        gSlider.addChangeListener(e -> updateTintColor());
        sidebar.add(gSlider);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));

        sidebar.add(new JLabel("Tinte Azul (B):"));
        bSlider = new JSlider(0, 255, 255);
        bSlider.addChangeListener(e -> updateTintColor());
        sidebar.add(bSlider);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        depthTestCheck = new JCheckBox("Habilitar GL_DEPTH_TEST", true);
        depthTestCheck.addActionListener(e -> {
            depthTestEnabled = depthTestCheck.isSelected();
            renderPanel.repaint();
        });
        sidebar.add(depthTestCheck);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));

        texturesCheck = new JCheckBox("Habilitar Texturas", true);
        texturesCheck.addActionListener(e -> {
            texturesEnabled = texturesCheck.isSelected();
            renderPanel.repaint();
        });
        sidebar.add(texturesCheck);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));

        depthMapCheck = new JCheckBox("Ver Mapa de Profundidad", false);
        depthMapCheck.addActionListener(e -> {
            depthMapEnabled = depthMapCheck.isSelected();
            renderPanel.repaint();
        });
        sidebar.add(depthMapCheck);

        split.setLeftComponent(renderPanel);
        split.setRightComponent(sidebar);
        add(split);

        updateSelectedLayerUI();
    }

    private void updateSelectedLayerUI() {
        int idx = layerCombo.getSelectedIndex();
        isUpdating = true;
        zSlider.setValue((int) (zValues[idx] * 100.0));
        alphaSlider.setValue(alphas[idx]);
        rSlider.setValue(colors[idx].getRed());
        gSlider.setValue(colors[idx].getGreen());
        bSlider.setValue(colors[idx].getBlue());
        isUpdating = false;
    }

    private void updateTintColor() {
        if (!isUpdating) {
            int idx = layerCombo.getSelectedIndex();
            colors[idx] = new Color(rSlider.getValue(), gSlider.getValue(), bSlider.getValue());
            renderPanel.repaint();
        }
    }

    class RenderPanel extends GLJPanel implements GLEventListener {
        public RenderPanel() {
            super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
            addGLEventListener(this);
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();
            gl.glClearColor(0.082f, 0.082f, 0.082f, 1.0f);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDepthFunc(GL2.GL_LEQUAL);

            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

            gl.glEnable(GL2.GL_TEXTURE_2D);
            cargarTexturasJOGL(gl);
        }

        private void cargarTexturasJOGL(GL2 gl) {
            for (int i = 0; i < 3; i++) {
                if (textures[i] != null) {
                    try {
                        joglTextures[i] = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL2), textures[i], true);
                        joglTextures[i].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
                        joglTextures[i].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
                        joglTextures[i].setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
                        joglTextures[i].setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();
            for (int i = 0; i < 3; i++) {
                if (joglTextures[i] != null) {
                    joglTextures[i].destroy(gl);
                    joglTextures[i] = null;
                }
            }
        }

        @Override
        public void display(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            if (depthMapEnabled) {
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            } else {
                gl.glClearColor(0.082f, 0.082f, 0.082f, 1.0f);
            }
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            if (depthTestEnabled) {
                gl.glEnable(GL2.GL_DEPTH_TEST);
            } else {
                gl.glDisable(GL2.GL_DEPTH_TEST);
            }

            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

            Integer[] orden = {0, 1, 2};
            Arrays.sort(orden, (a, b) -> Double.compare(zValues[b], zValues[a]));

            for (int i : orden) {
                double layerZ = zValues[i];
                int alpha = alphas[i];
                Color tint = colors[i];

                float r = tint.getRed() / 255.0f;
                float g = tint.getGreen() / 255.0f;
                float b = tint.getBlue() / 255.0f;
                float a = alpha / 255.0f;
                gl.glColor4f(r, g, b, a);

                if (depthMapEnabled) {
                    gl.glDisable(GL2.GL_TEXTURE_2D);
                } else if (texturesEnabled && joglTextures[i] != null) {
                    gl.glEnable(GL2.GL_TEXTURE_2D);
                    joglTextures[i].bind(gl);
                } else {
                    gl.glDisable(GL2.GL_TEXTURE_2D);
                }

                boolean isTilted = (i == 1);
                double zLeft = isTilted ? layerZ - 0.4 : layerZ;
                double zRight = isTilted ? layerZ + 0.4 : layerZ;

                double xLeft = xOffsets[i] - 75.0;
                double xRight = xOffsets[i] + 75.0;
                double yTop = yOffsets[i] - 75.0;
                double yBottom = yOffsets[i] + 75.0;

                if (depthMapEnabled) {
                    double minZ = 0.5;
                    double maxZ = 2.5;
                    float intLeft = (float) (1.0 - (zLeft - minZ) / (maxZ - minZ));
                    float intRight = (float) (1.0 - (zRight - minZ) / (maxZ - minZ));
                    intLeft = Math.max(0.0f, Math.min(1.0f, intLeft));
                    intRight = Math.max(0.0f, Math.min(1.0f, intRight));

                    gl.glBegin(GL2.GL_QUADS);
                        gl.glColor4f(intLeft, intLeft, intLeft, 1.0f);
                        gl.glVertex3d(xLeft, yTop, -zLeft);

                        gl.glColor4f(intRight, intRight, intRight, 1.0f);
                        gl.glVertex3d(xRight, yTop, -zRight);

                        gl.glColor4f(intRight, intRight, intRight, 1.0f);
                        gl.glVertex3d(xRight, yBottom, -zRight);

                        gl.glColor4f(intLeft, intLeft, intLeft, 1.0f);
                        gl.glVertex3d(xLeft, yBottom, -zLeft);
                    gl.glEnd();
                } else {
                    gl.glBegin(GL2.GL_QUADS);
                        gl.glTexCoord2f(0.0f, 0.0f);
                        gl.glVertex3d(xLeft, yTop, -zLeft);

                        gl.glTexCoord2f(1.0f, 0.0f);
                        gl.glVertex3d(xRight, yTop, -zRight);

                        gl.glTexCoord2f(1.0f, 1.0f);
                        gl.glVertex3d(xRight, yBottom, -zRight);

                        gl.glTexCoord2f(0.0f, 1.0f);
                        gl.glVertex3d(xLeft, yBottom, -zLeft);
                    gl.glEnd();
                }
            }
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL2 gl = drawable.getGL().getGL2();
            if (height <= 0) height = 1;

            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();

            double w = width;
            double h = height;
            double nearVal = 0.05;
            gl.glFrustum(-w/2.0 * nearVal, w/2.0 * nearVal, h/2.0 * nearVal, -h/2.0 * nearVal, nearVal, 50.0);

            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();
        }
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new ProyectoCapas3D().setVisible(true);
        });
    }
}
