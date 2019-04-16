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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.BuyCoalActivity;
import com.sxhalo.PullCoal.activity.CoalOrderDetailActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.Orders;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 煤炭订单
 * Created by liz on 2018/5/2.
 */

public class CoalOrderFragment extends BaseFragment implements BaseAdapterUtils.BaseAdapterBack<Orders> {

    @Bind(R.id.layout_no_order)
    RelativeLayout layoutNoOrder;
    @Bind(R.id.listview)
    SmoothListView ordersList;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.divider)
    View divider;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    private BaseActivity myActivity;
    private FilterPopupWindow filterPopupWindow;
    private List<Orders> orders = new ArrayList<Orders>();
    private int currentPage = 1;
    private BaseAdapterUtils baseAdapterUtils;
    private Map<String, FilterEntity> map = new HashMap<>();
    private int height;
    private String searchValue;//搜索关键字
    private MyRefreshReceiver myRefreshReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coal_order, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = (BaseActivity) getActivity();
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
        filter.addAction(Constant.UPDATE_COAL_ORDER_FRAGMENT + "");
        myActivity.registerReceiver(myRefreshReceiver, filter);
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
                        //时间选择
                        params.put("timeRange", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("订单状态") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //订单状态
                        params.put("orderState", entry.getValue().dictCode);
                    }
                }
            }
            //搜索
            params.put("searchValue", searchValue);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(getActivity(), params).getUserCoalOrderList(new DataUtils.DataBack<APPDataList<Orders>>() {
                @Override
                public void getData(APPDataList<Orders> dataOrders) {
                    try {
                        if (dataOrders == null) {
                            return;
                        }
                        if (dataOrders.getList() != null) {
                            orders = dataOrders.getList();
                        }
                        baseAdapterUtils.refreshData(orders);
                        (myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoOrder, ordersList);
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2080001".equals(e.getMessage())){
                        displayToast("网络连接失败，请稍后再试！");
                    }else{
                        displayToast("网络连接失败，请稍后再试！");
                    }
                    (myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoOrder, ordersList);
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
        }
    }


    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(myActivity, ordersList);
        baseAdapterUtils.setViewItemData(R.layout.my_orders_list_item, orders);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, Orders orders, int pos) {
        View view_line = helper.getView().findViewById(R.id.divider);
        if (pos == 0) {
            view_line.setVisibility(View.GONE);
        } else {
            view_line.setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.tv_coal_name,orders.getCoalName());
        helper.setText(R.id.tv_coal, orders.getTradingVolume().replace(".00", "") + "吨    " + orders.getCalorificValue() + "kCal/kg    " + orders.getCoalCategoryName());
        helper.setText(R.id.tv_seller, orders.getCoalSalesName());
        helper.setText(R.id.tv_place_of_pruduction, orders.getMineMouthName());
        helper.setText(R.id.tv_time, orders.getCreateTime());
        String orderStatus = orders.getOrderState(); //0，未受理；1，受理；2，拒绝；3，超期；100，取消
        TextView tvOrderStatus = (TextView) helper.getView().findViewById(R.id.tv_order_status);
        if (orderStatus.equals("0") || orderStatus.equals("1")|| orderStatus.equals("4")|| orderStatus.equals("5")) {
            //进行中 0 1 4 5
            orderStatus = "进行中";
            tvOrderStatus.setTextColor(getResources().getColor(R.color.app_title_text_color));
        }else if (orderStatus.equals("2")) {
            //已拒绝 2
            orderStatus = "已拒绝";
            tvOrderStatus.setTextColor(getResources().getColor(R.color.app_title_text_color));
        }else if (orderStatus.equals("3") || orderStatus.equals("6")) {
            //已超期 3 6
            orderStatus = "已超期";
            tvOrderStatus.setTextColor(getResources().getColor(R.color.app_title_text_color));
        }else if (orderStatus.equals("7")) {
            //已完成 7
            orderStatus = "已完成";
            tvOrderStatus.setTextColor(getResources().getColor(R.color.app_title_text_color));
        }else{
            //已取消 100
            orderStatus = "已取消";
            tvOrderStatus.setTextColor(getResources().getColor(R.color.app_title_text_color));
        }
        helper.setText(R.id.tv_order_status, orderStatus);

        // 是否白条订单：0否、1是
        if ("1".equals(orders.getIouUse())){
            helper.getView().findViewById(R.id.lous_orders_type).setVisibility(View.VISIBLE);
        }else{
            helper.getView().findViewById(R.id.lous_orders_type).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<Orders> mAdapter) {
        Intent intent = new Intent();
        intent.setClass(myActivity, CoalOrderDetailActivity.class);
        intent.putExtra("orderNumber", mAdapter.getItem(position - 1).getOrderNumber());
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

    @OnClick({R.id.layout_filter, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_filter:
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
                break;
            case R.id.btn_add:
                //立刻下单 跳转到煤炭列表
                startActivity(new Intent(myActivity, BuyCoalActivity.class));
                break;
        }
    }

    private void createPopupWindow() {
        List<Dictionary> data = new ArrayList<>();

        //时间选择
        Dictionary timeRange = getTimeRangeType();
        data.add(0, timeRange);

        Dictionary orderStatus = getOrderStatusType();

        data.add(1, orderStatus);
        filterPopupWindow = new FilterPopupWindow(myActivity, data, false, false,false, ordersList.getHeight() == 0 ? layoutNoOrder.getHeight() + height : ordersList.getHeight() + height);
        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                map = filterPopupWindow.getMap();
                baseAdapterUtils.onRefresh();
            }
        });
        filterPopupWindow.setTitle("是否保价");
    }

    private Dictionary getOrderStatusType() {
        Dictionary orderStatusType = new Dictionary();
        orderStatusType.title = "订单状态";
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.dictCode = "";
        filterEntity.dictValue = "不限";
        filterEntity.setChecked(true);

        FilterEntity filterEntity0 = new FilterEntity();
        filterEntity0.dictCode = "0";
        filterEntity0.dictValue = "进行中";


        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "已取消";

        FilterEntity filterEntity3 = new FilterEntity();
        filterEntity3.dictCode = "3";
        filterEntity3.dictValue = "已完成";

        FilterEntity filterEntity4 = new FilterEntity();
        filterEntity4.dictCode = "4";
        filterEntity4.dictValue = "已拒绝";

        orderStatusType.list = new ArrayList<>();
        orderStatusType.list.add(0, filterEntity);
        orderStatusType.list.add(1, filterEntity0);
        orderStatusType.list.add(2, filterEntity2);
        orderStatusType.list.add(3, filterEntity3);
        orderStatusType.list.add(4, filterEntity4);
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

    /**
     * 接受到广播后刷新页面
     */
    class MyRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constant.UPDATE_COAL_ORDER_FRAGMENT + "").equals(intent.getAction())) {
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