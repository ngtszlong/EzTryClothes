package com.ngtszlong.eztrycloth.Profile;

public class Profile {
    private String email;
    private String uid;
    private String name;
    private String gender;
    private String age;
    private String height;
    private String birth;
    private String address;
    private String phone;
    private String front;
    private String side;
    private String weight;

    public Profile() {
    }

    public Profile(String email, String uid, String name, String gender, String age, String height, String birth, String address, String phone, String front, String side, String weight) {
        this.email = email;
        this.uid = uid;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.birth = birth;
        this.address = address;
        this.phone = phone;
        this.front = front;
        this.side = side;
        this.weight = weight;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
