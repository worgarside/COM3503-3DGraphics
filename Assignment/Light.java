import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import java.util.ArrayList;

public class Light {

    public static final Vec3 DEFAULT_AMBIENT = new Vec3(0.2f, 0.2f, 0.2f);

    private Material material;
    private Vec3 bulbPos;
    private Mat4 model;
    private Shader shader;
    private Camera camera;
    private Mat4 perspective;

    private static ArrayList<Vec3> originalDiffuse = new ArrayList<Vec3>();
    private static ArrayList<Vec3> originalSpecular = new ArrayList<Vec3>();
    private static ArrayList<Vec3> position = new ArrayList<Vec3>();
    private static ArrayList<Vec3> size = new ArrayList<Vec3>();
    private static ArrayList<Vec3> direction = new ArrayList<Vec3>();
    private static ArrayList<Float> bulbRotation = new ArrayList<Float>();
    private static ArrayList<Float> cutOff = new ArrayList<Float>();
    private static ArrayList<Float> outerCutOff = new ArrayList<Float>();
    private static ArrayList<Integer> spotlight = new ArrayList<Integer>();

    // ------------ Constructor ------------ \\

    public Light(GL3 gl) {
        material = new Material();
        material.setAmbient(DEFAULT_AMBIENT);

        for(int i = 0; i < Arty.lightCount; i++) {
            originalDiffuse.add(new Vec3(Arty.lightData.get(i)[0], Arty.lightData.get(i)[1], Arty.lightData.get(i)[2]));
            originalSpecular.add(new Vec3(Arty.lightData.get(i)[3], Arty.lightData.get(i)[4], Arty.lightData.get(i)[5]));
            position.add(new Vec3(Arty.lightData.get(i)[6], Arty.lightData.get(i)[7], Arty.lightData.get(i)[8]));
            size.add(new Vec3(Arty.lightData.get(i)[9], Arty.lightData.get(i)[10], Arty.lightData.get(i)[11]));
            direction.add(new Vec3(Arty.lightData.get(i)[12], Arty.lightData.get(i)[13], Arty.lightData.get(i)[14]));
            bulbRotation.add(Arty.lightData.get(i)[15]);
            cutOff.add(Arty.lightData.get(i)[16]);
            outerCutOff.add(Arty.lightData.get(i)[17]);
            spotlight.add(Math.round(Arty.lightData.get(i)[18]));

            material.setDiffusePoint(i, originalDiffuse.get(i).x, originalDiffuse.get(i).y, originalDiffuse.get(i).z);
            material.setSpecularPoint(i, originalSpecular.get(i).x, originalSpecular.get(i).y, originalSpecular.get(i).z);
        }
        model = new Mat4(1);
        shader = new Shader(gl, "shaders/vs_light_01.glsl", "shaders/fs_light_01.glsl");
        fillBuffers(gl);
    }

    // ------------ Setters ------------ \\
    public void setPosition(int i, Vec3 pos) {
        position.get(i).x = pos.x;
        position.get(i).y = pos.y;
        position.get(i).z = pos.z;
    }

    public void setMaterial(Material m) {
        material = m;
    }

    public void setSize(int i, Vec3 size){
        this.size.get(i).x = size.x;
        this.size.get(i).y = size.y;
        this.size.get(i).z = size.z;
    }

    public void setRotation(int i, float rot){
        bulbRotation.set(i, rot);
    }

    public void setDirection(int i, Vec3 dir){
        direction.get(i).x = dir.x;
        direction.get(i).y = dir.y;
        direction.get(i).z = dir.z;
    }

    public void setCutoff(int i, float cutoff){
        this.cutOff.set(i, cutoff);
    }

    public void setExponent(int i, float exp){
        outerCutOff.set(i, exp);
    }

    public void setPerspective(Mat4 perspective) {
        this.perspective = perspective;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    // ------------ Getters ------------ \\

    public Vec3 getPosition(int i) {
        return position.get(i);
    }

    public Material getMaterial() {
        return material;
    }

    public Vec3 getSize(int i){
        return size.get(i);
    }

    public float getRotation(int i){
        return bulbRotation.get(i);
    }

    public Vec3 getDirection(int i){
        return direction.get(i);
    }

    public float getCutOff(int i){
        return cutOff.get(i);
    }

    public float getOuterCutOff(int i){
        return outerCutOff.get(i);
    }

    public int getSpotlight(int i) {
        return spotlight.get(i);
    }

    // ------------ Methods ------------ \\

    public void render(GL3 gl) {
        gl.glBindVertexArray(vertexArrayId[0]);
        Mat4 m = new Mat4(1);

        for (int i = 0; i < Arty.lightCount; i++) {
            if (spotlight.get(i) != 1) {
                m = new Mat4(1);
                m = Mat4.multiply(Mat4Transform.scale(size.get(i)), m);
                m = Mat4.multiply(Mat4Transform.rotateAroundY(bulbRotation.get(i)), m);
                m = Mat4.multiply(Mat4Transform.translate(position.get(i)), m);
                Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), m));
                shader.use(gl);
                shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
                gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
            }
        }

        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, vertexBufferId, 0);
        gl.glDeleteVertexArrays(1, vertexArrayId, 0);
        gl.glDeleteBuffers(1, elementBufferId, 0);
    }

    public void setPower(int lightNum, int powerLevel) {
        material.setDiffusePoint(lightNum, originalDiffuse.get(lightNum).x*powerLevel, originalDiffuse.get(lightNum).y*powerLevel, originalDiffuse.get(lightNum).z*powerLevel);
        material.setSpecularPoint(lightNum, originalSpecular.get(lightNum).x*powerLevel, originalSpecular.get(lightNum).y*powerLevel, originalSpecular.get(lightNum).z*powerLevel);
    }

    public String toString() {
        return originalDiffuse.get(0) + ", " + originalSpecular.get(0) + ", " + position.get(0) + ", " + size.get(0) + ", " + direction.get(0) + ", " + bulbRotation.get(0) + ", " + cutOff.get(0) + ", " + outerCutOff.get(0) + ", " + spotlight.get(0);
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