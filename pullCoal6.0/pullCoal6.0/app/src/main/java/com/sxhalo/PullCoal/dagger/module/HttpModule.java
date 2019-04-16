package com.sxhalo.PullCoal.dagger.module;

import android.content.Context;

import com.sxhalo.PullCoal.BuildConfig;
import com.sxhalo.PullCoal.common.ApiConstant;
import com.sxhalo.PullCoal.net.CustomTrustManager;
import com.sxhalo.PullCoal.net.HttpUtil;
import com.sxhalo.PullCoal.net.api.BaseApi;
import com.sxhalo.PullCoal.net.api.FreightApi;
import com.sxhalo.PullCoal.net.api.FreightService;
import com.sxhalo.PullCoal.net.api.HomeApi;
import com.sxhalo.PullCoal.net.api.HomeApiService;
import com.sxhalo.PullCoal.net.api.OrderApi;
import com.sxhalo.PullCoal.net.api.OrderApiService;
import com.sxhalo.PullCoal.net.interceptor.HttpCommonInterceptor;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
@Module
public class HttpModule {

    private Context mContext;

    private static final int CONNECT_TIME_OUT = 30;
    private static final int READ_TIME_OUT = 30;
    private static final int WRITE_TIME_OUT = 30;

    @Provides
    OkHttpClient provideOkHttpClient(Context context) {
        this.mContext = context;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //添加日志拦截器
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpUtil.getHttpLoggingInterceptor());
        }

        //添加公共参数拦截器
        builder.addInterceptor(new HttpCommonInterceptor(HttpUtil.getHeaderParamsMap()));

        OkHttpClient okHttpClient = builder
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(HttpUtil.getSSLSocketFactory(), new CustomTrustManager())
                .hostnameVerifier(HttpUtil.getHostnameVerifier())
                .build();

        return okHttpClient;
    }


    @Provides
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ApiConstant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    @Provides
    HomeApi provideHomeApi(Retrofit retrofit) {
        return HomeApi.getInstance(retrofit.create(HomeApiService.class));
    }

    @Provides
    OrderApi provideOrderApi(Retrofit retrofit) {
        return OrderApi.getInstance(retrofit.create(OrderApiService.class));
    }

    @Provides
    FreightApi provideFreightApi(Retrofit retrofit){
        return FreightApi.getInstance(retrofit.create(FreightService.class));
    }

}
