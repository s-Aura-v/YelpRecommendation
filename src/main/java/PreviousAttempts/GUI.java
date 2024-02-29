/*
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

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

    private String storedInput;

    // Place Holder
    private JPanel firstRecc;
    private JPanel secondRecc;
    private JLabel text1;
    private JLabel text2;

    public GUI() {
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

        setUpGUI();
        setCloseButton();
        storeString();
    }

    public void setUpGUI() {
        frame.setSize(500,400);
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
                System.out.println(storedInput);
                input.setText("");
                list output = InfoRetrieval.tfIDF(storedInput);
                updateRecs(output);
                System.out.println(output);
                //HrIbP2-jdRJAU92yqyDmyw
            }
        };
        submitButton.addActionListener(submitRequest);
    }

    private void updateRecs(HashMap<String, Double> recs) {
        JLabel rec1 = new JLabel("First Recommendation");
        JLabel rec2 = new JLabel("Second Recommendation");
        // Sort the HashMap by values to get the top recommendations
        int i = 0;
        for (String word : recs.keySet()) {
            if (recs.get(word) < .9) {
                if (i == 0) {
                    rec1.setText(recs.get(word) + ": " + word);
                } else if (i == 1) {
                    rec2.setText(recs.get(word) + ": " + word);
                    break; // We only need top two recommendations
                }
                i++;
            }

        }
        // Add recommendations to the panels
        firstRecc.add(rec1);
        secondRecc.add(rec2);
        // Refresh GUI
        frame.revalidate();
        frame.repaint();
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


    public static void main(String[] args) {
        GUI gui = new GUI();
    }
}

 */




