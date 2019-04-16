package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;

import java.util.List;

/**
 * Created by amoldZhang on 2018/12/17.
 */

public class HeadViewDataUtils<T> {

    private CustomListView homeRecommendLv;
    private Activity myActivity;
    private int layout;
    private List<T> listData;
    private QuickAdapter<T> mAdapter;

    public HeadViewDataUtils(Activity myActivity, CustomListView homeRecommendLv,int layout, List<T> listData){
        this.myActivity = myActivity;
        this.homeRecommendLv = homeRecommendLv;
        this.layout = layout;
        this.listData = listData;

        initView();
    }

    private void initView() {
        mAdapter = new QuickAdapter<T>(myActivity, layout, listData) {
            @Override
            protected void convert(BaseAdapterHelper helper, T item, int position) {
                   if (adapter != null){
                       adapter.setAdapter(helper,item,position);
                   }
            }
        };
        homeRecommendLv.setAdapter(mAdapter);
        homeRecommendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null){
                    adapter.onItemClick(view,position);
                }
            }
        });
    }
    private MyAdapter adapter;
    void setMyActivity(MyAdapter adapter){
        this.adapter = adapter;
    }
    public abstract static class MyAdapter<T>{
        void setAdapter(BaseAdapterHelper helper, T item, int position){};
        void onItemClick(View view, int position){};
    }
}
