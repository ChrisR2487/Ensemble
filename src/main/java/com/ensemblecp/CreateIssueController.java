package com.ensemblecp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class CreateIssueController {
    @FXML private TextField messageField;
    @FXML private ToggleButton tb1;
    @FXML private ToggleButton tb2;
    @FXML private ToggleButton tb3;
    @FXML private ToggleButton tb4;
    @FXML private ToggleButton tb5;
    @FXML private ToggleGroup Type;

    public void cancelCreate_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projIssues");
    }

    public void submitIssue_onClick(ActionEvent actionEvent) throws IOException, SQLException {
        // Get issue info
        Database db = new Database();
        HashMap<String, String> info = new HashMap<>();
        info.put("message", messageField.getText());

        // Determine type
        String getSelectedField = null;

        if(tb1.isSelected()) { getSelectedField = "No Score"; }
        if(tb2.isSelected()) { getSelectedField = "Timeline"; }
        if(tb3.isSelected()) { getSelectedField = "Budget"; }
        if(tb4.isSelected()) { getSelectedField = "Task"; }
        if(tb5.isSelected()) { getSelectedField = "Team"; }

        int type = -1;
        switch (getSelectedField) {
            case "No Score": type = IssueType.NO_SCORE; break;
            case "Timeline": type = IssueType.TIMELINE; break;
            case "Budget": type = IssueType.BUDGET; break;
            case "Task": type = IssueType.TASK; break;
            case "Team": type = IssueType.TEAM; break;
        }
        info.put("type", String.valueOf(type));

        // Create issue record
        db.createIssue(info);

        // Update issue score
        float score = -1.0f; // Score to add to project
        switch (getSelectedField) {
            case "No Score": score = IssueScore.NO_SCORE; break;
            case "Timeline": score = IssueScore.ISSUE_TIMELINE; break;
            case "Budget": score = IssueScore.ISSUE_BUDGET; break;
            case "Task": score = IssueScore.ISSUE_TASK; break;
            case "Team": score = IssueScore.ISSUE_TEAM; break;
        }
        db.updateIssueScore(Main.curProject.getPid(), score);

        // Go back to issue list
        Main.show("projIssues");
    }
}
