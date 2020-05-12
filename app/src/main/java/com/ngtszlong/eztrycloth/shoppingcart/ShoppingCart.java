package com.ngtszlong.eztrycloth.shoppingcart;

public class ShoppingCart {
    private String companyuid;
    private String uid;
    private String no;
    private String image;
    private String gender;
    private String name;
    private String quantity;
    private String color;
    private String price;
    private String discount;
    private String tryimage;
    private String str;

    public ShoppingCart() {
    }

    public ShoppingCart(String companyuid, String uid, String no, String image, String gender, String name, String quantity, String color, String price, String discount, String tryimage, String str) {
        this.companyuid = companyuid;
        this.uid = uid;
        this.no = no;
        this.image = image;
        this.gender = gender;
        this.name = name;
        this.quantity = quantity;
        this.color = color;
        this.price = price;
        this.discount = discount;
        this.tryimage = tryimage;
        this.str = str;
    }

    public String getCompanyuid() {
        return companyuid;
    }

    public void setCompanyuid(String companyuid) {
        this.companyuid = companyuid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTryimage() {
        return tryimage;
    }

    public void setTryimage(String tryimage) {
        this.tryimage = tryimage;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
