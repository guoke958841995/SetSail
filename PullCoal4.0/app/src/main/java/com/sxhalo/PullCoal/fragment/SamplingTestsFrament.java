package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.SamplingAnalysisDetailsActivity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.SamplingTest;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullFooterLayout;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullHeadViewLayout;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  采样化验单列表布局
 * Created by amoldZhang on 2019/3/20.
 */
public class SamplingTestsFrament extends Fragment{

    @Bind(R.id.pull_to_refresh_recycler_view)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.iv_type)
    ImageView ivType;
    @Bind(R.id.iv_time)
    ImageView ivTime;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;

    private int currentPage = 1;  //当前请求页数
    private RecyclerView myRecyclerView; //当前界面显示控件布局
    private CommonAdapter<SamplingTest> myAdapter; //当前数据显示控制器
    private List<SamplingTest> myListData = new ArrayList<>();
    private List<Map<String, String>> stringTimeList = new ArrayList<Map<String, String>>();
    private String[] stringTime = new String[]{"全部","这个月", "上一个月", "三个月内", "半年内"};
    private int consultingTime = 4; //时间范围0.全部 1：这个月 2：上一个月 3：三个月内 4：半年内
    private PopupWindow popupWindowTime;

    private PopupWindow popupWindowType;
    private List<Map<String, String>> stringTypeList = new ArrayList<Map<String, String>>();
    private String[] stringType0 = new String[]{"全部","已提交", "采样中","已邮寄","完成","用户取消"};
    private String[] stringType1 = new String[]{"全部","已提交", "采样中","化验中","已邮寄","完成","用户取消"};
    private int typeRange0 = 0; //采样状态0全部 1已提交（待受理）  2采样中（已受理）4已邮寄  5完成 100用户取消
    private int typeRange1 = 0; //化验状态0全部 1已提交（待受理）  2采样中（已受理）3化验中  4已邮寄  5完成 100用户取消

    private boolean needRefresh = false;//是否需要刷新
    private Activity myActivity;
    private int type; // 0 采样 1 化验

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = getActivity();
        type = getArguments().getInt("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_sampling_tests, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e("货运功能界面展示", e.toString());
        }
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            type = getArguments().getInt("type");
            getDataType(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getDataType(true);
        createTypePopupWindow();
        createTimePopupWindow();
    }

    private void getDataType(boolean flage) {
        try{
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            // 采样状态： 0已提交（待受理）  1采样中（已受理）  2化验中 3 拒绝 4已邮寄  5完成 100用户取消
            int typeRange = 0; //后台可识别状态
            if (type == 0){
                //采样状态0全部 1已提交（待受理）  2采样中（已受理）4已邮寄  5完成 100用户取消
                if (typeRange0 < 3){
                    typeRange = typeRange0 - 1;
                }else if(typeRange0 == 5){
                    typeRange = 100;
                }else if (typeRange0 == 3 || typeRange0 == 4){
                    typeRange = typeRange0 + 1;
                }
            }else{
                //化验状态0全部 1已提交（待受理）  2采样中（已受理）3化验中  4已邮寄  5完成 100用户取消
                if(typeRange1 <= 3){
                    typeRange = typeRange1 - 1;
                }else if(typeRange1 == 6){
                    typeRange = 100;
                }else{
                    typeRange = typeRange1;
                }
            }
            if (typeRange >= 0){
                params.put("sampleState", typeRange + "");
            }

            // 时间范围0：这个月 1：上一个月 2：三个月内 3：半年内 4.时间范围全部
            if (consultingTime != 4){
                params.put("timeRange",consultingTime + "");
            }
            // 类型 1化验 2采样
            params.put("type", type == 0 ? "2" : "1" );
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(myActivity, params).getOperatesampleList(new DataUtils.DataBack<APPDataList<SamplingTest>>() {
                @Override
                public void getData(APPDataList<SamplingTest> appDataList) {
                    try {
                        if (appDataList == null) {
                            return;
                        }
                        if (appDataList.getList() == null) {
                            myListData = new ArrayList<SamplingTest>();
                        } else {
                            if (appDataList.getList() != null) {
                                if (currentPage == 1){
                                    refreshData(appDataList.getList());
                                }else{
                                    loadData(appDataList.getList());
                                }
                                myListData = myAdapter.getDatas();
                            }
                        }
                        pullToRefreshRecyclerView.onRefreshComplete();
                        myRecyclerView.getAdapter().notifyDataSetChanged();
                        ((BaseActivity)myActivity).showEmptyView(myAdapter.getItemCount(), layoutNoData, pullToRefreshRecyclerView);

                        if (appDataList.getList().size() <= 10){
                            pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }else{
                            pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    ((BaseActivity)myActivity).showEmptyView(myAdapter.getItemCount(), layoutNoData, pullToRefreshRecyclerView);
                }
            },flage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshRecyclerView.setHeaderLayout(new PullHeadViewLayout(myActivity));
        pullToRefreshRecyclerView.setFooterLayout(new PullFooterLayout(myActivity));

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
        myRecyclerView.setLayoutManager(new LinearLayoutManager(myActivity));

        myAdapter = new CommonAdapter<SamplingTest>(myActivity,R.layout.list_item_sampling_test,myListData){
            @Override
            protected void convert(ViewHolder holder, SamplingTest samplingTest, int position) {
                holder.setText(R.id.sampling_name,samplingTest.getMineMouthName());
                holder.setText(R.id.sampling_type,type == 0 ? "采样":"化验");
                holder.setText(R.id.sampling_time,samplingTest.getDifferMinute());

                String sampleState = "";
                //采样状态： 0已提交（待受理）  1采样中（已受理）  2化验中 3 拒绝 4已邮寄  5完成 100用户取消
                if ("0".equals(samplingTest.getSampleState())){
                    sampleState = "已提交";
                }else
                if ("1".equals(samplingTest.getSampleState())){
                    sampleState = "采样中";
                }else
                if ("2".equals(samplingTest.getSampleState())){
                    sampleState = "化验中";
                }else
                if ("3".equals(samplingTest.getSampleState())){
                    sampleState = "拒绝";
                }else
                if ("4".equals(samplingTest.getSampleState())){
                    sampleState = "已邮寄";
                }else
                if ("5".equals(samplingTest.getSampleState())){
                    sampleState = "完成";
                }else
                if ("100".equals(samplingTest.getSampleState())){
                    sampleState = "用户取消";
                }
                holder.setText(R.id.sampling_state,sampleState);
            }
        };

        myAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                SamplingTest samplingTest = myAdapter.getDatas().get(position);
                samplingTest.setTypeName(type == 0? "采样":"化验");
                UIHelper.jumpAct(myActivity,SamplingAnalysisDetailsActivity.class,samplingTest,false);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        myRecyclerView.setAdapter(myAdapter);
    }

    @OnClick({R.id.rl_time,R.id.rl_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_time:
                ivTime.setImageResource(R.mipmap.icon_arrow_up);
                if (popupWindowTime.isShowing()) {
                    popupWindowTime.dismiss();
                } else {
                    popupWindowTime.showAsDropDown(this.view);
                    darkenBackground(0.7f);
                }
                break;
            case R.id.rl_type:
                ivType.setImageResource(R.mipmap.icon_arrow_up);
                if (popupWindowType.isShowing()) {
                    popupWindowType.dismiss();
                }else {
                    popupWindowType.showAsDropDown(this.view);
                    darkenBackground(0.7f);
                }
                break;
        }
    }

    /**
     * 界面数据刷新
     * @param entityList
     */
    private void refreshData(List<SamplingTest> entityList) {
        myListData.clear();
        myRecyclerView.getAdapter().notifyDataSetChanged();
        myListData.addAll(entityList);
    }

    private void loadData(List<SamplingTest> entityList) {
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

    /**
     * 类型筛选
     */
    private void createTypePopupWindow() {
        final String[] stringS;
        if (type == 0){
            stringS = stringType0;
        }else{
            stringS = stringType1;
        }
        for (int i = 0; i < stringS.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", stringS[i]);
            if (i == 0) {
                map.put("show", "true");
            } else {
                map.put("show", "false");
            }
            stringTypeList.add(map);
        }

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null, false);
        // 创建PopupWindow实例,宽度和高度充满
        popupWindowType = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindowType.setFocusable(true);
        // 设置允许在外点击消失
        popupWindowType.setOutsideTouchable(true);
        //刷新状态（必须刷新否则无效）
        popupWindowType.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindowType.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindowType.setAnimationStyle(R.style.AnimationFade);

        popupWindowType.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (needRefresh) {
                    needRefresh = false;
                    currentPage = 1;
                    getDataType(true);
                }
                darkenBackground(1f);
                ivType.setImageResource(R.mipmap.icon_arrow_down);
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        ListView lv = (ListView) customView.findViewById(R.id.popview_listview);
        final QuickAdapter<Map<String, String>> adapter = new QuickAdapter<Map<String, String>>(myActivity, R.layout.popupwindow_list_item, stringTypeList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> item, int position) {
                helper.setText(R.id.tv_list_item, item.get("title"));
                if (item.get("show").equals("true")) {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (type == 0){
                    if (typeRange0 == i) {
                        popupWindowType.dismiss();
                        return;
                    }
                    needRefresh = true;
                    typeRange0 = i;
                }else{
                    if (typeRange1 == i) {
                        popupWindowType.dismiss();
                        return;
                    }
                    needRefresh = true;
                    typeRange1 = i;
                }

                for (int k = 0; k < stringTypeList.size(); k++) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (k == i) {
                        map.put("show", "true");
                    } else {
                        map.put("show", "false");
                    }
                    map.put("title", stringTypeList.get(k).get("title"));
                    stringTypeList.set(k, map);
                }
                tvType.setText(stringS[i]);
                adapter.replaceAll(stringTypeList);
                popupWindowType.dismiss();
            }
        });
    }

    /**
     * 时间筛选
     */
    private void createTimePopupWindow() {
        for (int i = 0; i < stringTime.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", stringTime[i]);
            if (i == 0) {
                map.put("show", "true");
            } else {
                map.put("show", "false");
            }
            stringTimeList.add(map);
        }

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null, false);
        // 创建PopupWindow实例,宽度和高度充满
        popupWindowTime = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindowTime.setFocusable(true);
        // 设置允许在外点击消失
        popupWindowTime.setOutsideTouchable(true);
        //刷新状态（必须刷新否则无效）
        popupWindowTime.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindowTime.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindowTime.setAnimationStyle(R.style.AnimationFade);

        popupWindowTime.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (needRefresh) {
                    needRefresh = false;
                    currentPage = 1;
                    getDataType(true);
                }
                darkenBackground(1f);
                ivTime.setImageResource(R.mipmap.icon_arrow_down);
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        ListView lv = (ListView) customView.findViewById(R.id.popview_listview);
        final QuickAdapter<Map<String, String>> adapter = new QuickAdapter<Map<String, String>>(myActivity, R.layout.popupwindow_list_item, stringTimeList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> item, int position) {
                helper.setText(R.id.tv_list_item, item.get("title"));
                if (item.get("show").equals("true")) {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (consultingTime == i) {
                    popupWindowTime.dismiss();
                    return;
                }
                needRefresh = true;
                consultingTime = i;
                for (int k = 0; k < stringTimeList.size(); k++) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (k == i) {
                        map.put("show", "true");
                    } else {
                        map.put("show", "false");
                    }
                    map.put("title", stringTimeList.get(k).get("title"));
                    stringTimeList.set(k, map);
                }
                tvTime.setText(stringTime[i]);
                adapter.replaceAll(stringTimeList);
                popupWindowTime.dismiss();
            }
        });
    }

    /**
     * 改变背景颜色
     */
    private void darkenBackground(Float bgcolor) {
        WindowManager.LayoutParams lp = myActivity.getWindow().getAttributes();
        lp.alpha = bgcolor;
        myActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        myActivity.getWindow().setAttributes(lp);
    }
}
