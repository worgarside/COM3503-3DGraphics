import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Lamp {

    public Lamp(int lightNum, Mesh cubeBase, Mesh cubeBody, Mesh cubeHead, Vec3 position) {
        this.lightNum = lightNum;
        this.cubeBase = cubeBase;
        this.cubeBody = cubeBody;
        this.cubeHead = cubeHead;
        this.position = position;
    }

    public Vec3 getLightBulbPos() {
        lightBulbMatrix = new Mat4(1);
        lightBulbMatrix = Mat4.multiply(lightBulbMatrix, transformNodeToMat4(baseTranslate));
        lightBulbMatrix = Mat4.multiply(lightBulbMatrix, transformNodeToMat4(bodyTranslate));
        lightBulbMatrix = Mat4.multiply(lightBulbMatrix, transformNodeToMat4(bulbTranslate));

        return coordsFromMat4(lightBulbMatrix);
    }

    // ***************************************************
    /* THE SCENE
    */

    private int lightNum;
    private Mesh cubeBase, cubeBody, cubeHead;
    private Vec3 position;
    private SGNode lamp;
    private Mat4 lightBulbMatrix = new Mat4(1);
    private TransformNode bodyTranslate, baseTranslate, bulbTranslate;
    private static final int LAMP_ARM_COUNT = 4;

    public void initialise(GL3 gl) {

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode baseShape = new MeshNode("Cube(base)", cubeBase);
        MeshNode bodyShape = new MeshNode("Cube(body)", cubeBody);

        lamp = new NameNode("root");
        NameNode base = new NameNode("base");
        NameNode body = new NameNode("body");

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

        lamp.update();
    }

    public void render(GL3 gl) {
        lamp.draw(gl);
    }

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

    public void setState(Light light, int state) {
        light.setPower(lightNum, state);
    }
}