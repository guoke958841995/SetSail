package com.sxhalo.PullCoal.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.fragment.AcceptedFragment;
import com.sxhalo.PullCoal.fragment.CoalOrderFragment;
import com.sxhalo.PullCoal.fragment.UnAcceptedFragment;
import com.sxhalo.PullCoal.ui.SuperViewPager;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2018/5/18.
 */

public class CoalOrderActivity extends BaseActivity{

    @Bind(R.id.title)
    TextView title;

    private FragmentManager fragmentManager;
    private ArrayList<Fragment> lists = new ArrayList<Fragment>();
    private CoalOrderFragment coalOrderFragment;
    private ArrayList<String> fragmentTags;
    private int currIndex = 0;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_coal_order);
    }

    @Override
    protected void initTitle() {
        title.setText("煤炭订单");
        try {
            fragmentManager = getSupportFragmentManager();
            showFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void getData() {

    }

    private void showFragment() throws Exception{
        fragmentTags = new ArrayList<String>(Arrays.asList("coalOrderFragment"));
        currIndex = 0;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment == null) {
            fragment = instantFragment(currIndex);
        }
        for (int i = 0; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if (f != null && f.isAdded()) {
                fragmentTransaction.hide(f);
            }
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                coalOrderFragment = new CoalOrderFragment();
                return coalOrderFragment;
            default:
                return null;
        }
    }

    @OnClick({R.id.title_bar_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
        }
    }
}
