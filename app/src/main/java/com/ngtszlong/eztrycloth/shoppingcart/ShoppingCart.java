package com.ngtszlong.eztrycloth.shoppingcart;

public class ShoppingCart {
    private String uid;
    private String no;
    private String image;
    private String gender;
    private String name;
    private String color;
    private String price;
    private String discount;

    public ShoppingCart(String uid, String no, String image, String gender, String name, String color, String price, String discount) {
        this.uid = uid;
        this.no = no;
        this.image = image;
        this.gender = gender;
        this.name = name;
        this.color = color;
        this.price = price;
        this.discount = discount;
    }

    public ShoppingCart() {
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
}
