package com.makgyber.vsells.models;


import com.google.firebase.Timestamp;

public class Message {
    private String chatId;
    private Timestamp dateCreated;
    private String senderType;
    private String content;
    private String imageUri;


    public Message() {}

    public Message(String chatId, Timestamp dateCreated, String senderType, String content, String imageUri) {
        this.chatId = chatId;
        this.dateCreated = dateCreated;
        this.senderType = senderType;
        this.content = content;
        this.imageUri = imageUri;
    }


    public String getContent() {
        return content;
    }

    public String getSenderType() {
        return senderType;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public String getChatId() {
        return chatId;
    }

    public String getImageUri() {
        return imageUri;
    }
}
