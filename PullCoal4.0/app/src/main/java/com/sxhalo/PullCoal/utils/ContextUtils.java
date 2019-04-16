package com.sxhalo.PullCoal.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;


public class ContextUtils {
    /**
     * 对于一个没有被载入或者想要动态载入的界面, 都需要使用inflate来载入
     *
     * @param context
     * @return
     */
    public static LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 判断网络是否连接
     *
     * @param contxt
     * @return
     */
    public static boolean isNetWorking(Context contxt) {
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) contxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}
