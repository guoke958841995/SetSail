package com.sxhalo.PullCoal.ui.daiglog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
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
 * Created by liz on 2017/9/15.
 */

public class UpdateDailog extends Dialog {

    private Listener mListener;
    private Activity mContext;
    private Button btnUpdate, btnExit;
    private TextView tvTitle, tvContent;

    public UpdateDailog(@NonNull Activity context, UpdateApp updateApp, Listener listener) {
        super(context);
        this.mContext = context;
        this.mListener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉系统标题头
        setContentView(R.layout.update_app_dialog);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnExit = (Button) findViewById(R.id.btn_exit);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("最新版本：V" + updateApp.getVersion());
        tvContent.setText(updateApp.getRemark());
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
        wl.width = width * 2 / 3;
        wl.height = height * 3 / 4;
        //设置弹窗位置
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
                mListener.onLeftClick();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
                mListener.onRightClick();
            }
        });
        show();
    }

    /**
     *
     */
    public interface Listener {
        public void onLeftClick();

        public void onRightClick();
    }
}
