package com.sxhalo.PullCoal.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * viewPager + fragment 显示渲染器
 * Created by amoldZhang on 2016/12/13.
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {

    private int mChildCount = 0;
    /**
     * 界面碎片集合
     */
    private ArrayList<Fragment> fragments;
    private List<String> tagList = new ArrayList<String>();

    /**
     * 对应的一个界面碎片
     */
    private FragmentManager fm;

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public MyViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }



    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        tagList.add(makeFragmentName(container.getId(), getItemId(position)));
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        super.destroyItem(container, position, object);
        tagList.remove(makeFragmentName(container.getId(), getItemId(position)));
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public void update(int position,Bundle data){
        Fragment fragment = fm.findFragmentByTag(tagList.get(position));
        if(fragment == null){
            return;
        }
        //这里唯一的要求是Fragment要实现UpdateAble接口
        if(fragment instanceof UpdateAble){
            ((UpdateAble)fragment).update(data);
        }
    }

    public interface UpdateAble {
        void update(Bundle data);
    }

}
