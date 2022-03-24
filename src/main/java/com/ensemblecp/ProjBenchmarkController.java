package com.ensemblecp;

import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

// ProjViewScreen Class
public class ProjBenchmarkController implements Initializable {
    @FXML AnchorPane root;
    @FXML Label tagsLabel;
    @FXML Label roiLabel;
    @FXML Label budgetLabel;
    @FXML Label kickoffLabel;
    @FXML Label deadlineLabel;
    @FXML Label investmentCostsLabel;
    @FXML Label titleLabel;
    @FXML Label issueScoreLabel;

    @FXML ImageView removeButton;
    @FXML ImageView editButton;
    @FXML ImageView addComponent;
    @FXML ImageView refreshROI;

    @FXML VBox taskListVBox;
    @FXML Label detailLabel;
    @FXML Pane taskDetailPane;
    @FXML Label titleDetail;
    @FXML Label kickoffDetail;
    @FXML Label deadlineDetail;
    @FXML Label descriptionDetail;
    @FXML Label memberDetail;
    @FXML Button actionButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set insight data
        tagsLabel.setText(tagsLabel.getText() + "\n\t" +
                Main.curProject.getTag1() + "\n\t" +
                Main.curProject.getTag2() + "\n\t" +
                Main.curProject.getTag3() + "\n\t" +
                Main.curProject.getTag4());
        roiLabel.setText(roiLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getRoi()));
        budgetLabel.setText(budgetLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getBudget()));
        kickoffLabel.setText(kickoffLabel.getText() + "\n\t" + Main.curProject.getKickoff().toString());
        deadlineLabel.setText(deadlineLabel.getText() + "\n\t" + Main.curProject.getDeadline().toString());
        investmentCostsLabel.setText(investmentCostsLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getInvestmentCosts()));
        issueScoreLabel.setText(issueScoreLabel.getText() + "\n\t" + String.valueOf(Main.curProject.getIssueScore()));
        titleLabel.setText(Main.curProject.getTitle());

        // Setup benchmark timeline
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                setupBenchmarkTimeline();
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load benchmark timeline, trying again...");
                tryCount++;
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to load benchmark timeline, end execution.");
        }

        // List project tasks (Create list and add onClick listeners)
        ArrayList<Pane> taskPanes = new ArrayList<>();
        int indx = 1;
        for(Task task: Main.curProject.getTasks()) {
            // Setup each pane
            Label paneTitle = new Label(task.getTitle());
            paneTitle.setFont(new Font(20.0));
            paneTitle.setLayoutX(6.0);
            paneTitle.setLayoutY(6.0);
            paneTitle.setTextFill(Paint.valueOf("white"));

            Label paneDeadline = new Label("Due: " + task.getDeadline().toString());
            paneDeadline.setFont(new Font(14.0));
            paneDeadline.setLayoutX(6.0);
            paneDeadline.setLayoutY(34.0);
            paneDeadline.setTextFill(Paint.valueOf("white"));

            Pane taskPane = new Pane();
            taskPane.setPrefWidth(260.0);
            taskPane.setPrefHeight(60.0); // 20.0 (title) + 14.0 (due) + ???
            taskPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#2A2A2A"), new CornerRadii(0), new Insets(0))));
            if (indx == Main.curProject.getTasks().size()) VBox.setMargin(taskPane, new Insets(10.0, 10.0, 10.0, 10.0));
            else VBox.setMargin(taskPane, new Insets(10.0, 10.0, 0.0, 10.0));
            taskPane.getChildren().addAll(paneTitle, paneDeadline);

            // Add onClick listener
            taskPane.setOnMouseClicked(mouseEvent -> {
                onShowTaskDetails(task);
            });

            // Add to pane list
            taskPanes.add(taskPane);
            indx++;
        }
        taskListVBox.getChildren().addAll(taskPanes); // Add panes to vbox

        // Setup scroll pane
        ScrollPane sp = new ScrollPane();
        sp.setLayoutX(1140.0);
        sp.setLayoutY(204.0);
        sp.setPrefWidth(280.0);
        sp.setPrefHeight(800.0);
        sp.setContent(taskListVBox);
        sp.setBackground(new Background(new BackgroundFill(Paint.valueOf("#1D1D1E"), new CornerRadii(0), new Insets(0))));

        // Add scrollpane to view
        root.getChildren().add(sp);
    }

    private void onShowTaskDetails(Task task) {
        // Check if visible
        if (!detailLabel.isVisible()) detailLabel.setVisible(true);
        if (!taskDetailPane.isVisible()) taskDetailPane.setVisible(true);

        // Set data
        titleDetail.setText(task.getTitle());
        descriptionDetail.setText(task.getDescription());
        kickoffDetail.setText("Kickoff: " + task.getKickoff().toString());
        deadlineDetail.setText("Deadline: " + task.getDeadline().toString());
        memberDetail.setText("Assigned to MEMID: " + String.valueOf(task.getMemid()));

        // Setup action button listener
            // TODO: Complete listener on click
    }

    public void onTaskAction(Task task, int action) {
        // TODO: Complete listener on click
    }

    private void setupBenchmarkTimeline() throws SQLException {
        // Get ResultSet data
        if (Main.curProject.getTasks() == null) {
            Database db = new Database();
            ResultSet rs = db.getProjectTasks(Main.curProject.getPid());
            Main.curProject.parseAndSaveTasks(rs); // Store tasks locally
            db.closeDB();
        }

        // Create root timeline
        BenchmarkTimeline bt = new BenchmarkTimeline();
        bt.setExpanded(true);
        GanttChart<BenchmarkTimeline> gantt = new GanttChart<>(bt);

        // Set timeline layers
        Layer allLayer = new Layer("All");
        gantt.getLayers().addAll(allLayer);

        // Create TaskTimelines & Create Timelines with TimelineData (Loop here)
        ArrayList<TaskTimeline> timelines = new ArrayList<>();
        for(Task task: Main.curProject.getTasks()) {
            TaskTimeline tt = new TaskTimeline(task.getTitle()); // Create TaskTimeline object
            tt.addActivity(allLayer, new Timeline(new TimelineData(task))); // Set TaskTimeline data with Timeline
            timelines.add(tt); // Add TaskTimeline to timelines collection
        }

        // Add ProjectTimeline collection to CompanyTimeline
        bt.getChildren().addAll(timelines);
        bt.setName(Main.curProject.getTitle() + " Timeline");
        gantt.setDisplayMode(GanttChart.DisplayMode.STANDARD); // Standard view, names and times
        gantt.setTableMenuButtonVisible(false); // Disable add button

        // Stop editing of table
        gantt.getGraphics().setActivityEditingCallback(Timeline.class, editingCallbackParameter -> false);

        // Set layout attributes & add chart to view
        gantt.setLayoutX(217.0);
        gantt.setLayoutY(164.0);
        gantt.setPrefHeight(550.0);
        gantt.setPrefWidth(900.0);
        root.getChildren().add(gantt);
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void dashButton_onClick(Event mouseEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event mouseEvent) throws IOException {
        Main.show("projList");
    }

    public void archiveButton_onClick(Event mouseEvent) {
    }

    public void editProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projEditor");
    }

    public void removeProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projDelete");
    }

    public void addComponentButton_onClick(ActionEvent event) throws IOException {
        Main.show("compCreator");
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

