package com.ngtszlong.eztrycloth.menu.detail;

public class Detail {
    String No;
    String Company;
    String Name_Chi;
    String Name_Eng;
    String Gender;
    String Type;
    String Color_Chi;
    String Color_Eng;
    int Thumbnail;
    String XXL;
    String XL;
    String L;
    String M;
    String S;
    String XS;
    String XXS;
    String Description_Chi;
    String Description_Eng;
    String Material_Chi;
    String Material_Eng;
    String Quantity;
    String Price;
    String Discount;

    public Detail() {
    }

    public Detail(String no, String company, String name_Chi, String name_Eng, String gender, String type, String color_Chi, String color_Eng, int thumbnail, String XXL, String XL, String l, String m, String s, String XS, String XXS, String description_Chi, String description_Eng, String material_Chi, String material_Eng, String quantity, String price, String discount) {
        No = no;
        Company = company;
        Name_Chi = name_Chi;
        Name_Eng = name_Eng;
        Gender = gender;
        Type = type;
        Color_Chi = color_Chi;
        Color_Eng = color_Eng;
        Thumbnail = thumbnail;
        this.XXL = XXL;
        this.XL = XL;
        L = l;
        M = m;
        S = s;
        this.XS = XS;
        this.XXS = XXS;
        Description_Chi = description_Chi;
        Description_Eng = description_Eng;
        Material_Chi = material_Chi;
        Material_Eng = material_Eng;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getName_Chi() {
        return Name_Chi;
    }

    public void setName_Chi(String name_Chi) {
        Name_Chi = name_Chi;
    }

    public String getName_Eng() {
        return Name_Eng;
    }

    public void setName_Eng(String name_Eng) {
        Name_Eng = name_Eng;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getColor_Chi() {
        return Color_Chi;
    }

    public void setColor_Chi(String color_Chi) {
        Color_Chi = color_Chi;
    }

    public String getColor_Eng() {
        return Color_Eng;
    }

    public void setColor_Eng(String color_Eng) {
        Color_Eng = color_Eng;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getXXL() {
        return XXL;
    }

    public void setXXL(String XXL) {
        this.XXL = XXL;
    }

    public String getXL() {
        return XL;
    }

    public void setXL(String XL) {
        this.XL = XL;
    }

    public String getL() {
        return L;
    }

    public void setL(String l) {
        L = l;
    }

    public String getM() {
        return M;
    }

    public void setM(String m) {
        M = m;
    }

    public String getS() {
        return S;
    }

    public void setS(String s) {
        S = s;
    }

    public String getXS() {
        return XS;
    }

    public void setXS(String XS) {
        this.XS = XS;
    }

    public String getXXS() {
        return XXS;
    }

    public void setXXS(String XXS) {
        this.XXS = XXS;
    }

    public String getDescription_Chi() {
        return Description_Chi;
    }

    public void setDescription_Chi(String description_Chi) {
        Description_Chi = description_Chi;
    }

    public String getDescription_Eng() {
        return Description_Eng;
    }

    public void setDescription_Eng(String description_Eng) {
        Description_Eng = description_Eng;
    }

    public String getMaterial_Chi() {
        return Material_Chi;
    }

    public void setMaterial_Chi(String material_Chi) {
        Material_Chi = material_Chi;
    }

    public String getMaterial_Eng() {
        return Material_Eng;
    }

    public void setMaterial_Eng(String material_Eng) {
        Material_Eng = material_Eng;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
