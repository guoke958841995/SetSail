package com.sxhalo.PullCoal.ui.pullrecyclerview.view.cardview;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.sxhalo.PullCoal.utils.GHLog;


public class ShadowTransformer implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.5f;//缩放的比例
    private static final float MIN_ALPHA = 0.5f;//透明度的比例
    private static final float MOVE_Y = 3;//设置y轴移动的基数

    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;

    // 当前ViewPager展示页
    private int currentPosition = 1;

    public ShadowTransformer(ViewPager viewPager, CardAdapter adapter) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
    }

    /**
     *  显示布局和隐藏布局是否等比例
     * @param enable
     */
    public void enableScaling(boolean enable) {
        if (mScalingEnabled && !enable) {
            // shrink main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        }else if(!mScalingEnabled && enable){
            // grow main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1f);
            }
        }

        mScalingEnabled = enable;
    }


    /***
     *  设置 viewPage的滑动切换动画
     * @param page
     * @param position
     */
    @Override
    public void transformPage(View page, float position) {
        if (position < -1 || position > 1) {
            page.setAlpha(MIN_ALPHA);
            if (mScalingEnabled){
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            }
        } else {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            if (mScalingEnabled){
                if (position < 0) {
                    float scaleX = 1 + 0.3f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.3f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
            }
            // 左滑 [0,-1] 不透明->半透明    右滑 [1,0] 半透明->不透明
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        int realCurrentPosition;
//        int nextPosition;
//        float baseElevation = mAdapter.getBaseElevation();
//        float realOffset;
//        boolean goingLeft = mLastOffset > positionOffset;
//
//        // If we're going backwards, onPageScrolled receives the last position
//        // instead of the current one
//        if (goingLeft) {
//            realCurrentPosition = position + 1;
//            nextPosition = position;
//            realOffset = 1 - positionOffset;
//        } else {
//            nextPosition = position + 1;
//            realCurrentPosition = position;
//            realOffset = positionOffset;
//        }
//
//        // Avoid crash on overscroll
//        if (nextPosition > mAdapter.getCount() - 1
//                || realCurrentPosition > mAdapter.getCount() - 1) {
//            return;
//        }
//
//        CardView currentCard = mAdapter.getCardViewAt(realCurrentPosition);
//
//        // This might be null if a fragment is being used
//        // and the views weren't created yet
//        if (currentCard != null) {
//            if (mScalingEnabled) {
//                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffset)));
//                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffset)));
//            }
//            currentCard.setCardElevation((baseElevation + baseElevation
//                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffset)));
//        }
//
//        CardView nextCard = mAdapter.getCardViewAt(nextPosition);
//        // We might be scrolling fast enough so that the next (or previous) card
//        // was already destroyed or a fragment might not have been created yet
//        if (nextCard != null) {
//            if (mScalingEnabled) {
//                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
//                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
//            }
//            nextCard.setCardElevation((baseElevation + baseElevation
//                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
//        }
//        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // SCROLL_STATE_IDLE ：空闲状态
        // SCROLL_STATE_DRAGGING ：滑动状态
        // SCROLL_STATE_SETTLING ：滑动后滑翔的状态

        //若viewpager滑动未停止，直接返回
        if (state != ViewPager.SCROLL_STATE_IDLE){
            return;
        } else{
            //若当前为第一张，设置页面为倒数第二张
            if (currentPosition == 0) {
                mViewPager.setCurrentItem(mAdapter.getCount()-2,false);
                // 解决当前在第一页时，切换到倒数第一页的布局，即向左滑动，界面全灰bug
                mViewPager.setCurrentItem(mAdapter.getCount()-2,true);
            } else if (currentPosition == mAdapter.getCount()-1) {
                //若当前为倒数第一张，设置页面为第二张
                mViewPager.setCurrentItem(1,false);
                CardView currentCard = mAdapter.getCardViewAt(0);
                currentCard.setAlpha(MIN_ALPHA);
                mViewPager.setCurrentItem(1,true);  // 解决当前界面是最后一个时，切换到第一页的布局，即
            }
        }
    }
}
