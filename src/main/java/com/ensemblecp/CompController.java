package com.ensemblecp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class CompController {


    @FXML
    public void showComps() throws IOException {
        Main.show("Components");
    }

    @FXML
    public void showCustomComp() throws IOException {
        Main.show("CustomComps");
    }
}
