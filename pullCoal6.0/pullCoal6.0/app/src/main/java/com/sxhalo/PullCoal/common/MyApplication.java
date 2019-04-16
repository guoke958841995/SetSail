package com.sxhalo.PullCoal.common;

import android.app.Application;
import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerApplicationComponent;
import com.sxhalo.PullCoal.dagger.module.ApplicationModule;
import com.sxhalo.PullCoal.dagger.module.HttpModule;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;

import io.paperdb.Paper;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
public class MyApplication extends Application {

    private static MyApplication mApplication;
    private ApplicationComponent applicationComponent;

    public static int ifLogin = 0;  //0 登录 1 修改密码

    //数据库初始化
    private static LiteOrm liteOrm;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .httpModule(new HttpModule())
                .build();

        //数据库初始化
        initDatabase();

        //初始化异常捕获
        initCrash();

        initPaper();
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        try {
            if (liteOrm == null) {
                liteOrm = OrmLiteDBUtil.getLiteOrm(getApplicationContext());
            }
            OrmLiteDBUtil.createDb(getApplicationContext());
        } catch (Exception e) {
            LogUtil.e("数据库创建失败", e.toString());
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


    /**
     * 初始化Paper
     */
    private void initPaper() {
        Paper.init(this);
    }

    /**
     * 初始化异常捕获
     */
    private void initCrash() {
        CrashHandler instance = CrashHandler.getInstance();
        instance.init(this);
    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    public static Context getContext() {
        return mApplication.getApplicationContext();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public void exits() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
