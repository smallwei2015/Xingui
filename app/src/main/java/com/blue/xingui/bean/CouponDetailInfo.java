package com.blue.xingui.bean;

import java.io.Serializable;

/**
 * Created by cj on 2017/8/11.
 */

public class CouponDetailInfo implements Serializable{

    private long couponId;
    private double coupon;
    private int state;
    private String deathDate;
    private double useCondition;
    private int type;
    private String redeemCode;

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public double getUseCondition() {
        return useCondition;
    }

    public void setUseCondition(double useCondition) {
        this.useCondition = useCondition;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }
}
