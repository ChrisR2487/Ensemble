package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ProjCreatorController {
    @FXML TextField investmentCostsField;
    @FXML TextField descriptionField;
    @FXML TextField budgetField;
    @FXML TextField titleField;
    @FXML TextField kickoffField;
    @FXML TextField deadlineField;
    @FXML TextField tag1Field;
    @FXML TextField tag2Field;
    @FXML TextField tag3Field;
    @FXML TextField tag4Field;

    @FXML
    public void createProject_onClick(Event e) throws SQLException, IOException {
        // Get data
        HashMap<String, String> info = new HashMap<String, String>();
        info.put("pid", String.valueOf(titleField.getText().hashCode()));
        info.put("title", titleField.getText());
        info.put("description", descriptionField.getText());
        info.put("investmentCosts", investmentCostsField.getText());
        info.put("budget", budgetField.getText());
        info.put("kickoff", kickoffField.getText());
        info.put("deadline", deadlineField.getText());
        info.put("tag1", tag1Field.getText());
        info.put("tag2", tag1Field.getText());
        info.put("tag3", tag1Field.getText());
        info.put("tag4", tag1Field.getText());
        info.put("complete", "false");

        // Get roi
        info.put("roi", "0"); // TODO: Fix this to get predicated ROI, set as value of hashmap

        // Get issue score
        info.put("issueScore", "0"); // TODO: Fix this later for real issue score, set as value of hashmap

        // Get manager ID
            // TODO: Get manid of current user, set as value of hashmap

        // Add data record
        Database db = new Database();
        ResultSet rs = db.createProject(info);

        // Add team members
            // TODO: Create Team Project table and add members selected

        // Add project to Main cache
        Main.projects.add(0, new Project(rs, null, db));
        Main.trimCache();

        // Close database
        db.closeDB();

        // Display proper view
        Main.show("projViewScreen");
    }

    @FXML
    public void cancelCreate_onClick(Event e) throws IOException {
        // Cancel project creation
        Main.show("Dashboard");
    }
}
