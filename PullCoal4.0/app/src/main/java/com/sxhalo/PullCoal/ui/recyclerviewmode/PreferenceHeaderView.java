package com.sxhalo.PullCoal.ui.recyclerviewmode;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.utils.GHLog;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2018/11/26.
 */
public class PreferenceHeaderView<T> {

    @Bind(R.id.my_srecycler_view)
    RecyclerView mySrecyclerView;

    private Activity mActivity;
    private int maxLength;
    private int column;
    private boolean flage;
    private View view;
    private List<T> mDatas = new ArrayList<>();
    private CommonAdapter<T> mAdapter;

    public PreferenceHeaderView(Activity mActivity, RecyclerView mContextRecyclerView, int maxLength, List<T> mDatas, int column, boolean flage) {
        this.mActivity = mActivity;
        this.maxLength = maxLength;
        this.column = column;
        this.flage = flage;
        view = LayoutInflater.from(mActivity).inflate(R.layout.preference_view_layout, mContextRecyclerView, false);
        ButterKnife.bind(this, view);
        getView(R.layout.layout_preference_list_item,mDatas);
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
                    if (position == 0){
                        holder.getView(R.id.view_line).setVisibility(View.VISIBLE);
                    }else{
                        holder.getView(R.id.view_line).setVisibility(View.GONE);
                    }
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
                        onClick.onListItemClick(view,position);
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
                getView(R.layout.layout_preference_list_item,mDataList);
            }else{
                mySrecyclerView.getAdapter().notifyDataSetChanged();
            }
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

    private OnClickListener onClick;
    public void setOnClickListener(OnClickListener onClick){
        this.onClick = onClick ;
    }
    public static abstract class OnClickListener<T>{

        public void setItemLayout(ViewHolder holder, T T, int position){
        }
        public void onListItemClick(View v, int position) {
        }
    }
}
