package com.amoldzhang.iamhere.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.amoldzhang.iamhere.R;
import com.amoldzhang.iamhere.ui.view.JSObject;
import com.amoldzhang.iamhere.ui.view.JsInterface;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amoldZhang on 2017/6/8.
 */
public class JSCoalAndroidActivity extends BaseActivity {

    @Bind(R.id.resultText)
    TextView mReusultText;
    @Bind(R.id.webview)
    WebView mWebView;

    private String LOG_TAG = "JSCoalAndroidActivity";
    private WebSettings webSettings;


    //android调用JS网页的时候会用到
    private Handler mHandler = new Handler();

    private JsInterface JSInterface2;
    private JSObject jsObject;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_coal_android);
        ButterKnife.bind(this);

//        JSInterface2 = new JsInterface(mWebView);
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.addJavascriptInterface(JSInterface2, "JSInterface");
//        mWebView.setWebViewClient(new webviewClient());
//        mWebView.loadUrl("file:///android_asset/index2.html");

//        //WebSettings 几乎浏览器的所有设置都在该类中进行
        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(false);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        //支持多种分辨率，需要js网页支持
        webSettings.setUserAgentString("mac os");
        webSettings.setDefaultTextEncodingName("utf-8");


        jsObject = new JSObject(mWebView);
        /*
         * DemoJavaScriptInterface类为js调用android服务器端提供接口
         * android 作为DemoJavaScriptInterface类的客户端接口被js调用
         * 调用的具体方法在DemoJavaScriptInterface中定义：
         * 例如该实例中的clickOnAndroid
         */
        //在JSHook类里实现javascript想调用的方法，并将其实例化传入webview, "JSObject"这个字串告诉javascript调用哪个实例的方法
        mWebView.addJavascriptInterface(jsObject,"JSObject");
        mWebView.setWebViewClient(new MyWebChromeClient());
        //显示本地js网页
//        mWebView.loadUrl("file:///android_asset/index.html");
        mWebView.loadUrl("javascript:androidCallJS('"+getInfo()+"')");
        mWebView.loadUrl("file:///android_asset/heloJS.html");

        mReusultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsObject.javaCallJsFunction(1);
            }
        });
    }

    public String getInfo(){
        return "来自手机内的内容！！！";
    }

    class webviewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            mReusultText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSInterface2.javaCallJsFunction(1);
                }
            });
        }
    }


    class MyWebChromeClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(LOG_TAG, " url:"+url);
            view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true;
        }
    }
//
//
//
//    @Override
//    //设置回退
//    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
//            mWebView.goBack(); //goBack()表示返回WebView的上一页面
//            this.finish();
//            return true;
//        }
//        return false;
//    }
}
