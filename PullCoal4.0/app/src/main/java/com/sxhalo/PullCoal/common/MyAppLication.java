package com.sxhalo.PullCoal.common;

import android.app.Activity;
import android.app.Application;
import com.litesuits.orm.LiteOrm;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.sina.weibo.sdk.auth.AuthInfo;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.tools.jpush.SettingJPush;
import com.sxhalo.PullCoal.ui.ColaProgress;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDex;

import java.util.Date;
import java.util.List;


/**
 * APP程序入口
 * Created by amoldZhang on 2016/12/2.
 */
public class MyAppLication extends Application {


    public static MyAppLication app;
    //数据库初始化
    private static LiteOrm liteOrm;

    private static ColaProgress mColaProgress;

    //初始化微信登陆IWXAPI
    public static IWXAPI mIWXAPI;
    //初始化qq登陆Tencent
    public  static Tencent mTencent;
//    public  static AuthInfo mAuthInfo;

    public static boolean isWxLogin = false;

    public static int ifLogin = 0;  //0 登录 1 修改密码
    private static SettingJPush jspush;

    public MyAppLication() {
        app = this;
    }

    public static synchronized MyAppLication getInstance() {
        if (app == null) {
            app = new MyAppLication();
        }
        return app;
    }

//    public static RefWatcher getRefWatcher(Context context) {
//        MyAppLication application = (MyAppLication) context.getApplicationContext();
//        return application.refWatcher;
//    }
//
//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedTools.getStringValue(this,"dns_ip","");
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        refWatcher =  LeakCanary.install(this);
        //数据库初始化
        initDatabase();
        //app异常接口上报
        serveAPP();

        getAlias();
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);     //UniversalImageLoader初始化
    }

    /**
     * 极光注册
     */
    public static SettingJPush getAlias() {
        if (jspush == null){
            jspush = new SettingJPush(app.getApplicationContext());
        }
        return jspush;
    }

    /**
     * 添加的app字体不随系统设置字体改变而改变
     *   需要 onConfigurationChanged(Configuration newConfig) 和 getResources() 方法配合
     *   可以写在 AppLication 或 BaseActivity 中。
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    /**
     *  设置 app字体不随 系统字体改变而改变
     *    与 onConfigurationChanged(Configuration newConfig) 方法配合使用
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    //为了适配5.0以下机型
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void serveAPP(){
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    /**
     * 数据库初始化
     */
    private  void initDatabase() {
        try {
            if(liteOrm == null){
                liteOrm = OrmLiteDBUtil.getLiteOrm(app.getApplicationContext());
            }
            OrmLiteDBUtil.createDb(app.getApplicationContext());
        } catch (Exception e) {
            GHLog.e("数据库创建失败",e.toString());
        }
    }


    /**
     * 关闭数据库
     */
    public void closeDB() {
        try {
            if (liteOrm != null) {
                liteOrm.close();
                liteOrm = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Tencent getTencent(){
        if (mTencent == null){
            mTencent = Tencent.createInstance(new Config().QQ_APPID,app.getApplicationContext());
        }
        return mTencent;
    }


//    public static AuthInfo getAuthInfo() {
//        if (mAuthInfo == null){
//            mAuthInfo = new AuthInfo(app.getApplicationContext(),
//                    new Config().Sina_APP_KEY,new  Config().Sina_REDIRECT_URL ,
//                    "all");
//        }
//        return mAuthInfo;
//    }

    public static IWXAPI getIWXAPI(){
        if (mIWXAPI == null){
            mIWXAPI = WXAPIFactory.createWXAPI(app.getApplicationContext(), new Config().weChat_APPID, false );
            mIWXAPI.registerApp(new Config().weChat_APPID);
        }
        return mIWXAPI;
    }

    public static void showDelog(Activity mActivity, String showText) {
        mColaProgress = ColaProgress.show(mActivity, showText, false, false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
//						Utils.showText(activity, 0, "联网中请稍后");
                        // dismissDelog();
                    }
                });
    }

    public static void dismissDelog() {
        if (mColaProgress != null) {
            mColaProgress.dismiss();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //整体摧毁的时候调用这个方法
        AppManager.getAppManager().AppExit(this);
        closeDB();
    }
}
