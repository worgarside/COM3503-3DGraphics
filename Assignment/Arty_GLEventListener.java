import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Arty_GLEventListener implements GLEventListener {
  
    private static final boolean DISPLAY_SHADERS = false;
    private static final int DIGIT_COUNT = 5;
    private static final int PHALANGE_COUNT = 3;
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

    public void rotPalmXPos() {
        palmXAngle++;
        palmRotateX.setTransform(Mat4Transform.rotateAroundX(palmXAngle));
        palmRotateX.update();
    }

    public void rotPalmXNeg() {
        palmXAngle--;
        palmRotateX.setTransform(Mat4Transform.rotateAroundX(palmXAngle));
        palmRotateX.update();
    }

    public void rotPalmZPos() {
        palmZAngle++;
        palmRotateZ.setTransform(Mat4Transform.rotateAroundZ(palmZAngle));
        palmRotateZ.update();
    }

    public void rotPalmZNeg() {
        palmZAngle--;
        palmRotateZ.setTransform(Mat4Transform.rotateAroundZ(palmZAngle));
        palmRotateZ.update();
    }

    public void rotToAngle(int angle) {
        armRotateY.setTransform(Mat4Transform.rotateAroundY(angle));
        armRotateY.update();
    }

    public void curlFing1(){
//        fing1Anim = true;
    }

    public void curlFing2(){
//        fing2Anim = true;
    }

    public void curlFing3(){
//        fing3Anim = true;
    }

    public void curlFing4(){
//        fing4Anim = true;
    }

    public void curlThumb(){
//        fing0Anim = true;
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

    private int palmXAngle, palmZAngle;

    private int[][] maxAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                        // Maximum angle phalange can be (most acute)
    private int[][] minAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                        // Minimum angle phalange can be (most obtuse)
    private int[][] angleX = new int[DIGIT_COUNT][PHALANGE_COUNT];                          // Current angle of phalange
//    private boolean[][] digitStraight = new boolean[DIGIT_COUNT][PHALANGE_COUNT];           // Boolean to check if the digit is straight
    private boolean[] digitAnim = new boolean[DIGIT_COUNT];                                 // Boolean to check if digit is animating
    private TransformNode[][] phalRotX = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating phalanges about X-axis
    private TransformNode[][] phalRotZ = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating proximal phalanges about Z-axis
    private int fing0ProxAngleZ;                                                            // Z-angle of fing0 (thumb) proximal phalange
    private TransformNode armRotateY, palmRotateX, palmRotateZ;


//    private boolean fing1Straight = true;
//    private boolean fing1Anim = false;
//    private TransformNode fing1ProxRotateX, fing1ProxRotateZ, fing1MiddRotateX, fing1DistRotateX;
//    private int fing1ProxAngleX, fing1MiddAngleX, fing1DistAngleX; // default to 0

//    private boolean fing2Straight = true;
//    private boolean fing2Anim = false;
//    private TransformNode fing2ProxRotateX, fing2ProxRotateZ, fing2MiddRotateX, fing2DistRotateX;
//    private int fing2ProxAngleX, fing2MiddAngleX, fing2DistAngleX; // default to 0

//    private boolean fing3Straight = true;
//    private boolean fing3Anim = false;
//    private TransformNode fing3ProxRotateX, fing3ProxRotateZ, fing3MiddRotateX, fing3DistRotateX;
//    private int fing3ProxAngleX, fing3MiddAngleX, fing3DistAngleX; // default to 0

//    private boolean fing4Straight = true;
//    private boolean fing4Anim = false;
//    private TransformNode fing4ProxRotateX, fing4ProxRotateZ, fing4MiddRotateX, fing4DistRotateX;
//    private int fing4ProxAngleX, fing4MiddAngleX, fing4DistAngleX; // default to 0

//    private boolean fing0Straight = true;
//    private boolean fing0Anim = false;
//    private TransformNode fing0ProxRotateX, fing0ProxRotateZ, fing0MiddRotateZ, fing0DistRotateZ;
//    private int fing0ProxAngleX, fing0ProxAngleZ, fing0MiddAngleZ, fing0DistAngleZ; // default to 0


  
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

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode phalangeShape[][] = new MeshNode[DIGIT_COUNT][PHALANGE_COUNT];
        MeshNode armShape = new MeshNode("Cube(arm)", cube);
        MeshNode palmShape = new MeshNode("Cube(palm)", cube);

        NameNode digit[][] = new NameNode[DIGIT_COUNT][PHALANGE_COUNT];
        robotHand = new NameNode("root");
        NameNode arm = new NameNode("arm");
        NameNode palm = new NameNode("palm");

        TransformNode phalTLate[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];
        TransformNode phalTForm[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];

        // ------------ Dimensions + Positions ------------ \\

        float armWidth = 2f;
        float armHeight = 5f;
        float armDepth = 1.25f;
        float palmWidth = 4f;
        float palmHeight = 4f;
        float palmDepth = 1.25f;

        float fingXLgHeight = 1.8f;

        float fingLrgWidth = 0.8f;
        float fingLrgHeight = 1.5f;
        float fingLrgDepth = 0.8f;

        float fingMedWidth = 0.75f;
        float fingMedHeight = 1.3f;
        float fingMedDepth = 0.75f;

        float fingSmlWidth = 0.7f;
        float fingSmlHeight = 1.2f;
        float fingSmlDepth = 0.7f;

        float fingXSmWidth = 0.65f;
        float fingXSmHeight = 1.1f;
        float fingXSmDepth = 0.65f;

        float[] digitHrzPos = {palmWidth/2, 1.5f, 0.5f, -0.5f, -1.5f};

        float[][][] phalDims = {
                {{fingXLgHeight, fingLrgWidth, fingLrgDepth}, {fingLrgHeight, fingMedWidth, fingMedDepth}, {fingMedHeight, fingSmlWidth, fingSmlDepth}},
                {{fingLrgWidth, fingLrgHeight, fingLrgDepth}, {fingMedWidth, fingMedHeight, fingMedDepth}, {fingSmlWidth, fingSmlHeight, fingSmlDepth}},
                {{fingLrgWidth, fingXLgHeight, fingLrgDepth}, {fingMedWidth, fingLrgHeight, fingMedDepth}, {fingSmlWidth, fingSmlHeight, fingSmlDepth}},
                {{fingLrgWidth, fingLrgHeight, fingLrgDepth}, {fingMedWidth, fingMedHeight, fingMedDepth}, {fingSmlWidth, fingSmlHeight, fingSmlDepth}},
                {{fingSmlWidth, fingXSmHeight, fingSmlDepth}, {fingXSmWidth, fingXSmHeight, fingXSmDepth}, {fingXSmWidth, fingXSmHeight, fingXSmDepth}}
        };

        // ------------ Initialise all Arrays ------------ \\

        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                if (d != 0){
                    maxAngle[d][p] = 90;
                    minAngle[d][p] = 0;
                }

                phalangeShape[d][p] = new MeshNode("Cube(digit" + Integer.toString(d) + "-phal" + Integer.toString(p) + "])", cube);
                digit[d][p] = new NameNode("[" + Integer.toString(d) + "][" + Integer.toString(p) + "]");
            }
            digitAnim[d] = false;
        }
        System.out.println("Variables initialised");

        // Set thumb-specific angles etc.


        // ------------ Initialising TranslationNodes ------------ \\ -- could go in loop

        phalTLate[0][0] = new TransformNode("phalTLate[0][0]", Mat4Transform.translate(digitHrzPos[0], 1f, 0.5f));
        phalTLate[0][1] = new TransformNode("phalTLate[0][1]", Mat4Transform.translate(fingXLgHeight, 0, 0));
        phalTLate[0][2] = new TransformNode("phalTLate[0][2]", Mat4Transform.translate(fingLrgHeight, 0, 0));

        phalTLate[1][0] = new TransformNode("phalTLate[1][0]", Mat4Transform.translate(digitHrzPos[1], palmHeight, 0));
        phalTLate[1][1] = new TransformNode("phalTLate[1][1]", Mat4Transform.translate(0, fingLrgHeight, 0));
        phalTLate[1][2] = new TransformNode("phalTLate[1][2]", Mat4Transform.translate(0, fingMedHeight, 0));

        phalTLate[2][0] = new TransformNode("phalTLate[2][0]", Mat4Transform.translate(digitHrzPos[2], palmHeight, 0));
        phalTLate[2][1] = new TransformNode("phalTLate[2][1]", Mat4Transform.translate(0, fingXLgHeight, 0));
        phalTLate[2][2] = new TransformNode("phalTLate[2][2]", Mat4Transform.translate(0, fingLrgHeight, 0));

        phalTLate[3][0] = new TransformNode("phalTLate[3][0]", Mat4Transform.translate(digitHrzPos[3], palmHeight, 0));
        phalTLate[3][1] = new TransformNode("phalTLate[3][1]", Mat4Transform.translate(0, fingLrgHeight, 0));
        phalTLate[3][2] = new TransformNode("phalTLate[3][2]", Mat4Transform.translate(0, fingMedHeight, 0));

        phalTLate[4][0] = new TransformNode("phalTLate[4][0]", Mat4Transform.translate(digitHrzPos[4], palmHeight, 0));
        phalTLate[4][1] = new TransformNode("phalTLate[4][1]", Mat4Transform.translate(0, fingXSmHeight, 0));
        phalTLate[4][2] = new TransformNode("phalTLate[4][2]", Mat4Transform.translate(0, fingXSmHeight, 0));


        // ------------ Arm + Palm ------------ \\


        Mat4 m = Mat4Transform.scale(armWidth, armHeight, armDepth); // Sets dimensions of arm
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0)); // Move up by 0.5*height for origin
        TransformNode armTransform = new TransformNode("arm transform", m);
        armRotateY = new TransformNode("arm rotate",Mat4Transform.rotateAroundY(0));

        TransformNode palmTranslate = new TransformNode("palm translate", Mat4Transform.translate(0,armHeight,0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(palmWidth, palmHeight, palmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode palmTransform = new TransformNode("palm transform", m);
        palmRotateX = new TransformNode("palmX rotate",Mat4Transform.rotateAroundX(0));
        palmRotateZ = new TransformNode("palmZ rotate",Mat4Transform.rotateAroundZ(0));


        // ------------ Thumb ------------ \\

        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                m = new Mat4(1);
                m = Mat4.multiply(m, Mat4Transform.scale(phalDims[d][p][0], phalDims[d][p][1], phalDims[d][p][2]));
                if (d==0) {
                    m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
                }else{
                    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                }
                phalTForm[d][p] = new TransformNode("phalTForm[" + d + "][" + Integer.toString(p) + "]", m);
                phalRotX[d][p] = new TransformNode("phalRotX[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundX(1));
                phalRotZ[d][p] = new TransformNode("phalRotZ[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundZ(0));
            }
        }
/*
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingXLgHeight, fingLrgWidth, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
        phalTForm[0][0] = new TransformNode("phalTForm[0][0]", m);
        phalRotX[0][0] = new TransformNode("phalRotX[0][0]", Mat4Transform.rotateAroundX(1));
        phalRotZ[0][0] = new TransformNode("phalRotZ[0]", Mat4Transform.rotateAroundZ(0));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgHeight, fingMedWidth, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
        phalTForm[0][1] = new TransformNode("phalTForm[0][1]", m);
        phalRotZ[0][1] = new TransformNode("phalRotX[0][1]", Mat4Transform.rotateAroundX(1));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedHeight, fingSmlWidth, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
        phalTForm[0][2] = new TransformNode("phalTForm[0][2]", m);
        phalRotZ[0][2] = new TransformNode("phalRotX[0][2]", Mat4Transform.rotateAroundX(1));

        // ------------ Finger #1 (Index) ------------ \\

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingLrgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[1][0] = new TransformNode("phalTForm[1][0]", m);
        phalRotX[1][0] = new TransformNode("phalRotX[1][0]", Mat4Transform.rotateAroundX(1));
        phalRotZ[1][0] = new TransformNode("phalRotZ[1]", Mat4Transform.rotateAroundZ(0));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingMedHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        phalTForm[1][1] = new TransformNode("phalTForm[1][1]", m);
        phalRotX[1][1] = new TransformNode("phalRotX[1][1]", Mat4Transform.rotateAroundX(1));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingSmlHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[1][2] = new TransformNode("phalTForm[1][2]", m);
        phalRotX[1][2] = new TransformNode("phalRotX[1][2]", Mat4Transform.rotateAroundX(1));

        // ------------ Finger #2 (Middle) ------------ \\

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingXLgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[2][0] = new TransformNode("phalTForm[2][0]", m);
        phalRotX[2][0] = new TransformNode("phalRotX[2][0]", Mat4Transform.rotateAroundX(1));
        phalRotZ[2][0] = new TransformNode("phalRotZ[2]", Mat4Transform.rotateAroundZ(0));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingLrgHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        phalTForm[2][1] = new TransformNode("phalTForm[2][1]", m);
        phalRotX[2][1] = new TransformNode("phalRotX[2][1]", Mat4Transform.rotateAroundX(1));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingSmlHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[2][2] = new TransformNode("phalTForm[2][2]", m);
        phalRotX[2][2] = new TransformNode("phalRotX[2][2]", Mat4Transform.rotateAroundX(1));

        // ------------ Finger #3 (Ring) ------------ \\

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingLrgWidth, fingLrgHeight, fingLrgDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[3][0] = new TransformNode("phalTForm[3][0]", m);
        phalRotX[3][0] = new TransformNode("phalRotX[3][0]", Mat4Transform.rotateAroundX(1));
        phalRotZ[3][0] = new TransformNode("phalRotZ[3]", Mat4Transform.rotateAroundZ(0));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingMedWidth, fingMedHeight, fingMedDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        phalTForm[3][1] = new TransformNode("phalTForm[3][1]", m);
        phalRotX[3][1] = new TransformNode("phalRotX[3][1]", Mat4Transform.rotateAroundX(1));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingSmlHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[3][2] = new TransformNode("phalTForm[3][2]", m);
        phalRotX[3][2] = new TransformNode("phalRotX[3][2]", Mat4Transform.rotateAroundX(1));


        // ------------ Finger #4 (Little) ------------ \\

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingSmlWidth, fingXSmHeight, fingSmlDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[4][0] = new TransformNode("phalTForm[4][0]", m);
        phalRotX[4][0] = new TransformNode("phalRotX[4][0]", Mat4Transform.rotateAroundX(1));
        phalRotZ[4][0] = new TransformNode("phalRotZ[4]", Mat4Transform.rotateAroundZ(0));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingXSmWidth, fingXSmHeight, fingXSmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        phalTForm[4][1] = new TransformNode("phalTForm[4][1]", m);
        phalRotX[4][1] = new TransformNode("phalRotX[4][1]", Mat4Transform.rotateAroundX(1));

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(fingXSmWidth, fingXSmHeight, fingXSmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        phalTForm[4][2] = new TransformNode("phalTForm[4][2]", m);
        phalRotX[4][2] = new TransformNode("phalRotX[4][2]", Mat4Transform.rotateAroundX(1));
*/
        // ------------ Scene Graph ------------ \\

        robotHand.addChild(arm);
            arm.addChild(armRotateY);
                armRotateY.addChild(armTransform);
                    armTransform.addChild(armShape);
                    armRotateY.addChild(palm);

                        palm.addChild(palmTranslate);
                            palmTranslate.addChild(palmRotateX);
                                palmRotateX.addChild(palmRotateZ);
                                    palmRotateZ.addChild(palmTransform);
                                        palmTransform.addChild(palmShape);


                                    palmRotateZ.addChild(phalTLate[0][0]);
                                        phalTLate[0][0].addChild(digit[0][0]);
                                            digit[0][0].addChild(phalRotX[0][0]);
                                                phalRotX[0][0].addChild(phalRotZ[0][0]);
                                                    phalRotZ[0][0].addChild(phalTForm[0][0]);
                                                        phalTForm[0][0].addChild(phalangeShape[0][0]);

                                                    phalRotZ[0][0].addChild(phalTLate[0][1]);
                                                        phalTLate[0][1].addChild(digit[0][1]);
                                                            digit[0][1].addChild(phalRotZ[0][1]);
                                                                phalRotZ[0][1].addChild(phalTForm[0][1]);
                                                                    phalTForm[0][1].addChild(phalangeShape[0][1]);

                                                                phalRotZ[0][1].addChild(phalTLate[0][2]);
                                                                    phalTLate[0][2].addChild(digit[0][2]);
                                                                        digit[0][2].addChild(phalRotZ[0][2]);
                                                                            phalRotZ[0][2].addChild(phalTForm[0][2]);
                                                                                phalTForm[0][2].addChild(phalangeShape[0][2]);

                                    palmRotateZ.addChild(phalTLate[1][0]);
                                        phalTLate[1][0].addChild(digit[1][0]);
                                            digit[1][0].addChild(phalRotX[1][0]);
                                                phalRotX[1][0].addChild(phalTForm[1][0]);
                                                    phalTForm[1][0].addChild(phalangeShape[1][0]);

                                                phalRotX[1][0].addChild(phalTLate[1][1]);
                                                    phalTLate[1][1].addChild(digit[1][1]);
                                                        digit[1][1].addChild(phalRotX[1][1]);
                                                            phalRotX[1][1].addChild(phalTForm[1][1]);
                                                                phalTForm[1][1].addChild(phalangeShape[1][1]);

                                                            phalRotX[1][1].addChild(phalTLate[1][2]);
                                                                phalTLate[1][2].addChild(digit[1][2]);
                                                                    digit[1][2].addChild(phalRotX[1][2]);
                                                                        phalRotX[1][2].addChild(phalTForm[1][2]);
                                                                            phalTForm[1][2].addChild(phalangeShape[1][2]);

                                    palmRotateZ.addChild(phalTLate[2][0]);
                                        phalTLate[2][0].addChild(digit[2][0]);
                                            digit[2][0].addChild(phalRotX[2][0]);
                                                phalRotX[2][0].addChild(phalTForm[2][0]);
                                                    phalTForm[2][0].addChild(phalangeShape[2][0]);
                                            
                                                phalRotX[2][0].addChild(phalTLate[2][1]);
                                                    phalTLate[2][1].addChild(digit[2][1]);
                                                        digit[2][1].addChild(phalRotX[2][1]);
                                                            phalRotX[2][1].addChild(phalTForm[2][1]);
                                                                phalTForm[2][1].addChild(phalangeShape[2][1]);
                                                        
                                                            phalRotX[2][1].addChild(phalTLate[2][2]);
                                                                phalTLate[2][2].addChild(digit[2][2]);
                                                                    digit[2][2].addChild(phalRotX[2][2]);
                                                                        phalRotX[2][2].addChild(phalTForm[2][2]);
                                                                            phalTForm[2][2].addChild(phalangeShape[2][2]);

                                    palmRotateZ.addChild(phalTLate[3][0]);
                                        phalTLate[3][0].addChild(digit[3][0]);
                                            digit[3][0].addChild(phalRotX[3][0]);
                                                phalRotX[3][0].addChild(phalTForm[3][0]);
                                                    phalTForm[3][0].addChild(phalangeShape[3][0]);
                                            
                                                phalRotX[3][0].addChild(phalTLate[3][1]);
                                                    phalTLate[3][1].addChild(digit[3][1]);
                                                        digit[3][1].addChild(phalRotX[3][1]);
                                                            phalRotX[3][1].addChild(phalTForm[3][1]);
                                                                phalTForm[3][1].addChild(phalangeShape[3][1]);
                                                        
                                                            phalRotX[3][1].addChild(phalTLate[3][2]);
                                                                phalTLate[3][2].addChild(digit[3][2]);
                                                                    digit[3][2].addChild(phalRotX[3][2]);
                                                                        phalRotX[3][2].addChild(phalTForm[3][2]);
                                                                            phalTForm[3][2].addChild(phalangeShape[3][2]);

                                    palmRotateZ.addChild(phalTLate[4][0]);
                                        phalTLate[4][0].addChild(digit[4][0]);
                                            digit[4][0].addChild(phalRotX[4][0]);
                                                phalRotX[4][0].addChild(phalTForm[4][0]);
                                                    phalTForm[4][0].addChild(phalangeShape[4][0]);
                                            
                                                phalRotX[4][0].addChild(phalTLate[4][1]);
                                                    phalTLate[4][1].addChild(digit[4][1]);
                                                        digit[4][1].addChild(phalRotX[4][1]);
                                                            phalRotX[4][1].addChild(phalTForm[4][1]);
                                                                phalTForm[4][1].addChild(phalangeShape[4][1]);
                                                        
                                                            phalRotX[4][1].addChild(phalTLate[4][2]);
                                                                phalTLate[4][2].addChild(digit[4][2]);
                                                                    digit[4][2].addChild(phalRotX[4][2]);
                                                                        phalRotX[4][2].addChild(phalTForm[4][2]);
                                                                            phalTForm[4][2].addChild(phalangeShape[4][2]);

        robotHand.update();


    }
    private int count = 0;
    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        updatePerspectiveMatrices();

        light.setPosition(getLightPosition());  // changing light position each frame
        light.render(gl);
        floor.render(gl);

        /*
        if (fing0Anim) {
            if (fing0Straight) {
                if (fing0ProxAngleX < 50) {
                    fing0ProxAngleX++;
                    fing0ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing0ProxAngleX));
                    fing0ProxRotateX.update();
                }

                if (fing0ProxAngleZ < 60) {
                    fing0ProxAngleZ++;
                    fing0ProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(fing0ProxAngleZ));
                    fing0ProxRotateZ.update();
                }

                if (fing0MiddAngleZ < 60) {
                    fing0MiddAngleZ++;
                    fing0MiddRotateZ.setTransform(Mat4Transform.rotateAroundZ(fing0MiddAngleZ));
                    fing0MiddRotateZ.update();
                }

                fing0DistAngleZ++;
                fing0DistRotateZ.setTransform(Mat4Transform.rotateAroundZ(fing0DistAngleZ));
                fing0DistRotateZ.update();

                if (fing0DistAngleZ > 90) {
                    fing0Anim = false;
                    fing0Straight = false;
                }

            }else{
                if (fing0ProxAngleX > 0) {
                    fing0ProxAngleX--;
                    fing0ProxRotateX.setTransform(Mat4Transform.rotateAroundX(fing0ProxAngleX));
                    fing0ProxRotateX.update();
                }

                if (fing0ProxAngleZ > 0) {
                    fing0ProxAngleZ--;
                    fing0ProxRotateZ.setTransform(Mat4Transform.rotateAroundZ(fing0ProxAngleZ));
                    fing0ProxRotateZ.update();
                }

                if (fing0MiddAngleZ > 0) {
                    fing0MiddAngleZ--;
                    fing0MiddRotateZ.setTransform(Mat4Transform.rotateAroundZ(fing0MiddAngleZ));
                    fing0MiddRotateZ.update();
                }

                fing0DistAngleZ--;
                fing0DistRotateZ.setTransform(Mat4Transform.rotateAroundZ(fing0DistAngleZ));
                fing0DistRotateZ.update();

                if (fing0DistAngleZ == 0) {
                    fing0Anim = false;
                    fing0Straight = true;
                }
            }
        }
        */

        // rotate fing1Prox about Z
        // stop when angle reached


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