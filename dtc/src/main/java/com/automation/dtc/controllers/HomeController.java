package com.automation.dtc.controllers;

import java.io.IOException;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private Button about_btn;

    @FXML
    private Button settings_btn;

    @FXML
    private Button start_btn;

    @FXML
    void about_btn_clicked(ActionEvent event) {

    }

    @FXML
    void settings_btn_clicked(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("/view/settings_view.fxml"));
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root, 400,448);
    	
//    	scene.getStylesheets().add(getClass().getResource("darkMode.css").toExternalForm());
    	
    	
    	stage.setScene(scene);
    	stage.show();
    }

    @FXML
    void start_btn_clicked(ActionEvent event) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("/view/imports_view.fxml"));
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root, 650,500);
    	scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    	stage.setScene(scene);
    	stage.show();
    }

}
