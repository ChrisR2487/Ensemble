package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.*;

public class customCompController implements Initializable {

    @FXML public MenuItem stringItem;
    @FXML public MenuItem intItem;
    @FXML public MenuItem tableItem;
    @FXML public MenuItem fileItem;
    @FXML public Button submit_Button;
    @FXML public Button addItem;
    @FXML public Button exitButton;
    @FXML public TextField inputField;
    @FXML public MenuButton menuButton;
    @FXML public VBox mainVBox;
    @FXML public VBox fieldVBox;
    @FXML public TextField compTitle;
    @FXML public Button cancelButton;
    String template = "";
    ArrayList<compRow> groupList = new ArrayList<>();
    compRow gr = new compRow();
    int partID = 0;


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
    }

    public void addField_onClick(Event actionEvent) {
        MenuItem item1 = new MenuItem("Integer");
        MenuItem item2 = new MenuItem("String");
        //MenuItem item3 = new MenuItem("Table");
        //MenuItem item4 = new MenuItem("File");
        MenuButton m = new MenuButton("Select Type",null, item1,item2);
        TextField t = new TextField();

        //MenuButton Listeners
        item1.setOnAction(e -> {
            m.setText(item1.getText());
            gr.setType(m.getText());
            template = m.getText().substring(0,1).toUpperCase();
        });
        item2.setOnAction(e -> {
            m.setText(item2.getText());
            gr.setType(m.getText());
        });
        /*
        item3.setOnAction(e -> {
            m.setText(item3.getText());

        });
        item4.setOnAction(e -> {
            m.setText(item4.getText());

        });

         */

        m.setPrefWidth(160);
        m.setPrefHeight(40);
        t.setPrefWidth(600);
        t.setPrefHeight(40);

        HBox newBox = new HBox(m,t);
        fieldVBox.setSpacing(10);
        fieldVBox.getChildren().add(newBox);
    }
    public void removeField_onClick(Event actionEvent) {
        ArrayList<Node> VBox = getAllNodes(fieldVBox);
        VBox.remove(VBox.size()-1);
        /*
        for (Node parent : VBox){
            if (parent instanceof HBox){

            }
        }
         */
    }

    public void submit_Button_onClick(Event actionEvent) throws SQLException, IOException {
        //partId shows the order, use layout of components
        //add new record to Component table when creating a template
        String temp = "";
        String template = "";
        //Get Template

        ArrayList<Node> VBox = getAllNodes(fieldVBox);

        for (Node parent : VBox){
            if (parent instanceof HBox){
                for (Node children : ((HBox) parent).getChildren()) {
                    if(children instanceof MenuButton){
                        if(Objects.equals(((MenuButton) children).getText(), "String")){
                            template += "S";
                            children.setId(temp + partID++);
                            System.out.println("String button found");
                            System.out.println(children.getId());
                        }
                        if(Objects.equals(((MenuButton) children).getText(), "Integer")){
                            template += "I";
                            children.setId(temp + partID++);
                            System.out.println("Integer button found");
                            System.out.println(children.getId());
                        }
                    }
                    if(children instanceof TextField){
                        //gr.setTextField(((TextField) children).getText());

                        System.out.println(((TextField) children).getText());
                    }
                }
            }
        }
        groupList.add(gr);
        System.out.println(template);
        //System.out.println(groupList);
         /*
        //Store Input in Hashmap
        HashMap<String, String> info = new HashMap<String, String>();
        info.put("cid", "1");
        info.put("title", compTitle.getText());
        info.put("template", "Test Template");

        //add data record
        Database db = new Database();
        ResultSet rs = db.createComponent(info);

        db.closeDB();

         */
        //Main.show("projOverview");
    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    public void cancelButton_onClick(Event actionEvent) throws IOException {
        Main.show("compCreator");
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }



}
