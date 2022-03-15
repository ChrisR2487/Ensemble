package com.ensemblecp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends Application {
    private final static boolean IS_FULLSCREEN = true;
    public static Project curProject;
    private static Stage mainStage;
    public static ArrayList<Project> projects;
    public final static int cacheLimit = 5;
    public static Account account;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize stage
        mainStage = stage;
        mainStage.setTitle("Ensemble");
        stage.initStyle(StageStyle.UNDECORATED);
        projects = new ArrayList<Project>(cacheLimit+1);

        // Show startup screen
        show("login");
        mainStage.show();
    }

    public static void show(String screenName) throws IOException {
        // Load FXML file and initial dimensions
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(screenName + ".fxml")); // Get FXML file
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine scene data
        Scene newScene;
        if (screenName.equals("login")) newScene = new Scene(fxmlLoader.load(), 800, 450);
        else newScene = new Scene(fxmlLoader.load(), screen.getWidth(), screen.getHeight());
        mainStage.setScene(newScene);
    }

    public static void disableScreen() {
        // Change screen view
        mainStage.hide();
        mainStage.setX(0.0);
        mainStage.setY(0.0);
    }

    public static void enableScreen() {
        mainStage.show();
    }

    public static void setCredentials(int id) throws SQLException { // TODO: Confirm this works
        Database db = new Database();
        ResultSet rs = db.getManagerAccount(id); // Get tuple with info
        if (!rs.next()) {
            // Member account
            rs = db.getMemberAccount(id);
            rs.next(); // Move pointer
            try {
                account = new Account(rs, AccountType.MEMBER);
            } catch (SQLException e) {
                // No matches found despite login successful, throw error
                throw new IllegalStateException("Error while processing login.");
            }
        }
        else {
            // Manager account
            account = new Account(rs, AccountType.MANAGER);
        }
        db.closeDB(); // Close db connection

    }

    public static void trimCache() {
        // Trims the Main.projects cache if necessary
        if (Main.projects.size() > cacheLimit) {
            Main.projects.remove(cacheLimit);
        }
    }

    public static Project inCache(int pid) {
        // Finds if project is in cache, returns null otherwise
        for (Project proj : Main.projects) {
            if (proj.getPid() == pid) return proj;
        }
        return null;
    }

    public static void refreshCache(Project proj) {
        Main.projects.remove(proj);
        Main.projects.add(0, proj);
    }

    public static void main(String[] args) throws SQLException { launch();}
}
