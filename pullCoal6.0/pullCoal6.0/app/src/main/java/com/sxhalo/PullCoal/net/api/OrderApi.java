package com.sxhalo.PullCoal.net.api;

import com.sxhalo.PullCoal.net.HttpResultFunc;
import com.sxhalo.PullCoal.net.HttpUtil;
import com.sxhalo.PullCoal.net.ResponseDecryptFuncation;
import com.sxhalo.PullCoal.net.RxSchedulers;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import io.reactivex.Observable;

public class OrderApi {
    private OrderApiService mOrderApiService;

    public static OrderApi mInstance;

    //参数是否加密（默认加密，返回数据需要解密）
    private boolean ifEncryption = true;

    private OrderApi(OrderApiService orderApiService) {
        this.mOrderApiService = orderApiService;
    }

    public static OrderApi getInstance(OrderApiService orderApiService) {
        if (mInstance == null) {
            synchronized (HomeApi.class) {
                if (mInstance == null) {
                    mInstance = new OrderApi(orderApiService);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取煤炭订单列表
     *
     * @param type
     * @param params
     * @return
     */
    public Observable getCoalOrderList(Type type, LinkedHashMap<String, String> params) {

        //参数加密
        LinkedHashMap<String, String> map = HttpUtil.setParameterEncryption(params);
        Observable observable = mOrderApiService.getCoalOrderList(map)
                .compose(RxSchedulers.applySchedulers())  //线程切换
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body

        return observable;
    }

    /**
     * 获取派车单列表
     */
    public Observable getSendCarOrderlist(Type type, LinkedHashMap<String, String> params) {

        //参数加密
        LinkedHashMap<String, String> map = HttpUtil.setParameterEncryption(params);
        Observable observable = mOrderApiService.getSendCarOrderlist(map)
                .compose(RxSchedulers.applySchedulers())  //线程切换
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body
        return observable;

    }

    /**
     * 获取求购单列表
     */
    public Observable getUserDemandlist(Type type, LinkedHashMap<String, String> params) {

        //参数加密
        LinkedHashMap<String, String> map = HttpUtil.setParameterEncryption(params);
        Observable observable = mOrderApiService.getUserDemandlist(map)
                .compose(RxSchedulers.applySchedulers())  //线程切换
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body
        return observable;

    }

    /**
     * 货运单列表
     * */
    public Observable  getTransportOrderList(Type type, LinkedHashMap<String, String> params) {

        //参数加密
        LinkedHashMap<String, String> map = HttpUtil.setParameterEncryption(params);
        Observable observable = mOrderApiService.getUsertransportOrderlist(map)
                .compose(RxSchedulers.applySchedulers())  //线程切换
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body
        return observable;

    }




}
