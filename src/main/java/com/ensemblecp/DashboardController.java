package com.ensemblecp;

// Java libraries

import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private AnchorPane root;
    @FXML private TableView<ProjectRow> projectTable;
    @FXML private TableColumn<ProjectRow, String> statusColumn;
    @FXML private TableColumn<ProjectRow, String> titleColumn;
    @FXML private TableColumn<ProjectRow, String> remainColumn;
    @FXML private TableColumn<ProjectRow, String> kickoffColumn;
    @FXML private TableColumn<ProjectRow, String> deadlineColumn;

    @FXML private TableView<MemberRow> membersTable;
    @FXML private TableColumn<MemberRow, String> nameColumn;
    @FXML private TableColumn<MemberRow, String> memberStatusColumn;

    @FXML ImageView settingsBtn;
    @FXML MenuButton settingsButton;

    private ArrayList<Pane> notificationPanes;
    @FXML VBox notificationVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup project list
        Database db = null;
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                db = new Database();
                setupProjectList(db);
                break;
            } catch (SQLException | IOException e) {
                System.out.println("Failed to start dashboard, trying again...");
                tryCount++;
                try {
                    db.closeDB();
                } catch (NullPointerException | SQLException ignored) { }
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to initialize dashboard with database info, end execution.");
        }

        // Setup company timeline
        int tryCount2 = 0;
        while (tryCount2 < Main.ATTEMPT_LIMIT) {
            try {
                setupCompanyTimeline(db);
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

        int tryCount4 = 0;
        while (tryCount4 < Main.ATTEMPT_LIMIT) {
            try {
                setupMembers(db);
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load members, trying again...");
                tryCount4++;
            }
        }
        if (tryCount4 == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to load members, end execution.");
        }

        //Setup Notifications
        int tryCount3 = 0;
        while (tryCount3 < Main.ATTEMPT_LIMIT) {
            try {
                setupNotifications(db);
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load notifications, trying again...");
                tryCount3++;
            }
        }
        if (tryCount3 == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to load notifications, end execution.");
        }
        try {
            db.closeDB();
        } catch (NullPointerException | SQLException ignored) { }
    }

    private void setupMembers(Database db) throws SQLException {
        ArrayList<MemberRow> rowArrayList = new ArrayList<>();
        ResultSet rs = db.getMembers();
        while (rs.next()) {
            MemberRow mr = new MemberRow();
            mr.setName(rs.getString("name"));
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

        // Convert to array
        MemberRow[] rowList = rowArrayList.toArray(new MemberRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<MemberRow> rows = List.of(rowList);
        ObservableList<MemberRow> projectRows = FXCollections.observableList(rows);

        // Set row data
        memberStatusColumn.setCellValueFactory(new PropertyValueFactory("status"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        membersTable.setItems(projectRows);
    }

    private void setupNotifications(Database db) throws SQLException {
        // List notifications (Create list and add onClick listeners)
        notificationPanes = new ArrayList<>();
        int manid = Main.account.getId();
        ResultSet issue = db.getManagerIssues(manid);

        while(issue.next()) {
            // Setup each pane
            Label paneProjName = new Label(issue.getString("title"));
            paneProjName.setFont(new Font(20.0));
            paneProjName.setLayoutX(6.0);
            paneProjName.setLayoutY(6.0);
            paneProjName.setTextFill(Paint.valueOf("white"));

            Label paneMessage = new Label(issue.getString("message"));
            paneMessage.setFont(new Font(14.0));
            paneMessage.setLayoutX(6.0);
            paneMessage.setLayoutY(34.0);
            paneMessage.setTextFill(Paint.valueOf("white"));

            Pane notificationPane = new Pane();
            notificationPane.setMinWidth(406.0);
            notificationPane.setMinHeight(60.0);
            notificationPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#2A2A2A"), new CornerRadii(0), new Insets(0))));
            VBox.setMargin(notificationPane, new Insets(10.0, 10.0, 10.0, 10.0));
            notificationPane.getChildren().addAll(paneProjName, paneMessage);

            // Add to pane list
            notificationPanes.add(notificationPane);
        }
        notificationVBox.getChildren().addAll(notificationPanes); // Add panes to vbox

        // Setup scroll pane

        ScrollPane sp = new ScrollPane();
        sp.setStyle("-fx-background: #1D1D1E; -fx-background-color: #1D1D1E");
        sp.setLayoutX(1439.0);
        sp.setLayoutY(103.0);
        sp.setPrefWidth(443.0);
        sp.setPrefHeight(638.0);
        sp.setContent(notificationVBox);
        //sp.setBackground(new Background(new BackgroundFill(Paint.valueOf("#1D1D1E"), new CornerRadii(0), new Insets(0))));

        // Add scrollpane to view
        root.getChildren().add(sp);
        db.closeDB();

    }


    private void setupProjectList(Database db) throws SQLException, IOException {
        ArrayList<ProjectRow> rowArrayList = new ArrayList<>();
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
                    e.printStackTrace();
                    tryCount++;
                }
            }
            if (tryCount == Main.ATTEMPT_LIMIT) {
                // Failed to load dashboard
                System.out.println("Unable to load project view, end listener execution.");
            }
        });
    }

    private void setupCompanyTimeline(Database db) throws SQLException {
        // Get ResultSet data
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

        // Setup css
        ///System.out.println(gantt.getTimeline().getEventline().getUserAgentStylesheet());
        ///System.out.println(gantt.getGraphics().getUserAgentStylesheet());
        ///System.out.println(gantt.getTimeline().getDateline().getUserAgentStylesheet());
        ///System.out.println(gantt.getStylesheets().toString());

        /*gantt.getStylesheets().clear(); // Remove old
        gantt.getStylesheets().add("file:src/main/resources/css/ganttchart.css"); // Add new
        gantt.applyCss();*/

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

    public void exitButton_onClick(ActionEvent mouseEvent) {
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

    public void settings_Hover(){
        settingsBtn.setOpacity(0.5);
    }
    public void settings_HoverOff(){
        settingsBtn.setOpacity(1.0);
    }

}