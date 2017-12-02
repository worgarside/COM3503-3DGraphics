import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.util.ArrayList;

public class Arty_GLEventListener implements GLEventListener {

    private static final boolean DISPLAY_SHADERS = false;
    private float aspect;

    public Arty_GLEventListener(Camera camera) {
        this.camera = camera;
    }

    // ***************************************************
    /*
    * METHODS DEFINED BY GLEventListener
    */

    /* Initialisation */
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
        gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
        gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
        gl.glEnable(GL2.GL_LIGHTING);
        initialise(gl);
        startTime = getSeconds();
    }

    /* Called to indicate the drawing surface has been moved and/or resized  */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        aspect = (float)width/(float)height;
    }

    /* Draw */
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    /* Clean up memory, if necessary */
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        disposeMeshes(gl);
    }

    // ***************************************************
    /* TIME
    */

    private double startTime;

    public static double getSeconds() {
        return System.currentTimeMillis()/1000.0;
    }

    // ***************************************************
      /* An array of random numbers
       */

    private int NUM_RANDOMS = 1000;
    private float[] randoms;

    private void createRandomNumbers() {
        randoms = new float[NUM_RANDOMS];
        for (int i=0; i<NUM_RANDOMS; ++i) {
            randoms[i] = (float)Math.random();
        }
    }

    // ***************************************************
    /* INTERACTION
    *
    *
    */

    private boolean animation = false;
    private double savedTime = 0;

    public void startAnimation() {
        animation = true;
        startTime = getSeconds()-savedTime;
    }

    public void stopAnimation() {
        animation = false;
        double elapsedTime = getSeconds()-startTime;
        savedTime = elapsedTime;
    }

    public void rotArmToAngle(int angle) {
        robotHand.rotRHToAngle(angle);
    }

    public void changeHandPos(char letter){
        robotHand.setRobotHandPos(letter);
    }

    // ***************************************************
    /* THE SCENE
    * Now define all the methods to handle the scene.
    * This will be added to in later examples.
    */

    private Camera camera;
    private Mat4 perspective;
    private Mesh sphere, cubeRobot, sphereRing, sphereRingGem, cubeLampBase, cubeLampBody;
    private Mesh floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, ceiling, outside;
    private Light light;
    private RobotHand robotHand;
    private Gallery gallery;
    private Lamp lamp1, lamp2, lamp3, lamp4;
    private ArrayList<Mesh> meshList = new ArrayList<Mesh>();
    private float gallerySize = 24f;

    private void initialise(GL3 gl) {
        createRandomNumbers();
        int[] textureFloor = TextureLibrary.loadTexture(gl, "textures/textureFloor.jpg");
        int[] textureRobot = TextureLibrary.loadTexture(gl, "textures/textureRobot.jpg");
        int[] textureRobotSpecular = TextureLibrary.loadTexture(gl, "textures/textureRobotSpecular.jpg");
        int[] textureRing = TextureLibrary.loadTexture(gl, "textures/textureRing.jpg");
        int[] textureRingSpecular = TextureLibrary.loadTexture(gl, "textures/textureRingSpecular.jpg");
        int[] textureWallBackTop = TextureLibrary.loadTexture(gl, "textures/textureWallBackTop.jpg");
        int[] textureWallBackLeft = TextureLibrary.loadTexture(gl, "textures/textureWallBackLeft.jpg");
        int[] textureWallBackRight = TextureLibrary.loadTexture(gl, "textures/textureWallBackRight.jpg");
        int[] textureWallBackBottom = TextureLibrary.loadTexture(gl, "textures/textureWallBackBottom.jpg");
        int[] textureWallFront = TextureLibrary.loadTexture(gl, "textures/textureWallFront.jpg");
        int[] textureWallLeft = TextureLibrary.loadTexture(gl, "textures/textureWallLeft.jpg");
        int[] textureWallRight = TextureLibrary.loadTexture(gl, "textures/textureWallRight.jpg");
        int[] textureCeiling = TextureLibrary.loadTexture(gl, "textures/textureCeiling.jpg");
        int[] textureOutside = TextureLibrary.loadTexture(gl, "textures/textureOutside.jpg");
        int[] textureLampBase = TextureLibrary.loadTexture(gl, "textures/textureLampBase.jpg");
        int[] textureLampBody = TextureLibrary.loadTexture(gl, "textures/textureLampBody.jpg");
        int[] textureDefaultSpecular = TextureLibrary.loadTexture(gl, "textures/textureDefaultSpecular.jpg");

        // make meshes
        sphere = new Sphere(gl, textureRobot, textureRobotSpecular);
        cubeRobot = new Cube(gl, textureRobot, textureRobotSpecular);
        sphereRing = new Sphere(gl, textureRing, textureRingSpecular);
        sphereRingGem = new Sphere(gl, textureRobot, textureRobotSpecular);
        cubeLampBase = new Cube(gl, textureLampBase, textureDefaultSpecular);
        cubeLampBody = new Cube(gl, textureLampBody, textureDefaultSpecular);
        meshList.add(sphere);
        meshList.add(cubeRobot);
        meshList.add(sphereRing);
        meshList.add(sphereRingGem);
        meshList.add(cubeLampBase);
        meshList.add(cubeLampBody);

        floor = new TwoTriangles(gl, textureFloor);
        wallLeft = new TwoTriangles(gl, textureWallLeft);
        wallRight = new TwoTriangles(gl, textureWallRight);
        wallFront = new TwoTriangles(gl, textureWallFront);
        wallBackTop = new TwoTriangles(gl, textureWallBackTop);
        wallBackLeft = new TwoTriangles(gl, textureWallBackLeft);
        wallBackRight = new TwoTriangles(gl, textureWallBackRight);
        wallBackBottom = new TwoTriangles(gl, textureWallBackBottom);
        ceiling = new TwoTriangles(gl, textureCeiling);
        outside = new TwoTriangles(gl, textureOutside);

        meshList.add(floor);
        meshList.add(wallLeft);
        meshList.add(wallRight);
        meshList.add(wallFront);
        meshList.add(wallBackTop);
        meshList.add(wallBackLeft);
        meshList.add(wallBackRight);
        meshList.add(wallBackBottom);
        meshList.add(ceiling);
        meshList.add(outside);


        light = new Light(gl);
        light.setCamera(camera);

        for (Mesh mesh : meshList) {
            mesh.setLight(light);
            mesh.setCamera(camera);
        }

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        robotHand = new RobotHand(cubeRobot, sphereRing, sphereRingGem);
        robotHand.initialise(gl);

        gallery = new Gallery(gallerySize, floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, ceiling, outside);

        lamp1 = new Lamp(cubeLampBase, cubeLampBody, cubeRobot, new Vec3(gallerySize*0.4f, 0, -gallerySize*0.4f));
        lamp1.initialise(gl);

        lamp2 = new Lamp(cubeLampBase, cubeLampBody, cubeRobot, new Vec3(gallerySize*0.4f, 0, gallerySize*0.4f));
        lamp2.initialise(gl);

        lamp3 = new Lamp(cubeLampBase, cubeLampBody, cubeRobot, new Vec3(-gallerySize*0.4f, 0, gallerySize*0.4f));
        lamp3.initialise(gl);

        lamp4 = new Lamp(cubeLampBase, cubeLampBody, cubeRobot, new Vec3(-gallerySize*0.4f, 0, -gallerySize*0.4f));
        lamp4.initialise(gl);
    }

    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        updatePerspectiveMatrices();

//        light.setPosition(0, robotHand.getRingPos());
        light.setPosition(0, getLightPosition());


        light.render(gl);

        robotHand.render(gl);
        gallery.render(gl);
        lamp1.render(gl);
        lamp2.render(gl);
        lamp3.render(gl);
        lamp4.render(gl);
    }

    private void updatePerspectiveMatrices() {
        perspective = Mat4Transform.perspective(45, aspect);

        light.setPerspective(perspective);
        for (Mesh mesh : meshList) {
            mesh.setPerspective(perspective);
        }
    }

    private void disposeMeshes(GL3 gl) {
        light.dispose(gl);

        for (Mesh mesh : meshList) {
            mesh.dispose(gl);
        }
    }

    private Vec3 getLightPosition() {
        double elapsedTime = getSeconds()-startTime;
        float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
        float y = 2.7f;
        float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
        return new Vec3(x,y,z);
        //return new Vec3(5f,3.4f,5f);
    }
}