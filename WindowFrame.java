import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class WindowFrame {

    public WindowFrame(Mesh cubeWindowFrame, float gallerySize) {
        this.cubeWindowFrame = cubeWindowFrame;
        this.gallerySize = gallerySize;
    }

    private SGNode windowFrame;
    private Mesh cubeWindowFrame;
    private float gallerySize;
    private TransformNode translateTop, translateLeft, translateRight, translateBottom;

    public void initialise(GL3 gl) {

        // ------------ MeshNodes, NameNodes, TranslationNodes, TransformationNodes ------------ \\

        MeshNode shapeTop = new MeshNode("Cube(top)", cubeWindowFrame);
        MeshNode shapeLeft = new MeshNode("Cube(left)", cubeWindowFrame);
        MeshNode shapeRight = new MeshNode("Cube(body)", cubeWindowFrame);
        MeshNode shapeBottom = new MeshNode("Cube(body)", cubeWindowFrame);

        windowFrame = new NameNode("root");
        NameNode nameTop = new NameNode("top");
        NameNode nameLeft = new NameNode("left");
        NameNode nameRight = new NameNode("right");
        NameNode nameBottom = new NameNode("bottom");

        // ------------ Dimensions ------------ \\

        float frameHeight = 0.5f;
        float frameDepth = 0.75f;
        float windowSize = (0.5f * gallerySize) + frameHeight;

        // ------------ Initialise ------------ \\

        translateTop = new TransformNode("top translate", Mat4Transform.translate(0, (gallerySize / 4) * 3, -gallerySize / 2));
        Mat4 m = Mat4Transform.scale(windowSize, frameHeight, frameDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode scaleTop = new TransformNode("top scale", m);

        translateLeft = new TransformNode("left translate", Mat4Transform.translate(-gallerySize / 4, gallerySize / 4, -gallerySize / 2));
        m = Mat4Transform.scale(frameHeight, windowSize, frameDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode scaleLeft = new TransformNode("left scale", m);

        translateRight = new TransformNode("right translate", Mat4Transform.translate(gallerySize / 4, gallerySize / 4, -gallerySize / 2));
        m = Mat4Transform.scale(frameHeight, windowSize, frameDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode scaleRight = new TransformNode("right scale", m);

        translateBottom = new TransformNode("bottom translate", Mat4Transform.translate(0, (gallerySize / 4), -gallerySize / 2));
        m = Mat4Transform.scale(windowSize, frameHeight, frameDepth);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode scaleBottom = new TransformNode("bottom scale", m);

        // ------------ Scene Graph ------------ \\

        windowFrame.addChild(nameTop);
            nameTop.addChild(translateTop);
                translateTop.addChild(scaleTop);
                    scaleTop.addChild(shapeTop);
            
        windowFrame.addChild(nameLeft);
            nameLeft.addChild(translateLeft);
                translateLeft.addChild(scaleLeft);
                    scaleLeft.addChild(shapeLeft);
            
        windowFrame.addChild(nameRight);
            nameRight.addChild(translateRight);
                translateRight.addChild(scaleRight);
                    scaleRight.addChild(shapeRight);
        
        windowFrame.addChild(nameBottom);
            nameBottom.addChild(translateBottom);
                translateBottom.addChild(scaleBottom);
                    scaleBottom.addChild(shapeBottom);

        windowFrame.update();
    }

    public void render(GL3 gl) {
        windowFrame.draw(gl);
    }



}