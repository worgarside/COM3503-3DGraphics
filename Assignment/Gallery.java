import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.util.ArrayList;
  
public class Gallery {

    private Mesh floor, wallLeft, wallRight, wallFront, wallBackTop, wallBackLeft, wallBackRight, wallBackBottom, ceiling, outside;
    private ArrayList<Mesh> meshListGallery = new ArrayList<Mesh>();
    private float gallerySize;


    public Gallery (float gallerySize, Mesh floor, Mesh wallLeft, Mesh wallRight, Mesh wallFront, Mesh wallBackTop, Mesh wallBackLeft, Mesh wallBackRight, Mesh wallBackBottom, Mesh ceiling, Mesh outside) {
        this.floor = floor;
        this.wallLeft = wallLeft;
        this.wallRight = wallRight;
        this.wallFront = wallFront;
        this.wallBackTop = wallBackTop;
        this.wallBackLeft = wallBackLeft;
        this.wallBackRight = wallBackRight;
        this.wallBackBottom = wallBackBottom;
        this.ceiling = ceiling;
        this.outside = outside;
        this.gallerySize = gallerySize;

        meshListGallery.add(floor);
        meshListGallery.add(wallLeft);
        meshListGallery.add(wallRight);
        meshListGallery.add(wallFront);
        meshListGallery.add(wallBackTop);
        meshListGallery.add(wallBackLeft);
        meshListGallery.add(wallBackRight);
        meshListGallery.add(wallBackBottom);
        meshListGallery.add(ceiling);
        meshListGallery.add(outside);
    }


    private void  initMeshes(){
        floor.setModelMatrix(getFloorMatrix());
        wallLeft.setModelMatrix(getWallLeftMatrix());
        wallRight.setModelMatrix(getWallRightMatrix());
        wallFront.setModelMatrix(getWallFrontMatrix());
        wallBackTop.setModelMatrix(getWallBackTopMatrix());
        wallBackLeft.setModelMatrix(getWallBackLeftMatrix());
        wallBackRight.setModelMatrix(getWallBackRightMatrix());
        wallBackBottom.setModelMatrix(getWallBackBottomMatrix());
        ceiling.setModelMatrix(getCeilingMatrix());
        outside.setModelMatrix(getOutsideMatrix());
    }

    public void render(GL3 gl) {
        initMeshes();
        for (Mesh mesh : meshListGallery) {
            mesh.render(gl);
        }
    }

    private Mat4 getFloorMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize, 1f, gallerySize), model);
        return model;
    }

    private Mat4 getWallLeftMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), model);
        model = Mat4.multiply(Mat4Transform.translate(-gallerySize *0.5f, gallerySize *0.5f, 0), model);
        return model;
    }

    private Mat4 getWallRightMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(gallerySize *0.5f, gallerySize *0.5f,0), model);
        return model;
    }

    private Mat4 getWallFrontMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundY(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize *0.5f, gallerySize *0.5f), model);
        return model;
    }

    private Mat4 getCeilingMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f, gallerySize), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundZ(180), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize,0), model);
        return model;
    }

    private Mat4 getWallBackTopMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f, gallerySize *0.25f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize *0.875f,-gallerySize *0.5f), model);
        return model;
    }

    private Mat4 getWallBackLeftMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize *0.25f,1f, gallerySize *0.5f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(-gallerySize *0.375f, gallerySize *0.5f,-gallerySize *0.5f), model);
        return model;
    }

    private Mat4 getWallBackRightMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize *0.25f,1f, gallerySize *0.5f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(gallerySize *0.375f, gallerySize *0.5f,-gallerySize *0.5f), model);
        return model;
    }

    private Mat4 getWallBackBottomMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize,1f, gallerySize *0.25f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize *0.125f,-gallerySize *0.5f), model);
        return model;
    }

    private Mat4 getOutsideMatrix() {
        Mat4 model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(gallerySize *1.6f*2f,1f, gallerySize *0.9f*2f), model);
        model = Mat4.multiply(Mat4Transform.rotateAroundX(90), model);
        model = Mat4.multiply(Mat4Transform.translate(0, gallerySize *0.5f,-gallerySize *2), model);
        return model;
    }
}