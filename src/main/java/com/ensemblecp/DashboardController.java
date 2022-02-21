package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        ArrayList<ProjectRow> temp0 = new ArrayList<ProjectRow>();
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
                temp0.add(pr);
            }
            db.closeDB();
        }
        catch (SQLException e) {
            e.printStackTrace(); // TODO: Add better handling for loop
        }

        // Convert to array
        ProjectRow[] temp1 = new ProjectRow[temp0.size()];
        int indx = 0;
        for (ProjectRow pr: temp0) {
            temp1[indx] = pr;
            indx++;
        }

        // Cast to ObservableList
        List<ProjectRow> temp2 = List.of(temp1);
        ObservableList<ProjectRow> projectRows = FXCollections.observableList(temp2);

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
        String projectTitle = selectedList.get(0).getTitle();
        Database db = new Database();
        Main.curProject = (new Project(db.getProject(projectTitle), null));

        // Display screen
        Main.show("projectView"); // TODO: fix to give correct name
    }

    @FXML
    public void selectItem(ContextMenuEvent contextMenuEvent) {
    }

}