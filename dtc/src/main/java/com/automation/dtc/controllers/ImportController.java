package com.automation.dtc.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.automation.dtc.inputsdata.ReadDtcTable;
import com.automation.dtc.inputsdata.ReadXMLDiag;
import com.automation.dtc.inputsdata.ReadXMLMessaging;
import com.automation.dtc.xmlsfiles.FilesPaths;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
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

    @FXML
    void DSD_btn_clicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
    	ExtensionFilter excelFilter = new ExtensionFilter("DSD Files", "*.xml");
    	fileChooser.getExtensionFilters().add(excelFilter);
    	
    	List<File> files = fileChooser.showOpenMultipleDialog(null);
    	if (files == null || files.isEmpty()) {
    		return;
    	}else {
    		List<String> paths = new ArrayList<>();
    	    List<String> names = new ArrayList<>();
    	    for (File file : files) {
    	        paths.add(file.getPath());
    	        names.add(file.getName());
    	    }
    	    filesPaths.setDsd_files(paths);
    		DSD_input.setText(names.get(0) + " ...");
    		DSD_input.setStyle("-fx-text-fill: green; -fx-opacity: 1; -fx-font-size: 10px;");
    	}
    }

    @FXML
    void DiagObject_btn_clicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
    	ExtensionFilter excelFilter = new ExtensionFilter("Diag Files", "*.xml");
    	fileChooser.getExtensionFilters().add(excelFilter);
    	List<File> files = fileChooser.showOpenMultipleDialog(null);
    	if (files == null || files.isEmpty()) {
    		return;
    	}else {
    		List<String> paths = new ArrayList<>();
    	    List<String> names = new ArrayList<>();
    	    for (File file : files) {
    	        paths.add(file.getPath());
    	        names.add(file.getName());
    	    }
    	    filesPaths.setDiag_files(paths);
    		DiagObject_input.setText(names.get(0) + " ...");
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
        readDtcTable.readTable();
        ReadXMLMessaging readXMLMessaging = new ReadXMLMessaging();
        for(String file : ImportController.filesPaths.getDiag_files()) {
//    		System.out.println(readDtcTable.concatDTCs());
    		readDtcTable.removeIntegratedDTCs(readDtcTable.extractDTCsCode(), new ReadXMLDiag().readXMLDtc(file));
    		readXMLMessaging.dtc_code_parameter_exists(file);
    	}
    }

    public boolean isFilesChecked() throws Exception{
    	String rcdPath = readDtcTable.getRcdPath();
    	List<String> dsd_files = filesPaths.getDsd_files();
    	List<String> diag_files = filesPaths.getDiag_files();
    	if (rcdPath == null || rcdPath.equals("") || dsd_files == null || dsd_files.isEmpty() || diag_files == null || diag_files.isEmpty()) {
    		return false;
    	}else {
    		return true;
    	}
    }

}
