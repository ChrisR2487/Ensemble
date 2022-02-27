package com.ensemblecp;

import java.sql.Array;
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
            if (type == 'T') {
                part = new Part(cData.getInt("partid"), type, cData.getString("value"), db, pid, cid);
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
    public void update(ResultSet compInfo) {
        // TODO: Given the new <pid>-<pid>-Data table, update component data

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
     * The Part class ???
     */
    public class Part {
        /* Class Variables */
        private int partid;
        private char type;
        private String data;
        private String[][] table;

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
         * @param cid
         */
        public Part(int partid, char type, String data, Database db, int pid, int cid) throws SQLException {
            this(partid, type, data);

            // Table type, need data
            ResultSet pData = db.getComponentTablePart(pid,cid,partid);
            while(pData.next()) {
                // TODO: Convert resultset of table part data to stored data
            }
        }

        /* Class Methods */
            // TODO: Finish documentation comments
        /**
         *
         * @param tData
         */
        public void updateTable(ResultSet tData) {
            // Update table
                // TODO: Complete this method, using tData tuples of <pid>-<cid>-<partid>-Value
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

        public String[][] getTable() {
            return table;
        }

        private void setTable(String[][] table) {
            this.table = table;
        }
    }
    // End of Part Class
}
// End of Component Class
