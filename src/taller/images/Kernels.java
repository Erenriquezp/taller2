package taller.images;

public class Kernels {

    // No modifica imagen
    public static final float[] kNormal = {
            0f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 0f
    };

    // Enfoque (Sharpen)
    public static final float[] kEnfoque = {
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f
    };

    // Desenfoque (blur)
    public static final float[] kDesenfoque = {
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f
    };

    public static final float[] kDesenfoque9 = {
            1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f,
            1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f,
            1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f,
            1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f,
            1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f,
            1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f, 1/81f
    };

    // Deteccion de bordes
    public static final float[] kBordes = {
            -0.5f, -0.5f, -0.5f,
            -0.5f, 4f, -0.5f,
            -0.5f, -0.5f, -0.5f
    };

    // Aclarar
    public static final float[] kAclaracion = {
            0.1f, 0.1f, 0.1f,
            0.1f, 1f, 0.1f,
            0.1f, 0.1f, 0.1f
    };

    // Oscurecer
    public static final float[] kOscurecer = {
            0.01f, 0.01f, 0.01f,
            0.01f, 0.5f, 0.01f,
            0.01f, 0.01f, 0.01f
    };
}
