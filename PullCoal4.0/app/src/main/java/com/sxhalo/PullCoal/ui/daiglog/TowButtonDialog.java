package com.sxhalo.PullCoal.ui.daiglog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;


/**
 * 自定义对话框
 * @author jinkai
 *
 */
public class TowButtonDialog extends RLDialog {
	private Listener listener;
	private CharSequence msg;
	private Context context;
	private Button btn_left, btn_right;
	private TextView tv_msg;
	private String leftBtnStr, rightBtnStr;
	private int linkify=-1;
	private View mView;

	/**
	 *
	 * @param context
	 * @param msg
	 * @param leftBtnStr
	 * @param rightBtnStr
	 * @param listener
	 */
	public TowButtonDialog(Context context, CharSequence msg, String leftBtnStr, String rightBtnStr, Listener listener) {
		super(context);
		this.msg=msg;
		this.leftBtnStr=leftBtnStr;
		this.rightBtnStr=rightBtnStr;
		this.listener=listener;
		this.context=context;
		super.createView();
		super.setCanceledOnTouchOutside(false);
	}
	/**
	 *
	 * @param context
	 * @param view
	 * @param leftBtnStr
	 * @param rightBtnStr
	 * @param listener
	 */
	public TowButtonDialog(Context context, View view, String leftBtnStr, String rightBtnStr, Listener listener) {
		super(context);
		this.leftBtnStr=leftBtnStr;
		this.rightBtnStr=rightBtnStr;
		this.listener=listener;
		this.context=context;
		this.mView = view;
		super.createView();
		super.setCanceledOnTouchOutside(false);
	}

	/**
	 *
	 * @param context
	 * @param msg
	 * @param msgLinkify
	 * @param leftBtnStr
	 * @param rightBtnStr
	 * @param listener
	 */
	public TowButtonDialog(Context context, CharSequence msg, int msgLinkify,
						   String leftBtnStr, String rightBtnStr, Listener listener) {
		super(context);
		this.msg=msg;
		this.linkify=msgLinkify;
		this.leftBtnStr=leftBtnStr;
		this.rightBtnStr=rightBtnStr;
		this.listener=listener;
		this.context=context;
		super.createView();
		super.setCanceledOnTouchOutside(false);
	}

	@Override
	protected View getView() {
		View view=LayoutInflater.from(context).inflate(R.layout.dialog_two_button, null);
		tv_msg=(TextView) view.findViewById(R.id.tv_msg);
		if (mView != null) {
			LinearLayout ll1 = (LinearLayout) view.findViewById(R.id.ll1);
			tv_msg.setVisibility(View.GONE);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			ll1.addView(mView,params);
		}
		tv_msg.setAutoLinkMask(linkify);
		tv_msg.setText(msg);
		btn_left=(Button) view.findViewById(R.id.btn_left);
		btn_left.setText(leftBtnStr);

		if (rightBtnStr != null){
			if(linkify == -1){
				btn_right=(Button) view.findViewById(R.id.btn_right);
				btn_right.setText(rightBtnStr);
			}else {
				btn_right=(Button) view.findViewById(R.id.btn_right);
				btn_right.setText(rightBtnStr);
			}
		}
		btn_left.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view) {
				dismiss();
				listener.onLeftClick();
			}
		});
		btn_right.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view) {
				dismiss();
				listener.onRightClick();
			}
		});
		if (TextUtils.isEmpty(rightBtnStr)) {
			btn_right.setVisibility(View.GONE);
			view.findViewById(R.id.divider).setVisibility(View.GONE);
		}
		setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		});
		return view;
	}

	/**
	 *
	 */
	public interface Listener{
		public void onLeftClick();
		public void onRightClick();
	}

}
