package com.sxhalo.PullCoal.ui.freight.book;

import android.app.Activity;

import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.bean.APPData;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.RouteEntity;
import com.sxhalo.PullCoal.bean.TransportMode;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *  货运 列表功能数据请求
 * Created by amoldZhang on 2019/4/12
 */
public interface FreightBookContract {

    interface View extends BaseContract.BaseView {
        void getTransportSubscribeCreate(RouteEntity routeEntity, Throwable e);
        void getTransportSubscribeList(List<APPDataList<RouteEntity>> appDataListList, Throwable e);
        void getTransportSubscribeDelete(String message, Throwable e);
    }

     interface Presenter extends BaseContract.BasePresenter<View> {
        void getTransportSubscribeCreate(Activity activity, LinkedHashMap<String, String> mParams, boolean flag);
        void getTransportSubscribeList(Activity activity, LinkedHashMap<String, String> mParams, boolean flag);
        void getTransportSubscribeDelete(Activity activity, LinkedHashMap<String, String> mParams, boolean flag);
    }

}
