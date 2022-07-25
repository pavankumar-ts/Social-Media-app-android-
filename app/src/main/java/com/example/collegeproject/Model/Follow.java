package com.example.collegeproject.Model;

public class Follow {
    String follower, following;
    public Follow() {
    }

    public Follow(String follower, String following) {
        this.follower = follower;
        this.following = following;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }
}
