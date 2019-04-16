package com.sxhalo.PullCoal.ui.daiglog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.DialogBase;
import com.sxhalo.PullCoal.utils.ContextUtils;
import com.sxhalo.PullCoal.utils.Utils;


/**
 * Created by 加载 on 2016/1/13.
 */
public class LoadProgressDialog extends DialogBase {
    private String message;

    public LoadProgressDialog(Activity context, String message) {
        super(context);
        this.message = message;
    }


    @Override
    public void setTitleContent() {

    }

    @Override
    public void setContainer() {
        View view = ContextUtils.getLayoutInflater(getContext()).inflate(
                R.layout.dialog_loadprogress, null);
        addView(view);
        TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);
        if (Utils.isStringEmpty(message)) {
            tipTextView.setText("获取信息中，请稍候...");
        } else {
            tipTextView.setText(message);
        }
    }

    @Override
    public void OnClickListenEvent(View v) {

    }
}
