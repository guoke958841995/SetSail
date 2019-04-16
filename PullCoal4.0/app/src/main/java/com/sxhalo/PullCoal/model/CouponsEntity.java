package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * Created by liz on 2018/1/6.
 */

public class CouponsEntity implements Serializable{

    /**
     * usingRange : 煤炭,货运信息均可支付
     * couponName : 信息部券
     * couponType : 0
     * periodValidity : 2018.01.03-2018.01.03
     * couponPicUrl : http://172.16.99.199:8080/picservice/
     * denomination : 2000
     */
    private int usePage = 0; //当前用已使用的代金卷张数
    private int currentAmountUse = 0; //当前代金券已使用金额

    private String couponId;
    private String usingRange;
    private String couponName;
    private String number;  //数量
    private String couponType;
    private String periodValidity;
    private String couponPicUrl;
    private String denomination;  //面额
    private String restrictType;  // 0、不限；1、所有煤炭；2、所有货运；3、指定煤炭种类；4、指定货运路线；5、指定煤炭信息；6、指定货运信息；



    /**
     * title : 注册登录
     * status : 信息部券
     */

    private String title;
    private String status;   //状态：0未获得 1未领取，2已领取
    private String index;

    public String getRestrictType() {
        return restrictType;
    }

    public void setRestrictType(String restrictType) {
        this.restrictType = restrictType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getCurrentAmountUse() {
        return currentAmountUse;
    }

    public void setCurrentAmountUse(int currentAmountUse) {
        this.currentAmountUse = currentAmountUse;
    }

    public int getUsePage() {
        return usePage;
    }

    public void setUsePage(int usePage) {
        this.usePage = usePage;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUsingRange() {
        return usingRange;
    }

    public void setUsingRange(String usingRange) {
        this.usingRange = usingRange;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getPeriodValidity() {
        return periodValidity;
    }

    public void setPeriodValidity(String periodValidity) {
        this.periodValidity = periodValidity;
    }

    public String getCouponPicUrl() {
        return couponPicUrl;
    }

    public void setCouponPicUrl(String couponPicUrl) {
        this.couponPicUrl = couponPicUrl;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
