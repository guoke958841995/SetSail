package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.CouponsEntity;
import com.sxhalo.PullCoal.model.PayMent;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import butterknife.Bind;
import butterknife.OnClick;

/**
 *  资讯费支付界面
 * Created by amoldZhang on 2018/1/3.
 */
public class PaymentInformationActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.payment_type)
    TextView paymentType;
    @Bind(R.id.payment_name)
    TextView paymentName;
    @Bind(R.id.payment_num)
    TextView paymentNum;
    @Bind(R.id.payment_time)
    TextView paymentTime;
    @Bind(R.id.amount_to_paid)
    TextView amountToPaid;
    @Bind(R.id.balance_textview)
    TextView balanceTV;
    @Bind(R.id.deductible_amount)
    TextView deductibleText;

    @Bind(R.id.goto_payment)
    TextView goToPayment;
    @Bind(R.id.available_number)
    TextView availableNumber;


    //当前选中的支付方式
    private String TYPE;
    private String targetId;
    private PayMent data;
    private HashMap<String, String> selectParams = new HashMap<String, String>();
    private ArrayList<CouponsEntity> couponsEntityList = new ArrayList<CouponsEntity>();
    private int deductibleAmoount = 0; // 抵扣券总额
    private int inforCost; // 资讯费金额
    private int balance = 0; //当前用户使用余额数
    private int amount; //当前用户待支付金额
    private int appropriateAmount = 0; //当前系统默认计算出的最优代金券支付额度

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information_payment);
    }

    @Override
    protected void initTitle() {
        TYPE = getIntent().getStringExtra("type");
        title.setText("资讯费支付");

        if (TYPE.equals("0")){
            Coal coal = (Coal)getIntent().getSerializableExtra("Entity");
            targetId = coal.getGoodsId();
        }else{
            TransportMode transportMode = (TransportMode)getIntent().getSerializableExtra("Entity");
            targetId = transportMode.getTransportId();
        }
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("consultingFeeType", TYPE);
            params.put("targetId", targetId);
            new DataUtils(this,params).getConsultingFeeRecordPrepaid(new DataUtils.DataBack<PayMent>() {

                @Override
                public void getData(PayMent data) {
                    try {
                        data.setConsultingFeeType(TYPE);
                        data.setTargetId(targetId);
                        PaymentInformationActivity.this.data = data;
                        if (appropriateAmount != 0){
                            PaymentInformationActivity.this.data.setAppropriateAmount(appropriateAmount);
                        }
                        setView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView() {
        inforCost = Integer.valueOf(data.getCostAmount());
        if (StringUtils.isEmpty(paymentType.getText().toString())){
            paymentType.setText(data.getCostType());
            paymentName.setText(data.getCostName());
            paymentNum.setText((Double.valueOf(data.getCostAmount())/100) + "");
            paymentTime.setText(data.getValidTime());
        }

        if ("0".equals(data.getAvailableQuantity()) && "0".equals(data.getAvailableNumber())){
            deductibleText.setText("无可用");
            deductibleText.setTextColor(getResources().getColor(R.color.detail_title_text_color));
            availableNumber.setVisibility(View.GONE);
        }else{
            if (deductibleAmoount == 0){
                deductibleText.setText("去使用");
                deductibleText.setTextColor(getResources().getColor(R.color.text_color_red));
                if ("0".equals(data.getAvailableNumber())){
                    availableNumber.setVisibility(View.GONE);
                }else{
                    availableNumber.setVisibility(View.VISIBLE);
                    availableNumber.setText(data.getAvailableNumber()+"张可使用");
                }
            }else{
                availableNumber.setVisibility(View.VISIBLE);
                deductibleText.setText(("已抵扣"+ Double.valueOf(deductibleAmoount)/100)  + "元");
                deductibleText.setTextColor(getResources().getColor(R.color.detail_title_text_color));
            }
        }

        amount = inforCost - deductibleAmoount;
        if (amount <= 0){
            amount = 0 ;
        }
        //当余额金额数足够抵扣资讯费时
        balance = Integer.valueOf(data.getPocketAmount());
        if (amount <= balance){
            balance = amount;
            amount = 0;
        }else{  //当余额不足以抵扣时
            if (balance -  amount >= 0){
                balance = amount;
                amount = 0 ;
            }else {
                amount = amount - balance ;
            }
        }
        if (balance != 0){
            balanceTV.setText("-"+ (Double.valueOf(balance)/100) + "元");
            balanceTV.setTextColor(getResources().getColor(R.color.text_color_red));
        }else{
            balanceTV.setText("已抵扣"+ (Double.valueOf(balance)/100) + "元");
            balanceTV.setTextColor(getResources().getColor(R.color.detail_title_text_color));
        }
        amountToPaid.setText((Double.valueOf(amount)/100)  + "元");

        if (amount == 0){
            goToPayment.setText("立即支付");
        }else{
            goToPayment.setText("微信支付");
        }

        data.setCouponNum(deductibleAmoount + "");
        data.setBalanceNum(balance + "");
    }


    @OnClick({R.id.title_bar_left, R.id.goto_payment,R.id.layout_my_coupons})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_my_coupons:
                //当前可用代金券种类为0 并且 不能获取更多时
                if (Integer.valueOf(data.getAvailableSpecies()) == 0 && "0".equals(data.getAvailableQuantity().toString())){
                    displayToast("您当前没有可用代金券，请选择其他支付方式！");
                }else{
                    Intent intent = new Intent(PaymentInformationActivity.this,PaymentConfirmationActivity.class);
                    intent.putExtra("couponsEntityList",couponsEntityList);
                    intent.putExtra("selectParams",selectParams);
                    intent.putExtra("PayMent",data);
                    intent.putExtra("Entity",getIntent().getSerializableExtra("Entity"));
                    startActivityForResult(intent, Constant.REFRESH_CODE);
                }
                break;
            case R.id.goto_payment:
                if ( (deductibleAmoount == 0 && !"0".equals(data.getAvailableQuantity())) || (deductibleAmoount == 0 && !"0".equals(data.getAvailableNumber())) ){
                    showDaiLog("您有可使用的代金券，是否去使用？");
                    return;
                }
                if (amount == 0){  //直接抵用支付逻辑
                    doCouponPayment();
                }else{  //需要联合微信支付逻辑
                    if (deductibleAmoount != 0 || balance != 0){
                        setUnionWeChatPay();
                    }else{
                        setUserWeChatPay();
                    }
                }
                break;
        }
    }

    /**
     * 当抵扣金额不足时联合微信支付
     */
    private void setUnionWeChatPay(){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("payMethord", "coupon");
            if (!StringUtils.isEmpty(selectParams.get("couponId")) && !StringUtils.isEmpty(selectParams.get("useNumber"))){
                params.put("couponId", selectParams.get("couponId"));
                params.put("useNumber", selectParams.get("useNumber"));
            }
            if (balance != 0){
                params.put("accountMoney", balance + "");
            }
            params.put("totalFee", amount + ""); //待支付金额
            params.put("consultingFeeType", data.getConsultingFeeType());
            params.put("targetId", data.getTargetId());
            new DataUtils(this,params).setUserWeChatPay(new DataUtils.DataBack<PayMent>() {

                @Override
                public void getData(PayMent payMent) {
                    try {
                        if (data != null){
                            data.setTradeNo(payMent.getTradeNo());
                            data.setPrepayId(payMent.getPrepayId());
                        }
                        Intent intent = new Intent(PaymentInformationActivity.this,PaymentConfirmationUnionActivity.class);
                        intent.putExtra("PayMent",data);
                        intent.putExtra("Entity",getIntent().getSerializableExtra("Entity"));
                        startActivity(intent);
                        setCallBack(new CallBack() {
                            @Override
                            public void onCallBack() {
                                finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2060022".equals(e.getMessage())){
                        displayToast("已支付，请不要重复支付！");
                    }else if ("2060030".equals(e.getMessage())){
                        displayToast("账户异常，请联系客服！");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 抵用全额支付
     */
    private void doCouponPayment() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("payMethord", "coupon");
            params.put("targetId", data.getTargetId());
            params.put("consultingFeeType", data.getConsultingFeeType());
            if (deductibleAmoount != 0){
                params.put("couponId", selectParams.get("couponId"));
                params.put("useNumber", selectParams.get("useNumber"));
            }
            if (balance != 0){
                params.put("accountMoney", balance + "");
            }
            new DataUtils(this,params).getConsultingFeeRecordCouponPayment(new DataUtils.DataBack<String>() {

                @Override
                public void getData(String data) {
                    try {
                        displayToast(data);
                        gotoNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2060011".equals(e.getMessage())){
                        displayToast("已支付，请不要重复支付！");
                    }else if ("2060030".equals(e.getMessage())){
                        displayToast("账户异常，请联系客服！");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoNext(){
        Intent intent = new Intent();
        //跳转对应商品详情，并关闭之前的两个页面
        String entity_type = data.getConsultingFeeType();
        if (entity_type.equals("0")){
            intent.setClass(PaymentInformationActivity.this,DetailsWebActivity.class);
            intent.putExtra("Coal",getIntent().getSerializableExtra("Entity"));
            intent.putExtra("inforDepartId","煤炭详情");
        }else{
            intent.setClass(PaymentInformationActivity.this,TransportDetailActivity.class);
            TransportMode transportMode = (TransportMode)getIntent().getSerializableExtra("Entity");
            intent.putExtra("waybillId", transportMode.getTransportId());//货运id
            sendBroadcast();
        }
        //支付成功后通知煤炭 货运列表页面刷新数据
        mContext.sendBroadcast(new Intent(HomePagerFragment.RECEIVED_ACTION));

        startActivity(intent);
        finish();
    }

    /**
     * 用户登录成功 发送广播 刷新相关页面
     */
    private void sendBroadcast() {
        Intent intent = new Intent(Constant.REFRESH_CODE + "");
        sendBroadcast(intent);
    }




    private void showDaiLog(String message) {
        new RLAlertDialog(this, "系统提示", message, "立即支付",
                "去使用", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                if (amount == 0){  //直接抵用支付逻辑
                    doCouponPayment();
                }else{  //需要联合微信支付逻辑
                    if (deductibleAmoount != 0 || balance != 0){
                        setUnionWeChatPay();
                    }else{
                        setUserWeChatPay();
                    }
                }
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(PaymentInformationActivity.this,PaymentConfirmationActivity.class);
                intent.putExtra("couponsEntityList",couponsEntityList);
                intent.putExtra("selectParams",selectParams);
                intent.putExtra("PayMent",data);
                intent.putExtra("Entity",getIntent().getSerializableExtra("Entity"));
                startActivityForResult(intent, Constant.REFRESH_CODE);
            }
        }).show();
    }

    /**
     * 支付方式 coupon：优惠券支付，wechat：微信支付
     */
    private void setUserWeChatPay(){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("payMethord", "wechat");
            params.put("consultingFeeType", TYPE);
            params.put("targetId", targetId);
            params.put("totalFee", amount + "");
            new DataUtils(this,params).setUserWeChatPay(new DataUtils.DataBack<PayMent>() {

                @Override
                public void getData(PayMent payMent) {
                    try {
                        if (data != null){
                            data.setTradeNo(payMent.getTradeNo());
                            data.setPrepayId(payMent.getPrepayId());
                        }
                        Intent intent = new Intent(PaymentInformationActivity.this,PaymentConfirmationUnionActivity.class);
                        intent.putExtra("PayMent",data);
                        intent.putExtra("Entity",getIntent().getSerializableExtra("Entity"));
                        startActivity(intent);
                        setCallBack(new CallBack() {
                            @Override
                            public void onCallBack() {
                                finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2060022".equals(e.getMessage())){
                        displayToast("已支付，请不要重复支付！");
                    }else if ("2060030".equals(e.getMessage())){
                        displayToast("账户异常，请联系客服！");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CallBack coalBack;
    public void setCallBack(CallBack coalBack){
        this.coalBack = coalBack;
    }
    public interface CallBack{
        void onCallBack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK && requestCode == Constant.REFRESH_CODE) {
            selectParams = (HashMap<String, String>)data.getSerializableExtra("selectParams");
            couponsEntityList = (ArrayList<CouponsEntity>)data.getSerializableExtra("couponsEntityList");
            deductibleAmoount = data.getIntExtra("deductibleAmoount",0);
            appropriateAmount = Integer.valueOf(selectParams.get("appropriateAmount"));
            getData();
        }
    }
}
