package com.makgyber.vbuys.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Product {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String productName;
    private String description;
    private String tindahanName;
    private String tindahanId;
    private Double price;
    private Boolean publish;
    private List<String> tags;
    private String imageUri;
    private String category;
    private GeoPoint position;
    private ArrayList<String> imageList;

    public Product() {
        //need empty constructor
    }

    public Product(String productName,
                   String description,
                   String tindahanName,
                   String tindahanId,
                   Double price,
                   Boolean publish,
                   List<String> tags,
                   String imageUri,
                   String category,
                   GeoPoint position) {
        this.productName = productName;
        this.description = description;
        this.tindahanName = tindahanName;
        this.tindahanId = tindahanId;
        this.price = price;
        this.publish = publish;
        this.tags = tags;
        this.imageUri = imageUri;
        this.category = category;
        this.position = position;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public String getTindahanName() {
        return tindahanName;
    }

    public String getTindahanId() {
        return tindahanId;
    }

    public Boolean getPublish() {
        return publish;
    }

    public List<String> getTags() {
        return tags;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }
}
