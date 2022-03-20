package com.ensemblecp;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.ensemblecp.Main.curProject;

public class ProjDeleteController implements Initializable {
    @FXML CheckBox remCheckBox;
    @FXML TextField logBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private final Border INVALID_BORDER = new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5)));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remCheckBox.setText(remCheckBox.getText() + curProject.getTitle() + "?");
    }

    public void onSubmit_onClick(Event e) throws IOException, SQLException, InterruptedException, NoSuchAlgorithmException {
        //set login border to normal
        usernameField.setBorder(null);
        passwordField.setBorder(null);

        // Check for confirmed
        if (!remCheckBox.isSelected()) return;
        //if login fails, return
        if(!verifyLogin()) return;

        //if login succeeded, do following:
        // Enter log of removal
        Database db = new Database();
        HashMap<String, String> log = new HashMap<>();
        log.put("pid", String.valueOf(Main.curProject.getPid()));
        log.put("manid", String.valueOf(Main.account.getId()));
        log.put("message", "Delete Project, reason: " + logBox.getText());
        db.createLog(log);

        // Delete project
        db.removeProject(curProject.getPid()); // Handles all table deletion
        db.closeDB();
        Main.projects.remove(curProject);
        curProject = null;

        // Go to dashboard
        Main.show("Dashboard");
    }

    public void onCancel_onClick(Event e) throws IOException {
        Main.show("projOverview");
    }

    //verify login
    public boolean verifyLogin() throws IOException, SQLException, NoSuchAlgorithmException
    { // TODO: Confirm this works
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
            usernameField.setBorder(INVALID_BORDER);
            passwordField.setBorder(INVALID_BORDER);

            return false; // End method execution
        }
        do {
            // Compute hash
            String inputHash = hashInput(userMatches.getString("salt") + pass);

            // Check for password equality
            System.out.println(userMatches.getString("password"));
            System.out.println(inputHash);
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
        usernameField.setBorder(INVALID_BORDER);
        passwordField.setBorder(INVALID_BORDER);
        return false;
    }

    private String hashInput(String input) throws NoSuchAlgorithmException { // TODO: Confirm this works
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        byte[] inputByteHash = hash.digest(input.getBytes());
        return new String(inputByteHash);
    }

}
