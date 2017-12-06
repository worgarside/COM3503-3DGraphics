import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Keyframe {

    String name;
    int[][] prmAngles = new int[RobotHand.DIGIT_COUNT][RobotHand.PHALANGE_COUNT];
    int[] secAngles = new int[RobotHand.DIGIT_COUNT];

    public Keyframe(String name, int[][] prmAngles, int[] secAngles) {
        this.name = name;
        this.prmAngles = prmAngles;
        this.secAngles = secAngles;
    }

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

    public String getName() {
        return name;
    }

    public int[][] getPrmAngles() {
        return prmAngles;
    }

    public int[] getSecAngles() {
        return secAngles;
    }
}