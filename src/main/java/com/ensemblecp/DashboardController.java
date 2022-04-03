package com.ensemblecp;

// Java libraries

import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private Button exitButton;
    @FXML private AnchorPane root;
    @FXML private TableView<ProjectRow> projectTable;
    @FXML private TableColumn<ProjectRow, String> statusColumn;
    @FXML private TableColumn<ProjectRow, String> titleColumn;
    @FXML private TableColumn<ProjectRow, String> remainColumn;
    @FXML private TableColumn<ProjectRow, String> kickoffColumn;
    @FXML private TableColumn<ProjectRow, String> deadlineColumn;

    @FXML private TableView membersTable;
    @FXML private TableColumn<MemberRow, String> nameColumn;
    @FXML private TableColumn<MemberRow, String> memberStatusColumn;

    @FXML ImageView settingsBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup project list
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                setupProjectList();
                break;
            } catch (SQLException | IOException e) {
                System.out.println("Failed to start dashboard, trying again...");
                tryCount++;
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to initialize dashboard with database info, stopping program.");
            System.exit(ExitStatusType.FAILED_LOAD);
        }

        // Setup company timeline
        int tryCount2 = 0;
        while (tryCount2 < Main.ATTEMPT_LIMIT) {
            try {
                setupCompanyTimeline();
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load company timeline, trying again...");
                tryCount2++;
            }
        }
        if (tryCount2 == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to load company timeline, end execution.");
        }
    }

    private void setupProjectList() throws SQLException, IOException {
        ArrayList<ProjectRow> rowArrayList = new ArrayList<>();
        Database db = new Database();
        ResultSet rs = db.getProjects();
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

            pr.setRemain(String.valueOf(rs.getInt("budget") - rs.getInt("investmentCosts")));
            pr.setKickoff(rs.getDate("kickoff").toString());
            pr.setDeadline(rs.getDate("deadline").toString());
            pr.setPid(String.valueOf(rs.getInt("pid")));
            rowArrayList.add(pr);
        }
        db.closeDB();

        // Convert to array
        ProjectRow[] rowList = rowArrayList.toArray(new ProjectRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<ProjectRow> rows = List.of(rowList);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(rows);

        // Set row data
        titleColumn.setCellValueFactory(new PropertyValueFactory("title"));
        remainColumn.setCellValueFactory(new PropertyValueFactory("remain"));
        statusColumn.setCellValueFactory(new PropertyValueFactory("complete"));
        kickoffColumn.setCellValueFactory(new PropertyValueFactory("kickoff"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory("deadline"));
        projectTable.setItems(projectRows);
        TableView.TableViewSelectionModel<ProjectRow> mod = projectTable.getSelectionModel();
        ObservableList<ProjectRow> sel = mod.getSelectedItems();
        sel.addListener((ListChangeListener<ProjectRow>) change -> {
            int tryCount = 0;
            while (tryCount < Main.ATTEMPT_LIMIT) {
                try {
                    onChange(change);
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

    private void setupCompanyTimeline() throws SQLException {
        // Get ResultSet data
        Database db = new Database();
        ResultSet rs = db.getTimelines();

        // Create root timeline
        CompanyTimeline ct = new CompanyTimeline();
        ct.setExpanded(true);
        GanttChart<CompanyTimeline> gantt = new GanttChart<>(ct);

        // Set timeline layers
        Layer allLayer = new Layer("All");
        gantt.getLayers().addAll(allLayer);

        // Create ProjectTimelines & Create Timelines with TimelineData (Loop here)
        ArrayList<ProjectTimeline> timelines = new ArrayList<>();
        while (rs.next()) {
            ProjectTimeline pj = new ProjectTimeline(rs.getString("title")); // Create ProjectTimeline object
            pj.addActivity(allLayer, new Timeline(new TimelineData(rs))); // Set ProjectTimeline data with Timeline
            timelines.add(pj); // Add ProjectTimeline to timelines collection
        }
        db.closeDB(); // Close database

        // Add ProjectTimeline collection to CompanyTimeline
        ct.getChildren().addAll(timelines);
        ct.setName("Projects");
        gantt.setDisplayMode(GanttChart.DisplayMode.STANDARD); // Standard view, names and times
        gantt.setTableMenuButtonVisible(false); // Disable add button

        // Stop editing of table
        gantt.getGraphics().setActivityEditingCallback(Timeline.class, editingCallbackParameter -> false);

        // Set layout attributes & add chart to view
        gantt.setLayoutX(212.0);
        gantt.setLayoutY(103.0);
        gantt.setPrefHeight(638.0);
        gantt.setPrefWidth(1181.0);
        root.getChildren().add(gantt);
    }

    void onChange(ListChangeListener.Change change) throws IOException, SQLException {
        // Get data
        ObservableList<ProjectRow> selectedList = change.getList();
        int pid = Integer.parseInt(selectedList.get(0).getPid());

        // Check if project is in cache
        Project projInCache = Main.inCache(pid); // Get pid of current project, check if in cache
        if (projInCache == null) {
            // Not in cache, get project data and save to cache
            Database db = new Database();
            projInCache = new Project(db.getProject(pid), db.getProjectComponents(pid), db);
            db.closeDB();
            Main.projects.add(0, projInCache);
            Main.trimCache();
        }
        else {
            // Project in cache, change cache ordering
            Main.refreshCache(projInCache);
        }
        Main.curProject = projInCache;

        // Display screen
        Main.show("projOverview");
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

    public void settings_Hover(){
        settingsBtn.setOpacity(0.5);
    }
    public void settings_HoverOff(){
        settingsBtn.setOpacity(1.0);
    }

}