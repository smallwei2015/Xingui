package com.blue.xingui.bean;

import com.blue.xingui.R;

/**
 * Created by cj on 2017/6/23.
 */

public class Barrel {

    public static int iconSrc= R.mipmap.hotel_icon;
    private double longitude;
    private double latitude;

    private String des;
    private String title;
    private double capacity;


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
}
