package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.fragment.SamplingTestsFrament;
import com.sxhalo.PullCoal.ui.scrolllayout.content.NoScrollViewPager;

import java.util.ArrayList;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 买家记录
 * Created by amoldZhang on 2019/3/19.
 */
public class BuyerRecordsActivity extends BaseActivity {

    @Bind(R.id.tv_sampling)
    TextView tvSampling;
    @Bind(R.id.tv_laboratory_tests)
    TextView tvLaboratoryTests;
    @Bind(R.id.my_viewpage)
    NoScrollViewPager myViewpage;

    private int tag = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_buyer_records);
    }

    @Override
    protected void initTitle() {
        tvSampling.setSelected(true);
        tvLaboratoryTests.setSelected(false);
    }

    @Override
    protected void getData() {
        getFragment();
        myViewpage.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), fragments));
//        myViewpage.setCurrentItem(tag);
    }

    @OnClick({R.id.title_bar_left, R.id.tv_sampling, R.id.tv_laboratory_tests})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.tv_sampling:
                if (tag != 0){
                    tag = 0;
                    tvSampling.setSelected(true);
                    tvLaboratoryTests.setSelected(false);
                    myViewpage.setCurrentItem(tag);
                }
                break;
            case R.id.tv_laboratory_tests:
                if (tag != 1) {
                    tag = 1;
                    tvSampling.setSelected(false);
                    tvLaboratoryTests.setSelected(true);
                    myViewpage.setCurrentItem(tag);
//                    fragments.get(tag).onHiddenChanged(false);
                }
                break;
        }
    }

    /**
     * 加载列表展示布局
     */
    public void getFragment() {
        for (int i=0;i<2;i++){
            SamplingTestsFrament samplingTestsFrament = new SamplingTestsFrament();
            Bundle build = new Bundle();
            build.putInt("type",i);
            samplingTestsFrament.setArguments(build);
            fragments.add(samplingTestsFrament);
        }
    }
}
