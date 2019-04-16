package com.sxhalo.PullCoal.net.api;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import io.reactivex.Observable;

/**
 *  货运相关接口
 * Created by amoldZhang on 2019/4/12
 */
public class FreightApi extends BaseApi{

    private FreightService mFreightService;

    public static FreightApi mInstance;


    private FreightApi(FreightService mFreightService) {
        this.mFreightService = mFreightService;
    }

    public static FreightApi getInstance(FreightService mFreightService) {
        if (mInstance == null) {
            synchronized (HomeApi.class) {
                if (mInstance == null) {
                    mInstance = new FreightApi(mFreightService);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取 货运 列表数据
     *
     * @param type
     * @param params
     * @return
     */
    public Observable getCoalTransportList(Type type, LinkedHashMap<String, String> params) {
        LinkedHashMap<String, String> map = setParamsAES(true, params);
        return getObservable(mFreightService.getCoalTransportList(map),type,true);
    }
    /**
     * 添加 货运 订阅数据
     *
     * @param type
     * @param params
     * @return
     */
    public Observable getTransportSubscribeCreate(Type type, LinkedHashMap<String, String> params) {
        LinkedHashMap<String, String> map = setParamsAES(true, params);
        return getObservable(mFreightService.getTransportSubscribeCreate(map),type,true);
    }
    /**
     * 获取 订阅货运 列表数据
     *
     * @param type
     * @param params
     * @return
     */
    public Observable getTransportSubscribeList(Type type, LinkedHashMap<String, String> params) {
        LinkedHashMap<String, String> map = setParamsAES(true, params);
        return getObservable(mFreightService.getTransportSubscribeList(map),type,true);
    }
    /**
     * 删除 货运 订阅数据
     *
     * @param type
     * @param params
     * @return
     */
    public Observable getTransportSubscribeDelete(Type type, LinkedHashMap<String, String> params) {
        LinkedHashMap<String, String> map = setParamsAES(true, params);
        return getObservable(mFreightService.getTransportSubscribeDelete(map),type,true);
    }
}
