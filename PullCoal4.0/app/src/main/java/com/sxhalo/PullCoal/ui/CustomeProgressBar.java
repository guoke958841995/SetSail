package com.sxhalo.PullCoal.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.utils.DeviceUtil;

/**
 * 自定义进度条 目前仅用来显示司机认证情况
 * Created by liz on 2018/1/5.
 */

public class CustomeProgressBar extends ProgressBar {

    private Context mContext;
    private Paint mPaint;
    private int mProgress;
    private boolean mShowText = false;

    public CustomeProgressBar(Context context) {
        super(context, null, android.R.attr.progressBarStyleHorizontal);
        mContext = context;
        init();
    }

    public CustomeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setProgress(int progress) {
        super.setProgress(progress);
        this.mProgress = progress;
    }

    public void setShowText(boolean showText) {
        this.mShowText = showText;
    }

    private void init() {
        setMax(100);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(DeviceUtil.sp2px(mContext, 10f));
        mPaint.setTypeface(Typeface.MONOSPACE);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgress <= 40) {
            setProgressDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.pb_custome_yellow));
        } else {
            setProgressDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.pb_custome_green));
        }
        setProgress(mProgress);
        if (mShowText) {
            String text = mProgress + "%认证";
            Rect textRect = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), textRect);
            float textY = (getHeight() / 2) - textRect.centerY();
            canvas.drawText(text, 10, textY, mPaint);
        }
    }
}