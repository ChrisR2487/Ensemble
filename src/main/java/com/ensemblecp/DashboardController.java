package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ResourceBundle;


public class DashboardController implements Initializable {
    @FXML private TableView<ProjectRow> projectTable;
    @FXML private TableColumn<ProjectRow, String> statusColumn;
    @FXML private TableColumn<ProjectRow, String> titleColumn;
    @FXML private TableColumn<ProjectRow, String> remainColumn;
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
                pr.setRemain(String.valueOf(rs.getInt("budget") - rs.getInt("investmentCosts")));
                pr.setKickoff(rs.getDate("kickoff").toString());
                pr.setDeadline(rs.getDate("deadline").toString());
                pr.setPid(String.valueOf(rs.getInt("pid")));
                rowArrayList.add(pr);
            }
            db.closeDB();
        }
        catch (SQLException e) {
            e.printStackTrace(); // TODO: Add better handling for loop
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
        TableView.TableViewSelectionModel mod = projectTable.getSelectionModel();
        ObservableList sel = mod.getSelectedItems();
        sel.addListener(new ListChangeListener<ProjectRow>() {
            @Override public void onChanged(Change<? extends ProjectRow> change) {
                try {
                    onChange(change);
                } catch (IOException | SQLException e) {
                    e.printStackTrace(); // TODO: Handle error better
                }
            }
        });
    }

    @FXML
    public void add_onClick() throws IOException {
        Main.show("projCreator");
    }

    @FXML void onChange(ListChangeListener.Change change) throws IOException, SQLException {
        // Get data
        ObservableList<ProjectRow> selectedList = change.getList();
        int pid = Integer.parseInt(selectedList.get(0).getPid());

        // Check if project is in cache
        Project projInCache = Main.inCache(pid); // Get pid of current project, check if in cache
        if (projInCache == null) {
            // Not in cache, get project data and save to cache
            Database db = new Database();
            projInCache = new Project(db.getProject(pid), null, db); // TODO: Get component RS format
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
        Main.show("projViewScreen");
    }

    @FXML
    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }

    public void dashButton_onClick(Event actionEvent) throws IOException {
        Main.show("Dashboard");
    }

    public void projListButton_onClick(Event actionEvent) {
    }

    public void archiveButton_onClick(Event actionEvent) {
    }

}