import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Gallery {
  
    private Mesh floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, ceiling;
    private float gallerySize = 16f;

    public Gallery(Mesh floor, Mesh wallLeft, Mesh wallRight, Mesh wallFront, Mesh wallBackTop, Mesh wallBackLeft, Mesh wallBackRight, Mesh wallBackBottom, Mesh ceiling) {
        this.floor = floor;
        this.wallLeft = wallLeft;
        this.wallRight = wallRight;
        this.wallFront = wallFront;
        this.wallBackTop = wallBackTop;
        this.wallBackLeft = wallBackLeft;
        this.wallBackRight = wallBackRight;
        this.wallBackBottom = wallBackBottom;
        this.ceiling = ceiling;
    }

    private void initMatrices() {
        floor.setModelMatrix(getFloorMatrix());
        wallBackTop.setModelMatrix(getWallBackTopMatrix());
        wallBackLeft.setModelMatrix(getWallBackLeftMatrix());
        wallBackRight.setModelMatrix(getWallBackRightMatrix());
        wallBackBottom.setModelMatrix(getWallBackBottomMatrix());
        wallLeft.setModelMatrix(getWallLeftMatrix());
        wallRight.setModelMatrix(getWallRightMatrix());
        wallFront.setModelMatrix(getWallFrontMatrix());
        ceiling.setModelMatrix(getCeilingMatrix());
    }

    public void render(GL3 gl) {
        initMatrices();
        floor.render(gl);
        wallBackTop.render(gl);
        wallBackLeft.render(gl);
        wallBackRight.render(gl);
        wallBackBottom.render(gl);
        wallLeft.render(gl);
        wallRight.render(gl);
        wallFront.render(gl);
        ceiling.render(gl);
    }

    private Mat4 getFloorMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize), model);
        return model;
    }

    private Mat4 getWallBackTopMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0,gallerySize*0.5f,-gallerySize*0.5f), model);
        return model;
    }

    private Mat4 getWallBackLeftMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0,gallerySize*0.5f,-gallerySize*0.5f), model);
        return model;
    }

    private Mat4 getWallBackRightMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0,gallerySize*0.5f,-gallerySize*0.5f), model);
        return model;
    }

    private Mat4 getWallBackBottomMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0,gallerySize*0.5f,-gallerySize*0.5f), model);
        return model;
    }

    private Mat4 getWallLeftMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), model);
        model = Mat4.multiply(Mat4Transform.translate(-gallerySize*0.5f, gallerySize*0.5f, 0), model);
        return model;
    }

    private Mat4 getWallRightMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(gallerySize*0.5f,gallerySize*0.5f,0), model);
        return model;
    }

    private Mat4 getWallFrontMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0,gallerySize*0.5f,gallerySize*0.5f), model);
        return model;
    }

    private Mat4 getCeilingMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f,gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0,gallerySize,0), model);
        return model;
    }
}