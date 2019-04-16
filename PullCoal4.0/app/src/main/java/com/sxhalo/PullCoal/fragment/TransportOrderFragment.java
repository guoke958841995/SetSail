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
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.MyTransportOrderDetailActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
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

import static android.app.Activity.RESULT_OK;

/**
 * 货运单
 * Created by liz on 2018/5/2.
 */
public class TransportOrderFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<TransportMode>{

    @Bind(R.id.listview)
    SmoothListView smoothListView;
    @Bind(R.id.layout_no_transport_order)
    RelativeLayout layoutNoData;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.divider)
    View divider;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    private List<TransportMode> transportModeList = new ArrayList<TransportMode>();
    private FilterPopupWindow filterPopupWindow;
    private Map<String, FilterEntity> map = new HashMap<>();
    private int height;
    private String searchValue;//搜索关键字
    private MyRefreshReceiver myRefreshReceiver;

    public static final int REFRESH = 119;


    private BaseActivity myActivity;
    private int currentPage = 1;
    private BaseAdapterUtils baseAdapterUtils;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_transport_order, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e("提货单", e.toString());
        }
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
        filter.addAction(Constant.UPDATE_MY_TRANSPORT_ORDER + "");
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

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(myActivity, smoothListView);
        baseAdapterUtils.setViewItemData(R.layout.transport_order_item, transportModeList);
        baseAdapterUtils.setBaseAdapterBack(this);
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
                    if (entry.getKey().equals("订单状态") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //发热量
                        params.put("orderState", entry.getValue().dictCode);
                    }
                }
            }
            //搜索
            params.put("searchValue", searchValue);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(myActivity,params).getUserTransportOrderList(new DataUtils.DataBack<APPDataList<TransportMode>>() {
                @Override
                public void getData(APPDataList<TransportMode> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        if (dataMemager.getList() != null && dataMemager.getList().size() > 0) {
                            baseAdapterUtils.refreshData(dataMemager.getList());
                            transportModeList = baseAdapterUtils.getListData();
                        }
                        if (currentPage == 1){
                            (myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, smoothListView);
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

    @Override
    public void getItemViewData(BaseAdapterHelper helper, TransportMode transportMode, int pos) {
        View view_line = helper.getView().findViewById(R.id.divider);
        if (pos == 0) {
            view_line.setVisibility(View.GONE);
        } else {
            view_line.setVisibility(View.VISIBLE);
        }
        String acceptanceStatus = transportMode.getOrderState();
        String type = transportMode.getOrderType();//货运单类型：0，主动接单；1，邀请接单
        //货运单状态：0、待处理；1、同意；2、拒绝；3、司机确认拉货   4、信息部确认司机到达；100、取消
        switch (Integer.valueOf(acceptanceStatus)){
            case 0: //待处理
                if (type.equals("0")){
                    //主动接单
                    helper.setText(R.id.tv_status, "待审核");
                }else if (type.equals("1")){
                    //受邀
                    helper.setText(R.id.tv_status, "待接受");
                }
                helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));

                helper.setTextColor(R.id.tv_status,getResources().getColor(R.color.actionsheet_blue) );
                break;
            case 1: //同意
                //审核通过
                helper.setText(R.id.tv_status, "进行中");
                helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.actionsheet_blue));

                helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                break;
            case 2:  //拒绝
                if (type.equals("0")){
                    //主动接单
                    helper.setText(R.id.tv_status, "信息部拒绝");
                }else if (type.equals("1")){
                    //受邀
                    helper.setText(R.id.tv_status, "已拒绝");
                }
                helper.setTextColor(R.id.tv_status,getResources().getColor( R.color.gray));

                helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                break;
            case 3:  //司机确认拉货
                //已完成
                helper.setText(R.id.tv_status, "已完成");
                helper.setTextColor(R.id.tv_status,getResources().getColor( R.color.gray));

                helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                break;
            case 4: //信息部确认司机到达
                //已完成
                helper.setText(R.id.tv_status, "已完成");
                helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.gray));

                helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                break;
            case 100: //取消
                //已取消
                helper.setText(R.id.tv_status, "已取消");
                helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.gray));

                helper.setTextColor(R.id.tv_transport_order_time,getResources().getColor(R.color.gray));
                helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                break;
        }
        helper.setText(R.id.tv_transport_order_num, transportMode.getTransportOrderCode());
        helper.setText(R.id.tv_start, transportMode.getFromPlace());
        helper.setText(R.id.tv_end, transportMode.getToPlace());
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<TransportMode> mAdapter) {
        Intent intent = new Intent(myActivity, MyTransportOrderDetailActivity.class);
        intent.putExtra("waybilNumber",mAdapter.getItem(position - 1).getTransportOrderCode());
        startActivityForResult(intent, REFRESH);
    }

    @Override
    public void getOnRefresh(int page) {
        currentPage = page;
        getDataPath();
    }

    @Override
    public void getOnLoadMore(int page) {
        currentPage = page;
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
                //跳转到货运列表模块
                ((MainActivity) myActivity).setShowFragment(false);
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
        filterPopupWindow = new FilterPopupWindow(myActivity, data, false, false,false, smoothListView.getHeight() == 0 ? layoutNoData.getHeight() + height : smoothListView.getHeight() + height);
        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                map = filterPopupWindow.getMap();
                baseAdapterUtils.onRefresh();
            }
        });
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
        filterEntity0.dictValue = "待处理";

        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "进行中";

        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "已拒绝";

        FilterEntity filterEntity3 = new FilterEntity();
        filterEntity3.dictCode = "3";
        filterEntity3.dictValue = "已完成";

        FilterEntity filterEntity4 = new FilterEntity();
        filterEntity4.dictCode = "100";
        filterEntity4.dictValue = "已取消";

        orderStatusType.list = new ArrayList<>();
        orderStatusType.list.add(0, filterEntity);
        orderStatusType.list.add(1, filterEntity0);
        orderStatusType.list.add(2, filterEntity1);
        orderStatusType.list.add(3, filterEntity2);
        orderStatusType.list.add(4, filterEntity3);
        orderStatusType.list.add(5, filterEntity4);
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
            if ((Constant.UPDATE_MY_TRANSPORT_ORDER + "").equals(intent.getAction())) {
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