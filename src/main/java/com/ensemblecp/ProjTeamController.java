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
import java.util.List;
import java.util.ResourceBundle;

public class ProjTeamController implements Initializable {
    @FXML private Button exitButton;
    @FXML private Label tagsLabel;
    @FXML private Label roiLabel;
    @FXML private Label budgetLabel;
    @FXML private Label kickoffLabel;
    @FXML private Label deadlineLabel;
    @FXML private Label investmentCostsLabel;
    @FXML private Label titleLabel;
    @FXML private Label issueScoreLabel;
    @FXML private Label descLabel;

    @FXML private TableView<MemberRow> memberTable;
    @FXML private TableColumn<MemberRow, String> positionColumn;
    @FXML private TableColumn<MemberRow, String> memidColumn;
    @FXML private TableColumn<MemberRow, String> nameColumn;
    @FXML private TableColumn<MemberRow, String> statusColumn;

    @FXML ImageView removeButton;
    @FXML ImageView editButton;
    @FXML ImageView addComponent;
    @FXML ImageView refreshROI;

    @FXML ImageView settingsBtn;
    @FXML MenuButton settingsButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup insights
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

        // Create memberRow list
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                setupTeamList();
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load project team, trying again...");
                tryCount++;
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to initialize project team, ending execution.");
        }
    }

    public void setupTeamList() throws SQLException {
        // Setup members table
        ArrayList<MemberRow> rowArrayList = new ArrayList<>();
        Database db = new Database();
        ResultSet rs = db.getProjectMembers(Main.curProject.getPid());
        while (rs.next()) {
            MemberRow mr = new MemberRow();
            mr.setName(rs.getString("name"));
            mr.setMemid(String.valueOf(rs.getInt("memid")));
            mr.setPosition(rs.getString("position"));
            mr.setPhoto("N/A");

            int status = Integer.parseInt(rs.getString("status"));
            switch(status){
                case StatusType.AVAILABLE:
                    mr.setStatus("Available");
                    break;
                case StatusType.AWAY:
                    mr.setStatus("Away");
                    break;
                case StatusType.BUSY:
                    mr.setStatus("Busy");
                    break;
            }

            rowArrayList.add(mr);
        }
        db.closeDB();

        // Convert to array
        MemberRow[] rowList = rowArrayList.toArray(new MemberRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<MemberRow> rows = List.of(rowList);
        ObservableList<MemberRow> memberRows = FXCollections.observableList(rows);

        // Set row data
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memidColumn.setCellValueFactory(new PropertyValueFactory<>("memid"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        memberTable.setItems(memberRows);
    }

    public void exitButton_onClick(Event mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void dashButton_onClick(Event actionEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event actionEvent) throws IOException {
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

    public void addComponentButton_onClick(ActionEvent event) throws IOException {
        Main.show("compSelector");
    }

    public void editTeam_onClick(ActionEvent event) throws IOException {
        Main.show("editProjTeam");
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
