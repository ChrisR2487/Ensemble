package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ProjDeleteController implements Initializable {
    @FXML CheckBox remCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remCheckBox.setText(remCheckBox.getText() + Main.curProject.getTitle() + "?");
    }

    public void onSubmit_onClick(Event e) throws IOException, SQLException {
        // Check for confirmed
        if (!remCheckBox.isSelected()) return;

        // Delete project
        Database db = new Database();
        db.removeProject(Main.curProject.getPid()); // Handles all table deletion
        db.closeDB();
        Main.projects.remove(Main.curProject);
        Main.curProject = null;

        // Go to dashboard
        Main.show("Dashboard");
    }

    public void onCancel_onClick(Event e) throws IOException {
        Main.show("projViewScreen");
    }

}
