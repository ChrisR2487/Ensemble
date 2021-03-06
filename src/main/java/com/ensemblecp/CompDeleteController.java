package com.ensemblecp;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
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

public class CompDeleteController implements Initializable {
    @FXML CheckBox remCheckBox;
    @FXML TextArea logBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    public static Component component;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remCheckBox.setText(remCheckBox.getText() + component.getTitle() + "?");
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
        log.put("message", "Delete component " + component.getTitle() + " from project, reason: " + logBox.getText());
        db.createLog(log);

        // Delete component from database
        db.removeComponent(curProject.getPid(), component.getCid());
        db.closeDB();
        // Delete component from project object
        Main.curProject.getComponents().remove(component);
        // Go to dashboard
        Main.show("projOverview");
    }

    public void onCancel_onClick(Event e) throws IOException {
        Main.show("projOverview");
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
