package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProjIssuesController implements Initializable {
    @FXML TableView<IssueRow> issueTable;
    //@FXML TableColumn<IssueRow, String> dateColumn;
    @FXML TableColumn<IssueRow, String> originColumn;
    @FXML TableColumn<IssueRow, String> descriptionColumn;
    @FXML TableColumn<IssueRow, String> typeColumn;
    @FXML TableColumn<IssueRow, String> stateColumn;
    @FXML Label tagsLabel;
    @FXML Label roiLabel;
    @FXML Label budgetLabel;
    @FXML Label kickoffLabel;
    @FXML Label deadlineLabel;
    @FXML Label investmentCostsLabel;
    @FXML Label titleLabel;

    @FXML ImageView removeButton;
    @FXML ImageView editButton;
    @FXML ImageView addComponent;
    @FXML ImageView refreshROI;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set insights
        tagsLabel.setText(tagsLabel.getText() + "\n\t" +
                Main.curProject.getTag1() + "\n\t" +
                Main.curProject.getTag2() + "\n\t" +
                Main.curProject.getTag3() + "\n\t" +
                Main.curProject.getTag4());
        roiLabel.setText(roiLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getRoi()));
        budgetLabel.setText(budgetLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getBudget()));
        kickoffLabel.setText(kickoffLabel.getText() + "\n\t" + Main.curProject.getKickoff().toString());
        deadlineLabel.setText(deadlineLabel.getText() + "\n\t" + Main.curProject.getDeadline().toString());
        investmentCostsLabel.setText(investmentCostsLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getInvestmentCosts()));
        titleLabel.setText(Main.curProject.getTitle());

        // Hookup issues table (temp) TODO: Change to better display format
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                setupIssuesList();
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load project issues, trying again...");
                tryCount++;
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to load project issues, end execution.");
        }
    }

    private void setupIssuesList() throws SQLException {
        ArrayList<IssueRow> rowArrayList = new ArrayList<>();
        Database db = new Database();
        ResultSet rs = db.getProjectIssues(Main.curProject.getPid());
        while (rs.next()) {
            IssueRow ir = new IssueRow();
            ir.setOrigin(String.valueOf(rs.getInt("memid")));
            ir.setDescription(rs.getString("message"));

            String state = "";
            switch(rs.getInt("state")) {
                case IssueState.NEW -> state = "New";
                case IssueState.SEEN -> state = "Seen";
                case IssueState.DONE -> state = "Done";
            }
            ir.setState(state);

            String type = "";
            switch(rs.getInt("type")) {
                case IssueType.TEAM -> type = "Team";
                case IssueType.BUDGET -> type = "Budget";
                case IssueType.NO_SCORE -> type = "No Score";
                case IssueType.TASK -> type = "Task";
                case IssueType.TIMELINE -> type = "Timeline";
            }
            ir.setType(type);
            rowArrayList.add(ir);
        }
        db.closeDB();

        // Convert to array
        IssueRow[] rowList = rowArrayList.toArray(new IssueRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<IssueRow> rows = List.of(rowList);
        ObservableList<IssueRow> issueRows = FXCollections.observableList(rows);

        // Set row data
        originColumn.setCellValueFactory(new PropertyValueFactory("origin"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory("type"));
        stateColumn.setCellValueFactory(new PropertyValueFactory("state"));
        issueTable.setItems(issueRows);
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void dashButton_onClick(Event mouseEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event mouseEvent) throws IOException {
        Main.show("projList");
    }

    public void archiveButton_onClick(Event mouseEvent) {
    }

    public void editProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projEditor");
    }

    public void removeProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projDelete");
    }

    public void addComponentButton_onClick(ActionEvent event) throws IOException {
        Main.show("compCreator");
    }

    public void createIssue_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("createIssue");
    }

    public void viewTeam_onClick(ActionEvent event) throws IOException {
        Main.show("projTeam");
    }

    public void viewOverview_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projOverview");
    }

    public void viewBenchmark_onClick(ActionEvent actionEvent) {
        // TODO: Implement this view change
    }

    public void viewIssue_onClick(ActionEvent event) throws IOException {
        Main.show("projIssues");
    }

    public void addComponent_Hover(){
        addComponent.setOpacity(0.5);
    }

    public void addComponent_HoverOff(){
        addComponent.setOpacity(1.0);
    }

    public void editButton_Hover(){
        editButton.setOpacity(0.5);
    }

    public void editButton_HoverOff(){
        editButton.setOpacity(1.0);
    }
    public void removeButton_Hover(){
        removeButton.setOpacity(0.5);
    }

    public void removeButton_HoverOff(){
        removeButton.setOpacity(1.0);
    }
    public void refreshROI_Hover(){
        refreshROI.setOpacity(0.5);
    }

    public void refreshROI_HoverOff(){
        refreshROI.setOpacity(1.0);
    }

}
