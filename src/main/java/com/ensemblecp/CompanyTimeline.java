package com.ensemblecp;

// Flexgantt libraries

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;

// CompanyTimeline: Child of root, Parent of ProjectTimeline, Uses ActivityBase Activity
public class CompanyTimeline extends ModelObject<Row<?,?,?>, ProjectTimeline, Activity> { }

// ProjectTimeline: Child of CompanyTimeline, Parent of TaskTimeline, Uses ActivityBase Timeline
class ProjectTimeline extends ModelObject<CompanyTimeline, TaskTimeline, Timeline> {
    public ProjectTimeline(String name) {
        this.setName(name);
    }
}

// CompanyTimeline: Child of root, Parent of ProjectTimeline, Uses ActivityBase Activity
class BenchmarkTimeline extends ModelObject<Row<?,?,?>, TaskTimeline, Activity> { }

// TaskTimeline: Child of BenchmarkTimeline, Parent of Nothing, Uses ActivityBase Timeline
class TaskTimeline extends Row<BenchmarkTimeline, TaskTimeline, Timeline> {
    public TaskTimeline(String name) {
        this.setName(name);
    }
}

/* Backend classes */
// Timeline: Activity for ProjectTimeline
class Timeline extends MutableActivityBase<TimelineData> {
    public Timeline(TimelineData data) throws SQLException {
        super(data.getName()); // the activity name
        setStartTime(data.getStartTime());
        setEndTime(data.getEndTime());
        setUserObject(data);
    }
}

// TimelineData: Data of Timeline Activity
class TimelineData {
    private String name;
    private Instant kickoff;
    private Instant deadline;
    private Object data;

    public TimelineData (ResultSet rs) throws SQLException {
        this.name = rs.getString("title");
        this.kickoff = rs.getDate("kickoff").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        this.deadline = rs.getDate("deadline").toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        this.data = rs;
    }

    public TimelineData(Task task) {
        this.name = task.getTitle();
        this.kickoff = task.getKickoff().toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        this.deadline = task.getDeadline().toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        this.data = task;
    }

    public TimelineData(Project project) {
        this.name = project.getTitle();
        this.kickoff = project.getKickoff().toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        this.deadline = project.getDeadline().toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        this.data = project;
    }

    public String getName() {
        return name;
    }

    public Instant getStartTime() {
        return kickoff;
    }

    public Instant getEndTime() {
        return deadline;
    }
}

class ModelObject<
        P extends Row<?,?,?>, // Type of parent row
        C extends Row<?,?,?>, // Type of child rows
        A extends Activity> extends Row<P, C, A> { }