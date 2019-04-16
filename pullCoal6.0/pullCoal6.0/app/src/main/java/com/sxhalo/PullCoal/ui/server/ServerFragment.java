package com.sxhalo.PullCoal.ui.server;

import android.os.Bundle;
import android.view.View;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;

/**
 * 服务Fragment
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public class ServerFragment extends BaseFragment {

    public static ServerFragment newInstance() {
        Bundle args = new Bundle();
        ServerFragment fragment = new ServerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_server;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {

    }

    @Override
    public void initData() {

    }
}
