import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Light {

    private Material material;
    private Vec3 position;
    private Mat4 model;
    private Shader shader;
    private Camera camera;
    private Mat4 perspective;
    private Vec3 spotlightDirection = new Vec3(16f, 16f, 16f);
    private Vec3 spotlightPosition;


    private Vec3[] pointLightPositions = new Vec3[] {
            new Vec3 (3f, 5f, 4f),
            new Vec3 (-3f, 5f, 4f)
    };

    public Light(GL3 gl) {
        material = new Material();
        material.setAmbient(0.5f, 0.5f, 0.5f);
        material.setDiffuse(0.8f, 0.8f, 0.8f);
        material.setSpecular(1.0f, 1.0f, 1.0f);
        position = new Vec3(7f, 2f, 1f);
        model = new Mat4(1);
        shader = new Shader(gl, "vs_light_01.glsl", "fs_light_01.glsl");
        fillBuffers(gl);
    }

    public void setPosition(Vec3 v) {
        position.x = v.x;
        position.y = v.y;
        position.z = v.z;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getPointLightPosition(int lightNum) {
        return pointLightPositions[lightNum];
    }

    public void setSpotlightPosition(Vec3 spotlightPosition) {
        this.spotlightPosition = spotlightPosition;
    }

    public Vec3 getSpotlightPosition(){
        return spotlightPosition;
    }

    public void setSpotlightDirection(Vec3 spotlightDirection){
        this.spotlightDirection = spotlightDirection;
    }

    public Vec3 getSpotlightDirection(){
        return spotlightDirection;
    }

    public void setMaterial(Material m) {
        material = m;
    }

    public Material getMaterial() {
        return material;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setPerspective(Mat4 perspective) {
        this.perspective = perspective;
    }

    public void render(GL3 gl) {
        gl.glBindVertexArray(vertexArrayId[0]);

        shader.use(gl);
        for (int i = 0; i < pointLightPositions.length; i++) {
            setLightColor(gl, i);
            drawLight(gl, i);
        }

        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, vertexBufferId, 0);
        gl.glDeleteVertexArrays(1, vertexArrayId, 0);
        gl.glDeleteBuffers(1, elementBufferId, 0);
    }

    // ***************************************************
    // THE DATA
    // anticlockwise/counterclockwise ordering

    private float[] vertices = new float[] {  // x,y,z
            -0.5f, -0.5f, -0.5f,  // 0
            -0.5f, -0.5f,  0.5f,  // 1
            -0.5f,  0.5f, -0.5f,  // 2
            -0.5f,  0.5f,  0.5f,  // 3
            0.5f, -0.5f, -0.5f,  // 4
            0.5f, -0.5f,  0.5f,  // 5
            0.5f,  0.5f, -0.5f,  // 6
            0.5f,  0.5f,  0.5f   // 7
    };

    private int[] indices =  new int[] {
            0, 1, 3, // x -ve
            3, 2, 0, // x -ve
            4, 6, 7, // x +ve
            7, 5, 4, // x +ve
            1, 5, 7, // z +ve
            7, 3, 1, // z +ve
            6, 4, 0, // z -ve
            0, 2, 6, // z -ve
            0, 4, 5, // y -ve
            5, 1, 0, // y -ve
            2, 3, 7, // y +ve
            7, 6, 2  // y +ve
    };

    private int vertexStride = 3;
    private int vertexXYZFloats = 3;

    // ***************************************************
  /* THE LIGHT BUFFERS
   */

    private int[] vertexBufferId = new int[1];
    private int[] vertexArrayId = new int[1];
    private int[] elementBufferId = new int[1];

    private void fillBuffers(GL3 gl) {
        gl.glGenVertexArrays(1, vertexArrayId, 0);
        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glGenBuffers(1, vertexBufferId, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
        FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

        int stride = vertexStride;
        int numXYZFloats = vertexXYZFloats;
        int offset = 0;
        gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
        gl.glEnableVertexAttribArray(0);

        gl.glGenBuffers(1, elementBufferId, 0);
        IntBuffer ib = Buffers.newDirectIntBuffer(indices);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
        gl.glBindVertexArray(0);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void drawLight(GL3 gl, int i) {
        Mat4 model;
        Mat4 mvpMatrix;
        model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(1f,1f,1f), model);
        if (i==2 || i==3) {
            model = Mat4.multiply(Mat4Transform.scale(2f,2f,2f), model);
        }
        model = Mat4.multiply(Mat4Transform.translate(pointLightPositions[i]), model);
        model = Mat4.multiply(Mat4Transform.translate(0,0.5f,0), model);

        mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    }

    // sets the color of the light 'bulb' to either grey (off) or white (on)
    public void setLightColor(GL3 gl, int i) {
        shader.setFloat(gl, "lightColor", 1.0f);
    }
}