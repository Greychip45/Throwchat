package com.example.friendsbook.Models;

public class postModel {
    private String posterId,postImage,comments,likes,post,key;
    private boolean isLiked;

    public postModel(String posterId, String postImage, String comments, String likes, String postText, String key, boolean isLiked) {
        this.posterId = posterId;
        this.postImage = postImage;
        this.comments = comments;
        this.likes = likes;
        this.post = postText;
        this.key = key;
        this.isLiked = isLiked;
    }

    public String getPosterId() {
        return posterId;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getComments() {
        return comments;
    }

    public String getLikes() {
        return likes;
    }

    public String getPostText() {
        return post;
    }

    public String getKey() {
        return key;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public postModel(){}
}
