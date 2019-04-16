package com.sxhalo.PullCoal.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sxhalo.PullCoal.retrofithttp.api.MyException;


/**
 * RecyclerView 高度自适应
 */
public class MyLayoutManage extends LinearLayoutManager {

    private int itemWidth;

    public MyLayoutManage(Context mContext, int itemWidth) {
        super(mContext);
        this.itemWidth = itemWidth;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        try {
            if (getChildCount() > 0){
                View view = recycler.getViewForPosition(0);
                if(view != null){
                    //            measureChild(view, widthSpec, heightSpec);
                    //            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
                    //            int measuredHeight = view.getMeasuredHeight();
                    //            setMeasuredDimension(measuredWidth, measuredHeight);

                    measureChild(view, itemWidth, heightSpec);
                    int measuredWidth = View.MeasureSpec.getSize(itemWidth);
                    int measuredHeight = view.getMeasuredHeight();
                    setMeasuredDimension(measuredWidth, measuredHeight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
