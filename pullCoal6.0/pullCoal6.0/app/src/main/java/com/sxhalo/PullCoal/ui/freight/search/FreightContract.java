package com.sxhalo.PullCoal.ui.freight.search;

import android.app.Activity;

import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.bean.RouteEntity;
import com.sxhalo.PullCoal.bean.TransportMode;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *  货运 列表功能数据请求
 * Created by amoldZhang on 2019/4/12
 */
public interface FreightContract {

    interface View extends BaseContract.BaseView {
        void getCoalTransportList(List<TransportMode> transportModeList, Throwable e);
    }

     interface Presenter extends BaseContract.BasePresenter<View> {
        void getCoalTransportList(Activity activity, LinkedHashMap<String, String> mParams,boolean flag);
    }

}
