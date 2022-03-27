package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

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
    @FXML AnchorPane root;
    @FXML ListView<Hyperlink> fileList;
    @FXML Label tagsLabel;
    @FXML Label roiLabel;
    @FXML Label budgetLabel;
    @FXML Label kickoffLabel;
    @FXML Label deadlineLabel;
    @FXML Label investmentCostsLabel;
    @FXML Label titleLabel;
    @FXML Label issueScoreLabel;
    @FXML Label c1;
    @FXML Label c2;


    @FXML ImageView removeButton;
    @FXML ImageView editButton;
    @FXML ImageView addComponent;
    @FXML ImageView refreshROI;

    @FXML VBox Comp1;
    @FXML VBox Comp2;

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

        // Set file data
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
            try {
                setupFileList();
                break;
            } catch (SQLException e) {
                System.out.println("Failed to load file list, trying again...");
                e.printStackTrace();
                tryCount++;
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load file list
            System.out.println("Unable to load file list.");
        }

        // set issues data
            // TODO: List recent issues not seen/done

        // Set component data
        Comp1.setVisibility(true);
        c1.setText(c1.getText() + "\n\t" + String.valueOf(Main.curProject.getComponents()));

    }

    private void setupFileList() throws SQLException {
        // Check if already saved
        if (Main.curProject.getLinks() != null) {
            Hyperlink[] arr = new Hyperlink[Main.curProject.getLinks().size()];
            Main.curProject.getLinks().toArray(arr);
            List<Hyperlink> list = List.of(arr);
            fileList.setItems(FXCollections.observableList(list));
            return;
        }

        // Get files
        Database db = new Database();
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
        db.closeDB();
        Main.curProject.setLinks(links);
        Hyperlink[] arr = new Hyperlink[links.size()];
        links.toArray(arr);
        List<Hyperlink> list = List.of(arr);
        fileList.setItems(FXCollections.observableList(list));
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

