package com.sxhalo.PullCoal.ui.order.presenter;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.base.BasePresenter;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.SendCarEntity;
import com.sxhalo.PullCoal.net.api.OrderApi;
import com.sxhalo.PullCoal.net.subscribers.ProgressObserver;
import com.sxhalo.PullCoal.net.subscribers.SubscriberOnNextListener;
import com.sxhalo.PullCoal.ui.order.contract.SebdCarOrderContract;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import javax.inject.Inject;

public class SebdCarOrderPresenter extends BasePresenter<SebdCarOrderContract.View, BaseContract.BaseView> implements SebdCarOrderContract.Presenter {

    private OrderApi mOrderApi;

    @Inject
    public SebdCarOrderPresenter(OrderApi orderApi) {

        this.mOrderApi = orderApi;
    }

    @Override
    public void getSendCarOrderList(Activity activity, LinkedHashMap<String, String> params, boolean isRefresh, boolean flag) {

        Type type = new TypeToken<APPDataList<SendCarEntity>>() {
        }.getType();

        mOrderApi.getSendCarOrderlist(type, params)
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPDataList<SendCarEntity>>() {

                    @Override
                    protected void onNext(APPDataList<SendCarEntity> sendCarEntityAPPDataList) {
                        if (isRefresh) {
                            mView.handlerRefreshSendCarOrderList(sendCarEntityAPPDataList, null);
                        } else {
                            mView.handlerLoadMoreSendCarOrderList(sendCarEntityAPPDataList, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isRefresh) {
                            mView.handlerRefreshSendCarOrderList(null, e);
                        } else {
                            mView.handlerLoadMoreSendCarOrderList(null, e);
                        }
                        e.printStackTrace();
                    }
                }, activity, flag));


    }


}
