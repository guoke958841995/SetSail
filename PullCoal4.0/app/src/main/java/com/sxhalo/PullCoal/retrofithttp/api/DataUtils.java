package com.sxhalo.PullCoal.retrofithttp.api;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.AdvertisementEntity;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.model.ActivityData;
import com.sxhalo.PullCoal.model.AppPublic;
import com.sxhalo.PullCoal.model.Article;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.ComplaintEntity;
import com.sxhalo.PullCoal.model.CouponsEntity;
import com.sxhalo.PullCoal.model.Discover;
import com.sxhalo.PullCoal.model.EscrowAccount;
import com.sxhalo.PullCoal.model.ExpressEntity;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.ListCoalEntity;
import com.sxhalo.PullCoal.model.OperateVisitorRegistration;
import com.sxhalo.PullCoal.model.Orders;
import com.sxhalo.PullCoal.model.ProvinceModel;
import com.sxhalo.PullCoal.model.RouteEntity;
import com.sxhalo.PullCoal.model.SaleManager;
import com.sxhalo.PullCoal.model.SamplingTest;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.model.TransactionBean;
import com.sxhalo.PullCoal.model.UpdateApp;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.APPMessage;
import com.sxhalo.PullCoal.model.MineDynamic;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.model.PayMent;
import com.sxhalo.PullCoal.model.PayMentOrder;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.model.UserBrowseRecord;
import com.sxhalo.PullCoal.model.UserCallRecord;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.model.UserDemandBBS;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.model.UserGuestbook;
import com.sxhalo.PullCoal.model.UserMessage;
import com.sxhalo.PullCoal.retrofithttp.HttpDataLoader;
import com.sxhalo.PullCoal.retrofithttp.subscribers.ProgressSubscriber;
import com.sxhalo.PullCoal.retrofithttp.subscribers.SubscriberOnNextListener;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import net.sf.json.JSONString;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  接口请求设置类
 * Created by amoldZhang on 2017/7/24.
 */
public class DataUtils {

    private String webUrl;
    private LinkedHashMap<String, String> myParams;
    private Activity myActivity;

    public abstract static class DataBack<T> {
        public void getData(T t) {
        }

        public void getError(Throwable e) {
        }
    }

    public DataUtils(Activity myActivity) {
        this(myActivity, new LinkedHashMap<String, String>());
    }

    public DataUtils(Activity myActivity, LinkedHashMap<String, String> myParams) {
        this(myActivity, APIConfig.WEB_SERVICE, myParams);
    }
    public DataUtils(LinkedHashMap<String, String> myParams) {
        this(APIConfig.WEB_SERVICE, myParams);
    }

    public DataUtils(Activity myActivity, String webUrl, LinkedHashMap<String, String> myParams) {
        this.myActivity = myActivity;
        this.webUrl = webUrl;
        this.myParams = myParams;
    }
    public DataUtils(String webUrl, LinkedHashMap<String, String> myParams) {
        this.webUrl = webUrl;
        this.myParams = myParams;
    }


    /**
     * APP 获取AccessKey
     *
     * @param dataBack
     */
    public void getPublicAccessKey(final DataBack dataBack) {
        Type type = new TypeToken<String>() {}.getType();
        new HttpDataLoader(false,getHeaderMap(), type).getHttpData(APIConfig.PUBLIC_ACCESS_KEY, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<String>() {
                    @Override
                    public void onNext(String data) {
                        AppPublic  appPublic =  new Gson().fromJson(data,AppPublic.class);
                        if (!StringUtils.isEmpty(appPublic.getError())) {
                            if (dataBack != null) {
                                Throwable e = new Throwable(appPublic.getError());
                                dataBack.getError(e);
                            }
                        }
                        else{
                                if (dataBack != null) {
                                    dataBack.getData(appPublic);
                                }
                            }


                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，

    }

    /**
     * APP 红点数获取
     *
     * @param dataBack
     */
    public void getPushMessageUnreadCount(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.PUSH_MESSAGE_UNREAD_COUNT, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，

    }

    /**
     * APP 红点阅读状态
     *
     * @param dataBack
     */
    public void getPushMessageReadMessage(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserMessage>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.PUSH_MESSAGE_READ_MESSAGE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserMessage>>() {
                    @Override
                    public void onNext(APPData<UserMessage> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("operate060005")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * app电话上报接口
     *
     * @param dataBack
     */
    public void getUserCallRecordCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserCallRecord>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_CALL_RECORD_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserCallRecord>>() {
                    @Override
                    public void onNext(APPData<UserCallRecord> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("sys060002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * app浏览记录上报
     *
     * @param dataBack
     */
    public void getUserBrowseRecordCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserBrowseRecord>>() {
        }.getType();
        myParams.put("regionCode", SharedTools.getStringValue(myActivity, "adCode", ""));
        myParams.put("longitude", SharedTools.getStringValue(myActivity, "longitude", ""));
        myParams.put("latitude", SharedTools.getStringValue(myActivity, "latitude", ""));
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_bROWSE_RECORD_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserBrowseRecord>>() {
                    @Override
                    public void onNext(APPData<UserBrowseRecord> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("sys060003")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * APP 异常日志上报
     *
     * @param dataBack
     */
    public void getLogExceptionCreate(final DataBack dataBack) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.LOG_EXCEPTION_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Object>>() {
                    @Override
                    public void onNext(APPData<Object> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, false));  //最后一个参数表示是否显示等待条，true 表示显示，

    }

    /**
     * APP 获取短信验证码
     *
     * @param dataBack
     */
    public void getSendSMS(final DataBack dataBack) {
        Type type = new TypeToken<SaleManager>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.SEND_SMS, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<SaleManager>() {
                    @Override
                    public void onNext(SaleManager data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，

    }

    /**
     * APP 校验短信验证码
     *
     * @param dataBack
     */
    public void getVerificationCode(final DataBack dataBack) {
        Type type = new TypeToken<SaleManager>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.VERIFCATION_CODE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<SaleManager>() {
                    @Override
                    public void onNext(SaleManager data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，

    }

    /**
     * APP 轨迹上传
     *
     * @param dataBack
     */
    public void getDriverUpdatePosition(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.DRIVER_UPDATE_POSITION, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * APP 自动更新
     *
     * @param dataBack
     */
    public void getAppVesion(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UpdateApp>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.GET_APP_VESION, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UpdateApp>>() {
                    @Override
                    public void onNext(APPData<UpdateApp> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("sys060002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，

    }

    /**
     *  app初始化接口请求
     * @param dataBack
     */
    public void getDictionary(final DataBack dataBack) {
        Type type = new TypeToken<List<Dictionary>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.GET_DICTIONARY, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<Dictionary>>() {
                    @Override
                    public void onNext(List<Dictionary> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * /**
     * 获取行政区数据
     * @param dataBack
     */
    public void getRegionCode(final DataBack dataBack) {
        Type type = new TypeToken<List<ProvinceModel>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.GET_REGION_CODE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<ProvinceModel>>() {
                    @Override
                    public void onNext(List<ProvinceModel> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  广告接口
     * @param dataBack
     */
    public void getAdvertisement(final DataBack dataBack) {
        Type type = new TypeToken<APPData<AdvertisementEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.GET_ADVERTISEMENT, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<AdvertisementEntity>>() {
                    @Override
                    public void onNext(APPData<AdvertisementEntity> data) {
                        String dataType = data.getDataType();
                        if (dataType.equals("operate060003")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  首页搜索
     * @param dataBack
     */
    public void getHomeSearch(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.HOME_SEARCH, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            onError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  首页接口数据请求
     * @param dataBack
     */
    public void getHome(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.GET_HOME_DATA, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        HomeData appModel = new HomeData().getHomeData(data);
                        if (dataBack != null) {
                            dataBack.getData(appModel);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示
    }

    /**
     *  资讯列表数据请求
     * @param dataBack
     */
    public void getArticleList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<Article>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.ARTICLE_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<Article>>() {
                    @Override
                    public void onNext(APPDataList<Article> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("operate120003")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  订阅货运列表数据
     * @param dataBack
     */
    public void getTransportSubscribeList(final DataBack dataBack) {
        Type type = new TypeToken<List<APPDataList<RouteEntity>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.TRANSPORT_SUBSCRIBE_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPDataList<RouteEntity>>>() {
                    @Override
                    public void onNext(List<APPDataList<RouteEntity>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  添加订阅货运
     * @param dataBack
     */
    public void getTransportSubscribeCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<RouteEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.TRANSPORT_SUBSCRIBE_CREATE, myParams).subscribe(
                new ProgressSubscriber<>(new SubscriberOnNextListener<APPData<RouteEntity>>() {
                    @Override
                    public void onNext(APPData<RouteEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal090007")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  删除订阅货运
     * @param dataBack
     */
    public void getTransportSubscribeDelete(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.TRANSPORT_SUBSCRIBE_DELETE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal090008")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  我的货运列表数据请求
     * @param dataBack
     */
    public void getUserTransportList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<TransportMode>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSPORT_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<TransportMode>>() {
                    @Override
                    public void onNext(APPDataList<TransportMode> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal090001")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  我的货运发布数据提交
     * @param dataBack
     */
    public void userTransportCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<TransportMode>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSPORT_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<TransportMode>>() {
                    @Override
                    public void onNext(APPData<TransportMode> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal090005")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  货运列表数据请求
     * @param dataBack
     */
    public void getCoalTransportList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPDataList<TransportMode>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_TRANSPORT_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<TransportMode>>() {
                    @Override
                    public void onNext(APPDataList<TransportMode> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal090001")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  货运详情
     * @param dataBack
     */
    public void getCoalTransportInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<MineDynamic>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_TRANSPORT_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<MineDynamic>>>() {
                    @Override
                    public void onNext(List<APPData<MineDynamic>> dataList) {
                        if (dataList == null) {
                            if (dataBack != null) {
                                dataBack.getData(dataList);
                            }
                            return;
                        }
                        List<TransportMode> dataS = new ArrayList<TransportMode>();
                        for (APPData<MineDynamic> data : dataList) {
                            if (data.getDataType().equals("coal090002")) {
                                dataS.add(0, data.getEntity());
                            }
                            if (data.getDataType().equals("coal020004")) {
                                dataS.add(1, data.getEntity());
                            }
                            if (data.getDataType().equals("coal010004")) {
                                dataS.add(2, data.getEntity());
                            }
                        }
                        if (dataBack != null) {
                            dataBack.getData(dataS);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  货运抢单
     * @param dataBack
     */
    public void getUserTransportOrderCreate(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<TransportMode>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSPORT_ORDER_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<TransportMode>>>() {
                    @Override
                    public void onNext(List<APPData<TransportMode>> dataList) {
                        if (dataList == null) {
                            if (dataBack != null) {
                                dataBack.getData(dataList);
                            }
                            return;
                        }
                        List<TransportMode> dataS = new ArrayList<TransportMode>();
                        for (APPData<TransportMode> data : dataList) {
                            if (data.getDataType().equals("coal090004")) {
                                dataS.add(0, data.getEntity());
                            }
                            if (data.getDataType().equals("coal020009")) {
                                dataS.add(1, data.getEntity());
                            }
                        }
                        if (dataBack != null) {
                            dataBack.getData(dataS);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  煤炭历史数据列表请求
     * @param dataBack
     */
    public void getCoalGoodsCollectList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<Coal>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_GOODS_COLLECT_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Coal>>() {
                    @Override
                    public void onNext(APPData<Coal> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  精品煤炭数据请求
     * @param dataBack
     */
    public void getCoalGoodsTop(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_GOODS_TOP, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        ListCoalEntity listCoalEntity = new ListCoalEntity(data);
                        if (dataBack != null) {
                            dataBack.getData(listCoalEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  煤炭列表数据请求
     * @param dataBack
     */
    public void getCoalGoodsList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_GOODS_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        ListCoalEntity listCoalEntity = new ListCoalEntity(data);
                        if (dataBack != null) {
                            dataBack.getData(listCoalEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     *  煤炭模块列表数据请求
     * @param dataBack
     */
    public void getCoalGoodsModelList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<Map<String, Object>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_GOODS_MODEL_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Map<String, Object>>>() {
                    @Override
                    public void onNext(APPData<Map<String, Object>> data) {
                        ListCoalEntity listCoalEntity = new ListCoalEntity(data);
                        if (dataBack != null) {
                            dataBack.getData(listCoalEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  煤炭详情
     * @param dataBack
     */
    public void getCoalGoodsInfo(final DataBack dataBack) {
        Type type = new TypeToken<APPData<Coal>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_GOODS_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Coal>>() {
                    @Override
                    public void onNext(APPData<Coal> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal070002")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 煤炭收藏
     * @param dataBack
     * @param flage  0 ：添加收藏；1：取消收藏
     */
    public void getCoalGoodsColect(final DataBack dataBack,int flage) {
        Type type = new TypeToken<APPData<Coal>>() {
        }.getType();
        String URL = "";
        if (flage == 0){
            URL = APIConfig.COAL_GOODS_CREATE;
        }else{
            URL = APIConfig.COAL_GOODS_DELECT;
        }
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(URL, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Coal>>() {
                    @Override
                    public void onNext(APPData<Coal> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  校验订单
     * @param dataBack
     */
    public void chackCoalOrderStatus(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CHECK_COAR_ORDER_STATUS, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (data != null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dataBack.getError(e);
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     *  下订单
     * @param dataBack
     */
    public void getUserCoalGoodsCreate(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Orders>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_ORDER_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Orders>>>() {
                    @Override
                    public void onNext(List<APPData<Orders>> dataList) {
                        if (dataList == null) {
                            if (dataBack != null) {
                                dataBack.getData(dataList);
                            }
                            return;
                        }
                        List<Orders> dataS = new ArrayList<Orders>();
                        for (APPData<Orders> data : dataList) {
                            if (data.getDataType().equals("coal080002")) {
                                dataS.add(0, data.getEntity());
                            }
                            if (data.getDataType().equals("coal020008")) {
                                dataS.add(1, data.getEntity());
                            }
                        }
                        if (dataBack != null) {
                            dataBack.getData(dataS);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  信息部列表请求
     * @param dataBack
     */
    public void getCoalSalesList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<InformationDepartment>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_SALES_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<InformationDepartment>>() {
                    @Override
                    public void onNext(APPDataList<InformationDepartment> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal020002")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  信息部煤炭列表请求
     * @param dataBack
     */
    public void getCoalSalesCoalGoodsList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<Coal>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_SALES_COAL_GOODS_LIST, myParams).subscribe(
                new ProgressSubscriber<APPDataList<Coal>>(new SubscriberOnNextListener<APPDataList<Coal>>() {
                    @Override
                    public void onNext(APPDataList<Coal> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal070003")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  信息部货运列表请求
     * @param dataBack
     */
    public void getCoalSalesCoalTransportList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<TransportMode>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_SALES_COAL_TRANSPORT_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<TransportMode>>() {
                    @Override
                    public void onNext(APPDataList<TransportMode> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal090003")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  信息部详情请求
     * @param dataBack
     */
    public void getCoalSalesInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_SALES_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        InformationDepartment myListData = new InformationDepartment().getInformationDepartmentInfoData(data);
                        if (dataBack != null) {
                            dataBack.getData(myListData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  服务列表请求
     * @param dataBack
     */
    public void getActivityList(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.ACTIVITY_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        ActivityData activityData = new ActivityData().getActivityData(data);
                        if (dataBack != null) {
                            dataBack.getData(activityData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  地图列表请求
     * @param dataBack
     */
    public void getDiscoverList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<Discover>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.DISCOVER_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<Discover>>() {
                    @Override
                    public void onNext(APPDataList<Discover> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("operate120005")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 根据运单，承运快递，手机号后四位 来获取运单详情
     * @param dataBack
     */
    public void getExpressInfo(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<ExpressEntity>>() {}.getType();
        new HttpDataLoader(true,getHeaderMap(),type).getHttpData(APIConfig.EXPRESSINFO_QUWEY,myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<ExpressEntity>>() {
                    @Override
                    public void onNext(APPData<ExpressEntity> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *采样化验单使用优惠券抵扣
     * @param dataBack
     */
    public void getOperatesampleCouponPay(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.OPERATESAMPLE_COUPON_PAY, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *采样化验单列表
     * @param dataBack
     */
    public void getOperatesampleList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPDataList<SamplingTest>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.OPERATESAMPLE_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<SamplingTest>>() {
                    @Override
                    public void onNext(APPDataList<SamplingTest> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("sample150001")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *采样化验单详情
     * @param dataBack  operatesample/info
     */
    public void getOperatesampleInfo(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<SamplingTest>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.OPERATESAMPLE_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<SamplingTest>>() {
                    @Override
                    public void onNext(APPData<SamplingTest> data) {
                        String dataType = data.getDataType();
                        if (dataType.equals("sample150002")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *采样化验订单取消
     * @param dataBack
     */
    public void getOperatesampleCancel(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.OPERATESAMPLE_CANCEL, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *提交采样化验
     * @param dataBack
     */
    public void getOperatesampleCreate(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<SamplingTest>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).postHttpData(APIConfig.OPERATESAMPLE_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<SamplingTest>>() {
                    @Override
                    public void onNext(APPData<SamplingTest> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("sample150003")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  矿口 下商品名称请求列表
     * @param dataBack
     */
    public void getCoalGoodsCoalNameList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPDataList<SamplingTest>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_GOODS_COAL_NAME_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<SamplingTest>>() {
                    @Override
                    public void onNext(APPDataList<SamplingTest> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal070009")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  矿口名称请求列表
     * @param dataBack
     */
    public void getCoalCompaniesMinenameList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPDataList<SamplingTest>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_COMPANIES_MINENAME_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<SamplingTest>>() {
                    @Override
                    public void onNext(APPDataList<SamplingTest> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal010005")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  矿口列表请求
     * @param dataBack
     */
    public void getCompaniesList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<MineMouth>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_COMPANIES_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<MineMouth>>() {
                    @Override
                    public void onNext(APPDataList<MineMouth> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("coal010001")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     *  矿口详情请求
     * @param dataBack
     */
    public void getCompaniesInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_COMPANIES_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        MineMouth myListData = new MineMouth().getMineMouthData(data);
                        if (dataBack != null) {
                            dataBack.getData(myListData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  矿口动态发布
     * @param dataBack
     */
    public void getCoalCompanyRealtimeInfoCreate(List<File> fileList, final DataBack dataBack) {
        Type type = new TypeToken<APPData<MineDynamic>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).upLoadFiles(APIConfig.COAL_COMPANY_REALIIME_INFO_CREATE, fileList, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<MineDynamic>>() {
                    @Override
                    public void onNext(APPData<MineDynamic> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal010004")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  矿口动态列表请求
     * @param dataBack
     */
    public void getCoalCompanyRealtimeInfoList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<MineDynamic>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_COMPANY_REALIIME_INFO_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<MineDynamic>>() {
                    @Override
                    public void onNext(APPDataList<MineDynamic> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal010003")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getList());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  设置密码
     * @param dataBack
     */
    public void getUserRegister(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_REGISTER, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  更新个人数据
     * @param dataBack
     */
    public void getUserInfo(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  修改个人数据
     * @param dataBack
     */
    public void getUserUpdata(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_UPDATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  用户登录/验证密码
     * @param dataBack
     */
    public void getUserLogin(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_LOGIN, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  注销登录
     * @param dataBack
     */
    public void getUserLogout(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_LOGOUT, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  修改密码
     * @param dataBack
     */
    public void getUserModifyPassword(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_MODIFY_PASSWORD, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  执行第三方后台登陆或查询
     * @param dataBack
     */
    public void getUserThirdPartyLogin(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_THIRD_PARTY_LOGIN, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  执行第三方后台注册并绑定手机号
     * @param dataBack
     */
    public void getUserThirdPartyBinding(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_THIRD_PARTY_BINDING, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  第三方微信联网
     * @param dataBack
     */
    public void getUserThirdParty(String url, final DataBack dataBack) {
        Type type = new TypeToken<String>() {
        }.getType();
        new HttpDataLoader(webUrl,false, type).getHttpData(url, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<String>() {
                    @Override
                    public void onNext(String data) {
                        if (data == null) {
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  我的订单列表
     * @param dataBack
     */
    public void getUserCoalOrderList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<Orders>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_ORDER_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<Orders>>() {
                    @Override
                    public void onNext(APPDataList<Orders> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal080001")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  我的订单详情
     * @param dataBack
     */
    public void getUserCoalOrderInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String,Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_ORDER_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String,Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String,Object>>> dataList) {
                        if (dataList == null) {
                            return;
                        }
                        Orders orders = new Orders().getOrders(dataList);
                        if (dataBack != null) {
                            dataBack.getData(orders);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  处理我的煤炭订单(取消100、完成7、确认成交5)
     * @param dataBack
     */
    public void handleCoalOrder(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String,Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_ORDER_HANDLE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String,Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String,Object>>> dataList) {
                        if (dataList == null) {
                            return;
                        }
                        Orders orders = new Orders().getOrders(dataList);
                        if (dataBack != null) {
                            dataBack.getData(orders);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     *  我的派车单
     * @param dataBack
     */
    public void getMyCoalOrderTransport(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<SendCarEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_ORDER_TRANSPORT, myParams).subscribe(
                new ProgressSubscriber<APPDataList<SendCarEntity>>(new SubscriberOnNextListener<APPDataList<SendCarEntity>>() {
                    @Override
                    public void onNext(APPDataList<SendCarEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal080006")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null){
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  确认派车
     * @param dataBack
     */
    public void getMyCoalOrderTransportCerate(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<SendCarEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_ORDER_TRANSPORT_CREATE, myParams).subscribe(
                new ProgressSubscriber<APPDataList<SendCarEntity>>(new SubscriberOnNextListener<APPDataList<SendCarEntity>>() {
                    @Override
                    public void onNext(APPDataList<SendCarEntity> data) {
                        if (data.getDataType().equals("coal080007")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  我的派车单详情
     * @param dataBack
     */
    public void getMyCoalOrderTransportDetail(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String,Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_ORDER_TRANSPORT_DETAIL, myParams).subscribe(
                new ProgressSubscriber<List<APPData<Map<String,Object>>>>(new SubscriberOnNextListener<List<APPData<Map<String,Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String,Object>>> dataList) {

                        if (dataList == null) {
                            return;
                        }
                        SendCarEntity sendCarEntity = new SendCarEntity().getEntity(dataList);
                        if (dataBack != null) {
                            dataBack.getData(sendCarEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  处理派车单(1：确认收货，2：确认发货，3：确认司机到达，4：取消派车单)
     * @param dataBack
     */
    public void handleSendCarOrder(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String,Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.COAL_SEND_CAR_ORDER_HANDLE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String,Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String,Object>>> dataList) {
                        if (dataList == null) {
                            return;
                        }
                        SendCarEntity sendCarEntity = new SendCarEntity().getEntity(dataList);
                        if (dataBack != null) {
                            dataBack.getData(sendCarEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  我的货运单列表
     * @param dataBack
     */
    public void getUserTransportOrderList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<MineDynamic>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSPOR_ORDER_LIST, myParams).subscribe(
                new ProgressSubscriber<APPDataList<TransportMode>>(new SubscriberOnNextListener<APPDataList<TransportMode>>() {
                    @Override
                    public void onNext(APPDataList<TransportMode> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal090003")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  我的货运单详情
     * @param dataBack
     */
    public void getUserTransportOrderInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<TransportMode>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSPORT_ORDER_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<TransportMode>>>() {
                    @Override
                    public void onNext(List<APPData<TransportMode>> dataList) {
                        if (dataList == null) {
                            if (dataBack != null) {
                                dataBack.getData(dataList);
                            }
                            return;
                        }
                        List<TransportMode> dataS = new ArrayList<TransportMode>();
                        for (APPData<TransportMode> data : dataList) {
                            if (data.getDataType().equals("coal090004")) {
                                dataS.add(0, data.getEntity());
                            }
                            if (data.getDataType().equals("coal020009")) {
                                dataS.add(1, data.getEntity());
                            }
                        }
                        if (dataBack != null) {
                            dataBack.getData(dataS);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  装货净重更新(货运单)
     * @param dataBack
     */
    public void getUserTransportOrderCarryWeight(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<TransportMode>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSPORT_ORDER_CARRY_WEIGHT_UPDATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<TransportMode>>>() {
                    @Override
                    public void onNext(List<APPData<TransportMode>> dataList) {
                        if (dataList == null) {
                            if (dataBack != null) {
                                dataBack.getData(dataList);
                            }
                            return;
                        }
                        List<TransportMode> dataS = new ArrayList<TransportMode>();
                        for (APPData<TransportMode> data : dataList) {
                            if (data.getDataType().equals("coal090004")) {
                                dataS.add(0, data.getEntity());
                            }
                            if (data.getDataType().equals("coal020009")) {
                                dataS.add(1, data.getEntity());
                            }
                        }
                        if (dataBack != null) {
                            dataBack.getData(dataS);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null){
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  装货净重更新(派车单)
     * @param dataBack
     */
    public void getUserCoalOrderCarryWeight(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String,Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_ORDER_CARRY_WEIGHT_UPDATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String,Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String,Object>>> dataList) {
                        if (dataList == null) {
                            return;
                        }
                        SendCarEntity sendCarEntity = new SendCarEntity().getEntity(dataList);
                        if (dataBack != null) {
                            dataBack.getData(sendCarEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null){
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     * 我对货运单的操作
     * @param dataBack
     */
    public void getUserTransportOrderHandle(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<TransportMode>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSPORT_ORDER_HANDLE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<TransportMode>>>() {
                    @Override
                    public void onNext(List<APPData<TransportMode>> dataList) {
                        if (dataList == null) {
                            if (dataBack != null) {
                                dataBack.getData(dataList);
                            }
                            return;
                        }
                        List<TransportMode> dataS = new ArrayList<TransportMode>();
                        for (APPData<TransportMode> data : dataList) {
                            if (data.getDataType().equals("coal090004")) {
                                dataS.add(0, data.getEntity());
                            }
                            if (data.getDataType().equals("coal020009")) {
                                dataS.add(1, data.getEntity());
                            }
                        }
                        if (dataBack != null) {
                            dataBack.getData(dataS);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  关注的信息部列表
     * @param dataBack
     */
    public void getUserCoalSalesList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<InformationDepartment>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_SALES_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<InformationDepartment>>() {
                    @Override
                    public void onNext(APPDataList<InformationDepartment> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal020006")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  好友列表
     * @param dataBack
     */
    public void getUserFriendList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_FRIEND_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<UserEntity>>() {
                    @Override
                    public void onNext(APPDataList<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030001")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  司机列表
     * @param dataBack
     */
    public void getDriverList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPDataList<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.DRIVER_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<UserEntity>>() {
                    @Override
                    public void onNext(APPDataList<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user040002")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  用户消息列表
     * @param dataBack
     */
    public void getPushMessageList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<UserMessage<Object>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.PUSH_MESSAGE_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<UserMessage<Object>>>() {
                    @Override
                    public void onNext(APPDataList<UserMessage<Object>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("operate060002")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     *  用户消息删除
     * @param dataBack
     */
    public void getPushMessageDelete(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.PUSH_MESSAGE_DELETE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("operate060003")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  添加好友
     * @param dataBack
     */
    public void getUserFriendCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_FRIEND_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  删除好友
     * @param dataBack
     */
    public void getUserFriendDelete(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_FRIEND_DELETE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  买煤求购
     * @param dataBack
     */
    public void getUserDemandList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<UserDemand>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<UserDemand>>() {
                    @Override
                    public void onNext(APPDataList<UserDemand> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal130001")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getList());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  买煤求购详情
     * @param dataBack
     */
    public void getUserDemandInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        UserDemand myListData = new UserDemand().getUserDemandInfoData(data);
                        if (dataBack != null) {
                            dataBack.getData(myListData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  确认意向信息部
     * @param dataBack
     */
    public void getUserDemandDelivervSelected(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_DELIVERY_SELECTED, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        UserDemand myListData = new UserDemand().getUserDemandInfoData(data);
                        if (dataBack != null) {
                            dataBack.getData(myListData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  买煤求购取消
     * @param dataBack
     */
    public void getUserDemandCancel(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_CANCEL, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        UserDemand myListData = new UserDemand().getUserDemandInfoData(data);
                        if (dataBack != null) {
                            dataBack.getData(myListData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  与信息部是否达成意向
     * @param dataBack
     */
    public void getuserDemandDeliveryConfing(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_DELIVERY_CONFIRM, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        UserDemand myListData = new UserDemand().getUserDemandInfoData(data);
                        if (dataBack != null) {
                            dataBack.getData(myListData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  申请退款
     * @param dataBack
     */
    public void getuserDemandBondRefundRecordCreate(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Map<String, Object>>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_BOND_REFUND_RECORD_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String, Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String, Object>>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        UserDemand myListData = new UserDemand().getUserDemandInfoData(data);
                        if (dataBack != null) {
                            dataBack.getData(myListData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  保证金预支付
     * @param dataBack
     */
    public void getUserDemandBondPayRecordPrepaid(final DataBack dataBack) {
        Type type = new TypeToken<PayMent>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_BOND_PAY_RECORD_PREPAID, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<PayMent>() {
                    @Override
                    public void onNext(PayMent data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal130005")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  保证金微信支付
     * @param dataBack
     */
    public void getUserDemandBondPayRecordCraate(final DataBack dataBack) {
        Type type = new TypeToken<PayMent>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_BOND_PAY_RECORD_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<PayMent>() {
                    @Override
                    public void onNext(PayMent data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal130006")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  保证金余额支付
     * @param dataBack
     */
    public void getUserDemandBondPayRecordAccountPayment(final DataBack dataBack) {
        Type type = new TypeToken<PayMent>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_BOND_PAY_RECORD_ACCOUNT_PAYMENT, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<PayMent>() {
                    @Override
                    public void onNext(PayMent data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  添加买煤求购
     * @param dataBack
     */
    public void getUserDemandCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserDemand>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserDemand>>() {
                    @Override
                    public void onNext(APPData<UserDemand> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("coal130002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  删除买煤求购
     * @param dataBack
     */
    public void getUserDemandDelete(final DataBack dataBack) {
        Type type = new TypeToken<APPData<APPData>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_DELETE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<APPData>>() {
                    @Override
                    public void onNext(APPData<APPData> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        try {
                            if (data.getDataType().equals("coal130004")) {
                                if (dataBack != null) {
                                    dataBack.getData(data.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            if (dataBack != null) {
                                dataBack.getData("");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  我要拉煤列表
     * @param dataBack
     */
    public void getUserDemandBBSList(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserDemandBBS>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_BBS_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserDemandBBS>>() {
                    @Override
                    public void onNext(APPData<UserDemandBBS> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user040005")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getList());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  添加我要拉煤
     * @param dataBack
     */
    public void getUserDemandBBSCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DEMAND_BBS_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     *  留言列表
     */
    public void getUserGuestbookList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<UserGuestbook>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_GUESTBOOK_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<UserGuestbook>>() {
                    @Override
                    public void onNext(APPDataList<UserGuestbook> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030007")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getList());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  我的留言列表
     */
    public void getMyGuestbookList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<UserGuestbook>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.MY_GUESTBOOK_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<UserGuestbook>>() {
                    @Override
                    public void onNext(APPDataList<UserGuestbook> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030008")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getList());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  关注信息部
     * @param dataBack
     */
    public void getUserCoalSalesCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<InformationDepartment>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_SALES_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<InformationDepartment>>() {
                    @Override
                    public void onNext(APPData<InformationDepartment> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  取消关注信息部
     * @param dataBack
     */
    public void getUserCoalSalesDelete(final DataBack dataBack) {
        Type type = new TypeToken<APPData<InformationDepartment>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_SALES_DELETE, myParams).subscribe(
                new ProgressSubscriber<>(new SubscriberOnNextListener<APPData<InformationDepartment>>() {
                    @Override
                    public void onNext(APPData<InformationDepartment> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030002")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  提交实名认证资料
     * @param dataBack
     */
    public void getUserRealnameAuthCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserAuthenticationEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_REALNAME_AUTH_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030003")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  获取煤款详情
     * @param dataBack
     */
    public void getUserEscrowAccount(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<EscrowAccount>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_ESCROW_ACCOUNT, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<EscrowAccount>>() {
                    @Override
                    public void onNext(APPData<EscrowAccount> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030014")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  获取实名认证资料
     * @param dataBack
     */
    public void getUserRealnameAuthInfo(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserAuthenticationEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_REALNAME_AUTH_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserAuthenticationEntity>>() {
                    @Override
                    public void onNext(APPData<UserAuthenticationEntity> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030003")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  提交司机认证资料
     * @param dataBack
     */
    public void getUserDriverAuthCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DRIVER_AUTH_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (data.getDataType().equals("user030006")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     *  获取司机认证资料
     * @param dataBack
     */
    public void getUserDriverAuthInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<UserAuthenticationEntity>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DRIVER_AUTH_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<UserAuthenticationEntity>>>() {
                    @Override
                    public void onNext(List<APPData<UserAuthenticationEntity>> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        APPData<UserAuthenticationEntity> appData = new APPData<UserAuthenticationEntity>();
                        for (APPData<UserAuthenticationEntity> appData1 : data) {
                            if (appData1.getDataType().equals("user030004")) {
                                appData.setEntity(appData1.getEntity());
                            }
                            if (appData1.getDataType().equals("user030010")) {
                                appData.setList(appData1.getList());
                            }
                        }
                        if (dataBack != null) {
                            dataBack.getData(appData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 添加/编辑车辆信息
     * @param dataBack
     */
    public void getUserVehiclesCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_VEHICLES_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }

                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 司机登记信息
     * @param dataBack
     */
    public void getUserDriverRegister(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_DRIVER_REGISTER, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     * 获取访客登记信息
     * @param dataBack
     */
    public void getUserOperateVisitorList(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPDataList<OperateVisitorRegistration>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_OPERATE_VISITOR_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<OperateVisitorRegistration>>() {
                    @Override
                    public void onNext(APPDataList<OperateVisitorRegistration> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 获取访客登记信息详情
     * @param dataBack
     */
    public void getUserOperateVisitorInfo(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<Map<String, Object>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_OPERATE_VISITOR_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Map<String, Object>>>() {
                    @Override
                    public void onNext(APPData<Map<String, Object>> data) {
                        OperateVisitorRegistration appData = new OperateVisitorRegistration().getOperateVisitorRegistration(data);
                        if (dataBack != null) {
                            dataBack.getData(appData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 编辑访客登记信息
     * @param dataBack
     */
    public void getUserOperateVisitorUpdate(final DataBack dataBack,boolean flage) {
        Type type = new TypeToken<APPData<APPData>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).postHttpData(APIConfig.USER_OPERATE_VISITOR_UPDATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<APPData>>() {
                    @Override
                    public void onNext(APPData<APPData> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, flage));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 图片上传
     * @param dataBack
     */
    public void getPictureUpload(String myParamsString, final DataBack dataBack) throws Exception {
        Type type = new TypeToken<APPMessage>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).upLoadFile(APIConfig.USER_PICTURE_UPLOAD, myParamsString, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPMessage>() {
                    @Override
                    public void onNext(APPMessage data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
//                        if (data.getDataType().equals("user030001")){  //修改头像
//                            if (dataBack != null){
//                                dataBack.getData(data);
//                            }
//                        }else if (data.getDataType().equals("user030003")){ //实名认证
//                            if (dataBack != null){
//                                dataBack.getData(data);
//                            }
//                        }else if (data.getDataType().equals("user030004")){ //司机认证
//                            if (dataBack != null){
//                                dataBack.getData(data);
//                            }
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 磅单图片上传
     * @param dataBack
     * type 0货运单磅单上传  1派车单磅单上传
     */
    public void uploadSendCar(int types,String myParamsString, final DataBack dataBack) throws Exception {
        Type type = new TypeToken<List<APPData<Map<String,Object>>>>() {
        }.getType();
        String URL = "";
        if (0==types){ //
            URL = APIConfig.USER_TRANSPORT_ORDER_WEIGHT_DOC_PIC_UPLOAD;
        }else{
            URL = APIConfig.USER_COAL_ORDER_WEIGHT_DOC_PIC_UPLOAD;
        }
        new HttpDataLoader(true,getHeaderMap(), type)
                .upLoadFile(URL, myParamsString, myParams)
                .subscribe(new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Map<String,Object>>>>() {
                    @Override
                    public void onNext(List<APPData<Map<String,Object>>> dataList) {

                        if (dataList == null) {
                            return;
                        }
//                        SendCarEntity sendCarEntity = new SendCarEntity().getEntity(dataList);
//                        if (dataBack != null) {
//                            dataBack.getData(sendCarEntity);
//                        }
                        if (dataBack != null) {
                            dataBack.getData(dataList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  取消支付
     * @param dataBack
     */
    public void getConsultingFeeRecordCancel(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTINT_FEE_RECORD_CANCEL, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  资讯费支付信息获取
     * @param dataBack
     */
    public void getConsultingFeeRecordPrepaid(final DataBack dataBack) {
        Type type = new TypeToken<PayMent>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTINT_FEE_RECORD_PREPAID, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<PayMent>() {
                    @Override
                    public void onNext(PayMent data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  调后台接口生成预支付订单
     * @param dataBack
     */
    public void setUserWeChatPay(final DataBack dataBack) {
        Type type = new TypeToken<PayMent>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTINT_FEE_RECORD_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<PayMent>() {
                    @Override
                    public void onNext(PayMent data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  获取地址管理列表
     * @param dataBack
     */
    public void getUserAddressList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<UserAddress>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_ADDRESS_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<UserAddress>>() {
                    @Override
                    public void onNext(APPDataList<UserAddress> data) {
                        if (data.getDataType().equals("user030016")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  新增地址
     * @param dataBack
     */
    public void getUserAddressCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserAddress>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_ADDRESS_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserAddress>>() {
                    @Override
                    public void onNext(APPData<UserAddress> data) {
                        if (data.getDataType().equals("user030017")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  更新地址
     * @param dataBack
     */
    public void getUserAddressUpData(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserAddress>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_ADDRESS_UPDATA, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserAddress>>() {
                    @Override
                    public void onNext(APPData<UserAddress> data) {
                        if (data.getDataType().equals("user030018")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  删除地址
     * @param dataBack
     */
    public void getUserAddressDelete(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_ADDRESS_DELETE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  获取煤款流水列表
     * @param dataBack
     */
    public void getUserCoalDepositPayRecordList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<TransactionBean>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COAL_DEPOPIT_PAY_RECORD_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<TransactionBean>>() {
                    @Override
                    public void onNext(APPDataList<TransactionBean> data) {
                        if (data.getDataType().equals("user030015")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  获取支付订单列表
     * @param dataBack
     */
    public void getConsultingFeeRecordList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<PayMentOrder>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTINT_FEE_RECORD_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<PayMentOrder>>() {
                    @Override
                    public void onNext(APPDataList<PayMentOrder> data) {
                        if (data.getDataType().equals("operate060009")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  获取支付订单详情
     * @param dataBack
     */
    public void getConsultingFeeRecordInfo(final DataBack dataBack) {
        Type type = new TypeToken<List<APPData<Object>>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTINT_FEE_RECORD_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<List<APPData<Object>>>() {
                    @Override
                    public void onNext(List<APPData<Object>> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  提交投诉记录
     * @param dataBack
     */
    public void getUserConsultingFeeComplaintCreate(final DataBack dataBack) {
        Type type = new TypeToken<APPData<Object>>() {}.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTINT_FEE_COMPLAINT_CREATE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Object>>() {
                    @Override
                    public void onNext(APPData<Object> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }
    /**
     *  获取投诉记录列表
     * @param dataBack
     */
    public void getUserConsultingFeeComplaintList(final DataBack dataBack) {
        Type type = new TypeToken<APPData<ComplaintEntity>>() {}.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTINT_FEE_COMPLAINT_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<ComplaintEntity>>() {
                    @Override
                    public void onNext(APPData<ComplaintEntity> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  获取我的优惠卷
     * @param dataBack
     */
    public void getUserCouponList(final DataBack dataBack) {
        Type type = new TypeToken<APPData<CouponsEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COUPON_LIST, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<CouponsEntity>>() {
                    @Override
                    public void onNext(APPData<CouponsEntity> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  获取更多优惠卷
     * @param dataBack
     */
    public void getUserCouponMore(final DataBack dataBack) {
        Type type = new TypeToken<APPData<CouponsEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COUPON_MORE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<CouponsEntity>>() {
                    @Override
                    public void onNext(APPData<CouponsEntity> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  领取优惠卷
     * @param dataBack
     */
    public void getUserCouponObtainType(final DataBack dataBack) {
        Type type = new TypeToken<APPData<CouponsEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COUPON_OBTAIN, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<CouponsEntity>>() {
                    @Override
                    public void onNext(APPData<CouponsEntity> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  优惠卷支付
     * @param dataBack
     */
    public void getConsultingFeeRecordCouponPayment(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.CONSULTING_FEE_RECORD_COUPON_PAYMENT, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (dataBack != null) {
                            dataBack.getData(data.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  免密码登录
     * @param dataBack
     */
    public void getUserAvoidLogin(final DataBack dataBack) {
        Type type = new TypeToken<APPData<UserEntity>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_AVOID_LOGIN, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<UserEntity>>() {
                    @Override
                    public void onNext(APPData<UserEntity> data) {
                        if (data.getDataType().equals("user030001"))
                            if (dataBack != null) {
                                dataBack.getData(data.getEntity());
                            }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  用户留言/反馈
     * @param dataBack
     */
    public void commitContent(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_COMMIT_MESSAGE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Object>>() {
                    @Override
                    public void onNext(APPData<Object> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 删除车辆信息
     *
     * @param dataBack
     */
    public void deleteVehicle(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.DELETE_VEHICLE_INFO, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Object>>() {
                    @Override
                    public void onNext(APPData<Object> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *启用车辆
     *
     * @param dataBack
     */
    public void startVehicle(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.START_VEHICLE, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData<Object>>() {
                    @Override
                    public void onNext(APPData<Object> data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));   //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *用户余额提现
     * @param dataBack
     */
    public void getUserWithdrawRecord(final DataBack dataBack) {
        Type type = new TypeToken<APPData>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_WITHDRAW, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPData>() {
                    @Override
                    public void onNext(APPData data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("user030012")) {
                            if (dataBack != null) {
                                dataBack.getData(data.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, true));   //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *用户流水
     * @param dataBack
     */
    public void getUserTransactionList(final DataBack dataBack) {
        Type type = new TypeToken<APPDataList<TransactionBean>>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(APIConfig.USER_TRANSACTION, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<APPDataList<TransactionBean>>() {
                    @Override
                    public void onNext(APPDataList<TransactionBean> data) {
                        if (data == null) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                            return;
                        }
                        String dataType = data.getDataType();
                        if (dataType.equals("user030011")) {
                            if (dataBack != null) {
                                dataBack.getData(data);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GHLog.e("联网失败", e.toString());
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                    }
                }, myActivity, false));   //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     *  自定义接口 GET 网络请求
     * @param url
     * @param dataBack
     */
    public void getHttpData(String url, Type type, final DataBack dataBack) {
        new HttpDataLoader(true,getHeaderMap(), type).getHttpData(url, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<Object>() {
                    @Override
                    public void onNext(Object data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (dataBack != null) {
                            dataBack.getError(e);
                        }
                        GHLog.e("联网失败", e.toString());
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }

    /**
     * 自定义接口 POST 网络请求
     *
     * @param dataBack
     */
    public void postHttpData(String URL, final DataBack dataBack) {
        Type type = new TypeToken<JSONObject>() {
        }.getType();
        new HttpDataLoader(true,getHeaderMap(), type).postHttpData(URL, myParams).subscribe(
                new ProgressSubscriber(new SubscriberOnNextListener<JSONObject>() {
                    @Override
                    public void onNext(JSONObject data) {
                        if (dataBack != null) {
                            dataBack.getData(data);
                        }
                    }
                }, myActivity, true));  //最后一个参数表示是否显示等待条，true 表示显示，
    }


    /**
     * 在消息请求头中添加 公共参数
     * @return
     */
    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("deviceId", Build.BRAND);  //设备型号
        if (!"null".equals(BaseUtils.GetDeviceID(MyAppLication.app.getApplicationContext()))){
            headerMap.put("machineCode", BaseUtils.GetDeviceID(MyAppLication.app.getApplicationContext()));  //机器码
        }
        headerMap.put("platform", "android");  //设备类型
        headerMap.put("appName", "lmb");  //应用名称
        try {
            headerMap.put("appVersion", new BaseUtils().getVersionName(MyAppLication.app.getApplicationContext()));   //当前应用版本号
        } catch (Exception e) {
            e.printStackTrace();
        }
        headerMap.put("platformVersion", Build.VERSION.RELEASE);         //当前设备版本号
        headerMap.put("channel", new BaseUtils().getAppMetaData(MyAppLication.app.getApplicationContext(), new Config().CHANNEL_KEY)); //获取打包渠道名称
        headerMap.put("apiVersion", APIConfig.API_VERSION);   //协议版本号

        String imei = "";
        if (ActivityCompat.checkSelfPermission(MyAppLication.app.getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

        }else{
            imei  =  ((TelephonyManager) (MyAppLication.app.getApplicationContext().getSystemService(myActivity.TELEPHONY_SERVICE))).getDeviceId();
        }

        if (!StringUtils.isEmpty(imei)) {
            headerMap.put("imei", ((TelephonyManager) (MyAppLication.app.getApplicationContext().getSystemService(myActivity.TELEPHONY_SERVICE))).getDeviceId());   //获取当前手机唯一标识
        }
        headerMap.put("machineIp", BaseUtils.getIPAddress(MyAppLication.app.getApplicationContext()));   //当前手机ip
        String userId = SharedTools.getStringValue(MyAppLication.app.getApplicationContext(),"userId","-1");
        if(!userId.equals("-1")){
            headerMap.put("userId",userId);  //用户id
        }
        String cityId = SharedTools.getStringValue(MyAppLication.app.getApplicationContext(),"cityId","-1");
        if(!userId.equals("-1")){
            headerMap.put("cityId",cityId); //城市id
        }
        return headerMap;
    }

}
