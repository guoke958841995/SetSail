package com.sxhalo.PullCoal.ui.scrolllayout.content;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Created by amoldZhang on 2018/11/13.
 */

public class MyScrollview extends NestedScrollView {


    private float preX;
    private float preY;
    private float touchSlop;
    private boolean isViewPagerDragged;

    private ScrollViewListener scrollViewListener = null;

    public MyScrollview(Context context) {
        super(context);
    }

    public MyScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    /**
     * 在onInterceptTouchEvent()方法里，
     * 如果水平移动距离大于竖直移动距离，ScrollView不拦截这个事件
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float currentX=ev.getX();
        float currentY=ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                preX=currentX;
                preY=currentY;
                isViewPagerDragged=false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(isViewPagerDragged){
                    return false;
                }
                float dx=Math.abs(preX-currentX);
                float dy=Math.abs(preY-currentY);

                if(dx>dy && dx>touchSlop){
                    isViewPagerDragged=true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isViewPagerDragged=false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    public interface ScrollViewListener {
        void onScrollChanged(MyScrollview scrollView, int l, int t, int oldl, int oldt);
    }
}
