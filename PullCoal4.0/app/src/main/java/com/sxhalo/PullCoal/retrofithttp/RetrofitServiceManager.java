package com.sxhalo.PullCoal.retrofithttp;

import android.text.TextUtils;

import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.MovieService;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * 说明：
 * 创建了一个RetrofitServiceManager类，该类采用单例模式，在私有的构造方法中，生成了Retrofit 实例，
 * 并配置了OkHttpClient和一些公共配置。提供了一个create（）方法，生成接口实例，接收Class范型，
 * 因此项目中所有的接口实例Service都可以用这个来生成，代码如下：
 * mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
 * 通过create()方法生成了一个MovieService
 * Created by amoldZhang on 2017/7/13.
 */
public class RetrofitServiceManager {
    private  static final int DEFAULT_TIME_OUT = 10;//超时时间
    private static final int DEFAULT_READ_TIME_OUT = 20;
    private static boolean flage = false;
    private  Retrofit mRetrofit;
    private static String baseUrl = APIConfig.WEB_SERVICE;
    private MovieService apiService;

    public RetrofitServiceManager(String webURL,boolean ifEncryption) {
        this(webURL,ifEncryption,null);
    }

    public RetrofitServiceManager(boolean ifEncryption,Map<String,String> headers) {
        this(baseUrl,ifEncryption,headers);

    }

    public RetrofitServiceManager(String webURL,boolean ifEncryption, Map<String, String> mHeaderParamsMap) {
        if (TextUtils.isEmpty(webURL)) {
            webURL = baseUrl;
        }

//        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                .tlsVersions(TlsVersion.TLS_1_1)
//                .tlsVersions(TlsVersion.TLS_1_2)
//                .cipherSuites(
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
//                .build();

        // 创建 OKHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dns(new MyDns())
                .retryOnConnectionFailure(false)   //错误重连
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)     //连接超时时间  以毫秒计数  MILLISECONDS
                .connectionPool(new ConnectionPool(99, 10, TimeUnit.SECONDS))  // 这里可以自己设置同时连接的个数和时间，这里5个，和每个保持时间为10s
//                builder.cookieJar(new NovateCookieManger(context))
//                builder.cache(cache)
//                .connectionSpecs(Collections.singletonList(spec))
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .addInterceptor(new HttpCommonInterceptor(mHeaderParamsMap))  //添加公共参数
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(webURL)
                // 对http请求结果进行统一的预处理
                .addConverterFactory(ResponseConvertFactory.create(ifEncryption))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        apiService = mRetrofit.create(MovieService.class);
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    /**
     * 获取RetrofitServiceManager
     *  @url  自定义接口
     * @return
     */
    public static RetrofitServiceManager getInstance(String webURL,boolean ifEncryption){
        flage = true;
        return new RetrofitServiceManager(webURL,ifEncryption);
    }

    /**
     * 获取RetrofitServiceManager
     *  @url  自定义接口
     * @return
     */
    public static RetrofitServiceManager getInstance(boolean ifEncryption,Map<String,String> headers){
        flage = false;
        return new RetrofitServiceManager(ifEncryption,headers);
    }

    /**
     *  自定义接口和添加请求头
     * @param webURL
     * @param headers
     * @return
     */
    public static RetrofitServiceManager getInstance(String webURL,boolean ifEncryption, Map<String,String> headers){
        flage = true;
        return new RetrofitServiceManager(webURL,ifEncryption,headers);
    }

    /**
     * 获取对应的Service
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service){
        return mRetrofit.create(service);
    }

    public Retrofit getRetrofit(){
        return mRetrofit;
    }

}