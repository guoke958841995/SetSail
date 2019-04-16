package com.sxhalo.PullCoal.adapter;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sxhalo.PullCoal.utils.BaseUtils;

/**
 *  设置 RecyclerView 的 左边距和右边距
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration{
    private Activity mActivity;
    private int halfSpaceInPx;

    public SpacingItemDecoration(Activity mActivity, int horizontalSpaceInPx) {
        halfSpaceInPx = (int) (0.5f * horizontalSpaceInPx);
        this.mActivity = mActivity ;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent != null) {
//                int childIndex=parent.getChildPosition(view);//deprecated
            int childIndex = parent.getChildAdapterPosition(view);
            RecyclerView.Adapter adapter = parent.getAdapter();
            if (adapter != null) {
                int childCount = adapter.getItemCount();
                outRect.left = halfSpaceInPx;
                outRect.right = halfSpaceInPx;
                if (childIndex != 0){
                    outRect.top = halfSpaceInPx;
                }

                //如果是横向的话，最左和最右间距相同
//                if (childIndex == 0) {//the first one，第一个，左边缘间距
//                    outRect.left = BaseUtils.dip2px(mActivity,12f);//12dp;
//                }
//                if (childIndex == childCount - 1) {//the last one,最后一个，右边缘间距
//                    outRect.right = BaseUtils.dip2px(mActivity,12f);//12dp
//                }
            }
        }
    }
}
