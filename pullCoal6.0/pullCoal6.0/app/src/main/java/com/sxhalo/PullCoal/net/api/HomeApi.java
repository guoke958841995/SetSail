package com.sxhalo.PullCoal.net.api;

import com.google.gson.Gson;
import com.sxhalo.PullCoal.bean.AccessKeyBean;
import com.sxhalo.PullCoal.net.HttpResultFunc;
import com.sxhalo.PullCoal.net.HttpUtil;
import com.sxhalo.PullCoal.net.ResponseDecryptFuncation;
import com.sxhalo.PullCoal.net.RxSchedulers;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
public class HomeApi {

    private HomeApiService mHomeApiService;

    public static HomeApi mInstance;

    //接口参数加密[默认加密,返回数据解密]
    private boolean ifEncryption = true;

    private HomeApi(HomeApiService homeApiService) {
        this.mHomeApiService = homeApiService;
    }

    public static HomeApi getInstance(HomeApiService homeApiService) {
        if (mInstance == null) {
            synchronized (HomeApi.class) {
                if (mInstance == null) {
                    mInstance = new HomeApi(homeApiService);
                }
            }
        }
        return mInstance;
    }


    /**
     * 获取公共的key
     */
    public Observable<AccessKeyBean> getPublicKey(Type type) {

        Observable observable = mHomeApiService.getPublicKey()
                .compose(RxSchedulers.applySchedulers())
                .map(new Function<ResponseBody, AccessKeyBean>() {
                    @Override
                    public AccessKeyBean apply(ResponseBody responseBody) throws Exception {
                        String result = responseBody.string();
                        AccessKeyBean bean = new Gson().fromJson(result, type);
                        return bean;
                    }
                });

        return observable;
    }


    /**
     * 获取广告数据
     */
    public Observable getAdvertData(Type type, LinkedHashMap<String, String> params) {

        //对接口参数进行加密
        LinkedHashMap<String, String> map = HttpUtil.setParameterEncryption(params);

        Observable observable = mHomeApiService.getAdvertisement(map)
                .compose(RxSchedulers.applySchedulers())
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body

        return observable;
    }

    /**
     * 获取数据字典
     */
    public Observable getDictionary(Type type) {

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //对接口参数进行加密
        LinkedHashMap<String, String> params = HttpUtil.setParameterEncryption(map);

        Observable observable = mHomeApiService.getDictionary(params)
                .compose(RxSchedulers.applySchedulers())    //线程切换
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body

        return observable;
    }

    /**
     * 获取行政区划数据
     */
    public Observable getRegionCode(Type type) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //对接口参数进行加密
        LinkedHashMap<String, String> params = HttpUtil.setParameterEncryption(map);

        Observable observable = mHomeApiService.getRegionCode(params)
                .compose(RxSchedulers.applySchedulers())
                .map(new ResponseDecryptFuncation(ifEncryption))    //对返回数据解密
                .map(new HttpResultFunc(type));     //过滤头部,只取body

        return observable;
    }

}
