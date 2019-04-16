package com.sxhalo.PullCoal.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.autonavi.rtbt.IFrameForRTBT;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.entity.User;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.NetworkEncryptionMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2017/1/7.
 */
public class WebViewActivity extends BaseActivity{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_bar_right_imageview)
    ImageView titleBarRight;
    @Bind(R.id.ss_htmlprogessbar)
    ProgressBar ssHtmlprogessbar;
    @Bind(R.id.wb_details)
    WebView wbDetails;

    @Bind(R.id.view_no_net)
    LinearLayout view_no_net;

    public String TAG = "web界面加载";
    private String APP_CACAHE_DIRNAME = "/webcache";
    private String news_url;
    private int status = 0;
    private boolean loadError;
    private String TYPE;
    private boolean flage = true; //判断跳转后是否需要刷新界面

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_view);
    }

    @Override
    protected void initTitle() {
        if (StringUtils.isEmpty(getIntent().getStringExtra("title"))){
        }else{
            String titleString = getIntent().getStringExtra("title");
            if (titleString.equals("资讯详情")){
                TYPE = "5";
            }else{
                TYPE = "7";
            }
            title.setText(titleString);
        }
        LinkedHashMap<String,String> parameMap = new LinkedHashMap<String, String>();
        news_url = getIntent().getStringExtra("URL") + "?data="+ NetworkEncryptionMode.setParameterEncryptionString(parameMap);
    }

    @Override
    protected void getData() {
        if (!StringUtils.isEmpty(getIntent().getStringExtra("articleId"))){
            //添加浏览上报
            try {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("targetId", getIntent().getStringExtra("articleId"));
                params.put("browseType", TYPE);
                new DataUtils(this,params).getUserBrowseRecordCreate(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // 启用javascript ,为了分享成功返回时界面空白时调用
            if (BaseUtils.isNetworkConnected(this)) {
                if (flage){
                    flage = false;
                    initWebView();
                    findView();
                }
            } else {
                view_no_net.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            GHLog.e("煤炭详情", e.toString());
        }
    }

    public void initWebView() throws Exception {
        wbDetails.onResume();
        wbDetails.setFocusableInTouchMode(false);
        wbDetails.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = wbDetails.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(false); //打开页面时， 自适应屏幕
        settings.setLoadWithOverviewMode(true);//打开页面时， 自适应屏幕
        settings.setSupportZoom(false);// 用于设置webview放大
        settings.setBuiltInZoomControls(false);



        //设置webView的缓存
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
//      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.i(TAG, "cacheDirPath=" + cacheDirPath);
        //设置数据库缓存路径
        settings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        settings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        settings.setAppCacheEnabled(false);


    }

    private void findView() {
        if (!TextUtils.isEmpty(news_url)) {
            wbDetails.setWebViewClient(new WebViewClient() {

                @TargetApi(android.os.Build.VERSION_CODES.M)
                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    // 这个方法在6.0才出现
                    int statusCode = errorResponse.getStatusCode();
                    System.out.println("onReceivedHttpError code = " + statusCode);
//                    if (404 == statusCode || 500 == statusCode) {
//                        loadError = true;
//                        view.loadUrl("about:blank");// 避免出现默认的错误界面
////                        view.loadUrl(mErrorUrl);
//                        //载入本地assets文件夹下面的错误提示页面404.html
//                        wbDetails.setVisibility(View.GONE);
//                        findViewById(R.id.error_image).setVisibility(View.VISIBLE);
//                    }
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    GHLog.i(TAG, "onLoadResource url=" + url);
//                    WebView.HitTestResult result = view.getHitTestResult();
//                    if (result.getType() == WebView.HitTestResult.PHONE_TYPE){
//                        try {
//                            Log.i(TAG, "WebView.HitTestResult.PHONE_TYPE = " + url);
//                            UIHelper.showCollTel(WebViewActivity.this,result.getExtra()+"");
//                        } catch (Exception e) {
//                            displayToast("请检查您的SIM卡");
//                        }
//                    }
                    super.onLoadResource(view, url);
                }

                /*　 类型名　                               意义
                 　　WebView.HitTestResult.UNKNOWN_TYPE                   未知类型
                 　　WebView.HitTestResult.PHONE_TYPE                     电话类型
                 　　WebView.HitTestResult.EMAIL_TYPE                     电子邮件类型
                 　　WebView.HitTestResult.GEO_TYPE                       地图类型
                 　　WebView.HitTestResult.SRC_ANCHOR_TYPE                超链接类型
                 　　WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE          带有链接的图片类型
                 　　WebView.HitTestResult.IMAGE_TYPE                     单纯的图片类型
                 　　WebView.HitTestResult.EDIT_TEXT_TYPE                 选中的文字类型
                 */
                @Override
                public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                    GHLog.i(TAG, "shouldOverrideUrlLoading url=" + url);
                    Uri uri = Uri.parse(url);
                    if (loadError) {
                        webview.stopLoading();
                        //载入本地assets文件夹下面的错误提示页面404.html
                        wbDetails.setVisibility(View.GONE);
                        findViewById(R.id.error_image).setVisibility(View.VISIBLE);
                    } else {
                        view_no_net.setVisibility(View.GONE);
                        if (uri.getScheme().equals("tel")) {
                            Map<String,String> map = new HashMap<String, String>();
                            map.put("tel",url.substring(4));
                            UIHelper.showCollTel(WebViewActivity.this,map,false);
                            return true;
                        }
                        webview.loadUrl(url);
                    }
                    return true;
                }


                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    GHLog.e(TAG, "onPageStarted");
//                    view.getHitTestResult().getExtra();
                    ssHtmlprogessbar.setVisibility(View.VISIBLE); // 显示加载界面
                }

                @Override
                public void onPageFinished(WebView view, String url) {
//                    String title = view.getTitle();
//                    GHLog.e(TAG, "onPageFinished WebView title=" + title);
//                    tv_topbar_title.setText(title);
//                    tv_topbar_title.setVisibility(View.VISIBLE);
                    ssHtmlprogessbar.setVisibility(View.GONE); // 隐藏加载界面
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    ssHtmlprogessbar.setVisibility(View.GONE); // 隐藏加载界面
//                    Toast.makeText(getApplicationContext(), "加载完成", Toast.LENGTH_LONG).show();
                }
            });

            wbDetails.setWebChromeClient(new WebChromeClient() {

                /**
                 * 当WebView加载之后，返回 HTML 页面的标题 Title
                 * @param view
                 * @param titleText
                 */
                @Override
                public void onReceivedTitle(WebView view, String titleText) {
                    if (null != titleText && !(titleText.contains("html") || titleText.contains("?"))){
                        GHLog.e(TAG, "onReceivedTitle " + titleText);
                        if (StringUtils.isEmpty(getIntent().getStringExtra("title"))){
                            title.setText(titleText);
                        }
                    }
                    //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
                    if(!TextUtils.isEmpty(titleText) && titleText.toLowerCase().contains("error")){
                        loadError = true;
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        if (titleText.contains("404") || titleText.contains("500") || titleText.contains("Error")) {
                            loadError = true;
                            view.loadUrl("about:blank");// 避免出现默认的错误界面
//                            view.loadUrl(mErrorUrl);
                            //载入本地assets文件夹下面的错误提示页面404.html
                            wbDetails.setVisibility(View.GONE);
                            ((View) findViewById(R.id.error_image)).setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress >= 100) {
                        ssHtmlprogessbar.setVisibility(View.GONE);
                    } else {
                        ssHtmlprogessbar.setProgress(newProgress);
                    }
                }

                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                    GHLog.e(TAG, "onJsAlert " + message);
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    result.confirm();
                    return true;
                }

                @Override
                public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//                    GHLog.e(TAG, "onJsConfirm " + message);
                    return super.onJsConfirm(view, url, message, result);
                }

                @Override
                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                    GHLog.e(TAG, "onJsPrompt " + url);
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }
            });

            // 通过addJavascriptInterface()将Java对象映射到JS对象
            //参数1：Javascript对象名
            //参数2：Java对象名
            wbDetails.addJavascriptInterface(new AndroidtoJs(), "js2Android");//AndroidtoJS类对象映射到js的test对象
            wbDetails.loadUrl(news_url,NetworkEncryptionMode.setWebHeard(this));
        }
    }

    // 继承自Object类
    public class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void eventTobuyCoal(String goodsId) {
            flage = false;
            System.out.print("goodsId== "+goodsId);
            Intent intent = new Intent();
            intent.setClass(mContext, DetailsWebActivity.class);
            Coal coal = new Coal();
            coal.setGoodsId(goodsId);
            intent.putExtra("Coal", coal);
            intent.putExtra("inforDepartId", "煤炭详情");
            startActivity(intent);
        }

        /**
         * 跳转零钱明细
         * @param goodsId
         */
        @JavascriptInterface
        public void open_pocket_details(String goodsId) {
            flage = true;
            getPersonal();
        }

        /**
         * 未登录回调
         */
        @JavascriptInterface
        public void open_login_page() {
            flage = true;
           String userId = SharedTools.getStringValue(mContext, "userId", "-1");
            if (userId.equals("-1")) {
                //未登录点击跳转登录界面
                showDaiLogLogin(mContext,"您当前尚未登录，是否去登录？");
            } else {
                GHLog.i("登录检测","您已经登录");
            }
        }

        /**
         * 未实名时弹框回调
         */
        @JavascriptInterface
        public void open_realname_auth() {
            flage = true;
            showDaiLog(mContext, getString(R.string.unable_real_name_the_authentication));
        }
    }

    private void showDaiLog(Activity mActivity, String message) {
        new RLAlertDialog(mActivity, "系统提示", message, "关闭",
                "立刻前往", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                startActivity(new Intent(mContext, BuyerCertificationActivity.class));
            }
        }).show();
    }

    private  void showDaiLogLogin(final Activity context, String message) {
        new RLAlertDialog(context, "系统提示",message, "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                context.finish();
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(context, RegisterAddLoginActivity.class);
                context.startActivity(intent);
            }
        }).show();
    }

    /**
     * 联网获取个人信息
     */
    private void getPersonal() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(mContext, params).getUserInfo(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        //登陆后跳转到要去的界面
                        Intent intent = new Intent(mContext, MyBalanceActivity.class);
                        intent.putExtra("UserEntity", dataMemager);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网获取当前用户状态", e.toString());
        }
    }


    @OnClick({R.id.title_bar_left})
    public void onClick(View view) {
        onBack();
    }

    /**
     * 使点击回退按钮不会直接退出整个应用程序而是返回上一个页面
     *
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && wbDetails.canGoBack()){
            if (!loadError){
                wbDetails.goBack();//返回上个页面
            }else{
                return super.onKeyDown(keyCode, event);//退出当前界面
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);//退出当前界面
    }


    private void onBack(){
        if (wbDetails.canGoBack() && !loadError){
            wbDetails.goBack();
        }else{
            finish();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        wbDetails.onPause();
    }

    /**
     *  字符串是不是数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {

        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
        Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");
        Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {
        Log.i(TAG, "delete file path=" + file.getAbsolutePath());
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
        }
    }
}
