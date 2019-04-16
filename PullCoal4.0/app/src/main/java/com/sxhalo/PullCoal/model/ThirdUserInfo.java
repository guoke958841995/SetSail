package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2016/11/9.
 */
public class ThirdUserInfo implements Serializable{

    public interface ErrCode {
        int ERR_OK = 0;     //授权成功
        int ERR_AUTH_DENIED = -1;  //用户拒绝授权
        int ERR_USER_CANCEL = -2;  //用户取消
        int ERR_BAN = -6;  //签名错无
    }



    private String thirdID;
    private String nickName;
    private String city;
    private String icon;
    private String userSex;
    private String type;
    private String errText;
    private int errCode;


    public ThirdUserInfo(){}

    public String getErrText() {
        return errText;
    }

    public void setErrText(String errText) {
        this.errText = errText;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getThirdID() {
        return thirdID;
    }

    public void setThirdID(String thirdID) {
        this.thirdID = thirdID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
