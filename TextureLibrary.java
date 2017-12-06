import java.io.File;
import java.io.FileInputStream;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

/**
 * TextureLibrary.java
 * A class for import RGB jgep files to the JOGL environment
 *
 * @author Dr. Steve Maddock
 * @version 1.0 2017-12-06
 */
public final class TextureLibrary {

    /**
     * Loads texture into JOGL environment
     *
     * @param gl - graphics library
     * @param filename - the name of the jpeg file
     * @return - then texture ID as an int[]
     */
    public static int[] loadTexture(GL3 gl, String filename) {
        return loadTexture(gl, filename, GL.GL_REPEAT, GL.GL_REPEAT, GL.GL_LINEAR, GL.GL_LINEAR);
    }

    /**
     * Stores a jpeg file in the JOGL environment for later reference by the texture ID
     *
     * @param gl - graphics library
     * @param filename - the name of the jpeg file
     * @param wrappingS
     * @param wrappingT
     * @param filterS
     * @param filterT
     * @return - the ID of the texture now stored in the library
     */
    public static int[] loadTexture(GL3 gl, String filename,
                                    int wrappingS, int wrappingT, int filterS, int filterT) {
        int[] textureId = new int[1];
        try {
            File f = new File(filename);
            JPEGImage img = JPEGImage.read(new FileInputStream(f));
            gl.glGenTextures(1, textureId, 0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrappingS);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrappingT);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, filterS);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, filterT);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, img.getWidth(), img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getData());
            gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        }
        catch(Exception e) {
            System.out.println("Error loading texture " + filename + ": " + e);
        }
        return textureId;
    }

}