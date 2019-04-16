package com.sxhalo.PullCoal.common;

/**
 * app 常量
 */
public class AppConstant {

    /***************************************** Paper Key *****************************************/


    public static final String PUBLIC_KEY = "publicKey";

    public static final String SECRET_KEY = "secretKey";

    public static final String ACCESS_KEY = "accessKey";

    public static final String USER_ID_KEY = "userId";

    public static final String CITY_ID_KEY = "cityId";

    public static final String SELECT_CODE_KEY = "select_code";

    public static final String SELECT_CITY_KEY = "select_city";

    /**
     * 行政区划版本号
     */
    public static final String REGION_VERSION_KEY = "region_version";

    /**
     * 行政区划本地存储文件名
     */
    public static final String REGION_CODE_FILE_NAME = "region_code";

    public static final boolean AES_APP = true;

    /**
     * 下拉刷新头部 最后刷新时间
     */
    public static final String REFRESH_HEADER_UPDATE_LAST_TIME_KEY = "refresh_header_last_time";

    /**
     * 用户第一次使用
     */
    public static final String FIRST_USE_KEY = "first_use";

    /**
     * 数据库版本号
     * 更新时候只需要修改这里就可以了
     */
    public static final int DB_VERSION = 10;
    /**
     * 数据库名称
     */
    public static final String DB_NAME = "pullcoaldatabase.db";


}
