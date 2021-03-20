package com.makgyber.vbuys.models;

import android.net.Uri;

public class User {
    private String email;
    private String phoneNumber;
    private String displayName;
    private String photoUrl;
    private String address;
    private String uid;

    public User(String email, String phoneNumber, String displayName, String photoUrl, String address, String uid) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.address = address;
        this.uid = uid;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
