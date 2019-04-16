package com.sxhalo.PullCoal.ui.titlebar;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.DetaileInformationDepartmentActivity;
import com.sxhalo.PullCoal.activity.DetaileMineActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.Discover;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.scrolllayout.content.ContentListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amoldZhang on 2017/10/16.
 */
public class TitleBarViewFragment extends Fragment {

    @Bind(R.id.list_view)
    ContentListView listView;

    private Activity myActivity;
    private List<Discover> arrayList = new ArrayList<Discover>();
    private QuickAdapter<Discover> mAdapter;
    private int Type;
    private String latitude;
    private String longitude;
    private String itemId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            latitude = getArguments().getString("latitude");
            longitude = getArguments().getString("longitude");
            itemId = getArguments().getString("itemId");
            Type =  getArguments().getInt("type");
        } catch (Exception e) {
            GHLog.e("Bundle传值",e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title_bar_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            myActivity = getActivity();
            initView();
            getDataPath();
        } catch (Exception e) {
            GHLog.e("界面初始化",e.toString());
        }
    }

//    public void refresh(Activity myActivity,Bundle savedInstanceState) {
//        this.myActivity = myActivity;
//        latitude = savedInstanceState.getString("latitude");
//        longitude = savedInstanceState.getString("longitude");
//        itemId = savedInstanceState.getString("itemId");
//        Type =  savedInstanceState.getInt("type");
//        getDataPath();
//    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (!hidden){
//            getDataPath();
//        }
//    }

    public void getDataPath() {
        try {
            LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
            params.put("searchType", Type + "");
            params.put("itemId", itemId);
            params.put("longitude", longitude + "");
            params.put("latitude", latitude + "");
            params.put("distance","10000");
            params.put("currentPage",1 +"");
            params.put("pageSize","10");
            new DataUtils(myActivity,params).getDiscoverList(new DataUtils.DataBack<APPDataList<Discover>>() {
                @Override
                public void getData(APPDataList<Discover> data) {
                    if (data == null){
                        GHLog.e("服务器出错", "Success = 1");
                        return;
                    }
                    arrayList = data.getList();
                    mAdapter.addAll(arrayList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mAdapter = new QuickAdapter<Discover>(myActivity,R.layout.title_bar_view_item, arrayList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Discover t, final int pos) {
                try {
                    if (Type == 1){ //矿口
                        helper.getView().findViewById(R.id.ratingbar_view).setVisibility(View.GONE);
                    }
                    if (Type == 2){ //信息部
                        helper.getView().findViewById(R.id.ratingbar_view).setVisibility(View.VISIBLE);
                        ResetRatingBar  ratingbar = (ResetRatingBar)helper.getView().findViewById(R.id.ratingbar);
                        ratingbar.setStar(Integer.valueOf(StringUtils.isEmpty(t.getCreditRating()) ? "1":t.getCreditRating()));
                    }
                    setData(helper,t);
                } catch (Exception e) {
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                try {
                    if (Type == 1){ //矿口
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), DetaileMineActivity.class);
                        String inforDepartId = mAdapter.getItem(pos).getItemId();
                        intent.putExtra("InfoDepartId", inforDepartId);
                        intent.putExtra("TYPE", "-1");
                        startActivityForResult(intent, Constant.REFRESH_CODE);
                    }
                    if (Type == 2){ //信息部
                        Intent intent = new Intent();
                        intent.setClass(getActivity(),DetaileInformationDepartmentActivity.class);
                        String inforDepartId = mAdapter.getItem(pos).getItemId();
                        intent.putExtra("InfoDepartId",inforDepartId);
                        intent.putExtra("TYPE","-1");
                        startActivityForResult(intent, Constant.REFRESH_CODE);
                    }
                } catch (Exception e) {
                    GHLog.e("Item点击", e.toString());
                }
            }
        });
    }

    private void setData(BaseAdapterHelper helper, Discover t) {
        helper.setText(R.id.layout_lift_name,t.getItemName());
        helper.setText(R.id.distance_and_addess,((Integer.valueOf(t.getDistance()) / 1000) == 0 ? Integer.valueOf(t.getDistance()) + "m   " : (Integer.valueOf(t.getDistance())/ 1000) + "Km   " ) + t.getAddress());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
