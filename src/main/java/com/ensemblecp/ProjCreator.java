package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ProjCreator {
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
    public void createProject_onClick(Event e) throws SQLException, IOException {
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
        info.put("tag3", tag1Field.getText());
        info.put("tag4", tag1Field.getText());
        info.put("complete", "false"); // TODO: Fix this late to get correct status
        Database db = new Database();
        ResultSet rs = db.createProject(info);
        Main.projects.add(new Project(rs, null));
        db.closeDB();
        Main.show("Dashboard"); // TODO: get correct name of project view screen
    }

    @FXML
    public void cancelCreate_onClick(Event e) throws IOException {
        // Cancel project creation
        Main.show("Dashboard");
    }
}
