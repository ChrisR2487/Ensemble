package com.ensemblecp;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

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
    private ArrayList<Component> components; // List of project components
    String template = "";
    ArrayList<compRow> groupList = new ArrayList<>();
    ArrayList<Node[]> partNodes = new ArrayList<>();
    compRow gr = new compRow();
    private final Border INVALID_BORDER = new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5)));
    private final Border NORMAL_BORDER = new Border(new BorderStroke(Color.TRANSPARENT,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5)));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        stringItem.setOnAction(e -> {
            menuButton.setText(stringItem.getText());
        });
        intItem.setOnAction(e -> {
            menuButton.setText(intItem.getText());
        });
        partNodes.add(new Node[] {menuButton, inputField});
    }

    public void addField_onClick(Event actionEvent) {
        MenuItem item1 = new MenuItem("Integer");
        MenuItem item2 = new MenuItem("String");
        MenuButton m = new MenuButton("Entry Type",null, item1,item2);
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

        m.setAlignment(Pos.CENTER);
        m.setStyle("-fx-font: 18 System;");
        m.setPrefWidth(170);
        m.setPrefHeight(40);
        t.setStyle("-fx-font: 18 System");
        t.setPrefWidth(900);
        t.setPrefHeight(40);

        HBox newBox = new HBox(m,t);
        VBox.setMargin(newBox, new Insets(0, 0, 0, 30));
        fieldVBox.setSpacing(20);
        fieldVBox.getChildren().add(newBox);
        partNodes.add(new Node[] {m, t});

    }

    public void removeField_onClick(Event actionEvent) {
        ArrayList<Node> VBox = getAllNodes(fieldVBox);
        if (fieldVBox.getChildren().size() != 1) {
            fieldVBox.getChildren().remove(1);
        } else System.out.println("Can't remove first element");
    }

    public void submit_Button_onClick(Event actionEvent) throws SQLException, IOException {
        //partId shows the order, use layout of components
        String template = "";
        Database db = new Database();
        HashMap<String, String> info = new HashMap<String, String>();

        Integer pid = Main.curProject.getPid();
        Integer cid = Math.abs(compTitle.getText().hashCode());
        boolean isString = false;
        boolean isInteger = false;

        //Get Template
        ArrayList<Node> VBox = getAllNodes(fieldVBox);

        compTitle.setBorder(NORMAL_BORDER);
        if (Objects.equals(compTitle.getText(), "")){
            compTitle.setBorder(INVALID_BORDER);
            return;
        }
        for (Node parent : VBox){
            if (parent instanceof HBox){
                for (Node children : ((HBox) parent).getChildren()) {
                    if(children instanceof MenuButton){
                        ((MenuButton) children).setBorder(NORMAL_BORDER);
                        if(Objects.equals(((MenuButton) children).getText(), "String")){
                            template += "S";
                            isString = true;
                        }
                        if(Objects.equals(((MenuButton) children).getText(), "Integer")){
                            template += "I";
                            isInteger = true;
                        }
                        else if (Objects.equals(((MenuButton) children).getText(), "Entry Type")){
                            ((MenuButton) children).setBorder(INVALID_BORDER);
                            return;
                        }
                    }
                    if(children instanceof TextField){
                        ((TextField) children).setBorder(NORMAL_BORDER);
                        if (!Objects.equals(((TextField) children).getText(), "")){
                            if (isString){
                                if (isNumeric(((TextField) children).getText())) {
                                    ((TextField) children).setBorder(INVALID_BORDER);
                                    return;
                                }
                                //Component newComp = new Component(pid, cid, template, db);
                                //addComponent(newComp);
                                isString = false;
                            }
                            if (isInteger){
                                if (isNumeric(((TextField) children).getText())){
                                    //Component newComp = new Component(pid, cid, template, db);
                                    //addComponent(newComp);
                                    isInteger = false;
                                }else {
                                    ((TextField) children).setBorder(INVALID_BORDER);
                                    return;
                                }
                            }
                        } else {
                            ((TextField) children).setBorder(INVALID_BORDER);
                            return;
                        }
                    }
                }
            }
        }
        groupList.add(gr);
        String dataType = "";
        String inputText = "";
        String input = "";
        for (int i=0; i < partNodes.size()-1; i++){
            Node[] data = partNodes.get(i);
            if (data[i] instanceof MenuButton){
                dataType = ((MenuButton) data[i]).getText();
            }
            if (data[i] instanceof TextField){
                inputText = ((TextField) data[i]).getText();
            }
            input = dataType +": " + inputText;
            info.put("partid", String.valueOf(i));
            info.put("value", input);
        }


        //Add record to <pid>_<cid> table
        info.put("pid", pid.toString());
        info.put("cid", cid.toString());
        info.put("title", compTitle.getText());
        info.put("template", template);
        db.addComponent(info);
        //Component newComp = new Component(pid, cid, template, db);
        //addComponent(newComp);


        db.closeDB();

        Main.show("projOverview");
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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

    public void addComponent(Component comp) {
        components.add(comp);
    }

    public void cancelButton_onClick(Event actionEvent) throws IOException {
        Main.show("compCreator");
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }
}
