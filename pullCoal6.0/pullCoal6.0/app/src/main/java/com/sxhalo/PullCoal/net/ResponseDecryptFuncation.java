package com.sxhalo.PullCoal.net;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sxhalo.PullCoal.bean.HttpResult;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.utils.AESUtil;
import com.sxhalo.PullCoal.utils.LogUtil;

import java.io.StringReader;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * 对服务端返回数据解密
 *
 * @author Xiao_
 * @date 2019/4/8 0008
 */
public class ResponseDecryptFuncation<T> implements Function<ResponseBody, T> {
    private static final String TAG = "ResponseDecryptFuncatio";

    private boolean ifEncryption;

    public ResponseDecryptFuncation(boolean ifEncryption) {
        this.ifEncryption = ifEncryption;
    }

    @Override
    public T apply(ResponseBody responseBody) throws Exception {
        String response = responseBody.string();
        try {
            HttpResult httpResult;
            //当BaseUrl.AES等与0 时，执行加密
            if (AppConstant.AES_APP && ifEncryption) {
                String responseAES = AESUtil.DecryptECB(response);
                response = (responseAES == null ? response : responseAES.replace("\"entity\":\"\"", "\"entity\": {}").replace("\"body\":\"\"", "\"body\": {}")).replace("\"list\":\"\"", "\"body\": [{}]");
                LogUtil.i("联网反回", response);
            }
            if (response.contains("head") && response.contains("body")) {
                if (response != null) {
                    JsonReader jsonReader = new JsonReader(new StringReader(response));//其中jsonContext为String类型的Json数据
                    jsonReader.setLenient(true);
                    httpResult = new Gson().fromJson(jsonReader, HttpResult.class);
                    return (T) httpResult;
                }
            } else {
                httpResult = new HttpResult();
                HttpResult.HeadBean headBean = new HttpResult.HeadBean();
                headBean.setMsg("success");
                headBean.setCmd("");
                headBean.setParam("");
                httpResult.setHead(headBean);
                httpResult.setBody(response);
                return (T) httpResult;
            }

        } catch (Exception e) {
            LogUtil.e("数据处理异常", e.toString());
        } finally {
            responseBody.close();
        }
        return null;
    }
}
