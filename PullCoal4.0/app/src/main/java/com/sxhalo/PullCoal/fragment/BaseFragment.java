package com.sxhalo.PullCoal.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.sxhalo.PullCoal.activity.BaseActivity;

/**
 * Created by amoldZhang on 2018/12/6.
 */

public class BaseFragment extends Fragment {

    private Activity myActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = getActivity();
    }

    public void displayToast(String showText){
        ((BaseActivity)myActivity).displayToast(showText);
    }

}
