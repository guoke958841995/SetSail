package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransactionBean;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 流水界面
 * Created by liz on 2018/3/15.
 */

public class TransactionActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<TransactionBean> {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.listView)
    SmoothListView listView;
    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;

    private List<TransactionBean> transationBeanList = new ArrayList<>();
    private BaseAdapterUtils baseAdapterUtils;
    private int currentPage = 1;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_transaction);
    }

    @Override
    protected void initTitle() {
        title.setText("明细");
        baseAdapterUtils = new BaseAdapterUtils(this, listView);
        baseAdapterUtils.setViewItemData(R.layout.account_details_list_item, transationBeanList);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this,params).getUserTransactionList(new DataUtils.DataBack<APPDataList<TransactionBean>>() {
                @Override
                public void getData(APPDataList<TransactionBean> list) {
                    try {
                        if (list.getList() == null) {
                            transationBeanList = new ArrayList<>();
                        } else {
                            transationBeanList = list.getList();
                        }
                        baseAdapterUtils.refreshData(transationBeanList);
                        showEmptyView(baseAdapterUtils.getCount(), relativeLayout, listView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @OnClick({R.id.title_bar_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, TransactionBean data, int pos) {
        ImageView ivType = (ImageView)helper.getView().findViewById(R.id.type_image);
        String amount = "";
        //00、充值；01、支付；02、提现；03、内部调帐；04、原交易退款；05、原交易撤销；06、退款；07、红包
        int changeType = Integer.valueOf(data.getChangeType());
        switch (changeType){
            case 00:
//                helper.setText(R.id.account_details_type_name,"充值");
//                ivType.setImageResource(R.mipmap.icon_recharge);
                break;
            case 01:
                helper.setText(R.id.account_details_type_name,"支付");
                ivType.setImageResource(R.mipmap.icon_payment);
                break;
            case 02:
                helper.setText(R.id.account_details_type_name,"提现");
                ivType.setImageResource(R.mipmap.icon_put_forward);
                break;
            case 03:
                helper.setText(R.id.account_details_type_name,"内部调帐");
                break;
            case 04:
//                helper.setText(R.id.account_details_type_name,"原交易退款");
                helper.setText(R.id.account_details_type_name,"退款");
                ivType.setImageResource(R.mipmap.icon_refund);
                break;
            case 05:
                helper.setText(R.id.account_details_type_name,"原交易撤销");
                break;
            case 06:
                helper.setText(R.id.account_details_type_name,"退款");
                ivType.setImageResource(R.mipmap.icon_refund);
                break;
            case 07:
                helper.setText(R.id.account_details_type_name,"红包");
                ivType.setImageResource(R.mipmap.icon_readbox);
                break;
        }
        //类别 0、来帐；1、往帐
        if ("0".equals(data.getSeqFlag())){
            amount = "+ ￥";
        }else if ("1".equals(data.getSeqFlag())){
            amount = "- ￥";
        }
        helper.setText(R.id.account_details_type_num,amount + StringUtils.setNumLenth(Float.valueOf(data.getCashAmount())/100, 2));

        helper.setText(R.id.account_details_type_count_num,StringUtils.setNumLenth(Float.valueOf(data.getAmount())/100, 2));
        helper.setText(R.id.account_details_type_count,StringUtils.isEmpty(data.getMemo())?"":data.getMemo());

    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<TransactionBean> mAdapter) {

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
