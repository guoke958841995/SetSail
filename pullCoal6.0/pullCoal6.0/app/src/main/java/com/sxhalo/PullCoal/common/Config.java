package com.sxhalo.PullCoal.common;


/**
 * APP 参数变量配置
 * Created by amoldZhang on 2016/12/3.
 */
public class Config {

    public  String CHANNEL_KEY = "SXHALO_CHANNEL_NAME"; //多渠道打包参数
    /**数据库版本号*/
    public  int DB_VERSION = 10; // 更新时候只需要修改这里就可以了
    /**数据库名称*/
    public  String DB_NAME = "pullcoaldatabase.db";// 数据库名称

    public  String USER_AGREEMENT = "http://www.17lm.com/lamweb/useragreement.html";  //用户协议
    public  String PRIVACY_POLICY = "http://www.17lm.com/lamweb/privacypolicy.html";  //隐私政策
    public  String DISCLAIMER_OF_LIABILITY = "http://www.17lm.com/lamweb/disclaimer.html";  //免责声明
//    public  String USER_MANUAL = "http://www.17lm.com/tradingcheats.html";  //用户手册（服务-交易秘籍）
    public  String ABOUT_MY = "http://www.17lm.com/lamweb/aboutlmb.html";  //关于拉煤宝
    public  String TRUCKSKE_DAND_QUESTIONS = "http://www.17lm.com/lamweb/mobile/lmbtruckquestions.html";  //司机认证说明
    public  String USERASKE_DAND_QUESTIONS = "http://www.17lm.com/lamweb/mobile/lmbusersquestions.html";  //买家认证说明
    public  String ORDER_QUESTIONS = "http://www.17lm.com/lamweb/mobile/lmborderquestions.html";  // 订单常见问题帮助
    public  String PULL_COAL_QUESTIONS = "http://www.17lm.com/lamweb/mobile/lmbdrawingquestions.html";//接单拉煤常见问题帮助
    public  String COMMON_PROBLEM = "http://www.17lm.com/lamweb/mobile/lmballquestions.html"; //常见问题帮助
    public  String COUPONS_RULES = "http://www.17lm.com/lamweb/mobile/lmbcoupontypesandrules.html"; //优惠券使用规则
    public  String COUPONS_NOTICE_FOR_USE = "http://www.17lm.com/lamweb/mobile/lmbcouponquestions.html"; //优惠券使用須知
    public  String COUPONS_NOTICE_RULE_OF_MARGIN = "http://www.17lm.com/lamweb/mobile/lmbuserdemand.html"; //保证金规则
    public  String COUPONS_NOTICE_BUY_COAL_LONLINE = "http://www.17lm.com/lamweb/mobile/lmbbuycoalonline.html"; //在线买煤常见问题说明
    public  String COUPONS_JOINRULE = "https://www.17lm.com/mobile/joinrule.html"; //入住加盟


    /**qq第三方登陆appId*/
    public  String QQ_APPID = "1105468578";
    /**微信第三方登陆appId*/
    public  String weChat_APPID = "wx277a84cfb2d5ab6c";
    /**微信公众号appId*/
    public  String weChat_GZ_APPID = "wx390fb15941423ebf";
    /**微信商户平台Id*/
    public  String weChat_MCH_Id = "1495438252";
    /**微信第三方登陆AppSecret*/
    public  String weChat_APPSecret = "d911788f6a5f39ebe1c2d17b7326f88a";
    /**微信商户平台密钥*/
    public  String weChat_APP_KEY = "95brWU3rfr0HFgye23hEStRDcMQ30FPq";
    /**微博Appkey*/
    private  String Sina_APP_KEY = "1078584556";
    /**微博AppSecret*/
    private  String Sina_APP_SECRET = "2fcee0ae22d7deb98e428c73e80ad7da";
    /**微博redirect_url*/
    private  String Sina_REDIRECT_URL = "http://open.weibo.com/apps/1078584556";
    /**阿里百川反馈appkey*/
    public  String ALI_APPKEY = "23414676";
    /**短信验证appkey*/
    public  String SIM_APPKEY = "135924e960c1f";
    /**短信验证appSecret*/
    public  String SIM_APPSECRET = "4f65e32519a55127b7b404e6e8d244dd";
    /**高德地图appkey*/
    public  String MAP_APPKEY = "921033231bf2ef395cf9a5a6446751a2";

    //AES加密密码
//    private  String password = SharedTools.getStringValue(MyAppLication.app,"secretKey","-1");
    //AES加密偏移量
    private  String  IV ="43C0EC5CECAD4396";

    //輪詢時間(单位：s)
    private  int Timg = 300;

    // 0加密   非0 不加密
    public static String AES = "0";
    public static boolean AES_APP = true;
    //是否开启模拟导航   true为开启
    public static boolean if_NAVI = false;

    public  String getWeChat_APPID() {
        return weChat_APPID;
    }

    public  String getQqAppid() {
        return QQ_APPID;
    }

    public  String getWeChat_APPSecret() {
        return weChat_APPSecret;
    }

    public  String getSina_APP_KEY() {
        return Sina_APP_KEY;
    }

    public  void setSina_APP_KEY(String sina_APP_KEY) {
        Sina_APP_KEY = sina_APP_KEY;
    }

    public  String getSina_APP_SECRET() {
        return Sina_APP_SECRET;
    }

    public  void setSina_APP_SECRET(String sina_APP_SECRET) {
        Sina_APP_SECRET = sina_APP_SECRET;
    }

    public  String getSinaRedirectUrl() {
        return Sina_REDIRECT_URL;
    }

    public  void setSinaRedirectUrl(String sinaRedirectUrl) {
        Sina_REDIRECT_URL = sinaRedirectUrl;
    }

    public  String getAliAppkey() {
        return ALI_APPKEY;
    }

    public  void setAliAppkey(String aliAppkey) {
        ALI_APPKEY = aliAppkey;
    }

    public  String getSIM_APPKEY() {
        return SIM_APPKEY;
    }

    public  void setSIM_APPKEY(String SIM_APPKEY) {
        SIM_APPKEY = SIM_APPKEY;
    }

    public  String getSIM_APPSECRET() {
        return SIM_APPSECRET;
    }

    public  void setSIM_APPSECRET(String APPSECRET) {
        APPSECRET = APPSECRET;
    }


//    public  String getPassword() {
//        return password;
//    }

    public  void setPassword(String password) {
        password = password;
    }

    public  String getIV() {
        return IV;
    }

    public  void setIV(String IV) {
        IV = IV;
    }

    public  int getTimg() {
        return Timg;
    }

    public  void setTimg(int timg) {
        Timg = timg;
    }
}
