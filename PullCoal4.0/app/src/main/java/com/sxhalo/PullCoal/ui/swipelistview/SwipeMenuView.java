package com.sxhalo.PullCoal.ui.swipelistview;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * 横向的LinearLayout，就是整个swipemenu的父布局
 * 主要定义了添加Item的方法及Item的属性设置
 *  注：SwipeMenuView​就是滑动时显示的View，
 *      看他的构造函数SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView)​；
 *      遍历Items：
 *      menu.getMenuItems();
 *      调用addItem方法向​SwipeMenuView中添加item
 *      在addItem方法中：每一个item 都是一个LinearLayout​。
 *
 * @author baoyz
 * @date 2014-8-24
 */
public class SwipeMenuView extends LinearLayout implements OnClickListener {

	private SwipeMenuListView mListView;
	private SwipeMenuLayout mLayout;
	private SwipeMenu mMenu;
	private OnSwipeItemClickListener onItemClickListener;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView) {
		super(menu.getContext());
		mListView = listView;
		mMenu = menu;
		// MenuItem的list集合
		List<SwipeMenuItem> items = menu.getMenuItems();
		int id = 0;
		//通过item构造出View添加到SwipeMenuView中
		for (SwipeMenuItem item : items) {
			addItem(item, id++);
		}
	}

	/**
	 * 将 MenuItem 转换成 UI控件,一个item就相当于一个垂直的LinearLayout，
	 * SwipeMenuView就是横向的LinearLayout，
	 */
	private void addItem(SwipeMenuItem item, int id) {
		//布局参数
		LayoutParams params = new LayoutParams(item.getWidth(),
				LayoutParams.MATCH_PARENT);
		LinearLayout parent = new LinearLayout(getContext());
		//设置menuitem的id，用于后边的点击事件区分item用的
		parent.setId(id);
		parent.setGravity(Gravity.CENTER);
		parent.setOrientation(LinearLayout.VERTICAL);
		parent.setLayoutParams(params);
		parent.setBackgroundDrawable(item.getBackground());
		//设置监听器
		parent.setOnClickListener(this);
		addView(parent);  //加入到SwipeMenuView中，横向的

		if (item.getIcon() != null) {
			parent.addView(createIcon(item));
		}
		if (!TextUtils.isEmpty(item.getTitle())) {
			parent.addView(createTitle(item));
		}

	}

	/**
	 *  创建img空件
	 * @param item
	 * @return
	 */
	private ImageView createIcon(SwipeMenuItem item) {
		ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(item.getIcon());
		return iv;
	}

	/**
	 * 根据参数创建title
	 * @param item
	 * @return
	 */
	private TextView createTitle(SwipeMenuItem item) {
		TextView tv = new TextView(getContext());
		tv.setText(item.getTitle());
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(item.getTitleSize());
		tv.setTextColor(item.getTitleColor());
		return tv;
	}

	/**
	 * 用传来的mLayout判断是否打开
	 * 调用onItemClick点击事件
	 */
	@Override
	public void onClick(View v) {
		if (onItemClickListener != null && mLayout.isOpen()) {
			onItemClickListener.onItemClick(this, mMenu, v.getId());
		}
	}

	public OnSwipeItemClickListener getOnSwipeItemClickListener() {
		return onItemClickListener;
	}

	/**
	 * 设置item的点击事件
	 * @param onItemClickListener
	 */
	public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setLayout(SwipeMenuLayout mLayout) {
		this.mLayout = mLayout;
	}

	/**
	 * 点击事件的回调接口
	 */
	public static interface OnSwipeItemClickListener {
		/**
		 * onClick点击事件中调用onItemClick
		 * @param view 父布局
		 * @param menu menu实体类
		 * @param index menuItem的id
		 */
		void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
	}
}
