package com.example.collegeproject.Model;

public class UserProfile {
    UserProfile(){

    }

    public UserProfile(String name, String uri, String bio, String dob, String userId) {
        this.name = name;
        this.uri = uri;
        this.bio = bio;
        this.dob = dob;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String name, uri,  bio, dob, userId;

}
