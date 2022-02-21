package com.ensemblecp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
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
