package com.blue.xingui.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cj on 2017/7/5.
 */

public class Hotel implements Serializable{

    private long dataId;
    private String title;
    private String desc;
    private double lon;
    private double lat;
    private String location;
    private List<String> manyPic;
    private double distance;
    private double picsize;
    private double height;
    private double weight;


    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getManyPic() {
        return manyPic;
    }

    public void setManyPic(List<String> manyPic) {
        this.manyPic = manyPic;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPicsize() {
        return picsize;
    }

    public void setPicsize(double picsize) {
        this.picsize = picsize;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
