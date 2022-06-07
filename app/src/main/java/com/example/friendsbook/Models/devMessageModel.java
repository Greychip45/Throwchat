package com.example.friendsbook.Models;

public class devMessageModel {
    String message;
    String sender;

    public devMessageModel(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public devMessageModel(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
