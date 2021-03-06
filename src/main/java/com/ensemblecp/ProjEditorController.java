package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;

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
    @FXML TextArea descriptionField;
    @FXML TextField titleField;
    @FXML DatePicker kickoffField;
    @FXML DatePicker deadlineField;
    @FXML TextField tag1Field;
    @FXML TextField tag2Field;
    @FXML TextField tag3Field;
    @FXML TextField tag4Field;
    @FXML TextField budgetField;
    @FXML TextField profitField;

    @FXML Label profitLabel;

    @FXML ToggleSwitch archiveSwitch;

    String initialTitle;

    private final Border INVALID_BORDER = new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5)));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        investmentCostsField.setText(String.valueOf(Main.curProject.getInvestmentCosts()));
        descriptionField.setText(Main.curProject.getDescription());
        titleField.setText(Main.curProject.getTitle());
        initialTitle = Main.curProject.getTitle();
        kickoffField.setValue(LOCAL_DATE(Main.curProject.getKickoff().toString()));
        deadlineField.setValue(LOCAL_DATE(Main.curProject.getDeadline().toString()));
        if(Main.curProject.isComplete()){
            archiveSwitch.setSelected(true);
            profitField.setVisible(true);
            profitLabel.setVisible(true);
            profitField.setText(String.valueOf(Main.curProject.getInvestmentCosts() * Main.curProject.getRoi()));
            profitField.setEditable(true);
        }
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
        //reset error borders
        kickoffField.setBorder(null);
        deadlineField.setBorder(null);
        investmentCostsField.setBorder(null);
        budgetField.setBorder(null);
        titleField.setBorder(null);
        tag1Field.setBorder(null);
        tag2Field.setBorder(null);
        tag3Field.setBorder(null);
        tag4Field.setBorder(null);

        // Get data
        HashMap<String, String> info = new HashMap<>();
        info.put("pid", String.valueOf(Main.curProject.getPid()));
        info.put("title", titleField.getText());

        String title = titleField.getText();
        Database db = new Database();
        if(title.equals("")){
            //empty input
            titleField.setBorder(INVALID_BORDER);
            return;
        }
        else if(db.getProjectByName(title).isBeforeFirst() && !title.equals(initialTitle)){
            //duplicate found
            System.out.println("duplicate title found");
            titleField.setBorder(INVALID_BORDER);
            return;
        }
        info.put("title", title);
        info.put("description", descriptionField.getText());

        //validate input for investment
        String investment = investmentCostsField.getText();
        try{
            Float cast = Float.parseFloat(investment);
        }
        catch(Exception investError){
            investmentCostsField.setBorder(INVALID_BORDER);
            System.out.println("INVALID INVESTMENT FIELD");
            return;
        }
        info.put("investmentCosts", investment);

        //validate input for budget
        String budget = budgetField.getText();
        try{
            Float cast = Float.parseFloat(budget);
        }
        catch(Exception budgetError){
            budgetField.setBorder(INVALID_BORDER);
            System.out.println("INVALID BUDGET FIELD");
            return;
        }
        info.put("budget", budget);

        //error checking for date range
        LocalDate kickOff = kickoffField.getValue();
        LocalDate deadline = deadlineField.getValue();
        if(kickOff.compareTo(deadline) >= 0){
            kickoffField.setBorder(INVALID_BORDER);
            deadlineField.setBorder(INVALID_BORDER);
            //cancel project creation
            return;
        }
        info.put("kickoff", kickoffField.getValue().toString());
        info.put("deadline", deadlineField.getValue().toString());

        String tag1 = tag1Field.getText().trim();
        String tag2 = tag2Field.getText().trim();
        String tag3 = tag3Field.getText().trim();
        String tag4 = tag4Field.getText().trim();
        if(tag1.equals("") && tag2.equals("") && tag3.equals("") && tag4.equals("")){
            tag1Field.setBorder(INVALID_BORDER);
            tag2Field.setBorder(INVALID_BORDER);
            tag3Field.setBorder(INVALID_BORDER);
            tag4Field.setBorder(INVALID_BORDER);
            return;
        }
        info.put("tag1", tag1);
        info.put("tag2", tag2);
        info.put("tag3", tag3);
        info.put("tag4", tag4);

        //if project is marked as completed, handle it differently
        if(archiveSwitch.isSelected()){
            Float roi;
            Float fProfit;
            String profit = profitField.getText();

            if(profit.equals("")){
                profitField.setBorder(INVALID_BORDER);
                return;
            }

            //ensure proper input type
            try{
                fProfit = Float.parseFloat(profit);
            }
            catch (Exception invalidTypeException){
                profitField.setBorder(INVALID_BORDER);
                return;
            }

            //with proper input type, calculate ROI
            roi = fProfit / Float.parseFloat(investment);
            info.put("roi", Float.toString(roi));
        }
        else{
            // Get roi
            Float roi = db.getROI(info);
            info.put("roi", Float.toString(roi));
        }

        info.put("complete", String.valueOf(archiveSwitch.isSelected()));
        info.put("manid", String.valueOf(Main.curProject.getManid())); // Use existing manid value


        // Get issue score
        float newIssueScore = Main.curProject.getIssueScore();
        if (archiveSwitch.isSelected() != Main.curProject.isComplete()) {
            // Completeness has changed
            if (archiveSwitch.isSelected() && IssueScore.checkOverdue(Main.curProject.getDeadline().toString()) > 0.0f) {
                newIssueScore += -1 * IssueScore.PROJECT_OVERDUE;
            }
            else {
                // Now incomplete
                if (IssueScore.checkOverdue(Main.curProject.getDeadline().toString()) > 0.0f) { // Is new deadline overdue?
                    newIssueScore += IssueScore.checkOverdue(deadlineField.getValue().toString());
                }
            }
        }
        else if (!archiveSwitch.isSelected()) { // Not complete
            if (IssueScore.checkOverdue(Main.curProject.getDeadline().toString()) > 0.0f) { // Is old deadline already overdue?
                newIssueScore += IssueScore.checkOverdue(deadlineField.getValue().toString()) - IssueScore.PROJECT_OVERDUE; // No change if overdue still, remove penalty otherwise
            } else { // Old deadline not overdue, check normally
                newIssueScore += IssueScore.checkOverdue(deadlineField.getValue().toString());
            }
        }

        if(IssueScore.checkOverbudget(Main.curProject.getInvestmentCosts(), Main.curProject.getBudget()) > 0.1f) { // Is old project overbudget?
            newIssueScore += IssueScore.checkOverbudget(Float.parseFloat(investmentCostsField.getText()), Float.parseFloat(budgetField.getText())) - IssueScore.PROJECT_OVERBUDGET; // No change if overbudget still, remove penalty otherwise
        }
        else { // Old project not overbudget, check normally
            newIssueScore += IssueScore.checkOverbudget(Float.parseFloat(investmentCostsField.getText()), Float.parseFloat(budgetField.getText()));
        }
        info.put("issueScore", String.valueOf(newIssueScore));

        // Update project row
        ResultSet rs = db.updateProject(info);
        Main.curProject.update(rs);
        db.closeDB();
        Main.show("projOverview");
    }

    @FXML
    public void cancelModify_onClick(Event e) throws IOException {
        // Cancel project modification
        Main.show("projOverview");
    }

    public void toggleProfitField(Event e){
        if(profitField.isVisible()){
            profitField.setVisible(false);
            profitLabel.setVisible(false);
        }
        else{
            profitField.setVisible(true);
            profitLabel.setVisible(true);
        }
    }

}
