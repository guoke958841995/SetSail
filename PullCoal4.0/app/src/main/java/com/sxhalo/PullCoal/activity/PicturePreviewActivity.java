package com.sxhalo.PullCoal.activity;


import android.os.Bundle;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.ui.ZoomImageView;
import com.sxhalo.PullCoal.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 图片预览功能界面
 * Created by liz on 2017/3/27.
 */
public class PicturePreviewActivity extends BaseActivity{

    @Bind(R.id.zoom_view)
    ZoomImageView zoomImageView;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_picture_preview);
    }

    @Override
    protected void initTitle() {
    }

    @OnClick(R.id.zoom_view)
    public void onClick() {
        finish();
    }
    @Override
    public void getData() {
        final String url = getIntent().getStringExtra("url");
        if (!StringUtils.isEmpty(url)) {
            showProgressDialog("加载中...");
            getImageManager().loadPreviewPicture(url,zoomImageView, this);
        }
    }
}
