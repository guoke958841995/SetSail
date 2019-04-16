package com.sxhalo.PullCoal.ui.freight;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.ui.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.ui.freight.book.BookFreightFragment;
import com.sxhalo.PullCoal.ui.freight.search.FreightSearchFragment;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.UIHelper;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 货运Fragment
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public class FreightFragment extends BaseFragment {

    @BindView(R.id.title_bar_left)
    ImageView titleBarLeft;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_title_bar_right_type)
    TextView tvTitleBarRightType;
    @BindView(R.id.vp_freight)
    ViewPager vpFreight;

    private int currentIndex = 0;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    public static FreightFragment newInstance() {
        Bundle args = new Bundle();
        FreightFragment fragment = new FreightFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_freight;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        titleBarLeft.setVisibility(View.GONE);
        tvTitleBarRightType.setText("货运发布");
        tvLeft.setText("货运搜索");
        tvRight.setText("订阅货运");

        tvLeft.setSelected(true);
        tvRight.setSelected(false);
        initFragment();
    }

    @Override
    public void initData() {

    }

    private void initFragment() {
        mFragmentList.add(0,FreightSearchFragment.newInstance());
        mFragmentList.add(1,BookFreightFragment.newInstance());

        FragmentManager fm = getChildFragmentManager();
        MyViewPagerAdapter mAdapetr = new MyViewPagerAdapter(fm, mFragmentList);
        vpFreight.setAdapter(mAdapetr);
        vpFreight.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 界面切换
     * @param currentIndex
     */
    public void setCurrentItem(int currentIndex) {
        if (currentIndex == 0){
            tvLeft.setSelected(true);
            tvRight.setSelected(false);
        }else{
            tvLeft.setSelected(false);
            tvRight.setSelected(true);
        }
        vpFreight.setCurrentItem(currentIndex);
    }

    @OnClick({R.id.tv_left, R.id.tv_right, R.id.tv_title_bar_right_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_left:
                if (currentIndex != 0){
                    currentIndex = 0;
                    setCurrentItem(currentIndex);
                }
                break;
            case R.id.tv_right:
                if (currentIndex != 1) {
                    currentIndex = 1;
                    setCurrentItem(currentIndex);
                }
                break;
            case R.id.tv_title_bar_right_type:
                //货运发布
                String userId = PaperUtil.get("userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(getActivity());
                } else {
                    //登陆后点击跳转货运记录界面
//                    startActivityForResult(new Intent(myActivity, MyTransportOrdersListActivity.class), Constant.JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT);
//                    startActivityForResult(new Intent(myActivity, MyTransportReleaseListActivity.class), Constant.JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT);
                   displayToast("跳转货运发布界面");
                }
                break;
        }
    }
}
