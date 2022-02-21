package com.ensemblecp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    private final boolean isFullscreen = true;
    private FXMLLoader fxmlLoader;
    private Scene mainScene;
    private Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize stage
        mainStage = stage;
        mainStage.setFullScreen(isFullscreen);
        mainStage.setTitle("Ensemble");
            //stage.initStyle(StageStyle.UNDECORATED); TODO: change to login screen, then use this

        // Show startup screen
        show("Dashboard");
        mainStage.show();
    }

    public void show(String screenName) throws IOException {
        fxmlLoader = new FXMLLoader(Main.class.getResource(screenName+".fxml"));
        Scene newScene = new Scene(fxmlLoader.load(), 1600, 900);
        mainStage.setScene(newScene);
    }

    public static void main(String[] args) {
        launch();
    }
}
