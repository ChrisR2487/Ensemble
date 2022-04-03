package com.ensemblecp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProjectRow {
    private StringProperty pid;
    public void setPid(String value) { pidProperty().set(value); }
    public String getPid() { return pidProperty().get(); }
    public StringProperty pidProperty() {
        if (pid == null) pid = new SimpleStringProperty(this, "pid");
        return pid;
    }

    private StringProperty title;
    public void setTitle(String value) { titleProperty().set(value); }
    public String getTitle() { return titleProperty().get(); }
    public StringProperty titleProperty() {
        if (title == null) title = new SimpleStringProperty(this, "title");
        return title;
    }

    private StringProperty complete;
    public void setComplete(String value) { completeProperty().set(value); }
    public String getComplete() { return completeProperty().get(); }
    public StringProperty completeProperty() {
        if (complete == null) complete = new SimpleStringProperty(this, "complete");
        return complete;
    }

    private StringProperty kickoff;
    public void setKickoff(String value) { kickoffProperty().set(value); }
    public String getKickoff() { return kickoffProperty().get(); }
    public StringProperty kickoffProperty() {
        if (kickoff == null) kickoff = new SimpleStringProperty(this, "kickoff");
        return kickoff;
    }

    private StringProperty deadline;
    public void setDeadline(String value) { deadlineProperty().set(value); }
    public String getDeadline() { return deadlineProperty().get(); }
    public StringProperty deadlineProperty() {
        if (deadline == null) deadline = new SimpleStringProperty(this, "deadline");
        return deadline;
    }

    private StringProperty tags;
    public void setTags(String value) { tagsProperty().set(value); }
    public String getTags() { return tagsProperty().get(); }
    public StringProperty tagsProperty() {
        if (tags == null) tags = new SimpleStringProperty(this, "tags");
        return tags;
    }

    private StringProperty managerName;
    public void setManagerName(String value) { managerNameProperty().set(value); }
    public String getManagerName() { return managerNameProperty().get(); }
    public StringProperty managerNameProperty() {
        if (managerName == null) managerName = new SimpleStringProperty(this, "managerName");
        return managerName;
    }

    private StringProperty issueScore;
    public void setIssueScore(String value) { issueScoreProperty().set(value); }
    public String getIssueScore() { return issueScoreProperty().get(); }
    public StringProperty issueScoreProperty() {
        if (issueScore== null) issueScore = new SimpleStringProperty(this, "issueScore");
        return issueScore;
    }

    /*
    private float issueScore;
    public void setIssueScore(float d) { issueScore = d; }
    public float getIssueScore() { return issueScore; }
    */

    private StringProperty remain;
    public void setRemain(String value) { remainProperty().set(value); }
    public String getRemain() { return remainProperty().get(); }
    public StringProperty remainProperty() {
        if (remain == null) remain = new SimpleStringProperty(this, "remain");
        return remain;
    }
}
