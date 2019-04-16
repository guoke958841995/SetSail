package com.sxhalo.PullCoal.retrofithttp;


import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.retrofithttp.api.HttpResult;
import com.sxhalo.PullCoal.utils.GHLog;

/**
 * Created by amoldZhang on 2017/7/18.
 */
public class ApiException extends RuntimeException {

    public static final int DATA_ERROR = 100; //数据模型不对
    public static final int API_ERROR = 101; //接口访问异常
    public static final int DATA_NULL = 102; // body 为 空

    public ApiException(HttpResult httpResult) {
        this(getApiExceptionMessage(httpResult));
    }

    /**
     *
     * @param detailMessage  联网成功返回的JsonString
     * @param httpResult   解析头部后的实体
     */
    public ApiException(String detailMessage, HttpResult httpResult) {
        this(detailMessage,getApiExceptionThrowable(httpResult));
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }


    public ApiException(String detailMessage, Throwable throwable) {
        super(detailMessage,throwable);
    }

    private static Throwable getApiExceptionThrowable(HttpResult httpResult){
        Throwable throwable = new Throwable(getApiExceptionMessage(httpResult));
        return throwable;
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     *
     * @param httpResult
     * @return
     */
    private static String getApiExceptionMessage(HttpResult httpResult) {
        String messageErroe = "";
        int switchCode = 0;
        try {
            switchCode = Integer.valueOf(httpResult.getHead().getError_code());
        } catch (Exception e) {
            GHLog.e("ErroeCode转换异常",e.toString());
        }
        switch (switchCode) {
            case 0:
                messageErroe = "反回的ErroeCode不能转换为Int类型";
                break;
            case DATA_ERROR:
                messageErroe = "反回的JSON不能转换为传入的Bean类型";
                break;
            case API_ERROR:  //接口访问异常
                messageErroe = httpResult.getHead().getError();
                break;
            case DATA_NULL:  // body 为 空
                messageErroe = "反回的JSON不能转换为传入的Bean类型";
                break;
            case 2090001:
                messageErroe = "网络连接错误，请稍候再试";
//                GHLog.e(httpResult.getHead().getError_code(),httpResult.getHead().getError());
                break;
            case 2100001:
                messageErroe = "";
//                GHLog.e(httpResult.getHead().getError_code(),httpResult.getHead().getError());
                break;
            case 1030001:  // 该手机号已注册
                messageErroe =  httpResult.getHead().getError();
                break;
            case 1030002:  // 用户不存在
                messageErroe =  httpResult.getHead().getError();
                break;
            case 1030003:  // 此用户已被禁用
                messageErroe =  httpResult.getHead().getError();
                break;
            case 1030004:  // 密码错误
                switch (MyAppLication.ifLogin){
                    case 0:
//                        messageErroe =  "抱歉，您的密码输错啦";
                        break;
                    case 1:
                        messageErroe =  "旧密码输入有误，请检查后重新输入";
                        break;
                }
                break;
            case 1030005:  //新密码不能和旧密码一样！
                messageErroe =  httpResult.getHead().getError();
                break;
            case 1060001:  //图片上传   //图片大小不能超过10M
                messageErroe =  httpResult.getHead().getError();
                break;
            case 2060018:  //微信提现失败信息
                messageErroe =  httpResult.getHead().getError();
                break;
            case 2030015:  //userId不存在
                messageErroe =  httpResult.getHead().getError();
                break;
            case 2040003:  //未认证司机不能发布我要拉煤
                messageErroe =  httpResult.getHead().getError();
                break;
//            case 2060030:  //账户异常，请联系客服！
//                messageErroe =  httpResult.getHead().getError();
//                break;
//            case 2060011:  //已支付，请不要重复支付！
//                messageErroe =  httpResult.getHead().getError();
//                break;
//            default:
//                messageErroe = httpResult.getHead().getError();
//                break;
        }
        return messageErroe;
    }
}
