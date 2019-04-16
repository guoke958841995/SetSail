package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * 投递意向信息部
 * Created by amoldZhang on 2018/5/19.
 */

public class DelivererInformationDepartment implements Serializable{
//     "address": "大石公路",
//             "coalSalesId": "20000004",
//             "companyName": "聚亿煤炭信息部",
//             "userMobile": "15399306166",
//             "realName": "马安东",
//             "userId": "80000256"

    private String address;
    private String coalSalesId;
    private String companyName;
    private String userMobile;
    private String realName;
    private String userId;


    private boolean selectInfor;

    public boolean isSelectInfor() {
        return selectInfor;
    }

    public void setSelectInfor(boolean selectInfor) {
        this.selectInfor = selectInfor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoalSalesId() {
        return coalSalesId;
    }

    public void setCoalSalesId(String coalSalesId) {
        this.coalSalesId = coalSalesId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
