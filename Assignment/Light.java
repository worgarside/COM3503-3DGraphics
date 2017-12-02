import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Light {

    public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);
    private static final int LIGHT_COUNT = 3;

    private Material material;
    private Vec3 bulbPos;
    private Mat4 model;
    private Shader shader;
    private Camera camera;
    private Mat4 perspective;

    private Vec3 originalDiffuse[] = new Vec3[] {
            new Vec3(0.75f, 0.75f, 0.75f),
            new Vec3(0.75f, 0.75f, 0.75f),
            new Vec3(0.75f, 0.75f, 0.75f),
    };

    private Vec3 originalSpecular[] = new Vec3[] {
            new Vec3(0.0f, 0.0f, 0.0f),
            new Vec3(1.0f, 1.0f, 1.0f),
            new Vec3(1.0f, 1.0f, 1.0f),
    };

    private Vec3[] position = new Vec3[] {
            new Vec3(3f, 4f, 5f),
            new Vec3(-4f, 4f, -5f),
            new Vec3(7f, 6f, -5f),
    };

    private Vec3[] size = new Vec3[] {
            new Vec3(0.2f, 0.2f, 0.2f),
            new Vec3(1.38f, 1.38f, 1.38f),
            new Vec3(1.38f, 1.38f, 1.38f),
    };

    private float[] bulbRotation = new float[] {0, 45, 45};

    private Vec3[] direction = new Vec3[] {
            new Vec3(0.5f, 0.5f, 0.5f),
            new Vec3(0.5f, 0.5f, 0.5f),
            new Vec3(0.5f, 0.5f, 0.5f),
    };

    private float[] cutoff = new float[] {0.5f, 0.5f, 0.5f};

    private float[] exponent = new float[] {0.5f, 0.5f, 0.5f};

    // ------------ Constructor ------------ \\

    public Light(GL3 gl) {
        material = new Material();
        material.setAmbient(DEFAULT_AMBIENT);

        for (int i =0; i < LIGHT_COUNT; i++) {
            material.setDiffusePoint(i, originalDiffuse[i].x, originalDiffuse[i].y, originalDiffuse[i].z);
            material.setSpecularPoint(i, originalSpecular[i].x, originalSpecular[i].y, originalSpecular[i].z);
        }
//        material.setAllDiffusePoints(DEFAULT_DIFFUSE.x, DEFAULT_DIFFUSE.y, DEFAULT_DIFFUSE.z);
//        material.setAllSpecularPoints(DEFAULT_SPECULAR.x, DEFAULT_SPECULAR.y, DEFAULT_SPECULAR.z);
//        material.setAllDiffuseSpots(DEFAULT_DIFFUSE.x, DEFAULT_DIFFUSE.y, DEFAULT_DIFFUSE.z);
//        material.setAllSpecularSpots(DEFAULT_SPECULAR.x, DEFAULT_SPECULAR.y, DEFAULT_SPECULAR.z);
        model = new Mat4(1);
        shader = new Shader(gl, "shaders/vs_light_01.glsl", "shaders/fs_light_01.glsl");
        fillBuffers(gl);
    }

    // ------------ Setters ------------ \\
    public void setPosition(int i, Vec3 pos) {
        position[i].x = pos.x;
        position[i].y = pos.y;
        position[i].z = pos.z;
    }

    public void setMaterial(Material m) {
        material = m;
    }

    public void setSize(int i, Vec3 size){
        this.size[i].x = size.x;
        this.size[i].y = size.y;
        this.size[i].z = size.z;
    }

    public void setRotation(int i, float rot){
        bulbRotation[i] = rot;
    }

    public void setDirection(int i, Vec3 dir){
        direction[i].x = dir.x;
        direction[i].y = dir.y;
        direction[i].z = dir.z;
    }

    public void setCutoff(int i, float cutoff){
        this.cutoff[i] = cutoff;
    }

    public void setExponent(int i, float exp){
        exponent[i] = exp;
    }

    public void setPerspective(Mat4 perspective) {
        this.perspective = perspective;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    // ------------ Getters ------------ \\

    public Vec3 getPosition(int i) {
        return position[i];
    }

    public Material getMaterial() {
        return material;
    }

    public Vec3 getSize(int i){
        return size[i];
    }

    public float getRotation(int i){
        return bulbRotation[i];
    }

    public Vec3 getDirection(int i){
        return direction[i];
    }

    public float getCutoff(int i){
        return cutoff[i];
    }

    public float getExponent(int i){
        return exponent[i];
    }

    // ------------ Methods ------------ \\

    public void render(GL3 gl) {
        gl.glBindVertexArray(vertexArrayId[0]);
        Mat4 m = new Mat4(1);

        for (int i = 0; i < position.length; i++) {
            m = new Mat4(1);
            m = Mat4.multiply(Mat4Transform.scale(size[i]), m);
            m = Mat4.multiply(Mat4Transform.rotateAroundY(bulbRotation[i]), m);
            m = Mat4.multiply(Mat4Transform.translate(position[i]), m);
            Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), m));
            shader.use(gl);
            shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
            gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        }

        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, vertexBufferId, 0);
        gl.glDeleteVertexArrays(1, vertexArrayId, 0);
        gl.glDeleteBuffers(1, elementBufferId, 0);
    }

    public void setPower(int lightNum, int powerLevel) {
        material.setDiffusePoint(lightNum, originalDiffuse[lightNum].x*powerLevel, originalDiffuse[lightNum].y*powerLevel, originalDiffuse[lightNum].z*powerLevel);
        material.setSpecularPoint(lightNum, originalSpecular[lightNum].x*powerLevel, originalSpecular[lightNum].y*powerLevel, originalSpecular[lightNum].z*powerLevel);

    }

    // ------------ Data ------------ \\

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

    // ------------ Buffers ------------ \\

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

}