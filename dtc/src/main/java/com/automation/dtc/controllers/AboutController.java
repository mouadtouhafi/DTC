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

public class AboutController {

	@FXML private Button cancel_btn;
	
	@FXML private void cancel_btn_clicked(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/view/home_view.fxml"));
    	Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root, 650, 500);
    	scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    	stage.setScene(scene);
    	stage.show();
	}
}
