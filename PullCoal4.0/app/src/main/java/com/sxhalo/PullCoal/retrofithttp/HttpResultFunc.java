package com.sxhalo.PullCoal.retrofithttp;


import com.google.gson.Gson;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.retrofithttp.api.HttpResult;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.GHLog;

import java.lang.reflect.Type;

import rx.functions.Func1;


/**
 *  用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回
 *  真正需要的数据类型，也就是Data部分的数据类型
 * Created by amoldZhang on 2017/7/13.
 */
public class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {


    private Type type;

    public  HttpResultFunc(Type type){
      this.type = type;
    }

    @Override
    public T call(HttpResult<T> httpResult) {
        if (httpResult != null) {
            if (httpResult.getHead().getMsg().equals("error")){
                try {
                    GHLog.e("接口反回异常",
                                    "\n msg = " + httpResult.getHead().getMsg() +
                                    "\n cmd = " + httpResult.getHead().getCmd() +
                                    "\n param = " + httpResult.getHead().getParam() +
                                    "\n error_code ＝" + httpResult.getHead().getError_code() +
                                    "\n error ＝" + httpResult.getHead().getError() +
                                    "\n body ＝" + (httpResult.getBody() == null ? "":httpResult.getBody().toString()));
                } catch (Exception e) {
                }
                getApiExceptionMessage(httpResult);
            }
        }
        Gson gson = new Gson();
        T t =  gson.fromJson(gson.toJson(httpResult.getBody()),type);
        return t;
    }

    /**
     * 过滤服务器异常反回
     *
     * @param httpResult
     * @return
     */
    private  void getApiExceptionMessage(HttpResult httpResult) {
//        int switchCode = 0;
//        try {
//            switchCode = Integer.valueOf(httpResult.getHead().getError_code());
//        } catch (Exception e) {
//            GHLog.e("ErroeCode转换异常",e.toString());
//        }
//        //当在修改密码并且旧密码输入错误的时候不用处理
//        if (switchCode == 1030004 && MyAppLication.ifLogin == 1){
//
//        }else{  //其余都处理
          throw new ApiException(httpResult.getHead().getError_code(),httpResult);
//        }
    }
}
