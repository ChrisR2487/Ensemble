package com.ensemblecp;

import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }
}
