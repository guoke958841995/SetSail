package com.sxhalo.PullCoal.weight;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.weight.dialog.DialogBase;


public class LoadProgressDialog extends DialogBase {
    private Context mContext;
    private String message;

    public LoadProgressDialog(Context context, String message) {
        super(context);
        this.mContext = context;
        this.message = message;
    }

    @Override
    public void setTitleContent() {

    }

    @Override
    public void setContainer() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loadprogress, null);

        addView(view);
        TextView tipTextView = (TextView) view.findViewById(R.id.tv_progress_bar_tip);
        if (TextUtils.isEmpty(message)) {
            tipTextView.setText("获取信息中，请稍候...");
        } else {
            tipTextView.setText(message);
        }
    }

    @Override
    public void OnClickListenEvent(View v) {

    }
}
