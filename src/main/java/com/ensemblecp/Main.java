package com.ensemblecp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends Application {
    private final static boolean IS_FULLSCREEN = true;
    public static Project curProject;
    private static Stage mainStage;
    public static ArrayList<Project> projects;
    public final static int CACHE_LIMIT = 5;
    public static Account account;
    public final static int ATTEMPT_LIMIT = 10;
    public final static long MB = 1024*1024;
    public final static long FILE_SIZE_LIMIT = 16*MB;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        // Initialize stage
        mainStage = stage;
        mainStage.setTitle("Ensemble");
        stage.initStyle(StageStyle.UNDECORATED);
        projects = new ArrayList<Project>(CACHE_LIMIT +1);

        // Show startup screen
        show("customComp");
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

    public static void setCredentials(int id) throws SQLException {
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
                System.out.println("Error while processing login, stopping software.");
                db.closeDB();
                System.exit(ExitStatusType.FAILED_LOGIN);
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
        if (Main.projects.size() > CACHE_LIMIT) {
            Main.projects.remove(CACHE_LIMIT);
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

    public static File browseForFile() {
        return new FileChooser().showOpenDialog(mainStage);
    }

    public static void main(String[] args) throws SQLException { launch();}
}

class ExitStatusType {
    public final static int EXIT_BUTTON = 0;
    public final static int FAILED_LOAD = -1;
    public final static int FAILED_QUERY = -2;
    public final static int FAILED_LOGIN = -3;
    public final static int FAILED_FILE_LOAD = -4;
}