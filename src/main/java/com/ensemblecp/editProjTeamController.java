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
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class editProjTeamController implements Initializable {
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
    @FXML private TableColumn<MemberRow, String> photoColumn;
    @FXML private TableColumn<MemberRow, CheckBox> selectColumn;
    @FXML private TableColumn<MemberRow, String> statusColumn;

    @FXML ImageView removeButton;
    @FXML ImageView editButton;
    @FXML ImageView addComponent;
    @FXML ImageView refreshROI;

    ArrayList<MemberRow> rowArrayList = new ArrayList<>();

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
        Database db = new Database();
        ResultSet rs = db.getMembers();
        while (rs.next()) {
            MemberRow mr = new MemberRow();
            mr.setSelect(new CheckBox());
            mr.setName(rs.getString("name"));
            mr.setMemid(String.valueOf(rs.getInt("memid")));
            mr.setPosition(rs.getString("position"));

            int status = Integer.parseInt(rs.getString("status"));
            switch(status){
                case MemberState.AVAILABLE:
                    mr.setStatus("Available");
                    break;
                //todo - add more statuses
            }
            mr.setPhoto("N/A"); // TODO: Get correct file for member photo

            rowArrayList.add(mr);
        }

        // Set active members of project to already selected
        ResultSet currTeam = db.getProjectMembers(Main.curProject.getPid());
        while(currTeam.next()){
            int memId = currTeam.getInt("memid");
            for (MemberRow mr: rowArrayList){
                if(Integer.parseInt(mr.getMemid()) == memId){
                    mr.getSelect().setSelected(true);
                }
            }
        }
        db.closeDB();

        // Convert to array
        MemberRow[] rowList = rowArrayList.toArray(new MemberRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<MemberRow> rows = List.of(rowList);
        ObservableList<MemberRow> memberRows = FXCollections.observableList(rows);

        // Set row data
        memberTable.setEditable(true);
        selectColumn.setCellValueFactory(new PropertyValueFactory("select"));
        selectColumn.setEditable(true);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memidColumn.setCellValueFactory(new PropertyValueFactory<>("memid"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        memberTable.setItems(memberRows);
    }

    public void editTeamSubmit_onClick(ActionEvent actionEvent) throws SQLException, IOException {
        //create database
        Database db = new Database();
        String charPid = Project.IDtoChars(Main.curProject.getPid());

        //remove all rows from previous members table
        db.dropMembers(charPid);
        // Add team members
        db.addMembers(getSelectedMembers(), charPid);

        Main.show("projTeam");
    }

    //method to return arraylist of selected members
    public HashMap<String, HashMap<String, String>> getSelectedMembers(){
        HashMap<String, HashMap<String, String>> retVal = new HashMap<>();
        int memberNum = 1;
        for(MemberRow r: rowArrayList){
            if(r.getSelect().isSelected()){
                HashMap<String, String> cell = new HashMap<>();
                //member ID
                cell.put("memid", String.valueOf(r.getMemid()));
                retVal.put(String.valueOf(memberNum), cell);
                memberNum++;
            }
        }
        return retVal;
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void dashButton_onClick(Event actionEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event actionEvent) throws IOException {
        Main.show("projList");
    }

    public void archiveButton_onClick(Event actionEvent) {
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
        Main.show("compCreator");
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
}
