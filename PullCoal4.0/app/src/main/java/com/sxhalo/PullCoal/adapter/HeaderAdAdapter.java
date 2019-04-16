package com.sxhalo.PullCoal.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by sunfusheng on 16/4/20.
 */
public class HeaderAdAdapter extends PagerAdapter {

    private Context mContext;
    private List<ImageView> ivList; // ImageView的集合
    private int count = 1; // 广告的数量

    public HeaderAdAdapter(Context context, List<ImageView> ivList) {
        super();
        this.mContext = context;
        this.ivList = ivList;
        if(ivList != null && ivList.size() > 0){
            count = ivList.size();
        }
    }

    @Override
    public int getCount() {
        if (count == 1) {
            return 1;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public void notifyDataSetChanged(List<ImageView> ivList) {
        this.ivList = ivList;
        if(ivList != null && ivList.size() > 0){
            count = ivList.size();
        }
        notifyDataSetChanged();
    }

    public int getRealPageEndPos(){
        return count -1;
    }

    public int getRealPageStartPos(){
        return 0;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int newPosition = position % count;
        // 先移除在添加，更新图片在container中的位置（把iv放至container末尾）
        ImageView iv = ivList.get(newPosition);
        container.removeView(iv);
        container.addView(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageOnClickListener != null) {
                    imageOnClickListener.MYImageOnClickListener(view,newPosition);
                }
            }
        });
        return iv;
    }

    /**
     * 添加点击外接接口
     */
    private ImageOnClickListener imageOnClickListener;
    public void setImageOnClickListener(ImageOnClickListener imageOnClickListener) {
        this.imageOnClickListener = imageOnClickListener;
    }


    public interface ImageOnClickListener {
        void MYImageOnClickListener(View view, int position);
    }
}
