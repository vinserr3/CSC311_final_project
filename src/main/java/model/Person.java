package model;

public class Person {
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String role;
    private String email;
    private String imageURL;
    private int userID;

    public Person() {
    }
    public Person(String firstName, String lastName, String Address, String role, String email, String imageURL, int userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = Address;
        this.role = role;
        this.email = email;
        this.imageURL = imageURL;
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Person(Integer id, String firstName, String lastName, String Address, String role, String email, String imageURL, int userID) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = Address;
        this.role = role;
        this.email = email;
        this.imageURL = imageURL;
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}