package com.example.collegeproject.Model;

public class ModelSavedPost {
    String postId, userId, savedUserId;

    public ModelSavedPost() {
    }

    public ModelSavedPost(String postId, String userId, String savedUserId) {
        this.postId = postId;
        this.userId = userId;
        this.savedUserId = savedUserId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSavedUserId() {
        return savedUserId;
    }

    public void setSavedUserId(String savedUserId) {
        this.savedUserId = savedUserId;
    }
}
