package com.ensemblecp;

import javafx.event.ActionEvent;

import java.io.IOException;

public class CreateIssueController {
    public void cancelCreate_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projIssues");
    }

    public void submitIssue_onClick(ActionEvent actionEvent) throws IOException {
        // Get issue info
        Database db = new Database();
        HashMap<String, String> info = new HashMap<>();
        info.put("message", messageField.getText()); // TODO: Change to correct name of message field

        // Determine type
        String getSelectedField = null;

        ToggleGroup group = new ToggleGroup();

        ToggleButton tb1 = new ToggleButton("No Score");
        tb1.setToggleGroup(group);

        ToggleButton tb2 = new ToggleButton("Timeline");
        tb2.setToggleGroup(group);

        ToggleButton tb3 = new ToggleButton("Budget");
        tb3.setToggleGroup(group);

        ToggleButton tb4 = new ToggleButton("Task");
        tb4.setToggleGroup(group);

        ToggleButton tb5 = new ToggleButton("Team");
        tb5.setToggleGroup(group);

        if(tb1.isSelected()){
            getSelectedField = "No Score";
        }
        if(tb2.isSelected()){
            getSelectedField = "Timeline";
        }
        if(tb3.isSelected()){
            getSelectedField = "Budget";
        }
        if(tb4.isSelected()){
            getSelectedField = "Task";
        }
        if(tb5.isSelected()){
            getSelectedField = "Team";
        }

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
