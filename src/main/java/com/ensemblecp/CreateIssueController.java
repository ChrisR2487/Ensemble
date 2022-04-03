package com.ensemblecp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class CreateIssueController {
    @FXML private TextField messageField;
    @FXML ToggleGroup type;
    @FXML ToggleGroup severity;
    @FXML ToggleGroup probability;

    public void cancelCreate_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projIssues");
    }

    public void submitIssue_onClick(ActionEvent actionEvent) throws IOException, SQLException {
        // Get issue info
        Database db = new Database();
        HashMap<String, String> info = new HashMap<>();
        info.put("message", messageField.getText());

        // Determine type, severity, and probability
        String getSelectedFieldType = ((RadioButton) (type.getSelectedToggle())).getText();
        String getSelectedFieldSeverity = ((RadioButton) (severity.getSelectedToggle())).getText();
        String getSelectedFieldProbability = ((RadioButton) (probability.getSelectedToggle())).getText();

        int type = switch (getSelectedFieldType) {
            case "No Score" -> IssueType.NO_SCORE;
            case "Timeline" -> IssueType.TIMELINE;
            case "Budget" -> IssueType.BUDGET;
            case "Task" -> IssueType.TASK;
            case "Team" -> IssueType.TEAM;
            default -> -1;
        };
        info.put("type", String.valueOf(type));


        int severity = -1; float severityCost = -1;
        switch(getSelectedFieldSeverity) {
            case "Insignificant" -> {
                severity = IssueSeverity.INSIGNIFICANT;
                severityCost = IssueScore.INSIGNIFICANT;
            }
            case "Tolerable" -> {
                severity = IssueSeverity.TOLERABLE;
                severityCost = IssueScore.TOLERABLE;
            }
            case "Serious" -> {
                severity = IssueSeverity.SERIOUS;
                severityCost = IssueScore.SERIOUS;
            }
            case "Catastrophic" -> {
                severity = IssueSeverity.CATASTROPHIC;
                severityCost = IssueScore.CATASTROPHIC;
            }
        }

        int probability = -1; float probabilityCost = -1;
        switch(getSelectedFieldProbability) {
            case "Very Low" -> {
                probability = IssueProbability.VERY_LOW;
                probabilityCost = IssueScore.VERY_LOW;
            }
            case "Low" -> {
                probability = IssueProbability.LOW;
                probabilityCost = IssueScore.LOW;
            }
            case "Moderate" -> {
                probability = IssueProbability.MODERATE;
                probabilityCost = IssueScore.MODERATE;
            }
            case "High" -> {
                probability = IssueProbability.HIGH;
                probabilityCost = IssueScore.HIGH;
            }
            case "Very High" -> {
                probability = IssueProbability.VERY_HIGH;
                probabilityCost = IssueScore.VERY_HIGH;
            }
        }

        // Create issue record
        db.createIssue(info);

        // Calculate issue score
        float score = severityCost * probabilityCost * switch (getSelectedFieldType) { // Score to add to project
            case "No Score" -> IssueScore.NO_SCORE;
            case "Timeline" -> IssueScore.ISSUE_TIMELINE;
            case "Budget" -> IssueScore.ISSUE_BUDGET;
            case "Task" -> IssueScore.ISSUE_TASK;
            case "Team" -> IssueScore.ISSUE_TEAM;
            default -> -1;
        };

        // Update issue score
        if (Main.curProject.getIssueScore() + score > 100.0f) {
            db.updateIssueScore(Main.curProject.getPid(), 100.0f - Main.curProject.getIssueScore());
            Main.curProject.addIssueScore(100.0f - Main.curProject.getIssueScore());
        }
        else {
            db.updateIssueScore(Main.curProject.getPid(), score);
            Main.curProject.addIssueScore(score);
        }

        // Go back to issue list
        Main.show("projIssues");
    }
}
