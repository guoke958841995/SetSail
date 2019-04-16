package com.sxhalo.PullCoal.model;

import android.content.Context;
import android.widget.Toast;

/**
 * 身份证校验结果类
*/
public class Result {
    /**
     * 错误消息，为空时，代表验证通过
     */
    private String error;


    public boolean isLegal() {
        //两个变量为默认值,即认为是合法的
        return error == null || error.equals("");
    }

    public String getError() {
        return error;
    }

    public void setError(String message) {
        this.error = message;
    }

    public void show(Context context) {
        if (!isLegal())
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}