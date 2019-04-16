package com.sxhalo.PullCoal.net.subscribers;

/**
 * Created by amoldZhang on 2017/7/13.
 */
public abstract class SubscriberOnNextListener<T> {
    /**
     * 联网成功回调
     * @param t
     */
    protected abstract void onNext(T t);

    /**
     * 緩存回調結果
     * @param string
     */
    public void onCacheNext(String string){

    }

    /**
     * 失败或者错误方法
     * 主动调用，更加灵活
     * @param e
     */
    public  void onError(Throwable e){

    }

    /**
     * 取消回調
     */
    public void onCancel(){

    }
}
