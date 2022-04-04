package com.ensemblecp;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class CompSelectorController implements Initializable {
    @FXML TextField templateTitle;
    @FXML VBox partMenuList;
    @FXML Pane chooseExistingPane;
    @FXML Pane createCustomPane;
    @FXML Button cancelButton;
    @FXML Button exitButton;
    @FXML TableView<ComponentRow> existingTable;
    @FXML TableColumn<ComponentRow, String> titleColumn;
    @FXML TableColumn<ComponentRow, String> templateColumn;
    ArrayList<ComboBox<String>> templateFields;
    int lastUsedIndex;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup custom creator stuff
        templateFields = new ArrayList<>();
        lastUsedIndex = -1;
    }

    /* Selector methods */
    public void showExistingTemplates(ActionEvent event) throws IOException {
        // Create componentRow list
        createCustomPane.setVisible(false);
        ArrayList<ComponentRow> rowArrayList = new ArrayList<>();
        int tryCount = 0;
        while (tryCount < Main.ATTEMPT_LIMIT) {
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
                break;
            }
            catch (SQLException e) {
                System.out.println("Failed to get component data, trying again...");
                tryCount++;
            }
        }
        if (tryCount == Main.ATTEMPT_LIMIT) {
            // Failed to load dashboard
            System.out.println("Unable to get component data, end execution.");
            return;
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

        sel.addListener((ListChangeListener<ComponentRow>) change -> {
            int tryCount2 = 0;
            while (tryCount2 < Main.ATTEMPT_LIMIT) {
                try {
                    onSelectTemplate(change);
                    break;
                } catch (IOException e) {
                    System.out.println("Failed to find template, trying again...");
                    tryCount2++;
                }
            }
            if (tryCount2 == Main.ATTEMPT_LIMIT) {
                // Failed to choose template
                System.out.println("Unable to find template, end execution.");
                return;
            }
        });

        chooseExistingPane.setVisible(true);
    }

    private void onSelectTemplate(ListChangeListener.Change change) throws IOException {
        // Setup transition
        ObservableList<ComponentRow> selectedList = change.getList();
        int cid = Integer.parseInt(selectedList.get(0).getCid());
        String template = selectedList.get(0).getTemplate();
        // Move to data entry screen
        CompDataCreatorController.cid = cid;
        CompDataCreatorController.template = template;
        CompDataCreatorController.title = selectedList.get(0).getTitle();
        Main.show("compDataCreator");
    }

    public void showCustomCreator() throws IOException {
        // Hide component template table
        chooseExistingPane.setVisible(false);
        createCustomPane.setVisible(true);
    }

    /* Custom creator methods */
    public void addField_onClick(Event actionEvent) {
        if(lastUsedIndex != templateFields.size()-1) {
            // Already exists, re-enable old field
            lastUsedIndex++;
            ComboBox<String> reshowMenu = templateFields.get(lastUsedIndex);
            reshowMenu.setDisable(false);
            reshowMenu.setVisible(true);
        }
        else {
            // Set items
            ComboBox<String> m = new ComboBox<String>();
            m.getItems().addAll("Text", "Whole Number", "Real Number", "List");
            // Set layout
            m.setPrefWidth(360.0);
            m.setPrefHeight(50.0);
            m.setStyle("-fx-font-size: 16.0; -fx-text-alignment: center;");
            VBox.setMargin(m, new Insets(9.0, 9.0, 9.0, 9.0));
            // Set parent
            partMenuList.getChildren().add(m);
            // Add to local list of parts
            lastUsedIndex++;
            templateFields.add(m);
        }
    }

    public void removeField_onClick(Event actionEvent) {
        if (lastUsedIndex == -1) return;
        ComboBox<String> removedMenu = templateFields.get(lastUsedIndex);
        lastUsedIndex--;
        removedMenu.setDisable(true);
        removedMenu.setVisible(false);
    }

    /* Software methods */
    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void cancelButton_onClick(ActionEvent mouseEvent) throws IOException {
        Main.show("projOverview");
    }

    public void createTemplate_onClick(ActionEvent actionEvent) throws SQLException, IOException {
        // Insert template record into database
        String newTemplateString = "";
        for (int i = 0; i <= lastUsedIndex; i++) {
            templateFields.get(i).setBorder(LoginController.NORMAL_BORDER);
            String selected = templateFields.get(i).getSelectionModel().getSelectedItem();
            char templateChar = ' ';
            switch (selected) {
                case "Text" -> templateChar = 'S';
                case "Whole Number" -> templateChar = 'I';
                case "Real Number" -> templateChar = 'R';
                case "List" -> templateChar = 'L';
                default -> {
                    templateFields.get(i).setBorder(LoginController.INVALID_BORDER);
                    return;
                }
            }
            newTemplateString += templateChar;
        }
        Database db = new Database();
        HashMap<String, String> info = new HashMap<>();
        info.put("template", newTemplateString);
        info.put("title", templateTitle.getText());
        int cid = Math.abs(templateTitle.getText().hashCode());
        info.put("cid", String.valueOf(cid));
        db.createComponent(info);
        db.closeDB();

        // Move to data entry screen
        CompDataCreatorController.cid = cid;
        CompDataCreatorController.template = newTemplateString;
        CompDataCreatorController.title = templateTitle.getText();
        Main.show("compDataCreator");
    }
}
