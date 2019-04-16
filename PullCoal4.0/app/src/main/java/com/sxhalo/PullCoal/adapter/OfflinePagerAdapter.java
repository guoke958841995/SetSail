package com.sxhalo.PullCoal.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager数据
 */
public class OfflinePagerAdapter extends PagerAdapter {

	private View mAllOfflineMapList;
	private View mOfflineDowloadedList;

	private ViewPager mContentViewPager;

	public OfflinePagerAdapter(ViewPager contentViewPager,
							   View mAllOfflineMapList, View offlineDowloadedList) {
		mContentViewPager = contentViewPager;
		this.mOfflineDowloadedList = offlineDowloadedList;
		this.mAllOfflineMapList = mAllOfflineMapList;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if (arg1 == 0) {
			mContentViewPager.removeView(mAllOfflineMapList);
		} else {
			mContentViewPager.removeView(mOfflineDowloadedList);

		}

	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {

		if (arg1 == 0) {
			mContentViewPager.addView(mAllOfflineMapList);
			return mAllOfflineMapList;
		} else {
			mContentViewPager.addView(mOfflineDowloadedList);
			return mOfflineDowloadedList;
		}

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
