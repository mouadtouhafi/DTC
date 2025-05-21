package com.automation.dtc.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.automation.dtc.readtable.ReadDtcTable;
import com.automation.dtc.xmlsfiles.FilesPaths;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    
    private FilesPaths filesPaths = new FilesPaths();
    private ReadDtcTable readDtcTable = new ReadDtcTable();

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
    		System.out.println(file.getPath());
    		RCD_input.setText(file.getName());
    		RCD_input.setStyle("-fx-text-fill: green; -fx-opacity: 1; -fx-font-size: 10px;");
    	}
    }

    @FXML
    void validate_import_btn_clicked(ActionEvent event) throws Exception {
    	if(isFilesChecked()) {
    		
    	}
    }
    
    public boolean isFilesChecked() throws Exception{
    	if (readDtcTable.getRcdPath().equals("") || 
    		readDtcTable.getRcdPath() == null || 
    		filesPaths.getDsd_files().isEmpty() || 
    		filesPaths.getDiag_files().isEmpty()) {
    		
    		return false;
    	}else {
    		return false;
    	}
    }

}
