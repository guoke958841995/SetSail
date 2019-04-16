package com.sxhalo.PullCoal.ui.personal;

import android.os.Bundle;
import android.view.View;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public class PersonalFragment extends BaseFragment {

    public static PersonalFragment newInstance() {
        Bundle args = new Bundle();
        PersonalFragment fragment = new PersonalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_personal;
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
