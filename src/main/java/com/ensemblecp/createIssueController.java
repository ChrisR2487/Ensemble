package com.ensemblecp;

import javafx.event.ActionEvent;

import java.io.IOException;

public class createIssueController {
    public void cancelCreate_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("issueList");
    }

    public void submitIssue_onClick(ActionEvent actionEvent) throws IOException {
        // TODO: Create issue in system

        // Go back to issue list
        Main.show("issueList");
    }
}
