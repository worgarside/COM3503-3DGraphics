import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.util.Arrays;

public class RobotHand {

    // ------------ Constants and Variables ------------ \\
    static final int DIGIT_COUNT = 5;
    static final int PHALANGE_COUNT = 3;
    private boolean keyframeAnimation = false;
    private boolean midAnimation = false;
    private boolean animationOn = true;
    private int currentKeyframe = 0;

    private Mesh cubeRobot, sphereRing, sphereRingGem;
    private SGNode robotHand;

    private int[][] maxPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Maximum angle phalange can be (most acute)
    private int[][] minPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Minimum angle phalange can be (most obtuse)
    private int[] maxSecAngle = new int[DIGIT_COUNT];                                       // Maximum angle prox can be (most acute)
    private int[] minSecAngle = new int[DIGIT_COUNT];                                       // Minimum angle prox can be (most obtuse)
    private int[][] angleX = new int[DIGIT_COUNT][PHALANGE_COUNT];                          // Current angle of phalange
    private TransformNode[][] phalRotX = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating phalanges about X-axis
    private TransformNode[][] phalRotZ = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating proximal phalanges about Z-axis
    private TransformNode armRotateY, palmTranslate, ringGemTranslate, ringTranslate;       // TransformNodes for one-off objects
    private int[][] currentPrmAngles = new int[DIGIT_COUNT][PHALANGE_COUNT];                // current primary angles of digits
    private int[] currentSecAngles = new int[DIGIT_COUNT];                                  // current secondary angles of digits
    private int[][] desiredPrmAngles = new int[DIGIT_COUNT][PHALANGE_COUNT];                // target primary angles for animation
    private int[] desiredSecAngles = new int[DIGIT_COUNT];                                  // target secondary angles for animation
    private TransformNode phalTLate[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];   // TranslationNodes for digits
    private TransformNode phalTForm[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];   // TransformNodes for digits

    private Mat4 ringGemMatrix = new Mat4(1);
    private Mat4 m = new Mat4(1);
    private Mat4 armMatrix = new Mat4(1);
    private Mat4 palmMatrix = new Mat4(1);
    private Mat4 digit3ProxMatrix = new Mat4(1);

    // ------------ Constructor and Initialiser ------------ \\

    public RobotHand(Mesh cubeRobot, Mesh sphereRing, Mesh sphereRingGem) {
        this.cubeRobot = cubeRobot;
        this.sphereRing = sphereRing;
        this.sphereRingGem = sphereRingGem;
    }

    /*
        Contains all MeshNodes, NameNodes, TransformNodes to create a RobotHand object.
        Also contains the Scene Graph and calculations for initial positions and rotations.
    */
    public void initialise(GL3 gl) {

        // ------------ Dimensions & Positions ------------ \\

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
                {{phalXLgHeight, phalLrgWidth, phalLrgDepth}, {phalLrgHeight, phalMedWidth, phalMedDepth}, {phalMedHeight, phalSmlWidth, phalSmlDepth}},    // Thumb, P-M-D
                {{phalLrgWidth, phalLrgHeight, phalLrgDepth}, {phalMedWidth, phalMedHeight, phalMedDepth}, {phalSmlWidth, phalSmlHeight, phalSmlDepth}},    // Finger 1, P-M-D
                {{phalLrgWidth, phalXLgHeight, phalLrgDepth}, {phalMedWidth, phalLrgHeight, phalMedDepth}, {phalSmlWidth, phalSmlHeight, phalSmlDepth}},    // Finger 2, P-M-D
                {{phalLrgWidth, phalLrgHeight, phalLrgDepth}, {phalMedWidth, phalMedHeight, phalMedDepth}, {phalSmlWidth, phalSmlHeight, phalSmlDepth}},    // Finger 3, P-M-D
                {{phalSmlWidth, phalXSmHeight, phalSmlDepth}, {phalXSmWidth, phalXSmHeight, phalXSmDepth}, {phalXSmWidth, phalXSmHeight, phalXSmDepth}}     // Finger 4, P-M-D
        };

        float[][][] phalTranslations = {
                {{digitHrzPos[0], 1f, 0.5f}, {phalXLgHeight, 0, 0}, {phalLrgHeight, 0, 0}},         // Thumb, P-M-D
                {{digitHrzPos[1], palmHeight, 0}, {0, phalLrgHeight, 0}, {0, phalMedHeight, 0}},    // Finger 1, P-M-D
                {{digitHrzPos[2], palmHeight, 0}, {0, phalXLgHeight, 0}, {0, phalLrgHeight, 0}},    // Finger 2, P-M-D
                {{digitHrzPos[3], palmHeight, 0}, {0, phalLrgHeight, 0}, {0, phalMedHeight, 0}},    // Finger 3, P-M-D
                {{digitHrzPos[4], palmHeight, 0}, {0, phalXSmHeight, 0}, {0, phalXSmHeight, 0}}     // Finger 4, P-M-D
        };

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode phalangeShape[][] = new MeshNode[DIGIT_COUNT][PHALANGE_COUNT];
        MeshNode armShape = new MeshNode("Cube(arm)", cubeRobot);
        MeshNode palmShape = new MeshNode("Cube(palm)", cubeRobot);
        MeshNode ringShape = new MeshNode("Cube(ring)", sphereRing);
        MeshNode ringGemShape = new MeshNode("Cube(ringGem)", sphereRingGem);

        robotHand = new NameNode("root");
        NameNode digit[][] = new NameNode[DIGIT_COUNT][PHALANGE_COUNT];
        NameNode arm = new NameNode("arm");
        NameNode palm = new NameNode("palm");
        NameNode ring = new NameNode("ring");
        NameNode ringGem = new NameNode("ringGem");

        // ------------ Initialise all Arrays & Generate Digit Node ------------ \\

        for (int d = 0; d < DIGIT_COUNT; d++) {
            // Set boundaries for digit rotations
            maxSecAngle[d] = 20;
            minSecAngle[d] = -20;

            for (int p = 0; p < PHALANGE_COUNT; p++) {
                // Initialise neutral position if it has been defined
                if (Arty.neutralKeyframe != null) {
                    desiredPrmAngles[d][p] = Arty.neutralKeyframe.getPrmAngles()[d][p];
                    currentPrmAngles[d][p] = Arty.neutralKeyframe.getPrmAngles()[d][p];
                }

                // Boundaries for phalanges
                maxPrmAngle[d][p] = 90;
                minPrmAngle[d][p] = -5;

                phalangeShape[d][p] = new MeshNode("Cube(digit" + d + "-phal" + p + ")", cubeRobot);
                digit[d][p] = new NameNode("digit[" + d + "][" + p + "]");
                phalTLate[d][p] = new TransformNode("phalTLate[" + d + "][" + p + "]",
                        Mat4Transform.translate(phalTranslations[d][p][0], phalTranslations[d][p][1], phalTranslations[d][p][2]));

                m = new Mat4(1);
                m = Mat4.multiply(m, Mat4Transform.scale(phalDims[d][p][0], phalDims[d][p][1], phalDims[d][p][2]));
                if (d==0) {
                    m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
                }else{
                    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                }

                // Save the matrix used for the phalange to get ring position
                if ((d==3) && (p==0)){
                    digit3ProxMatrix = m;
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

            // Initialise neutral position if it has been defined
            if (Arty.neutralKeyframe != null) {
                desiredSecAngles[d] = Arty.neutralKeyframe.getSecAngles()[d];
                currentSecAngles[d] = Arty.neutralKeyframe.getSecAngles()[d];
            }
        }

        // Thumb-Specific Angles
        maxPrmAngle[0][1] = 60;
        maxPrmAngle[0][2] = 90;
        maxSecAngle[0] = 90;
        minSecAngle[0] = 0;

        // ------------ Arm & Palm ------------ \\

        armMatrix = Mat4Transform.scale(armWidth, armHeight, armDepth); // Sets dimensions of arm
        armMatrix = Mat4.multiply(armMatrix, Mat4Transform.translate(0,0.5f,0)); // Move up by 0.5*height for origin
        TransformNode armTransform = new TransformNode("arm transform", armMatrix);
        armRotateY = new TransformNode("arm rotate",Mat4Transform.rotateAroundY(0));

        palmTranslate = new TransformNode("palm translate", Mat4Transform.translate(0, armHeight, 0));
        palmMatrix = Mat4.multiply(palmMatrix, Mat4Transform.scale(palmWidth, palmHeight, palmDepth));
        palmMatrix = Mat4.multiply(palmMatrix, Mat4Transform.translate(0,0.5f,0));
        TransformNode palmTransform = new TransformNode("palm transform", palmMatrix);

        // ------------ Ring Node Gen ------------ \\

        ringTranslate = new TransformNode("ring translate", Mat4Transform.translate(0, 0.5f*phalLrgHeight, 0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(1.8f*phalLrgWidth, 0.4f*phalLrgHeight, 1.8f*phalLrgDepth));
        TransformNode ringTransform = new TransformNode("ring transform", m);
        ringGemTranslate = new TransformNode("ringGem translate", Mat4Transform.translate(0, 0, -0.8f*phalLrgDepth));
        ringGemMatrix = new Mat4(1);
        ringGemMatrix = Mat4.multiply(ringGemMatrix, Mat4Transform.scale(0.4f, 0.4f, 0.4f));
        TransformNode ringGemTransform = new TransformNode("ringGem transform", ringGemMatrix);

        // ------------ Scene Graph ------------ \\

        robotHand.addChild(arm);
            arm.addChild(armRotateY);
                armRotateY.addChild(armTransform);
                    armTransform.addChild(armShape);
                armRotateY.addChild(palm);

                    palm.addChild(palmTranslate);
                        palmTranslate.addChild(palmTransform);
                            palmTransform.addChild(palmShape);

                        palmTranslate.addChild(phalTLate[0][0]);
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
                            palmTranslate.addChild(phalTLate[d][0]);
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

                                            phalRotX[3][0].addChild(ringTranslate);
                                                ringTranslate.addChild(ring);
                                                    ring.addChild(ringTransform);
                                                        ringTransform.addChild(ringShape);
                                                    ring.addChild(ringGemTranslate);
                                                        ringGemTranslate.addChild(ringGem);
                                                            ringGem.addChild(ringGemTransform);
                                                                ringGemTransform.addChild(ringGemShape);
        robotHand.update();
    }

    // ------------ User Controlled Functions ------------ \\

    public void rotRHToAngle(int angle) {
        armRotateY.setTransform(Mat4Transform.rotateAroundY(angle));
    }

    public void moveToKeyframe(int keyframe){
        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                desiredPrmAngles[d][p] = Arty.keyframes.get(keyframe).getPrmAngles()[d][p];
            }
            desiredSecAngles[d] = Arty.keyframes.get(keyframe).getSecAngles()[d];
        }
        midAnimation = true;
        currentKeyframe = keyframe;
    }

    public void toggleGlobalAnims() {
        animationOn = !animationOn;
    }

    public void toggleKeyframeSequence() {
        keyframeAnimation = !keyframeAnimation;
    }

    // ------------ Getters ------------ \\

    public Vec3 getRingPos() {
        Mat4 ringGemMatrixTotal = new Mat4(1);
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(armRotateY));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(palmTranslate));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(phalTLate[3][0]));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(phalRotZ[3][0]));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(phalRotX[3][0]));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(ringTranslate));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(ringGemTranslate));

        return coordsFromMat4(ringGemMatrixTotal);
    }

    public Vec3 getRingDir() {
        Mat4 ringGemMatrixTotal = new Mat4(1);
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(armRotateY));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(phalRotZ[3][0]));
        ringGemMatrixTotal = Mat4.multiply(ringGemMatrixTotal, transformNodeToMat4(phalRotX[3][0]));

        return rotationFromMat4(ringGemMatrixTotal);
    }

    // ------------ Model Manipulation/Drawing ------------ \\

    private void updateCurrentAngles(){
        if (midAnimation) {
            for (int d = 0; d < DIGIT_COUNT; d++) {
                //Primary Angles
                for (int p = 0; p < PHALANGE_COUNT; p++){
                    if (currentPrmAngles[d][p] - desiredPrmAngles[d][p] < 0) {
                        if (currentPrmAngles[d][p] < maxPrmAngle[d][p]){
                            currentPrmAngles[d][p]++;
                        }else{
                            desiredPrmAngles[d][p] = currentPrmAngles[d][p];
                        }
                    } else if (currentPrmAngles[d][p] - desiredPrmAngles[d][p] > 0) {
                        if (currentPrmAngles[d][p] > minPrmAngle[d][p]){
                            currentPrmAngles[d][p]--;
                        }else{
                            desiredPrmAngles[d][p] = currentPrmAngles[d][p];
                        }
                    }
                }

                //Secondary Angles
                if (currentSecAngles[d] - desiredSecAngles[d] < 0) {
                    if (currentSecAngles[d] < maxSecAngle[d]) {
                        currentSecAngles[d]++;
                    }else{
                        desiredSecAngles[d] = currentSecAngles[d];
                    }
                } else if (currentSecAngles[d] - desiredSecAngles[d] > 0) {
                    if (currentSecAngles[d] > minSecAngle[d]) {
                        currentSecAngles[d]--;
                    }else{
                        desiredSecAngles[d] = currentSecAngles[d];
                    }
                }
            }
            midAnimation = !((Arrays.deepEquals(currentPrmAngles, desiredPrmAngles)) && (Arrays.equals(currentSecAngles, desiredSecAngles)));
        }
    }

    private void updateFingerPositions(){
        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                if (d!=0){
                    phalRotX[d][p].setTransform(Mat4Transform.rotateAroundX(currentPrmAngles[d][p]));
                    phalRotZ[d][p].setTransform(Mat4Transform.rotateAroundZ(currentSecAngles[d]));
                }else{
                    phalRotZ[d][p].setTransform(Mat4Transform.rotateAroundZ(currentPrmAngles[d][p]));
                    phalRotX[d][p].setTransform(Mat4Transform.rotateAroundX(currentSecAngles[d]));
                }
            }
        }
    }

    public void render(GL3 gl) {
        if (animationOn){
            if ((keyframeAnimation) && (!midAnimation)) {
                currentKeyframe++;
                if (currentKeyframe > Arty.keyframes.size()-1){
                    currentKeyframe = 0;
                }
                moveToKeyframe(currentKeyframe);
            }

            updateCurrentAngles();
            updateFingerPositions();
        }

        robotHand.update();
        robotHand.draw(gl);
    }

    // ------------ Matrix Maths Functions ------------ \\

    private Mat4 transformNodeToMat4(TransformNode tNode) {
        Mat4 matrix = tNode.getMat4();
        return matrix;
    }

    private Vec3 coordsFromMat4(Mat4 matrix) {
        float[][] values = matrix.getValues();
        float x = values[0][3];
        float y = values[1][3];
        float z = values[2][3];
        return new Vec3(x, y, z);
    }

    private Vec3 rotationFromMat4(Mat4 matrix) {
        float[][] values = matrix.getValues();
        double x, y, z;

        y = Math.asin( values[0][2] );

        if( y < Math.PI/2 ) {
            if( y > -Math.PI/2 ) {
                x = Math.atan( -values[1][2]/values[2][2] );
                z = Math.atan( -values[0][1]/values[0][0] );
            } else {
                x = -Math.atan( values[1][2]/values[2][2] );
                z = 0;
            }
        } else {
            x = Math.atan( values[1][2]/values[2][2] );
            z = 0;
        }
        return new Vec3((float) x, (float) y, (float) z);
    }
}