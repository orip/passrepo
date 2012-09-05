package com.example.passrepo.model;

public class PasswordEntry {
    public transient String id; // only used in the app
    public String title;
    public String userName;
    public String password;

    public PasswordEntry(String id, String title, String userName, String password) {
        this.id = id;
        this.title = title;
        this.userName = userName;
        this.password = password;
    }

    // Used by ArrayAdapter in the list fragment
    @Override
    public String toString() {
        return this.title;
    }
}
