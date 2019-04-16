package com.sxhalo.PullCoal.ui.order.contract;

import android.app.Activity;

import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.TransportMode;

import java.util.LinkedHashMap;

public interface TransportOrderContract {

    interface View extends BaseContract.BaseView {

        void handlerRefreshTransportOrderList(APPDataList<TransportMode> sendCarEntitylist, Throwable e);

        void handlerLoadMoreTransportOrderList(APPDataList<TransportMode> sendCarEntitylist, Throwable e);
    }

    //调用网络接口
    interface Presenter extends BaseContract.BasePresenter<TransportOrderContract.View> {

        void getTransportOrderList(Activity activity, LinkedHashMap<String, String> params, boolean isRefresh, boolean flag);
    }


}
