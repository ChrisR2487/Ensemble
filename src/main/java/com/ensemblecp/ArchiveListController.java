package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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

public class ArchiveListController implements Initializable {
    @FXML ImageView settingsBtn;
    @FXML MenuButton settingsButton;
    @FXML
    private TableView<ProjectRow> projectTable;
    @FXML private TableColumn<ProjectRow, String> issueScoreColumn;
    @FXML private TableColumn<ProjectRow, String> managerNameColumn;
    @FXML private TableColumn<ProjectRow, String> tagsColumn;
    @FXML private TableColumn<ProjectRow, String> statusColumn;
    @FXML private TableColumn<ProjectRow, String> titleColumn;
    @FXML private TableColumn<ProjectRow, String> kickoffColumn;
    @FXML private TableColumn<ProjectRow, String> deadlineColumn;

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
    }

    private void setupProjectList() throws SQLException, IOException {
        ArrayList<ProjectRow> rowArrayList = new ArrayList<>();
        Database db = new Database();
        ResultSet rs = db.getProjectsWithManagerName();
       while (rs.next()) {
           if(rs.getBoolean("complete")) {
               ProjectRow pr = new ProjectRow();
               pr.setTitle(rs.getString("title"));
               if(rs.getBoolean("complete")){
                   //if project complete = true, set display to "Complete"
                   pr.setComplete("Complete");
               }
               else{
                   pr.setComplete("Incomplete");
               }
               pr.setKickoff(rs.getDate("kickoff").toString());
               pr.setDeadline(rs.getDate("deadline").toString());
               pr.setPid(String.valueOf(rs.getInt("pid")));
               pr.setIssueScore(String.valueOf(rs.getFloat("issueScore")));
               pr.setManagerName(rs.getString("name"));

               String tags = rs.getString("tag1");
               for (int i = 2; i < 5; i++) {
                   String nextTag = rs.getString("tag" + String.valueOf(i));
                   if (nextTag == null) break;
                   tags += ", " + nextTag;
               }
               pr.setTags(tags);

               rowArrayList.add(pr);
           }
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
        projectTable.setItems(projectRows);

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

    public void dashButton_onClick(Event actionEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event actionEvent) throws IOException {
        Main.show("projList");
    }

    public void archiveButton_onClick(Event mouseEvent) throws IOException {
        Main.show("archiveList");
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
}
