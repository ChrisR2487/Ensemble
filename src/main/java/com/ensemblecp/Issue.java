package com.ensemblecp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Issue {
    private int isid; // Issue id
    private int memid; // Source member of issue
    private String message; // Issue message
    private int state; // State of issue

    public Issue(ResultSet rs) throws SQLException {
        this.isid = rs.getInt("isid");
        this.memid = rs.getInt("memid");
        this.message = rs.getString("message");
        this.state = rs.getInt("state");
    }

    /* Setters and Getters */
    public int getIsid() {
        return isid;
    }

    private void setIsid(int isid) {
        this.isid = isid;
    }

    public int getMemid() {
        return memid;
    }

    private void setMemid(int memid) {
        this.memid = memid;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

class IssueType {
    public final static int NEW = 0;
    public final static int SEEN = 1;
    public final static int DONE = 2;
}

class IssueScore {
    public final static float PROJECT_OVERBUDGET = 10.0f; // TODO: Fix to proper values
    public final static float PROJECT_OVERDUE = 10.0f; // Fix
    public final static float TASK_OVERDUE = 10.0f; // Fix
    public final static float ISSUE_BUDGET = 10.0f; // Fix
    public final static float ISSUE_TIMELINE = 10.0f; // Fix
    public final static float NO_SCORE = 0.0f; // TODO: Make sure this name is good

    public static float checkOverdue(String deadline) {
        // Check for overdue
        LocalDate nDate = LocalDate.now();
        LocalDate dDate = LocalDate.parse(deadline);
        return nDate.compareTo(dDate) > 0.0f ? IssueScore.PROJECT_OVERDUE: IssueScore.NO_SCORE;
    }

    public static float checkOverbudget(float investmentCosts, float budget) {
        // Check for overbudget
        return (budget-investmentCosts) < 0.0f ? IssueScore.PROJECT_OVERBUDGET: IssueScore.NO_SCORE;
    }
}
