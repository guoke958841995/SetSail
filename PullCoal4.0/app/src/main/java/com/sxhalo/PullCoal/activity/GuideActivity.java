package com.sxhalo.PullCoal.activity;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.AdvertisementEntity;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.viewpagerindicator.CirclePageIndicator;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.List;

import butterknife.Bind;

/**
 * Created by liz on 2017/4/11.
 */
public class GuideActivity extends BaseActivity {
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.indicator)
    CirclePageIndicator indicator;
    @Bind(R.id.btnHome)
    Button btnHome;

    private GalleryPagerAdapter adapter;
    private AdvertisementEntity advertisementEntity;
    private int[] images = {
            R.mipmap.newer01,
            R.mipmap.newer02,
            R.mipmap.newer03,
    };

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.guide);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //设置沉浸式
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void initTitle() {
        advertisementEntity = (AdvertisementEntity)getIntent().getSerializableExtra("Entity");
        initView();
    }

    @Override
    protected void getData() {
        new PermissionUtil().requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权
            }
        });
    }

    private void initView () {
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedTools.putBooleanValue(GuideActivity.this,"first-time-use", false);
                if (!StringUtils.isEmpty(advertisementEntity)) {
                    //需要显示广告页面
                    UIHelper.jumpAct(GuideActivity.this, AdvertisementActivity.class, advertisementEntity, true);
                } else {
                    //不需要显示广告页面 直接进入首页
                    UIHelper.jumpAct(GuideActivity.this, MainActivity.class, true);
                }
            }
        });
        adapter = new GalleryPagerAdapter();
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == images.length - 1) {
                    btnHome.setVisibility(View.VISIBLE);
                } else {
                    btnHome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (images.length > 0) {
                return images.length;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(GuideActivity.this);
            item.setScaleType(ImageView.ScaleType.CENTER_CROP);
            item.setImageResource(images[position]);
            container.addView(item, position);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }
}
