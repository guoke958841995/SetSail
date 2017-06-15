package com.amoldzhang.iamhere.ui.view;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by amoldZhang on 2017/6/8.
 */

public class JSObject {

    private WebView mWebView;

    public JSObject(WebView webView) {
        this.mWebView = webView;
    }

        @JavascriptInterface
        public void javaMethod(final String p){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //update UI in main looper, or it will crash
                    Toast.makeText(mWebView.getContext(), p, Toast.LENGTH_SHORT).show();
                }
            });
        }

         @JavascriptInterface
        public void showAndroid(){
             new Handler(Looper.getMainLooper()).post(new Runnable() {
                 @Override
                 public void run() {
                     String info = "获取到的手机信息";
                     //调用JS中的 函数，当然也可以不传参
                     mWebView.loadUrl("javascript:androidCallJS('"+info+"')");
                 }
             });
        }

    public void javaCallJsFunction(int code){
        mWebView.loadUrl(String.format("javascript:javaCallJsFunction("+code+")"));
    }

}
