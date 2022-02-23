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
    public static int pid = 0; // TODO: Set up database to use pid system
    public static String databaseName = ""; // Name of database in system, prepended on table names

    /* Class Constructors */
    /**
     * Default constructor.
     */
    public Database() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:mysql://34.150.158.26:3306","root","G6DevsOP2487!");
        /*
        String createTable = "create table guestbook.Project\n" +
                "(\n" +
                "    title           varchar(30) not null,\n" +
                "    investmentCosts float       not null,\n" +
                "    budget          float       not null,\n" +
                "    roi             float       not null,\n" +
                "    kickoff         date        not null,\n" +
                "    deadline        date        not null,\n" +
                "    issueScore      float       not null,\n" +
                "    tag1            varchar(30) not null,\n" +
                "    tag2            varchar(30) not null,\n" +
                "    tag3            varchar(30) not null,\n" +
                "    tag4            varchar(30) not null,\n" +
                "    complete        boolean     not null,\n" +
                "    constraint Project_pk\n" +
                "        primary key (title)\n" +
                ");\n";
        Statement stmt = conn.createStatement();
        stmt.execute(createTable);
         */
    }

    public ResultSet getProject(int pid) throws SQLException {
        // Get tuple TODO: Fix table so this works
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
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString (1, info.get("title"));
        preparedStmt.setFloat(2, Float.parseFloat(info.get("investmentCosts")));
        preparedStmt.setFloat(3, Float.parseFloat(info.get("budget")));
        preparedStmt.setFloat(4, Float.parseFloat(info.get("roi")));
        preparedStmt.setDate(5, Date.valueOf(info.get("kickoff")));
        preparedStmt.setDate(6, Date.valueOf(info.get("deadline")));
        preparedStmt.setFloat(7, Float.parseFloat(info.get("issueScore")));
        preparedStmt.setString(8, info.get("tag1"));
        preparedStmt.setString(9, info.get("tag2"));
        preparedStmt.setString(10, info.get("tag3"));
        preparedStmt.setString(11, info.get("tag4"));
        preparedStmt.setBoolean(12, Boolean.parseBoolean(info.get("complete")));
        preparedStmt.execute();

        // Get tuple
        preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Project where title = ?");
        preparedStmt.setString(1, info.get("title"));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on create project");
        return rs;
    }

    public ResultSet updateProject(HashMap<String, String> info) throws SQLException {
        // Update record
        String query = " update guestbook.Project" +
                " set investmentCosts = ?, budget = ?, roi = ?, kickoff = ?," +
                " deadline = ?, issueScore = ?, tag1 = ?, tag2 = ?, tag3 = ?, tag4 = ?," +
                " complete = ? where title = ?";
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
        preparedStmt.execute();

        // Get tuple
        preparedStmt = conn.prepareStatement("select * from guestbook.Project where title = (?)");
        preparedStmt.setString(1, info.get("title"));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on update project");
        return rs;
    }

    public void removeProject(String title) throws SQLException {
        String query = " delete from " + databaseName + ".Project where title = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, title);
        preparedStmt.execute();
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
}
// End of Database Class

