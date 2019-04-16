package com.sxhalo.PullCoal.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.base.SupportFragment;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.ui.freight.FreightFragment;
import com.sxhalo.PullCoal.ui.home.HomeFragment;
import com.sxhalo.PullCoal.ui.order.OrderFragment;
import com.sxhalo.PullCoal.ui.personal.PersonalFragment;
import com.sxhalo.PullCoal.ui.server.ServerFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/1 0001.
 */
public class MainActivity extends BaseActivity {


    @BindView(R.id.fl_content_container)
    FrameLayout flContentContainer;
    @BindView(R.id.rb_bottom_home)
    RadioButton rbBottomHome;
    @BindView(R.id.rb_bottom_freight)
    RadioButton rbBottomFreight;
    @BindView(R.id.rb_bottom_order)
    RadioButton rbBottomOrder;
    @BindView(R.id.rb_bottom_service)
    RadioButton rbBottomService;
    @BindView(R.id.rb_bottom_personal)
    RadioButton rbBottomPersonal;
    @BindView(R.id.rg_bottom_group)
    RadioGroup rgBottomGroup;
    @BindView(R.id.rl_bottom_navigation_bar)
    RelativeLayout rlBottomNavigationBar;

    private SupportFragment[] mFragments = new SupportFragment[5];


    @Override
    public int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            // 没有的时候去创建Fragment
            mFragments[0] = HomeFragment.newInstance();
            mFragments[1] = FreightFragment.newInstance();
            mFragments[2] = OrderFragment.newInstance();
            mFragments[3] = ServerFragment.newInstance();
            mFragments[4] = PersonalFragment.newInstance();

            // 加载多个同级根Fragment
            getSupportDelegate().loadMultipleRootFragment(R.id.fl_content_container, 0,
                    mFragments[0], mFragments[1], mFragments[2], mFragments[3], mFragments[4]);

        } else {
            // 从栈内获取已有的Fragment
            mFragments[0] = findFragment(HomeFragment.class);
            mFragments[1] = findFragment(FreightFragment.class);
            mFragments[2] = findFragment(OrderFragment.class);
            mFragments[3] = findFragment(ServerFragment.class);
            mFragments[4] = findFragment(PersonalFragment.class);
        }

        //默认显示第一个Fragment
        showCurrentFragment(0);
    }

    @Override
    public void initData() {

    }


    /**
     * 设置当前显示的fragment
     *
     * @param position
     */
    private void showCurrentFragment(int position) {
        getSupportDelegate().showHideFragment(mFragments[position]);
    }

    @OnClick({R.id.rb_bottom_home, R.id.rb_bottom_freight, R.id.rb_bottom_order, R.id.rb_bottom_service, R.id.rb_bottom_personal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_bottom_home:
                showCurrentFragment(0);
                break;
            case R.id.rb_bottom_freight:
                showCurrentFragment(1);
                break;
            case R.id.rb_bottom_order:
                showCurrentFragment(2);
                break;
            case R.id.rb_bottom_service:
                showCurrentFragment(3);
                break;
            case R.id.rb_bottom_personal:
                showCurrentFragment(4);
                break;
        }
    }
}
