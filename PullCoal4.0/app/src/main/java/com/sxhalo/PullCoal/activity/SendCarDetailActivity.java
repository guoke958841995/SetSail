package com.sxhalo.PullCoal.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.PullToRefreshScrollView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.TimeLineAdapter;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.common.callback.CustomCallback;
import com.sxhalo.PullCoal.model.CoalOrderStatusEntity;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullHeadViewLayout;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.UDPATE_SEND_CAR_DETAIL;

/**
 * 派车单详情界面
 * Created by liz on 2018/5/16.
 */

public class SendCarDetailActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;//标题
    @Bind(R.id.coal_funds_view)
    RelativeLayout coalFundsView;//煤款不足布局
    @Bind(R.id.tv_driver_number)
    TextView tvDriverNumber; //车牌号
    @Bind(R.id.tv_driver_name)
    TextView tvDriverName;//司机姓名
    @Bind(R.id.tv_driver_status)
    TextView tvDriverStatus;//是否是平台认证司机标记
    @Bind(R.id.tv_driver_tel)
    TextView tvDriverTel;//司机电话
    @Bind(R.id.tv_code)
    TextView tvCode;//提货码
    @Bind(R.id.code_view)
    RelativeLayout codeView;//提货码布局
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;//水平方向时间轴
    @Bind(R.id.layout_pound)
    RelativeLayout layoutPound;//磅单视图父控件
    @Bind(R.id.upload_pund_view)
    LinearLayout uploadPundView;//重新上传磅单父控件
    @Bind(R.id.btn_upload1)
    TextView btn_upload1;//上传磅单按钮
    @Bind(R.id.divider_pound)
    View dividerPound;//磅单视图底部分割线
    @Bind(R.id.iv_pound)
    ImageView ivPound;//派车单
    @Bind(R.id.tv_pound)
    TextView tvPound;//点击放大布局
    @Bind(R.id.tv_real_time_price)
    TextView tvRealTimePrice;//实时价格
    @Bind(R.id.tv_real_time_price_title)
    TextView tvRealTimePriceTitle;//实时价格
    @Bind(R.id.tv_order_float_title)
    TextView tvOrderFloatTitle;//订单调价浮动标题
    @Bind(R.id.tv_order_float)
    TextView tvOrderFloat;//浮动值
    @Bind(R.id.tv_dispatching_car_floating_title)
    TextView tvDispatchingCarFloatingTitle;//派车单调价浮动标题
    @Bind(R.id.tv_dispatching_car_floating)
    TextView tvDispatchingCarFloating;//派车单浮动值
    @Bind(R.id.tv_settlement_price_title)
    TextView tvSettlementPriceTitle;//结算价格
    @Bind(R.id.tv_settlement_price)
    TextView tvSettlementPrice;//结算价格
    @Bind(R.id.tv_suttle_title)
    TextView tvSuttleTitle;//净重
    @Bind(R.id.tv_suttle)
    TextView tvSuttle;//净重
    @Bind(R.id.tv_payment_goods_title)
    TextView tvPaymentGoodsTitle;//实付煤款
    @Bind(R.id.tv_payment_goods)
    TextView tvPaymentGoods;//实付煤款
    @Bind(R.id.tv_saler_name)
    TextView tvSalerName;//卖家姓名
    @Bind(R.id.tv_saler_phone)
    TextView tvSalerPhone;//卖家电话
    @Bind(R.id.tv_saler_address)
    TextView tvSalerAddress;//卖家地址
    @Bind(R.id.tv_mine_mouth)
    TextView tvMineMouth;//矿口地址
    @Bind(R.id.tv_buyer_name)
    TextView tvBuyerName;//买家姓名
    @Bind(R.id.tv_buyer_phone)
    TextView tvBuyerPhone;//买家电话
    @Bind(R.id.tv_buyer_address)
    TextView tvBuyerAddress;//买家地址
    @Bind(R.id.layout_with_two_status)
    LinearLayout layoutWithTwoStatus;//取消状态时候显示的时间轴父控件

    @Bind(R.id.user_infor_view)
    LinearLayout userInforView;//派车单信息
    @Bind(R.id.tv_one_text)
    TextView tvOneText;//一个按钮的文本显示(根据状态不同，

    @Bind(R.id.refresh_scroll_view)
    PullToRefreshScrollView rsv;

    private String transportOrderCode;
    private SendCarEntity sendCarEntity;
    private InformationDepartment informationDepartment;
    private MineMouth mineMouth;
    private String orderStatus;
    private TimeLineAdapter timeLineAdapter;
    private List<CoalOrderStatusEntity> listStatus = new ArrayList<>();//订单正常流程状态的数据

    private boolean ifRefresh = false; // 跳转返回是否刷新
    private boolean upData = false; // 检测当前界面状态是否最新
    private CustomCallback coalBack;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_send_car_detail);
    }

    @Override
    protected void initTitle() {
        transportOrderCode = getIntent().getStringExtra("transportOrderCode");
        title.setText("派车单详情");
        intRefreshScrollView();
    }

    //进行初使化  （1.设置模式 2.自定义设置header与footer的刷新布局 3.设置监听事件）
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
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("transportOrderCode", transportOrderCode);
            new DataUtils(this, params).getMyCoalOrderTransportDetail(new DataUtils.DataBack<SendCarEntity>() {
                @Override
                public void getData(SendCarEntity sendCarEntity) {
                    try {
                        if (sendCarEntity == null) {
                            return;
                        }
                        if (upData){
                            if (!orderStatus.equals(sendCarEntity.getOrderState())){
                                showUser(sendCarEntity);
                            }else{
                                new RLAlertDialog(mContext, "系统提示", "是否确认取消派车单?", "取消",
                                        "确定", new RLAlertDialog.Listener() {
                                    @Override
                                    public void onLeftClick() {
                                    }

                                    @Override
                                    public void onRightClick() {
                                        //操作 取消
                                        handle2Service("4");
                                    }
                                }).show();
                            }
                            upData = false;
                        }else{
                            upData(sendCarEntity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接失败，请稍候再试！");
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    private void upData(SendCarEntity sendCarEntity){
        SendCarDetailActivity.this.sendCarEntity = sendCarEntity;
        informationDepartment = sendCarEntity.getInformationDepartment();
        mineMouth = sendCarEntity.getMineMouth();
        orderStatus = sendCarEntity.getOrderState();
        //根据货运单状态 初始化订单流程时间轴数据
        initOrderStatusData();
        //初始化货运单状态水平方向时间轴
        initOrderStatusHorizontalView();
        setData();

        if (coalBack != null){
            coalBack.onCallback();
        }
    }

    private void showUser(final SendCarEntity sendCarEntity) {
        rsv.getRefreshableView().fullScroll(View.FOCUS_UP);
        upData(sendCarEntity);
        displayToast("当前派车单已发生变化，界面已自动刷新！");
//        new RLAlertDialog(mContext, "温馨提示", "当前派车单状态已经更新，是否刷新界面?", "关闭",
//                "刷新", new RLAlertDialog.Listener() {
//            @Override
//            public void onLeftClick() {
//                dialogView.setVisibility(View.GONE);
//                finish();
//            }
//
//            @Override
//            public void onRightClick() {
//                dialogView.setVisibility(View.GONE);
//                upData(sendCarEntity);
//            }
//        }).show();
    }

    private void initView() {
        if ("0".equals(orderStatus)) {
            //派车中 只有在0状态时才可以取消
            tvOneText.setText("取消");
            tvOneText.setVisibility(View.VISIBLE);
            //不显示磅单相关信息
            layoutPound.setVisibility(View.GONE);
            dividerPound.setVisibility(View.GONE);
        } else if ("1".equals(orderStatus) || "2".equals(orderStatus) || "3".equals(orderStatus)) {
            tvOneText.setVisibility(View.GONE);

            // 显示上传磅视图 可进行的操作只有上传操作 订单不可取消
            dividerPound.setVisibility(View.VISIBLE);
            layoutPound.setVisibility(View.VISIBLE);

            //上传磅单
            if ("1".equals(orderStatus)){
                tvPound.setVisibility(View.GONE);
                ivPound.setImageResource(R.drawable.bg_no_car);
                btn_upload1.setText("上传磅单");
            }else{  //重新上传
                tvPound.setVisibility(View.VISIBLE);
                getImageManager().loadUrlImageCertificationView(sendCarEntity.getCarryWeightDocPicUrl(), ivPound);
                btn_upload1.setText("重新上传磅单");
            }

            if (!"3".equals(orderStatus)){
                uploadPundView.setVisibility(View.VISIBLE);
            }else{
                uploadPundView.setVisibility(View.GONE);
            }

            if ("2".equals(orderStatus)){
                //煤款总额
                Float  accountAmount = Float.valueOf(sendCarEntity.getAccountAmount());
                //不可支配煤款
                Float freezeAmount = Float.valueOf(sendCarEntity.getFreezeAmount());
                //实付煤款
                Float paymentAmount = Float.valueOf(sendCarEntity.getPaymentAmount());

                if (accountAmount - freezeAmount < paymentAmount){
                    coalFundsView.setVisibility(View.VISIBLE);
                }else{
                    coalFundsView.setVisibility(View.GONE);
                }
            }else{
                coalFundsView.setVisibility(View.GONE);
            }

            String userId = SharedTools.getStringValue(mContext,"userId","-1");
            if (userId.equals(sendCarEntity.getUserId())){
                tvRealTimePriceTitle.setVisibility(View.VISIBLE);
                //实时价格
                tvRealTimePrice.setText(sendCarEntity.getPriceLock() + "元/吨");

                tvOrderFloatTitle.setVisibility(View.VISIBLE);
                //订单价格浮动
                if (sendCarEntity.getQuoteAdjust().contains("-")) {
                    //价格浮动名称
                    tvOrderFloatTitle.setText("订单优惠：");
                    //价格浮动
                    tvOrderFloat.setText(sendCarEntity.getQuoteAdjust().replace("-", "") + "元/吨");
                    tvOrderFloat.setTextColor(getResources().getColor(R.color.label_two_background));
                } else {
                    if ("0".equals(sendCarEntity.getQuoteAdjust())) {
                        tvOrderFloatTitle.setVisibility(View.GONE);
                    } else {
                        //订单调价浮动标题
                        tvOrderFloatTitle.setText("订单加价：");
                        //浮动值
                        tvOrderFloat.setText(sendCarEntity.getQuoteAdjust() + "元/吨");
                        tvOrderFloat.setTextColor(getResources().getColor(R.color.actionsheet_red));
                    }
                }

                tvDispatchingCarFloatingTitle.setVisibility(View.VISIBLE);
                if (sendCarEntity.getPriceAdjust().contains("-")) {
                    //价格浮动名称
                    tvDispatchingCarFloatingTitle.setText("派车优惠：");
                    //价格浮动
                    tvDispatchingCarFloating.setText(sendCarEntity.getPriceAdjust().replace("-", "") + "元/吨");
                    tvDispatchingCarFloating.setTextColor(getResources().getColor(R.color.label_two_background));
                } else {
                    if ("0".equals(sendCarEntity.getPriceAdjust())) {
                        tvDispatchingCarFloatingTitle.setVisibility(View.GONE);
                    } else {
                        //派车单调价浮动标题
                        tvDispatchingCarFloatingTitle.setText("派车加价：");
                        //派车单浮动值
                        tvDispatchingCarFloating.setText(sendCarEntity.getPriceAdjust() + "元/吨");
                        tvDispatchingCarFloating.setTextColor(getResources().getColor(R.color.actionsheet_red));
                    }
                }
                tvSettlementPriceTitle.setVisibility(View.VISIBLE);
                //结算价格
                tvSettlementPrice.setText(sendCarEntity.getPrice() + "元/吨");

                if (!"1".equals(orderStatus)){
                    //实付煤款
                    tvPaymentGoods.setText(StringUtils.fmtMicrometer(sendCarEntity.getPaymentAmount()) + "元");
                    tvPaymentGoodsTitle.setVisibility(View.VISIBLE);
                    tvPaymentGoods.setVisibility(View.VISIBLE);
                }else{
                    tvPaymentGoodsTitle.setVisibility(View.GONE);
                    tvPaymentGoods.setVisibility(View.GONE);
                }
            }else{
                tvRealTimePriceTitle.setVisibility(View.GONE);
                tvOrderFloatTitle.setVisibility(View.GONE);
                tvDispatchingCarFloatingTitle.setVisibility(View.GONE);
                tvSettlementPriceTitle.setVisibility(View.GONE);
                tvPaymentGoodsTitle.setVisibility(View.GONE);
            }
            if (!"1".equals(orderStatus)){
                //净重
                tvSuttle.setText(sendCarEntity.getCarryWeight() + "吨");
                tvSuttleTitle.setVisibility(View.VISIBLE);
                tvSuttle.setVisibility(View.VISIBLE);
            }else{
                tvSuttleTitle.setVisibility(View.GONE);
                tvSuttle.setVisibility(View.GONE);
            }


        }
    }

    private void initOrderStatusHorizontalView() {
        if (orderStatus.equals("100")) {
            //取消状态
            layoutWithTwoStatus.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            userInforView.setVisibility(View.GONE);
            tvOneText.setVisibility(View.GONE);
        } else {
            userInforView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            layoutWithTwoStatus.setVisibility(View.GONE);
            timeLineAdapter = new TimeLineAdapter(this, listStatus);
            LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(this);
            linearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManagerHorizontal);
            recyclerView.setAdapter(timeLineAdapter);
            //根据用户身份设置显示内容
            initView();
        }
    }

    private void initOrderStatusData() {
        listStatus.clear();
        CoalOrderStatusEntity coalOrderStatusEntity0 = new CoalOrderStatusEntity();
        coalOrderStatusEntity0.setMemo("派车中");
        CoalOrderStatusEntity coalOrderStatusEntity1 = new CoalOrderStatusEntity();
        coalOrderStatusEntity1.setMemo("装车中");
        CoalOrderStatusEntity coalOrderStatusEntity2 = new CoalOrderStatusEntity();
        coalOrderStatusEntity2.setMemo("已装车");
        CoalOrderStatusEntity coalOrderStatusEntity3 = new CoalOrderStatusEntity();
        coalOrderStatusEntity3.setMemo("起运");
        switch (Integer.valueOf(orderStatus)) {
            case 0:
                //派车中
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(false);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(false);

                coalOrderStatusEntity2.setCurrentStatus(false);
                coalOrderStatusEntity2.setStartLineIsBlue(false);
                coalOrderStatusEntity2.setEndLineIsBlue(false);

                coalOrderStatusEntity3.setCurrentStatus(false);
                coalOrderStatusEntity3.setStartLineIsBlue(false);
                coalOrderStatusEntity3.setEndLineIsBlue(false);
                break;
            case 1:
                //装车中
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(true);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(true);

                coalOrderStatusEntity2.setCurrentStatus(false);
                coalOrderStatusEntity2.setStartLineIsBlue(true);
                coalOrderStatusEntity2.setEndLineIsBlue(false);

                coalOrderStatusEntity3.setCurrentStatus(false);
                coalOrderStatusEntity3.setStartLineIsBlue(false);
                coalOrderStatusEntity3.setEndLineIsBlue(false);
                break;
            case 2:
                //已装车
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(true);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(true);

                coalOrderStatusEntity2.setCurrentStatus(true);
                coalOrderStatusEntity2.setStartLineIsBlue(true);
                coalOrderStatusEntity2.setEndLineIsBlue(true);

                coalOrderStatusEntity3.setCurrentStatus(false);
                coalOrderStatusEntity3.setStartLineIsBlue(true);
                coalOrderStatusEntity3.setEndLineIsBlue(false);

                break;
            case 3:
                //起运
                coalOrderStatusEntity0.setCurrentStatus(true);
                coalOrderStatusEntity0.setEndLineIsBlue(true);

                coalOrderStatusEntity1.setCurrentStatus(true);
                coalOrderStatusEntity1.setStartLineIsBlue(true);
                coalOrderStatusEntity1.setEndLineIsBlue(true);

                coalOrderStatusEntity2.setCurrentStatus(true);
                coalOrderStatusEntity2.setStartLineIsBlue(true);
                coalOrderStatusEntity2.setEndLineIsBlue(true);

                coalOrderStatusEntity3.setCurrentStatus(true);
                coalOrderStatusEntity3.setStartLineIsBlue(true);
                break;
        }
        listStatus.add(coalOrderStatusEntity0);
        listStatus.add(coalOrderStatusEntity1);
        listStatus.add(coalOrderStatusEntity2);
        listStatus.add(coalOrderStatusEntity3);
    }


    /**
     * 設置數據
     */
    private void setData() {
        tvDriverNumber.setText(sendCarEntity.getPlateNumber());
        tvDriverName.setText(sendCarEntity.getDriverRealName());
        if (sendCarEntity.getDriverAuthState().equals("2")) {
            tvDriverStatus.setVisibility(View.VISIBLE);
        }

        tvDriverTel.setText(sendCarEntity.getDriverPhone());

        //提货码显示与否判断
        if ("0".equals(sendCarEntity.getOrderState())){
            codeView.setVisibility(View.VISIBLE);
            tvCode.setText("提货码：" + sendCarEntity.getCertificate());
        }else{
            codeView.setVisibility(View.GONE);
        }

        tvSalerName.setText(informationDepartment.getContactPerson());
        tvSalerPhone.setText(informationDepartment.getContactPhone());
        tvSalerAddress.setText(informationDepartment.getAddress());
        tvMineMouth.setText(mineMouth.getAddress());
        tvBuyerName.setText(sendCarEntity.getContactPerson());
        tvBuyerPhone.setText(sendCarEntity.getContactPhone());
        tvBuyerAddress.setText(sendCarEntity.getReceiptAddress());
    }

    @OnClick({R.id.title_bar_left,R.id.tv_driver_tel,R.id.tv_saler_phone,R.id.tv_buyer_phone,R.id.tv_one_coles ,R.id.tv_one_text, R.id.iv_pound, R.id.btn_upload1, R.id.layout_saler, R.id.layout_mine_mouth,R.id.coal_funds})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_one_coles:
            case R.id.title_bar_left:
                onBackPressed();
                break;
            case R.id.tv_driver_tel:  //司机信息 中电话拨打
                Map<String, String> map = new HashMap<String, String>();
                map.put("tel", sendCarEntity.getDriverPhone());
                map.put("callType", Constant.CALE_TYPE_COAL_SEND_CAR_ORDER);
                map.put("targetId", sendCarEntity.getTransportOrderCode());
                UIHelper.showCollTel(mContext, map, true);
                break;
            case R.id.tv_saler_phone:  //卖家信息 中电话拨打
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("tel", informationDepartment.getContactPhone());
                map1.put("callType", Constant.CALE_TYPE_COAL_SEND_CAR_ORDER);
                map1.put("targetId", sendCarEntity.getTransportOrderCode());
                UIHelper.showCollTel(mContext, map1, true);
                break;
            case R.id.tv_buyer_phone:  //买家信息 中电话拨打
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("tel", sendCarEntity.getContactPhone());
                map2.put("callType", Constant.CALE_TYPE_COAL_SEND_CAR_ORDER);
                map2.put("targetId", sendCarEntity.getTransportOrderCode());
                UIHelper.showCollTel(mContext, map2, true);
                break;
            case R.id.tv_one_text:
                upData = true;
                getData();
                break;
            case R.id.iv_pound:
                if (!StringUtils.isEmpty(sendCarEntity.getCarryWeightDocPicUrl())){
                    //跳转至查看大图界面
                    Intent intent = new Intent();
                    intent.putExtra("url", sendCarEntity.getCarryWeightDocPicUrl());//磅单
                    intent.setClass(this, PicturePreviewActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.layout_saler:
                //跳转至地图界面
                new PermissionUtil().requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
                    @Override
                    public void onGranted() { //用户同意授权
                        Intent intent = new Intent(SendCarDetailActivity.this, MapActivity.class);
                        intent.putExtra("Entity", informationDepartment);
                        intent.putExtra("Type", "InformationDepartment");
                        startActivity(intent);
                    }

                    @Override
                    public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                    }
                });
                break;
            case R.id.layout_mine_mouth:
                //跳转至地图界面 mineProduct 矿口实体
                new PermissionUtil().requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
                    @Override
                    public void onGranted() { //用户同意授权
                        Intent intent = new Intent(SendCarDetailActivity.this, MapActivity.class);
                        intent.putExtra("Entity", mineMouth);
                        intent.putExtra("Type", "MineMouth");
                        startActivity(intent);
                    }

                    @Override
                    public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                    }
                });
                break;
            //上传磅单  /重新上传磅单
            case R.id.btn_upload1:
                coalBack = new CustomCallback() {
                    @Override
                    public void onCallback() {
                        if (!"3".equals(orderStatus)){
                            Intent intent1 = new Intent(mContext, PoundListActivity.class);
                            intent1.putExtra("entity", sendCarEntity);
                            intent1.putExtra("type", 1);
                            startActivityForResult(intent1, UDPATE_SEND_CAR_DETAIL);
                            coalBack = null;
                        }else{
                            displayToast("本车已由信息部确认完成！");
                        }
                    }
                };
                getData();
                break;
            case R.id.coal_funds:  //煤款不足，去充值
                new RLAlertDialog(this, "消息提示", "您的煤款不足，请充值！", "取消",
                        "联系客服", new RLAlertDialog.Listener() {
                    @Override
                    public void onLeftClick() {
                    }

                    @Override
                    public void onRightClick() {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("tel", getResources().getString(R.string.service_hot_line));
                        map.put("callType", Constant.CALE_TYPE_COAL_RECHARGE);
                        map.put("targetId", SharedTools.getStringValue(mContext,"userId","-1"));
                        UIHelper.showCollTel(mContext, map, true);
                    }
                }).show();
                break;
        }
    }

    private void onBack() {
        if (ifRefresh){
            setResult(RESULT_OK);
            sendBroadcast();
            finish();
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }

    /**
     * 向服务器提交处理操作
     *  根据状态处理参数 1.确认收货   2.确认发货  3.确认司机到达  4.取消派车单
     * @param handleStatus
     */
    private void handle2Service(String handleStatus) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("transportOrderCode", transportOrderCode);
            params.put("orderState", handleStatus);
            new DataUtils(this, params).handleSendCarOrder(new DataUtils.DataBack<SendCarEntity>() {
                @Override
                public void getData(SendCarEntity sendCarEntity) {
                    try {
                        if (sendCarEntity == null) {
                            return;
                        }
                        SendCarDetailActivity.this.sendCarEntity = sendCarEntity;
                        informationDepartment = sendCarEntity.getInformationDepartment();
                        mineMouth = sendCarEntity.getMineMouth();
                        orderStatus = sendCarEntity.getOrderState();
                        //根据货运单状态 初始化订单流程时间轴数据
                        initOrderStatusData();
                        //初始化货运单状态水平方向时间轴
                        initOrderStatusHorizontalView();
                        setData();
                        displayToast("取消成功！");
                        ifRefresh = true;
                        sendBroadcast();
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
        Intent intent = new Intent(Constant.UPDATE_SEND_CAR_ORDER_FRAGMENT + "");
        sendBroadcast(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UDPATE_SEND_CAR_DETAIL) {
            getData();
        }
    }
}
