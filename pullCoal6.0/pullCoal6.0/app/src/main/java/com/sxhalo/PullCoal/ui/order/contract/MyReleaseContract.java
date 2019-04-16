package com.sxhalo.PullCoal.ui.order.contract;

import android.app.Activity;

import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.SendCarEntity;
import com.sxhalo.PullCoal.bean.UserDemand;

import java.util.LinkedHashMap;

public interface MyReleaseContract {


    //派车单
    // 通知View更新UI
    interface View extends BaseContract.BaseView {

        void handlerRefreshUserDemandList(APPDataList<UserDemand> userDemandlist, Throwable e);

        void handlerLoadMoreUserDemandList(APPDataList<UserDemand> userDemandlist, Throwable e);

    }

    //调用网络接口
    interface Presenter extends BaseContract.BasePresenter<MyReleaseContract.View> {

        void getMyReleaseOrderList(Activity activity, LinkedHashMap<String, String> params, boolean isRefresh, boolean flag);

    }
}
