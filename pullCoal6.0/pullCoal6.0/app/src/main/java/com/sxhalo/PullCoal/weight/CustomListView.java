package com.sxhalo.PullCoal.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 *  自定义ListView 布局充满
 * Created by amoldZhang on 2019/4/12
 */
public class CustomListView extends ListView {

	public CustomListView(Context context) {
		super(context);
	}
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs); 
	} 

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
	} 

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec); 
	} 
} 
