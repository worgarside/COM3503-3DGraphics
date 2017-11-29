import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Gallery {
  
    private Mesh floor, wallBack, wallLeft, wallRight, wallFront, ceiling;
    private int gallerySize = 25;

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
        floor = new TwoTriangles(gl, textureFloor);
        floor.setModelMatrix(getFloorMatrix());
        wallBackTop = new TwoTriangles(gl, textureWallWindow);
        wallBackTop.setModelMatrix(getWallBackTopMatrix());
        wallBackLeft = new TwoTriangles(gl, textureWallWindow);
        wallBackLeft.setModelMatrix(getWallBackLeftMatrix());
        wallBackRight = new TwoTriangles(gl, textureWallWindow);
        wallBackRight.setModelMatrix(getWallBackRightMatrix());
        wallBackBottom = new TwoTriangles(gl, textureWallWindow);
        wallBackBottom.setModelMatrix(getWallBackBottomMatrix());
        wallLeft = new TwoTriangles(gl, textureWall1);
        wallLeft.setModelMatrix(getWallLeftMatrix());
        wallRight = new TwoTriangles(gl, textureWall2);
        wallRight.setModelMatrix(getWallRightMatrix());
        wallFront = new TwoTriangles(gl, textureWallDoor);
        wallFront.setModelMatrix(getWallFrontMatrix());
        ceiling = new TwoTriangles(gl, textureCeiling);
        ceiling.setModelMatrix(getCeilingMatrix());
    }

    private void render(GL3 gl) {
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