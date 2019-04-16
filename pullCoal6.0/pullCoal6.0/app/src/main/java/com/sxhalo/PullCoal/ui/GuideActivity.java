package com.sxhalo.PullCoal.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.bean.AdvertisementBean;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.UIHelper;
import com.sxhalo.PullCoal.utils.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.utils.permissionutil.PermissionUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 引导页面
 *
 * @author Xiao_
 * @date 2019/4/13 0013
 */
public class GuideActivity extends BaseActivity {

    private static final String TAG = "GuideActivity";

    @BindView(R.id.vp_guide_pager)
    ViewPager mViewPager;
    @BindView(R.id.btn_enter)
    Button btnEnter;

    private int[] images = {
            R.mipmap.newer01,
            R.mipmap.newer02,
            R.mipmap.newer03,
    };

    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE};
    private AdvertisementBean advertisementData;

    @Override
    public int getContentLayout() {
        return R.layout.activity_guide;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            advertisementData = (AdvertisementBean) intent.getSerializableExtra("advertisement_data");
        }
        requestPermission();
    }

    @Override
    public void initData() {
        MyAdapter mAdapter = new MyAdapter(this, images);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        btnEnter.setVisibility(View.GONE);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (images != null && images.length > 0) {
                    if (position == images.length - 1) {
                        btnEnter.setVisibility(View.VISIBLE);
                    } else {
                        btnEnter.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    /**
     * 动态权限获取
     */
    private void requestPermission() {
        new PermissionUtil().requestPermissions(this, permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                //同意授权
            }

            @Override
            public void onDenied(Context context, List<String> permissions) {
                //拒绝授权
            }
        });
    }


    @OnClick({R.id.btn_enter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_enter:
                //将first_use改变,下次不进入引导页面
                PaperUtil.put(AppConstant.FIRST_USE_KEY, false);

                if (advertisementData != null) {
                    //有广告,进入广告页面
                    toAdvertPage(advertisementData);
                } else {
                    //没有广告,进入主页
                    UIHelper.jumpAct(GuideActivity.this, MainActivity.class, true);
                }
                break;
        }
    }

    /**
     * 跳转至广告页
     *
     * @param advertisementData
     */
    private void toAdvertPage(AdvertisementBean advertisementData) {
        Intent intent = new Intent(this, AdvertisementActivity.class);
        intent.putExtra("advertisement_data", advertisementData);
        startActivity(intent);
        finish();
    }

    /**
     * ViewPager Adapter
     */
    class MyAdapter extends PagerAdapter {

        private Context context;
        private int[] images;

        public MyAdapter(Context context, int[] images) {
            this.context = context;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images == null ? 0 : images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView ivImg = new ImageView(context);
            ivImg.setImageResource(images[position]);
            container.addView(ivImg);
            return ivImg;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }
}
