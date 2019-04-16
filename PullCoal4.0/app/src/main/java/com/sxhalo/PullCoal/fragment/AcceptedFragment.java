package com.sxhalo.PullCoal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.BuyCoalActivity;
import com.sxhalo.PullCoal.activity.CoalOrderDetailActivity;
import com.sxhalo.PullCoal.model.Orders;
import com.sxhalo.PullCoal.model.APPDataList;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liz on 2017/7/24.
 */

public class AcceptedFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<Orders> {

    @Bind(R.id.layout_no_order)
    RelativeLayout layoutNoData;
    @Bind(R.id.orders_list)
    SmoothListView ordersList;
    private BaseActivity myActivity;

    private List<Orders> orders = new ArrayList<Orders>();
    private int currentPage = 1;
    private BaseAdapterUtils baseAdapterUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = (BaseActivity) getActivity();
        initView();
        getDataPath();
    }

    public void getDataPath() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("orderState", "1"); //受理状态：0，未受理；1，同意受理
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(getActivity(),params).getUserCoalOrderList(new DataUtils.DataBack<APPDataList<Orders>>() {
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
                        (myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, ordersList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }


    private void initView() {
        baseAdapterUtils= new BaseAdapterUtils(myActivity, ordersList);
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
//        helper.setText(R.id.coal_name, orders.getCoalName());
//        helper.setText(R.id.coal_num, orders.getTradingVolume().replace(".00","") + "吨");
//        helper.setText(R.id.push_time, orders.getReservationTime());
//        helper.setText(R.id.orider_num, orders.getOrderNumber());
//        helper.setText(R.id.infor_dep, orders.getCompanyName());
//        String state =  orders.getOrderState(); //0，未受理；1，受理；2，拒绝；3，超期
//        TextView TV = (TextView)helper.getView().findViewById(R.id.order_state);
//        if (state.equals("0")){
//            state = "未受理";
//            TV.setTextColor(getResources().getColor(R.color.app_title_text_color));
//        }
//        if (state.equals("1")){
//            state = "已受理";
//            TV.setTextColor(getResources().getColor(R.color.app_title_text_color));
//        }
//        if (state.equals("2")){
//            state = "已拒绝";
//            TV.setTextColor(getResources().getColor(R.color.app_title_text_color));
//        }
//        if (state.equals("3")){
//            state = "已过期";
//            TV.setTextColor(getResources().getColor(R.color.actionsheet_gray));
//        }
//        helper.setText(R.id.order_state,state);
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<Orders> mAdapter) {
        Intent intent = new Intent();
        intent.setClass(myActivity,CoalOrderDetailActivity.class);
        intent.putExtra("orderNumber",mAdapter.getItem(position-1).getOrderNumber());
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

    @OnClick(R.id.btn_add)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                //跳转到煤炭列表
                startActivity(new Intent(myActivity, BuyCoalActivity.class));
//                getActivity().finish();
                break;

        }
    }
}
