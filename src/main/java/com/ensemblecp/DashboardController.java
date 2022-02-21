package com.ensemblecp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;


public class DashboardController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void selectItem(ContextMenuEvent contextMenuEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
        System.exit(-1);
    }
}