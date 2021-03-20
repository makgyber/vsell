package com.makgyber.vsells.models;

public class InvoiceItem {
    private String productId;
    private String productName;
    private Number quantity;
    private Number unitCost;
    private Number amount;

    public InvoiceItem() {}

    public InvoiceItem(String productId, String productName, Number quantity, Number unitCost, Number amount) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Number getQuantity() {
        return quantity;
    }

    public Number getUnitCost() {
        return unitCost;
    }

    public Number getAmount() {
        return amount;
    }
}
