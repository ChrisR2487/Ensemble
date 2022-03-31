package com.ensemblecp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CompEditorController implements Initializable {
    @FXML VBox partDataEntryList;
    ArrayList<TextField> dataFields;
    public static Component component;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataFields = new ArrayList<>();
        int index = 1;
        for (char partChar: component.getTemplate().toCharArray()) {
            // Create pane
            FlowPane partPane = new FlowPane();
            VBox.setMargin(partPane, new Insets(6.5, 6.5, 6.5, 6.5));
            partPane.setPrefWidth(480.0);
            partPane.setPrefHeight(50.0);

            Label typeLabel = new Label();
            typeLabel.setFont(new Font(16.0));
            typeLabel.setTextFill(Paint.valueOf("white"));
            typeLabel.setPrefHeight(45.0);
            typeLabel.setLayoutX(5.0);
            typeLabel.setLayoutY(5.0);
            typeLabel.setTextAlignment(TextAlignment.RIGHT);

            TextField entryBox = new TextField();
            entryBox.setFont(new Font(16.0));
            entryBox.setPrefWidth(460.0);
            entryBox.setText(component.getParts().get(index-1).getData()); // Set data from old component
            dataFields.add(entryBox);

            switch(partChar) {
                case 'S' -> {
                    // String part
                    typeLabel.setText("Part " + index + " - Text:");
                    entryBox.setPromptText("This is a piece of text");
                }
                case 'I' -> {
                    // Whole number part
                    typeLabel.setText("Part " + index + " - Whole Number:");
                    entryBox.setPromptText("1234");
                }
                case 'R' -> {
                    // Real number part
                    typeLabel.setText("Part " + index + " - Real Number:");
                    entryBox.setPromptText("1234.56");
                }
                case 'L' -> {
                    // List part
                    typeLabel.setText("Part " + index + " - List:");
                    entryBox.setPromptText("Item 1, Item 2, Item 3, ...");
                }
            }
            partPane.getChildren().addAll(typeLabel, entryBox);

            // Add pane to vbox
            partDataEntryList.getChildren().add(partPane);
            index++;
        }
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void cancelButton_onClick(ActionEvent mouseEvent) throws IOException {
        Main.show("projOverview");
    }

    public void submitButton_onClick(ActionEvent actionEvent) throws SQLException, IOException {
        // Read and check data
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < component.getTemplate().length(); i++) {
            dataFields.get(i).setBorder(LoginController.NORMAL_BORDER);
            try {
                switch (component.getTemplate().charAt(i)) {
                    case 'S' -> {
                        // Don't check, just insert
                        data.add(dataFields.get(i).getText().trim());
                    }
                    case 'R' -> {
                        // Check real, then insert
                        Float.parseFloat(dataFields.get(i).getText().trim());
                        data.add(dataFields.get(i).getText().trim());
                    }
                    case 'I' -> {
                        // Check integer, then insert
                        Integer.parseInt(dataFields.get(i).getText().trim());
                        data.add(dataFields.get(i).getText().trim());
                    }
                    case 'L' -> {
                        // Reformat list, then insert
                        String oldList = dataFields.get(i).getText().trim();
                        String newList = oldList.replaceAll(",\\s+", ",");
                        data.add(newList);
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid format type, stop parsing and show problem
                dataFields.get(i).setBorder(LoginController.INVALID_BORDER);
                return;
            }
        }

        // Create component data table and add component to project
        Database db = new Database();
        ResultSet rs = db.updateProjectComponent(Main.curProject.getPid(), component.getCid(), data);

        // Add component to local project object
        Main.curProject.getComponents().get(Main.curProject.getComponents().indexOf(component)).update(rs, db);
        db.closeDB();

        // Return to project overview
        Main.show("projOverview");
    }
}
