package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.User;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.model.UpdateApp;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.DataCleanManager;
import com.sxhalo.PullCoal.tools.VersionManagementUtil;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.TowButtonDialog;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;
import ch.ielse.view.SwitchView;
import cn.jpush.android.api.JPushInterface;

import static com.sxhalo.PullCoal.common.base.Constant.ACCOUNT_CONFLICT;

/**
 * 设置界面
 * Created by amoldZhang on 2017/1/10.
 */
public class SettingActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_cache_number)
    TextView tvCacheNumber;
    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Bind(R.id.view_line)
    View viewLine;
    @Bind(R.id.layout_logout)
    RelativeLayout layoutLogout;
    @Bind(R.id.switchView)
    SwitchView switchView;

    private String userId;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initTitle() {
        title.setText("设   置");
        if (!"-1".equals(SharedTools.getStringValue(this, "userId", "-1"))) {
            layoutLogout.setVisibility(View.VISIBLE);
            viewLine.setVisibility(View.VISIBLE);
        }
        caculateCacheSize();
        try {
            tvVersion.setText(BaseUtils.getVersionName(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void getData() {
        caculateCacheSize();
        userId = SharedTools.getStringValue(SettingActivity.this,"userId","-1");
        if ("-1".equals(userId)){
            userId = "";
        }
        boolean isOpened = SharedTools.getBooleanValue(SettingActivity.this,userId +"if_accept_push",true);
        switchView.toggleSwitch(isOpened);
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             boolean isOpened = switchView.isOpened();
             SharedTools.putBooleanValue(SettingActivity.this,userId + "if_accept_push",isOpened);
            }
        });
        switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true); // or false
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false); // or true
            }
        });

    }

    @OnClick({R.id.title_bar_left, R.id.version_number, R.id.clear_cache, R.id.layout_logout, R.id.layout_help, R.id.layout_about,R.id.layout_offline_map})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.version_number:  // 检测更新
                updateApp();
                break;
            case R.id.clear_cache:
                onClickCleanCache();
                break;
            case R.id.layout_offline_map: //离线地图
                UIHelper.jumpAct(this, OfflineMapActivity.class,false);//高德demo离线地图
                break;
            case R.id.layout_about://关于拉煤宝
                UIHelper.jumpAct(this, AboutUsActivity.class,false);
                break;
            case R.id.layout_help://常见问题帮助
                String URL = new Config().getCOMMON_PROBLEM();
                UIHelper.showWEB(this,URL,"常见问题帮助");
                break;
            case R.id.layout_logout://退出登录
                showDaiLog(mContext, getString(R.string.logout_tips));
                break;

        }
    }

    private void showDaiLog(Activity mActivity, String text){
        new TowButtonDialog(mActivity, text, "取消",
                "确定", new TowButtonDialog.Listener() {
            @Override
            public void onLeftClick() {
            }
            @Override
            public void onRightClick() {
                cancelLogin();
            }
        }).show();
    }

    private void cancelLogin() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this,params).getUserLogout(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String dataMemager) {
                    if (StringUtils.isEmpty(dataMemager)){
                        return;
                    }
                    displayToast(dataMemager);
                    logOut();
                    //用户注销成功 刷新相关页面数据
                    Intent intent = new Intent(Constant.REFRESH_CODE + "");
                    sendBroadcast(intent);
                }

                @Override
                public void getError(Throwable e) {
                    displayToast(getString(R.string.logout_success));
                    logOut();
                    //用户用户注销成功 刷新相关页面数据
                    Intent intent = new Intent(Constant.REFRESH_CODE + "");
                    sendBroadcast(intent);
                }
            });

        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    private void logOut(){
        //注销绑定用户极光
        JPushInterface.setAlias(this, "", null);
        UIHelper.cleanUserData(this);
        OrmLiteDBUtil.deleteAll(User.class);
        Intent hideIntent = new Intent(ACCOUNT_CONFLICT);
        Intent refreshIntent = new Intent(HomePagerFragment.RECEIVED_ACTION);
        sendBroadcast(hideIntent);
        sendBroadcast(refreshIntent);
        finish();
    }


    private void updateApp() {
        try {
            new DataUtils(this).getAppVesion(new DataUtils.DataBack<UpdateApp>() {
                @Override
                public void getData(UpdateApp data) {
                    if (data != null) {
                        String currentVersion = VersionManagementUtil.getVersion(getBaseContext());
                        if (VersionManagementUtil.VersionComparison(
                                data.getVersion(), currentVersion) == 1) {
                            VersionManagementUtil.getInstance(
                                    getBaseContext()).Update(data,
                                    SettingActivity.this);
                        } else {
                            displayToast(getString(R.string.check_update_success));
                        }
                    } else {
                        displayToast(getString(R.string.check_update_failed));
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    //------****** 缓存相关****----------
    private final int CLEAN_SUC = 1001;
    private final int CLEAN_FAIL = 1002;

    private void onClickCleanCache() {
        new TowButtonDialog(this, getString(R.string.clear_cache_tips), "取消",
                "确定", new TowButtonDialog.Listener() {
            @Override
            public void onLeftClick() {
            }
            @Override
            public void onRightClick() {
                clearAppCache();
            }
        }).show();
    }

    /**
     * 计算缓存的大小
     */
    private void caculateCacheSize() {
        try {
            String cacheSize = DataCleanManager.getTotalCacheSize(this);
            tvCacheNumber.setText(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除app缓存
     */
    public void myclearaAppCache() {
        DataCleanManager.clearAllCache(this);
    }

    /**
     * 清除app缓存
     *
     * @param
     */
    public void clearAppCache() {

        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    myclearaAppCache();
                    msg.what = CLEAN_SUC;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = CLEAN_FAIL;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEAN_FAIL:
                    displayToast(getString(R.string.clear_cache_failed));
                    break;
                case CLEAN_SUC:
                    displayToast(getString(R.string.clear_cache_success));
                    caculateCacheSize();
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            // 拦截MENU按钮点击事件，让他无任何操作
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                return true;
            }
        return super.onKeyDown(keyCode, event);
    }
}
