package com.automation.dtc.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ErrorController {
	@FXML private ImageView progressImage;
	@FXML private Label label;
	@FXML private Button home_btn;
	
	@FXML private void home_btn_clicked(ActionEvent e) {}
	
    public void setErrorMessage(String message) {
    	label.setText(message);
    }
}
