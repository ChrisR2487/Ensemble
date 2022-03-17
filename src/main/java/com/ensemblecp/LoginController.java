package com.ensemblecp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private final Border INVALID_BORDER = new Border(new BorderStroke(Color.RED,
                                      BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5)));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Do nothing
    }

    public void exitButton_onClick(MouseEvent mouseEvent) {
        System.exit(ExitStatusType.EXIT_BUTTON);
    }

    public void verifyLogin(ActionEvent actionEvent) throws IOException, SQLException, NoSuchAlgorithmException {
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

            return; // End method execution
        }
        do {
            // Compute hash
            String inputHash = hashInput(userMatches.getString("salt") + pass);
            // Check for password equality
            if (inputHash.equals(userMatches.getString("password"))) {
                // Account found
                Main.disableScreen(); // Disable screen
                Main.setCredentials(userMatches.getInt("id")); // Set account info
                Main.show("Dashboard"); // Swap scenes to dashboard
                Main.enableScreen(); // Enable screen
                db.closeDB(); // Close db
                return; // End method execution
            }
        } while(userMatches.next());

        // Login unsuccessful via password, show error
        db.closeDB(); // Close db, no password matches
        passwordField.setText("");
        usernameField.setBorder(INVALID_BORDER);
        passwordField.setBorder(INVALID_BORDER);
    }

    private String hashInput(String input) throws NoSuchAlgorithmException {
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        byte[] inputByteHash = hash.digest(input.getBytes());
        return new String(inputByteHash);
    }
}
