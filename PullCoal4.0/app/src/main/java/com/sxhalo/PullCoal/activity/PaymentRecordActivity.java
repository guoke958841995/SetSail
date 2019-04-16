package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.PayMentOrder;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.SearchView;
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
import butterknife.OnClick;

/**
 * 支付记录列表
 * Created by amoldZhang on 2018/1/8.
 */
public class PaymentRecordActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<PayMentOrder>{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_all)
    TextView tvAll;
    @Bind(R.id.tv_select_month)
    TextView tvSelectMonth;
    @Bind(R.id.iv_all)
    ImageView ivAll;
    @Bind(R.id.iv_month)
    ImageView ivMonth;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.search_view)
    SearchView searchView;
    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;
    @Bind(R.id.smooth_list_view)
    SmoothListView smoothListView;

    private List<Map<String, String>> stringList = new ArrayList<Map<String, String>>();
    private String[] strings = new String[]{"全部分类", "煤炭信息", "货运信息"};
    private int consultingFeeType = 0;// // 0全部分类 1煤炭信息 2货运信息
    private PopupWindow popupWindowOrder;

    private PopupWindow popupWindowTime;
    private List<Map<String, String>> stringTimeList = new ArrayList<Map<String, String>>();
    private String[] stringTime = new String[]{"本月", "上一个月","三个月内", "半年内","全部"};
    private int timeRange = 0;//  当前选择位置 0：这个月 1：上一个月 2：三个月内 3：半年内 4 全部


    private List<PayMentOrder> paymentList = new ArrayList<PayMentOrder>();
    private BaseAdapterUtils<PayMentOrder> baseAdapterUtils;
    private int currentPage = 1;
    private String searchValue;

    private boolean needRefresh = false;//是否需要刷新

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_payment_record);
    }

    @Override
    protected void initTitle() {
        title.setText("支付记录列表");
        createOrderPopupWindow();
        createPopupWindow();
        initView();
    }

    private void initView() {
        searchView.setHint("请输入关键字",R.color.text_grey);
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {

            @Override
            public void onRefreshAutoComplete(String text) {

            }

            @Override
            public void onSearch(String text) {
                if (!StringUtils.isEmpty(text)){
                    searchValue = text;
                    baseAdapterUtils.onRefresh();
                }
            }

            @Override
            public void clearContent() {

            }

            @Override
            public void onTipsItemClick(String text) {

            }

            @Override
            public void onSearchHasFocus(boolean hasFocus) {

            }
        });
        baseAdapterUtils = new BaseAdapterUtils<PayMentOrder>(this,smoothListView);
        baseAdapterUtils.setViewItemData(R.layout.payment_list_item, paymentList);
        baseAdapterUtils.setBaseAdapterBack(this);
    }


    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

            //搜索条件
            if (!StringUtils.isEmpty(searchValue)){
                params.put("searchValue", searchValue);
            }
            // 费用类别 0：煤炭，1：货运
            if (consultingFeeType != 0){
                params.put("consultingFeeType", (consultingFeeType == 1 ? 0:1) + "");
            }
            // 时间范围 0：这个月 1：上一个月 2：三个月内 3：半年内
            if (timeRange != 4){
                params.put("timeRange", timeRange + "");
            }
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this, params).getConsultingFeeRecordList(new DataUtils.DataBack<APPDataList<PayMentOrder>>() {
                @Override
                public void getData(APPDataList<PayMentOrder> appDataList) {
                    try {
                        if (appDataList == null) {
                            return;
                        }
                        if (appDataList.getList() == null) {
                            paymentList = new ArrayList<PayMentOrder>();
                        } else {
                            paymentList = appDataList.getList();
                        }
                        baseAdapterUtils.refreshData(paymentList);
                        showEmptyView(baseAdapterUtils.getCount(), layoutNoData, smoothListView);
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    showEmptyView(baseAdapterUtils.getCount(), layoutNoData, smoothListView);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 账单类型筛选
     */
    private void createOrderPopupWindow() {
        for (int i = 0; i < strings.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", strings[i]);
            if (i == 0) {
                map.put("show", "true");
            } else {
                map.put("show", "false");
            }
            stringList.add(map);
        }

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null, false);
        // 创建PopupWindow实例,宽度和高度充满
        popupWindowOrder = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindowOrder.setFocusable(true);
        // 设置允许在外点击消失
        popupWindowOrder.setOutsideTouchable(true);
        //刷新状态（必须刷新否则无效）
        popupWindowOrder.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindowOrder.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindowOrder.setAnimationStyle(R.style.AnimationFade);

        popupWindowOrder.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (needRefresh) {
                    needRefresh = false;
                    baseAdapterUtils.onRefresh();
                }
                darkenBackground(1f);
                ivAll.setImageResource(R.mipmap.icon_arrow_down);
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        ListView lv = (ListView) customView.findViewById(R.id.popview_listview);
        final QuickAdapter<Map<String, String>> adapter = new QuickAdapter<Map<String, String>>(this, R.layout.popupwindow_list_item, stringList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> item, int position) {
                helper.setText(R.id.tv_list_item, item.get("title"));
                if (item.get("show").equals("true")) {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (consultingFeeType == i) {
                    popupWindowOrder.dismiss();
                    return;
                }
                needRefresh = true;
                consultingFeeType = i;
                for (int k = 0; k < stringList.size(); k++) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (k == i) {
                        map.put("show", "true");
                    } else {
                        map.put("show", "false");
                    }
                    map.put("title", stringList.get(k).get("title"));
                    stringList.set(k, map);
                }
                tvAll.setText(strings[i]);
                adapter.replaceAll(stringList);
                popupWindowOrder.dismiss();
            }
        });
    }

    /**
     * 时间筛选
     */
    private void createPopupWindow() {
        for (int i = 0; i < stringTime.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", stringTime[i]);
            if (i == 0) {
                map.put("show", "true");
            } else {
                map.put("show", "false");
            }
            stringTimeList.add(map);
        }

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null, false);
        // 创建PopupWindow实例,宽度和高度充满
        popupWindowTime = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindowTime.setFocusable(true);
        // 设置允许在外点击消失
        popupWindowTime.setOutsideTouchable(true);
        //刷新状态（必须刷新否则无效）
        popupWindowTime.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindowTime.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindowTime.setAnimationStyle(R.style.AnimationFade);

        popupWindowTime.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (needRefresh) {
                    needRefresh = false;
                    baseAdapterUtils.onRefresh();
                }
                darkenBackground(1f);
                ivMonth.setImageResource(R.mipmap.icon_arrow_down);
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        ListView lv = (ListView) customView.findViewById(R.id.popview_listview);
        final QuickAdapter<Map<String, String>> adapter = new QuickAdapter<Map<String, String>>(this, R.layout.popupwindow_list_item, stringTimeList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> item, int position) {
                helper.setText(R.id.tv_list_item, item.get("title"));
                if (item.get("show").equals("true")) {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (timeRange == i) {
                    popupWindowTime.dismiss();
                    return;
                }
                needRefresh = true;
                timeRange = i;
                for (int k = 0; k < stringTimeList.size(); k++) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (k == i) {
                        map.put("show", "true");
                    } else {
                        map.put("show", "false");
                    }
                    map.put("title", stringTimeList.get(k).get("title"));
                    stringTimeList.set(k, map);
                }
                tvSelectMonth.setText(stringTime[i]);
                adapter.replaceAll(stringTimeList);
                popupWindowTime.dismiss();
            }
        });
    }

    /**
     * 改变背景颜色
     */
    private void darkenBackground(Float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @OnClick({R.id.title_bar_left, R.id.rl_all_type,R.id.rl_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.rl_all_type:
                ivAll.setImageResource(R.mipmap.icon_arrow_up);
                if (popupWindowOrder.isShowing()) {
                    popupWindowOrder.dismiss();
                }else {
                    popupWindowOrder.showAsDropDown(this.view);
                    darkenBackground(0.7f);
                }
                break;
            case R.id.rl_select:
                ivMonth.setImageResource(R.mipmap.icon_arrow_up);
                if (popupWindowTime.isShowing()) {
                    popupWindowTime.dismiss();
                } else {
                    popupWindowTime.showAsDropDown(this.view);
                    darkenBackground(0.7f);
                }
                break;
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, PayMentOrder payMent, int pos) {
        ImageView ivPayType = (ImageView)helper.getView().findViewById(R.id.iv_pay_type);
        if ("0".equals(payMent.getConsultingFeeType())){  //煤炭
            ivPayType.setImageResource(R.mipmap.pay_type_coal);
        }else{   //货运
            ivPayType.setImageResource(R.mipmap.pay_type_transport);
        }
        helper.setText(R.id.tv_payment_name,payMent.getCostName());
        helper.setText(R.id.tv_payment_end_time,payMent.getPayEndTime());

        TextView TV = (TextView)helper.getView().findViewById(R.id.tv_license_minute);
        if ("已失效".equals(payMent.getLicenseMinute())){
            TV.setText(payMent.getLicenseMinute());
            TV.setTextColor(getResources().getColor(R.color.gary));
        }else{
            if (payMent.getPayState().equals("5")){
                TV.setText("已退款");
            }else{
                TV.setText(payMent.getLicenseMinute());
            }
            TV.setTextColor(getResources().getColor(R.color.short_red));
        }
        helper.setText(R.id.tv_pay_num,(Double.valueOf(payMent.getConsultingFee())/100) + "");
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<PayMentOrder> mAdapter) {
        Intent intent = new Intent(this,PaymentRecordDetaileActivity.class);
        intent.putExtra("PayMentOrder",mAdapter.getItem(position-1));
        startActivity(intent);
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
}
