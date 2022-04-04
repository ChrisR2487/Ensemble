package com.ensemblecp;// Imports

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

// Project Class
/**
 * A class used to temporarily create a java object
 * which represents the data of a project.
 */
public class Database {
    /* Class variables */
    public Connection conn;
    public static String databaseName = "Ensemble"; // Name of database in system, prepended on table names

    /* Class Constructors */
    /**
     * Default constructor.
     */
    public Database() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:mysql://34.150.158.26:3306","root","G6DevsOP2487!");
    }

    public Database(boolean isBigQuery) throws SQLException {
        if (isBigQuery) {
            Properties p = new Properties();
            p.setProperty("ProjectId", "ensemble-340721");
            p.setProperty("OAuthServiceAcctEmail", "intelijconnect@ensemble-340721.iam.gserviceaccount.com");
            p.setProperty("OAuthPvtKeyPath", "src/main/resources/database/ensemble-creds.json");
            this.conn = DriverManager.getConnection("jdbc:bigquery://https://www.googleapis.com/bigquery/v2:", p);
        }
        else {
            this.conn = DriverManager.getConnection("jdbc:mysql://34.150.158.26:3306","root","G6DevsOP2487!");
        }
    }

    public ResultSet getProject(int pid) throws SQLException {
        // Get tuple
        PreparedStatement preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Project where pid = ?");
        preparedStmt.setInt(1, pid);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }


    /* Class Methods */
    /**
     *  Create project record
     */
    public ResultSet createProject(HashMap<String, String> info) throws SQLException {
        // Insert record
        String query = " insert into "+ databaseName + ".Project"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Integer.parseInt(info.get("pid")));
        preparedStmt.setString (2, info.get("title"));
        preparedStmt.setString (3, info.get("description"));
        preparedStmt.setFloat(4, Float.parseFloat(info.get("investmentCosts")));
        preparedStmt.setFloat(5, Float.parseFloat(info.get("budget")));
        preparedStmt.setFloat(6, Float.parseFloat(info.get("roi")));
        preparedStmt.setDate(7, Date.valueOf(info.get("kickoff")));
        preparedStmt.setDate(8, Date.valueOf(info.get("deadline")));
        preparedStmt.setFloat(9, Float.parseFloat(info.get("issueScore")));
        preparedStmt.setString(10, info.get("tag1"));
        preparedStmt.setString(11, info.get("tag2"));
        preparedStmt.setString(12, info.get("tag3"));
        preparedStmt.setString(13, info.get("tag4"));
        preparedStmt.setBoolean(14, Boolean.parseBoolean(info.get("complete")));
        preparedStmt.setInt(15, Integer.parseInt(info.get("manid")));
        preparedStmt.execute();

        String charPid = Project.IDtoChars(Integer.parseInt(info.get("pid")));
        String createTable;
        Statement stmt = conn.createStatement();
        // Add related tables
        createTable = "create table " + databaseName + "." + charPid + "_Components("
                + " cid int not null, template varchar(128) not null, constraint " + charPid + "_Component_pk primary key (cid));";
        stmt.execute(createTable);

        //create project team table
        createTable = "create table " + databaseName + "." + charPid + "_Team("
                + "memid int primary key)";
        stmt.execute(createTable);

        createTable = "create table " + databaseName + "." + charPid + "_Tasks("
                + "tid int primary key,"
                + "title varchar(64) not null,"
                + "memid int not null,"
                + "description varchar(128) not null,"
                + "kickoff date not null,"
                + "deadline date not null,"
                + "complete boolean not null,"
                + "constraint " + charPid + "_Tasks_uq unique(title))";
        stmt.execute(createTable);

        createTable = "create table " + databaseName + "." + charPid + "_Issues("
                + "memid int not null,"
                + "message varchar(128) not null,"
                + "type int not null,"
                + "state int not null,"
                + "posted timestamp not null,"
                + "constraint " + charPid + "_Issues_pk primary key (memid, message))";
        stmt.execute(createTable);

        createTable = "create table " + databaseName + "." + charPid + "_Files("
                + "filid int not null,"
                + "private boolean not null,"
                + "constraint " + charPid + "_Files_pk primary key (filid),"
                + "constraint " + charPid + "_Files_fk foreign key (filid) references " + databaseName + ".Files(filid) ON DELETE CASCADE)";
        stmt.execute(createTable);

        // Get tuple
        preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Project where pid = ?");
        preparedStmt.setInt(1, Integer.parseInt(info.get("pid")));
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet createTask(HashMap<String, String> info) throws SQLException{
        //insert record
        String charPid = Project.IDtoChars(Main.curProject.getPid());
        String query = "insert into " + databaseName + "." + charPid + "_Tasks values (?, ?, ?, ? ,?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Integer.parseInt(info.get("tid")));
        preparedStmt.setString(2, info.get("title"));
        preparedStmt.setInt(3, Integer.parseInt(info.get("memid")));
        preparedStmt.setString(4, info.get("desc"));
        preparedStmt.setDate(5, Date.valueOf(info.get("kickoff")));
        preparedStmt.setDate(6, Date.valueOf(info.get("deadline")));
        preparedStmt.setBoolean(7, Boolean.parseBoolean(info.get("complete")));
        preparedStmt.execute();

        // Get tuple
        query = "select * from " + databaseName + "." + charPid + "_Tasks where tid=?";
        preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Integer.parseInt(info.get("tid")));
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public void addMembers(HashMap<String, HashMap<String, String>> info, String charPid) throws SQLException {
        //save project team
        String teamQuery = " insert into " + databaseName + "." + charPid + "_Team"
                + " values (?)";
        PreparedStatement preparedTeamStmt = conn.prepareStatement(teamQuery);

        //add each row to the database
        for(int i = 1; i <= info.size(); i++) {
            HashMap<String,String> row = info.get(String.valueOf(i));

            //populate data and save row
            preparedTeamStmt.setInt(1, Integer.parseInt(row.get("memid")));
            preparedTeamStmt.execute();
        }
    }

    public void dropMembers(String charPid) throws SQLException {
        String dropQuery = " delete from " + databaseName + "." + charPid + "_Team";
        PreparedStatement preparedStatement = conn.prepareStatement(dropQuery);
        preparedStatement.execute();
    }

    public ResultSet updateProject(HashMap<String, String> info) throws SQLException {
        // Update record
        String query = " update "+ databaseName +".Project" +
                " set investmentCosts = ?, budget = ?, roi = ?, kickoff = ?," +
                " deadline = ?, issueScore = ?, tag1 = ?, tag2 = ?, tag3 = ?, tag4 = ?," +
                " complete = ?, title = ?, description = ? where pid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setFloat(1, Float.parseFloat(info.get("investmentCosts")));
        preparedStmt.setFloat(2, Float.parseFloat(info.get("budget")));
        preparedStmt.setFloat(3, Float.parseFloat(info.get("roi")));
        preparedStmt.setDate(4, Date.valueOf(info.get("kickoff")));
        preparedStmt.setDate(5, Date.valueOf(info.get("deadline")));
        preparedStmt.setFloat(6, Float.parseFloat(info.get("issueScore")));
        preparedStmt.setString(7, info.get("tag1"));
        preparedStmt.setString(8, info.get("tag2"));
        preparedStmt.setString(9, info.get("tag3"));
        preparedStmt.setString(10, info.get("tag4"));
        preparedStmt.setBoolean(11, Boolean.parseBoolean(info.get("complete")));
        preparedStmt.setString(12, info.get("title"));
        preparedStmt.setString(13, info.get("description"));
        preparedStmt.setInt(14, Integer.parseInt(info.get("pid")));
        preparedStmt.execute();

        // Get tuple
        preparedStmt = conn.prepareStatement("select * from "+ databaseName +".Project where pid = ?");
        preparedStmt.setInt(1, Integer.parseInt(info.get("pid")));
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public void removeProject(int pid) throws SQLException {
        // Delete record
        String charPid = Project.IDtoChars(pid);
        String query = " delete from " + databaseName + ".Project where pid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, pid);
        preparedStmt.execute();
        query = "select * from " + databaseName + "." + charPid + "_Components;";
        ResultSet rs = preparedStmt.executeQuery(query);

        // Delete related tables
        String dropTable = "";
        dropTable += "drop table " + databaseName + "." + charPid + "_Components;";
        dropTable += "drop table " + databaseName + "." + charPid + "_Team;";
        dropTable += "drop table " + databaseName + "." + charPid + "_Tasks;";
        dropTable += "drop table " + databaseName + "." + charPid + "_Issues;";
        dropTable += "drop table " + databaseName + "." + charPid + "_Files;";
        // Remove all component tables

        while (!rs.next()) {
            // Drop component table info
            String charCid = Project.IDtoChars(rs.getInt("cid"));
            dropTable += "drop table " + databaseName + "." + charPid + "_" + charCid + "_Data;";
        }
        preparedStmt.execute(dropTable);
    }

    public void removeComponent(int pid, int cid) throws SQLException {
        // Delete record
        String charPid = Project.IDtoChars(pid);
        String charCid = Project.IDtoChars(cid);
        String query = "delete from " + databaseName + "." + charPid + "_Components where cid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, cid);
        preparedStmt.execute();

        query = "drop table " + databaseName + "." + charPid + "_" + charCid + "_Data";
        preparedStmt = conn.prepareStatement(query);
        preparedStmt.execute();
    }

    public ResultSet getProjects() throws SQLException {
        String query = "select * from " + databaseName + ".Project";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getMembers() throws SQLException {
        String query = "select * from " + databaseName + ".ProjectMember";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getComponents() throws SQLException {
        String query = "select * from " + databaseName + ".Component";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public void closeDB() throws SQLException {
        conn.close();
    }

    public ResultSet getComponentData(int pid, int cid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String charCid = Project.IDtoChars(cid);
        String query = "select * from " + databaseName + "." + charPid + "_" + charCid + "_Data";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getComponentTablePart(int pid, int cid, int partid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String charCid = Project.IDtoChars(cid);
        String charPartid = Project.IDtoChars(partid);
        String query = "select * from " + databaseName + "." + charPid + "_" + charCid + "_" + charPartid + "_Value";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getProjectMembers(int pid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "select * from " + databaseName + ".ProjectMember INNER JOIN " + databaseName + "." + charPid + "_Team USING(memid)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }


    public ResultSet getProjectByName(String title) throws SQLException{
        String query = "select * from " + databaseName + ".Project where pid=?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Math.abs(title.hashCode()));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying projects with matching titles");
        return rs;
    }


    public ResultSet getTimelines() throws SQLException {
        String query = "select title, kickoff, deadline from " + databaseName + ".Project";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet matchUsername(String username) throws SQLException {
        String query = "select * from " + databaseName + ".Account where username=?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, username);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getMemberAccount(int id) throws SQLException {
        String query = "select * from " + databaseName + ".ProjectMember where memid=?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, id);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getManagerAccount(int id) throws SQLException {
        String query = "select * from " + databaseName + ".ProjectManager where manid=?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, id);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getProjectComponents(int pid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "select * from " + databaseName + "." + charPid + "_Components NATURAL JOIN " + databaseName + ".Component";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public void createLog(HashMap<String, String> info) throws SQLException, InterruptedException {
        String query = "insert into " + databaseName + ".Log values(?, ?, ?, ?);";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, Integer.parseInt(info.get("manid")));
        preparedStatement.setInt(2, Integer.parseInt(info.get("pid")));
        preparedStatement.setString(4, info.get("message"));

        // Get time and try to insert
        while(true) {
            try {
                preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
                preparedStatement.execute();
                break;
            } catch (SQLIntegrityConstraintViolationException e) {
                // Error on insertion, try again
                wait(100);
            }
        }

    }

    public ResultSet createIssue(HashMap<String, String> info) throws SQLException {
        String charPid = Project.IDtoChars(Main.curProject.getPid());
        String query = "insert into " + databaseName + "." + charPid + "_Issues values (?, ?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Main.account.getId());
        preparedStmt.setString(2, info.get("message"));
        preparedStmt.setInt(3, Integer.parseInt(info.get("type")));
        preparedStmt.setInt(4, IssueState.NEW); // Set issue as new (not seen or done)
        preparedStmt.setTimestamp(5, Timestamp.from(Instant.now())); // Set current date
        preparedStmt.execute();

        query = "select * from " + databaseName + "." + charPid + "_Issues where memid=? and message=?";
        preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Main.account.getId());
        preparedStmt.setString(2, info.get("message"));
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getProjectsWithManagerName() throws SQLException {
        String query = "select * from " + databaseName + ".Project inner join " + databaseName + ".ProjectManager USING(manid)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet createComponent(HashMap<String, String> info) throws SQLException {
        // Add component template to system
        String query = " insert into "+ databaseName + ".Component" + " values (?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Integer.parseInt(info.get("cid")));
        preparedStmt.setString (2, info.get("title"));
        preparedStmt.setString (3, info.get("template"));
        preparedStmt.execute();

        preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Component where cid = ?");
        preparedStmt.setInt(1, Integer.parseInt(info.get("cid")));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on create Component");
        return rs;
    }

    public void addComponent(HashMap<String, String> info, ArrayList<String> data) throws SQLException {
        // Insert record into <Project charPid>Components
        String charPid = Project.IDtoChars(Integer.parseInt(info.get("pid")));
        String query = "INSERT INTO " + databaseName + "." + charPid + "_Components VALUES(?, ?);";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Integer.parseInt(info.get("cid")));
        preparedStmt.setString (2, (info.get("template")));
        preparedStmt.execute();

        // Create table <project charPid><component charCid>Data
        String charCid = Project.IDtoChars(Integer.parseInt(info.get("cid")));
        query = "create table " + databaseName + "." + charPid + "_" + charCid + "_Data ("
                + " partid int primary key,"
                + " value varchar(256))";
        preparedStmt = conn.prepareStatement(query);
        preparedStmt.execute();

        // Create query to insert data as records into <project charPid><component charCid>_Data
        query = " insert into " + databaseName + "." + charPid + "_" + charCid + "_Data" + " values (?, ?)";
        for (int j = 1; j < data.size(); j++) query += ", (?, ?)";
        preparedStmt = conn.prepareStatement(query);
        // Populate data and then execute
        for (int i = 0; i < data.size(); i++){
            preparedStmt.setInt(i*2+1, i+1);
            preparedStmt.setString(i*2+2, data.get(i));
        }
        preparedStmt.execute();
    }

    public void updateIssueScore(int pid, float score) throws SQLException {
        String query = "update " + databaseName + ".Project set issueScore = (issueScore + ?) WHERE pid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setFloat(1, score);
        preparedStmt.setInt(2, pid);
        preparedStmt.execute();
    }

    public ResultSet getProjectIssues(int pid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "select * from " + databaseName + "." + charPid + "_Issues";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public ResultSet getProjectTasks(int pid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "select * from " + databaseName + "." + charPid + "_Tasks";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        return rs;
    }

    public void markIssueSeen(HashMap<String, HashMap<String, String>> updates, String charPid) throws SQLException {
        String query = "update " + databaseName + "." + charPid + "_Issues set state = ? where memid = ? AND message = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);

        //add each row to the database
        for(int i = 1; i <= updates.size(); i++) {
            HashMap<String, String> row = updates.get(String.valueOf(i));

            //populate data and save row
            preparedStatement.setInt(1, IssueState.SEEN);
            preparedStatement.setInt(2, Integer.parseInt(row.get("memid")));
            preparedStatement.setString(3, row.get("message"));
            preparedStatement.execute();
        }
    }

    public void markIssueResolved(HashMap<String, HashMap<String, String>> updates, String charPid) throws SQLException {
        String query = "update " + databaseName + "." + charPid + "_Issues set state = ? where memid = ? AND message = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);

        //add each row to the database
        for(int i = 1; i <= updates.size(); i++) {
            HashMap<String,String> row = updates.get(String.valueOf(i));

            //populate data and save row
            preparedStatement.setInt(1, IssueState.DONE);
            preparedStatement.setInt(2, Integer.parseInt(row.get("memid")));
            preparedStatement.setString(3, row.get("message"));
            preparedStatement.execute();
        }
    }

    public void markIssueNew(HashMap<String, HashMap<String, String>> updates, String charPid) throws SQLException {
        String query = "update " + databaseName + "." + charPid + "_Issues set state = ? where memid = ? AND message = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);

        //add each row to the database
        for(int i = 1; i <= updates.size(); i++) {
            HashMap<String,String> row = updates.get(String.valueOf(i));

            //populate data and save row
            preparedStatement.setInt(1, IssueState.NEW);
            preparedStatement.setInt(2, Integer.parseInt(row.get("memid")));
            preparedStatement.setString(3, row.get("message"));
            preparedStatement.execute();
        }
    }

    public void markTask(int pid, int tid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "update " + databaseName + "." + charPid + "_Tasks set complete = true where tid = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, tid);
        preparedStatement.execute();
    }

    public void unmarkTask(int pid, int tid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "update " + databaseName + "." + charPid + "_Tasks set complete = false where tid = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, tid);
        preparedStatement.execute();
    }

    public void createFile(File file) throws SQLException {
        // Read file
        Blob blob = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            byte[] arr = new byte[(int) file.length()];
            blob = conn.createBlob();
            arr = stream.readAllBytes();
            blob.setBytes(1, arr);
        } catch (IOException e) {
            System.out.println("File does not exist, exiting program");
            e.printStackTrace();
            System.exit(ExitStatusType.FAILED_FILE_LOAD);
        }

        // Get file attribute
        String fullName = file.getName();
        int period = fullName.lastIndexOf('.');
        String extension = fullName.substring(period+1);

        // Execute query
        String query = "insert into " + databaseName + ".Files values(?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, fullName.hashCode());
        preparedStatement.setString(2, fullName);
        preparedStatement.setString(3, extension);
        preparedStatement.setBlob(4, blob);
        preparedStatement.execute();

        // Get recently added file
        query = "select * from " + databaseName + ".Files where filid = ?";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, fullName.hashCode());
        preparedStatement.execute();
    }

    public void downloadFile(int filid) throws SQLException, IOException {
        // Execute query
        String query = "select * from " + databaseName + ".Files where filid=?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, filid);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next(); // Move cursor
        Blob blob = rs.getBlob("file"); // Get blob of file

        String home = System.getProperty("user.home"); // Get home location of user pc
        File downloadedFile = new File(home + "/Downloads/" + rs.getString("filename"));
        FileOutputStream stream = new FileOutputStream(downloadedFile); // Create file and setup write
        stream.write(blob.getBinaryStream().readAllBytes()); // Write to file
    }

    public void addFiles(int pid, int[] filids, boolean[] isPrivates) throws SQLException {
        // Execute query
        String charPid = Project.IDtoChars(pid);
        String query = "insert into " + databaseName + "." + charPid + "_Files values (?, ?)";
        for (int j = 1; j < filids.length; j++) query += ", (?, ?)"; // Create query string
        PreparedStatement preparedStatement = conn.prepareStatement(query); // Create statement
        for (int i = 0; i < filids.length; i++) { // Set values
            preparedStatement.setInt(i*2+1, filids[i]);
            preparedStatement.setBoolean(i*2+2, isPrivates[i]);
        }
        preparedStatement.execute(); // Insert records
    }

    public void removeFiles(int pid, int[] filids) throws SQLException {
        // Execute query
        String charPid = Project.IDtoChars(pid);
        String query = "delete from " + databaseName + "." + charPid + "_Files where filid=?";
        for (int j = 0; j< filids.length; j++) query += " or filid=?"; // Create query string
        PreparedStatement preparedStatement = conn.prepareStatement(query); // Create statement
        for (int i = 0; i < filids.length; i++) { // Set values
            preparedStatement.setInt(i+1, filids[i]);
        }
        preparedStatement.execute(); // Remove records
    }

    public void deleteFiles(int[] filids) throws SQLException {
        // Execute query
        String query = "delete from " + databaseName + ".Files where filid=?";
        for (int j = 0; j< filids.length; j++) query += " or filid=?"; // Create query string
        PreparedStatement preparedStatement = conn.prepareStatement(query); // Create statement
        for (int i = 0; i < filids.length; i++) { // Set values
            preparedStatement.setInt(i+1, filids[i]);
        }
        preparedStatement.execute(); // Delete records
    }

    public ResultSet getProjectFiles(int pid) throws SQLException {
        // Execute query
        String charPid = Project.IDtoChars(pid);
        String query = "select * from " + databaseName + "." + charPid + "_Files NATURAL JOIN " + databaseName + ".Files";
        PreparedStatement preparedStatement = conn.prepareStatement(query); // Create statement
        return preparedStatement.executeQuery(); // Delete records
    }

    public ResultSet updateProjectComponent(int pid, int cid, ArrayList<String> data) throws SQLException {
        // Drop all previous part data records
        String charPid = Project.IDtoChars(pid);
        String charCid = Project.IDtoChars(cid);
        String query = "DELETE FROM " + databaseName + "." + charPid + "_" + charCid + "_Data";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.execute();

        // Create query to insert data as records into <project charPid><component charCid>_Data
        query = "insert into " + databaseName + "." + charPid + "_" + charCid + "_Data" + " values (?, ?)";
        for (int j = 1; j < data.size(); j++) query += ", (?, ?)";
        preparedStmt = conn.prepareStatement(query);
        // Populate data and then execute
        for (int i = 0; i < data.size(); i++){
            preparedStmt.setInt(i*2+1, i+1);
            preparedStmt.setString(i*2+2, data.get(i));
        }
        preparedStmt.execute();

        // Get new data
        query = "SELECT * FROM " + databaseName + "." + charPid + "_" + charCid + "_Data";
        preparedStmt = conn.prepareStatement(query);
        return preparedStmt.executeQuery();
    }

    public void removeTask(int pid, int tid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "delete from " + databaseName + "." + charPid + "_Tasks where tid = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, tid);
        preparedStatement.execute();
    }

    public ResultSet updateProjectTask(int pid, int tid, HashMap<String, String> info) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "update " + databaseName + "." + charPid + "_Tasks set title = ?, memid = ?, description = ?, kickoff = ?, deadline = ? where tid = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, info.get("title"));
        preparedStatement.setInt(2, Integer.parseInt(info.get("memid")));
        preparedStatement.setString(3, info.get("desc"));
        preparedStatement.setDate(4, Date.valueOf(info.get("kickoff")));
        preparedStatement.setDate(5, Date.valueOf(info.get("deadline")));
        preparedStatement.setInt(6, tid);
        preparedStatement.execute();

        // Return new task
        query = "select * from " + databaseName + "." + charPid + "_Tasks where tid = ?";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, tid);
        return preparedStatement.executeQuery();
    }

    public void updateMemberStatus(int memid, int newStatus, int accountType) throws SQLException {
        if (accountType == AccountType.MANAGER) {
            String query = "update " + databaseName + ".ProjectManager set status = ? where manid = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, newStatus);
            preparedStatement.setInt(2, memid);
            preparedStatement.execute();
        }
        else if (accountType == AccountType.MEMBER) {
            String query = "update " + databaseName + ".ProjectMember set status = ? where memid = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, newStatus);
            preparedStatement.setInt(2, memid);
            preparedStatement.execute();
        }
    }
}
// End of Database Class
