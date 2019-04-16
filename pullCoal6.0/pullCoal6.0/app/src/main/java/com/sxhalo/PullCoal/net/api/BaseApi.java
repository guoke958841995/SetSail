package com.sxhalo.PullCoal.net.api;

import com.sxhalo.PullCoal.net.HttpResultFunc;
import com.sxhalo.PullCoal.net.HttpUtil;
import com.sxhalo.PullCoal.net.ResponseDecryptFuncation;
import com.sxhalo.PullCoal.net.RxSchedulers;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 *  接口公用方法调用
 * Created by amoldZhang on 2019/4/12
 */
public class BaseApi {

    /**
     * 添加联网代理 消耗联网
     * @param responseBodyObservable 接口调用
     * @param type  解析类型
     * @return  默认加密
     */
    public Observable getObservable(Observable<ResponseBody> responseBodyObservable,Type type) {
        Observable observable = responseBodyObservable
                .compose(RxSchedulers.applySchedulers())
                .map(new ResponseDecryptFuncation(true))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body
        return observable;
    }

    /**
     * 添加联网代理 消耗联网
     * @param responseBodyObservable 接口调用
     * @param type  解析类型
     * @param ifEncryption  是否加密
     * @return
     */
    public Observable getObservable(Observable<ResponseBody> responseBodyObservable,Type type,boolean ifEncryption) {
        Observable observable = responseBodyObservable
                .compose(RxSchedulers.applySchedulers())
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body
        return observable;
    }

    /**
     * 接口参数加密
     *
     * @param ifEncryption 是否加密
     * @return
     */
    public LinkedHashMap<String, String> setParamsAES(boolean ifEncryption) {
        return setParamsAES(ifEncryption, new LinkedHashMap<String, String>());
    }

    /**
     * 接口参数加密
     *
     * @param ifEncryption 是否加密
     * @param params
     * @return
     */
    public LinkedHashMap<String, String> setParamsAES(boolean ifEncryption, LinkedHashMap<String, String> params) {
        if (ifEncryption) {
            LinkedHashMap<String, String> map = HttpUtil.setParameterEncryption(params);
            return map;
        }
        return params;
    }
}
