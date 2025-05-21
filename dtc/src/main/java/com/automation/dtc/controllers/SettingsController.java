package com.automation.dtc.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SettingsController {

    @FXML
    private TextField lineField;

    @FXML
    public void handleSubmit() {
        String input = lineField.getText();
        if (input.matches("\\d+")) {
            int startingLine = Integer.parseInt(input);
            System.out.println("Starting line set to: " + startingLine);
            // TODO: Store or use this value as needed
        } else {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}
