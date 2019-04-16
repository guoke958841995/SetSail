package com.sxhalo.PullCoal.ui.addrightview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 在布局的7右上角添加一个图片或布局
 *  中的子空件
 * Created by amoldZhang on 2018/8/10.
 */
public class RightTopView extends FrameLayout {

    private int width;
    private int height;
    private int childWidth;
    private int childHeight;

    public RightTopView(Context context) {
        super(context);
    }

    public RightTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RightTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount()!=2){
            throw new IllegalStateException("you should have two child ,你必须拥有两个子控件");
        }
        //测量总控件的宽和高
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //测量第二个子控件的宽和高
        childWidth = getChildAt(1).getMeasuredWidth();
        childHeight =getChildAt(1).getMeasuredHeight();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //对控件进行位置布局
        getChildAt(0).layout(childWidth/2,childHeight/2,width-childWidth/2,height-childHeight/2);
        getChildAt(1).layout(width-childWidth,0,width,childHeight);
    }
}
