package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.ensemblecp.Main.curProject;

public class TaskDeleteController implements Initializable {
    @FXML CheckBox remCheckBox;
    @FXML TextField logBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    public static Task task;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remCheckBox.setText(remCheckBox.getText() + task.getTitle() + "?");
    }

    public void onSubmit_onClick(Event e) throws IOException, SQLException, InterruptedException, NoSuchAlgorithmException {
        //set login border to normal
        usernameField.setBorder(null);
        passwordField.setBorder(null);
        remCheckBox.setBorder(null);

        // Check for confirmed
        if (!remCheckBox.isSelected()){
            remCheckBox.setBorder(LoginController.INVALID_BORDER);
            return;
        }
        //if login fails, return
        if(!verifyLogin()) return;

        // if login succeeded, do following:
        // Enter log of removal
        Database db = new Database();
        HashMap<String, String> log = new HashMap<>();
        log.put("pid", String.valueOf(Main.curProject.getPid()));
        log.put("manid", String.valueOf(Main.account.getId()));
        log.put("message", "Delete task " + task.getTitle() + " from project, reason: " + logBox.getText());
        db.createLog(log);

        // Delete task from database
        db.removeTask(curProject.getPid(), task.getTid());
        db.closeDB();
        // Delete task from project object
        Main.curProject.getTasks().remove(task);
        // Go to dashboard
        Main.show("projBenchmark");
    }

    public void onCancel_onClick(Event e) throws IOException {
        Main.show("projBenchmark");
    }

    //verify login
    public boolean verifyLogin() throws IOException, SQLException, NoSuchAlgorithmException
    {
        // Get user input
        String pass = passwordField.getText();
        String user = usernameField.getText();

        // Check if username matches any tuples
        Database db = new Database();
        ResultSet userMatches = db.matchUsername(user);
        if (!userMatches.next()) {
            // Login unsuccessful via username, show error after delay
            db.closeDB(); // Close db, no username matches
            passwordField.setText("");
            usernameField.setBorder(LoginController.INVALID_BORDER);
            passwordField.setBorder(LoginController.INVALID_BORDER);

            return false; // End method execution
        }
        do {
            // Compute hash
            String inputHash = hashInput(userMatches.getString("salt") + pass);

            // Check for password equality
            if (inputHash.equals(userMatches.getString("password"))) {
                // Account found
                //Main.setCredentials(userMatches.getInt("id")); // Set account info
                db.closeDB(); // Close db
                return true; // End method execution
            }
        } while(userMatches.next());

        // Login unsuccessful via password, show error
        db.closeDB(); // Close db, no password matches
        passwordField.setText("");
        usernameField.setBorder(LoginController.INVALID_BORDER);
        passwordField.setBorder(LoginController.INVALID_BORDER);
        return false;
    }

    private String hashInput(String input) throws NoSuchAlgorithmException {
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        byte[] inputByteHash = hash.digest(input.getBytes());
        return new String(inputByteHash);
    }

}
