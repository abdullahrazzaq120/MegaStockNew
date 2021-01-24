package com.sortscript.megastock.Model;

public class ProductsModel {

    String productImage, productTitle, productPacking, productDescription, productUnit, productCostPrice,
            productSalePrice, productCompanyBrand, productStock, productCategory, productId;

    public ProductsModel() {
    }

    public ProductsModel(String productImage, String productTitle, String productPacking, String productDescription,
                         String productUnit, String productCostPrice, String productSalePrice, String productCompanyBrand,
                         String productStock, String productCategory, String productId) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPacking = productPacking;
        this.productDescription = productDescription;
        this.productUnit = productUnit;
        this.productCostPrice = productCostPrice;
        this.productSalePrice = productSalePrice;
        this.productCompanyBrand = productCompanyBrand;
        this.productStock = productStock;
        this.productCategory = productCategory;
        this.productId = productId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPacking() {
        return productPacking;
    }

    public void setProductPacking(String productPacking) {
        this.productPacking = productPacking;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductCostPrice() {
        return productCostPrice;
    }

    public void setProductCostPrice(String productCostPrice) {
        this.productCostPrice = productCostPrice;
    }

    public String getProductSalePrice() {
        return productSalePrice;
    }

    public void setProductSalePrice(String productSalePrice) {
        this.productSalePrice = productSalePrice;
    }

    public String getProductCompanyBrand() {
        return productCompanyBrand;
    }

    public void setProductCompanyBrand(String productCompanyBrand) {
        this.productCompanyBrand = productCompanyBrand;
    }

    public String getProductStock() {
        return productStock;
    }

    public void setProductStock(String productStock) {
        this.productStock = productStock;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
