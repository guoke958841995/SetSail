package com.sxhalo.PullCoal.ui.pullrecyclerview.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.BuyCoalActivity;
import com.sxhalo.PullCoal.activity.DriverListAcrivity;
import com.sxhalo.PullCoal.activity.FriendActivity;
import com.sxhalo.PullCoal.activity.GoingToPullCoalActivity;
import com.sxhalo.PullCoal.activity.InformationDepartmentListActivity;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.MessageCenterActivity;
import com.sxhalo.PullCoal.activity.MineProductListActivity;
import com.sxhalo.PullCoal.activity.MyOrderActivity;
import com.sxhalo.PullCoal.activity.MyTransportOrdersListActivity;
import com.sxhalo.PullCoal.activity.PurchasingReservationActivity;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.fragment.FunctionListFragment;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.model.Function;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.viewpagerindicator.CirclePageIndicator;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 功能菜单列表
 * Created by amoldZhang on 2018/12/18.
 */
public class TemplateFunction extends BaseView{

    @Bind(R.id.vp_fl)
    ViewPager vpFl;
    @Bind(R.id.indicator)
    CirclePageIndicator indicator;

    private View mRootView;
    private FragmentActivity myActivity;
    private FragmentManager fragmentManager;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private MyViewPagerAdapter mAdapetr;
    private int count;

    public TemplateFunction(Activity context) {
        super(context);
        myActivity = (BaseActivity)context;
        initView();
    }

    private void initView() {
        mRootView = View.inflate(mContext, R.layout.header_function_list_layout, null);
        ButterKnife.bind(this, mRootView);

        FragmentManager fm = this.getFragmentManager();
        mAdapetr = new MyViewPagerAdapter(fm,fragments);
        vpFl.setAdapter(mAdapetr);
        addTemplateView();
    }

    @Override
    public void setData(BaseModule module) {
        if (module == null || !(module instanceof HomeData))
        {
            return;
        }
        fillData(module);
    }

    @Override
    public void fillData(BaseModule module) {
        try {
            if (module == null || !(module instanceof HomeData) || ((HomeData) module).getFunctionList() == null)
            {
                return;
            }
            if (((HomeData) module).getFunctionList().size() != 0){
                dealWithTheView(((HomeData) module).getFunctionList());
            }
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTemplateView() {
//        LayoutParams lp = new LayoutParams(-1, (int) (mContext.getResources().getDisplayMetrics().density * 160));
//        mRootView.setLayoutParams(lp);
        addView(mRootView);
    }

    @Override
    public void reFresh() {

    }

    private void dealWithTheView(List<Function> functionList) {
        try {
            fragments.clear();//清空
            mAdapetr.notifyDataSetChanged();
            count = functionList.size()/8;
            if(count == 0){
                count = 1;
            }else{
                count = functionList.size()%8 == 0 ? count : count + 1;
            }
            for (int i = 0; i < count; i++) {
                Bundle data = new Bundle();
                data.putSerializable("functionList", (Serializable) functionList);
                data.putInt("index", i );
                FunctionListFragment newfragment = new FunctionListFragment();
                newfragment.setArguments(data);
                fragments.add(newfragment);
            }
            vpFl.setOffscreenPageLimit(count);
            mAdapetr.notifyDataSetChanged();

            // 添加指示图标
            addIndicatorImageViews();
        } catch (Exception e) {
            GHLog.e("功能列表", e.toString());
        }
    }

    // 添加指示图标
    private void addIndicatorImageViews() {
        try {
            if (fragments.size() > 1){
                indicator.setVisibility(View.VISIBLE);
                indicator.setViewPager(vpFl);
                indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }else{
                indicator.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }




    public FragmentManager getFragmentManager() {
        fragmentManager = myActivity.getSupportFragmentManager();
//        fragmentManager = homePagerFragment.getChildFragmentManager();
        return fragmentManager;
    }
}
