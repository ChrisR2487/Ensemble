package com.ensemblecp;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private int id;
    private String name;
    private String position;
    private String status;
    private int type;

    public Account(ResultSet info, int type) throws SQLException {
        this.name = info.getString("name");
        this.position = info.getString("position");
        this.status = info.getString("status");
        this.type = type;
        this.id = type == AccountType.MEMBER ? Integer.parseInt(info.getString("memid")): Integer.parseInt(info.getString("manid"));
    }

    /* Accessor and Mutators */
    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    private void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    private void setType(int type) {
        this.type = type;
    }
}

class AccountType {
    public final static int MEMBER = 0;
    public final static int MANAGER = 1;
}

class StatusType {
    public final static int AVAILABLE = 0;
    public final static int BUSY = 1;
    public final static int AWAY = 2;

    public static String mapStatus(int status) {
        switch(status) {
            case AVAILABLE -> {
                return "Available";
            }
            case BUSY -> {
                return "Busy";
            }
            case AWAY -> {
                return "Away";
            }
            default -> {
                return "N/A";
            }
        }
    }
}
