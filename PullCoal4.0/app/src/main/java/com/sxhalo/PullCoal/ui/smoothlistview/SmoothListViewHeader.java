package com.sxhalo.PullCoal.ui.smoothlistview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;


public class SmoothListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	//	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	//	private Animation mRotateUpAnim;
//	private Animation mRotateDownAnim;
	private AnimationDrawable animP;

//	private final int ROTATE_ANIM_DURATION = 180;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public SmoothListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public SmoothListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.smoothlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.smoothlistview_header_arrow);
		mHintTextView = (TextView)findViewById(R.id.smoothlistview_header_hint_textview);

		reset();
//		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
//		mRotateUpAnim.setFillAfter(true);
//		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
//		mRotateDownAnim.setFillAfter(true);

	}

	// 初始化到未刷新状态
	public void reset() {
		if (animP != null) {
			animP.stop();
			animP = null;
		}
		mArrowImageView.setImageResource(R.mipmap.loading_20);
	}

	// 释放后刷新时的回调
	public void refreshing() {
		mHintTextView.setText(R.string.smoothlistview_header_hint_loading);
		if (animP == null) {
			mArrowImageView.setImageResource(R.drawable.refreshing_header_anim);
			animP = (AnimationDrawable) mArrowImageView.getDrawable();
		}
		mArrowImageView.post(new Runnable(){
			@Override
			public void run(){
				animP.start();
			}
		});
	}

	public void setState(int state) {
		if (state == mState) return ;
		switch(state){
			case STATE_NORMAL: //开始下啦
				reset();
				mHintTextView.setText(R.string.smoothlistview_header_hint_normal);
				mArrowImageView.setImageResource(R.mipmap.loading_20);
				break;
			case STATE_READY:  //达到触发条件
				mHintTextView.setText(R.string.smoothlistview_header_hint_ready);
				mArrowImageView.setImageResource(R.mipmap.loading_01);
				break;
			case STATE_REFRESHING: //正在加载中
				refreshing();
				break;
			default:
		}
		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
