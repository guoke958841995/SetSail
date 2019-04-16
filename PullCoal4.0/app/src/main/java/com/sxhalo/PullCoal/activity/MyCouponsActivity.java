package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.CouponsEntity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
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
 * 我的代金券
 * Created by liz on 2018/1/6.
 */

public class MyCouponsActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<CouponsEntity>{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.listView)
    SmoothListView listView;

    @Bind(R.id.btn_bottom)
    Button btnBottom;
    @Bind(R.id.tv_coupons)
    TextView tvCoupons;
    @Bind(R.id.no_data_view)
    LinearLayout noDataView;

    private BaseAdapterUtils baseAdapterUtils;

    private List<CouponsEntity> myCoupons = new ArrayList<>();

    private int currentPage = 1;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_coupons);
    }

    @Override
    protected void initTitle() {
        title.setText("我的代金券");
        initView();
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(this, listView);
        baseAdapterUtils.settingList(true, false);
        baseAdapterUtils.setViewItemData(R.layout.my_coupons_item, myCoupons);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentPage == 1){
            initData();
        }else{
            baseAdapterUtils.onRefresh();
        }
    }

    private void initData(){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "-1");
            new DataUtils(this,params).getUserCouponList(new DataUtils.DataBack<APPData<CouponsEntity>>() {

                @Override
                public void getData(APPData<CouponsEntity> couponsEntity) {
                    try {
                        if ("0".equals(couponsEntity.getAvailableQuantity())){
                            btnBottom.setVisibility(View.GONE);
                        }else{
                            btnBottom.setVisibility(View.VISIBLE);
                        }
                        if (couponsEntity.getList().size() != 0){
                            tvCoupons.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                            noDataView.setVisibility(View.GONE);
                            baseAdapterUtils.refreshData(couponsEntity.getList());
                            myCoupons = baseAdapterUtils.getListData();
                        }else{
                            listView.setVisibility(View.GONE);
                            tvCoupons.setVisibility(View.GONE);
                            noDataView.setVisibility(View.VISIBLE);
                        }
                        listView.setLoadMoreEnable(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接失败，请稍候再试！");
                }
            });
        } catch (Exception e) {
            GHLog.e("我的代金券",e.toString());
        }
    }

    @OnClick({R.id.title_bar_left, R.id.tv_rules, R.id.btn_bottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.tv_rules:
                //使用规则跳转
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("URL", new Config().getCOUPONS_RULES());
                intent.putExtra("title", "代金券的类型和规则");
                startActivity(intent);
                break;
            case R.id.btn_bottom:
                // 获取更多优惠券
                UIHelper.jumpAct(this, GetMoreCouponsActivity.class, false);
                break;
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, CouponsEntity couponsEntity, int pos) {
        helper.setText(R.id.tv_title, couponsEntity.getCouponName());
        helper.setText(R.id.tv_time, couponsEntity.getPeriodValidity());
        helper.setText(R.id.tv_face_value, (Double.valueOf(couponsEntity.getDenomination()) / 100 ) + "");
        helper.setText(R.id.tv_tips, couponsEntity.getUsingRange());
        helper.setText(R.id.tv_number, couponsEntity.getNumber() + "张");
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<CouponsEntity> mAdapter) {
//        displayToast("点击" + position);
    }

    @Override
    public void getOnRefresh(int page) {
        currentPage = page;
        initData();
    }

    @Override
    public void getOnLoadMore(int page) {
        currentPage = page;
        initData();
    }
}
