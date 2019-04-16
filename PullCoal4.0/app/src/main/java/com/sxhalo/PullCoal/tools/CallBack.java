package com.sxhalo.PullCoal.tools;


import android.content.Context;

/**
 * Created by amoldZhang on 2016/11/29.
 */
public class CallBack {

    public static Callback callback;

    //实现数据传递
    public void onCallBack(Callback callback) {
        this.callback = callback;
    }
    //创建接口
    public interface Callback {
        void onCallBack();
    }

    public static MyCallBack mBack;
    public void setMyCallBack(MyCallBack mBack){
        this.mBack = mBack;
    }

    public interface MyCallBack{
        void OnAreaText(String cityTxt, String cityCode);
    }

    public static AppException  appException;
    public void setAppException(AppException appException){
        this.appException = appException;
    }
    public interface AppException{
        void onBack(Context cont,String crashReport);
    }
}
