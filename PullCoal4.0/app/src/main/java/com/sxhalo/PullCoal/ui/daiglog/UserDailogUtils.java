package com.sxhalo.PullCoal.ui.daiglog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UpdateApp;
import com.sxhalo.PullCoal.utils.DeviceUtil;

/**
 * Created by amoldZhang on 2018/7/30.
 */

public class UserDailogUtils extends Dialog {

    private TextView cancelText;
    private View layout;
    private Activity mContext;
    private Listener mListener;

    public UserDailogUtils(@NonNull Activity context, View layout, Listener listener) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
        this.layout = layout;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layout);
        cancelText = (TextView)layout.findViewById(R.id.close_clear_cancel);
    }

    public void showDialog() {
        int height = DeviceUtil.getWindowHeight(mContext);
        int width = DeviceUtil.getWindowWidth(mContext);
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = width ;
        wl.height = width * 132/100;
        //设置弹窗位置
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
        cancelText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
                mListener.onDissmiss();
            }
        });
        show();
    }

    /**
     *
     */
    public interface Listener {
        public void onDissmiss();
        public void onClick();
    }
}
