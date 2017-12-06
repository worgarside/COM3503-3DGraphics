import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/**
 * Sphere.java
 * Sphere class with functions for setting up shader data and rendering spherical meshes
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
public class Sphere extends Mesh {

    private int[] textureMain;
    private int[] textureSpec;
    private static final Vec3 SCENE_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);

    /**
     * Constructor for Sphere object
     *
     * @param gl - graphics library
     * @param textureMain - main texture for Sphere
     * @param textureSpec - specular texture for Sphere
     */
    public Sphere(GL3 gl, int[] textureMain, int[] textureSpec) {
        super(gl);
        createVertices();
        super.vertices = this.vertices;
        super.indices = this.indices;
        this.textureMain = textureMain;
        this.textureSpec = textureSpec;

        material.setAmbient(SCENE_AMBIENT);

        material.setShininess(32.0f);
        shader = new Shader(gl, "shaders/vs_object.glsl", "shaders/fs_object.glsl");
        fillBuffers(gl);
    }

    /**
     * Initialise matrices and vectors and sends them to the shaders
     *
     * @param gl - grpahics library
     * @param model - the model matrix used for the Sphere
     */
    public void render(GL3 gl, Mat4 model) {
        Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);
        shader.setFloatArray(gl, "model", model.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        shader.setVec3(gl, "viewPos", camera.getPosition());

        for (int i =0; i < Arty.lightCount; i++) {
            super.setShaderValues(gl, shader, i, SCENE_AMBIENT);
        }

        shader.setFloat(gl, "material.shininess", material.getShininess());
        shader.setInt(gl, "material.diffuse", 0);
        shader.setInt(gl, "material.specular", 1);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureMain[0]);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureSpec[0]);

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

    /**
     * Removes the Meshes from memory on system exit
     *
     * @param gl
     */
    public void dispose(GL3 gl) {
        super.dispose(gl);
        gl.glDeleteBuffers(1, textureMain, 0);
        gl.glDeleteBuffers(1, textureSpec, 0);
    }

    // ------------ Data ------------ \\

    private float[] vertices;
    private int[] indices;

    /**
     * Creates vertices for the Sphere object for rendering
     */
    private void createVertices() {
        int XLONG = 30;
        int YLAT = 30;
        double r = 0.5;
        int step = 8;
        //float[]
        vertices = new float[XLONG*YLAT*step];
        for (int j = 0; j<YLAT; ++j) {
            double b = Math.toRadians(-90+180*(double)(j)/(YLAT-1));
            for (int i = 0; i<XLONG; ++i) {
                double a = Math.toRadians(360*(double)(i)/(XLONG-1));
                double z = Math.cos(b) * Math.cos(a);
                double x = Math.cos(b) * Math.sin(a);
                double y = Math.sin(b);
                vertices[j * XLONG * step + i * step + 0] = (float)(r * x);
                vertices[j * XLONG * step + i * step + 1] = (float)(r * y);
                vertices[j * XLONG * step + i * step + 2] = (float)(r * z);
                vertices[j * XLONG * step + i * step + 3] = (float)x;
                vertices[j * XLONG * step + i * step + 4] = (float)y;
                vertices[j * XLONG * step + i * step + 5] = (float)z;
                vertices[j * XLONG * step + i * step + 6] = (float)(i) / (float)(XLONG - 1);
                vertices[j * XLONG * step + i * step + 7] = (float)(j) / (float)(YLAT - 1);
            }
        }

        indices = new int[(XLONG - 1) * (YLAT - 1) * 6];
        for (int j = 0; j < YLAT - 1; j++) {
            for (int i = 0; i < XLONG - 1; i++) {
                indices[j * (XLONG - 1) * 6 + i * 6 + 0] = j * XLONG + i;
                indices[j * (XLONG - 1) * 6 + i * 6 + 1] = j * XLONG + i + 1;
                indices[j * (XLONG - 1) * 6 + i * 6 + 2] = (j + 1) * XLONG + i + 1;
                indices[j * (XLONG - 1) * 6 + i * 6 + 3] = j * XLONG + i;
                indices[j * (XLONG - 1) * 6 + i * 6 + 4] = (j + 1) * XLONG + i + 1;
                indices[j * (XLONG - 1) * 6 + i * 6 + 5] = (j + 1) * XLONG + i;
            }
        }
    }

}