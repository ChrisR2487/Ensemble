package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.ensemblecp.Main.curProject;

public class ProjDeleteController implements Initializable {
    @FXML CheckBox remCheckBox;
    @FXML TextField logBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remCheckBox.setText(remCheckBox.getText() + curProject.getTitle() + "?");
    }

    public void onSubmit_onClick(Event e) throws IOException, SQLException, InterruptedException {
        // Check for confirmed
        if (!remCheckBox.isSelected()) return;

        // Enter log of removal
        Database db = new Database();
        HashMap<String, String> log = new HashMap<>();
        log.put("pid", String.valueOf(Main.curProject.getPid()));
        log.put("manid", String.valueOf(Main.account.getId()));
        log.put("message", "Delete Project, reason: " + logBox.getText());
        db.createLog(log);


        // Delete project
        db.removeProject(curProject.getPid()); // Handles all table deletion
        db.closeDB();
        Main.projects.remove(curProject);
        curProject = null;

        // Go to dashboard
        Main.show("Dashboard");
    }

    public void onCancel_onClick(Event e) throws IOException {
        Main.show("projViewScreen");
    }

}
