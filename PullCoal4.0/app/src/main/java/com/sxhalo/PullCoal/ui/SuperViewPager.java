package com.sxhalo.PullCoal.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SuperViewPager extends ViewPager {

	public SuperViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SuperViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

//	private boolean noScroll = true;
//
//	public void setNoScroll(boolean noScroll) {
//		this.noScroll = noScroll;
//	}

	@Override
	public void scrollBy(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollBy(x, y);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		if (noScroll) {
//			return super.onTouchEvent(arg0);
//		} else {
//			return false;
//		}
//
//	}
//
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent arg0) {
//		// TODO Auto-generated method stub
////		if (noScroll) {
////			return super.onInterceptHoverEvent(arg0);
////		} else {
//			return false;
////		}
//
//	}

	@Override
	public void setCurrentItem(int item) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item);
	}

}
