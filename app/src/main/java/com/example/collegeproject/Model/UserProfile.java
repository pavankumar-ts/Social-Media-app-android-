package com.example.collegeproject.Model;

import com.google.firebase.firestore.auth.User;

public class UserProfile {
    UserProfile(){

    }

    public String name, uri,  bio;

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


    public UserProfile(String name, String uri, String DOB, String bio, String userId) {
        this.name = name;
        this.uri = uri;
        this.bio = bio;
    }
}
