package com.automation.dtc.controllers;

import java.io.File;
import java.io.IOException;

import com.automation.dtc.inputsdata.ReadDtcTable;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class MainController {
	@FXML
    public AnchorPane anchor_pane, global_pane;
	
	@FXML
    private ImageView LOGO_VIEW, START_BTN_VIEW, SETTING_BTN_VIEW, ABOUT_BTN_VIEW;
	
	@FXML
    private Button START_BTN, SETTING_BTN, ABOUT_BTN;

    @FXML
    private AnchorPane contentPane;
    
    @FXML 
    private Text COPYRIGHT;

    @FXML
    public void handleImport() {
        ReadDtcTable readDtcTable = new ReadDtcTable();
        FileChooser fileChooser = new FileChooser();
    	ExtensionFilter excelFilter = new ExtensionFilter("Excel Files", "*.xlsx", "*.xls");
    	fileChooser.getExtensionFilters().add(excelFilter);
    	File file = fileChooser.showOpenDialog(null);
    	if (file == null) {
    		return;
    	}else {
    		readDtcTable.setRcdPath(file.getPath());
    	}
    }
    
    @FXML
    void START_BTN_CLICKED(ActionEvent event) throws Exception {
    	
    }
    
    @FXML
    void SETTING_BTN_CLICKED(ActionEvent event) throws Exception {
    	
    }
    
    @FXML
    void ABOUT_BTN_CLICKED(ActionEvent event) throws Exception {
    	
    }
    
//    @FXML
//    void ABOUT_BTN_CLICKED(ActionEvent event) throws Exception {
//    	Parent root = FXMLLoader.load(getClass().getResource("/application/Help.fxml"));
//    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//    	Scene scene = new Scene(root, 400,500);
//    	scene.getStylesheets().add(getClass().getResource("settings-view.css").toExternalForm());
//    	stage.setScene(scene);
//    	stage.show();
//    }

    @FXML
    public void handleRun() {
        System.out.println("Run clicked.");
        // Your logic here
    }
    
    private void loadView(String fxmlPath) {
        try {
        	// Parent allows loading any layout: VBox, AnchorPane, etc.
            Parent pane = FXMLLoader.load(getClass().getResource(fxmlPath));

            // Clear and replace content
            contentPane.getChildren().setAll(pane);

            // Make sure the loaded pane fills the AnchorPane
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
