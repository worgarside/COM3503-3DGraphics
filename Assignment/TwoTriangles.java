import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class TwoTriangles extends Mesh {

    private int[] textureId;
    private static final Vec3 SCENE_AMBIENT = new Vec3(0.05f, 0.05f, 0.05f);

    public TwoTriangles(GL3 gl, int[] textureId) {
        super(gl);
        super.vertices = this.vertices;
        super.indices = this.indices;
        this.textureId = textureId;
        material.setAmbient(SCENE_AMBIENT);

        material.setAllDiffusePoints(0.75f, 0.75f, 0.75f);
        material.setAllDiffuseSpots(1f, 1f, 1f);
        material.setAllSpecularSpots(0.05f, 0.05f, 0.05f);
        material.setAllSpecularSpots(0.5f, 0.5f, 0.5f);

        switch(textureId[0]) {
            case 1 : // Floor
                material.setAllSpecularPoints(0.6f, 0.6f, 0.6f);
                break;
            case 13 : // textureWallLeft
            case 14 : // textureWallRight
            case 12 : // textureWallFront
            case 8 : // textureWallBackTop
            case 9 : // textureWallBackLeft
            case 10 : // textureWallBackRight
            case 11 : // textureWallBackBottom
            case 15 : // textureCeiling
                material.setAllSpecularPoints(0.1f, 0.1f, 0.1f);
                break;
            case 16 : // textureOutside
                material.setAllSpecularPoints(0f, 0f, 0f);
                break;
        }


        material.setShininess(32.0f);
        shader = new Shader(gl, "shaders/vs_tt_05.glsl", "shaders/fs_tt_05.glsl");
        fillBuffers(gl);
    }

    public void render(GL3 gl, Mat4 model) {
        Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);

        shader.setFloatArray(gl, "model", model.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        shader.setVec3(gl, "viewPos", camera.getPosition());

        for (int i = 0; i < Arty.lightCount; i++) {
            shader.setVec3(gl, "lightSources[" + i + "].position", light.getPosition(i));
            shader.setVec3(gl, "lightSources[" + i + "].ambient", SCENE_AMBIENT);
            shader.setVec3(gl, "lightSources[" + i + "].diffuse", light.getMaterial().getDiffusePoint(i));
            shader.setVec3(gl, "lightSources[" + i + "].specular", light.getMaterial().getSpecularPoint(i));
            shader.setFloat(gl, "lightSources[" + i + "].falloffConstant", 1f);      // Change this number
            shader.setFloat(gl, "lightSources[" + i + "].falloffLinear", 0.25f);     // Change this number
            shader.setFloat(gl, "lightSources[" + i + "].falloffQuadratic", 0.1f);   // Change this number
            shader.setVec3(gl, "lightSources[" + i + "].spotDirection", light.getDirection(i));
            shader.setFloat(gl, "lightSources[" + i + "].spotCutoff", light.getCutOff(i));
            shader.setFloat(gl, "lightSources[" + i + "].position", light.getOuterCutOff(i));
            shader.setInt(gl, "lightSources[" + i + "].spotlight", light.getSpotlight(i));
            shader.setVec3(gl, "material.diffuse", material.getDiffusePoint(i));
            shader.setVec3(gl, "material.specular", material.getSpecularPoint(i));
        }


        shader.setVec3(gl, "material.ambient", material.getAmbient());
        shader.setFloat(gl, "material.shininess", material.getShininess());

        shader.setInt(gl, "first_texture", 0);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        super.dispose(gl);
        gl.glDeleteBuffers(1, textureId, 0);
    }

    // ***************************************************
  /* THE DATA
   */
    // anticlockwise/counterclockwise ordering
    private float[] vertices = {      // position, colour, tex coords
            -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
            -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
            0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
            0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f   // top right
    };

    private int[] indices = {         // Note that we start from 0!
            0, 1, 2,
            0, 2, 3
    };

}