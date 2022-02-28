package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompController {
    @FXML TableView<ComponentRow> existingTable;
    @FXML TableColumn<ComponentRow, String> titleColumn;
    @FXML TableColumn<ComponentRow, String> templateColumn;
    private static boolean templatesLoaded = false;

    public void showComps() throws IOException {
        // Hide custom component creator
            // TODO: Add this line when format is known
        // Check if templates are already loaded
        if (templatesLoaded) {
            existingTable.setVisible(true);
            return;
        }

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
        TableView.TableViewSelectionModel compSelectModel = existingTable.getSelectionModel();
        ObservableList sel = compSelectModel.getSelectedItems();
        sel.addListener(new ListChangeListener<ComponentRow>() {
            @Override public void onChanged(Change<? extends ComponentRow> change) {
                try {
                    onChangeTemplate(change);
                } catch (IOException e) {
                    e.printStackTrace(); // TODO: Handle error better
                }
            }
        });

        // Display table of existing components
        this.templatesLoaded = true;
        existingTable.setVisible(true);
    }

    private void onChangeTemplate(ListChangeListener.Change change) throws IOException {
            // TODO: Check if I need to see if table is visible to validate

        // Setup transition
        ObservableList<ComponentRow> selectedList = change.getList();
        int cid = Integer.parseInt(selectedList.get(0).getCid());
            // TODO: Set cid of template being used in data entry controller

        // Change scene
        Main.show("Components"); // TODO: Fix this name, go to data entry screen
    }

    private void onSubmitCustom_onClick(Event event) throws IOException {
            // TODO: Check if I need to see if creator is visible to validate

        // Add custom component to database
            // TODO: Add this feature to insert custom comp records

        // Setup transition
            // TODO: Use new cid of custom template and set in data entry controller

        // Change scene
        Main.show("Components"); // TODO: Fix this name, go to data entry screen
    }

    public void showCustomComp() throws IOException {
        // Hide component template table
        existingTable.setVisible(false);

        // Setup custom component creator
            // TODO: Setup component creator system

        // Display custom component creator
            // TODO: Show custom component creator
    }
}
