package com.sxhalo.PullCoal.ui.pullrecyclerview.view.cardview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.sxhalo.PullCoal.model.Coal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amoldZhang on 2019/2/25.
 */

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter{

    private List<CardFragment> mFragments;
    private float mBaseElevation;
    private List<Coal> myListData;

    public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation,List<Coal> myListData) {
        super(fm);
        mFragments = new ArrayList<>();
        mBaseElevation = baseElevation;
        setData(myListData);
    }

    public void setData(List<Coal> myListData) {
        this.myListData = myListData;
        addCardFragment(CardFragment.instantiate(myListData.get(myListData.size()-1)));
        for(int i = 0; i < myListData.size(); i++){
            addCardFragment(CardFragment.instantiate(myListData.get(i)));
        }
        addCardFragment(CardFragment.instantiate(myListData.get(0)));
        notifyDataSetChanged();
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mFragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }


    /**
     * 初始化一个条目
     * @param container
     * @param position 当前需要加载条目的索引
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFragments.set(position, (CardFragment) fragment);
        return fragment;
    }



    public void addCardFragment(CardFragment fragment) {
        mFragments.add(fragment);
    }


}
