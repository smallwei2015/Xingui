package com.blue.xingui.bean;

import java.io.Serializable;

/**
 * Created by cj on 2017/7/20.
 */

public class BarrelNetInfo implements Serializable {

    private int dataId;
    private String caskNum;
    private int capacityType;
    private String hotelName;
    private String location;
    private String hotelPhone;
    private double totalCapacity;
    private double currentCapacity;
    private String wineMaster;
    private String masterPhone;


    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getCaskNum() {
        return caskNum;
    }

    public void setCaskNum(String caskNum) {
        this.caskNum = caskNum;
    }

    public int getCapacityType() {
        return capacityType;
    }

    public void setCapacityType(int capacityType) {
        this.capacityType = capacityType;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHotelPhone() {
        return hotelPhone;
    }

    public void setHotelPhone(String hotelPhone) {
        this.hotelPhone = hotelPhone;
    }

    public double getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(double totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public String getWineMaster() {
        return wineMaster;
    }

    public void setWineMaster(String wineMaster) {
        this.wineMaster = wineMaster;
    }

    public String getMasterPhone() {
        return masterPhone;
    }

    public void setMasterPhone(String masterPhone) {
        this.masterPhone = masterPhone;
    }
}
