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
import java.util.Arrays;

public class Arty extends JFrame implements ActionListener {

    private static final int WIDTH = 1080;
    private static final int HEIGHT = 920;
    private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
    private GLCanvas canvas;
    private Arty_GLEventListener glEventListener;
    private final FPSAnimator animator;
    private Camera camera;

    private static final String CSV_DELIM = ",";
    private static final String LIGHT_DATA_FILE = "lightData.csv";
    private static final int LIGHT_DATA_COUNT = 18;
    static ArrayList<float[]> lightData = new ArrayList<float[]>();
    static int lightCount = 0;

    private static final String KEYFRAME_DATA_FILE = "keyframes.csv";
    static ArrayList<Keyframe> keyframes = new ArrayList<Keyframe>();



    public static void main(String[] args) {
        Arty b1 = new Arty("COM3503 - Robot Hand");
        b1.getContentPane().setPreferredSize(dimension);
        b1.pack();
        b1.setVisible(true);
        readLightData();
        readKeyframeData();
        for(int i = 0; i < keyframes.size(); i++) {
            System.out.println(keyframes.get(i));
        }
    }

    public Arty(String textForTitleBar) {
        super(textForTitleBar);
        GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        canvas = new GLCanvas(glcapabilities);
        camera = new Camera(new Vec3(-4f,12f,24f), new Vec3(0f,8f,0f), Camera.DEFAULT_UP);
        glEventListener = new Arty_GLEventListener(camera);
        canvas.addGLEventListener(glEventListener);
        canvas.addMouseMotionListener(new MyMouseInput(camera));
        canvas.addKeyListener(new MyKeyboardInput(camera));
        getContentPane().add(canvas, BorderLayout.CENTER);

        JMenuBar menuBar=new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
        menuBar.add(fileMenu);

        JPanel panel = new JPanel();
        JButton btn = new JButton("Neutral");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("aslW");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("aslI");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("aslL");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("Custom");
        btn.addActionListener(this);
        panel.add(btn);
        btn = new JButton("Toggle Lamps");
        btn.addActionListener(this);
        panel.add(btn);
        JSlider armAngleSlider = new JSlider(JSlider.HORIZONTAL, 0, 720, 0);
        armAngleSlider.addChangeListener(sliderListener);
        panel.add(armAngleSlider);
        this.add(panel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                animator.stop();
                remove(canvas);
                dispose();
                System.exit(0);
            }
        });
        animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    ChangeListener sliderListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event)
        {
            JSlider source = (JSlider) event.getSource();
            glEventListener.rotArmToAngle(source.getValue());
        }
    };

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("camera X")) {
            camera.setCamera(Camera.CameraType.X);
            canvas.requestFocusInWindow();
        }
        else if (e.getActionCommand().equalsIgnoreCase("camera Z")) {
            camera.setCamera(Camera.CameraType.Z);
            canvas.requestFocusInWindow();
        }
        else if (e.getActionCommand().equalsIgnoreCase("Neutral")) {
            glEventListener.changeHandPos('N');
        }
        else if (e.getActionCommand().equalsIgnoreCase("aslW")) {
            glEventListener.changeHandPos('W');
        }
        else if (e.getActionCommand().equalsIgnoreCase("aslI")) {
            glEventListener.changeHandPos('I');
        }
        else if (e.getActionCommand().equalsIgnoreCase("aslL")) {
            glEventListener.changeHandPos('L');
        }
        else if (e.getActionCommand().equalsIgnoreCase("Custom")) {
            glEventListener.changeHandPos('P');
        }
        else if (e.getActionCommand().equalsIgnoreCase("Toggle Lamps")) {
            glEventListener.toggleLamps();
        }
        else if(e.getActionCommand().equalsIgnoreCase("quit")){
            System.exit(0);
        }
    }

    public static void readLightData() {
        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader(LIGHT_DATA_FILE));

            String line = "";
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] dataString = line.split(CSV_DELIM);
                if(dataString.length > 0 ) {
                    float[] dataFloats = new float[LIGHT_DATA_COUNT];
                    for (int i = 0; i < LIGHT_DATA_COUNT; i++) {
                        dataFloats[i] = Float.parseFloat(dataString[i]);
                    }
                    lightData.add(dataFloats);
                    lightCount ++;
                    System.out.print(".");
                }
            }
            System.out.println("!");
        } catch(Exception ee) {
            ee.printStackTrace();
        }
        finally {
            try {
                br.close();
            } catch(IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    public static void readKeyframeData() {
        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader(KEYFRAME_DATA_FILE));

            String line = "";
            br.readLine();

            while ((line = br.readLine()) != null) {

                String[] dataString = line.split(CSV_DELIM);

                if(dataString.length > 0 ) {
                    String keyframeName = dataString[0];

                    String[] prmAnglesSection = Arrays.copyOfRange(dataString, 1, 16);
                    String[] secAnglesSection = Arrays.copyOfRange(dataString, 16, 21);

                    int[][] prmAngles = new int[RobotHand.DIGIT_COUNT][RobotHand.PHALANGE_COUNT];
                    int[] secAngles = new int[RobotHand.DIGIT_COUNT];

                    int i = 0;
                    for (int j = 0; j < RobotHand.DIGIT_COUNT; j++) {
                        for (int k = 0; k < RobotHand.PHALANGE_COUNT; k++) {
                            prmAngles[j][k] = Integer.parseInt(prmAnglesSection[i++]);
                        }
                    }

                    for (int j = 0; j < secAnglesSection.length; j++) {
                        secAngles[j] = Integer.parseInt(secAnglesSection[j]);
                    }

                    keyframes.add(new Keyframe(keyframeName, prmAngles, secAngles));
                    System.out.print(".");
                }
            }
            System.out.println("!");
        } catch(Exception ee) {
            ee.printStackTrace();
        }
        finally {
            try {
                br.close();
            } catch(IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }
}

class MyKeyboardInput extends KeyAdapter  {
    private Camera camera;

    public MyKeyboardInput(Camera camera) {
        this.camera = camera;
    }

    public void keyPressed(KeyEvent e) {
        Camera.Movement m = Camera.Movement.NO_MOVEMENT;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
            case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
            case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
            case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
            case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
            case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
            case KeyEvent.VK_ESCAPE:  System.exit(0);  break;
        }
        camera.keyboardInput(m);
    }
}

class MyMouseInput extends MouseMotionAdapter {
    private Point lastpoint;
    private Camera camera;

    public MyMouseInput(Camera camera) {
        this.camera = camera;
    }

    public void mouseDragged(MouseEvent e) {
        Point ms = e.getPoint();
        float sensitivity = 0.001f;
        float dx=(float) (ms.x-lastpoint.x)*sensitivity;
        float dy=(float) (ms.y-lastpoint.y)*sensitivity;
        //System.out.println("dy,dy: "+dx+","+dy);
        if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
            camera.updateYawPitch(dx, -dy);
        lastpoint = ms;
    }

    public void mouseMoved(MouseEvent e) {
        lastpoint = e.getPoint();
    }
}