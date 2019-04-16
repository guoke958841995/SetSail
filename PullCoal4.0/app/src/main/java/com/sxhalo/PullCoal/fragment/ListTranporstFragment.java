package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.activity.BuyCoalActivity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.ui.pullrecyclerview.CustomLondMoreButtomView;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullFooterLayout;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.TransportDetailActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.scrolllayout.content.CustomViewPager;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import butterknife.ButterKnife;

import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

/**
 * Created by amoldZhang on 2018/11/13.
 */
public class ListTranporstFragment extends BaseFragment {

    @Bind(R.id.freight_list)
    RecyclerView mRecyclerView;
//    @Bind(R.id.listview_no_data)
//    LinearLayout listviewEmenty;
//    @Bind(R.id.listview_no_net)
//    LinearLayout listviewNoNet;


    private List<TransportMode> freightList = new ArrayList<TransportMode>();
    private int currentPage = 1;
    private Activity myActivity;

    private MyReceiver myReceiver;
    private TransportMode stayTransportMode;
    private int stayPos;


    private View rootView;
    private CustomLondMoreButtomView customLondMoreButtomView;
    private View mEmptyView;

    public static ListTranporstFragment getFragment(String InfoDepartId){
        ListTranporstFragment fragment = new ListTranporstFragment();
        Bundle bundle = new Bundle();
        bundle.putString("InfoDepartId", InfoDepartId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.activity_transport_information_list, container, false);
            ButterKnife.bind(this, rootView);
            myActivity = getActivity();
            initView();
        }else{
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        getData();
        return rootView;
    }


    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(myActivity));

        final CommonAdapter<TransportMode> mAdapter = new CommonAdapter<TransportMode>(myActivity, R.layout.information_department_item, freightList){
            @Override
            protected void convert(ViewHolder helper, final TransportMode transportMode, int pos) {
                try {
                    View lr = helper.getView(R.id.infor_depar_title);
                    View line_view = helper.getView(R.id.view_line);
                    if (pos == 0) {
                        lr.setVisibility(View.GONE);
                        line_view.setVisibility(View.GONE);
                    } else {
                        lr.setVisibility(View.GONE);
                        line_view.setVisibility(View.VISIBLE);
                    }

                    // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
                    String transportType = transportMode.getTransportType();
                    if (transportType.equals("0")) {
                        helper.getView(R.id.ll_surplus).setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_surplus, transportMode.getSurplusNum());
                        helper.setText(R.id.freight_type, " / 煤炭");
                    } else if (transportType.equals("1")) {
                        helper.getView(R.id.ll_surplus).setVisibility(View.GONE);
                        helper.setText(R.id.freight_type, "    长期货运 / 煤炭");
                    } else if (transportType.equals("2")) {
                        helper.setText(R.id.freight_type, " / 普货");

                        helper.getView(R.id.ll_surplus).setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_surplus, transportMode.getSurplusNum());
                    }

                    TextView publishTag0 = (TextView) helper.getView(R.id.publishTag0);
                    TextView publishTag1 = (TextView) helper.getView(R.id.publishTag1);
                    TextView publishTag2 = (TextView) helper.getView(R.id.publishTag2);
                    TextView publishTag3 = (TextView) helper.getView(R.id.publishTag3);
                    String publishTag = transportMode.getPublishTag() == "" ? null : transportMode.getPublishTag();
                    String payName;
                    int payColor;
                    //当资讯信息是免费的
                    if (transportMode.getConsultingFee().equals("0")) {
                        payName = "免费信息";
                        payColor = R.color.blue;
                        helper.getView(R.id.free_type_image).setVisibility(View.GONE);
                        helper.getView(R.id.tv_tips).setVisibility(View.GONE);
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
                        }
                    } else {
                        publishTag0.setVisibility(View.GONE);
                        publishTag1.setVisibility(View.GONE);
                        publishTag2.setVisibility(View.GONE);
                        publishTag3.setVisibility(View.GONE);

                        ImageView typeImage = (ImageView)helper.getView(R.id.free_type_image);
                        TextView typeText = (TextView)helper.getView(R.id.tv_tips);
                        typeImage.setVisibility(View.VISIBLE);
                        typeText.setVisibility(View.VISIBLE);

                        //当前登录用户和发布人同属于一个信息部
                        if (SharedTools.getStringValue(myActivity,"coalSalesId","-1").equals(transportMode.getCoalSalesId())){
                            payName = "付费信息" + "¥" + (Double.valueOf(transportMode.getConsultingFee()) / 100);
                            payColor = R.color.actionsheet_red;
                            typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                            typeText.setText("发布人："+ transportMode.getPublishUser());
                            typeText.setTextColor(getResources().getColor(R.color.actionsheet_gray));
                        }else
                            //0未支付，1已支付
                            if (transportMode.getIsPay().equals("1")) {
                                //当资讯信息已经付过费
                                payName = "付费信息" + "¥" + (Double.valueOf(transportMode.getConsultingFee()) / 100) + "，已支付，";
                                payColor = R.color.actionsheet_gray;
                                typeImage.setImageResource(R.mipmap.icon_buyer_app);
                                typeText.setText(transportMode.getLicenseMinute());
                                typeText.setTextColor(getResources().getColor(payColor));
                            } else {
                                //当资讯信息尚未付费
                                payName = "付费信息" + "¥" + (Double.valueOf(transportMode.getConsultingFee()) / 100);
                                payColor = R.color.actionsheet_red;
                                typeImage.setImageResource(R.mipmap.icon_about_app);
                                typeText.setText(transportMode.getLicenseMinute());
                                typeText.setTextColor(getResources().getColor(payColor));
                            }
                    }
                    helper.setText(R.id.free_type, payName);
                    ((TextView) helper.getView(R.id.free_type)).setTextColor(myActivity.getResources().getColor(payColor));
                    /*******************************************************************************/
                    helper.setText(R.id.pubilshTime, transportMode.getDifferMinute());
                    helper.setText(R.id.tv_transport_cost, transportMode.getCost());
                    helper.setText(R.id.tv_start_address, StringUtils.setString(transportMode.getFromPlace(), "省", 1).replace("市", ""));
                    helper.setText(R.id.tv_end_address, StringUtils.setString(transportMode.getToPlace(), "省", 1).replace("市", ""));
                    helper.setText(R.id.tv_surplus, transportMode.getSurplusNum());
                    final String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
                    helper.getView(R.id.iv_phone).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (transportMode.getConsultingFee().equals("0")) {
                                //免费信息或者已支付 可以直接打电话
                                callPhone(transportMode);
                            } else {
                                //不免费 先判断是否登录
                                if (userId.equals("-1")) {
                                    //未登录 先去登录
                                    UIHelper.jumpActLogin(myActivity,  false);
                                } else {
                                    //已登录 判断是否支付
                                    if (transportMode.getIsPay().equals("1")) {
                                        //已支付 直接打电话
                                        callPhone(transportMode);
                                    } else {
                                        //未支付 弹框提示
                                        ((BaseActivity) myActivity).showPayDialog(transportMode, "1");
                                    }
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                TransportMode transportMode = mAdapter.getDatas().get(position);
                Intent intent = new Intent();
                intent.setClass(myActivity, TransportDetailActivity.class);
                intent.putExtra("waybillId", transportMode.getTransportId());
                String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
                //先判断是否登录
                if (userId.equals("-1")) {
                    //未登录 先去登录
                    UIHelper.jumpActLogin(myActivity,  false);
                } else {
                    if (transportMode.getConsultingFee().equals("0") || SharedTools.getStringValue(myActivity,"coalSalesId","-1").equals(transportMode.getCoalSalesId())) {
                        //免费信息或者已支付 可以直接查看详情
                        startActivity(intent);
                    } else {
                        //已登录 判断是否支付
                        if (transportMode.getIsPay().equals("1")) {
                            //已支付 直接查看
                            startActivity(intent);
                        } else {
                            //未支付 弹框提示
                            ((BaseActivity)myActivity).showPayDialog(transportMode, "1");
                        }
                    }
                }

                stayTransportMode = transportMode;
                stayPos = position;

                myReceiver = new MyReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(RECEIVED_ACTION);
                myActivity.registerReceiver(myReceiver, filter);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        customLondMoreButtomView = new CustomLondMoreButtomView(myActivity,mRecyclerView,mAdapter);
        customLondMoreButtomView.setOnLoadMoreListener(new CustomLondMoreButtomView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage ++ ;
                        getData();
                    }
                }, 1500);
            }
        });

        mEmptyView = LayoutInflater.from(myActivity).inflate(R.layout.layout_no_data,null);
        mEmptyView.findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
        ImageView IV = (ImageView)mEmptyView.findViewById(R.id.iv_no_data);
        IV.setImageResource(R.mipmap.icon_no_transport_order);
        TextView TV = (TextView)mEmptyView.findViewById(R.id.tv_no_data);
        TV.setText("暂无货运！");

        mRecyclerView.setAdapter(customLondMoreButtomView.mRefreshableView);

    }

    protected void getData() {
        try {
            String InfoDepartId = getArguments().getString("InfoDepartId");
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("coalSalesId", InfoDepartId);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(myActivity, params).getCoalSalesCoalTransportList(new DataUtils.DataBack<APPDataList<TransportMode>>() {
                @Override
                public void getData(APPDataList<TransportMode> dataInfor) {
                    try {
                        if (dataInfor == null) {
                            return;
                        }
                        if (currentPage == 1) {
                            freightList.clear();
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            freightList.addAll(dataInfor.getList());
                        }else{
                            loadData(dataInfor.getList());
                        }

                        if (freightList.size() == 0){
                            customLondMoreButtomView.mRefreshableView.addHeaderView(mEmptyView);
                        }else{
                            customLondMoreButtomView.mRefreshableView.removeHeaderView(mEmptyView);
                        }
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData(List<TransportMode> myListData) {
        try {
            if (myListData.size() < 10 ){
                customLondMoreButtomView.setmLoadingMore(false);
            }else{
                customLondMoreButtomView.setmLoadingMore(true);
            }

            if (freightList.size() != 0){
                int index = freightList.size() + 1;
                freightList.addAll(myListData);
                mRecyclerView.getAdapter().notifyItemInserted(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void callPhone(TransportMode transportMode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", transportMode.getPublishUserPhone());
        map.put("callType", Constant.CALE_TYPE_FREIGHT);
        map.put("targetId", transportMode.getTransportId());
        UIHelper.showCollTel(myActivity, map, true);
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
                    if (stayTransportMode == null){
//                        getDataType(true);
                    }else{
                        stayTransportMode.setIsPay("1");
                        stayTransportMode.setLicenseMinute("永久有效");
                        mRecyclerView.getAdapter().notifyItemChanged(stayPos,stayTransportMode);
                    }
                } catch (Exception e) {
                    GHLog.e("MyReceiver", e.toString());
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myReceiver != null){
            myActivity.unregisterReceiver(myReceiver);
        }
    }

}
