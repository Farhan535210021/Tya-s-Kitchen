package com.example.foodapplication.model;

public class User {
    private String username;
    private String password;
    private String nomor_hp;  // Ganti dari email ke nomor_hp
    private String alamat;
    private int roleID;

    // Constructor
    public User(String username, String password, String nomor_hp, String alamat, int roleID) {
        this.username = username;
        this.password = password;
        this.nomor_hp = nomor_hp;
        this.alamat = alamat;
        this.roleID = roleID;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNomorHp() {  // Ganti dari getEmail ke getNomorHp
        return nomor_hp;
    }

    public void setNomorHp(String nomor_hp) {  // Ganti dari setEmail ke setNomorHp
        this.nomor_hp = nomor_hp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }
}
