package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.PayMentOrder;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 支付记录详情
 * Created by amoldZhang on 2018/1/8.
 */
public class PaymentRecordDetaileActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.payment_record_fee)
    TextView paymentRecordFee;
    @Bind(R.id.common_problem)
    LinearLayout commonProblem;
    @Bind(R.id.receivables_tel)
    LinearLayout receivablesTel;
    @Bind(R.id.complaint_tel)
    LinearLayout complaintTel;
    @Bind(R.id.payment_record_type)
    TextView paymentRecordType;
    @Bind(R.id.payment_record_infontent_type)
    TextView paymentRecordInfontentType;
    @Bind(R.id.payment_record_receivables)
    TextView paymentRecordReceivables;
    @Bind(R.id.payment_record_pay_time)
    TextView paymentRecordPayTime;
    @Bind(R.id.payment_record_odd_numbers)
    TextView paymentRecordOddNumbers;
    @Bind(R.id.payment_coal_image)
    ImageView paymentCoalImage;
    @Bind(R.id.payment_coal_name)
    TextView paymentCoalName;
    @Bind(R.id.payment_coal_term)
    TextView paymentCoalTerm;
    @Bind(R.id.payment_coal_calorificvalue)
    TextView paymentCoalCalorificvalue;
    @Bind(R.id.payment_coal_storagerate)
    TextView paymentCoalStoragerate;
    @Bind(R.id.coal_line)
    View coalLine;
    @Bind(R.id.paytemt_coal_view)
    LinearLayout paytemtCoalView;
    @Bind(R.id.payment_freight_start_address)
    TextView paymentFreightStartAddress;
    @Bind(R.id.payment_freight_end_address)
    TextView paymentFreightEndAddress;
    @Bind(R.id.layout_from)
    LinearLayout layoutFrom;
    @Bind(R.id.payment_freight_transport_cost)
    TextView paymentFreightTransportCost;
    @Bind(R.id.payment_freight_surplus)
    TextView paymentFreightSurplus;
    @Bind(R.id.ll_surplus)
    LinearLayout llSurplus;
    @Bind(R.id.freight_type)
    TextView freightType;
    @Bind(R.id.payment_freight_view)
    RelativeLayout paymentFreightView;

    //代金券支付
    @Bind(R.id.coupon_view)
    RelativeLayout couponView;
    @Bind(R.id.payment_coupon_coast)
    TextView paymentCouponCoast;

    //微信支付
    @Bind(R.id.weixin_view)
    RelativeLayout weixinView;
    @Bind(R.id.payment_weixin_coast)
    TextView paymentWeixinCoast;

    //余额支付
    @Bind(R.id.account_money_view)
    RelativeLayout accountMoneyView;
    @Bind(R.id.payment_account_money_coast)
    TextView paymentAccountMoneyCoast;

    private PayMentOrder payMentOrder;
    private Coal coal;
    private TransportMode transportMode;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_payment_record_detaile);
    }

    @Override
    protected void initTitle() {
        title.setText("资讯费支付详情");
    }

    @Override
    protected void getData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            PayMentOrder payMentOrder = (PayMentOrder)getIntent().getSerializableExtra("PayMentOrder");
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("tradeNo", payMentOrder.getTradeNo());
            new DataUtils(this, params).getConsultingFeeRecordInfo(new DataUtils.DataBack<List<APPData<Object>>>() {
                @Override
                public void getData(List<APPData<Object>> appDataList) {
                    try {
                        if (appDataList == null) {
                            return;
                        }
                        setData(appDataList);
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        GHLog.e("货运列表联网", e.toString());
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    private void setData(List<APPData<Object>> appDataList) {
        try {
            Gson gson = new Gson();
            for (APPData<Object> appData : appDataList){
                if (appData.getDataType().equals("operate060008")){
                    String jsonString = gson.toJson(appData.getEntity());
                    payMentOrder =  gson.fromJson(jsonString,PayMentOrder.class);
                }else if (appData.getDataType().equals("coal070002")){
                    String jsonString = gson.toJson(appData.getEntity());
                    coal =  gson.fromJson(jsonString,Coal.class);
                } else if (appData.getDataType().equals("coal090002")){
                    String jsonString = gson.toJson(appData.getEntity());
                    transportMode =  gson.fromJson(jsonString,TransportMode.class);
                }
            }
            initView();
        } catch (JsonSyntaxException e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("数据解析",e.toString());
        }
    }

    private void initView() {
        if (!StringUtils.isEmpty(payMentOrder.getCouponMoney()) && !"0".equals(payMentOrder.getCouponMoney())){
            couponView.setVisibility(View.VISIBLE);
            paymentCouponCoast.setText("￥" + (Double.valueOf(payMentOrder.getCouponMoney())/100));
        }
        if (!StringUtils.isEmpty(payMentOrder.getWxTotalFee()) && !"0".equals(payMentOrder.getWxTotalFee())){
            weixinView.setVisibility(View.VISIBLE);
            paymentWeixinCoast.setText("￥" + (Double.valueOf(payMentOrder.getWxTotalFee())/100));
        }

        if (!StringUtils.isEmpty(payMentOrder.getAccountMoney()) && !"0".equals(payMentOrder.getAccountMoney())){
            accountMoneyView.setVisibility(View.VISIBLE);
            paymentAccountMoneyCoast.setText("￥" + (Double.valueOf(payMentOrder.getAccountMoney())/100));
        }

        paymentRecordFee.setText((Double.valueOf(payMentOrder.getConsultingFee())/100) + "");
        String payState = "";
        // 0、待支付；1、进行中；2、支付成功；3、支付失败
        if (payMentOrder.getPayState().equals("0")){
            payState = "待支付";
        }else if (payMentOrder.getPayState().equals("1")){
            payState = "进行中";
        } else if (payMentOrder.getPayState().equals("2")){
            payState = "支付成功";
        }else if(payMentOrder.getPayState().equals("3")){
            payState = "支付失败";
        }else if(payMentOrder.getPayState().equals("4")){
            payState = "已取消";
        }else if(payMentOrder.getPayState().equals("5")){
            payState = "已退款";
            paymentRecordType.setTextColor(getResources().getColor(R.color.actionsheet_red));
        }
        paymentRecordType.setText(payState);
        paymentRecordInfontentType.setText(payMentOrder.getCostType());
        paymentRecordPayTime.setText(payMentOrder.getPayEndTime());
        paymentRecordOddNumbers.setText(payMentOrder.getTradeNo());

        if (payMentOrder.getConsultingFeeType().equals("0")){  //煤炭信息
            paytemtCoalView.setVisibility(View.VISIBLE);
            paymentRecordReceivables.setText(coal.getCompanyName());

            if (Utils.isImagesTrue(coal.getImageUrl()) == 200 ){
                getImageManager().loadCoalTypeUrlImage(coal.getImageUrl(),coal.getCategoryImage(),paymentCoalImage);
            }else{
                getImageManager().loadCoalTypeUrlImage(coal.getCategoryImage(),paymentCoalImage);
            }

            paymentCoalName.setText(coal.getCoalName());
            if (payMentOrder.getPayState().equals("5")){
                paymentCoalTerm.setVisibility(View.INVISIBLE);
            }else {
                paymentCoalTerm.setVisibility(View.VISIBLE);
                paymentCoalTerm.setText(payMentOrder.getLicenseMinute());
            }
            paymentCoalCalorificvalue.setText(coal.getCalorificValue() + "kCal/kg");
            paymentCoalStoragerate.setText(coal.getMineMouthName());
        }else {
            paymentFreightView.setVisibility(View.VISIBLE);
            paymentRecordReceivables.setText(transportMode.getCompanyName());

            paymentFreightStartAddress.setText(transportMode.getFromPlace());
            paymentFreightEndAddress.setText(transportMode.getToPlace());
            paymentFreightTransportCost.setText(transportMode.getCost());

            // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
            String transportType = transportMode.getTransportType();
            if (transportType.equals("0")) {
                llSurplus.setVisibility(View.VISIBLE);
                paymentFreightSurplus.setText(transportMode.getSurplusNum());
                freightType.setText(" / 煤炭");
            } else if (transportType.equals("1")) {
                llSurplus.setVisibility(View.GONE);
                freightType.setText("    长期货运 / 煤炭");
            } else if (transportType.equals("2")) {
                freightType.setText(" / 普货");

                llSurplus.setVisibility(View.VISIBLE);
                paymentFreightSurplus.setText(transportMode.getSurplusNum());
            }

        }

    }


    @OnClick({R.id.title_bar_left,R.id.paytemt_coal_view,R.id.payment_freight_view, R.id.common_problem, R.id.receivables_tel,R.id.complaint_service_tel, R.id.complaint_tel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.paytemt_coal_view:
                if (payMentOrder.getPayState().equals("5")){
                    displayToast("您已退款，不能查看详细信息！");
                }else{
                    if ("0".equals(payMentOrder.getEmpowerAccessState())){
                        displayToast("当前授权已失效！");
                        return;
                    }else {
                        if ("已失效".equals(payMentOrder.getLicenseMinute())){
                            //未支付 弹框提示
                            showPayDialog(coal, "0");
                        }else{
                            String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                            if (userId.equals("-1")) {
                                //未登录 先去登录
                                UIHelper.jumpActLogin(mContext,false);
                            } else {
                                Intent intent = new Intent();
                                intent.setClass(this, DetailsWebActivity.class);
                                intent.putExtra("Coal", coal);
                                intent.putExtra("inforDepartId", "煤炭详情");
                                startActivity(intent);
                            }
                        }
                    }
                }
                break;
            case R.id.payment_freight_view:
                if (payMentOrder.getPayState().equals("5")){
                    displayToast("您已退款，不能查看详细信息！");
                }else {
                    if ("0".equals(payMentOrder.getEmpowerAccessState())) {
                        displayToast("当前授权已经失效！");
                    } else {
                        Intent intent1 = new Intent(this, TransportDetailActivity.class);
                        intent1.putExtra("waybillId", transportMode.getTransportId());//货运id
                        startActivity(intent1);
                    }
                }
                break;
            case R.id.common_problem:
                String URL = new Config().getCOUPONS_NOTICE_FOR_USE();
                UIHelper.showWEB(this,URL,"使用须知");
                break;
            case R.id.receivables_tel:
                String phoneNum = "";
                if (payMentOrder.getConsultingFeeType().equals("0")){
                    phoneNum = coal.getPublishUserPhone();
                }else{
                    phoneNum = transportMode.getPublishUserPhone();
                }
                Map<String,String> map = new HashMap<String, String>();
                map.put("tel",phoneNum);
                UIHelper.showCollTel(this, map,false);
                break;
            case R.id.complaint_service_tel: //联系客服
                String phoneNum1 = getResources().getString(R.string.service_hot_line);
                Map<String,String> map1 = new HashMap<String, String>();
                map1.put("tel",phoneNum1);
                map1.put("callType", Constant.CALE_TYPE_COMPLAINT);
                map1.put("targetId", payMentOrder.getTradeNo());
                UIHelper.showCollTel(this, map1,true);
                break;
            case R.id.complaint_tel: //投诉
                if ("0".equals(payMentOrder.getIsCanComplaint()) && "0".equals(payMentOrder.getIsHaveComplaint())){
                    showDaiLog("您当前订单已经过了投诉期限，请尝试其他方式");
                }else{
                    UIHelper.jumpAct(this,ComplaintActivity.class,payMentOrder.getTradeNo(),false);
                }
                break;
        }
    }

    private void showDaiLog(String message) {
        new RLAlertDialog(this, "系统提示", message, "知道了",
                "", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {

            }
        }).show();
    }
}
