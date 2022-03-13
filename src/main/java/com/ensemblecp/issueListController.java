package com.ensemblecp;

import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class issueListController implements Initializable {

    @FXML private Label tagsLabel;
    @FXML private Label roiLabel;
    @FXML private Label budgetLabel;
    @FXML private Label kickoffLabel;
    @FXML private Label deadlineLabel;
    @FXML private Label investmentCostsLabel;
    @FXML private Label titleLabel;

    @FXML private TableView<IssueRow> issueTable;
    @FXML private TableColumn<IssueRow, String> nameColumn;
    @FXML private TableColumn<IssueRow, String> isidColumn;
    @FXML private TableColumn<IssueRow, String> messageColumn;
    @FXML private TableColumn<IssueRow, String> positionColumn;
    @FXML private TableColumn<IssueRow, String> scoreColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup insights
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
        titleLabel.setText(Main.curProject.getTitle());

        // Setup issues table
        ArrayList<IssueRow> rowArrayList = new ArrayList<>();
        try {
            Database db = new Database();
            ResultSet rs = db.getProjectMembers(Main.curProject.getPid());
            while (rs.next()) {
                IssueRow mr = new IssueRow();
                mr.setName(rs.getString("name"));
                mr.setIsid(String.valueOf(rs.getInt("isid")));
                mr.setMessage(rs.getString("message"));
                mr.setPosition(rs.getString("position"));
                mr.setScore(rs.getString("position"));


                rowArrayList.add(mr);
            }
            db.closeDB();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Add better handling for loop
        }

        // Convert to array
        IssueRow[] rowList = rowArrayList.toArray(new IssueRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<IssueRow> rows = List.of(rowList);
        ObservableList<IssueRow> issueRows = FXCollections.observableList(rows);

        // Set row data
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        isidColumn.setCellValueFactory(new PropertyValueFactory<>("isid"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        issueTable.setItems(issueRows);

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

    public void editProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projEditor");
    }

    public void removeProjectButton_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projDelete");
    }

    public void viewTeam_onClick(ActionEvent event) throws IOException {
        Main.show("projTeam");
    }

    public void viewOverview_onClick(ActionEvent actionEvent) throws IOException {
        Main.show("projViewScreen");
    }

    public void viewBenchmark_onClick(ActionEvent actionEvent) {
        // TODO: Implement this view change to benchmark
    }
}
