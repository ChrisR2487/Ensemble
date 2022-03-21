package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

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
    @FXML public Button submit_Button;
    @FXML public Button addItem;
    @FXML public ImageView exitButton;
    @FXML public StackPane stackPane;
    @FXML public TextField inputField;
    @FXML public MenuButton menuButton;
    @FXML public VBox mainVBox;
    @FXML public VBox fieldVBox;
    MenuItem item1 = new MenuItem("Integer");
    MenuItem item2 = new MenuItem("String");
    MenuItem item3 = new MenuItem("Table");
    MenuItem item4 = new MenuItem("File");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
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

         */
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
        /*
        addItem.setOnAction((event) -> {
            listView.getItems().add(new CellData());
            System.out.println("Hi ;)");
        });
         */
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }

    public void addField_onClick(Event actionEvent) {
        MenuButton m = new MenuButton("Select Type",null, item1,item2,item3,item4);
        //MenuButton m = new MenuButton();
        TextField t = new TextField();
        m.setPrefWidth(160);
        m.setPrefHeight(40);
        t.setPrefWidth(600);
        m.setPrefHeight(40);
        HBox newBox = new HBox(m,t);
        fieldVBox.setSpacing(20);
        fieldVBox.getChildren().add(newBox);

    }


    public void submit_Button_onClick(Event actionEvent) {


    }


}
