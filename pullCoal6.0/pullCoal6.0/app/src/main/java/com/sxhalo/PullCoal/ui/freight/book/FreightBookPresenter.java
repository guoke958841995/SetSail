package com.sxhalo.PullCoal.ui.freight.book;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.base.BasePresenter;
import com.sxhalo.PullCoal.bean.APPData;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.RouteEntity;
import com.sxhalo.PullCoal.bean.TransportMode;
import com.sxhalo.PullCoal.net.api.FreightApi;
import com.sxhalo.PullCoal.net.subscribers.ProgressObserver;
import com.sxhalo.PullCoal.net.subscribers.SubscriberOnNextListener;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

/**
 *  货运相关联网处理
 * Created by amoldZhang on 2019/4/12
 */
public class FreightBookPresenter extends BasePresenter<FreightBookContract.View> implements FreightBookContract.Presenter{

    private static final String TAG = "SplashPresenter";

    private FreightApi freightApi;

    @Inject
    public FreightBookPresenter(FreightApi freightApi) {
        this.freightApi = freightApi;
    }

    @Override
    public void getTransportSubscribeCreate(Activity activity, LinkedHashMap<String, String> mParams, boolean flag) {
        Type type = new TypeToken<APPData<RouteEntity>>() {}.getType();
        freightApi.getTransportSubscribeCreate(type,mParams)
                .compose(mView.bindToLife())
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPData<RouteEntity>>() {

                    @Override
                    protected void onNext(APPData<RouteEntity> data) {
                        mView.getTransportSubscribeCreate(data.getEntity(), null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.getTransportSubscribeCreate(null, e);
                    }
                }, activity, flag));
    }

    @Override
    public void getTransportSubscribeDelete(Activity activity, LinkedHashMap<String, String> mParams, boolean flag) {
        Type type = new TypeToken<APPData>() {}.getType();
        freightApi.getTransportSubscribeDelete(type,mParams)
                .compose(mView.bindToLife())
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPData>() {

                    @Override
                    protected void onNext(APPData data) {
                        mView.getTransportSubscribeDelete(data.getMessage(), null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.getTransportSubscribeDelete(null, e);
                    }
                }, activity, flag));
    }

    @Override
    public void getTransportSubscribeList(Activity activity, LinkedHashMap<String, String> mParams, boolean flag) {
        Type type = new TypeToken<List<APPDataList<RouteEntity>>>() {}.getType();
        freightApi.getTransportSubscribeList(type,mParams)
                .compose(mView.bindToLife())
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<List<APPDataList<RouteEntity>>>() {

                    @Override
                    protected void onNext(List<APPDataList<RouteEntity>> data) {
                        mView.getTransportSubscribeList(data, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.getTransportSubscribeList(null, e);
                    }
                }, activity, flag));
    }
}
