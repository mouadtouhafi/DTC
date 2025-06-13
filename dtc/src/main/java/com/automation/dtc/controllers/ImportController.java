package com.automation.dtc.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.automation.dtc.blockbuilder.BuildDIAGXmlBlock;
import com.automation.dtc.inputsdata.ReadDtcTable;
import com.automation.dtc.inputsdata.ReadXMLDiag;
import com.automation.dtc.inputsdata.ReadXMLMessaging;
import com.automation.dtc.xmlsfiles.FilesPaths;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportController {

    @FXML
    private Button DSD_btn;

    @FXML
    private TextField DSD_input;

    @FXML
    private Button DiagObject_btn;

    @FXML
    private TextField DiagObject_input;

    @FXML
    private Button RCD_btn;

    @FXML
    private TextField RCD_input;

    @FXML
    private Button validate_import_btn;
    
    public static FilesPaths filesPaths = new FilesPaths();
    public static ReadDtcTable readDtcTable = new ReadDtcTable();
    public static BuildDIAGXmlBlock buildDiag = new BuildDIAGXmlBlock();
    ReadXMLMessaging readXMLMessaging = new ReadXMLMessaging();

    @FXML
    void DSD_btn_clicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
    	ExtensionFilter excelFilter = new ExtensionFilter("DSD Files", "*.xml");
    	fileChooser.getExtensionFilters().add(excelFilter);
    	File file = fileChooser.showOpenDialog(null);
    	if (file == null) {
    		return;
    	}else {
    	    filesPaths.setDsd_files(file.getPath());
    		DSD_input.setText(file.getName());
    		DSD_input.setStyle("-fx-text-fill: green; -fx-opacity: 1; -fx-font-size: 10px;");
    	}
    }

    @FXML
    void DiagObject_btn_clicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
    	ExtensionFilter excelFilter = new ExtensionFilter("Diag Files", "*.xml");
    	fileChooser.getExtensionFilters().add(excelFilter);
    	File file = fileChooser.showOpenDialog(null);
    	if (file == null) {
    		return;
    	}else {
    	    filesPaths.setDiag_files(file.getPath());
    		DiagObject_input.setText(file.getName());
    		DiagObject_input.setStyle("-fx-text-fill: green; -fx-opacity: 1; -fx-font-size: 10px;");
    	}
    }

    @FXML
    void RCD_btn_clicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
    	ExtensionFilter excelFilter = new ExtensionFilter("Excel Files", "*.xlsx", "*.xls");
    	fileChooser.getExtensionFilters().add(excelFilter);
    	File file = fileChooser.showOpenDialog(null);
    	if (file == null) {
    		return;
    	}else {
    		readDtcTable.setRcdPath(file.getPath());
    		RCD_input.setText(file.getName());
    		RCD_input.setStyle("-fx-text-fill: green; -fx-opacity: 1; -fx-font-size: 10px;");
    	}
    }
 
    @FXML
    void validate_import_btn_clicked(ActionEvent event) throws Exception {
        if (!isFilesChecked()) {
        	Alert alert = new Alert(Alert.AlertType.WARNING);
        	alert.setTitle("Missing Files");
        	alert.setHeaderText(null);
        	alert.setContentText("Please import all required files (RCD, DSD, and DiagObject) before proceeding.");
        	DialogPane pane = alert.getDialogPane();
        	pane.setMinWidth(Region.USE_PREF_SIZE);
        	pane.setPrefWidth(350);
        	pane.setMaxWidth(Region.USE_PREF_SIZE);
        	pane.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        	pane.getStyleClass().add("custom-alert");
        	alert.showAndWait();
            return;
        }
        validate_import_btn.setDisable(true);
        
        Map<String, List<String>> readXmlDiagData = new ReadXMLDiag().readXMLDtc(filesPaths.getDiag_files());
    	readDtcTable.readTable();
    	
    	Parent root = FXMLLoader.load(getClass().getResource("/view/dtcs_view.fxml"));
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root, 650,500);
    	scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    	stage.setScene(scene);
    	stage.show();
    	
//    	Map<String, List<String>> organizedData = readDtcTable.organizeDtcCaras(ReadDtcTable.rcdFinalData, readXmlDiagData);
//    	List<String> organized_labels = readDtcTable.organize_labels();
//    	buildDiag.create_unexisting_dtc_blocks(filesPaths.getDiag_files(), organizedData, organized_labels);
//    	
//    	readXMLMessaging.dtc_code_parameter_exists(filesPaths.getDsd_files());
//		readXMLMessaging.fault_type_parameter_exists(filesPaths.getDsd_files());
        
    }

    public boolean isFilesChecked() throws Exception{
    	String rcdPath = readDtcTable.getRcdPath();
    	String dsd_file = filesPaths.getDsd_files();
    	String diag_file = filesPaths.getDiag_files();
    	if (rcdPath == null || rcdPath.equals("") || dsd_file == null || dsd_file.isEmpty() || diag_file == null || diag_file.isEmpty()) {
    		return false;
    	}else {
    		return true;
    	}
    }
}
