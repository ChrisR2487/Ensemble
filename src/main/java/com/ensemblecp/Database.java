package com.ensemblecp;// Imports
import java.sql.*;
import java.util.HashMap;

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

    public ResultSet getProject(int pid) throws SQLException {
        // Get tuple
        PreparedStatement preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Project where pid = ?");
        preparedStmt.setInt(1, pid);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on create project");
        return rs;
    }


    /* Class Methods */
    /**
     *  Create project record
     */
    public ResultSet createProject(HashMap<String, String> info) throws SQLException {
        // Insert record
        String query = " insert into "+ databaseName + ".Project"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        preparedStmt.execute();

        // Add related tables TODO: Add other tables to add
        String createTable = "create table " + databaseName + "." + info.get("pid") + "-Component\n" +
                "(\n" +
                "    cid             int         not null,\n" +
                "    template        varchar(128) not null,\n" +
                "    constraint Component_pk\n" +
                "        primary key (cid)\n" +
                ");\n";
        Statement stmt = conn.createStatement();
        stmt.execute(createTable);

        // Get tuple
        preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Project where pid = ?");
        preparedStmt.setInt(1, Integer.parseInt(info.get("pid")));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on create project");
        return rs;
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
        System.out.println("Success on update project");
        return rs;
    }

    public void removeProject(int pid) throws SQLException {
        // Delete record
        String query = " delete from " + databaseName + ".Project where pid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, pid);
        preparedStmt.execute();

        // Delete related tables TODO: Add other removes
        String createTable = "drop table " + databaseName + "." + String.valueOf(pid) + "-Component;";
        Statement stmt = conn.createStatement();
        stmt.execute(createTable);

        // Remove all component tables
            // TODO: implement this

        // Display message
        System.out.println("Success on removing a project");
    }

    public ResultSet getProjects() throws SQLException {
        String query = "select * from " + databaseName + ".Project";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying project table");
        return rs;
    }

    public void closeDB() throws SQLException {
        conn.close();
    }

    public ResultSet getComponentData(int pid, int cid) {
        return null;
    }

    public ResultSet getComponentTablePart(int pid, int cid, int partid) {
        return null;
    }
}
// End of Database Class

