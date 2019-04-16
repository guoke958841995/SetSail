package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import com.google.gson.Gson;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.model.AdvertisementEntity;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.AppPublic;
import com.sxhalo.PullCoal.model.ProvinceModel;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.retrofithttp.api.RSAEncrypt;
import com.sxhalo.PullCoal.tools.FileUtils;
import com.sxhalo.PullCoal.tools.jpush.SettingJPush;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import net.sf.json.JSONString;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static com.sxhalo.PullCoal.common.MyAppLication.getAlias;

public class SplashActivity extends BaseActivity {

    private String userId;
    private boolean isFirst;
    private APPData<AdvertisementEntity> advertisement;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        SharedTools.putIntValue(this,"ifUseIp",0);

        //当从快没宝打开拉煤宝时，保持登录状态
        String userId = getIntent().getStringExtra("userId") == null ? "-1" : getIntent().getStringExtra("userId");
        if (!userId.equals("-1")){
            SharedTools.putStringValue(this,"userId",userId);
            getPersonal();
        }
        // 用户第一次使用
        isFirst = SharedTools.getBooleanValue(this, "first-time-use", true);
        //将用户选择的货运筛选条件还原
        SharedTools.putStringValue(this,"select_city","全国");
        SharedTools.putStringValue(this,"select_code","");
    }

    /**
     * 挡在登陆状态下联网获取当前用户状态，并更新数据库
     */
    private void getPersonal() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this, params).getUserInfo(new DataUtils.DataBack<UserEntity>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        UserEntity users = dataMemager;
                        SharedTools.putBooleanValue(mContext, "isWXBind", StringUtils.isEmpty(users.getWeChatCredential())?false :true);
                        UIHelper.saveUserData(mContext,dataMemager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网获取当前用户状态", e.toString());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setStatusBar(0,-1);
        //判断是否有焦点
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void initTitle() {
    }

    @Override
    protected void getData() {
        new DataUtils(this).getPublicAccessKey(new DataUtils.DataBack<AppPublic>() {
            @Override
            public void getData(AppPublic data) {
                String secretKey = data.getSecretKey();
                String accessKey = data.getAccessKey();
                GHLog.e("secretKey", "secretKey==" + secretKey);
                GHLog.e("accessKey", "accessKey==" + accessKey);
                SharedTools.putStringValue(mContext,"secretKey",secretKey);
                SharedTools.putStringValue(mContext,"accessKey",accessKey);
                getAdData();
            }

            @Override
            public void getError(Throwable e) {
                GHLog.e("app初始化失败",e.toString());
                if (!"115".equals( APIConfig.API_OPERATION_MODE )){
                    shwoDialog(e.getMessage() == null?"字典数据接口连接异常":e.getMessage());
                }
//                SharedTools.putStringValue(mContext,"secretKey","FC72E7899C804EEC");
//                SharedTools.putStringValue(mContext,"accessKey","ea1d786f-3324-464f-8185-7d165d1432dc");
//                getAdData();
            }
        });
    }

    /**
     * 获取广告数据
     */
    private void getAdData() {
        try {
            LinkedHashMap<String, String> myParams = new LinkedHashMap<>();
            myParams.put("adCotegoryCode","lameibao");
            new DataUtils(this,myParams).getAdvertisement(new DataUtils.DataBack<APPData<AdvertisementEntity>>() {
                @Override
                public void getData(APPData<AdvertisementEntity> data) {
                    try {
                        advertisement = data;
                        getInItData();
                    } catch (Exception e) {
                        goToNext(null);
                    }
                }

                @Override
                public void getError(Throwable e) {
                    getInItData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToNext(final AdvertisementEntity entity) {
        userId = SharedTools.getStringValue(this, "userId", "-1");
        if (!userId.equals("-1")) {
            try {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                new DataUtils(this, params).getUserInfo(new DataUtils.DataBack<UserEntity>() {
                    @Override
                    public void getData(UserEntity dataMemager) {
                        try {
                            if (dataMemager == null) {
                                return;
                            }
                            setAlias(dataMemager.getUserId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirst) {
                    //第一次使用app 打开引导页面
                    UIHelper.jumpAct(SplashActivity.this, GuideActivity.class, entity, true);
                } else {
                    if (entity == null) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        if(getIntent().getParcelableExtra("intent") != null){
                            intent.putExtra("intent",
                                    getIntent().getParcelableExtra("intent"));
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        UIHelper.jumpAct(SplashActivity.this, AdvertisementActivity.class, entity, true);
                    }
                }
            }
        }, 500);
    }

    /**
     * 极光注册
     */
    private void setAlias(String userId) {
        String alias = userId;
        SettingJPush jspush = getAlias();
        jspush.setAlias(alias);
    }

    public void getInItData() {
        try {
            new DataUtils(this).getDictionary(new DataUtils.DataBack<List<Dictionary>>() {
                @Override
                public void getData(List<Dictionary> dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
//                        List<Dictionary> dictionaryList = OrmLiteDBUtil.getQueryAll(Dictionary.class);
//                        Dictionary sys100001 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100001"}).get(0);
//                        Dictionary sys100002 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100002"}).get(0);
//                        Dictionary sys100003 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100003"}).get(0);
//                        Dictionary sys100004 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100004"}).get(0);

                        try{
                            if (OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100001"}).size() != 0){
                                OrmLiteDBUtil.updateALL(dataList);
                            }else{
                                OrmLiteDBUtil.deleteAll(Dictionary.class);
                                OrmLiteDBUtil.insertAll(dataList);
                            }
                        }catch (Exception e){
                        }
                        int regionVersion = SharedTools.getIntValue(mContext, "region_version", -1);
                        int dictValue = -1;
                        for(int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).dataType.equals("sys100009")) {
                                for (int j = 0; j < dataList.get(i).list.size(); j++) {
                                    if (dataList.get(i).list.get(j).dictCode.equals("region_version")) {
                                        dictValue =  Integer.valueOf(dataList.get(i).list.get(j).dictValue);
                                        break;
                                    }
                                }
                            }
                        }
                        if (regionVersion == -1 || dictValue > regionVersion) {
                            //sp文件中没有行政区划版本号，或者服务器有更新内容，
                            //将新版本号存入sp文件并获取最新内容写入本地保存
                            SharedTools.putIntValue(mContext, "region_version", dictValue);
                            //调服务器接口获取行政区划json串  GET_REGION_CODE
                            getRegionCode();
                        }
//                        getAdData();
                        if (advertisement == null || advertisement.getList().size() == 0 || advertisement.getList().get(0) == null || StringUtils.isEmpty(advertisement.getList().get(0).getAdPicUrl())) {
                            //没有广告
                            goToNext(null);
                        } else {
                            //有广告
                            goToNext(advertisement.getList().get(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2000001".equals(e.getMessage())) {
                        shwoDialog("请到拉煤宝后台设置accessKey后再试");
                    } else {
                            if (!"115".equals( APIConfig.API_OPERATION_MODE )){
                                shwoDialog(e.getMessage() == null?"字典数据接口连接异常":e.getMessage());
                            }else{
                                if (!isFirst) {
                                    getAdData();
                                }else{
                                    displayToast("网络连接失败，请稍后再试！");
                                }
                            }
                    }
                    GHLog.e("联网失败", e.toString());
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }


    /**
     * 获取行政区数据
     * **/
    private void getRegionCode() {
        try {
            new DataUtils(this).getRegionCode(new DataUtils.DataBack<List<ProvinceModel>>() {
                @Override
                public void getData(List<ProvinceModel> dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
                        //存入本地文件
                        String JsonString = new Gson().toJson(dataList);
                        FileUtils.saveDataToFile(mContext, JsonString, "region_code");
                    } catch (Exception e) {
                        GHLog.e("地址存入异常",e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    GHLog.e("联网失败", e.toString());
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }


    }

    private void shwoDialog(String message) {
        new RLAlertDialog(this, "系统提示", message, "退出", "再次尝试",
                new RLAlertDialog.Listener() {
                    @Override
                    public void onLeftClick() {
                        AppManager.getAppManager().AppExit(getApplicationContext());
                    }

                    @Override
                    public void onRightClick() {
                        getInItData();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        MyAppLication.app.dismissDelog();
        super.onDestroy();
    }
}
