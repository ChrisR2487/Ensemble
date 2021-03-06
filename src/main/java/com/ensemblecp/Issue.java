package com.ensemblecp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;

public class Issue {
    private int memid; // Source member of issue
    private String message; // Issue message
    private int state; // State of issue
    private Timestamp posted; // Time posted
    private float score; // Score of issue
    private int type;

    public Issue(ResultSet rs) throws SQLException {
        this.memid = rs.getInt("memid");
        this.message = rs.getString("message");
        this.state = rs.getInt("state");
        this.posted = rs.getTimestamp("posted");
        this.score = rs.getFloat("score");
        this.type = rs.getInt("type");
    }

    public Issue(HashMap<String, String> info, Timestamp posted) throws SQLException {
        this.memid = Integer.parseInt(info.get("memid"));
        this.message = info.get("message");
        this.state = Integer.parseInt(info.get("state"));
        this.posted = posted;
        this.score = Float.parseFloat(info.get("score"));
        this.type = Integer.parseInt(info.get("type"));
    }

    /* Setters and Getters */
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

    public Timestamp getPosted() {
        return posted;
    }

    public void setPosted(Timestamp posted) {
        this.posted = posted;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

class IssueType {
    public final static int TEAM = 0;
    public final static int TIMELINE = 1;
    public final static int BUDGET = 2;
    public final static int TASK = 3;
    public final static int NO_SCORE = 4;
}

class IssueState {
    public final static int NEW = 0;
    public final static int SEEN = 1;
    public final static int DONE = 2;
}

class IssueSeverity {
    public final static int INSIGNIFICANT = 0;
    public final static int TOLERABLE = 1;
    public final static int SERIOUS = 2;
    public final static int CATASTROPHIC = 3;
}

class IssueProbability {
    public final static int VERY_LOW = 0;
    public final static int LOW = 1;
    public final static int MODERATE = 2;
    public final static int HIGH = 3;
    public final static int VERY_HIGH = 4;
}

class IssueScore {
    // Type ODO: Fix to proper values
    public final static float PROJECT_OVERBUDGET = 10.0f;
    public final static float PROJECT_OVERDUE = 10.0f;
    public final static float TASK_OVERDUE = 5.0f;
    public final static float ISSUE_BUDGET = 2.0f;
    public final static float ISSUE_TIMELINE = 2.0f;
    public final static float ISSUE_TEAM = 1.0f;
    public final static float ISSUE_TASK = 1.0f;
    public final static float NO_SCORE = 0.0f;

    // Severity
    public final static float INSIGNIFICANT = 0.8f;
    public final static float TOLERABLE = 1.0f;
    public final static float SERIOUS = 1.75f;
    public final static float CATASTROPHIC = 2.5f;

    // Probability
    public final static float VERY_LOW = 0.5f;
    public final static float LOW = 0.75f;
    public final static float MODERATE = 1.0f;
    public final static float HIGH = 1.25f;
    public final static float VERY_HIGH = 1.5f;

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

    public static float checkOverdueTask(String deadline) {
        // Check for overdue
        LocalDate nDate = LocalDate.now();
        LocalDate dDate = LocalDate.parse(deadline);
        return nDate.compareTo(dDate) > 0.0f ? IssueScore.TASK_OVERDUE: IssueScore.NO_SCORE;
    }
}
