package com.sxhalo.PullCoal.retrofithttp.subscribers;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.retrofithttp.progress.ProgressCancelListener;
import com.sxhalo.PullCoal.retrofithttp.progress.ProgressDialogHandler;
import com.sxhalo.PullCoal.ui.daiglog.UpDataDialog;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Subscriber;


/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by amoldZhang on 2017/7/13.
 */
public class ProgressSubscriber <T> extends Subscriber<T> implements ProgressCancelListener {

    private boolean flage;
    private SubscriberOnNextListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;

    private Activity mActivity;

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Activity context, boolean flage) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = context;
        this.flage = flage;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, false);
    }

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, boolean flage) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.flage = flage;
    }

    private void showProgressDialog(String massageString){
        if (mProgressDialogHandler != null) {
            Message message = new Message();
            message.what = ProgressDialogHandler.SHOW_PROGRESS_DIALOG;
            message.obj = massageString;
            mProgressDialogHandler.handleMessage(message);
        }
    }

    private void dismissProgressDialog(){
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
    public void onStart() {
        if (flage){
            showProgressDialog("加载中，请稍候...");
        }
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        if (flage){
            dismissProgressDialog();
        }else{
            if (!this.isUnsubscribed()) {
                this.unsubscribe();
            }
        }
//        Toast.makeText(context, "网络连接成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Toast.makeText(mActivity,"网络链接超时，请稍后再试！", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
//            Toast.makeText(mActivity, "服务器未启用，请联系服务器人员！", Toast.LENGTH_SHORT).show();
        }  else if (e instanceof UnknownHostException) {
            Toast.makeText(mActivity, "当前网络不可用，请检查网络是否正常！", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if(!StringUtils.isEmpty(e.getCause().getMessage()))
                Toast.makeText(mActivity,e.getCause().getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {

            }
        }
        try {
            GHLog.e("网络连接失败","code = " + e.getMessage() + "Error ＝" + e.getCause().getMessage());
        } catch (Exception e1) {
        }
        if (flage){
            dismissProgressDialog();
        }else{
            if (!this.isUnsubscribed()) {
                this.unsubscribe();
            }
        }
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onError(e);
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
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onCancel();
        }
    }
}
