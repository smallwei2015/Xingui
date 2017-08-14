package com.blue.xingui.bean;

import java.io.Serializable;

/**
 * Created by cj on 2017/7/11.
 */

public class BarrelInfo implements Serializable {

    private int id;
    private int state;
    private String number;
    private String hotel_name;
    private String hotel_address;
    private String hotel_phone;
    private double capacity;
    private double total_capacity;
    private String bartender_name;
    private String bartender_phone;




    public double getTotal_capacity() {
        return total_capacity;
    }

    public void setTotal_capacity(double total_capacity) {
        this.total_capacity = total_capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }

    public String getHotel_address() {
        return hotel_address;
    }

    public void setHotel_address(String hotel_address) {
        this.hotel_address = hotel_address;
    }

    public String getHotel_phone() {
        return hotel_phone;
    }

    public void setHotel_phone(String hotel_phone) {
        this.hotel_phone = hotel_phone;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public String getBartender_name() {
        return bartender_name;
    }

    public void setBartender_name(String bartender_name) {
        this.bartender_name = bartender_name;
    }

    public String getBartender_phone() {
        return bartender_phone;
    }

    public void setBartender_phone(String bartender_phone) {
        this.bartender_phone = bartender_phone;
    }
}
