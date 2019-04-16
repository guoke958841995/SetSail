package com.sxhalo.PullCoal.common;

import com.sxhalo.PullCoal.BuildConfig;

/**
 * 网络请求常量
 *
 * @author Xiao_
 * @date 2019/4/8 0008
 */
public class ApiConstant {

    /**
     * API版本
     */
    public static final String API_VERSION = BuildConfig.API_VERSION;

    /**
     * APP版本号
     */
    public static final String APP_VERSION = BuildConfig.APP_VERSION;

    /**
     * 接口域名配置
     */
    public static final String HOST_NAME = BuildConfig.BASE_URL;

    /**
     * 接口访问配置
     */
    public static final String PATH = BuildConfig.SERVICE;

    /**
     * 网络请求URL
     */
    public static final String BASE_URL = HOST_NAME + PATH;

    /**
     * 接口服务模式
     * API_OPERATION_MODE  接口服务模式
     */
    public static final String API_OPERATION_MODE = BuildConfig.OPERATION_MODE;


}
