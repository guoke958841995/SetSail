package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autonavi.rtbt.IFrameForRTBT;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.DetailPurchasingReservationActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.UPDATE_MY_RELEASE_FRAGMENT;

/**
 * Created by liz on 2017/11/10.
 * 买煤求购
 */

public class ProcurementFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<UserDemand> {

    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;
    @Bind(R.id.iv_no_data)
    ImageView ivNoData;
    @Bind(R.id.tv_no_data)
    TextView tvNoData;
    @Bind(R.id.procurement_list)
    SmoothListView procurementList;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.divider)
    View divider;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    @Bind(R.id.layout)
    RelativeLayout layout;

    public BaseAdapterUtils baseAdapterUtils;
    private FilterPopupWindow filterPopupWindow;
    private List<UserDemand> userDemandEntities = new ArrayList<UserDemand>();
    private int currentPage = 1;
    private Activity myActivity;
    private Map<String, String> coalTypeMap = new HashMap<String, String>();
    private String haseBond = "";//是否保价 0:否1:是
    private String searchValue;//搜索关键字
    private Map<String, FilterEntity> map = new HashMap<>();
    private String regionCode = "0";
    private MyRefreshReceiver myRefreshReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_procurement, container, false);
            ButterKnife.bind(this, view);

            ivNoData.setImageResource(R.mipmap.icon_no_transport_order);
            tvNoData.setText("暂无求购单");
        } catch (Exception e) {
            GHLog.e("货运功能界面展示", e.toString());
        }
        return view;
    }

    private void initView() {
        layout.setVisibility(View.VISIBLE);
        baseAdapterUtils = new BaseAdapterUtils(myActivity, procurementList);
        baseAdapterUtils.setViewItemData(R.layout.purchase_orders_list_item, userDemandEntities);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (myRefreshReceiver != null) {
            myActivity.unregisterReceiver(myRefreshReceiver);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        initView();
        initListener();
        queryCoalType();
        registerMyReceiver();
        getData();
    }

    /**
     * 注册广播
     */
    public void registerMyReceiver() {
        myRefreshReceiver = new MyRefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_MY_RELEASE_FRAGMENT + "");
        myActivity.registerReceiver(myRefreshReceiver, filter);
    }

    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchValue = etSearch.getText().toString().trim();
                if (!StringUtils.isEmpty(searchValue) && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) myActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    refresh();
                    return true;
                }else if (StringUtils.isEmpty(searchValue) && baseAdapterUtils.getCount() == 0){
                    refresh();
                    return true;
                }
                return false;
            }
        });
    }

    private void queryCoalType() {
        Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100002"}).get(0);
        for (int i = 0; i < coalTypes.list.size(); i++) {
            coalTypeMap.put(coalTypes.list.get(i).dictCode, coalTypes.list.get(i).dictValue);
        }
    }

    private void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            if (map.size() > 0) {
                for (Map.Entry<String, FilterEntity> entry : map.entrySet()) {
                    if (entry.getKey().equals("发热量") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //发热量
                        params.put("calorificValue", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("时间选择") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //发热量
                        params.put("timeRange", entry.getValue().dictCode);
                    }
                }
            }
            //地区
            if (!regionCode.equals("0")) {
                params.put("regionCode", regionCode);
            }
            //是否含保证金
            if (!StringUtils.isEmpty(haseBond)) {
                params.put("haseBond", haseBond);
            }
            //搜索
            params.put("searchValue", searchValue);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(myActivity, params).getUserDemandList(new DataUtils.DataBack<List<UserDemand>>() {
                @Override
                public void getData(List<UserDemand> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        userDemandEntities = dataMemager;
                        baseAdapterUtils.refreshData(userDemandEntities);
                        ((BaseActivity) myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, procurementList);
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
    public void getItemViewData(BaseAdapterHelper helper, UserDemand entity, int pos) {
        View dividerRough = helper.getView().findViewById(R.id.layout_divider_rough);
        View dividerThin = helper.getView().findViewById(R.id.layout_divider_thin);
        if (pos == 0) {
            dividerRough.setVisibility(View.GONE);
            dividerThin.setVisibility(View.VISIBLE);
        } else {
            dividerRough.setVisibility(View.VISIBLE);
            dividerThin.setVisibility(View.GONE);
        }
        CircleImageView ivHead = (CircleImageView) helper.getView().findViewById(R.id.iv_head);
        if (!StringUtils.isEmpty(entity.getHeadPic())) {
            // 头像
            ((BaseActivity) myActivity).getImageManager().loadUrlImageView(entity.getHeadPic(), ivHead);
        } else {
            ivHead.setImageResource(R.mipmap.main_tab_item);
        }
        String coalType = coalTypeMap.get(entity.getCategoryId());
        TextView tvStatus = (TextView) helper.getView().findViewById(R.id.tv_status);
        if (entity.getRealnameAuthState().equals("1")) {
            //已认证 显示名字 隐藏中间
            helper.setText(R.id.tv_name, StringUtils.setName(entity.getContactPerson()));
            tvStatus.setText("已认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_green));
        } else {
            //未认证 显示电话号码隐藏中间4位
            helper.setText(R.id.tv_name, StringUtils.setPhoneNumber(entity.getContactPhone()));
            tvStatus.setText("未认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_gray));
        }

        TextView tvCash = (TextView) helper.getView().findViewById(R.id.tv_cash);
        TextView tvCashDeposit = (TextView) helper.getView().findViewById(R.id.tv_cash_deposit);
        if ("0".equals(entity.getBond())) {
            tvCash.setVisibility(View.GONE);
            tvCashDeposit.setVisibility(View.GONE);
        } else {
            tvCash.setVisibility(View.VISIBLE);
            tvCashDeposit.setVisibility(View.VISIBLE);
            tvCash.setText("¥" + StringUtils.setNumLenth(Float.valueOf(entity.getBond())/100, 2));
        }
        helper.setText(R.id.tv_title, "求购基低位发热量 " + entity.getCalorificValue() + "Kcal/Kg " + entity.getCoalName());
        helper.setText(R.id.tv_coal_name, coalType);
        helper.setText(R.id.tv_area, entity.getRegionName());
        helper.setText(R.id.tv_num, entity.getNumber());
        helper.setText(R.id.tv_price, StringUtils.isEmpty(entity.getPrice()) ? "无" : entity.getPrice());
        helper.setText(R.id.tv_time, entity.getCreateTime());
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserDemand> mAdapter) {
        Intent intent = new Intent();
        intent.setClass(myActivity, DetailPurchasingReservationActivity.class);
        intent.putExtra("demandId", mAdapter.getItem(position - 1).getDemandId());
        myActivity.startActivityForResult(intent, Constant.REFRESH_CODE);
    }

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        this.currentPage = page;
        getData();
    }

    public void refresh() {
        if (baseAdapterUtils != null){
            baseAdapterUtils.onRefresh();
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

        Dictionary sys100007 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100007"}).get(0);
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setChecked(true);
        filterEntity.dictCode = "";
        filterEntity.dictValue = "不限";
        sys100007.list.add(0, filterEntity);
        data.add(1, sys100007);
        filterPopupWindow = new FilterPopupWindow(myActivity, data, true, true,false, procurementList.getHeight()==0? layoutNoData.getHeight():procurementList.getHeight());
        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                haseBond = filterPopupWindow.isOpened() == true ? "1" : "";
                map = filterPopupWindow.getMap();
                refresh();
            }
        });
        filterPopupWindow.setOnClickListener(new FilterPopupWindow.ResetRegionCode() {
            @Override
            public void reset(String follow) {
                    regionCode = follow;
            }
        });
        filterPopupWindow.setTitle("是否保价");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && resultCode == myActivity.RESULT_OK) {
             regionCode = data.getStringExtra("code");
            String name = data.getStringExtra("name");
            filterPopupWindow.setAreaText(name);
            return;
        }
        //从发布返回
        if (resultCode == myActivity.RESULT_OK){
            getData();
        }
    }

    /**
     * 接受到广播后刷新页面
     */
    class MyRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((UPDATE_MY_RELEASE_FRAGMENT + "").equals(intent.getAction())) {
                baseAdapterUtils.onRefresh();
            }
        }
    }
}
