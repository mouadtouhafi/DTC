package com.automation.dtc.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.automation.dtc.inputsdata.InvalidDtcCodeException;
import com.automation.dtc.inputsdata.ReadDtcTable;
import com.automation.dtc.inputsdata.ReadXMLDiag;
import com.automation.dtc.xmlsfiles.FilesPaths;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;

public class DtcController implements Initializable{
	
	@FXML private TableColumn<ObservableList<String>, Boolean> checkboxes_column;
	@FXML private TableColumn<ObservableList<String>, String> dtc_column;
	@FXML private TableColumn<ObservableList<String>, String> dtc_label_column;
	@FXML private TableColumn<ObservableList<String>, String> property_column;
	@FXML private TableColumn<ObservableList<String>, String> property_label_column;
	@FXML private TableView<ObservableList<String>> tableView;
	@FXML private Button validate_selection_btn;
	@FXML private Button back_btn;
	
	public FilesPaths filesPaths = new FilesPaths();
	
	public static List<List<String>> onlyDtcsToDisplay;
    private List<SimpleBooleanProperty> rowSelectProps;
    public static Map<String, List<String>> organizedData;
    public static Map<String, List<String>> newOrganizedData;
    public static List<String> organized_labels;
    public static List<String> newOrganized_labels;
    public static List<List<String>> newRcdFinalData;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Map<String, List<String>> readXmlDiagData = new ReadXMLDiag().readXMLDtc(ImportController.filesPaths.getDiag_files());
		try {
	        ImportController.readDtcTable.readTable();
	    } catch (InvalidDtcCodeException e) {
	        Platform.runLater(() -> {
	            try {
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/error_view.fxml"));
	                Parent root = loader.load();

	                ErrorController controller = loader.getController();
	                controller.setErrorMessage("DTC Error: " + e.getMessage());

	                Stage stage = (Stage) tableView.getScene().getWindow();
	                Scene scene = new Scene(root, 650, 500);
	                stage.setScene(scene);
	                stage.show();
	            } catch (Exception ex) {
	                ex.printStackTrace(); // fallback
	            }
	        });
	        return; // prevent continuing init if DTCs are invalid
	    }
    	organizedData = ImportController.readDtcTable.organizeDtcCaras(ReadDtcTable.rcdFinalData, readXmlDiagData);
    	organized_labels = ImportController.readDtcTable.organize_labels();
    	onlyDtcsToDisplay = ImportController.readDtcTable.onlyDtcToDisplay(ReadDtcTable.rcdFinalData, organizedData);
    	
    	System.out.println("organizedData : "+organizedData);
    	System.out.println("organized_labels : "+organized_labels);
    	System.out.println("onlyDtcsToDisplay : "+onlyDtcsToDisplay);
    	
		ObservableList<ObservableList<String>> tableData = FXCollections.observableArrayList();;
		rowSelectProps = new ArrayList<>();
		for (List<String> row : onlyDtcsToDisplay) {
			tableData.add(FXCollections.observableArrayList(row));
			rowSelectProps.add(new SimpleBooleanProperty(false));
		}
		tableView.setEditable(true);
		checkboxes_column.setEditable(true);
		
		CheckBox selectAll = (CheckBox) checkboxes_column.getGraphic();
		selectAll.selectedProperty().addListener((obs, was, now) -> {
	        rowSelectProps.forEach(p -> p.set(now));
	    });
		
		rowSelectProps.forEach(p -> p.addListener((obs, oldV, newV) -> {
	        long selectedCount = rowSelectProps.stream().filter(BooleanProperty::get).count();
	        if (selectedCount == 0) {
	            selectAll.setIndeterminate(false);
	            selectAll.setSelected(false);
	        } else if (selectedCount == rowSelectProps.size()) {
	            selectAll.setIndeterminate(false);
	            selectAll.setSelected(true);
	        } else {
	            selectAll.setIndeterminate(true);
	        }
	        validate_selection_btn.setDisable(selectedCount == 0);
	    }));
		
		checkboxes_column.setCellValueFactory(cellData -> {
	        int rowIndex = tableView.getItems().indexOf(cellData.getValue());
	        return rowSelectProps.get(rowIndex);
	    });
	    checkboxes_column.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxes_column));

		dtc_column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().get(0)));
		dtc_label_column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().get(1)));
		property_column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().get(2)));
		property_label_column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().get(3)));
		
		checkboxes_column.setSortable(false);
		dtc_column.setSortable(false);
		dtc_label_column.setSortable(false);
		property_column.setSortable(false);
		property_label_column.setSortable(false);

		validate_selection_btn.setDisable(true);

		tableView.setItems(tableData);
		
		validate_selection_btn.setOnAction(e -> {
			try {
				validate_selection_btn_clicked(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}
	
	@FXML
    public void validate_selection_btn_clicked(ActionEvent e) throws Exception {
        for (int i=rowSelectProps.size() - 1; i >= 0; i--) {
            if (!rowSelectProps.get(i).get()) {
                onlyDtcsToDisplay.remove(i);
            }
        }
        
        newOrganizedData = new LinkedHashMap<>();
        for (List<String> dtcEntry : onlyDtcsToDisplay) {
            String dtcCode = dtcEntry.get(0);
            String subCode = dtcEntry.get(2);

            for (Map.Entry<String, List<String>> entry : organizedData.entrySet()) {
                String originalKey = entry.getKey();
                List<String> values = entry.getValue();

                String normalizedKey = originalKey.startsWith("$") ? originalKey.substring(1) : originalKey;

                if (normalizedKey.equals(dtcCode) && values.contains(subCode)) {
                    newOrganizedData
                        .computeIfAbsent(originalKey, k -> new ArrayList<>())
                        .add(subCode);
                }
            }
        }
        System.out.println("newOrganizedData : "+ newOrganizedData);
        
        
        newOrganized_labels = new ArrayList<>();
        for (String entry : organized_labels) {
            String[] parts = entry.split("\\|");
            if (parts.length >= 3) {
                String dtcCode = parts[0].trim();
                String subCode = parts[2].trim();
                List<String> values = newOrganizedData.get(dtcCode);
                List<String> valuesWithDollar = newOrganizedData.get("$" + dtcCode);
                boolean match = (values != null && values.contains(subCode)) || (valuesWithDollar != null && valuesWithDollar.contains(subCode));
                if (match) {
                    newOrganized_labels.add(entry);
                }
            }
        }
        System.out.println("newOrganized_labels : "+newOrganized_labels);
        
        newRcdFinalData = new ArrayList<>();
        for(List<String> dtc : ReadDtcTable.rcdFinalData) {
        	String dtcCode = dtc.get(0);
        	if(newOrganizedData.containsKey(dtcCode) || newOrganizedData.containsKey("$"+dtcCode)) {
        		List<String> values = newOrganizedData.get(dtcCode);
        		List<String> valuesWithDollar = newOrganizedData.get("$" + dtcCode);
        		boolean dtcExists = (values != null && values.contains(dtc.get(2))) || (valuesWithDollar != null && valuesWithDollar.contains(dtc.get(2)));
		        if (dtcExists) {
		            newRcdFinalData.add(dtc);
		        }
        	}
        }
        System.out.println("newRcdFinalData : "+newRcdFinalData);
        
        System.out.println("onlyDtcsToDisplay : "+onlyDtcsToDisplay);
        Parent root = FXMLLoader.load(getClass().getResource("/view/execute_view.fxml"));
    	Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
    	Scene scene = new Scene(root, 650,500);
    	scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
    	stage.setScene(scene);
    	stage.show();
    }
	
	@FXML
	public void back_btn_clicked(ActionEvent e) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/view/home_view.fxml"));
	    Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
	    Scene scene = new Scene(root, 650,500);
	    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
	    stage.setScene(scene);
	    stage.show();
	}
}
