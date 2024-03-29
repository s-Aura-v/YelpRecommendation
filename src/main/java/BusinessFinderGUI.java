import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BusinessFinderGUI {
    private JFrame container;
    private JPanel title;
    private JLabel topMenu;
    private JPanel westUserInput;
    private JScrollBar scrollBar1;
    private JTextField textField1;

    private JPanel output;

    public BusinessFinderGUI() {
        container = new JFrame("Business Finder GUI");
        title = new JPanel();
        westUserInput = new JPanel();
        output = new JPanel();


        createUserInput();
        createMenu();
        createOutput();
        container.setSize(550,450);
        container.setLayout(new BorderLayout());
        container.add(title, BorderLayout.NORTH);
        container.add(westUserInput, BorderLayout.WEST);
        container.add(output, BorderLayout.CENTER);

        container.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        container.setVisible(true);
    }

    private void createOutput() {
        JTextArea output = new JTextArea(20,20);
        this.output.add(output);
    }

    private void createUserInput() {
        westUserInput.setLayout(new GridLayout(2,1));
        JPanel userInputPanel = new JPanel();
        userInputPanel.setLayout(new GridLayout(3,1));
        JPanel businessListPanel = new JPanel();

        westUserInput.setSize(200,200);
        JLabel info = new JLabel("Enter a business ID:");
        JTextField businessID = new JTextField(16);
        JButton submitBtn = new JButton("Submit Business ID");
        userInputPanel.add(info);
        userInputPanel.add(businessID);
        userInputPanel.add(submitBtn);
        JScrollPane businessList = new JScrollPane();
        for (int i = 0; i < 20; i++) {
            businessList.add(new JLabel("i"));
        }
        JLabel label = new JLabel("hi");
        businessListPanel.add(label);
        businessListPanel.add(businessList);

        westUserInput.add(userInputPanel);
        westUserInput.add(businessListPanel);
    }



    private void createMenu() {
        JLabel info = new JLabel("Input a business ID and get the two most similar Businessess!");
        title.add(info);
    }

    public static void main(String[] args) {
        BusinessFinderGUI gui = new BusinessFinderGUI();
    }

}
