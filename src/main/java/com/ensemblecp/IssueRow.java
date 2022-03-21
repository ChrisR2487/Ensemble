package com.ensemblecp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IssueRow {
    private StringProperty memid;
    public void setMemid(String value) { memidProperty().set(value); }
    public String getMemid() { return memidProperty().get(); }
    public StringProperty memidProperty() {
        if (memid == null) memid = new SimpleStringProperty(this, "memid");
        return memid;
    }

    private StringProperty isid;
    public void setIsid(String value) { isidProperty().set(value); }
    public String getIsid() { return isidProperty().get(); }
    public StringProperty isidProperty() {
        if (memid == null) memid = new SimpleStringProperty(this, "isid");
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

    private StringProperty message;
    public void setMessage(String value) { messageProperty().set(value); }
    public String getMessage() { return messageProperty().get(); }
    public StringProperty messageProperty() {
        if (message == null) message = new SimpleStringProperty(this, "message");
        return message;
    }

    private StringProperty score;
    public void setScore(String value) { memidProperty().set(value); }
    public String getScore() { return memidProperty().get(); }
    public StringProperty scoreProperty() {
        if (score == null) score = new SimpleStringProperty(this, "score");
        return score;
    }
}

