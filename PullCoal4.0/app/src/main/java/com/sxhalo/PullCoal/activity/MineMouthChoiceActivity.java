package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.SamplingTest;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.SearchView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.popwin.SelectAreaPopupWindow;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullFooterLayout;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 *  矿口选择
 * Created by amoldZhang on 2019/3/8.
 */
public class MineMouthChoiceActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_select_area)
    TextView tvSelectArea;
    @Bind(R.id.iv_select_area)
    ImageView ivSelectArea;
    @Bind(R.id.search_layout)
    SearchView searchLayout;
    @Bind(R.id.anchor_view)
    View anchorView;
    @Bind(R.id.pull_to_refresh_recycler_view)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;

    private RecyclerView myRecyclerView; //布局展示控价
    private int currentPage = 1; //加载更多时页数
    private CommonAdapter<SamplingTest> myAdapter; //数据渲染器
    private List<SamplingTest> myListData = new ArrayList<>(); //数据请求列表结合
    private String searchValue = ""; //搜索内容
    private String regionCode = "";
    private SelectAreaPopupWindow areaPopupWindow; // 地区筛选控件

    private String typeName = "-1";
    private SamplingTest samplingTest;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_mouth_choice);
    }

    @Override
    protected void initTitle() {
        title.setText("选择矿口");
        //第一次进入
        if (!StringUtils.isEmpty(getIntent().getStringExtra("Entity"))){
            typeName = getIntent().getStringExtra("Entity");
        }else{ //修改矿口进入
            samplingTest = (SamplingTest)getIntent().getSerializableExtra("Entity");
        }
        initView();
        createAreaWindow();
    }

    @Override
    protected void getData(){
        currentPage = 1;
        getDataType(true);
    }

    private void initView() {
        searchLayout.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onRefreshAutoComplete(String text) {
            }

            @Override
            public void onSearch(String text) {
                if(!StringUtils.isEmpty(text)){
                    searchValue = text;
                    currentPage = 1;
                    getDataType(true);
                }
            }

            @Override
            public void clearContent() {
                searchValue = "";
                currentPage = 1;
                getDataType(true);
            }

            @Override
            public void onTipsItemClick(String text) {
                displayToast("onTipsItemClick");
            }

            @Override
            public void onSearchHasFocus(boolean hasFocus) {
            }
        });
        setPullToRefreshRecyclerView();
    }

    private void setPullToRefreshRecyclerView() {
        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

//        pullToRefreshRecyclerView.setHeaderLayout(new PullHeadViewLayout(this));
        pullToRefreshRecyclerView.setFooterLayout(new PullFooterLayout(this));

        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage ++ ;
                        getDataType(false);
                    }
                },1000);
            }
        });

        myRecyclerView = pullToRefreshRecyclerView.getRefreshableView();
        myRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        myAdapter = new CommonAdapter<SamplingTest>(mContext,R.layout.list_item_minemouth_choice,myListData){
            @Override
            protected void convert(ViewHolder holder, SamplingTest samplingTest, int position) {
                holder.setText(R.id.text_title,samplingTest.getMineMouthName());
            }
        };

        myAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                SamplingTest data = null;
                if (!"-1".equals(typeName)) {
                    data = myAdapter.getDatas().get(position);
                    data.setTypeName(typeName);
                } else {
                    samplingTest.setMineMouthId(myAdapter.getDatas().get(position).getMineMouthId());
                    samplingTest.setMineMouthName(myAdapter.getDatas().get(position).getMineMouthName());
                    data = samplingTest;
                }
                //修改矿口时跳转
                if (AppManager.getAppManager().ifActivity(SamplingTestActivity.class)){
                    Intent intent = new Intent(mContext,SamplingTestActivity.class);
                    intent.putExtra("Entity",data);
                    setResult(RESULT_OK,intent);
                    finish();
                }else { //初始时跳转
                    UIHelper.jumpAct(mContext,
                            CoalNameChoiceActivity.class,
                            data,
                            true);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        myRecyclerView.setAdapter(myAdapter);
    }

    private void getDataType(boolean flage) {
        try{
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

            //搜索条件
            if (!StringUtils.isEmpty(searchValue)){
                params.put("searchValue", searchValue);
            }
            if (!StringUtils.isEmpty(regionCode) && !"-1".equals(regionCode)){
                params.put("regionCode", regionCode);
            }
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "15");
            new DataUtils(this, params).getCoalCompaniesMinenameList(new DataUtils.DataBack<APPDataList<SamplingTest>>() {
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
                        myAdapter.notifyDataSetChanged();
                        showEmptyView(myAdapter.getItemCount(), layoutNoData, pullToRefreshRecyclerView);

                        if (appDataList.getList().size() < 15){
                            pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
                        }else{
                            pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                        }
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    showEmptyView(myAdapter.getItemCount(), layoutNoData, pullToRefreshRecyclerView);
                }
            },flage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 界面数据刷新
     * @param entityList
     */
    private void refreshData(List<SamplingTest> entityList) {
        myListData.clear();
        myAdapter.notifyDataSetChanged();
        myListData.addAll(entityList);
    }

    /**
     * 加载更多数据
     * @param entityList
     */
    private void loadData(List<SamplingTest> entityList) {
        try {
            if (entityList.size() != 0){
                int index = myListData.size() + 1;
                myListData.addAll(entityList);
                myAdapter.notifyItemInserted(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.title_bar_left, R.id.layout_select_area})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_select_area:
                areaPopupWindow.showPopupWindow(tvSelectArea,0,anchorView);
                ivSelectArea.setImageResource(R.mipmap.icon_arrow_up);
                break;
        }
    }

    /**
     * 创建城市筛选布局
     */
    private void createAreaWindow() {
        areaPopupWindow = new SelectAreaPopupWindow(this, 0);
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivSelectArea.setImageResource(R.mipmap.icon_arrow_down);
                regionCode = areaPopupWindow.getStartCode();
                getDataType(true);
            }
        });
    }
}
