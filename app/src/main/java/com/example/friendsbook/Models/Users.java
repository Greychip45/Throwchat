package com.example.friendsbook.Models;

public class Users {
    String profileUrl, name, lastMessage, userId, email, phone,chatId,status;



    public Users(String profileUrl, String name, String lastMessage, String userId, String email, String phone,String chatId,String status) {
        this.profileUrl = profileUrl;
        this.name = name;
        this.lastMessage = lastMessage;
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.chatId = chatId;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }


    public void setUsername(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



}


