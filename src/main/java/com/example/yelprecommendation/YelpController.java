package com.example.yelprecommendation;

import com.example.yelprecommendation.Classes.BTree;
import com.example.yelprecommendation.Classes.Business;
import com.example.yelprecommendation.Classes.InfoRetrieval;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.yelprecommendation.Classes.InfoRetrieval.geographicCluster;

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
    @FXML
    private Label destinationBusinessLabel;

    @FXML
    private ListView<String> destinationBusinessList;
    @FXML
    private Label startingBusinessLabel;

    @FXML
    private ListView<String> startingBusinessList;

    @FXML
    private Label pathDescLabel;

    @FXML
    private TextArea path;

    @FXML
    private Label validBusinessLabel;

    @FXML
    private ListView<String> validBusinessList;

    @FXML
    private LineChart<Number, Number> pathGraph;

    HashMap<String, Business> mapOfBusiness;



    public YelpController() {
        InfoRetrieval.hashTableSetup();
    }

    @FXML
    private void submitRequest(ActionEvent event) throws IOException {
        String businessName = inputButton.getText();
        System.out.println(businessName);
        String businessID = getIDFromName(businessName);
        System.out.println(businessID);
        mapOfBusiness = InfoRetrieval.tfIDF(businessID);
        updateRecommendations();
        updateClusters();
        setRoutes();
        findValidBusinessPath();
    }

    private String getIDFromName(String businessName) {
        BTree<String,String> tree = InfoRetrieval.getBusinessBTree();
        return tree.get(businessName);
    }

    private void updateRecommendations() {
        List<Map.Entry<String, Double>> sortedScores = InfoRetrieval.getSortedScores();
//        businessID.setText("ID: " + mapOfBusiness.get(sortedScores.get(1).getKey()).getId());
        businessName.setText("Business Name: \n" + sortedScores.get(1).getKey());
        businessScore.setText("businessScore: \n" + sortedScores.get(1).getValue());
        clusterLabel.setText("Cluster: " + mapOfBusiness.get(getIDFromName(sortedScores.get(1).getKey())).getCluster());

//        businessIDJr.setText("ID: " + mapOfBusiness.get(sortedScores.get(2).getKey()).getId());
        businessNameJr.setText("Business Name: \n" + sortedScores.get(2).getKey());
        businessScoreJr.setText("businessScore: \n" + sortedScores.get(2).getValue());
        clusterLabelJr.setText("Cluster: " + mapOfBusiness.get(getIDFromName(sortedScores.get(2).getKey())).getCluster());
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

    private void updateValidBusinessList(ArrayList<String> businessList) {
        for (String businessName : businessList) {
            validBusinessList.getItems().add(businessName);
        }
    }

    private void findValidBusinessPath() {
        validBusinessList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedItem = getIDFromName(validBusinessList.getSelectionModel().getSelectedItem());
                InfoRetrieval.geographicCluster(selectedItem);
                InfoRetrieval.createPaths(selectedItem);
                InfoRetrieval.findPath(getIDFromName(startingBusinessList.getSelectionModel().getSelectedItem()), selectedItem);
                path.setText(InfoRetrieval.findPath(getIDFromName(startingBusinessList.getSelectionModel().getSelectedItem()), selectedItem));
            }
        });
    }

    private void createPathGraph() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Latitude");
        yAxis.setLabel("Longitude");
        pathGraph = new LineChart<>(xAxis, yAxis);

        XYChart.Series pathNotTaken = new XYChart.Series();
        pathNotTaken.setName("Neighboring Business");
        XYChart.Series pathTaken = new XYChart.Series();
        pathTaken.setName("Path to Business");
    }


    /*
    * Sets the starting point after cosine similarity has been found.
    * Sets the destination based on the cluster that the selected valeus are in
     */
    private void setRoutes() {
        final String[] sourceID = new String[1];
        for (Business business : mapOfBusiness.values()) {
            startingBusinessList.getItems().add(business.getName());
        }
        startingBusinessList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedBusiness = startingBusinessList.getSelectionModel().getSelectedItem();
                String selectedBusinessID = getIDFromName(selectedBusiness);
                int selectedCluster = mapOfBusiness.get(selectedBusinessID).getCluster();
                destinationBusinessList.getItems().clear();
                for (Business business : mapOfBusiness.values()) {
                    if (business.getCluster() == selectedCluster) {
                        destinationBusinessList.getItems().add(business.getName());
                    }
                }
                sourceID[0] = selectedBusinessID;
                geographicCluster(selectedBusinessID);
                InfoRetrieval.createPaths(selectedBusinessID);
            }
        });
        destinationBusinessList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String destination = destinationBusinessList.getSelectionModel().getSelectedItem();
                String destinationID = getIDFromName(destination);
                // TODO: Debug Remove
//                String destinationID = "rZ4WHc8fcopYKCGi7ahwzA";
//                geographicCluster(destinationID);
                InfoRetrieval.businessCluster(destinationID);
                InfoRetrieval.geographicCluster(destinationID);
                InfoRetrieval.createPaths(destinationID);
                validBusinessList.getItems().clear();
                updateValidBusinessList(InfoRetrieval.allBusinessUnionFind(sourceID[0]));
                path.setText(InfoRetrieval.findPath(sourceID[0], destinationID));
            }
        });
    }

}