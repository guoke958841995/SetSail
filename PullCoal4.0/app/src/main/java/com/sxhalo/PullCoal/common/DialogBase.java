package com.sxhalo.PullCoal.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.Utils;


public abstract class DialogBase extends Dialog implements DialogInterface {
    private Window window = null;
    private Context context = null;

    public DialogBase(Context context) {
        super(context, R.style.my_dialog);
        this.context = context;
        window = getWindow();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleContent();
        setContainer();
        setWindowParams();
    }

    public void setWindowParams() {
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (BaseUtils.getWindowsWidth((Activity) context) * 0.8); // 宽度设置为屏幕的0.8
        window.setAttributes(p);
    }

    public void addView(View view) {
        if (window == null) {
            return;
        }
        window.setContentView(view);
    }

    protected void onStop() {
    }

    public View.OnClickListener viewOnClickListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            OnClickListenEvent(v);
        }
    };
}
