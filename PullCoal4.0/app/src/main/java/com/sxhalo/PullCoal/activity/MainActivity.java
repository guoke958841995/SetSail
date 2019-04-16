package com.sxhalo.PullCoal.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.fragment.FindFragment;
import com.sxhalo.PullCoal.fragment.FreightTransportFragment;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.fragment.MyFragment;
import com.sxhalo.PullCoal.fragment.OrderFragment;
import com.sxhalo.PullCoal.model.UpdateApp;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.servers.ExampleUtil;
import com.sxhalo.PullCoal.tools.UpDateMassage;
import com.sxhalo.PullCoal.tools.VersionManagementUtil;
import com.sxhalo.PullCoal.tools.map.LocationClient;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.StatusBarUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;


public class MainActivity extends BaseActivity {

    @Bind(R.id.group)
    RadioGroup group;
//    @Bind(R.id.layout_header)
//    RelativeLayout layoutHeader;
//    @Bind(R.id.view_header)
//    View viewHeader;

    @Bind(R.id.foot_bar_car)
    RadioButton footBarCar;
    @Bind(R.id.layoutFooter)
    RelativeLayout layoutFooter;

    private static final String TAG = MainActivity.class.getSimpleName();

    private int currentIndex = 0;
    private ArrayList<String> fragmentTags;
    private FragmentManager fragmentManager;
    private String city = "榆林";

    private HomePagerFragment homePagerFragment;
    private FreightTransportFragment freightTransportFragment;
    private OrderFragment orderFragment;
    private MyFragment myFragment;
    private LocationClient locationClient;  //轨迹上传
    private Activity mActivity;
    private UpDateMassage updateManager;  //自动更新工具

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        if (getIntent().getParcelableExtra("intent") != null) {
            Intent intent = getIntent().getParcelableExtra("intent");
            startActivity(intent);
        }
        mActivity = this;
        fragmentManager = getSupportFragmentManager();
        initData(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initTitle() {
        try {
            registerMessageReceiver();
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void getData(){
        updateApp();
    }

    /**
     * app 更新
     */
    private void updateApp() {
        try {
            new DataUtils(this, new LinkedHashMap<String, String>()).getAppVesion(new DataUtils.DataBack<UpdateApp>() {
                @Override
                public void getData(final UpdateApp data) {
                    if (data != null) {
                        String currentVersion = VersionManagementUtil.getVersion(getBaseContext());
                        if (VersionManagementUtil.VersionComparison(
                                data.getVersion(), currentVersion) == 1) {
                            new PermissionUtil().requestPermissions(mActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                                @Override
                                public void onGranted() { //用户同意授权
                                    updateManager = VersionManagementUtil.getInstance(
                                            getBaseContext()).Update(data,
                                            MainActivity.this);
                                }
                                @Override
                                public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                                }
                            });

                        }
                    } else {
                        displayToast("未能获取服务器版本号");
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(CURR_INDEX, currentIndex);
    }

    private void initData(Bundle savedInstanceState) {
        fragmentTags = new ArrayList<String>(Arrays.asList("HomePagerFragment", "FreightSearchFragment", "OrderFragment", "FindFragment", "MyFragment"));
        currentIndex = 0;
//        if (savedInstanceState != null) {
//            currentIndex = savedInstanceState.getInt(CURR_INDEX);
//            hideSavedFragment();
//        }
    }

    //    private void hideSavedFragment() {
//        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currentIndex));
//        if (fragment != null) {
//            fragmentManager.beginTransaction().hide(fragment).commit();
//        }
//    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            //            AnimationDrawable animP;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton radioButton = (RadioButton)findViewById(checkedId);
//                StateListDrawable background = (StateListDrawable) radioButton.getCompoundDrawables()[1];//获得文字顶部图片;
//                Drawable current = background.getCurrent();
//                if (animP != null){
//                    animP.stop();
//                    animP = null;
//                }
//                if (current instanceof AnimationDrawable) {
//                    animP = (AnimationDrawable) current;
//                    animP.start();
//                }
                switch (checkedId) {
                    case R.id.foot_bar_home:
                        setStatusBar(-1,-1);
                        currentIndex = 0;
                        setFlage(false);
                        break;
                    case R.id.foot_bar_car:
                        setStatusBar(-1,-1);
                        currentIndex = 1;
                        break;
                    case R.id.foot_bar_order:
                        setStatusBar(-1,-1);
                        currentIndex = 2;
                        setFlage(false);
                        break;
                    case R.id.foot_bar_found:
                        setStatusBar(-1,-1);
                        currentIndex = 3;
                        setFlage(false);
                        break;
                    case R.id.foot_bar_news:
                        setStatusBar(0,-1);
                        currentIndex = 4;
                        setFlage(false);
                        break;
                    default:
                        break;
                }
                showFragment();
            }
        });
        showFragment();
    }

    private void showFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currentIndex));
        if (fragment == null) {
            fragment = instantFragment(currentIndex);
        }
        for (int i = 0; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if (f != null && f.isAdded()) {
                fragmentTransaction.hide(f);
            }
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currentIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();

//        if (!"-1".equals(SharedTools.getStringValue(mContext,"userId","-1")) && locationClient == null){
//            //启动轨迹上传
//            locationClient = new LocationClient(mContext);
//            locationClient.startLocation();
//        }
//        if ("-1".equals(SharedTools.getStringValue(mContext,"userId","-1")) && locationClient != null){
//            locationClient.stopLocation();
//            locationClient = null;
//        }
        if (currentIndex == 2) {
            String userId = SharedTools.getStringValue(mActivity,"userId","-1");
            if ("-1".equals(userId)) {
                //未登录
                UIHelper.jumpActLogin(mActivity, false);
                orderFragment.showView(false);
            } else {
                //已登录
                orderFragment.showView(true);
            }
        }
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                homePagerFragment = new HomePagerFragment();
                return homePagerFragment;
            case 1:
                freightTransportFragment = new FreightTransportFragment();
                return freightTransportFragment;
            case 2:
                orderFragment = new OrderFragment();
                Bundle args = new Bundle();
                args.putInt("footerHeight", layoutFooter.getHeight());
                orderFragment.setArguments(args);
                return orderFragment;
            case 3:
                return new FindFragment();
            case 4:
                myFragment = new MyFragment();
                return new MyFragment();
            default:
                return null;
        }
    }

    public void setShowFragment(boolean flage){
        if (currentIndex == 0) {
            setFlage(flage);
            footBarCar.toggle();
        } else if (currentIndex == 2) {
            footBarCar.toggle();
        }
    }
    private boolean isFlage = false;
    public void setFlage(boolean isFlage) {
        this.isFlage = isFlage;
    }
    public boolean getFlage() {
        return isFlage;
    }

    private long mExitTime;
    // 判断点击退出大于2秒刷新
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    //关闭轨迹上传
                    locationClient.stopLocation();
                    // 退出时，注销极光推送 重置界面的下表值
                    AppManager.getAppManager().AppExit(this);
                    this.finish();
                }
                return true;
            }
            // 拦截MENU按钮点击事件，让他无任何操作
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                return true;
            }

        } catch (Exception e) {
            Log.i(TAG + "推出程序监听", e.toString());
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isForeground = true;
    // for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Constant.RECEIVE_MESSAGE);
        filter.addAction(Constant.ACCOUNT_CONFLICT);
        registerReceiver(mMessageReceiver, filter);
    }


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Constant.RECEIVE_MESSAGE.equals(intent.getAction())) {
                    //定位后收到通知 隐藏dialog
                    if (!StringUtils.isEmpty(intent.getStringExtra("city"))) {
                        homePagerFragment.setLocationText(intent);
                        return;
                    }
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    SharedTools.putBooleanValue(mActivity,"ivRedPoint",true);
                    homePagerFragment.setIvRedPoint();
                    if(myFragment != null){
                        myFragment.setIvRedPoint();
                    }
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                } else if (Constant.ACCOUNT_CONFLICT.equals(intent.getAction())) {
                    //被挤下线或注销登录 隐藏消息红点
                    SharedTools.putBooleanValue(mActivity,"ivRedPoint",false);
                    homePagerFragment.setIvRedPoint();

                    if(myFragment != null){
                        myFragment.setIvRedPoint();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String machinCode = SharedTools.getStringValue(this, "machinCode", "");
        if (!StringUtils.isEmpty(machinCode)) {
            SharedTools.putStringValue(this, "machinCode", "");
            Utils.showDialog(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case Constant.JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT:
                    if (freightTransportFragment != null){
                        freightTransportFragment.setCurrentItem(0);
                    }
                    if (currentIndex == 4) {
                        currentIndex = 1;
//                group.check(R.id.foot_bar_car);
                        footBarCar.toggle();
                    }
                    break;
                case Constant.ACTION_MANAGE_UNKNOWN_APP_SOURCES:
                    if (updateManager != null){
                        updateManager.installProcess();
                    }
                    break;
                default: // 刷新订单功能界面中的求购单地址筛选
                    orderFragment.onActivityResult(requestCode,resultCode,data);
                    break;
            }

        }
    }

}
