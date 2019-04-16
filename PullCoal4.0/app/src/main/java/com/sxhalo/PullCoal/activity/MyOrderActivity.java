package com.sxhalo.PullCoal.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.fragment.AcceptedFragment;
import com.sxhalo.PullCoal.fragment.UnAcceptedFragment;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.SuperViewPager;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liz on 2017/7/24.
 */

public class MyOrderActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.left_text)
    TextView leftText;
    @Bind(R.id.right_text)
    TextView rightText;
    @Bind(R.id.tab_line_iv)
    ImageView tabLineIv;
    @Bind(R.id.super_viewpager)
    SuperViewPager viewPager;

    private ArrayList<Fragment> lists = new ArrayList<Fragment>();
    private AcceptedFragment leftFragment;
    private UnAcceptedFragment rightFragment;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_order);
    }

    @Override
    protected void initTitle() {
        title.setText("我的订单");
        try {
            setViewPage();
            initTabLineWidth();
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    protected void getData() {

    }

    private void setViewPage() throws Exception{
        leftFragment = new AcceptedFragment();
        rightFragment = new UnAcceptedFragment();

        lists.add(leftFragment);
        lists.add(rightFragment);
        FragmentManager fm = getSupportFragmentManager();
        MyViewPagerAdapter mFragmentAdapter = new MyViewPagerAdapter(fm,lists);
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setCurrentItem(0);

        viewPager.setOnPageChangeListener(new SuperViewPager.OnPageChangeListener() {

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }
            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabLineIv
                        .getLayoutParams();

                // Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景： 记3个页面, 从左到右分别为0,1,2 0->1; 1->0
                 */

                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                }
                tabLineIv.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        leftText.setTextColor(Color.rgb(29, 122, 235));
                        break;
                    case 1:
                        rightText.setTextColor(Color.rgb(29, 122, 235));
                        break;
                }
                currentIndex = position;
            }
        });
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        try{
            rightText.setTextColor(Color.rgb(102, 102, 102));
            leftText.setTextColor(Color.rgb(102, 102, 102));
        }catch (Exception e){
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("货运功能界面",e.toString());
        }
    }

    /**
     * 设置滑动条的宽度为屏幕的1/2(根据Tab的个数而定)
     */
    private void initTabLineWidth() throws Exception{
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 2;
        tabLineIv.setLayoutParams(lp);
    }

    @OnClick({R.id.title_bar_left, R.id.left_text, R.id.right_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.left_text:
                viewPager.setCurrentItem(0);
                leftText.setTextColor(getResources().getColor(
                        R.color.app_title_text_color));
                rightText.setTextColor(getResources().getColor(
                        R.color.father_group_text_color));
                break;
            case R.id.right_text:
                viewPager.setCurrentItem(1);
                leftText.setTextColor(getResources().getColor(
                        R.color.father_group_text_color));
                rightText.setTextColor(getResources().getColor(
                        R.color.app_title_text_color));
                break;
        }
    }
}
