package com.example.collegeproject.Model;

public class Post {
    Post(){

    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getDpUrl() {
        return dpUrl;
    }

    public void setDpUrl(String dpUrl) {
        this.dpUrl = dpUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Post(String descriptionText, String dpUrl, String imageUrl, String videoUrl, String likes, String location, String name, String postId, String timeStamp, String userId) {
        this.descriptionText = descriptionText;
        this.dpUrl = dpUrl;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.likes = likes;
        this.location = location;
        this.name = name;
        this.postId = postId;
        this.timeStamp = timeStamp;
        this.userId = userId;
    }

    String descriptionText, dpUrl, imageUrl, likes, location, name, postId, timeStamp, userId, videoUrl;

}
