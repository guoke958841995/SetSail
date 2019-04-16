package com.sxhalo.PullCoal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;


import com.sxhalo.PullCoal.retrofithttp.api.MyException;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<E> extends BaseAdapter {

    protected List<E> mList = new ArrayList<E>();
    protected Context mContext;
    protected LayoutInflater mInflater;

    public static final int ONE_SCREEN_COUNT = 7; // 一屏能显示的个数，这个根据屏幕高度和各自的需求定
    public static final int ONE_REQUEST_COUNT = 10; // 一次请求的个数

    public BaseListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public BaseListAdapter(Context context, List<E> list) {
        this(context);
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void clearAll() {
        mList.clear();
    }

    public List<E> getData() {
        return mList;
    }

    public void addALL(List<E> lists){
        if(lists==null||lists.size()==0){
            return ;
        }
        mList.addAll(lists);
    }
    public void add(E item){
        mList.add(item);
    }

    @Override
    public E getItem(int position){
        E e = null;
        try {
            position = position>=0?position:0;
            e = (E) mList.get(position);
        } catch (Exception e1) {
            Log.i("首页getItem", e1.toString());
        }
        return e;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeEntity(E e){
        mList.remove(e);
    }

    /**
     * adapter刷新
     */
    public void OnRefresh(List<E> mallList) {
        this.mList = mallList;
        notifyDataSetChanged();
    }

}
