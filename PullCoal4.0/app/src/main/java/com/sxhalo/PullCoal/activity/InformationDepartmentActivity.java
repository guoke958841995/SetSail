package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 关注的信息部列表
 * Created by amoldZhang on 2016/12/20.
 */
public class InformationDepartmentActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<InformationDepartment>{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.my_freight_list)
    SmoothListView homeRecommendLv;
    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;

    private int currentPage = 0;
    //信息部列表
    private List<InformationDepartment> inforDepartmentList = new ArrayList<InformationDepartment>();

    private BaseAdapterUtils<InformationDepartment> baseAdapterUtils;
    public static int IF_Refresh = 201 ;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information_department);
    }

    @Override
    protected void initTitle() {
        title.setText("信息部");
        initView();
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this,params).getUserCoalSalesList(new DataUtils.DataBack<APPDataList<InformationDepartment>>() {
                @Override
                public void getData(APPDataList<InformationDepartment> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        if (dataMemager.getList() != null) {
                            baseAdapterUtils.refreshData(dataMemager.getList());
                            inforDepartmentList = baseAdapterUtils.getListData();
                        }
                        showEmptyView(baseAdapterUtils.getCount(), relativeLayout, homeRecommendLv);
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
        baseAdapterUtils = new BaseAdapterUtils<InformationDepartment>(this,homeRecommendLv);
        baseAdapterUtils.setViewItemData(R.layout.home_recommend_item, inforDepartmentList);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @OnClick({R.id.title_bar_left, R.id.title_bar_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, InformationDepartment item, int pos) {
        RelativeLayout relativeLayout = (RelativeLayout) helper.getView().findViewById(R.id.recommend_infoDeparts_rl);
        relativeLayout.setVisibility(View.VISIBLE);
        helper.setText(R.id.recommend_name, item.getCompanyName());
        helper.setText(R.id.recommend_postal_address_v, item.getAddress());
        ResetRatingBar recommend_ratingBar = (ResetRatingBar) helper.getView().findViewById(R.id.recommend_room_ratingbar1);
        recommend_ratingBar.setStar(Integer.valueOf(Integer.valueOf(StringUtils.isEmpty(item.getCreditRating()) ? "1" : item.getCreditRating())));
        ImageView iv = (ImageView)helper.getView().findViewById(R.id.recommend_IV);
        getImageManager().loadMinebgUrlImage(item.getCoalSalePic(),iv);

        helper.setText(R.id.coal_size, item.getGoodsTotal()+"个");
        helper.setText(R.id.freight_size, item.getTransTotal() + "个");
        ImageView mineBusinessType = (ImageView)helper.getView().findViewById(R.id.recommend_business_type);
        TextView doBusinessTime = (TextView)helper.getView().findViewById(R.id.recommend_do_business_time);
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

//        //是否可派车 0 不可派车
//        if ("0".equals(item.getProvideTransport())){
//            helper.getView().findViewById(R.id.information_department_send_car).setVisibility(View.GONE);
//        }else{
//            helper.getView().findViewById(R.id.information_department_send_car).setVisibility(View.VISIBLE);
//        }

        String typeId = item.getTypeId();
        Dictionary sys100003 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100003"}).get(0);
        String carModeName = "";
        for (FilterEntity filterEntity : sys100003.list) {
            if (filterEntity.dictCode.equals(typeId)) {
                carModeName = filterEntity.dictValue;
                break;
            }
        }
        helper.setText(R.id.recommend_type, "  " + carModeName);
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<InformationDepartment> mAdapter) {
        Intent intent = new Intent();
        GHLog.i("信息部展示", position + "被点击");
        intent.setClass(InformationDepartmentActivity.this, DetaileInformationDepartmentActivity.class);
        String inforDepartId = mAdapter.getItem(position - 1).getCoalSalesId();
        intent.putExtra("InfoDepartId", inforDepartId);
        startActivityForResult(intent,IF_Refresh);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 201){
            baseAdapterUtils.onRefresh();
        }
    }
}
