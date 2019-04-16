package com.sxhalo.PullCoal.ui.upmarqueeview;

/**
 * Created by liz on 2017/4/7.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.sxhalo.PullCoal.R;

import java.util.List;

/**
 * 仿淘宝首页的 淘宝头条滚动的自定义View
 *
 * Created by mengwei on 2016/7/20.
 */
public class UPMarqueeView extends ViewFlipper {

    private Context mContext;
    private boolean isSetAnimDuration = false;
    private int interval = 4000;//显示下一个动画时间
    /**
     * 动画时间
     */
    private int animDuration = 1000;

    public UPMarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;
        setFlipInterval(interval);
        Animation animIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_in);
        if (isSetAnimDuration) animIn.setDuration(animDuration);
        setInAnimation(animIn);
        Animation animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_marquee_out);
        if (isSetAnimDuration) animOut.setDuration(animDuration);
        setOutAnimation(animOut);
    }


    /**
     * 设置循环滚动的View数组
     *
     * @param views
     */
    public void setViews(final List<View> views) {
        if (views == null || views.size() == 0) return;
        removeAllViews();
        for ( int i = 0; i < views.size(); i++) {
            final int position=i;
            //设置监听回调
//            views.get(i).setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onItemClickListener != null) {
//                        onItemClickListener.onItemClick(position, views.get(position));
//                    }
//                }
//            });
            addView(views.get(i));
        }
        startFlipping();
    }

    /**
     * 点击
     */
    private OnItemClickListener onItemClickListener;

    /**
     * 设置监听接口
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * item_view的接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
}
