package com.sxhalo.PullCoal.ui.recyclerviewmode;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.HeaderAdAdapter;
import com.sxhalo.PullCoal.model.AdvertisementEntity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.image.ImageManager;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderAdViewView;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.zhy.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amoldZhang on 2018/7/24.
 */
public class AdHeaderView {

    @Bind(R.id.vp_ad)
    ViewPager vpAd;
    @Bind(R.id.ll_index_container)
    LinearLayout llIndexContainer;

    @Bind(R.id.management_mode_image)
    TextView managementModeImage;
    @Bind(R.id.information_department_ratingbar1)
    ResetRatingBar informationDepartmentRatingbar1;
    @Bind(R.id.information_department_ratingbar_ll)
    LinearLayout informationDepartmentRatingbarLl;
    @Bind(R.id.information_department_follow_yes)
    TextView informationDepartmentFollowYes;
    @Bind(R.id.information_department_follow_not)
    TextView informationDepartmentFollowNot;
    @Bind(R.id.if_follow_ll)
    LinearLayout ifFollowLl;
    @Bind(R.id.bottom_rl_view)
    RelativeLayout bottomRlView;

    private static final int TYPE_CHANGE_AD = 0;
    private Thread mThread;
    private List<ImageView> ivList = new ArrayList<>();
    private boolean isStopThread = false;
    private ImageManager mImageManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TYPE_CHANGE_AD) {
                vpAd.setCurrentItem(vpAd.getCurrentItem() + 1);
            }
        }
    };
    private HeaderAdAdapter photoAdapter;
    private List<AdvertisementEntity> list;
    private Activity mContext;
    private View view;

    public AdHeaderView(Activity mContext, RecyclerView mRecyclerView, List<AdvertisementEntity> list) {
        try {
            this.mContext = mContext;
            this.list = list;
            mImageManager = new ImageManager(mContext);
            view = LayoutInflater.from(mContext).inflate(R.layout.header_ad_layout, mRecyclerView, false);
            ButterKnife.bind(this, view);

            if (list.size() == 0){
                view.setVisibility(View.GONE);
            }else{
                view.setVisibility(View.VISIBLE);
                dealWithTheView(list);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
            GHLog.e("界面控件控制", e.toString());
        }
    }

    public View getView() {
        return view;
    }

    public void dealWithTheView(List<AdvertisementEntity> list) {
        try {
            ivList.clear();
            final int size = list.size();
            if (size == 2){
                // 如果图片集合等于2，那么添加两倍图片,防止右滑动出现空白页，自行优化是一张图片的时候就不让左右滑动和轮播
                for (int i = 0; i < size * 2 ; i++) {
                    ivList.add(createImageView(list.get(i % size).getAdPicUrl()));
                }
            }else{
                for (int i = 0; i < size; i++) {
                    ivList.add(createImageView(list.get(i).getAdPicUrl()));
                }
            }
            if (photoAdapter == null){
                photoAdapter = new HeaderAdAdapter(mContext, ivList);
                vpAd.setAdapter(photoAdapter);
                photoAdapter.setImageOnClickListener(new HeaderAdAdapter.ImageOnClickListener() {

                    @Override
                    public void MYImageOnClickListener(View view, int position) {
                        if (size == 2){
                            position = position % size;
                        }
                        if (adOnClickListener != null) {
                            adOnClickListener.adItemOnClickListener(view, position);
                        }
                    }
                });
            }else{
                photoAdapter.notifyDataSetChanged(ivList);
            }
            addIndicatorImageViews(size);
            setViewPagerChangeListener(size);
            startADRotate();
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
            GHLog.e("轮播图界面", e.toString());
        }
    }

    // 创建要显示的ImageView并显示网路图片
    private ImageView createImageView(String url) {
        ImageView imageView = new ImageView(mContext);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageManager.loadUrlImageAuthentication(url, imageView); //轮播图图片展示
        return imageView;
    }

    // 添加指示图标
    private void addIndicatorImageViews(int size) {
        try {
            llIndexContainer.removeAllViews();
            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DeviceUtil.dip2px(mContext, 8), DeviceUtil.dip2px(mContext, 8));
                if (i != 0) {
                    lp.leftMargin = DeviceUtil.dip2px(mContext, 10);
                }
                iv.setLayoutParams(lp);
                iv.setImageResource(R.drawable.xml_round_orange_grey_sel);
                iv.setEnabled(false);
                if (i == 0) {
                    iv.setEnabled(true);
                }
                llIndexContainer.addView(iv);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("添加指示图标", e.toString());
        }
    }

    public void setLlIndexContainer(int gravity) {
        if (llIndexContainer != null) {
            llIndexContainer.setGravity(gravity);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = DeviceUtil.dip2px(mContext, 15);
            lp .gravity = Gravity.BOTTOM;
            llIndexContainer.setLayoutParams(lp);
        }
    }

    public void setLlIndexContainer() {
        if (llIndexContainer != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp .gravity = Gravity.TOP;
            llIndexContainer.setLayoutParams(lp);
        }
    }


    public void setBottomRlView(String TEXT){
        if (bottomRlView != null){
            bottomRlView.setVisibility(View.VISIBLE);
            managementModeImage.setText("   " + TEXT);
        }
    }

    // 为ViewPager设置监听器
    private void setViewPagerChangeListener(final int size) {
        try {
            vpAd.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (ivList != null && size > 0) {
                        int newPosition = position % size;
                        for (int i = 0; i < size; i++) {
                            View indexView = llIndexContainer.getChildAt(i);
                            if (indexView != null) {
                                indexView.setEnabled(false);
                                if (i == newPosition) {
                                    indexView.setEnabled(true);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onPageScrolled(int position, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("设置监听器", e.toString());
        }

    }

    // 启动循环广告的线程
    private void startADRotate() {
        try {
            // 一个广告的时候不用转
            if (ivList == null || ivList.size() <= 1) {
                if (mThread != null){
                    stopADRotate();
                }
                return;
            }
            if (mThread == null) {
                mThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // 当没离开该页面时一直转
                        while (!isStopThread) {
                            // 每隔5秒转一次
                            SystemClock.sleep(5000);
                            // 在主线程更新界面
                            mHandler.sendEmptyMessage(TYPE_CHANGE_AD);
                        }
                    }
                });
                mThread.start();
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("开启控制", e.toString());
        }
    }

    // 停止循环广告的线程，清空消息队列
    public void stopADRotate() {
        isStopThread = true;
        if (mHandler != null && mHandler.hasMessages(TYPE_CHANGE_AD)) {
            mHandler.removeMessages(TYPE_CHANGE_AD);
        }
    }

    /**
     * 添加点击外接接口
     */
    private HeaderAdViewView.ADOnClickListener adOnClickListener;

    public void setADItemOnClickListener(HeaderAdViewView.ADOnClickListener imageOnClickListener) {
        this.adOnClickListener = imageOnClickListener;
    }

    public interface ADOnClickListener {
        void adItemOnClickListener(View view, int position);
    }

}
