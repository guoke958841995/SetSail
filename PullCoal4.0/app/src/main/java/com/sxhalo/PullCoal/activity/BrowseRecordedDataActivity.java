package com.sxhalo.PullCoal.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.ListCoalEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.addrightview.LayoutContrastView;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullFooterLayout;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullHeadViewLayout;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.AREA_CODE;
import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

/**
 * 浏览记录列表
 * Created by amoldZhang on 2018/12/13.
 */

public class BrowseRecordedDataActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.pull_to_refresh_recycler_view)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;

    @Bind(R.id.ll_contrast)
    LayoutContrastView llContrast;

    private RecyclerView myRecyclerView;
    private String groupType;
    private int currentPage = 1;
    private CommonAdapter<Coal> myAdapter;
    private List<Coal> myListData = new ArrayList<Coal>();
    private MyReceiver myReceiver;

    private String upData = "-1";

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_browse_recorde_data);
    }

    @Override
    protected void initTitle() {
        groupType = getIntent().getStringExtra("groupType");
        title.setText(groupType + "过的煤炭记录");
        initView();
    }

    private void initView() {
        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshRecyclerView.setHeaderLayout(new PullHeadViewLayout(this));
        pullToRefreshRecyclerView.setFooterLayout(new PullFooterLayout(this));

        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage = 1;
                        getDataType(false);
                    }
                },1000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage ++ ;
                        getDataType(false);
                    }
                }, 1500);
            }
        });

        myRecyclerView = pullToRefreshRecyclerView.getRefreshableView();
        myRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

//        myAdapter = new CommonAdapter<Coal>(mContext,R.layout.layout_item_coal_buy,myListData){
        myAdapter = new CommonAdapter<Coal>(mContext,R.layout.fragment_coal_list_item,myListData){
            @Override
            protected void convert(ViewHolder holder, Coal coal, int position) {
//                holder.getView(R.id.layout_mode_2).setVisibility(View.VISIBLE);
//                holder.getView(R.id.layout_mode_1).setVisibility(View.GONE);

                setModeCoalInitItem(holder,coal,position);
            }
        };

        myAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Coal coal = myAdapter.getDatas().get(position);
                onClockItem(coal);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        myRecyclerView.setAdapter(myAdapter);

        llContrast.initView(new LayoutContrastView.CoalBack(){
            @Override
            public void setLayout(boolean flage){
                if (flage) {
                    llContrast.setVisibility(View.VISIBLE);
                }else {
                    llContrast.setVisibility(View.GONE);
                }
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 煤炭条目点击
     * @param coal
     */
    private void onClockItem(Coal coal){
        Intent intent = new Intent();
        intent.setClass(mContext, DetailsWebActivity.class);
        intent.putExtra("Coal", coal);
        intent.putExtra("inforDepartId", "煤炭详情");
        String userId = SharedTools.getStringValue(mContext, "userId", "-1");
        if (userId.equals("-1")) {
            //未登录 先去登录
            UIHelper.jumpActLogin(mContext,false);
        } else {
            if (coal.getConsultingFee().equals("0") ||
                    SharedTools.getStringValue(mContext, "coalSalesId", "-1").equals(coal.getCoalSalesId())) {

                startActivityForResult(intent, Constant.AREA_CODE_COAL_DETAILS);
            } else {
                //已登录 判断是否支付
                if (coal.getIsPay().equals("1") && coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                    //已支付 直接查看
                    startActivity(intent);
                } else {
                    //未支付 弹框提示
                    showPayDialog(coal, "0");
                }
            }
        }
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_ACTION);
        registerReceiver(myReceiver, filter);
    }

    /**
     *
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RECEIVED_ACTION.equals(intent.getAction()) && intent != null) {
                try {
                    GHLog.e("MyReceiver", "在线买煤数据刷新");
                    getDataType(true);
                } catch (Exception e) {
                    GHLog.e("MyReceiver", e.toString());
                }
            }
        }
    }

    /**
     * 初始化煤炭模块列表模式
     * @param helper
     * @param coals
     * @param position
     */
    private void setModeCoalInitItem(ViewHolder helper,final Coal coals,final int position) {
        try {
            if (Utils.isImagesTrue(coals.getImageUrl()) == 200 ){
                getImageManager().loadCoalTypeUrlImage(coals.getImageUrl(),coals.getCategoryImage(),(ImageView)helper.getView(R.id.iv_com_head));
            }else{
                getImageManager().loadCoalTypeUrlImage(coals.getCategoryImage(),(ImageView)helper.getView(R.id.iv_com_head));
            }
            helper.setText(R.id.coal_item_name, coals.getCoalName());
            helper.setText(R.id.coal_price, coals.getOneQuote());
            helper.setText(R.id.tv_com_calorificvalue, coals.getCalorificValue() + "kCal/kg");
            helper.setText(R.id.tv_com_storagerate, coals.getMineMouthName());
            helper.setText(R.id.information_name, coals.getCompanyName());
            if (StringUtils.isEmpty(coals.getCoalReportPicUrl())) {
                helper.getView(R.id.iv_test).setVisibility(View.GONE);
            } else {
                helper.getView(R.id.iv_test).setVisibility(View.VISIBLE);
            }
            helper.setText(R.id.push_time, coals.getDifferMinute());
            String payName;
            int payColor;
            //当资讯信息是免费的
            if (coals.getConsultingFee().equals("0")) {
                payName = "免费信息";
                payColor = R.color.blue;
                helper.getView(R.id.coal_price).setVisibility(View.VISIBLE);
                helper.getView(R.id.coal_price_image).setVisibility(View.VISIBLE);
                helper.getView(R.id.free_type).setVisibility(View.GONE);
                helper.getView(R.id.free_type_image).setVisibility(View.GONE);
            } else {
                ImageView typeImage = (ImageView)helper.getView(R.id.free_type_image);
                TextView typeText = (TextView)helper.getView(R.id.free_type);
                typeImage.setVisibility(View.VISIBLE);
                typeText.setVisibility(View.VISIBLE);

                if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())){
                    payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    helper.getView(R.id.coal_price).setVisibility(View.GONE);
                    helper.getView(R.id.coal_price_image).setVisibility(View.GONE);
                    typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                    typeText.setText("发布人："+ coals.getPublishUser());
                }else
                    //0未支付，1已支付
                    if (coals.getIsPay().equals("1")&& coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {
                        //当资讯信息已经付过费
                        payName = "(已支付)收费信息";
                        payColor = R.color.actionsheet_red;
                        helper.getView(R.id.coal_price).setVisibility(View.VISIBLE);
                        helper.getView(R.id.coal_price_image).setVisibility(View.VISIBLE);
                        typeImage.setImageResource(R.mipmap.icon_buyer_app);
                        typeText.setText(coals.getLicenseMinute());
                    } else {
                        //当资讯信息尚未付费
                        payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + "收费信息";
                        payColor = R.color.actionsheet_red;
                        helper.getView(R.id.coal_price).setVisibility(View.GONE);
                        helper.getView(R.id.coal_price_image).setVisibility(View.GONE);
                        typeImage.setImageResource(R.mipmap.icon_about_app);
                        if (coals.getLicenseMinute().contains("已失效")){
                            typeText.setText("支付后可查看更多");
                        }else{
                            typeText.setText(coals.getLicenseMinute().contains("已失效")?"支付后可查看更多":coals.getLicenseMinute());
                        }
                    }
            }
            helper.setText(R.id.information_free, payName);
            ((TextView) helper.getView(R.id.information_free)).setTextColor(getResources().getColor(payColor));
            helper.setText(R.id.coal_price, coals.getOneQuote());


            TextView contrastText = (TextView)helper.getView(R.id.contrast_text);
            contrastText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                    //先判断是否登录
                    if (userId.equals("-1")) {
                        //未登录 先去登录
                        UIHelper.jumpActLogin(mContext,false);
                    } else {
                        if (coals.getConsultingFee().equals("0") ||
                                SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())) {
                            //免费信息或者已支付 直接对比
                            llContrast.setAddContrastView(coals);
                        } else {
                            //已登录 判断是否支付
                            if (coals.getIsPay().equals("1")&& coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {
                                //已支付 直接查看
                                //免费信息或者已支付 直接对比
                                llContrast.setAddContrastView(coals);
                            } else {
                                //未支付 弹框提示
                                showPayDialog(coals, "0");
                            }
                        }
                    }
                }
            });

            if (llContrast.getifContrast(coals)){
                contrastText.setBackgroundResource(R.drawable.button_shape_pressed);
                contrastText.setTextColor(getResources().getColor(R.color.white));
            }else{
                contrastText.setBackgroundResource(R.drawable.bull_send_car);
                contrastText.setTextColor(getResources().getColor(R.color.actionsheet_blue));
            }
        } catch (Exception e) {
            GHLog.e("煤炭赋值", e.toString());
        }
    }

    @Override
    protected void getData(){
        getDataType(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.AREA_CODE_COAL_DETAILS:
                    if (data != null && "1".equals(data.getStringExtra("upData"))){
                        upData = "1";
                        getDataType(true);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (llContrast != null){
            llContrast.setContrastLayout();
        }
    }

    private void getDataType(boolean flage){
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        // type（必填）1 收藏列表  2预订列表  3浏览列表
        if ("浏览".equals(groupType)){
            params.put("type", "3");
        }else if ("收藏".equals(groupType)){
            params.put("type", "1");
        }else{ //预订
            params.put("type", "2");
        }
        params.put("currentPage", currentPage + "");
        params.put("pageSize", "10");
        new DataUtils(this,params).getCoalGoodsCollectList(new DataUtils.DataBack<APPData<Coal>>() {

            @Override
            public void getData(APPData<Coal> appData) {
                if (currentPage == 1){
                    refreshData(appData.getList());
                }else{
                    loadData(appData.getList());
                }
                myAdapter.notifyDataSetChanged();
                pullToRefreshRecyclerView.onRefreshComplete();
                showEmptyView(myAdapter.getDatas().size(), layoutNoData, pullToRefreshRecyclerView);
            }

            @Override
            public void getError(Throwable e) {
                displayToast("网络连接失败，请稍后再试！");
                showEmptyView(0, layoutNoData, pullToRefreshRecyclerView);
            }
        }, flage);
    }


    /**
     * 界面数据刷新
     * @param entityList
     */
    private void refreshData(List<Coal> entityList) {
        myListData.clear();
        myRecyclerView.getAdapter().notifyDataSetChanged();
        myListData.addAll(entityList);
    }

    private void loadData(List<Coal> entityList) {
        try {
            if (entityList.size() != 0){
                int index = myListData.size() + 1;
                myListData.addAll(entityList);
                myRecyclerView.getAdapter().notifyItemInserted(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("upData",upData);
        setResult(RESULT_OK,intent);
        finish();
    }

}
