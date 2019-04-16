package com.sxhalo.PullCoal.retrofithttp;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * Created by Charles on 2016/3/17.
 */
public class ResponseConvertFactory extends Converter.Factory{

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     * @param ifEncryption
     */
    public static ResponseConvertFactory create(boolean ifEncryption) {
        return create(new Gson(),ifEncryption);
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static ResponseConvertFactory create(Gson gson,boolean ifEncryption) {
        return new ResponseConvertFactory(gson,ifEncryption);
    }

    private final Gson gson;
    private final boolean ifEncryption; //是否加密解密
    private ResponseConvertFactory(Gson gson, boolean ifEncryption) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        this.ifEncryption = ifEncryption;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new GsonResponseBodyConverter(gson,type,ifEncryption);
    }

}
