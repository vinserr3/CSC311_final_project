package service;

import java.util.prefs.Preferences;

public class UserSession {
    private static final UserSession instance = new UserSession();
    private int userId = -1;
    private String email;
    private final Preferences preferences;
    private UserSession() {
        preferences = Preferences.userRoot().node(this.getClass().getName());
    }
    public static UserSession getInstance() {
        return instance;
    }
    public synchronized void login(int userId, String email) {
        this.userId = userId;
        this.email = email;
        preferences.putInt("USER_ID", userId);
        preferences.put("EMAIL", email);
    }
    public synchronized int getUserId() {
        return userId;
    }
    public synchronized String getEmail() {
        return email;
    }
}
