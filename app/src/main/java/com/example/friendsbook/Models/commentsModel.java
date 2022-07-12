package com.example.friendsbook.Models;

public class commentsModel {
    private String comment,date,time,userId,postId;

    public commentsModel(String comment, String date, String time, String userId, String postId) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.userId = userId;
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }
    public commentsModel(){}

}
