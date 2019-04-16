package com.sxhalo.PullCoal.ui.freight.search;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.base.BasePresenter;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.TransportMode;
import com.sxhalo.PullCoal.net.api.FreightApi;
import com.sxhalo.PullCoal.net.subscribers.ProgressObserver;
import com.sxhalo.PullCoal.net.subscribers.SubscriberOnNextListener;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import javax.inject.Inject;

/**
 *  货运相关联网处理
 * Created by amoldZhang on 2019/4/12
 */
public class FreightPresenter extends BasePresenter<FreightContract.View> implements FreightContract.Presenter{

    private static final String TAG = "SplashPresenter";

    private FreightApi freightApi;

    @Inject
    public FreightPresenter(FreightApi freightApi) {
        this.freightApi = freightApi;
    }

    @Override
    public void getCoalTransportList(Activity activity, LinkedHashMap<String, String> mParams,boolean flag) {
        Type type = new TypeToken<APPDataList<TransportMode>>() {}.getType();

        freightApi.getCoalTransportList(type,mParams)
                .compose(mView.bindToLife())
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPDataList<TransportMode>>() {

                    @Override
                    protected void onNext(APPDataList<TransportMode> dataList) {
                        mView.getCoalTransportList(dataList.getList(), null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.getCoalTransportList(null, e);
                    }
                }, activity, flag));
    }

}
