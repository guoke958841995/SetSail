package com.sxhalo.PullCoal.ui;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.base.BasePresenter;
import com.sxhalo.PullCoal.bean.APPData;
import com.sxhalo.PullCoal.bean.AccessKeyBean;
import com.sxhalo.PullCoal.bean.AdvertisementBean;
import com.sxhalo.PullCoal.bean.Dictionary;
import com.sxhalo.PullCoal.bean.ProvinceModel;
import com.sxhalo.PullCoal.net.api.HomeApi;
import com.sxhalo.PullCoal.net.subscribers.ProgressObserver;
import com.sxhalo.PullCoal.net.subscribers.SubscriberOnNextListener;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * mvp模式中的 Presenter层,负责Module和View的通信
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public class SplashPresenter extends BasePresenter<SplashContract.View, BaseContract.BaseView> implements SplashContract.Presenter {

    private static final String TAG = "SplashPresenter";

    private HomeApi mHomeApi;

    @Inject
    public SplashPresenter(HomeApi homeApi) {
        this.mHomeApi = homeApi;
    }


    @Override
    public void getPublicKey(Activity context) {

        Type type = new TypeToken<AccessKeyBean>() {
        }.getType();

        mHomeApi.getPublicKey(type)
                .compose(mView.bindToLife())
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<AccessKeyBean>() {

                    @Override
                    protected void onNext(AccessKeyBean accessKeyBean) {
                        mView.savePublicKey(accessKeyBean, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.savePublicKey(null, e);
                    }
                }, context, false));


    }

    @Override
    public void getAdvertData(Activity activity, LinkedHashMap<String, String> params) {
        Type type = new TypeToken<APPData<AdvertisementBean>>() {
        }.getType();

        mHomeApi.getAdvertData(type, params)
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPData<AdvertisementBean>>() {

                    @Override
                    protected void onNext(APPData<AdvertisementBean> advertisementBeanAPPData) {
                        mView.loadAdverData(advertisementBeanAPPData, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.loadAdverData(null, e);
                    }
                }, activity, false)); //flag: false:不显示进度条
    }

    @Override
    public void getDictionary(Activity context) {
        Type type = new TypeToken<List<Dictionary>>() {
        }.getType();

        mHomeApi.getDictionary(type)
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<List<Dictionary>>() {
                    @Override
                    protected void onNext(List<Dictionary> list) {
                        mView.updateDictionary(list, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.updateDictionary(null, e);
                    }
                }, context, false));//flag: false:不显示进度条
    }

    @Override
    public void getRegionCode(Activity activity) {

        Type type = new TypeToken<List<ProvinceModel>>() {
        }.getType();

        mHomeApi.getRegionCode(type)
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<List<ProvinceModel>>() {

                    @Override
                    protected void onNext(List<ProvinceModel> provinceModels) {
                        mView.saveRegionCode(provinceModels, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.saveRegionCode(null, e);
                    }
                }, activity, false));//flag: false:不显示进度条
    }
}
