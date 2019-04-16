package com.sxhalo.PullCoal.retrofithttp.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.utils.BaseUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by amoldZhang on 2018/8/21.
 */
public class MyException{

    public static void uploadExceptionToServer(Activity mActivity, Throwable ex) {
        uploadExceptionToServer(mActivity.getApplicationContext(),ex ,false);
    }
    public static void uploadExceptionToServer(Context mActivity, Throwable ex) {
        uploadExceptionToServer(mActivity.getApplicationContext(),ex ,false);
    }

    public static void uploadExceptionToServer(Context mContext, Throwable ex ,final boolean flage) {
        try {
            mContext = mContext.getApplicationContext();
            String crashReport = getCrashReport(mContext, ex);
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            try {
                params.put("moduleName", ex.getMessage().substring(0,ex.getMessage().toString().length() < 200 ? ex.getMessage().toString().length() : 200)); //操作模块
            } catch (Exception e) {
                params.put("moduleName", "app捕获异常"); //操作模块
            }
            try {
                params.put("operationName",params.get("moduleName")); //操作名称
                if (ex != null && ex.getCause() != null && ex.getCause().getMessage() != null){
                    params.put("operationName",ex.getCause().getMessage().substring(0,ex.getMessage().toString().length() < 200 ? ex.getMessage().toString().length() : 200)); //操作名称
                }
            } catch (Exception e) {
                if (ex.getMessage() != null){
                    int line = ex.getMessage().toString().length();
                    params.put("operationName",ex.toString().substring(0,line < 200 ? line : 200)); //操作名称
                }
            }
            params.put("machineIp", BaseUtils.getIPAddress(mContext)); //当前ip
            params.put("message", crashReport);
            new DataUtils(params).getLogExceptionCreate(new DataUtils.DataBack<APPData<Object>>() {
                @Override
                public void getData(APPData<Object> o) {
                    if (flage){
                        //退出
//                        AppManager.getAppManager().AppExit(mActivity);
                    }
                }
            });
            if (flage){
                //退出
//                AppManager.getAppManager().AppExit(mActivity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取APP崩溃异常报告
     * @param ex
     * @return
     */
    private static String getCrashReport(Context context, Throwable ex) {
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if(pinfo == null) pinfo = new PackageInfo();
        StringBuffer exceptionStr = new StringBuffer();
        //应用的版本名称和版本号
        exceptionStr.append("App Version: "+pinfo.versionName+"("+pinfo.versionCode+")\n");
        //android版本号
        exceptionStr.append("Android Version: "+android.os.Build.VERSION.RELEASE+"("+android.os.Build.VERSION.SDK_INT+")\n");
        //手机制造商 和 手机型号
        exceptionStr.append("OS Vendor: : "+android.os.Build.MANUFACTURER+"("+android.os.Build.MODEL+")\n");
        //cpu架构
        exceptionStr.append("CPU ABI: "+android.os.Build.CPU_ABI+"("+android.os.Build.CPU_ABI+")\n");
        //异常日志
        exceptionStr.append("Exception: "+ex.getMessage()+"\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString()+"\n");
        }
        return exceptionStr.toString();
    }

}
