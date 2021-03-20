package com.makgyber.vbuys.models;


import com.google.firebase.Timestamp;

import java.util.List;

public class Invoice {
    private String id;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private Timestamp deliverByDate;
    private Timestamp dateCompleted;
    private Timestamp dateCreated;
    private Timestamp lastUpdated;
    private String status;
    private Number totalAmount;
    private List<InvoiceItem> items;
    private String delivery;
    private String paymentMethod;

    public Invoice() {}

    public Invoice(String buyerId, String buyerName, String sellerId, String sellerName,
                   Timestamp deliverByDate, String status, Number totalAmount,
                   List<InvoiceItem> items, String delivery, String paymentMethod) {
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.deliverByDate = deliverByDate;
        this.dateCreated = Timestamp.now();
        this.lastUpdated = Timestamp.now();
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
        this.delivery = delivery;
        this.paymentMethod = paymentMethod;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Timestamp getDeliverByDate() {
        return deliverByDate;
    }

    public Timestamp getDateCompleted() {
        return dateCompleted;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public Number getTotalAmount() {
        return totalAmount;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }



}
