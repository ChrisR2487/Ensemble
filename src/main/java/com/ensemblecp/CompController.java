package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

public class CompController implements Initializable {
    @FXML private Button cancelButton;
    @FXML private Button exitButton;
    @FXML TableView<ComponentRow> existingTable;
    @FXML TableColumn<ComponentRow, String> titleColumn;
    @FXML TableColumn<ComponentRow, String> templateColumn;
    private static boolean templatesLoaded = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create componentRow list
        showComps();
    }

    public void showComps() throws IOException {
        // Create componentRow list
        ArrayList<ComponentRow> rowArrayList = new ArrayList<>();
        try {
            Database db = new Database();
            ResultSet rs = db.getComponents();
            while (rs.next()) {
                ComponentRow pr = new ComponentRow();
                pr.setTitle(rs.getString("title"));
                pr.setCid(String.valueOf(rs.getInt("cid")));
                pr.setTemplate(rs.getString("template"));
                rowArrayList.add(pr);
            }
            db.closeDB();
        }
        catch (SQLException e) {
            e.printStackTrace(); // TODO: Add better handling for loop
        }

        // Convert to array
        ComponentRow[] rowList = rowArrayList.toArray(new ComponentRow[rowArrayList.size()]);

        // Cast to ObservableList
        List<ComponentRow> rows = List.of(rowList);
        ObservableList<ComponentRow> compRows = FXCollections.observableList(rows);

        // Set row data
        titleColumn.setCellValueFactory(new PropertyValueFactory("title"));
        templateColumn.setCellValueFactory(new PropertyValueFactory("template"));
        existingTable.setItems(compRows);

        // On select, go to data screen
        TableView.TableViewSelectionModel<ComponentRow> compSelectModel = existingTable.getSelectionModel();
        ObservableList<ComponentRow> sel = compSelectModel.getSelectedItems();
        sel.addListener(new ListChangeListener<>() {
            @Override public void onChanged(Change<? extends ComponentRow> change) {
                try {
                    onChangeTemplate(change);
                } catch (IOException e) {
                    e.printStackTrace(); // TODO: Handle error better
                }
            }
        });
    }

    private void onChangeTemplate(ListChangeListener.Change change) throws IOException {
        // Setup transition
        ObservableList<ComponentRow> selectedList = change.getList();
        int cid = Integer.parseInt(selectedList.get(0).getCid());

        // Change scene
        Main.show("customComp");
    }

    public void showCustomComp() throws IOException {
        // Hide component template table
        existingTable.setVisible(false);

        // Setup custom component creator
        Main.show("customComp");
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }

    public void cancelButton_onClick(ActionEvent mouseEvent) throws IOException {
        Main.show("projOverview");
    }
}
