package com.sxhalo.PullCoal.wxapi;


import com.sxhalo.PullCoal.model.ThirdUserInfo;

/**
 * 数据回掉接口
 * Created by amoldZhang on 2016/11/11.
 */
public class ThirdUserVerify {

    private static ThirdUserData loginData;
    public interface ThirdUserData {
        <T> void setThirdUserData(T t);
    }
    public void getThirdUserData(ThirdUserData loginData) {
        this.loginData = loginData;
    }

    public static void verifyUser(ThirdUserInfo info) {
        if(loginData != null){
            loginData.setThirdUserData(info);
        }
    }
}
