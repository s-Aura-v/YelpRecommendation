package com.example.yelprecommendation;

import com.example.yelprecommendation.Classes.InfoRetrieval;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class YelpController {
    @FXML
    private Label businessID;

    @FXML
    private Label businessIDJr;

    @FXML
    private Label businessName;

    @FXML
    private Label businessNameJr;

    @FXML
    private Label businessScore;

    @FXML
    private Label businessScoreJr;

    @FXML
    private Button clusterButton;

    @FXML
    private Label clusterFourLabel;

    @FXML
    private ListView<?> clusterFourList;

    @FXML
    private Label clusterOneLabel;

    @FXML
    private ListView<?> clusterOneList;

    @FXML
    private Label clusterThreeLabel;

    @FXML
    private ListView<?> clusterThreeList;

    @FXML
    private Label clusterTwoLabel;

    @FXML
    private ListView<?> clusterTwoList;

    @FXML
    private Label clusterZeroLabel;

    @FXML
    private ListView<String> clusterZeroList;

    @FXML
    private Text functionLabel;

    @FXML
    private Button graphButton;

    @FXML
    private Text inputLabel;

    @FXML
    private TextField inputTextField;

    @FXML
    private Text recommendationLabel;

    @FXML
    private Text recommendationLabel2;

    @FXML
    private Button submitUserInput;

    @FXML
    private AnchorPane textOutputAnchor;

    @FXML
    private TitledPane title;

    @FXML
    private AnchorPane userInputAnchor;

    public YelpController() {
    }

    @FXML
    private void submitRequest(ActionEvent event) {
        String businessName = inputTextField.getText();
        List<Map.Entry<String, Double>> output;
        try {
            output = InfoRetrieval.tfIDF(businessName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateRecommendations(output);
        updateClusters();
    }

    private void updateRecommendations(List<Map.Entry<String, Double>> output) {
        businessID.setText("ID: none");
        businessName.setText("Business Name: none");
        businessScore.setText("businessScore: none");

        businessIDJr.setText("ID: 2none");
        businessNameJr.setText("Business Name: 2none");
        businessScoreJr.setText("businessScore: 2none");
    }

    private void updateClusters() {
        clusterZeroList.getItems().clear();
        clusterZeroList.getItems().add("Item 1");
        clusterZeroList.getItems().add("Item 2");
        clusterZeroList.getItems().add("Item 3");
    }
}