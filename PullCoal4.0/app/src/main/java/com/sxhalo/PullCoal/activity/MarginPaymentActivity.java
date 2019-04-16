package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.PayMent;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.CallBack;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 保证金支付界面
 * Created by amoldZhang on 2018/4/19.
 */
public class MarginPaymentActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.payment_type)
    TextView paymentType;
    @Bind(R.id.payment_name)
    TextView paymentName;
    @Bind(R.id.payment_num)
    TextView paymentNum;
    @Bind(R.id.balance_textview)
    TextView balanceTextview;
    @Bind(R.id.amount_to_paid)
    TextView amountToPaid;
    @Bind(R.id.goto_payment)
    TextView goToPayment;


    private int pocketAmount;  //账户余额
    private int costAmount;    //需要支付的保证金金额
    private PayMent payMent;   // 预支付详情

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_margin_payment);
    }

    @Override
    protected void initTitle() {
        title.setText("保证金支付");
    }

    @Override
    protected void getData() {
        try {
            UserDemand userDemand = (UserDemand) getIntent().getSerializableExtra("UserDemand");
            LinkedHashMap<String, String> praram = new LinkedHashMap<String, String>();
            praram.put("demandId", userDemand.getDemandId());
            new DataUtils(this, praram).getUserDemandBondPayRecordPrepaid(new DataUtils.DataBack<PayMent>() {

                @Override
                public void getData(PayMent data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        payMent = data;
                        initView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        paymentType.setText(payMent.getCostType());
        paymentName.setText(payMent.getCostName());
        paymentNum.setText(StringUtils.setNumLenth(Float.valueOf(payMent.getCostAmount())/100, 2));

        //当前账户余额
        pocketAmount = Integer.valueOf(payMent.getPocketAmount());
        //当前需要支付的保证金金额
        costAmount = Integer.valueOf(payMent.getCostAmount());

        if (pocketAmount == 0){
            balanceTextview.setText("已抵扣"+ (StringUtils.setNumLenth(Float.valueOf(pocketAmount)/100, 2)) + "元");
            balanceTextview.setTextColor(getResources().getColor(R.color.detail_title_text_color));
        }
        if (pocketAmount < costAmount){
            balanceTextview.setText("-"+ (StringUtils.setNumLenth(Float.valueOf(pocketAmount)/100, 2)) + "元");
            balanceTextview.setTextColor(getResources().getColor(R.color.text_color_red));

            amountToPaid.setText((StringUtils.setNumLenth(Float.valueOf(costAmount - pocketAmount)/100, 2))  + "元");
            goToPayment.setText("微信支付");

        }else{
            balanceTextview.setText("-"+ (StringUtils.setNumLenth(Float.valueOf(costAmount)/100, 2)) + "元");
            balanceTextview.setTextColor(getResources().getColor(R.color.text_color_red));

            amountToPaid.setText((StringUtils.setNumLenth(Float.valueOf("0")/100, 2))  + "元");
            goToPayment.setText("立即支付");
        }
    }

    @OnClick({R.id.title_bar_left, R.id.goto_payment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                setResult(RESULT_OK, new Intent());
                finish();
                break;
            case R.id.goto_payment:
                // 需要联合微信支付
                if ("微信支付".equals(goToPayment.getText().toString().trim())){
                    doWXPay();
                }else{  //直接使用余额支付
                    doAccountPay();
                }
                break;
        }
    }

    /**
     * 余额支付
     */
    private void doAccountPay() {
        try {
            final UserDemand userDemand = (UserDemand) getIntent().getSerializableExtra("UserDemand");
            LinkedHashMap<String, String> praram = new LinkedHashMap<String, String>();
            praram.put("demandId", userDemand.getDemandId());
            praram.put("accountMoney", costAmount + "");  // 账户余额支付金额
            new DataUtils(this, praram).getUserDemandBondPayRecordAccountPayment(new DataUtils.DataBack<PayMent>() {
                @Override
                public void getData(PayMent data) {
                    try {
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2130004".equals(e.getMessage())){
                        displayToast("网络连接失败，请稍后再试！");
                    }else if ("2060011".equals(e.getMessage())){
                        displayToast("已支付，请不要重复支付！");
                    }else if ("2060030".equals(e.getMessage())){
                        displayToast("账户异常，请联系客服！");
                    }else if ("2060031".equals(e.getMessage())){
                        displayToast("账户异常，请联系客服！");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用代金券和微信支付
     */
    private void doWXPay(){
        try {
            final UserDemand userDemand = (UserDemand) getIntent().getSerializableExtra("UserDemand");
            LinkedHashMap<String, String> praram = new LinkedHashMap<String, String>();
            praram.put("demandId", userDemand.getDemandId());
            praram.put("totalFee", (costAmount - pocketAmount)+ "");  // 抵扣后金额
            praram.put("accountMoney", pocketAmount + "");  // 账户余额支付金额
            new DataUtils(this, praram).getUserDemandBondPayRecordCraate(new DataUtils.DataBack<PayMent>() {
                @Override
                public void getData(PayMent data) {
                    try {
                        if (data != null){
                            payMent.setTradeNo(data.getTradeNo());
                            payMent.setPrepayId(data.getPrepayId());
                        }
                        Intent intent = new Intent(MarginPaymentActivity.this,MarginPaymentUnionActivity.class);
                        intent.putExtra("PayMent",payMent);
                        intent.putExtra("UserDemand",userDemand);
                        startActivity(intent);
                        setCallBack(new CallBack() {
                            @Override
                            public void onCallBack() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
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
}
