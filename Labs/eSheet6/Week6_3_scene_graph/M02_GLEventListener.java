import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class M02_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private float aspect;
    
  public M02_GLEventListener(Camera camera) {
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
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Mesh floor, cube;
  private Light light;
  private SGNode twoBranch;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    
    floor = new TwoTriangles(gl, textureId0);
    floor.setModelMatrix(Mat4Transform.scale(16,1f,16));
    
    cube = new Cube(gl, textureId1, textureId2);

    light = new Light(gl);
    light.setCamera(camera);
    
    floor.setLight(light);
    floor.setCamera(camera);
    cube.setLight(light);
    cube.setCamera(camera);   
    
    twoBranch = new NameNode("two-branch structure");
      NameNode lowerBranch = new NameNode("lower branch");
        Mat4 m = Mat4Transform.scale(2,4,2);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode lowerBranchTransform = new TransformNode("scale(2,4,2); translate(0,0.5,0)", m);
          MeshNode lowerBranchShape = new MeshNode("Cube(0)", cube);
        TransformNode translateToTop = new TransformNode("translate(0,4,0)",Mat4Transform.translate(0,4,0));
          NameNode upperBranch = new NameNode("upper branch");
            m = Mat4Transform.scale(1.4f,3.9f,1.4f);
            m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
            TransformNode upperBranchTransform = new TransformNode("scale(1.4f,3.9f,1.4f);translate(0,0.5,0)", m);
              MeshNode upperBranchShape = new MeshNode("Cube(1)", cube);
        
    twoBranch.addChild(lowerBranch);
      lowerBranch.addChild(lowerBranchTransform);
        lowerBranchTransform.addChild(lowerBranchShape);
      lowerBranch.addChild(translateToTop);
        translateToTop.addChild(upperBranch);
        upperBranch.addChild(upperBranchTransform);
        upperBranchTransform.addChild(upperBranchShape);
    twoBranch.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    // Following two lines can be used to check scene graph construction is correct
    //twoBranch.print(0, false);
    //System.exit(0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    updatePerspectiveMatrices(); 
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);  
    twoBranch.draw(gl);
  }
  
  private void updatePerspectiveMatrices() {
    // needs to be changed if user resizes the window
    perspective = Mat4Transform.perspective(45, aspect);
    light.setPerspective(perspective);
    floor.setPerspective(perspective);
    cube.setPerspective(perspective);
  }
  
  private void disposeMeshes(GL3 gl) {
    light.dispose(gl);
    floor.dispose(gl);
    cube.dispose(gl);
  }
  
  // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }
  
}