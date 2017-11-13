import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Shader03_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  /* The constructor is not used to initialise anything */
  public Shader03_GLEventListener() {
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
    initialise(gl);
  startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
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
  }

  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  public void initialise(GL3 gl) {
    initialiseShader(gl);
    shaderProgram = compileAndLink(gl);
    fillBuffers(gl);
  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
  
    gl.glUseProgram(shaderProgram);
  
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  // ***************************************************
  /* THE DATA
   */
   
  private float[] vertices = {
     0.0f,  0.5f, 0.0f, 1.0f, 0.0f, 0.0f, // Top Middle, red
     0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Bottom Right, green
    -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f  // Bottom Left, blue
  };
  
  private int vertexStride = 6;
  private int vertexXYZFloats = 3;
  private int vertexColourFloats = 3;
  
  private int[] indices = {         // Note that we start from 0!
      0, 1, 2
  }; 
  
  // ***************************************************
  /* THE BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];
                                    // We now use an element buffer
    
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

    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    gl.glBindVertexArray(0);
  }

  
  // ***************************************************
  /* THE SHADER
   */
  
  private String vertexShaderSource = 
    "#version 330 core\n" +
    "\n" +
    "layout (location = 0) in vec3 position;\n" +
    "layout (location = 1) in vec3 color;\n" +
    "out vec3 ourColor;\n" +
    "\n" +
    "void main() {\n" +
    "  gl_Position = vec4(position, 1.0);\n" +
    "  ourColor = color;\n" +
    "}";

  private String fragmentShaderSource = 
    "#version 330 core\n" +
    "in vec3 ourColor;\n" +
    "out vec4 color;\n" +
    "\n" +
    "void main() {\n" +
    "  color = vec4(ourColor, 1.0f);\n" +
    "}";
  
  private int shaderProgram;

  private void initialiseShader(GL3 gl) {
    if (DISPLAY_SHADERS) {
      System.out.println("***Vertex shader***");
      System.out.println(vertexShaderSource);
      System.out.println("\n***Fragment shader***");
      System.out.println(fragmentShaderSource);
    }
  }
  
  private int compileAndLink(GL3 gl) {
    String[][] sources = new String[1][1];
    sources[0] = new String[]{ vertexShaderSource };
    ShaderCode vertexShaderCode = new ShaderCode(GL3.GL_VERTEX_SHADER, sources.length, sources);
    boolean compiled = vertexShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile vertex shader: " + sources);
    sources[0] = new String[]{ fragmentShaderSource };
    ShaderCode fragmentShaderCode = new ShaderCode(GL3.GL_FRAGMENT_SHADER, sources.length, sources);
    compiled = fragmentShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile fragment shader: " + sources);
    ShaderProgram program = new ShaderProgram();
    program.init(gl);
    program.add(vertexShaderCode);
    program.add(fragmentShaderCode);
    program.link(gl, System.out);
    if (!program.validateProgram(gl, System.out))
      System.err.println("[error] Unable to link program");
    return program.program();
  }

}