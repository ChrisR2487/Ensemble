package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProjListController implements Initializable {
    @FXML private TableView<ProjectRow> projectTable;
    @FXML private TableColumn<ProjectRow, String> issueScoreColumn;
    @FXML private TableColumn<ProjectRow, String> pidColumn;
    @FXML private TableColumn<ProjectRow, String> manidNameColumn;
    @FXML private TableColumn<ProjectRow, String> tagsColumn;
    @FXML private TableColumn<ProjectRow, String> statusColumn;
    @FXML private TableColumn<ProjectRow, String> titleColumn;
    @FXML private TableColumn<ProjectRow, String> kickoffColumn;
    @FXML private TableColumn<ProjectRow, String> deadlineColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create projectRow list
        ArrayList<ProjectRow> rowArrayList = new ArrayList<ProjectRow>();
        try {
            Database db = new Database();
            ResultSet rs = db.getProjects();
            while (rs.next()) {
                ProjectRow pr = new ProjectRow();
                pr.setTitle(rs.getString("title"));
                pr.setComplete(String.valueOf(rs.getBoolean("complete")));
                pr.setKickoff(rs.getDate("kickoff").toString());
                pr.setDeadline(rs.getDate("deadline").toString());
                pr.setPid(String.valueOf(rs.getInt("pid")));
                pr.setIssueScore(String.valueOf(rs.getFloat("issueScore")));
                pr.setManid("N/A"); // TODO: String.valueOf(rs.getInt("manid"))

                String tags = rs.getString("tag1");
                for (int i = 2; i < 5; i++) {
                    String nextTag = rs.getString("tag"+String.valueOf(i));
                    if (nextTag == null) break;
                    tags += ", " + nextTag;
                }
                pr.setTags(tags);

                rowArrayList.add(pr);
            }
            db.closeDB();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Add better handling for loop
        }

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
        manidNameColumn.setCellValueFactory(new PropertyValueFactory<>("manid")); // TODO: Change to manager name
        issueScoreColumn.setCellValueFactory(new PropertyValueFactory<>("issueScore"));
        tagsColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        pidColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));
        projectTable.setItems(projectRows);
        TableView.TableViewSelectionModel<ProjectRow> mod = projectTable.getSelectionModel();
        ObservableList<ProjectRow> sel = mod.getSelectedItems();
        sel.addListener((ListChangeListener<ProjectRow>) change -> {
            try {
                new DashboardController().onChange(change);
            } catch (IOException | SQLException e) {
                e.printStackTrace(); // TODO: Handle error better
            }
        });
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }

    public void dashButton_onClick(Event actionEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event actionEvent) throws IOException {
        Main.show("projList");
    }

    public void archiveButton_onClick(Event actionEvent) {
    }
}
