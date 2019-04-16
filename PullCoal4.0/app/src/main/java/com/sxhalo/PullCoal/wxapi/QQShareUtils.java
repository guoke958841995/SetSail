package com.sxhalo.PullCoal.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.util.ArrayList;

/**
 *  QQ分享实现工具
 * Created by amoldZhang on 2018/6/26.
 */
public class QQShareUtils {

    private Toast mToast;
    private Tencent mTencent;
    private Activity activity;

    public QQShareUtils(Activity activity){
        this.activity = activity;
        mTencent = MyAppLication.getTencent();
    }

    /**
     * qq 分享
     */
    public void setQShareCommit(String title,String targetUrl,String summary){
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        //这条分享消息被好友点击后的跳转URL。
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        //分享的消息摘要，最长50个字
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        //分享的图片URL
        String path = "http://pp.myapp.com/ma_icon/0/icon_42297866_1528688507/96";
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, path);
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "拉煤宝");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);

        doShareToQQ(params);
    }


    private void doShareToQQ(final Bundle params) {
        // QQ分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(activity, params, qqShareListener);
                }
            }
        });
    }

    /**
     * QQ 空间分享需要在主线程中实现
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void setQqShareResultData(int requestCode, int resultCode, Intent data){
        if (qZoneShareListener != null){
            Tencent.onActivityResultData(requestCode,resultCode,data,qqShareListener);
        }
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
//            toastMessage(activity, "分享成功");
        }
        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
//            toastMessage(activity, "onComplete: " + response.toString());
//            toastMessage(activity, "");
        }
        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
//            toastMessage(activity, "onError: " + e.errorMessage, "e");
        }
    };

    public void shareToQzone(String title,String targetUrl,String summary){
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,targetUrl);

        // 支持传多个imageUrl
        ArrayList<String> imageUrls = new ArrayList<String>();
        String path = "http://pp.myapp.com/ma_icon/0/icon_42297866_1528688507/96";
        imageUrls.add(path);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

//        Bundle bundle2 = new Bundle();
//        bundle2.putString(QzonePublish.HULIAN_EXTRA_SCENE, scene.getText().toString());
//        bundle2.putString(QzonePublish.HULIAN_CALL_BACK, callback.getText().toString());
//
//        params.putBundle(QzonePublish.PUBLISH_TO_QZONE_EXTMAP, bundle2);
        doShareToQzone(params);
    }

    /**
     * 用异步方式启动分享
     * @param params
     */
    private void doShareToQzone(final Bundle params) {
        // QZone分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQzone(activity, params, qZoneShareListener);
                }
            }
        });
    }

    /**
     * QQ 空间分享需要在主线程中实现
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void setqZoneShareResultData(int requestCode, int resultCode, Intent data){
        if (qZoneShareListener != null){
            Tencent.onActivityResultData(requestCode,resultCode,data,qZoneShareListener);
        }
    }

    IUiListener qZoneShareListener = new IUiListener() {

        @Override
        public void onCancel() {
//            toastMessage(activity, "onCancel:test ");
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
//            toastMessage(activity, "onError: " + e.errorMessage, "e");
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
//            toastMessage(activity, "onComplete: " + response.toString());
        }

    };

    /**
     * 打印消息并且用Toast显示消息
     *
     * @param activity
     * @param message
     *            填d, w, e分别代表debug, warn, error; 默认是debug
     */
    public  void toastMessage(final Activity activity,
                              final String message) {
        toastMessage(activity, message, null);
    }

    /**
     * 打印消息并且用Toast显示消息
     *
     * @param activity
     * @param message
     * @param logLevel
     *            填d, w, e分别代表debug, warn, error; 默认是debug
     */
    public void toastMessage(final Activity activity,
                             final String message, String logLevel) {
        if ("w".equals(logLevel)) {
            Log.w("sdkDemo", message);
        } else if ("e".equals(logLevel)) {
            Log.e("sdkDemo", message);
        } else {
            Log.d("sdkDemo", message);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }


    private void showToast(String text) {
        if (mToast != null && !activity.isFinishing()) {
            mToast.setText(text);
            mToast.show();
            return;
        }
        mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
