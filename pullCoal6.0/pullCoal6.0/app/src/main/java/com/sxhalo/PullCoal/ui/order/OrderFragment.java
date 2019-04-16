package com.sxhalo.PullCoal.ui.order;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.sxhalo.PullCoal.bean.Dictionary;
import com.sxhalo.PullCoal.bean.FilterEntity;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.ui.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.ui.order.coalorder.CoalOrderFragment;
import com.sxhalo.PullCoal.ui.order.myreleaseFragment.MyReleaseFragment;
import com.sxhalo.PullCoal.ui.order.sendcarorder.SendCarOrderFragment;
import com.sxhalo.PullCoal.ui.order.transportorder.TransportOrderFragment;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.LogUtil;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * 订单Fragment
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public class OrderFragment extends BaseFragment {

    @BindView(R.id.layout_login)
    LinearLayout layoutLogin;
    @BindView(R.id.all_rg_nav_content)
    RadioGroup allRgNavContent;
    @BindView(R.id.all_iv_nav_indicator)
    ImageView allIvNavIndicator;
    @BindView(R.id.all_rl_nav)
    RelativeLayout allRlNav;
    @BindView(R.id.all_mHsv)
    SyncHorizontalScrollView allMHsv;
    @BindView(R.id.viewpager)
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

    private MyReleaseFragment seekOrderFragment;
    private CoalOrderFragment coalOrderFragment;
    private SendCarOrderFragment sendCarOrderFragment;
    private TransportOrderFragment transportOrderFragment;
    private MyViewPagerAdapter mAdapetr;



    public static OrderFragment newInstance() {
        Bundle args = new Bundle();
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_order;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
//        DaggerHttpComponent.builder()
//                .applicationComponent(appComponent)
//                .build()
//                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        myActivity = getActivity();


    }

    @Override
    public void initData() {
        initIndicator();
        initTabColumn();
        setTabColumn(index);
        initFragment();
        initViewPager();


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
                                ((checkedId) > 1 ? ((RadioButton) allRgNavContent.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) allRgNavContent.getChildAt(2)).getLeft(),
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
                for (int i=0;i<sys100012.list.size();i++){
                    types.add(new FilterEntity());
                }
                for (FilterEntity filterEntity:sys100012.list){
                    int i = Integer.valueOf(filterEntity.dictCode) - 1;
                    types.add(i ,filterEntity);
                }
                types = sys100012.list;
            }

//            for (int i=0; i<4;i++){
//                FilterEntity filterEntity =new FilterEntity();
//                filterEntity.setDictCode(""+i);
//                filterEntity.setDictValue(i+"白天");
//                types.add(i ,filterEntity);
//            }

            allRgNavContent.removeAllViews();
            for (int i = 0; i < types.size(); i++) {
//            for (int i = 0; i < types.length; i++) {
//                RadioButton rb = (RadioButton) mInflater.inflate(R.layout.order_coal_item, null);
                RadioButton rb = (RadioButton) mInflater.inflate(R.layout.fragment_order_coal_type_item, null);
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
//            if (fragments.size() == 0){
//                fragments.add(new Fragment());
//                fragments.add(new Fragment());
//                fragments.add(new Fragment());
//                fragments.add(new Fragment());
//            }

            //煤炭订单
            coalOrderFragment = new CoalOrderFragment();
           // coalOrderFragment.setHeight(footerHeight);

            //派车单
            sendCarOrderFragment = new SendCarOrderFragment();
          //  sendCarOrderFragment.setHeight(footerHeight);

            //求购单
            seekOrderFragment = new MyReleaseFragment();
           // seekOrderFragment.setHeight(footerHeight);

            //货运单
            transportOrderFragment = new TransportOrderFragment();
           // transportOrderFragment.setHeight(footerHeight);

            Log.i("xxx", "initFragment222 : "+fragments);
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

            Log.i("xxx", "initFragment33333 : "+fragments);

//            fragments.add(seekOrderFragment);
//            fragments.add(coalOrderFragment);
//            fragments.add(sendCarOrderFragment);
//            fragments.add(transportOrderFragment);
        } catch (Exception e) {
            LogUtil.e("煤种赋值", e.toString());
        }
    }

    private void initViewPager() {
        mAdapetr = new MyViewPagerAdapter(getChildFragmentManager(), fragments);
        viewpager.setAdapter(mAdapetr);
        viewpager.setCurrentItem(0);

        viewpager.addOnPageChangeListener(pageListener);

    }
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
}