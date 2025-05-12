package com.example.appdocbao.Model;


public class Message {
    private String text;
    private boolean isUser; // true nếu là tin nhắn của người dùng, false nếu là của chatbot

    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }
}