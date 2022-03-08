package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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

public class ProjTeamController implements Initializable {
    @FXML private Label tagsLabel;
    @FXML private Label roiLabel;
    @FXML private Label budgetLabel;
    @FXML private Label kickoffLabel;
    @FXML private Label deadlineLabel;
    @FXML private Label investmentCostsLabel;
    @FXML private Label titleLabel;

    @FXML private TableView<MemberRow> memberTable;
    @FXML private TableColumn<MemberRow, String> positionColumn;
    @FXML private TableColumn<MemberRow, String> memidColumn;
    @FXML private TableColumn<MemberRow, String> nameColumn;
    @FXML private TableColumn<MemberRow, String> photoColumn;

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

        // Setup members table
        ArrayList<MemberRow> rowArrayList = new ArrayList<>();
        try {
            Database db = new Database();
            ResultSet rs = db.getProjectMembers(Main.curProject.getPid());
            while (rs.next()) {
                MemberRow mr = new MemberRow();
                mr.setName(rs.getString("name"));
                mr.setMemid(String.valueOf(rs.getInt("memid")));
                mr.setPosition(rs.getString("position"));
                mr.setPhoto("N/A"); // TODO: Get correct file for member photo

                rowArrayList.add(mr);
            }
            db.closeDB();
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Add better handling for loop
        }

        // Convert to array
        MemberRow[] rowList = rowArrayList.toArray(new MemberRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<MemberRow> rows = List.of(rowList);
        ObservableList<MemberRow> memberRows = FXCollections.observableList(rows);

        // Set row data
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memidColumn.setCellValueFactory(new PropertyValueFactory<>("memid"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        memberTable.setItems(memberRows);
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
}
