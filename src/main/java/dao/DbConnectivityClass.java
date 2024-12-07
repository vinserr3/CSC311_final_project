package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Person;
import service.MyLogger;
import service.UserSession;

import java.sql.*;
public class DbConnectivityClass {
    final static String DB_NAME = "serrcsc311server";
    MyLogger lg = new MyLogger();
    final static String SQL_SERVER_URL = "jdbc:mysql://serrcsc311server.mysql.database.azure.com/";//update this server name
    final static String DB_URL = "jdbc:mysql://serrcsc311server.mysql.database.azure.com/" + DB_NAME;//update this database name
    final static String USERNAME = "serranoadmin";// update this username
    final static String PASSWORD = "password123!";// update this password
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    public ObservableList<Person> getData() {
        connectToDatabase();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, UserSession.getInstance().getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                lg.makeLog("No data");
            }
            data.clear();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String department = resultSet.getString("department");
                String major = resultSet.getString("major");
                String email = resultSet.getString("email");
                String imageURL = resultSet.getString("imageURL");
                int user_id = resultSet.getInt("user_id");
                data.add(new Person(id, first_name, last_name, department, major, email, imageURL, user_id));
            }
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Create Database If Not Exists
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            conn.close();
            // Connect to the Database
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            // Create Users Table for users table data
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "first_name VARCHAR(200) NOT NULL, " +
                    "last_name VARCHAR(200) NOT NULL, " +
                    "department VARCHAR(200), " +
                    "major VARCHAR(200), " +
                    "email VARCHAR(200) NOT NULL, " +
                    "imageURL VARCHAR(200), " +
                    "user_id INT, " +
                    "FOREIGN KEY (user_id) REFERENCES user_data(user_id))"; // Referencing user_id in user_data
            statement.executeUpdate(sql);
            // Create User Data Table for Login Credentials
            String createUserDataTable = """
                    CREATE TABLE IF NOT EXISTS user_data (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(200) NOT NULL UNIQUE,
                        password VARCHAR(200) NOT NULL
                    );
                    """;
            statement.executeUpdate(createUserDataTable);
            // Check If Users Exist
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                hasRegisteredUsers = numUsers > 0;
            }
            statement.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasRegisteredUsers;
    }

    public void queryUserByLastName(String name) {
        connectToDatabase();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users WHERE last_name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String major = resultSet.getString("major");
                String department = resultSet.getString("department");

                lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                        + ", Major: " + major + ", Department: " + department);
            }
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listAllUsers() {
        connectToDatabase();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String department = resultSet.getString("department");
                String major = resultSet.getString("major");
                String email = resultSet.getString("email");

                lg.makeLog("ID: " + id + ", Name: " + first_name + " " + last_name + " "
                        + ", Department: " + department + ", Major: " + major + ", Email: " + email);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(Person person) {
        connectToDatabase();
        try {
            System.out.println(person);
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO users (first_name, last_name, department, major, email, imageURL, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getFirstName());
            preparedStatement.setString(2, person.getLastName());
            preparedStatement.setString(3, person.getDepartment());
            preparedStatement.setString(4, person.getMajor());
            preparedStatement.setString(5, person.getEmail());
            preparedStatement.setString(6, person.getImageURL());
            preparedStatement.setInt(7, person.getUserID());
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                lg.makeLog("A new user was inserted successfully.");
            }
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editUser(int id, Person p) {
        connectToDatabase();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "UPDATE users SET first_name=?, last_name=?, department=?, major=?, email=?, imageURL=? WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, p.getFirstName());
            preparedStatement.setString(2, p.getLastName());
            preparedStatement.setString(3, p.getDepartment());
            preparedStatement.setString(4, p.getMajor());
            preparedStatement.setString(5, p.getEmail());
            preparedStatement.setString(6, p.getImageURL());
            preparedStatement.setInt(7, p.getUserID());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRecord(Person person) {
        int id = person.getId();
        connectToDatabase();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM users WHERE id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Method to retrieve id from database where it is auto-incremented.
    public int retrieveId(Person p) {
        connectToDatabase();
        int id;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT id FROM users WHERE email=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, p.getEmail());

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            id = resultSet.getInt("id");
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        lg.makeLog(String.valueOf(id));
        return id;
    }

    // Helper method that checks if the user exists
    public boolean doesUserExist(String email, int userId) {
        // Check if the email exists for a different user (exclude current user_id)
        String query = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);  // Check the email
            ps.setInt(2, userId);     // Exclude the current user_id
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // Email exists for another user
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Email does not exist for another user
    }
    // Insert new user into user_data table
    public boolean insertNewUser(String email, String password) {
        String query = "INSERT INTO user_data (email, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);
            return ps.executeUpdate() > 0;  // Return true if insert succeeds
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Insert failed
    }
    public int getUserIdFromEmail(String email) {
        int userId = -1;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT user_id FROM user_data WHERE email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
    public boolean validateLogin(String email, String password) {
        boolean isValid = false;
        String sql = "SELECT * FROM user_data WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                isValid = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }
    public boolean doesUserExistForCurrentUser(String email, int userId) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // User exists for the current user_id
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // User doesn't exist or exists with a different user_id
    }


}