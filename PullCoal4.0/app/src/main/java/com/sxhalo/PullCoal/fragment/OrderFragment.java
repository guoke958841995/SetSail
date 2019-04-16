package com.sxhalo.PullCoal.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.ui.SyncHorizontalScrollView;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.sxhalo.PullCoal.common.base.Constant.ACCOUNT_CONFLICT;
import static com.sxhalo.PullCoal.common.base.Constant.AREA_CODE;
import static com.sxhalo.PullCoal.common.base.Constant.PAYMENT_CODE;

/**
 * Created by liz on 2018/4/27.
 */
public class OrderFragment extends Fragment {

    @Bind(R.id.layout_login)
    LinearLayout layoutLogin;
    @Bind(R.id.all_rg_nav_content)
    RadioGroup allRgNavContent;
    @Bind(R.id.all_iv_nav_indicator)
    ImageView allIvNavIndicator;
    @Bind(R.id.all_rl_nav)
    RelativeLayout allRlNav;
    @Bind(R.id.all_mHsv)
    SyncHorizontalScrollView allMHsv;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    private FragmentActivity myActivity;
    // 指示器宽度
    private int indicatorWidth;
    private LayoutInflater mInflater;
    //记录当前位移
    private int currentIndicatorLeft = 0;
    //当前选中位置
    private int index = 0;
//        private String[] types = {"求购单", "煤炭订单", "派车单", "货运单"};
    private ArrayList<FilterEntity> types = new ArrayList<>();

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private int footerHeight;
    private MyRefreshReceiver myRefreshReceiver;
    private MyReleaseFragment seekOrderFragment;
    private CoalOrderFragment coalOrderFragment;
    private SendCarOrderFragment sendCarOrderFragment;
    private TransportOrderFragment transportOrderFragment;
    private MyViewPagerAdapter mAdapetr;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_order, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e("订单页面", e.toString());
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        footerHeight = getArguments().getInt("footerHeight", 0);
        registerMyReceiver();
        initIndicator();
        initTabColumn();
        setTabColumn(index);
        initFragment();
        initViewPager();
    }

    public void showView(boolean show) {
        if (show) {
            layoutLogin.setVisibility(View.VISIBLE);
        } else {
            layoutLogin.setVisibility(View.GONE);
        }
    }

    private void initIndicator() {
        try {
            int widthPixels = BaseUtils.getWindowsWidth(myActivity);
            //		int hPIXELS = BaseUtils.getWindowsHight(activity);
            indicatorWidth = widthPixels / 4;// 指示器宽度为屏幕宽度的1/4
            ViewGroup.LayoutParams cursor_Params = allIvNavIndicator.getLayoutParams();
            cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
            allIvNavIndicator.setLayoutParams(cursor_Params);

            allMHsv.setSomeParam(allRlNav, null, null, myActivity, widthPixels);
            // 获取布局填充器
            mInflater = (LayoutInflater) myActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            Log.i("initView() ", e.toString());
        }
    }

    /**
     * 初始化Column栏目项
     */
    private void initTabColumn() {
        try {
            allRgNavContent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (allRgNavContent.getChildAt(checkedId) != null) {

                        TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft, allRgNavContent.getChildAt(checkedId).getLeft(), 0f, 0f);
                        animation.setInterpolator(new LinearInterpolator());
                        animation.setDuration(100);
                        animation.setFillAfter(true);
                        // 执行位移动画
                        allIvNavIndicator.startAnimation(animation);
                        index = checkedId;
                        viewpager.setCurrentItem(index);
                        // 记录当前 下标的距最左侧的 距离
                        currentIndicatorLeft = (allRgNavContent.getChildAt(checkedId)).getLeft();
                        allMHsv.smoothScrollTo(
                                (checkedId > 1 ? ((RadioButton) allRgNavContent.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) allRgNavContent.getChildAt(2)).getLeft(),
                                0);
                    }
                }
            });
        } catch (Exception e) {
            Log.i("setChangelView()", e.toString());
        }
    }

    /**
     * 设置模块显示样式
     */
    private void setTabColumn(int position) {
        try {
            Dictionary sys100012 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100012"}).get(0);
            if ("订单标签".equals(sys100012.title)){
//                for (int i=0;i<sys100012.list.size();i++){
//                    types.add(new FilterEntity());
//                }
//                for (FilterEntity filterEntity:sys100012.list){
//                    int i = Integer.valueOf(filterEntity.dictCode) - 1;
//                    types.add(i ,filterEntity);
//                }
                types = sys100012.list;
            }
            allRgNavContent.removeAllViews();
            for (int i = 0; i < types.size(); i++) {
//            for (int i = 0; i < types.length; i++) {
                RadioButton rb = (RadioButton) mInflater.inflate(R.layout.coal_type_item, null);
                rb.setId(i);
                rb.setText(types.get(i).dictValue);
//                rb.setText(types[i]);
                rb.setTextSize(16f);
                rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth, ViewGroup.LayoutParams.MATCH_PARENT));
                if (i == position) {
                    rb.setChecked(true);
                } else {
                    rb.setChecked(false);
                }
                if (i == types.size() - 1) {
//                if (i == types.length - 1) {
                    Drawable myImage = getResources().getDrawable(android.R.color.transparent);
                    rb.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                }
                allRgNavContent.addView(rb);
            }
        } catch (Exception e) {
            Log.i("initNavigationHSV() ", e.toString());
        }
    }

    private void initFragment() {
        try {
            if (fragments.size() == 0){
                fragments.add(new Fragment());
                fragments.add(new Fragment());
                fragments.add(new Fragment());
                fragments.add(new Fragment());
            }

            //煤炭订单
            coalOrderFragment = new CoalOrderFragment();
            coalOrderFragment.setHeight(footerHeight);

            //派车单
            sendCarOrderFragment = new SendCarOrderFragment();
            sendCarOrderFragment.setHeight(footerHeight);

            //求购单
            seekOrderFragment = new MyReleaseFragment();
            seekOrderFragment.setHeight(footerHeight);

            //货运单
            transportOrderFragment = new TransportOrderFragment();
            transportOrderFragment.setHeight(footerHeight);

            for (FilterEntity filterEntity:types){
                // 煤炭订单
                if ("1".equals(filterEntity.dictCode)){
                    fragments.add(0,coalOrderFragment);
                }
                // 派车单
                if ("2".equals(filterEntity.dictCode)){
                    fragments.add(1,sendCarOrderFragment);
                }
                // 求购单
                if ("3".equals(filterEntity.dictCode)){
                    fragments.add(2,seekOrderFragment);
                }
                // 货运单
                if ("4".equals(filterEntity.dictCode)){
                    fragments.add(3,transportOrderFragment);
                }
            }

//            fragments.add(seekOrderFragment);
//            fragments.add(coalOrderFragment);
//            fragments.add(sendCarOrderFragment);
//            fragments.add(transportOrderFragment);
        } catch (Exception e) {
            GHLog.e("煤种赋值", e.toString());
        }
    }

    private void initViewPager() {
        mAdapetr = new MyViewPagerAdapter(getChildFragmentManager(), fragments);
        viewpager.setAdapter(mAdapetr);
        viewpager.setOnPageChangeListener(pageListener);
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
            viewpager.setCurrentItem(position);
            index = position;
            tabSelectedChange(position);
        }
    };

    private void tabSelectedChange(int pos) {
        for (int i = 0; i < allRgNavContent.getChildCount(); i++) {
            RadioButton rb = (RadioButton) allRgNavContent.getChildAt(i);
            if (rb.getId() == pos) {
                rb.setChecked(true);
            } else {
                rb.setChecked(false);
            }
        }
    }

    /**
     * 注册登录成功后刷新界面的广播
     */
    public void registerMyReceiver() {
        myRefreshReceiver = new MyRefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Constant.REFRESH_CODE + "");
        filter.addAction(ACCOUNT_CONFLICT);
        myActivity.registerReceiver(myRefreshReceiver, filter);
    }

    /**
     * 登录成功或注销时候的广播接收者
     */
    class MyRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constant.REFRESH_CODE + "").equals(intent.getAction())) {
                //登录成功
                layoutLogin.setVisibility(View.VISIBLE);
                setTabColumn(index);
                initFragment();
                initViewPager();
            } else if (ACCOUNT_CONFLICT.equals(intent.getAction())) {
                //被踢下线
                layoutLogin.setVisibility(View.GONE);
                removeView();
            }
        }
    }

    /**
     * 被踢下线，移除所有跟用户相关的视图，弹框提示用户登录
     */
    private void removeView() {
        seekOrderFragment = null;
        coalOrderFragment = null;
        sendCarOrderFragment = null;
        transportOrderFragment = null;
        fragments.clear();
        mAdapetr.notifyDataSetChanged();
        index = 0;

    }

    /**
     *  刷新订单功能界面中的求购单地址筛选
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AREA_CODE:
                    if (index == 2) {
                        seekOrderFragment.onActivityResult(AREA_CODE, resultCode, data);
                    }
                    break;
                case PAYMENT_CODE:
                    if (index == 2) {
                        seekOrderFragment.refresh();
                    }
                    break;
            }
        }
    }

}
