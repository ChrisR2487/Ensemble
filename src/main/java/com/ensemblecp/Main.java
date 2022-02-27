package com.ensemblecp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;

import java.sql.*;

public class Main extends Application {
    private final static boolean isFullscreen = true;
    public static Project curProject;
    private static FXMLLoader fxmlLoader;
    private Scene mainScene;
    private static Stage mainStage;
    public static ArrayList<Project> projects;
    public final static int cacheLimit = 5;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize stage
        mainStage = stage;
        mainStage.setFullScreen(isFullscreen);
        mainStage.setTitle("Ensemble");
            //stage.initStyle(StageStyle.UNDECORATED); TODO: change to startup on login screen, then use this
        projects = new ArrayList<Project>(cacheLimit);

        // Show startup screen
        show("Dashboard");
        mainStage.show();
    }

    public static void show(String screenName) throws IOException {
        fxmlLoader = new FXMLLoader(Main.class.getResource(screenName+".fxml"));
        Scene newScene = new Scene(fxmlLoader.load(), 1600, 900);
        mainStage.setScene(newScene);
        mainStage.setFullScreen(isFullscreen);
    }

    public static void update() throws IOException {
        // TODO: Implement update stuff on view
    }

    public static void trimCache() {
        // Trims the Main.projects cache if necessary
        if (Main.projects.size() > cacheLimit) {
            Main.projects.remove(cacheLimit); // TODO: Confirm this works
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

    public static void main(String[] args) throws SQLException {
        //Project p = new Project("Text.java");
        Project p = new Project();
        p.update("tag1", "potato");
        p.update("complete", true);
        p.update("investmentCosts", (float) 1999.93);
        p.update("roi", (float) 1000);
        p.update("budget", (float) 1500000);
        p.update("kickoff", new Date(2021,9,19));
        p.update("deadline", new Date(2021,10,19));
        p.update("issueScore", (float) 4);
        //launch();
    }
}
