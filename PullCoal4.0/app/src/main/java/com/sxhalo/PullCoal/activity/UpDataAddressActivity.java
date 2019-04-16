package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.UserAddress;
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
 * 地址管理界面
 * Created by amoldZhang on 2018/5/5.
 */

public class UpDataAddressActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<UserAddress>{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;
    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;
    @Bind(R.id.procurement_list)
    SmoothListView procurementList;

    private List<UserAddress> paymentList = new ArrayList<UserAddress>();
    private BaseAdapterUtils<UserAddress> baseAdapterUtils;
    private int currentPage = 1;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_updata_address);
    }

    @Override
    protected void initTitle() {
        title.setText("地址管理");
        mapType.setText("新增地址");
        mapType.setVisibility(View.VISIBLE);
        initView();
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils<UserAddress>(this,procurementList);
        baseAdapterUtils.setViewItemData(R.layout.updata_address_list_item, paymentList);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this, params).getUserAddressList(new DataUtils.DataBack<APPDataList<UserAddress>>() {
                @Override
                public void getData(APPDataList<UserAddress> appDataList) {
                    try {
                        if (appDataList == null) {
                            return;
                        }
                        if (appDataList.getList() == null) {
                            paymentList = new ArrayList<UserAddress>();
                        } else {
                            paymentList = appDataList.getList();
                        }
                        baseAdapterUtils.refreshData(paymentList);
                        showEmptyView(baseAdapterUtils.getCount(), layoutNoData, procurementList);
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    showEmptyView(baseAdapterUtils.getCount(), layoutNoData, procurementList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, final UserAddress userAddress, int pos) {
//        ImageView ivPayType = (ImageView)helper.getView().findViewById(R.id.iv_pay_type);
//        if ("0".equals(payMent.getConsultingFeeType())){  //煤炭
//            ivPayType.setImageResource(R.mipmap.pay_type_coal);
//        }else{   //货运
//            ivPayType.setImageResource(R.mipmap.pay_type_transport);
//        }
        helper.setText(R.id.tv_region_name,userAddress.getRegionName());
        helper.setText(R.id.tv_address_name,userAddress.getAddressName());

        helper.setText(R.id.tv_address,userAddress.getAddress());

        helper.setText(R.id.tv_contact_person,userAddress.getContactPerson());
        helper.setText(R.id.tv_contact_phone,userAddress.getContactPhone());

        helper.getView().findViewById(R.id.layout_updata_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpDataAddressActivity.this, AddAddressActivity.class);
                intent.putExtra("UserAddress",userAddress); //编辑
                startActivityForResult(intent, Constant.AREA_CODE);
            }
        });
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserAddress> mAdapter) {
        if (!"MyFragment".equals(getIntent().getStringExtra("Entity"))){
            Intent intent = new Intent();
            intent.putExtra("addressEntity", mAdapter.getItem(position - 1));
            setResult(RESULT_OK, intent);
            finish();
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

    @OnClick({R.id.title_bar_left, R.id.map_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                Intent intent = new Intent(this, AddAddressActivity.class);
                startActivityForResult(intent, Constant.AREA_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.AREA_CODE) {
            baseAdapterUtils.onRefresh();
        }
    }
}
