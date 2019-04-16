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
import android.widget.TextView;
import com.sxhalo.PullCoal.model.ListCoalEntity;
import com.sxhalo.PullCoal.ui.pullrecyclerview.CustomLondMoreButtomView;
import com.sxhalo.PullCoal.utils.Utils;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.DetailsWebActivity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

/**
 * Created by amoldZhang on 2018/11/13.
 */
public class ListCoalFragment extends BaseFragment{

    @Bind(R.id.coal_list_rv)
    RecyclerView mRecyclerView;

    private List<Coal> coalList = new ArrayList<Coal>();
    private int currentPage = 1;
    private Activity myActivity;

    private View rootView;
    private String TAG = "ListCoalFragment";
    private CustomLondMoreButtomView customLondMoreButtomView;
    private View mEmptyView;

    private MyReceiver myReceiver;
    //待支付的煤炭
    private ListCoalEntity coalEntity = new ListCoalEntity();


    public static ListCoalFragment getFragment(String InfoDepartId){
        ListCoalFragment fragment = new ListCoalFragment();
        Bundle bundle = new Bundle();
        bundle.putString("InfoDepartId", InfoDepartId);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.activity_coal_information_list, container, false);
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

    protected void getData() {
        try {
            String InfoDepartId = getArguments().getString("InfoDepartId");
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("coalSalesId", InfoDepartId);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(myActivity,params).getCoalSalesCoalGoodsList(new DataUtils.DataBack<APPDataList<Coal>>() {
                @Override
                public void getData(APPDataList<Coal> dataInfor) {
                    try {
                        if (dataInfor == null) {
                            return;
                        }
                        if (currentPage == 1){
                            coalList.clear();
                            coalList.addAll(dataInfor.getList());
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }else{
                            loadData(dataInfor.getList());
                        }

                        if (coalList.size() == 0){
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
                    if (mRecyclerView.getAdapter().getItemCount() == 0){
                        View view = LayoutInflater.from(myActivity).inflate(R.layout.layout_no_data,null);
                        view.findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
                        ImageView IV = (ImageView)view.findViewById(R.id.iv_no_data);
                        IV.setImageResource(R.mipmap.icon_no_transport_order);
                        TextView TV = (TextView)view.findViewById(R.id.tv_no_data);
                        TV.setText("暂无煤炭！");
                        customLondMoreButtomView.mRefreshableView.addHeaderView(view);
                    }
                    GHLog.e("ListCoalFragment  fragment_ID==","数据初加载后rootView的高度=====" + rootView.getHeight());
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
            e.printStackTrace();
        }
    }



    /**
     * 初始化RecyclerView
     */
    private void initView() {
        final CommonAdapter<Coal> mAdapter = new CommonAdapter<Coal>(myActivity, R.layout.activity_com_girdview_item, coalList){
            @Override
            protected void convert(ViewHolder helper, final Coal coal, final int position){
                try {
                    if (position == 0) {
                        helper.getView(R.id.view).setVisibility(View.GONE);
                    } else {
                        helper.getView(R.id.view).setVisibility(View.VISIBLE);
                    }

                    if (Utils.isImagesTrue(coal.getImageUrl()) == 200 ){
                        ((BaseActivity) myActivity).getImageManager().loadCoalTypeUrlImage(coal.getImageUrl(), coal.getCategoryImage(),(ImageView) helper.getView(R.id.iv_com_head));
                    }else{
                        ((BaseActivity) myActivity).getImageManager().loadCoalTypeUrlImage(coal.getCategoryImage(),(ImageView)helper.getView(R.id.iv_com_head));
                    }

                    if (StringUtils.isEmpty(coal.getCoalReportPicUrl())) {
                        helper.getView(R.id.iv_test).setVisibility(View.GONE);
                    } else {
                        helper.getView(R.id.iv_test).setVisibility(View.VISIBLE);
                    }
                    helper.setText(R.id.tv_name, coal.getCoalName());
                    helper.setText(R.id.tv_price, coal.getOneQuote());
                    helper.setText(R.id.tv_com_calorificvalue, coal.getCalorificValue() + "kCal/kg");
                    helper.setText(R.id.tv_com_storagerate, coal.getMineMouthName());
                    helper.setText(R.id.updata_time,coal.getDifferMinute());
                    String payName;
                    int payColor;
                    //当资讯信息是免费的
                    if (coal.getConsultingFee().equals("0")) {
                        payName = "免费信息";
                        payColor = R.color.blue;
                        helper.getView(R.id.tv_price).setVisibility(View.VISIBLE);
                        helper.getView(R.id.tv_price_img).setVisibility(View.VISIBLE);
                        helper.getView(R.id.free_type_image).setVisibility(View.GONE);
                        helper.getView(R.id.tv_tips).setVisibility(View.GONE);
                    } else {
                        ImageView typeImage = (ImageView)helper.getView(R.id.free_type_image);
                        TextView typeText = (TextView)helper.getView(R.id.tv_tips);
                        typeImage.setVisibility(View.VISIBLE);
                        typeText.setVisibility(View.VISIBLE);
                        //当前登录用户和发布人同属于一个信息部
                        if (SharedTools.getStringValue(myActivity,"coalSalesId","-1").equals(coal.getCoalSalesId())){
                            payName = "付费信息" + "¥" + (Double.valueOf(coal.getConsultingFee()) / 100);
                            payColor = R.color.actionsheet_red;
                            helper.getView(R.id.tv_price).setVisibility(View.GONE);
                            helper.getView(R.id.tv_price_img).setVisibility(View.GONE);
                            typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                            typeText.setText("发布人："+ coal.getPublishUser());
                        }else
                            //0未支付，1已支付
                            if (coal.getIsPay().equals("1")&& coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                                //当资讯信息已经付过费
                                payName = "付费信息" + "¥" + (Double.valueOf(coal.getConsultingFee()) / 100) + "，已支付，";
                                payColor = R.color.actionsheet_gray;
                                helper.getView(R.id.tv_price).setVisibility(View.VISIBLE);
                                helper.getView(R.id.tv_price_img).setVisibility(View.VISIBLE);
                                typeImage.setImageResource(R.mipmap.icon_buyer_app);
                                typeText.setText(coal.getLicenseMinute());
                            } else {
                                //当资讯信息尚未付费
                                payName = "付费信息" + "¥" + (Double.valueOf(coal.getConsultingFee()) / 100);
                                payColor = R.color.actionsheet_red;
                                helper.getView(R.id.tv_price).setVisibility(View.GONE);
                                helper.getView(R.id.tv_price_img).setVisibility(View.GONE);
                                typeImage.setImageResource(R.mipmap.icon_about_app);
                                if (coal.getLicenseMinute().contains("已失效")){
                                    typeText.setText("支付后可查看更多");
                                }else{
                                    typeText.setText(coal.getLicenseMinute().contains("已失效")?"支付后可查看更多":coal.getLicenseMinute());
                                }
                            }
                    }
                    helper.setText(R.id.free_type, payName);
                    ((TextView) helper.getView(R.id.free_type)).setTextColor(myActivity.getResources().getColor(payColor));

                } catch (Exception e) {
                    MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Coal dataInformationDepartment = mAdapter.getDatas().get(position);

                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsWebActivity.class);
                intent.putExtra("Coal", dataInformationDepartment);
                intent.putExtra("inforDepartId", "信息部");

                String userId = SharedTools.getStringValue(getActivity(), "userId", "-1");
                //先判断是否登录
                if (userId.equals("-1")) {
                    //未登录 先去登录
                    UIHelper.jumpActLogin(getActivity(), false);
                } else {
                    if (dataInformationDepartment.getConsultingFee().equals("0") ||
                            SharedTools.getStringValue(getActivity(),"coalSalesId","-1").equals(dataInformationDepartment.getCoalSalesId())) {
                        //免费信息
                        startActivity(intent);
                    } else {
                        //已登录 判断是否支付
                        if (dataInformationDepartment.getIsPay().equals("1")&& dataInformationDepartment.getLicenseMinute() != null && !dataInformationDepartment.getLicenseMinute().contains("已失效")) {
                            //已支付 直接查看
                            startActivity(intent);
                        } else {
                            //未支付 弹框提示
                            ((BaseActivity) myActivity).showPayDialog(dataInformationDepartment, "0");
                        }
                    }
                }
                coalEntity.setCoal(dataInformationDepartment);
                coalEntity.setPos(position);

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


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(myActivity);
        mRecyclerView.setLayoutManager(linearLayoutManager);

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
        TV.setText("暂无煤炭！");

        mRecyclerView.setAdapter(customLondMoreButtomView.mRefreshableView);
    }


    private void loadData(List<Coal> myListData) {
        try {
            if (myListData.size() < 10 ){
                customLondMoreButtomView.setmLoadingMore(false);
            }else{
                customLondMoreButtomView.setmLoadingMore(true);
            }

            if (coalList.size() != 0){
                int index = coalList.size() + 1;
                coalList.addAll(myListData);
                mRecyclerView.getAdapter().notifyItemInserted(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    if (coalEntity == null){
//                        getDataType(true);
                    }else{
                        coalEntity.getCoal().setIsPay("1");
                        coalEntity.getCoal().setLicenseMinute("7天后失效");
                        mRecyclerView.getAdapter().notifyItemChanged(coalEntity.getPos(),coalEntity.getCoal());
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
