package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.ExpressEntity;
import com.sxhalo.PullCoal.model.SamplingTest;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.pullrecyclerview.WrapRecyclerAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderSamplingAnalysis;
import com.sxhalo.PullCoal.utils.GHLog;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 采样化验详情
 * Created by amoldZhang on 2019/3/22.
 */
public class SamplingAnalysisDetailsActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.my_recycler_view)
    RecyclerView myRecyclerView;

    private SamplingTest samplingTest;
    private String sampleId;

    private List<ExpressEntity> appDataList = new ArrayList<>();
    private String typeName;
    private WrapRecyclerAdapter wrapRecyclerAdapter;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sampling_analysis_details);
    }

    @Override
    protected void initTitle() {
        SamplingTest samplingTest = (SamplingTest) getIntent().getSerializableExtra("Entity");
        sampleId = samplingTest.getSampleId();
        typeName = samplingTest.getTypeName();
        title.setText(samplingTest.getTypeName().contains("采") ? "采样详情" : "化验详情");
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("sampleId", sampleId);
            new DataUtils(this, params).getOperatesampleInfo(new DataUtils.DataBack<APPData<SamplingTest>>() {

                @Override
                public void getData(APPData<SamplingTest> appData) {
                    if (appData != null) {
                        samplingTest = appData.getEntity();
                    }
                    samplingTest.setTypeName(typeName);
                    initView();
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            }, true);
        } catch (Exception e) {
            GHLog.e(getClass().getName(), e.toString());
        }
    }

    private void initView() {
        myRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        CommonAdapter myAdapter = new CommonAdapter<ExpressEntity>(mContext, R.layout.list_item_express, appDataList) {
            @Override
            protected void convert(ViewHolder holder, ExpressEntity dataEntity, int position) {
                ImageView dotIV = (ImageView) holder.getView(R.id.dot_iv);
                TextView acceptStationTV = (TextView) holder.getView(R.id.accept_station_tv);
                TextView acceptTimeTV = (TextView) holder.getView(R.id.accept_time_tv);
                if (position == 1) {
                    holder.getView(R.id.view_top).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.view_down).setBackgroundColor(getResources().getColor(R.color.text_chose));
                    dotIV.setImageDrawable(getResources().getDrawable(R.drawable.bg_amount_blue_layout));
                    acceptStationTV.setTextColor(getResources().getColor(R.color.text_chose));
                    acceptTimeTV.setTextColor(getResources().getColor(R.color.text_chose));
                } else {
                    if (position == 2){
                        holder.getView(R.id.view_top).setBackgroundColor(getResources().getColor(R.color.text_chose));
                    }else{
                        holder.getView(R.id.view_top).setBackgroundColor(getResources().getColor(R.color.text_normal));
                    }
                    holder.getView(R.id.view_down).setBackgroundColor(getResources().getColor(R.color.text_normal));
                    dotIV.setImageDrawable(getResources().getDrawable(R.drawable.bg_colaprogress));
                    acceptStationTV.setTextColor(getResources().getColor(R.color.text_normal));
                    acceptTimeTV.setTextColor(getResources().getColor(R.color.text_normal));
                }
                acceptStationTV.setText(dataEntity.context);
                acceptTimeTV.setText(dataEntity.time);
            }
        };
        wrapRecyclerAdapter = new WrapRecyclerAdapter(myAdapter);
        wrapRecyclerAdapter.addHeaderView(new HeaderSamplingAnalysis(mContext).getView(samplingTest));
        myRecyclerView.setAdapter(wrapRecyclerAdapter);

        //采样状态： 0已提交（待受理）  1采样中（已受理）  2化验中 3 拒绝 4已邮寄  5完成 100用户取消
        if (samplingTest.getSampleState().equals("4") || samplingTest.getSampleState().equals("5")) {
            getExpressInfo();
        }
    }

    /**
     * 调取第三方来获取快递详情
     */
    public void getExpressInfo() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("express", samplingTest.getExpress());
            params.put("expressNum", samplingTest.getExpressNum());
            new DataUtils(this, params).getExpressInfo(new DataUtils.DataBack<APPData<ExpressEntity>>() {

                @Override
                public void getData(APPData<ExpressEntity> appData) {
                    if (appData != null && appData.getList().size() != 0) {
                        appDataList.addAll(appData.getList());
                        myRecyclerView.getAdapter().notifyDataSetChanged();
                    }else{
                        displayToast("抱歉！暂无查询结果...");
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("抱歉！暂无查询结果...");
                }
            },false);
        } catch (Exception e) {
            GHLog.e(getClass().getName(), e.toString());
        }
    }

    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }
}
