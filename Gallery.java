import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import gmaths.*;

import java.util.ArrayList;

public class Gallery {

    private Mesh floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, ceiling, outsideDay, outsideNight;
    private ArrayList<Mesh> meshList = new ArrayList<Mesh>();
    private float gallerySize;

    public Gallery (float gallerySize, Mesh floor, Mesh wallLeft, Mesh wallRight, Mesh wallFront,
                    Mesh wallBackTop, Mesh wallBackLeft, Mesh wallBackRight, Mesh wallBackBottom,
                    Mesh ceiling, Mesh outsideDay, Mesh outsideNight) {
        this.floor = floor;
        this.wallLeft = wallLeft;
        this.wallRight = wallRight;
        this.wallFront = wallFront;
        this.wallBackTop = wallBackTop;
        this.wallBackLeft = wallBackLeft;
        this.wallBackRight = wallBackRight;
        this.wallBackBottom = wallBackBottom;
        this.ceiling = ceiling;
        this.outsideDay = outsideDay;
        this.outsideNight = outsideNight;
        this.gallerySize = gallerySize;

        meshList.add(floor);
        meshList.add(wallLeft);
        meshList.add(wallRight);
        meshList.add(wallFront);
        meshList.add(wallBackTop);
        meshList.add(wallBackLeft);
        meshList.add(wallBackRight);
        meshList.add(wallBackBottom);
        meshList.add(ceiling);
    }

    private void  initMeshes() {
        floor.setModelMatrix(getFloorMatrix());
        wallLeft.setModelMatrix(getWallLeftMatrix());
        wallRight.setModelMatrix(getWallRightMatrix());
        wallFront.setModelMatrix(getWallFrontMatrix());
        wallBackTop.setModelMatrix(getWallBackTopMatrix());
        wallBackLeft.setModelMatrix(getWallBackLeftMatrix());
        wallBackRight.setModelMatrix(getWallBackRightMatrix());
        wallBackBottom.setModelMatrix(getWallBackBottomMatrix());
        ceiling.setModelMatrix(getCeilingMatrix());
        outsideDay.setModelMatrix(getOutsideMatrix());
        outsideNight.setModelMatrix(getOutsideMatrix());
    }

    public void render(GL3 gl) {
        initMeshes();
        for (Mesh mesh : meshList) {
            mesh.render(gl);
        }
        if (Arty.night) {
            outsideNight.render(gl);
        } else {
            outsideDay.render(gl);
        }
    }

    private Mat4 getFloorMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize), model);
        return model;
    }

    private Mat4 getWallLeftMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), model);
        model = Mat4.multiply(Mat4Transform.translate(-gallerySize * 0.5f, gallerySize * 0.5f, 0), model);
        return model;
    }

    private Mat4 getWallRightMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(gallerySize * 0.5f, gallerySize * 0.5f, 0), model);
        return model;
    }

    private Mat4 getWallFrontMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize * 0.5f, gallerySize * 0.5f), model);
        return model;
    }

    private Mat4 getCeilingMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize, 0), model);
        return model;
    }

    private Mat4 getWallBackTopMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize * 0.25f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize * 0.875f, -gallerySize * 0.5f), model);
        return model;
    }

    private Mat4 getWallBackLeftMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize * 0.25f, 1f, gallerySize * 0.5f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(-gallerySize * 0.375f, gallerySize * 0.5f, -gallerySize * 0.5f), model);
        return model;
    }

    private Mat4 getWallBackRightMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize * 0.25f, 1f, gallerySize * 0.5f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(gallerySize * 0.375f, gallerySize * 0.5f, -gallerySize * 0.5f), model);
        return model;
    }

    private Mat4 getWallBackBottomMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize * 0.25f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize * 0.125f, -gallerySize * 0.5f), model);
        return model;
    }

    private Mat4 getOutsideMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize * 4f, 1f, gallerySize * 2), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize * 0.5f, -gallerySize * 2), model);
        return model;
    }
}