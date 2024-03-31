module com.example.yelprecommendation {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.google.gson;

    opens com.example.yelprecommendation to javafx.fxml;
    exports com.example.yelprecommendation;
}