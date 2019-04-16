package com.sxhalo.PullCoal.ui.pullrecyclerview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.BuyCoalActivity;
import com.sxhalo.PullCoal.activity.DetailsWebActivity;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.cardview.CardFragmentPagerAdapter;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.cardview.ShadowTransformer;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  精品煤炭布局
 * Created by amoldZhang on 2018/12/18.
 */
public class TemplateFineCoalViewPage extends BaseView{

    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private List<Coal> myListData = new ArrayList<>();
    private View mRootView;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    public TemplateFineCoalViewPage(Activity context) {
        super(context);
        initView();
    }

    private void initView() {
        mRootView = View.inflate(mContext, R.layout.header_horizontal_view, null);
        ButterKnife.bind(this, mRootView);

        addTemplateView();
    }

    @Override
    public void setData(BaseModule module) {
        fillData(module);
    }

    @Override
    public void fillData(BaseModule module) {
        if (module == null || !(module instanceof HomeData))
        {
            return;
        }

        if (((HomeData) module).getCoalList().size() != 0){
            mRootView.setVisibility(VISIBLE);
            myListData.clear();
            myListData.addAll(((HomeData) module).getCoalList());

            if (mFragmentCardAdapter == null){
                mFragmentCardAdapter = new CardFragmentPagerAdapter(((MainActivity)mContext).getSupportFragmentManager(),
                        BaseUtils.dip2px(mContext,0),myListData);
                mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
                //设置主显示布局是否放大效果
                mFragmentCardShadowTransformer.enableScaling(false); //放大缩小
                mViewPager.setAdapter(mFragmentCardAdapter);
                // 设置viewPage 切换动画
                mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
                //设置每次加载的布局个数
                mViewPager.setOffscreenPageLimit(3);
                //设置子布局间距
                mViewPager.setPageMargin(BaseUtils.dip2px(mContext,10));
                //默认加载第一个
                mViewPager.setCurrentItem(1);
            }else{
                mFragmentCardAdapter.setData(myListData);
                mFragmentCardAdapter.notifyDataSetChanged();
            }
        }else {
            mRootView.setVisibility(GONE);
            return;
        }
//        this.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onItemClick(module);
//            }
//        });
        invalidate();
    }

    @Override
    public void addTemplateView() {
//        LayoutParams lp = new LayoutParams(-1, (int) (mContext.getResources().getDisplayMetrics().density * 40));
//        mRootView.setLayoutParams(lp);
        addView(mRootView);
    }

    @Override
    public void reFresh() {

    }
}
