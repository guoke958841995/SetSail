package com.sxhalo.PullCoal.ui.titlebar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.RouteNaviActivity;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.ui.SuperViewPager;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 左右菜单切换封装
 * Created by amoldZhang on 2017/10/16.
 */
public class FragmentTitleBar extends LinearLayout {


    @Bind(R.id.left_text)
    TextView leftText;
    @Bind(R.id.right_text)
    TextView rightText;
    @Bind(R.id.tab_line_iv)
    ImageView tabLineIv;
    @Bind(R.id.super_viewpager)
    SuperViewPager superViewpager;

    //界面底部默认布局
    @Bind(R.id.default_map_navi_title)
    TextView defaultmMapNaviTitle;
    @Bind(R.id.default_map_navi_dis)
    TextView defaultmMapNaviDis;
    @Bind(R.id.default_map_navi_addaess)
    TextView defaultmMapNaviAddaess;

    @Bind(R.id.bottom_default_view)
    LinearLayout defaultBottomDifoutView;
    @Bind(R.id.open_layout_view)
    LinearLayout openLayoutView;

    @Bind(R.id.layout_title)
    TextView layoutTitle;
    @Bind(R.id.layout_type)
    TextView layoutType;
    @Bind(R.id.layout_address)
    TextView layoutAddress;
    @Bind(R.id.layout_tel)
    TextView layoutTel;
    @Bind(R.id.layout_navi)
    ImageView layoutNavi;



    private Activity mActivity;
    private LayoutInflater mInflate;
    private ArrayList<Fragment> lists = new ArrayList<Fragment>();

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex = 0;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    private InformationDepartment informationDepartment;
    private MineMouth minMouth;
    private LatLng latLng;
    private LatLng endLatLng;
    private String itemId;


    public FragmentTitleBar(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        mInflate = LayoutInflater.from(mContext);
        View myView = mInflate.inflate(R.layout.fragment_title_bar_layout, null);
        ButterKnife.bind(this, myView);
        addView(myView);
    }

    public FragmentTitleBar(Context mContext) {
        super(mContext);
        mInflate = LayoutInflater.from(mContext);
        View myView = mInflate.inflate(R.layout.fragment_title_bar_layout, null);
        ButterKnife.bind(this, myView);
        addView(myView);
    }

    public void setInItView(Activity mActivity, InformationDepartment informationDepartment,LatLng endLatLng) {
        try {
            this.mActivity = mActivity;
            this.informationDepartment = informationDepartment;
            this.endLatLng = endLatLng;
            this.itemId = informationDepartment.getCoalSalesId();
            double latitude = Double.valueOf(SharedTools.getStringValue(mActivity,"latitude",""));
            double longitude = Double.valueOf(SharedTools.getStringValue(mActivity,"longitude",""));
            latLng = new LatLng(latitude,longitude);
            setData(endLatLng);
            setViewPage();
            initTabLineWidth();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setInItViewMinMouth(BaseActivity mActivity, MineMouth minMouth, LatLng endLatLng) {
        try {
            this.mActivity = mActivity;
            this.minMouth = minMouth;
            this.endLatLng = endLatLng;
            this.itemId = minMouth.getMineMouthId();
            double latitude = Double.valueOf(SharedTools.getStringValue(mActivity,"latitude",""));
            double longitude = Double.valueOf(SharedTools.getStringValue(mActivity,"longitude",""));
            latLng = new LatLng(latitude,longitude);
            setDataMinMouth(endLatLng);
            setViewPage();
            initTabLineWidth();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLeftText(String leText) {
        leftText.setText(leText);
    }

    public void setRightText(String riText) {
        rightText.setText(riText);
    }

    /**
     * 设置信息部数据展示
     */
    private void setData(LatLng endLatLng) {
        defaultmMapNaviTitle.setText(informationDepartment.getCompanyName());
        float distance = AMapUtils.calculateLineDistance(latLng,endLatLng)/1000;
        int length = 1;
        if (distance < 0){
            length = 2;
        }
        defaultmMapNaviDis.setText("距您"+ StringUtils.setNumLenth(distance,length) + "公里");
        defaultmMapNaviAddaess.setText(informationDepartment.getAddress());

        layoutTitle.setText(informationDepartment.getCompanyName());
        String typeId = informationDepartment.getTypeId();
        Dictionary sys100003 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100003"}).get(0);
        String carModeName = "";
        for (FilterEntity filterEntity : sys100003.list) {
            if (filterEntity.dictCode.equals(typeId)) {
                carModeName = filterEntity.dictValue;
                break;
            }
        }
        layoutType.setText(carModeName);
        layoutAddress.setText(informationDepartment.getAddress());

        layoutTel.setVisibility(View.VISIBLE);
        layoutTel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> map = new HashMap<String, String>();
                map.put("tel",informationDepartment.getContactPhone());
                map.put("callType", Constant.CALE_TYPE_INFORMATION_DEPARTENT);
                map.put("targetId",informationDepartment.getCoalSalesId());
                UIHelper.showCollTel(mActivity,map,true);
            }
        });
    }

    /**
     * 设置矿口展示
     */
    private void setDataMinMouth(LatLng endLatLng) {
        defaultmMapNaviTitle.setText(minMouth.getMineMouthName());
        float distance = AMapUtils.calculateLineDistance(latLng,endLatLng)/1000;
        int length = 1;
        if (distance < 0){
            length = 2;
        }
        defaultmMapNaviDis.setText("距您"+StringUtils.setNumLenth(distance,length)+"公里");
        defaultmMapNaviAddaess.setText(minMouth.getAddress());
        layoutTitle.setText(minMouth.getMineMouthName());
        String typeId = minMouth.getTypeId();
        Dictionary sys100004 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100004"}).get(0);
        String carModeName = "";
        for (FilterEntity filterEntity : sys100004.list) {
            if (filterEntity.dictCode.equals(typeId)) {
                carModeName = filterEntity.dictValue;
                break;
            }
        }
        layoutType.setText(carModeName);
        layoutAddress.setText(minMouth.getAddress());

//        layoutTel.setVisibility(INVISIBLE);
//        telPhone = minMouth.getContactPhone();
    }

    /**
     * 给据布局的开启状态来改变UI显示
     *
     * @param type 布局移动  0
     *              布局收起  1
     *              布局打开到固定位置  2
     */
    public void setSuperView(int type) {
        switch (type) {
            case 0:
                defaultBottomDifoutView.setVisibility(GONE);
                openLayoutView.setVisibility(VISIBLE);
                Bitmap mp0 = BitmapFactory.decodeResource(getResources(), R.mipmap.route_wite);
                layoutNavi.setImageBitmap(mp0);
                break;
            case 1:
                defaultBottomDifoutView.setVisibility(View.VISIBLE);
                openLayoutView.setVisibility(GONE);
                Bitmap mp1 = BitmapFactory.decodeResource(getResources(), R.mipmap.route_blue);
                layoutNavi.setImageBitmap(mp1);
                break;
            case 2:
                defaultBottomDifoutView.setVisibility(GONE);
                openLayoutView.setVisibility(VISIBLE);
                Bitmap mp2 = BitmapFactory.decodeResource(getResources(), R.mipmap.route_wite);
                layoutNavi.setImageBitmap(mp2);
//                Bundle data = new Bundle();
//                data.putInt("type",currentIndex);
//                data.putString("latitude",endLatLng.latitude + "");
//                data.putString("longitude",endLatLng.longitude + "");
//                data.putString("itemId",itemId);
//                ((TitleBarViewFragment)(lists.get(currentIndex))).refresh(mActivity,data);
//                superViewpager.setCurrentItem(currentIndex);
                break;
        }
    }

    /**
     * 类型0全部1矿口2信息部
     */
    private void setViewPage() {
        lists.clear();
        superViewpager.removeAllViews();
        currentIndex = 0;
        for (int i = 2;i>0;i--){
            Bundle data = new Bundle();
            data.putInt("type",i);
            data.putString("latitude",endLatLng.latitude + "");
            data.putString("longitude",endLatLng.longitude + "");
            data.putString("itemId",itemId);
            TitleBarViewFragment  leftFragment = new TitleBarViewFragment();
            leftFragment.setArguments(data);
            lists.add(leftFragment);
        }

        FragmentManager fm = ((BaseActivity)mActivity).getSupportFragmentManager();
        MyViewPagerAdapter mFragmentAdapter = new MyViewPagerAdapter(fm, lists);
        superViewpager.setAdapter(mFragmentAdapter);
        superViewpager.setCurrentItem(currentIndex);

        superViewpager.setOnPageChangeListener(new SuperViewPager.OnPageChangeListener() {

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                LayoutParams lp = (LayoutParams) tabLineIv
                        .getLayoutParams();

                // Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景： 记3个页面, 从左到右分别为0,1,2 0->1; 1->0
                 */

                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));
                }
                tabLineIv.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        leftText.setTextColor(Color.rgb(29, 122, 235));
                        break;
                    case 1:
                        rightText.setTextColor(Color.rgb(29, 122, 235));
                        break;
                }
                currentIndex = position;
            }
        });
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        try {
            rightText.setTextColor(Color.rgb(102, 102, 102));
            leftText.setTextColor(Color.rgb(102, 102, 102));
        } catch (Exception e) {
            GHLog.e("货运功能界面", e.toString());
        }
    }

    /**
     * 设置滑动条的宽度为屏幕的1/2(根据Tab的个数而定)
     */
    private void initTabLineWidth() throws Exception {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        mActivity.getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LayoutParams lp = (LayoutParams) tabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 2;
        tabLineIv.setLayoutParams(lp);
    }

    @OnClick({R.id.left_text, R.id.right_text,R.id.default_map_navi_button,R.id.layout_navi}) //,R.id.open_layout_view
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_text:
                superViewpager.setCurrentItem(0);
                leftText.setTextColor(mActivity.getResources().getColor(
                        R.color.app_title_text_color));
                rightText.setTextColor(mActivity.getResources().getColor(
                        R.color.father_group_text_color));
                break;
            case R.id.right_text:
                superViewpager.setCurrentItem(1);
                leftText.setTextColor(mActivity.getResources().getColor(
                        R.color.father_group_text_color));
                rightText.setTextColor(mActivity.getResources().getColor(
                        R.color.app_title_text_color));
                break;
            case R.id.default_map_navi_button:
//                mActivity.displayToast("直接导航界面");
                if (BaseUtils.isGPSOPen(mActivity)) {
                    Intent intent = new Intent(mActivity, RouteNaviActivity.class);
                    intent.putExtra("type", "nive");
                    intent.putExtra("endLatlag", endLatLng.latitude);
                    intent.putExtra("endLatlog", endLatLng.longitude);
                    intent.putExtra("startLatlag", latLng.latitude);
                    intent.putExtra("startLatlog", latLng.longitude);

                    String endAddress = mActivity.getIntent().getStringExtra("endAddress");
                    intent.putExtra("endAddress", endAddress);
                    mActivity.startActivity(intent);
                } else {
                    ((BaseActivity)mActivity).displayToast(mActivity.getString(R.string.open_gps));
                }
                break;
            case R.id.layout_navi:
                if (informationDepartment != null){
                    UIHelper.showRoutNavi(mActivity,informationDepartment.getLatitude(),informationDepartment.getLongitude(),informationDepartment.getCompanyName());
                }else if (minMouth != null){
                    UIHelper.showRoutNavi(mActivity,minMouth.getLatitude(),minMouth.getLongitude(),minMouth.getMineMouthName());
                }
                break;
        }
    }

}
