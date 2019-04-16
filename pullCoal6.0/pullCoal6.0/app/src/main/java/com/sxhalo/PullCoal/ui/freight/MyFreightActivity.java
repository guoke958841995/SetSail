package com.sxhalo.PullCoal.ui.freight;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.bean.RouteEntity;
import com.sxhalo.PullCoal.bean.TransportMode;
import com.sxhalo.PullCoal.common.Constant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.ui.freight.search.FreightContract;
import com.sxhalo.PullCoal.ui.freight.search.FreightPresenter;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.UIHelper;
import com.sxhalo.PullCoal.weight.AppEmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的订阅货运列表
 * Created by amoldZhang on 2019/4/14
 */
public class MyFreightActivity extends BaseActivity<FreightPresenter> implements FreightContract.View {


    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mRefreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.title)
    TextView title;

    private List<TransportMode> transports = new ArrayList<TransportMode>();
    private BaseQuickAdapter<TransportMode, BaseViewHolder> baseQuickAdapter;
    private int currentPage = 1;

    @Override
    public int getContentLayout() {
        return R.layout.activity_my_transport;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        title.setText("订阅货运");
        initView();
    }

    @Override
    public void initData() {
        getData(true);
    }

    private void getData(boolean flage) {
        RouteEntity data = (RouteEntity)getIntent().getSerializableExtra("RouteEntity");
        LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
        if (!data.getFromPlace().equals("0")){
            params.put("fromPlace", data.getFromPlace());
        }
        if (!data.getToPlace().equals("0")){
            params.put("toPlace", data.getToPlace());
        }
        params.put("freeInfor", "0");
        params.put("currentPage", currentPage + "");
        params.put("pageSize", "10");
        mPresenter.getCoalTransportList(this,params,flage);
    }

    @Override
    public void getCoalTransportList(List<TransportMode> transportModeList, Throwable e) {
        if (transportModeList == null) {
            // 网络连接错误
        } else {
            if (currentPage == 1){  //当是刷新时
                transports.clear();
                baseQuickAdapter.notifyDataSetChanged();
                transports.addAll(transportModeList);
                baseQuickAdapter.notifyDataSetChanged();
                if (baseQuickAdapter.getData().size() < 10){
                    mRefreshLayout.setNoMoreData(true);
                }else{
                    mRefreshLayout.setNoMoreData(false);
                }
            }else{  //当是加载更多时
                if (transportModeList.size() != 0){
                    int index = baseQuickAdapter.getData().size() + 1;
                    transports.addAll(transportModeList);
                    baseQuickAdapter.notifyItemInserted(index);
                }
                if (transportModeList.size() < 10){
                    mRefreshLayout.setNoMoreData(true);
                }
            }
        }
    }

    /**
     * RecyclerView 界面初始化
     */
    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        baseQuickAdapter = new BaseQuickAdapter<TransportMode, BaseViewHolder>(R.layout.view_freight_search_list_item, transports) {
            @Override
            protected void convert(BaseViewHolder helper, TransportMode item) {
                int position = helper.getLayoutPosition();
                setViewItem(helper, item, position);
            }
        };
        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onClickItem(adapter, position);
            }
        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currentPage = 1;
                getData(false);
                //刷新更多玩成
                mRefreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currentPage++;
                getData(false);
                //加载更多完成
                mRefreshLayout.finishLoadMore(2000/*,false*/);//传入false表示刷新失败
            }


        });


        AppEmptyView emptyView = new AppEmptyView(this, new AppEmptyView.ClickDoView() {
            @Override
            public void onClick() {
                currentPage = 1;
                getData(true);
            }
        });
        //添加空视图
        baseQuickAdapter.setEmptyView(emptyView.getEmptyView());
        mRecyclerView.setAdapter(baseQuickAdapter);
    }


    /**
     * 列表数据点击
     * @param mAdapter
     * @param position
     */
    private void onClickItem(BaseQuickAdapter mAdapter, int position) {
        try {
            String userId = PaperUtil.get("userId", "-1");
            if ("-1".equals(userId)) {
                //未登录点击跳转登录界面
                UIHelper.jumpActLogin(this);
            } else {
                TransportMode transportMode = (TransportMode)mAdapter.getItem(position);
                Intent intent = new Intent(this, TransportDetailActivity.class);
                intent.putExtra("waybillId", transportMode.getTransportId());//货运id
                if ("0".equals(transportMode.getConsultingFee()) || PaperUtil.get("coalSalesId", "-1").equals(transportMode.getCoalSalesId())) {
                    //免费信息或者已支付 可以直接查看详情
                    startActivity(intent);
                } else {
                    //不免费
                    //已登录 判断是否支付
                    if ("1".equals(transportMode.getIsPay())) {
                        //已支付 直接查看
                        startActivity(intent);
                    } else {
                        //未支付 弹框提示
                        showPayDialog(transportMode, "1");
                    }
                }
            }
        } catch (Exception e) {
            Log.i("煤炭列表点击", e.toString());
        }
    }

    /**
     * 列表数据赋值
     * @param helper
     * @param item
     */
    private void setViewItem(BaseViewHolder helper, TransportMode item,int position) {
        try {
            View view_line = helper.getView(R.id.layout_divider);
            if (position == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }
            // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
            String transportType = item.getTransportType();
            if ("0".equals(transportType)) {
                helper.getView(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
                helper.setText(R.id.freight_type, " / 煤炭");
            } else if ("1".equals(transportType)) {
                helper.getView(R.id.ll_surplus).setVisibility(View.GONE);
                helper.setText(R.id.freight_type, "    长期货运 / 煤炭");
            } else if ("2".equals(transportType)) {
                helper.setText(R.id.freight_type, " / 普货");

                helper.getView(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
            }
            helper.setText(R.id.pubilshTime, item.getDifferMinute());
            helper.setText(R.id.tv_source, item.getCompanyName());
            helper.setText(R.id.tv_transport_cost, item.getCost());
            helper.setText(R.id.tv_start_address, item.getFromPlace());
            helper.setText(R.id.tv_end_address, item.getToPlace());

            final String userId = PaperUtil.get("userId", "-1");
            helper.getView(R.id.iv_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("0".equals(item.getConsultingFee())) {
                        //免费信息或者已支付 可以直接打电话
                        callPhone(item);
                    } else {
                        //先判断是否登录
                        if ("-1".equals(userId)) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(getParent());
                        } else {
                            //已登录 判断是否支付
                            if ("1".equals(item.getIsPay())) {
                                //已支付 直接打电话
                                callPhone(item);
                            } else {
                                //未支付 弹框提示
                                showPayDialog(item, "1");
                            }
                        }
                    }
                }
            });
            /****************************信息费********************************/

            String payName;
            int payColor;
            //当资讯信息是免费的
            if ("0".equals(item.getConsultingFee())) {
                payName = "免费信息";
                payColor = R.color.blue;
                //免费信息 隐藏信息提示 显示货运标签
                helper.getView(R.id.ll_information_free).setVisibility(View.GONE);
                //设置货运标签
                TextView publishTag0 = (TextView) helper.getView(R.id.publishTag0);
                TextView publishTag1 = (TextView) helper.getView(R.id.publishTag1);
                TextView publishTag2 = (TextView) helper.getView(R.id.publishTag2);
                TextView publishTag3 = (TextView) helper.getView(R.id.publishTag3);
                String publishTag = item.getPublishTag() == "" ? null : item.getPublishTag();
                if (!StringUtils.isEmpty(publishTag)) {
                    String[] strings = publishTag.split(",");
                    int size = strings.length;
                    if (strings.length > 0) {
                        if (size >= 1 && !StringUtils.isEmpty(strings[0])) {
                            publishTag0.setVisibility(View.VISIBLE);
                            publishTag0.setText(strings[0]);
                        } else {
                            publishTag0.setVisibility(View.GONE);
                        }
                        if (size >= 2 && !StringUtils.isEmpty(strings[1])) {
                            publishTag1.setVisibility(View.VISIBLE);
                            publishTag1.setText(strings[1]);
                        } else {
                            publishTag1.setVisibility(View.GONE);
                        }
                        if (size >= 3 && !StringUtils.isEmpty(strings[2])) {
                            publishTag2.setVisibility(View.VISIBLE);
                            publishTag2.setText(strings[2]);
                        } else {
                            publishTag2.setVisibility(View.GONE);
                        }
                        if (size >= 4 && !StringUtils.isEmpty(strings[3])) {
                            publishTag3.setVisibility(View.VISIBLE);
                            publishTag3.setText(strings[3]);
                        } else {
                            publishTag3.setVisibility(View.GONE);
                        }
                    }
                } else {
                    publishTag0.setVisibility(View.GONE);
                    publishTag1.setVisibility(View.GONE);
                    publishTag2.setVisibility(View.GONE);
                    publishTag3.setVisibility(View.GONE);
                }
            } else {
                ImageView typeImage = (ImageView) helper.getView(R.id.free_type_image);
                TextView typeText = (TextView) helper.getView(R.id.free_type);
                //当前登录用户和发布人同属于一个信息部
                if (PaperUtil.get("coalSalesId", "-1").equals(item.getCoalSalesId())) {
                    payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                    typeText.setText("发布人：" + item.getPublishUser());
                    typeText.setTextColor(getResources().getColor(R.color.actionsheet_gray));
                } else if ("1".equals(item.getIsPay())) {  //0未支付，1已支付
                    //当资讯信息已经付过费
                    payName = "(已支付)收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_buyer_app);
                    typeText.setText(item.getLicenseMinute());
                    typeText.setTextColor(payColor);
                } else {
                    //当资讯信息尚未付费
                    payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    ((ImageView) helper.getView(R.id.free_type_image)).setImageResource(R.mipmap.icon_about_app);
                    typeText.setText(item.getLicenseMinute());
                    typeText.setTextColor(payColor);
                }

                //收费信息 隐藏货运标签 显示信息提示
                helper.getView(R.id.ll_information_free).setVisibility(View.VISIBLE);
                //隐藏货运标签
                helper.getView(R.id.publishTag0).setVisibility(View.GONE);
                helper.getView(R.id.publishTag1).setVisibility(View.GONE);
                helper.getView(R.id.publishTag2).setVisibility(View.GONE);
                helper.getView(R.id.publishTag3).setVisibility(View.GONE);
            }
            helper.setText(R.id.information_free, payName);
            ((TextView) helper.getView(R.id.information_free)).setTextColor(getResources().getColor(payColor));
        } catch (Exception e) {
            LogUtil.e("货运展示", "货运赋值错误");
        }
    }

    private void callPhone(TransportMode transportMode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", transportMode.getPublishUserPhone());
        map.put("callType", Constant.CALE_TYPE_FREIGHT);
        map.put("targetId", transportMode.getTransportId());
        UIHelper.showCollTel(this, map, true);
    }


    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }

}
