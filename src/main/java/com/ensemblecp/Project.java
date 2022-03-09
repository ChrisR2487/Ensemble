package com.ensemblecp;// Imports

import java.sql.*;
import java.util.ArrayList;

// Project Class
/**
 * A class used to temporarily create a java object
 * which represents the data of a project.
 */
public class Project {
    /* Class variables */
    private int pid; // Id of project
    private String title; // Title of the project
    private String description; // Description of the project
    private float investmentCosts; // Investment costs of the project
    private float budget; // Budget of the project
    private float roi; // Return on investment of the project
    private Date kickoff; // The starting working date of the project
    private Date deadline; // The submission deadline of the project
    private float issueScore; // The issue score of the project
    private String tag1; // Tag 1 of the project
    private String tag2; // Tag 2 of the project
    private String tag3; // Tag 3 of the project
    private String tag4; // Tag 4 of the project
    private boolean complete; // Is the project marked as complete?
    private ArrayList<Component> components; // List of project components
    private int manid = 0; // manid of project manager

    /* Class Constructors */
    /**
     * Default constructor.
     */
    public Project(ResultSet projectInfo, ResultSet componentInfo, Database db) throws SQLException {
        // Save data
        update(projectInfo);

        // Save components
        if (componentInfo != null) this.components = parseComponents(componentInfo, db);
    }

    /* Class Methods */
    /**
     *  Update project object and project record
     */
    public void update(ResultSet projectInfo) throws SQLException {
        // Save data
        projectInfo.next();
        this.pid = projectInfo.getInt("pid");
        this.title = projectInfo.getString("title");
        this.description = projectInfo.getString("description");
        this.investmentCosts = projectInfo.getFloat("investmentCosts");
        this.budget = projectInfo.getFloat("budget");
        this.roi = projectInfo.getFloat("roi");
        this.kickoff = projectInfo.getDate("kickoff");
        this.deadline = projectInfo.getDate("deadline");
        this.issueScore = projectInfo.getFloat("issueScore");
        this.tag1 = projectInfo.getString("tag1");
        this.tag2 = projectInfo.getString("tag2");
        this.tag3 = projectInfo.getString("tag3");
        this.tag4 = projectInfo.getString("tag4");
        this.complete = projectInfo.getBoolean("complete");
        this.manid = projectInfo.getInt("manid");

        // Check for overbudget
            // TODO: Add this functionality to check for overbudget
    }

    /**
     * Parse component info to create all component objects
     * @param compInfo List of components in project
     * @return List of Component objects for project
     */
    private ArrayList<Component> parseComponents(ResultSet compInfo, Database db) throws SQLException {
        // Initial setup
        if (!compInfo.next()) return new ArrayList<Component>(); // Check for no components

        // Parse each component and its data
        ArrayList<Component> componentsAL = new ArrayList<Component>();
        do {
            // Create component object
            Component comp = new Component(this.pid, compInfo.getInt("cid"), compInfo.getString("template"), db); // Pass parameters to constructor
            componentsAL.add(comp);
        } while (compInfo.next());

        // Return components
        return componentsAL;
    }

    public static String IDtoChars(int pid) {
        String sPid = String.valueOf(pid);
        char[] cPid = sPid.toCharArray();
        char[] charPid = new char[cPid.length];
        for (int i = 0; i < cPid.length; i++) {
            charPid[i] = (char) (Integer.parseInt(String.valueOf(cPid[i])) + 65); // Convert number to letter
        }
        return String.valueOf(charPid);
    }

    public String getCompPartData(int cIndex, int pIndex) {
        return getCompParts(cIndex).get(pIndex).getData();
    }

    public ArrayList<Component.Part> getCompParts(int cIndex) {
        return this.components.get(cIndex).getParts();
    }

    /**
     * Set project timeline dates
     * @param kickoff Starting date of project
     * @param deadline Ending date of project
     */
    private void setProjectTimeline(Date kickoff, Date deadline) {
        setKickoff(kickoff);
        setDeadline(deadline);
    }

    /**
     * Set all tags of a project
     * @param tag1 Tag 1
     * @param tag2 Tag 2
     * @param tag3 Tag 3
     * @param tag4 Tag 4
     */
    private void setTags(String tag1, String tag2, String tag3, String tag4) {
        setTag1(tag1);
        setTag2(tag2);
        setTag3(tag3);
        setTag4(tag4);
    }

    /* Getters & Setters */
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public float getInvestmentCosts() {
        return investmentCosts;
    }

    private void setInvestmentCosts(float investmentCosts) {
        this.investmentCosts = investmentCosts;
    }

    public float getBudget() {
        return budget;
    }

    private void setBudget(float budget) {
        this.budget = budget;
    }

    public float getRoi() {
        return roi;
    }

    private void setRoi(float roi) {
        this.roi = roi;
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

    public float getIssueScore() {
        return issueScore;
    }

    private void setIssueScore(float issueScore) {
        this.issueScore = issueScore;
    }

    public String getTag1() {
        return tag1;
    }

    private void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    private void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    private void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getTag4() {
        return tag4;
    }

    private void setTag4(String tag4) {
        this.tag4 = tag4;
    }

    public boolean isComplete() {
        return complete;
    }

    private void setComplete(boolean complete) {
        this.complete = complete;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    private void setComponents(ArrayList<Component> components) {
        this.components = components;
    }

    public int getManid() {
        return manid;
    }

    private void setManid(int manid) {
        this.manid = manid;
    }
}
// End of Project Class
