import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class M03tree_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private float aspect;
    
  public M03tree_GLEventListener(Camera camera) {
    this.camera = camera;
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    aspect = (float)width/(float)height;
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    disposeMeshes(gl);
  }

  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
   public void incXPosition() {
     xPosition += 0.5f;
     if (xPosition>5f) xPosition = 5f;
     updateX();
   }
   
   public void decXPosition() {
     xPosition -= 0.5f;
     if (xPosition<-5f) xPosition = -5f;
     updateX();
   }
   
   private void updateX() {
     translateX.setTransform(Mat4Transform.translate(xPosition,0,0));
     translateX.update(); // IMPORTANT – the scene graph has changed
   }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */
   
  private Camera camera;
  private Mat4 perspective;
  private Mesh floor, sphere;
  private Light light;
  private SGNode multiBranch;
  
  private TransformNode translateX, rotate_b0, rotate_b01, rotate_b02, rotate_b012;
  private float xPosition = 0;
  private float rotate_b0_AngleStart = 25, rotate_b0_Angle = rotate_b0_AngleStart;
  private float rotate_b01_AngleStart = -60, rotate_b01_Angle = rotate_b01_AngleStart;
  private float rotate_b02_AngleStart = 30, rotate_b02_Angle = rotate_b02_AngleStart;
  private float rotate_b012_AngleStart = -45, rotate_b012_Angle = rotate_b012_AngleStart;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    
    // make meshes
    floor = new TwoTriangles(gl, textureId0);
    floor.setModelMatrix(Mat4Transform.scale(16,1,16));
    
    sphere = new Sphere(gl, textureId1, textureId2);

    light = new Light(gl);
    light.setCamera(camera);
    
    floor.setLight(light);
    floor.setCamera(camera);
    sphere.setLight(light);
    sphere.setCamera(camera);   
    
    // make nodes
    MeshNode b0_BranchShape = new MeshNode("Sphere(0)", sphere);
    MeshNode b01_BranchShape = new MeshNode("Sphere(1)", sphere);
    MeshNode b02_BranchShape = new MeshNode("Sphere(2)", sphere);
    MeshNode b012_BranchShape = new MeshNode("Sphere(3)", sphere);
    
    multiBranch = new NameNode("two-branch structure");
    NameNode b0_Branch = new NameNode("lower branch");
    NameNode b01_Branch = new NameNode("upper branch");
    NameNode b02_Branch = new NameNode("upper branch2");
    NameNode b012_Branch = new NameNode("upper branch3");
    
    float b0_height = 4.2f;
    float b01_height = 3.1f;
    float b02_height = 3.1f;
    float b012_height = 1.9f;
    
    translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));
    rotate_b0 = new TransformNode("rotateAroundZ("+rotate_b0_Angle+")", Mat4Transform.rotateAroundZ(rotate_b0_Angle));
    
    Mat4 m = Mat4Transform.scale(2.5f,b0_height,2.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode make_b0_Branch = new TransformNode("scale(2.5,4,2.5); translate(0,0.5,0)", m);
              
    TransformNode translate_b01_to_top_of_b0 = new TransformNode("translate(0,4,0)",Mat4Transform.translate(0,b0_height,0));
    rotate_b01 = new TransformNode("rotateAroundZ("+rotate_b01_Angle+")",Mat4Transform.rotateAroundZ(rotate_b01_Angle));
    m = Mat4Transform.scale(1.4f,b01_height,1.4f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode make_b01_Branch = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);
                  
    TransformNode translate_b02_to_top_of_b0 = new TransformNode("translate(0,4,0)",Mat4Transform.translate(0,b0_height,0));
    rotate_b02 = new TransformNode("rotateAroundX("+rotate_b02_Angle+")",Mat4Transform.rotateAroundX(rotate_b02_Angle));
    m = Mat4Transform.scale(1.4f,b02_height,1.4f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode make_b02_Branch = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);
                  
    TransformNode translate_b012_to_top_of_b01 = new TransformNode("translate(0,4,0)",Mat4Transform.translate(0,b01_height,0));
    rotate_b012 = new TransformNode("rotateAroundX("+rotate_b012_Angle+")",Mat4Transform.rotateAroundX(rotate_b012_Angle));
    m = Mat4Transform.scale(1.1f,b012_height,1.1f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode make_b012_Branch = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);

    // join nodes to make a scene graph
    multiBranch.addChild(translateX);
      translateX.addChild(rotate_b0);
        rotate_b0.addChild(b0_Branch);
          b0_Branch.addChild(make_b0_Branch);
            make_b0_Branch.addChild(b0_BranchShape);
          b0_Branch.addChild(translate_b01_to_top_of_b0);
            translate_b01_to_top_of_b0.addChild(rotate_b01);
              rotate_b01.addChild(b01_Branch);
                b01_Branch.addChild(make_b01_Branch);
                  make_b01_Branch.addChild(b01_BranchShape);
        b0_Branch.addChild(translate_b02_to_top_of_b0);
            translate_b02_to_top_of_b0.addChild(rotate_b02);
              rotate_b02.addChild(b02_Branch);
                b02_Branch.addChild(make_b02_Branch);
                  make_b02_Branch.addChild(b02_BranchShape);
        b02_Branch.addChild(translate_b012_to_top_of_b01);
                  translate_b012_to_top_of_b01.addChild(rotate_b012);
                    rotate_b012.addChild(b012_Branch);
                      b012_Branch.addChild(make_b012_Branch);
                        make_b012_Branch.addChild(b012_BranchShape);  
          
    multiBranch.update();  // IMPORTANT – must be done every time any part of the scene graph changes
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    updatePerspectiveMatrices();
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);
    updateBranches();
    multiBranch.draw(gl);
  }
  
  private void updatePerspectiveMatrices() {
    // needs to be changed if user resizes the window
    perspective = Mat4Transform.perspective(45, aspect);
    light.setPerspective(perspective);
    floor.setPerspective(perspective);
    sphere.setPerspective(perspective);
  }
  
  private void disposeMeshes(GL3 gl) {
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
  }

  private void updateBranches() {
    double elapsedTime = getSeconds()-startTime;
    rotate_b0_Angle = rotate_b0_AngleStart*(float)Math.sin(elapsedTime);
    rotate_b01_Angle = rotate_b01_AngleStart*(float)Math.sin(elapsedTime*0.7f);
    rotate_b02_Angle = rotate_b02_AngleStart*(float)Math.sin(elapsedTime*0.9f);
    rotate_b012_Angle = rotate_b012_AngleStart*(float)Math.sin(elapsedTime*0.6f);
    rotate_b0.setTransform(Mat4Transform.rotateAroundZ(rotate_b0_Angle));
    rotate_b01.setTransform(Mat4Transform.rotateAroundZ(rotate_b01_Angle));
    rotate_b02.setTransform(Mat4Transform.rotateAroundX(rotate_b02_Angle));
    rotate_b012.setTransform(Mat4Transform.rotateAroundX(rotate_b012_Angle));
    multiBranch.update(); // IMPORTANT – the scene graph has changed
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }
  
}