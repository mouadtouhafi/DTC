package com.automation.dtc.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.automation.dtc.blockbuilder.BuildDIAGXmlBlock;
import com.automation.dtc.inputsdata.ReadXMLMessaging;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ExecuteController implements Initializable {

	@FXML private ImageView progressImage;
	@FXML private Label label;
	@FXML private Button home_btn;
	
	private RotateTransition rotate;
	public BuildDIAGXmlBlock buildDiag = new BuildDIAGXmlBlock();
	ReadXMLMessaging readXMLMessaging = new ReadXMLMessaging();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					home_btn.setDisable(true);
					Platform.runLater(() -> {
						progressImage.setVisible(true);
						label.setVisible(true);
						ImageRotation(progressImage);
					});
					Thread.sleep(3000);

					buildDiag.create_unexisting_dtc_blocks(ImportController.filesPaths.getDiag_files(), DtcController.newOrganizedData, DtcController.newOrganized_labels);
					if (!ImportController.filesPaths.getDsd_files().equals("") && !ImportController.filesPaths.getDsd_files().isEmpty() && ImportController.filesPaths.getDsd_files() != null) {
						readXMLMessaging.dtc_code_parameter_exists(ImportController.filesPaths.getDsd_files());
						readXMLMessaging.fault_type_parameter_exists(ImportController.filesPaths.getDsd_files());
					}
					Platform.runLater(() -> {
						updateImageView(progressImage, true);
						updateLabel(label, true);
					});
					Thread.sleep(1000);
					home_btn.setVisible(true);
					home_btn.setDisable(false);
				} catch (Exception e) {
					e.printStackTrace();
					Platform.runLater(() -> {
						updateImageView(progressImage, false);
						updateLabel(label, false);
					});
					home_btn.setVisible(true);
					home_btn.setDisable(false);
				}
			}
		}).start();

	}

	public void ImageRotation(ImageView imageview) {
		rotate = new RotateTransition(Duration.seconds(3), imageview);
		rotate.setFromAngle(-360);
		rotate.setToAngle(360);
		rotate.setCycleCount(RotateTransition.INDEFINITE);
		rotate.play();
	}

	private void updateImageView(ImageView imageview, boolean passed) {
		Image green = new Image("file:src/main/resources/images/greenCheck.png");
		Image red = new Image("file:src/main/resources/images/redX.png");

		if (passed) {
			imageview.setImage(green);
		} else {
			imageview.setImage(red);
			imageview.setTranslateY(2);
		}

		rotate.stop();
		imageview.setRotate(0);
		imageview.setFitHeight(15);
		imageview.setFitWidth(15);
	}

	private void updateLabel(Label label, boolean state) {
		if(state) {
			label.setText("Integration of fault codes done in succes.");
		}else {
			label.setText("Integration of fault codes is failed.");
		}
		
	}
	
	@FXML
	private void home_btn_clicked(ActionEvent e) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/view/home_view.fxml"));
	    Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
	    Scene scene = new Scene(root, 650,500);
	    scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
	    stage.setScene(scene);
	    stage.show();
	}
}
