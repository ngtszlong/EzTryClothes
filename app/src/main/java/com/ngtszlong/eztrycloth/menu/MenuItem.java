package com.ngtszlong.eztrycloth.menu;

public class MenuItem {
    private String name;
    private int Thumbnail;

    public MenuItem(String name, int thumbnail) {
        this.name = name;
        Thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
