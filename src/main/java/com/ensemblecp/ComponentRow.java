package com.ensemblecp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ComponentRow {
    private StringProperty cid;
    public void setCid(String value) { cidProperty().set(value); }
    public String getCid() { return cidProperty().get(); }
    public StringProperty cidProperty() {
        if (cid == null) cid = new SimpleStringProperty(this, "cid");
        return cid;
    }

    private StringProperty title;
    public void setTitle(String value) { titleProperty().set(value); }
    public String getTitle() { return titleProperty().get(); }
    public StringProperty titleProperty() {
        if (title == null) title = new SimpleStringProperty(this, "title");
        return title;
    }

    private StringProperty template;
    public void setTemplate(String value) { templateProperty().set(value); }
    public String getTemplate() { return templateProperty().get(); }
    public StringProperty templateProperty() {
        if (template == null) template = new SimpleStringProperty(this, "template");
        return template;
    }
}
