package org.example;

public class User {
    private final String username;
    private final String password;
    private final String password_complexity;
    private final String accessLevel;

    public User(String username, String password,String password_complexity,String accessLevel) {
        this.username = username;
        this.password = password;
        this.password_complexity = password_complexity;
        this.accessLevel = accessLevel;
    }

    public String getUsername() {
        return username;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public String getPassword() {
        return password;
    }

    public String getPassword_complexity() {
        return password_complexity;
    }
}

