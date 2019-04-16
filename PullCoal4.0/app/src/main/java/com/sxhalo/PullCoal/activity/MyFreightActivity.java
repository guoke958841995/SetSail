package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.RouteEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/** 订阅货运
 * Created by amoldZhang on 2017/8/22.
 */

public class MyFreightActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<TransportMode>{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.coal_message_list)
    SmoothListView coalMessageList;
    @Bind(R.id.listview_ementy)
    LinearLayout listviewEmenty;
    @Bind(R.id.listview_no_net)
    LinearLayout listviewNoNet;

    private List<TransportMode> transports = new ArrayList<TransportMode>();
    private BaseAdapterUtils baseAdapterUtils;
    private int currentPage = 1;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_transport);
    }

    @Override
    protected void initTitle() {
        title.setText("订阅货运");
        initData();
    }

    private void initData() {
        baseAdapterUtils = new BaseAdapterUtils(this, coalMessageList);
        baseAdapterUtils.setViewItemData(R.layout.freight_transport_item, transports);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    protected void getData() {
        setData(true);
    }

    private void setData(boolean flage) {
        try {
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
            new DataUtils(this,params).getCoalTransportList(new DataUtils.DataBack<APPDataList<TransportMode>>() {
                @Override
                public void getData(APPDataList<TransportMode> appDataList) {
                    if (appDataList == null){
                        return;
                    }
                    if (appDataList.getList() == null) {
                        transports = new ArrayList<TransportMode>();
                    } else {
                        transports = appDataList.getList();
                    }
                    baseAdapterUtils.refreshData(transports);
                    showEmptyView(baseAdapterUtils.getCount(), listviewEmenty, coalMessageList);
                }
            },flage);
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }


    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, final TransportMode item, int pos) {
        try {
            View view_line = helper.getView().findViewById(R.id.layout_divider);
            if (pos == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }
            // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
            String transportType = item.getTransportType();
            if (transportType.equals("0")) {
                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
                helper.setText(R.id.freight_type, " / 煤炭");
            } else if (transportType.equals("1")) {
                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.GONE);
                helper.setText(R.id.freight_type, "    长期货运 / 煤炭");
            } else if (transportType.equals("2")) {
                helper.setText(R.id.freight_type, " / 普货");

                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
            }
            helper.setText(R.id.pubilshTime, item.getDifferMinute());
            helper.setText(R.id.tv_source, item.getCompanyName());
            helper.setText(R.id.tv_transport_cost, item.getCost());
            helper.setText(R.id.tv_start_address, item.getFromPlace());
            helper.setText(R.id.tv_end_address, item.getToPlace());

            final String userId = SharedTools.getStringValue(mContext, "userId", "-1");
            helper.getView().findViewById(R.id.iv_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getConsultingFee().equals("0")) {
                        //免费信息或者已支付 可以直接打电话
                        callPhone(item);
                    } else {
                        //不免费 先判断是否登录
                        if (userId.equals("-1")) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(mContext, false);
                        } else {
                            //已登录 判断是否支付
                            if (item.getIsPay().equals("1")) {
                                //已支付 直接打电话
                                callPhone(item);
                            } else {
                                //未支付 弹框提示
                                ((BaseActivity)mContext).showPayDialog(item, "1");
                            }
                        }
                    }
                }
            });
            /****************************信息费********************************/

            String payName;
            int payColor;
            //当资讯信息是免费的
            if (item.getConsultingFee().equals("0")) {
                payName = "免费信息";
                payColor = R.color.blue;
                //免费信息 隐藏信息提示 显示货运标签
                helper.getView().findViewById(R.id.ll_information_free).setVisibility(View.GONE);
                //设置货运标签
                TextView publishTag0 = (TextView) helper.getView().findViewById(R.id.publishTag0);
                TextView publishTag1 = (TextView) helper.getView().findViewById(R.id.publishTag1);
                TextView publishTag2 = (TextView) helper.getView().findViewById(R.id.publishTag2);
                TextView publishTag3 = (TextView) helper.getView().findViewById(R.id.publishTag3);
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
                ImageView typeImage = (ImageView)helper.getView().findViewById(R.id.free_type_image);
                TextView typeText = (TextView)helper.getView().findViewById(R.id.free_type);
                //当前登录用户和发布人同属于一个信息部
                if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(item.getCoalSalesId())){
                    //当资讯信息尚未付费
                    payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                    typeText.setText("发布人："+ item.getPublishUser());
                    typeText.setTextColor(getResources().getColor(R.color.actionsheet_gray));
                }else if (item.getIsPay().equals("1")) {  //0未支付，1已支付
                    //当资讯信息已经付过费
                    payName = "(已支付)收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_buyer_app);
                    typeText.setText(item.getLicenseMinute());
                } else {
                    //当资讯信息尚未付费
                    payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_about_app);
                    typeText.setText(item.getLicenseMinute());
                }
                //收费信息 隐藏货运标签 显示信息提示
                helper.getView().findViewById(R.id.ll_information_free).setVisibility(View.VISIBLE);
                //隐藏货运标签
                helper.getView().findViewById(R.id.publishTag0).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag1).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag2).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag3).setVisibility(View.GONE);
            }
            helper.setText(R.id.information_free, payName);
            ((TextView) helper.getView().findViewById(R.id.information_free)).setTextColor(getResources().getColor(payColor));
            GHLog.i("货运展示", "货运赋值");
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    private void callPhone(TransportMode transportMode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", transportMode.getPublishUserPhone());
        map.put("callType", Constant.CALE_TYPE_FREIGHT);
        map.put("targetId", transportMode.getTransportId());
        UIHelper.showCollTel(mContext, map, true);
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<TransportMode> mAdapter) {
        try {
            TransportMode transportMode = mAdapter.getItem(position - 1);
            Intent intent = new Intent(this, TransportDetailActivity.class);
            intent.putExtra("waybillId", transportMode.getTransportId());//货运id
            String userId = SharedTools.getStringValue(this, "userId", "-1");
            if (userId.equals("-1")) {
                //未登录点击跳转登录界面
                UIHelper.jumpActLogin(mContext, false);
            } else {
            if (transportMode.getConsultingFee().equals("0") || SharedTools.getStringValue(this,"coalSalesId","-1").equals(transportMode.getCoalSalesId())) {
                //免费信息或者已支付 可以直接查看详情
                    startActivity(intent);
            } else {
                    //已登录 判断是否支付
                    if (transportMode.getIsPay().equals("1")) {
                        //已支付 直接查看
                        startActivity(intent);
                    } else {
                        //未支付 弹框提示
                        ((BaseActivity)this).showPayDialog(transportMode, "1");
                    }
                }
            }
        } catch (Exception e) {
            Log.i("煤炭列表点击", e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }

    }

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        setData(false);
    }

    @Override
    public void getOnLoadMore(int page) {
      this.currentPage = page;
        setData(false);
    }
}
