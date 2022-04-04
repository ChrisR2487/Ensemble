package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private TableColumn<MemberRow, CheckBox> selectColumn;
    @FXML private TableColumn<MemberRow, String> statusColumn;

    private final Border INVALID_BORDER = new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5)));

    ArrayList<MemberRow> rowArrayList = new ArrayList<MemberRow>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //default date values
        LocalDate ld = LocalDate.now();
        kickoffField.setValue(LOCAL_DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ld)));
        ld = ld.plusMonths(1);
        deadlineField.setValue(LOCAL_DATE(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ld)));

        // fill memberRow list
        try {
            Database db = new Database();
            ResultSet rs = db.getMembers();
            while (rs.next()) {
                MemberRow mr = new MemberRow();
                mr.setName(rs.getString("name"));
                mr.setPosition(rs.getString("position"));
                mr.setMemid(String.valueOf(rs.getInt("memid")));
                mr.setStatus(String.valueOf(rs.getInt("status")));
                mr.setSelect(new CheckBox());

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
        //selectColumn.setCellValueFactory( cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().getSelect()));
        //selectColumn.setCellFactory(CheckBoxTableCell.<MemberRow>forTableColumn(selectColumn));
        selectColumn.setCellValueFactory(new PropertyValueFactory("select"));
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
        //reset error borders
        kickoffField.setBorder(null);
        deadlineField.setBorder(null);
        investmentCostsField.setBorder(null);
        budgetField.setBorder(null);
        titleField.setBorder(null);

        // Get data
        Database db = new Database();
        HashMap<String, String> info = new HashMap<String, String>();
        info.put("pid", String.valueOf(Math.abs(titleField.getText().hashCode()))); // Use Math.abs() for no negative PIDs

        String title = titleField.getText();
        if(title.equals("")){
            //empty input
            titleField.setBorder(INVALID_BORDER);
            return;
        }
        else if(db.getProjectByName(title).isBeforeFirst()){
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
        info.put("kickoff", kickOff.toString());
        info.put("deadline", deadline.toString());

        info.put("tag1", tag1Field.getText());
        info.put("tag2", tag2Field.getText());
        info.put("tag3", tag3Field.getText());
        info.put("tag4", tag4Field.getText());
        info.put("complete", "false");

        // Get issue score
        float score = 0.0f; // Base score
        score += IssueScore.checkOverdue(info.get("deadline"));
        score += IssueScore.checkOverbudget(Float.parseFloat(info.get("investmentCosts")), Float.parseFloat(info.get("budget")));
        info.put("issueScore", String.valueOf(score));

        // Get manager ID
        info.put("manid", String.valueOf(Main.account.getId()));

        // Get roi
        Float roi = db.getROI(info);
        info.put("roi", Float.toString(roi));

        // Add data record
        ResultSet rs = db.createProject(info);

        // Add team members
        String charPid = Project.IDtoChars(Integer.parseInt(info.get("pid")));
        db.addMembers(getSelectedMembers(), charPid);

        // Add project to Main cache
        Main.curProject = new Project(rs, null, db);
        Main.projects.add(0, Main.curProject);
        Main.trimCache();

        // Close database
        db.closeDB();

        // Display proper view
        Main.show("projOverview");
    }

    @FXML
    public void cancelCreate_onClick(Event e) throws IOException {
        // Cancel project creation
        Main.show("projList");
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
}