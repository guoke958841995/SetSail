package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的货运单
 */
public class MyTransportOrdersListActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<TransportMode>{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.my_transport_orders_list)
    SmoothListView smoothListView;
    @Bind(R.id.layout_no_transport_order)
    RelativeLayout relativeLayout;
    private List<TransportMode> transportModeList = new ArrayList<TransportMode>();

    private int currentPage = 1;
    public static final int REFRESH = 119;

    private BaseAdapterUtils baseAdapterUtils;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_transport_orders_list);
    }

    @Override
    protected void initTitle() {
        title.setText("我的货运单");
        initView();
    }

    @Override
    protected void getData() {
        getDataPath();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        page = 0;
//
//    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(mContext, smoothListView);
        baseAdapterUtils.setViewItemData(R.layout.my_transport_orders_item, transportModeList);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    public void getDataPath() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this,params).getUserTransportOrderList(new DataUtils.DataBack<APPDataList<TransportMode>>() {
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
                        showEmptyView(baseAdapterUtils.getCount(), relativeLayout, smoothListView);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REFRESH:
                    baseAdapterUtils.onRefresh();
                    break;
            }
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, TransportMode transportMode, int pos) {
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
                helper.setText(R.id.tv_time, transportMode.getCreateTime());
                helper.setTextColor(R.id.tv_time, getResources().getColor(R.color.actionsheet_blue));

                helper.setImageDrawable(R.id.iv_status, getResources().getDrawable(R.drawable.icon_time_line_blue));
                helper.setTextColor(R.id.tv_status,getResources().getColor(R.color.actionsheet_blue) );
                break;
            case 1: //同意
                //审核通过
                helper.setText(R.id.tv_status, "进行中");
                helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.actionsheet_blue));

                helper.setText(R.id.tv_time, transportMode.getCreateTime());
                helper.setTextColor(R.id.tv_time, getResources().getColor(R.color.actionsheet_blue));
                helper.setImageDrawable(R.id.iv_status, getResources().getDrawable(R.drawable.icon_time_line_blue));
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
                helper.setTextColor(R.id.tv_time, getResources().getColor(R.color.gray));
                helper.setText(R.id.tv_time, transportMode.getCreateTime());
                helper.setImageDrawable(R.id.iv_status, getResources().getDrawable(R.drawable.icon_timer_shaft_history));
                break;
            case 3:  //司机确认拉货
                //已完成
                helper.setText(R.id.tv_status, "已完成");
                helper.setTextColor(R.id.tv_status,getResources().getColor( R.color.dark_yellow));

                helper.setTextColor(R.id.tv_time, getResources().getColor(R.color.gray));
                helper.setText(R.id.tv_time, transportMode.getCreateTime());
                helper.setImageDrawable(R.id.iv_status, getResources().getDrawable(R.drawable.icon_timer_shaft_history));
                break;
            case 4: //信息部确认司机到达
                //已完成
                helper.setText(R.id.tv_status, "已完成");
                helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.dark_yellow));

                helper.setTextColor(R.id.tv_time, getResources().getColor(R.color.gray));
                helper.setText(R.id.tv_time, transportMode.getCreateTime());
                helper.setImageDrawable(R.id.iv_status, getResources().getDrawable(R.drawable.icon_timer_shaft_history));
                break;
            case 100: //取消
                //已取消
                helper.setText(R.id.tv_status, "已取消");
                helper.setTextColor(R.id.tv_status, R.color.gray);

                helper.setTextColor(R.id.tv_time, R.color.gray);
                helper.setText(R.id.tv_time, transportMode.getCreateTime());
                helper.setImageDrawable(R.id.iv_status, getResources().getDrawable(R.drawable.icon_timer_shaft_history));
                break;
        }
        helper.setText(R.id.tv_my_freight_order_num, transportMode.getTransportOrderCode());
        helper.setText(R.id.tv_start, transportMode.getFromPlace());
        helper.setText(R.id.tv_destination, transportMode.getToPlace());
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<TransportMode> mAdapter) {
        Intent intent = new Intent(MyTransportOrdersListActivity.this, MyTransportOrderDetailActivity.class);
        intent.putExtra("waybilNumber",mAdapter.getItem(position - 1).getTransportOrderCode());
        startActivityForResult(intent, REFRESH);
    }

    @Override
    public void getOnRefresh(int page) {
        MyTransportOrdersListActivity.this.currentPage = page;
        getDataPath();
    }

    @Override
    public void getOnLoadMore(int page) {
        MyTransportOrdersListActivity.this.currentPage = page;
        getDataPath();
    }

    @OnClick({R.id.title_bar_left, R.id.btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.btn_add:
                //跳转到货运列表
                setResult(RESULT_OK, null);
                finish();
                break;
        }
    }
}
