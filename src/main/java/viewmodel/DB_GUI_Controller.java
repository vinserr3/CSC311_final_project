package viewmodel;

import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;
import service.UserSession;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

public class DB_GUI_Controller implements Initializable {

    @FXML
    TextField first_name, last_name, department, email, imageURL;
    @FXML
    ComboBox<Major> major;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id, tv_user_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    @FXML
    private Button addBtn;
    @FXML
    private ProgressBar statusProgressBar;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private MenuItem editItem;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
            //populate combo-box
            major.setItems(FXCollections.observableArrayList(Major.values()));
            // hide progressbar on start
            statusProgressBar.setVisible(false);
            tv_user_id.setVisible(false);
            buttonHandler();
            addListeners();
            validateForm();
            clearForm();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void addNewRecord() {
        Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                major.getValue().toString(), email.getText(), imageURL.getText(), UserSession.getInstance().getUserId());
        cnUtil.insertUser(p);
        cnUtil.retrieveId(p);
        p.setId(cnUtil.retrieveId(p));
        data.add(p);
        showSuccessMessage();
        clearForm();
    }
    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        email.setText("");
        imageURL.setText("");
        major.setValue(null);
        tv.getSelectionModel().clearSelection();
        img_view.setImage(null);
    }
    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void closeApplication() {
        System.exit(0);
    }
    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }
        int index = data.indexOf(p);
        int userId = UserSession.getInstance().getUserId();
        Major selectedMajor = major.getValue();
        Person p2 = new Person(p.getId(), first_name.getText(), last_name.getText(), department.getText(),
                selectedMajor.toString(), email.getText(), imageURL.getText(), userId);
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
        showSuccessMessage();
        clearForm();
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
        showSuccessMessage();
    }
    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }
    @FXML
    protected void addRecord() {
        showSomeone();
    }
    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        major.setValue(Major.valueOf(p.getMajor()));
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
        if (!p.getImageURL().isEmpty()) {
            try {
                Image image = new Image(p.getImageURL());
                img_view.setImage(image);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid Image URL");
                img_view.setImage(null);
            }
        } else {
            img_view.setImage(null);
        }
    }
    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }
    public enum Major {
        CS, CPIS, ENG, BSME  // Define the available majors
    }
    private static class Results {
        String fname;
        String lname;
        Major major;
        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }
    private void validateForm() {
        boolean formValid = true;
        if (first_name.getText().isEmpty() || !first_name.getText().matches("^[A-Za-z]+$")) {
            formValid = false;
        } else if (last_name.getText().isEmpty() || !last_name.getText().matches("^[A-Za-z]+$")) {
            formValid = false;
        } else if (department.getText().isEmpty() || !department.getText().matches("^[A-Za-z ]+$")) {
            formValid = false;
        } else if (email.getText().isEmpty() || !email.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            formValid = false;
        } else if (!imageURL.getText().isEmpty() && !imageURL.getText().matches("^https?://[\\S]+$")) {
            formValid = false;
        } else if (major.getValue() == null) {
            formValid = false;
        }
        addBtn.setDisable(!formValid);
        editBtn.setDisable(!formValid);
        editItem.setDisable(!formValid);
    }
    private void buttonHandler() {
        tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            boolean recordSelected = (newValue != null);
            editBtn.setDisable(!recordSelected);
            deleteBtn.setDisable(!recordSelected);
            editItem.setDisable(!recordSelected);
        });
    }
    private void addListeners(){
        first_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        last_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        department.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        email.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        imageURL.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        major.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }
    private void showSuccessMessage() {
        statusProgressBar.setVisible(true);
        statusProgressBar.setProgress(0);
        new Thread(() -> {
            try {
                statusProgressBar.setProgress(1.0);
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    statusProgressBar.setVisible(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Data has been successfully edited!");
                    alert.showAndWait();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @FXML
    protected void importCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import CSV File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showOpenDialog(img_view.getScene().getWindow());

        if (file != null) {
            try (Scanner sc = new Scanner(file)) {
                sc.nextLine();
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (!line.isEmpty()) {
                        String[] parts = line.split(",");
                        String firstName = parts[1].trim();
                        String lastName = parts[2].trim();
                        String department = parts[3].trim();
                        String major = parts[4].trim();
                        String email = parts[5].trim();
                        String imgURL = parts[6].trim();
                        Person person = new Person(firstName, lastName, department, major, email, imgURL, UserSession.getInstance().getUserId());
                        // Check if the person already exists in the database based on email and userID
                        if (!cnUtil.doesUserExist(person.getEmail(), UserSession.getInstance().getUserId())) {
                            // Proceed with adding the user if no duplicate email is found
                            cnUtil.insertUser(person);
                            data.add(person);
                        } else {
                            // Show a warning if a duplicate email is found
                            showAlert(Alert.AlertType.WARNING, "Skipped Entry", "The email " + email + " is already registered");
                        }
                    }
                }
                tv.refresh();  // Refresh the TableView to display the newly added records
                showAlert(Alert.AlertType.INFORMATION, "Import Successful", "CSV file imported successfully.");
            } catch (FileNotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "CSV file not found.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error importing CSV: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "No file was selected for import.");
        }
    }
    @FXML
    protected void exportCSV() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export CSV File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fc.showSaveDialog(null);
        if (selectedFile != null) {
            try (FileWriter fw = new FileWriter(selectedFile)) {
                fw.write("First Name,Last Name,Department,Major,Email,Image URL\n");
                Set<String> seenEmails = new HashSet<>();
                cnUtil.getData().clear();
                for (Person person : cnUtil.getData()) {
                    if (!seenEmails.contains(person.getEmail())) {
                        seenEmails.add(person.getEmail());
                        fw.write(String.join(",",
                                person.getFirstName(),
                                person.getLastName(),
                                person.getDepartment(),
                                person.getMajor(),
                                person.getEmail(),
                                person.getImageURL()
                        ) + "\n");
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Data exported to CSV successfully.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error exporting CSV: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "No file was selected for export.");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> {
        });
    }
}