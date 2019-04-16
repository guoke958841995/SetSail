package com.sxhalo.PullCoal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.MyTransportOrdersListActivity;
import com.sxhalo.PullCoal.activity.MyTransportReleaseListActivity;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 货运功能界面展示
 */
public class FreightTransportFragment extends Fragment {

//    @Bind(R.id.layout_freight)
//    LinearLayout layoutFreight;
//    @Bind(R.id.tv_freight_record)
//    TextView tvFreightRecord;
    @Bind(R.id.tv_freight_search)
    TextView tvFreightSearch;
    @Bind(R.id.tv_book_freight)
    TextView tvBookFreight;

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentActivity myActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_freight_transport, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFragment();
        myActivity = getActivity();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            viewPager.setCurrentItem(0);
            fragments.get(0).onHiddenChanged(true);
        }else{
            ((MainActivity)getActivity()).setFlage(false);
        }
    }

    private void initFragment() {
        FreightSearchFragment freightTransportFragment = new FreightSearchFragment();
        BookFreightFragment bookFreightFragment = new BookFreightFragment();
        fragments.add(0,freightTransportFragment);
        fragments.add(1,bookFreightFragment);
        FragmentManager fm = getChildFragmentManager();
        MyViewPagerAdapter mAdapetr = new MyViewPagerAdapter(fm, fragments);
        viewPager.setAdapter(mAdapetr);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(pageListener);
    }

    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            viewPager.setCurrentItem(position);
            setUI(position);
        }
    };

    public void setCurrentItem(int currentIndex) {
        viewPager.setCurrentItem(currentIndex);
    }

    @OnClick({ R.id.tv_freight_search, R.id.tv_book_freight, R.id.tv_freight_record,})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_freight_search:
                //货运搜索
                setUI(0);
                break;
            case R.id.tv_book_freight:
                //订阅货运
                setUI(1);
                break;
            case R.id.tv_freight_record:
                //货运发布
                String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(myActivity,false);
                } else {
                    //登陆后点击跳转货运记录界面
//                    startActivityForResult(new Intent(myActivity, MyTransportOrdersListActivity.class), Constant.JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT);
                    startActivityForResult(new Intent(myActivity, MyTransportReleaseListActivity.class), Constant.JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT);
                }
                break;
        }
    }

    public void setUI(int type) {
        setCurrentItem(type);
        int paddingHorizontal = tvBookFreight.getPaddingLeft();
        int paddingVertical = tvBookFreight.getPaddingTop();
        switch (type) {
            case 0:
                //货运搜索被点击
                tvFreightSearch.setTextColor(getResources().getColor(R.color.white));
                tvBookFreight.setTextColor(getResources().getColor(R.color.app_title_text_color));

                tvFreightSearch.setBackgroundResource(R.drawable.offlinearrow_tab1_pressed);
                tvBookFreight.setBackgroundResource(R.drawable.offlinearrow_tab2_normal);
                break;
            case 1:
                //订阅货运被点击
                tvFreightSearch.setTextColor(getResources().getColor(R.color.app_title_text_color));
                tvBookFreight.setTextColor(getResources().getColor(R.color.white));

                tvFreightSearch.setBackgroundResource(R.drawable.offlinearrow_tab1_normal);
                tvBookFreight.setBackgroundResource(R.drawable.offlinearrow_tab2_pressed);
                break;
        }
        tvFreightSearch.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);

        tvBookFreight.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
    }
}

