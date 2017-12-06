import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/**
 * Cube.java
 * Cube class with functions for setting up shader data and rendering cube meshes
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
public class Cube extends Mesh {

    private int[] textureMain;
    private int[] textureSpec;
    private static final Vec3 SCENE_AMBIENT = new Vec3(0.1f, 0.1f, 0.1f);

    /**
     * Constructor for Cube object
     *
     * @param gl - graphics library
     * @param textureMain - the ID of the main textureMain
     * @param textureSpec - the ID of the specular textureMain
     */
    public Cube(GL3 gl, int[] textureMain, int[] textureSpec) {
        super(gl);
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
     * @param model - the model matrix used for the Cube
     */
    public void render(GL3 gl, Mat4 model) {
        Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);
        shader.setFloatArray(gl, "model", model.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        shader.setVec3(gl, "viewPos", camera.getPosition());

        // Uses Mesh class to create shader values
        for (int i = 0; i < Arty.lightCount; i++) {
            super.setShaderValues(gl, shader, i, SCENE_AMBIENT);
        }

        shader.setInt(gl, "material.diffuse", 0);
        shader.setInt(gl, "material.specular", 1);
        shader.setFloat(gl, "material.shininess", material.getShininess());

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureMain[0]);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureSpec[0]);

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

    /**
     * Removes meshes from memory on system exit
     *
     * @param gl
     */
    public void dispose(GL3 gl) {
        super.dispose(gl);
        gl.glDeleteBuffers(1, textureMain, 0);
        gl.glDeleteBuffers(1, textureSpec, 0);
    }

    /**
     * Vertices used in a Cube mesh
     */
    private float[] vertices = new float[] {
            -0.5f, -0.5f, -0.5f,  -1, 0, 0,  0.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  -1, 0, 0,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  -1, 0, 0,  0.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  -1, 0, 0,  1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,   1, 0, 0,  1.0f, 0.0f,
            0.5f, -0.5f,  0.5f,   1, 0, 0,  0.0f, 0.0f,
            0.5f,  0.5f, -0.5f,   1, 0, 0,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,   1, 0, 0,  0.0f, 1.0f,

            -0.5f, -0.5f, -0.5f,  0, 0, -1,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0, 0, 1,   0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0, 0, -1,  1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0, 0, 1,   0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0, 0, -1,  0.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  0, 0, 1,   1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  0, 0, -1,  0.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0, 0, 1,   1.0f, 1.0f,

            -0.5f, -0.5f, -0.5f,  0, -1, 0,  0.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0, -1, 0,  0.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0, 1, 0,   0.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0, 1, 0,   0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,  0, -1, 0,  1.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  0, -1, 0,  1.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  0, 1, 0,   1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0, 1, 0,   1.0f, 0.0f
    };

    /**
     * Indices used in a Cube mesh
     */
    private int[] indices =  new int[] {
            0, 1, 3,
            3, 2, 0,
            4, 6, 7,
            7, 5, 4,
            9, 13, 15,
            15, 11, 9,
            8, 10, 14,
            14, 12, 8,
            16, 20, 21,
            21, 17, 16,
            23, 22, 18,
            18, 19, 23,
    };

}