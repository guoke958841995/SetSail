package com.sxhalo.PullCoal.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhalo.PullCoal.R;

public class ToastUtils {

    private Toast currentToast;

    /**
     * Toast提示
     *
     * @param text
     */
    public void displayToast(String text, Activity myActivity) {
        if (myActivity == null) return;

        if (currentToast == null) {
            currentToast = new Toast(myActivity);
        }

        View view = LayoutInflater.from(myActivity).inflate(R.layout.myself_toast, null);
        TextView tvToastText = view.findViewById(R.id.tv_toast_text);
        tvToastText.setText(text);
        tvToastText.setTypeface(Typeface.create("sans-serif-condensed", 0));
        currentToast.setDuration(Toast.LENGTH_SHORT);
        currentToast.setView(view);
        currentToast.show();
    }
}
