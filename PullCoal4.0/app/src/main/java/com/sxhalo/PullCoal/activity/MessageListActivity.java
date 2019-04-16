package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.fragment.AllMessageFragment;
import com.sxhalo.PullCoal.fragment.MyMessageFragment;
import com.sxhalo.PullCoal.ui.guide.GuidePage;
import com.sxhalo.PullCoal.ui.guide.GuidePageManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.REFRESH_CODE;

/**
 * 留言板块
 * Created by liz on 2018/3/5.
 */
public class MessageListActivity extends BaseActivity{

    @Bind(R.id.tv_all_message)
    TextView tvAllMessage;
    @Bind(R.id.tv_my_message)
    TextView tvMyMessage;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private AllMessageFragment allMessageFragment;
    private MyMessageFragment myMessageFragment;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_message_list);

        if(GuidePageManager.hasNotShowed(this, MessageListActivity.class.getSimpleName())){
            new GuidePage.Builder(this)
                    .setLayoutId(R.layout.view_find_legal_counsulting_guide_page_002)
                    .setKnowViewId(R.id.btn_home_act_enter_know)
                    .setPageTag(MessageListActivity.class.getSimpleName())
                    .builder()
                    .apply();
        }
    }

    @Override
    protected void initTitle() {
        initFragments();
    }

    @Override
    protected void getData() {

    }

    private void initFragments() {
        allMessageFragment = new AllMessageFragment();
        myMessageFragment = new MyMessageFragment();
        fragments.add(0,allMessageFragment);
        fragments.add(1,myMessageFragment);
        FragmentManager fm = getSupportFragmentManager();
        MyViewPagerAdapter mAdapetr = new MyViewPagerAdapter(fm, fragments);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setCurrentItem(0);
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
        int paddingHorizontal = tvAllMessage.getPaddingLeft();
        int paddingVertical = tvMyMessage.getPaddingTop();
        switch (index) {
            case 0:
                mViewPager.setCurrentItem(0);
                tvAllMessage.setTextColor(getResources().getColor(R.color.white));
                tvMyMessage.setTextColor(getResources().getColor(R.color.app_title_text_color));
                tvMyMessage.setBackgroundResource(R.drawable.offlinearrow_tab2_normal);
                tvAllMessage.setBackgroundResource(R.drawable.offlinearrow_tab1_pressed);
                break;
            case 1:
                mViewPager.setCurrentItem(1);
                tvMyMessage.setTextColor(getResources().getColor(R.color.white));
                tvAllMessage.setTextColor(getResources().getColor(R.color.app_title_text_color));
                tvMyMessage.setBackgroundResource(R.drawable.offlinearrow_tab2_pressed);
                tvAllMessage.setBackgroundResource(R.drawable.offlinearrow_tab1_normal);
                break;
        }
        tvAllMessage.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
        tvMyMessage.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
    }

    @OnClick({R.id.iv_bak, R.id.tv_release, R.id.tv_all_message, R.id.tv_my_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bak:
                finish();
                break;
            case R.id.tv_all_message:
                //所有留言
                resetStyle(0);
                break;
            case R.id.tv_my_message:
                //我的留言
                    resetStyle(1);
                break;
            case R.id.tv_release:
                //发布留言
                Intent intent = new Intent(this, LeaveMessageActivity.class);
                startActivityForResult(intent, REFRESH_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REFRESH_CODE:
                    allMessageFragment.baseAdapterUtils.onRefresh();
                    myMessageFragment.baseAdapterUtils.onRefresh();

                    if(GuidePageManager.hasNotShowed(this, MessageListActivity.class.getSimpleName())){
                        new GuidePage.Builder(this)
                                .setLayoutId(R.layout.view_find_legal_counsulting_guide_page_003)
                                .setKnowViewId(R.id.btn_home_act_enter_know)
                                .setPageTag(MessageListActivity.class.getSimpleName())
                                .builder()
                                .apply();
                    }
                    break;
            }
        }
    }
}
