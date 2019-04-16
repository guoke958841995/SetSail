package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.UserSearchHistory;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.SamplingTest;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.SearchHistory;
import com.sxhalo.PullCoal.ui.SearchView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullFooterLayout;
import com.sxhalo.PullCoal.ui.pullrecyclerview.WrapRecyclerAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 *  通过矿口 获取矿口下商品名称
 * Created by amoldZhang on 2019/3/18.
 */
public class CoalNameChoiceActivity extends BaseActivity {


    @Bind(R.id.search_layout)
    SearchView searchLayout;
    @Bind(R.id.pull_to_refresh_recycler_view)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;

    private RecyclerView myRecyclerView; //布局展示控价
    private CommonAdapter<SamplingTest> myCommonAdapter; //内容结果渲染控件
    private int currentPage = 1; //加载更多时页数
    private List<SamplingTest> myListData = new ArrayList<>(); //数据请求列表结合
    private String searchValue = ""; //搜索内容
    private SamplingTest samplingTest; //采样化验实体
    private WrapRecyclerAdapter wrapRecyclerAdapter; //布局渲染控件
    private SearchHistory initSearchHistory;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_coal_name_choice);
    }

    @Override
    protected void initTitle() {
        samplingTest = (SamplingTest)getIntent().getSerializableExtra("Entity");
        initView();
    }

    @Override
    protected void getData() {
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
                    initSearchHistory.savekeyWord(searchValue);
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
            }

            @Override
            public void onSearchHasFocus(boolean hasFocus) {
                if (hasFocus){
                    initSearchHistory.notifyHistoryView();
                    myRecyclerView.getAdapter().notifyDataSetChanged();
                }else{
                    initSearchHistory.notifyHistoryView();
                    myRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
        setPullToRefreshRecyclerView();
    }

    private void setPullToRefreshRecyclerView() {
        myRecyclerView = pullToRefreshRecyclerView.getRefreshableView();
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

        myCommonAdapter = new CommonAdapter<SamplingTest>(mContext,R.layout.list_item_minemouth_choice,myListData){
            @Override
            protected void convert(ViewHolder holder, SamplingTest samplingTest, int position) {
                holder.setText(R.id.text_title,samplingTest.getCoalName());
            }
        };

        myCommonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (wrapRecyclerAdapter.mHeaderViews.size() != 0){
                    position = position - 1;
                }
                samplingTest.setCoalName(myCommonAdapter.getDatas().get(position).getCoalName());
                samplingTest.setGoodsId(myCommonAdapter.getDatas().get(position).getGoodsId());
                if (AppManager.getAppManager().ifActivity(SamplingTestActivity.class)){
                    Intent intent = new Intent(mContext,SamplingTestActivity.class);
                    intent.putExtra("Entity",samplingTest);
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    UIHelper.jumpAct(mContext,
                            SamplingTestActivity.class,
                            samplingTest,
                            true);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        wrapRecyclerAdapter = new WrapRecyclerAdapter(myCommonAdapter);
        initSearchHistory = new SearchHistory(mContext,"2", new SearchHistory.SearchHistoryListener() {
            @Override
            public void onItemClick(String keyWord) {
                if(!StringUtils.isEmpty(keyWord)){
                    searchValue = keyWord;
                    searchLayout.setText(searchValue);
                    currentPage = 1;
                    getDataType(true);
                }
            }

            @Override
            public void onClearData() {
                initSearchHistory.notifyHistoryView();
                myRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        wrapRecyclerAdapter.addHeaderView(initSearchHistory.getView());
        myRecyclerView.setAdapter(wrapRecyclerAdapter);
    }

    private void getDataType(boolean flage) {
        try{
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

            //搜索条件
            if (!StringUtils.isEmpty(searchValue)){
                params.put("searchValue", searchValue);
            }
            params.put("mineMouthId", samplingTest.getMineMouthId());
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this, params).getCoalGoodsCoalNameList(new DataUtils.DataBack<APPDataList<SamplingTest>>() {
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
                                myListData = myCommonAdapter.getDatas();
                            }
                        }
                        pullToRefreshRecyclerView.onRefreshComplete();
                        myRecyclerView.getAdapter().notifyDataSetChanged();
                        showEmptyView(myCommonAdapter.getItemCount(), layoutNoData, pullToRefreshRecyclerView);

                        if (appDataList.getList().size() < 10){
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
                    showEmptyView(myRecyclerView.getAdapter().getItemCount(), layoutNoData, pullToRefreshRecyclerView);
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

    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }

}
