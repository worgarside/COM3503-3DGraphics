import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Lamp {

    public Lamp(Mesh cubeBase, Mesh cubeBody, Mesh cubeHead, Vec3 position) {
        this.cubeBase = cubeBase;
        this.cubeBody = cubeBody;
        this.cubeHead = cubeHead;
        this.position = position;
    }

    // ***************************************************
    /* THE SCENE
    */

    private Mesh cubeBase, cubeBody, cubeHead;
    private Vec3 position;
    private SGNode lamp;

    public void initialise(GL3 gl) {

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode baseShape = new MeshNode("Cube(base)", cubeBase);
        MeshNode bodyShape = new MeshNode("Cube(body)", cubeBody);
        MeshNode arm1Shape = new MeshNode("Cube(arm1)", cubeBody);
        MeshNode arm2Shape = new MeshNode("Cube(arm1)", cubeBody);
        MeshNode arm3Shape = new MeshNode("Cube(arm1)", cubeBody);

        lamp = new NameNode("root");
        NameNode base = new NameNode("base");
        NameNode body = new NameNode("body");
        NameNode arm1 = new NameNode("arm1");
        NameNode arm2 = new NameNode("arm2");
        NameNode arm3 = new NameNode("arm3");

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

        // ------------ Initialise ------------ \\

        TransformNode baseTranslate = new TransformNode("base translate", Mat4Transform.translate(position.x, position.y, position.z));
        Mat4 m = Mat4Transform.scale(baseWidth, baseHeight, baseDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode baseScale = new TransformNode("base scale", m);

        m = new Mat4(1);
        TransformNode bodyTranslate = new TransformNode("body translate", Mat4Transform.translate(0, baseHeight, 0));
        m = Mat4Transform.scale(bodyWidth, bodyHeight, bodyDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode bodyScale = new TransformNode("body scale", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, bodyHeight-0.1f, 0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(120));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(45));
        m = Mat4.multiply(m, Mat4Transform.scale(armWidth, armHeight, armDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode arm1Scale = new TransformNode("arm1 scale", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, bodyHeight-0.1f, 0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(240));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(45));
        m = Mat4.multiply(m, Mat4Transform.scale(armWidth, armHeight, armDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode arm2Scale = new TransformNode("arm2 scale", m);

        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, bodyHeight-0.1f, 0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(45));
        m = Mat4.multiply(m, Mat4Transform.scale(armWidth, armHeight, armDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode arm3Scale = new TransformNode("arm3 scale", m);

        // ------------ Scene Graph ------------ \\

        lamp.addChild(base); //lampTanslate??
            base.addChild(baseTranslate);
                baseTranslate.addChild(baseScale);
                    baseScale.addChild(baseShape);

                baseTranslate.addChild(body);
                    body.addChild(bodyTranslate);
                        bodyTranslate.addChild(bodyScale);
                            bodyScale.addChild(bodyShape);

                        bodyTranslate.addChild(arm1);
                            arm1.addChild(arm1Scale);
                                arm1Scale.addChild(arm1Shape);

                        bodyTranslate.addChild(arm2);
                            arm2.addChild(arm2Scale);
                                arm2Scale.addChild(arm2Shape);

                        bodyTranslate.addChild(arm3);
                            arm3.addChild(arm3Scale);
                                arm3Scale.addChild(arm3Shape);

        lamp.update();
    }

    public void render(GL3 gl) {
        lamp.draw(gl);
    }

}