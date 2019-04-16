package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sxhalo.PullCoal.utils.Utils;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.PullToRefreshScrollView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.TimeLineAdapter;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.CoalOrderStatusEntity;
import com.sxhalo.PullCoal.model.EscrowAccount;
import com.sxhalo.PullCoal.model.Orders;
import com.sxhalo.PullCoal.model.SaleManager;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullHeadViewLayout;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.UPDATE_COAL_ORDER_DETAIL;

/**
 * 在线买煤订单详情
 * Created by liz on 2018/5/9.
 */

public class CoalOrderDetailActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView tvTitle;//标题
    @Bind(R.id.map_type)
    TextView tvDial;//电话联系
    @Bind(R.id.layout_one_button)
    LinearLayout layoutOneButton;//显示一个按钮的布局
    @Bind(R.id.tv_one_text)
    TextView tvOneText;//一个按钮的文本显示(根据状态不同，点击后请求的接口不同)
    @Bind(R.id.tv_order_status)
    TextView tvOrderStatus;//订单状态提示信息

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;//横向时间轴列表
    @Bind(R.id.layout_with_two_status)
    LinearLayout layoutWithTwoStatus;//当订单状态为2 3 6 100时 显示的时间轴
    @Bind(R.id.tv_status_end)
    TextView tvStatusEnd;//当订单状态为2 3 6 100时 后一个状态显示的控件

    @Bind(R.id.layout_freight_info)
    LinearLayout layoutFreightInfo;//货运信息父控件
    @Bind(R.id.customeListView)
    CustomListView customListView;//货运信息列表控件
    @Bind(R.id.layout_no_car)
    RelativeLayout layoutNoCar;//无货运信息视图
    @Bind(R.id.send_number_car_view)
    LinearLayout sendNumberView;//共需派车数布局
    @Bind(R.id.send_number_car)
    TextView sendNumberCar;// 共需派车数
    @Bind(R.id.btn_send_more_car)
    Button btnSendMoreCar;//继续派车按钮

    @Bind(R.id.tv_information_department)
    TextView tvInformationDepartment;//信息部名称
    @Bind(R.id.coal_image)
    ImageView coalImage;//煤炭图片
    @Bind(R.id.tv_coal_name)
    TextView tvCoalName;//煤炭名称
    @Bind(R.id.tv_calorific_value)
    TextView tvCaloroficValue;//发热量
    @Bind(R.id.tv_mouth_value)
    TextView tvmouthValue;//矿口名称

    @Bind(R.id.tv_order_number)
    TextView tvOrderNumber;//预订单号
    @Bind(R.id.tv_number)
    TextView tvNumber;//预订量
    @Bind(R.id.tv_reservation_price)
    TextView tvReservationPrice;//预定价格
    @Bind(R.id.tv_reservation_amount)
    TextView tvReservationAmount;//预定金额
    @Bind(R.id.real_time_price)
    RelativeLayout realTimePrice;//实时价格布局
    @Bind(R.id.tv_real_time_price)
    TextView tvRealTimePrice;//实时价格
    @Bind(R.id.price_fluctuation)
    RelativeLayout priceFluctuation;//价格浮动布局
    @Bind(R.id.tv_price_fluctuation_title)
    TextView tvPriceFluctuationTitle;//价格浮动名称
    @Bind(R.id.tv_price_fluctuation)
    TextView tvPriceFluctuation;//价格浮动
    @Bind(R.id.tv_order_time)
    TextView tvOrderTime;//预定时间
    @Bind(R.id.tv_receive_name)
    TextView tvReceiveName;//收货人姓名
    @Bind(R.id.rl_remarks_view)
    RelativeLayout rlRemarksView;//备注布局
    @Bind(R.id.tv_remarks)
    TextView tvRemarks;//备注显示控件

    @Bind(R.id.tv_tonnage_of_transport)
    TextView tvTonnageTransport;//已运达吨数
    @Bind(R.id.tv_coal_payment)
    TextView tvCoalPayment;//实付煤款总额
    @Bind(R.id.lous_orders_type)
    TextView lousOrdersType;//白条标识

    @Bind(R.id.refresh_scroll_view)
    PullToRefreshScrollView rsv;

    private TimeLineAdapter timeLineAdapter;//水平方向时间轴adapter
    private QuickAdapter<SendCarEntity> quickAdapter;
    private List<CoalOrderStatusEntity> listStatus = new ArrayList<>();//订单正常流程状态的数据
    //    private String transportType;//00 买家派车自提   01 信息部代发
    private String orderNumber;//订单编号
    private String orderStatus;//订单状态 0、提交订单；1、已受理磋商中；2、拒绝订单；3、受理超期；4、磋商完成待确认成交； 5、已确认成交，开始派车提货；6、超期未确认；7、定单完成；100、取消订单；
    private Orders orders;//数据返回实体

    private boolean ifRefresh = false; // 跳转返回是否刷新

    private boolean isComplete = false;//订单是否完成


    private boolean updataOrder = false;//是否需要验证当前订单状态

    private String currentUserPhone;//当前登录用户电话号码

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_coal_order_detail);
    }

    @Override
    protected void initTitle() {
        tvTitle.setText("订单详情");
        orderNumber = getIntent().getStringExtra("orderNumber");
        currentUserPhone = SharedTools.getStringValue(this, "user_mobile", "-1");
        intRefreshScrollView();
    }

    //进行初使化  （1.设置模式 2.设置自定义header与footer的刷新布局 3.设置监听事件）
    private void intRefreshScrollView() {
        //设置刷新模式 ，both代表支持上拉和下拉，pull_from_end代表上拉，pull_from_start代表下拉
        rsv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        rsv.setHeaderLayout(new PullHeadViewLayout(this));

        //3.设置监听事件
        rsv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getData();//请求网络数据，并更新listview组件
                refreshComplete();//数据加载完成后，关闭header,footer
            }
        });
    }

    /**
     * 刷新完成时关闭
     */
    public void refreshComplete(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rsv.onRefreshComplete();
            }
        },1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ifRefresh){
            getData();
        }
    }

    /**
     * 根据订单状态 初始化订单流程时间轴数据
     */
    private void initOrderStatusData() {
        listStatus.clear();
        CoalOrderStatusEntity coalOrderStatusEntity0 = new CoalOrderStatusEntity();
        coalOrderStatusEntity0.setMemo("订单提交");
        CoalOrderStatusEntity coalOrderStatusEntity1 = new CoalOrderStatusEntity();
        coalOrderStatusEntity1.setMemo("订单受理");
        CoalOrderStatusEntity coalOrderStatusEntity5 = new CoalOrderStatusEntity();
        coalOrderStatusEntity5.setMemo("派车提货");
        CoalOrderStatusEntity coalOrderStatusEntity7 = new CoalOrderStatusEntity();
        coalOrderStatusEntity7.setMemo("订单完成");
        switch (Integer.valueOf(orderStatus)) {
            case 0:
                //提交订单
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(false);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(false);

                coalOrderStatusEntity5.setCurrentStatus(false);
                coalOrderStatusEntity5.setStartLineIsBlue(false);
                coalOrderStatusEntity5.setEndLineIsBlue(false);

                coalOrderStatusEntity7.setCurrentStatus(false);
                coalOrderStatusEntity7.setStartLineIsBlue(false);
                coalOrderStatusEntity7.setEndLineIsBlue(false);
                break;
            case 1:
                //受理
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(true);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(true);

                coalOrderStatusEntity5.setCurrentStatus(false);
                coalOrderStatusEntity5.setStartLineIsBlue(true);
                coalOrderStatusEntity5.setEndLineIsBlue(false);

                coalOrderStatusEntity7.setCurrentStatus(false);
                coalOrderStatusEntity7.setStartLineIsBlue(false);
                coalOrderStatusEntity7.setEndLineIsBlue(false);
                break;
            case 5:
                //派车提货
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(true);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(true);


                coalOrderStatusEntity5.setStartLineIsBlue(true);
                coalOrderStatusEntity5.setCurrentStatus(true);
                coalOrderStatusEntity5.setEndLineIsBlue(true);

                coalOrderStatusEntity7.setStartLineIsBlue(true);
                coalOrderStatusEntity7.setCurrentStatus(false);
                break;
            case 7:
                //提交订单
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setStartLineIsBlue(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(true);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(true);

                coalOrderStatusEntity5.setCurrentStatus(true);
                coalOrderStatusEntity5.setStartLineIsBlue(true);
                coalOrderStatusEntity5.setEndLineIsBlue(true);

                coalOrderStatusEntity7.setStartLineIsBlue(true);
                //当点击“确认完成”按钮后，显示为蓝色，否则显示灰色
                if (isComplete) {
                    coalOrderStatusEntity7.setCurrentStatus(true);
                } else {
                    coalOrderStatusEntity7.setCurrentStatus(false);
                }
                break;
        }
        listStatus.add(coalOrderStatusEntity0);
        listStatus.add(coalOrderStatusEntity1);
        listStatus.add(coalOrderStatusEntity5);
        listStatus.add(coalOrderStatusEntity7);
    }

    private void setData() {
        //信息部名称
        tvInformationDepartment.setText(orders.getCoalSalesName());

        if (Utils.isImagesTrue(orders.getImageUrl()) == 200 ){
            getImageManager().loadCoalTypeUrlImage(orders.getImageUrl(),orders.getCategoryImage(),coalImage);
        }else{
            getImageManager().loadCoalTypeUrlImage(orders.getCategoryImage(),coalImage);
        }

        //煤炭名称
        tvCoalName.setText(orders.getCoalName());
        //发热量
        tvCaloroficValue.setText(StringUtils.isEmpty(orders.getCalorificValue()) ? "未提供" : orders.getCalorificValue() + "kCal/kg");
        tvmouthValue.setText(orders.getMineMouthName());

        // 是否白条订单：0否、1是
        if ("1".equals(orders.getIouUse())){
            lousOrdersType.setVisibility(View.VISIBLE);
        }else {
            lousOrdersType.setVisibility(View.INVISIBLE);
        }

        //预订单号
        tvOrderNumber.setText(orders.getOrderNumber());
        //预订量
        tvNumber.setText(orders.getTradingVolume() + "吨");
        //预订价格
        tvReservationPrice.setText(orders.getOneQuote() + "元/吨");
        //预定金额
        tvReservationAmount.setText(StringUtils.fmtMicrometer((Integer.valueOf(orders.getTradingVolume()) * Integer.valueOf(orders.getOneQuote())) + "") + "元");  //预订吨数*预订价格

        if ("2".equals(orderStatus) || "3".equals(orderStatus) || "6".equals(orderStatus) || "7".equals(orderStatus) || "100".equals(orderStatus)){
            //实时价格布局
            realTimePrice.setVisibility(View.GONE);
        }else{
            //实时价格布局
            realTimePrice.setVisibility(View.VISIBLE);
            //实时价格
            tvRealTimePrice.setText(orders.getRealTimePrice() + "元/吨");
        }

        if ("2".equals(orderStatus)){
            priceFluctuation.setVisibility(View.GONE);
        }else {
            priceFluctuation.setVisibility(View.VISIBLE);
            if (orders.getQuoteAdjust().contains("-")){
                //价格浮动名称
                tvPriceFluctuationTitle.setText("订单优惠");
                //价格浮动
                tvPriceFluctuation.setText(orders.getQuoteAdjust().replace("-","")+ " 元/吨");
                tvPriceFluctuation.setTextColor(getResources().getColor(R.color.label_two_background));
            }else{
                if ("0".equals(orders.getQuoteAdjust())){
                    priceFluctuation.setVisibility(View.GONE);
                }else{
                    //价格浮动名称
                    tvPriceFluctuationTitle.setText("订单加价");
                    //价格浮动
                    tvPriceFluctuation.setText(orders.getQuoteAdjust()+ " 元/吨");
                    tvPriceFluctuation.setTextColor(getResources().getColor(R.color.actionsheet_red));
                }
            }
        }

        //收货人姓名
        tvReceiveName.setText(orders.getContactPerson() + "    " + orders.getContactPhone());
        //预定时间
        tvOrderTime.setText(orders.getCreateTime());

        //备注
        if (!StringUtils.isEmpty(orders.getRemark())){
            rlRemarksView.setVisibility(View.VISIBLE);
            tvRemarks.setText(orders.getRemark());
        }else{
            rlRemarksView.setVisibility(View.GONE);
        }

        if ("0".equals(orders.getAlreadyVolume())|| "0.0".equals(orders.getAlreadyVolume()) || "0.00".equals(orders.getAlreadyVolume())){
            //已运达吨数
            tvTonnageTransport.setVisibility(View.GONE);
        }else{
            tvTonnageTransport.setVisibility(View.VISIBLE);
            //已运达吨数
            tvTonnageTransport.setText("已起运总吨数：" + orders.getAlreadyVolume() + " 吨");
        }

        if ("0".equals(orders.getAlreadyAmount()) || "0.0".equals(orders.getAlreadyAmount()) || "0.00".equals(orders.getAlreadyAmount())){
            //实付煤款总额
            tvCoalPayment.setVisibility(View.GONE);
        }else{
            tvCoalPayment.setVisibility(View.VISIBLE);
            //实付煤款总额
            tvCoalPayment.setText("实付煤款总额：" + StringUtils.fmtMicrometer(orders.getAlreadyAmount())+ " 元");

        }
    }

    /**
     * 初始化货运信息
     */
    private void initFreightInformationView() {
        //最多显示两条信息
        List<SendCarEntity> list = new ArrayList<>();
        if (orders.getOrderFreightList().size() > 2) {
            list.add(orders.getOrderFreightList().get(0));
            list.add(orders.getOrderFreightList().get(1));
        } else {
            list = orders.getOrderFreightList();
        }

        if (((orderStatus.equals("1") || orderStatus.equals("5") || orderStatus.equals("7")))|| list.size() > 0) {
            layoutFreightInfo.setVisibility(View.VISIBLE);
            if (!orderStatus.equals("7")){
                sendNumberView.setVisibility(View.VISIBLE);
                int carNumber =  setSendCarNumber();
                sendNumberCar.setText(" "+ carNumber + " ");
                if (carNumber > 0 && list.size() > 0){
                    btnSendMoreCar.setVisibility(View.VISIBLE);
                }else{
                    btnSendMoreCar.setVisibility(View.GONE);
                }
            }else{
                sendNumberView.setVisibility(View.GONE);
                btnSendMoreCar.setVisibility(View.GONE);
            }
            if (orderStatus.equals("1") && list.size() == 0) {
                layoutNoCar.setVisibility(View.VISIBLE);
                customListView.setVisibility(View.GONE);
            } else {
                initSendCarList(list);
            }
            if (orderStatus.equals("100")){
                btnSendMoreCar.setVisibility(View.GONE);
            }
        } else {
            layoutFreightInfo.setVisibility(View.GONE);
        }
    }

    /**
     *  初始化订单详情中的派车单列表数据
     * @param list
     */
    private void initSendCarList(List<SendCarEntity> list) {
        layoutNoCar.setVisibility(View.GONE);
        customListView.setVisibility(View.VISIBLE);
        quickAdapter = new QuickAdapter<SendCarEntity>(this, R.layout.send_car_order_item, list) {
            @Override
            protected void convert(BaseAdapterHelper helper, final SendCarEntity sendCarEntity, int position) {
                helper.setText(R.id.tv_car_plate, sendCarEntity.getPlateNumber());
                Button btnSendSMS = (Button) helper.getView().findViewById(R.id.btn_send_sms);
                //0、待处理(派车中)；1、装车中；2、已起运；3、已完成；100、取消
                if ("0".equals(sendCarEntity.getOrderState())) {
                    helper.setText(R.id.tv_status, "待处理");
                } else if ("1".equals(sendCarEntity.getOrderState())) {
                    helper.setText(R.id.tv_status, "装车中");
                } else if ("2".equals(sendCarEntity.getOrderState())) {
                    helper.setText(R.id.tv_status, "已装车");
                } else if ("3".equals(sendCarEntity.getOrderState())) {
                    helper.setText(R.id.tv_status, "起运");
                } else if ("100".equals(sendCarEntity.getOrderState())) {
                    helper.setText(R.id.tv_status, "已取消");
                }

                if ("3".equals(sendCarEntity.getOrderState())){
                    helper.getView().findViewById(R.id.amount_of_payment_view).setVisibility(View.VISIBLE);
                    helper.setText(R.id.cend_car_settlement_price, sendCarEntity.getPrice());
                    helper.setText(R.id.cend_car_suttle, sendCarEntity.getCarryWeight());
                    helper.setText(R.id.amount_of_payment_number, StringUtils.fmtMicrometer(sendCarEntity.getPaymentAmount()));
                }else{
                    helper.getView().findViewById(R.id.amount_of_payment_view).setVisibility(View.GONE);
                }

                //发送短信按钮是否显示
                if (!currentUserPhone.equals(sendCarEntity.getDriverPhone()) && "0".equals(sendCarEntity.getOrderState())) {
                    //买家或者信息部身份进入
                    btnSendSMS.setVisibility(View.VISIBLE);
                    btnSendSMS.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPayDialog(sendCarEntity);
                        }
                    });
                } else {
                    btnSendSMS.setVisibility(View.GONE);
                }
                if ("0".equals(sendCarEntity.getOrderState())){
                    helper.getView().findViewById(R.id.tv_driver_status).setVisibility(View.GONE);
                }else{
                    //司机认证状态
                    if (sendCarEntity.getDriverAuthState().equals("2")) {
                        helper.getView().findViewById(R.id.tv_driver_status).setVisibility(View.VISIBLE);
                    } else {
                        helper.getView().findViewById(R.id.tv_driver_status).setVisibility(View.GONE);
                    }
                }

                helper.setText(R.id.tv_time, sendCarEntity.getCreateTime());

                //提货码显示与否判断
                if ("0".equals(sendCarEntity.getOrderState())){
                    //已送达
                    helper.getView().findViewById(R.id.code_view).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_code, "提货码：" + sendCarEntity.getCertificate());
                }else{
                    helper.getView().findViewById(R.id.code_view).setVisibility(View.GONE);
                }

                helper.setText(R.id.tv_driver_name, sendCarEntity.getDriverRealName());
                helper.setText(R.id.tv_phone, sendCarEntity.getDriverPhone());
            }
        };
        customListView.setAdapter(quickAdapter);
        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CoalOrderDetailActivity.this, SendCarDetailActivity.class);
                intent.putExtra("transportOrderCode", quickAdapter.getItem(position).getTransportOrderCode());
                startActivityForResult(intent, UPDATE_COAL_ORDER_DETAIL);
            }
        });
    }

    /**
     * 弹出框
     */
    public void showPayDialog(final SendCarEntity sendCarEntity) {
        new RLAlertDialog(this, "系统提示", "确认是否发送?", "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                //发送短信
                String content = String.format(getResources().getString(R.string.string_send_sms),
                        sendCarEntity.getContactPerson(),
                        sendCarEntity.getFromToPlace(),
                        sendCarEntity.getContactPhone(),
                        sendCarEntity.getCertificate());
                sendSMS(sendCarEntity.getDriverPhone(), content);
            }
        }).show();
    }

    /**
     * /初始化订单状态水平方向时间轴
     */
    private void initOrderStatusHorizontalView() {
        if (orderStatus.equals("2") || orderStatus.equals("3") || orderStatus.equals("6") || orderStatus.equals("100")) {
            //订单拒绝、超期、取消时 显示仅有两个状态的订单流程视图
            layoutWithTwoStatus.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            if (orderStatus.equals("2")) {
                tvStatusEnd.setText("已拒绝");
            } else if (orderStatus.equals("3") || orderStatus.equals("6")) {
                tvStatusEnd.setText("订单超期");
            } else {
                tvStatusEnd.setText("订单取消");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutWithTwoStatus.setVisibility(View.GONE);
            //订单正常流程
            timeLineAdapter = new TimeLineAdapter(this, listStatus);
        }
        LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(this);
        linearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManagerHorizontal);
        recyclerView.setAdapter(timeLineAdapter);
    }


    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("orderNumber", orderNumber);
            new DataUtils(this, params).getUserCoalOrderInfo(new DataUtils.DataBack<Orders>() {
                @Override
                public void getData(final Orders orders) {
                    try {
                        if (orders == null) {
                            return;
                        }
                        if (updataOrder){
                            if (!orderStatus.equals(orders.getOrderState())){
                                showUser(orders);
                            }else{
                                boolean canComplete = false;//当有派车单时，是否有进行中的派车单
                                boolean isConfirm = false;//当有派车单时，是否有已经完成的派车单
                                if (orders.getOrderFreightList().size() != 0){
                                    //有货运信息 并且所有派车单状态中只要有进行中(装车中、已起运)的就不能取消
                                    for (int i = 0; i < orders.getOrderFreightList().size(); i++) {
                                        if (orders.getOrderFreightList().get(i).getOrderState().equals("1") || orders.getOrderFreightList().get(i).getOrderState().equals("2")) {
                                            //派车单状态为装车中或已起运状态，视为进行中
                                            canComplete = true;
                                            break;
                                        }
                                    }
                                    if (canComplete && "可取消订单".equals(tvOneText.getText().toString())){
                                        showUser(orders);
                                        return;
                                    }
                                }
                                if ("可取消订单".equals(tvOneText.getText().toString())){
                                    upDataOrder(); // 可取消
                                }else if ("提前完成".equals(tvOneText.getText().toString())){ //当派车单中有至少一单处于完成状态，可点订单完成
                                    //当有进行中的派车单时，只能联系客服
                                    if ( !canComplete){
                                        //当有派车单时，是否有已经完成的派车单
                                        for (int i = 0; i < orders.getOrderFreightList().size(); i++) {
                                            if (orders.getOrderFreightList().get(i).getOrderState().equals("3")) {
                                                isConfirm = true;
                                                break;
                                            }
                                        }
                                        // 派车单中没有进行中的派车单，并且有已完成的派车单，可点完成
                                        if (isConfirm){
                                            upDataOrder(); // 可提前完成
                                        }else{
                                            showUser(orders);
                                        }
                                    }else{
                                        showUser(orders);
                                    }
                                }
                            }
                            updataOrder = false;
                        }else{
                            setOrderView(orders);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    private void showUser(final Orders orders) {
        rsv.getRefreshableView().fullScroll(View.FOCUS_UP);
        setOrderView(orders);
        displayToast("当前订单已发生变化，界面已自动刷新！");
//        new RLAlertDialog(mContext, "温馨提示", "当前订单状态已经更新，是否刷新界面?", "关闭",
//                "刷新", new RLAlertDialog.Listener() {
//            @Override
//            public void onLeftClick() {
//                finish();
//            }
//
//            @Override
//            public void onRightClick() {
//                setOrderView(orders);
//            }
//        }).show();
    }

    /**
     * 界面刷新
     * @param orders
     */
    private void setOrderView(Orders orders){
        CoalOrderDetailActivity.this.orders = orders;
        orderStatus = orders.getOrderState();
        //初始化订单提示信息及受理时间
        initTopView();
        //根据订单状态 初始化订单流程时间轴数据
        initOrderStatusData();
        //初始化订单状态水平方向时间轴
        initOrderStatusHorizontalView();
        //初始化货运信息
        initFreightInformationView();
        //设置信息部及订单信息数据
        setData();

        //获取煤款账户信息
        getUserEscrowAccount();
    }

    /**
     * 获取当前用户煤款数
     */
    private void getUserEscrowAccount() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this, params).getUserEscrowAccount(new DataUtils.DataBack<EscrowAccount>() {
                @Override
                public void getData(EscrowAccount appData) {
                    try {
                        if (appData == null) {
                            return;
                        }
                        //可退款煤款
                        String coalBalance = (StringUtils.setNumLenth(Float.valueOf(appData.getUncashAmount())/100, 2));
                        // 是否白条订单：0否、1是
                        if ("1".equals(orders.getIouUse())){
                            boolean flage = false;
                            //货运状态：0、待处理(派车中)；1、装车中；2、已起运；3、已完成；100、取消
                            for (SendCarEntity sendCarEntity:orders.getOrderFreightList()){
                                if ("3".equals(sendCarEntity.getOrderState())){
                                    flage = true;
                                    break;
                                }
                            }
                            // 当是白条订单时，可退款煤款为负并且订单状态为派车提货时提示用户去充值
                            if (coalBalance.contains("-") && "5".equals(orderStatus) && !flage){
                                showUserMessage("您已经派车提货，请及时充值！");
                            }
                        }else {
                            // 当是普通订单时，煤款为负时，提示用户煤款不足去充值(未受理，取消，完成)
                            if (coalBalance.contains("-") && !"7".equals(orderStatus) && !"2".equals(orderStatus)&& !"100".equals(orderStatus)){
                                showUserMessage("您的煤款已经不足，请您及时补充煤款！");
                            }
                        }
                    } catch (Exception e) {
                        GHLog.e("煤款托管账户", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
//                    if (!"".equals(e.getMessage())){
//                        displayToast(e.getCause().getMessage());
//                    }
                    GHLog.e("煤款托管账户", e.toString());
                }
            },false);
        } catch (Exception e) {
            GHLog.e("联网获取当前用户状态", e.toString());
        }
    }

    private void showUserMessage(String message){
        new RLAlertDialog(mContext, "温馨提示", message, "知道了",
                "去充值", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tel", getResources().getString(R.string.service_hot_line));
                map.put("callType", Constant.CALE_TYPE_COAL_RECHARGE);
                map.put("targetId", orders.getOrderNumber());
                UIHelper.showCollTel(mContext, map, true);
            }
        }).show();
    }

    /**
     * 订单流程状态处理  orderStatus
     *  0 订单已提交，等待信息部反馈中
     *  1 订单已受理，可以继续磋商具体的煤炭指标信息和成交价格
     *  2 信息部未受理 ，拒绝接受订单，订单未成交
     *  //3 信息部卖家超期未接受订单磋商，订单未成交
     *  //4 磋商已完成，等待买家确认成交
     *  5 派车提货，已确认成交
     *  //6 信息部卖家已提交的磋商结果信息，买家超期未确认，订单未成交
     *  7 派车提货结束，定单已完成！
     *  100 订单已取消！
     */
    private void initTopView() {
        if ("0".equals(orderStatus)) {
            tvOrderStatus.setText("订单已提交，等待信息部反馈中！");
            layoutOneButton.setVisibility(View.VISIBLE);
            tvOneText.setText("可取消订单");
            findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
        } else if ("1".equals(orderStatus)) {
            tvOrderStatus.setText("信息部已受理！");
            layoutOneButton.setVisibility(View.VISIBLE);
            tvOneText.setText("可取消订单");
            findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
        } else if ("2".equals(orderStatus)) {
            tvOrderStatus.setText("信息部已拒绝！");
            layoutOneButton.setVisibility(View.VISIBLE);
            tvOneText.setVisibility(View.GONE);
            findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
        } else if ("5".equals(orderStatus)) {
            tvOrderStatus.setText("信息部已受理，派车提货中！");
            layoutOneButton.setVisibility(View.VISIBLE);
            if (orders.getOrderFreightList().size() > 0) {
                boolean canComplete = false;//当有派车单时，是否有进行中的派车单
                boolean isConfirm = false;//当有派车单时，是否有进行中的派车单
                //有货运信息 并且所有派车单状态中只要有进行中(装车中、已起运)的就不能取消
                for (int i = 0; i < orders.getOrderFreightList().size(); i++) {
                    if (orders.getOrderFreightList().get(i).getOrderState().equals("1") || orders.getOrderFreightList().get(i).getOrderState().equals("2")) {
                        //派车单状态为装车中或已起运状态，视为进行中
                        canComplete = true;
                        break;
                    }
                }
                //当有进行中的派车单时，只能联系客服
                if (canComplete){
                    tvOneText.setText("联系客服");
                    findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
                } else {
                    //当有派车单时，是否有进行中的派车单
                    for (int i = 0; i < orders.getOrderFreightList().size(); i++) {
                        if (orders.getOrderFreightList().get(i).getOrderState().equals("3")) {
                            isConfirm = true;
                            break;
                        }
                    }
                    // 派车单中没有进行中的派车单，并且有已完成的派车单，可点完成
                    if (isConfirm){
                        tvOneText.setText("提前完成");
                        findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
                        //已起运吨数
                        float alreadyVolume = Float.valueOf(orders.getAlreadyVolume());
                        //预订吨数
                        float tradingVolume = Float.valueOf(orders.getTradingVolume());
                        if (alreadyVolume >= tradingVolume){
                            tvOneText.setVisibility(View.GONE);
                        }
                    }else {
                        tvOneText.setText("联系客服");
                        findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
                    }
                }
                //当派车单中没有已完成和进行中的派车单时，订单可操作取消
                if (canComplete == false && isConfirm == false){
                    tvOneText.setText("可取消订单");
                    findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
                }
            } else {
                tvOneText.setText("可取消订单");
                findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
            }
        } else if ("7".equals(orderStatus)) {
            tvOrderStatus.setText("订单已完成！");
            layoutOneButton.setVisibility(View.VISIBLE);
            tvOneText.setVisibility(View.GONE);
            isComplete = true;
            findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
        } else if ("100".equals(orderStatus)) {
            tvOrderStatus.setText("订单已取消！");
            layoutOneButton.setVisibility(View.VISIBLE);
            tvOneText.setVisibility(View.GONE);
            findViewById(R.id.tv_one_coles).setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.title_bar_left,R.id.tv_one_coles, R.id.tv_one_text,R.id.tv_see_more, R.id.btn_send_more_car, R.id.btn_send_car,R.id.coal_infor_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_one_coles:
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.tv_one_text:
                if ("联系客服".equals(tvOneText.getText().toString())){ //当派车单中没有进行中的派车单和已完成的订单时
                    //订单异常。联系客服
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tel", getResources().getString(R.string.service_hot_line));
                    map.put("callType", Constant.CALE_TYPE_COAL_ORDER);
                    map.put("targetId", orders.getOrderNumber());
                    UIHelper.showCollTel(mContext, map, true);
                }else{
                    updataOrder = true;
                    getData();
                }
                break;
            case R.id.tv_see_more:
                //查看更多
                Intent intent1 = new Intent(this, SendCarListActivity.class);
                intent1.putExtra("orderNumber", orderNumber);
                startActivityForResult(intent1, UPDATE_COAL_ORDER_DETAIL);
                break;
            case R.id.coal_infor_view:  //跳转煤炭详情
                Intent intent2 = new Intent(this, DetailsWebActivity.class);
                Coal coal = new Coal();
                coal.setGoodsId(orders.getGoodsId());
                coal.setReleaseNum(orders.getReleaseNum());
                intent2.putExtra("Coal", coal);
                intent2.putExtra("orderCoal", "-1");
                startActivity(intent2);
                break;
            case R.id.btn_send_car:
            case R.id.btn_send_more_car:
                int carNumber =  setSendCarNumber();
                if (carNumber <= 0){
                    displayToast("您派车数量超出预算（30吨/车）车辆数，不可派车！\n");
                    return;
                }
                //去派车
                Intent intent = new Intent(this, SendCarActivity.class);
                intent.putExtra("orderNumber", orderNumber);
                intent.putExtra("number", carNumber);
                startActivityForResult(intent, UPDATE_COAL_ORDER_DETAIL);
                break;
        }
    }

    private void upDataOrder(){
        // 当订单在待受理（0），受理未派车（1），受理派车，派车单中没有进行中和完成时 订单可取消
        if ("可取消订单".equals(tvOneText.getText().toString())){
            new RLAlertDialog(mContext, "系统提示", "是否确认取消订单?", "取消",
                    "确定", new RLAlertDialog.Listener() {
                @Override
                public void onLeftClick() {
                }

                @Override
                public void onRightClick() {
                    //取消订单
                    handleOrder("100");
                }
            }).show();
        }else if ("提前完成".equals(tvOneText.getText().toString())){ //当派车单中有至少一单处于完成状态，可点订单完成
            new RLAlertDialog(mContext, "系统提示", "当前订单还未拉完，是否提前完成？", "取消",
                    "确定", new RLAlertDialog.Listener() {
                @Override
                public void onLeftClick() {
                }

                @Override
                public void onRightClick() {
                    //定单完成
                    handleOrder("7");
                }
            }).show();
        }
    }

    /**
     *  获取动态派车数
     * @return
     */
    private int setSendCarNumber(){
        float coalNumber = Float.valueOf(orders.getTradingVolume())  - Float.valueOf(orders.getAlreadyVolume());
        if (coalNumber <= 0){
            return 0;
        }

        //当前还需派车数
        int carNumber = 0;
        if (coalNumber % 30 > 0 ){
            carNumber = (int) (coalNumber / 30) + 1 ;
        }else{
            carNumber = (int)(coalNumber / 30);
        }
        int romNum = 0; // 已经取消的派车单
        int runningNum = 0; // 正在进行中的派车单
        int overNum = 0 ;  // 已完成的派车单
        if (orders != null && orders.getOrderFreightList() != null && orders.getOrderFreightList().size() != 0){
            for (SendCarEntity sendCarEntity : orders.getOrderFreightList()){
                //当前已取消的派车数
                if ("100".equals(sendCarEntity.getOrderState())){
                    romNum ++ ;
                }
                //当前已完成的派车数
                if ("3".equals(sendCarEntity.getOrderState())){
                    overNum ++ ;
                }
            }
            //正在进行中的派车数
            runningNum = orders.getOrderFreightList().size() - overNum - romNum;
        }
        // 是否白条订单：0否、1是
        if ("1".equals(orders.getIouUse())){
            //有进行中的车 或 有已完成的车 0
            if (overNum != 0 || runningNum != 0){
                carNumber = 0 ;
            }else{  //当没有派车时  1    //有派车，并且没有进行中没有已完成 1
                carNumber = 1 ;
            }
        }else{
            //当前有效派车数
            carNumber = carNumber - runningNum ;
            if (carNumber <= 0){
                carNumber = 0;
            }
        }
        return carNumber;
    }

    private void handleOrder(final String handleStatus) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("orderNumber", orderNumber);
            params.put("orderState", handleStatus);
            new DataUtils(this, params).handleCoalOrder(new DataUtils.DataBack<Orders>() {
                @Override
                public void getData(Orders orders) {
                    try {
                        if (orders == null) {
                            return;
                        }
                        setOrderView(orders);
                        if ("5".equals(handleStatus)) {
                            displayToast("确认提交成功！");
                        } else if ("7".equals(handleStatus)) {
                            displayToast("定单已提前完成！");
                        } else {
                            displayToast("取消成功！");
                        }
                        sendBroadcast();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2060033".equals(e.getMessage())){
                        displayToast("煤款账户余额不足，请充值后确认成交！");
                    }else{
                        displayToast("网络连接失败，请稍后再试！");
                    }
                }
            });

        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
        switch (requestCode) {
            case UPDATE_COAL_ORDER_DETAIL:
                getData();
                break;
        }
//        }
    }

    private void sendSMS(String phoneNumber, String content) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber", phoneNumber);  //手机号
            params.put("sendContent", content); //短信内容
            new DataUtils(this, params).getSendSMS(new DataUtils.DataBack<SaleManager>() {
                @Override
                public void getData(SaleManager dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        displayToast("发送成功！");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    /**
     * 操作成功，发送广播
     */
    private void sendBroadcast() {
        Intent intent = new Intent(Constant.UPDATE_COAL_ORDER_FRAGMENT + "");
        sendBroadcast(intent);
    }
}
