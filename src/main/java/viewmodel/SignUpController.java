package viewmodel;

import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.UserSession;

public class SignUpController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    @FXML
    protected void createNewAccount(ActionEvent actionEvent) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please fill in all fields.");
            return;
        }
        if (cnUtil.emailCheck(email)) {
            showAlert(Alert.AlertType.ERROR, "Account Exists", "An account with this email already exists.");
            return;
        }
        if (cnUtil.insertNewUser(email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Account Created", "Your account has been created successfully!");
            goBack(actionEvent);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account. Please try again.");
        }
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Helper method for alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
