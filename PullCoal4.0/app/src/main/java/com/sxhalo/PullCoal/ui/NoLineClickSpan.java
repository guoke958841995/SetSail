package com.sxhalo.PullCoal.ui;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by liz on 2017/4/21.
 */

public class NoLineClickSpan extends ClickableSpan {

    private Context mContext;
    public NoLineClickSpan(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public void onClick(View widget) {
        if (myClickListener != null) {
            myClickListener.onClick();
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (myClickListener != null) {
            myClickListener.setTextColorAndUnderline(ds);
        }
    }

    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    private MyClickListener myClickListener;

    public interface MyClickListener{
        void onClick();
        void setTextColorAndUnderline(TextPaint ds);
    }

}
