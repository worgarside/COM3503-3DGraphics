import com.jogamp.opengl.*;
import gmaths.*;

/**
 * Keyframe.java
 * Object for storing values for a RobotHand Keyframe
 *
 * @author Will Garside // worgarside@gmail.com
 * @version 1.0 2017-12-06
 */
public class Keyframe {

    String name;
    int[][] prmAngles = new int[RobotHand.DIGIT_COUNT][RobotHand.PHALANGE_COUNT];
    int[] secAngles = new int[RobotHand.DIGIT_COUNT];

    /**
     * Constructor for Keyframe object
     *
     * @param name - the name of the keyframe
     * @param prmAngles - primary angles of the digits
     * @param secAngles - secondary angles of the digits
     */
    public Keyframe(String name, int[][] prmAngles, int[] secAngles) {
        this.name = name;
        this.prmAngles = prmAngles;
        this.secAngles = secAngles;
    }

    /**
     * Allows a Keyframe to be represented as a String for debugging and testing
     *
     * @return Keyframe as String
     */
    public String toString() {
        String prmAnglesString = "";
        String secAnglesString = "";

        for (int i = 0; i < RobotHand.DIGIT_COUNT; i++) {
            for (int j = 0; j < RobotHand.PHALANGE_COUNT; j++) {
                prmAnglesString += Integer.toString(prmAngles[i][j]) + ", ";
            }
            secAnglesString += Integer.toString(secAngles[i]) + ", ";
        }

        return "Keyframe: " + name + ", " + prmAnglesString + secAnglesString;
    }

    // ------------ Getters ------------ \\

    /**
     * Getter for Keyframe name
     *
     * @return Keyframe name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for all primary angles
     *
     * @return primary angles as an int[][]
     */
    public int[][] getPrmAngles() {
        return prmAngles;
    }

    /**
     * Getter for all secondary angles
     *
     * @return secondary angles as an int[][]
     */
    public int[] getSecAngles() {
        return secAngles;
    }
}