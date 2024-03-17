import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FindSimilarBusiness {
    private final JFrame container;
    private final JPanel inputWindow, outputWindow;
    private JPanel runnerUpBusiness, mostSimilarBusiness;
    private JButton submitButton;
    private JTextField inputField;

    public FindSimilarBusiness() {
        container = new JFrame("Yelp Recommendations");
        inputWindow = new JPanel();
        outputWindow = new JPanel();
        mostSimilarBusiness = new JPanel();
        runnerUpBusiness = new JPanel();
        container.setSize(800,500);
        setInputWindow();
        setOutputWindow();
        submitRequest();
        container.setVisible(true);
        container.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setOutputWindow() {
        outputWindow.setLayout(new GridLayout(1,2));
        JLabel title = new JLabel("The Most Similar Business is: ");
        mostSimilarBusiness.add(title);

        JLabel runnerUpTitle = new JLabel("The Runner Up Business is: ");
        runnerUpBusiness.add(runnerUpTitle);

        outputWindow.add(mostSimilarBusiness);
        outputWindow.add(runnerUpBusiness);
        container.add(outputWindow);
    }

    private void setInputWindow() {
        container.setLayout(new FlowLayout());
        submitButton = new JButton("Submit");
        JLabel userInstructions = new JLabel("Enter a business ID: ");
        inputField = new JTextField(20);
        inputWindow.add(userInstructions);
        inputWindow.add(inputField);
        inputWindow.add(submitButton);
        container.add(inputWindow);
    }

    private void submitRequest() {
        ActionListener submitRequest = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String businessID = inputField.getText();
                inputField.setText("");
                //HrIbP2-jdRJAU92yqyDmyw
                List<Map.Entry<String, Double>> output = null;
                try {
                    output = InfoRetrieval.tfIDF(businessID);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                updateOutputWindow(output, businessID);
            }
        };
        submitButton.addActionListener(submitRequest);
    }

    void updateOutputWindow(List<Map.Entry<String, Double>> businessList, String businessID) {
        double champion = 0.0;
        double runnerUp = 0.0;
        for (int i = 0; i < 3; i++) {
            System.out.println(businessList.get(i));
            if (businessList.get(i).getValue() < .999) {
                if (businessList.get(i).getValue() > champion) {
                    champion = businessList.get(i).getValue();
                    JLabel label = new JLabel(businessList.get(i).getKey() + "\n with a similarity score of: "
                            + businessList.get(i).getValue());
                    mostSimilarBusiness.add(label);
                } else {
                    runnerUp = businessList.get(i).getValue();
                    JLabel label = new JLabel(businessList.get(i).getKey() + "\n with a similarity score of: "
                            + businessList.get(i).getValue());
                    runnerUpBusiness.add(label);
                }
            }
        }
        container.revalidate();
        container.repaint();
    }

    public static void main(String[] args) {
        FindSimilarBusiness gui = new FindSimilarBusiness();
    }
}
