package com.sxhalo.myexpandablelistview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by amoldZhang on 2016/10/22.
 */

public abstract class MyBaseListAdapter<E> extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflate;
    protected List<E> mList = new ArrayList<E>();

    public MyBaseListAdapter(Context mContext) {
        this.mContext = mContext;
        mInflate = LayoutInflater.from(mContext);
    }

    public MyBaseListAdapter(Context mContext,List<E> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mInflate = LayoutInflater.from(mContext);
    }

    /**
     * 返回数据列表条数
     * @return   条数
     */
    @Override
    public int getCount() {
        return mList.size()>0?mList.size():0;
    }

    /**
     * 返回下标下的实体数据
     * @param position  下标值
     * @return
     */
    @Override
    public E getItem(int position) {
        return (E)mList.get(position);
    }

    /**
     * 返回当前数据下标值
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 清空该控件中的所有数据
     */
    public void clearAll() {
        mList.clear();
    }

    /**
     * 获取该控件中的所有数据
     * @return
     */
    public List<E> getData() {
        return mList;
    }

    /**
     * 将数组整体添加进控件
     * @param lists   需要添加的数据
     */
    public void addALL(List<E> lists){
        if(lists==null||lists.size()==0){
            return ;
        }
        mList.addAll(lists);
    }

    /**
     * 添加一条对应实体
     * @param item  需要添加的对应实体
     */
    public void add(E item){
        mList.add(item);
    }

    /**
     * 移除对应的一条实体
     * @param e 对象实体
     */
    public void removeEntity(E e){
        mList.remove(e);
    }

}
