package com.sxhalo.PullCoal.ui.order.contract;

import android.app.Activity;

import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.Orders;
import com.sxhalo.PullCoal.bean.SendCarEntity;

import java.util.LinkedHashMap;

public interface SebdCarOrderContract {
    //派车单
    // 通知View更新UI
    interface View extends BaseContract.BaseView {

        void handlerRefreshSendCarOrderList(APPDataList<SendCarEntity> sendCarEntity, Throwable e);

        void handlerLoadMoreSendCarOrderList(APPDataList<SendCarEntity> sendCarEntity, Throwable e);
    }

    //调用网络接口
    interface Presenter extends BaseContract.BasePresenter<SebdCarOrderContract.View> {

        void getSendCarOrderList(Activity activity, LinkedHashMap<String, String> params, boolean isRefresh, boolean flag);

    }

}
