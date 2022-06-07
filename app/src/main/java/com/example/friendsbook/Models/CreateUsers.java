package com.example.friendsbook.Models;

import com.example.friendsbook.Models.Users;

public class CreateUsers extends Users {

    String name,phone,chatId,email;

    public CreateUsers(String name, String phone, String chatId, String email) {


        this.email = email;
        this.chatId = chatId;
        this.name = name;
        this.phone = phone;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatUserId) {
        this.chatId = chatUserId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
