package com.example.collegeproject.Model;

public class ModelLikes {
    String postId, userId;

    ModelLikes(){

    }
    public ModelLikes(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
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


}
