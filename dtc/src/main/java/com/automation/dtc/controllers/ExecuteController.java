package com.automation.dtc.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ExecuteController implements Initializable{
	
	@FXML private ImageView progressImage;
	@FXML private Label label;
	private RotateTransition rotate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		progressImage.setVisible(true);
	    label.setVisible(true);

	    ImageRotation(progressImage);
	    PauseTransition pause = new PauseTransition(Duration.seconds(3));
	    pause.setOnFinished(evt -> updateImageView(progressImage, false));
	    pause.play();
		
//    	buildDiag.create_unexisting_dtc_blocks(filesPaths.getDiag_files(), organizedData, organized_labels);
//    	
//    	readXMLMessaging.dtc_code_parameter_exists(filesPaths.getDsd_files());
//		readXMLMessaging.fault_type_parameter_exists(filesPaths.getDsd_files());
	}
	
    public void ImageRotation(ImageView imageview) {
    	rotate = new RotateTransition(Duration.seconds(3), imageview);
    	rotate.setFromAngle(-360);
    	rotate.setToAngle(360);
    	rotate.setCycleCount(RotateTransition.INDEFINITE);
    	rotate.play();
    }
    
    private void updateImageView(ImageView imageview, boolean passed) {
        // note the plural “images”
    	Image green = new Image("file:src/main/resources/images/greenCheck.png");
    	Image red   = new Image("file:src/main/resources/images/redX.png");

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

}
