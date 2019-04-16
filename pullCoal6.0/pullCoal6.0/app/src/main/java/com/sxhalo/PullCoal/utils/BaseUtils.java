package com.sxhalo.PullCoal.utils;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.sxhalo.PullCoal.R;

import java.io.File;

public class BaseUtils {

    /**
     * 获取屏幕的宽度
     */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 文件自定义路径
     *
     * @param context
     * @param name
     * @return
     */
    public static String setDbPath(Context context, String name) {
        String packageName = context.getPackageName();
        String dbPath = String.format(FileUtils.getDbPath(context), packageName, name);
        File file = new File(dbPath);
        if (file.exists()) {
            System.out.println("exists" + file.exists());
        } else {
            file.getParentFile().mkdirs();
            if (file.exists()) {
                System.out.println("exists");
            } else {
                System.out.println("not exists");
            }
        }
        return dbPath;
    }

    /**
     * 获取屏幕内容高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int result = 0;
        int resourceId = activity.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        int screenHeight = dm.heightPixels - result;
        return screenHeight;
    }

    /**
     * 获取屏幕的高度
     */
    @SuppressWarnings("deprecation")
    public final static int getWindowsHight(Context countext) {
        WindowManager wm = (WindowManager) countext
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 电话卡校验
     * @param mContext
     * @return
     */
    public static String getSIM(Context mContext){
        TelephonyManager mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = mTelephonyManager.getSimState();
        String hintMessage = "";
        switch (simState) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                hintMessage = "未知SIM卡";
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                hintMessage = mContext.getString(R.string.no_sim_card);
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                hintMessage = "锁定：需要用户的卡销解锁";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                hintMessage = "锁定：要求用户的SIM卡PUK码解锁";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                hintMessage = "锁定：要求网络销解锁";
                break;
            case TelephonyManager.SIM_STATE_READY:
                hintMessage = "Ready";
                break;
            default:
                break;
        }
        return hintMessage;
    }
}