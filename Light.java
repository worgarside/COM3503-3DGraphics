import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import java.util.ArrayList;

/**
 * Light.java
 * Light object used for illuminating the scene
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
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
    private static ArrayList<Integer> spotlightFlag = new ArrayList<Integer>();
    private static ArrayList<Float> fallOffConstant = new ArrayList<Float>();
    private static ArrayList<Float> fallOffLinear = new ArrayList<Float>();
    private static ArrayList<Float> fallOffQuadratic = new ArrayList<Float>();
    private float lampColor = 1.0f;


    /**
     * Constructor for the Light object, sets all properties
     *
     * @param gl - graphics library
     */
    public Light(GL3 gl) {
        material = new Material();
        material.setAmbient(DEFAULT_AMBIENT);

        // For each lightSource, set the values imported from the csv file
        for(int i = 0; i < Arty.lightCount; i++) {
            originalDiffuse.add(new Vec3(Arty.lightData.get(i)[0], Arty.lightData.get(i)[1], Arty.lightData.get(i)[2]));
            originalSpecular.add(new Vec3(Arty.lightData.get(i)[3], Arty.lightData.get(i)[4], Arty.lightData.get(i)[5]));
            position.add(new Vec3(Arty.lightData.get(i)[6], Arty.lightData.get(i)[7], Arty.lightData.get(i)[8]));
            size.add(new Vec3(Arty.lightData.get(i)[9], Arty.lightData.get(i)[10], Arty.lightData.get(i)[11]));
            direction.add(new Vec3(Arty.lightData.get(i)[12], Arty.lightData.get(i)[13], Arty.lightData.get(i)[14]));
            bulbRotation.add(Arty.lightData.get(i)[15]);
            cutOff.add(Arty.lightData.get(i)[16]);
            outerCutOff.add(Arty.lightData.get(i)[17]);
            spotlightFlag.add(Math.round(Arty.lightData.get(i)[18]));
            fallOffConstant.add(Arty.lightData.get(i)[19]);
            fallOffLinear.add(Arty.lightData.get(i)[20]);
            fallOffQuadratic.add(Arty.lightData.get(i)[21]);

            material.setDiffusePoint(i, originalDiffuse.get(i).x, originalDiffuse.get(i).y, originalDiffuse.get(i).z);
            material.setSpecularPoint(i, originalSpecular.get(i).x, originalSpecular.get(i).y, originalSpecular.get(i).z);
        }
        model = new Mat4(1);
        shader = new Shader(gl, "shaders/vs_light_01.glsl", "shaders/fs_light_01.glsl");
        fillBuffers(gl);
    }

    // ------------ Setters ------------ \\

    /**
     * Sets the position of the lightSource
     *
     * @param i - lightSource reference number
     * @param pos - world coordinates of lightSource
     */
    public void setPosition(int i, Vec3 pos) {
        position.get(i).x = pos.x;
        position.get(i).y = pos.y;
        position.get(i).z = pos.z;
    }

    /**
     * Sets the material the lightSource is shining on
     *
     * @param m - the material
     */
    public void setMaterial(Material m) {
        material = m;
    }

    /**
     * Sets the size of the light bulb
     *
     * @param i - lightSource reference number
     * @param size - size of the bulb as a Vec3
     */
    public void setSize(int i, Vec3 size) {
        this.size.get(i).x = size.x;
        this.size.get(i).y = size.y;
        this.size.get(i).z = size.z;
    }

    /**
     * Sets the Y rotation of the bulb
     *
     * @param i - lightSource reference number
     * @param rot - rotation value in degrees
     */
    public void setRotation(int i, float rot) {
        bulbRotation.set(i, rot);
    }

    /**
     * Sets the direction that the light is shining in
     * Only used for spotlight
     *
     * @param i - lightSource reference number
     * @param dir - the direction as a Vec3
     */
    public void setDirection(int i, Vec3 dir) {
        direction.get(i).x = dir.x;
        direction.get(i).y = dir.y;
        direction.get(i).z = dir.z;
    }

    /**
     * Sets the cutOff value for the spotlight
     *
     * @param i - lightSource reference number
     * @param cutoff - cutOff angle
     */
    public void setCutoff(int i, float cutoff) {
        this.cutOff.set(i, cutoff);
    }

    /**
     * Sets the outCutOff value for the spotlight
     *
     * @param i - lightSource reference number
     * @param outerCutOff - outerCutOff angle
     */
    public void setOuterCutOff(int i, float outerCutOff) {
        this.outerCutOff.set(i, outerCutOff);
    }

    /**
     * Sets the perspective view of the Light
     *
     * @param perspective - the perspective Matrix as a Mat4
     */
    public void setPerspective(Mat4 perspective) {
        this.perspective = perspective;
    }

    /**
     * Sets the reference camera for the light
     *
     * @param camera - the Camera object for the user
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    // ------------ Getters ------------ \\

    /**
     * Gets the position of the light
     *
     * @param i - lightSource reference number
     * @return the world space coords as a Vec3
     */
    public Vec3 getPosition(int i) {
        return position.get(i);
    }

    /**
     * Gets the material the light is shining on
     *
     * @return the material as a Material object
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the size of the light bulb
     *
     * @param i - lightSource reference number
     * @return the size as a Vec3
     */
    public Vec3 getSize(int i) {
        return size.get(i);
    }

    /**
     * Gets the rotation of the light bulb
     *
     * @param i - lightSource reference number
     * @return the rotation angle as a float
     */
    public float getRotation(int i) {
        return bulbRotation.get(i);
    }

    /**
     * Gets the direction the light is shining in
     *
     * @param i - lightSource reference number
     * @return the direction as a Vec3
     */
    public Vec3 getDirection(int i) {
        return direction.get(i);
    }

    /**
     * Gets the cutOff angle for the spotlight
     *
     * @param i - lightSource reference number
     * @return the cutOff angle as a float
     */
    public float getCutOff(int i) {
        return cutOff.get(i);
    }

    /**
     * Gets the outerCutOff value for the spotlight
     *
     * @param i - lightSource reference number
     * @return the outerCutOff as a float
     */
    public float getOuterCutOff(int i) {
        return outerCutOff.get(i);
    }

    /**
     * Gets the spotlight flag for a lightSource (i.e. whether it's a spotlight or a point light)
     *
     * @param i - lightSource reference number
     * @return the flag as an int
     */
    public int getSpotlight(int i) {
        return spotlightFlag.get(i);
    }

    /**
     * gets the constant fallOff value for the lightSource
     *
     * @param i - lightSource reference number
     * @return constant fallOff value as a float
     */
    public float getFallOffConstant(int i) {
        return fallOffConstant.get(i);
    }

    /**
     * gets the linear fallOff value for the lightSource
     *
     * @param i - lightSource reference number
     * @return linear fallOff value as a float
     */
    public float getFallOffLinear(int i) {
        return fallOffLinear.get(i);
    }

    /**
     * gets the quadratic fallOff value for the lightSource
     *
     * @param i - lightSource reference number
     * @return quadratic fallOff value as a float
     */
    public float getFallOffQuadratic(int i) {
        return fallOffQuadratic.get(i);
    }

    // ------------ Methods ------------ \\

    /**
     * Renders the light bulb for a particular source
     * The spotlight is omitted as the bulb 'is' the ringGem
     *
     * @param gl - graphics library
     */
    public void render(GL3 gl) {
        gl.glBindVertexArray(vertexArrayId[0]);
        Mat4 m = new Mat4(1);

        for (int i = 0; i < Arty.lightCount; i++) {
            // Check that the light is not a spotlight, then render the bulb
            if (spotlightFlag.get(i) != 1) {
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

        shader.setFloat(gl, "lightColor", lampColor);

        gl.glBindVertexArray(0);
    }

    /**
     * Removes the Meshes from memory on system exit
     *
     * @param gl - graphics library
     */
    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, vertexBufferId, 0);
        gl.glDeleteVertexArrays(1, vertexArrayId, 0);
        gl.glDeleteBuffers(1, elementBufferId, 0);
    }

    /**
     * Sets the power level/brightness of a particular light source
     *
     * @param i - lightSource reference number
     * @param powerLevel - the continuous power level of the lightSource
     */
    public void setPower(int i, float powerLevel) {
        material.setDiffusePoint(i, originalDiffuse.get(i).x * powerLevel, originalDiffuse.get(i).y * powerLevel, originalDiffuse.get(i).z * powerLevel);
        material.setSpecularPoint(i, originalSpecular.get(i).x * powerLevel, originalSpecular.get(i).y * powerLevel, originalSpecular.get(i).z * powerLevel);
        if (i != 3) {
            lampColor = powerLevel + 0.1f;
        }
    }

    // ------------ Data ------------ \\

    private float[] vertices = new float[] {
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

    /**
     * Sends the data to the GPU buffers
     *
     * @param gl - graphics library
     */
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
        gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);
        gl.glEnableVertexAttribArray(0);

        gl.glGenBuffers(1, elementBufferId, 0);
        IntBuffer ib = Buffers.newDirectIntBuffer(indices);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
        gl.glBindVertexArray(0);
    }

}