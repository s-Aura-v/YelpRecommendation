/*
import javax.swing.*;
import java.awt.*;

public class GUI2 {
    private JFrame mainWindow;
    private JPanel champion, runnerUp, inputWindow;

    public GUI2() {
        mainWindow = new JFrame("Yelp Recommendations");
        mainWindow.setLayout(new FlowLayout());
        champion = new JPanel();
        runnerUp = new JPanel();
        inputWindow = new JPanel();
        mainWindow.setSize(500,500);
        setLayout();
        mainWindow.setVisible(true);
    }

    void setLayout() {
        JLabel userInstructions = new JLabel("Enter a business ID: ");


        JButton submitButton = new JButton("Submit");


        JTextArea inputField = new JTextArea("");
        inputWindow.add(userInstructions);
        inputWindow.add(inputField);
        inputWindow.add(submitButton);


        mainWindow.add(champion);
        mainWindow.add(runnerUp);
        mainWindow.add(inputWindow);
    }

    public static void main(String[] args) {
        GUI2 gui = new GUI2();
    }
}

 */
