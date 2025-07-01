package com.automation.dtc.controllers;

import java.io.IOException;

import com.automation.dtc.inputsdata.ReadDtcTable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class SettingController {

    @FXML private Button cancel_btn;
    @FXML private Spinner<Integer> spinner_1;
    @FXML private Button submit_btn;
    
    @FXML
    public void initialize() {
        spinner_1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, ReadDtcTable.startRow));
    }

    @FXML
    void cancel_btn_clicked(ActionEvent event) throws IOException {
    	ReadDtcTable.startRow = spinner_1.getValue();
    	Parent root = FXMLLoader.load(getClass().getResource("/view/home_view.fxml"));
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root, 650, 500);
    	scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    	stage.setScene(scene);
    	stage.show();
    }

    @FXML
    void submit_btn_clicked(ActionEvent event) throws IOException {
    	ReadDtcTable.startRow = spinner_1.getValue();
    	Parent root = FXMLLoader.load(getClass().getResource("/view/home_view.fxml"));
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root, 650, 500);
    	scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    	stage.setScene(scene);
    	stage.show();
    }
}
