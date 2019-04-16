package com.sxhalo.PullCoal.retrofithttp.progress;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.sxhalo.PullCoal.ui.daiglog.LoadProgressDialog;


/**
 * Created by amoldZhang on 2017/7/13.
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private LoadProgressDialog dialog;

    private Activity context;
    private boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;


    public ProgressDialogHandler(Activity context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable) {
        super();
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }

    private void initProgressDialog(String message){
        if (dialog == null){
            dialog = new LoadProgressDialog(context, message);
            dialog.setCancelable(cancelable);
            if (cancelable) {
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }
        }

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void dismissProgressDialog(){
        try {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog(msg.obj.toString());
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

}
