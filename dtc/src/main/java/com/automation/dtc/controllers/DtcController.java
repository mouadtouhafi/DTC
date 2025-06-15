package com.automation.dtc.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.automation.dtc.inputsdata.ReadDtcTable;
import com.automation.dtc.inputsdata.ReadXMLDiag;
import com.automation.dtc.xmlsfiles.FilesPaths;

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
	
	private List<List<String>> onlyDtcsToDisplay;
    private List<SimpleBooleanProperty> rowSelectProps;
    public static Map<String, List<String>> organizedData;
    public static List<String> organized_labels;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Map<String, List<String>> readXmlDiagData = new ReadXMLDiag().readXMLDtc(ImportController.filesPaths.getDiag_files());
    	ImportController.readDtcTable.readTable();
    	organizedData = ImportController.readDtcTable.organizeDtcCaras(ReadDtcTable.rcdFinalData, readXmlDiagData);
    	organized_labels = ImportController.readDtcTable.organize_labels();
    	onlyDtcsToDisplay = ImportController.readDtcTable.onlyDtcToDisplay(ReadDtcTable.rcdFinalData, organizedData);

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
        for (int i = rowSelectProps.size() - 1; i >= 0; i--) {
            if (!rowSelectProps.get(i).get()) {
                onlyDtcsToDisplay.remove(i);
            }
        }
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
