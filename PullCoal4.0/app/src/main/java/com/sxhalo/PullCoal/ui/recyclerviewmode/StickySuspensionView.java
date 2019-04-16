package com.sxhalo.PullCoal.ui.recyclerviewmode;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.utils.GHLog;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.srecyclerview.SRecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  悬浮布局中 煤种筛选
 * Created by amoldZhang on 2018/7/31.
 */
public class StickySuspensionView {

    private final Activity mActivity;
    private final View view;
    @Bind(R.id.tv_order)
    TextView tvOrder;
    @Bind(R.id.iv_order)
    ImageView ivOrder;
    @Bind(R.id.tv_msg_type)
    TextView tvMsgType;
    @Bind(R.id.iv_msg_type)
    ImageView ivMsgType;
    @Bind(R.id.tv_filter)
    TextView tvFilter;

    public StickySuspensionView(Activity mActivity, RecyclerView mContextRecyclerView) {
        this.mActivity = mActivity;
        view = LayoutInflater.from(mActivity).inflate(R.layout.screening_suspension_item_group, mContextRecyclerView, false);
        ButterKnife.bind(this, view);

        initView();
    }

    private void initView() {

    }

    public void setView(String textOrder,String textMessage) {
        try {
            tvOrder.setText(textOrder);
            tvMsgType.setText(textMessage);
        } catch (Exception e) {
            GHLog.e("界面控件控制", e.toString());
        }
    }

    public View getView(){
        return view;
    }

    @OnClick({R.id.rl_order, R.id.rl_msg_type, R.id.layout_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_order:
                if (onClick != null){
                    onClick.onItemClick(tvOrder,0);
                }
                break;
            case R.id.rl_msg_type:
                if (onClick != null){
                    onClick.onItemClick(tvMsgType,1);
                }
                break;
            case R.id.layout_filter:
                if (onClick != null){
                    onClick.onItemClick(tvFilter,2);
                }
                break;
        }
    }

    private OnClickListener onClick;
    public void setOnClickListener(OnClickListener onClick){
        this.onClick = onClick ;
    }
    public static abstract class OnClickListener<T>{

        public void onItemClick(TextView TV, int pos) {

        }
    }
}
