package com.sxhalo.PullCoal.ui.pullrecyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.amoldzhang.library.LoadingLayoutBase;
import com.sxhalo.PullCoal.R;

/**
 * Created by amoldZhang on 2018/10/11.
 */
public class PullHeadViewLayout extends LoadingLayoutBase {

    private FrameLayout mInnerLayout;
    private TextView mHeaderText;
    private TextView mSubHeaderText;
    private ImageView mGoodsImage;

    private CharSequence mPullLabel;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;

    private AnimationDrawable animP;
    private float scaleOf; //当前布局滑动位置

    public PullHeadViewLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.pull_header_loadinglayout, this);
        mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
        mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
        mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
        mGoodsImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_cat);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        lp.gravity = Gravity.BOTTOM;

        // Load in labels
        mPullLabel = context.getString(R.string.smoothlistview_header_hint_normal);
        mRefreshingLabel = context.getString(R.string.smoothlistview_header_hint_loading);
        mReleaseLabel = context.getString(R.string.smoothlistview_header_hint_ready);

        reset();
    }



    // 获取"加载头部"高度
    @Override
    public int getContentSize() {
        return mInnerLayout.getHeight();
    }

    // 开始下拉时的回调
    @Override
    public void pullToRefresh() {
        mSubHeaderText.setText(mPullLabel);
    }

    // "加载头部"完全显示时的回调
    @Override
    public void releaseToRefresh() {
        mSubHeaderText.setText(mReleaseLabel);
    }

    // 下拉拖动时的回调
    @Override
    public void onPull(float scaleOfLayout) {
        if (scaleOfLayout > 0 && scaleOfLayout < getContentSize()/2){
                if (animP == null) {
                    mGoodsImage.setImageResource(R.drawable.refreshing_header_anim_pull);
                    animP = (AnimationDrawable) mGoodsImage.getDrawable();
                    mGoodsImage.post(new Runnable(){
                        @Override
                        public void run(){
                            if (animP != null){
                                animP.start();
                            }
                        }
                    });
                }
        }else if (scaleOfLayout >= getContentSize()/2 ){
            if (animP != null) {
                animP.stop();
                animP = null;
            }
            mGoodsImage.setImageResource(R.mipmap.loading_15);
        }

    }

    // 释放后刷新时的回调
    @Override
    public void refreshing() {
        mSubHeaderText.setText(mRefreshingLabel);

        if (animP != null) {
            animP.stop();
            animP = null;
        }

        if (animP == null) {
            mGoodsImage.setImageResource(R.drawable.refreshing_header_anim);
            animP = (AnimationDrawable) mGoodsImage.getDrawable();
        }
        mGoodsImage.post(new Runnable(){
            @Override
            public void run(){
                if (animP != null){
                    animP.start();
                }
            }
        });
    }

    // 初始化到未刷新状态
    @Override
    public void reset() {
        if (animP != null) {
            animP.stop();
            animP = null;
        }
        mGoodsImage.setImageResource(R.mipmap.loading_01);
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {
        mPullLabel = pullLabel;
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        mRefreshingLabel = refreshingLabel;
    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {
        mReleaseLabel = releaseLabel;
    }
}
