package com.automation.dtc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
	@Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/home_view.fxml"));
        Scene scene = new Scene(root, 650, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon.png")));
        primaryStage.setTitle("DTC Automation App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
