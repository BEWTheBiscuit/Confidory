package com.bisbizkuit.whistalk.objects;

public class User {

    public String username, bio, email, icon, password, first_name, last_name, id;

    public User() {
    }

    public User(String username, String bio, String email, String icon, String password, String first_name, String last_name, String id) {
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.icon = icon;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return first_name;
    }

    public void setFirstname(String first_name) {
        this.first_name = first_name;
    }

    public String getLastname() {
        return last_name;
    }

    public void setLastname(String last_name) {
        this.last_name = last_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", bio='" + bio + '\'' +
                ", email='" + email + '\'' +
                ", icon='" + icon + '\'' +
                ", password='" + password + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
