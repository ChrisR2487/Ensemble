package com.ensemblecp;

import javafx.event.ActionEvent;

import java.io.IOException;

public class CreateIssueController {
    public void cancelCreate_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projIssues");
    }

    public void submitIssue_onClick(ActionEvent actionEvent) throws IOException {
        // TODO: Create issue in system

        // Go back to issue list
        Main.show("projIssues");
    }
}
