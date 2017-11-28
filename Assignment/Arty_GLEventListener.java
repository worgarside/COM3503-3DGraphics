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

    private static final int[][] DIGIT_PRM_ANGLE_NEUTRAL = {
        {60, 20, 10},
        {-2, 5, 5},
        {-2, 5, 5},
        {-2, 5, 5},
        {-2, 5, 5}
    };
    private static final int[] DIGIT_SEC_ANGLE_NEUTRAL = {10, -2, -1, 1, 2};

    private static final int[][] DIGIT_PRM_ANGLE_W = {
            {90, 85, 25},
            {2, 2, 2},
            {2, 2, 2},
            {2, 2, 2},
            {90, 90, 2}
    };
    private static final int[] DIGIT_SEC_ANGLE_W = {35, -10, 0, 10, 30};

    private static final int[][] DIGIT_PRM_ANGLE_I = {
            {90, 90, 30},
            {90, 90, 2},
            {90, 90, 2},
            {90, 90, 2},
            {2, 2, 2}
    };
    private static final int[] DIGIT_SEC_ANGLE_I = {60, 0, 0, 0, 10};

    private static final int[][] DIGIT_PRM_ANGLE_L = {
            {2, 2, 2},
            {2, 2, 2},
            {90, 90, 2},
            {90, 90, 2},
            {90, 90, 2}
    };
    private static final int[] DIGIT_SEC_ANGLE_L = {0, 0, 0, 0, 0};

    private static final int[][] DIGIT_PRM_ANGLE_POS = {
            {90, 85, 25},
            {-2, 5, 5},
            {-2, 5, 5},
            {90, 90, 2},
            {90, 90, 2}
    };
    private static final int[] DIGIT_SEC_ANGLE_POS = {40, -10, 10, 8, 7};

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

    public void changeHandPos(char letter){
        switch(letter) {
            case 'W':
                for (int d = 0; d < DIGIT_COUNT; d++) {
                    for (int p = 0; p < PHALANGE_COUNT; p++) {
                        desiredPrmAngles[d][p] = DIGIT_PRM_ANGLE_W[d][p];
                    }
                    desiredSecAngles[d] = DIGIT_SEC_ANGLE_W[d];
                }
                break;
            case 'I':
                for (int d = 0; d < DIGIT_COUNT; d++) {
                    for (int p = 0; p < PHALANGE_COUNT; p++) {
                        desiredPrmAngles[d][p] = DIGIT_PRM_ANGLE_I[d][p];
                    }
                    desiredSecAngles[d] = DIGIT_SEC_ANGLE_I[d];
                }
                break;
            case 'L':
                for (int d = 0; d < DIGIT_COUNT; d++) {
                    for (int p = 0; p < PHALANGE_COUNT; p++) {
                        desiredPrmAngles[d][p] = DIGIT_PRM_ANGLE_L[d][p];
                    }
                    desiredSecAngles[d] = DIGIT_SEC_ANGLE_L[d];
                }
                break;
            case 'N':
                for (int d = 0; d < DIGIT_COUNT; d++) {
                    for (int p = 0; p < PHALANGE_COUNT; p++) {
                        desiredPrmAngles[d][p] = DIGIT_PRM_ANGLE_NEUTRAL[d][p];
                    }
                    desiredSecAngles[d] = DIGIT_SEC_ANGLE_NEUTRAL[d];
                }
                break;
            case 'P':
                for (int d = 0; d < DIGIT_COUNT; d++) {
                    for (int p = 0; p < PHALANGE_COUNT; p++) {
                        desiredPrmAngles[d][p] = DIGIT_PRM_ANGLE_POS[d][p];
                    }
                    desiredSecAngles[d] = DIGIT_SEC_ANGLE_POS[d];
                }
                break;
            default:
                System.out.println("Invalid ASL Position");
                System.exit(0);
        }

    }

    public void updateAngles(){
        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                if (d!=0){
                    phalRotX[d][p].setTransform(Mat4Transform.rotateAroundX(currentPrmAngles[d][p]));
                    phalRotX[d][p].update();
                    phalRotZ[d][p].setTransform(Mat4Transform.rotateAroundZ(currentSecAngles[d]));
                    phalRotZ[d][p].update();
                }else{
                    phalRotZ[d][p].setTransform(Mat4Transform.rotateAroundZ(currentPrmAngles[d][p]));
                    phalRotZ[d][p].update();
                    phalRotX[d][p].setTransform(Mat4Transform.rotateAroundX(currentSecAngles[d]));
                    phalRotX[d][p].update();
                }
            }

        }
    }

    // ***************************************************
    /* THE SCENE
    * Now define all the methods to handle the scene.
    * This will be added to in later examples.
    */

    private Camera camera;
    private Mat4 perspective;
    private Mesh sphere, cube, cube2, cube3;
    private Mesh floor, wall1, wall2, wall3, wall4, ceiling;
    private Light light, ringLight;
    private SGNode robotHand;

    private int palmXAngle, palmZAngle;

    private int[][] maxPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Maximum angle phalange can be (most acute)
    private int[][] minPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Minimum angle phalange can be (most obtuse)
    private int[] maxSecAngle = new int[DIGIT_COUNT];                                       // Maximum angle prox can be (most acute)
    private int[] minSecAngle = new int[DIGIT_COUNT];                                       // Minimum angle prox can be (most obtuse)
    private int[][] angleX = new int[DIGIT_COUNT][PHALANGE_COUNT];                          // Current angle of phalange
    private TransformNode[][] phalRotX = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating phalanges about X-axis
    private TransformNode[][] phalRotZ = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating proximal phalanges about Z-axis
    private TransformNode armRotateY, palmRotateX, palmRotateZ;                             // TransformNodes for arm & palm
    private int[][] currentPrmAngles = new int[DIGIT_COUNT][PHALANGE_COUNT];                // current primary angles of digits
    private int[] currentSecAngles = new int[DIGIT_COUNT];                                  // current secondary angles of digits
    private int[][] desiredPrmAngles = new int[DIGIT_COUNT][PHALANGE_COUNT];                // target primary angles for animation
    private int[] desiredSecAngles = new int[DIGIT_COUNT];                                  // target secondary angles for animation

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
        sphere = new Sphere(gl, textureId1, textureId2);
        cube = new Cube(gl, textureId3, textureId4);
        cube2 = new Cube(gl, textureId5, textureId6);
        cube3 = new Cube(gl, textureId1, textureId1);


        floor = new TwoTriangles(gl, textureId0);
        floor.setModelMatrix(Mat4Transform.scale(16,1,16));
        wall1 = new TwoTriangles(gl, textureId2);
        wall1.setModelMatrix(getMforTT2());
        wall2 = new TwoTriangles(gl, textureId2);
        wall2.setModelMatrix(getMforTT3());
        wall3 = new TwoTriangles(gl, textureId2);
        wall3.setModelMatrix(getMforTT4());
        wall4 = new TwoTriangles(gl, textureId2);
        wall4.setModelMatrix(getMforTT5());
        ceiling = new TwoTriangles(gl, textureId2);
        ceiling.setModelMatrix(getMforTT6());

        light = new Light(gl);
        //ringLight = new Light(gl);
        light.setCamera(camera);

        sphere.setLight(light);
        sphere.setCamera(camera);
        cube.setLight(light);
        cube.setCamera(camera);
        cube2.setLight(light);//ringLight);
        cube2.setCamera(camera);
        cube3.setLight(light);
        cube3.setCamera(camera);

        floor.setLight(light);
        floor.setCamera(camera);
        wall1.setLight(light);
        wall1.setCamera(camera);
        wall2.setLight(light);
        wall2.setCamera(camera);
        wall3.setLight(light);
        wall3.setCamera(camera);
        wall4.setLight(light);
        wall4.setCamera(camera);
        ceiling.setLight(light);
        ceiling.setCamera(camera);

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode phalangeShape[][] = new MeshNode[DIGIT_COUNT][PHALANGE_COUNT];
        MeshNode armShape = new MeshNode("Cube(arm)", cube);
        MeshNode palmShape = new MeshNode("Cube(palm)", cube);
        MeshNode ringBandShape = new MeshNode("Cube(ringBand", cube2);
        MeshNode ringGemShape = new MeshNode("Cube(ringGem", cube3);

        NameNode digit[][] = new NameNode[DIGIT_COUNT][PHALANGE_COUNT];
        robotHand = new NameNode("root");
        NameNode arm = new NameNode("arm");
        NameNode palm = new NameNode("palm");
        NameNode ringBand = new NameNode("ringBand");
        NameNode ringGem = new NameNode("ringGem");

        TransformNode phalTLate[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];
        TransformNode phalTForm[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];

        // ------------ Dimensions + Positions ------------ \\

        float armWidth = 2f;
        float armHeight = 5f;
        float armDepth = 1.2f;
        float palmWidth = 4f;
        float palmHeight = 4f;
        float palmDepth = 1.25f;

        float phalXLgHeight = 1.8f;

        float phalLrgWidth = 0.8f;
        float phalLrgHeight = 1.5f;
        float phalLrgDepth = 0.8f;

        float phalMedWidth = 0.75f;
        float phalMedHeight = 1.3f;
        float phalMedDepth = 0.75f;

        float phalSmlWidth = 0.7f;
        float phalSmlHeight = 1.2f;
        float phalSmlDepth = 0.7f;

        float phalXSmWidth = 0.65f;
        float phalXSmHeight = 1.1f;
        float phalXSmDepth = 0.65f;

        float[] digitHrzPos = {palmWidth/2, 1.5f, 0.5f, -0.5f, -1.5f};

        float[][][] phalDims = {
                {{phalXLgHeight, phalLrgWidth, phalLrgDepth}, {phalLrgHeight, phalMedWidth, phalMedDepth}, {phalMedHeight, phalSmlWidth, phalSmlDepth}},
                {{phalLrgWidth, phalLrgHeight, phalLrgDepth}, {phalMedWidth, phalMedHeight, phalMedDepth}, {phalSmlWidth, phalSmlHeight, phalSmlDepth}},
                {{phalLrgWidth, phalXLgHeight, phalLrgDepth}, {phalMedWidth, phalLrgHeight, phalMedDepth}, {phalSmlWidth, phalSmlHeight, phalSmlDepth}},
                {{phalLrgWidth, phalLrgHeight, phalLrgDepth}, {phalMedWidth, phalMedHeight, phalMedDepth}, {phalSmlWidth, phalSmlHeight, phalSmlDepth}},
                {{phalSmlWidth, phalXSmHeight, phalSmlDepth}, {phalXSmWidth, phalXSmHeight, phalXSmDepth}, {phalXSmWidth, phalXSmHeight, phalXSmDepth}}
        };


        // ------------ Initialise all Arrays ------------ \\

        for (int d = 0; d < DIGIT_COUNT; d++) {
            maxSecAngle[d] = 20;
            minSecAngle[d] = -20;
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                desiredPrmAngles[d][p] = DIGIT_PRM_ANGLE_NEUTRAL[d][p];
                currentPrmAngles[d][p] = DIGIT_PRM_ANGLE_NEUTRAL[d][p];
                if (d != 0){
                    maxPrmAngle[d][p] = 90;
                }
                minPrmAngle[d][p] = -5;
                phalangeShape[d][p] = new MeshNode("Cube(digit" + Integer.toString(d) + "-phal" + Integer.toString(p) + ")", cube);
                digit[d][p] = new NameNode("[" + Integer.toString(d) + "][" + Integer.toString(p) + "]");
            }
            desiredSecAngles[d] = DIGIT_SEC_ANGLE_NEUTRAL[d];
            currentSecAngles[d] = DIGIT_SEC_ANGLE_NEUTRAL[d];
        }
        System.out.println("Variables initialised");

        // Thumb-Specific Angles
        maxPrmAngle[0][0] = 90;
        maxPrmAngle[0][1] = 60;
        maxPrmAngle[0][2] = 90;
        maxSecAngle[0] = 90;
        minSecAngle[0] = 0;


        // ------------ Initialising TranslationNodes ------------ \\ -- could go in loop

        phalTLate[0][0] = new TransformNode("phalTLate[0][0]", Mat4Transform.translate(digitHrzPos[0], 1f, 0.5f));
        phalTLate[0][1] = new TransformNode("phalTLate[0][1]", Mat4Transform.translate(phalXLgHeight, 0, 0));
        phalTLate[0][2] = new TransformNode("phalTLate[0][2]", Mat4Transform.translate(phalLrgHeight, 0, 0));

        phalTLate[1][0] = new TransformNode("phalTLate[1][0]", Mat4Transform.translate(digitHrzPos[1], palmHeight, 0));
        phalTLate[1][1] = new TransformNode("phalTLate[1][1]", Mat4Transform.translate(0, phalLrgHeight, 0));
        phalTLate[1][2] = new TransformNode("phalTLate[1][2]", Mat4Transform.translate(0, phalMedHeight, 0));

        phalTLate[2][0] = new TransformNode("phalTLate[2][0]", Mat4Transform.translate(digitHrzPos[2], palmHeight, 0));
        phalTLate[2][1] = new TransformNode("phalTLate[2][1]", Mat4Transform.translate(0, phalXLgHeight, 0));
        phalTLate[2][2] = new TransformNode("phalTLate[2][2]", Mat4Transform.translate(0, phalLrgHeight, 0));

        phalTLate[3][0] = new TransformNode("phalTLate[3][0]", Mat4Transform.translate(digitHrzPos[3], palmHeight, 0));
        phalTLate[3][1] = new TransformNode("phalTLate[3][1]", Mat4Transform.translate(0, phalLrgHeight, 0));
        phalTLate[3][2] = new TransformNode("phalTLate[3][2]", Mat4Transform.translate(0, phalMedHeight, 0));

        phalTLate[4][0] = new TransformNode("phalTLate[4][0]", Mat4Transform.translate(digitHrzPos[4], palmHeight, 0));
        phalTLate[4][1] = new TransformNode("phalTLate[4][1]", Mat4Transform.translate(0, phalXSmHeight, 0));
        phalTLate[4][2] = new TransformNode("phalTLate[4][2]", Mat4Transform.translate(0, phalXSmHeight, 0));


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


        // ------------ Digit Node Generation ------------ \\

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
                if (d==0) {
                    phalRotX[d][p] = new TransformNode("phalRotX[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundX(currentSecAngles[d]));
                    phalRotZ[d][p] = new TransformNode("phalRotZ[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundZ(currentPrmAngles[d][p]));
                } else {
                    phalRotX[d][p] = new TransformNode("phalRotX[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundX(currentPrmAngles[d][p]));
                    phalRotZ[d][p] = new TransformNode("phalRotZ[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundZ(currentSecAngles[d]));
                }
            }
        }

        // ------------ Ring Node Gen ------------ \\

        TransformNode ringBandTranslate = new TransformNode("ringBand translate", Mat4Transform.translate(0, 0.5f*phalLrgHeight, 0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(1.5f*phalLrgWidth, 0.4f*phalLrgHeight, 1.5f*phalLrgDepth));
        TransformNode ringBandTransform = new TransformNode("ringBand transform", m);

        TransformNode ringGemTranslate = new TransformNode("ringGem translate", Mat4Transform.translate(0, 0, -0.8f*phalLrgDepth));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(0.4f, 0.4f, 0.4f));
        TransformNode ringGemTransform = new TransformNode("ringGem transform", m);

        // ------------ Lamp Node Gen ------------ \\




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

                                    for (int d = 1; d < DIGIT_COUNT; d++) {
                                        palmRotateZ.addChild(phalTLate[d][0]);
                                            phalTLate[d][0].addChild(digit[d][0]);
                                                digit[d][0].addChild(phalRotZ[d][0]);
                                                    phalRotZ[d][0].addChild(phalRotX[d][0]);
                                                        phalRotX[d][0].addChild(phalTForm[d][0]);
                                                            phalTForm[d][0].addChild(phalangeShape[d][0]);

                                                        phalRotX[d][0].addChild(phalTLate[d][1]);
                                                            phalTLate[d][1].addChild(digit[d][1]);
                                                                digit[d][1].addChild(phalRotX[d][1]);
                                                                    phalRotX[d][1].addChild(phalTForm[d][1]);
                                                                        phalTForm[d][1].addChild(phalangeShape[d][1]);

                                                                    phalRotX[d][1].addChild(phalTLate[d][2]);
                                                                        phalTLate[d][2].addChild(digit[d][2]);
                                                                            digit[d][2].addChild(phalRotX[d][2]);
                                                                                phalRotX[d][2].addChild(phalTForm[d][2]);
                                                                                    phalTForm[d][2].addChild(phalangeShape[d][2]);
                                    }

                                                        phalRotX[3][0].addChild(ringBandTranslate);
                                                            ringBandTranslate.addChild(ringBand);
                                                                ringBand.addChild(ringBandTransform);
                                                                    ringBandTransform.addChild(ringBandShape);
                                                                ringBand.addChild(ringGemTranslate);
                                                                    ringGemTranslate.addChild(ringGem);
                                                                        ringGem.addChild(ringGemTransform);
                                                                            ringGemTransform.addChild(ringGemShape);
        robotHand.update();
    }

    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        for (int d = 0; d < DIGIT_COUNT; d++) {
            //Primary Angles
            for (int p = 0; p < PHALANGE_COUNT; p++){
                if (currentPrmAngles[d][p] - desiredPrmAngles[d][p] < 0) {
                    if (currentPrmAngles[d][p] < maxPrmAngle[d][p]){
                        currentPrmAngles[d][p]++;
                    }
                } else if (currentPrmAngles[d][p] - desiredPrmAngles[d][p] > 0) {
                    if (currentPrmAngles[d][p] > minPrmAngle[d][p]){
                        currentPrmAngles[d][p]--;
                    }
                }
            }

            //Secondary Angles
            if (currentSecAngles[d] - desiredSecAngles[d] < 0) {
                if (currentSecAngles[d] < maxSecAngle[d]) {
                    currentSecAngles[d]++;
                }
            } else if (currentSecAngles[d] - desiredSecAngles[d] > 0) {
                if (currentSecAngles[d] > minSecAngle[d]) {
                    currentSecAngles[d]--;
                }
            }
        }

        updatePerspectiveMatrices();
        light.setPosition(getLightPosition());
        light.render(gl);
//        //ringLight.setPosition(new Vec3(5f,3.4f,5f));
//        //ringLight.render(gl);
        updateAngles();
        robotHand.draw(gl);

        floor.render(gl);
        wall1.render(gl);
        wall2.render(gl);
        wall3.render(gl);
        wall4.render(gl);
        ceiling.render(gl);
    }

    private void updatePerspectiveMatrices() {
        // needs to be changed if user resizes the window
        perspective = Mat4Transform.perspective(45, aspect);
        light.setPerspective(perspective);
        //ringLight.setPerspective(perspective);
        sphere.setPerspective(perspective);
        cube.setPerspective(perspective);
        cube2.setPerspective(perspective);
        cube3.setPerspective(perspective);

        floor.setPerspective(perspective);
        wall1.setPerspective(perspective);
        wall2.setPerspective(perspective);
        wall3.setPerspective(perspective);
        wall4.setPerspective(perspective);
        ceiling.setPerspective(perspective);


    }
  
    private void disposeMeshes(GL3 gl) {
        light.dispose(gl);
        sphere.dispose(gl);
        cube.dispose(gl);
        cube2.dispose(gl);
        cube3.dispose(gl);

        floor.dispose(gl);
        wall1.dispose(gl);
        wall2.dispose(gl);
        wall3.dispose(gl);
        wall4.dispose(gl);
        ceiling.dispose(gl);
    }
  
    // The light's postion is continually being changed, so needs to be calculated for each frame.
    private Vec3 getLightPosition() {
        double elapsedTime = getSeconds()-startTime;
        float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
        float y = 2.7f;
        float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
//        return new Vec3(x,y,z);
        return new Vec3(5f,3.4f,5f);
    }

    private Mat4 getMforTT2() {
        float size = 16f;
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), model);
        return model;
    }

    private Mat4 getMforTT3() {
        float size = 16f;
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), model);
        model = Mat4.multiply(Mat4Transform.translate(-size*0.5f, size*0.5f, 0), model);
        return model;
    }

    private Mat4 getMforTT4() {
        float size = 16f;
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(size*0.5f,size*0.5f,0), model);
        return model;
    }

    private Mat4 getMforTT5() {
        float size = 16f;
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,size*0.5f), model);
        return model;
    }

    private Mat4 getMforTT6() {
        float size = 16f;
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(size,1f,size), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0,size,0), model);
        return model;
    }
  
}