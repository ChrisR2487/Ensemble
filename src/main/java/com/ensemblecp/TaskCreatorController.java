package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class TaskCreatorController implements Initializable {
    @FXML  Button cancelButton;
    @FXML  Button submitButton;
    @FXML  TextField taskTitle;
    @FXML  TextField taskDesc;
    @FXML  DatePicker kickoffDate;
    @FXML  DatePicker deadlineDate;

    @FXML private TableView<MemberRow> memberTable;
    @FXML private TableColumn<MemberRow, String> photoColumn;
    @FXML private TableColumn<MemberRow, String> nameColumn;
    @FXML private TableColumn<MemberRow, String> positionColumn;
    @FXML private TableColumn<MemberRow, Integer> memIDColumn;
    @FXML private TableColumn<MemberRow, CheckBox> selectColumn;
    @FXML private TableColumn<MemberRow, String> statusColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //default date values
        LocalDate ld = LocalDate.now();
        kickoffDate.setValue(ProjCreatorController.LOCAL_DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ld)));
        ld = ld.plusMonths(1);
        deadlineDate.setValue(ProjCreatorController.LOCAL_DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ld)));

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
            mr.setPhoto("N/A"); // TODO: Get correct file for member photo
            mr.setStatus(rs.getString("status"));

            rowArrayList.add(mr);
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
        memIDColumn.setCellValueFactory(new PropertyValueFactory<>("memid"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        memberTable.setItems(memberRows);
    }

    public void createTask_onClick(Event e) throws SQLException, IOException{

        //reset error borders
        kickoffDate.setBorder(null);
        deadlineDate.setBorder(null);
        taskTitle.setBorder(null);

        // Get data
        Database db = new Database();
        HashMap<String, String> info = new HashMap<String, String>();

        //get task id
        info.put("tid", String.valueOf(Math.abs(taskTitle.getText().hashCode())));

        //validate input for title
        String title = taskTitle.getText();
        if(title.equals("")){
            //empty input
            taskTitle.setBorder(LoginController.INVALID_BORDER);
            return;
        }
        info.put("title", title);

        //error checking for date range
        LocalDate kickOff = kickoffDate.getValue();
        LocalDate deadline = deadlineDate.getValue();
        if(kickOff.compareTo(deadline) >= 0){
            kickoffDate.setBorder(LoginController.INVALID_BORDER);
            deadlineDate.setBorder(LoginController.INVALID_BORDER);
            return;
        }
        info.put("kickoff", kickOff.toString());
        info.put("deadline", deadline.toString());

        //validate input for desc
        String desc = taskDesc.getText();
        if (taskDesc.getText().equals("")){
            taskDesc.setBorder(LoginController.INVALID_BORDER);
            return;
        }
        info.put("desc", desc);
        info.put("complete", "false");

        // Save task
            // TODO: Insert task record locally and on database

        // Return to overview screen
        Main.show("projOverview");
    }

    public void cancelCreate_onClick(Event e) throws IOException {
        // Cancel project creation
        Main.show("projOverview");
    }

}
