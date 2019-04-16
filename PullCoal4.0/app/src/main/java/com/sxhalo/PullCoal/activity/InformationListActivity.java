package com.sxhalo.PullCoal.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.fragment.InformationListFragment;
import com.sxhalo.PullCoal.ui.SyncHorizontalScrollView;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 煤炭资讯列表
 * Created by liz on 2017/4/12.
 */

public class InformationListActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.mViewPager)
    ViewPager mViewPager;

    @Bind(R.id.all_rg_nav_content)
    RadioGroup allRgNavContent;
    @Bind(R.id.all_iv_nav_indicator)
    ImageView allIvNavIndicator;
    @Bind(R.id.all_rl_nav)
    RelativeLayout allRlNav;
    @Bind(R.id.all_mHsv)
    SyncHorizontalScrollView allMHsv;
    @Bind(R.id.all_iv_nav_left)
    ImageView allIvNavLeft;
    @Bind(R.id.all_iv_nav_right)
    ImageView allIvNavRight;



    // 指示器宽度
    private int indicatorWidth;
    private LayoutInflater mInflater;
    //当前选中位置
    private int index = 0;
    //记录当前位移
    private int currentIndicatorLeft = 0;
    private int what;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private List<FilterEntity> userChannelList = new ArrayList<FilterEntity>();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information_list);
    }

    @Override
    protected void initTitle() {
        title.setText("煤炭资讯");
    }

    @Override
    protected void getData() {
        userChannelList = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100001"}).get(0).list;
        if (userChannelList.size() < 5) {
            what = userChannelList.size();
        } else {
            what = 4;
        }
        initView();
    }

    /**
     * 布局初始化
     */
    private void initView() {
        try {
            setChangelView();
        } catch (Exception e) {
            Log.i("initView() ", e.toString());
        }
    }

    /**
     * 当栏目项发生变化时候调用
     */
    private void setChangelView() {
        initIndicator();
        initTabColumn();
        setTabColumn(index);
        initFragment(userChannelList);
    }

    private void initIndicator() {
        try {
            int widthPixels = BaseUtils.getWindowsWidth(this);
            //		int hPIXELS = BaseUtils.getWindowsHight(activity);
            indicatorWidth = widthPixels / what;// 指示器宽度为屏幕宽度的5/1
            ViewGroup.LayoutParams cursor_Params = allIvNavIndicator.getLayoutParams();
            cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
            allIvNavIndicator.setLayoutParams(cursor_Params);

            allMHsv.setSomeParam(allRlNav, allIvNavLeft, allIvNavRight, this, widthPixels);
            // 获取布局填充器
            mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            Log.i("initIndicator() ", e.toString());
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

                        TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft,  allRgNavContent.getChildAt(checkedId).getLeft(), 0f, 0f);
                        animation.setInterpolator(new LinearInterpolator());
                        animation.setDuration(100);
                        animation.setFillAfter(true);
                        // 执行位移动画
                        allIvNavIndicator.startAnimation(animation);
                        index = checkedId;
                        mViewPager.setCurrentItem(index);
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
            allRgNavContent.removeAllViews();
            for (int i = 0; i < userChannelList.size(); i++) {
                RadioButton rb = (RadioButton) mInflater.inflate(R.layout.coal_type_item, null);
                rb.setId(i);
                rb.setText(userChannelList.get(i).dictValue);
                rb.setTextSize(18f);
                rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth, ViewGroup.LayoutParams.MATCH_PARENT));
                if (i == position) {
                    rb.setChecked(true);
                } else {
                    rb.setChecked(false);
                }
                if (i == userChannelList.size() - 1) {
                    Drawable myImage = getResources().getDrawable(android.R.color.transparent);
                    rb.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
                }
                allRgNavContent.addView(rb);
            }
        } catch (Exception e) {
            Log.i("initNavigationHSV() ", e.toString());
        }
    }

    private void initFragment(List<FilterEntity> userChannelList) {
        try {
            fragments.clear();
            for (int i = 0; i < userChannelList.size(); i++) {
                Bundle data = new Bundle();
                data.putSerializable("FilterEntity", userChannelList.get(i));
                InformationListFragment newfragment = new InformationListFragment();
                newfragment.setArguments(data);
                fragments.add(newfragment);
            }
            MyViewPagerAdapter mAdapetr = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
            mViewPager.setAdapter(mAdapetr);
            mViewPager.setOnPageChangeListener(pageListener);
        } catch (Exception e) {
            GHLog.e("资讯赋值", e.toString());
        }
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
            mViewPager.setCurrentItem(position);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.title_bar_left)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
        }
    }


}
