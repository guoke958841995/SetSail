package com.sxhalo.PullCoal.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sxhalo.PullCoal.R;

/**
 * 自定义司机进度
 * Created by liz on 2018/1/8.
 */

public class MyProgressView extends View {

    private int mOuterColor = Color.BLUE;
    private int mInnerColor = Color.GRAY;
    private int mBorderWidth = 20;//px
    private int mTopTextSize;
    private int mTopTextColor;
    private int mBottomTextSize;
    private int mBottomTextColor;

    private Paint mOuterPaint, mInnerPaint, mTopTextPaint, mBottomTextPaint;

    //最大值
    private int progressMax = 100;

    //当前值
    private int progressCurrent;

    public void setProgressCurrent(int progressCurrent) {
        this.progressCurrent = progressCurrent;
        invalidate();//设置当前值后 需要不断绘制图形
    }

    public MyProgressView(Context context) {
        this(context, null);
    }

    public MyProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyProgressView);
        mOuterColor = array.getColor(R.styleable.MyProgressView_outerColor, mOuterColor);
        mInnerColor = array.getColor(R.styleable.MyProgressView_innerColor, mInnerColor);
        mBorderWidth = (int) array.getDimension(R.styleable.MyProgressView_borderWidth, mBorderWidth);
        mTopTextSize = array.getDimensionPixelSize(R.styleable.MyProgressView_topTextSize, mTopTextSize);
        mTopTextColor = array.getColor(R.styleable.MyProgressView_topTextColor, mTopTextColor);
        mBottomTextSize = array.getDimensionPixelSize(R.styleable.MyProgressView_bottomTextSize, mBottomTextSize);
        mBottomTextColor = array.getColor(R.styleable.MyProgressView_bottomTextColor, mBottomTextColor);
        array.recycle();

        mOuterPaint = new Paint();
        //设置抗锯齿效果
        mOuterPaint.setAntiAlias(true);
        //设置防抖动效果
        mOuterPaint.setDither(true);
        //设置空心线宽
        mOuterPaint.setStrokeWidth(mBorderWidth);
        //设置画笔颜色
        mOuterPaint.setColor(mOuterColor);
        //设置笔刷的样式
        // ROUND 圆形
        // SQUARE 方形
        mOuterPaint.setStrokeCap(Paint.Cap.ROUND);
        //画笔样式
        // Paint.Style.STROKE 一条线
        // Paint.Style.FILL 从起点开始，一直到终点为止，形成一扇形的绘制区。
        // Paint.Style.FILL_AND_STROKE 为扇形区再加上一个圈
        mOuterPaint.setStyle(Paint.Style.STROKE);


        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setDither(true);
        mInnerPaint.setStrokeWidth(mBorderWidth);
        mInnerPaint.setColor(mInnerColor);
        mInnerPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerPaint.setStyle(Paint.Style.STROKE);

        mTopTextPaint = new Paint();
        mTopTextPaint.setAntiAlias(true);
        mTopTextPaint.setColor(mTopTextColor);
        mTopTextPaint.setTextSize(mTopTextSize);

        mBottomTextPaint = new Paint();
        mBottomTextPaint.setAntiAlias(true);
        mBottomTextPaint.setColor(mBottomTextColor);
        mBottomTextPaint.setTextSize(mBottomTextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width > height ? height : width, width >= height ? height : width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画外圆弧
        RectF rectF = new RectF(mBorderWidth / 2, mBorderWidth / 2, getWidth() - mBorderWidth / 2, getWidth()- mBorderWidth / 2 );
        canvas.drawArc(rectF, -90, 360, false, mOuterPaint);

        //画内圆弧
        float sweepAngle = (float) progressCurrent / progressMax;
        canvas.drawArc(rectF, -90, sweepAngle * 360, false, mInnerPaint);
        //画文字
        String stepText = (int)(sweepAngle*100) + "%";
        String text = "资料完成";
        //画一片文字的矩形区域
        Rect textBounds = new Rect();
        mTopTextPaint.getTextBounds(stepText, 0, stepText.length(), textBounds);
        int dx = getWidth() / 2 - textBounds.width() / 2;
        int baseLine = getHeight() / 2;
        canvas.drawText(stepText, dx, baseLine, mTopTextPaint);

        Rect textBounds1 = new Rect();
        mBottomTextPaint.getTextBounds(text, 0, text.length(), textBounds1);
        int dx1 = getWidth() / 2 - textBounds1.width() / 2;
        int baseLine1 = getHeight() / 2 + textBounds.height();
        canvas.drawText(text, dx1, baseLine1, mBottomTextPaint);

    }


}
