package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ProjEditorController implements Initializable {
    @FXML TextField investmentCostsField;
    @FXML TextField descriptionField;
    @FXML TextField titleField;
    @FXML DatePicker kickoffField;
    @FXML DatePicker deadlineField;
    @FXML TextField tag1Field;
    @FXML TextField tag2Field;
    @FXML TextField tag3Field;
    @FXML TextField tag4Field;
    @FXML TextField budgetField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        investmentCostsField.setText(String.valueOf(Main.curProject.getInvestmentCosts()));
        descriptionField.setText(Main.curProject.getDescription());
        titleField.setText(Main.curProject.getTitle());
        kickoffField.setValue(LOCAL_DATE(Main.curProject.getKickoff().toString()));
        deadlineField.setValue(LOCAL_DATE(Main.curProject.getDeadline().toString()));
        tag1Field.setText(Main.curProject.getTag1());
        tag2Field.setText(Main.curProject.getTag2());
        tag3Field.setText(Main.curProject.getTag3());
        tag4Field.setText(Main.curProject.getTag4());
        budgetField.setText(String.valueOf(Main.curProject.getBudget()));
    }

    public static LocalDate LOCAL_DATE (String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }

    @FXML
    public void modifyProject_onClick(Event e) throws SQLException, IOException {
        // Get data
        HashMap<String, String> info = new HashMap<String, String>();
        info.put("pid", String.valueOf(Main.curProject.getPid()));
        info.put("title", titleField.getText());                            //TODO - ensure not a duplicate project name

        info.put("description", descriptionField.getText());

        info.put("investmentCosts", investmentCostsField.getText());        //TODO - ensure proper data type
        info.put("budget", budgetField.getText());                          //TODO - ensure proper data type
        info.put("kickoff", kickoffField.getValue().toString());
        info.put("deadline", deadlineField.getValue().toString());
        info.put("tag1", tag1Field.getText());
        info.put("tag2", tag2Field.getText());
        info.put("tag3", tag3Field.getText());
        info.put("tag4", tag4Field.getText());
        info.put("complete", "false");

        // Get roi
        info.put("roi", "0"); // TODO: Fix this to get predicated ROI, set as value of hashmap

        // Get issue score
        info.put("issueScore", "0"); // TODO: Fix this later for real issue score, set as value of hashmap

        // Update project row
        Database db = new Database();
        ResultSet rs = db.updateProject(info);
        Main.curProject.update(rs);
        db.closeDB();
        Main.show("projViewScreen");
    }

    @FXML
    public void cancelModify_onClick(Event e) throws IOException {
        // Cancel project modification
        Main.show("Dashboard");
    }

}
