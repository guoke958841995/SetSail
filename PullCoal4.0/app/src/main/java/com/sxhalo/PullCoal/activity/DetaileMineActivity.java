package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderMineProductEssential;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2017/2/28.
 * 矿口详情
 */
public class DetaileMineActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<InformationDepartment> {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_bar_right_imageview)
    ImageView rightImage;
    @Bind(R.id.details_mine_list)
    SmoothListView detailsMineList;


    private HeaderMineProductEssential mineProductsESL;
    private List<InformationDepartment> mineProducts = new ArrayList<InformationDepartment>();
    //轮播图链接列表
    private List<Slide> adList = new ArrayList<Slide>();
    private MineMouth mineProductData;
    private String mineMouthId;
    private BaseAdapterUtils baseAdapterUtils;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_detaile);
    }

    @Override
    protected void initTitle() {
        title.setText("矿口详情");
        rightImage.setVisibility(View.VISIBLE);
        rightImage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_share));
    }

    @Override
    protected void getData() {
        try {
            mineMouthId = getIntent().getStringExtra("InfoDepartId");
            if (mineMouthId == null) {
                return;
            }
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("mineMouthId", mineMouthId);
            params.put("regionCode", SharedTools.getStringValue(this, "adCode",""));
            params.put("longitude",SharedTools.getStringValue(this, "longitude", ""));
            params.put("latitude",SharedTools.getStringValue(this, "latitude", ""));
            new DataUtils(this,params).getCompaniesInfo(new DataUtils.DataBack<MineMouth>() {
                @Override
                public void getData(MineMouth dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
                        DetaileMineActivity.this.mineProductData = dataList;
                        if (mineProductsESL == null){
                            initData();
                        }else{
                            if (dataList.getMineDynamic() != null){
                                mineProductsESL.refreshData(dataList);
                            }
                            baseAdapterUtils.refreshData(mineProductData.getInformationDepartmentList());
                            mineProducts = baseAdapterUtils.getListData();
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

    private void initData() {
        // 矿口基本信息
        mineProductsESL = new HeaderMineProductEssential(this);
        mineProductsESL.fillView(mineProductData, detailsMineList);

        mineProducts = mineProductData.getInformationDepartmentList();

//        if (mineProducts.size() == 0){
//            List<String> stringList = new ArrayList<String>();
//            stringList.add("相关信息部");
//            //间隔
//            headerView = new HeaderView(DetaileMineActivity.this);
//            headerView.fillView(stringList, detailsMineList);
//            headerView.setTitleColor(R.color.text_color_input);
//        }

        baseAdapterUtils = new BaseAdapterUtils<InformationDepartment>(this,detailsMineList);
        baseAdapterUtils.settingList(false,false);
        baseAdapterUtils.setViewItemData(R.layout.relative_information_department_item, mineProducts);
        baseAdapterUtils.setBaseAdapterBack(this);
    }


    @OnClick({R.id.title_bar_left,R.id.title_bar_right_imageview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
//                String  TYPE = mContext.getIntent().getStringExtra("TYPE") == null ? "0" : mContext.getIntent().getStringExtra("TYPE");
//                if (!TYPE.equals("0")){
//                    mContext.setResult(RESULT_OK);
//                }
                finish();
                break;
            case R.id.title_bar_right_imageview:
                String title = mineProductData.getMineMouthName();
                // TODO 需要实现分享
                String targetUrl = new Config().getSHARE_RELEASE_COMP() + "?mineMouthId=" + mineMouthId;
                String summary = mineProductData.getMineMouthName() + "  地址：" + mineProductData.getAddress();
                if(BaseUtils.isNetworkConnected(getApplicationContext())){
                    shareDailog(title,targetUrl,summary);
                }else{
                    displayToast(getString(R.string.unable_net_work));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200){
            baseAdapterUtils.onRefresh();
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, InformationDepartment item, int pos) {
        try {
            helper.setText(R.id.tv_inormation_name, item.getCompanyName());
            helper.setText(R.id.tv_coal_num, item.getGoodsTotal() + "个");
            helper.setText(R.id.tv_freight_size, item.getTransTotal() + "个");

            ResetRatingBar recommend_ratingBar = (ResetRatingBar) helper.getView().findViewById(R.id.reset_rating_bar);
            recommend_ratingBar.setStar(Integer.valueOf(StringUtils.isEmpty(item.getCreditRating())?"1":item.getCreditRating()));
            ImageView mineBusinessType = (ImageView)helper.getView().findViewById(R.id.iv_status);
            TextView doBusinessTime = (TextView)helper.getView().findViewById(R.id.tv_time);
            //1（营业）、2（停业）、3（关闭）、4（筹建）、5（其他）',
            if (item.getOperatingStatus().equals("1")){
                mineBusinessType.setImageResource(R.mipmap.do_business_status);
                mineBusinessType.setVisibility(View.VISIBLE);
                doBusinessTime.setVisibility(View.GONE);
            }else if (item.getOperatingStatus().equals("2")){
                mineBusinessType.setImageResource(R.mipmap.out_of_business_status);
                mineBusinessType.setVisibility(View.VISIBLE);
                doBusinessTime.setVisibility(View.VISIBLE);
                String time = StringUtils.isEmpty(item.getReportEndDate())? "" : DateUtil.strToStrType(item.getReportEndDate(),"MM-dd").replace("-",".");
                doBusinessTime.setText("预计"+time+"营业");
            } else {
                mineBusinessType.setVisibility(View.GONE);
                doBusinessTime.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            GHLog.e("信息部展示", e.toString());
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<InformationDepartment> mAdapter) {
        if (mineProducts.size() != 0){
//            if (adList.size() != 0){
//                position = position - 4;
//            }else{
//                position = position - 3;
//            }
            position = position - 2;
            Intent intent = new Intent();
            GHLog.i("信息部展示", position + "被点击");
            intent.setClass(DetaileMineActivity.this, DetaileInformationDepartmentActivity.class);
            String inforDepartId = mAdapter.getItem(position).getCoalSalesId();
            intent.putExtra("InfoDepartId", inforDepartId);
            startActivity(intent);
        }
    }

    @Override
    public void getOnRefresh(int page) {
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        getData();
    }
}
