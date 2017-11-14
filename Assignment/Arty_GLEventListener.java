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

    private double getSeconds() {
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

    public void incXPosition() {
        xPosition += 0.5f;
        if (xPosition>5f) xPosition = 5f;
        updateMove();
    }
   
    public void decXPosition() {
        xPosition -= 0.5f;
        if (xPosition<-5f) xPosition = -5f;
        updateMove();
    }
 
    private void updateMove() {
        robotHandMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
        robotHandMoveTranslate.update();
    }
  
    public void loweredArms() {
        stopAnimation();
    //    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    //    leftArmRotate.update();
    //    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    //    rightArmRotate.update();
    }
   
    public void raisedArms() {
        stopAnimation();
    //    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    //    leftArmRotate.update();
    //    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    //    rightArmRotate.update();
    }

    // ***************************************************
    /* THE SCENE
    * Now define all the methods to handle the scene.
    * This will be added to in later examples.
    */

    private Camera camera;
    private Mat4 perspective;
    private Mesh floor, sphere, cube, cube2;
    private Light light;
    private SGNode robotHand;

    private float xPosition = 0;
    private TransformNode translateX, robotHandMoveTranslate; //, leftArmRotate, rightArmRotate;
  
    private void initialise(GL3 gl) {
        createRandomNumbers();
        int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
        int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
        int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
        int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
        int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
        int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
        int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");

        // make meshes
        floor = new TwoTriangles(gl, textureId0);
        floor.setModelMatrix(Mat4Transform.scale(16,1,16));
        sphere = new Sphere(gl, textureId1, textureId2);
        cube = new Cube(gl, textureId3, textureId4);
        cube2 = new Cube(gl, textureId5, textureId6);

        light = new Light(gl);
        light.setCamera(camera);

        floor.setLight(light);
        floor.setCamera(camera);
        sphere.setLight(light);
        sphere.setCamera(camera);
        cube.setLight(light);
        cube.setCamera(camera);
        cube2.setLight(light);
        cube2.setCamera(camera);

        // make nodes
        MeshNode armShape = new MeshNode("Cube(arm)", cube);
        MeshNode palmShape = new MeshNode("Cube(palm)", cube);
        MeshNode fing1ProxShape = new MeshNode("Cube(fing1Prox)", cube);
        MeshNode fing1MiddShape = new MeshNode("Cube(fing1Midd)", cube);
        MeshNode fing1DistShape = new MeshNode("Cube(fing1Dist)", cube);
        MeshNode fing2ProxShape = new MeshNode("Cube(fing2Prox)", cube);
        MeshNode fing2MiddShape = new MeshNode("Cube(fing2Midd)", cube);
        MeshNode fing2DistShape = new MeshNode("Cube(fing2Dist)", cube);
        MeshNode fing3ProxShape = new MeshNode("Cube(fing3Prox)", cube);
        MeshNode fing3MiddShape = new MeshNode("Cube(fing3Midd)", cube);
        MeshNode fing3DistShape = new MeshNode("Cube(fing3Dist)", cube);
        MeshNode fing4ProxShape = new MeshNode("Cube(fing4Prox)", cube);
        MeshNode fing4MiddShape = new MeshNode("Cube(fing4Midd)", cube);
        MeshNode fing4DistShape = new MeshNode("Cube(fing4Dist)", cube);
        MeshNode thumbProxShape = new MeshNode("Cube(thumbProx)", cube);
        MeshNode thumbMiddShape = new MeshNode("Cube(thumbMidd)", cube);
        MeshNode thumbDistShape = new MeshNode("Cube(thumbDist)", cube);


        robotHand = new NameNode("root");
        NameNode arm = new NameNode("arm");
        NameNode palm = new NameNode("palm");
        NameNode fing1Prox = new NameNode("fing1Prox");
        NameNode fing1Midd = new NameNode("fing1Midd");
        NameNode fing1Dist = new NameNode("fing1Dist");
        NameNode fing2Prox = new NameNode("fing2Prox");
        NameNode fing2Midd = new NameNode("fing2Midd");
        NameNode fing2Dist = new NameNode("fing2Dist");
        NameNode fing3Prox = new NameNode("fing3Prox");
        NameNode fing3Midd = new NameNode("fing3Midd");
        NameNode fing3Dist = new NameNode("fing3Dist");
        NameNode fing4Prox = new NameNode("fing4Prox");
        NameNode fing4Midd = new NameNode("fing4Midd");
        NameNode fing4Dist = new NameNode("fing4Dist");
        NameNode thumbProx = new NameNode("thumbProx");
        NameNode thumbMidd = new NameNode("thumbMidd");
        NameNode thumbDist = new NameNode("thumbDist");

        float armWidth = 2f;
        float armHeight = 5f;
        float armDepth = 1.25f;
        float palmWidth = 4f;
        float palmHeight = 4f;
        float palmDepth = 1.25f;
        float fingLrgWidth = 0.8f;
        float fingLrgHeight = 1.5f;
        float fingLrgDepth = 1f;
        float fingMedWidth = 0.7f;
        float fingMedHeight = 1.3f;
        float fingMedDepth = 0.9f;
        float fingDistHeight = armHeight + palmHeight;
        float fingMiddLrgHeight = fingDistHeight + fingLrgHeight;
        float fingProxLMHeight = fingMiddLrgHeight + fingMedHeight;
        float fingProxLLHeight = fingMiddLrgHeight + fingLrgHeight;

        // ------------ Arm + Palm ------------ \\
        robotHandMoveTranslate = new TransformNode("robotHand transform",Mat4Transform.translate(xPosition,0,0));
        TransformNode robotHandTranslate = new TransformNode("robotHand transform",Mat4Transform.translate(0,0,0));

        Mat4 m = Mat4Transform.scale(armWidth, armHeight, armDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode armTransform = new TransformNode("arm transform", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,armHeight,0));
        m = Mat4.multiply(m, Mat4Transform.scale(palmWidth,palmHeight,palmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode palmTransform = new TransformNode("palm transform", m);

        // ------------ Finger #1 (Index) ------------ \\

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-1.5f, fingDistHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingLrgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing1DistTransform = new TransformNode("fing1Dist transform", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-1.5f, fingMiddLrgHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingMedHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing1MiddTransform = new TransformNode("fing1Midd transform", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-1.5f, fingProxLMHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingMedHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing1ProxTransform = new TransformNode("fing1Prox transform", m);

        // ------------ Finger #2 (Middle) ------------ \\

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-0.5f, fingDistHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingLrgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing2DistTransform = new TransformNode("fing2Dist transform", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-0.5f, fingMiddLrgHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingLrgHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing2MiddTransform = new TransformNode("fing2Midd transform", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-0.5f, fingProxLLHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingMedHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode fing2ProxTransform = new TransformNode("fing2Prox transform", m);

        // make scene graph
        robotHand.addChild(robotHandMoveTranslate);
            robotHandMoveTranslate.addChild(robotHandTranslate);
                robotHandTranslate.addChild(arm);
                    arm.addChild(armTransform);
                        armTransform.addChild(armShape);
                    arm.addChild(palm);
                        palm.addChild(palmTransform);
                            palmTransform.addChild(palmShape);
                        palm.addChild(fing1Dist);
                            fing1Dist.addChild(fing1DistTransform);
                                fing1DistTransform.addChild(fing1DistShape);
                            fing1Dist.addChild(fing1Midd);
                                fing1Midd.addChild(fing1MiddTransform);
                                    fing1MiddTransform.addChild(fing1MiddShape);
                                fing1Midd.addChild(fing1ProxTransform);
                                    fing1ProxTransform.addChild(fing1ProxShape);
                        palm.addChild(fing2Dist);
                            fing2Dist.addChild(fing2DistTransform);
                                fing2DistTransform.addChild(fing2DistShape);
                            fing2Dist.addChild(fing2Midd);
                                fing2Midd.addChild(fing2MiddTransform);
                                    fing2MiddTransform.addChild(fing2MiddShape);
                                fing2Midd.addChild(fing2ProxTransform);
                                    fing2ProxTransform.addChild(fing2ProxShape);
//                        palm.addChild(fing3Dist);
//                        palm.addChild(fing4Dist);




        robotHand.update();
    }
 
    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        updatePerspectiveMatrices();

        light.setPosition(getLightPosition());  // changing light position each frame
        light.render(gl);

        floor.render(gl);

        //    if (animation) updateLeftArm();
        robotHand.draw(gl);
    }
    
    private void updatePerspectiveMatrices() {
        // needs to be changed if user resizes the window
        perspective = Mat4Transform.perspective(45, aspect);
        light.setPerspective(perspective);
        floor.setPerspective(perspective);
        sphere.setPerspective(perspective);
        cube.setPerspective(perspective);
        cube2.setPerspective(perspective);
    }
  
    private void disposeMeshes(GL3 gl) {
        light.dispose(gl);
        floor.dispose(gl);
        sphere.dispose(gl);
        cube.dispose(gl);
        cube2.dispose(gl);
    }
  
//  private void updateLeftArm() {
//    double elapsedTime = getSeconds()-startTime;
//    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
//    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
//    leftArmRotate.update();
//    rotateAngle = -rotateAngle;
//    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
//    rightArmRotate.update();
//  }
  
    // The light's postion is continually being changed, so needs to be calculated for each frame.
    private Vec3 getLightPosition() {
        double elapsedTime = getSeconds()-startTime;
        float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
        float y = 2.7f;
        float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
        return new Vec3(x,y,z);
        //return new Vec3(5f,3.4f,5f);
    }
  
}