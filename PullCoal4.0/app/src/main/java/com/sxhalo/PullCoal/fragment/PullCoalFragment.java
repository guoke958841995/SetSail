package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.DetailPurchasingReservationActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.model.UserDemandBBS;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我要拉煤中所有人的发布界面
 * Created by amoldZhang on 2018/4/23.
 */

public class PullCoalFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<UserDemandBBS>{

    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;
    @Bind(R.id.procurement_list)
    SmoothListView procurementList;
    public BaseAdapterUtils baseAdapterUtils;
    private List<UserDemandBBS> userDemandEntities = new ArrayList<UserDemandBBS>();
    private int currentPage = 1;
    private Activity myActivity;
    private Map<String, String> coalTypeMap = new HashMap<String, String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_procurement_pullcoal, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e("货运功能界面展示", e.toString());
        }
        ButterKnife.bind(this, view);
        return view;
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(myActivity, procurementList);
        baseAdapterUtils.setViewItemData(R.layout.release_pullcoal_item, userDemandEntities);
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
        queryCoalType();
        getData();
    }

    private void queryCoalType() {
        Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100002"}).get(0);
        for (int i = 0; i < coalTypes.list.size(); i++) {
            coalTypeMap.put(coalTypes.list.get(i).dictCode, coalTypes.list.get(i).dictValue);
        }
    }

    private void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(myActivity, params).getUserDemandBBSList(new DataUtils.DataBack<List<UserDemandBBS>>() {
                @Override
                public void getData(List<UserDemandBBS> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        userDemandEntities = dataMemager;
                        baseAdapterUtils.refreshData(userDemandEntities);
                        ((BaseActivity) myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, procurementList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    super.getError(e);
                    ((BaseActivity) myActivity).displayToast("网络连接失败，请稍候再试");
//                    ((BaseActivity) myActivity).showEmptyView(baseAdapterUtils.getCount(), layoutNoData, procurementList);
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, UserDemandBBS entity, int pos) {
        final String targetId = entity.getRecordId();
        final String tel = entity.getContactPhone();
        helper.getView().findViewById(R.id.tv_tel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tel", tel);
                map.put("callType", Constant.CALE_TYPE_PULL_COAL);
                map.put("targetId", targetId);
                UIHelper.showCollTel(myActivity, map, true);
            }
        });

        CircleImageView ivHead = (CircleImageView) helper.getView().findViewById(R.id.iv_head);
        if (!StringUtils.isEmpty(entity.getHeadPic())){
            // 头像
            ((BaseActivity)myActivity).getImageManager().loadUrlImageView(entity.getHeadPic(), ivHead);
        }else{
            ivHead.setImageResource(R.mipmap.main_tab_item);
        }

        helper.setText(R.id.tv_name,entity.getContactPerson());
        TextView tvStatus = (TextView) helper.getView().findViewById(R.id.tv_status);
        if (entity.getRealnameAuthState().equals("1")) {
            //已认证 显示名字 隐藏中间
            helper.setText(R.id.tv_name, StringUtils.setName(entity.getContactPerson()));
            tvStatus.setText("已认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_green));
        } else {
            //未认证 显示电话号码隐藏中间4位
            helper.setText(R.id.tv_name, StringUtils.setPhoneNumber(entity.getContactPhone()));
            tvStatus.setText("未认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_gray));
        }

        helper.setText(R.id.tv_title, entity.getContent());
        helper.setText(R.id.tv_time, entity.getDifferMinute());
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserDemandBBS> mAdapter) {
//        Intent intent = new Intent();
//        intent.setClass(myActivity, DetailPurchasingReservationActivity.class);
//        intent.putExtra("demandId", mAdapter.getItem(position - 1).getDemandId());
//        myActivity.startActivityForResult(intent, Constant.REFRESH_CODE);
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
