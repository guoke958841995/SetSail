package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 *  采样化验实体
 * Created by amoldZhang on 2019/3/19.
 */
public class SamplingTest implements Serializable{

    private String typeName;
    private String mineMouthName; // 矿口名称
    private String mineMouthId;   //矿口id

    private String goodsId;     //煤id
    private String coalName;   // 煤名称

    private String sampleId;   // 采样化验id
    private String sampleState;   // 采样化验 状态  0未受理  1受理  2已采样   3 化验中   4已邮寄  5完成 100用户取消
    private String updateTime;   // 更新时间
    private String differMinute;   // 转化后时间

    private String money;   //总金额（元）
    private String tradeNo;   // 采样化验预支付单号
    private String prepayId;   //微信预支付单号
    private String denomination;   //抵扣金额（元）

    private String address;
    private String contactPhone; //联系人电话
    private String contactPerson; //联系人姓名
    private String express;//快递拼音
    private String expressNum; //快递单号
    private String expressName; //快递公司名称
    private String payType;
    private String payMoney; //微信支付金额
    private String couponMoney; //代金券支付金额


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getExpressNum() {
        return expressNum;
    }

    public void setExpressNum(String expressNum) {
        this.expressNum = expressNum;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getCouponMoney() {
        return couponMoney;
    }

    public void setCouponMoney(String couponMoney) {
        this.couponMoney = couponMoney;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
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

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleState() {
        return sampleState;
    }

    public void setSampleState(String sampleState) {
        this.sampleState = sampleState;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDifferMinute() {
        return differMinute;
    }

    public void setDifferMinute(String differMinute) {
        this.differMinute = differMinute;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMineMouthName() {
        return mineMouthName;
    }

    public void setMineMouthName(String mineMouthName) {
        this.mineMouthName = mineMouthName;
    }

    public String getMineMouthId() {
        return mineMouthId;
    }

    public void setMineMouthId(String mineMouthId) {
        this.mineMouthId = mineMouthId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getCoalName() {
        return coalName;
    }

    public void setCoalName(String coalName) {
        this.coalName = coalName;
    }
}
