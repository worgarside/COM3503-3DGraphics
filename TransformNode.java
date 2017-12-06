import gmaths.*;
import com.jogamp.opengl.*;

/**
 * TransformNode.java
 * A class for creating Transformation Nodes in the scene graph
 *
 * @author Dr. Steve Maddock
 * @version 1.0 2017-12-06
 */
public class TransformNode extends SGNode {

    private Mat4 transform;

    /**
     * Constructor for the TransformNode
     *
     * @param name - name of the Node
     * @param t - the Transformation Matrix as a Mat4
     */
    public TransformNode(String name, Mat4 t) {
        super(name);
        transform = new Mat4(t);
    }

    /**
     * Sets the Transform Node to the passed matrix
     *
     * @param m - matrix to set the Transform Node to
     */
    public void setTransform(Mat4 m) {
        transform = new Mat4(m);
    }

    /**
     * Updates the worldTransform matrix to match the passed matrix
     *
     * @param t - the new Matrix
     */
    protected void update(Mat4 t) {
        worldTransform = t;
        t = Mat4.multiply(worldTransform, transform);
        for (int i = 0; i < children.size(); i++) {
            children.get(i).update(t);
        }
    }

    /**
     * Custom function to print a Transform Node clearly
     *
     * @param indent - indent size
     * @param inFull - flag to set print verbosity
     */
    public void print(int indent, boolean inFull) {
        if (inFull) {
            System.out.println("worldTransform");
            System.out.println(worldTransform);
            System.out.println("transform node:");
            System.out.println(transform);
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).print(indent + 1, inFull);
        }
    }

    /**
     * Standard function for converting Transform Node to String
     *
     * @return - Transform Node as String
     */
    public String toString() {
        return transform.toString();
    }

    /**
     * Gets the Transform Node as a Matrix
     *
     * @return - Transform Node as a Mat4
     */
    public Mat4 getMat4() {
        return transform;
    }

}