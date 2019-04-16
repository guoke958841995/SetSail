package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.fragment.PullCoalMyReleaseFragment;
import com.sxhalo.PullCoal.fragment.PullCoalFragment;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我要拉煤
 * Created by amoldZhang on 2018/4/23.
 */
public class GoingToPullCoalActivity extends BaseActivity{

    @Bind(R.id.tv_pull_coal)
    TextView tvPullCoal;
    @Bind(R.id.tv_my_release)
    TextView tvMyRelease;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private PullCoalFragment pullCoalFragment;
    private PullCoalMyReleaseFragment pullCoalMyReleaseFragment;
//    private int type; // 区分是从首页进入还是从我的界面进入 0 首页进入 1 我的界面进入
    private String userId = "-1";

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_going_to_pull_coal);
    }

    @Override
    protected void initTitle() {
//        type = getIntent().getIntExtra("type", 0);
        initFragments();
    }

    @Override
    protected void getData() {

    }

    private void initFragments() {
        pullCoalFragment = new PullCoalFragment();
        pullCoalMyReleaseFragment = new PullCoalMyReleaseFragment();
        fragments.add(0,pullCoalFragment);
        fragments.add(1,pullCoalMyReleaseFragment);
        FragmentManager fm = getSupportFragmentManager();
        MyViewPagerAdapter mAdapetr = new MyViewPagerAdapter(fm, fragments);
        mViewPager.setAdapter(mAdapetr);
//        if (type == 0) {
            mViewPager.setCurrentItem(0);
//        } else {
//            resetStyle(1);
//            mViewPager.setCurrentItem(1);
//        }
        mViewPager.setOnPageChangeListener(pageListener);
    }

    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            resetStyle(position);
        }
    };

    private void resetStyle(int index) {
        int paddingHorizontal = tvMyRelease.getPaddingLeft();
        int paddingVertical = tvMyRelease.getPaddingTop();
        switch (index) {
            case 0:
                mViewPager.setCurrentItem(0);
                tvPullCoal.setTextColor(getResources().getColor(R.color.white));
                tvMyRelease.setTextColor(getResources().getColor(R.color.app_title_text_color));
                tvMyRelease.setBackgroundResource(R.drawable.offlinearrow_tab2_normal);
                tvPullCoal.setBackgroundResource(R.drawable.offlinearrow_tab1_pressed);
                break;
            case 1:
                String userId1 = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
                // 判断是否登录
                if (!userId1.equals("-1")) { // 已登录并且信息获取成功
                    mViewPager.setCurrentItem(1);
                    tvMyRelease.setTextColor(getResources().getColor(R.color.white));
                    tvPullCoal.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvMyRelease.setBackgroundResource(R.drawable.offlinearrow_tab2_pressed);
                    tvPullCoal.setBackgroundResource(R.drawable.offlinearrow_tab1_normal);
                } else {
                    mViewPager.setCurrentItem(0);
                    UIHelper.jumpActLogin(GoingToPullCoalActivity.this,  false);
                }
                break;
        }
        tvPullCoal.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
        tvMyRelease.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
    }


    @OnClick({R.id.iv_bak, R.id.tv_release, R.id.tv_pull_coal, R.id.tv_my_release})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bak:
                finish();
                break;
            case R.id.tv_pull_coal:
                //我要拉煤
                resetStyle(0);
                break;
            case R.id.tv_my_release:
                //我的发布
                userId = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
                // 判断是否登录
                if (!userId.equals("-1")) { // 已登录并且信息获取成功
                    resetStyle(1);
                } else {
                    UIHelper.jumpActLogin(GoingToPullCoalActivity.this,  false);
                }
                break;
            case R.id.tv_release:
                //发布
                userId = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
                // 判断是否登录
                if (!userId.equals("-1")) { // 已登录并且信息获取成功
                    ifWhow();
                } else {
                    UIHelper.jumpActLogin(GoingToPullCoalActivity.this, false);
                }
                break;
        }
    }

    private void ifWhow() {
        try {
            new DataUtils(this, new LinkedHashMap<String, String>()).getUserDriverAuthInfo(new DataUtils.DataBack<APPData<UserAuthenticationEntity>>() {
                @Override
                public void getData(APPData<UserAuthenticationEntity> data) {
                    if (data == null) {
                        return;
                    }
                    setDataView(data.getEntity());
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    private void setDataView(UserAuthenticationEntity dataView) {
        //0未认证
        String vehicleState = StringUtils.isEmpty(dataView.getDriverAuthState()) ? "0" : dataView.getDriverAuthState();  //司机认证状态
        if ("0".equals(vehicleState)){
            showDaiLog(GoingToPullCoalActivity.this, getString(R.string.no_submit_vehicle_data));
        }else{
            Intent intent = new Intent();
            intent.setClass(GoingToPullCoalActivity.this, ReleaseCoalActivity.class);
            startActivityForResult(intent, Constant.REFRESH_CODE);
        }
    }

    private void showDaiLog(Activity mActivity, String message) {
        new RLAlertDialog(mActivity, "系统提示", message, "稍后提示",
                "立刻前往", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                UIHelper.togoSubmitAuthentication(GoingToPullCoalActivity.this, DriverCertificationActivity.class);
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.REFRESH_CODE) {
            pullCoalFragment.baseAdapterUtils.onRefresh();
            pullCoalMyReleaseFragment.baseAdapterUtils.onRefresh();
        }
    }
}
