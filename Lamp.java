import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import gmaths.*;

/**
 * Lamp.java
 * Creates an Lamp object
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
public class Lamp {

    /**
     * Constructor for Lamp object
     *
     * @param lightNum - the lightSource reference number
     * @param cubeBase - Mesh for the Lamp base
     * @param cubeBody - Mesh for the Lamp body and arms
     * @param position - a Vec3 to set the position of the Lamp in the scene
     */
    public Lamp(int lightNum, Mesh cubeBase, Mesh cubeBody, Vec3 position) {
        this.lightNum = lightNum;
        this.cubeBase = cubeBase;
        this.cubeBody = cubeBody;
        this.position = position;
    }

    /**
     * Getter for the NameNode bulb world position for setting the lightSource's position
     *
     * @return bulb world position as a Vec3
     */
    public Vec3 getLightBulbPos() {
        return bulb.getWorldTransform().getCoords();
    }

    private int lightNum;
    private Mesh cubeBase, cubeBody;
    private Vec3 position;
    private SGNode lamp;
    private Mat4 lightBulbMatrix = new Mat4(1);
    private TransformNode bodyTranslate, baseTranslate, bulbTranslate;
    private static final int LAMP_ARM_COUNT = 4;
    private NameNode bulb;

    /**
     * Initialises all Nodes for creating a Lamp object
     * Generates scene graph
     *
     * @param gl - graphics library
     */
    public void initialise(GL3 gl) {

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode baseShape = new MeshNode("Cube(base)", cubeBase);
        MeshNode bodyShape = new MeshNode("Cube(body)", cubeBody);

        lamp = new NameNode("root");
        NameNode base = new NameNode("base");
        NameNode body = new NameNode("body");
        bulb = new NameNode("bulb");

        // ------------ Dimensions + Positions ------------ \\

        float baseWidth = 2.4f;
        float baseHeight = 0.8f;
        float baseDepth = 2.4f;

        float bodyWidth = 0.6f;
        float bodyHeight = 6.4f;
        float bodyDepth = 0.6f;

        float armWidth = 0.28f;
        float armHeight = 1.6f;
        float armDepth = 0.28f;

        int armAngleX = 45;
        int[] armAngleY = {45, 135, 225, 315};

        // ------------ Initialise ------------ \\

        baseTranslate = new TransformNode("base translate", Mat4Transform.translate(position.x, position.y, position.z));
        Mat4 m = Mat4Transform.scale(baseWidth, baseHeight, baseDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode baseScale = new TransformNode("base scale", m);

        m = new Mat4(1);
        bodyTranslate = new TransformNode("body translate", Mat4Transform.translate(0, baseHeight, 0));
        m = Mat4Transform.scale(bodyWidth, bodyHeight, bodyDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode bodyScale = new TransformNode("body scale", m);


        TransformNode[] armScale = new TransformNode[LAMP_ARM_COUNT];
        MeshNode[] armShape = new MeshNode[LAMP_ARM_COUNT];
        NameNode[] arm = new NameNode[LAMP_ARM_COUNT];

        for (int i = 0; i < LAMP_ARM_COUNT; i++) {
            armShape[i]  = new MeshNode("Cube(arm" + i + ")", cubeBody);

            arm[i] = new NameNode("arm" + i);
            m = new Mat4(1);
            m = Mat4.multiply(m, Mat4Transform.translate(0, bodyHeight-0.1f, 0));
            m = Mat4.multiply(m, Mat4Transform.rotateAroundY(armAngleY[i]));
            m = Mat4.multiply(m, Mat4Transform.rotateAroundX(armAngleX));
            m = Mat4.multiply(m, Mat4Transform.scale(armWidth, armHeight, armDepth));
            m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
            armScale[i] = new TransformNode("arm" + i + " scale", m);
        }

        bulbTranslate = new TransformNode("bulb translate", Mat4Transform.translate(0, bodyHeight + 1.5f, 0));

        // ------------ Scene Graph ------------ \\

        lamp.addChild(base); //lampTanslate??
            base.addChild(baseTranslate);
                baseTranslate.addChild(baseScale);
                    baseScale.addChild(baseShape);

                baseTranslate.addChild(body);
                    body.addChild(bodyTranslate);
                        bodyTranslate.addChild(bodyScale);
                            bodyScale.addChild(bodyShape);

                        for (int i = 0; i < LAMP_ARM_COUNT; i++) {
                            bodyTranslate.addChild(arm[i]);
                                arm[i].addChild(armScale[i]);
                                    armScale[i].addChild(armShape[i]);
                        }

                        bodyTranslate.addChild(bulbTranslate);
                            bulbTranslate.addChild(bulb);

        lamp.update();
    }

    /**
     * Renders the lamp object in the scene
     *
     * @param gl - graphics library
     */
    public void render(GL3 gl) {
        lamp.draw(gl);
    }

    /**
     * Sets the on/off state for the Lamp
     *
     * @param light - the Light object being edited
     * @param state - the on/off state for the lamp
     */
    public void setState(Light light, int state) {
        light.setPower(lightNum, state);
    }
}