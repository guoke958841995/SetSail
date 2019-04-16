package com.sxhalo.PullCoal.net.subscribers;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.SendCarEntity;
import com.sxhalo.PullCoal.net.progress.ProgressCancelListener;
import com.sxhalo.PullCoal.net.progress.ProgressDialogHandler;
import com.sxhalo.PullCoal.utils.LogUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 */
public class ProgressObserver<T> implements Observer<T>, ProgressCancelListener {

    private boolean flag;
    private SubscriberOnNextListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;

    private Activity mContext;

    private Disposable disposable;

    public ProgressObserver(SubscriberOnNextListener subscriberOnNextListener, Activity context, boolean flag) {
        this.mSubscriberOnNextListener = subscriberOnNextListener;
        this.mContext = context;
        this.flag = flag;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, false);
    }

//
//    public ProgressObserver(SubscriberOnNextListener subscriberOnNextListener, boolean flag) {
//        this.mSubscriberOnNextListener = subscriberOnNextListener;
//        this.flag = flag;
//    }

    private void showProgressDialog(String massageString) {
        if (mProgressDialogHandler != null) {
            Message message = new Message();
            message.what = ProgressDialogHandler.SHOW_PROGRESS_DIALOG;
            message.obj = massageString;
            mProgressDialogHandler.handleMessage(message);
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if (flag) {
            showProgressDialog("加载中，请稍候...");
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Toast.makeText(mContext, "网络链接超时，请稍后再试！", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
//            Toast.makeText(mActivity, "服务器未启用，请联系服务器人员！", Toast.LENGTH_SHORT).show();
        } else if (e instanceof UnknownHostException) {
            Toast.makeText(mContext, "当前网络不可用，请检查网络是否正常！", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (!TextUtils.isEmpty(e.getCause().getMessage()))
                    Toast.makeText(mContext, e.getCause().getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {

            }
        }
        try {
            LogUtil.e("网络连接失败", "code = " + e.getMessage() + "Error ＝" + e.getCause().getMessage());
        } catch (Exception e1) {
        }
        if (flag) {
            dismissProgressDialog();
        } else {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onError(e);
        }
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        if (flag) {
            dismissProgressDialog();
        } else {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }


    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onCancel();
        }
    }
}
