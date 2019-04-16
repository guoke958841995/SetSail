package com.sxhalo.PullCoal.ui.pullrecyclerview.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.adapter.HeaderAdAdapter;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.image.ImageManager;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  首页轮播图布局
 * Created by amoldZhang on 2018/12/18.
 */
public class TemplateBanner extends BaseView{

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
    private List<ImageView> ivList;
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
    private View mRootView;


    public TemplateBanner(Activity context) {
        super(context);
        ivList = new ArrayList<ImageView>();
        mImageManager = new ImageManager(context);
        initView();
    }

    private void initView() {
        try {
            mRootView = View.inflate(mContext, R.layout.header_ad_layout, null);
            ButterKnife.bind(this, mRootView);

            addTemplateView();
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }

    @Override
    public void setData(BaseModule module) {
        fillData(module);
    }

    @Override
    public void fillData(final BaseModule module) {
        if (module == null || !(module instanceof HomeData))
        {
            return;
        }

        if (((HomeData) module).getSlideList().size() != 0){
            dealWithTheView(((HomeData) module).getSlideList());
        }
//        this.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onItemClick(module);
//            }
//        });
        invalidate();
    }

    @Override
    public void addTemplateView() {
        LayoutParams lp = new LayoutParams(-1, (int) (mContext.getResources().getDisplayMetrics().density * 150));
        mRootView.setLayoutParams(lp);
        addView(mRootView);
    }

    @Override
    public void reFresh() {

    }

    public void dealWithTheView(final List<Slide> adList) {
        try {
            ivList.clear();
            final int size = adList.size();
            if (size == 2){
                // 如果图片集合等于2，那么添加两倍图片,防止右滑动出现空白页，自行优化是一张图片的时候就不让左右滑动和轮播
                for (int i = 0; i < size * 2 ; i++) {
                    ivList.add(createImageView(adList.get(i % size).getImageUrl()));
                }
            }else{
                for (int i = 0; i < size; i++) {
                    ivList.add(createImageView(adList.get(i).getImageUrl()));
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
                        try {
                            Slide mEntity = adList.get(position);
                            System.out.println("跳转连接++++"+mEntity.getPublishedUrl());
                            if(mEntity.getPublishedUrl().equals("")||mEntity.getPublishedUrl().equals("null")){
                            }else{
                                GHLog.i("头条点击", position+"");
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra("URL",mEntity.getPublishedUrl());
                                intent.putExtra("title","资讯详情");
                                intent.putExtra("articleId",mEntity.getArticleId());
                                mContext.startActivity(intent);
                            }
                        } catch (Exception e) {
                            GHLog.e("头条点击", e.toString());
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
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("轮播图界面", e.toString());
        }
    }

    // 创建要显示的ImageView并显示网路图片
    private ImageView createImageView(String url) {
        ImageView imageView = new ImageView(mContext);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageManager.loadUrlImageAuthentication(url, imageView); //轮播图展示
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

}
