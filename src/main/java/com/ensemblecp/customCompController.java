package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class customCompController implements Initializable {

    @FXML public MenuItem stringItem;
    @FXML public MenuItem intItem;
    @FXML public MenuItem tableItem;
    @FXML public MenuItem fileItem;
    @FXML public javafx.scene.control.Button submit_Button;
    @FXML public javafx.scene.control.Button addItem;
    @FXML public ImageView exitButton;
    @FXML public StackPane stackPane;
    @FXML public javafx.scene.control.TextField inputField;
    @FXML public MenuButton menuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ListView<CellData> listView = new ListView();
        listView.setCellFactory((ListView<CellData> param) -> {
            ListCell<CellData> cell = new ListCell<CellData>() {
                HBox hBox = new HBox(menuButton, inputField);
                {
                    HBox.setHgrow(menuButton, Priority.ALWAYS);
                    HBox.setHgrow(inputField, Priority.ALWAYS);
                }

                @Override
                protected void updateItem(CellData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        inputField.setPromptText(item.getTextFieldPromptText());
                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                    }
                }
            };
            return cell;
        });
        stringItem.setOnAction(e -> {
            menuButton.setText(stringItem.getText());
        });
        intItem.setOnAction(e -> {
            menuButton.setText(intItem.getText());
        });
        tableItem.setOnAction(e -> {
            menuButton.setText(tableItem.getText());
        });
        fileItem.setOnAction(e -> {
            menuButton.setText(fileItem.getText());
        });
        addItem.setOnAction((event) -> {
            listView.getItems().add(new CellData());
            System.out.println("Hi ;)");
        });
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }

    public void AddField_onClick(Event actionEvent) {
        TextField newField = new TextField();
        menuButton = new MenuButton("Data Type");
        menuButton.getItems().addAll(new MenuItem("String"), new MenuItem("Integer"));
        stackPane.getChildren().add(menuButton);

    }


    public void submit_Button_onClick(Event actionEvent) {


    }


}
