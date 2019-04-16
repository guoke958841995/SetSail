package com.sxhalo.PullCoal.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.utils.ContextUtils;
import com.sxhalo.PullCoal.utils.PaperUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * 自定义下拉刷新头部
 *
 * @author Xiao_
 * @date 2019/4/10 0010
 */
public class PullCoalHeader extends InternalAbstract implements RefreshHeader {

    public static String REFRESH_HEADER_PULLING = "下拉刷新";//"下拉刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新";//"正在刷新";
    public static String REFRESH_HEADER_RELEASE = "松开刷新";//"松开刷新";
    public static String REFRESH_HEADER_FINISH = "刷新完成";//"刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";//"刷新失败";

    public static String DATE_FORMAT = "M-d HH:mm"; // 日期格式

    private TextView tvRefreshTitle;
    private ImageView ivRefreshImage;
    private AnimationDrawable pullDownAnim;
    private AnimationDrawable refreshingAnim;

    private boolean mEnableLastTime = false;
    private TextView tvRefreshLastTime;
    private LinearLayout llRefreshHeader;

    protected DateFormat mLastUpdateFormat;

    public PullCoalHeader(Context context) {
        this(context, null);
    }

    public PullCoalHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected PullCoalHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.view_refresh_header, this);
        llRefreshHeader = view.findViewById(R.id.ll_pull_coal_refresh_header);
        ivRefreshImage = view.findViewById(R.id.iv_refresh_image);
        tvRefreshTitle = view.findViewById(R.id.tv_refresh_title);
        tvRefreshLastTime = view.findViewById(R.id.tv_refresh_last_time);


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PullCoalHeader);

        if (ta.hasValue(R.styleable.PullCoalHeader_enable_last_time)) {
            mEnableLastTime = ta.getBoolean(R.styleable.PullCoalHeader_enable_last_time, mEnableLastTime);
        }

        if (ta.hasValue(R.styleable.PullCoalHeader_header_background_color)) {
            int bgColor = ta.getColor(R.styleable.PullCoalHeader_header_background_color, 0);
            llRefreshHeader.setBackgroundColor(bgColor);
        }

        if (ta.hasValue(R.styleable.PullCoalHeader_refresh_title_text_size)) {
            int titleTextSize = ta.getDimensionPixelSize(R.styleable.PullCoalHeader_refresh_title_text_size, ContextUtils.sp2px(context, 12));
            tvRefreshTitle.getPaint().setTextSize(titleTextSize);
        }

        if (ta.hasValue(R.styleable.PullCoalHeader_refresh_title_text_color)) {
            int titleTextColor = ta.getColor(R.styleable.PullCoalHeader_refresh_title_text_color, Color.parseColor("#222222"));
            tvRefreshTitle.setTextColor(titleTextColor);
        }

        if (ta.hasValue(R.styleable.PullCoalHeader_last_time_text_size)) {
            int lastTimeTextSize = ta.getDimensionPixelSize(R.styleable.PullCoalHeader_last_time_text_size, ContextUtils.sp2px(context, 10));
            tvRefreshLastTime.getPaint().setTextSize(lastTimeTextSize);
        }

        if (ta.hasValue(R.styleable.PullCoalHeader_last_time_text_color)) {
            int lastTimeTextColor = ta.getColor(R.styleable.PullCoalHeader_last_time_text_color, Color.parseColor("#444444"));
            tvRefreshLastTime.setTextColor(lastTimeTextColor);
        }

        //显示or隐藏最后更新时间
        tvRefreshLastTime.setVisibility(mEnableLastTime ? VISIBLE : GONE);

        mLastUpdateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        setLastUpdateTime(new Date(PaperUtil.get(AppConstant.REFRESH_HEADER_UPDATE_LAST_TIME_KEY, System.currentTimeMillis())));
        //释放资源
        ta.recycle();
    }


    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (success) {
            tvRefreshTitle.setText(REFRESH_HEADER_FINISH);
            if (mEnableLastTime) {
                setLastUpdateTime(new Date());
            }
        } else {
            tvRefreshTitle.setText(REFRESH_HEADER_FAILED);
        }

        // 停止动画
        if (pullDownAnim != null && pullDownAnim.isRunning()) {
            pullDownAnim.stop();
        }
        if (refreshingAnim != null && refreshingAnim.isRunning()) {
            refreshingAnim.stop();
        }

        return super.onFinish(refreshLayout, success); //[可以设置延迟xxx秒之后再弹回]
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh: //下拉过程
                ivRefreshImage.setImageResource(R.drawable.anim_pull_start);
                pullDownAnim = (AnimationDrawable) ivRefreshImage.getDrawable();
                pullDownAnim.start();
                tvRefreshTitle.setText(REFRESH_HEADER_PULLING);
                break;
            case ReleaseToRefresh: //松开刷新
                tvRefreshTitle.setText(REFRESH_HEADER_RELEASE);
                break;
            case Refreshing: //刷新中
                ivRefreshImage.setImageResource(R.drawable.anim_pull_refreshing);
                refreshingAnim = (AnimationDrawable) ivRefreshImage.getDrawable();
                refreshingAnim.start();
                tvRefreshTitle.setText(REFRESH_HEADER_REFRESHING);
                break;
        }
    }


    public void setLastUpdateTime(Date time) {
        tvRefreshLastTime.setText("最后更新： " + mLastUpdateFormat.format(time));
        PaperUtil.put(AppConstant.REFRESH_HEADER_UPDATE_LAST_TIME_KEY, time.getTime());
    }

}

