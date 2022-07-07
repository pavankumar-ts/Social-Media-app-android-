package com.example.collegeproject.Model;

public class ModelComment {
    String postId, userId, timeStamp;

    public ModelComment() {

    }

    public ModelComment(String postId, String userId, String timeStamp) {
        this.postId = postId;
        this.userId = userId;
        this.timeStamp = timeStamp;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
