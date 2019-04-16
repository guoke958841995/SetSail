package com.sxhalo.PullCoal.net;

import android.util.Log;

import com.google.gson.Gson;
import com.sxhalo.PullCoal.common.ApiConstant;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.common.MyApplication;
import com.sxhalo.PullCoal.utils.AESUtil;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.MD5Util;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.SystemUtil;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 网络请求相关
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
public class HttpUtil {

    private static final String TAG = "HttpUtil";

    /**
     * 获取日志拦截器
     *
     * @return
     */
    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, "log: " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return loggingInterceptor;
    }

    /**
     * https相关
     *
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new CustomTrustManager()}, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslSocketFactory;
    }

    /**
     * 校验服务器主机名的合法性(后续改善,否则可能受到中间人攻击.)
     *
     * @return true:信任所有服务器地址
     */
    public static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        return hostnameVerifier;
    }

    /**
     * 获取公共参数
     *
     * @return
     */
    public static HashMap<String, String> getHeaderParamsMap() {

        HashMap<String, String> mHeaderParamsMap = new HashMap<>();
        //设备型号
        mHeaderParamsMap.put("deviceId", SystemUtil.getDeviceID());
        //机器码
        mHeaderParamsMap.put("machineCode", SystemUtil.getMachineCode(MyApplication.getContext()));
        //设备类型
        mHeaderParamsMap.put("platform", "android");
        //应用名称
        mHeaderParamsMap.put("appName", "lmb");
        //当前应用版本号
//        String appVersion = SystemUtil.getAppVersion(MyApplication.getContext());
        mHeaderParamsMap.put("appVersion", ApiConstant.APP_VERSION);    //TODO 需要修改,动态获取
        //当前设备版本号
        mHeaderParamsMap.put("platformVersion", SystemUtil.getPlatformVersion());
        //获取打包渠道名称
        //SystemUtil.getChannel(MyApplication.getContext(), "SXHALO_CHANNEL_NAME")
        mHeaderParamsMap.put("channel", "");    //TODO 需要修改,动态获取
        //协议版本号
        mHeaderParamsMap.put("apiVersion", ApiConstant.API_VERSION);
        //获取当前手机唯一标识
        mHeaderParamsMap.put("imei", SystemUtil.getIMEI(MyApplication.getContext()));
        //当前手机ip
        mHeaderParamsMap.put("machineIp", SystemUtil.getIpAddress(MyApplication.getContext()));
        //用户id
        String user_id = PaperUtil.get(AppConstant.USER_ID_KEY, "-1");
        if (!"-1".equals(user_id)) {
            mHeaderParamsMap.put("userId", user_id);
        }
        else{
            mHeaderParamsMap.put("userId", "80000720");//80004431
        }

        //城市id
        String city_id = PaperUtil.get(AppConstant.CITY_ID_KEY, "-1");
        if (!"-1".equals(city_id)) {
            mHeaderParamsMap.put("cityId", city_id);
        }

        return mHeaderParamsMap;
    }


    /**
     * 进行网络参数加密
     * 流程：
     * 1.获取当前时间戳  nowTime (long)
     * 2.将当前时间戳加入近网络连接中的请求参数中  map.add("timeStamp",nowTime)
     * 3.将当前网络请求参数转换成Json数据类型  JSON
     * 4.用MD5对 （固定参数标识(数据库给定) + 参数传换成的Json类型数据 + 生成的时间戳）生成不可逆的密文
     * 5.将生成不可逆的秘文添加入请求参数中
     * 6.对新生成的请求参数进行AES加密
     * 7.将AES密文放入字典data中
     *
     * @param map 当前联网拼接参数
     */
    public static LinkedHashMap<String, String> setParameterEncryption(LinkedHashMap<String, String> map) {
        LinkedHashMap<String, String> paramsAES = new LinkedHashMap<String, String>();
        try {
            if (map == null) {
                map = new LinkedHashMap<String, String>();
            }
            //生成一个时间戳
            String timeStamp = String.valueOf(DateUtil.getNewTimeLong());
            //将时间戳加入到联网请求中
            map.put("timeStamp", timeStamp);
            // 将当前网络请求参数转换成Json数据类型
            String sourceRequest = new Gson().toJson(map);
            LogUtil.i("联网请求参数", "sourceRequest = " + sourceRequest);

            String access_key = PaperUtil.get(AppConstant.ACCESS_KEY);
            String dataJson = (access_key + sourceRequest + timeStamp);
            //生成不可逆的密文
            String sign = MD5Util.MD5Encode(dataJson, "utf-8");
            //将这个不可逆的密文放入请求参数中
            map.put("sign", sign);
            LogUtil.i("联网请求参数", "map = " + map);
            //对新生成的请求参数AES加密并放入字典的data中
            if (map != null) {
                String info = new Gson().toJson(map);
                LogUtil.i("传入参数", "传入参数" + info);

                // 使用AES算法将用户的请求参数加密
                String data = AESUtil.EncryptECB(info);
                paramsAES.put("data", data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramsAES;
    }
}
