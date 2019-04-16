package com.sxhalo.PullCoal.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.RegisterAddLoginActivity;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.User;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.TowButtonDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by amoldZhang on 2016/12/2.
 */
public class Utils {

    private static final String TAG = "Utils";
    public static int navigationHeight;

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }

    /**
     * 判断网络图片是否存在
     * posturl 图片地址链接
     */
    public static int isImagesTrue(String posturl){
        if (Patterns.WEB_URL.matcher(posturl).matches()) {
            //符合标准
            return 200;
        } else{
            //不符合标准
            return 404;
        }
    }

    /**
     * 获取请求状态码
     *
     * @param url
     * @return 请求状态码
     */
    private int getResponseCode(String url) {
        try {
            URL getUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            return connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 设置手机网络类型，wifi，cmwap，ctwap，用于联网参数选择
     * @return
     */
    static String getNetworkType() {
        String networkType = "wifi";
        ConnectivityManager manager = (ConnectivityManager) MyAppLication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
        if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
            return ""; // 当前网络不可用
        }

        String info = netWrokInfo.getExtraInfo();
        if ((info != null)
                && ((info.trim().toLowerCase().equals("cmwap"))
                || (info.trim().toLowerCase().equals("uniwap"))
                || (info.trim().toLowerCase().equals("3gwap")) || (info
                .trim().toLowerCase().equals("ctwap")))) {
            // 上网方式为wap
            if (info.trim().toLowerCase().equals("ctwap")) {
                // 电信
                networkType = "ctwap";
            } else {
                networkType = "cmwap";
            }
        }
        return networkType;
    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 判断String是否为空
     *
     * @param data
     * @return
     */
    public static boolean isStringEmpty(String data) {
        return data == null || "".equals(data);
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    private static Toast toast;

    /**
     * 界面提示语弹框
     * @param what  选择哪一种模式
     * @param textString    提示嫩容
     */
    @SuppressLint("RtlHardcoded")
    public static void showText(Context activity,int what,String textString){
        switch (what) {
            case 0://自定义位置Toast
                toast = Toast.makeText(activity,textString, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case 1: //带图片Toast
                toast = Toast.makeText(activity, textString, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout toastView = (LinearLayout) toast.getView();
                ImageView imageCodeProject = new ImageView(activity);
                imageCodeProject.setImageResource(R.mipmap.ic_launcher);
                toastView.addView(imageCodeProject, 0);
                toast.show();
                break;
            case 2://完全自定义Toast
//			LayoutInflater inflater = activity.getLayoutInflater();
//			View layout = inflater.inflate(R.layout.custom, (ViewGroup) activity.findViewById(R.id.llToast));
//			ImageView image = (ImageView) layout.findViewById(R.id.tvImageToast);
//			image.setImageResource(R.drawable.ic_launcher);
//			TextView title = (TextView) layout.findViewById(R.id.tvTitleToast);
//			title.setText("Attention");
//			TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
//			text.setText(textString);
//			toast = new Toast(activity);
//			toast.setGravity(Gravity.RIGHT | Gravity.TOP, 12, 40);
//			toast.setDuration(Toast.LENGTH_LONG);
//			toast.setView(layout);
//			toast.show();
                break;
            default:
                Toast.makeText(activity, textString, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }

    /**
     * 将json文件读取成StringJSON数据
     * @param context
     * @param fileName
     * @return
     */
    public static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void showDialog(final Activity topAct) {
        JPushInterface.setAlias(topAct, "", null);
        UIHelper.cleanUserData(topAct);
        OrmLiteDBUtil.deleteAll(User.class);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TowButtonDialog(topAct, "当前账号已在其他设备上登录!", "注    销",
                        "重新登录", new TowButtonDialog.Listener() {
                    @Override
                    public void onLeftClick() {
                        topAct.startActivity(new Intent(topAct, MainActivity.class));
                    }
                    @Override
                    public void onRightClick() {
//						Intent[] intents = new Intent[2];
//						intents[0] = new Intent(topAct, MainActivity.class);
//						intents[1] = new Intent(topAct, RegisterAddLoginActivity.class);
//						topAct.startActivities(intents);
                        UIHelper.jumpAct(topAct, RegisterAddLoginActivity.class, false);
                    }
                }).show();
            }
        });
    }

}
