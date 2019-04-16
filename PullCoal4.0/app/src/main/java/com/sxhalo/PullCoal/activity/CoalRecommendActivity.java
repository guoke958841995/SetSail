package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.addrightview.LayoutContrastView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.Utils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.srecyclerview.SRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 精品煤炭展示
 * Created by amoldZhang on 2018/7/30.
 */
public class CoalRecommendActivity extends BaseActivity{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.mRecyclerView)
    SRecyclerView mRecyclerView;

    @Bind(R.id.ll_contrast)
    LayoutContrastView llContrast;

    private List<Coal> myListData = new ArrayList<>();
    private CommonAdapter<Coal> mAdapter;
    private EmptyWrapper mEmptyWrapper;
    private int currentPage = 1;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_coal_recommend);
    }

    @Override
    protected void initTitle() {
//        mRecyclerView = (SRecyclerView)findViewById(R.id.mRecyclerView);
        title.setText("精品煤炭");
        initView();
    }

    @Override
    protected void getData() {
        try {
            String InfoDepartId = getIntent().getStringExtra("InfoDepartId");
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("coalSalesId", InfoDepartId);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "-1");
            new DataUtils(this,params).getCoalSalesCoalGoodsList(new DataUtils.DataBack<APPDataList<Coal>>() {
                @Override
                public void getData(APPDataList<Coal> dataInfor) {
                    try {
                        if (dataInfor == null) {
                            return;
                        }
                        if (dataInfor.getList() != null) {
//                            if (currentPage == 1){
                            refreshData(dataInfor.getList());
//                            }else{
//                                loadData(dataInfor.getList());
//                            }
                            myListData = mAdapter.getDatas();
                        }

                        if (dataInfor.getList().size() <= 10){
                            mRecyclerView.loadNoMoreData();
                        }else{
                            mRecyclerView.loadingComplete();
                        }

                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("联网错误", e.toString());
        }
    }

    private void initView() {
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(5,0,5,0));

        mAdapter = new CommonAdapter<Coal>(this, R.layout.layout_fine_coal_list_item, myListData){
            @Override
            protected void convert(ViewHolder holder,final Coal coals, final int position){

                    if (Utils.isImagesTrue(coals.getImageUrl()) == 200 ){
                        getImageManager().loadCoalTypeUrlImage(coals.getImageUrl(),coals.getCategoryImage(),(ImageView) holder.getView(R.id.iv_com_head));
                    }else{
                        getImageManager().loadCoalTypeUrlImage(coals.getCategoryImage(),(ImageView)holder.getView(R.id.iv_com_head));
                    }

                holder.setText(R.id.coal_item_name, coals.getCoalName());
                holder.setText(R.id.coal_price, coals.getOneQuote());
                holder.setText(R.id.tv_com_calorificvalue, coals.getCalorificValue() + "kCal/kg");

                TextView contrastText = (TextView)holder.getView(R.id.contrast_text);
                contrastText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                        //先判断是否登录
                        if (userId.equals("-1")) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(CoalRecommendActivity.this,false);
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
            }
        };
//        //如果设置了加载监听，就是需要刷新加载功能，如果没有设置加载监听，那么就没有下拉与底部加载
//        mRecyclerView.setLoadListener(new SRecyclerView.LoadListener() {
//            @Override
//            public void refresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        currentPage = 1;
//                        getData();
//                        mRecyclerView.refreshComplete();
//                    }
//                }, 1500);
//            }
//
//            @Override
//            public void loading() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        currentPage ++ ;
//                        getData();
//                        mRecyclerView.loadingComplete();
//                    }
//                }, 1500);
//            }
//        });


        //可以设置一个EmptyView
        //mRecyclerView.setEmptyView(new View(this));
        initEmptyView();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemClickListener(new SRecyclerView.ItemClickListener() {
            @Override
            public void click(View v, int position) {
                try {
                    Coal coal = mAdapter.getDatas().get(position);
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
                            //免费信息
                            startActivity(intent);
                        } else {
                            //不免费 先判断是否登录
                            if (userId.equals("-1")) {
                                //未登录 先去登录
                                UIHelper.jumpActLogin(mContext, false);
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
                    }
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    Log.i("煤炭列表点击", e.toString());
                }
            }
        });

        llContrast.initView(new LayoutContrastView.CoalBack(){
            @Override
            public void setLayout(boolean flage){
                if (flage) {
                    llContrast.setVisibility(View.VISIBLE);
                }else {
                    llContrast.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initEmptyView(){
        mEmptyWrapper = new EmptyWrapper(mAdapter);
        mEmptyWrapper.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.layout_no_data, mRecyclerView, false));
    }

    /**
     * 界面数据刷新
     * @param coalEntity
     */
    private void refreshData(List<Coal> coalEntity) {
        mRecyclerView.refreshComplete();
        myListData.clear();
        mRecyclerView.getAdapter().notifyDataSetChanged();

        myListData.addAll(coalEntity);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void loadData(List<Coal> coalEntity) {
        try {
            if (coalEntity.size() < 10) {
                mRecyclerView.loadNoMoreData();
            } else {
                mRecyclerView.loadingComplete();
            }

            if (coalEntity.size() != 0){
                int index = myListData.size() + 1;
                myListData.addAll(coalEntity);
                mRecyclerView.getAdapter().notifyItemInserted(index);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
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

}
