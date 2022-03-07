package com.ensemblecp;

// Flexgantt libraries
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;

// Java libraries
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;


class ModelObject<
        P extends Row<?,?,?>, // Type of parent row
        C extends Row<?,?,?>, // Type of child rows
        A extends Activity> extends Row<P, C, A> {
}

// CompanyTimeline: Child of root, Parent of ProjectTimeline, Uses ActivityBase Activity
public class CompanyTimeline extends ModelObject<Row<?,?,?>, ProjectTimeline, Activity> { }

// ProjectTimeline: Child of CompanyTimeline, Parent of ????, Uses ActivityBase Timeline
class ProjectTimeline extends ModelObject<CompanyTimeline, TaskTimeline, Timeline> {
    public ProjectTimeline(String name) {
        this.setName(name);
    }
}

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

class TaskTimeline extends Row<Row<?,?,?>, Row<?,?,?>, Activity> { }