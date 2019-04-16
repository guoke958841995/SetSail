package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.model.ActivityData;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amoldZhang on 2017/12/14.
 */
public class HeaderActivityView extends HeaderViewInterface<ActivityData> {


    @Bind(R.id.tv_activity)
    TextView tvActivity;
    @Bind(R.id.view_activity)
    View viewActivity;
    @Bind(R.id.view_activity1)
    View viewActivity1;
    @Bind(R.id.find_show_listview)
    ListView findShowListview;

    private ActivityData mData;
    protected Activity mContext;
    private HeaderAdViewView listViewAdHeaderView;
    private List<Slide> gridSlides = new ArrayList<Slide>();

    public HeaderActivityView(Activity context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void getView(ActivityData data, ListView listView) {
        View view = mInflate.inflate(R.layout.header_activity_view, listView, false);
        ButterKnife.bind(this, view);
        this.mData = data;
        refreshData(data);
        listView.addHeaderView(view);
    }

    public void refreshData(ActivityData data) {
        this.mData = data;
        if (mData.getSlideList().size() != 0) {
            viewActivity.setVisibility(View.VISIBLE);
            viewActivity1.setVisibility(View.VISIBLE);
            tvActivity.setVisibility(View.VISIBLE);
            findShowListview.setVisibility(View.VISIBLE);
            if (listViewAdHeaderView != null) {
                listViewAdHeaderView.dealWithTheView(mData.getSlideList());
            } else {
                //矿口的图片轮播
                createADView();
            }
        } else {
            viewActivity.setVisibility(View.GONE);
            viewActivity1.setVisibility(View.GONE);
            tvActivity.setVisibility(View.GONE);
            findShowListview.setVisibility(View.GONE);
            if (listViewAdHeaderView != null) {
                findShowListview.removeHeaderView(listViewAdHeaderView.getView());
                listViewAdHeaderView = null;
            }
        }
    }

    /**
     * 创建广告视图
     */
    private void createADView() {
        // 设置广告数据
        listViewAdHeaderView = new HeaderAdViewView(mContext);
        listViewAdHeaderView.fillView(mData.getSlideList(),findShowListview);

        listViewAdHeaderView.setADItemOnClickListener(new HeaderAdViewView.ADOnClickListener() {
            @Override
            public void adItemOnClickListener(View view, int position) {
                try {
                    String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                    Slide mEntity = mData.getSlideList().get(position);
                    if ("1".equals(mEntity.getAccessCondition()) && userId.equals("-1")){  //需要判断是否登录
                        //未登录点击跳转登录界面
                        UIHelper.jumpActLogin(mContext, false);
                    }else{  //不需要登录
                        if(StringUtils.isEmpty(mEntity.getSendUrl())){
                        }else{
                            GHLog.i("头条点击", position+"");
                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra("URL",mEntity.getSendUrl());
                            intent.putExtra("articleId",mEntity.getArticleId());
                            mContext.startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    GHLog.e("头条点击", e.toString());
                }
            }
        });
        QuickAdapter<Slide> gridViewAdapter = new QuickAdapter<Slide>(mContext, R.layout.news_gridview_item, gridSlides) {
            @Override
            protected void convert(BaseAdapterHelper helper, Slide itemMap, int position) {
//                ((DetaileMineActivity) mContext).getImageManager().newsloadUrlImageLong(itemMap, (ImageView) helper.getView().findViewById(R.id.imageView));
            }
        };
        findShowListview.setAdapter(gridViewAdapter);
    }
}
