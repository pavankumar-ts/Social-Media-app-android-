package com.example.collegeproject.Model;

import com.firebase.ui.database.FirebaseRecyclerOptions;

public class Comment {
    String postId, userId,commentsText, timeStamp;

    public Comment() {
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

    public String getCommentsText() {
        return commentsText;
    }

    public void setCommentsText(String commentsText) {
        this.commentsText = commentsText;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Comment(String postId, String userId, String commentsText, String timeStamp) {
        this.postId = postId;
        this.userId = userId;
        this.commentsText = commentsText;
        this.timeStamp = timeStamp;
    }
}


