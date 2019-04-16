package com.sxhalo.PullCoal.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.PayMent;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.ColaProgress;
import com.sxhalo.PullCoal.utils.AdvancedCountdownTimer;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.MD5Util;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.wxapi.ThirdUserVerify;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 保证金联合支付界面
 * Created by amoldZhang on 2018/5/10.
 */
public class MarginPaymentUnionActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.payment_type)
    TextView paymentType;
    @Bind(R.id.payment_name)
    TextView paymentName;
    @Bind(R.id.payment_num)
    TextView paymentNum;
    @Bind(R.id.payment_balance_num)
    TextView paymentBalanceNum;
    @Bind(R.id.do_payment_number)
    TextView doPaymentNumber;
    @Bind(R.id.do_payment_type)
    TextView doPaymentType;
    @Bind(R.id.do_payment_sum)
    TextView doPaymentSum;
    @Bind(R.id.wechat_pay_view)
    LinearLayout wechatPayView;

    private PayMent data;
    private ColaProgress mColaProgress;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_margin_payment_union);
    }

    @Override
    protected void initTitle() {
        title.setText("微信支付");
        data = (PayMent)getIntent().getSerializableExtra("PayMent");
        initView();
    }

    @Override
    protected void getData() {

    }

    /**
     * 支付基本信息显示
     */
    private void initView() {
        paymentType.setText(data.getCostType());
        paymentName.setText(data.getCostName());
        paymentNum.setText(StringUtils.setNumLenth(Float.valueOf(data.getCostAmount())/100, 2));
        paymentBalanceNum.setText(StringUtils.setNumLenth(Float.valueOf(data.getPocketAmount())/100, 2));

        doPaymentNumber.setText(data.getTradeNo());
        doPaymentType.setText("微信支付");
        Integer cost = Integer.valueOf(data.getCostAmount()) - Integer.valueOf(data.getPocketAmount());
        doPaymentSum.setText(StringUtils.setNumLenth(Float.valueOf(cost)/100, 2));
    }


    @OnClick({R.id.title_bar_left, R.id.goto_payment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                getConsultingFeeRecordCancel();
                break;
            case R.id.goto_payment:
                doWXPayment();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeCount != null){
            timeCount.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (timeCount != null){
            timeCount.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeCount != null){
            timeCount.pause();
        }
    }

    /**
     * 时间计时器
     */
    private AdvancedCountdownTimer timeCount;

    private void startTimeCount(long millisInFuture, long countDownInterval){
        timeCount =  new AdvancedCountdownTimer(millisInFuture,countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished, int percent) {  //计时过程显示
                if (mColaProgress != null){
                    mColaProgress.setMessage("支付中，请稍后 "+ millisUntilFinished / 1000 + " 秒");
                }
                if ((millisUntilFinished / 1000) == 0){
                    displayToast("您的操作已经超时，请重新再式！");
                }
            }

            @Override
            public void onFinish() {  //计时完毕时触发
                try {
                    if (timeCount != null){
                        timeCount.cancel();
                    }
                    if (mColaProgress != null){
                        mColaProgress.dismiss();
                    }
                } catch (Exception e) {
                    GHLog.e(getClass().getName(),e.toString());
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                }
            }
        };
        timeCount.start();
    }

    /**
     * 字段名	变量名	类型	必填	示例值	描述
     应用ID	appid	String(32)	是	wx8888888888888888	微信开放平台审核通过的应用APPID
     商户号	partnerid	String(32)	是	1900000109	微信支付分配的商户号
     预支付交易会话ID	prepayid	String(32)	是	WX1217752501201407033233368018	微信返回的支付交易会话ID
     扩展字段	package	String(128)	是	Sign=WXPay	暂填写固定值Sign=WXPay
     随机字符串	noncestr	String(32)	是	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
     时间戳	timestamp	String(10)	是	1412000000	时间戳，请见接口规则-参数规定
     签名	sign	String(32)	是	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
     */
    private void doWXPayment() {
        startTimeCount(15000,1000);
        mColaProgress = ColaProgress.show(this, "支付中，请稍后...", false, false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
        new ThirdUserVerify().getThirdUserData(new ThirdUserVerify.ThirdUserData() {
            @Override
            public <T> void setThirdUserData(T t) {
                if (mColaProgress != null){
                    mColaProgress.dismiss();
                }
                ThirdUserInfo userInfo = (ThirdUserInfo)t;
                switch (userInfo.getErrCode()){
                    case 0:  //成功
                        if (MarginPaymentActivity.coalBack != null){
                            MarginPaymentActivity.coalBack.onCallBack();
                        }
                        finish();
                        break;
                    case -1:
                        GHLog.e("微信支付失败",userInfo.getErrText());
                        break;
                    case -2:
                        getConsultingFeeRecordCancel();
                        break;
                    default:
                        break;
                }
                if (timeCount != null){
                    timeCount.cancel();
                }
            }
        });
        // 将该app注册到微信
        IWXAPI api = MyAppLication.getIWXAPI();
        LinkedHashMap<String,String> map = getParamsMap(data);
        PayReq req = new PayReq();
        req.appId			=  map.get("appid");
        req.nonceStr		=  map.get("noncestr");
        req.prepayId		=  map.get("prepayid");
        req.partnerId		=  map.get("partnerid");
        req.packageValue	=  map.get("package");
        req.timeStamp		=  map.get("timestamp");
        req.sign			=  genAppSign(map);
        // 吊起微信支付
        api.sendReq(req);
    }

    /**
     *  添加微信支付请求参数
     * @param data
     * @return
     */
    private LinkedHashMap<String,String> getParamsMap(PayMent data){
        LinkedHashMap<String,String> stringMap = new LinkedHashMap<>();
        stringMap.put("appid",new Config().weChat_APPID);
        stringMap.put("noncestr",getNonceStr());
        stringMap.put("package","Sign=WXPay");
        stringMap.put("partnerid",new Config().weChat_MCH_Id);
        stringMap.put("prepayid", data.getPrepayId());
        stringMap.put("timestamp", (System.currentTimeMillis() /1000) + "");
        return stringMap;
    }

    /**
     * 生成微信支付密钥
     * @param params
     * @return
     */
    private String genAppSign(Map<String,String> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() > 0) {
            Set<String> keys = params.keySet();
            for (String headerKey : keys) {
                sb.append(headerKey);
                sb.append('=');
                sb.append(params.get(headerKey));
                sb.append('&');
            }
        }
        GHLog.i("stringA",sb.toString());
        sb.append("key=");  //注：key为商户平台设置的密钥key
        sb.append(new Config().weChat_APP_KEY);
        GHLog.i("stringSignTemp",sb.toString());
        String sign = null; //注：MD5签名方式
        try {
            sign = MD5Util.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
        GHLog.i("sign",sign);
        return sign;
    }

    /**
     * 生成微信支付随机字符串
     * @return
     */
    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
    }

    /**
     * 用户取消支付
     */
    private void getConsultingFeeRecordCancel(){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("tradeNo", data.getTradeNo());
            new DataUtils(this,params).getConsultingFeeRecordCancel(new DataUtils.DataBack<APPData>() {

                @Override
                public void getData(APPData payMent) {
                    try {
                        displayToast("取消支付");
                        finish();
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }
}
