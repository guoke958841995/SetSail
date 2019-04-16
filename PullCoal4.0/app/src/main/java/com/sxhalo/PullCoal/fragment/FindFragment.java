package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.ImageBrowseActivity;
import com.sxhalo.PullCoal.model.ActivityData;
import com.sxhalo.PullCoal.model.MineDynamic;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderActivityView;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderAddToolView;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderToolView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 服务界面展示
 * Created by amoldZhang on 2016/12/6.
 */
public class FindFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<MineDynamic> {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.my_list_view)
    SmoothListView myListView;



    private List<MineDynamic> myListData = new ArrayList<MineDynamic>();
    private Activity myActivity;
    private BaseAdapterUtils<MineDynamic> baseAdapterUtils;
    private int currentPage = 1;
    private ActivityData mActivityData = new ActivityData();
    private HeaderActivityView mHeaderActivityView;
    private HeaderToolView mHeaderToolView;
    private HeaderAddToolView mHeaderAddToolView; //自定义布局添加

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frigment_find, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        setView();
        getData();
    }

    /**
     * listView 初始化
     */
    private void setView() {
        tvTitle.setText("服务");

        mHeaderActivityView = new HeaderActivityView(myActivity);
        mHeaderActivityView.fillView(mActivityData, myListView);

        mHeaderToolView = new HeaderToolView(myActivity);
        mHeaderToolView.fillView(mActivityData, myListView);

        mHeaderAddToolView = new HeaderAddToolView(myActivity);
        mHeaderAddToolView.fillView(mActivityData, myListView);

        baseAdapterUtils = new BaseAdapterUtils<MineDynamic>(myActivity, myListView);
        baseAdapterUtils.settingList(true,false);
        baseAdapterUtils.setViewItemData(R.layout.fragment_find_list_item, myListData);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    private void getData() {
        try {
            LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
            params.put("currentPage",currentPage +"");
            params.put("pageSize","10");
            new DataUtils(getActivity(),params).getActivityList(new DataUtils.DataBack<ActivityData>() {
                @Override
                public void getData(ActivityData data) {
                    if (data == null){
                        return;
                    }
                    mActivityData = data;
                    if (data.getMineDynamicList() == null) {
                        myListData = new ArrayList<MineDynamic>();
                    } else {
                        myListData = data.getMineDynamicList();
                    }
                    if (currentPage == 1){
                        mHeaderActivityView.refreshData(data);
                    }
                    baseAdapterUtils.refreshData(myListData);
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }


    @Override
    public void getItemViewData(BaseAdapterHelper helper, MineDynamic mineDynamic, int pos) {
        try {
            if (pos == 0){
                helper.getView(R.id.tv_dynamic).setVisibility(View.VISIBLE);
                helper.getView(R.id.view_dynamic).setVisibility(View.VISIBLE);
            }else{
                helper.getView(R.id.tv_dynamic).setVisibility(View.GONE);
                helper.getView(R.id.view_dynamic).setVisibility(View.GONE);
            }
            setNewsGradView(helper, mineDynamic, pos);
        } catch (Exception e) {
            GHLog.e("赋值", e.toString());
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<MineDynamic> driversAdapter) {
    }

    private void setNewsGradView(BaseAdapterHelper helper, MineDynamic mineDynamic, int pos) {
        final  ArrayList<String> newsImageUrlList = setImageUrl(mineDynamic);
        int count = newsImageUrlList.size();
        NoScrollGridView newsGridview = (NoScrollGridView)helper.getView().findViewById(R.id.news_gridview);
        View viewGrid = helper.getView().findViewById(R.id.view_grid);
        ImageView imageView = (ImageView)helper.getView().findViewById(R.id.imageView);
        if (count == 0 || count == 1) {
            newsGridview.setVisibility(View.GONE);
            if (count == 0) {
                viewGrid.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            } else {
                viewGrid.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(myActivity,ImageBrowseActivity.class);
                        intent.putExtra("position",0);
                        intent.putStringArrayListExtra("list",newsImageUrlList);
                        startActivity(intent);
                    }
                });
                ((BaseActivity)myActivity).getImageManager().newsloadUrlImageLong(newsImageUrlList.get(0), imageView);
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
            QuickAdapter<String>  gridViewAdapter = new QuickAdapter<String>(myActivity, R.layout.news_gridview_item, newsImageUrlList) {
                @Override
                protected void convert(BaseAdapterHelper helper, String itemMap, int position) {
                     ImageView imageView  = (ImageView) helper.getView().findViewById(R.id.imageView);

                    ((BaseActivity)myActivity).getImageManager().newsloadUrlImageLong(itemMap, imageView);
                }
            };
            newsGridview.setAdapter(gridViewAdapter);
            newsGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(myActivity,ImageBrowseActivity.class);
                    intent.putExtra("position",position);
                    intent.putStringArrayListExtra("list",newsImageUrlList);
                    startActivity(intent);
                }
            });
        }
        final TextView tvMore = (TextView)helper.getView().findViewById(R.id.news_content_more);
        final TextView newsContent = (TextView)helper.getView().findViewById(R.id.news_content);
        ViewTreeObserver vto = newsContent.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            boolean isstate = true;
            @Override
            public boolean onPreDraw() {
                if (isstate){
                    isstate = false;
                    //获取textView的行数
                    int txtPart = newsContent.getLineCount();
                    if (txtPart > 3) {
                        //大于三行做的操作
                        newsContent.setMaxLines(3);
                        tvMore.setVisibility(View.VISIBLE);
                        //显示查看更多
                    } else {
                        //隐藏查看更多
                        tvMore.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });
        tvMore.setOnClickListener(new View.OnClickListener() {
            boolean isFirst = true;
            @Override
            public void onClick(View view) {
                if (isFirst) {
                    isFirst = false;
                    newsContent.setEllipsize(null);
                    newsContent.setSingleLine(isFirst);
                    tvMore.setText("收起");
                } else {
                    isFirst = true;
                    newsContent.setEllipsize(TextUtils.TruncateAt.END);
                    newsContent.setLines(3);
                    tvMore.setText("全部");
                }
            }
        });

        helper.setText(R.id.news_title,mineDynamic.getMineMouthName());
        helper.setText(R.id.news_content,mineDynamic.getSummary());
        helper.setText(R.id.time_refresh,mineDynamic.getReportTime());
        //1（营业）、2（停业）、3（关闭）、4（筹建）、5（其他）',
        if ("1".equals(mineDynamic.getOperatingStatus())){
            helper.getView(R.id.recommend_business_type).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.recommend_business_type).setVisibility(View.GONE);
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

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        this.currentPage = page;
        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
