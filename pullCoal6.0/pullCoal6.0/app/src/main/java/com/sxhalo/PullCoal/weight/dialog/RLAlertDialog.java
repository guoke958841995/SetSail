package com.sxhalo.PullCoal.weight.dialog;

import android.content.Context;
import android.text.TextUtils;
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
public class RLAlertDialog extends RLDialog {
	private Listener listener;
	private CharSequence title, msg;
	private Context context;
	private Button btn_left, btn_right;
	private TextView tv_msg;
	private String leftBtnStr, rightBtnStr;
	private View mView;
	private boolean flage = false;

	/**
	 * 设置弹框弹出时，点击反回按钮是否响应
	 * @param flage
	 */
	public void setFlage(boolean flage){
		this.flage = flage;
	}

	/**
	 *
	 * @param context
	 * @param title
	 * @param msg
	 * @param leftBtnStr
	 * @param rightBtnStr
	 * @param listener
	 */
	public RLAlertDialog(Context context, String title, CharSequence msg, String leftBtnStr, String rightBtnStr, Listener listener) {
		super(context);
		this.title=title;
		this.msg=msg;
		this.leftBtnStr=leftBtnStr;
		this.rightBtnStr=rightBtnStr;
		this.listener=listener;
		this.context=context;
		super.createView();
		super.setCanceledOnTouchOutside(flage);
	}
	/**
	 *
	 * @param context
	 * @param title
	 * @param view
	 * @param leftBtnStr
	 * @param rightBtnStr
	 * @param listener
	 */
	public RLAlertDialog(Context context, String title, View view, String leftBtnStr, String rightBtnStr, Listener listener) {
		super(context);
		this.title=title;
		this.leftBtnStr=leftBtnStr;
		this.rightBtnStr=rightBtnStr;
		this.listener=listener;
		this.context=context;
		this.mView = view;
		super.createView();
		super.setCanceledOnTouchOutside(flage);
	}

	@Override
	protected View getView() {
		View view=LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
		TextView tv_title=(TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);
		tv_msg=(TextView) view.findViewById(R.id.tv_msg);
		if (mView != null) {
			LinearLayout ll1 = (LinearLayout) view.findViewById(R.id.ll1);
			tv_msg.setVisibility(View.GONE);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			ll1.addView(mView,params);
		}
		tv_msg.setText(msg);
		btn_left=(Button) view.findViewById(R.id.btn_left);
		btn_left.setText(leftBtnStr);

		if (rightBtnStr != null){
				btn_right=(Button) view.findViewById(R.id.btn_right);
				btn_right.setText(rightBtnStr);
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
		return view;
	}

	/**
	 *
	 */
	public interface Listener{
		 void onLeftClick();
		 void onRightClick();
	}

	public void setLiftBtnTest(String liftBtnTest){
       this.leftBtnStr = liftBtnTest;
		btn_left.setText(leftBtnStr);
	}
	public void setRightBtnTest(String rightBtnTest){
		this.rightBtnStr = rightBtnTest;
		btn_right.setText(rightBtnStr);
	}

	public Button getLiftBtn(){
		return btn_left;
	}
	public Button getRightBtn(){
		return btn_right;
	}

}
