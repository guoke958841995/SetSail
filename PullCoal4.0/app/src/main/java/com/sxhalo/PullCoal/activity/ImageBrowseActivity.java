package com.sxhalo.PullCoal.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bm.library.PhotoView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.tools.image.ImageManager;
import com.sxhalo.PullCoal.ui.ExtendedViewPager;
import java.util.ArrayList;
import butterknife.Bind;

/**
 * 图片预览
 * Created by amoldZhang on 2017/11/15.
 */
public class ImageBrowseActivity extends BaseActivity {

    @Bind(R.id.view_pager)
    ExtendedViewPager mViewPager;
    @Bind(R.id.page_size)
    TextView pageSize;

    private ArrayList<String> newsImageUrlList = new ArrayList<String>();
    private int position;
    //当前的位置
    private ZoomImageAdapter zoomImageAdapter;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_image_browse);
    }

    @Override
    protected void initTitle() {
        initView();
    }

    @Override
    protected void getData() {
    }

    private void initView() {
        position = getIntent().getIntExtra("position", 0);
        newsImageUrlList = getIntent().getStringArrayListExtra("list");
        zoomImageAdapter = new ZoomImageAdapter(this, newsImageUrlList);
        pageSize.setText(position + 1 + "/" + newsImageUrlList.size());
        mViewPager.setAdapter(zoomImageAdapter);
        mViewPager.setOffscreenPageLimit(newsImageUrlList.size());
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(pageChangeListener);
    }

     class ZoomImageAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<String> newsImageUrlList = new ArrayList<String>();

        public ZoomImageAdapter(Context context, ArrayList<String> newsImageUrlList) {
            this.mContext = context;
            this.newsImageUrlList = newsImageUrlList;
        }

        @Override
        public int getCount() {
            return newsImageUrlList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
//            ZoomImageView img = new ZoomImageView(container.getContext());

            final PhotoView photoView = new PhotoView(container.getContext());
            // 启用图片缩放功能
            photoView.enable();
            // 获取/设置 最大缩放倍数
            photoView.setMaxScale(3.0f);
            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ((BaseActivity)mContext).getImageManager().loadPreviewPicture(newsImageUrlList.get(position), new ImageManager.GlideImageLoadingListener() {

                @Override
                public void onResourceReady(GlideDrawable bitmap, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    ((BaseActivity)mContext).dismisProgressDialog();
                    photoView.setImageDrawable(bitmap);
                    zoomImageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    ((BaseActivity)mContext).dismisProgressDialog();
                    ((BaseActivity)mContext).displayToast(mContext.getString(R.string.load_img_failed));
                    zoomImageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onStart() {
                    ((BaseActivity)mContext).showProgressDialog("加载中...");
                }

                @Override
                public void onStop() {
                    ((BaseActivity)mContext).dismisProgressDialog();
                }

            });

            container.addView(photoView);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) mContext).finish();
                }
            });
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            position = arg0 + 1;
            pageSize.setText( position + "/" + newsImageUrlList.size());
            zoomImageAdapter.notifyDataSetChanged();
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {
        }
    };
}
