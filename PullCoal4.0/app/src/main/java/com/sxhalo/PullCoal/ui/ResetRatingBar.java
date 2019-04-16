package com.sxhalo.PullCoal.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sxhalo.PullCoal.R;


/**
 * 重写RatingBar
 */

public class ResetRatingBar extends LinearLayout {

    @SuppressWarnings("unused")
    private boolean mClickable;
    private int starCount;
    @SuppressWarnings("unused")
    private OnRatingChangeListener onRatingChangeListener;
    private float starImageSize;
    private Drawable starEmptyDrawable;
    private Drawable starFillDrawable;

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    public void setmClickable(boolean clickable) {
        this.mClickable = clickable;
    }

    public void setStarFillDrawable(Drawable starFillDrawable) {
        this.starFillDrawable = starFillDrawable;
    }

    public void setStarEmptyDrawable(Drawable starEmptyDrawable) {
        this.starEmptyDrawable = starEmptyDrawable;
    }

    public void setStarImageSize(float starImageSize) {
        this.starImageSize = starImageSize;
    }

    /**
     * @param context
     * @param attrs
     */
    @SuppressLint("Recycle")
    public ResetRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ResetRatingBar);
        starImageSize = mTypedArray.getDimension(R.styleable.ResetRatingBar_starImageSize, 20);
        starCount = mTypedArray.getInteger(R.styleable.ResetRatingBar_starCount, 5);
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.ResetRatingBar_starEmpty);
        starFillDrawable = mTypedArray.getDrawable(R.styleable.ResetRatingBar_starFill);
        mClickable = mTypedArray.getBoolean(1,false);
        for (int i = 0; i < starCount; ++i) {
            ImageView imageView = getStarImageView(context, attrs);
            addView(imageView);
        }
    }

    /**
     * @param context
     * @param attrs
     * @return
     */
    private ImageView getStarImageView(Context context, AttributeSet attrs) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams para = new ViewGroup.LayoutParams(
                Math.round(starImageSize),
                Math.round(starImageSize)
        );
        imageView.setLayoutParams(para);
        imageView.setPadding(0, 0, 5, 0);
        imageView.setImageDrawable(starEmptyDrawable);
        imageView.setMaxWidth(10);
        imageView.setMaxHeight(10);
        return imageView;
    }

    /**
     * setting start
     *
     * @param starCount
     */
    public void setStar(int starCount) {
        starCount = starCount > this.starCount ? this.starCount : starCount;
        starCount = starCount < 0 ? 0 : starCount;
        for (int i = 0; i < starCount; ++i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starFillDrawable);
        }

        for (int i = this.starCount - 1; i >= starCount; --i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starEmptyDrawable);
        }

    }

    /**
     * change stat listener
     */
    public interface OnRatingChangeListener {

        void onRatingChange(int RatingCount);

    }

}
