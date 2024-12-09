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
import java.util.*;

public class DB_GUI_Controller implements Initializable {

    @FXML
    TextField first_name, last_name, address, email, imageURL;
    @FXML
    ComboBox<Role> role;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id, tv_user_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_address, tv_role, tv_email;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private MenuItem editItem;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();
    Image defaultImage = new Image(getClass().getResource("/images/profile.png").toExternalForm());


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_address.setCellValueFactory(new PropertyValueFactory<>("address"));
            tv_role.setCellValueFactory(new PropertyValueFactory<>("role"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
            //populate combo-box
            role.setItems(FXCollections.observableArrayList(Role.values()));
            tv_user_id.setVisible(false);
            buttonHandler();
            addListeners();
            validateForm();
            clearForm();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // Will add a new employee
    @FXML
    protected void addNewEmployee() {
        String newEmail = email.getText().trim();
        boolean emailExists = data.stream()
                .anyMatch(person -> person.getEmail().equalsIgnoreCase(newEmail));
        if (emailExists) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Email", "An employee with this email already exists.");
            return;
        }
        Person p = new Person(
                first_name.getText().trim(),
                last_name.getText().trim(),
                address.getText().trim(),
                role.getValue().toString().trim(),
                newEmail,
                imageURL.getText().trim(),
                UserSession.getInstance().getUserId()
        );
        cnUtil.insertUser(p);
        p.setId(cnUtil.retrieveId(p));
        data.add(p);
        showSuccessMessage();
        clearForm();
    }
    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        address.setText("");
        email.setText("");
        imageURL.setText("");
        role.setValue(null);
        tv.getSelectionModel().clearSelection();
        Image defaultImage = new Image(getClass().getResource("/images/profile.png").toExternalForm());
        img_view.setImage(defaultImage);
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
        Role selectedRole = role.getValue();
        Person p2 = new Person(p.getId(), first_name.getText(), last_name.getText(), address.getText(),
                selectedRole.toString(), email.getText(), imageURL.getText(), userId);
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
        if(p != null) {
            int index = data.indexOf(p);
            cnUtil.deleteRecord(p);
            data.remove(index);
            tv.getSelectionModel().select(index);
            showSuccessMessage();
        }
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
        address.setText(p.getAddress());
        role.setValue(Role.valueOf(p.getRole()));
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
        if (!p.getImageURL().isEmpty()) {
            try {
                Image image = new Image(p.getImageURL());
                img_view.setImage(image);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid Image URL");
                img_view.setImage(defaultImage);
            }
        } else {
            img_view.setImage(defaultImage);
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
        ObservableList<Role> options =
                FXCollections.observableArrayList(Role.values());
        ComboBox<Role> comboBox = new ComboBox<>(options);
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
                    results.fname + " " + results.lname + " " + results.role);
        });
    }
    public enum Role {
        BAKER, MIXER, BALLER, CASHIER, MANAGER
    }
    private static class Results {
        String fname;
        String lname;
        Role role;
        public Results(String name, String date, Role venue) {
            this.fname = name;
            this.lname = date;
            this.role = venue;
        }
    }
    private void validateForm() {
        boolean formValid =
                first_name.getText().matches("^[A-Za-z]+$") &&
                        last_name.getText().matches("^[A-Za-z]+$") &&
                        address.getText().matches("^\\d+\\s[A-Za-z0-9\\s,.'-]+(\\s[A-Za-z0-9\\s,.'-]+)*$") &&
                        email.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") &&
                        (imageURL.getText().isEmpty() || imageURL.getText().matches("^(https?://)(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/[\\S]*)?$")) &&
                        role.getValue() != null;

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
        address.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        email.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        imageURL.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        role.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }
    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Data has been successfully edited!");
        alert.showAndWait();
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
                        String address = parts[3].trim();
                        String role = parts[4].trim();
                        String email = parts[5].trim();
                        String imgURL = parts[6].trim();
                        Person person = new Person(firstName, lastName, address, role, email, imgURL, UserSession.getInstance().getUserId());
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
                tv.refresh();
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
                fw.write("First Name,Last Name,Address,Role,Email,Image URL\n");
                Set<String> seenEmails = new HashSet<>();
                cnUtil.getData().clear();
                for (Person person : cnUtil.getData()) {
                    if (!seenEmails.contains(person.getEmail())) {
                        seenEmails.add(person.getEmail());
                        fw.write(String.join(",",
                                person.getFirstName(),
                                person.getLastName(),
                                person.getAddress(),
                                person.getRole(),
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