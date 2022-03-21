package com.ensemblecp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Component Class
/**
 * A class used to temporarily create a java object
 * which represents the data of a component.
 */
public class Component {
    /* Class Variables */
    private String template;
    private int cid;
    private ArrayList<Part> parts;

    /* Constructors */
    /**
     * Constructor with component template and db connection
     *
     * @param pid Project id
     * @param cid Component id
     * @param template Template of component
     * @param db Database connection
     */
    public Component(int pid, int cid, String template, Database db) throws SQLException {
        // Initialization
        this.cid = cid;
        this.template = template;
        this.parts = new ArrayList<>();

        // Get component data
        ResultSet cData = db.getComponentData(pid, cid);
        // Parse data
        while(cData.next()) {
            // Parse template
            char type = template.charAt(0);
            Part part;
            if (type == PartType.TABLE) {
                part = new Part(cData.getInt("partid"), type, cData.getString("value"), db, pid);
                template = template.substring(template.indexOf(']')+1);
            }
            else {
                part = new Part(cData.getInt("partid"), type, cData.getString("value"));
                template = template.substring(1);
            }
            parts.add(part);
        }
    }

    /* Class Methods */
        // TODO: Finish documentation comments
    /**
     * Update the component data
     *
     * @param compInfo
     */
    public void update(ResultSet compInfo, Database db) throws SQLException {
        for (Part part: parts) {
            compInfo.next(); // Position cursor
            if (part.type == PartType.TABLE) part.update(compInfo, db); // Update table part
            else part.update(compInfo); // Update part
        }
    }

    /* Getters and Setters */
    public String getTemplate() {
        return template;
    }

    private void setTemplate(String template) {
        this.template = template;
    }

    public int getCid() {
        return cid;
    }

    private void setCid(int cid) {
        this.cid = cid;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    private void setParts(ArrayList<Part> parts) {
        this.parts = parts;
    }

    // Part Class
    /**
     * The Part class, used to store values of parts in Components.
     */
    public class Part {
        /* Class Variables */
        private int partid;
        private char type;
        private String data;
        private ArrayList<ArrayList<String>> table;
        private int tableWidth;

        /* Constructors */
            // TODO: Finish documentation comments
        /**\
         * Constructor used for most part types
         *
         * @param partid
         * @param type
         * @param data
         */
        public Part(int partid, char type, String data) {
            this.partid = partid;
            this.type = type;
            this.data = data;
        }

        /**
         * Constructor used for tables
         *
         * @param partid
         * @param type
         * @param data
         * @param db
         * @param pid
         */
        public Part(int partid, char type, String data, Database db, int pid) throws SQLException {
            this(partid, type, data);

            // Table type, need data
            ResultSet pData = db.getComponentTablePart(pid,cid,partid); // Get table data
            updateTable(pData); // Updates table variable with data

            // Parse template for row size
            this.tableWidth = (int) (data.chars().filter(ch -> ch == ',').count() + 1); // Count number of commas, add 1
        }

        /* Class Methods */
            // TODO: Finish documentation comments
        /**
         *
         * @param tData
         */
        private void updateTable(ResultSet tData) throws SQLException { // TODO: Confirm this works
            // Update table
            ArrayList<ArrayList<String>> newTable = new ArrayList<>();
            while(tData.next()) {
                ArrayList<String> newRow = new ArrayList<>();
                for (int i = 1; i <= tableWidth; i++) {
                    newRow.add(tData.getString(i));
                }
                newTable.add(newRow);
            }
            setTable(newTable);
        }

        public void update(ResultSet partInfo) throws SQLException {
            // Update primitive data
            this.data = partInfo.getString("value");
        }

        public void update(ResultSet partInfo, Database db) throws SQLException {
            // Update table data
            updateTable(db.getComponentTablePart(Main.curProject.getPid(),cid,partid)); // Updates table variable with data
        }

        /* Getters and Setters */
        public int getPartid() {
            return partid;
        }

        private void setPartid(int partid) {
            this.partid = partid;
        }

        public char getType() {
            return type;
        }

        private void setType(char type) {
            this.type = type;
        }

        public String getData() {
            return data;
        }

        private void setData(String data) {
            this.data = data;
        }

        public ArrayList<ArrayList<String>> getTable() {
            return table;
        }

        private void setTable(ArrayList<ArrayList<String>> table) {
            this.table = table;
        }
    }
    // End of Part Class

    class PartType {
        public final static char TABLE = 'T';
        public final static char LIST = 'L';
        public final static char INTEGER = 'I';
        public final static char STRING = 'S';
        public final static char FILE = 'F';
        public final static char TIMELINE = 'B';
        public final static char FLOAT = 'R';
    }
}
// End of Component Class
