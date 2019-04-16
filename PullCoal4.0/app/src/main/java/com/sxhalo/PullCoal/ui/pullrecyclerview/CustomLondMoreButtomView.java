package com.sxhalo.PullCoal.ui.pullrecyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import com.sxhalo.amoldzhang.library.LoadingLayoutBase;
import com.zhy.adapter.recyclerview.CommonAdapter;

import java.lang.reflect.Constructor;

/**
 * Created by amoldZhang on 2018/12/8.
 */
public class CustomLondMoreButtomView{


    private RecyclerView mRecyclerView;
    private Activity myActivity;
    private OnLoadMoreListener mOnLoadMoreListener;
    public WrapRecyclerAdapter mRefreshableView;
    private FrameLayout mLvFooterLoadingFrame;
    private LoadingLayoutBase mFooterLoadingView;

    /**
     * 是否执行加载更多
     */
    private boolean mLoading = false;

    /**
     * 是否开启加载更多
     */
    private boolean mLoadingMore = true;

    /**
     * 当前RecyclerView类型
     */
    protected LayoutManagerType layoutManagerType;

    /**
     * 最后一个的位置
     */
    private int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;

    public CustomLondMoreButtomView(Activity myActivity, RecyclerView mRecyclerView, CommonAdapter mAdapter) {
        this.myActivity = myActivity;
        this.mRecyclerView = mRecyclerView;
        mRefreshableView = new WrapRecyclerAdapter(mAdapter);
        setFooterLayout(new PullFooterLayout(myActivity));
    }

    public void notifyDataSetChanged(){
        mRefreshableView.notifyDataSetChanged();
    }


    public void setFooterLayout(LoadingLayoutBase footerLayout) {
        try {
            Constructor c = footerLayout.getClass().getDeclaredConstructor(new Class[]{Context.class});
            LoadingLayoutBase mFooterLayout = (LoadingLayoutBase)c.newInstance(new Object[]{myActivity});
            if(null != mFooterLayout) {
                mRefreshableView.removeFooterView(mLvFooterLoadingFrame);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
                mLvFooterLoadingFrame = new FrameLayout(myActivity);
                mLvFooterLoadingFrame.setLayoutParams(lp);

                mFooterLoadingView = mFooterLayout;
                mFooterLoadingView.setVisibility(View.GONE);
                mLvFooterLoadingFrame.addView(mFooterLoadingView);

                mRefreshableView.addFooterView(mLvFooterLoadingFrame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (!(layoutManager instanceof LinearLayoutManager)) {
                    return;
                }
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int lastCompletePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mRefreshableView.getItemCount() >= 10) {
                    if (mLoading) {
                        return;
                    }
                    if (lastCompletePosition == mRefreshableView.getItemCount() - 2) {
                        View child = linearLayoutManager.findViewByPosition(lastCompletePosition);
                        if (child == null)
                            return;
                        int deltaY = (recyclerView.getBottom() - recyclerView.getPaddingBottom()) - child.getBottom();
                        if (deltaY > 0) {
                            recyclerView.smoothScrollBy(0, -deltaY);
                        }
                    } else if (lastCompletePosition == mRefreshableView.getItemCount() - 1) {
                        mLoading = true;
                        switchLoadMoreState();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                if (layoutManagerType == null) {
                    if (layoutManager instanceof LinearLayoutManager) {
                        layoutManagerType = LayoutManagerType.LinearLayout;
                    } else if (layoutManager instanceof GridLayoutManager) {
                        layoutManagerType = LayoutManagerType.GridLayout;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        layoutManagerType = LayoutManagerType.StaggeredGridLayout;
                    } else {
                        throw new RuntimeException(
                                "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                    }
                }

                switch (layoutManagerType) {
                    case LinearLayout:
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case GridLayout:
                        lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case StaggeredGridLayout:
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        if (lastPositions == null) {
                            lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        }
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        lastVisibleItemPosition = findMax(lastPositions);
                        break;
                }
            }
        });
    }

    // 显示 '加载更多' 或者 '努力加载中'
    private void switchLoadMoreState() {
        if (mFooterLoadingView == null) {
            return;
        }
        if (!mLoadingMore) {
            mFooterLoadingView.setVisibility(View.VISIBLE);
            mFooterLoadingView.setPullLabel("");
        }else{
            mFooterLoadingView.setVisibility(View.VISIBLE);
            mFooterLoadingView.setLastUpdatedLabel("加载更多...");
            mFooterLoadingView.releaseToRefresh();
            if (mOnLoadMoreListener != null){
                mOnLoadMoreListener.onLoadMore();
            }
        }

    }

    public void setmLoadingMore(boolean mLoadingMore) {
        this.mLoadingMore = mLoadingMore;
        stopLoadingMore();
    }

    /**
     * 加载更多完成后调用
     */
    public void stopLoadingMore() {
        if (mRefreshableView != null) {
            mLoading = false;
            hideLoadMoreItem();

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (linearLayoutManager == null) {
                return;
            }
            int lp = linearLayoutManager.findLastVisibleItemPosition();

            if (mRefreshableView.getItemCount() - 1 == lp) {
                View child = linearLayoutManager.findViewByPosition(mRefreshableView.getItemCount() - 2);
                if (child == null) {
                    return;
                }
                // mRecyclerView topMargin=0 childPosition=0, mRecyclerView.getBottom即为height
                int inHeight = mRecyclerView.getBottom() - mRecyclerView.getPaddingBottom();
                int childBottom = child.getBottom();
                int deltaY = inHeight - childBottom;
                mRecyclerView.smoothScrollBy(0, -deltaY);
            }
        }
    }

    // 隐藏'加载更多'item
    void hideLoadMoreItem() {
        if (mFooterLoadingView == null) {
            return;
        }
        if (mFooterLoadingView.getVisibility() != View.INVISIBLE) {
            mFooterLoadingView.setVisibility(View.INVISIBLE);
            mFooterLoadingView.reset();
        }
    }

    /**
     * 取数组中最大值
     *
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public static enum LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }

}
