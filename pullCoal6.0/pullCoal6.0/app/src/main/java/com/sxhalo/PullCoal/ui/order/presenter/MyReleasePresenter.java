package com.sxhalo.PullCoal.ui.order.presenter;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.base.BasePresenter;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.UserDemand;
import com.sxhalo.PullCoal.net.api.OrderApi;
import com.sxhalo.PullCoal.net.subscribers.ProgressObserver;
import com.sxhalo.PullCoal.net.subscribers.SubscriberOnNextListener;
import com.sxhalo.PullCoal.ui.order.contract.MyReleaseContract;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import javax.inject.Inject;

public class MyReleasePresenter extends BasePresenter<MyReleaseContract.View, BaseContract.BaseView>  implements  MyReleaseContract.Presenter{

    private OrderApi mOrderApi;
    @Inject
    public  MyReleasePresenter(OrderApi orderApi){

        this.mOrderApi = orderApi;
    }

    @Override
    public void getMyReleaseOrderList(Activity activity, LinkedHashMap<String, String> params, boolean isRefresh, boolean flag) {
        Type type = new TypeToken<APPDataList<UserDemand>>() {
        }.getType();
        mOrderApi.getUserDemandlist(type,params)
                .subscribe(new ProgressObserver(new SubscriberOnNextListener<APPDataList<UserDemand>>() {

                    @Override
                    protected void onNext(APPDataList<UserDemand> userDemandAPPDataList) {
                        if (isRefresh) {
                            mView.handlerRefreshUserDemandList(userDemandAPPDataList, null);
                        } else {
                            mView.handlerLoadMoreUserDemandList(userDemandAPPDataList, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isRefresh) {
                            mView.handlerRefreshUserDemandList(null, e);
                        } else {
                            mView.handlerLoadMoreUserDemandList(null, e);
                        }
                        e.printStackTrace();
                    }
                }, activity, flag));
    }

}
