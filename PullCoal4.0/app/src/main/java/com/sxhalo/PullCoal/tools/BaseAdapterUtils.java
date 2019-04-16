package com.sxhalo.PullCoal.tools;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.sxhalo.PullCoal.model.UserMessage;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

/**
 * listView的简单使用工具类
 *  使用方法：
 * Created by amoldZhang on 2017/4/12.
 */
public class BaseAdapterUtils<T> implements SmoothListView.ISmoothListViewListener{

    /**
     * 当前activity
     */
    private Activity myActivity;
    /**
     * 当前界面ListView
     */
    private SmoothListView myListView;
    /**
     * 当前ListView 的渲染器
     */
    private QuickAdapter<T> mAdapter;
    /**
     * 当前ListView数据显示集合
     */
    private List<T> myListData = new ArrayList<T>();

    private int currentPage = 1;  //当前页数
    private boolean onRefresh = true;
    private boolean onLoadMore = true;

    //刷新时消息列表
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private List<UserMessage> listData;

    /**
     * listView　实现构造方法
     * @param myActivity
     * @param myListView  当前listView
     */
    public BaseAdapterUtils(Activity myActivity,SmoothListView myListView) {
        this.myActivity = myActivity;
        this.myListView = myListView;
    }

    /**
     * 設置listView是否包含刷新，如果不設置則都爲true
     * @param onRefresh   下啦刷新 默認爲true
     * @param onLoadMore  上啦加載更多 默認爲 true
     */
    public void settingList(boolean onRefresh,boolean onLoadMore){
     this.onRefresh = onRefresh;
     this.onLoadMore = onLoadMore;
    }

    /**
     * listView 初始化
     * @param layoutResId   当前item布局
     */
    public void setViewItemData(int layoutResId,List<T> myListData){
        mAdapter = new QuickAdapter<T>(myActivity, layoutResId,myListData) {
            @Override
            protected void convert(BaseAdapterHelper helper, T t, int pos) {
                try {
                    if (baseAdapterBack != null){
                        baseAdapterBack.getItemViewData(helper,t,pos);
                    }
                } catch (Exception e) {
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        // 设置ListView数据 是否刷新
        myListView.setRefreshEnable(onRefresh);
        // 设置ListView数据 是否加载更多
        myListView.setLoadMoreEnable(onLoadMore);
        myListView.setDrawingCacheEnabled(true);
        myListView.setSmoothListViewListener(this);
        myListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        myListView.setAdapter(mAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (baseAdapterBack != null){
                        baseAdapterBack.getOnItemClickListener(position,mAdapter);
                    }
                } catch (Exception e) {
                    Log.i("", e.toString());
                }
            }
        });
    }

    /**
     * listView 的数据刷新
     * @param t
     */
    public void refreshData(List<T> t){
        if (currentPage == 1){
            myListData.clear();
            for (int i=0;i<t.size();i++) {
                myListData.add(i, t.get(i));
            }
            mAdapter.replaceAll(t);
        }else{
            myListData.addAll(t);
            mAdapter.addAll(t);
        }
        if (myListData.size()<10){
            myListView.setLoadMoreEnable(false);
        }else{
            myListView.setLoadMoreEnable(true);
        }
        GHLog.i(" t 中的个数", t.size()+"");
        GHLog.i("当前数据条数：",mAdapter.getCount()+"条");
    }

    /**
     * listView 的数据刷新
     * @param t
     * @param focusList 头条个数
     */
    public void refreshData(List<T> t,List<T> focusList){
        if (currentPage == 1){
            myListData.clear();
            for (int i=0;i<t.size();i++) {
                myListData.add(i, t.get(i));
            }
            mAdapter.replaceAll(t);
        }else{
            myListData.addAll(t);
            mAdapter.addAll(t);
        }
        if ((myListData.size() + focusList.size())<10){
            myListView.setLoadMoreEnable(false);
        }else{
            myListView.setLoadMoreEnable(true);
        }
        GHLog.i(" t 中的个数", t.size()+"");
        GHLog.i("当前数据条数：",mAdapter.getCount()+"条");
    }

    /**
     * 删除列表指定位置数据具并刷新界面
     */
    public void deleteData(int pos){
        myListData.remove(pos);
        mAdapter.remove(pos);
    }

    public List<T> getListData() {
        return myListData;
    }

    /**
     * 界面刷新
     */
    public void refreshData(){
        mAdapter.replace();
    }

    /**
     * 获取当前列表条数
     * @return
     */
    public int getCount(){
       return  myListData.size();
    }


    public QuickAdapter<T> getAdapter(){
        return mAdapter;
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myListView.stopRefresh();
                myListData.clear();
                mAdapter.replace();
                currentPage = 1;
                if (baseAdapterBack != null){
                    baseAdapterBack.getOnRefresh(currentPage);
                }
                myListView.setRefreshTime("刚刚");
            }
        }, 200);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage++;
                if (baseAdapterBack != null){
                    baseAdapterBack.getOnLoadMore(currentPage);
                }
                myListView.stopLoadMore();
            }
        }, 200);
    }

    private BaseAdapterBack baseAdapterBack;
    public void setBaseAdapterBack(BaseAdapterBack baseAdapterBack) {
        this.baseAdapterBack = baseAdapterBack;
    }

    public interface BaseAdapterBack<T>{
        /**
         * item 賦值
         * @param helper
         * @param t
         * @param pos
         */
        void getItemViewData(BaseAdapterHelper helper, T t, int pos);

        /**
         * listView的item點擊監聽
         * @param position
         */
        void getOnItemClickListener(int position,QuickAdapter<T> mAdapter);

        /**
         * listView刷新
         * @param page
         */
        void getOnRefresh(int page);

        /**
         * listView的加載更多
         * @param page
         */
        void getOnLoadMore(int page);
    }
}
