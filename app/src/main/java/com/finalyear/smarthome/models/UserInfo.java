package com.finalyear.smarthome.models;

public class UserInfo {
    private String Name;
    private String Username;
    private String email;
    private String phoneNumber;
    private String password;

    public UserInfo() {
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        this.Name = name;
    }

    public String getUsername() {
        return Username;
    }
    public void setUsername(String username) {
        this.Username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phonenumber) {
        this.phoneNumber = phonenumber;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
