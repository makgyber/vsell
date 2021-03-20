package com.makgyber.vbuys.models;

import com.google.firebase.Timestamp;

public class Chat {
    private String id;
    private String topic;
    private String buyerId;
    private String buyerName;
    private String buyerImage;
    private String storeId;
    private String storeName;
    private String storeImage;
    private Timestamp dateCreated;
    private Timestamp lastMessageCreated;
    private boolean sellerSeen;
    private boolean buyerSeen;

    public Chat(){}

    public Chat(String id, String topic, String buyerId, String buyerName, String buyerImage, String sellerId, String sellerName, String sellerImage, Timestamp dateCreated) {
        this.topic = topic;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerImage = buyerImage;
        this.storeId = sellerId;
        this.storeName = sellerName;
        this.storeImage = sellerImage;
        this.dateCreated = dateCreated;
        this.lastMessageCreated = dateCreated;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerImage() {
        return buyerImage;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public Timestamp getLastMessageCreated() {
        return lastMessageCreated;
    }

    public void setLastMessageCreated(Timestamp lastMessageCreated) {
        this.lastMessageCreated = lastMessageCreated;
    }

    public boolean isSellerSeen() {
        return sellerSeen;
    }

    public void setSellerSeen(boolean sellerSeen) {
        this.sellerSeen = sellerSeen;
    }

    public boolean isBuyerSeen() {
        return buyerSeen;
    }

    public void setBuyerSeen(boolean buyerSeen) {
        this.buyerSeen = buyerSeen;
    }
}

