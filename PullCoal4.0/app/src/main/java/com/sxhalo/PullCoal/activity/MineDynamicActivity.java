package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.MineDynamic;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.tools.usertextview.ExpandTextView;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 矿口动态
 * Created by amoldZhang on 2017/11/7.
 */

public class MineDynamicActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<MineDynamic>{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.details_mine_list)
    SmoothListView detailsMineList;
    @Bind(R.id.map_type)
    TextView mapType;

    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;

    private List<MineDynamic> mineProducts = new ArrayList<MineDynamic>();
    private BaseAdapterUtils baseAdapterUtils;
    private int page = 1;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_dynamic);
    }

    @Override
    protected void initTitle() {
        mapType.setVisibility(View.VISIBLE);
        mapType.setText("发布");
        title.setText("矿口动态");
        initView();
    }

    @Override
    protected void getData() {
        String  mineMouthId = getIntent().getStringExtra("InfoDepartId");
        if (mineMouthId == null) {
            return;
        }
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("mineMouthId", mineMouthId);
            params.put("currentPage", page+"");
            params.put("pageSize", "10");
            new DataUtils(this,params).getCoalCompanyRealtimeInfoList(new DataUtils.DataBack<List<MineDynamic>>() {
                @Override
                public void getData(List<MineDynamic> dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
                        baseAdapterUtils.refreshData(dataList);
                        mineProducts = baseAdapterUtils.getListData();
                        showEmptyView(baseAdapterUtils.getCount(), relativeLayout, detailsMineList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        baseAdapterUtils = new BaseAdapterUtils<MineDynamic>(this,detailsMineList);
        baseAdapterUtils.settingList(true,true);
        baseAdapterUtils.setViewItemData(R.layout.mine_dynamic_item, mineProducts);
        baseAdapterUtils.setBaseAdapterBack(this);
    }


    @OnClick({R.id.title_bar_left, R.id.map_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                setResult(200,new Intent(this,DetaileMineActivity.class));
                finish();
                break;
            case R.id.map_type:
                if ("-1".equals(SharedTools.getStringValue(this,"userId","-1"))){
                    UIHelper.jumpActLogin(this, false);
                }else{
                    Intent intent = new Intent(this,MineDynamicReleaseActivity.class);
                    intent.putExtra("InfoDepartId",getIntent().getStringExtra("InfoDepartId"));
                    startActivityForResult(intent,200);
                }
                break;
        }
    }

    private ArrayList<String> setImageUrl(MineDynamic mineDynamic){
        ArrayList<String> newsImageUrlList = new ArrayList<String>();
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl1())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl1());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl2())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl2());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl3())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl3());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl4())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl4());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl5())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl5());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl6())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl6());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl7())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl7());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl8())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl8());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl9())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl9());
        }
        return newsImageUrlList;
    }

    private void setNewsGradView(BaseAdapterHelper helper, MineDynamic mineDynamic, int pos) {
        final  ArrayList<String> newsImageUrlList = setImageUrl(mineDynamic);
        int count = newsImageUrlList.size();
        NoScrollGridView newsGridview = (NoScrollGridView)helper.getView().findViewById(R.id.news_gridview);
        View viewGrid = (View)helper.getView().findViewById(R.id.view_grid);
        ImageView imageView = (ImageView)helper.getView().findViewById(R.id.imageView);
        if (count == 0 || count == 1) {
            newsGridview.setVisibility(View.GONE);
            if (count == 1) {
                viewGrid.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext,ImageBrowseActivity.class);
                        intent.putExtra("position",0);
                        intent.putStringArrayListExtra("list",newsImageUrlList);
                        startActivity(intent);
                    }
                });
                getImageManager().newsloadUrlImageLong(newsImageUrlList.get(0), imageView);
            }
        } else {
            viewGrid.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            newsGridview.setVisibility(View.VISIBLE);
            if (count == 4) {
                count = 2;
                viewGrid.setVisibility(View.VISIBLE);
            } else {
                count = 3;
            }
            newsGridview.setNumColumns(count); // 设置列数量=列表集合数
            QuickAdapter<String>  gridViewAdapter = new QuickAdapter<String>(this, R.layout.news_gridview_item, newsImageUrlList) {
                @Override
                protected void convert(BaseAdapterHelper helper, String itemMap, int position) {
                    getImageManager().newsloadUrlImageLong(itemMap, (ImageView) helper.getView().findViewById(R.id.imageView));
                }
            };
            newsGridview.setAdapter(gridViewAdapter);
            newsGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext,ImageBrowseActivity.class);
                    intent.putExtra("position",position);
                    intent.putStringArrayListExtra("list",newsImageUrlList);
                    startActivity(intent);
                }
            });
        }
        final ExpandTextView newsContent = (ExpandTextView)helper.getView().findViewById(R.id.news_content);

        // 设置TextView可展示的宽度 ( 父控件宽度 - 左右margin - 左右padding）
        int whidth = DeviceUtil.getWindowWidth(this) - DeviceUtil.dip2px(this, 16 * 2);
        newsContent.initWidth(whidth);
        // 设置最大行数(如果SDK >= 16 也可以直接在xml里设置)
        newsContent.setMaxLines(3);
        newsContent.setCloseText(mineDynamic.getSummary());

        helper.setText(R.id.time_refresh,mineDynamic.getReportTime());
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, MineDynamic mineDynamic, int pos) {
        setNewsGradView(helper, mineDynamic, pos);
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<MineDynamic> mAdapter) {
    }

    @Override
    public void getOnRefresh(int page) {
       this.page = page;
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        this.page = page;
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200){
            baseAdapterUtils.onRefresh();
        }
    }
}
