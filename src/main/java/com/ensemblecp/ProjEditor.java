package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ProjEditor {
    @FXML TextField investmentCostsField;
    @FXML TextField descriptionField;
    @FXML TextField titleField;
    @FXML TextField kickoffField;
    @FXML TextField deadlineField;
    @FXML TextField tag1Field;
    @FXML TextField tag2Field;
    @FXML TextField tag3Field;
    @FXML TextField tag4Field;

    @FXML
    public void modifyProject_onClick(Event e) throws SQLException, IOException {
        // Get data
        HashMap<String, String> info = new HashMap<String, String>();
        info.put("title", titleField.getText());
        info.put("investmentCosts", investmentCostsField.getText());
        info.put("budget", "1000000"); // TODO: Fix this to get real budget
        info.put("roi", "0"); // TODO: Fix this to get predicated ROI
        info.put("kickoff", kickoffField.getText());
        info.put("deadline", deadlineField.getText());
        info.put("issueScore", "0"); // TODO: Fix this later for real issue score
        info.put("tag1", tag1Field.getText());
        info.put("tag2", tag1Field.getText());
        info.put("tag4", tag1Field.getText());
        info.put("tag3", tag1Field.getText());
        info.put("complete", "false"); // TODO: Fix this late to get correct status
        Database db = new Database();
        ResultSet rs = db.updateProject(info);
        Main.projects.get(0).update(rs); // TODO: Fix this later to reference correct index
        db.closeDB();
        Main.show("projectView"); // TODO: get correct name of project view screen
    }

    @FXML
    public void cancelModify_onClick(Event e) throws IOException {
        // Cancel project modification
        Main.show("Dashboard");
    }
}
