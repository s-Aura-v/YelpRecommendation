package com.example.yelprecommendation;

import com.example.yelprecommendation.Classes.BTree;
import com.example.yelprecommendation.Classes.Business;
import com.example.yelprecommendation.Classes.InfoRetrieval;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;
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
    private Label clusterFourLabel;

    @FXML
    private ListView<String> clusterFourList;

    @FXML
    private Label clusterLabel;

    @FXML
    private Label clusterLabelJr;

    @FXML
    private Label clusterOneLabel;

    @FXML
    private ListView<String> clusterOneList;

    @FXML
    private Label clusterThreeLabel;

    @FXML
    private ListView<String> clusterThreeList;

    @FXML
    private Label clusterTwoLabel;

    @FXML
    private ListView<String> clusterTwoList;

    @FXML
    private Label clusterZeroLabel;

    @FXML
    private ListView<String> clusterZeroList;

    @FXML
    private Button displayClusterInfo;

    @FXML
    private Button displayClusters;

    @FXML
    private Button displayGraph;

    @FXML
    private Text functionLabel;

    @FXML
    private TextField inputButton;

    @FXML
    private Text inputLabel;

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
    HashMap<String, Business> mapOfBusiness;

    public YelpController() {
        InfoRetrieval.hashTableSetup();
    }

    @FXML
    private void submitRequest(ActionEvent event) throws IOException {
        String businessName = inputButton.getText();
        System.out.println(businessName);
        String businessID = getNameFromID(businessName);
        System.out.println(businessID);
        mapOfBusiness = InfoRetrieval.tfIDF(businessID);
        updateRecommendations();
        updateClusters();
    }

    private String getNameFromID(String businessName) {
        BTree<String,String> tree = InfoRetrieval.getBusinessBTree();
        return tree.get(businessName);
    }

    private void updateRecommendations() {
        List<Map.Entry<String, Double>> sortedScores = InfoRetrieval.getSortedScores();
//        businessID.setText("ID: " + mapOfBusiness.get(sortedScores.get(1).getKey()).getId());
        businessName.setText("Business Name: \n" + sortedScores.get(1).getKey());
        businessScore.setText("businessScore: \n" + sortedScores.get(1).getValue());
//        clusterLabel.setText("Cluster: " + mapOfBusiness.get(sortedScores.get(1).getKey()).getCluster());

//        businessIDJr.setText("ID: " + mapOfBusiness.get(sortedScores.get(2).getKey()).getId());
        businessNameJr.setText("Business Name: \n" + sortedScores.get(2).getKey());
        businessScoreJr.setText("businessScore: \n" + sortedScores.get(2).getValue());
//        clusterLabelJr.setText("Cluster: " + mapOfBusiness.get(sortedScores.get(2).getKey()).getCluster());
    }

    private void updateClusters() {
        for (Business business : mapOfBusiness.values()) {
            switch (business.getCluster()) {
                case 0 -> clusterZeroList.getItems().add(business.getName());
                case 1 -> clusterOneList.getItems().add(business.getName());
                case 2 -> clusterTwoList.getItems().add(business.getName());
                case 3 -> clusterThreeList.getItems().add(business.getName());
                case 4 -> clusterFourList.getItems().add(business.getName());
            }
        }
    }
}