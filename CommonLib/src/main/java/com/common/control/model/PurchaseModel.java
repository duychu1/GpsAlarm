package com.common.control.model;

public class PurchaseModel {
    private String productId;
    private String type;

    public PurchaseModel(String productId, String type) {
        this.productId = productId;
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public @interface ProductType {
        String INAPP = "inapp";
        String SUBS = "subs";
    }
}
