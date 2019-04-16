package com.sxhalo.PullCoal.ui.order.presenter;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.base.BasePresenter;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.Orders;
import com.sxhalo.PullCoal.net.api.OrderApi;
import com.sxhalo.PullCoal.net.subscribers.ProgressObserver;
import com.sxhalo.PullCoal.net.subscribers.SubscriberOnNextListener;
import com.sxhalo.PullCoal.ui.order.contract.CoalOrderContract;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import javax.inject.Inject;

public class CoalOrderPresenter extends BasePresenter<CoalOrderContract.View, BaseContract.BaseView> implements CoalOrderContract.Presenter {

    private static final String TAG = "CoalOrderPresenter";

    private OrderApi mOrderApi;

    @Inject
    public CoalOrderPresenter(OrderApi orderApi) {
        this.mOrderApi = orderApi;
    }

    @Override
    public void getCoalOrderList(Activity activity, LinkedHashMap<String, String> params, final boolean isRefresh, boolean flag) {

        Type type = new TypeToken<APPDataList<Orders>>() {
        }.getType();

        mOrderApi.getCoalOrderList(type, params)
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPDataList<Orders>>() {


                    @Override
                    protected void onNext(APPDataList<Orders> ordersAPPDataList) {

                        if (isRefresh) {
                            mView.handlerRefreshCoalOrderList(ordersAPPDataList, null);
                        } else {
                            mView.handlerLoadMoreCoalOrderList(ordersAPPDataList, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isRefresh) {
                            mView.handlerRefreshCoalOrderList(null, e);
                        } else {
                            mView.handlerLoadMoreCoalOrderList(null, e);
                        }
                        e.printStackTrace();

                    }
                }, activity, flag)); //flag 是否显示进度条
    }


}
