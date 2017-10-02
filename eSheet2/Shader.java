import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.glsl.*;  
  
public class Shader {
  
  private static final boolean DISPLAY_SHADERS = false;
  
  private int shaderProgram;
  private String vertexShaderSource;
  private String fragmentShaderSource;
  
  /* The constructor */
  public Shader(GL3 gl, String vertexPath, String fragmentPath) {
    try {
      vertexShaderSource = new String(Files.readAllBytes(Paths.get(vertexPath)), Charset.defaultCharset());
      fragmentShaderSource = new String(Files.readAllBytes(Paths.get(fragmentPath)), Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    if (DISPLAY_SHADERS) display();
    shaderProgram = compileAndLink(gl);
  }
  
  public void use(GL3 gl) {
    gl.glUseProgram(shaderProgram);
  }
  
  private void display() {
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