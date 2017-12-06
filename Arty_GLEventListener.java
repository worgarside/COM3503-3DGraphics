import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import gmaths.*;

import java.util.ArrayList;

/**
 * Arty_GLEventListener.java
 * Contains all GL initialisation and interaction for Arty.java
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
public class Arty_GLEventListener implements GLEventListener {

    private float aspect;
    private Camera camera;
    private Mat4 perspective;
    private Mesh sphere, cubeRobot, sphereRing, sphereRingGem, cubeLampBase, cubeLampBody, cubeWindowFrame;
    private Mesh floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight,
            wallBackBottom, ceiling, outsideDay, outsideNight;
    private Light light;
    private RobotHand robotHand;
    private Gallery gallery;
    private Lamp lamp1, lamp2;
    private WindowFrame windowFrame;
    private ArrayList<Mesh> meshList = new ArrayList<Mesh>();
    private float gallerySize = 24f;
    private boolean lampsOn = true;
    private boolean worldLightOn = false;
    private boolean dayNightCycle = true;
    private double time = 0;
    private float worldLightPower = 1;

    // ------------ Constructor & GL Functions ------------ \\

    /**
     * Constructor for JOGL Event Listener
     *
     * @param camera
     */
    public Arty_GLEventListener(Camera camera) {
        this.camera = camera;
    }

    /**
     * initialises JOGL Event Listener
     *
     * @param drawable
     */
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL2.GL_LIGHTING);
        initialise(gl);
    }

    /**
     * Allows reshape of Arty frame
     *
     * @param drawable
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        aspect = (float)width/(float)height;
    }

    /**
     * Draws and renders scene
     *
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    // ------------ Scene Creation and Modification ------------ \\

    /**
     * Contains all the code needed to intialise the scene, including textures, meshes, all nodes and
     * object initialisation.
     *
     * @param gl - the graphics library
     */
    private void initialise(GL3 gl) {

        // ------------ Texture Imports & Mesh Creation ------------ \\

        int[] textureRobot = TextureLibrary.loadTexture(gl, "textures/textureRobot.jpg");
        int[] textureRobotSpecular = TextureLibrary.loadTexture(gl, "textures/textureRobotSpecular.jpg");
        int[] textureRing = TextureLibrary.loadTexture(gl, "textures/textureRing.jpg");
        int[] textureRingSpecular = TextureLibrary.loadTexture(gl, "textures/textureRingSpecular.jpg");
        int[] textureRingGem = TextureLibrary.loadTexture(gl, "textures/textureRingGem.jpg");
        int[] textureRingGemSpecular = TextureLibrary.loadTexture(gl, "textures/textureRingGemSpecular.jpg");
        int[] textureFloor = TextureLibrary.loadTexture(gl, "textures/textureFloor.jpg");
        int[] textureWallLeft = TextureLibrary.loadTexture(gl, "textures/textureWallLeft.jpg");
        int[] textureWallRight = TextureLibrary.loadTexture(gl, "textures/textureWallRight.jpg");
        int[] textureWallFront = TextureLibrary.loadTexture(gl, "textures/textureWallFront.jpg");
        int[] textureWallBackTop = TextureLibrary.loadTexture(gl, "textures/textureWallBackTop.jpg");
        int[] textureWallBackLeft = TextureLibrary.loadTexture(gl, "textures/textureWallBackLeft.jpg");
        int[] textureWallBackRight = TextureLibrary.loadTexture(gl, "textures/textureWallBackRight.jpg");
        int[] textureWallBackBottom = TextureLibrary.loadTexture(gl, "textures/textureWallBackBottom.jpg");
        int[] textureCeiling = TextureLibrary.loadTexture(gl, "textures/textureCeiling.jpg");
        int[] textureOutsideDay = TextureLibrary.loadTexture(gl, "textures/textureOutsideDay.jpg");
        int[] textureOutsideNight = TextureLibrary.loadTexture(gl, "textures/textureOutsideNight.jpg");
        int[] textureLampBase = TextureLibrary.loadTexture(gl, "textures/textureLampBase.jpg");
        int[] textureLampBody = TextureLibrary.loadTexture(gl, "textures/textureLampBody.jpg");
        int[] textureDefaultSpecularLow = TextureLibrary.loadTexture(gl, "textures/textureDefaultSpecularLow.jpg");
        int[] textureDefaultSpecularHigh = TextureLibrary.loadTexture(gl, "textures/textureDefaultSpecularHigh.jpg");
        int[] textureWindowFrame = TextureLibrary.loadTexture(gl, "textures/textureWindowFrame.jpg");

        sphere = new Sphere(gl, textureRobot, textureRobotSpecular);
        cubeRobot = new Cube(gl, textureRobot, textureRobotSpecular);
        sphereRing = new Sphere(gl, textureRing, textureRingSpecular);
        sphereRingGem = new Sphere(gl, textureRingGem, textureRingGemSpecular);
        cubeLampBase = new Cube(gl, textureLampBase, textureDefaultSpecularLow);
        cubeLampBody = new Cube(gl, textureLampBody, textureDefaultSpecularLow);
        cubeWindowFrame = new Cube(gl, textureWindowFrame, textureDefaultSpecularLow);
        meshList.add(sphere);
        meshList.add(cubeRobot);
        meshList.add(sphereRing);
        meshList.add(sphereRingGem);
        meshList.add(cubeLampBase);
        meshList.add(cubeLampBody);
        meshList.add(cubeWindowFrame);

        floor = new TwoTriangles(gl, textureFloor);
        wallLeft = new TwoTriangles(gl, textureWallLeft);
        wallRight = new TwoTriangles(gl, textureWallRight);
        wallFront = new TwoTriangles(gl, textureWallFront);
        wallBackTop = new TwoTriangles(gl, textureWallBackTop);
        wallBackLeft = new TwoTriangles(gl, textureWallBackLeft);
        wallBackRight = new TwoTriangles(gl, textureWallBackRight);
        wallBackBottom = new TwoTriangles(gl, textureWallBackBottom);
        ceiling = new TwoTriangles(gl, textureCeiling);
        outsideDay = new TwoTriangles(gl, textureOutsideDay);
        outsideNight = new TwoTriangles(gl, textureOutsideNight);

        meshList.add(floor);
        meshList.add(wallLeft);
        meshList.add(wallRight);
        meshList.add(wallFront);
        meshList.add(wallBackTop);
        meshList.add(wallBackLeft);
        meshList.add(wallBackRight);
        meshList.add(wallBackBottom);
        meshList.add(ceiling);
        meshList.add(outsideDay);
        meshList.add(outsideNight);

        light = new Light(gl);
        light.setCamera(camera);

        for (Mesh mesh : meshList) {
            mesh.setLight(light);
            mesh.setCamera(camera);
        }

        // ------------ Objects ------------ \\

        robotHand = new RobotHand(cubeRobot, sphereRing, sphereRingGem);
        robotHand.initialise(gl);

        gallery = new Gallery(gallerySize, floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, ceiling, outsideDay, outsideNight);

        lamp1 = new Lamp(1, cubeLampBase, cubeLampBody, new Vec3(-gallerySize * 0.4f, 0, -gallerySize * 0.4f));
        lamp1.initialise(gl);

        lamp2 = new Lamp(2, cubeLampBase, cubeLampBody, new Vec3(gallerySize * 0.4f, 0, -gallerySize * 0.4f));
        lamp2.initialise(gl);

        windowFrame = new WindowFrame(cubeWindowFrame, gallerySize);
        windowFrame.initialise(gl);
    }

    /**
     * Set robot arm bearing
     *
     * @param angle - the new angle for the arm
     */
    public void rotArmToAngle(int angle) {
        robotHand.setArmBearing(angle);
    }

    /**
     * Move the RobotHand to match an imported keyframe
     *
     * @param keyframe - the keyframe to move the RobotHand to
     */
    public void setHandKeyframe(int keyframe){
        robotHand.moveToKeyframe(keyframe);
    }

    // ------------ User Toggle Functions ------------ \\

    /**
     * Toggles lamps in scene
     */
    public void toggleLamps() {
        if (lampsOn) {
            lamp1.setState(light, 0);
            lamp2.setState(light, 0);
            lampsOn = false;
        } else {
            lamp1.setState(light, 1);
            lamp2.setState(light, 1);
            lampsOn = true;
        }
    }

    /**
     * Toggles main world light
     */
    public void toggleWorldLight() {
        worldLightOn = !worldLightOn;
    }

    /**
     * Toggles Day/Night cycle
     */
    public void toggleDayNight() {
        dayNightCycle = !dayNightCycle;
    }

    /**
     * Toggles repeating loop of imported keyframes
     */
    public void toggleKeyframeSequence() {
        robotHand.toggleKeyframeSequence();
    }

    /**
     * Toggles all animations in scene
     */
    public void toggleGlobalAnims() {
        robotHand.toggleGlobalAnims();
    }

    // ------------ Scene Management ------------ \\

    /**
     * Removes all meshes from memory on system exit
     *
     * @param drawable
     */
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        light.dispose(gl);
        for (Mesh mesh : meshList) {
            mesh.dispose(gl);
        }
    }

    /**
     * Renders all object in scene
     * Progresses Day/Night cycle and sets world light brightness
     *
     * @param gl
     */
    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        updatePerspectiveMatrices();

        light.setPosition(0, robotHand.getRingPos()); //set spotlight pos
        light.setDirection(0, robotHand.getRingDir()); //set spotlight pos
        light.setPosition(1, lamp1.getLightBulbPos()); //set bulb pos
        light.setPosition(2, lamp2.getLightBulbPos()); //set bulb pos

        // Render all objects
        light.render(gl);
        robotHand.render(gl);
        gallery.render(gl);
        lamp1.render(gl);
        lamp2.render(gl);
        windowFrame.render(gl);

        // Updates time to reflect Day/Night cycle by changing the outside scene and the world light brightness
        if (dayNightCycle){
            time = System.currentTimeMillis() / 1000.0 % 60;
            Arty.updateClock((int) time);
            if (time < 30) {
                Arty.night = false;
                worldLightPower = 1f;
            }else{
                Arty.night = true;
                worldLightPower = 0.2f;
            }
        }

        if (worldLightOn) {
            light.setPower(3, 0);
        } else {
            light.setPower(3, worldLightPower);
        }
    }

    private void updatePerspectiveMatrices() {
        perspective = Mat4Transform.perspective(45, aspect);
        light.setPerspective(perspective);
        for (Mesh mesh : meshList) {
            mesh.setPerspective(perspective);
        }
    }
}