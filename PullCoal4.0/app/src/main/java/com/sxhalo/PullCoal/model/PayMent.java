package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2018/1/4.
 */

public class PayMent implements Serializable{


    /**
     * dataType : operate060006
     * title : 资讯费预支付
     * costType : 费用类别
     * costName : 费用名称
     * costAmount : 费用金额
     * validTime : 有效期
     * availableSpecies : 可用种类
     * couponTotalSpecies : 总种类
     *
     * "tradeNo": "80000252020000127100001042000101",//平台支付记录流水号
     * "prepayId": "wx201410272009395522657a690389285100"//预支付交易会话标识
     */

    private String consultingFeeType;  //当前资讯费支付类型 0 煤炭 1 货运
    private String targetId;  //待支付的的商品id
    private String couponNum;  //代金券抵扣金额
    private String balanceNum;  //余额抵扣金额
    private int appropriateAmount;  //默认计算出的总金额


    private String dataType;
    private String title;
    private String costType;
    private String costName;
    private String costAmount;  //费用金额
    private String validTime;
    private String availableSpecies;  //可用种类
    private String couponTotalSpecies;  //总种类
    private String tradeNo;
    private String prepayId;
    private String availableQuantity;  // 可领代金券张数
    private String availableNumber;  // 可领代金券张数
    private String pocketAmount;  // 拉煤宝账户余额

    public String getAvailableNumber() {
        return availableNumber;
    }

    public int getAppropriateAmount() {
        return appropriateAmount;
    }

    public void setAppropriateAmount(int appropriateAmount) {
        this.appropriateAmount = appropriateAmount;
    }

    public void setAvailableNumber(String availableNumber) {
        this.availableNumber = availableNumber;
    }

    public String getBalanceNum() {
        return balanceNum;
    }

    public void setBalanceNum(String balanceNum) {
        this.balanceNum = balanceNum;
    }

    public String getPocketAmount() {
        return pocketAmount;
    }

    public void setPocketAmount(String pocketAmount) {
        this.pocketAmount = pocketAmount;
    }

    public String getCouponNum() {
        return couponNum;
    }

    public void setCouponNum(String couponNum) {
        this.couponNum = couponNum;
    }

    public String getConsultingFeeType() {
        return consultingFeeType;
    }

    public void setConsultingFeeType(String consultingFeeType) {
        this.consultingFeeType = consultingFeeType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getCostName() {
        return costName;
    }

    public void setCostName(String costName) {
        this.costName = costName;
    }

    public String getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(String costAmount) {
        this.costAmount = costAmount;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getAvailableSpecies() {
        return availableSpecies;
    }

    public void setAvailableSpecies(String availableSpecies) {
        this.availableSpecies = availableSpecies;
    }

    public String getCouponTotalSpecies() {
        return couponTotalSpecies;
    }

    public void setCouponTotalSpecies(String couponTotalSpecies) {
        this.couponTotalSpecies = couponTotalSpecies;
    }
}
