package com.sxhalo.PullCoal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.Article;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderAdViewView;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 资讯列表展示
 * Created by amoldZhang on 2017/1/6.
 */
public class InformationListFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<Article>{

    @Bind(R.id.coal_list_type)
    SmoothListView listView;
    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;
    private BaseActivity myActivity;
    private FilterEntity userChannel;
    //轮播图链接列表
    private List<Article> tList = new ArrayList<Article>();
    private final int Company = 1;
    private final int Waybill = 2;
    private int currentPage = 1;

    //轮播图加载工具显示控件
    private HeaderAdViewView listViewAdHeaderView;
    //轮播图链接列表
    private List<Slide> focusList = new ArrayList<Slide>();

    private BaseAdapterUtils baseAdapterUtils;
    private int type;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            userChannel = (FilterEntity) getArguments().getSerializable("FilterEntity");
            type = Integer.valueOf(userChannel.dictCode);
        } catch (Exception e) {
            GHLog.e("Bundle传值", e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = (BaseActivity) getActivity();
        initData();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initData() {
        // 设置广告数据
        createdADHeader();
        baseAdapterUtils = new BaseAdapterUtils(myActivity, listView);
        baseAdapterUtils.setViewItemData(R.layout.home_recommend_item, tList);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    private void createdADHeader() {
        listViewAdHeaderView = new HeaderAdViewView(getActivity());
        listViewAdHeaderView.fillView(focusList, listView);

        listViewAdHeaderView.setADItemOnClickListener(new HeaderAdViewView.ADOnClickListener() {
            @Override
            public void adItemOnClickListener(View view, int position) {
                try {
                    Slide mEntity = focusList.get(position);
                    System.out.println("跳转连接++++"+mEntity.getPublishedUrl());
                    if(mEntity.getPublishedUrl().equals("")||mEntity.getPublishedUrl().equals("null")){
                    }else{
                        GHLog.i("头条点击", position+"");
                        Intent intent = new Intent(myActivity, WebViewActivity.class);
                        intent.putExtra("URL",mEntity.getPublishedUrl());
                        intent.putExtra("title","资讯详情");
                        intent.putExtra("articleId",mEntity.getArticleId());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    GHLog.e("头条点击", e.toString());
                }
            }
        });
    }

    /**
     * 资讯中的煤赋值
     * @param helper
     * @param article
     */
    private void setCoalData(BaseAdapterHelper helper, Article article) {
        try{
            helper.setText(R.id.information_title, article.getTitle());
            helper.getView().findViewById(R.id.information_time_coal).setVisibility(View.VISIBLE);
            helper.setText(R.id.information_time_coal,article.getPushtime() );
        }catch (Exception e){
            GHLog.e("赋值", e.toString());
        }
    }

    /**
     * 资讯中的货运赋值
     * @param helper
     * @param article
     */
    private void setFreightData(BaseAdapterHelper helper, Article article, int pos) {
        try{
            View view_line = helper.getView().findViewById(R.id.layout_divider);
            if (pos == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }
            helper.setText(R.id.tv_from_place, article.getFromPlace());
            helper.setText(R.id.tv_to_place, article.getToPlace());
            helper.setText(R.id.tv_content, article.getContent());
            helper.setText(R.id.tv_time, article.getPushtime());
        }catch (Exception e){
            GHLog.e("赋值", e.toString());
        }
    }

    /**
     * 资讯赋值
     * @param helper
     * @param article
     */
    private void setInformationData(BaseAdapterHelper helper, Article article) {
        try{
            RelativeLayout relativeLayout = (RelativeLayout)helper.getView().findViewById(R.id.recommend_informations_rl);
            if ((StringUtils.isEmpty(article.getIsSpecial())?"0":article.getIsSpecial()).equals("1")){  //头条

            }else {  //普通新闻
                relativeLayout.setVisibility(View.VISIBLE);
                helper.setText(R.id.recommend_informations_title,article.getTitle());
                helper.setText(R.id.recommend_informations_originPlace,article.getSource());
                helper.setText(R.id.recommend_informations_num,article.getClickNum());
                String pushTime;
                try{
                    pushTime= DateUtil.formatDisplayTime(article.getPushtime(),"MM-dd HH:mm");
                }catch (Exception e){
                    GHLog.e("时间转换异常",e.toString());
                    pushTime = article.getPushtime();
                }
                helper.setText(R.id.recommend_informations_push_time,pushTime);
                String imageUrl = StringUtils.isEmpty(article.getImageUrl())?"":article.getImageUrl();
                ImageView image = (ImageView)helper.getView().findViewById(R.id.recommend_informations_image);
                if (image.equals("")){
                    image.setVisibility(View.GONE);
                }else{
                    image.setVisibility(View.VISIBLE);
                    myActivity.getImageManager().loadCoalTypeUrlImage(imageUrl,image);
                }
            }
        }catch (Exception e){
            GHLog.e("赋值", e.toString());
        }

    }

    private void getData(){
        try {
            LinkedHashMap<String, String> treeMap =   new LinkedHashMap<String, String>();
            treeMap.put("typeId",userChannel.dictCode);
            treeMap.put("currentPage",currentPage+"");
            treeMap.put("pageSize","10");
            new DataUtils(myActivity,treeMap).getArticleList(new DataUtils.DataBack<APPDataList<Article>>() {
                @Override
                public void getData(APPDataList<Article> data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        setData(data.getList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    public void setData(List<Article> data) {
        if (currentPage == 1){
            focusList.clear();
        }
        List<Article> list = setSwitch(data);
        if (list != null) {
            tList = list;
        }
        if (focusList.size() != 0){
            if (listViewAdHeaderView.getView() == null ){
                listViewAdHeaderView.getView(focusList,listView);
            }else{
                listViewAdHeaderView.dealWithTheView(focusList);
            }
            baseAdapterUtils.refreshData(tList,focusList);
        }else{
            baseAdapterUtils.refreshData(tList);
        }
        GHLog.i("资讯轮播图界面",focusList.size()+"条");

        if (baseAdapterUtils.getCount() == 0){
            myActivity.showEmptyView(focusList.size(), relativeLayout, listView);
        }else{
            myActivity.showEmptyView(baseAdapterUtils.getCount(), relativeLayout, listView);
        }
    }


    /**
     * 根据不同的需求取出相应的实体
     * @param data
     * @return
     */
    private List<Article> setSwitch(List<Article> data){
        List<Article> list = new ArrayList<Article>();
        for (int i=0;i<data.size();i++){  //0 不是 1是
            String isSpecial = StringUtils.isEmpty(data.get(i).getIsSpecial())?"0":data.get(i).getIsSpecial();
            if (isSpecial.equals("1")){
                Slide slide= new Slide();
                slide.setImageUrl(data.get(i).getImageUrl());
                slide.setPublishedUrl(data.get(i).getPublishedUrl());
                focusList.add(slide);
            }else{
                list.add(data.get(i));
            }
        }
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listViewAdHeaderView.stopADRotate();
        ButterKnife.unbind(this);
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, Article article, int pos) {
        try {
            switch (type){
                case Company:  //煤炭
                    RelativeLayout relativeLayoutCoal = (RelativeLayout)helper.getView().findViewById(R.id.information_coal);
                    relativeLayoutCoal.setVisibility(View.VISIBLE);
                    setCoalData(helper,article);
                    break;
                case Waybill:  //货运
                    LinearLayout relativeLayoutFreight = (LinearLayout)helper.getView().findViewById(R.id.information_freight);
                    relativeLayoutFreight.setVisibility(View.VISIBLE);
                    setFreightData(helper, article, pos);
                    break;
                default:  //其他资讯
                    setInformationData(helper,article);
                    break;
            }
        } catch (Exception e) {
            GHLog.e("赋值", e.toString());
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<Article> mAdapter) {
        if (type == 2) {
            return;
        }
        Intent intent = new Intent(myActivity, WebViewActivity.class);
        intent.putExtra("title","资讯详情");
        if(focusList.size() == 0){
            position = position - 1;
        }else{
            position = position - 2;
        }
        GHLog.i("当前点击条目","点击了 "+position+"");
        GHLog.i("当前列表下头条个数","头条个数为： "+focusList.size()+"");
        gotoWeb(intent,position,mAdapter);
        startActivity(intent);
    }

    /**
     * 修改点击量
     * @param intent
     * @param position
     * @param mAdapter
     */
    private void gotoWeb(Intent intent,int position,QuickAdapter<Article> mAdapter){
        try {
            GHLog.i("资讯跳转路径",mAdapter.getItem(position).getPublishedUrl());
            intent.putExtra("URL",mAdapter.getItem(position).getPublishedUrl());
            intent.putExtra("articleId",mAdapter.getItem(position).getArticleId());
            int infoNumber = Integer.valueOf(mAdapter.getItem(position).getClickNum()) + 1;
            mAdapter.getItem(position).setClickNum(infoNumber+"");
            mAdapter.set(position,mAdapter.getItem(position));
        } catch (NumberFormatException e) {
            GHLog.e("点击异常", e.toString());
        }
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

}
