package com.sxhalo.PullCoal.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxhalo.PullCoal.BuildConfig;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 煤炭对比
 * Created by amoldZhang on 2018/8/13.
 */
public class CoalContrastActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    //    @Bind(R.id.scrollable_panel)
//    ScrollablePanel scrollablePanel;
    @Bind(R.id.ss_htmlprogessbar)
    ProgressBar ssHtmlprogessbar;
    @Bind(R.id.wb_details)
    WebView wbDetails;

    @Bind(R.id.view_no_net)
    LinearLayout view_no_net;

//    private ScrollablePanelAdapter scrollablePanelAdapter;

    private String news_url;
    public String TAG = "web界面加载";
    private String APP_CACAHE_DIRNAME = "/webcache";
    private boolean loadError;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_buy_contrast);
    }

    @Override
    protected void initTitle() {
        news_url = new Config().getCOAL_COMPARE() + "?" + getIntent().getStringExtra("URL");
    }

    @Override
    protected void getData() {
//        scrollablePanelAdapter = new ScrollablePanelAdapter(mContext,getImageManager());
//        generateTestData(scrollablePanelAdapter);
//        scrollablePanel.setPanelAdapter(scrollablePanelAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // 启用javascript ,为了分享成功返回时界面空白时调用
            if (BaseUtils.isNetworkConnected(this)) {
                initWebView();
                findView();
            } else {
                view_no_net.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
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
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    GHLog.i(TAG, "onLoadResource url=" + url);
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
//                    String titleString = view.getTitle();
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
            wbDetails.loadUrl(news_url);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wbDetails.onPause();
    }

//    private void generateTestData(ScrollablePanelAdapter scrollablePanelAdapter) {
//        List<String> roomInfoList = new ArrayList<>();
//        roomInfoList.add("发热量");
//        roomInfoList.add("粒度");
//        roomInfoList.add("挥发分");
//        roomInfoList.add("全水分");
//        roomInfoList.add("全硫分");
//        roomInfoList.add("固定碳");
//        roomInfoList.add("灰分");
//        roomInfoList.add("工艺");
//        roomInfoList.add("产地");
//        roomInfoList.add("距离");
//        roomInfoList.add("价格");
//        roomInfoList.add("化验单");
//        roomInfoList.add("");
//        scrollablePanelAdapter.setRoomInfoList(roomInfoList);
//
//        List<Coal> coalNameInfoList = (ArrayList<Coal>)getIntent().getSerializableExtra("contrastList");
//        scrollablePanelAdapter.setCoalNameInfoList(coalNameInfoList);
//
//        List<List<Coal>> ordersList = new ArrayList<>();
//        for (int i = 0; i < roomInfoList.size(); i++) {
//            List<Coal> orderInfoList = new ArrayList<>();
//            for (int j = 0; j < coalNameInfoList.size(); j++) {
//                orderInfoList.add(coalNameInfoList.get(j));
//            }
//            ordersList.add(orderInfoList);
//        }
//        scrollablePanelAdapter.setOrdersList(ordersList);
//
//    }


    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }
}
