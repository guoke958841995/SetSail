package com.sxhalo.PullCoal.tools.validator;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by amoldZhang on 2017/3/13.
 * 选择验校的正则表达式
 */
public class FormatValidation extends ValidationExecutor {

    private int age;

    public FormatValidation(int age){
        this.age = age;
    }

    /**
     * 这里做校验处理
     *
     * @param context
     * @param text
     * @param message
     * @return 校验成功返回true 否则返回false
     */
    @Override
    public boolean doValidate(Context context, String text, String message) {
        String regex = getRegex();
//        boolean result = Pattern.compile(regex).matcher(text).find();
        boolean result = isFormat(text,regex);
        if (!result) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 获取验校规则
     * @return
     */
    private String getRegex(){
        String regex = "";
        switch (age){
            case 0: //验校字母或字母加数字的7到11位字符
                regex = "^[a-zA-Z](?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9_]{7,11}$";
                break;
            case 1://验校包含字母或字母加数字的7到11位字符
                regex = "^[a-zA-Z][A-Za-z0-9]{7,11}$";
                break;
            case 2: // 只能输入非0的正整数
                regex = "^\\+?[1-9][0-9]*$";
                break;
            case 3:
                regex = "^[a-zA-Z][A-Za-z0-9]{7,11}$";
                break;
        }
        return regex;
    }
}
