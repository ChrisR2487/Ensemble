package com.ensemblecp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    private static final boolean isFullscreen = true;
    private static FXMLLoader fxmlLoader;
    private Scene mainScene;
    private static Stage mainStage;
    public static ArrayList<Project> projects;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize stage
        mainStage = stage;
        mainStage.setFullScreen(isFullscreen);
        mainStage.setTitle("Ensemble");
            //stage.initStyle(StageStyle.UNDECORATED); TODO: change to login screen, then use this
        projects = new ArrayList<Project>();

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

    public static void main(String[] args) {
        launch();
    }
}
