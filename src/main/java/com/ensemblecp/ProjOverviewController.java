package com.ensemblecp;

import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

// ProjViewScreen Class
public class ProjOverviewController implements Initializable {
    @FXML Pane parentPane;
    @FXML ScrollPane sp;
    @FXML ListView<Hyperlink> fileList;
    @FXML Label tagsLabel;
    @FXML Label roiLabel;
    @FXML Label budgetLabel;
    @FXML Label kickoffLabel;
    @FXML Label deadlineLabel;
    @FXML Label investmentCostsLabel;
    @FXML Label titleLabel;
    @FXML Label issueScoreLabel;
    @FXML Label descLabel;

    @FXML ImageView removeButton;
    @FXML ImageView editButton;
    @FXML ImageView addComponent;
    @FXML ImageView refreshROI;

    @FXML ImageView settingsBtn;
    @FXML MenuButton settingsButton;

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
        descLabel.setMaxWidth(390.0);
        descLabel.setText(Main.curProject.getDescription());
        titleLabel.setText(Main.curProject.getTitle());

        // Set file data
        Database db = null;
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                db = new Database();
                setupFileList(db);
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load file list, trying again...");
                tryCount++;
                try {
                    db.closeDB();
                } catch (NullPointerException | SQLException ignored) { }
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load file list
            System.out.println("Unable to load file list.");
        }

        // set issues data
            // TODO: List recent issues not seen/done

        // Set component data
        setupComponents();

        // Set timeline data
        int tryCount2 = 0;
        while (tryCount2 < Main.ATTEMPT_LIMIT) {
            try {
                setupTimeline(db);
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load timeline, trying again...");
                e.printStackTrace();
                tryCount2++;
            }
        }
        if (tryCount2 == Main.ATTEMPT_LIMIT) {
            // Failed to load file list
            System.out.println("Unable to load timeline.");
        }
        try {
            db.closeDB();
        } catch (NullPointerException | SQLException ignored) { }
    }

    private void setupTimeline(Database db) throws SQLException {
        // Get ResultSet data
        if (Main.curProject.getTasks() == null) {
            ResultSet rs = db.getProjectTasks(Main.curProject.getPid());
            Main.curProject.parseAndSaveTasks(rs); // Store tasks locally
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
        gantt.setDisplayMode(GanttChart.DisplayMode.GRAPHICS_ONLY); // Include only gantt
        gantt.setTableMenuButtonVisible(false); // Disable add button

        // Stop editing of table
        gantt.getGraphics().setActivityEditingCallback(Timeline.class, editingCallbackParameter -> false);

        // Set layout attributes & add chart to view
        gantt.setPrefHeight(250.0);
        gantt.setPrefWidth(1200.0);
        gantt.setLayoutX(10.0);
        gantt.setLayoutY(15.0);
        parentPane.getChildren().add(gantt);
    }

    private void setupFileList(Database db) throws SQLException {
        // Check if already saved
        if (Main.curProject.getLinks() != null) {
            Hyperlink[] arr = new Hyperlink[Main.curProject.getLinks().size()];
            Main.curProject.getLinks().toArray(arr);
            List<Hyperlink> list = List.of(arr);
            fileList.setItems(FXCollections.observableList(list));
            return;
        }

        // Get files
        ResultSet rs = db.getProjectFiles(Main.curProject.getPid());
        ArrayList<Hyperlink> links = new ArrayList<>();
        while(rs.next()) {
            Hyperlink link = new Hyperlink(rs.getString("filename"));
            HashMap<String, Object> map = new HashMap<>();
            int filid = rs.getInt("filid");
            link.setOnAction(actionEvent -> {
                int tryCount = 0;
                while (tryCount < Main.ATTEMPT_LIMIT) {
                    try {
                        Database db2 = new Database();
                        db2.downloadFile(filid);
                        db2.closeDB();
                        break;
                    } catch (SQLException | IOException e) {
                        System.out.println("Failed to download file, trying again...");
                        e.printStackTrace();
                        tryCount++;
                    }
                }
                if (tryCount == Main.ATTEMPT_LIMIT) {
                    // Failed to download file
                    System.out.println("Unable to download file.");
                }
            });
            links.add(link);
        }
        Main.curProject.setLinks(links);
        Hyperlink[] arr = new Hyperlink[links.size()];
        links.toArray(arr);
        List<Hyperlink> list = List.of(arr);
        fileList.setItems(FXCollections.observableList(list));
    }

    private void setupComponents() {
        // Set component data
        sp.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(0), new Insets(0))));
        parentPane.setPadding(new Insets(10, 10, 10, 10));
        double layY = 795.0;
        double layX = 10.0;
        for (Component comp: Main.curProject.getComponents()) {
            // Setup label
            Label compLabel = new Label(comp.getTitle());
            compLabel.setLayoutY(layY);
            compLabel.setLayoutX(layX);
            compLabel.setFont(new Font(35.0));
            compLabel.setTextFill(Paint.valueOf("white"));

            // Setup options button
            MenuButton compActions = new MenuButton();
            compActions.setFont(new Font(16.0));
            compActions.setLayoutY(layY+58.0);
            compActions.setLayoutX(layX + 1145.0);
            compActions.setStyle("-fx-background-color: transparent;");
            MenuItem modifyComp = new MenuItem("Edit");
            modifyComp.setOnAction(event -> {
                CompEditorController.component = comp;
                int tryCount = 0;
                while (tryCount < Main.ATTEMPT_LIMIT) {
                    try {
                        Main.show("compEditor");;
                        break;
                    } catch (IOException e) {
                        System.out.println("Failed to load components, trying again...");
                        tryCount++;
                    }
                }
                if (tryCount == Main.ATTEMPT_LIMIT) {
                    // Failed to load file list
                    System.out.println("Unable to load components.");
                }
            });
            MenuItem deleteComp = new MenuItem("Delete");
            deleteComp.setOnAction(event -> {
                CompDeleteController.component = comp;
                int tryCount = 0;
                while (tryCount < Main.ATTEMPT_LIMIT) {
                    try {
                        Main.show("compDelete");;
                        break;
                    } catch (IOException e) {
                        System.out.println("Failed to load components, trying again...");
                        tryCount++;
                    }
                }
                if (tryCount == Main.ATTEMPT_LIMIT) {
                    // Failed to load file list
                    System.out.println("Unable to load components.");
                }
            });
            compActions.getItems().addAll(modifyComp, deleteComp);

            // Setup pane
            Pane compPane = new Pane();
            compPane.setLayoutX(layX);
            compPane.setLayoutY(layY+50.0);
            compPane.setPrefWidth(1200.0);
            compPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#1D1D1E"), new CornerRadii(0), new Insets(0))));
            compPane.setPadding(new Insets(0, 0, 8.0, 0));

            double layPartY = 8.0;
            for (Component.Part part: comp.getParts()) {
                switch(part.getType()) {
                    case 'L' -> {
                        Label partList = new Label();
                        partList.setStyle("-fx-font-size: 20.0; -fx-text-fill: white;");
                        partList.setLayoutY(layPartY);
                        partList.setLayoutX(10.0);
                        int prevPos = 0;
                        int commaPos = part.getData().indexOf(',', prevPos);
                        if (commaPos == -1) {
                            partList.setText("\u2022 " + part.getData());
                            layPartY += 30.0;
                        }
                        else {
                            String listStr = "\u2022 " + part.getData().substring(prevPos, commaPos);
                            prevPos = commaPos+1;
                            commaPos = part.getData().indexOf(',', prevPos);
                            layPartY += 30.0;
                            while (commaPos != -1){
                                listStr += "\n\u2022 " + part.getData().substring(prevPos, commaPos);
                                prevPos = commaPos+1;
                                commaPos = part.getData().indexOf(',', prevPos);
                                layPartY += 30.0;
                            }
                            listStr += "\n\u2022 " + part.getData().substring(prevPos);
                            partList.setText(listStr);
                            layPartY += 30.0;
                        }
                        compPane.getChildren().add(partList);
                    }
                    default -> {
                        Label partLabel = new Label(part.getData());
                        partLabel.setFont(new Font(20.0));
                        partLabel.setTextFill(Paint.valueOf("white"));
                        partLabel.setLayoutY(layPartY);
                        partLabel.setLayoutX(10.0);
                        compPane.getChildren().add(partLabel);
                        layPartY += 30.0;
                    }
                }
            }

            // Add to sp and increment layY
            parentPane.getChildren().addAll(compLabel, compPane, compActions);
            layY += layPartY + 70.0;
        }
    }

    public void exitButton_onClick(Event mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void dashButton_onClick(Event mouseEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event mouseEvent) throws IOException {
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

    public void addFile_onClick(ActionEvent actionEvent) throws SQLException {
        // Browse for file
        File file = Main.browseForFile();
        if (file == null) return;
        if (file.length() > Main.FILE_SIZE_LIMIT) {
            // File too large, display error
                // TODO: Implement error check
        }

        // Get privacy level
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Public");
        dialog.setTitle("Ensemble");
        dialog.setHeaderText("File security");
        dialog.setContentText("Would you like this file to be public or private? (Only project managers can view private files)");
        dialog.getItems().add("Private");
        dialog.getItems().add("Public");
        boolean isPrivate = dialog.showAndWait().get().equals("Private");;

        // Upon receiving file, check extension and try to upload
        String fullName = file.getName();
        int period = fullName.lastIndexOf('.');
        String extension = fullName.substring(period+1);
        switch(extension) {
            case "pdf", "txt", "png", "jpg", "doc", "docx" -> {
                // Valid type, save file
                Database db = new Database();
                db.createFile(file);
                // Add file to project
                db.addFiles(Main.curProject.getPid(), new int[] {fullName.hashCode()}, new boolean[] {isPrivate});

                // Setup hyperlink and add to project object
                Hyperlink link = new Hyperlink(fullName);
                link.setOnAction(event -> {
                    int tryCount = 0;
                    while (tryCount < Main.ATTEMPT_LIMIT) {
                        try {
                            Database db2 = new Database();
                            db2.downloadFile(fullName.hashCode());
                            db2.closeDB();
                            break;
                        } catch (SQLException | IOException e) {
                            System.out.println("Failed to download file, trying again...");
                            tryCount++;
                        }
                    }
                    if (tryCount == Main.ATTEMPT_LIMIT) {
                        // Failed to download file
                        System.out.println("Unable to download file.");
                    }
                });
                Main.curProject.getLinks().add(link); // Add to local project to display in future
                Hyperlink[] arr = new Hyperlink[Main.curProject.getLinks().size()]; // Add to fileList to immediately display
                Main.curProject.getLinks().toArray(arr);
                List<Hyperlink> list = List.of(arr);
                fileList.setItems(FXCollections.observableList(list));
            }
            default -> {
                // Invalid file type, display error
                    // TODO: Implement error check
            }
        }
    }

    public void editProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projEditor");
    }

    public void removeProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projDelete");
    }

    public void addComponentButton_onClick(ActionEvent event) throws IOException {
        Main.show("compSelector");
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
    public void settings_Hover(){
        settingsBtn.setOpacity(0.5);
    }
    public void settings_HoverOff(){
        settingsBtn.setOpacity(1.0);
    }
}

