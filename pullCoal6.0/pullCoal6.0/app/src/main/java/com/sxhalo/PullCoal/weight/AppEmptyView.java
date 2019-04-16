package com.sxhalo.PullCoal.weight;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  加载数据空布局
 * Created by amoldZhang on 2019/4/13
 */
public class AppEmptyView {

    @BindView(R.id.iv_no_data)
    ImageView ivNoData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.layout_no_data)
    RelativeLayout layoutNoData;

    private Activity mActivity;
    private View emptyView;

    public ClickDoView clickDoView;
    public interface ClickDoView{
        void onClick();
    }

    public AppEmptyView(Activity mActivity) {
        this.mActivity = mActivity;
        emptyView = mActivity.getLayoutInflater().inflate(R.layout.layout_no_data, null);
        ButterKnife.bind(mActivity, emptyView);
    }

    public AppEmptyView(Activity mActivity,ClickDoView clickDoView) {
        this.mActivity = mActivity;
        this.clickDoView = clickDoView;
        emptyView = mActivity.getLayoutInflater().inflate(R.layout.layout_no_data, null);
        ButterKnife.bind(this, emptyView);
    }

    /**
     * 获取布局view
     * @return
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置 空布局提示图片
     * @param image
     */
    public void setIvNoData(int image){
        if (image == 0)image = R.mipmap.icon_no_transport_order;
        ivNoData.setImageDrawable(mActivity.getResources().getDrawable(image));
    }

    /**
     * 设置空布局 提示文字
     * @param message
     */
    public void setTvNoData(String message){
        if (StringUtils.isEmpty(message)) message = "暂无数据";
        tvNoData.setText(message);
    }


    @OnClick(R.id.layout_no_data)
    public void onViewClicked() {
        if (clickDoView != null){
            clickDoView.onClick();
        }
    }

}
