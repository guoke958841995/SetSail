package com.sxhalo.PullCoal.ui.popwin;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;


/**
 * Created by amoldZhang on 2018/6/28.
 */
public class SelectSharePopupWindow extends PopupWindow{
    private LayoutInflater inflater;
    private TextView btn_cancel;
    private LinearLayout item1,item2,item3,item4;
    private View mMenuView;

    public SelectSharePopupWindow(Activity context){
        super(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void setSelectSharePopupWindow(View.OnClickListener itemsOnClick) {
        mMenuView = inflater.inflate(R.layout.share_dialog_popupwindon, null);
        item1 = (LinearLayout) mMenuView.findViewById(R.id.wechat);
        item2 = (LinearLayout) mMenuView.findViewById(R.id.moments);
        item3 = (LinearLayout) mMenuView.findViewById(R.id.qq);
        item4 = (LinearLayout) mMenuView.findViewById(R.id.qzone);
        btn_cancel = (TextView) mMenuView.findViewById(R.id.cancel);
        //取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });
        item1.setOnClickListener(itemsOnClick);
        item2.setOnClickListener(itemsOnClick);
        item3.setOnClickListener(itemsOnClick);
        item4.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
//        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
        isShowing();
    }
}
