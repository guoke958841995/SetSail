package com.sxhalo.PullCoal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.bean.APPData;
import com.sxhalo.PullCoal.bean.AccessKeyBean;
import com.sxhalo.PullCoal.bean.AdvertisementBean;
import com.sxhalo.PullCoal.bean.Dictionary;
import com.sxhalo.PullCoal.bean.ProvinceModel;
import com.sxhalo.PullCoal.common.ApiConstant;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.common.MyApplication;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.utils.FileUtils;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.RSAEncrypt;
import com.sxhalo.PullCoal.weight.dialog.RLAlertDialog;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 启动页面
 *
 * @author Xiao_
 * @date 2019/4/1 0001.
 */
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

    private static final String TAG = "SplashActivity";
    // 用户第一次使用
    private Boolean isFirstUse;
    private APPData<AdvertisementBean> adverData;
    private String userId;

    @Override
    public int getContentLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {

        isFirstUse = PaperUtil.get(AppConstant.FIRST_USE_KEY, true);

    }

    /**
     * 1.getPublicKey   //公共key
     * 2.getAdvertData  //广告数据
     * 3.getDictionary  //字典数据
     * getRegionCode   //获取行政区数据
     * 4.getUserInfo    //个人数据
     */
    @Override
    public void initData() {
        // 获取公共key
        LogUtil.i(TAG, "获取公共key");
        mPresenter.getPublicKey(this);
    }


    @Override
    public void savePublicKey(AccessKeyBean accessKeyBean, Throwable e) {
        if (accessKeyBean != null) {
            String publicKey = accessKeyBean.getPublicKey();
            PaperUtil.put(AppConstant.PUBLIC_KEY, publicKey);

            String secretData = accessKeyBean.getSecretKey();

            if (!TextUtils.isEmpty(publicKey) && !TextUtils.isEmpty(secretData)) {
                String secretKey = RSAEncrypt.decryptByPublicKey(secretData, publicKey);
                LogUtil.i(TAG, "secretKey: " + secretKey);
                PaperUtil.put(AppConstant.SECRET_KEY, secretKey);
            }

            String accessData = accessKeyBean.getAccessKey();
            if (!TextUtils.isEmpty(publicKey) && !TextUtils.isEmpty(accessData)) {
                String accessKey = RSAEncrypt.decryptByPublicKey(accessData, publicKey);
                LogUtil.i(TAG, "accessKey: " + accessKey);
                PaperUtil.put(AppConstant.ACCESS_KEY, accessKey);
            }
        } else if (e != null) {
            // 获取公共key异常
            LogUtil.e(TAG, "获取公共key异常" + e.getMessage());
        }
        //
        getAdvertData();
    }

    /**
     * 跳转主页,关掉当前页面
     */
    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 获取广告数据
     */
    private void getAdvertData() {
        LogUtil.i(TAG, "获取广告数据");
        LinkedHashMap<String, String> mParams = new LinkedHashMap<>();
        mParams.put("adCotegoryCode", "lameibao");
        mPresenter.getAdvertData(this, mParams);
    }

    /**
     * 加载广告
     */
    @Override
    public void loadAdverData(APPData<AdvertisementBean> advertisementBeanAPPData, Throwable e) {

        if (advertisementBeanAPPData != null) {
            adverData = advertisementBeanAPPData;
        } else if (e != null) {
            // 广告数据异常
            LogUtil.e(TAG, "获取广告数据异常" + e.getMessage());
        }
        getDictionary();
    }

    /**
     * 获取数据字典
     */
    private void getDictionary() {
        LogUtil.i(TAG, "获取数据字典");
        mPresenter.getDictionary(this);
    }

    /**
     * 更新数据字典
     *
     * @param dictionaryList
     */
    @Override
    public void updateDictionary(List<Dictionary> dictionaryList, Throwable e) {
        if (dictionaryList != null) {
            try {
                if (OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type",
                        new String[]{"sys100001"}).size() != 0) {

                    OrmLiteDBUtil.updateALL(dictionaryList);
                } else {
                    OrmLiteDBUtil.deleteAll(Dictionary.class);
                    OrmLiteDBUtil.insertAll(dictionaryList);
                }
            } catch (Exception exception) {
                LogUtil.e(TAG, exception.getMessage());
            }

            int regionVersion = PaperUtil.get(AppConstant.REGION_VERSION_KEY, -1);
            int dictValue = -1;
            for (int i = 0; i < dictionaryList.size(); i++) {
                if (dictionaryList.get(i).dataType.equals("sys100009")) {
                    for (int j = 0; j < dictionaryList.get(i).list.size(); j++) {
                        if (dictionaryList.get(i).list.get(j).dictCode.equals("region_version")) {
                            dictValue = Integer.valueOf(dictionaryList.get(i).list.get(j).dictValue);
                            break;
                        }
                    }
                }
            }

            if (regionVersion == -1 || dictValue > regionVersion) {
                //本地文件中没有行政区划版本号，或者服务器有更新内容，
                //将新版本号存入本地文件并获取最新内容写入本地保存
                PaperUtil.put(AppConstant.REGION_VERSION_KEY, dictValue);
                //调服务器接口获取行政区划json串  GET_REGION_CODE
                getRegionCode();
            }

            if (adverData != null && adverData.getList().size() > 0) {
                //有广告
                AdvertisementBean advertisementBean = adverData.getList().get(0);
                toNext(advertisementBean);
            } else {
                //没有广告
                AdvertisementBean advertisementBean = new AdvertisementBean();
                advertisementBean.setAdUrl("http://www.baidu.com");
                advertisementBean.setAdPicUrl("http://cn.bing.com/th?id=OHR.BigWindDay_ROW3524994269_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg");
                toNext(advertisementBean);
            }
        } else if (e != null) {
            // 数据字典异常
            LogUtil.e(TAG, "获取数据字典异常" + e.getMessage());
            if ("2000001".equals(e.getMessage())) {
                shwoDialog("请到拉煤宝后台设置accessKey后再试");
            } else {
                if (!"115".equals(ApiConstant.API_OPERATION_MODE)) {
                    shwoDialog(e.getMessage() == null ? "字典数据接口连接异常" : e.getMessage());
                } else {
                    if (!isFirstUse) {
                        getAdvertData();
                    } else {
                        displayToast("网络连接失败，请稍后再试！");
                    }
                }
            }
        }
    }

    /**
     * 获取行政区划数据
     */
    private void getRegionCode() {
        LogUtil.i(TAG, "获取行政区划数据");
        mPresenter.getRegionCode(this);
    }

    @Override
    public void saveRegionCode(List<ProvinceModel> provinceModelList, Throwable e) {
        if (provinceModelList != null) {
            //存入本地文件
            String JsonString = new Gson().toJson(provinceModelList);
            FileUtils.saveDataToFile(this, JsonString, AppConstant.REGION_CODE_FILE_NAME);
        } else if (e != null) {
            // 行政区划数据异常
            LogUtil.e(TAG, "获取行政区划数据异常:" + e.getMessage());
        }
    }

    private void toNext(AdvertisementBean advertisementBean) {
        userId = PaperUtil.get(AppConstant.USER_ID_KEY, "-1");
        if (!"-1".equals(userId)) {
            // TODO getUserInfo 获取用户信息
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstUse) {
                    //第一次使用APP,进入引导页面
                    LogUtil.i(TAG, "跳转至引导页面");
                    toGuideActivity(advertisementBean);
                } else {
                    //
                    if (advertisementBean == null) {
                        //没有广告
                        LogUtil.i(TAG, "跳转至主页");
                        toMainActivity();
                    } else {
                        //有广告
                        LogUtil.i(TAG, "跳转至广告页");
                        toAdvertActivity(advertisementBean);
                    }
                }
            }
        }, 500);
    }

    /**
     * 跳转至引导页面
     */
    private void toGuideActivity(AdvertisementBean advertisementBean) {
        Intent intent = new Intent(this, GuideActivity.class);
        intent.putExtra("advertisement_data", advertisementBean);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转至广告页面
     */
    private void toAdvertActivity(AdvertisementBean advertisementBean) {
        Intent intent = new Intent(this, AdvertisementActivity.class);
        intent.putExtra("advertisement_data", advertisementBean);
        startActivity(intent);
        finish();

    }

    private void shwoDialog(String message) {
        new RLAlertDialog(this, "系统提示", message, "退出", "再次尝试",
                new RLAlertDialog.Listener() {
                    @Override
                    public void onLeftClick() {
                        //退出
                        MyApplication.getInstance().exits();
                    }

                    @Override
                    public void onRightClick() {
                        //获取字典数据
                        getDictionary();
                    }
                }).show();
    }

}
