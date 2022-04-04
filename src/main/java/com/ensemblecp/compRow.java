package com.ensemblecp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class compRow {

    private StringProperty type;
    private StringProperty textField;

    //Methods
    public StringProperty typeProperty() {
        if (type == null) type = new SimpleStringProperty(this, "type");
        return type;
    }
    public StringProperty textProperty() {
        if (textField == null) textField = new SimpleStringProperty(this, "text");
        return textField;
    }

    //Setters
    public void setType(String type) { typeProperty().set(type); }
    public void setTextField(String textField) {textProperty().set(textField); }

    //Getters
    public String getType() { return typeProperty().get(); }
    public String getTextField() { return textProperty().get(); }




}
