package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ProjIssuesController implements Initializable {
    @FXML TableColumn<IssueRow, String> dateColumn;
    @FXML TableView<IssueRow> issueTable;
    @FXML TableColumn<IssueRow, String> originColumn;
    @FXML TableColumn<IssueRow, String> descriptionColumn;
    @FXML TableColumn<IssueRow, String> typeColumn;
    @FXML TableColumn<IssueRow, String> stateColumn;
    @FXML TableColumn<IssueRow, String> markColumn;

    @FXML Label tagsLabel;
    @FXML Label roiLabel;
    @FXML Label budgetLabel;
    @FXML Label kickoffLabel;
    @FXML Label deadlineLabel;
    @FXML Label investmentCostsLabel;
    @FXML Label titleLabel;
    @FXML Label issueScoreLabel;
    @FXML Label descLabel;

    @FXML ImageView removeButton;
    @FXML ImageView editButton;
    @FXML ImageView addComponent;
    @FXML ImageView refreshROI;

    @FXML ImageView settingsBtn;
    @FXML MenuButton settingsButton;

    ArrayList<IssueRow> rowArrayList = new ArrayList<>();

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
        descLabel.setText(Main.curProject.getDescription());
        investmentCostsLabel.setText(investmentCostsLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getInvestmentCosts()));
        issueScoreLabel.setText(issueScoreLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getIssueScore()));
        titleLabel.setText(Main.curProject.getTitle());

        // Hookup issues table
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
        Database db = new Database();
        ResultSet rs = db.getProjectIssues(Main.curProject.getPid());
        while (rs.next()) {
            IssueRow ir = new IssueRow();
            ir.setOrigin(String.valueOf(rs.getInt("memid")));
            ir.setDescription(rs.getString("message"));
            ir.setSelect(new CheckBox());

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

            ir.setDate(rs.getTimestamp("posted").toString());
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
        issueTable.setEditable(true);
        markColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        markColumn.setEditable(true);
        originColumn.setCellValueFactory(new PropertyValueFactory("origin"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory("type"));
        stateColumn.setCellValueFactory(new PropertyValueFactory("state"));
        dateColumn.setCellValueFactory(new PropertyValueFactory("date"));
        issueTable.setItems(issueRows);
    }

    public void markDone_onClick(ActionEvent actionEvent) throws SQLException, IOException {
        Database db = new Database();
        String charPid = Project.IDtoChars(Main.curProject.getPid());
        //get selected rows by memid and message
        db.markIssueResolved(getSelectedRows(), charPid);
        db.closeDB();
        Main.show("projIssues");
    }

    public void markSeen_onClick(ActionEvent actionEvent) throws SQLException, IOException {
        Database db = new Database();
        String charPid = Project.IDtoChars(Main.curProject.getPid());
        //get selected rows by memid and message
        db.markIssueSeen(getSelectedRows(), charPid);
        db.closeDB();
        Main.show("projIssues");
    }

    public void markNew_onClick(ActionEvent actionEvent) throws SQLException, IOException {
        Database db = new Database();
        String charPid = Project.IDtoChars(Main.curProject.getPid());
        //get selected rows by memid and message
        db.markIssueNew(getSelectedRows(), charPid);
        db.closeDB();
        Main.show("projIssues");
    }

    public HashMap<String, HashMap<String, String>> getSelectedRows(){
        HashMap<String, HashMap<String, String>> retVal = new HashMap<>();
        int rowNum = 1;
        for(IssueRow r: rowArrayList){
            if(r.getSelect().isSelected()){
                HashMap<String, String> cell = new HashMap<>();
                //member ID
                cell.put("memid", String.valueOf(r.getOrigin()));
                retVal.put(String.valueOf(rowNum), cell);

                //message of issue
                cell.put("message", r.getDescription());
                retVal.put(String.valueOf(rowNum), cell);
                rowNum++;
            }
        }
        return retVal;
    }

    public void exitButton_onClick(Event mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void dashButton_onClick(Event mouseEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event mouseEvent) throws IOException {
        Main.show("projList");
    }

    public void archiveButton_onClick(Event mouseEvent) throws IOException {
        Main.show("archiveList");
    }

    public void updateStatus_onClick(Event actionEvent) throws SQLException {
        String status  = ((MenuItem) (actionEvent.getSource())).getText();
        int newStatus = switch(status) {
            case "Available" -> StatusType.AVAILABLE;
            case "Busy" -> StatusType.BUSY;
            case "Away" -> StatusType.AWAY;
            default -> -1;
        };
        Database db = new Database();
        db.updateMemberStatus(Main.account.getId(), newStatus, Main.account.getType());
        Main.account.setStatus(status);
        db.closeDB();
    }

    public void logout_onClick(ActionEvent actionEvent) throws IOException {
        Main.account = null;
        Main.projects.clear();
        Main.curProject = null;
        Main.show("login");
    }

    public void editProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projEditor");
    }

    public void removeProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projDelete");
    }

    public void addComponentButton_onClick(ActionEvent event) throws IOException {
        Main.show("compSelector");
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

    public void viewBenchmark_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projBenchmark");
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
    public void settings_Hover(){
        settingsBtn.setOpacity(0.5);
    }
    public void settings_HoverOff(){
        settingsBtn.setOpacity(1.0);
    }
}
