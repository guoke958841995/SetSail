package com.sxhalo.PullCoal.net.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
public interface HomeApiService {

    @GET("public/getAccessKey")
    Observable<ResponseBody> getPublicKey();

    @GET("getDictionary")
    Observable<ResponseBody> getDictionary(@QueryMap Map<String, String> params);

    @GET("getAdvertisement")
    Observable<ResponseBody> getAdvertisement(@QueryMap Map<String, String> params);

    @GET("getRegionCode")
    Observable<ResponseBody> getRegionCode(@QueryMap Map<String, String> params);
}
