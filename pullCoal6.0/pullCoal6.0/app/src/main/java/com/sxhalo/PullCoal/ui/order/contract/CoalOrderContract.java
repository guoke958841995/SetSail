package com.sxhalo.PullCoal.ui.order.contract;

import android.app.Activity;

import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.Orders;

import java.util.LinkedHashMap;

/**
 * 契约类
 */
public interface CoalOrderContract {

    // 通知View更新UI
    interface View extends BaseContract.BaseView {
        void handlerRefreshCoalOrderList(APPDataList<Orders> ordersAPPDataList, Throwable e);

        void handlerLoadMoreCoalOrderList(APPDataList<Orders> ordersAPPDataList, Throwable e);
    }

    //调用网络接口
    interface Presenter extends BaseContract.BasePresenter<View> {

        void getCoalOrderList(Activity activity, LinkedHashMap<String, String> params, boolean isRefresh, boolean flag);

    }
}
