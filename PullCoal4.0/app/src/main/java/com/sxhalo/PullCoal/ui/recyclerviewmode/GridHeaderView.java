package com.sxhalo.PullCoal.ui.recyclerviewmode;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.utils.GHLog;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 网格布局工具类
 * Created by amoldZhang on 2018/7/25.
 */
public class GridHeaderView<T> {


    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.right_layout)
    LinearLayout rightLayout;
    @Bind(R.id.title_bar_layout)
    RelativeLayout titleBarLayout;
    @Bind(R.id.my_srecycler_view)
    RecyclerView mySrecyclerView;

    @Bind(R.id.view_line)
    View viewLine;


    private Activity mActivity;
    private View view;
    private CommonAdapter<T> mAdapter;
    private int maxLength = -1; //最多展示条数 ，默认全部展示 即 maxLength = -1
    private List<T> mDatas;
    private int column;
    private boolean flage;

    public GridHeaderView(Activity mActivity, RecyclerView mContextRecyclerView,int maxLength,List<T> mDatas, int column,boolean flage) {
        this.mActivity = mActivity;
        this.maxLength = maxLength;
        this.column = column;
        this.flage = flage;
        view = LayoutInflater.from(mActivity).inflate(R.layout.recycler_view_layout, mContextRecyclerView, false);
        ButterKnife.bind(this, view);

        getView(R.layout.layout_fine_coal_list_item,mDatas);
    }

    private void getView(int layout,List<T> mDatas) {
        try {
            List<T> mDataList = new ArrayList<>();
            if (maxLength != -1){
                if (mDatas.size() <= maxLength){
                    maxLength = mDatas.size();
                }
                for (int i = 0;i < maxLength;i++){
                    if (i < mDatas.size()){
                        mDataList.add(i,mDatas.get(i));
                    }else{
                        mDataList.addAll(mDatas);
                    }
                }
            }else{
                mDataList.addAll(mDatas);
            }
            this.mDatas = mDataList;
            mySrecyclerView.setLayoutManager(new GridLayoutManager(mActivity, column));
            if (flage){
                mySrecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(5,0,5,0));
            }
            mAdapter = new CommonAdapter<T>(mActivity,layout, mDataList) {
                @Override
                protected void convert(ViewHolder holder, T t, int position) {
                    if (onClick != null){
                        onClick.setItemLayout(holder,t,position);
                    }
                }
            };

            mySrecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    if (onClick != null){
                        onClick.onGridItemClick(view,position);
                    }
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
        } catch (Exception e) {
            GHLog.e("界面控件控制", e.toString());
        }
    }


    public void dealWithTheView(List<T> mDatas){
        if (mDatas.size() == 0 ){
            view.setVisibility(View.GONE);
        }else{
            view.setVisibility(View.VISIBLE);
            List<T> mDataList = new ArrayList<>();
            if (maxLength != -1){
                for (int i = 0;i < maxLength;i++){
                    if (i < mDatas.size()){
                        mDataList.add(i,mDatas.get(i));
                    }else{
                        mDataList.addAll(mDatas);
                    }
                }
            }else{
                mDataList.addAll(mDatas);
            }
            this.mDatas = mDataList;
            if (mAdapter == null){
                getView(R.layout.layout_fine_coal_list_item,mDataList);
            }else{
                mySrecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    public View getView(){
        return view;
    }

    public void setViewLine(boolean flage){
        if (flage){
            viewLine.setVisibility(View.VISIBLE);
        }else{
            viewLine.setVisibility(View.GONE);
        }
    }

    public void setTitleText(String text){
        if (!TextUtils.isEmpty(text)){
            titleText.setText(text);
        }
    }

    @OnClick(R.id.right_layout)
    public void onViewClicked() {
        if (onClick != null){
            onClick.onGridTitleTextClick();
        }
    }

    private OnClickListener onClick;
    public void setOnClickListener(OnClickListener onClick){
        this.onClick = onClick ;
    }
    public static abstract class OnClickListener<T>{

        public void setItemLayout(ViewHolder holder, T T, int position){
        }
        public void onGridTitleTextClick() {
        }
        public void onGridItemClick(View v, int position) {
        }
    }
}
