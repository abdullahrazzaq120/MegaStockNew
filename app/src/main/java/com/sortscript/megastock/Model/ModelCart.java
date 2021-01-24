package com.sortscript.megastock.Model;

public class ModelCart {

    String cartName, cartCategory, cartPId, cartImage, cartPrice, cartUnit, cartStockQuantity, cartNodeKey;

    public ModelCart() {
    }

    public ModelCart(String cartName, String cartCategory, String cartPId, String cartImage, String cartPrice,
                     String cartUnit, String cartStockQuantity, String cartNodeKey) {
        this.cartName = cartName;
        this.cartCategory = cartCategory;
        this.cartPId = cartPId;
        this.cartImage = cartImage;
        this.cartPrice = cartPrice;
        this.cartUnit = cartUnit;
        this.cartStockQuantity = cartStockQuantity;
        this.cartNodeKey = cartNodeKey;
    }

    public String getCartImage() {
        return cartImage;
    }

    public void setCartImage(String cartImage) {
        this.cartImage = cartImage;
    }

    public String getCartUnit() {
        return cartUnit;
    }

    public void setCartUnit(String cartUnit) {
        this.cartUnit = cartUnit;
    }

    public String getCartStockQuantity() {
        return cartStockQuantity;
    }

    public void setCartStockQuantity(String cartStockQuantity) {
        this.cartStockQuantity = cartStockQuantity;
    }

    public String getCartNodeKey() {
        return cartNodeKey;
    }

    public void setCartNodeKey(String cartNodeKey) {
        this.cartNodeKey = cartNodeKey;
    }

    public String getCartName() {
        return cartName;
    }

    public void setCartName(String cartName) {
        this.cartName = cartName;
    }

    public String getCartCategory() {
        return cartCategory;
    }

    public void setCartCategory(String cartCategory) {
        this.cartCategory = cartCategory;
    }

    public String getCartPId() {
        return cartPId;
    }

    public void setCartPId(String cartPId) {
        this.cartPId = cartPId;
    }

    public String getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(String cartPrice) {
        this.cartPrice = cartPrice;
    }
}
