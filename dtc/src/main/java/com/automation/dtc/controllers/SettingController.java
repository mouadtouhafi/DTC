package com.automation.dtc.controllers;

import com.automation.dtc.inputsdata.ReadDtcTable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class SettingController {

    @FXML private Button cancel_btn;
    @FXML private Spinner<Integer> spinner_1;
    @FXML private Button submit_btn;
    
    @FXML
    public void initialize() {
        spinner_1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 26));
    }

    @FXML
    void cancel_btn_clicked(ActionEvent event) {
    	
    }

    @FXML
    void submit_btn_clicked(ActionEvent event) {
    	ReadDtcTable.startRow = spinner_1.getValue();
    }

}
