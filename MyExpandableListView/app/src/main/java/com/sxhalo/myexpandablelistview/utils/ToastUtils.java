package com.sxhalo.myexpandablelistview.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by amoldZhang on 2016/10/22.
 */

public class ToastUtils {


    public static void showText(Context mContext, String text){
        Toast.makeText(mContext,text,Toast.LENGTH_SHORT).show();
    }

}
