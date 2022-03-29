package com.ensemblecp;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Task {
    private String title; // Title of task
    private int tid; // Id of task
    private int memid; // Assigned member to task
    private String description; // Description of task
    private Date kickoff; // Kickoff of task
    private Date deadline; // Deadline of task
    private boolean complete; // Is complete?

    public Task(ResultSet rs) throws SQLException {
        this.tid = rs.getInt("tid");
        this.title = rs.getString("title");
        this.memid = rs.getInt("memid");
        this.description = rs.getString("description");
        this.kickoff = rs.getDate("kickoff");
        this.deadline = rs.getDate("deadline");
        this.complete = rs.getBoolean("complete");
    }

    /* Class Methods */
    public void update(ResultSet rs) throws SQLException {
        this.memid = rs.getInt("memid");
        this.description = rs.getString("description");
        this.kickoff = rs.getDate("kickoff");
        this.deadline = rs.getDate("deadline");
        this.complete = rs.getBoolean("complete");
    }

    /* Setters and Getters */
    public int getTid() {
        return tid;
    }

    private void setTid(int tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public int getMemid() {
        return memid;
    }

    private void setMemid(int memid) {
        this.memid = memid;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public Date getKickoff() {
        return kickoff;
    }

    private void setKickoff(Date kickoff) {
        this.kickoff = kickoff;
    }

    public Date getDeadline() {
        return deadline;
    }

    private void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

class TaskAction {
    public final static int MODIFY_TASK = 0;
    public final static int DELETE_TASK = 1;
    public final static int MARK_TASK = 2;
    public final static int UNMARK_TASK = 3;
}
