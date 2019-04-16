package com.sxhalo.PullCoal.net.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 *  货运相接口
 * Created by amoldZhang on 2019/4/12
 */
public interface FreightService {

    /**
     * 货运列表请求
     * @param params 请求参数
     * @return
     */
    @GET("coalTransport/list")
    Observable<ResponseBody> getCoalTransportList(@QueryMap Map<String, String> params);

    /**
     * 订阅货运列表
     * @param params 请求参数
     * @return
     */
    @GET("transportSubscribe/list")
    Observable<ResponseBody> getTransportSubscribeList(@QueryMap Map<String, String> params);

    /**
     * 添加订阅货运
     * @param params 请求参数
     * @return
     */
    @GET("transportSubscribe/create")
    Observable<ResponseBody> getTransportSubscribeCreate(@QueryMap Map<String, String> params);

    /**
     * 删除订阅货运
     * @param params 请求参数
     * @return
     */
    @GET("transportSubscribe/delete")
    Observable<ResponseBody> getTransportSubscribeDelete(@QueryMap Map<String, String> params);

}
