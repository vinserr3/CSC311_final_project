package viewmodel;

import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.UserSession;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    @FXML
    public void login(ActionEvent actionEvent) {
        String email = usernameTextField.getText();
        String password = passwordField.getText();
        // Validate email and password fields
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Failed", "Please enter both email and password.");
            return;
        }
        // Check if the credentials are correct
        if (cnUtil.validateLogin(email, password)) {
            // If credentials are valid, retrieve the userId from the database
            int userId = cnUtil.getUserIdFromEmail(email);
            // Store the userId and email in the UserSession
            UserSession.getInstance().login(userId, email);
            System.out.println(UserSession.getInstance().getUserId());
            // Load the main screen (db_interface_gui.fxml)
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/db_interface_gui.fxml"));
                Scene scene = new Scene(root, 900, 600);
                scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
                Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Show an error alert if login failed
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
        }
    }
    @FXML
    public void signUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/signUp.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Helper method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
