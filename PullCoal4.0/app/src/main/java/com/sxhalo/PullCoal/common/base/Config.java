package com.sxhalo.PullCoal.common.base;

import android.os.Bundle;

import com.sxhalo.PullCoal.BuildConfig;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.util.List;

/**
 * APP 参数变量配置
 * Created by amoldZhang on 2016/12/3.
 */
public class Config {

    public  String CHANNEL_KEY = "SXHALO_CHANNEL_NAME"; //多渠道打包参数
    /**数据库版本号*/
    public  int DB_VERSION = 11; // 更新时候只需要修改这里就可以了
    /**数据库名称*/
    public  String DB_NAME = "pullcoaldatabase.db";// 数据库名称


    private  String ABOUT_MY = "ABOUT_MY";  //关于拉煤宝
    private  String COMMON_PROBLEM = "COMMON_PROBLEM"; //常见问题帮助
    private  String COUPONS_NOTICE_BUY_COAL_LONLINE = "COUPONS_NOTICE_BUY_COAL_LONLINE"; //在线买煤常见问题说明
    private  String COUPONS_NOTICE_FOR_USE = "COUPONS_NOTICE_FOR_USE"; //优惠券使用須知
    private  String COUPONS_NOTICE_RULE_OF_MARGIN = "COUPONS_NOTICE_RULE_OF_MARGIN"; //保证金规则
    private  String COUPONS_RULES = "COUPONS_RULES"; //优惠券使用规则
    private  String DISCLAIMER_OF_LIABILITY = "DISCLAIMER_OF_LIABILITY"; //免责声明
    private  String ORDER_QUESTIONS = "ORDER_QUESTIONS"; //订单常见问题帮助
    private  String PRIVACY_POLICY = "PRIVACY_POLICY"; //隐私政策
    private  String PULL_COAL_QUESTIONS = "PULL_COAL_QUESTIONS"; //接单拉煤常见问题帮助

    private  String TRUCKSKE_DAND_QUESTIONS = "TRUCKSKE_DAND_QUESTIONS"; //司机认证说明
    private  String USERASKE_DAND_QUESTIONS = "USERASKE_DAND_QUESTIONS"; //买家认证说明
    private  String USER_AGREEMENT = "USER_AGREEMENT"; //用户协议
    private  String USER_MANUAL = "USER_MANUAL"; //	用户手册（服务-交易秘籍）
    private  String COUPONS_JOINRULE = "COUPONS_JOINRULE"; //入驻加盟

    private  String COAL_COMPARE = "COAL_COMPARE"; //煤炭对比
    private  String COALGOODSINFO = "COALGOODSINFO"; //煤炭详情


    private  String SHARE_RELEASE_COMP = "SHARE_RELEASE_COMP"; //矿口正式分享
    private  String SHARE_RELEASE_SALES = "SHARE_RELEASE_SALES"; //信息部正式分享
    private  String SHARE_RELEASE_TRAN = "SHARE_RELEASE_TRAN"; //货运正式分享
    private  String SHARE_RELEASE_COAL = "SHARE_RELEASE_COAL"; //煤炭正式分享


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
    public  String Sina_APP_KEY = "1078584556";
    /**微博AppSecret*/
    public  String Sina_APP_SECRET = "2fcee0ae22d7deb98e428c73e80ad7da";
    /**微博redirect_url*/
    public  String Sina_REDIRECT_URL = "http://open.weibo.com/apps/1078584556";
    /**阿里百川反馈appkey*/
    public  String ALI_APPKEY = "23414676";
    /**短信验证appkey*/
    public  String SIM_APPKEY = "135924e960c1f";
    /**短信验证appSecret*/
    public  String SIM_APPSECRET = "4f65e32519a55127b7b404e6e8d244dd";
    /**高德地图appkey*/
    public  String MAP_APPKEY = "921033231bf2ef395cf9a5a6446751a2";

    //AES加密密码
    public  String password = SharedTools.getStringValue(MyAppLication.app,"secretKey","-1");
    //AES加密偏移量
    public  String  IV ="43C0EC5CECAD4396";

    //輪詢時間(单位：s)
    public  int Timg = 300;

    // 0加密   非0 不加密
    public static String AES = "0";
    public static boolean AES_APP = true;
    //是否开启模拟导航   true为开启
    public static boolean if_NAVI = false;


    public List<Dictionary> getDictionary() {
        List<Dictionary> dictionaryList =
                OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100015"});
        return dictionaryList;
    }

    public String getDictionaryValue(String key){
        if (getDictionary().size() != 0){
            String value = "";
            for (FilterEntity filterEntity :getDictionary().get(0).list){
                if (key.equals(filterEntity.dictCode)){
                    value = filterEntity.dictValue;
                    return value;
                }
            }
        }
        return null;
    }

    public String getCHANNEL_KEY() {
        return CHANNEL_KEY;
    }

    public int getDB_VERSION() {
        return DB_VERSION;
    }

    public String getDB_NAME() {
        return DB_NAME;
    }

    public String getABOUT_MY() {
        return getDictionaryValue(ABOUT_MY);
    }

    public String getCOMMON_PROBLEM() {
        return getDictionaryValue(COMMON_PROBLEM);
    }

    public String getCOUPONS_NOTICE_BUY_COAL_LONLINE() {
        return getDictionaryValue(COUPONS_NOTICE_BUY_COAL_LONLINE);
    }

    public String getCOUPONS_NOTICE_FOR_USE() {
        return getDictionaryValue(COUPONS_NOTICE_FOR_USE);
    }

    public String getCOUPONS_NOTICE_RULE_OF_MARGIN() {
        return getDictionaryValue(COUPONS_NOTICE_RULE_OF_MARGIN);
    }

    public String getCOUPONS_RULES() {
        return getDictionaryValue(COUPONS_RULES);
    }

    public String getDISCLAIMER_OF_LIABILITY() {
        return getDictionaryValue(DISCLAIMER_OF_LIABILITY);
    }

    public String getORDER_QUESTIONS() {
        return getDictionaryValue(ORDER_QUESTIONS);
    }

    public String getPRIVACY_POLICY() {
        return getDictionaryValue(PRIVACY_POLICY);
    }

    public String getPULL_COAL_QUESTIONS() {
        return getDictionaryValue(PULL_COAL_QUESTIONS);
    }

    public String getTRUCKSKE_DAND_QUESTIONS() {
        return getDictionaryValue(TRUCKSKE_DAND_QUESTIONS);
    }

    public String getUSERASKE_DAND_QUESTIONS() {
        return getDictionaryValue(USERASKE_DAND_QUESTIONS);
    }

    public String getUSER_AGREEMENT() {
        return getDictionaryValue(USER_AGREEMENT);
    }

    public String getUSER_MANUAL() {
        return getDictionaryValue(USER_MANUAL);
    }

    public String getCOUPONS_JOINRULE() {
        return getDictionaryValue(COUPONS_JOINRULE);
    }

    public String getSHARE_RELEASE_COMP() {
        return getDictionaryValue(SHARE_RELEASE_COMP);
    }

    public String getSHARE_RELEASE_SALES() {
        return getDictionaryValue(SHARE_RELEASE_SALES);
    }

    public String getSHARE_RELEASE_TRAN() {
        return getDictionaryValue(SHARE_RELEASE_TRAN);
    }

    public String getSHARE_RELEASE_COAL() {
        return getDictionaryValue(SHARE_RELEASE_COAL);
    }

    public String getCOAL_COMPARE() {
        return getDictionaryValue(COAL_COMPARE);
    }

    public String getCOALGOODSINFO() {
        return getDictionaryValue(COALGOODSINFO);
    }



}
