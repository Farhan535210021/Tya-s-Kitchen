package com.example.foodapplication.model;

public class LoginResponse {
    private String message;
    private int user_id;
    private int roleid;  // Pastikan sesuai dengan respons dari API
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    // Constructor
    public LoginResponse(String message, int user_id, int roleid) {
        this.message = message;
        this.user_id = user_id;
        this.roleid = roleid;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
}
