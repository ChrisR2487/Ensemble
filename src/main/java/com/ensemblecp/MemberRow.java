package com.ensemblecp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MemberRow {
    private StringProperty memid;
    public void setMemid(String value) { memidProperty().set(value); }
    public String getMemid() { return memidProperty().get(); }
    public StringProperty memidProperty() {
        if (memid == null) memid = new SimpleStringProperty(this, "memid");
        return memid;
    }

    private StringProperty name;
    public void setName(String value) { nameProperty().set(value); }
    public String getName() { return nameProperty().get(); }
    public StringProperty nameProperty() {
        if (name == null) name = new SimpleStringProperty(this, "name");
        return name;
    }

    private StringProperty position;
    public void setPosition(String value) { positionProperty().set(value); }
    public String getPosition() { return positionProperty().get(); }
    public StringProperty positionProperty() {
        if (position == null) position = new SimpleStringProperty(this, "position");
        return position;
    }

    private StringProperty photo; // TODO: Store as correct property type, likely object
    public void setPhoto(String value) { photoProperty().set(value); }
    public String getPhoto() { return photoProperty().get(); }
    public StringProperty photoProperty() {
        if (photo == null) photo = new SimpleStringProperty(this, "photo");
        return photo;
    }
}