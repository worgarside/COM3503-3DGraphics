import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.util.Arrays;

/**
 * RobotHand.java
 * Purpose: Creates and Animates a Robot Hand
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
public class RobotHand {

    // ------------ Constants and Variables ------------ \\

    static final int DIGIT_COUNT = 5;
    static final int PHALANGE_COUNT = 3;
    private boolean keyframeAnimation = false;
    private boolean midAnimation = false;
    private boolean animationOn = true;
    private int currentKeyframe = 0; // flag for which imported keyframe is displayed

    private Mesh cubeRobot, sphereRing, sphereRingGem;
    private SGNode robotHand;

    private int[][] maxPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Maximum angle phalange can be (most acute)
    private int[][] minPrmAngle = new int[DIGIT_COUNT][PHALANGE_COUNT];                     // Minimum angle phalange can be (most obtuse)
    private int[] maxSecAngle = new int[DIGIT_COUNT];                                       // Maximum angle prox can be (most acute)
    private int[] minSecAngle = new int[DIGIT_COUNT];                                       // Minimum angle prox can be (most obtuse)
    private int[][] angleX = new int[DIGIT_COUNT][PHALANGE_COUNT];                          // Current angle of phalange
    private TransformNode[][] rotateXPhal = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating phalanges about X-axis
    private TransformNode[][] rotateZPhal = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];    // TransformNodes for rotating proximal phalanges about Z-axis
    private TransformNode rotateYArm, translatePalm;                                        // TransformNodes for one-off objects
    private TransformNode translateRingGem, translateRing, translateSpotlight;              // TransformNodes for one-off objects
    private int[][] currentPrmAngles = new int[DIGIT_COUNT][PHALANGE_COUNT];                // current primary angles of digits
    private int[] currentSecAngles = new int[DIGIT_COUNT];                                  // current secondary angles of digits
    private int[][] desiredPrmAngles = new int[DIGIT_COUNT][PHALANGE_COUNT];                // target primary angles for animation
    private int[] desiredSecAngles = new int[DIGIT_COUNT];                                  // target secondary angles for animation
    private TransformNode translatePhal[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];   // TranslationNodes for digits
    private TransformNode scalePhal[][] = new TransformNode[DIGIT_COUNT][PHALANGE_COUNT];   // TransformNodes for digits

    private Mat4 m = new Mat4(1);
    private TransformNode scaleRingGem;
    private NameNode spotlightBeacon;

    // ------------ Constructor and Initialiser ------------ \\

    public RobotHand(Mesh cubeRobot, Mesh sphereRing, Mesh sphereRingGem) {
        this.cubeRobot = cubeRobot;
        this.sphereRing = sphereRing;
        this.sphereRingGem = sphereRingGem;
    }

    /**
     * Contains all MeshNodes, NameNodes, TransformNodes to create a RobotHand object.
     * Also contains the Scene Graph and calculations for initial positions and rotations.
     *
     * @param gl - grpahics library
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

        float[] digitHrzPos = {palmWidth / 2, 1.5f, 0.5f, -0.5f, -1.5f};

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

        MeshNode shapePhal[][] = new MeshNode[DIGIT_COUNT][PHALANGE_COUNT];
        MeshNode shapeArm = new MeshNode("Cube(shapeArm)", cubeRobot);
        MeshNode shapePalm = new MeshNode("Cube(shapePalm)", cubeRobot);
        MeshNode shapeRing = new MeshNode("Sphere(shapeRing)", sphereRing);
        MeshNode shapeRingGem = new MeshNode("Sphere(shapeRingGem)", sphereRingGem);

        robotHand = new NameNode("root");
        NameNode digit[][] = new NameNode[DIGIT_COUNT][PHALANGE_COUNT];
        NameNode arm = new NameNode("arm");
        NameNode palm = new NameNode("palm");
        NameNode ring = new NameNode("ring");
        NameNode ringGem = new NameNode("ringGem");

        // ------------ Initialise all Arrays & Generate Digit Node ------------ \\

        /*
        Loops across all digits and up the phalanges to initialise all Nodes, Angles, and Keyframe Data
         */
        for (int d = 0; d < DIGIT_COUNT; d++) {

            // Set boundaries for digit rotations
            maxSecAngle[d] = 20;
            minSecAngle[d] = -20;

            for (int p = 0; p < PHALANGE_COUNT; p++) {

                // Initialise neutral keyframe if it has been defined
                if (Arty.neutralKeyframe != null) {
                    desiredPrmAngles[d][p] = Arty.neutralKeyframe.getPrmAngles()[d][p];
                    currentPrmAngles[d][p] = Arty.neutralKeyframe.getPrmAngles()[d][p];
                }

                // Boundaries for phalanges
                maxPrmAngle[d][p] = 90;
                minPrmAngle[d][p] = -5;

                shapePhal[d][p] = new MeshNode("Cube(digit" + d + "-phal" + p + ")", cubeRobot);
                digit[d][p] = new NameNode("digit[" + d + "][" + p + "]");
                translatePhal[d][p] = new TransformNode("translatePhal[" + d + "][" + p + "]",
                        Mat4Transform.translate(phalTranslations[d][p][0], phalTranslations[d][p][1], phalTranslations[d][p][2]));

                m = new Mat4(1);
                m = Mat4.multiply(m, Mat4Transform.scale(phalDims[d][p][0], phalDims[d][p][1], phalDims[d][p][2]));
                if (d == 0) {
                    m = Mat4.multiply(m, Mat4Transform.translate(0.5f, 0, 0));
                }else{
                    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                }

                scalePhal[d][p] = new TransformNode("scalePhal[" + d + "][" + Integer.toString(p) + "]", m);
                if (d == 0) {
                    rotateXPhal[d][p] = new TransformNode("rotateXPhal[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundX(currentSecAngles[d]));
                    rotateZPhal[d][p] = new TransformNode("rotateZPhal[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundZ(currentPrmAngles[d][p]));
                } else {
                    rotateXPhal[d][p] = new TransformNode("rotateXPhal[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundX(currentPrmAngles[d][p]));
                    rotateZPhal[d][p] = new TransformNode("rotateZPhal[" + d + "][" + Integer.toString(p) + "]", Mat4Transform.rotateAroundZ(currentSecAngles[d]));
                }
            }

            // Initialise neutral keyframe if it has been defined
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

        m = new Mat4(1);
        m = Mat4Transform.scale(armWidth, armHeight, armDepth); // Sets dimensions of arm
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0)); // Move up by 0.5 * height for origin
        TransformNode scaleArm = new TransformNode("arm transform", m);
        rotateYArm = new TransformNode("arm rotate", Mat4Transform.rotateAroundY(0));

        m = new Mat4(1);
        translatePalm = new TransformNode("palm translate", Mat4Transform.translate(0, armHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(palmWidth, palmHeight, palmDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode scalePalm = new TransformNode("palm transform", m);

        // ------------ Ring & Spotlight ------------ \\

        translateRing = new TransformNode("ring translate", Mat4Transform.translate(0, 0.5f * phalLrgHeight, 0));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(1.8f * phalLrgWidth, 0.4f * phalLrgHeight, 1.8f * phalLrgDepth));
        TransformNode scaleRing = new TransformNode("ring transform", m);
        translateRingGem = new TransformNode("ringGem translate", Mat4Transform.translate(0, 0, -0.8f * phalLrgDepth));
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(0.4f, 0.4f, 0.4f));
        scaleRingGem = new TransformNode("ringGem transform", m);

        // Nodes for setting direction of the Spotlight
        translateSpotlight = new TransformNode("spotlight translate", Mat4Transform.translate(0, 0, 1));
        spotlightBeacon = new NameNode("Spotlight Beacon");

        // ------------ Scene Graph ------------ \\

        robotHand.addChild(arm);
            arm.addChild(rotateYArm);
                rotateYArm.addChild(scaleArm);
                    scaleArm.addChild(shapeArm);
                rotateYArm.addChild(palm);

                    palm.addChild(translatePalm);
                        translatePalm.addChild(scalePalm);
                            scalePalm.addChild(shapePalm);

                        translatePalm.addChild(translatePhal[0][0]);
                            translatePhal[0][0].addChild(digit[0][0]);
                                digit[0][0].addChild(rotateXPhal[0][0]);
                                    rotateXPhal[0][0].addChild(rotateZPhal[0][0]);
                                        rotateZPhal[0][0].addChild(scalePhal[0][0]);
                                            scalePhal[0][0].addChild(shapePhal[0][0]);

                                        rotateZPhal[0][0].addChild(translatePhal[0][1]);
                                            translatePhal[0][1].addChild(digit[0][1]);
                                                digit[0][1].addChild(rotateZPhal[0][1]);
                                                    rotateZPhal[0][1].addChild(scalePhal[0][1]);
                                                        scalePhal[0][1].addChild(shapePhal[0][1]);

                                                    rotateZPhal[0][1].addChild(translatePhal[0][2]);
                                                        translatePhal[0][2].addChild(digit[0][2]);
                                                            digit[0][2].addChild(rotateZPhal[0][2]);
                                                                rotateZPhal[0][2].addChild(scalePhal[0][2]);
                                                                    scalePhal[0][2].addChild(shapePhal[0][2]);

                        for (int d = 1; d < DIGIT_COUNT; d++) {
                            translatePalm.addChild(translatePhal[d][0]);
                                translatePhal[d][0].addChild(digit[d][0]);
                                    digit[d][0].addChild(rotateZPhal[d][0]);
                                        rotateZPhal[d][0].addChild(rotateXPhal[d][0]);
                                            rotateXPhal[d][0].addChild(scalePhal[d][0]);
                                                scalePhal[d][0].addChild(shapePhal[d][0]);

                                            rotateXPhal[d][0].addChild(translatePhal[d][1]);
                                                translatePhal[d][1].addChild(digit[d][1]);
                                                    digit[d][1].addChild(rotateXPhal[d][1]);
                                                        rotateXPhal[d][1].addChild(scalePhal[d][1]);
                                                            scalePhal[d][1].addChild(shapePhal[d][1]);

                                                        rotateXPhal[d][1].addChild(translatePhal[d][2]);
                                                            translatePhal[d][2].addChild(digit[d][2]);
                                                                digit[d][2].addChild(rotateXPhal[d][2]);
                                                                    rotateXPhal[d][2].addChild(scalePhal[d][2]);
                                                                        scalePhal[d][2].addChild(shapePhal[d][2]);
                        }

                                            rotateXPhal[3][0].addChild(translateRing);
                                                translateRing.addChild(ring);
                                                    ring.addChild(scaleRing);
                                                        scaleRing.addChild(shapeRing);
                                                    ring.addChild(translateRingGem);
                                                        translateRingGem.addChild(ringGem);
                                                            ringGem.addChild(scaleRingGem);
                                                                scaleRingGem.addChild(shapeRingGem);
                                                            ringGem.addChild(translateSpotlight);
                                                                translateSpotlight.addChild(spotlightBeacon);
        robotHand.update();
        robotHand.print(1, true);
    }

    // ------------ User Controlled Functions ------------ \\

    /**
     * Sets rotation of Arm around Y-axis
     *
     * @param angle - the angle that the arm is set to
     */
    public void setArmBearing(int angle) {
        rotateYArm.setTransform(Mat4Transform.rotateAroundY(angle));
    }

    /**
     * Moves the robotHand to the keyframe at line [keyframe] in the data csv
     *
     * @param keyframe - the Keyframe reference number for the imported keyframes
     */
    public void moveToKeyframe(int keyframe) {
        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                desiredPrmAngles[d][p] = Arty.keyframes.get(keyframe).getPrmAngles()[d][p];
            }
            desiredSecAngles[d] = Arty.keyframes.get(keyframe).getSecAngles()[d];
        }
        midAnimation = true;
        currentKeyframe = keyframe;
    }

    /**
     * Toggles all animations in the scene
     */
    public void toggleGlobalAnims() {
        animationOn = !animationOn;
    }

    /**
     * Toggles the robotHand's loop through the imported keyframes
     */
    public void toggleKeyframeSequence() {
        keyframeAnimation = !keyframeAnimation;
    }

    // ------------ Getters ------------ \\

    /**
     * Gets the ring's current world-space position for the spotlight
     *
     * @return - ring position as a Vec3
     */
    public Vec3 getRingPos() {
        return scaleRingGem.getWorldTransform().getCoords();
    }

    //

    /**
     * Gets the vector between the spotlight and the 'beacon' (+1 in the relative Z-axis) as direction
     * to get the spotlight direction
     *
     * @return - direction of spotlight as Vec3
     */
    public Vec3 getRingDir() {
        Vec3 spotlightBeaconPos = spotlightBeacon.getWorldTransform().getCoords();
        Vec3 spotlightOrigin = getRingPos();
        Vec3 direction = Vec3.subtract(spotlightOrigin, spotlightBeaconPos);
        return direction;
    }

    // ------------ Model Manipulation/Drawing ------------ \\

    /**
     * Updates current angles of phalanges to match the target angles. It increments 1 degree per render loop to
     * interpolate smoothly between keyframes. Also checks if the maximum rotation of that phalange has been reached
     */
    private void updateCurrentAngles() {
        if (midAnimation) {
            for (int d = 0; d < DIGIT_COUNT; d++) {

                // Primary Angles
                for (int p = 0; p < PHALANGE_COUNT; p++) {
                    if (currentPrmAngles[d][p] - desiredPrmAngles[d][p] < 0) {
                        if (currentPrmAngles[d][p] < maxPrmAngle[d][p]) {
                            currentPrmAngles[d][p]++;
                        }else{
                            desiredPrmAngles[d][p] = currentPrmAngles[d][p];
                        }
                    } else if (currentPrmAngles[d][p] - desiredPrmAngles[d][p] > 0) {
                        if (currentPrmAngles[d][p] > minPrmAngle[d][p]) {
                            currentPrmAngles[d][p]--;
                        }else{
                            desiredPrmAngles[d][p] = currentPrmAngles[d][p];
                        }
                    }
                }

                // Secondary Angles
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
            // If all desired angles have been reach, set the midAnimation flag to false
            midAnimation = !((Arrays.deepEquals(currentPrmAngles, desiredPrmAngles)) && (Arrays.equals(currentSecAngles, desiredSecAngles)));
        }
    }

    /**
     * Updates the actual world-space Nodes to match the current angles set in the above function
     */
    private void updateDigitPositions() {
        for (int d = 0; d < DIGIT_COUNT; d++) {
            for (int p = 0; p < PHALANGE_COUNT; p++) {
                if (d != 0) {
                    rotateXPhal[d][p].setTransform(Mat4Transform.rotateAroundX(currentPrmAngles[d][p]));
                    rotateZPhal[d][p].setTransform(Mat4Transform.rotateAroundZ(currentSecAngles[d]));
                }else{
                    rotateZPhal[d][p].setTransform(Mat4Transform.rotateAroundZ(currentPrmAngles[d][p]));
                    rotateXPhal[d][p].setTransform(Mat4Transform.rotateAroundX(currentSecAngles[d]));
                }
            }
        }
    }

    /**
     * Renders the robotHand, checking for the animationOn flag and keyframeSequence flag
     *
     * @param gl - graphics library
     */
    public void render(GL3 gl) {
        if (animationOn) {
            // If the keyframe toggle is true and no animation is taking place...
            if ((keyframeAnimation) && (!midAnimation)) {
                // ... move to the next keyframe
                currentKeyframe++;

                // Keeps the currentKeyframe value from surpassing the length of the csv
                if (currentKeyframe > Arty.keyframes.size()-1) {
                    currentKeyframe = 0;
                }
                moveToKeyframe(currentKeyframe);
            }

            // Only updates digits if animation is toggled on
            updateCurrentAngles();
            updateDigitPositions();
        }

        robotHand.update();
        robotHand.draw(gl);
    }
}