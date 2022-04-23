package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class TaskEditorController implements Initializable {
    @FXML  Button cancelButton;
    @FXML  Button submitButton;
    @FXML  TextField taskTitle;
    @FXML  TextField taskDesc;
    @FXML  DatePicker kickoffDate;
    @FXML  DatePicker deadlineDate;

    @FXML private TableView<MemberRow> memberTable;
    @FXML private TableColumn<MemberRow, String> nameColumn;
    @FXML private TableColumn<MemberRow, String> positionColumn;
    @FXML private TableColumn<MemberRow, Integer> memIDColumn;
    @FXML private TableColumn<MemberRow, RadioButton> selectColumn;
    @FXML private TableColumn<MemberRow, String> statusColumn;
    ArrayList<MemberRow> rowArrayList = new ArrayList<>();
    public static Task task;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set saved values
        kickoffDate.setValue(ProjCreatorController.LOCAL_DATE(task.getKickoff().toString()));
        deadlineDate.setValue(ProjCreatorController.LOCAL_DATE(task.getDeadline().toString()));
        taskDesc.setText(task.getDescription());
        taskTitle.setText(task.getTitle());

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
        ResultSet rs = db.getProjectMembers(Main.curProject.getPid());
        while (rs.next()) {
            MemberRow mr = new MemberRow();
            mr.setName(rs.getString("name"));
            mr.setMemid(String.valueOf(rs.getInt("memid")));
            mr.setPosition(rs.getString("position"));
            mr.setPhoto("N/A");
            mr.setStatus(rs.getString("status"));
            mr.getAssigned().setToggleGroup(MemberRow.assignGroup);
            rowArrayList.add(mr);
        }

        // Set active members of project to already selected
        for (MemberRow mr: rowArrayList){
            if(Integer.parseInt(mr.getMemid()) == task.getMemid()){
                MemberRow.assignGroup.selectToggle(mr.getAssigned());
                break;
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
        selectColumn.setCellValueFactory(new PropertyValueFactory("assigned"));
        selectColumn.setEditable(true);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memIDColumn.setCellValueFactory(new PropertyValueFactory<>("memid"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        memberTable.setItems(memberRows);
    }

    public void modifyTask_onClick(Event e) throws SQLException, IOException{
        //reset error borders
        kickoffDate.setBorder(null);
        deadlineDate.setBorder(null);
        taskTitle.setBorder(null);

        // Get data
        Database db = new Database();
        HashMap<String, String> info = new HashMap<String, String>();

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

        // Check if task is overdue
        float newIssueScore = Main.curProject.getIssueScore();
        if (IssueScore.checkOverdueTask(Main.curProject.getDeadline().toString()) > 0.1f) { // Is old deadline already overdue?
            newIssueScore += IssueScore.checkOverdueTask(deadline.toString()) - IssueScore.TASK_OVERDUE; // No change if overdue still, remove penalty otherwise
            db.updateIssueScore(Main.curProject.getPid(), newIssueScore);
        }
        else { // Old deadline not overdue, check normally
            newIssueScore += IssueScore.checkOverdueTask(deadline.toString());
            db.updateIssueScore(Main.curProject.getPid(), newIssueScore);
        }

        //validate input for desc
        String desc = taskDesc.getText();
        if (taskDesc.getText().equals("")){
            taskDesc.setBorder(LoginController.INVALID_BORDER);
            return;
        }

        // get memid
        HashMap<String, HashMap<String, String>> members = getAssignedMember();
        HashMap<String,String> row = members.get("1");
        int memid = Integer.parseInt(row.get("memid"));
        info.put("memid", Integer.toString(memid));
        //get desc
        info.put("desc", desc);
        info.put("complete", "false");

        // Save task
        ResultSet taskRS = db.updateProjectTask(Main.curProject.getPid(), task.getTid(), info);
        taskRS.next();
        task.update(taskRS);
        db.closeDB();

        // Move back to benchmark
        Main.show("projBenchmark");
    }

    public HashMap<String, HashMap<String, String>> getAssignedMember(){
        HashMap<String, HashMap<String, String>> retVal = new HashMap<>();
        int memberNum = 1;
        for(MemberRow r: rowArrayList){
            if(r.getAssigned().isSelected()){
                HashMap<String, String> cell = new HashMap<>();
                //member ID
                cell.put("memid", String.valueOf(r.getMemid()));
                retVal.put(String.valueOf(memberNum), cell);

                //member name
                cell.put("name", r.getName());
                retVal.put(String.valueOf(memberNum), cell);

                //member position
                cell.put("position", r.getPosition());
                retVal.put(String.valueOf(memberNum), cell);

                //member status
                cell.put("status", String.valueOf(r.getStatus()));
                retVal.put(String.valueOf(memberNum), cell);

                //member active
                cell.put("active", "true");
                retVal.put(String.valueOf(memberNum), cell);
                memberNum++;
            }
        }
        return retVal;
    }

    public void cancelModify_onClick(Event e) throws IOException {
        // Cancel project creation
        Main.show("projBenchmark");
    }

}
