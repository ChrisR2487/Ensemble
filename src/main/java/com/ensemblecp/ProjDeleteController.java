package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;

public class ProjDeleteController {


    @FXML
    public void onSubmit_onClick(Event e) throws IOException, SQLException {
        // Delete project
        Database db = new Database();
        db.removeProject(Main.curProject.getPid()); // Handles all table deletion
        Main.projects.remove(Main.curProject);
        Main.curProject = null;

        // Go to dashboard
        Main.show("Dashboard");
    }
}
