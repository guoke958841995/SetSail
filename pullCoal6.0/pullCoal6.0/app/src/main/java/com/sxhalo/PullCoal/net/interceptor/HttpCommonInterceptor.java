package com.sxhalo.PullCoal.net.interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 公共参数拦截器
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
public class HttpCommonInterceptor implements Interceptor {

    private HashMap<String, String> mHeaderParamsMap;

    public HttpCommonInterceptor(HashMap<String, String> map) {
        this.mHeaderParamsMap = map;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        if (mHeaderParamsMap != null && mHeaderParamsMap.size() > 0) {
            Set<String> keySet = mHeaderParamsMap.keySet();
            for (String key : keySet) {
                builder.addHeader(key, mHeaderParamsMap.get(key)).build();
            }
        }

        return chain.proceed(builder.build());
    }
}


