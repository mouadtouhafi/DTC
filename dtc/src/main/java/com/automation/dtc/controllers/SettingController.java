package com.automation.dtc.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class SettingController {

    @FXML
    private Button cancel_btn;

    @FXML
    private Spinner<Integer> spinner_1;


    @FXML
    private Button submit_btn;
    
    @FXML
    public void initialize() {
        // Set value factory: min = 0, max = 100, initial = 10, step = 1
        spinner_1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 26));
    }

    @FXML
    void cancel_btn_clicked(ActionEvent event) {
    	
    }

    @FXML
    void submit_btn_clicked(ActionEvent event) {

    }

}
