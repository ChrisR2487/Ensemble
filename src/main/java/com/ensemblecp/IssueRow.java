package com.ensemblecp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class IssueRow {
    private StringProperty date;
    public void setDate(String value) { dateProperty().set(value); }
    public String getDate() { return dateProperty().get(); }
    public StringProperty dateProperty() {
        if (date == null) date = new SimpleStringProperty(this, "date");
        return date;
    }

    private StringProperty origin;
    public void setOrigin(String value) { originProperty().set(value); }
    public String getOrigin() { return originProperty().get(); }
    public StringProperty originProperty() {
        if (origin == null) origin = new SimpleStringProperty(this, "origin");
        return origin;
    }

    private StringProperty description;
    public void setDescription(String value) { descriptionProperty().set(value); }
    public String getDescription() { return descriptionProperty().get(); }
    public StringProperty descriptionProperty() {
        if (description == null) description = new SimpleStringProperty(this, "description");
        return description;
    }

    private StringProperty type;
    public void setType(String value) { typeProperty().set(value); }
    public String getType() { return typeProperty().get(); }
    public StringProperty typeProperty() {
        if (type == null) type = new SimpleStringProperty(this, "type");
        return type;
    }

    private StringProperty state;
    public void setState(String value) { stateProperty().set(value); }
    public String getState() { return stateProperty().get(); }
    public StringProperty stateProperty() {
        if (state == null) state = new SimpleStringProperty(this, "state");
        return state;
    }

    private CheckBox select = new CheckBox();
    public CheckBox getSelect() {
        return this.select;
    }
    public void setSelect(CheckBox value) {
        this.select = value;
    }

}

