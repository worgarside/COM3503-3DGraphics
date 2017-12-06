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
    private static final String KEYFRAME_DATA_FILE = "keyframes.csv";
    static ArrayList<float[]> lightData = new ArrayList<float[]>();
    static ArrayList<Keyframe> keyframes = new ArrayList<Keyframe>();
    static int lightCount = 0;
    static Keyframe neutralKeyframe;
    private static JPanel panel = new JPanel();
    private static RotatedIcon clockRot;
    public static boolean night = false;
    private ArrayList<JLabel> labelList = new ArrayList<JLabel>();

    public static void main(String[] args) {
        readLightData();
        readKeyframeData();
        Arty arty = new Arty("COM3503 - Robot Hand");
        arty.getContentPane().setPreferredSize(dimension);
        arty.pack();
        arty.setVisible(true);
    }

    public Arty(String textForTitleBar) {
        super(textForTitleBar);
        GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        glcapabilities.setSampleBuffers(true);
        glcapabilities.setNumSamples(4);

        canvas = new GLCanvas(glcapabilities);
        camera = new Camera(new Vec3(-4f, 12f, 24f), new Vec3(0f, 8f, 0f), Camera.DEFAULT_UP);
        glEventListener = new Arty_GLEventListener(camera);
        canvas.addGLEventListener(glEventListener);
        canvas.addMouseMotionListener(new MyMouseInput(camera));
        canvas.addKeyListener(new MyKeyboardInput(camera));
        getContentPane().add(canvas, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(WIDTH, 100));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        createClockFace(panel, gbc);

        createControls(panel, gbc);

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

    ChangeListener sliderListener = new ChangeListener() {
        public void stateChanged(ChangeEvent event) {
            JSlider source = (JSlider) event.getSource();
            glEventListener.rotArmToAngle(source.getValue());
        }
    };

    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < keyframes.size(); i++) {
            if (e.getActionCommand().equalsIgnoreCase(keyframes.get(i).getName())) {
                glEventListener.changeHandPos(i);
            }
        }

        switch (e.getActionCommand().toLowerCase()) {
            case "lamps":
                glEventListener.toggleLamps();
                break;
            case "world light":
                glEventListener.toggleWorldLight();
                break;
            case "keyframe sequence":
                glEventListener.toggleKeyframeSequence();
                break;
            case "all animations":
                glEventListener.toggleGlobalAnims();
                break;
            case "day/night cycle":
                glEventListener.toggleDayNight();
                break;
            case "exit":
                System.exit(0);
                break;
        }
    }

    private static void readLightData() {
        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader(LIGHT_DATA_FILE));

            String line = "";
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] dataString = line.split(CSV_DELIM);
                if(dataString.length > 0 ) {
                    float[] dataFloats = new float[dataString.length];
                    for (int i = 0; i < dataString.length; i++) {
                        dataFloats[i] = Float.parseFloat(dataString[i]);
                    }
                    lightData.add(dataFloats);
                    lightCount ++;
                }
            }
        } catch(Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                br.close();
            } catch(IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    private static void readKeyframeData() {
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
                    if (keyframeName.equalsIgnoreCase("neutral")) {
                        neutralKeyframe = new Keyframe(keyframeName, prmAngles, secAngles);
                    }
                }
            }
        } catch(Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                br.close();
            } catch(IOException ie) {
                System.out.println("Error occured while closing the BufferedReader");
                ie.printStackTrace();
            }
        }
    }

    private static void createClockFace(JPanel panel, GridBagConstraints gbc) {
        JPanel clockPanel = new JPanel();
        LayoutManager overlay = new OverlayLayout(clockPanel);
        clockPanel.setLayout(overlay);
        clockPanel.setPreferredSize(new Dimension(90, 90));

        JLabel clockFace = new JLabel(new ImageIcon("textures/clockFace.png"));
        clockFace.setLayout(new BorderLayout());
        clockPanel.add(clockFace);

        ImageIcon clockIcon = new ImageIcon("textures/pointer.png");

        clockRot = new RotatedIcon(clockIcon, 0);
        JLabel clockLabel = new JLabel("", clockRot, JLabel.CENTER);

        clockFace.add(clockLabel);
        clockPanel.add(clockFace, BorderLayout.CENTER );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panel.add(clockPanel, gbc);
    }

    private void createControls(JPanel panel, GridBagConstraints gbc) {
        // Create buttons for each keyframe defined in KEYFRAME_DATA_FILE
        JButton btn = new JButton();
        JLabel posLabel  = new JLabel("Positions");
        JLabel armLabel  = new JLabel("Arm Bearing");
        JLabel toggleLabel = new JLabel("Toggle Controls");
        labelList.add(posLabel);
        labelList.add(armLabel);
        labelList.add(toggleLabel);

        for (JLabel label : labelList) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JPanel posBtnPanel = new JPanel();
        for (int i = 0; i < keyframes.size(); i++) {
            btn = new JButton(keyframes.get(i).getName());
            btn.addActionListener(this);
            posBtnPanel.add(btn);
        }

        // Controls for rotating the entire model
        JSlider armAngleSlider = new JSlider(JSlider.HORIZONTAL, 0, 720, 0);
        armAngleSlider.setPreferredSize(new Dimension(125, 25));
        armAngleSlider.addChangeListener(sliderListener);
        JPanel sliderPanel = new JPanel();
        sliderPanel.add(armAngleSlider);

        JPanel toggleBtnPanel = new JPanel();
        btn = new JButton("Lamps");
        btn.addActionListener(this);
        toggleBtnPanel.add(btn);
        btn = new JButton("World Light");
        btn.addActionListener(this);
        toggleBtnPanel.add(btn);
        btn = new JButton("Keyframe Sequence");
        btn.addActionListener(this);
        toggleBtnPanel.add(btn);
        btn = new JButton("All Animations");
        btn.addActionListener(this);
        toggleBtnPanel.add(btn);
        btn = new JButton("Day/Night Cycle");
        btn.addActionListener(this);
        toggleBtnPanel.add(btn);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(posLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(armLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        panel.add(toggleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(posBtnPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(sliderPanel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        panel.add(toggleBtnPanel, gbc);

        btn = new JButton("Exit");
        btn.addActionListener(this);
        panel.add(btn);
    }

    public static void updateClock(int seconds) {
        clockRot.setDegrees(seconds*6);
        panel.repaint();
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
        if (e.getModifiers()==MouseEvent.BUTTON1_MASK) {
            camera.updateYawPitch(dx, -dy);
        }
        lastpoint = ms;
    }

    public void mouseMoved(MouseEvent e) {
        lastpoint = e.getPoint();
    }
}