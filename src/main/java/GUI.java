import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.jar.JarEntry;

public class GUI {
    private JFrame frame;
    private JButton submitButton;
    private JButton closeButton;
    private JPanel container;
    private JPanel top;
    private JPanel results;
    private JLabel reccs;
    private JLabel info;
    private JTextField input;

    private int width;
    private int height;
    private String storedInput;

    // Place Holder
    private JPanel firstRecc;
    private JPanel secondRecc;
    private JLabel text1;
    private JLabel text2;

    public GUI(int w, int h) {
        frame = new JFrame();
        container = new JPanel();
        results = new JPanel(new GridLayout(1,2));
        firstRecc = new JPanel(new FlowLayout());
        secondRecc = new JPanel(new FlowLayout());
        top = new JPanel(new FlowLayout());

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        text1 = new JLabel("First Recommendation");
        text2 = new JLabel("Second Recommendation");

        submitButton = new JButton("Submit");
        closeButton = new JButton("Close");
        info = new JLabel("Enter something and we'll give you some recommendations!");
        reccs = new JLabel("Here are some recommendations!");
        input = new JTextField(15);
        this.width = w;
        this.height = h;
    }

    public void setUpGUI() {
        frame.setSize(width,height);
        frame.setTitle("GUI Demo");
        top.add(info);
        top.add(input);
        top.add(submitButton);
        top.add(closeButton);

        results.setBackground(Color.blue);

        results.add(firstRecc);
        results.add(secondRecc);

        firstRecc.add(text1);
        secondRecc.add(text2);

        container.add(top);
        container.add(results);
        frame.add(container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void storeString() {
        ActionListener submitRequest = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storedInput = input.getText();
                input.setText("");
                System.out.println(storedInput);
            }
        };
        submitButton.addActionListener(submitRequest);
    }

    private void updateRecs() {
        JLabel recc1 = new JLabel("Hello");
        results.add(recc1);
    }

    public String getStoredInput() {
        return storedInput;
    }

    public void setCloseButton() {
        ActionListener closeGUI = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        };
        closeButton.addActionListener(closeGUI);
    }
}


