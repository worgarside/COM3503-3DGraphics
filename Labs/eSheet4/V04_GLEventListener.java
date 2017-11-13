import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;
  
public class V04_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shader;
  private float aspect;
    
  /* The constructor is not used to initialise anything */
  public V04_GLEventListener() {
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
    //gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    //gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    //gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
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
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
    gl.glDeleteBuffers(1, textureId1, 0);
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

  // texture id
  private int[] textureId1 = new int[1];

  public void initialise(GL3 gl) {
    shader = new Shader(gl, "v01_vs.txt", "v01_fs.txt");
    fillBuffers(gl);
    textureId1 = TextureLibrary.loadTexture(gl, "cube.jpg");
    createRandomNumbers();
  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    shader.use(gl);
    
    Mat4 perspective = Mat4Transform.perspective(45, aspect);
    Mat4 view = getViewMatrix();
    
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
  
    for (int i=0; i<100; ++i) {
      Mat4 model = getModelMatrix(i);
      Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(view, model));
      shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());    
      
      gl.glBindVertexArray(vertexArrayId[0]);
      gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
      gl.glBindVertexArray(0);
    }
  }
    
  private Mat4 getModelMatrix(int i) {
    double elapsedTime = getSeconds()-startTime;
    Mat4 model = new Mat4(1);    
    float yAngle = (float)(elapsedTime*100*randoms[(i+637)%NUM_RANDOMS]);
    float multiplier = 12.0f;
    float x = multiplier*randoms[i%NUM_RANDOMS] - multiplier*0.5f;
    float y = multiplier*randoms[(i+137)%NUM_RANDOMS] - multiplier*0.5f;
    float z = multiplier*randoms[(i+563)%NUM_RANDOMS] - multiplier*0.5f;
    model = Mat4.multiply(model, Mat4Transform.translate(x,y,z));
    model = Mat4.multiply(model, Mat4Transform.rotateAroundY(yAngle));
    return model;
  }
  
  private Mat4 getViewMatrix() {
    double elapsedTime = getSeconds()-startTime;
    float xposition = 4;
    float yposition = 5;
    float zposition = 12;
    Mat4 view = Mat4Transform.lookAt(new Vec3(xposition,yposition,zposition), new Vec3(0,0,0), new Vec3(0,1,0));
    return view;
  }

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  
  private float[] vertices = new float[] {  // x,y,z, colour, s,t
      -0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f,  0.0f, 0.67f,  // 0
      -0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 0.0f,  0.33f, 0.67f,  // 1
      -0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f,  0.0f, 1.0f,  // 2
      -0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,  0.33f, 1.0f,  // 3
       0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f,  1.0f, 0.33f,  // 4
       0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 0.0f,  0.67f, 0.33f,  // 5
       0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f,  1.0f, 0.67f,  // 6
       0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,  0.67f, 0.67f,  // 7

      -0.5f, -0.5f, -0.5f,  0.0f, 1.0f, 0.0f,  0.67f, 0.33f,  // 8+
      -0.5f, -0.5f,  0.5f,  0.0f, 1.0f, 0.0f,  0.33f, 0.67f,  // 9-
      -0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 0.0f,  0.67f, 0.67f,  // 10+
      -0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 0.0f,  0.33f, 1.0f,  // 11-
       0.5f, -0.5f, -0.5f,  0.0f, 1.0f, 0.0f,  0.33f, 0.33f,  // 12+
       0.5f, -0.5f,  0.5f,  0.0f, 1.0f, 0.0f,  0.67f, 0.67f,  // 13-
       0.5f,  0.5f, -0.5f,  0.0f, 1.0f, 0.0f,  0.33f, 0.67f,  // 14+
       0.5f,  0.5f,  0.5f,  0.0f, 1.0f, 0.0f,  0.67f, 1.0f,  // 15-

      -0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 1.0f,  0.67f, 0.67f,  // 16-
      -0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  0.67f, 1.0f,  // 17-
      -0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 1.0f,  0.0f, 0.67f,  // 18+
      -0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  0.0f, 0.33f,  // 19+
       0.5f, -0.5f, -0.5f,  0.0f, 0.0f, 1.0f,  1.0f, 0.67f,  // 20-
       0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  1.0f, 1.0f,  // 21-
       0.5f,  0.5f, -0.5f,  0.0f, 0.0f, 1.0f,  0.33f, 0.67f,  // 22+
       0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  0.33f, 0.33f   // 23+
  };
    
  private int[] indices =  new int[] {
      0,1,3, // x -ve 
      3,2,0, // x -ve
      4,6,7, // x +ve
      7,5,4, // x +ve
      9,13,15, // z +ve
      15,11,9, // z +ve
      8,10,14, // z -ve
      14,12,8, // z -ve
      16,20,21, // y -ve
      21,17,16, // y -ve
      23,22,18, // y +ve
      18,19,23  // y +ve
  };
  
  private int vertexStride = 8;
  private int vertexXYZFloats = 3;
  private int vertexColourFloats = 3;
  private int vertexTexFloats = 2;

  
  // ***************************************************
  /* THE BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];
    
  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
    
    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);
  
    int numColorFloats = vertexColourFloats; // red, green and blue values for each colour 
    offset = numXYZFloats*Float.BYTES;  // the colour values are three floats after the three x,y,z values
                                    // so change the offset value
    gl.glVertexAttribPointer(1, numColorFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
                                    // the vertex shader uses location 1 (sometimes called index 1)
                                    // for the colour information
                                    // location, size, type, normalize, stride, offset
                                    // offset is relative to the start of the array of data
    gl.glEnableVertexAttribArray(1);// Enable the vertex attribute array at location 1

    // now do the texture coordinates  in vertex attribute 2
    int numTexFloats = vertexTexFloats;
    offset = (numXYZFloats+numColorFloats)*Float.BYTES;
    gl.glVertexAttribPointer(2, numTexFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(2);
    
    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    gl.glBindVertexArray(0);
  }

}