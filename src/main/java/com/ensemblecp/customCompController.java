package com.ensemblecp;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class customCompController {

    @FXML private ImageView exitButton;

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }
}
