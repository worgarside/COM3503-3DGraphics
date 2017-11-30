import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
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

    public void rotToAngle(int angle) {
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
    private Mesh sphere, cubeRobot, sphereRing, sphereRingGem;
    private Mesh floor, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, wallLeft, wallRight, wallFront, ceiling;
    private Light light, ringLight;
    private RobotHand robotHand;
    private Gallery gallery;

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
        int[] textureWallDoor = TextureLibrary.loadTexture(gl, "textures/textureWallDoor.jpg");
        int[] textureWall1 = TextureLibrary.loadTexture(gl, "textures/textureWall1.jpg");
        int[] textureWall2 = TextureLibrary.loadTexture(gl, "textures/textureWall2.jpg");
        int[] textureCeiling = TextureLibrary.loadTexture(gl, "textures/textureCeiling.jpg");

        // make meshes
        sphere = new Sphere(gl, textureRobot, textureRobotSpecular);
        cubeRobot = new Cube(gl, textureRobot, textureRobotSpecular);
        sphereRing = new Sphere(gl, textureRing, textureRingSpecular);
        sphereRingGem = new Sphere(gl, textureRobot, textureRobotSpecular);

        light = new Light(gl);
        light.setCamera(camera);

        sphere.setLight(light);
        sphere.setCamera(camera);
        cubeRobot.setLight(light);
        cubeRobot.setCamera(camera);
        sphereRing.setLight(light);//ringLight);
        sphereRing.setCamera(camera);
        sphereRingGem.setLight(light);
        sphereRingGem.setCamera(camera);

        floor.setLight(light);
        floor.setCamera(camera);
        wallBackTop.setLight(light);
        wallBackTop.setCamera(camera);
        wallBackLeft.setLight(light);
        wallBackLeft.setCamera(camera);
        wallBackRight.setLight(light);
        wallBackRight.setCamera(camera);
        wallBackBottom.setLight(light);
        wallBackBottom.setCamera(camera);
        wallLeft.setLight(light);
        wallLeft.setCamera(camera);
        wallRight.setLight(light);
        wallRight.setCamera(camera);
        wallFront.setLight(light);
        wallFront.setCamera(camera);
        ceiling.setLight(light);
        ceiling.setCamera(camera);

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        robotHand = new RobotHand(cubeRobot, sphereRing, sphereRingGem);
        robotHand.initialise(gl);

        gallery = new Gallery(floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, ceiling);
    }

    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);


        updatePerspectiveMatrices();
//        light.setPosition(getLightPosition());
        light.setPosition(robotHand.getRingPos());
        light.render(gl);

        robotHand.render(gl);

        gallery.render(gl);
    }

    private void updatePerspectiveMatrices() {
        // needs to be changed if user resizes the window
        perspective = Mat4Transform.perspective(45, aspect);
        light.setPerspective(perspective);
        sphere.setPerspective(perspective);
        cubeRobot.setPerspective(perspective);
        sphereRing.setPerspective(perspective);
        sphereRingGem.setPerspective(perspective);

        floor.setPerspective(perspective);
        wallBackTop.setPerspective(perspective);
        wallBackLeft.setPerspective(perspective);
        wallBackRight.setPerspective(perspective);
        wallBackBottom.setPerspective(perspective);
        wallLeft.setPerspective(perspective);
        wallRight.setPerspective(perspective);
        wallFront.setPerspective(perspective);
        ceiling.setPerspective(perspective);
    }

    private void disposeMeshes(GL3 gl) {
        light.dispose(gl);
        sphere.dispose(gl);
        cubeRobot.dispose(gl);
        sphereRing.dispose(gl);
        sphereRingGem.dispose(gl);

        floor.dispose(gl);
        wallBackTop.dispose(gl);
        wallBackLeft.dispose(gl);
        wallBackRight.dispose(gl);
        wallBackTop.dispose(gl);
        wallLeft.dispose(gl);
        wallRight.dispose(gl);
        wallFront.dispose(gl);
        ceiling.dispose(gl);
    }

    private void updateRingLight(){
//        light.setRingLightPos(robotHand.getRingPos());
//        light.setRingLightDir(robotHand.getRingDir());
    }

}