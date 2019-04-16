package com.sxhalo.PullCoal.ui.recyclerviewmode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.zhy.srecyclerview.AbsRefreshHeader;


/**
 * 功能：
 */

public class TestRefreshHeader extends AbsRefreshHeader {

    private TextView refreshText;
    private ImageView imageView;

    private AnimationDrawable animCat;

    public TestRefreshHeader(Context context) {
        super(context);
    }

    public TestRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.refresh_view, this, false);
        imageView = (ImageView) view.findViewById(R.id.v_refresh);
        refreshText = (TextView) view.findViewById(R.id.tv_refresh);
        addView(view);

        reset();
    }

    // 初始化到未刷新状态
    public void reset() {
        if (animCat != null) {
            animCat.stop();
            animCat = null;
        }
        imageView.setImageResource(R.mipmap.loading_01);
    }


    /**
     * 如果需要设置刷新动画时间，可以重写此方法
     */
    @Override
    public int getRefreshDuration() {
        return 300;
    }

    /**
     * 如果需要设置头部的Gravity，可以重写此方法
     *
     * @return HEADER_CENTER，HEADER_BOTTOM
     */
    @Override
    public int getRefreshGravity() {
        return AbsRefreshHeader.HEADER_CENTER;
    }

    /**
     * 如果需要设置刷新高度，也就是刷新临界值，可以重写此方法
     */
    @Override
    public int getRefreshHeight() {
        return dip2px(60);
    }

    private int dip2px(float value) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    /**
     * SRecyclerView的onDetachedFromWindow被调用，可能SRecyclerView所在的界面要被销毁，
     * 如果子类中有动画等未完成，可以重写此方法取消动画等耗时操作，避免造成内存泄露
     */
    @Override
    public void srvDetachedFromWindow() {
//        if(clockView!=null){
//            clockView.resetClock();
//        }

    }

    @Override
    public void refresh(int state, int height) {
        switch (state) {
            case NORMAL:
                refreshText.setText("下拉刷新");
//                clockView.stopClockAnim();
                reset();
                break;
            case REFRESH:
                refreshText.setText("正在刷新...");
//                clockView.startClockAnim();
                releaseToRefresh();
                break;
            case PREPARE_NORMAL:
                refreshText.setText("下拉刷新");
//                clockView.setClockAngle(height);
                reset();
                break;
            case PREPARE_REFRESH:
                refreshText.setText("释放立即刷新");
//                clockView.setClockAngle(height);

                break;
        }
    }


    public void releaseToRefresh() {

        if (animCat == null) {
            imageView.setImageResource(R.drawable.refreshing_header_anim);
            animCat = (AnimationDrawable) imageView.getDrawable();
        }
        animCat.start();
    }

}
