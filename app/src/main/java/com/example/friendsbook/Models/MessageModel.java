package com.example.friendsbook.Models;

public class MessageModel {

    String uId,message,sender,receiver;
    Long timestamp;
    boolean isseen;
    String imgUrl;

    public MessageModel(String uId, String message, Long timestamp,boolean isseen,String imgUrl) {

        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
        this.isseen = isseen;
        this.imgUrl = imgUrl;
    }

    public MessageModel(String uId, String message,String sender,String receiver,String imgUrl) {
        this.uId = uId;
        this.message = message;
        this.sender = sender;
        this.receiver= receiver;
        this.imgUrl = imgUrl;

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSender() {
        return sender;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public MessageModel () {

    }
}
