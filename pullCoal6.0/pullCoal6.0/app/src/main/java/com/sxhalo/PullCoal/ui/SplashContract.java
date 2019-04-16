package com.sxhalo.PullCoal.ui;

import android.app.Activity;

import com.sxhalo.PullCoal.base.BaseContract;
import com.sxhalo.PullCoal.bean.APPData;
import com.sxhalo.PullCoal.bean.AccessKeyBean;
import com.sxhalo.PullCoal.bean.AdvertisementBean;
import com.sxhalo.PullCoal.bean.Dictionary;
import com.sxhalo.PullCoal.bean.ProvinceModel;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * mvp模式的契约类
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public interface SplashContract {

    interface View extends BaseContract.BaseView {
        void savePublicKey(AccessKeyBean accessKeyBean, Throwable e);

        void loadAdverData(APPData<AdvertisementBean> advertisementBeanAPPData, Throwable e);

        void updateDictionary(List<Dictionary> dictionaryList, Throwable e);

        void saveRegionCode(List<ProvinceModel> provinceModelList, Throwable e);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getPublicKey(Activity activity);

        void getAdvertData(Activity activity, LinkedHashMap<String, String> mParams);

        void getDictionary(Activity activity);

        void getRegionCode(Activity activity);

    }

}
