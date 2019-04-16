package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的货运发布列表
 * Created by amoldZhang on 2018/8/14.
 */

public class MyTransportReleaseListActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<TransportMode>{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;
    @Bind(R.id.freight_list)
    SmoothListView freightList;

    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;

    private int currentPage = 1;

    private List<TransportMode> transports = new ArrayList<TransportMode>();
    private BaseAdapterUtils baseAdapterUtils;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_transport_release_list);
    }

    @Override
    protected void initTitle() {
        title.setText("货运发布");
        mapType.setText("发布");
        mapType.setVisibility(View.VISIBLE);
        initView();
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(this, freightList);
        baseAdapterUtils.setViewItemData(R.layout.my_freight_transport_item, transports);
        baseAdapterUtils.setBaseAdapterBack(this);

    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            GHLog.i("当前获取数据页数", "currentPage = " + currentPage);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(mContext, params).getUserTransportList(new DataUtils.DataBack<APPDataList<TransportMode>>() {
                @Override
                public void getData(APPDataList<TransportMode> appDataList) {
                    if (appDataList == null) {
                        return;
                    }
                    if (appDataList.getList() == null) {
                        transports = new ArrayList<TransportMode>();
                    } else {
                        transports = appDataList.getList();
                    }
                    baseAdapterUtils.refreshData(transports);
                    ((BaseActivity) mContext).showEmptyView(baseAdapterUtils.getCount(), relativeLayout, freightList);
                }

                @Override
                public void getError(Throwable e) {
                    GHLog.e("联网失败", e.toString());
                    ((BaseActivity) mContext).showEmptyView(baseAdapterUtils.getCount(), relativeLayout, freightList);
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("货运列表联网", e.toString());
        }
    }



    @OnClick({R.id.title_bar_left, R.id.map_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                //联网获取认证状态
                getSelfAuthentication();
                break;
        }
    }

    private void getSelfAuthentication() {
        try {
            new DataUtils(this, new LinkedHashMap<String, String>()).getUserRealnameAuthInfo(new DataUtils.DataBack<UserAuthenticationEntity>() {
                @Override
                public void getData(UserAuthenticationEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        checkStatus(StringUtils.isEmpty(dataMemager.getAuthState()) ? "100" : dataMemager.getAuthState());
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("煤种赋值", e.toString());
        }

    }

    /**
     * 校验用户状态 只有实名用户才可以委托货运
     */
    private void checkStatus(String cerrtificationState) {
        //0审核中1审核成功2审核失败 100未提交
        switch (Integer.valueOf(cerrtificationState)) {
            case 100: //未提交
                showDaiLog(this, getString(R.string.unable_real_name_the_authentication));
                break;
            case 0: //审核中
                displayToast(getString(R.string.under_review));
                break;
            case 1: //审核成功
                //不是自己发布的  进入的下订单界面
                startActivityForResult(new Intent(this, MyTransportReleaseActivity.class), Constant.JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT);
                break;
            case 2: //审核失败
                showDaiLog(this, getString(R.string.submit_under_review_failed));
                break;
        }
    }

    private void showDaiLog(Activity mActivity, String message) {
        new RLAlertDialog(mActivity, "系统提示", message, "关闭",
                "立刻前往", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                startActivity(new Intent(MyTransportReleaseListActivity.this, BuyerCertificationActivity.class));
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT) {
            baseAdapterUtils.onRefresh();
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, TransportMode transportMode, int pos) {
        // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
        String transportType = transportMode.getTransportType();
        if (transportType.equals("0")) {
            helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_surplus, transportMode.getSurplusNum());
            helper.setText(R.id.freight_type, " / 煤炭");
        } else if (transportType.equals("1")) {
            helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.GONE);
            helper.setText(R.id.freight_type, "    长期货运 / 煤炭");
        } else if (transportType.equals("2")) {
            helper.setText(R.id.freight_type, " / 普货");

            helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_surplus, transportMode.getSurplusNum());
        }
        helper.setText(R.id.my_freight_time, transportMode.getDifferMinute());

        helper.setText(R.id.my_freight_from_adss, transportMode.getFromPlace());
        helper.setText(R.id.my_freight_to_adss, transportMode.getToPlace());
        helper.setText(R.id.tv_transport_cost, transportMode.getCost());
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<TransportMode> mAdapter) {
        try {
           String userId = SharedTools.getStringValue(mContext, "userId", "-1");
            if (userId.equals("-1")) {
                //未登录点击跳转登录界面
                UIHelper.jumpActLogin(mContext, false);
            } else {
                Intent intent = new Intent(mContext, TransportDetailActivity.class);
                intent.putExtra("waybillId", mAdapter.getItem(position - 1).getTransportId());//货运id
                startActivity(intent);
            }

        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            Log.i("煤炭列表点击", e.toString());
        }
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
