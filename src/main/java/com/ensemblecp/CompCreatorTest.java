package com.ensemblecp;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CompCreatorTest extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Custom Component Creator");
        ListView<CellData> listView = new ListView();

        listView.setCellFactory((ListView<CellData> param) -> {
            ListCell<CellData> cell = new ListCell<CellData>() {
                MenuItem item1 = new MenuItem("Integer");
                MenuItem item2 = new MenuItem("String");
                MenuItem item3 = new MenuItem("Table");
                MenuItem item4 = new MenuItem("File");
                MenuButton menuButton = new MenuButton("Select Type",null, item1,item2,item3,item4);
                TextField textField = new TextField();
                HBox hBox = new HBox(menuButton, textField);
                {
                    item1.setOnAction(e -> {
                        menuButton.setText(item1.getText());
                    });
                    item2.setOnAction(e -> {
                        menuButton.setText(item2.getText());
                    });
                    item3.setOnAction(e -> {
                        menuButton.setText(item3.getText());
                    });
                    item4.setOnAction(e -> {
                        menuButton.setText(item4.getText());
                    });
                    HBox.setHgrow(menuButton, Priority.ALWAYS);
                    HBox.setHgrow(textField, Priority.ALWAYS);
                }

                @Override
                protected void updateItem(CellData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        textField.setPromptText(item.getTextFieldPromptText());
                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                    }
                }
            };
            return cell;
        });

        Button addButton = new Button("Add Item");
        Button submitButton = new Button("Submit");
        addButton.setOnAction((event) -> {
            listView.getItems().add(new CellData());
        });

        VBox vbox = new VBox(addButton, listView, submitButton);
        StackPane root = new StackPane();
        root.setMinSize(600,300);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root);
        //scene.getStylesheets().add("src/main/resources/css/compCreatorStyle.css");
        stage.setScene(scene);
        stage.show();
    }
}
