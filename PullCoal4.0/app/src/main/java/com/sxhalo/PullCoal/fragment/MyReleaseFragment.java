package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.daiglog.TowButtonDialog;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.UPDATE_MY_RELEASE_FRAGMENT;

/**
 * 买煤求购 我的发布界面
 * Created by liz on 2017/11/10.
 */

public class MyReleaseFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<UserDemand> {

    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;
    @Bind(R.id.iv_no_data)
    ImageView ivNoData;
    @Bind(R.id.tv_no_data)
    TextView tvNoData;
    @Bind(R.id.procurement_list)
    SmoothListView releaseList;
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
    private int height;
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

    /**
     * 注册广播
     */
    public void registerMyReceiver() {
        myRefreshReceiver = new MyRefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_MY_RELEASE_FRAGMENT + "");
        myActivity.registerReceiver(myRefreshReceiver, filter);
    }

    private void initView() {
        layout.setVisibility(View.VISIBLE);
        baseAdapterUtils = new BaseAdapterUtils(myActivity, releaseList);
        baseAdapterUtils.setViewItemData(R.layout.purchase_my_release_item, userDemandEntities);
        baseAdapterUtils.setBaseAdapterBack(this);
        releaseList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                UserDemand userDemand = (UserDemand) baseAdapterUtils.getAdapter().getItem(position - 1);
                if ("0".equals(userDemand.getBond())){
                    showDeleteDialog(userDemand);
                }else{
                    if ("00".equals(userDemand.getDemandState()) || "60".equals(userDemand.getDemandState()) || "01".equals(userDemand.getDemandState())){
                        showDeleteDialog(userDemand);
                    }else {
                        showDialog();
                    }
                }
                return true;
            }
        });
    }

    private void showDeleteDialog(final UserDemand userDemandEntity) {
        new RLAlertDialog(myActivity, "用户提示",getString(R.string.delete_my_release), "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                delatData(userDemandEntity);
            }
        }).show();
    }

    private void showDialog() {
        new RLAlertDialog(myActivity, "用户提示",getString(R.string.prompt_my_release), "确定",
                "", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {

            }
        }).show();
    }

    /**
     * 删除买煤求购
     */
    private void delatData(final UserDemand userDemandEntity) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("demandId", userDemandEntity.getDemandId());
            new DataUtils(myActivity, params).getUserDemandDelete(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String dataMemager) {
                    try {
                        if (StringUtils.isEmpty(dataMemager)) {
                            ((BaseActivity) myActivity).displayToast(getString(R.string.delete_procurement_success));
                        } else {
                            ((BaseActivity) myActivity).displayToast(getString(R.string.delete_procurement_success));
                        }
                        baseAdapterUtils.onRefresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        initView();
        queryCoalType();
        initListener();
        String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
        // 判断是否登录
        if (!userId.equals("-1")) { // 已登录并且信息获取成功
            registerMyReceiver();
            getData();
        }
    }

    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchValue = etSearch.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) myActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    refresh();
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

    private void queryCoalType() {
        Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100002"}).get(0);
        for (int i = 0; i < coalTypes.list.size(); i++) {
            coalTypeMap.put(coalTypes.list.get(i).dictCode, coalTypes.list.get(i).dictValue);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            getData();
        }
    }

    private void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            String userId = SharedTools.getStringValue(myActivity, "userId", "-1");

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
            if (!regionCode.equals("0")) {
                params.put("regionCode", regionCode);
            }
            //是否含保证金
            if (!StringUtils.isEmpty(haseBond)) {
                params.put("haseBond", haseBond);
            }
            //搜索
            params.put("searchValue", searchValue);
            params.put("userId", userId);
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
                        ((BaseActivity) myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, releaseList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2130001".equals(e.getMessage())){
                        ((BaseActivity) myActivity).displayToast(e.getCause().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, UserDemand entity, int pos) {

        String coalType = coalTypeMap.get(entity.getCategoryId());
        helper.setText(R.id.tv_title, "求购基低位发热量" + entity.getCalorificValue() + "Kcal/Kg" + entity.getCoalName());
        helper.setText(R.id.tv_coal_name, coalType);
        helper.setText(R.id.tv_area, entity.getRegionName());
        helper.setText(R.id.tv_num, entity.getNumber());
        helper.setText(R.id.tv_time, entity.getCreateTime());

        LinearLayout layoutBond = (LinearLayout) helper.getView().findViewById(R.id.layout_bond);
        TextView tvCash = (TextView) helper.getView().findViewById(R.id.tv_cash);
        TextView tvDeliver = (TextView) helper.getView().findViewById(R.id.tv_deliver);
        if ("0".equals(entity.getBond())) {
            layoutBond.setVisibility(View.GONE);
            helper.setText(R.id.tv_overdue, "已发布");
            tvDeliver.setVisibility(View.GONE);
        } else {
            layoutBond.setVisibility(View.VISIBLE);
            tvCash.setText("¥" + StringUtils.setNumLenth(Float.valueOf(entity.getBond())/100, 2).replace(".00",""));

            tvDeliver.setVisibility(View.VISIBLE);
            if (entity.getDeliveryTotal() == null || "0".equals(entity.getDeliveryTotal())){
                helper.setText(R.id.tv_deliver, "没有信息部投递意向");
            }else{
                helper.setText(R.id.tv_deliver, "已有" + entity.getDeliveryTotal() + "家信息部投递意向");
            }
            //  01、未发布；10、已发布（待投递，确认）；20、待选定；30、磋商；40、未达成意向；41、已达成意向；50、退款；60、完成；00、取消
            String demandState = entity.getDemandState();
            if ( "01".equals(demandState)){
                tvDeliver.setVisibility(View.GONE);
                helper.setText(R.id.tv_overdue, "未发布");
            }else{
                int state = Integer.valueOf(demandState);
                switch (state){  //状态：01、未发布；10、已发布（待投递，确认）；20、待选定；30、磋商；40、未达成意向；41、已达成意向；50、退款；60、完成；00、取消
                    case 10:
                        tvDeliver.setVisibility(View.VISIBLE);
                        String TEXT = dateDiff(entity.getDeliveryEndTime());
                        if ( !"即将到期".equals(TEXT) && !"已过期".equals(TEXT)){
                            TEXT = TEXT + "后过期";
                        }
                        helper.setText(R.id.tv_overdue,TEXT );
                        break;
                    case 20:
                        tvDeliver.setVisibility(View.VISIBLE);
                        String TEXT1 = dateDiff(entity.getDeliveryEndTime());
                        if ( !"即将到期".equals(TEXT1) && !"已过期".equals(TEXT1)){
                            TEXT1 = TEXT1 + "后过期";
                        }
                        helper.setText(R.id.tv_overdue,TEXT1 );
                        break;
                    case 30:
                        tvDeliver.setVisibility(View.GONE);
                        helper.setText(R.id.tv_overdue, "磋商");
                        break;
                    case 40:
                    case 41:
                    case 50:
                        tvDeliver.setVisibility(View.GONE);
                        helper.setText(R.id.tv_overdue, "退款");
                        break;
                    case 60:
                        tvDeliver.setVisibility(View.GONE);
                        helper.setText(R.id.tv_overdue, "完成");
                        break;
                    case 00:
                        tvDeliver.setVisibility(View.GONE);
                        helper.setText(R.id.tv_overdue, "已取消");
                        break;
                }
            }
        }
    }


    private  String dateDiff(String endTime) {
        String strTime = null;
        long mDay = 10;
        long mHour = 10;
        long mMin = 30;
        long mSecond = 00;// 天 ,小时,分钟,秒

        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sd.format(curDate);
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime()
                    - sd.parse(str).getTime();
            mDay = diff / nd;// 计算差多少天
            mHour = diff % nd / nh;// 计算差多少小时
            mMin = diff % nd % nh / nm;// 计算差多少分钟
            mSecond = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (mDay >= 1) {
                strTime = mDay + "天" + mHour + "时"+ mMin + "分";
            } else {
                if (mHour >= 1) {
                    strTime = mDay + "天" + mHour + "时" + mMin + "分";
                } else {
                    if (mSecond >= 1) {
                        strTime = mDay + "天" + mHour + "时" + mMin + "分" + mSecond + "秒" ;
                    } else if (mSecond < 0){
                        strTime = "已过期";
                    }else{
                        strTime = "即将到期";
                    }
                }
            }
            return strTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
        baseAdapterUtils.onRefresh();
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
        filterPopupWindow = new FilterPopupWindow(myActivity, data, true, true,false, releaseList.getHeight() == 0 ? layoutNoData.getHeight() + height : releaseList.getHeight() + height);
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
        }
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
            if ((UPDATE_MY_RELEASE_FRAGMENT + "").equals(intent.getAction())) {
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
