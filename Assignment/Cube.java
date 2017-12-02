import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Cube extends Mesh {

    private int[] texture;
    private int[] textureSpecular;
    private static final int LIGHT_COUNT = 3;
    private static final Vec3 SCENE_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);


    public Cube(GL3 gl, int[] texture, int[] textureSpecular) {
        super(gl);
        super.vertices = this.vertices;
        super.indices = this.indices;
        this.texture = texture;
        this.textureSpecular = textureSpecular;
        material.setAmbient(1.0f, 0.5f, 0.31f);

        material.setDiffusePoint(1.0f, 0.5f, 0.31f);
        material.setSpecularPoint(0.5f, 0.5f, 0.5f);

        material.setDiffuseSpot(1.0f, 0.5f, 0.31f);
        material.setSpecularSpot(0.5f, 0.5f, 0.5f);

        material.setShininess(32.0f);
        shader = new Shader(gl, "shaders/vs_object.glsl", "shaders/fs_object.glsl");
        fillBuffers(gl);
    }

    public void render(GL3 gl, Mat4 model) {
        //Mat4 model = getObjectModelMatrix();
        Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);
        shader.setFloatArray(gl, "model", model.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        shader.setVec3(gl, "viewPos", camera.getPosition());
//        shader.setVec3(gl, "sceneAmbient", new Vec3(0.2f, 0.2f, 0.2f));

        for (int i =0; i < LIGHT_COUNT; i++) {
            shader.setVec3(gl, "lightSources[" + i + "].position", light.getPosition(i));
            shader.setVec3(gl, "lightSources[" + i + "].ambient", SCENE_AMBIENT);
            shader.setVec3(gl, "lightSources[" + i + "].diffuse", light.getMaterial().getDiffusePoint());
            shader.setVec3(gl, "lightSources[" + i + "].specular", light.getMaterial().getSpecularPoint());
            shader.setFloat(gl, "lightSources[" + i + "].falloffConstant", 1f);      // Change this number
            shader.setFloat(gl, "lightSources[" + i + "].falloffLinear", 1f);        // Change this number
            shader.setFloat(gl, "lightSources[" + i + "].falloffQuadratic", 1f);     // Change this number
        }

        shader.setFloat(gl, "material.shininess", material.getShininess());

        shader.setInt(gl, "material.diffuse", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
        shader.setInt(gl, "material.specular", 1);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureSpecular[0]);

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        super.dispose(gl);
        gl.glDeleteBuffers(1, texture, 0);
        gl.glDeleteBuffers(1, textureSpecular, 0);
    }

    // ***************************************************
  /* THE DATA
   */
    // anticlockwise/counterclockwise ordering

    private float[] vertices = new float[] {  // x,y,z, nx,ny,nz, s,t
            -0.5f, -0.5f, -0.5f,  -1, 0, 0,  0.0f, 0.0f,  // 0
            -0.5f, -0.5f,  0.5f,  -1, 0, 0,  1.0f, 0.0f,  // 1
            -0.5f,  0.5f, -0.5f,  -1, 0, 0,  0.0f, 1.0f,  // 2
            -0.5f,  0.5f,  0.5f,  -1, 0, 0,  1.0f, 1.0f,  // 3
            0.5f, -0.5f, -0.5f,   1, 0, 0,  1.0f, 0.0f,  // 4
            0.5f, -0.5f,  0.5f,   1, 0, 0,  0.0f, 0.0f,  // 5
            0.5f,  0.5f, -0.5f,   1, 0, 0,  1.0f, 1.0f,  // 6
            0.5f,  0.5f,  0.5f,   1, 0, 0,  0.0f, 1.0f,  // 7

            -0.5f, -0.5f, -0.5f,  0,0,-1,  1.0f, 0.0f,  // 8
            -0.5f, -0.5f,  0.5f,  0,0,1,   0.0f, 0.0f,  // 9
            -0.5f,  0.5f, -0.5f,  0,0,-1,  1.0f, 1.0f,  // 10
            -0.5f,  0.5f,  0.5f,  0,0,1,   0.0f, 1.0f,  // 11
            0.5f, -0.5f, -0.5f,  0,0,-1,  0.0f, 0.0f,  // 12
            0.5f, -0.5f,  0.5f,  0,0,1,   1.0f, 0.0f,  // 13
            0.5f,  0.5f, -0.5f,  0,0,-1,  0.0f, 1.0f,  // 14
            0.5f,  0.5f,  0.5f,  0,0,1,   1.0f, 1.0f,  // 15

            -0.5f, -0.5f, -0.5f,  0,-1,0,  0.0f, 0.0f,  // 16
            -0.5f, -0.5f,  0.5f,  0,-1,0,  0.0f, 1.0f,  // 17
            -0.5f,  0.5f, -0.5f,  0,1,0,   0.0f, 1.0f,  // 18
            -0.5f,  0.5f,  0.5f,  0,1,0,   0.0f, 0.0f,  // 19
            0.5f, -0.5f, -0.5f,  0,-1,0,  1.0f, 0.0f,  // 20
            0.5f, -0.5f,  0.5f,  0,-1,0,  1.0f, 1.0f,  // 21
            0.5f,  0.5f, -0.5f,  0,1,0,   1.0f, 1.0f,  // 22
            0.5f,  0.5f,  0.5f,  0,1,0,   1.0f, 0.0f   // 23
    };

    private int[] indices =  new int[] {
            0,1,3, // x -ve
            3,2,0, // x -ve
            4,6,7, // x +ve
            7,5,4, // x +ve
            9,13,15, // z +ve
            15,11,9, // z +ve
            8,10,14, // z -ve
            14,12,8, // z -ve
            16,20,21, // y -ve
            21,17,16, // y -ve
            23,22,18, // y +ve
            18,19,23  // y +ve
    };

}