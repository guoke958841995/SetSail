package com.sxhalo.PullCoal.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.SystemUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by amoldZhang on 2017/5/18.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/sxhalo/log/";
    private static final String FILE_NAME = "crash";

    //log文件的后缀名
    private static final String FILE_NAME_SUFFIX = ".trace";

    private static CrashHandler sInstance = new CrashHandler();

    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

    //构造方法私有，防止外部构造多个实例，即采用单例模式
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    //这里主要完成初始化工作
    public void init(Context context) {
        //获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        this.mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
//            //导出异常信息到SD卡中
//            dumpExceptionToSDCard(ex);
            //这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
            uploadExceptionToServer(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //打印出当前调用栈信息
        ex.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                LogUtil.d(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        //以当前时间创建log文件
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出发生异常的时间
            pw.println(time);

            //导出手机信息
            dumpPhoneInfo(pw);

            pw.println();
            //导出异常的调用栈信息
            ex.printStackTrace(pw);

            pw.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        //应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

    private void uploadExceptionToServer(Throwable ex) {
        try {

            String crashReport = getCrashReport(MyApplication.getContext(), ex);
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            try {
                params.put("moduleName", ex.getMessage().substring(0, ex.getMessage().toString().length() < 200 ? ex.getMessage().toString().length() : 200)); //操作模块
            } catch (Exception e) {
                params.put("moduleName", "全局捕获异常"); //操作模块
            }

            try {
                params.put("operationName", params.get("moduleName")); //操作名称
                if (ex != null && ex.getCause() != null && ex.getCause().getMessage() != null) {
                    params.put("operationName", ex.getCause().getMessage().substring(0, ex.getMessage().toString().length() < 200 ? ex.getMessage().toString().length() : 200)); //操作名称
                }
            } catch (Exception e) {
                if (ex.getMessage() != null) {
                    int line = ex.getMessage().toString().length();
                    params.put("operationName", ex.toString().substring(0, line < 200 ? line : 200)); //操作名称
                }
            }
            params.put("machineIp", SystemUtil.getIpAddress(MyApplication.getContext())); //当前ip
            params.put("message", crashReport);

            //TODO  处理日志上传
//            new DataUtils(params).getLogExceptionCreate(new DataUtils.DataBack<APPData<Object>>() {
//                @Override
//                public void getData(APPData<Object> o) {
//                    //退出
//                    AppManager.getAppManager().AppExit(MyAppLication.app.getApplicationContext());
//                }
//            });
            //退出
//            AppManager.getAppManager().AppExit(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取APP崩溃异常报告
     *
     * @param ex
     * @return
     */
    private String getCrashReport(Context context, Throwable ex) {
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (pinfo == null) pinfo = new PackageInfo();
        StringBuffer exceptionStr = new StringBuffer();
        //应用的版本名称和版本号
        exceptionStr.append("App Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
        //android版本号
        exceptionStr.append("Android Version: " + Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")\n");
        //手机制造商 和 手机型号
        exceptionStr.append("OS Vendor: : " + Build.MANUFACTURER + "(" + Build.MODEL + ")\n");
        //cpu架构
        exceptionStr.append("CPU ABI: " + Build.CPU_ABI + "(" + Build.CPU_ABI + ")\n");
        //异常日志
        exceptionStr.append("Exception: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return exceptionStr.toString();
    }

    private Map<String, String> addDeviceType(Map<String, String> params) {
        String device_model = Build.BRAND; // 设备型号
        String device_sdk = Build.VERSION.SDK; // 设备SDK版本
        String device_release = Build.VERSION.RELEASE; // 设备的系统版本
        String device_id = SystemUtil.getDeviceID();
        if (!TextUtils.isEmpty(device_id)) {
            params.put("machineCode", device_id);  //机器码
        }
        params.put("deviceType", "Android");  //设备类型
        params.put("deviceBrand", device_model);    //设备型号
        params.put("deviceVersion", device_release);    //Android系统版本号
        params.put("appName", "lmb");    //指定是拉煤宝用户
        return params;
    }

}

