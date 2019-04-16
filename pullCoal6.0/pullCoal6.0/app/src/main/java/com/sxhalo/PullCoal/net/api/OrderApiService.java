package com.sxhalo.PullCoal.net.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * desc
 *
 * @author zhaodong
 * @date 2019/4/11
 */
public interface OrderApiService {

    @GET("user/coalOrder/list")
    Observable<ResponseBody> getCoalOrderList(@QueryMap Map<String, String> params);

    @GET("user/coalOrderTransport/list")
    Observable<ResponseBody>getSendCarOrderlist(@QueryMap Map<String, String> params);

    @GET("userDemand/list")
    Observable<ResponseBody>getUserDemandlist(@QueryMap Map<String, String> params);

    @GET("user/transportOrder/list")
    Observable<ResponseBody>getUsertransportOrderlist(@QueryMap Map<String, String> params);


}
