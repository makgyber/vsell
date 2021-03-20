package com.makgyber.vbuys.models;

import com.google.firebase.firestore.GeoPoint;

public class Tindahan {
    private String tindahanName;
    private String owner;
    private String contactInfo;
    private String address;
    private String paymentOptions;
    private String deliveryOptions;
    private Boolean publish;
    private String id;
    private GeoPoint position;
    private String imageUri;

    public Tindahan() {
        //need empty constructor
    }

    public Tindahan(String tindahanName, String owner, String contactInfo, String address, String paymentOptions, String deliveryOptions, Boolean publish, GeoPoint position, String imageUri) {
        this.tindahanName = tindahanName;
        this.owner = owner;
        this.contactInfo = contactInfo;
        this.address = address;
        this.paymentOptions = paymentOptions;
        this.deliveryOptions = deliveryOptions;
        this.publish = publish;
        this.position = position;
        this.imageUri = imageUri;
    }

    public String getTindahanName() {
        return tindahanName;
    }

    public String getOwner() {
        return owner;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getPublish() {
        return publish;
    }

    public String getPaymentOptions() {
        return paymentOptions;
    }

    public String getDeliveryOptions() {
        return deliveryOptions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public String getImageUri() {
        return imageUri;
    }
}
