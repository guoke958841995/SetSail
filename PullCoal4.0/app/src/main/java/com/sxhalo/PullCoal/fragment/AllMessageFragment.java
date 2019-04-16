package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.model.UserGuestbook;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 所有留言列表
 */

public class AllMessageFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<UserGuestbook> {

    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;
    @Bind(R.id.procurement_list)
    SmoothListView procurementList;
    public BaseAdapterUtils baseAdapterUtils;
    private List<UserGuestbook> userGuestbooks = new ArrayList<>();
    private int currentPage = 1;
    private Activity myActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_procurement, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e("所有留言列表界面展示", e.toString());
        }
        ButterKnife.bind(this, view);
        return view;
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(myActivity, procurementList);
        baseAdapterUtils.setViewItemData(R.layout.message_list_item, userGuestbooks);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        initView();
        getData();
    }

    private void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("messageType", "0");//0-法律援助 1-反馈建议 2-其他
            params.put("pageSize", "10");
            new DataUtils(myActivity, params).getUserGuestbookList(new DataUtils.DataBack<List<UserGuestbook>>() {
                @Override
                public void getData(List<UserGuestbook> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        userGuestbooks = dataMemager;
                        baseAdapterUtils.refreshData(userGuestbooks);
                        ((BaseActivity) myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, procurementList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, UserGuestbook entity, int pos) {
        View dividerRough = helper.getView().findViewById(R.id.layout_divider_rough);
        if (pos == 0) {
            dividerRough.setVisibility(View.GONE);
        } else {
            dividerRough.setVisibility(View.VISIBLE);
        }
        CircleImageView ivHead = (CircleImageView)helper.getView().findViewById(R.id.iv_head);
        if (!StringUtils.isEmpty(entity.getHeadPic())){
            // 头像
            ((BaseActivity)myActivity).getImageManager().loadUrlImageView(entity.getHeadPic(), ivHead);
        }else{
            ivHead.setImageResource(R.mipmap.main_tab_item);
        }
        helper.setText(R.id.tv_name, entity.getNickname());
        helper.setText(R.id.tv_time, entity.getCreateTime());
        helper.setText(R.id.tv_content, entity.getMessage());
        RelativeLayout relativeLayout = helper.getView(R.id.rl_reply);
        View divider = helper.getView(R.id.divider);
        if (StringUtils.isEmpty(entity.getRemark())) {
            relativeLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            relativeLayout.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_reply_content, "回复：" + entity.getRemark());
            helper.setText(R.id.tv_reply_time, entity.getDealTime());
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserGuestbook> mAdapter) {

    }

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        this.currentPage = page;
        getData();
    }
}
