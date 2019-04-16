package com.sxhalo.PullCoal.utils;

import java.util.regex.Pattern;

/**
 * 正则校验类
 * Created by liz on 2017/11/22.
 */

public class RegexpUtils {
    /**
     * 匹配正整数
     */
    public static final String POSITIVE_INTEGER_REGEXP = "^[1-9]\\d*";

    public static final boolean validatePositiveInteger (String positiveInteger) {
        Pattern p = Pattern.compile(POSITIVE_INTEGER_REGEXP);
        return p.matcher(positiveInteger).matches();
    }
}
