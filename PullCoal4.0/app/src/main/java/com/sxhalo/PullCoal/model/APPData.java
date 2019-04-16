package com.sxhalo.PullCoal.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amoldZhang on 2017/7/27.
 */
public class APPData<T> implements Serializable{

    private String dataType;
    private String title;
    private String message;
    private String orderMsgCount;
    private String transMsgCount;
    private String sysMsgCount;
    private String recordCount;
    private String availableQuantity; //可领取优惠券数量
    private String obtainQuantity; //当前已领取优惠券数量
    private T entity;
    private List<T> list;
    private List<T> followUsers;

    public String getRecordCount() {
        return recordCount;
    }

    public List<T> getFollowUsers() {
        return followUsers;
    }

    public void setFollowUsers(List<T> followUsers) {
        this.followUsers = followUsers;
    }

    public void setRecordCount(String recordCount) {
        this.recordCount = recordCount;
    }

    public String getObtainQuantity() {
        return obtainQuantity;
    }

    public void setObtainQuantity(String obtainQuantity) {
        this.obtainQuantity = obtainQuantity;
    }

    public String getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getOrderMsgCount() {
        return orderMsgCount;
    }

    public void setOrderMsgCount(String orderMsgCount) {
        this.orderMsgCount = orderMsgCount;
    }

    public String getTransMsgCount() {
        return transMsgCount;
    }

    public void setTransMsgCount(String transMsgCount) {
        this.transMsgCount = transMsgCount;
    }

    public String getSysMsgCount() {
        return sysMsgCount;
    }

    public void setSysMsgCount(String sysMsgCount) {
        this.sysMsgCount = sysMsgCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
