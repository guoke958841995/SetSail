package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2018/1/11.
 */

public class PayMentOrder implements Serializable{


    /**
     * tradeNo : 80001706020000128100000592000101
     * consultingFeeType : 0
     * consultingFee : 100
     * costName : 【顺与信息部】【原煤】原煤资讯费
     * costType : 煤炭信息资讯费
     * payState : 2
     * wxPayEndTime : 2017-08-16 09:39:26
     * licenseMinute : 永久有效
     */

    private String tradeNo;
    private String consultingFeeType;
    private String consultingFee;
    private String costName;
    private String costType;
    private String payState;  //支付状态：0、待支付；1、进行中；2、支付成功；3、支付失败
    private String payEndTime;
    private String payMethord;  // 支付方式 coupon：优惠券支付，wechat：微信支付
    private String licenseMinute;

    private String couponMoney; //代金券抵用金额
    private String wxTotalFee;  //微信抵用金额
    private String accountMoney; // 余额抵用金额

    private String empowerAccessState; //授权状态 0 未授权 ，1已授权
    private String isCanComplaint; //是否可投诉 ：0、否；1、是
    private String isHaveComplaint; //是否有投诉：0、否；1、是

    public String getAccountMoney() {
        return accountMoney;
    }

    public void setAccountMoney(String accountMoney) {
        this.accountMoney = accountMoney;
    }

    public String getIsCanComplaint() {
        return isCanComplaint;
    }

    public void setIsCanComplaint(String isCanComplaint) {
        this.isCanComplaint = isCanComplaint;
    }

    public String getIsHaveComplaint() {
        return isHaveComplaint;
    }

    public void setIsHaveComplaint(String isHaveComplaint) {
        this.isHaveComplaint = isHaveComplaint;
    }

    public String getEmpowerAccessState() {
        return empowerAccessState;
    }

    public void setEmpowerAccessState(String empowerAccessState) {
        this.empowerAccessState = empowerAccessState;
    }

    public String getCouponMoney() {
        return couponMoney;
    }

    public void setCouponMoney(String couponMoney) {
        this.couponMoney = couponMoney;
    }

    public String getWxTotalFee() {
        return wxTotalFee;
    }

    public void setWxTotalFee(String wxTotalFee) {
        this.wxTotalFee = wxTotalFee;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getConsultingFeeType() {
        return consultingFeeType;
    }

    public void setConsultingFeeType(String consultingFeeType) {
        this.consultingFeeType = consultingFeeType;
    }

    public String getConsultingFee() {
        return consultingFee;
    }

    public void setConsultingFee(String consultingFee) {
        this.consultingFee = consultingFee;
    }

    public String getCostName() {
        return costName;
    }

    public void setCostName(String costName) {
        this.costName = costName;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState;
    }

    public String getPayEndTime() {
        return payEndTime;
    }

    public void setPayEndTime(String payEndTime) {
        this.payEndTime = payEndTime;
    }

    public String getPayMethord() {
        return payMethord;
    }

    public void setPayMethord(String payMethord) {
        this.payMethord = payMethord;
    }

    public String getLicenseMinute() {
        return licenseMinute;
    }

    public void setLicenseMinute(String licenseMinute) {
        this.licenseMinute = licenseMinute;
    }
}
