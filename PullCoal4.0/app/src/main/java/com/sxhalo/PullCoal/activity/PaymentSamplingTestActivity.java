package com.sxhalo.PullCoal.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.SamplingTest;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.ColaProgress;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
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
 * 采样化验确认支付
 * Created by amoldZhang on 2019/3/20.
 */
public class PaymentSamplingTestActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.sampling_test_name)
    TextView samplingTestName;
    @Bind(R.id.sampling_test_value)
    TextView samplingTestValue;
    @Bind(R.id.sampling_test_coal_name)
    TextView samplingTestCoalName;
    @Bind(R.id.sampling_test_coal_value)
    TextView samplingTestCoalValue;
    @Bind(R.id.sampling_test_cost)
    TextView samplingTestCost;
    @Bind(R.id.sampling_test_cost_value)
    TextView samplingTestCostValue;
    @Bind(R.id.sampling_test_recipient)
    TextView samplingTestRecipient;
    @Bind(R.id.sampling_test_recipient_value)
    TextView samplingTestRecipientValue;
    @Bind(R.id.sampling_test_address)
    TextView samplingTestAddress;
    @Bind(R.id.sampling_test_address_value)
    TextView samplingTestAddressValue;
    @Bind(R.id.sampling_test_pay_amount)
    TextView samplingTestPayAmount;
    @Bind(R.id.sampling_test_deductible_amount)
    TextView samplingTestDeductibleAmount;
    @Bind(R.id.sampling_test_amount)
    TextView samplingTestAmount;
    @Bind(R.id.sampling_test_pay)
    TextView samplingTestPay;

    private SamplingTest samplingTest;
    private UserAddress userAddress;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_payment_sampling_test);
    }

    @Override
    protected void initTitle() {
        title.setText("确认支付");
        samplingTest = (SamplingTest)getIntent().getSerializableExtra("SamplingTest");
        userAddress = (UserAddress)getIntent().getSerializableExtra("UserAddress");
    }

    @Override
    protected void getData(){
        initView();
    }

    private void initView() {
        samplingTestName.setText(samplingTest.getTypeName().contains("采")?"待采样矿口：":"待化验矿口：");
        samplingTestValue.setText(samplingTest.getMineMouthName());
        samplingTestCoalName.setText(samplingTest.getTypeName().contains("采")?"待采样煤种：":"待化验煤种：");
        samplingTestCoalValue.setText(samplingTest.getCoalName());
        samplingTestCost.setText(samplingTest.getTypeName().contains("采")?"采  样 费 用：":"化  验 费 用：");
        samplingTestCostValue.setText(samplingTest.getMoney() + "元");
        samplingTestRecipient.setText(samplingTest.getTypeName().contains("采")?"采样收件人：":"化验收件人：");
        String recipientValue = "<font color=\"#333333\">" + userAddress.getContactPerson() + "</font>";
        recipientValue = recipientValue + "   " + "<font color=\"#e55f48\">" + userAddress.getContactPhone() + "</font>";
        samplingTestRecipientValue.setText(Html.fromHtml(recipientValue));
        samplingTestAddress.setText(samplingTest.getTypeName().contains("采")?"采样收件地址：":"化验收件地址：");
        samplingTestAddressValue.setText(userAddress.getFullRegionName() + userAddress.getAddress());

        samplingTestPayAmount.setText("需付金额        " + samplingTest.getMoney() + "元");

        double denomination = Double.valueOf(StringUtils.isEmpty(samplingTest.getDenomination())?"0":samplingTest.getDenomination());
        if (denomination == 0){
            samplingTestDeductibleAmount.setText("代金券          " + "0" + "元");
        }else{
            samplingTestDeductibleAmount.setText("代金券          -" + denomination + "元");
        }
        Double money = Double.valueOf(StringUtils.isEmpty(samplingTest.getMoney())?"0":samplingTest.getMoney());

        Double num = money - denomination <=0 ? 0 :money - denomination;
        samplingTestAmount.setText(num + "元");
        if (num == 0){
            samplingTestPay.setText("支付");
        }else{
            samplingTestPay.setText("微信支付");
        }
    }

    @OnClick({R.id.title_bar_left, R.id.sampling_test_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                onBackPressed();
                break;
            case R.id.sampling_test_pay:
                // 需要联合微信支付
                if ("微信支付".equals(samplingTestPay.getText().toString().trim()) && !StringUtils.isEmpty(samplingTest.getPrepayId())){
                    doWXPayment(samplingTest.getPrepayId());
                }else{  //直接支付
                    doAccountPay();
                }
                break;
        }
    }

    private void getPay(){
        //通知结束上一个界面（采样化验提交界面）
        setResult(RESULT_OK);
        //跳转详情界面
        UIHelper.jumpAct(mContext,SamplingAnalysisDetailsActivity.class,samplingTest,true);
    }

    /**
     * 直接抵扣
     */
    private void doAccountPay() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("sampleId", samplingTest.getSampleId());
            new DataUtils(this,params).getOperatesampleCouponPay(new DataUtils.DataBack<APPData>() {

                @Override
                public void getData(APPData appData) {
                    displayToast("抵扣成功");
                    getPay();
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            },true);
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeCount != null){
            timeCount.cancel();
        }
        if (mColaProgress != null){
            mColaProgress.dismiss();
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
    private ColaProgress mColaProgress;

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
                }
            }
        };
        timeCount.start();
    }

    /**
     * 微信支付
     */
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
    private void doWXPayment(String prepayId) {
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
                        getPay();
                        break;
                    case -1:
                        displayToast("微信支付失败");
                        GHLog.e("微信支付失败",userInfo.getErrText());
                        break;
                    case -2: // 本次支付取消
                        displayToast("微信支付取消");
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
        LinkedHashMap<String,String> map = getParamsMap(prepayId);
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
     * @param prepayId  微信支付单号
     * @return
     */
    private LinkedHashMap<String,String> getParamsMap(String prepayId){
        LinkedHashMap<String,String> stringMap = new LinkedHashMap<>();
        stringMap.put("appid",new Config().weChat_APPID);
        stringMap.put("noncestr",getNonceStr());
        stringMap.put("package","Sign=WXPay");
        stringMap.put("partnerid",new Config().weChat_MCH_Id);
        stringMap.put("prepayid", prepayId);
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
    private void getOperatesampleCancel(){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("tradeNo", samplingTest.getTradeNo());
            new DataUtils(this,params).getOperatesampleCancel(new DataUtils.DataBack<APPData>() {

                @Override
                public void getData(APPData appData) {
                    displayToast("取消支付");
                    finish();
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            },true);
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
        }
    }


    @Override
    public void onBackPressed() {
        if (mColaProgress != null){
            mColaProgress.dismiss();
        }
        showDaiLog("您是否确定取消支付?");
    }

    private void showDaiLog(String message) {
        new RLAlertDialog(this, "系统提示", message, "确定",
                "取消", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                getOperatesampleCancel();
            }

            @Override
            public void onRightClick() {

            }
        }).show();
    }
}
