package com.sxhalo.PullCoal.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.DriverCertificationActivity;
import com.sxhalo.PullCoal.activity.GoingToPullCoalActivity;
import com.sxhalo.PullCoal.activity.PaymentInformationActivity;
import com.sxhalo.PullCoal.activity.SendCarDetailActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.SaleManager;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 派车单列表
 * Created by liz on 2018/5/2.
 */

public class SendCarOrderFragment extends BaseFragment implements BaseAdapterUtils.BaseAdapterBack<SendCarEntity> {

    @Bind(R.id.listview)
    SmoothListView listview;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.divider)
    View divider;
    @Bind(R.id.tv_filter)
    TextView tvFilter;

    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;
    @Bind(R.id.iv_no_data)
    ImageView ivNoData;
    @Bind(R.id.tv_no_data)
    TextView tvNoData;

    private FilterPopupWindow filterPopupWindow;
    private Map<String, FilterEntity> map = new HashMap<>();
    private List<SendCarEntity> sendCarEntities = new ArrayList<>();
    private int height;
    private String searchValue;//搜索关键字

    private BaseActivity myActivity;
    private int currentPage = 1;
    private BaseAdapterUtils baseAdapterUtils;
    private MyRefreshReceiver myRefreshReceiver;
    private String currentUserPhone;//当前登录用户电话号码

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_take_delivery, container, false);
            ButterKnife.bind(this, view);

            ivNoData.setImageResource(R.mipmap.icon_no_transport_order);
            tvNoData.setText("暂无派车单");
        } catch (Exception e) {
            GHLog.e("派车单列表", e.toString());
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = (BaseActivity) getActivity();
        currentUserPhone = SharedTools.getStringValue(myActivity, "user_mobile", "-1");
        initView();
        initListener();
        registerMyReceiver();
        getDataPath();
    }

    /**
     * 注册广播
     */
    public void registerMyReceiver() {
        myRefreshReceiver = new MyRefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.UPDATE_SEND_CAR_ORDER_FRAGMENT + "");
        myActivity.registerReceiver(myRefreshReceiver, filter);
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(myActivity, listview);
        baseAdapterUtils.setViewItemData(R.layout.send_car_order_item, sendCarEntities);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchValue = etSearch.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) myActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    baseAdapterUtils.onRefresh();
                    return true;
                }
                return false;
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    searchValue = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void getDataPath() {
        try {
            if ("-1".equals(SharedTools.getStringValue(myActivity,"userId","-1"))){
                return;
            }
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            if (map.size() > 0) {
                for (Map.Entry<String, FilterEntity> entry : map.entrySet()) {
                    if (entry.getKey().equals("时间选择") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //发热量
                        params.put("timeRange", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("派车单状态") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //发热量
                        params.put("orderState", entry.getValue().dictCode);
                    }
                }
            }
            //搜索
            params.put("searchValue", searchValue);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(myActivity, params).getMyCoalOrderTransport(new DataUtils.DataBack<APPDataList<SendCarEntity>>() {
                @Override
                public void getData(APPDataList<SendCarEntity> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        if (dataMemager.getList() != null && dataMemager.getList().size() > 0) {
                            sendCarEntities = dataMemager.getList();
                            baseAdapterUtils.refreshData(sendCarEntities);
                        }
                        (myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, listview);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2080001".equals(e.getMessage())){
                        displayToast("网络连接失败，请稍后再试！");
                    }else{
                        displayToast("网络连接失败，请稍后再试！");
                    }
                    (myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, listview);
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @OnClick(R.id.layout_filter)
    public void onViewClicked() {
        //筛选
        if (filterPopupWindow == null) {
            createPopupWindow();
        }
        if (!filterPopupWindow.isShowing()) {
            filterPopupWindow.showAsDropDown(this.divider);
            tvFilter.setTextColor(getResources().getColor(R.color.actionsheet_blue));
        } else {
            filterPopupWindow.dismiss();
            tvFilter.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void createPopupWindow() {
        List<Dictionary> data = new ArrayList<>();

        //时间选择
        Dictionary timeRange = getTimeRangeType();
        data.add(0, timeRange);

        Dictionary orderStatus = getOrderStatusType();

        data.add(1, orderStatus);
        filterPopupWindow = new FilterPopupWindow(myActivity, data, false, false,false, listview.getHeight() == 0 ? layoutNoData.getHeight() + height : listview.getHeight() + height);
        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                map = filterPopupWindow.getMap();
                baseAdapterUtils.onRefresh();
            }
        });
    }

    /**
     * 0、待处理(派车中)；1、装车中；2、已起运；3、已完成；100、取消
     * @return
     */
    private Dictionary getOrderStatusType() {
        Dictionary orderStatusType = new Dictionary();
        orderStatusType.title = "派车单状态";
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.dictCode = "";
        filterEntity.dictValue = "不限";
        filterEntity.setChecked(true);

        FilterEntity filterEntity0 = new FilterEntity();
        filterEntity0.dictCode = "0";
        filterEntity0.dictValue = "待处理";

        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "装车中";

        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "已装车";

        FilterEntity filterEntity3 = new FilterEntity();
        filterEntity3.dictCode = "3";
        filterEntity3.dictValue = "起运";

        FilterEntity filterEntity100 = new FilterEntity();
        filterEntity100.dictCode = "100";
        filterEntity100.dictValue = "已取消";

        orderStatusType.list = new ArrayList<>();
        orderStatusType.list.add(0, filterEntity);
        orderStatusType.list.add(1, filterEntity0);
        orderStatusType.list.add(2, filterEntity1);
        orderStatusType.list.add(3, filterEntity2);
        orderStatusType.list.add(4, filterEntity3);
        orderStatusType.list.add(5, filterEntity100);
        return orderStatusType;
    }

    private Dictionary getTimeRangeType() {
        Dictionary timeRangeType = new Dictionary();
        timeRangeType.title = "时间选择";
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.dictCode = "";
        filterEntity.dictValue = "不限";
        filterEntity.setChecked(true);

        FilterEntity filterEntity0 = new FilterEntity();
        filterEntity0.dictCode = "0";
        filterEntity0.dictValue = "近一周";

        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "一个月内";

        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "三个月内";

        timeRangeType.list = new ArrayList<>();
        timeRangeType.list.add(0, filterEntity);
        timeRangeType.list.add(1, filterEntity0);
        timeRangeType.list.add(2, filterEntity1);
        timeRangeType.list.add(3, filterEntity2);
        return timeRangeType;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, final SendCarEntity sendCarEntity, int pos) {
        View view_line = helper.getView().findViewById(R.id.divider);
        if (pos == 0) {
            view_line.setVisibility(View.GONE);
        } else {
            view_line.setVisibility(View.VISIBLE);
        }
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

        String userId = SharedTools.getStringValue(myActivity,"userId","-1");
        if ("3".equals(sendCarEntity.getOrderState()) && userId.equals(sendCarEntity.getUserId())){
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

        helper.setText(R.id.tv_driver_name, sendCarEntity.getDriverRealName());
        helper.setText(R.id.tv_phone, sendCarEntity.getDriverPhone());

        //提货码显示与否判断
        if ("0".equals(sendCarEntity.getOrderState())){
            //已送达
            helper.getView().findViewById(R.id.code_view).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_code, "提货码：" + sendCarEntity.getCertificate());
        }else{
            helper.getView().findViewById(R.id.code_view).setVisibility(View.GONE);
        }
    }

    /**
     * 弹出框
     */
    public void showPayDialog(final SendCarEntity sendCarEntity) {
        new RLAlertDialog(myActivity, "系统提示", "确认是否发送?", "取消",
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

    private void sendSMS(String phoneNumber, String content) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber", phoneNumber);  //手机号
            params.put("sendContent", content);  //短信内容
            new DataUtils(myActivity, params).getSendSMS(new DataUtils.DataBack<SaleManager>() {
                @Override
                public void getData(SaleManager dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        myActivity.displayToast("发送成功！");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<SendCarEntity> mAdapter) {
        Intent intent = new Intent(myActivity, SendCarDetailActivity.class);
        intent.putExtra("transportOrderCode", mAdapter.getItem(position - 1).getTransportOrderCode());
        startActivity(intent);
    }

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        getDataPath();
    }

    @Override
    public void getOnLoadMore(int page) {
        this.currentPage = page;
        getDataPath();
    }

    /**
     * 接受到广播后刷新页面
     */
    class MyRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constant.UPDATE_SEND_CAR_ORDER_FRAGMENT + "").equals(intent.getAction())) {
                baseAdapterUtils.onRefresh();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (myRefreshReceiver != null) {
            myActivity.unregisterReceiver(myRefreshReceiver);
        }
    }
}
