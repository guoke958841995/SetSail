package com.sxhalo.PullCoal.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhalo.PullCoal.BuildConfig;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.UserContrast;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.retrofithttp.NetworkEncryptionMode;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.popwin.SelectSharePopupWindow;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.wxapi.QQShareUtils;
import com.sxhalo.PullCoal.wxapi.WXSceneUtils;
import com.tencent.connect.common.Constants;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

public class DetailsWebActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_bar_right_imageview)
    ImageView rightImage;
    @Bind(R.id.ss_htmlprogessbar)
    ProgressBar ssHtmlprogessbar;
    @Bind(R.id.wb_details)
    WebView wbDetails;
    @Bind(R.id.view_no_net)
    LinearLayout view_no_net;

    @Bind(R.id.btn_add)
    LinearLayout btn_add;

    @Bind(R.id.btn_buy_coal)
    TextView btnBuyCoal;
//    @Bind(R.id.btn_coal_collect)
//    LinearLayout btnCoalCollect;
    @Bind(R.id.iv_btn_coal_collect)
    ImageView ivBtnCoalCollect;
    @Bind(R.id.tv_btn_coal_collect)
    TextView tvBtnCoalCollect;
//    @Bind(R.id.btn_coal_contrast)
//    LinearLayout btnCoalContrast;
    @Bind(R.id.iv_btn_coal_contrast)
    ImageView ivBtnCoalContrast;
    @Bind(R.id.tv_btn_coal_contrast)
    TextView tvBtnCoalContrast;



    public String TAG = "web界面加载";
    private String APP_CACAHE_DIRNAME = "/webcache";

    private String coalId;
    private Coal coal;
    private String news_url;
    private int status = 0;
    private boolean loadError;
    private MyReceiver myReceiver;
    private QQShareUtils qqShareUtils;  //qq 分享工具
    private WXSceneUtils wxSceneUtils;

    private String upData = "-1";

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_details);
    }

    @Override
    protected void initTitle() {
        title.setText("煤炭详情");
        btnBuyCoal.setText("我要买煤");
//        if (!"-1".equals(getIntent().getStringExtra("orderCoal"))){
        initBroadcastReceiver();
//        }

    }

    @Override
    protected void getData() {
        try {
            setData();
            // 启用javascript ,为了分享成功返回时界面空白时调用
            if (BaseUtils.isNetworkConnected(this)) {
                initWebView();
                findView();
                getData(coalId);
            } else {
                view_no_net.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),true);
            GHLog.e("煤炭详情", e.toString());
        }
    }

    private void setData() {
        try {
            String url;
            coal = (Coal) getIntent().getSerializableExtra("Coal");
            String publishUser = StringUtils.isEmpty(coal.getPublishUser()) ? "-1" : coal.getPublishUser();
            String userId = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
            if (publishUser.equals(userId) && !publishUser.equals("-1")) {
                btnBuyCoal.setVisibility(View.GONE);
            } else {
                if ("-1".equals(getIntent().getStringExtra("orderCoal"))){
                    btnBuyCoal.setVisibility(View.GONE);
                }else {
                    GHLog.i("接收到电话号码：", coal.getPublishUserPhone());
                    btnBuyCoal.setVisibility(View.VISIBLE);
                }
            }
            coalId = coal.getGoodsId();
            if (coal.getGoodsId() != null) {
//                LinkedHashMap<String, String> parameMap = new LinkedHashMap<String, String>();
//                parameMap.put("goodsId", coalId);
//                url = "?data=" + NetworkEncryptionMode.setParameterEncryptionString(parameMap);
//                news_url = APIConfig.WEB_SERVICE + "coalGoods/info.html" + url;


                //http://www.17lm.com/coalGoodsInfo.html?goodsId=200000401000016820001
                news_url = new Config().getCOALGOODSINFO() + "?goodsId=" + coalId;  //煤炭详情

                if ("-1".equals(getIntent().getStringExtra("orderCoal"))){
                    news_url = new Config().getSHARE_RELEASE_COAL() + "?releaseNum="+ coal.getReleaseNum();
                }

                GHLog.e("煤炭详情news_url:  ", news_url);
            } else {
                news_url = null;
            }

            //分享按钮
            if ("0".equals(coal.getConsultingFee())){
                rightImage.setVisibility(View.VISIBLE);
                rightImage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_share));
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),true);
            GHLog.e("控件数据初始化", e.toString());
        }
    }

    private void initBroadcastReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_ACTION);
        registerReceiver(myReceiver, filter);
    }

    private void getData(String goodsId) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("goodsId", goodsId);
            params.put("regionCode", SharedTools.getStringValue(this, "adCode", ""));
            params.put("longitude", SharedTools.getStringValue(this, "longitude", ""));
            params.put("latitude", SharedTools.getStringValue(this, "latitude", ""));
            new DataUtils(this, params).getCoalGoodsInfo(new DataUtils.DataBack<APPData<Coal>>() {
                @Override
                public void getData(APPData<Coal> dataList) {
                    try {
                        if (dataList != null) {
                            coal = dataList.getEntity();
                            if ("0".equals(coal.getConsultingFee())){
                                rightImage.setVisibility(View.VISIBLE);
                                rightImage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_share));
                            }else{
                                rightImage.setVisibility(View.GONE);
                            }
//                            setView(coal);
                            //当前煤炭信息是否发布
                            if("1".equals(coal.getPublishState()) || "1.0".equals(coal.getPublishState())){
                                btnBuyCoal.setVisibility(View.VISIBLE);
                            }else{
                                btnBuyCoal.setVisibility(View.GONE);
                            }

                            if ("1".equals(coal.getIsCollection())){
                                upData = "1";
                            }else{
                                upData = "0";
                            }
                            setCoalCollect();

                            //判断对比布局
                            if (getifContrast(coal)){
//                                btnCoalContrast.setBackgroundResource(R.drawable.bull_send_car);
                                ivBtnCoalContrast.setImageResource(R.mipmap.icon_buy_coal_contarst_pressed);
                                tvBtnCoalContrast.setTextColor(getResources().getColor(R.color.btn_cancel_color_hell));
                            }else{
//                                btnCoalContrast.setBackgroundResource(R.drawable.bg_amount_layout_gary);
                                ivBtnCoalContrast.setImageResource(R.mipmap.icon_buy_coal_contarst_normal);
                                tvBtnCoalContrast.setTextColor(getResources().getColor(R.color.btn_cancel_color));
                            }
                        }
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),true);
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    GHLog.e("联网错误", e.toString());
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),true);
            GHLog.e("联网错误", e.toString());
        }
    }

    /**
     * 更新煤炭列表添加的对比数据显示
     * @param coals
     */
    public void setAddContrastView(Coal coals){
        List<UserContrast> contrastList =  getContrastList();
        if(contrastList != null){
            UserContrast userContrast = new UserContrast();
            userContrast.setGoodsId(coals.getGoodsId());
            userContrast.setCoalName(coals.getCoalName());
            if (contrastList.size() == 0){
                OrmLiteDBUtil.insert(userContrast);
//                btnCoalContrast.setBackgroundResource(R.drawable.bull_send_car);
                ivBtnCoalContrast.setImageResource(R.mipmap.icon_buy_coal_contarst_pressed);
                tvBtnCoalContrast.setTextColor(getResources().getColor(R.color.btn_cancel_color_hell));
            }else{
                if (getifContrast(coals)){
                    int num = OrmLiteDBUtil.getQueryAllNum(UserContrast.class);
                    if (num >=1){
                        OrmLiteDBUtil.deleteWhere(UserContrast.class,"goods_id",new String[]{coals.getGoodsId()});
                    }
//                    btnCoalContrast.setBackgroundResource(R.drawable.bg_amount_layout_gary);
                    ivBtnCoalContrast.setImageResource(R.mipmap.icon_buy_coal_contarst_normal);
                    tvBtnCoalContrast.setTextColor(getResources().getColor(R.color.btn_cancel_color));
                }else{
                    int num = OrmLiteDBUtil.getQueryAllNum(UserContrast.class);
                    if (num < 3){
                        OrmLiteDBUtil.insert(userContrast);
//                        btnCoalContrast.setBackgroundResource(R.drawable.bull_send_car);
                        ivBtnCoalContrast.setImageResource(R.mipmap.icon_buy_coal_contarst_pressed);
                        tvBtnCoalContrast.setTextColor(getResources().getColor(R.color.btn_cancel_color_hell));
                    }else{
                        ((BaseActivity)mContext).displayToast("您最多只能添加三个");
                    }
                }
            }
        }

    }

    /**
     * 是否已经添加进来
     * @param coals
     * @return
     */
    public boolean getifContrast(Coal coals){
        UserContrast userContrast = new UserContrast();
        userContrast.setGoodsId(coals.getGoodsId());
        userContrast.setCoalName(coals.getCoalName());
        for (UserContrast userContrast1 : getContrastList()){
            if (userContrast1.equals(userContrast)){
                return true;
            }
        }
        return false;
    }

    public List<UserContrast> getContrastList(){
        return (List<UserContrast>) OrmLiteDBUtil.getQueryAll(UserContrast.class);
    }

    public void initWebView() throws Exception {
        WebSettings settings = wbDetails.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true); //打开页面时， 自适应屏幕
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

    private void findView() throws Exception {
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
//                    }
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                    Log.i(TAG, "onLoadResource url=" + url);
                    super.onLoadResource(view, url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                    Log.i(TAG, "intercept url=" + url);
                    Uri uri = Uri.parse(url);
                    if (loadError) {
                        webview.stopLoading();
                        //载入本地assets文件夹下面的错误提示页面404.html
                        wbDetails.setVisibility(View.GONE);
                        btnBuyCoal.setVisibility(View.GONE);
                        findViewById(R.id.error_image).setVisibility(View.VISIBLE);
                    } else {
                        view_no_net.setVisibility(View.GONE);
                        if (uri.getScheme().equals("tel")) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("tel", url.substring(4));
                            map.put("callType", Constant.CALE_TYPE_COAL);
                            map.put("targetId", coal.getReleaseNum());
                            UIHelper.showCollTel(DetailsWebActivity.this, map, true);
                            return true;
                        } else if (uri.getScheme().equals("js") && uri.getAuthority().equals("map_information_department")) {
                            //跳转到导航页面   信息部
                            getSalesData();
                            return true;
                        } else if (uri.getScheme().equals("js") && uri.getAuthority().equals("map_mine_mouth")) {
                            //跳转到导航页面   煤矿
                            getMineData();
                            return true;
                        } else if (uri.getScheme().equals("js") && uri.getAuthority().equals("open_picture")) {
                            //跳转到磅单图片预览界面
                            jump2PreviewActivity(coal.getCoalReportPicUrl());
                            return true;
                        } else if (uri.getScheme().equals("js") && uri.getAuthority().equals("open_sales_picture")) {
                            //跳转到信息部图片预览界面
                            jump2PreviewActivity(coal.getCoalSalePicUrl());
                            return true;
                        } else if (uri.getScheme().equals("js") && uri.getAuthority().equals("open_mineMouth_picture")) {
                            //跳转到矿口图片预览界面
                            jump2PreviewActivity(coal.getMineMouthPicUrl());
                            return true;
                        }
                        webview.loadUrl(url);
                    }
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                    Log.e(TAG, "onPageStarted");
                    ssHtmlprogessbar.setVisibility(View.VISIBLE); // 显示加载界面
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    ssHtmlprogessbar.setVisibility(View.GONE); // 隐藏加载界面

                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    ssHtmlprogessbar.setVisibility(View.GONE); // 隐藏加载界面
                    Toast.makeText(getApplicationContext(), "加载完成", Toast.LENGTH_LONG).show();
                    // 断网或者网络连接超时
                    if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                        view.loadUrl("about:blank"); // 避免出现默认的错误界面
//                        view.loadUrl(mErrorUrl);
                        wbDetails.setVisibility(View.GONE);
                        btnBuyCoal.setVisibility(View.GONE);
                        view_no_net.setVisibility(View.VISIBLE);
                    }
                }
            });

            wbDetails.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (!TextUtils.isEmpty(title) && title.toLowerCase().contains("error")) {
                        loadError = true;
                    }
                    // android 6.0 以下通过title获取
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error") || title.contains("error")) {
                        loadError = true;
                        view.loadUrl("about:blank");// 避免出现默认的错误界面
//                            view.loadUrl(mErrorUrl);
                        //载入本地assets文件夹下面的错误提示页面404.html
                        wbDetails.setVisibility(View.GONE);
                        btnBuyCoal.setVisibility(View.GONE);
                        (findViewById(R.id.error_image)).setVisibility(View.VISIBLE);
                    }
//                    }
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
//                    Log.e(TAG, "onJsAlert " + message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    result.confirm();
                    return true;
                }

                @Override
                public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//                    Log.e(TAG, "onJsConfirm " + message);
                    return super.onJsConfirm(view, url, message, result);
                }

                @Override
                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                    Log.e(TAG, "onJsPrompt " + url);
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }
            });

            wbDetails.loadUrl(news_url, NetworkEncryptionMode.setWebHeard(this));
        }
    }

    private void jump2PreviewActivity(String url) {
        Intent intent = new Intent();
        intent.putExtra("url", url);//化验单图片链接
        intent.setClass(DetailsWebActivity.this, PicturePreviewActivity.class);
        startActivity(intent);
    }

    /**
     * 获取信息部详情。并跳转定位界面
     */
    private void getSalesData() {
        if (!StringUtils.isEmpty(coal.getCoalSalesId())) {
            try {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("coalSalesId", coal.getCoalSalesId());
                new DataUtils(this, params).getCoalSalesInfo(new DataUtils.DataBack<InformationDepartment>() {
                    @Override
                    public void getData(InformationDepartment dataInfor) {
                        try {
                            if (dataInfor == null) {
                                return;
                            }
                            UIHelper.showMap(DetailsWebActivity.this, dataInfor, "InformationDepartment");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取矿口详情。并跳转定位界面
     */
    private void getMineData() {
        if (!StringUtils.isEmpty(coal.getMineMouthId())) {
            try {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("mineMouthId", coal.getMineMouthId());
                new DataUtils(this, params).getCompaniesInfo(new DataUtils.DataBack<MineMouth>() {
                    @Override
                    public void getData(MineMouth dataList) {
                        try {
                            if (dataList == null) {
                                return;
                            }
                            UIHelper.showMap(DetailsWebActivity.this, dataList, "MineMouth");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.REFRESH_CODE) {
            getData();
        }
        if (requestCode == Constants.REQUEST_QZONE_SHARE) {
            if (qqShareUtils != null){
                qqShareUtils.setqZoneShareResultData(requestCode,resultCode,data);
            }
        }
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            if (qqShareUtils != null){
                qqShareUtils.setQqShareResultData(requestCode,resultCode,data);
            }
        }
    }

    @OnClick({R.id.title_bar_left, R.id.btn_buy_coal,R.id.title_bar_right_imageview,R.id.btn_coal_collect,R.id.btn_coal_contrast})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.title_bar_left:
                    onBackPressed();
                    break;
                case R.id.title_bar_right_imageview:
                    String  title =  coal.getCoalName();
                    String  targetUrl =  new Config().getSHARE_RELEASE_COAL() + "?releaseNum="+ coal.getReleaseNum()+"&share=1";
                    //捷达综合信息部块煤发热量5200KCal/kg一票价2元/吨
                    String  summary = coal.getCompanyName() + coal.getCoalName() + "发热量" +coal.getCalorificValue()+ "kCal/kg " +"一票价 "+coal.getOneQuote()+ "元/吨" ;
                    if(BaseUtils.isNetworkConnected(getApplicationContext())){
                        shareDailog(title,targetUrl,summary);
                    }else{
                        displayToast(getString(R.string.unable_net_work));
                    }
                    break;
                case R.id.btn_buy_coal:
                    try {
                        String userId = SharedTools.getStringValue(this, "userId", "-1");
                        // 判断是否登录
                        if (userId.equals("-1")) {
                            // 未登录
                            showDaiLogLogin("您当前尚未登录，是否去登录？");
                        } else {
                            //已登录
                            String isSelfRelease = StringUtils.isEmpty(coal.getIsSelfRelease()) ? "0" : coal.getIsSelfRelease();
                            if (isSelfRelease.equals("1")) {  //"0"不是自己  1是自己
                                //是自己发布的 提示用户不能买自己的煤
                                showDaiLog(getString(R.string.unable_buy_myself));
                            } else if (SharedTools.getStringValue(this,"coalSalesId","-1").equals(coal.getCoalSalesId())){
                                showDaiLog("您不能购买同一个信息部下的煤！");
                            }else {
                                //联网获取认证状态
                                getSelfAuthentication();
                            }

                        }
                    } catch (Exception e) {
                        GHLog.e("下单跳转", e.toString());
                    }
                    break;
                case R.id.btn_coal_collect:
                    if ("1".equals(coal.getIsCollection())){  //当前煤炭已经收藏
                        setCollectData(1); //联网取消收藏
                    }else{
                        setCollectData(0); //联网添加收藏
                    }
                    break;
                case R.id.btn_coal_contrast:
                    setAddContrastView(coal);
                    break;
            }
        } catch (Exception e) {
            GHLog.e("点击按钮", e.toString());
        }
    }

    /**
     * 处理煤炭收藏相关逻辑
     */
    private void setCollectData(int flage) {
        if (!StringUtils.isEmpty(coal.getGoodsId())) {
            try {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("goodsId", coal.getGoodsId());
                new DataUtils(this, params).getCoalGoodsColect(new DataUtils.DataBack<APPData<Coal>>() {
                    @Override
                    public void getData(APPData<Coal> data) {
                        try {
                            displayToast(data.getMessage());

                            if ("1".equals(coal.getIsCollection())){
                                coal.setIsCollection("0");
                            }else{
                                coal.setIsCollection("1");
                            }
                            setCoalCollect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getError(Throwable e) {
                        displayToast("服务器繁忙请稍后再试！");
                    }
                },flage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 更新煤炭收藏状态
     */
    private void setCoalCollect(){
        //判断收藏布局展示
        if ("1".equals(coal.getIsCollection())){
//            btnCoalCollect.setBackgroundResource(R.drawable.bull_send_car);
            ivBtnCoalCollect.setImageResource(R.mipmap.coal_collect_pressed);
            tvBtnCoalCollect.setTextColor(getResources().getColor(R.color.btn_cancel_color_hell));
            tvBtnCoalCollect.setText("已收藏");
        }else{
//            btnCoalCollect.setBackgroundResource(R.drawable.bg_amount_layout_gary);
            ivBtnCoalCollect.setImageResource(R.mipmap.coal_collect_normal);
            tvBtnCoalCollect.setTextColor(getResources().getColor(R.color.btn_cancel_color));
            tvBtnCoalCollect.setText("收藏");
        }
    }



    private void getSelfAuthentication() {
        try {
            new DataUtils(this, new LinkedHashMap<String, String>()).getUserRealnameAuthInfo(new DataUtils.DataBack<UserAuthenticationEntity>() {
                @Override
                public void getData(UserAuthenticationEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        checkStatus(StringUtils.isEmpty(dataMemager.getAuthState()) ? "100" : dataMemager.getAuthState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("煤种赋值", e.toString());
        }

    }

    private void showDaiLogLogin(String message) {
        new RLAlertDialog(this, "系统提示", message, "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                startActivityForResult(new Intent(DetailsWebActivity.this, RegisterAddLoginActivity.class), Constant.REFRESH_CODE);
            }
        }).show();
    }

    private void showDaiLog(String message) {
        new RLAlertDialog(this, "抱歉", message, "确定",
                "", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
            }
        }).show();
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

    /**
     * 添支付成功时接到通知 需要刷新界面
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RECEIVED_ACTION.equals(intent.getAction())) {
                getData();
            }
        }
    }

    /**
     * 校验用户状态 只有实名用户才可以委托货运
     */
    private void checkStatus(String cerrtificationState) {
        //0审核中1审核成功2审核失败 100未提交
        switch (Integer.valueOf(cerrtificationState)) {
            case 100: //未提交
                showDaiLog(DetailsWebActivity.this, getString(R.string.unable_real_name_the_authentication));
                break;
            case 0: //审核中
                displayToast(getString(R.string.under_review));
                break;
            case 1: //审核成功
                //不是自己发布的  进入的下订单界面
                Intent intent = new Intent(DetailsWebActivity.this, PlaceOrderActivity.class);
                intent.putExtra("Coal", coal);
                startActivity(intent);
                break;
            case 2: //审核失败
                showDaiLog(DetailsWebActivity.this, getString(R.string.submit_under_review_failed));
                break;
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
                startActivity(new Intent(DetailsWebActivity.this, BuyerCertificationActivity.class));
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null){
            unregisterReceiver(myReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (upData.equals(coal.getIsCollection())){
            intent.putExtra("upData","-1");
        }else{
            intent.putExtra("upData","1");
        }
        setResult(RESULT_OK,intent);
        finish();
    }
}
