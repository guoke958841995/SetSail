package com.sxhalo.PullCoal.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import androidx.core.app.ActivityCompat;

/**
 * desc
 */
public class SystemUtil {

    /**
     * 获取设备型号
     *
     * @return 设备型号
     */
    public static String getDeviceID() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取机器码
     *
     * @return 机器码
     */
    public static String getMachineCode(Context context) {
        TelephonyManager telephonyManager
                = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        String deviceId = "";
        String serialNumber = "";

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            // 已授权
            deviceId = telephonyManager.getDeviceId();
            serialNumber = telephonyManager.getSimSerialNumber();
        }

        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if (!TextUtils.isEmpty(androidId) && !TextUtils.isEmpty(serialNumber)) {
            // 加密
            return new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | serialNumber.hashCode()).toString();
        } else if (!TextUtils.isEmpty(androidId)) {
            // 加密
            return new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32)).toString();
        }
        return "";
    }

    /**
     * 获取当前应用版本号
     *
     * @return 当前应用版本号
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            // 0代表是获取版本信息
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取手机的唯一标识
     *
     * @return 唯一标识
     */
    public static String getIMEI(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            // 已授权
            TelephonyManager telephonyManager
                    = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        }
        return "";
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getPlatformVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getChannel(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return "";
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), packageManager.GET_META_DATA);

            if (applicationInfo == null || applicationInfo.metaData == null) {
                return "";
            }
            return applicationInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机的ip地址,需要
     *
     * @param context
     * @return
     */
    public static String getIpAddress(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // 当前使用2G/3G/4G网络
                try {
                    Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
                    while (nis.hasMoreElements()) {
                        NetworkInterface ni = nis.nextElement();
                        Enumeration<InetAddress> addresses = ni.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            InetAddress inetAddress = addresses.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // 当前使用WiFi           
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String stringIP = intIP2StringIP(wifiInfo.getIpAddress());
                return stringIP;
            }
        }
        return "";
    }

    /**
     * 将int类型的ip转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

}
