package com.zhy.adapter.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 自定义网格布局的间距
 * Created by amoldZhang on 2018/7/25.
 */
public class RecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private  int leftDecoration; //左间距
    private  int topDecoration;   //top间距
    private  int rightDecoration; //右间距
    private  int bottomDecoration; //底部间距

    public RecyclerViewSpacesItemDecoration(int leftDecoration,int topDecoration,int rightDecoration,int bottomDecoration) {
        this.leftDecoration = leftDecoration;
        this.topDecoration = topDecoration;
        this.rightDecoration = rightDecoration;
        this.bottomDecoration = bottomDecoration;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.top = topDecoration;
        outRect.left = leftDecoration;
        outRect.right = rightDecoration;
        outRect.bottom = bottomDecoration;
    }
}

