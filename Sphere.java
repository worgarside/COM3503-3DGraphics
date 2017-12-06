import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Sphere extends Mesh {

    private int[] textureId1;
    private int[] textureId2;
    private static final Vec3 SCENE_AMBIENT = new Vec3(0.5f, 0.5f, 0.5f);

    public Sphere(GL3 gl, int[] textureId1, int[] textureId2) {
        super(gl);
        createVertices();
        super.vertices = this.vertices;
        super.indices = this.indices;
        this.textureId1 = textureId1;
        this.textureId2 = textureId2;
        material.setAmbient(SCENE_AMBIENT);

        material.setAllDiffusePoints(1f, 1f, 1f);
        material.setAllDiffuseSpots(1f, 1f, 1f);
        material.setAllSpecularPoints(0.05f, 0.05f, 0.05f);
        material.setAllSpecularSpots(0.5f, 0.5f, 0.5f);

        material.setShininess(32.0f);
        shader = new Shader(gl, "shaders/vs_object.glsl", "shaders/fs_object.glsl");
        fillBuffers(gl);
    }

    public void render(GL3 gl, Mat4 model) {
        Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), model));
        shader.use(gl);
        shader.setFloatArray(gl, "model", model.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
        shader.setVec3(gl, "viewPos", camera.getPosition());

        for (int i = 0; i < Arty.lightCount; i++) {
            super.setShaderValues(gl, shader, i, SCENE_AMBIENT);
        }
        shader.setVec3(gl, "material.ambient", material.getAmbient());
        shader.setFloat(gl, "material.shininess", material.getShininess());
        shader.setInt(gl, "material.diffuse", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
        shader.setInt(gl, "material.specular", 1);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        super.dispose(gl);
        gl.glDeleteBuffers(1, textureId1, 0);
        gl.glDeleteBuffers(1, textureId2, 0);
    }

    // ***************************************************
  /* THE DATA
   */
    // anticlockwise/counterclockwise ordering


    private float[] vertices;
    private int[] indices;

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