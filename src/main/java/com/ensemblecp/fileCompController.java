package com.ensemblecp;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class fileCompController implements Initializable {
    public TextArea textArea;
    public Pane dropInstructions;
    public ImageView imgView;

    @FXML
    private Label label;

    @FXML
    private BorderPane borderPane;

    @FXML
    public void handleButton(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File Dialog");
        Stage stage = (Stage)borderPane.getScene().getWindow();

        fileChooser.showOpenDialog(stage);
    }

    private void makeTextAreaDragTarget(Node node) {
        node.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
        });

        node.setOnDragExited(event -> {
            dropInstructions.setVisible(false);
        });

        node.setOnDragEntered(event -> {
            dropInstructions.setVisible(true);
        });

        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if(event.getDragboard().hasFiles()){
                fileLoaderTask(db.getFiles().get(0)).run();
            }

            dropInstructions.setVisible(false);
        });
    }

    private Task<String> fileLoaderTask(File fileToLoad) {
        //Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));

                //Use Files.lines() to calculate total lines - used for progress
                long lineCount;
                try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
                    lineCount = stream.count();
                }

                //Load in all lines one by one into a StringBuilder separated by "\n" - compatible with TextArea
                String line;
                StringBuilder totalFile = new StringBuilder();
                long linesLoaded = 0;
                while ((line = reader.readLine()) != null) {
                    totalFile.append(line);
                    totalFile.append("\n");
                    updateProgress(++linesLoaded, lineCount);
                }

                return totalFile.toString();
            }
        };

        //If successful, update the text area, display a success message and store the loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.setText(loadFileTask.get());
            } catch (InterruptedException | ExecutionException e) {
                textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });

        //If unsuccessful, set text area with error message and status message to failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            textArea.setText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
        });

        return loadFileTask;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //makeTextAreaDragTarget(textArea);
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(-1);
    }
}
