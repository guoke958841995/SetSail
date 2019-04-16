package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.fragment.MyReleaseFragment;
import com.sxhalo.PullCoal.fragment.ProcurementFragment;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.AREA_CODE;
import static com.sxhalo.PullCoal.common.base.Constant.PAYMENT_CODE;

/**
 * 买煤求购列表界面
 * Created by amoldZhang on 2016/12/21.
 */
public class PurchasingReservationActivity extends BaseActivity {

    @Bind(R.id.tv_procurement)
    TextView tvProcurement;
    @Bind(R.id.tv_my_release)
    TextView tvMyRelease;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ProcurementFragment procurementFragment;
    private MyReleaseFragment myReleaseFragment;
    private int type; // 区分是从首页进入还是从我的界面进入 0 首页进入 1 我的界面进入
    private String userId = "-1";
    private int currentIndex = 0;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_purchase);
    }

    @Override
    protected void initTitle() {
        type = getIntent().getIntExtra("type", 0);
        initFragments();
    }

    private void initFragments() {
        procurementFragment = new ProcurementFragment();
        myReleaseFragment = new MyReleaseFragment();
        fragments.add(0, procurementFragment);
        fragments.add(1, myReleaseFragment);
        FragmentManager fm = getSupportFragmentManager();
        MyViewPagerAdapter mAdapetr = new MyViewPagerAdapter(fm, fragments);
        mViewPager.setAdapter(mAdapetr);
        if (type == 0) {
            mViewPager.setCurrentItem(0);
        } else {
            resetStyle(1);
            mViewPager.setCurrentItem(1);
        }
        mViewPager.setOnPageChangeListener(pageListener);
    }

    @Override
    protected void getData() {

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
            currentIndex = position;
            resetStyle(position);
            switch (currentIndex) {
                case 0:
                    procurementFragment.refresh();
                    break;
                case 1:
                    myReleaseFragment.refresh();
                    break;
            }
        }
    };

    @OnClick({R.id.iv_bak, R.id.tv_release, R.id.tv_procurement, R.id.tv_my_release})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bak:
                finish();
                break;
            case R.id.tv_procurement:
                //买煤求购
                resetStyle(0);
                break;
            case R.id.tv_my_release:
                //我的发布
                userId = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
                // 判断是否登录
                if (!userId.equals("-1")) { // 已登录并且信息获取成功
                    resetStyle(1);
                } else {
                    UIHelper.jumpActLogin(PurchasingReservationActivity.this, false);
                }
                break;
            case R.id.tv_release:
                //发布
                userId = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
                // 判断是否登录
                if (!userId.equals("-1")) { // 已登录并且信息获取成功
                    ifWhow();
                } else {
                    UIHelper.jumpActLogin(PurchasingReservationActivity.this, false);
                }
                break;
        }
    }


    private void resetStyle(int index) {
        int paddingHorizontal = tvMyRelease.getPaddingLeft();
        int paddingVertical = tvMyRelease.getPaddingTop();
        switch (index) {
            case 0:
                mViewPager.setCurrentItem(0);
                tvProcurement.setTextColor(getResources().getColor(R.color.white));
                tvMyRelease.setTextColor(getResources().getColor(R.color.app_title_text_color));
                tvMyRelease.setBackgroundResource(R.drawable.offlinearrow_tab2_normal);
                tvProcurement.setBackgroundResource(R.drawable.offlinearrow_tab1_pressed);
                break;
            case 1:
                String userId1 = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
                // 判断是否登录
                if (!userId1.equals("-1")) { // 已登录并且信息获取成功
                    mViewPager.setCurrentItem(1);
                    tvMyRelease.setTextColor(getResources().getColor(R.color.white));
                    tvProcurement.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvMyRelease.setBackgroundResource(R.drawable.offlinearrow_tab2_pressed);
                    tvProcurement.setBackgroundResource(R.drawable.offlinearrow_tab1_normal);
                } else {
                    mViewPager.setCurrentItem(0);
                    UIHelper.jumpActLogin(PurchasingReservationActivity.this, false);
                }
                break;
        }
        tvProcurement.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
        tvMyRelease.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
    }

    private void ifWhow() {
//        new DataUtils(this, new LinkedHashMap<String, String>()).getUserRealnameAuthInfo(new DataUtils.DataBack<UserAuthenticationEntity>() {
//            @Override
//            public void getData(UserAuthenticationEntity dataMemager) {
//                try {
//                    if (dataMemager == null) {
//                        return;
//                    }
//                    String cerrtificationState = StringUtils.isEmpty(dataMemager.getAuthState()) ? "100" : dataMemager.getAuthState();
//                    //0审核中1审核成功2审核失败 100未提交
//                    switch (Integer.valueOf(cerrtificationState)) {
//                        case 100: //未提交
//                            showDaiLog(PurchasingReservationActivity.this, getString(R.string.unable_real_name_the_authentication));
//                            break;
//                        case 0: //审核中
//                            displayToast(getString(R.string.under_review));
//                            break;
//                        case 1: //审核成功
                        Intent intent = new Intent(PurchasingReservationActivity.this, ReservationPurchaseReleaseActivity.class);
                        startActivityForResult(intent, Constant.PAYMENT_CODE);
//                            break;
//                        case 2: //审核失败
//                            showDaiLog(PurchasingReservationActivity.this, getString(R.string.submit_under_review_failed));
//                            break;
//                        default:
//
//                            break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void showDaiLog(Activity mActivity, String TEXT) {
        LayoutInflater inflater1 = mActivity.getLayoutInflater();
        View layout = inflater1.inflate(R.layout.dialog_updata, null);
        ((TextView) layout.findViewById(R.id.updata_message)).setText(TEXT);
        new RLAlertDialog(mActivity, "系统消息", layout, "稍后提示",
                "立刻前往", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                UIHelper.togoSubmitAuthentication(PurchasingReservationActivity.this, BuyerCertificationActivity.class);
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AREA_CODE:
                    if (currentIndex == 0) {
                        procurementFragment.onActivityResult(AREA_CODE, resultCode, data);
                    } else {
                        myReleaseFragment.onActivityResult(AREA_CODE, resultCode, data);
                    }
                    break;
                case PAYMENT_CODE:
                    if (currentIndex == 0) {
                        procurementFragment.refresh();
                    } else {
                        myReleaseFragment.refresh();
                    }
                    break;
            }
        }
    }
}
