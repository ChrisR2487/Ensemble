package com.ensemblecp;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ProjCreatorController implements Initializable {

    @FXML TextField investmentCostsField;
    @FXML TextField descriptionField;
    @FXML TextField budgetField;
    @FXML TextField titleField;
    @FXML DatePicker kickoffField;
    @FXML DatePicker deadlineField;
    @FXML TextField tag1Field;
    @FXML TextField tag2Field;
    @FXML TextField tag3Field;
    @FXML TextField tag4Field;

    @FXML private TableView<MemberRow> memberTable;
    @FXML private TableColumn<MemberRow, String> nameColumn;
    @FXML private TableColumn<MemberRow, String> positionColumn;
    @FXML private TableColumn<MemberRow, Integer> memIDColumn;
    @FXML private TableColumn<MemberRow, Boolean> selectColumn;
    @FXML private TableColumn<MemberRow, String> statusColumn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //default date values
        LocalDate ld = LocalDate.now();
        kickoffField.setValue(LOCAL_DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ld)));
        ld = ld.plusMonths(1);
        deadlineField.setValue(LOCAL_DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ld)));

        // Create memberRow list
        ArrayList<MemberRow> rowArrayList = new ArrayList<MemberRow>();
        try {
            Database db = new Database();
            ResultSet rs = db.getMembers();
            while (rs.next()) {
                MemberRow mr = new MemberRow();
                mr.setName(rs.getString("name"));
                mr.setPosition(rs.getString("position"));
                mr.setMemid(String.valueOf(rs.getInt("memid")));
                mr.setStatus(String.valueOf(rs.getInt("status")));
                mr.setSelect(false);

                rowArrayList.add(mr);
            }
            db.closeDB();
        }
        catch (SQLException e) {
            e.printStackTrace(); // TODO: Add better handling for loop
        }

        // Convert to array
        MemberRow[] rowList = rowArrayList.toArray(new MemberRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<MemberRow> rows = List.of(rowList);
        ObservableList<MemberRow> memberRows = FXCollections.observableList(rows);

        // Set row data
        memberTable.setEditable(true);
        selectColumn.setCellValueFactory( cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().getSelect()));
        selectColumn.setCellFactory(CheckBoxTableCell.<MemberRow>forTableColumn(selectColumn));
        selectColumn.setEditable(true);
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory("position"));
        memIDColumn.setCellValueFactory(new PropertyValueFactory("memid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory("status"));
        memberTable.setItems(memberRows);
    }

    public static LocalDate LOCAL_DATE (String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }


    @FXML
    public void createProject_onClick(Event e) throws SQLException, IOException {
        // Get data
        HashMap<String, String> info = new HashMap<String, String>();
        info.put("pid", String.valueOf(Math.abs(titleField.getText().hashCode()))); // Use Math.abs() for no negative PIDs
        info.put("title", titleField.getText());                                        // TODO - error handle duplicate values
        info.put("description", descriptionField.getText());

        info.put("investmentCosts", investmentCostsField.getText());                    // TODO - error handle proper data type
        info.put("budget", budgetField.getText());

        info.put("kickoff", kickoffField.getValue().toString());                        // TODO - error handle if date range is reverse
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

        // Get manager ID
        info.put("manid", String.valueOf(Main.account.getId()));
            // TODO: Get manid of current user, set as value of hashmap

        // Add data record
        Database db = new Database();
        ResultSet rs = db.createProject(info);

        // Add team members
            // TODO: Create Team Project table and add members selected

        // Add project to Main cache
        Main.curProject = new Project(rs, null, db);
        Main.projects.add(0, Main.curProject);
        Main.trimCache();

        // Close database
        db.closeDB();

        // Display proper view
        Main.show("projViewScreen");
    }

    @FXML
    public void cancelCreate_onClick(Event e) throws IOException {
        // Cancel project creation
        Main.show("Dashboard");
    }
}
