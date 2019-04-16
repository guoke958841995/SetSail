package com.sxhalo.PullCoal.ui.order.presenter;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.base.BasePresenter;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.TransportMode;
import com.sxhalo.PullCoal.net.api.OrderApi;
import com.sxhalo.PullCoal.net.subscribers.ProgressObserver;
import com.sxhalo.PullCoal.net.subscribers.SubscriberOnNextListener;
import com.sxhalo.PullCoal.ui.order.contract.TransportOrderContract;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import javax.inject.Inject;

public class TransportOrderPresenter extends BasePresenter<TransportOrderContract.View, BaseContract.BaseView> implements TransportOrderContract.Presenter {

    private OrderApi mOrderApi;

    @Inject
    public TransportOrderPresenter(OrderApi orderApi) {

        this.mOrderApi = orderApi;
    }

    @Override
    public void getTransportOrderList(Activity activity, LinkedHashMap<String, String> params, boolean isRefresh, boolean flag) {
        Type type = new TypeToken<APPDataList<TransportMode>>() {
        }.getType();
        mOrderApi.getTransportOrderList(type,params)
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPDataList<TransportMode>>() {

                    @Override
                    protected void onNext(APPDataList<TransportMode> transportModeAPPDataList) {
                        if (isRefresh) {
                            mView.handlerRefreshTransportOrderList(transportModeAPPDataList, null);
                        } else {
                            mView.handlerLoadMoreTransportOrderList(transportModeAPPDataList, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isRefresh) {
                            mView.handlerRefreshTransportOrderList(null, e);
                        } else {
                            mView.handlerLoadMoreTransportOrderList(null, e);
                        }
                        e.printStackTrace();
                    }
                },activity,flag));
    }


}
