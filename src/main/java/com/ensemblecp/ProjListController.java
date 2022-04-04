package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ProjListController implements Initializable {
    @FXML private TableView<ProjectRow> projectTable;
    @FXML private TableColumn<ProjectRow, String> issueScoreColumn;
    @FXML private TableColumn<ProjectRow, String> pidColumn;
    @FXML private TableColumn<ProjectRow, String> managerNameColumn;
    @FXML private TableColumn<ProjectRow, String> tagsColumn;
    @FXML private TableColumn<ProjectRow, String> statusColumn;
    @FXML private TableColumn<ProjectRow, String> titleColumn;
    @FXML private TableColumn<ProjectRow, String> kickoffColumn;
    @FXML private TableColumn<ProjectRow, String> deadlineColumn;

    @FXML private RadioButton statusRadio;
    @FXML private RadioButton issueRadio;
    @FXML private RadioButton titleRadio;
    @FXML private RadioButton projRadio;
    @FXML private RadioButton sDateRadio;
    @FXML private RadioButton deadlineRadio;
    @FXML private RadioButton pManagerRadio;
    @FXML private RadioButton tagsRadio;

    @FXML private Pane radioPane;
    @FXML private TextField searchField;

    ArrayList<RadioButton> radioList = new ArrayList<>();

    ArrayList<ProjectRow> rowArrayList = new ArrayList<>();
    ArrayList<ProjectRow> backupList = new ArrayList<>();

    @FXML
    ImageView settingsBtn;
    @FXML MenuButton settingsButton;


    private final Border INVALID_BORDER = new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5)));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create projectRow list
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                setupProjectList();
                break;
            } catch (SQLException | IOException e) {
                System.out.println("Failed to start project list, trying again...");
                tryCount++;
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to initialize project list, stopping program.");
            System.exit(ExitStatusType.FAILED_LOAD);
        }
        radioList.add(statusRadio);
        radioList.add(issueRadio);
        radioList.add(titleRadio);
        radioList.add(projRadio);
        radioList.add(sDateRadio);
        radioList.add(deadlineRadio);
        radioList.add(pManagerRadio);
        radioList.add(tagsRadio);
    }

    private void setupProjectList() throws SQLException, IOException {

        Database db = new Database();
        ResultSet rs = db.getProjectsWithManagerName();
        while (rs.next()) {
            ProjectRow pr = new ProjectRow();
            pr.setTitle(rs.getString("title"));

            if(rs.getBoolean("complete")){
                //if project complete = true, set display to "Complete"
                pr.setComplete("Complete");
            }
            else{
                pr.setComplete("Incomplete");
            }
            //pr.setComplete(String.valueOf(rs.getBoolean("complete")));

            pr.setKickoff(rs.getDate("kickoff").toString());
            pr.setDeadline(rs.getDate("deadline").toString());
            pr.setPid(String.valueOf(rs.getInt("pid")));
            pr.setIssueScore(String.valueOf(rs.getFloat("issueScore")));
            pr.setManagerName(rs.getString("name"));

            String tags = rs.getString("tag1");
            for (int i = 2; i < 5; i++) {
                String nextTag = rs.getString("tag"+String.valueOf(i));
                if (nextTag == null) break;
                tags += ", " + nextTag;
            }
            pr.setTags(tags);

            rowArrayList.add(pr);
            backupList.add(pr);
        }
        db.closeDB();

        // Convert to array
        ProjectRow[] rowList = rowArrayList.toArray(new ProjectRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<ProjectRow> rows = List.of(rowList);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(rows);

        // Set row data
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("complete"));
        kickoffColumn.setCellValueFactory(new PropertyValueFactory<>("kickoff"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        managerNameColumn.setCellValueFactory(new PropertyValueFactory<>("managerName"));
        issueScoreColumn.setCellValueFactory(new PropertyValueFactory<>("issueScore"));
        tagsColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        pidColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));
        //projectTable.setItems(projectRows);
        projectTable.getItems().addAll(projectRows);

        TableView.TableViewSelectionModel<ProjectRow> mod = projectTable.getSelectionModel();
        ObservableList<ProjectRow> sel = mod.getSelectedItems();
        sel.addListener((ListChangeListener<ProjectRow>) change -> {
            int tryCount = 0;
            while (tryCount < Main.ATTEMPT_LIMIT) {
                try {
                    new DashboardController().onChange(change);
                    break;
                } catch (IOException | SQLException e) {
                    System.out.println("Failed to start project view, trying again...");
                    tryCount++;
                }
            }
            if (tryCount == Main.ATTEMPT_LIMIT) {
                // Failed to load dashboard
                System.out.println("Unable to load project view, end listener execution.");
            }
        });
    }

    public void sortButton_onClick(Event actionEvent) {
        radioPane.setBorder(null);

        Comparator <ProjectRow> statusComp = (ProjectRow r1, ProjectRow r2) -> { return r1.getComplete().compareTo(r2.getComplete());};
        Comparator <ProjectRow> issueComp = (ProjectRow r1, ProjectRow r2) -> { return Double.compare(Double.parseDouble(r1.getIssueScore()), Double.parseDouble(r2.getIssueScore()));};
        Comparator <ProjectRow> titleComp = (ProjectRow r1, ProjectRow r2) -> { return r1.getTitle().compareTo(r2.getTitle());};
        Comparator <ProjectRow> pidComp = (ProjectRow r1, ProjectRow r2) -> { return Integer.compare(Integer.parseInt(r1.getPid()), Integer.parseInt(r2.getPid()));};
        Comparator <ProjectRow> kickoffComp = (ProjectRow r1, ProjectRow r2) -> { return LocalDate.parse(r1.getKickoff()).compareTo(LocalDate.parse(r2.getKickoff()));};
        Comparator <ProjectRow> deadlineComp = (ProjectRow r1, ProjectRow r2) -> { return LocalDate.parse(r1.getDeadline()).compareTo(LocalDate.parse(r2.getDeadline()));};
        Comparator <ProjectRow> managerComp = (ProjectRow r1, ProjectRow r2) -> { return r1.getManagerName().compareTo(r2.getManagerName());};
        Comparator <ProjectRow> tagsComp = (ProjectRow r1, ProjectRow r2) -> { return r1.getTags().compareTo(r2.getTags());};

        int numberSelected = 0;
        int selectedIndex= 0;
        //check to make sure a check option is selected and mark which ones are
        for(int i = 0; i < radioList.size(); i++){
            if(radioList.get(i).isSelected()){
                selectedIndex = i;
                numberSelected++;
            }
        }
        if(numberSelected != 1){
            //user has selected an invalid number of radio buttons
            radioPane.setBorder(INVALID_BORDER);
            return;
        }

        //sort the arrayList depending on which radiobutton is checked
        switch(selectedIndex){
            case 0:
                //sort by status
                rowArrayList.sort(statusComp);
                break;
            case 1:
                //sort by issue score
                rowArrayList.sort(issueComp);
                break;
            case 2:
                //sort by title
                rowArrayList.sort(titleComp);
                break;
            case 3:
                //sort by project id
                rowArrayList.sort(pidComp);
                break;
            case 4:
                //sort by kickoff
                rowArrayList.sort(kickoffComp);
                break;
            case 5:
                //sort by deadline
                rowArrayList.sort(deadlineComp);
                break;
            case 6:
                //sort by manager
                rowArrayList.sort(managerComp);
                break;
            case 7:
                //sort by tags
                rowArrayList.sort(tagsComp);
                break;
            default:
                System.out.println("ERROR SORTING PROJECT TABLE");
        }

        // Convert to array
        ProjectRow[] rowList = rowArrayList.toArray(new ProjectRow[rowArrayList.size()]);
        // Cast to ObservableList
        List<ProjectRow> rows = List.of(rowList);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(rows);

        //clear project table data and fill with sorted list
        projectTable.getItems().clear();
        projectTable.getItems().addAll(projectRows);
    }

    public void incompleteButton_onClick(Event actionEvent) {
        //remove all projects which aren't incomplete
        ProjectRow[] rowList;

        rowArrayList.clear();
        rowArrayList.addAll(backupList);

        for (Iterator<ProjectRow> it = rowArrayList.iterator(); it.hasNext(); )
            if (it.next().getComplete().equals("Complete")) {
                it.remove();
            }
        rowList = rowArrayList.toArray(new ProjectRow[rowArrayList.size()]);
        // Cast to ObservableList
        List<ProjectRow> rows = List.of(rowList);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(rows);

        projectTable.getItems().clear();
        projectTable.getItems().addAll(projectRows);
    }

    public void overdueButton_onClick(Event actionEvent) {
        //remove all projects which are not overdue from the list
        ProjectRow[] rowList;

        rowArrayList.clear();
        rowArrayList.addAll(backupList);

        for (Iterator<ProjectRow> it = rowArrayList.iterator(); it.hasNext(); )
            if (LocalDate.parse(it.next().getDeadline()).compareTo(LocalDate.now()) > 0) {
                it.remove();
            }
        rowList = rowArrayList.toArray(new ProjectRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<ProjectRow> rows = List.of(rowList);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(rows);

        projectTable.getItems().clear();
        projectTable.getItems().addAll(projectRows);
    }

    public void resetTableButton_onClick(Event actionEvent) {
        rowArrayList.clear();
        rowArrayList.addAll(backupList);
        ProjectRow[] rowList = rowArrayList.toArray(new ProjectRow[rowArrayList.size()]);
        // Cast to ObservableList
        List<ProjectRow> rows = List.of(rowList);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(rows);

        projectTable.getItems().clear();
        projectTable.getItems().addAll(projectRows);
    }

    //todo - add more filters - discuss with the team which ones to add

    public void searchButton_onClick(Event actionEvent){
        //filter table to contain only projects with search query in title name
        String term = searchField.getText().toLowerCase();

        ProjectRow[] rowList;
        for (Iterator<ProjectRow> it = rowArrayList.iterator(); it.hasNext(); )
            if (!it.next().getTitle().toLowerCase().contains(term)) {
                it.remove();
            }
        rowList = rowArrayList.toArray(new ProjectRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<ProjectRow> rows = List.of(rowList);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(rows);

        projectTable.getItems().clear();
        projectTable.getItems().addAll(projectRows);

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

    public void add_onClick() throws IOException {
        Main.show("projCreator");
    }

    public void exitButton_onClick(Event mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }
    public void settings_Hover(){
        settingsBtn.setOpacity(0.5);
    }
    public void settings_HoverOff(){
        settingsBtn.setOpacity(1.0);
    }
}
