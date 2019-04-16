package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.fragment.FunctionListFragment;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.model.Function;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.viewpagerindicator.CirclePageIndicator;
import com.sxhalo.PullCoal.utils.GHLog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by amoldZhang on 2016/12/7.
 */
public class HeaderFunctionListView extends HeaderViewInterface<List<Function>> {


    @Bind(R.id.vp_fl)
    ViewPager vpFl;
    @Bind(R.id.indicator)
    CirclePageIndicator indicator;

    private FragmentActivity myActivity;
    private FragmentManager fragmentManager;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private View view;
    private MyViewPagerAdapter mAdapetr;
    private int count;
    public HomePagerFragment homePagerFragment;

    public HeaderFunctionListView(FragmentActivity myActivity, HomePagerFragment homePagerFragment) {
        super(myActivity);
        this.myActivity = myActivity;
        this.homePagerFragment = homePagerFragment;
    }

    @Override
    protected void getView(List<Function> list, ListView listView) {
        try {
            view = mInflate.inflate(R.layout.header_function_list_layout, listView, false);
            ButterKnife.bind(this, view);

            dealWithTheView(list);
            listView.addHeaderView(view);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }

    public View getView(List<Function> list) {
        view = mInflate.inflate(R.layout.header_function_list_layout, null, false);
        ButterKnife.bind(this, view);

        dealWithTheView(list);
        return view;
    }

    private void dealWithTheView(List<Function> functionList) {
        try {
            fragments.clear();//清空
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
            FragmentManager fm = this.getFragmentManager();
            mAdapetr = new MyViewPagerAdapter(fm,fragments);
            vpFl.setAdapter(mAdapetr);

            // 添加指示图标
            addIndicatorImageViews();
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
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
