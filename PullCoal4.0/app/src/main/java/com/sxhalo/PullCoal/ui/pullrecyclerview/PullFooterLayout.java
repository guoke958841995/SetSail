package com.sxhalo.PullCoal.ui.pullrecyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.amoldzhang.library.LoadingLayoutBase;

/**
 * Created by amoldZhang on 2018/12/8.
 */

public class PullFooterLayout extends LoadingLayoutBase {

    private FrameLayout mInnerLayout;
    public ImageView mCatImage;
    private final TextView mCatText;
    private AnimationDrawable animCat;

    public PullFooterLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.pull_to_footer_loadinglayout, this);
        mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
        mCatImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_cat);
        mCatText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);

        try {
            LayoutParams lp = (LayoutParams) mInnerLayout.getLayoutParams();
            lp.width = LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        } catch (Exception e) {
            GHLog.e("PullFooterLayout",e.toString());
            e.printStackTrace();
        }

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
        Log.e("pullToRefresh", "pullToRefresh: 开始下啦");
    }

    // "加载头部"完全显示时的回调
    @Override
    public void releaseToRefresh() {
        if (animCat == null) {
            mCatImage.setVisibility(VISIBLE);
            mCatImage.setImageResource(R.drawable.refreshing_footer_anim);
            animCat = (AnimationDrawable) mCatImage.getDrawable();
        }
        animCat.start();
    }

    // 下啦拖动时的回调
    @Override
    public void onPull(float scaleOfLayout) {

    }

    // 释放后刷新时的回调
    @Override
    public void refreshing() {

    }

    // 初始化到未刷新状态
    @Override
    public void reset() {
        if (animCat != null) {
            animCat.stop();
            animCat = null;
        }
        mCatImage.setImageResource(R.mipmap.loading_01);
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {
        mCatImage.setVisibility(GONE);
        mCatText.setText(pullLabel);
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        mCatText.setText(refreshingLabel);
    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {
        mCatText.setText(releaseLabel);
    }
}
